package com.github.bestheroz.demo.repository;

import com.github.bestheroz.demo.entity.Admin;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
  Page<Admin> findAllByRemovedFlagIsFalse(Pageable pageable);

  Optional<Admin> findByLoginIdAndRemovedFlagFalse(String loginId);

  Optional<Admin> findByLoginIdAndRemovedFlagFalseAndIdNot(String loginId, Long id);
}
