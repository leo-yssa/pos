package com.gw.pos.api.billing;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public final class BillModels {
  private BillModels() {}

  public enum PayMethod {
    CASH,
    CARD
  }

  public record BillHeader(long number, LocalDate date, LocalTime time, int total, PayMethod payMethod) {}

  public record BillLineItem(String name, int volume, int total) {}

  public record BillDetail(BillHeader header, List<BillLineItem> items) {}
}

