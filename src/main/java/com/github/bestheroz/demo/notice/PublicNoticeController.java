package com.github.bestheroz.demo.notice;

import com.github.bestheroz.standard.common.dto.ListResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("public/api/v1/notices")
@RequiredArgsConstructor
@Tag(name = "Notice", description = "공지사항 API")
public class PublicNoticeController {
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
