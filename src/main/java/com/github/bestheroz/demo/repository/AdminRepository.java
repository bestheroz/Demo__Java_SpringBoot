package com.github.bestheroz.demo.repository;

import com.github.bestheroz.demo.domain.Admin;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository
    extends JpaRepository<Admin, Long>, JpaSpecificationExecutor<Admin> {
  Optional<Admin> findByLoginIdAndRemovedFlagFalse(String loginId);

  Optional<Admin> findByLoginIdAndRemovedFlagFalseAndIdNot(String loginId, Long id);
}
