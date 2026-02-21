package GW_POS.Product;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import GW_POS.Api.PosApiClient;

public class OrderModel extends AbstractTableModel {
	private String[] columnNames = {"Product", "Price", "Qty", "Total"};
	private static final int ROWS = 1024;
	private static final int COLS = 4;
	private int row = 0;
	
	Object[][] data = new String[ROWS][COLS];
	
	public int getColumnCount() {
		return columnNames.length;
	}
	
	public int getRowCount() {
		return data.length;
	}
	
	public String getColumnName(int col) {
		return columnNames[col].toString();
	}
	
	public void add(String name, int price, int count, int total) {
		data[row][0] = name;
		data[row][1] = String.valueOf(price);
		data[row][2] = String.valueOf(count);
		data[row][3] = String.valueOf(total);
		row++;
		fireTableDataChanged();
	}
	public void add(String name, String price, String volume, String total) {
		data[row][0] = name;
		data[row][1] = price;
		data[row][2] = volume;
		data[row][3] = total;
		row++;
		fireTableDataChanged();
	}
	public void clearTable() {
		for(int i = 0; i < getRowCount(); i++) {
			for(int j = 0; j < getColumnCount(); j++) {
				data[i][j] = null;
			}
		}
		row = 0;
		fireTableDataChanged();
	}
	public void update(String name, int count, int total) {
		for(int i = 0; i < row; i++) {
			if(name.equals(data[i][0])) {
				data[i][2] = String.valueOf(count);
				data[i][3] = String.valueOf(total);
			}
		}
		fireTableDataChanged();
	}

	public List<PosApiClient.BillItemRequest> toBillItems() {
		List<PosApiClient.BillItemRequest> items = new ArrayList<PosApiClient.BillItemRequest>();
		for (int i = 0; i < row; i++) {
			if (data[i][0] == null || data[i][2] == null)
				continue;
			String name = String.valueOf(data[i][0]);
			int quantity = 0;
			try {
				quantity = Integer.parseInt(String.valueOf(data[i][2]));
			} catch (Exception e) {
				quantity = 0;
			}
			if (quantity > 0) {
				items.add(new PosApiClient.BillItemRequest(name, quantity));
			}
		}
		return items;
	}
	
	public void setValueAt(Object value, int row, int col) {
		data[row][col] = value;
		fireTableCellUpdated(row, col);
	}
	
	public Object getValueAt(int row, int col) {
		return data[row][col];
	}
}
