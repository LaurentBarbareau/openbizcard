package com.yov.scanner.imageprocessing;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;


public class TestUI implements ActionListener{

	int	index = 0;

	String filename;
	  
	CardScanner bcScanner;
	
	JButton scanBtn, selectBtn;
	
	String scannerName;
	
	JTextField scannerTxt;
	
	JFrame testWindow;
	
	public TestUI(){
		//init();
		//start();
	}
	
	public void init(){
		
		testWindow = new JFrame("TEST GUI for CardScanner Project");
		
		testWindow.setSize(400, 130);
		testWindow.setLocation(100, 100);
		testWindow.setResizable(false);
		testWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Container contentPane = testWindow.getContentPane();
		
		JPanel testPane = new JPanel();
		JPanel scannerPane = new JPanel();
		JPanel nullNorthPane = new JPanel();
		JPanel nullSouthPane = new JPanel();
		
		JPanel scannerUpperPane = new JPanel();
		JPanel scannerLowerPane = new JPanel();
		JPanel scannerBtnPane = new JPanel();
		JLabel scannerLab = new JLabel("ScannerName");
		
		scannerTxt = new JTextField("Please Select Scanner");
		selectBtn = new JButton("Select");
		scanBtn = new JButton("Scan");
		
		scannerTxt.setEditable(false);
		
		selectBtn.addActionListener(this);
		scanBtn.addActionListener(this);
		
		scannerUpperPane.setBackground(Color.WHITE);
		scannerUpperPane.setLayout(new BorderLayout());
		scannerUpperPane.add(scannerLab, BorderLayout.CENTER);
		
		scannerLowerPane.setBackground(Color.GREEN);
		scannerLowerPane.setLayout(new BorderLayout());
		scannerLowerPane.add(scannerTxt, BorderLayout.CENTER);
		scannerLowerPane.add(selectBtn, BorderLayout.EAST);
		
		scannerBtnPane.setBackground(Color.LIGHT_GRAY);
		scannerBtnPane.add(scanBtn);
		
		scannerPane.setBackground(Color.YELLOW);
		scannerPane.setLayout(new BorderLayout());
		scannerPane.add(scannerUpperPane, BorderLayout.NORTH);
		scannerPane.add(scannerLowerPane, BorderLayout.CENTER);
		scannerPane.add(scannerBtnPane, BorderLayout.SOUTH);
		
		nullNorthPane.setBackground(Color.RED);
		nullSouthPane.setBackground(Color.GREEN);
		testPane.setBackground(Color.BLUE);
		testPane.setLayout(new BorderLayout());
		//testPane.add(scannerPane, BorderLayout.NORTH);
		testPane.add(nullNorthPane, BorderLayout.NORTH);
		testPane.add(scannerPane, BorderLayout.CENTER);
		testPane.add(nullSouthPane, BorderLayout.SOUTH);
		
		
		filename = System.getProperty("user.home")+"\\My Documents\\test";
		
		bcScanner = new CardScanner(filename);
		//bcScanner.addScannerListener(this);
		
		
		contentPane.add(testPane);
		
		testWindow.setVisible(true);
		
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		//try{
			if(event.getSource() == scanBtn){
//				bcScanner.scan();
			}else if(event.getSource() == selectBtn){
//				scannerName = bcScanner.selectScanner();
				
				scannerTxt.setText(scannerName);
			}
		//}catch(ScannerIOException se){
		//	se.printStackTrace();
		//}
	}
	
	public static void main(String[] args){
		System.out.println("TestUI");
	
		TestUI scannerTest = new TestUI();
		scannerTest.init();
	}
}
