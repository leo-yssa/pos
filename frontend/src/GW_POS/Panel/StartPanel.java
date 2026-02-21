package GW_POS.Panel;


import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import GW_POS.Interfaces.Colleague;
import GW_POS.Interfaces.Mediator;

public class StartPanel extends JPanel implements Colleague, MouseListener {
	private Mediator mediator;
	private ImageIcon front = icon("/GW_POS/Images/front.png");
	private JLabel start;
	private JProgressBar progressBar;
	public StartPanel() {
		this.setLayout(null);
		this.setSize(350,284);
		start = new JLabel(front);
		start.setBounds(0, 0, 350, 264);
		start.addMouseListener(this);
		this.add(start);
		
		progressBar = new JProgressBar();
		progressBar.setBounds(0, 264, 350, 20);
		progressBar.setBorderPainted(true);
		progressBar.setForeground(Color.BLUE);
		progressBar.setStringPainted(isVisible());
		progressBar.setMinimum(UNDEFINED_CONDITION);
		progressBar.setMaximum(WHEN_IN_FOCUSED_WINDOW);
		this.add(progressBar);
		
		this.setVisible(true);
	}
	public void setMediator(Mediator mediator) {
		this.mediator = mediator;
	}
	public void setColleagueEnabled(boolean enabled) {
		this.setVisible(enabled);
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		mediator.colleagueChanged();
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	private ImageIcon icon(String classpathResource) {
		java.net.URL url = getClass().getResource(classpathResource);
		if (url != null) {
			return new ImageIcon(url);
		}
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		return new ImageIcon(img);
	}
}