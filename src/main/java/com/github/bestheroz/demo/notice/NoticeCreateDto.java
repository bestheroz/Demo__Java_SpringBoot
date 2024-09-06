package com.github.bestheroz.demo.notice;

import com.github.bestheroz.demo.entity.Notice;
import com.github.bestheroz.standard.common.security.Operator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

public class NoticeCreateDto {
  @Data
  public static class Request {
    @Schema(description = "제목", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "내용", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Schema(description = "사용 여부", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean useFlag;

    public Notice toEntity(Operator operator) {
      return new Notice(this.title, this.content, this.useFlag, operator);
    }
  }
}
