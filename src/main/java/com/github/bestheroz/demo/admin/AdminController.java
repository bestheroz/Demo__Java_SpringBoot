package com.github.bestheroz.demo.admin;

import com.github.bestheroz.standard.common.authenticate.Authenticated;
import com.github.bestheroz.standard.common.authenticate.CurrentUser;
import com.github.bestheroz.standard.common.dto.ListResult;
import com.github.bestheroz.standard.common.dto.TokenDto;
import com.github.bestheroz.standard.common.security.Operator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/admins")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "관리자 API")
public class AdminController {
  private final AdminService adminService;

  @GetMapping
  @Authenticated
  public ListResult<AdminDto.Response> getAdminList(
      @Schema(example = "1") @RequestParam Integer page,
      @Schema(example = "10") @RequestParam Integer pageSize) {
    return adminService.getAdminList(new AdminDto.Request(page, pageSize));
  }

  @GetMapping("{id}")
  @Authenticated
  public AdminDto.Response getAdmin(@PathVariable Long id) {
    return adminService.getAdmin(id);
  }

  @GetMapping("renew-token")
  @Operation(
      summary = "관리자 토큰 갱신",
      description =
          "*어세스 토큰* 만료 시 *리플래시 토큰* 으로 *어세스 토큰* 을 갱신합니다.\n"
              + "    \"(동시에 여러 사용자가 접속하고 있다면 *리플래시 토큰* 값이 달라서 갱신이 안될 수 있습니다.)")
  @Authenticated
  public TokenDto renewToken(
      @Schema(description = "리플래시 토큰") @RequestHeader(value = "AuthorizationR")
          String refreshToken) {
    return adminService.renewToken(refreshToken);
  }

  @PostMapping
  @Authenticated
  public AdminDto.Response createAdmin(
      @RequestBody AdminCreateDto.Request request, @CurrentUser Operator operator) {
    return adminService.createAdmin(request, operator);
  }

  @PutMapping("{id}")
  @Authenticated
  public AdminDto.Response updateAdmin(
      @PathVariable Long id,
      @RequestBody AdminUpdateDto.Request request,
      @CurrentUser Operator operator) {
    return adminService.updateAdmin(id, request, operator);
  }

  @PatchMapping("{id}/password")
  @Operation(summary = "관리자 비밀번호 변경")
  @Authenticated
  public AdminDto.Response changePassword(
      @PathVariable Long id,
      @RequestBody AdminChangePasswordDto.Request request,
      @CurrentUser Operator operator) {
    return adminService.changePassword(id, request, operator);
  }

  @DeleteMapping("logout")
  @Operation(
      summary = "관리자 로그아웃",
      description = "리플래시 토큰을 삭제합니다.",
      responses = {@ApiResponse(responseCode = "204")})
  @Authenticated
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void logout(@CurrentUser Operator operator) {
    adminService.logout(operator.getId());
  }

  @DeleteMapping("{id}")
  @Operation(
      description = "(Soft delete)",
      responses = {@ApiResponse(responseCode = "204")})
  @Authenticated
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteAdmin(@PathVariable Long id, @CurrentUser Operator operator) {
    adminService.deleteAdmin(id, operator);
  }
}
