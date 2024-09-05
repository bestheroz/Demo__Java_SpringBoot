package com.github.bestheroz.standard.common.enums;

public enum EventTypeEnum {
  CREATE("CREATE"),
  UPDATE("UPDATE"),
  DELETE("DELETE");

  private final String value;

  EventTypeEnum(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
