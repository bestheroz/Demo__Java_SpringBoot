package com.github.bestheroz.demo.entity;

import com.github.bestheroz.standard.common.entity.IdCreatedUpdated;
import com.github.bestheroz.standard.common.entity.converter.JsonAttributeConverter;
import com.github.bestheroz.standard.common.enums.AuthorityEnum;
import com.github.bestheroz.standard.common.enums.UserTypeEnum;
import com.github.bestheroz.standard.common.security.Operator;
import com.github.bestheroz.standard.common.util.PasswordUtil;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.Instant;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 위 파이썬 코드를 참고하여 아래와 같이 작성하자!
@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("admin")
public class Admin extends IdCreatedUpdated {
  @Column(nullable = false)
  private String loginId;

  private String password;
  private String token;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private Boolean useFlag;

  @Column(nullable = false)
  private Boolean managerFlag;

  @Convert(converter = JsonAttributeConverter.class)
  @Column(columnDefinition = "json", nullable = false)
  private List<AuthorityEnum> authorities;

  private Instant changePasswordAt;
  private Instant latestActiveAt;

  private Instant joinedAt;

  @Column(nullable = false)
  private Boolean removedFlag;

  private Instant removedAt;

  public UserTypeEnum getType() {
    return UserTypeEnum.ADMIN;
  }

  public List<AuthorityEnum> getAuthorities() {
    return this.managerFlag ? List.of(AuthorityEnum.values()) : this.authorities;
  }

  public Admin(
      String loginId,
      String password,
      String name,
      Boolean useFlag,
      Boolean managerFlag,
      List<AuthorityEnum> authorities,
      Operator operator) {
    Instant now = Instant.now();
    this.loginId = loginId;
    this.password = PasswordUtil.getPasswordHash(password);
    ;
    this.name = name;
    this.useFlag = useFlag;
    this.managerFlag = managerFlag;
    this.authorities = authorities;
    this.joinedAt = now;
    this.removedFlag = false;
    this.setCreatedAt(now);
    this.setCreatedObjectId(operator.getId());
    this.setCreatedObjectType(operator.getType());
    this.setUpdatedAt(now);
    this.setUpdatedObjectId(operator.getId());
    this.setUpdatedObjectType(operator.getType());
  }

  public void update(
      String loginId,
      String password,
      String name,
      Boolean useFlag,
      Boolean managerFlag,
      List<AuthorityEnum> authorities,
      Operator operator) {
    this.loginId = loginId;
    this.name = name;
    this.useFlag = useFlag;
    this.managerFlag = managerFlag;
    this.authorities = authorities;
    this.setUpdatedAt(Instant.now());
    this.setUpdatedObjectId(operator.getId());
    this.setUpdatedObjectType(operator.getType());
    if (StringUtils.isNotEmpty(password)) {
      this.password = PasswordUtil.getPasswordHash(password);
      this.changePasswordAt = Instant.now();
    }
  }

  public void changePassword(String password, Operator operator) {
    this.password = PasswordUtil.getPasswordHash(password);
    Instant now = Instant.now();
    this.changePasswordAt = now;
    this.setUpdatedAt(now);
    this.setUpdatedObjectId(operator.getId());
    this.setUpdatedObjectType(operator.getType());
  }

  public void remove(Operator operator) {
    this.removedFlag = true;
    Instant now = Instant.now();
    this.removedAt = now;
    this.setUpdatedAt(now);
    this.setUpdatedObjectId(operator.getId());
    this.setUpdatedObjectType(operator.getType());
  }

  public void renewToken(String token) {
    this.token = token;
    this.latestActiveAt = Instant.now();
  }

  public void logout() {
    this.token = null;
  }
}
