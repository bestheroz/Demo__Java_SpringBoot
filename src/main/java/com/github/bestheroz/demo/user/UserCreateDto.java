package com.github.bestheroz.demo.user;

import com.github.bestheroz.demo.entity.User;
import com.github.bestheroz.standard.common.enums.AuthorityEnum;
import com.github.bestheroz.standard.common.security.Operator;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

public class UserCreateDto {
  @Data
  public static class Request {
    @Schema(description = "로그인 아이디", requiredMode = Schema.RequiredMode.REQUIRED)
    private String loginId;

    @Schema(description = "비밀번호", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(description = "유저 이름", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "사용 여부", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean useFlag;

    @Schema(description = "매니저 여부(모든 권한 소유)", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean managerFlag;

    @Schema(description = "권한 목록", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<AuthorityEnum> authorities;

    public User toEntity(Operator operator) {
      return User.of(
          this.loginId, this.password, this.name, this.useFlag, this.authorities, operator);
    }
  }
}
