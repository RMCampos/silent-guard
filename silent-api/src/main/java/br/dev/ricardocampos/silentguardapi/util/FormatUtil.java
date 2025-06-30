package br.dev.ricardocampos.silentguardapi.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Objects;

/**
 * Utility class for formatting time-related information. This class provides methods to format a
 * pastime into a human-readable "time ago" format and to format a duration into a string
 * representation.
 */
public class FormatUtil {

  /**
   * Formats a given pastime into a human-readable "time ago" format.
   *
   * @param pastTime the pastime to format
   * @return a string representing the time elapsed since the pastime, or "none" if the input is
   *     null
   */
  public static String formatTimeAgo(LocalDateTime pastTime) {
    if (Objects.isNull(pastTime)) {
      return "none";
    }
    LocalDateTime now = LocalDateTime.now();
    Period period = Period.between(pastTime.toLocalDate(), now.toLocalDate());
    Duration duration = Duration.between(pastTime, now);
    if (period.getYears() > 1) {
      return String.format("%d years ago", period.getYears());
    } else if (period.getYears() > 0) {
      return String.format("%d year ago", period.getYears());
    } else if (period.getMonths() > 1) {
      return String.format("%d months ago", period.getMonths());
    } else if (period.getMonths() > 0) {
      return String.format("%d month ago", period.getMonths());
    } else if (period.getDays() > 1) {
      return String.format("%d days ago", period.getDays());
    } else if (period.getDays() > 0) {
      return String.format("%d day ago", period.getDays());
    } else if (duration.toHours() > 1L) {
      return String.format("%d hours ago", duration.toHours());
    } else if (duration.toHours() > 0L) {
      return String.format("%d hour ago", duration.toHours());
    } else if (duration.toMinutes() > 1L) {
      return String.format("%d minutes ago", duration.toMinutes());
    } else if (duration.toMinutes() > 0L) {
      return String.format("%d minute ago", duration.toMinutes());
    } else if (duration.toSeconds() > 1L) {
      return String.format("%d seconds ago", duration.toSeconds());
    } else {
      return "Moments ago";
    }
  }

  /**
   * Formats a given duration into a human-readable string representation.
   *
   * @param duration the duration to format
   * @return a string representing the duration in days, hours, minutes, and seconds
   */
  public static String formatDuration(Duration duration) {
    if (duration == null) {
      return "0 seconds";
    }

    long totalSeconds = duration.getSeconds();
    long days = totalSeconds / 86400;
    long hours = (totalSeconds % 86400) / 3600;
    long minutes = (totalSeconds % 3600) / 60;
    long seconds = totalSeconds % 60;

    StringBuilder result = new StringBuilder();

    if (days > 0) result.append(days).append("d ");
    if (hours > 0) result.append(hours).append("h ");
    if (minutes > 0) result.append(minutes).append("m ");
    if (seconds > 0 || result.isEmpty()) result.append(seconds).append("s");

    return result.toString().trim();
  }
}
