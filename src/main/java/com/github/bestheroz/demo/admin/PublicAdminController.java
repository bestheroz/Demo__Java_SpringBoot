package com.github.bestheroz.demo.admin;

import com.github.bestheroz.standard.common.dto.TokenDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("public/api/v1/admins")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "관리자 API")
public class PublicAdminController {
  private final AdminService adminService;

  @GetMapping("check-login-id")
  @Operation(summary = "로그인 아이디 중복 확인")
  public boolean checkLoginId(
      @Schema(description = "로그인 아이디") @RequestParam String loginId,
      @Schema(description = "관리자 ID") @RequestParam(required = false) Long id) {
    return adminService.checkLoginId(loginId, id);
  }

  @PostMapping("login")
  @Operation(summary = "관리자 로그인")
  public TokenDto loginAdmin(@RequestBody AdminLoginDto.Request request) {
    return adminService.loginAdmin(request);
  }
}
