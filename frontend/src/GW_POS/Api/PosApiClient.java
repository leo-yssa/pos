package GW_POS.Api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class PosApiClient {
	private final String baseUrl;

	private static volatile PosApiClient DEFAULT;

	public static PosApiClient getDefault() {
		if (DEFAULT == null) {
			synchronized (PosApiClient.class) {
				if (DEFAULT == null) {
					DEFAULT = new PosApiClient(resolveBaseUrl());
				}
			}
		}
		return DEFAULT;
	}

	public PosApiClient(String baseUrl) {
		this.baseUrl = trimTrailingSlash(baseUrl);
	}

	public List<String> listUserIds() throws IOException {
		String json = request("GET", "/api/users", null, true);
		return Json.parseStringArray(json);
	}

	public boolean login(String id, String password) throws IOException {
		String body = "{\"id\":\"" + Json.escape(id) + "\",\"password\":\"" + Json.escape(password) + "\"}";
		String json = request("POST", "/api/auth/login", body, true);
		try {
			return Json.extractBoolean(json, "ok");
		} catch (IllegalArgumentException ex) {
			throw new IOException("Invalid response from API server: " + summarize(json));
		}
	}

	public void createUser(String id, String password) throws IOException {
		String body = "{\"id\":\"" + Json.escape(id) + "\",\"password\":\"" + Json.escape(password) + "\"}";
		request("POST", "/api/users", body, false);
	}

	public void deleteUser(String id) throws IOException {
		request("DELETE", "/api/users/" + urlEncodePath(id), null, false);
	}

	public List<String> listTypes() throws IOException {
		String json = request("GET", "/api/types", null, true);
		return Json.parseStringArray(json);
	}

	public List<Product> listProductsByType(String type) throws IOException {
		String json = request("GET", "/api/products?type=" + urlEncode(type), null, true);
		return Json.parseProducts(json);
	}
	
	public List<Product> listAllProducts() throws IOException {
		String json = request("GET", "/api/products", null, true);
		return Json.parseProducts(json);
	}

	public void createProduct(String name, String type, int price) throws IOException {
		String body =
				"{\"name\":\"" + Json.escape(name) + "\",\"type\":\"" + Json.escape(type) + "\",\"price\":" + price + "}";
		request("POST", "/api/products", body, true);
	}

	public void deleteProduct(String name) throws IOException {
		request("DELETE", "/api/products/" + urlEncodePath(name), null, false);
	}

	public CreateBillResponse createBill(String payMethod, List<BillItemRequest> items) throws IOException {
		return createBill(payMethod, items, null, null);
	}

	public CreateBillResponse createBill(String payMethod, List<BillItemRequest> items, Integer discountAmount, Integer discountPercent)
			throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"payMethod\":\"").append(Json.escape(payMethod)).append("\",\"items\":[");
		for (int i = 0; i < items.size(); i++) {
			BillItemRequest it = items.get(i);
			if (i > 0)
				sb.append(",");
			sb.append("{\"productName\":\"").append(Json.escape(it.productName)).append("\",\"quantity\":")
					.append(it.quantity).append("}");
		}
		sb.append("]");
		if (discountAmount != null) {
			sb.append(",\"discountAmount\":").append(discountAmount.intValue());
		}
		if (discountPercent != null) {
			sb.append(",\"discountPercent\":").append(discountPercent.intValue());
		}
		sb.append("}");
		String json = request("POST", "/api/bills", sb.toString(), true);
		return Json.parseCreateBillResponse(json);
	}
	
	public long nextBillNumber() throws IOException {
		String json = request("GET", "/api/bills/next-number", null, false);
		try {
			return Long.parseLong(json.trim());
		} catch (Exception e) {
			return 1L;
		}
	}

	public List<BillHeader> listBills(String fromDate, String toDate) throws IOException {
		String json = request("GET", "/api/bills?from=" + urlEncode(fromDate) + "&to=" + urlEncode(toDate), null, true);
		return Json.parseBillHeaders(json);
	}

	public BillDetail getBillDetail(long number, String date, String time) throws IOException {
		String path = "/api/bills/" + number + "?date=" + urlEncode(date) + "&time=" + urlEncode(time);
		String json = request("GET", path, null, true);
		return Json.parseBillDetail(json);
	}
	
	public List<TopProduct> topProducts(String fromDate, String toDate, int limit) throws IOException {
		return topProducts(fromDate, toDate, limit, null);
	}

	public List<TopProduct> topProducts(String fromDate, String toDate, int limit, Integer timeSlot) throws IOException {
		String json = request("GET",
				"/api/analytics/top-products?from=" + urlEncode(fromDate) + "&to=" + urlEncode(toDate) + "&limit=" + limit
						+ (timeSlot == null ? "" : "&timeSlot=" + timeSlot.intValue()),
				null,
				true);
		return Json.parseTopProducts(json);
	}

	public List<RevenuePoint> revenue(String fromDate, String toDate, String mode) throws IOException {
		String json = request("GET",
				"/api/analytics/revenue?from=" + urlEncode(fromDate) + "&to=" + urlEncode(toDate) + "&mode=" + urlEncode(mode),
				null,
				true);
		return Json.parseRevenuePoints(json);
	}

	private String request(String method, String pathAndQuery, String jsonBody, boolean expectJson) throws IOException {
		URL url = new URL(baseUrl + pathAndQuery);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod(method);
		con.setConnectTimeout(5000);
		con.setReadTimeout(10000);
		con.setRequestProperty("Accept", expectJson ? "application/json" : "*/*");

		if (jsonBody != null) {
			con.setDoOutput(true);
			con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			byte[] bytes = jsonBody.getBytes("UTF-8");
			con.setFixedLengthStreamingMode(bytes.length);
			try (OutputStream os = con.getOutputStream()) {
				os.write(bytes);
			}
		}

		int code = con.getResponseCode();
		InputStream is = (code >= 200 && code < 300) ? con.getInputStream() : con.getErrorStream();
		String body = readAll(is);
		if (code < 200 || code >= 300) {
			throw new IOException("HTTP " + code + " " + method + " " + pathAndQuery + " -> " + body);
		}
		if (expectJson && !looksLikeJson(body)) {
			String ct = con.getHeaderField("Content-Type");
			throw new IOException("Expected JSON from API but got: content-type=" + ct + ", body=" + summarize(body));
		}
		return body;
	}

	private static boolean looksLikeJson(String s) {
		if (s == null)
			return false;
		String t = s.trim();
		return t.startsWith("{") || t.startsWith("[");
	}

	private static String summarize(String s) {
		if (s == null)
			return "";
		String t = s.trim().replace("\n", " ").replace("\r", " ");
		if (t.length() <= 200)
			return t;
		return t.substring(0, 200) + "...";
	}

	private static String readAll(InputStream is) throws IOException {
		if (is == null)
			return "";
		StringBuilder sb = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		}
		return sb.toString();
	}

	private static String urlEncode(String s) throws IOException {
		return URLEncoder.encode(s, "UTF-8");
	}

	private static String urlEncodePath(String s) throws IOException {
		return URLEncoder.encode(s, "UTF-8").replace("+", "%20");
	}

	private static String trimTrailingSlash(String s) {
		if (s == null)
			return "";
		if (s.endsWith("/"))
			return s.substring(0, s.length() - 1);
		return s;
	}

	private static String resolveBaseUrl() {
		String sys = System.getProperty("POS_API_BASE_URL");
		if (sys != null && !sys.trim().isEmpty())
			return sys.trim();
		String env = System.getenv("POS_API_BASE_URL");
		if (env != null && !env.trim().isEmpty())
			return env.trim();
		return "http://localhost:8080";
	}

	public static class Product {
		public final String name;
		public final String type;
		public final int price;
		public final int soldVolume;

		public Product(String name, String type, int price, int soldVolume) {
			this.name = name;
			this.type = type;
			this.price = price;
			this.soldVolume = soldVolume;
		}
	}

	public static class BillItemRequest {
		public final String productName;
		public final int quantity;

		public BillItemRequest(String productName, int quantity) {
			this.productName = productName;
			this.quantity = quantity;
		}
	}

	public static class CreateBillResponse {
		public final long number;
		public final String date;
		public final String time;
		public final int total;
		public final String payMethod;

		public CreateBillResponse(long number, String date, String time, int total, String payMethod) {
			this.number = number;
			this.date = date;
			this.time = time;
			this.total = total;
			this.payMethod = payMethod;
		}
	}

	public static class BillHeader {
		public final String number;
		public final String date;
		public final String time;
		public final String total;
		public final String payMethod;

		public BillHeader(String number, String date, String time, String total, String payMethod) {
			this.number = number;
			this.date = date;
			this.time = time;
			this.total = total;
			this.payMethod = payMethod;
		}
	}

	public static class BillLine {
		public final String name;
		public final String volume;
		public final String total;

		public BillLine(String name, String volume, String total) {
			this.name = name;
			this.volume = volume;
			this.total = total;
		}
	}

	public static class BillDetail {
		public final BillHeader header;
		public final List<BillLine> items;

		public BillDetail(BillHeader header, List<BillLine> items) {
			this.header = header;
			this.items = items;
		}
	}
	
	public static class TopProduct {
		public final String name;
		public final int volume;
		
		public TopProduct(String name, int volume) {
			this.name = name;
			this.volume = volume;
		}
	}

	public static class RevenuePoint {
		public final String label;
		public final int total;

		public RevenuePoint(String label, int total) {
			this.label = label;
			this.total = total;
		}
	}

	private static final class Json {
		private Json() {}

		static String escape(String s) {
			if (s == null)
				return "";
			StringBuilder sb = new StringBuilder(s.length() + 16);
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				switch (c) {
				case '\\':
					sb.append("\\\\");
					break;
				case '"':
					sb.append("\\\"");
					break;
				case '\n':
					sb.append("\\n");
					break;
				case '\r':
					sb.append("\\r");
					break;
				case '\t':
					sb.append("\\t");
					break;
				default:
					sb.append(c);
				}
			}
			return sb.toString();
		}

		static List<String> parseStringArray(String json) {
			if (json == null)
				return Collections.emptyList();
			String s = json.trim();
			if (s.length() < 2 || s.charAt(0) != '[')
				return Collections.emptyList();
			List<String> out = new ArrayList<String>();
			boolean inString = false;
			StringBuilder cur = new StringBuilder();
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				if (!inString) {
					if (c == '"') {
						inString = true;
						cur.setLength(0);
					}
				} else {
					if (c == '\\') {
						if (i + 1 < s.length()) {
							char n = s.charAt(i + 1);
							cur.append(n);
							i++;
						}
					} else if (c == '"') {
						inString = false;
						out.add(cur.toString());
					} else {
						cur.append(c);
					}
				}
			}
			return out;
		}

		static boolean extractBoolean(String json, String key) {
			String needle = "\"" + key + "\":";
			int idx = json.indexOf(needle);
			if (idx < 0)
				throw new IllegalArgumentException("missing key: " + key);
			int start = idx + needle.length();
			String rest = json.substring(start).trim().toLowerCase(Locale.ROOT);
			return rest.startsWith("true");
		}

		static List<Product> parseProducts(String json) {
			List<String> objs = splitObjectsArray(json);
			List<Product> out = new ArrayList<Product>(objs.size());
			for (String obj : objs) {
				String name = extractString(obj, "name");
				String type = extractString(obj, "type");
				int price = extractInt(obj, "price");
				int sold = extractInt(obj, "soldVolume");
				out.add(new Product(name, type, price, sold));
			}
			return out;
		}

		static CreateBillResponse parseCreateBillResponse(String json) {
			long number = extractLong(json, "number");
			String date = extractString(json, "date");
			String time = extractString(json, "time");
			int total = extractInt(json, "total");
			String payMethod = extractString(json, "payMethod");
			return new CreateBillResponse(number, date, time, total, payMethod);
		}

		static List<BillHeader> parseBillHeaders(String json) {
			List<String> objs = splitObjectsArray(json);
			List<BillHeader> out = new ArrayList<BillHeader>(objs.size());
			for (String obj : objs) {
				String number = extractRaw(obj, "number");
				String date = extractString(obj, "date");
				String time = extractString(obj, "time");
				String total = extractRaw(obj, "total");
				String payMethod = extractString(obj, "payMethod");
				out.add(new BillHeader(number, date, time, total, payMethod));
			}
			return out;
		}

		static BillDetail parseBillDetail(String json) {
			String headerObj = extractObject(json, "header");
			String itemsArr = extractArray(json, "items");
			BillHeader header =
					new BillHeader(extractRaw(headerObj, "number"), extractString(headerObj, "date"),
							extractString(headerObj, "time"), extractRaw(headerObj, "total"),
							extractString(headerObj, "payMethod"));
			List<String> itemObjs = splitObjectsArray(itemsArr);
			List<BillLine> items = new ArrayList<BillLine>(itemObjs.size());
			for (String obj : itemObjs) {
				String name = extractString(obj, "name");
				String volume = extractRaw(obj, "volume");
				String total = extractRaw(obj, "total");
				items.add(new BillLine(name, volume, total));
			}
			return new BillDetail(header, items);
		}
		
		static List<TopProduct> parseTopProducts(String json) {
			List<String> objs = splitObjectsArray(json);
			List<TopProduct> out = new ArrayList<TopProduct>(objs.size());
			for (String obj : objs) {
				String name = extractString(obj, "name");
				int volume = extractInt(obj, "volume");
				out.add(new TopProduct(name, volume));
			}
			return out;
		}

		static List<RevenuePoint> parseRevenuePoints(String json) {
			List<String> objs = splitObjectsArray(json);
			List<RevenuePoint> out = new ArrayList<RevenuePoint>(objs.size());
			for (String obj : objs) {
				String label = extractString(obj, "label");
				int total = extractInt(obj, "total");
				out.add(new RevenuePoint(label, total));
			}
			return out;
		}

		private static List<String> splitObjectsArray(String json) {
			if (json == null)
				return Collections.emptyList();
			String s = json.trim();
			if (s.isEmpty())
				return Collections.emptyList();
			if (s.charAt(0) != '[')
				return Collections.emptyList();
			List<String> out = new ArrayList<String>();
			int depth = 0;
			int start = -1;
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				if (c == '{') {
					if (depth == 0)
						start = i;
					depth++;
				} else if (c == '}') {
					depth--;
					if (depth == 0 && start >= 0) {
						out.add(s.substring(start, i + 1));
						start = -1;
					}
				}
			}
			return out;
		}

		private static String extractString(String obj, String key) {
			String needle = "\"" + key + "\":";
			int idx = obj.indexOf(needle);
			if (idx < 0)
				return "";
			int i = idx + needle.length();
			while (i < obj.length() && Character.isWhitespace(obj.charAt(i)))
				i++;
			if (i >= obj.length() || obj.charAt(i) != '"')
				return "";
			i++;
			StringBuilder sb = new StringBuilder();
			for (; i < obj.length(); i++) {
				char c = obj.charAt(i);
				if (c == '\\') {
					if (i + 1 < obj.length()) {
						sb.append(obj.charAt(i + 1));
						i++;
					}
				} else if (c == '"') {
					break;
				} else {
					sb.append(c);
				}
			}
			return sb.toString();
		}

		private static String extractRaw(String obj, String key) {
			String needle = "\"" + key + "\":";
			int idx = obj.indexOf(needle);
			if (idx < 0)
				return "";
			int i = idx + needle.length();
			while (i < obj.length() && Character.isWhitespace(obj.charAt(i)))
				i++;
			int end = i;
			while (end < obj.length() && obj.charAt(end) != ',' && obj.charAt(end) != '}' && obj.charAt(end) != ']')
				end++;
			return obj.substring(i, end).trim().replace("\"", "");
		}

		private static int extractInt(String obj, String key) {
			String raw = extractRaw(obj, key);
			try {
				return Integer.parseInt(raw);
			} catch (Exception e) {
				return 0;
			}
		}

		private static long extractLong(String obj, String key) {
			String raw = extractRaw(obj, key);
			try {
				return Long.parseLong(raw);
			} catch (Exception e) {
				return 0L;
			}
		}

		private static String extractObject(String json, String key) {
			String needle = "\"" + key + "\":";
			int idx = json.indexOf(needle);
			if (idx < 0)
				return "{}";
			int i = idx + needle.length();
			while (i < json.length() && json.charAt(i) != '{')
				i++;
			if (i >= json.length())
				return "{}";
			int depth = 0;
			int start = i;
			for (; i < json.length(); i++) {
				char c = json.charAt(i);
				if (c == '{')
					depth++;
				else if (c == '}') {
					depth--;
					if (depth == 0)
						return json.substring(start, i + 1);
				}
			}
			return "{}";
		}

		private static String extractArray(String json, String key) {
			String needle = "\"" + key + "\":";
			int idx = json.indexOf(needle);
			if (idx < 0)
				return "[]";
			int i = idx + needle.length();
			while (i < json.length() && json.charAt(i) != '[')
				i++;
			if (i >= json.length())
				return "[]";
			int depth = 0;
			int start = i;
			for (; i < json.length(); i++) {
				char c = json.charAt(i);
				if (c == '[')
					depth++;
				else if (c == ']') {
					depth--;
					if (depth == 0)
						return json.substring(start, i + 1);
				}
			}
			return "[]";
		}
	}
}

