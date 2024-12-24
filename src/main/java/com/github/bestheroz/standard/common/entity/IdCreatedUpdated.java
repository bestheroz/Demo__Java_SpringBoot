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
public class IdCreatedUpdated extends IdCreated {
  @Column(nullable = false)
  private UserTypeEnum updatedObjectType;

  @Column(nullable = false)
  private Instant updatedAt;

  @Column(name = "updated_object_id", nullable = false)
  private Long updatedObjectId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumnOrFormula(
      formula =
          @JoinFormula(
              value =
                  "CASE WHEN updated_object_type = 'ADMIN' THEN updated_object_id ELSE null END",
              referencedColumnName = "id"))
  private Admin updatedByAdmin;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumnOrFormula(
      formula =
          @JoinFormula(
              value = "CASE WHEN updated_object_type = 'USER' THEN updated_object_id ELSE null END",
              referencedColumnName = "id"))
  private User updatedByUser;

  public void setUpdatedBy(Operator operator, Instant instant) {
    this.updatedAt = instant;
    this.updatedObjectId = operator.getId();
    this.updatedObjectType = operator.getType();
    if (operator.getType().equals(UserTypeEnum.ADMIN)) {
      this.updatedByAdmin = Admin.of(operator);
    } else if (operator.getType().equals(UserTypeEnum.USER)) {
      this.updatedByUser = User.of(operator);
    }
  }

  public UserSimpleDto getUpdatedBy() {
    return switch (this.updatedObjectType) {
      case ADMIN -> UserSimpleDto.of(this.updatedByAdmin);
      case USER -> UserSimpleDto.of(this.updatedByUser);
    };
  }
}
