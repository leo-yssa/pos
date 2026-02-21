package com.gw.pos.api.api;

import com.gw.pos.api.billing.BillModels.PayMethod;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public final class ApiDtos {
  private ApiDtos() {}

  public record ProductDto(String name, String type, int price, int soldVolume) {}

  public record CreateProductRequest(
      @NotBlank String name,
      @NotBlank String type,
      @Min(0) int price) {}

  public record LoginRequest(@NotBlank String id, @NotBlank String password) {}

  public record LoginResponse(boolean ok) {}

  public record CreateUserRequest(@NotBlank String id, @NotBlank String password) {}

  public record CreateBillRequest(
      @NotNull PayMethod payMethod,
      @NotEmpty List<CreateBillItem> items,
      @Min(0) Integer discountAmount,
      @Min(0) @Max(100) Integer discountPercent) {}

  public record CreateBillItem(@NotBlank String productName, @Min(1) int quantity) {}

  public record CreateBillResponse(long number, LocalDate date, LocalTime time, int total, PayMethod payMethod) {}

  public record BillHeaderDto(long number, LocalDate date, LocalTime time, int total, PayMethod payMethod) {}

  public record BillItemDto(String name, int volume, int total) {}

  public record BillDetailDto(BillHeaderDto header, List<BillItemDto> items) {}
}

