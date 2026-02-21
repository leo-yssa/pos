package com.gw.pos.api.api;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
  private final JdbcTemplate jdbc;

  public AnalyticsController(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public record TopProductDto(String name, int volume) {}
  public record RevenuePointDto(String label, int total) {}

  @GetMapping("/top-products")
  public List<TopProductDto> topProducts(
      @RequestParam("from") LocalDate from,
      @RequestParam("to") LocalDate to,
      @RequestParam(name = "limit", defaultValue = "5") int limit,
      @RequestParam(name = "timeSlot", required = false) Integer timeSlot) {
    int safeLimit = Math.max(1, Math.min(limit, 50));
    String slotFilter = timeSlot == null ? "" : " and " + slotPredicate(timeSlot);
    return jdbc.query(
        "select name, sum(volume) as volume from bill where date between ? and ? and name <> 'DISCOUNT'"
            + slotFilter
            + " group by name order by volume desc limit ?",
        (ResultSet rs, int rowNum) -> new TopProductDto(rs.getString("name"), rs.getInt("volume")),
        from.toString(),
        to.toString(),
        safeLimit);
  }

  @GetMapping("/revenue")
  public List<RevenuePointDto> revenue(
      @RequestParam("from") LocalDate from,
      @RequestParam("to") LocalDate to,
      @RequestParam(name = "mode", defaultValue = "MONTH") String mode) {
    String m = mode == null ? "MONTH" : mode.trim().toUpperCase();
    if ("MONTH".equals(m)) {
      return jdbc.query(
          "select left(date, 7) as label, sum(total) as total from bills where date between ? and ? group by left(date, 7) order by label",
          (ResultSet rs, int rowNum) -> new RevenuePointDto(rs.getString("label"), rs.getInt("total")),
          from.toString(),
          to.toString());
    }
    if ("TIME".equals(m)) {
      Map<String, Integer> out = new LinkedHashMap<>();
      out.put("5-8", 0);
      out.put("8-11", 0);
      out.put("11-14", 0);
      out.put("14-17", 0);

      List<RevenuePointDto> rows =
          jdbc.query(
              "select label, sum(total) as total from ("
                  + " select (case"
                  + " when cast(substring_index(time, ':', 1) as unsigned) between 5 and 7 then '5-8'"
                  + " when cast(substring_index(time, ':', 1) as unsigned) between 8 and 10 then '8-11'"
                  + " when cast(substring_index(time, ':', 1) as unsigned) between 11 and 13 then '11-14'"
                  + " when cast(substring_index(time, ':', 1) as unsigned) between 14 and 16 then '14-17'"
                  + " else null end) as label, total"
                  + " from bills where date between ? and ?"
                  + " ) t where label is not null group by label order by label",
              (ResultSet rs, int rowNum) -> new RevenuePointDto(rs.getString("label"), rs.getInt("total")),
              from.toString(),
              to.toString());
      for (RevenuePointDto r : rows) {
        out.put(r.label(), r.total());
      }
      return out.entrySet().stream().map(e -> new RevenuePointDto(e.getKey(), e.getValue())).toList();
    }
    throw new IllegalArgumentException("Unsupported mode: " + mode);
  }

  private static String slotPredicate(int timeSlot) {
    // time column is stored as "H:M:S"
    String hour = "cast(substring_index(time, ':', 1) as unsigned)";
    return switch (timeSlot) {
      case 1 -> hour + " between 5 and 7";
      case 2 -> hour + " between 8 and 10";
      case 3 -> hour + " between 11 and 13";
      case 4 -> hour + " between 14 and 16";
      default -> "1=1";
    };
  }
}

