package com.github.bestheroz.demo.entity;

import com.github.bestheroz.standard.common.entity.IdCreatedUpdated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice extends IdCreatedUpdated {
  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String content;

  @Column(nullable = false)
  private Boolean useFlag;

  @Column(nullable = false)
  private Boolean removedFlag;

  private Instant removedAt;
}
