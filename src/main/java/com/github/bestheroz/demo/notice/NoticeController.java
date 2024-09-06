package com.github.bestheroz.demo.notice;

import com.github.bestheroz.standard.common.dto.ListResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/notices")
@RequiredArgsConstructor
public class NoticeController {
  private final NoticeService noticeService;

  @GetMapping
  public ListResult<NoticeDto.Response> getNotices(NoticeDto.Request request) {
    return noticeService.getNotices(request);
  }

  @GetMapping("{id}")
  public NoticeDto.Response getNotices(@PathVariable Long id) {
    return noticeService.getNotice(id);
  }
}
