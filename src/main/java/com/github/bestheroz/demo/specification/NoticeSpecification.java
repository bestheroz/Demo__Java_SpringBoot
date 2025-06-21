package com.github.bestheroz.demo.specification;

import com.github.bestheroz.demo.domain.Notice;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class NoticeSpecification {
  public Specification<Notice> removedFlagIsFalse() {
    return (root, cq, cb) -> cb.equal(root.get("removedFlag"), false);
  }

  public Specification<Notice> equalId(Long id) {
    return (root, cq, cb) -> cb.equal(root.get("id"), id);
  }

  public Specification<Notice> containsTitle(String title) {
    return (root, cq, cb) -> cb.like(root.get("title"), "%" + title + "%");
  }

  public Specification<Notice> equalUseFlag(Boolean useFlag) {
    return (root, cq, cb) -> cb.equal(root.get("useFlag"), useFlag);
  }
}
