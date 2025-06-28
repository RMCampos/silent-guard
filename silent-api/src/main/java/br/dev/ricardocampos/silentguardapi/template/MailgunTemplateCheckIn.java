package br.dev.ricardocampos.silentguardapi.template;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a Mailgun template for sending check-in requests. This template includes a link for
 * users to check in and can optionally include a carbon copy recipient.
 */
public class MailgunTemplateCheckIn implements MailgunTemplate {

  private String templateName = "check-in request";
  private String carbonCopy;
  private final Map<String, Object> props;

  /** Constructs a MailgunTemplateCheckIn instance with an empty properties map. */
  public MailgunTemplateCheckIn() {
    this.props = new HashMap<>();
  }

  /**
   * Constructs a MailgunTemplateCheckIn instance with the specified template name and properties.
   *
   * @param templateName the name of the template
   * @param props the properties to be included in the template
   */
  public void setCheckInLink(String checkInLink) {
    props.put("CHECK_IN_LINK", checkInLink);
  }

  /**
   * Returns the name of the template.
   *
   * @return the name of the template
   */
  @Override
  public String getName() {
    return templateName;
  }

  /**
   * Returns the properties of the template as a map. This map can include various variables needed
   * for rendering the email content, such as the check-in link.
   *
   * @return a map containing the template variables
   * @see MailgunTemplate#getVariables()
   */
  @Override
  public Map<String, Object> getVariables() {
    return props;
  }

  /**
   * Sets the carbon copy recipient for the email.
   *
   * @param carbonCopy the email address to be added as a carbon copy recipient
   */
  public void setCarbonCopy(String carbonCopy) {
    this.carbonCopy = carbonCopy;
  }

  /**
   * Returns the carbon copy recipient if set.
   *
   * @return an Optional containing the carbon copy recipient, or an empty Optional if not set
   */
  @Override
  public Optional<String> getCarbonCopy() {
    return Optional.ofNullable(carbonCopy);
  }
}
