package com.github.bestheroz.standard.common.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCrypt;

@UtilityClass
public class PasswordUtil {
  public boolean isPasswordValid(final String plainPassword, final String hashedPassword) {
    return BCrypt.checkpw(StringUtils.substring(plainPassword, 0, 72), hashedPassword);
  }

  public String getPasswordHash(final String password) {
    return BCrypt.hashpw(password, BCrypt.gensalt());
  }
}
