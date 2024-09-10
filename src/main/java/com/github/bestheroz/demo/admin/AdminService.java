package com.github.bestheroz.demo.admin;

import com.github.bestheroz.demo.entity.Admin;
import com.github.bestheroz.demo.repository.AdminRepository;
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
public class AdminService {
  private final AdminRepository adminRepository;
  private final JwtTokenProvider jwtTokenProvider;

  @Transactional(readOnly = true)
  public ListResult<AdminDto.Response> getAdminList(AdminDto.Request request) {
    return ListResult.of(
        adminRepository
            .findAllByRemovedFlagIsFalse(
                PageRequest.of(
                    request.getPage() - 1, request.getPageSize(), Sort.by("id").descending()))
            .map(AdminDto.Response::of));
  }

  @Transactional(readOnly = true)
  public AdminDto.Response getAdmin(final Long id) {
    return this.adminRepository
        .findById(id)
        .map(AdminDto.Response::of)
        .orElseThrow(() -> new RequestException400(ExceptionCode.UNKNOWN_ADMIN));
  }

  public AdminDto.Response createAdmin(final AdminCreateDto.Request request, Operator operator) {
    if (this.adminRepository.findByLoginIdAndRemovedFlagFalse(request.getLoginId()).isPresent()) {
      throw new RequestException400(ExceptionCode.ALREADY_JOINED_ACCOUNT);
    }

    return AdminDto.Response.of(this.adminRepository.save(request.toEntity(operator)));
  }

  public AdminDto.Response updateAdmin(
      final Long id, final AdminUpdateDto.Request request, Operator operator) {
    Admin admin =
        this.adminRepository
            .findById(id)
            .orElseThrow(() -> new RequestException400(ExceptionCode.UNKNOWN_ADMIN));
    if (admin.getRemovedFlag()) throw new RequestException400(ExceptionCode.UNKNOWN_ADMIN);
    if (!admin.getManagerFlag() && admin.getId().equals(operator.getId())) {
      throw new RequestException400(ExceptionCode.CANNOT_UPDATE_YOURSELF);
    }
    if (!admin.getManagerFlag() && !request.getManagerFlag() && !operator.getManagerFlag()) {
      throw new RequestException400(ExceptionCode.UNKNOWN_AUTHORITY);
    }

    if (this.adminRepository
        .findByLoginIdAndRemovedFlagFalseAndIdNot(request.getLoginId(), id)
        .isPresent()) {
      throw new RequestException400(ExceptionCode.ALREADY_JOINED_ACCOUNT);
    }

    admin.update(
        request.getLoginId(),
        request.getPassword(),
        request.getName(),
        request.getUseFlag(),
        request.getManagerFlag(),
        request.getAuthorities(),
        operator);
    return AdminDto.Response.of(admin);
  }

  public void deleteAdmin(final Long id, Operator operator) {
    Admin admin =
        this.adminRepository
            .findById(id)
            .orElseThrow(() -> new RequestException400(ExceptionCode.UNKNOWN_ADMIN));
    if (admin.getRemovedFlag()) throw new RequestException400(ExceptionCode.UNKNOWN_ADMIN);
    if (admin.getId().equals(operator.getId())) {
      throw new RequestException400(ExceptionCode.CANNOT_REMOVE_YOURSELF);
    }

    admin.remove(operator);
  }

  public AdminDto.Response changePassword(
      final Long id, final AdminChangePasswordDto.Request request, Operator operator) {
    Admin admin =
        this.adminRepository
            .findById(id)
            .orElseThrow(() -> new RequestException400(ExceptionCode.UNKNOWN_ADMIN));
    if (admin.getRemovedFlag()) throw new RequestException400(ExceptionCode.UNKNOWN_ADMIN);
    if (!PasswordUtil.verifyPassword(request.getOldPassword(), admin.getPassword())) {
      log.warn("password not match");
      throw new RequestException400(ExceptionCode.UNKNOWN_ADMIN);
    }
    if (admin.getPassword().equals(request.getNewPassword())) {
      throw new RequestException400(ExceptionCode.CHANGE_TO_SAME_PASSWORD);
    }

    admin.changePassword(request.getNewPassword(), operator);
    return AdminDto.Response.of(admin);
  }

  public TokenDto loginAdmin(AdminLoginDto.Request request) {
    Admin admin =
        this.adminRepository
            .findByLoginIdAndRemovedFlagFalse(request.getLoginId())
            .orElseThrow(() -> new RequestException400(ExceptionCode.UNJOINED_ACCOUNT));
    if (!admin.getUseFlag()) {
      throw new RequestException400(ExceptionCode.UNKNOWN_ADMIN);
    }
    if (!PasswordUtil.verifyPassword(request.getPassword(), admin.getPassword())) {
      log.warn("password not match");
      throw new RequestException400(ExceptionCode.UNKNOWN_ADMIN);
    }
    admin.renewToken(jwtTokenProvider.createRefreshToken(new Operator(admin)));
    return new TokenDto(jwtTokenProvider.createAccessToken(new Operator(admin)), admin.getToken());
  }

  public TokenDto renewToken(String refreshToken) {
    Long id = jwtTokenProvider.getId(refreshToken);
    Admin admin =
        this.adminRepository
            .findById(id)
            .orElseThrow(() -> new RequestException400(ExceptionCode.UNKNOWN_ADMIN));
    if (admin.getRemovedFlag()
        || admin.getToken() == null
        || !jwtTokenProvider.validateToken(refreshToken)) {
      throw new AuthenticationException401();
    }
    if (admin.getToken() != null
        && jwtTokenProvider.issuedRefreshTokenIn3Seconds(admin.getToken())) {
      return new TokenDto(
          jwtTokenProvider.createAccessToken(new Operator(admin)), admin.getToken());
    } else if (StringUtils.equals(admin.getToken(), refreshToken)) {
      admin.renewToken(jwtTokenProvider.createRefreshToken(new Operator(admin)));
      return new TokenDto(
          jwtTokenProvider.createAccessToken(new Operator(admin)), admin.getToken());
    } else {
      throw new AuthenticationException401();
    }
  }

  public void logout(Long id) {
    Admin admin =
        this.adminRepository
            .findById(id)
            .orElseThrow(() -> new RequestException400(ExceptionCode.UNKNOWN_ADMIN));
    admin.logout();
  }

  @Transactional(readOnly = true)
  public Boolean checkLoginId(String loginId, Long id) {
    return this.adminRepository.findByLoginIdAndRemovedFlagFalseAndIdNot(loginId, id).isEmpty();
  }
}
