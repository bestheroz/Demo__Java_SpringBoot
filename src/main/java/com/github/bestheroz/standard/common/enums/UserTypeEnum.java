package com.github.bestheroz.standard.common.enums;

// class EventType(StrEnum):
//    CREATE = "CREATE"
//    UPDATE = "UPDATE"
//    DELETE = "DELETE"
//
//
// class UserTypeEnum(StrEnum):
//    admin = "admin"
//    user = "user"
//
//
// class AuthorityEnum(StrEnum):
//    ADMIN_VIEW = "ADMIN_VIEW"
//    ADMIN_EDIT = "ADMIN_EDIT"
//
//    USER_VIEW = "USER_VIEW"
//    USER_EDIT = "USER_EDIT"
//
//    NOTICE_VIEW = "NOTICE_VIEW"
//    NOTICE_EDIT = "NOTICE_EDIT"

public enum UserTypeEnum {
  ADMIN("admin"),
  USER("user");

  private final String value;

  UserTypeEnum(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
