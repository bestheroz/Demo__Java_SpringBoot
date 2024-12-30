package com.github.bestheroz.demo.dtos.notice;

import com.github.bestheroz.demo.domain.Notice;
import com.github.bestheroz.standard.common.dto.IdCreatedUpdatedDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

public class NoticeDto {
  @Data
  @AllArgsConstructor
  public static class Request {
    @Schema(description = "페이지 번호", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer page;

    @Schema(description = "페이지 크기", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer pageSize;
  }

  @Data
  @EqualsAndHashCode(callSuper = true)
  public static class Response extends IdCreatedUpdatedDto {
    @Schema(description = "제목", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "내용", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Schema(description = "사용 여부", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean useFlag;

    public static Response of(Notice notice) {
      final Response response = new Response();
      response.setId(notice.getId());
      response.setTitle(notice.getTitle());
      response.setContent(notice.getContent());
      response.setUseFlag(notice.getUseFlag());
      response.setCreatedAt(notice.getCreatedAt());
      response.setCreatedBy(notice.getCreatedBy());
      response.setUpdatedAt(notice.getUpdatedAt());
      response.setUpdatedBy(notice.getUpdatedBy());
      return response;
    }
  }
}
