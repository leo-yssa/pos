package GW_POS.Frame;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.*;

import GW_POS.Api.PosApiClient;

// User management
public class MembershipFrame extends JFrame{
	private JLabel getID = new JLabel("ID");
	private JLabel getPW = new JLabel("PASSWORD");
	private JLabel checkPW = new JLabel("PASSWORD");
	private JLabel getID2 = new JLabel("ID");
	private JLabel getPW2 = new JLabel("PASSWORD");
	private JLabel checkPW2 = new JLabel("PASSWORD");
	private JTextField getIDTF = new JTextField(12);
	private JPasswordField getPWTF = new JPasswordField(12);
	private JPasswordField checkPWTF = new JPasswordField(12);
	private JTextField getIDTF2 = new JTextField(12);
	private JPasswordField getPWTF2 = new JPasswordField(12);
	private JPasswordField checkPWTF2 = new JPasswordField(12);
	private JButton createAccount = new JButton("Create");
	private JButton deleteAccount = new JButton("Delete");
	private JTabbedPane fr4aTap=new JTabbedPane();
	private JPanel pa1 = new JPanel();
	private JPanel pa2 = new JPanel();
	private JPanel pa3 = new JPanel();
	private JPanel pa4 = new JPanel();
	JDialog dia=new JDialog(this,"Message",false);
	JLabel diaLa=new JLabel("Ready.");
	JButton diaBtn=new JButton("OK");
	
	public MembershipFrame(){
		setSize(300,200);
		pa2.setLayout(new FlowLayout());
		pa4.setLayout(new FlowLayout());
		pa1.setLayout(new GridLayout(3,2,5,5));
		pa3.setLayout(new GridLayout(3,2,5,5));
		pa1.setSize(300,150);
		pa1.add(getID);
		pa1.add(getIDTF);
		pa1.add(getPW);
		pa1.add(getPWTF);
		pa1.add(checkPW);
		pa1.add(checkPWTF);
		pa2.add(pa1);
		pa2.add(createAccount);
		fr4aTap.setSize(300,200);
		fr4aTap.addTab("Create", pa2);
		pa3.add(getID2);
		pa3.add(getIDTF2);
		pa3.add(getPW2);
		pa3.add(getPWTF2);
		pa3.add(checkPW2);
		pa3.add(checkPWTF2);
		pa4.add(pa3);
		pa4.add(deleteAccount);
		fr4aTap.addTab("Delete", pa4);
		add(fr4aTap);
		createAccount.addActionListener(new myListener());
		deleteAccount.addActionListener(new myListener());
		this.setVisible(true);
		dia.setLayout(new FlowLayout());
		dia.setSize(200,120);
		dia.setLocation(470,450);
		dia.add(diaLa);
		dia.add(diaBtn);
		diaBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dia.setVisible(false);
			}
		});
	}
	class myListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			if(e.getSource() == createAccount){
				createAccount(getIDTF.getText(),getPWTF.getText(),checkPWTF.getText());
			}
			if(e.getSource() == deleteAccount){
				deleteAccount(getIDTF2.getText(),getPWTF2.getText(),checkPWTF2.getText());
			}
		}
	}
	public void createAccount(String a, String b, String c){
		if(b == null || !b.equals(c)){
			diaLa.setText("Password does not match.");
			dia.setVisible(true);
			return;
		}
		try {
			PosApiClient.getDefault().createUser(a, b);
			diaLa.setText("User created.");
		} catch (IOException e) {
			diaLa.setText("Failed to create user.");
		}
		dia.setVisible(true);
	}
	// delete account
	public void deleteAccount(String a, String b, String c){
		if(b == null || !b.equals(c)){
			diaLa.setText("Password does not match.");
			dia.setVisible(true);
			return;
		}
		try {
			boolean ok = PosApiClient.getDefault().login(a, b);
			if(!ok){
				diaLa.setText("Invalid password.");
				dia.setVisible(true);
				return;
			}
			PosApiClient.getDefault().deleteUser(a);
			diaLa.setText("User deleted.");
		} catch (IOException e) {
			diaLa.setText("Failed to delete user.");
		}
		dia.setVisible(true);
	}
}