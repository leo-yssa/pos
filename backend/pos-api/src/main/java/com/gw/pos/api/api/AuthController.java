package com.gw.pos.api.api;

import static com.gw.pos.api.api.ApiDtos.*;

import com.gw.pos.api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  @ResponseStatus(HttpStatus.OK)
  public LoginResponse login(@Valid @RequestBody LoginRequest req) {
    boolean ok = authService.verifyAndMaybeUpgradePassword(req.id(), req.password());
    return new LoginResponse(ok);
  }
}

