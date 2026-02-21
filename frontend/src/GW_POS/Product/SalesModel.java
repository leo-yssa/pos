package GW_POS.Product;

import javax.swing.table.AbstractTableModel;

public class SalesModel extends AbstractTableModel {
	private String[] columnNames = {"No", "Date", "Time", "Total", "Pay"};
	private static final int ROWS = 1024;
	private static final int COLS = 5;
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
	
	public void add(String name, String date, String time, String total, String pay) {
			data[row][0] = name;
			data[row][1] = date;
			data[row][2] = time;
			data[row][3] = String.valueOf(total);
			data[row][4] = pay;
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
	
	public void setValueAt(Object value, int row, int col) {
		data[row][col] = value;
		fireTableCellUpdated(row, col);
	}
	
	public Object getValueAt(int row, int col) {
		return data[row][col];
	}
}
