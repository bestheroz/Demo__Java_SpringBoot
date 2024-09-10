package com.github.bestheroz.standard.common.entity;

import com.github.bestheroz.demo.entity.Admin;
import com.github.bestheroz.demo.entity.User;
import com.github.bestheroz.standard.common.dto.UserSimpleDto;
import com.github.bestheroz.standard.common.enums.UserTypeEnum;
import com.github.bestheroz.standard.common.security.Operator;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class IdCreated {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @Column(nullable = false, updatable = false)
  private UserTypeEnum createdObjectType;

  @Column(name = "created_object_id", nullable = false, updatable = false)
  private Long createdObjectId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "created_object_id",
      referencedColumnName = "id",
      insertable = false,
      updatable = false)
  private Admin createdByAdmin;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "created_object_id",
      referencedColumnName = "id",
      insertable = false,
      updatable = false)
  private User createdByUser;

  public void setCreatedBy(Operator operator, Instant instant) {
    if (operator.getType().equals(UserTypeEnum.ADMIN)) {
      this.createdObjectType = UserTypeEnum.ADMIN;
      this.createdByAdmin = Admin.of(operator);
    } else if (operator.getType().equals(UserTypeEnum.USER)) {
      this.createdObjectType = UserTypeEnum.USER;
      this.createdByUser = User.of(operator);
    }
    this.setCreatedAt(instant);
    this.setCreatedObjectId(operator.getId());
    this.setCreatedObjectType(operator.getType());
  }

  public UserSimpleDto getCreatedBy() {
    return switch (this.createdObjectType) {
      case ADMIN -> UserSimpleDto.of(this.createdByAdmin);
      case USER -> UserSimpleDto.of(this.createdByUser);
    };
  }
}
