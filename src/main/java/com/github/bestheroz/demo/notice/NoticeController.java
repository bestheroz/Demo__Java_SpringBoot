package com.github.bestheroz.demo.notice;

import com.github.bestheroz.standard.common.authenticate.Authenticated;
import com.github.bestheroz.standard.common.authenticate.CurrentUser;
import com.github.bestheroz.standard.common.dto.ListResult;
import com.github.bestheroz.standard.common.security.Operator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

  @PostMapping
  @Authenticated(authority = "NOTICE_EDIT")
  public NoticeDto.Response createNotice(
      @RequestBody NoticeCreateDto.Request request, @CurrentUser Operator operator) {
    return noticeService.createNotice(request, operator);
  }

  @PutMapping("{id}")
  @Authenticated(authority = "NOTICE_EDIT")
  public NoticeDto.Response updateNotice(
      @PathVariable Long id,
      @RequestBody NoticeCreateDto.Request request,
      @CurrentUser Operator operator) {
    return noticeService.updateNotice(id, request, operator);
  }

  @DeleteMapping("{id}")
  @Operation(
      description = "(Soft delete)",
      responses = {@ApiResponse(responseCode = "204")})
  @Authenticated(authority = "NOTICE_EDIT")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteNotice(@PathVariable Long id, @CurrentUser Operator operator) {
    noticeService.deleteNotice(id, operator);
  }
}
