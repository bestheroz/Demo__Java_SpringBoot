package com.github.bestheroz.demo.notice;

import com.github.bestheroz.demo.repository.NoticeRepository;
import com.github.bestheroz.standard.common.dto.ListResult;
import com.github.bestheroz.standard.common.exception.ExceptionCode;
import com.github.bestheroz.standard.common.exception.RequestException400;
import com.github.bestheroz.standard.common.security.Operator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {
  private final NoticeRepository noticeRepository;

  @Transactional(readOnly = true)
  public ListResult<NoticeDto.Response> getNoticeList(NoticeDto.Request request) {
    return ListResult.of(
        noticeRepository
            .findAllByRemovedFlagIsFalse(
                PageRequest.of(
                    request.getPage() - 1, request.getPageSize(), Sort.by("id").descending()))
            .map(NoticeDto.Response::of));
  }

  @Transactional(readOnly = true)
  public NoticeDto.Response getNotice(Long id) {
    return noticeRepository
        .findById(id)
        .map(NoticeDto.Response::of)
        .orElseThrow(() -> new RequestException400(ExceptionCode.UNKNOWN_NOTICE));
  }

  public NoticeDto.Response createNotice(NoticeCreateDto.Request request, Operator operator) {
    return NoticeDto.Response.of(noticeRepository.save(request.toEntity(operator)));
  }

  public NoticeDto.Response updateNotice(
      Long id, NoticeCreateDto.Request request, Operator operator) {
    return NoticeDto.Response.of(
        noticeRepository
            .findById(id)
            .map(
                notice -> {
                  notice.update(
                      request.getTitle(), request.getContent(), request.getUseFlag(), operator);
                  return notice;
                })
            .orElseThrow(() -> new RequestException400(ExceptionCode.UNKNOWN_NOTICE)));
  }

  public void deleteNotice(Long id, Operator operator) {
    noticeRepository
        .findById(id)
        .orElseThrow(() -> new RequestException400(ExceptionCode.UNKNOWN_NOTICE))
        .remove(operator);
  }
}
