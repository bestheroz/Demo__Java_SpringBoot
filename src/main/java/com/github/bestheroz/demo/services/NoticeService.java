package com.github.bestheroz.demo.services;

import com.github.bestheroz.demo.domain.Notice;
import com.github.bestheroz.demo.dtos.notice.NoticeCreateDto;
import com.github.bestheroz.demo.dtos.notice.NoticeDto;
import com.github.bestheroz.demo.repository.NoticeRepository;
import com.github.bestheroz.demo.specification.NoticeSpecification;
import com.github.bestheroz.standard.common.dto.ListResult;
import com.github.bestheroz.standard.common.exception.ExceptionCode;
import com.github.bestheroz.standard.common.exception.RequestException400;
import com.github.bestheroz.standard.common.security.Operator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeService {
  private final NoticeRepository noticeRepository;

  public ListResult<NoticeDto.Response> getNoticeList(NoticeDto.Request request) {
    List<Specification<Notice>> specs =
        Stream.of(
                NoticeSpecification.removedFlagIsFalse(),
                request.getId() != null ? NoticeSpecification.equalId(request.getId()) : null,
                request.getTitle() != null
                    ? NoticeSpecification.containsTitle(request.getTitle())
                    : null,
                request.getUseFlag() != null
                    ? NoticeSpecification.equalUseFlag(request.getUseFlag())
                    : null)
            .filter(Objects::nonNull)
            .toList();

    Specification<Notice> spec =
        specs.isEmpty() ? null : specs.stream().reduce(Specification::and).orElse(null);

    return ListResult.of(
        noticeRepository
            .findAll(
                spec,
                PageRequest.of(
                    request.getPage() - 1, request.getPageSize(), Sort.by("id").descending()))
            .map(NoticeDto.Response::of));
  }

  public NoticeDto.Response getNotice(Long id) {
    return noticeRepository
        .findById(id)
        .map(NoticeDto.Response::of)
        .orElseThrow(() -> new RequestException400(ExceptionCode.UNKNOWN_NOTICE));
  }

  @Transactional
  public NoticeDto.Response createNotice(NoticeCreateDto.Request request, Operator operator) {
    return NoticeDto.Response.of(noticeRepository.save(request.toEntity(operator)));
  }

  @Transactional
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

  @Transactional
  public void deleteNotice(Long id, Operator operator) {
    noticeRepository
        .findById(id)
        .orElseThrow(() -> new RequestException400(ExceptionCode.UNKNOWN_NOTICE))
        .remove(operator);
  }
}
