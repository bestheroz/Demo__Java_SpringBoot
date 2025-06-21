package com.github.bestheroz.demo.dtos.admin;

import com.github.bestheroz.demo.domain.Admin;
import com.github.bestheroz.standard.common.dto.IdCreatedUpdatedDto;
import com.github.bestheroz.standard.common.enums.AuthorityEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

public class AdminDto {
  @Data
  @AllArgsConstructor
  public static class Request {
    @Schema(description = "페이지 번호", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer page;

    @Schema(description = "페이지 크기", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer pageSize;

    @Schema(description = "ID(KEY)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;

    @Schema(description = "로그인 아이디", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String loginId;

    @Schema(description = "관리자 이름", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String name;

    @Schema(description = "사용 여부", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean useFlag;

    @Schema(description = "매니저 여부(모든 권한 소유)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean managerFlag;
  }

  @Data
  @EqualsAndHashCode(callSuper = true)
  public static class Response extends IdCreatedUpdatedDto {
    @Schema(description = "로그인 아이디", requiredMode = Schema.RequiredMode.REQUIRED)
    private String loginId;

    @Schema(description = "관리자 이름", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "사용 여부", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean useFlag = true;

    @Schema(description = "매니저 여부(모든 권한 소유)", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean managerFlag;

    @Schema(description = "권한 목록", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<AuthorityEnum> authorities;

    @Schema(description = "가입 일시", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Instant joinedAt;

    @Schema(description = "최근 활동 일시", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Instant latestActiveAt;

    @Schema(description = "비밀번호 변경 일시", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Instant changePasswordAt;

    public static Response of(Admin admin) {
      final Response response = new Response();
      response.setId(admin.getId());
      response.setLoginId(admin.getLoginId());
      response.setName(admin.getName());
      response.setUseFlag(admin.getUseFlag());
      response.setManagerFlag(admin.getManagerFlag());
      response.setAuthorities(admin.getAuthorities());
      response.setJoinedAt(admin.getJoinedAt());
      response.setLatestActiveAt(admin.getLatestActiveAt());
      response.setChangePasswordAt(admin.getChangePasswordAt());
      response.setCreatedAt(admin.getCreatedAt());
      response.setCreatedBy(admin.getCreatedBy());
      response.setUpdatedAt(admin.getUpdatedAt());
      response.setUpdatedBy(admin.getUpdatedBy());
      return response;
    }
  }
}
