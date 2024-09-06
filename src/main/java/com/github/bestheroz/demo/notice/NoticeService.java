package com.github.bestheroz.demo.notice;

import com.github.bestheroz.demo.repository.NoticeRepository;
import com.github.bestheroz.standard.common.dto.ListResult;
import com.github.bestheroz.standard.common.exception.ExceptionCode;
import com.github.bestheroz.standard.common.exception.RequestException400;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {
  private final NoticeRepository noticeRepository;

  @Transactional(readOnly = true)
  public ListResult<NoticeDto.Response> getNotices(NoticeDto.Request request) {
    return ListResult.of(
        noticeRepository
            .findAll(PageRequest.of(request.getPage(), request.getPageSize()))
            .map(NoticeDto.Response::fromEntity));
  }

  @Transactional(readOnly = true)
  public NoticeDto.Response getNotice(Long id) {
    return noticeRepository
        .findById(id)
        .map(NoticeDto.Response::fromEntity)
        .orElseThrow(() -> new RequestException400(ExceptionCode.UNKNOWN_NOTICE));
  }
}
