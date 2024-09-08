package com.github.bestheroz.standard.common.entity.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.IOException;
import java.util.List;

@Converter
public class GenericEnumListJsonConverter<T extends Enum<T>>
    implements AttributeConverter<List<T>, String> {

  private static final ObjectMapper objectMapper = new ObjectMapper();
  private final Class<T> enumClass;

  public GenericEnumListJsonConverter(Class<T> enumClass) {
    this.enumClass = enumClass;
  }

  @Override
  public String convertToDatabaseColumn(List<T> attribute) {
    try {
      return objectMapper.writeValueAsString(attribute);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Error converting list to JSON", e);
    }
  }

  @Override
  public List<T> convertToEntityAttribute(String dbData) {
    try {
      JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, enumClass);
      return objectMapper.readValue(dbData, type);
    } catch (IOException e) {
      throw new RuntimeException("Error converting JSON to list", e);
    }
  }
}
