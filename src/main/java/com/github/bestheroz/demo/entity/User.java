package com.github.bestheroz.demo.entity;

import com.github.bestheroz.standard.common.entity.IdCreatedUpdated;
import com.github.bestheroz.standard.common.entity.converter.JsonAttributeConverter;
import com.github.bestheroz.standard.common.enums.AuthorityEnum;
import com.github.bestheroz.standard.common.enums.UserTypeEnum;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.*;

// 위 파이썬 코드를 참고하여 아래와 같이 작성하자!
@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends IdCreatedUpdated {
  @Column(nullable = false)
  private String loginId;

  private String password;
  private String token;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private Boolean useFlag;

  @Convert(converter = JsonAttributeConverter.class)
  @Column(columnDefinition = "json", nullable = false)
  private List<AuthorityEnum> authorities;

  private Instant changePasswordAt;
  private Instant latestActiveAt;

  private Instant joinedAt;

  @Convert(converter = JsonAttributeConverter.class)
  @Column(columnDefinition = "json", nullable = false)
  private Map<String, Object> additionalInfo;

  @Column(nullable = false)
  private Boolean removedFlag;

  private Instant removedAt;

  public UserTypeEnum getType() {
    return UserTypeEnum.USER;
  }
}
