package com.github.bestheroz.standard.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenDto {
  private String accessToken;
  private String refreshToken;
}
