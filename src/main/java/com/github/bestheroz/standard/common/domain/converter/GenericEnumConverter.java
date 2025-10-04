package com.github.bestheroz.standard.common.domain.converter;

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
    return attribute != null ? attribute.name() : null;
  }

  @Override
  public T convertToEntityAttribute(String dbData) {
    return dbData != null
        ? Stream.of(enumClass.getEnumConstants())
            .filter(e -> e.name().equals(dbData))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown enum value: " + dbData))
        : null;
  }
}
