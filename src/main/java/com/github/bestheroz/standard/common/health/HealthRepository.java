package com.github.bestheroz.standard.common.health;

import com.github.bestheroz.demo.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthRepository extends JpaRepository<Admin, Long> {

  @Query(value = "select now()", nativeQuery = true)
  void selectNow();
}
