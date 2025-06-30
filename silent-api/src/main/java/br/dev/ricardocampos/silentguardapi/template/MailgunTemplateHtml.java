package br.dev.ricardocampos.silentguardapi.template;

import java.util.Optional;
import lombok.Setter;

/**
 * Represents a Mailgun template for sending HTML emails. This template includes the HTML code and
 * can optionally include a carbon copy recipient.
 */
@Setter
public class MailgunTemplateHtml implements MailgunTemplate {

  private String htmlCode;
  private String carbonCopy;

  /**
   * Returns the HTML code of the template. This code is used to render the email content in HTML
   * format.
   */
  @Override
  public Optional<String> getHtmlCode() {
    return Optional.ofNullable(htmlCode);
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

  /**
   * Returns whether the template is in HTML format.
   *
   * @return true if the template is in HTML format, false otherwise
   */
  @Override
  public boolean isHtml() {
    return true;
  }
}
