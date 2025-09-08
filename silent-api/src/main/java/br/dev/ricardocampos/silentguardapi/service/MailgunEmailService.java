package br.dev.ricardocampos.silentguardapi.service;

import br.dev.ricardocampos.silentguardapi.config.AppConfig;
import br.dev.ricardocampos.silentguardapi.exception.MailServiceException;
import br.dev.ricardocampos.silentguardapi.template.MailgunTemplate;
import br.dev.ricardocampos.silentguardapi.template.MailgunTemplateCheckIn;
import br.dev.ricardocampos.silentguardapi.template.MailgunTemplateHtml;
import br.dev.ricardocampos.silentguardapi.util.FormatUtil;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Service class for sending emails using the Mailgun API. This service provides methods to send
 * check-in requests and HTML content messages to specified recipients.
 */
@Slf4j
@Service
public class MailgunEmailService {

  private final RestTemplate restTemplate;

  private final AppConfig appConfig;

  /**
   * Constructs a MailgunEmailService with the specified RestTemplateBuilder and AppConfig.
   *
   * @param templateBuilder the RestTemplateBuilder to create the RestTemplate instance
   * @param appConfig the application configuration containing Mailgun settings
   */
  public MailgunEmailService(RestTemplateBuilder templateBuilder, AppConfig appConfig) {
    this.restTemplate = templateBuilder.build();
    this.appConfig = appConfig;
  }

  /**
   * Sends a check-in request email to the first recipient in the list and optionally CCs others.
   *
   * @param recipients the list of email addresses to send the check-in request to
   * @param confirmationId the confirmation ID to include in the check-in link
   */
  public void sendCheckInRequest(List<String> recipients, String confirmationId, Duration hoursToRespond) {
    log.info("Sending check-in message");

    String to = recipients.getFirst();
    String subject = "Silent Guard hasn't heard from you in a while!";
    String link = getBaseUrl() + "?confirmation=%s";

    MailgunTemplateCheckIn checkInTemplate = new MailgunTemplateCheckIn();
    checkInTemplate.setCheckInLink(String.format(link, confirmationId));
    checkInTemplate.setTimeToRespond(FormatUtil.formatDuration(hoursToRespond));
    List<String> recipientsCarbonCopy = getRecipientsCarbonCopy(recipients);
    if (!recipientsCarbonCopy.isEmpty()) {
      checkInTemplate.setCarbonCopy(String.join(",", recipientsCarbonCopy));
    }

    boolean sent = sendEmail(to, subject, checkInTemplate);
    log.info("Check-in message sent successfully: {}", sent);
  }

  /**
   * Sends an HTML content message to the first recipient in the list and optionally CCs others.
   *
   * @param recipients the list of email addresses to send the HTML content message to
   * @param subject the subject of the email
   * @param htmlContent the HTML content to be included in the email body
   */
  public void sendHtmlContentMessage(List<String> recipients, String subject, String htmlContent) {
    log.info("Sending HTML content message");

    String to = recipients.getFirst();

    MailgunTemplateHtml htmlTemplate = new MailgunTemplateHtml();
    htmlTemplate.setHtmlCode(htmlContent);
    List<String> recipientsCarbonCopy = getRecipientsCarbonCopy(recipients);
    if (!recipientsCarbonCopy.isEmpty()) {
      htmlTemplate.setCarbonCopy(String.join(",", recipientsCarbonCopy));
    }

    boolean sent = sendEmail(to, subject, htmlTemplate);
    log.info("Content message sent successfully: {}", sent);
  }

  /**
   * Send an email message.
   *
   * @param to The target email address.
   * @param subject The message subject.
   * @param template The {@link MailgunTemplate} instance
   */
  private boolean sendEmail(String to, String subject, MailgunTemplate template) {
    String url = "https://api.mailgun.net/v3/" + appConfig.getMailgunDomain() + "/messages";
    String from = "Silent Guard <" + appConfig.getMailgunSender() + ">";

    log.debug("Mailgun URL: {}", url);
    log.debug("Mailgun Email from: {}", from);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    headers.set(HttpHeaders.AUTHORIZATION, basicAuth(appConfig.getMailgunApiKey()));

    MultiValueMap<String, String> mailData = new LinkedMultiValueMap<>();
    mailData.add("from", from);
    mailData.add("to", to);
    if (template.getCarbonCopy().isPresent()) {
      mailData.add("cc", template.getCarbonCopy().get());
    }
    boolean isTemplateHtml = template.isHtml() && template.getHtmlCode().isPresent();
    mailData.add("subject", subject);
    if (!isTemplateHtml) {
      mailData.add("template", template.getName());
      if (!template.getVariables().isEmpty()) {
        mailData.add("h:X-Mailgun-Variables", template.getVariableValuesJson());
      }
    } else {
      mailData.add("html", template.getHtmlCode().get());
    }

    for (Map.Entry<String, List<String>> entry : mailData.entrySet()) {
      String message = String.format("%s: %s", entry.getKey(), entry.getValue().getFirst());
      log.debug(message);
    }

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(mailData, headers);

    try {
      ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

      if (!response.getStatusCode().is2xxSuccessful()) {
        throw new MailServiceException("Failed to send email: " + response.getStatusCode());
      }

      log.info("Email message sent successfully.");
      return true;
    } catch (HttpClientErrorException ex) {
      log.error("HttpClientErrorException when sending email: {}", ex.getMessage());
    }
    return false;
  }

  private String basicAuth(String password) {
    String auth = "api" + ":" + password;
    return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
  }

  private String getBaseUrl() {
    if ("development".equals(appConfig.getTargetEnv())
        || Objects.isNull(appConfig.getTargetEnv())) {
      return "https://silentguard-local.ricardocampos.dev.br:5173";
    }
    return String.format("https://%s%s", "silentguard.", appConfig.getMailgunDomain() + "/");
  }

  private List<String> getRecipientsCarbonCopy(List<String> recipients) {
    if (recipients.size() == 1) {
      return List.of();
    }

    // skip the first one
    List<String> carbonCopyList = new ArrayList<>();
    for (int i = 1; i < recipients.size(); i++) {
      carbonCopyList.add(recipients.get(i));
    }

    return carbonCopyList;
  }
}
