package com.github.bestheroz.demo.notice;

import com.github.bestheroz.demo.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {
  private final NoticeRepository noticeRepository;

  @Transactional(readOnly = true)
  public Page<NoticeDto.Response> getNotice(NoticeDto.Request request) {
    return noticeRepository
        .findAll(PageRequest.of(request.getPage(), request.getPageSize()))
        .map(NoticeDto.Response::fromEntity);
  }
}
