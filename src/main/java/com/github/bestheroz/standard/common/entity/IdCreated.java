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
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinFormula;

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
  @JoinColumnOrFormula(
      formula =
          @JoinFormula(
              value =
                  "CASE WHEN created_object_type = 'ADMIN' THEN created_object_id ELSE null END",
              referencedColumnName = "id"))
  private Admin createdByAdmin;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumnOrFormula(
      formula =
          @JoinFormula(
              value = "CASE WHEN created_object_type = 'USER' THEN created_object_id ELSE null END",
              referencedColumnName = "id"))
  private User createdByUser;

  public void setCreatedBy(Operator operator, Instant instant) {
    this.createdAt = instant;
    this.createdObjectId = operator.getId();
    this.createdObjectType = operator.getType();
    if (operator.getType().equals(UserTypeEnum.ADMIN)) {
      this.createdByAdmin = Admin.of(operator);
    } else if (operator.getType().equals(UserTypeEnum.USER)) {
      this.createdByUser = User.of(operator);
    }
  }

  public UserSimpleDto getCreatedBy() {
    return switch (this.createdObjectType) {
      case ADMIN -> UserSimpleDto.of(this.createdByAdmin);
      case USER -> UserSimpleDto.of(this.createdByUser);
    };
  }
}
