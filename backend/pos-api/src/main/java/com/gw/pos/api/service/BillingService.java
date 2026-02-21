package com.gw.pos.api.service;

import static com.gw.pos.api.billing.BillModels.*;

import com.gw.pos.api.billing.BillJdbcRepository;
import com.gw.pos.api.domain.ProductEntity;
import com.gw.pos.api.repo.ProductRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class BillingService {
  private final BillJdbcRepository billJdbcRepository;
  private final ProductRepository productRepository;
  private static final String DISCOUNT_ITEM_NAME = "DISCOUNT";

  public BillingService(BillJdbcRepository billJdbcRepository, ProductRepository productRepository) {
    this.billJdbcRepository = billJdbcRepository;
    this.productRepository = productRepository;
  }

  @Transactional
  public BillHeader createBill(
      PayMethod payMethod, List<ItemRequest> items, Integer discountAmount, Integer discountPercent) {
    if (items == null || items.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "EMPTY_ITEMS");
    }

    long number = billJdbcRepository.nextBillNumber();
    LocalDate date = LocalDate.now();
    LocalTime time = LocalTime.now().withNano(0);

    List<BillLineItem> lineItems = new ArrayList<>(items.size());
    int total = 0;

    for (ItemRequest req : items) {
      if (req.quantity() < 1) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INVALID_QUANTITY");
      }
      ProductEntity product =
          productRepository
              .findById(req.productName())
              .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND"));

      int lineTotal = product.getPrice() * req.quantity();
      total += lineTotal;
      lineItems.add(new BillLineItem(product.getName(), req.quantity(), lineTotal));

      product.incrementSoldVolume(req.quantity());
      productRepository.save(product);
    }

    int discount = computeDiscount(total, discountAmount, discountPercent);
    if (discount > 0) {
      lineItems.add(new BillLineItem(DISCOUNT_ITEM_NAME, 1, -discount));
      total -= discount;
    }

    billJdbcRepository.insertBillItems(number, date, time, payMethod, lineItems);
    billJdbcRepository.insertBillHeader(number, date, time, total, payMethod);
    return new BillHeader(number, date, time, total, payMethod);
  }

  public List<BillHeader> listBills(LocalDate from, LocalDate toInclusive) {
    if (from == null || toInclusive == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "MISSING_DATE_RANGE");
    }
    return billJdbcRepository.findHeaders(from, toInclusive);
  }

  public BillDetail getBillDetail(long number, LocalDate date, LocalTime time) {
    BillHeader header = billJdbcRepository.findHeader(number, date, time);
    List<BillLineItem> items = billJdbcRepository.findItems(number, date, time);
    return new BillDetail(header, items);
  }

  public record ItemRequest(String productName, int quantity) {}

  private static int computeDiscount(int subtotal, Integer discountAmount, Integer discountPercent) {
    Integer amount = discountAmount;
    Integer percent = discountPercent;
    if (amount != null && amount < 0) amount = 0;
    if (percent != null && percent < 0) percent = 0;

    boolean hasAmount = amount != null && amount > 0;
    boolean hasPercent = percent != null && percent > 0;

    if (hasAmount && hasPercent) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "DISCOUNT_CONFLICT");
    }
    if (!hasAmount && !hasPercent) {
      return 0;
    }
    int discount =
        hasPercent ? (int) Math.round(subtotal * (percent / 100.0)) : Math.max(0, amount);
    if (discount > subtotal) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "DISCOUNT_TOO_LARGE");
    }
    return discount;
  }
}

