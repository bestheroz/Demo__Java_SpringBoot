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
public class IdCreated {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Instant createdAt;

  @Column(name = "created_object_type")
  private UserTypeEnum createdObjectType;

  @Column(name = "created_object_id")
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

  public UserSimpleDto getCreatedBy() {
    return switch (this.createdObjectType) {
      case ADMIN -> UserSimpleDto.fromEntity(this.createdByAdmin);
      case USER -> UserSimpleDto.fromEntity(this.createdByUser);
    };
  }
}
