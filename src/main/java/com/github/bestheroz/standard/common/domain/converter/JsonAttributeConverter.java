package com.github.bestheroz.standard.common.domain.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Converter
@RequiredArgsConstructor
public class JsonAttributeConverter implements AttributeConverter<Object, String> {

  private final ObjectMapper objectMapper;

  @Override
  public String convertToDatabaseColumn(Object attribute) {
    if (attribute == null) {
      return null;
    }
    try {
      return objectMapper.writeValueAsString(attribute);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Object convertToEntityAttribute(String dbData) {
    try {
      if (StringUtils.isEmpty(dbData)) {
        return null;
      }
      if (dbData.startsWith("[")) {
        return objectMapper.readValue(dbData, new TypeReference<List<Object>>() {});
      } else if (dbData.startsWith("{")) {
        return objectMapper.readValue(dbData, new TypeReference<Map<String, Object>>() {});
      }
      return dbData;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
