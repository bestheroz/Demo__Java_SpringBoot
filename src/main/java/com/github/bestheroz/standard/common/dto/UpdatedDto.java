package com.github.bestheroz.standard.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.Data;

@Data
public class UpdatedDto {
  @Schema(description = "수정일시", requiredMode = Schema.RequiredMode.REQUIRED)
  private Instant updatedAt;

  @Schema(description = "수정자", requiredMode = Schema.RequiredMode.REQUIRED)
  private UserSimpleDto updatedBy;
}
