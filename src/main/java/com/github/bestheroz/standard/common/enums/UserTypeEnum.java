package com.github.bestheroz.standard.common.enums;

import com.github.bestheroz.standard.common.entity.converter.GenericEnumConverter;
import jakarta.persistence.Converter;
import lombok.Getter;

@Getter
public enum UserTypeEnum {
  ADMIN("admin"),
  USER("user");

  private final String value;

  UserTypeEnum(String value) {
    this.value = value;
  }

  @Converter(autoApply = true)
  public static class EnumConverter extends GenericEnumConverter<UserTypeEnum> {}
}
