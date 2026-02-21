package GW_POS.Frame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import GW_POS.Api.PosApiClient;

public class ChartFrame extends JFrame implements ActionListener{
	private JPanel A, B;
	private JToggleButton bt1,bt2,bt3,bt4,bt5; // period buttons
	private ButtonGroup btg1,btg2,btg3,btgT;
	private JToggleButton btT1,btT2,btT3,btT4; // time buttons
	private JButton btnsale1, btnsale2;
	private JToggleButton Bt1w,Bt1m,Bt3m,Bt6m,Bt1y;
	private JToggleButton btTime,btMonth;
	private int y1,m1,d1;
	private Calendar cal = Calendar.getInstance(); 
	private CategoryDataset dataset;
    private JFreeChart chart;
    private ChartPanel chartPanel;
    private ArrayList<String> pname;
    private Hashtable<String, Integer> pvolume;
	public ChartFrame() {
		 super("ChartManagement");
		 JPanel Aa = new JPanel();
	     JPanel Ab = new JPanel();
	     JPanel aa = new JPanel();
	     JPanel ab = new JPanel();
	     JPanel ba = new JPanel();
	     JPanel bb = new JPanel();
	     A = new JPanel();
	     B = new JPanel();
	     A.setLayout(new GridLayout(2,1));
	     A.setPreferredSize(new Dimension(400,100));
	     A.add(Aa);
	     A.add(Ab);
	     
	     /**Aa***************/
	     Aa.setLayout(new FlowLayout(0,8,30));
	     Aa.setBorder(new LineBorder(new Color(242,150,97)));
	     btg1 =new ButtonGroup();
	   
	     bt1= new JToggleButton("1W") ;
	     bt2= new JToggleButton("1M");
	     bt3= new JToggleButton("3M");
	     bt4= new JToggleButton("6M");
	     bt5= new JToggleButton("1Y");
	     
	     btg1.add(bt1);
	     btg1.add(bt2);
	     btg1.add(bt3);
	     btg1.add(bt4);
	     btg1.add(bt5);
	     
/*
	     bt1.setBackground(new Color(255,247,212));
	     bt2.setBackground(new Color(255,247,212));
	     bt3.setBackground(new Color(255,247,212));
	     bt4.setBackground(new Color(255,247,212));
	     bt5.setBackground(new Color(255,247,212));
	     */
	    	     
	     btT1= new JToggleButton("5-8");
	     btT2= new JToggleButton("8-11");
	     btT3= new JToggleButton("11-14");
	     btT4= new JToggleButton("14-17");
	     btgT = new ButtonGroup();
	     btgT.add(btT1);
	     btgT.add(btT2);
	     btgT.add(btT3);
	     btgT.add(btT4);
	 
	     
	     btnsale1 = new JButton("Top Products");
	     btnsale1.setPreferredSize(new Dimension(380,80));
	    
	     aa.setBorder(new TitledBorder(new LineBorder(Color.orange),"Period"));
	     aa.setPreferredSize(new Dimension(380,70));
	     aa.add(bt1);
	     aa.add(bt2);
	     aa.add(bt3);
	     aa.add(bt4);
	     aa.add(bt5);
	     
	     ab.setBorder(new TitledBorder(new LineBorder(Color.orange),"Time"));
	     ab.setPreferredSize(new Dimension(380,70));
	     ab.add(btT1);
	     ab.add(btT2);
	     ab.add(btT3);
	     ab.add(btT4);
	
	    
	     Aa.add(aa);
	     Aa.add(ab);
	  
	     Aa.add(btnsale1);
	   /**Ab***************/
	     Ab.setBorder(new LineBorder(new Color(242,150,97)));
	     Ab.setLayout(new FlowLayout(0,8,25));
	     
	     btg2 =new ButtonGroup();
	     Bt1w= new JToggleButton("1W");
	     Bt1m= new JToggleButton("1M");
	     Bt3m= new JToggleButton("3M");
	     Bt6m= new JToggleButton("6M");
	     Bt1y= new JToggleButton("1Y");
	     
	     btg2.add(Bt1w);
	     btg2.add(Bt1m);
	     btg2.add(Bt3m);
	     btg2.add(Bt6m);
	     btg2.add(Bt1y);
	     
	     btg3 = new ButtonGroup();
	     btTime=new JToggleButton("By Time");
	     btMonth=new JToggleButton("By Month");
	     
	     btg3.add(btTime);
	     btg3.add(btMonth);
	     
	     btnsale2 = new JButton("Revenue");
	     btnsale2.setPreferredSize(new Dimension(380,80));
	     
	     ba.setBorder(new TitledBorder(new LineBorder(Color.orange),"Period"));
	     ba.setPreferredSize(new Dimension(380,70));
	     ba.add(Bt1w);
	     ba.add(Bt1m);
	     ba.add(Bt3m);
	     ba.add(Bt6m);
	     ba.add(Bt1y);
	     
	     bb.setBorder(new TitledBorder(new LineBorder(Color.orange),"Unit"));
	     bb.setPreferredSize(new Dimension(380,70));
	     bb.add(btTime);
	     bb.add(btMonth);

	     Ab.add(ba);
	     Ab.add(bb);
	     Ab.add(btnsale2);
	  
	    bt1.addActionListener(this);
	    bt2.addActionListener(this);
	    bt3.addActionListener(this);
	    bt4.addActionListener(this);
	    bt5.addActionListener(this);
		btT1.addActionListener(this);
		btT2.addActionListener(this);
		btT3.addActionListener(this);
		btT4.addActionListener(this);
		Bt1w.addActionListener(this);
		Bt1m.addActionListener(this);
		Bt3m.addActionListener(this);
		Bt6m.addActionListener(this);
		Bt1y.addActionListener(this);
		btTime.addActionListener(this);
		btMonth.addActionListener(this);
		
		btnsale1.addActionListener(this);
		btnsale2. addActionListener(this);
	     
		B.setBorder(new LineBorder(new Color(242,150,97)));
		B.setVisible(false);
	    add(A,BorderLayout.WEST);
		add(B,BorderLayout.CENTER);
		
		pname = new ArrayList<String>();
		pvolume = new Hashtable<String, Integer>();
		  
	    setSize(1366,768);
		setVisible(true);
		setLocationRelativeTo(null);
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		Calendar work = Calendar.getInstance();
		Date today = new Date();
		SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd");
		String date = format.format(today);
		String syear = null;
		String smonth = null;
		String sdate = null;
		String odate = null;
		if(arg0.getSource()==btnsale1){
			 
			  if(bt1.isSelected()) {
				  work.add ( Calendar.DATE, -7 ); 
				  syear = Integer.toString(work.get(Calendar.YEAR));
				  smonth = null;
				  sdate = Integer.toString(work.get(Calendar.DATE));
				  int imonth = work.get(Calendar.MONTH)+1;
				  if(imonth < 10) {
					  smonth = "0"+Integer.toString(imonth);
				  } else {
					  smonth = Integer.toString(imonth);
				  }
				  odate = syear + "-" + smonth + "-" + sdate;
				  
		  
			  }else if(bt2.isSelected()) {
				  work.add ( Calendar.MONTH, -1 ); 
				  syear = Integer.toString(work.get(Calendar.YEAR));
				  smonth = null;
				  sdate = Integer.toString(work.get(Calendar.DATE));
				  int imonth = work.get(Calendar.MONTH)+1;
				  if(imonth < 10) {
					  smonth = "0"+Integer.toString(imonth);
				  } else {
					  smonth = Integer.toString(imonth);
				  }
				  odate = syear + "-" + smonth + "-" + sdate;

			  }else if(bt3.isSelected()) {
				  work.add ( Calendar.MONTH, -3 ); 
				  syear = Integer.toString(work.get(Calendar.YEAR));
				  smonth = null;
				  sdate = Integer.toString(work.get(Calendar.DATE));
				  int imonth = work.get(Calendar.MONTH)+1;
				  if(imonth < 10) {
					  smonth = "0"+Integer.toString(imonth);
				  } else {
					  smonth = Integer.toString(imonth);
				  }
				  odate = syear + "-" + smonth + "-" + sdate;

			  }else if(bt4.isSelected()) {
				  work.add ( Calendar.MONTH, -6 ); 
				  syear = Integer.toString(work.get(Calendar.YEAR));
				  smonth = null;
				  sdate = Integer.toString(work.get(Calendar.DATE));
				  int imonth = work.get(Calendar.MONTH)+1;
				  if(imonth < 10) {
					  smonth = "0"+Integer.toString(imonth);
				  } else {
					  smonth = Integer.toString(imonth);
				  }
				  odate = syear + "-" + smonth + "-" + sdate;  

			  }else if(bt5.isSelected()) {
				  work.add ( Calendar.MONTH, -12 ); 
				  syear = Integer.toString(work.get(Calendar.YEAR));
				  smonth = null;
				  sdate = Integer.toString(work.get(Calendar.DATE));
				  int imonth = work.get(Calendar.MONTH)+1;
				  if(imonth < 10) {
					  smonth = "0"+Integer.toString(imonth);
				  } else {
					  smonth = Integer.toString(imonth);
				  }
				  odate = syear + "-" + smonth + "-" + sdate;
			  }
			  if (odate == null) {
				  JOptionPane.showMessageDialog(this, "Select period.");
				  return;
			  }
			  Integer slot = null;
			  if (btT1.isSelected()) slot = 1;
			  else if (btT2.isSelected()) slot = 2;
			  else if (btT3.isSelected()) slot = 3;
			  else if (btT4.isSelected()) slot = 4;
			  try {
				  java.util.List<PosApiClient.TopProduct> top =
						  PosApiClient.getDefault().topProducts(odate, date, 5, slot);
				  pname.clear();
				  pvolume.clear();
				  for (PosApiClient.TopProduct tp : top) {
					  pname.add(tp.name);
					  pvolume.put(tp.name, tp.volume);
				  }
			  } catch (IOException ex) {
				  JOptionPane.showMessageDialog(this, "Failed to load chart data.\n" + ex.getMessage());
			  }
			  drawChart(arg0.getActionCommand());
			  this.validate();
			  this.repaint();
		}else if(arg0.getSource()==btnsale2){
			if(Bt1w.isSelected()) {
				work.add ( Calendar.DATE, -7 ); 
				syear = Integer.toString(work.get(Calendar.YEAR));
				smonth = null;
				sdate = Integer.toString(work.get(Calendar.DATE));
				int imonth = work.get(Calendar.MONTH)+1;
				if(imonth < 10) {
					smonth = "0"+Integer.toString(imonth);
				} else {
					smonth = Integer.toString(imonth);
				}
				odate = syear + "-" + smonth + "-" + sdate;
			} else if(Bt1m.isSelected()) {
				work.add ( Calendar.MONTH, -1 ); 
				syear = Integer.toString(work.get(Calendar.YEAR));
				smonth = null;
				sdate = Integer.toString(work.get(Calendar.DATE));
				int imonth = work.get(Calendar.MONTH)+1;
				if(imonth < 10) {
					smonth = "0"+Integer.toString(imonth);
				} else {
					smonth = Integer.toString(imonth);
				}
				odate = syear + "-" + smonth + "-" + sdate;
			} else if(Bt3m.isSelected()) {
				work.add ( Calendar.MONTH, -3 ); 
				syear = Integer.toString(work.get(Calendar.YEAR));
				smonth = null;
				sdate = Integer.toString(work.get(Calendar.DATE));
				int imonth = work.get(Calendar.MONTH)+1;
				if(imonth < 10) {
					smonth = "0"+Integer.toString(imonth);
				} else {
					smonth = Integer.toString(imonth);
				}
				odate = syear + "-" + smonth + "-" + sdate;

			} else if(Bt6m.isSelected()) {
				work.add ( Calendar.MONTH, -6 ); 
				syear = Integer.toString(work.get(Calendar.YEAR));
				smonth = null;
				sdate = Integer.toString(work.get(Calendar.DATE));
				int imonth = work.get(Calendar.MONTH)+1;
				if(imonth < 10) {
					smonth = "0"+Integer.toString(imonth);
				} else {
					smonth = Integer.toString(imonth);
				}
				odate = syear + "-" + smonth + "-" + sdate;  
			} else if(Bt1y.isSelected()) {
				work.add ( Calendar.MONTH, -12 ); 
				syear = Integer.toString(work.get(Calendar.YEAR));
				smonth = null;
				sdate = Integer.toString(work.get(Calendar.DATE));
				int imonth = work.get(Calendar.MONTH)+1;
				if(imonth < 10) {
					smonth = "0"+Integer.toString(imonth);
				} else {
					smonth = Integer.toString(imonth);
				}
				odate = syear + "-" + smonth + "-" + sdate;
			}
			if (odate == null) {
				JOptionPane.showMessageDialog(this, "Select period.");
				return;
			}
			String mode = (btTime != null && btTime.isSelected()) ? "TIME" : "MONTH";
			try {
				java.util.List<PosApiClient.RevenuePoint> rows =
						PosApiClient.getDefault().revenue(odate, date, mode);
				pname.clear();
				pvolume.clear();
				for (PosApiClient.RevenuePoint r : rows) {
					pname.add(r.label);
					pvolume.put(r.label, r.total);
				}
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this, "Failed to load revenue data.\n" + ex.getMessage());
			}
			drawChart("Revenue");
			this.validate();
			this.repaint();
		}
		
	}
	private void drawChart(String or) {
		
	    dataset = createDataset();
	    chart = createChart(dataset, or);
	    chartPanel = new ChartPanel(chart); 
        chartPanel.setPreferredSize(new Dimension(950, 700));
        B.removeAll();
        B.add(chartPanel);
        B.setVisible(true);
	}
	  private CategoryDataset createDataset() {
	        
		  
	        // row keys...
	        final String series1 = "First";
	        final int size = pname.size();
	        // create the dataset...
	        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	        
	        for(int i = 0; i < size; i++) {
	        	String index = pname.get(i);
	        	dataset.addValue(pvolume.get(index), series1, index);
	        }
	        return dataset;
	        
	    }
	    
	    /**
	     * Creates a sample chart.
	     * 
	     * @param dataset  the dataset.
	     * 
	     * @return The chart.
	     */
	    private JFreeChart createChart(final CategoryDataset dataset, String or) {
	        
	        // create the chart...
	        final JFreeChart chart = ChartFactory.createBarChart(
	            or,       
	            "Category",               // domain axis label
	            "Value",                  // range axis label
	            dataset,                  // data
	            PlotOrientation.VERTICAL, // orientation
	            false,                    // include legend
	            true,                     // tooltips?
	            false                     // URLs?
	        );

	        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

	        // set the background color for the chart...
	        chart.setBackgroundPaint(Color.white);
	        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 20));
	       
	        
	        // get a reference to the plot for further customisation...
	        final CategoryPlot plot = chart.getCategoryPlot();
	        plot.setBackgroundPaint(Color.white);
	        plot.setDomainGridlinePaint(Color.lightGray);
	        plot.setRangeGridlinePaint(Color.lightGray);

	        plot.getDomainAxis().setLabelFont(new Font("SansSerif", Font.BOLD, 13));
	     // X axis label font
	        plot.getDomainAxis().setTickLabelFont(new Font("SansSerif", Font.BOLD, 15));
	     // Y axis label font
	        plot.getRangeAxis().setLabelFont(new Font("SansSerif", Font.BOLD, 13));
	     // Y axis tick label font
	        plot.getRangeAxis().setTickLabelFont(new Font("SansSerif", Font.BOLD, 15));

      
	        // set the range axis to display integers only...
	        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
	        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	        rangeAxis.setUpperMargin(0.15);
	        
	        // disable bar outlines...
	        final CategoryItemRenderer renderer = plot.getRenderer();
	        renderer.setSeriesItemLabelsVisible(0, Boolean.TRUE);
	        
	        final CategoryAxis domainAxis = plot.getDomainAxis();
	        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
	        

	        // OPTIONAL CUSTOMISATION COMPLETED.
	        
	        return chart;
	    }
}
