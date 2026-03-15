package com.github.bestheroz.standard.common.util;

import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.bcrypt.BCrypt;

@UtilityClass
public class PasswordUtil {
  public boolean isPasswordValid(final String plainPassword, final String hashedPassword) {
    return BCrypt.checkpw(
        plainPassword.substring(0, Math.min(plainPassword.length(), 72)), hashedPassword);
  }

  public String getPasswordHash(final String password) {
    return BCrypt.hashpw(password, BCrypt.gensalt());
  }
}
