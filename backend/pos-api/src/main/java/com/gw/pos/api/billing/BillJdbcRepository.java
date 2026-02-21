package com.gw.pos.api.billing;

import static com.gw.pos.api.billing.BillModels.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BillJdbcRepository {
  private final JdbcTemplate jdbc;

  public BillJdbcRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public long nextBillNumber() {
    Long next =
        jdbc.queryForObject(
            "select coalesce(max(cast(number as unsigned)), 0) + 1 from bills", Long.class);
    return next == null ? 1L : next;
  }

  public void insertBillHeader(long number, LocalDate date, LocalTime time, int total, PayMethod payMethod) {
    jdbc.update(
        "insert into bills (number, date, time, total, pay) values (?, ?, ?, ?, ?)",
        number,
        formatDate(date),
        formatTime(time),
        total,
        payMethod.name());
  }

  public void insertBillItems(long number, LocalDate date, LocalTime time, PayMethod payMethod, List<BillLineItem> items) {
    jdbc.batchUpdate(
        "insert into bill (number, date, time, name, volume, total, pay) values (?, ?, ?, ?, ?, ?, ?)",
        items,
        200,
        (ps, item) -> {
          ps.setLong(1, number);
          ps.setString(2, formatDate(date));
          ps.setString(3, formatTime(time));
          ps.setString(4, item.name());
          ps.setInt(5, item.volume());
          ps.setInt(6, item.total());
          ps.setString(7, payMethod.name());
        });
  }

  public List<BillHeader> findHeaders(LocalDate from, LocalDate toInclusive) {
    return jdbc.query(
        "select number, date, time, total, pay from bills where date between ? and ? order by date, time, number",
        (rs, rowNum) ->
            new BillHeader(
                rs.getLong("number"),
                parseDate(rs.getString("date")),
                parseTime(rs.getString("time")),
                rs.getInt("total"),
                PayMethod.valueOf(rs.getString("pay"))),
        formatDate(from),
        formatDate(toInclusive));
  }

  public BillHeader findHeader(long number, LocalDate date, LocalTime time) {
    return jdbc.queryForObject(
        "select number, date, time, total, pay from bills where number = ? and date = ? and time = ?",
        (rs, rowNum) ->
            new BillHeader(
                rs.getLong("number"),
                parseDate(rs.getString("date")),
                parseTime(rs.getString("time")),
                rs.getInt("total"),
                PayMethod.valueOf(rs.getString("pay"))),
        number,
        formatDate(date),
        formatTime(time));
  }

  public List<BillLineItem> findItems(long number, LocalDate date, LocalTime time) {
    return jdbc.query(
        "select name, volume, total from bill where number = ? and date = ? and time = ? order by name",
        (rs, rowNum) ->
            new BillLineItem(
                rs.getString("name"),
                rs.getInt("volume"),
                rs.getInt("total")),
        number,
        formatDate(date),
        formatTime(time));
  }

  private static String formatDate(LocalDate date) {
    return date.toString();
  }

  private static String formatTime(LocalTime time) {
    return time.getHour() + ":" + time.getMinute() + ":" + time.getSecond();
  }

  private static LocalDate parseDate(String raw) {
    return LocalDate.parse(raw);
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

