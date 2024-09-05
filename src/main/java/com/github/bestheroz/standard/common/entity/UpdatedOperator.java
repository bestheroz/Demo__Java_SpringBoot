package com.github.bestheroz.standard.common.entity;

import com.github.bestheroz.standard.common.enums.UserTypeEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "updated_object_type", discriminatorType = DiscriminatorType.STRING)
public abstract class UpdatedOperator {
  @Id private Long id;
  private UserTypeEnum type;
  private Boolean managerFlag;
  private String loginId;
  private String name;
}
