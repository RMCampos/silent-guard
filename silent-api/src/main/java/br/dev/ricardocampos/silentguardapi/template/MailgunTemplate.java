package br.dev.ricardocampos.silentguardapi.template;

import java.util.Map;
import java.util.Optional;

/** This interface represents a mailgun template structure. */
public interface MailgunTemplate {

  static final String STRING_SCAPE = "\"";
  static final String COLON = ":";
  static final String COMMA = ",";

  default String getName() {
    return "empty";
  }

  default Map<String, Object> getVariables() {
    return Map.of();
  }

  default Optional<String> getCarbonCopy() {
    return Optional.empty();
  }

  default Optional<String> getHtmlCode() {
    return Optional.empty();
  }

  default boolean isHtml() {
    return false;
  }

  /**
   * Default method to get variables in JSON format.
   *
   * @return The JSON String representation.
   */
  default String getVariableValuesJson() {
    StringBuilder sb = new StringBuilder("{");
    for (Map.Entry<String, Object> entry : getVariables().entrySet()) {
      if (sb.toString().length() > 1) {
        sb.append(COMMA);
      }
      sb.append(STRING_SCAPE).append(entry.getKey()).append(STRING_SCAPE);
      sb.append(COLON);
      sb.append(STRING_SCAPE).append(entry.getValue().toString()).append(STRING_SCAPE);
    }
    sb.append("}");

    return sb.toString();
  }
}
