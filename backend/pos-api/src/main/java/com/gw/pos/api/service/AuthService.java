package com.gw.pos.api.service;

import com.gw.pos.api.domain.LoginInfoEntity;
import com.gw.pos.api.repo.LoginInfoRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
  private final LoginInfoRepository loginInfoRepository;
  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  public AuthService(LoginInfoRepository loginInfoRepository) {
    this.loginInfoRepository = loginInfoRepository;
  }

  @Transactional
  public boolean verifyAndMaybeUpgradePassword(String id, String rawPassword) {
    LoginInfoEntity entity = loginInfoRepository.findById(id).orElse(null);
    if (entity == null) {
      return false;
    }

    String stored = entity.getPassword();
    if (stored == null) {
      return false;
    }

    boolean ok;
    if (looksLikeBcrypt(stored)) {
      ok = encoder.matches(rawPassword, stored);
    } else {
      ok = stored.equals(rawPassword);
      if (ok) {
        entity.setPassword(encoder.encode(rawPassword));
        loginInfoRepository.save(entity);
      }
    }
    return ok;
  }

  private static boolean looksLikeBcrypt(String s) {
    return s.startsWith("$2a$") || s.startsWith("$2b$") || s.startsWith("$2y$");
  }
}

