package com.github.bestheroz.demo.entity;

import com.github.bestheroz.standard.common.entity.IdCreatedUpdated;
import com.github.bestheroz.standard.common.security.Operator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.io.Serializable;
import java.time.Instant;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice extends IdCreatedUpdated implements Serializable {
  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String content;

  @Column(nullable = false)
  private Boolean useFlag;

  @Column(nullable = false)
  private Boolean removedFlag;

  private Instant removedAt;

  public Notice(String title, String content, Boolean useFlag, Operator operator) {
    this.title = title;
    this.content = content;
    this.useFlag = useFlag;
    this.removedFlag = false;
    Instant now = Instant.now();
    this.setCreatedBy(operator, now);
    this.setUpdatedBy(operator, now);
  }

  public void update(String title, String content, Boolean useFlag, Operator operator) {
    this.title = title;
    this.content = content;
    this.useFlag = useFlag;
    Instant now = Instant.now();
    this.setUpdatedBy(operator, now);
  }

  public void remove(Operator operator) {
    this.removedFlag = true;
    Instant now = Instant.now();
    this.removedAt = now;
    this.setUpdatedBy(operator, now);
  }
}
