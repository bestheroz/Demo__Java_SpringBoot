package com.github.bestheroz.standard.common.dto;

import com.github.bestheroz.demo.domain.Admin;
import com.github.bestheroz.demo.domain.User;
import com.github.bestheroz.standard.common.enums.UserTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;

public record UserSimpleDto(
    @Schema(description = "ID(KEY)", requiredMode = Schema.RequiredMode.REQUIRED) Long id,
    @Schema(description = "관리자 or 유저", requiredMode = Schema.RequiredMode.REQUIRED)
        UserTypeEnum type,
    @Schema(description = "관리자 ID or 유저 계정 ID", requiredMode = Schema.RequiredMode.REQUIRED)
        String loginId,
    @Schema(description = "관리자 이름 or 유저 이름", requiredMode = Schema.RequiredMode.REQUIRED)
        String name) {

  public static UserSimpleDto of(Admin entity) {
    return new UserSimpleDto(
        entity.getId(), entity.getType(), entity.getLoginId(), entity.getName());
  }

  public static UserSimpleDto of(User entity) {
    return new UserSimpleDto(
        entity.getId(), entity.getType(), entity.getLoginId(), entity.getName());
  }
}
