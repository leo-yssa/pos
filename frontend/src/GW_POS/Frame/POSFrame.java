package GW_POS.Frame;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import GW_POS.Api.PosApiClient;
import GW_POS.Panel.TitlebarPanel;
import GW_POS.Product.OrderModel;
import java.text.SimpleDateFormat;
public class POSFrame extends JFrame implements ActionListener {
/***************************POSFrame Field*************************************/
	private TitlebarPanel title;
	private JPanel type;
	private JPanel menu;
	private JPanel order;
	private JLabel lorder, ldiscount, ltotal, lfinal;
	private static int count=0;
	private int total = 0;
	private int finalTotal = 0;
	private Integer discountAmount = null;
	private Integer discountPercent = null;
	private JButton[] typebtn, menubtn;
	private JButton discount, cut, cancel, save;
	private JToggleButton cash, credit;
	private ButtonGroup bg;
	private ArrayList<String> list, list2;
	private Hashtable<String, Integer>hash2;
	private Hashtable<String, Integer>hash;
	private JTable table;
	private OrderModel model;
	private JScrollPane scrollPane;
	private DefaultTableCellRenderer celAlignCenter;
/***************************POSFrame Constructor*******************************/
	public POSFrame() {
		init();
	}
/***************************POSFrame Methods***********************************/
	public void init() {
		final int W = 1366;
		final int H = 768;
		final int TITLE_H = 56;
		this.setSize(W, H);
		this.setLayout(null);
		this.setUndecorated(true);
		
		title = new TitlebarPanel(W, TITLE_H);
		title.setBounds(0, 0, W, TITLE_H);
		this.add(title);
		
		type = new JPanel();
		type.setSize(W/2, 40);
		type.setLayout(new FlowLayout());
		type.setBounds(0, TITLE_H, W/2, 40);
		type.setBackground(Color.white);
		this.createTypeBtn();
		this.add(type);
		
		menu = new JPanel();
		menu.setSize(W/2, H - (TITLE_H + 40));
		menu.setLayout(new FlowLayout());
		menu.setBounds(0, TITLE_H + 40, W/2, H - (TITLE_H + 40));
		menu.setBackground(Color.WHITE);
		this.add(menu);
		
		order = new JPanel();
		order.setSize(W/2, H - TITLE_H);
		order.setBounds(W/2, TITLE_H, W/2, H - TITLE_H);
		order.setLayout(new BorderLayout());
		order.setBackground(Color.white);
		try {
			long next = PosApiClient.getDefault().nextBillNumber();
			count = (int) next - 1;
		} catch (Exception ex) {
			// ignore
		}
		String l = "Order #" + ++count; 
		lorder = new JLabel(l);
		lorder.setHorizontalAlignment(JLabel.CENTER);
		order.add(lorder, BorderLayout.NORTH);
		
		celAlignCenter = new DefaultTableCellRenderer();
		celAlignCenter.setHorizontalAlignment(JLabel.CENTER);
		model = new OrderModel();
		table = new JTable(model);
		table.setRowHeight(40);
		table.setShowHorizontalLines(false);
		table.getColumnModel().getColumn(0).setPreferredWidth(350);
		table.getColumnModel().getColumn(0).setCellRenderer(celAlignCenter);
		table.getColumnModel().getColumn(1).setPreferredWidth(125);
		table.getColumnModel().getColumn(1).setCellRenderer(celAlignCenter);
		table.getColumnModel().getColumn(2).setPreferredWidth(83);
		table.getColumnModel().getColumn(2).setCellRenderer(celAlignCenter);
		table.getColumnModel().getColumn(3).setPreferredWidth(125);
		table.getColumnModel().getColumn(3).setCellRenderer(celAlignCenter);
		
		scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(1366/2,400));
		order.add(scrollPane, BorderLayout.CENTER);
		
		
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(1366/2, 200));
		panel.setBackground(Color.WHITE);
		panel.setLayout(new GridLayout(4,4));
		
		discount = new JButton("Discount");
		discount.addActionListener(this);
		panel.add(discount);
		
		cut = new JButton("Split");
		cut.addActionListener(this);
		panel.add(cut);
		
		bg = new ButtonGroup();
		cash = new JToggleButton("Cash");
		cash.addActionListener(this);
		bg.add(cash);
		panel.add(cash);
		
		credit = new JToggleButton("Card");
		credit.addActionListener(this);
		bg.add(credit);
		panel.add(credit);
		
		panel.add(new JLabel("Discount : "));
		ldiscount = new JLabel("");
		panel.add(ldiscount);
		panel.add(new JLabel(""));
		panel.add(new JLabel(""));
		panel.add(new JLabel("Total : "));
		ltotal = new JLabel("");
		panel.add(ltotal);
		panel.add(new JLabel("Final : "));
		lfinal = new JLabel("");
		panel.add(lfinal);
		panel.add(new JLabel(""));
		panel.add(new JLabel(""));
		cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		panel.add(cancel);
		save = new JButton("Pay");
		save.addActionListener(this);
		panel.add(save);
		
		
		order.add(panel, BorderLayout.SOUTH);
		this.add(order);
		
		this.setVisible(true);
	}
	public void createTypeBtn() {
		try {
			list = new ArrayList<String>(PosApiClient.getDefault().listTypes());
			typebtn = new JButton[list.size()];
			for(int i = 0; i < list.size(); i++) {
				String label = list.get(i);
				typebtn[i] = new JButton(label);
				typebtn[i].setOpaque(false);
				typebtn[i].setPreferredSize(new Dimension(136, 30));
				typebtn[i].addActionListener(this);
				type.add(typebtn[i]);
			}
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(this, "Failed to load types.\n" + ex.getMessage());
		}
	}
	public void createMenuBtn(String type) {
		try {
			menu.removeAll();
			List<PosApiClient.Product> products = PosApiClient.getDefault().listProductsByType(type);
			list2 = new ArrayList<String>();
			if (hash2 == null)
				hash2 = new Hashtable<String, Integer>();
			if (hash == null)
				hash = new Hashtable<String, Integer>();
			for (PosApiClient.Product p : products) {
				list2.add(p.name);
				if (!hash2.containsKey(p.name))
					hash2.put(p.name, 0);
				hash.put(p.name, p.price);
			}
			menubtn = new JButton[list2.size()];
			for(int i = 0; i < list2.size(); i++) {
				String label = list2.get(i);
				menubtn[i] = new JButton(label);
				menubtn[i].setOpaque(false);
				menubtn[i].setPreferredSize(new Dimension(170, 80));
				menubtn[i].addActionListener(this);
				menu.add(menubtn[i]);
			}
			menu.revalidate();
			this.repaint();
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(this, "Failed to load products.\n" + ex.getMessage());
		}
		
	}

	private void recalcTotals() {
		int discount = computeDiscount(total);
		finalTotal = Math.max(0, total - discount);

		ltotal.setText(total == 0 ? "" : String.valueOf(total));
		lfinal.setText(total == 0 ? "" : String.valueOf(finalTotal));

		if (discount == 0) {
			ldiscount.setText("");
		} else if (discountPercent != null) {
			ldiscount.setText(discountPercent + "% (-" + discount + ")");
		} else {
			ldiscount.setText("-" + discount);
		}
	}

	private int computeDiscount(int subtotal) {
		if (subtotal <= 0) {
			return 0;
		}
		if (discountPercent != null && discountPercent.intValue() > 0) {
			int p = Math.max(0, Math.min(100, discountPercent.intValue()));
			return (int) Math.round(subtotal * (p / 100.0));
		}
		if (discountAmount != null && discountAmount.intValue() > 0) {
			return Math.min(subtotal, Math.max(0, discountAmount.intValue()));
		}
		return 0;
	}

	private void clearDiscount() {
		discountAmount = null;
		discountPercent = null;
	}

	private void handleDiscountClick() {
		String raw = JOptionPane.showInputDialog(this, "Discount (e.g. 10% or 1000). Empty to clear.");
		if (raw == null) {
			return; // cancelled
		}
		raw = raw.trim();
		if (raw.isEmpty()) {
			clearDiscount();
			recalcTotals();
			return;
		}
		try {
			if (raw.endsWith("%")) {
				String p = raw.substring(0, raw.length() - 1).trim();
				int percent = Integer.parseInt(p);
				discountPercent = Math.max(0, Math.min(100, percent));
				discountAmount = null;
			} else {
				int amount = Integer.parseInt(raw);
				discountAmount = Math.max(0, amount);
				discountPercent = null;
			}
			recalcTotals();
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "Invalid discount value.");
		}
	}

	private void handleSplitClick() {
		if (total <= 0) {
			JOptionPane.showMessageDialog(this, "No items to split.");
			return;
		}
		String raw = JOptionPane.showInputDialog(this, "Split count (e.g. 2, 3, 4)");
		if (raw == null) {
			return;
		}
		raw = raw.trim();
		if (raw.isEmpty()) {
			return;
		}
		try {
			int n = Integer.parseInt(raw);
			if (n < 2) {
				JOptionPane.showMessageDialog(this, "Split count must be >= 2.");
				return;
			}
			int each = finalTotal / n;
			int rem = finalTotal % n;
			String msg = "Final total: " + finalTotal + "\nSplit: " + n + "\nEach: " + each;
			if (rem != 0) {
				msg += "\nRemainder: " + rem;
			}
			JOptionPane.showMessageDialog(this, msg);
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "Invalid split count.");
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(list != null) {
			for(int i = 0; i < list.size(); i++) {
				String type = list.get(i);
				if(e.getActionCommand().equals(type)) {
					this.createMenuBtn(type);
					break;
				}
			}
		}
		if(list2 != null) {
			for(int i = 0; i < list2.size(); i++) {
				String name = list2.get(i);
				int n = hash2.get(name);
				if(e.getActionCommand().equals(name)) {
					n++;
					hash2.put(name, n);
					if(n == 1){
						model.add(name, hash.get(name), n, hash.get(name)*n);
						total += hash.get(name)*n;
						recalcTotals();
					}
					if(n > 1){
						model.update(name, n, hash.get(name)*n);
						total += hash.get(name);
						recalcTotals();
					}
				}
			}
		}
		if (e.getSource() == discount) {
			handleDiscountClick();
		}
		if (e.getSource() == cut) {
			handleSplitClick();
		}
		if(e.getSource() == save) {
			long totalMilliseconds = System.currentTimeMillis();
			long totalSeconds = totalMilliseconds/1000;
			long currentSecond = totalSeconds%60;
			long totalMin = totalSeconds/60;
			long currentMin = totalMin%60;
			long totalHours = currentMin/60;
			long currentHours = totalHours%24;
			String time = currentHours + ":" + currentMin + ":" + currentSecond;
			Date today = new Date();
			SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd");
			String date = format.format(today);
			if(cash.isSelected()) {
				
				int result = JOptionPane.showConfirmDialog(this, "Pay with cash?","",JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
				if( result == 0 ) {
					try {
						PosApiClient.CreateBillResponse r = PosApiClient.getDefault().createBill("CASH", model.toBillItems(), discountAmount, discountPercent);
						count = (int) r.number;
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(this, "Failed to save payment.\n" + ex.getMessage());
						return;
					}
					count++;
					String l = "Order #" + count;
					lorder.setText(l);
					total = 0;
					finalTotal = 0;
					clearDiscount();
					ltotal.setText("");
					lfinal.setText("");
					ldiscount.setText("");
					model.clearTable();
				}
			} 
			else if(credit.isSelected()) {
				int result = JOptionPane.showConfirmDialog(this, "Pay with card?","",JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
				if( result == 0 ) {
					try {
						PosApiClient.CreateBillResponse r = PosApiClient.getDefault().createBill("CARD", model.toBillItems(), discountAmount, discountPercent);
						count = (int) r.number;
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(this, "Failed to save payment.\n" + ex.getMessage());
						return;
					}
					count++;
					String l = "Order #" + count;
					lorder.setText(l);
					total = 0;
					finalTotal = 0;
					clearDiscount();
					ltotal.setText("");
					lfinal.setText("");
					ldiscount.setText("");
					model.clearTable();	
				}
			}
			else {
				JOptionPane.showMessageDialog(this, "Select payment method.");
			}
			this.revalidate();
			this.repaint();
		}
		if(e.getSource() == cancel) {
			if(hash != null)
				hash.clear();
			if(hash2 != null)
				hash2.clear();
			if(list2 != null)
				list2.clear();
			model.clearTable();
			total = 0;
			finalTotal = 0;
			clearDiscount();
			ltotal.setText("");
			lfinal.setText("");
			ldiscount.setText("");
			this.repaint();
			this.revalidate();
		}
	}
}

