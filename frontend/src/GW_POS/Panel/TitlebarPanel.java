package GW_POS.Panel;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import GW_POS.Frame.ChartFrame;
import GW_POS.Frame.MembershipFrame;
import GW_POS.Frame.SalesFrame;
import GW_POS.Interfaces.Mediator;
import GW_POS.Product.ProductTable;

public class TitlebarPanel extends JPanel implements ActionListener {
	private Mediator mediator;
	private JButton quit;
	private JButton sale;
	private JButton add;
	private JButton graph;
	private JButton member;
	private JLabel title;
	private final boolean showNavButtons;

	public TitlebarPanel(int x, int y) {
		this(x, y, true);
	}

	public TitlebarPanel(int x, int y, boolean showNavButtons) {
		this.showNavButtons = showNavButtons;
		this.setSize(x,y);
		this.setLayout(null);
		this.setBackground(Color.WHITE);
		
		int h = Math.max(y, 56);
		this.setSize(x, h);
		Font btnFont = new Font("SansSerif", Font.BOLD, 12);
		
		ImageIcon logo = iconOrNull("/GW_POS/Images/logo.png");
		title = (logo != null) ? new JLabel(logo) : new JLabel("POS");
		title.setFont(new Font("SansSerif", Font.BOLD, 18));
		title.setBounds(12, 0, 200, h);
		this.add(title);
		
		int w = 72;
		int pad = 8;
		
		quit = createButton("Exit", "/GW_POS/Images/exit.png", btnFont);
		quit.setBounds(x - (w + pad), pad, w, h - (pad * 2));
		quit.addActionListener(this);
		this.add(quit);
		
		if (!showNavButtons) {
			this.setVisible(true);
			return;
		}
		
		sale = createButton("Sales", "/GW_POS/Images/back.png", btnFont);
		sale.setBounds(x - (2 * (w + pad)), pad, w, h - (pad * 2));
		sale.addActionListener(this);
		this.add(sale);
		
		add = createButton("Products", "/GW_POS/Images/add.png", btnFont);
		add.setBounds(x - (3 * (w + pad)), pad, w, h - (pad * 2));
		add.addActionListener(this);
		this.add(add);
		
		graph = createButton("Chart", "/GW_POS/Images/graph.png", btnFont);
		graph.setBounds(x - (4 * (w + pad)), pad, w, h - (pad * 2));
		graph.addActionListener(this);
		this.add(graph);
		
		member = createButton("Users", "/GW_POS/Images/member.png", btnFont);
		member.setBounds(x - (5 * (w + pad)), pad, w, h - (pad * 2));
		member.addActionListener(this);
		this.add(member);
		this.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == quit) {
			int result = JOptionPane.showConfirmDialog(this, "Exit?","",JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
			if( result == 0 ) {
				System.exit(0);
			}
		}
			
		else if(event.getSource() == add){
			ProductTable p = new ProductTable();
		}
		
		else if(event.getSource() == sale) {
			SalesFrame s = new SalesFrame();
		}
		else if(event.getSource() == graph) {
			ChartFrame c = new ChartFrame();
		}
		else if(event.getSource() == member) {
			MembershipFrame mf = new MembershipFrame();
		}
	}

	private JButton createButton(String text, String iconPath, Font font) {
		JButton b = new JButton(text);
		b.setFont(font);
		b.setFocusPainted(false);
		b.setOpaque(true);
		b.setBackground(Color.WHITE);
		ImageIcon icon = iconOrNull(iconPath);
		if (icon != null) {
			b.setText("");
			b.setIcon(icon);
		}
		return b;
	}

	private ImageIcon iconOrNull(String classpathResource) {
		java.net.URL url = getClass().getResource(classpathResource);
		if (url != null) {
			return new ImageIcon(url);
		}
		return null;
	}
}
