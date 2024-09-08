package com.github.bestheroz.standard.common.entity.converter;

// @Converter(autoApply = true)
// public class InstantStringConverter implements AttributeConverter<Instant, String> {
//
//  private static final DateTimeFormatter formatter =
//      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//
//  @Override
//  public String convertToDatabaseColumn(Instant instant) {
//    return instant != null ? instant.toString() : null;
//  }
//
//  @Override
//  public Instant convertToEntityAttribute(String dbData) {
//    try {
//      return dbData != null ? Instant.parse(dbData) : null;
//    } catch (Exception e) {
//      return LocalDateTime.parse(dbData, formatter).toInstant(ZoneOffset.UTC);
//    }
//  }
// }
