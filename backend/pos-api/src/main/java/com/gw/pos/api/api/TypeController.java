package com.gw.pos.api.api;

import com.gw.pos.api.repo.TypeRepository;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/types")
public class TypeController {
  private final TypeRepository typeRepository;

  public TypeController(TypeRepository typeRepository) {
    this.typeRepository = typeRepository;
  }

  @GetMapping
  public List<String> list() {
    return typeRepository.findAll().stream().map(t -> t.getName()).toList();
  }
}

