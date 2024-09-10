package com.github.bestheroz.demo.user;

import com.github.bestheroz.demo.entity.User;
import com.github.bestheroz.demo.repository.UserRepository;
import com.github.bestheroz.standard.common.authenticate.JwtTokenProvider;
import com.github.bestheroz.standard.common.dto.ListResult;
import com.github.bestheroz.standard.common.dto.TokenDto;
import com.github.bestheroz.standard.common.exception.AuthenticationException401;
import com.github.bestheroz.standard.common.exception.ExceptionCode;
import com.github.bestheroz.standard.common.exception.RequestException400;
import com.github.bestheroz.standard.common.security.Operator;
import com.github.bestheroz.standard.common.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final JwtTokenProvider jwtTokenProvider;

  @Transactional(readOnly = true)
  public ListResult<UserDto.Response> getUserList(UserDto.Request request) {
    return ListResult.of(
        userRepository
            .findAllByRemovedFlagIsFalse(
                PageRequest.of(
                    request.getPage() - 1, request.getPageSize(), Sort.by("id").descending()))
            .map(UserDto.Response::of));
  }

  @Transactional(readOnly = true)
  public UserDto.Response getUser(final Long id) {
    return this.userRepository
        .findById(id)
        .map(UserDto.Response::of)
        .orElseThrow(() -> new RequestException400(ExceptionCode.UNKNOWN_USER));
  }

  public UserDto.Response createUser(final UserCreateDto.Request request, Operator operator) {
    if (this.userRepository.findByLoginIdAndRemovedFlagFalse(request.getLoginId()).isPresent()) {
      throw new RequestException400(ExceptionCode.ALREADY_JOINED_ACCOUNT);
    }

    return UserDto.Response.of(this.userRepository.save(request.toEntity(operator)));
  }

  public UserDto.Response updateUser(
      final Long id, final UserUpdateDto.Request request, Operator operator) {
    User user =
        this.userRepository
            .findById(id)
            .orElseThrow(() -> new RequestException400(ExceptionCode.UNKNOWN_USER));
    if (user.getRemovedFlag()) throw new RequestException400(ExceptionCode.UNKNOWN_USER);

    if (this.userRepository
        .findByLoginIdAndRemovedFlagFalseAndIdNot(request.getLoginId(), id)
        .isPresent()) {
      throw new RequestException400(ExceptionCode.ALREADY_JOINED_ACCOUNT);
    }

    user.update(
        request.getLoginId(),
        request.getPassword(),
        request.getName(),
        request.getUseFlag(),
        request.getAuthorities(),
        operator);
    return UserDto.Response.of(user);
  }

  public void deleteUser(final Long id, Operator operator) {
    User user =
        this.userRepository
            .findById(id)
            .orElseThrow(() -> new RequestException400(ExceptionCode.UNKNOWN_USER));
    if (user.getRemovedFlag()) throw new RequestException400(ExceptionCode.UNKNOWN_USER);
    if (user.getId().equals(operator.getId())) {
      throw new RequestException400(ExceptionCode.CANNOT_REMOVE_YOURSELF);
    }

    user.remove(operator);
  }

  public UserDto.Response changePassword(
      final Long id, final UserChangePasswordDto.Request request, Operator operator) {
    User user =
        this.userRepository
            .findById(id)
            .orElseThrow(() -> new RequestException400(ExceptionCode.UNKNOWN_USER));
    if (user.getRemovedFlag()) throw new RequestException400(ExceptionCode.UNKNOWN_USER);
    if (!PasswordUtil.verifyPassword(request.getOldPassword(), user.getPassword())) {
      log.warn("password not match");
      throw new RequestException400(ExceptionCode.UNKNOWN_USER);
    }
    if (user.getPassword().equals(request.getNewPassword())) {
      throw new RequestException400(ExceptionCode.CHANGE_TO_SAME_PASSWORD);
    }

    user.changePassword(request.getNewPassword(), operator);
    return UserDto.Response.of(user);
  }

  public TokenDto loginUser(UserLoginDto.Request request) {
    User user =
        this.userRepository
            .findByLoginIdAndRemovedFlagFalse(request.getLoginId())
            .orElseThrow(() -> new RequestException400(ExceptionCode.UNJOINED_ACCOUNT));
    if (!user.getUseFlag()) {
      throw new RequestException400(ExceptionCode.UNKNOWN_USER);
    }
    if (!PasswordUtil.verifyPassword(request.getPassword(), user.getPassword())) {
      log.warn("password not match");
      throw new RequestException400(ExceptionCode.UNKNOWN_USER);
    }
    user.renewToken(jwtTokenProvider.createRefreshToken(new Operator(user)));
    return new TokenDto(jwtTokenProvider.createAccessToken(new Operator(user)), user.getToken());
  }

  public TokenDto renewToken(String refreshToken) {
    Long id = jwtTokenProvider.getId(refreshToken);
    User user =
        this.userRepository
            .findById(id)
            .orElseThrow(() -> new RequestException400(ExceptionCode.UNKNOWN_USER));
    if (user.getRemovedFlag()
        || user.getToken() == null
        || !jwtTokenProvider.validateToken(refreshToken)) {
      throw new AuthenticationException401();
    }
    if (user.getToken() != null && jwtTokenProvider.issuedRefreshTokenIn3Seconds(user.getToken())) {
      return new TokenDto(jwtTokenProvider.createAccessToken(new Operator(user)), user.getToken());
    } else if (StringUtils.equals(user.getToken(), refreshToken)) {
      user.renewToken(jwtTokenProvider.createRefreshToken(new Operator(user)));
      return new TokenDto(jwtTokenProvider.createAccessToken(new Operator(user)), user.getToken());
    } else {
      throw new AuthenticationException401();
    }
  }

  public void logout(Long id) {
    User user =
        this.userRepository
            .findById(id)
            .orElseThrow(() -> new RequestException400(ExceptionCode.UNKNOWN_USER));
    user.logout();
  }

  @Transactional(readOnly = true)
  public Boolean checkLoginId(String loginId, Long id) {
    return this.userRepository.findByLoginIdAndRemovedFlagFalseAndIdNot(loginId, id).isEmpty();
  }
}
