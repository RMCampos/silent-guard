package br.dev.ricardocampos.silentguardapi.template;

import java.util.Optional;
import lombok.Setter;

@Setter
public class MailgunTemplateHtml implements MailgunTemplate {

  private String htmlCode;
  private String carbonCopy;

  @Override
  public Optional<String> getHtmlCode() {
    return Optional.ofNullable(htmlCode);
  }

  @Override
  public Optional<String> getCarbonCopy() {
    return Optional.ofNullable(carbonCopy);
  }

  @Override
  public boolean isHtml() {
    return true;
  }
}
