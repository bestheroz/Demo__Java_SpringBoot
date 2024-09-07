package com.github.bestheroz.demo.notice;

import com.github.bestheroz.standard.common.dto.ListResult;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("public/api/v1/notices")
@RequiredArgsConstructor
@Tag(name = "Notice", description = "공지사항 API")
public class PublicNoticeController {
  private final NoticeService noticeService;

  @GetMapping
  public ListResult<NoticeDto.Response> getNoticeList(
      @Schema(example = "1") @RequestParam Integer page,
      @Schema(example = "10") @RequestParam Integer pageSize) {
    return noticeService.getNoticeList(new NoticeDto.Request(page, pageSize));
  }

  @GetMapping("{id}")
  public NoticeDto.Response getNotice(@PathVariable Long id) {
    return noticeService.getNotice(id);
  }
}
