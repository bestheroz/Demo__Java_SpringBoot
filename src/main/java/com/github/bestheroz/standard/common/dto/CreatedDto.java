package com.github.bestheroz.standard.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.Data;

@Data
public class CreatedDto {
  @Schema(description = "생성일시")
  private Instant createdAt;

  @Schema(description = "생성자")
  private UserSimpleDto createdBy;
}
