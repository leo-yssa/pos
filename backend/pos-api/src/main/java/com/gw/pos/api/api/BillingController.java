package com.gw.pos.api.api;

import static com.gw.pos.api.api.ApiDtos.*;

import com.gw.pos.api.billing.BillModels.BillDetail;
import com.gw.pos.api.billing.BillModels.BillHeader;
import com.gw.pos.api.service.BillingService;
import com.gw.pos.api.billing.BillJdbcRepository;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bills")
public class BillingController {
  private final BillingService billingService;
  private final BillJdbcRepository billJdbcRepository;

  public BillingController(BillingService billingService, BillJdbcRepository billJdbcRepository) {
    this.billingService = billingService;
    this.billJdbcRepository = billJdbcRepository;
  }

  @GetMapping("/next-number")
  public long nextNumber() {
    return billJdbcRepository.nextBillNumber();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CreateBillResponse create(@Valid @RequestBody CreateBillRequest req) {
    BillHeader header =
        billingService.createBill(
            req.payMethod(),
            req.items().stream()
                .map(i -> new BillingService.ItemRequest(i.productName(), i.quantity()))
                .toList(),
            req.discountAmount(),
            req.discountPercent());
    return new CreateBillResponse(header.number(), header.date(), header.time(), header.total(), header.payMethod());
  }

  @GetMapping
  public List<BillHeaderDto> list(
      @RequestParam("from") LocalDate from,
      @RequestParam("to") LocalDate to) {
    return billingService.listBills(from, to).stream()
        .map(h -> new BillHeaderDto(h.number(), h.date(), h.time(), h.total(), h.payMethod()))
        .toList();
  }

  @GetMapping("/{number}")
  public BillDetailDto detail(
      @PathVariable("number") long number,
      @RequestParam("date") LocalDate date,
      @RequestParam("time") String time) {
    BillDetail detail = billingService.getBillDetail(number, date, parseTime(time));
    BillHeader h = detail.header();
    return new BillDetailDto(
        new BillHeaderDto(h.number(), h.date(), h.time(), h.total(), h.payMethod()),
        detail.items().stream().map(i -> new BillItemDto(i.name(), i.volume(), i.total())).toList());
  }

  private static LocalTime parseTime(String raw) {
    if (raw == null || raw.isBlank()) {
      return LocalTime.MIDNIGHT;
    }
    String[] parts = raw.trim().split(":");
    int h = parts.length > 0 ? Integer.parseInt(parts[0]) : 0;
    int m = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
    int s = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
    return LocalTime.of(h, m, s);
  }
}

