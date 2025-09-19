package br.dev.ricardocampos.silentguardapi.enums;

/**
 * Enum representing the types of time units that can be used to trigger actions.
 */
public enum TypeToTriggerEnum {
  DAYS,
  HOURS,
  MINUTES;

  public static TypeToTriggerEnum fromString(String value) {
    for (TypeToTriggerEnum type : TypeToTriggerEnum.values()) {
      if (type.name().equalsIgnoreCase(value)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Invalid type: " + value);
  }
}
