package br.dev.ricardocampos.silentguardapi.template;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.Setter;

/**
 * Represents a Mailgun template for sending check-in requests. This template includes a link for
 * users to check in and can optionally include a carbon copy recipient.
 */
public class MailgunTemplateCheckIn implements MailgunTemplate {

  @Setter private String carbonCopy;

  private final Map<String, Object> props;

  /** Constructs a MailgunTemplateCheckIn instance with an empty properties map. */
  public MailgunTemplateCheckIn() {
    this.props = new HashMap<>();
  }

  /**
   * Constructs a MailgunTemplateCheckIn instance with the specified template name and properties.
   *
   * @param checkInLink the check-in link to the user
   */
  public void setCheckInLink(String checkInLink) {
    props.put("CHECK_IN_LINK", checkInLink);
  }

  public void setTimeToRespond(String timeToRespond) {
    props.put("TIME_TO_RESPOND", timeToRespond);
  }

  /**
   * Returns the name of the template.
   *
   * @return the name of the template
   */
  @Override
  public String getName() {
    return "check-in request";
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
   * Returns the carbon copy recipient if set.
   *
   * @return an Optional containing the carbon copy recipient, or an empty Optional if not set
   */
  @Override
  public Optional<String> getCarbonCopy() {
    return Optional.ofNullable(carbonCopy);
  }
}
