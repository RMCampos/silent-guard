package br.dev.ricardocampos.silentguardapi.template;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MailgunTemplateCheckIn implements MailgunTemplate {
  
  private String templateName = "check-in request";
  private String carbonCopy;
  private final Map<String, Object> props;

  public MailgunTemplateCheckIn() {
    this.props = new HashMap<>();
  }

  public void setCheckInLink(String checkInLink) {
    props.put("CHECK_IN_LINK", checkInLink);
  }

  @Override
  public String getName() {
    return templateName;
  }

  @Override
  public Map<String, Object> getVariables() {
    return props;
  }

  public void setCarbonCopy(String carbonCopy) {
    this.carbonCopy = carbonCopy;
  }

  @Override
  public Optional<String> getCarbonCopy() {
    return Optional.ofNullable(carbonCopy);
  }
}
