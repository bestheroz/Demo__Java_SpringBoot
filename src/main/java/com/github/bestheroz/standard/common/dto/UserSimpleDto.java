package com.github.bestheroz.standard.common.dto;

import com.github.bestheroz.demo.entity.Admin;
import com.github.bestheroz.demo.entity.User;
import com.github.bestheroz.standard.common.enums.UserTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserSimpleDto {
  @Schema(description = "ID(KEY)")
  private Long id;

  @Schema(description = "관리자 or 유저")
  private UserTypeEnum type;

  @Schema(description = "관리자 ID or 유저 계정 ID")
  private String loginId;

  @Schema(description = "관리자 이름 or 유저 이름")
  private String name;

  public static UserSimpleDto fromEntity(Admin entity) {
    return new UserSimpleDto(
        entity.getId(), entity.getType(), entity.getLoginId(), entity.getName());
  }

  public static UserSimpleDto fromEntity(User entity) {
    return new UserSimpleDto(
        entity.getId(), entity.getType(), entity.getLoginId(), entity.getName());
  }
}