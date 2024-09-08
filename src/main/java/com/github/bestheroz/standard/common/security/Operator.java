package com.github.bestheroz.standard.common.security;

import com.github.bestheroz.demo.entity.Admin;
import com.github.bestheroz.demo.entity.User;
import com.github.bestheroz.standard.common.enums.AuthorityEnum;
import com.github.bestheroz.standard.common.enums.UserTypeEnum;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@EqualsAndHashCode(of = "id")
public class Operator implements UserDetails {
  private final Long id;
  private final String loginId;
  private final String name;
  private final UserTypeEnum type;
  private final Boolean managerFlag;
  private final List<AuthorityEnum> authorities;

  public Operator(Admin admin) {
    this.id = admin.getId();
    this.loginId = admin.getLoginId();
    this.name = admin.getName();
    this.type = admin.getType();
    this.managerFlag = admin.getManagerFlag();
    this.authorities = admin.getAuthorities();
  }

  public Operator(User user) {
    this.id = user.getId();
    this.loginId = user.getLoginId();
    this.name = user.getName();
    this.type = user.getType();
    this.managerFlag = false;
    this.authorities = user.getAuthorities();
  }

  public Operator(
      Long id,
      String loginId,
      String name,
      UserTypeEnum type,
      Boolean managerFlag,
      List<AuthorityEnum> authorities) {
    this.id = id;
    this.loginId = loginId;
    this.name = name;
    this.type = type;
    this.managerFlag = managerFlag;
    this.authorities = authorities;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    // 관리자 권한이 있을 경우 전체 권한을 부여하고, 그렇지 않으면 주어진 권한을 사용
    return (this.managerFlag ? Arrays.stream(AuthorityEnum.values()) : authorities.stream())
        .map(authority -> new SimpleGrantedAuthority(authority.name()))
        .toList();
  }

  @Override
  public String getPassword() {
    return null;
  }

  @Override
  public String getUsername() {
    return loginId;
  }

  @Override
  public boolean isAccountNonExpired() {
    // 계정 만료 여부를 여기서 정의 (true로 고정할 경우 만료되지 않음)
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    // 계정 잠금 여부를 여기서 정의 (true로 고정할 경우 잠기지 않음)
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    // 비밀번호 만료 여부 (true로 고정할 경우 만료되지 않음)
    return true;
  }

  @Override
  public boolean isEnabled() {
    // 계정이 활성화되었는지 여부 (useFlag에 따라 결정)
    return true;
  }
}
