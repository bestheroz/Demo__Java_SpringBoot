package com.github.bestheroz.demo.entity;

import com.github.bestheroz.standard.common.entity.IdCreatedUpdated;
import com.github.bestheroz.standard.common.entity.converter.JsonAttributeConverter;
import com.github.bestheroz.standard.common.enums.AuthorityEnum;
import com.github.bestheroz.standard.common.enums.UserTypeEnum;
import com.github.bestheroz.standard.common.security.Operator;
import com.github.bestheroz.standard.common.util.PasswordUtil;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("user")
public class User extends IdCreatedUpdated {
  @Column(nullable = false)
  private String loginId;

  private String password;
  private String token;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private Boolean useFlag;

  @Convert(converter = AuthorityEnum.AuthorityEnumListConverter.class)
  @Column(columnDefinition = "json", nullable = false)
  private List<AuthorityEnum> authorities;

  private Instant changePasswordAt;
  private Instant latestActiveAt;

  private Instant joinedAt;

  @Convert(converter = JsonAttributeConverter.class)
  @Column(columnDefinition = "json", nullable = false)
  private Map<String, Object> additionalInfo;

  @Column(nullable = false)
  private Boolean removedFlag;

  private Instant removedAt;

  public UserTypeEnum getType() {
    return UserTypeEnum.USER;
  }

  public User(
      String loginId,
      String password,
      String name,
      Boolean useFlag,
      List<AuthorityEnum> authorities,
      Operator operator) {
    Instant now = Instant.now();
    this.loginId = loginId;
    this.password = PasswordUtil.getPasswordHash(password);
    this.name = name;
    this.useFlag = useFlag;
    this.authorities = authorities;
    this.joinedAt = now;
    this.additionalInfo = Map.of();
    this.removedFlag = false;
    this.setCreatedBy(operator, now);
    this.setUpdatedBy(operator, now);
  }

  public static User fromOperator(Operator operator) {
    User user = new User();
    user.setId(operator.getId());
    user.setLoginId(operator.getLoginId());
    user.setName(operator.getName());
    return user;
  }

  public void update(
      String loginId,
      String password,
      String name,
      Boolean useFlag,
      List<AuthorityEnum> authorities,
      Operator operator) {
    this.loginId = loginId;
    this.name = name;
    this.useFlag = useFlag;
    this.authorities = authorities;
    Instant now = Instant.now();
    this.setUpdatedBy(operator, now);
    if (StringUtils.isNotEmpty(password)) {
      this.password = PasswordUtil.getPasswordHash(password);
      this.changePasswordAt = now;
    }
  }

  public void changePassword(String password, Operator operator) {
    this.password = PasswordUtil.getPasswordHash(password);
    Instant now = Instant.now();
    this.changePasswordAt = now;
    this.setUpdatedBy(operator, now);
  }

  public void remove(Operator operator) {
    this.removedFlag = true;
    Instant now = Instant.now();
    this.removedAt = now;
    this.setUpdatedBy(operator, now);
  }

  public void renewToken(String token) {
    this.token = token;
    this.latestActiveAt = Instant.now();
  }

  public void logout() {
    this.token = null;
  }
}
