package GW_POS.Frame;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import GW_POS.Api.PosApiClient;
import GW_POS.Panel.TitlebarPanel;

public class FrontFrame extends JFrame implements ActionListener {
/***************************FrontFrame Field*************************************/
	private TitlebarPanel title;
	private JPanel login;
	private JLabel id;
	private JLabel password;
	private JComboBox<String> comboBox;
	private JPasswordField passwordf;
	private JButton confirm, cancel;
/***************************FrontFrame Constructor*******************************/
	public FrontFrame() {
		init();
	}
/***************************FrontFrame Methods***********************************/
	public void init() {
		/* Location is the center of window */
		/* Splash Screen */
		super.setUndecorated(true);
		super.setTitle("POS Login");
		
		this.setLayout(null);
		final int W = 720;
		final int H = 420;
		final int TITLE_H = 56;
		this.setSize(W, H);
		super.setLocationRelativeTo(null);
		
		title = new TitlebarPanel(W, TITLE_H, false);
		title.setBounds(0, 0, W, TITLE_H);
		
		login = new JPanel();
		login.setSize(W, H - TITLE_H);
		login.setBounds(0, TITLE_H, W, H - TITLE_H);
		login.setLayout(null);
		login.setBackground(Color.WHITE);
		
		int labelX = 140;
		int fieldX = 290;
		int rowH = 44;
		int y1 = 110;
		int gap = 16;
		int fieldW = 260;
		
		id = new JLabel("ID");
		id.setBounds(labelX, y1, 120, rowH);
		login.add(id);
		comboBox = new JComboBox<String>();
		comboBox.setBounds(fieldX, y1, fieldW, rowH);
		login.add(comboBox);
		password = new JLabel("PASSWORD");
		password.setBounds(labelX, y1 + rowH + gap, 120, rowH);
		login.add(password);
		passwordf = new JPasswordField(20);
		passwordf.setBounds(fieldX, y1 + rowH + gap, fieldW, rowH);
		login.add(passwordf);
		confirm = new JButton("OK");
		confirm.setBounds(fieldX, y1 + (2 * (rowH + gap)) + 10, 120, 44);
		login.add(confirm);
		confirm.addActionListener(this);
		cancel = new JButton("Exit");
		cancel.setBounds(fieldX + 140, y1 + (2 * (rowH + gap)) + 10, 120, 44);
		login.add(cancel);
		cancel.addActionListener(this);
		
		add(title);
		add(login);
		
		this.fillCombobox();
		setVisible(true);
	}
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		if(e.getSource() == cancel){
			int result = JOptionPane.showConfirmDialog(this, "Exit?","",JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
			if( result == 0 ) {
				System.exit(0);
			}
		} else if(e.getSource() == confirm) {
			String id = (String)comboBox.getSelectedItem();
			if(login(id, passwordf.getPassword())){
				POSFrame pf = new POSFrame();
				this.setVisible(false);
			}
		} else {
			
		}
	}
	public void fillCombobox() {
		try {
			List<String> ids = PosApiClient.getDefault().listUserIds();
			for (String id : ids) {
				comboBox.addItem(id);
			}
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(this, "Cannot connect to API server.\n" + ex.getMessage());
		}
	}
	
	public void removeCombobox() {
		comboBox.removeAllItems();
	}
	public boolean login(String id, char[] pw) {
		try {
			String cpw = String.valueOf(pw);
			boolean ok = PosApiClient.getDefault().login(id, cpw);
			if (!ok) {
				JOptionPane.showMessageDialog(this, "Check password.");
			}
			return ok;
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(this, "Cannot connect to API server.\n" + ex.getMessage());
			return false;
		}
	}
}
