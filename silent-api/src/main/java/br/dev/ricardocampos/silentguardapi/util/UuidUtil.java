package br.dev.ricardocampos.silentguardapi.util;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Utility class for generating UUIDs based on names, specifically for recipient emails.
 * This class provides methods to generate a unique UUID for a given email address using
 * the SHA-1 hashing algorithm and a predefined namespace.
 */
public class UuidUtil {
  private final UUID namespaceUrl = UUID.fromString("6ba7b811-9dad-11d1-80b4-00c04fd430c8");

  /**
   * Generated a unique UUID to a given email.
   *
   * @param recipients The email to create the UUID.
   * @return The generated UUID.
   */
  public UUID generateRecipientUuid(String recipients) {
    return generateUuidFromName(namespaceUrl, recipients);
  }

  private UUID generateUuidFromName(UUID namespace, String name) {
    // SHA-1 digest of namespace UUID + name
    byte[] namespaceBytes = toBytes(namespace);
    byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);

    byte[] combined = new byte[namespaceBytes.length + nameBytes.length];
    System.arraycopy(namespaceBytes, 0, combined, 0, namespaceBytes.length);
    System.arraycopy(nameBytes, 0, combined, namespaceBytes.length, nameBytes.length);

    byte[] sha1 = sha1(combined);

    // Manipulate bits to make it UUID v5 (version 5, SHA-1)
    sha1[6] &= 0x0f;
    sha1[6] |= 0x50;
    sha1[8] &= 0x3f;
    sha1[8] |= (byte) 0x80;

    return bytesToUuid(sha1);
  }

  private byte[] toBytes(UUID uuid) {
    long msb = uuid.getMostSignificantBits();
    long lsb = uuid.getLeastSignificantBits();
    byte[] bytes = new byte[16];

    for (int i = 0; i < 8; i++) {
      bytes[i] = (byte) ((msb >>> (8 * (7 - i))) & 0xFF);
      bytes[8 + i] = (byte) ((lsb >>> (8 * (7 - i))) & 0xFF);
    }
    return bytes;
  }

  private byte[] sha1(byte[] input) {
    try {
      return java.security.MessageDigest.getInstance("SHA-1").digest(input);
    } catch (Exception e) {
      throw new RuntimeException("SHA-1 algorithm not available");
    }
  }

  private UUID bytesToUuid(byte[] hash) {
    long msb = 0;
    long lsb = 0;
    for (int i = 0; i < 8; i++) {
      msb = (msb << 8) | (hash[i] & 0xff);
    }
    for (int i = 8; i < 16; i++) {
      lsb = (lsb << 8) | (hash[i] & 0xff);
    }
    return new UUID(msb, lsb);
  }
}
