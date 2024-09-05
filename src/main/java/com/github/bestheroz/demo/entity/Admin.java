package com.github.bestheroz.demo.entity;

import com.github.bestheroz.standard.common.entity.IdCreatedUpdated;
import com.github.bestheroz.standard.common.entity.converter.JsonAttributeConverter;
import com.github.bestheroz.standard.common.enums.AuthorityEnum;
import com.github.bestheroz.standard.common.enums.UserTypeEnum;
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

  //  public static Admin of(final AdminCreate data, final int operatorId) {
  //    final Instant now = Instant.now();
  //    return new Admin(
  //        data.getLoginId(),
  //        data.getPassword(),
  //        data.getToken(),
  //        data.getName(),
  //        data.getUseFlag(),
  //        data.getManagerFlag(),
  //        data.getAuthorities(),
  //        data.getChangePasswordAt(),
  //        data.getLatestActiveAt(),
  //        data.getJoinedAt(),
  //        data.getRemovedFlag(),
  //        data.getRemovedAt(),
  //        data.getCreatedBy(),
  //        data.getUpdatedBy(),
  //        now,
  //        now,
  //        operatorId,
  //        UserTypeEnum.ADMIN,
  //        now,
  //        operatorId,
  //        UserTypeEnum.ADMIN);
  //  }
  //
  //  public void update(final AdminUpdate data, final int operatorId) {
  //    final Instant now = Instant.now();
  //    this.loginId = data.getLoginId();
  //    this.name = data.getName();
  //    this.useFlag = data.getUseFlag();
  //    this.authorities = data.getAuthorities();
  //    this.managerFlag = data.getManagerFlag();
  //    this.updatedAt = now;
  //    this.updatedById = operatorId;
  //    this.updatedObjectType = UserTypeEnum.ADMIN;
  //    if (data.getPassword() != null && !data.getPassword().isEmpty()) {
  //      this.password = data.getPassword();
  //      this.changePasswordAt = now;
  //    }
  //  }
  //
  //  public void changePassword(final String password, final Operator operator) {
  //    final Instant now = Instant.now();
  //    this.password = password;
  //    this.changePasswordAt = now;
  //    this.updatedAt = now;
  //    this.updatedById = operator.getId();
  //    this.updatedObjectType = operator.getType();
  //  }
  //
  //  public void remove(final int operatorId) {
  //    final Instant now = Instant.now();
  //    this.removedFlag = true;
  //    this.removedAt = now;
  //    this.updatedAt = now;
  //    this.updatedById = operatorId;
  //    this.updatedObjectType = UserTypeEnum.ADMIN;
  //  }
  //
  //  public void renewToken() {
  //    this.token = createRefreshToken(this);
  //    this.latestActiveAt = Instant.now();
  //  }
  //
  //  public void signOut() {
  //    this.token = null;
  //  }
}
