package com.github.bestheroz.demo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

public class UserUpdateDto {
  @EqualsAndHashCode(callSuper = true)
  @Data
  public static class Request extends UserCreateDto.Request {
    @Schema(description = "비밀번호", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String password;
  }
}
