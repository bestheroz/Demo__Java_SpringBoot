package com.github.bestheroz.demo.dtos.user;

import com.github.bestheroz.demo.domain.User;
import com.github.bestheroz.standard.common.dto.IdCreatedUpdatedDto;
import com.github.bestheroz.standard.common.enums.AuthorityEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

public class UserDto {
  @Data
  @AllArgsConstructor
  public static class Request {
    @Schema(description = "페이지 번호", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer page;

    @Schema(description = "페이지 크기", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer pageSize;
  }

  @Data
  @EqualsAndHashCode(callSuper = true)
  public static class Response extends IdCreatedUpdatedDto {
    @Schema(description = "로그인 아이디", requiredMode = Schema.RequiredMode.REQUIRED)
    private String loginId;

    @Schema(description = "유저 이름", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "사용 여부", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean useFlag = true;

    @Schema(description = "권한 목록", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<AuthorityEnum> authorities;

    @Schema(description = "가입 일시", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Instant joinedAt;

    @Schema(description = "최근 활동 일시", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Instant latestActiveAt;

    @Schema(description = "비밀번호 변경 일시", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Instant changePasswordAt;

    public static Response of(User user) {
      final Response response = new Response();
      response.setId(user.getId());
      response.setLoginId(user.getLoginId());
      response.setName(user.getName());
      response.setUseFlag(user.getUseFlag());
      response.setAuthorities(user.getAuthorities());
      response.setJoinedAt(user.getJoinedAt());
      response.setLatestActiveAt(user.getLatestActiveAt());
      response.setChangePasswordAt(user.getChangePasswordAt());
      response.setCreatedAt(user.getCreatedAt());
      response.setCreatedBy(user.getCreatedBy());
      response.setUpdatedAt(user.getUpdatedAt());
      response.setUpdatedBy(user.getUpdatedBy());
      return response;
    }
  }
}
