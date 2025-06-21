package com.github.bestheroz.demo.specification;

import com.github.bestheroz.demo.domain.Admin;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class AdminSpecification {
  public Specification<Admin> removedFlagIsFalse() {
    return (root, cq, cb) -> cb.equal(root.get("removedFlag"), false);
  }

  public Specification<Admin> equalId(Long id) {
    return (root, cq, cb) -> cb.equal(root.get("id"), id);
  }

  public Specification<Admin> containsLoginId(String loginId) {
    return (root, cq, cb) -> cb.like(root.get("loginId"), "%" + loginId + "%");
  }

  public Specification<Admin> containsName(String name) {
    return (root, cq, cb) -> cb.like(root.get("name"), "%" + name + "%");
  }

  public Specification<Admin> equalUseFlag(Boolean useFlag) {
    return (root, cq, cb) -> cb.equal(root.get("useFlag"), useFlag);
  }

  public Specification<Admin> equalManagerFlag(Boolean managerFlag) {
    return (root, cq, cb) -> cb.equal(root.get("managerFlag"), managerFlag);
  }
}
