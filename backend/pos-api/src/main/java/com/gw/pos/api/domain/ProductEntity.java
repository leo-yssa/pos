package com.gw.pos.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "product")
public class ProductEntity {
  @Id
  @Column(name = "name", nullable = false, length = 255)
  private String name;

  @Column(name = "type", nullable = false, length = 255)
  private String type;

  @Column(name = "price", nullable = false)
  private int price;

  @Column(name = "svolume", nullable = false)
  private int soldVolume;

  protected ProductEntity() {}

  public ProductEntity(String name, String type, int price, int soldVolume) {
    this.name = name;
    this.type = type;
    this.price = price;
    this.soldVolume = soldVolume;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public int getPrice() {
    return price;
  }

  public int getSoldVolume() {
    return soldVolume;
  }

  public void incrementSoldVolume(int delta) {
    this.soldVolume += delta;
  }
}

