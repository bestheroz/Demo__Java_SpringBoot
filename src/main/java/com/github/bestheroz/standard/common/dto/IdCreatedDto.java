package com.github.bestheroz.standard.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class IdCreatedDto extends CreatedDto {
  @Schema(description = "ID(KEY)", requiredMode = Schema.RequiredMode.REQUIRED)
  private Long id;
}
