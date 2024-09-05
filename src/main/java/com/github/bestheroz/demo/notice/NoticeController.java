package com.github.bestheroz.demo.notice;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/notices")
@RequiredArgsConstructor
public class NoticeController {
  private final NoticeService noticeService;

  @GetMapping
  public Page<NoticeDto.Response> getNotice(NoticeDto.Request request) {
    return noticeService.getNotice(request);
  }
}
