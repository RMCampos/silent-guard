package br.dev.ricardocampos.silentguardapi.service;

import br.dev.ricardocampos.silentguardapi.config.AppConfig;
import br.dev.ricardocampos.silentguardapi.exception.MailServiceException;
import br.dev.ricardocampos.silentguardapi.template.MailgunTemplate;
import br.dev.ricardocampos.silentguardapi.template.MailgunTemplateCheckIn;
import br.dev.ricardocampos.silentguardapi.template.MailgunTemplateHtml;
import java.nio.charset.StandardCharsets;
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

@Slf4j
@Service
public class MailgunEmailService {

  private final RestTemplate restTemplate;

  private final AppConfig appConfig;

  public MailgunEmailService(RestTemplateBuilder templateBuilder, AppConfig appConfig) {
    this.restTemplate = templateBuilder.build();
    this.appConfig = appConfig;
  }

  public void sendCheckInRequest(List<String> recipients, String confirmationId) {
    log.info("Sending check-in message");

    String to = recipients.getFirst();
    String subject = "Silent Guard hasn't heard from you in a while!";
    String link = getBaseUrl() + "?confirmation=%s";

    MailgunTemplateCheckIn checkInTemplate = new MailgunTemplateCheckIn();
    checkInTemplate.setCheckInLink(String.format(link, confirmationId));
    if (recipients.size() > 1) {
      // skip the first one
      StringBuilder sb = new StringBuilder();
      for (int i = 1; i < recipients.size(); i++) {
        if (!sb.isEmpty()) {
          sb.append(",");
        }
        sb.append(recipients.get(i));
      }

      checkInTemplate.setCarbonCopy(sb.toString());
    }

    boolean sent = sendEmail(to, subject, checkInTemplate);
    log.info("Check-in message sent successfully: {}", sent);
  }

  public void sendHtmlContentMessage(List<String> recipients, String subject, String htmlContent) {
    log.info("Sending HTML content message");

    String to = recipients.getFirst();

    MailgunTemplateHtml htmlTemplate = new MailgunTemplateHtml();
    htmlTemplate.setHtmlCode(htmlContent);
    if (recipients.size() > 1) {
      // skip the first one
      StringBuilder sb = new StringBuilder();
      for (int i = 1; i < recipients.size(); i++) {
        if (!sb.isEmpty()) {
          sb.append(",");
        }
        sb.append(recipients.get(i));
      }

      htmlTemplate.setCarbonCopy(sb.toString());
    }

    boolean sent = sendEmail(to, subject, htmlTemplate);
    log.info("Check-in message sent successfully: {}", sent);
  }

  /**
   * Send an email message.
   *
   * @param to The target email address.
   * @param subject The message subject.
   * @param textBody The message text body to be displayed.
   * @param htmlBody The message html body to be rendered.
   */
  private boolean sendEmail(String to, String subject, MailgunTemplate template) {
    String url = "https://api.mailgun.net/v3/" + appConfig.getMailgunDomain() + "/messages";
    String from = "Silent Guard <" + appConfig.getMailgunSender() + ">";

    log.debug("Mailgun URL: {}", url);
    log.debug("Mailgun Email from: {}", from);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    headers.set(HttpHeaders.AUTHORIZATION, basicAuth("api", appConfig.getMailgunApiKey()));

    MultiValueMap<String, String> mailData = new LinkedMultiValueMap<>();
    mailData.add("from", from);
    mailData.add("to", to);
    if (template.getCarbonCopy().isPresent()) {
      mailData.add("cc", template.getCarbonCopy().get());
    }
    mailData.add("subject", subject);
    if (!template.isHtml()) {
      mailData.add("template", template.getName());
      if (!template.getVariables().isEmpty()) {
        mailData.add("h:X-Mailgun-Variables", template.getVariableValuesJson());
      }
    } else {
      mailData.add("html", template.getHtmlCode().get());
    }

    for (Map.Entry<String, List<String>> entry : mailData.entrySet()) {
      String message = String.format("%s: %s", entry.getKey(), entry.getValue().get(0));
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

  private String basicAuth(String username, String password) {
    String auth = username + ":" + password;
    return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
  }

  private String getBaseUrl() {
    if ("development".equals(appConfig.getTargetEnv())
        || Objects.isNull(appConfig.getTargetEnv())) {
      return "http://localhost:5173";
    }
    String stage = appConfig.getTargetEnv().equals("stage") ? "stage." : "";
    return String.format("https://%s%s", stage, appConfig.getTargetEnv());
  }
}
