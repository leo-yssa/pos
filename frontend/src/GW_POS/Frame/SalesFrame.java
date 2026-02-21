package GW_POS.Frame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import GW_POS.Api.PosApiClient;
import GW_POS.Product.OrderModel;
import GW_POS.Product.SalesModel;
import cambodia.raven.DateChooser;

public class SalesFrame extends JFrame implements ActionListener,MouseListener{
	private JTable table1,table2;
	private JScrollPane jscroll,scrollPane;
	private SalesModel model;
	private OrderModel order;
	private JPanel calendar, receipt, detail; 				
	private DateChooser date_Chooser;
	private JButton btnsearch, bt1w, bt1m, bt3m, bt6m, bt1y;
	private JTextField y1,y2,m1,m2,d1,d2;
	private JTextField total1,mtotal,cash,card;
	private DefaultTableCellRenderer celAlignCenter;

    public SalesFrame() {
    	super("SalesManagement");
/****************************calendar*****************************/
    	calendar = new JPanel();
    	calendar.setLayout(new GridLayout(2,1));
    	JPanel Aa = new JPanel();
    	JPanel Ab = new JPanel();
    	Aa.setBorder(BorderFactory.createTitledBorder("Day"));
    	Ab.setBorder(BorderFactory.createTitledBorder("Range"));
     
    	calendar.add(Aa);
    	calendar.add(Ab);
/*******************************Aa********************************/
    	date_Chooser = new cambodia.raven.DateChooser();
    	Aa.add(date_Chooser);
/*******************************Ab********************************/
    	Ab.setLayout(new FlowLayout(FlowLayout.RIGHT)); 
    	bt1w= new JButton("1W");
    	bt1m= new JButton("1M");
    	bt3m= new JButton("3M");
    	bt6m= new JButton("6M");
    	bt1y= new JButton("1Y");
    	btnsearch = new JButton("Search");
    	btnsearch.setPreferredSize(new Dimension(330,80));
     
    	Ab.add(bt1w);
    	Ab.add(bt1m);
    	Ab.add(bt3m);
    	Ab.add(bt6m);
    	Ab.add(bt1y);
     
    	Ab.add(y1 = new JTextField(5));
    	Ab.add(new JLabel(" Y "));
    	Ab.add(m1 = new JTextField(5));
    	Ab.add(new JLabel(" M "));
    	Ab.add(d1 = new JTextField(5));
    	Ab.add(new JLabel(" D "));
    	Ab.add(new JLabel(" ~ "));
    	Ab.add(y2 = new JTextField(5));
    	Ab.add(new JLabel(" Y "));
    	Ab.add(m2 = new JTextField(5));
    	Ab.add(new JLabel(" M "));
    	Ab.add(d2 = new JTextField(5));
    	Ab.add(new JLabel(" D "));
    	Ab.add(btnsearch);
     
    	JLabel jla1=new JLabel("Total : ");
    	JLabel jla2=new JLabel("Cash : ");
    	JLabel jla3=new JLabel("Card : ");
    	jla1.setFont(new Font("SansSerif", Font.BOLD, 25));
    	jla2.setFont(new Font("SansSerif", Font.BOLD, 20));
    	jla3.setFont(new Font("SansSerif", Font.BOLD, 20));
     
    	Ab.add(jla1);
    	Ab.add(mtotal = new JTextField(18));
    	Ab.add(jla2);
    	Ab.add(cash = new JTextField(15));
    	Ab.add(jla3);
    	Ab.add(card = new JTextField(15));
    	calendar.setPreferredSize(new Dimension(350,768));
 /****************************calendar end****************************/    
 /****************************receipt*********************************/    
    	receipt = new JPanel();
    	receipt.setBorder(BorderFactory.createTitledBorder("Receipts"));
    	model = new SalesModel();
    	table1 = new JTable(model);
    	celAlignCenter = new DefaultTableCellRenderer();
		celAlignCenter.setHorizontalAlignment(JLabel.CENTER);
		table1.setShowHorizontalLines(false);
		table1.getColumnModel().getColumn(0).setPreferredWidth(50);
		table1.getColumnModel().getColumn(0).setCellRenderer(celAlignCenter);
		table1.getColumnModel().getColumn(1).setPreferredWidth(139);
		table1.getColumnModel().getColumn(1).setCellRenderer(celAlignCenter);
		table1.getColumnModel().getColumn(2).setPreferredWidth(139);
		table1.getColumnModel().getColumn(2).setCellRenderer(celAlignCenter);
		table1.getColumnModel().getColumn(3).setPreferredWidth(100);
		table1.getColumnModel().getColumn(3).setCellRenderer(celAlignCenter);
		table1.getColumnModel().getColumn(4).setPreferredWidth(80);
		table1.getColumnModel().getColumn(4).setCellRenderer(celAlignCenter);
		table1.addMouseListener(this);
		jscroll = new JScrollPane(table1);
		jscroll.setPreferredSize(new Dimension(480,700));
		receipt.add(jscroll);
/****************************receipt end****************************/
/*********************************detail****************************/
		detail = new JPanel();
		detail.setBorder(BorderFactory.createTitledBorder("Receipt Detail"));
		detail.setLayout(new BorderLayout()); 
		order = new OrderModel();
		table2 = new JTable(order);
		table2.setRowHeight(40);
		table2.setShowHorizontalLines(false);
		table2.getColumnModel().getColumn(0).setPreferredWidth(350);
		table2.getColumnModel().getColumn(0).setCellRenderer(celAlignCenter);
		table2.getColumnModel().getColumn(1).setPreferredWidth(125);
		table2.getColumnModel().getColumn(1).setCellRenderer(celAlignCenter);
		table2.getColumnModel().getColumn(2).setPreferredWidth(83);
		table2.getColumnModel().getColumn(2).setCellRenderer(celAlignCenter);
		table2.getColumnModel().getColumn(3).setPreferredWidth(125);
		table2.getColumnModel().getColumn(3).setCellRenderer(celAlignCenter);
		scrollPane = new JScrollPane(table2);
		scrollPane.setPreferredSize(new Dimension(480,400));
		detail.add(scrollPane,BorderLayout.NORTH);
		JLabel jla4=new JLabel("Total : ");
		jla4.setFont(new Font("SansSerif", Font.BOLD, 25));
		JPanel temp = new JPanel();
		temp.setLayout(new FlowLayout());
		temp.add(jla4);
		temp.add(total1 = new JTextField(15));
		detail.add(temp, BorderLayout.CENTER);
     
		add(calendar,BorderLayout.WEST);
		add(receipt ,BorderLayout.CENTER);
		add(detail, BorderLayout.EAST);
		
		setSize(1366,768);
		setVisible(true);
		setLocationRelativeTo(null);
		 
		btnsearch.addActionListener(this); 
		bt1w.addActionListener(this);
		bt1m.addActionListener(this);
		bt3m.addActionListener(this);
		bt6m.addActionListener(this);
		bt1y.addActionListener(this);
		date_Chooser.addMouseListener(this);
		
    }
    @Override
  	public void mouseClicked(MouseEvent arg0) {
  		// TODO Auto-generated method stub
    	if(arg0.getSource()==date_Chooser){
    		String strdate = date_Chooser.getSelectedDate();
    		String date[] =strdate.split("-");
    		y1.setText(date[2]);
    		y2.setText(date[2]);
    		m1.setText(date[1]);
    		m2.setText(date[1]);
    		d1.setText(date[0]);
    		d2.setText(date[0]);	
    	}    
    	if(arg0.getSource()==table1) {
    		String[] s = new String[model.getColumnCount()];
    		int row = table1.getSelectedRow();
    		for(int i = 0; i < model.getColumnCount(); i++) {
    			s[i] = (String)model.getValueAt(row, i);
    		}
    		int sum = 0;
    		try {
    			PosApiClient.BillDetail detail =
    					PosApiClient.getDefault().getBillDetail(Long.parseLong(s[0]), s[1], s[2]);
    			order.clearTable();
    			for (PosApiClient.BillLine item : detail.items) {
    				int v = Integer.parseInt(item.volume);
    				int t = Integer.parseInt(item.total);
    				String price = String.valueOf(v == 0 ? 0 : (t / v));
    				sum += t;
    				order.add(item.name, price, item.volume, item.total);
    			}
    			total1.setText(String.valueOf(sum));
    		} catch (IOException ex) {
    			JOptionPane.showMessageDialog(this, "Failed to load receipt details.\n" + ex.getMessage());
    		}
    		this.repaint();
			this.revalidate();
    	}
  	}
    
    @Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
    	Calendar cal = Calendar.getInstance ( ); 
    	
		if(e.getSource()==btnsearch){
			select();
		}if(e.getSource()==bt1w){//1??
			cal.add ( cal.DATE, -7 ); 
			String syear = Integer.toString(cal.get(cal.YEAR));
	    	String smonth = null;
	    	String sdate = Integer.toString(cal.get(cal.DATE));
	    	int imonth = cal.get(cal.MONTH)+1;
	    	if(imonth < 10) {
	    		smonth = "0"+Integer.toString(imonth);
	    	} else {
	    		smonth = Integer.toString(imonth);
	    	}
			y1.setText(syear);
    		m1.setText(smonth);
    		d1.setText(sdate);
		}if(e.getSource()==bt1m){
			cal.add ( cal.MONTH, -1 ); 
			String syear = Integer.toString(cal.get(cal.YEAR));
	    	String smonth = null;
	    	String sdate = Integer.toString(cal.get(cal.DATE));
	    	int imonth = cal.get(cal.MONTH)+1;
	    	if(imonth < 10) {
	    		smonth = "0"+Integer.toString(imonth);
	    	} else {
	    		smonth = Integer.toString(imonth);
	    	}
	    	y1.setText(syear);
    		m1.setText(smonth);
    		d1.setText(sdate);
		}if(e.getSource()==bt3m){
			cal.add ( cal.MONTH, -3 ); 
			String syear = Integer.toString(cal.get(cal.YEAR));
	    	String smonth = null;
	    	String sdate = Integer.toString(cal.get(cal.DATE));
	    	int imonth = cal.get(cal.MONTH)+1;
	    	if(imonth < 10) {
	    		smonth = "0"+Integer.toString(imonth);
	    	} else {
	    		smonth = Integer.toString(imonth);
	    	}
	    	y1.setText(syear);
    		m1.setText(smonth);
    		d1.setText(sdate);
		}if(e.getSource()==bt6m){
			cal.add ( cal.MONTH, -6 ); 
			String syear = Integer.toString(cal.get(cal.YEAR));
	    	String smonth = null;
	    	String sdate = Integer.toString(cal.get(cal.DATE));
	    	int imonth = cal.get(cal.MONTH)+1;
	    	if(imonth < 10) {
	    		smonth = "0"+Integer.toString(imonth);
	    	} else {
	    		smonth = Integer.toString(imonth);
	    	}
	    	y1.setText(syear);
    		m1.setText(smonth);
    		d1.setText(sdate);
		}if(e.getSource()==bt1y){
			cal.add ( cal.MONTH, -12 ); 
			String syear = Integer.toString(cal.get(cal.YEAR));
	    	String smonth = null;
	    	String sdate = Integer.toString(cal.get(cal.DATE));
	    	int imonth = cal.get(cal.MONTH)+1;
	    	if(imonth < 10) {
	    		smonth = "0"+Integer.toString(imonth);
	    	} else {
	    		smonth = Integer.toString(imonth);
	    	}
			y1.setText(syear);
    		m1.setText(smonth);
    		d1.setText(sdate);
		}
	}
   
	
    private void select() {

    	int cashs = 0;
    	int credit = 0;
    	int sum = 0;
    	try {
    		String from = y1.getText()+"-"+m1.getText()+"-"+d1.getText();
    		String to = y2.getText()+"-"+m2.getText()+"-"+d2.getText();
    		java.util.List<PosApiClient.BillHeader> bills = PosApiClient.getDefault().listBills(from, to);
			model.clearTable();
			for (PosApiClient.BillHeader b : bills) {
				String pay = "CASH".equals(b.payMethod) ? "Cash" : "Card";
				model.add(b.number, b.date, b.time, b.total, pay);
				if("CASH".equals(b.payMethod)) {
					cashs += Integer.parseInt(b.total);
				}else {
					credit += Integer.parseInt(b.total);
				}
				sum += Integer.parseInt(b.total);
				mtotal.setText(String.valueOf(sum));
				cash.setText(String.valueOf(cashs));
				card.setText(String.valueOf(credit));
			}
    	} catch (IOException e) {
    		JOptionPane.showMessageDialog(this, "Failed to load sales list.\n" + e.getMessage());
    	}

    }
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	} 
}

