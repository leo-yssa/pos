package com.gw.pos.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "type")
public class TypeEntity {
  @Id
  @Column(name = "name", nullable = false, length = 255)
  private String name;

  protected TypeEntity() {}

  public TypeEntity(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}

