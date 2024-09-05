package com.github.bestheroz.standard.common.entity;

import com.github.bestheroz.standard.common.enums.UserTypeEnum;
import jakarta.persistence.*;
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
  @Column(name = "updated_object_type", columnDefinition = "VARCHAR(10)")
  private UserTypeEnum updatedObjectType;

  private Instant updatedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "updated_object_id", referencedColumnName = "id")
  @Any
  @JoinColumnOrFormula(column = @JoinColumn(name = "updated_object_id"))
  @JoinColumnOrFormula(formula = @JoinFormula(value = "updated_object_type"))
  private UpdatedOperator updatedBy;
}
