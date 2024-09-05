package com.github.bestheroz.standard.common.util;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

@UtilityClass
@Slf4j
public class DateUtils {

  public String toStringNow(final String pattern) {
    Assert.hasText(pattern, "pattern parameter must not be empty or null");
    return toString(Instant.now(), pattern);
  }

  public String toString(final Instant instant, final String pattern) {
    Assert.hasText(pattern, "pattern parameter must not be empty or null");
    return toString(instant, pattern, ZoneId.of("UTC"));
  }

  public String toString(final Instant instant, final String pattern, final ZoneId zoneId) {
    Assert.hasText(pattern, "pattern parameter must not be empty or null");
    return Optional.ofNullable(instant)
        .map(
            item ->
                OffsetDateTime.ofInstant(item, zoneId).format(DateTimeFormatter.ofPattern(pattern)))
        .orElse(null);
  }

  public String toString(final Long timestamp, final String pattern) {
    Assert.hasText(pattern, "pattern parameter must not be empty or null");
    return toString(timestamp, pattern, ZoneId.of("UTC"));
  }

  public String toString(final Long timestamp, final String pattern, final ZoneId zoneId) {
    Assert.hasText(pattern, "pattern parameter must not be empty or null");
    return Optional.ofNullable(timestamp)
        .map(
            item ->
                OffsetDateTime.ofInstant(Instant.ofEpochMilli(item), zoneId)
                    .format(DateTimeFormatter.ofPattern(pattern)))
        .orElse(null);
  }

  public String toString(final Date date, final String pattern) {
    Assert.hasText(pattern, "pattern parameter must not be empty or null");
    return Optional.ofNullable(date)
        .map(item -> toString(item.getTime(), pattern, ZoneId.of("UTC")))
        .orElse(StringUtils.EMPTY);
  }

  public String toString(final String string, final String fromPattern, final String toPattern) {
    Assert.hasText(fromPattern, "fromPattern parameter must not be empty or null");
    Assert.hasText(toPattern, "toPattern parameter must not be empty or null");
    return Optional.ofNullable(string)
        .map(
            item ->
                parseOffsetDateTime(item, fromPattern)
                    .format(DateTimeFormatter.ofPattern(toPattern)))
        .orElse(StringUtils.EMPTY);
  }

  public OffsetDateTime parseOffsetDateTime(final String text, final String pattern) {
    Assert.hasText(pattern, "pattern parameter must not be empty or null");
    return Optional.ofNullable(text)
        .map(item -> OffsetDateTime.parse(item, DateTimeFormatter.ofPattern(pattern)))
        .orElse(null);
  }

  public OffsetDateTime parseOffsetDateTimeAtUTC(final String text) {
    return Optional.ofNullable(text).map(item -> OffsetDateTime.parse(text)).orElse(null);
  }
}
