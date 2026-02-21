package GW_POS.Product;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import GW_POS.Api.PosApiClient;

public class ProductTable extends JFrame implements ActionListener {
	
	private JTable table;
	private TableModel model;
	private JTextField productPrice;
	private JTextField productName;
	private JComboBox<String> productType;
	private JButton search, add, delete;
	private JScrollPane scrollPane;
	public ProductTable() {
		super("Product Management");
		setSize(1000,300);
		
		model = new TableModel();
		model.fillTable();
		JLabel label = new JLabel("Products");
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setFont(new Font("SansSerif", Font.PLAIN, 30));
		add(label, BorderLayout.NORTH);
		
		table = new JTable(model);
		
		scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(500,200));
		add(scrollPane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		
		panel.add(new JLabel("Type"));
		productType = new JComboBox<String>();
		this.fillCombobox();
		panel.add(productType);
		
		panel.add(new JLabel("Name"));
		productName = new JTextField(10);
		panel.add(productName);
		
		panel.add(new JLabel("Price"));
		productPrice = new JTextField(10);
		panel.add(productPrice);
		
		search = new JButton("Search");
		search.addActionListener(this);
		panel.add(search);
		
		add = new JButton("Add");
		add.addActionListener(this);
		panel.add(add);
		
		delete = new JButton("Delete");
		delete.addActionListener(this);
		panel.add(delete);
		
		add(panel, BorderLayout.SOUTH);
		
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent event) {
		String name = productName.getText();
		String type = (String)productType.getSelectedItem();
		String price = productPrice.getText();
		try {
			if(event.getSource() == add) {
				if(name != null && !name.equals("") && price != null && !price.equals("")) {
					int iprice = Integer.parseInt(price);
					PosApiClient.getDefault().createProduct(name, type, iprice);
					productName.setText("");
					productPrice.setText("");
					model.fillTable();
				}else {
					JOptionPane.showMessageDialog(this, "Enter product name and price.");
				}
			}
			if(event.getSource() == search) {
				model.removeTable();
				java.util.List<PosApiClient.Product> products = PosApiClient.getDefault().listProductsByType(type);
				for (PosApiClient.Product p : products) {
					if (name == null || name.equals("") || p.name.contains(name)) {
						model.add(p.name, p.type, String.valueOf(p.price), String.valueOf(p.soldVolume));
					}
				}
				this.repaint();
				this.revalidate();
				productName.setText("");
				productPrice.setText("");
			}
			
			if(event.getSource() == delete) {
				if(name != null && !name.equals("")) {
					PosApiClient.getDefault().deleteProduct(name);
					model.removeTable();
					model.fillTable();
					this.repaint();
					this.revalidate();
				}else
					System.out.println("error");
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "API request failed.\n" + e.getMessage());
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Price must be a number.");
		}
	}
	public void fillCombobox() {
		try {
			java.util.List<String> types = PosApiClient.getDefault().listTypes();
			for (String name : types) {
				productType.addItem(name);
			}
		}catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Failed to load types.\n" + e.getMessage());
		}
	}
	
	public void removeCombobox() {
		productType.removeAllItems();
	}
}
