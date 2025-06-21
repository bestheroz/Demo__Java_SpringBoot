package com.github.bestheroz.demo.repository;

import com.github.bestheroz.demo.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository
    extends JpaRepository<Notice, Long>, JpaSpecificationExecutor<Notice> {}
