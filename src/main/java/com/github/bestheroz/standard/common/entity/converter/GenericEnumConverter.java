package com.github.bestheroz.standard.common.entity.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.stream.Stream;

@Converter
public class GenericEnumConverter<T extends Enum<T>> implements AttributeConverter<T, String> {

  private final Class<T> enumClass;

  @SuppressWarnings("unchecked")
  public GenericEnumConverter() {
    this.enumClass =
        (Class<T>)
            ((java.lang.reflect.ParameterizedType) this.getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
  }

  @Override
  public String convertToDatabaseColumn(T attribute) {
    if (attribute == null) {
      return null;
    }
    return attribute.name().toLowerCase();
  }

  @Override
  public T convertToEntityAttribute(String dbData) {
    if (dbData == null) {
      return null;
    }
    return Stream.of(enumClass.getEnumConstants())
        .filter(e -> e.name().toLowerCase().equals(dbData))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unknown enum value: " + dbData));
  }
}
