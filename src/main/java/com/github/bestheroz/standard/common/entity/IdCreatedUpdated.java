package com.github.bestheroz.standard.common.entity;

import com.github.bestheroz.standard.common.enums.UserTypeEnum;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Any;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinFormula;

@Getter
@Setter
@MappedSuperclass
public class IdCreatedUpdated extends IdCreated {
  private UserTypeEnum updatedObjectType;
  private Instant updatedAt;

  private Long updatedObjectId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "updated_by_id", referencedColumnName = "id")
  @Any
  @JoinColumnOrFormula(column = @JoinColumn(name = "updated_object_id"))
  @JoinColumnOrFormula(formula = @JoinFormula(value = "updated_object_type"))
  private Operator updatedBy;
}
