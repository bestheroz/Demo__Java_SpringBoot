package com.github.bestheroz.demo.notice;

import com.github.bestheroz.standard.common.authenticate.Authenticated;
import com.github.bestheroz.standard.common.authenticate.CurrentUser;
import com.github.bestheroz.standard.common.security.Operator;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/notices")
@RequiredArgsConstructor
@Tag(name = "Notice", description = "공지사항 API")
public class NoticeController {
  private final NoticeService noticeService;

  @PostMapping
  @Authenticated
  public NoticeDto.Response createNotice(
      @RequestBody NoticeCreateDto.Request request, @CurrentUser Operator operator) {
    return noticeService.createNotice(request, operator);
  }

  @PutMapping("{id}")
  @Authenticated
  public NoticeDto.Response updateNotice(
      @PathVariable Long id,
      @RequestBody NoticeCreateDto.Request request,
      @CurrentUser Operator operator) {
    return noticeService.updateNotice(id, request, operator);
  }

  @DeleteMapping("{id}")
  @Authenticated
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteNotice(@PathVariable Long id) {
    noticeService.deleteNotice(id);
  }
}
