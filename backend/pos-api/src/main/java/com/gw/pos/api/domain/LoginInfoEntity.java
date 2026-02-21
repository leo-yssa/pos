package com.gw.pos.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "logininfo")
public class LoginInfoEntity {
  @Id
  @Column(name = "id", nullable = false, length = 255)
  private String id;

  @Column(name = "password", nullable = false, length = 255)
  private String password;

  protected LoginInfoEntity() {}

  public LoginInfoEntity(String id, String password) {
    this.id = id;
    this.password = password;
  }

  public String getId() {
    return id;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}

