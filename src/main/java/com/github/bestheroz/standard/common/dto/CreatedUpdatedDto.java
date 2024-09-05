package com.github.bestheroz.standard.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CreatedUpdatedDto extends CreatedDto {
  @Schema(description = "수정일시")
  private Instant updatedAt;

  @Schema(description = "수정자")
  private UserSimpleDto updatedBy;
}
