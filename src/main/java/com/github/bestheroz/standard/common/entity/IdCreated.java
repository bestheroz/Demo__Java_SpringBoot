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
public class IdCreated {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Instant createdAt;

  private UserTypeEnum createdObjectType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_object_id", referencedColumnName = "id")
  @Any
  @JoinColumnOrFormula(column = @JoinColumn(name = "created_object_id"))
  @JoinColumnOrFormula(formula = @JoinFormula(value = "created_object_type"))
  private CreatedOperator createdBy;
}
