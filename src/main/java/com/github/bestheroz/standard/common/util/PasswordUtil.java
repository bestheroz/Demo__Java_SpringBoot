package com.github.bestheroz.standard.common.util;

import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.bcrypt.BCrypt;

@UtilityClass
public class PasswordUtil {
  public boolean verifyPassword(final String plainPassword, final String hashedPassword) {
    return BCrypt.checkpw(plainPassword, hashedPassword);
  }

  public String getPasswordHash(final String password) {
    return BCrypt.hashpw(password, BCrypt.gensalt());
  }
}