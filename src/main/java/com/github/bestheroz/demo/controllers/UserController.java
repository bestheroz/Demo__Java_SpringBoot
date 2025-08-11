package com.github.bestheroz.demo.controllers;

import com.github.bestheroz.demo.dtos.user.*;
import com.github.bestheroz.demo.services.UserService;
import com.github.bestheroz.standard.common.authenticate.CurrentUser;
import com.github.bestheroz.standard.common.dto.ListResult;
import com.github.bestheroz.standard.common.dto.TokenDto;
import com.github.bestheroz.standard.common.security.Operator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "유저 API")
public class UserController {
  private final UserService userService;

  @GetMapping
  @SecurityRequirement(name = "bearerAuth")
  @PreAuthorize("hasAuthority('USER_VIEW')")
  public ListResult<UserDto.Response> getUserList(UserDto.Request request) {
    return userService.getUserList(request);
  }

  @GetMapping("check-login-id")
  @Operation(summary = "로그인 아이디 중복 확인")
  public boolean checkLoginId(
      @Schema(description = "로그인 아이디") @RequestParam String loginId,
      @Schema(description = "유저 ID") @RequestParam(required = false) Long userId) {
    return userService.checkLoginId(loginId, userId);
  }

  @PostMapping("login")
  @Operation(summary = "유저 로그인")
  public TokenDto loginUser(@RequestBody UserLoginDto.Request request) {
    return userService.loginUser(request);
  }

  @GetMapping("{id}")
  @SecurityRequirement(name = "bearerAuth")
  @PreAuthorize("hasAuthority('USER_VIEW')")
  public UserDto.Response getUser(@PathVariable Long id) {
    return userService.getUser(id);
  }

  @GetMapping("renew-token")
  @Operation(
      summary = "유저 토큰 갱신",
      description =
          "*어세스 토큰* 만료 시 *리플래시 토큰* 으로 *어세스 토큰* 을 갱신합니다.\n"
              + "    \"(동시에 여러 사용자가 접속하고 있다면 *리플래시 토큰* 값이 달라서 갱신이 안될 수 있습니다.)")
  public TokenDto renewToken(
      @Schema(description = "리플래시 토큰") @RequestHeader(value = "Authorization")
          String refreshToken) {
    return userService.renewToken(refreshToken);
  }

  @PostMapping
  @SecurityRequirement(name = "bearerAuth")
  @PreAuthorize("hasAuthority('USER_EDIT')")
  public UserDto.Response createUser(
      @RequestBody UserCreateDto.Request request, @CurrentUser Operator operator) {
    return userService.createUser(request, operator);
  }

  @PutMapping("{id}")
  @SecurityRequirement(name = "bearerAuth")
  @PreAuthorize("hasAuthority('USER_EDIT')")
  public UserDto.Response updateUser(
      @PathVariable Long id,
      @RequestBody UserUpdateDto.Request request,
      @CurrentUser Operator operator) {
    return userService.updateUser(id, request, operator);
  }

  @PatchMapping("{id}/password")
  @Operation(summary = "유저 비밀번호 변경")
  @SecurityRequirement(name = "bearerAuth")
  @PreAuthorize("hasAuthority('USER_EDIT')")
  public UserDto.Response changePassword(
      @PathVariable Long id,
      @RequestBody UserChangePasswordDto.Request request,
      @CurrentUser Operator operator) {
    return userService.changePassword(id, request, operator);
  }

  @DeleteMapping("logout")
  @Operation(
      summary = "유저 로그아웃",
      description = "리플래시 토큰을 삭제합니다.",
      responses = {@ApiResponse(responseCode = "204")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @SecurityRequirement(name = "bearerAuth")
  @PreAuthorize("hasAuthority('USER_EDIT')")
  public void logout(@CurrentUser Operator operator) {
    userService.logout(operator.getId());
  }

  @DeleteMapping("{id}")
  @Operation(
      description = "(Soft delete)",
      responses = {@ApiResponse(responseCode = "204")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @SecurityRequirement(name = "bearerAuth")
  @PreAuthorize("hasAuthority('USER_EDIT')")
  public void deleteUser(@PathVariable Long id, @CurrentUser Operator operator) {
    userService.deleteUser(id, operator);
  }
}
