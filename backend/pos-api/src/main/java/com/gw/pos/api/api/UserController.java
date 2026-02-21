package com.gw.pos.api.api;

import static com.gw.pos.api.api.ApiDtos.*;

import com.gw.pos.api.domain.LoginInfoEntity;
import com.gw.pos.api.repo.LoginInfoRepository;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final LoginInfoRepository loginInfoRepository;
  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  public UserController(LoginInfoRepository loginInfoRepository) {
    this.loginInfoRepository = loginInfoRepository;
  }

  @GetMapping
  public List<String> listIds() {
    return loginInfoRepository.findAll().stream().map(LoginInfoEntity::getId).sorted().toList();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public void create(@Valid @RequestBody CreateUserRequest req) {
    loginInfoRepository.save(new LoginInfoEntity(req.id(), encoder.encode(req.password())));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable("id") String id) {
    loginInfoRepository.deleteById(id);
  }
}

