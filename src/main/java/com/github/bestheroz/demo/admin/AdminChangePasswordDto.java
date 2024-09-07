package com.github.bestheroz.demo.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

public class AdminChangePasswordDto {
  @Data
  public static class Request {
    @Schema(description = "비밀번호", requiredMode = Schema.RequiredMode.REQUIRED)
    private String oldPassword;

    @Schema(description = "새 비밀번호", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newPassword;
  }
}
