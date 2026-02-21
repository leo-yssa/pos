package GW_POS.Product;

import java.io.IOException;

import javax.swing.table.AbstractTableModel;
import GW_POS.Api.PosApiClient;

public class TableModel extends AbstractTableModel {
	private String[] columnNames = {"Product", "Type", "Price", "Sold"};
	private static final int ROWS = 50;
	private static final int COLS = 4;
	Object[][] data = new String[ROWS][COLS];
	private int row = 0;
	public int getColumnCount() {
		return columnNames.length;
	}
	
	public int getRowCount() {
		return data.length;
	}
	
	public String getColumnName(int col) {
		return columnNames[col].toString();
	}
	
	public void fillTable() {
		try {
			removeTable();
			java.util.List<PosApiClient.Product> products = PosApiClient.getDefault().listAllProducts();
			for (PosApiClient.Product p : products) {
				if (row >= ROWS)
					break;
				data[row][0] = p.name;
				data[row][1] = p.type;
				data[row][2] = String.valueOf(p.price);
				data[row][3] = String.valueOf(p.soldVolume);
				row++;
			}
		} catch (IOException e) {
			// UI layer handles errors
		}
		
		fireTableDataChanged();
	}
	public void add(String name, String type, String price, String svolume) {
		data[row][0] = name;
		data[row][1] = type;
		data[row][2] = price;
		data[row][3] = svolume;
		row++;
		fireTableDataChanged();
	}
	public void removeTable() {
		for(int i = 0; i < getRowCount(); i++) {
			for(int j = 0; j < getColumnCount(); j++) {
				data[i][j] = null;
			}
		}
		row = 0;
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
