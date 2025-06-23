package com.github.bestheroz.demo.repository;

import com.github.bestheroz.demo.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
  Optional<User> findByLoginIdAndRemovedFlagFalse(String loginId);

  Optional<User> findByLoginIdAndRemovedFlagFalseAndIdNot(String loginId, Long id);
}
