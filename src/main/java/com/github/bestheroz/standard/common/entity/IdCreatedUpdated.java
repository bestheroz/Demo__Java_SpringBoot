package com.github.bestheroz.standard.common.entity;

import com.github.bestheroz.demo.entity.Admin;
import com.github.bestheroz.demo.entity.User;
import com.github.bestheroz.standard.common.dto.UserSimpleDto;
import com.github.bestheroz.standard.common.enums.UserTypeEnum;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class IdCreatedUpdated extends IdCreated {
  @Column(name = "updated_object_type")
  private UserTypeEnum updatedObjectType;

  private Instant updatedAt;

  @Column(name = "updated_object_id")
  private Long updatedObjectId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "updated_object_id",
      referencedColumnName = "id",
      insertable = false,
      updatable = false)
  private Admin updatedByAdmin;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "updated_object_id",
      referencedColumnName = "id",
      insertable = false,
      updatable = false)
  private User updatedByUser;

  public UserSimpleDto getUpdatedBy() {
    return switch (this.updatedObjectType) {
      case ADMIN -> UserSimpleDto.fromEntity(this.updatedByAdmin);
      case USER -> UserSimpleDto.fromEntity(this.updatedByUser);
    };
  }
}
