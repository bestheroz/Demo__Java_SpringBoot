package com.github.bestheroz.demo.specification;

import com.github.bestheroz.demo.domain.User;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class UserSpecification {
  public Specification<User> removedFlagIsFalse() {
    return (root, cq, cb) -> cb.equal(root.get("removedFlag"), false);
  }

  public Specification<User> equalId(Long id) {
    return (root, cq, cb) -> cb.equal(root.get("id"), id);
  }

  public Specification<User> containsLoginId(String loginId) {
    return (root, cq, cb) -> cb.like(root.get("loginId"), "%" + loginId + "%");
  }

  public Specification<User> containsName(String name) {
    return (root, cq, cb) -> cb.like(root.get("name"), "%" + name + "%");
  }

  public Specification<User> equalUseFlag(Boolean useFlag) {
    return (root, cq, cb) -> cb.equal(root.get("useFlag"), useFlag);
  }
}
