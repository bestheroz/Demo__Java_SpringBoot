package com.github.bestheroz.demo.user;

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
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "관리자 API")
public class UserController {
  private final UserService userService;

  @GetMapping
  @Authenticated(authority = "USER_VIEW")
  public ListResult<UserDto.Response> getUserList(
      @Schema(example = "1") @RequestParam Integer page,
      @Schema(example = "10") @RequestParam Integer pageSize) {
    return userService.getUserList(new UserDto.Request(page, pageSize));
  }

  @GetMapping("check-login-id")
  @Operation(summary = "로그인 아이디 중복 확인")
  public boolean checkLoginId(
      @Schema(description = "로그인 아이디") @RequestParam String loginId,
      @Schema(description = "관리자 ID") @RequestParam(required = false) Long id) {
    return userService.checkLoginId(loginId, id);
  }

  @PostMapping("login")
  @Operation(summary = "관리자 로그인")
  public TokenDto loginUser(@RequestBody UserLoginDto.Request request) {
    return userService.loginUser(request);
  }

  @GetMapping("{id}")
  @Authenticated(authority = "USER_VIEW")
  public UserDto.Response getUser(@PathVariable Long id) {
    return userService.getUser(id);
  }

  @GetMapping("renew-token")
  @Operation(
      summary = "관리자 토큰 갱신",
      description =
          "*어세스 토큰* 만료 시 *리플래시 토큰* 으로 *어세스 토큰* 을 갱신합니다.\n"
              + "    \"(동시에 여러 사용자가 접속하고 있다면 *리플래시 토큰* 값이 달라서 갱신이 안될 수 있습니다.)")
  @Authenticated(authority = "USER_VIEW")
  public TokenDto renewToken(
      @Schema(description = "리플래시 토큰") @RequestHeader(value = "AuthorizationR")
          String refreshToken) {
    return userService.renewToken(refreshToken);
  }

  @PostMapping
  @Authenticated(authority = "USER_EDIT")
  public UserDto.Response createUser(
      @RequestBody UserCreateDto.Request request, @CurrentUser Operator operator) {
    return userService.createUser(request, operator);
  }

  @PutMapping("{id}")
  @Authenticated(authority = "USER_EDIT")
  public UserDto.Response updateUser(
      @PathVariable Long id,
      @RequestBody UserUpdateDto.Request request,
      @CurrentUser Operator operator) {
    return userService.updateUser(id, request, operator);
  }

  @PatchMapping("{id}/password")
  @Operation(summary = "관리자 비밀번호 변경")
  @Authenticated(authority = "USER_EDIT")
  public UserDto.Response changePassword(
      @PathVariable Long id,
      @RequestBody UserChangePasswordDto.Request request,
      @CurrentUser Operator operator) {
    return userService.changePassword(id, request, operator);
  }

  @DeleteMapping("logout")
  @Operation(
      summary = "관리자 로그아웃",
      description = "리플래시 토큰을 삭제합니다.",
      responses = {@ApiResponse(responseCode = "204")})
  @Authenticated(authority = "USER_EDIT")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void logout(@CurrentUser Operator operator) {
    userService.logout(operator.getId());
  }

  @DeleteMapping("{id}")
  @Operation(
      description = "(Soft delete)",
      responses = {@ApiResponse(responseCode = "204")})
  @Authenticated(authority = "USER_EDIT")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteUser(@PathVariable Long id, @CurrentUser Operator operator) {
    userService.deleteUser(id, operator);
  }
}