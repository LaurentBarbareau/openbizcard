package com.yov.scanner.imageprocessing;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.*;

public class TestUI2 implements ActionListener{

	JFrame testWindow;
	
	CardScanner bcScanner;
	
	ImagePanel imagePane;
	JButton scanBtn, selectBtn, showBtn, rotateBtn, grayScaleBtn, brightBtn, 
		darkBtn, trimBtn, undoBtn, readBtn;
	JTextField scannerTxt;
	
	int	index = 0;

	String filename;
	String scannerName;
	
	BufferedImage scannedImage;
	BusinessCard scannedBCard;
	
	//RonCemerOCR testOCR;
	
	
	public TestUI2(){
		
		filename = System.getProperty("user.home")+"\\My Documents\\test";
		
		bcScanner = new CardScanner(filename + index + ".jpg");
		
	}
	
	public void init(){
		
		testWindow = new JFrame("TEST GUI for CardScanner Project");
		
		scannedImage = null;
		scannedBCard = null;
		
		//testWindow.setSize(400, 430);
		testWindow.setSize(500, 500);
		testWindow.setLocation(100, 100);
		testWindow.setResizable(false);
		testWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Container contentPane = testWindow.getContentPane();
		
		JPanel testPane = new JPanel();
		JPanel scannerPane = new JPanel();
		JPanel nullNorthPane = new JPanel();
		JPanel nullSouthPane = new JPanel();
		imagePane = new ImagePanel("");
		JPanel mainPane = new JPanel();
		
		JPanel scannerUpperPane = new JPanel();
		JPanel scannerLowerPane = new JPanel();
		JPanel buttonPane = new JPanel();
		JLabel scannerLab = new JLabel("ScannerName");
		
		scannerTxt = new JTextField("Please Select Scanner");
		selectBtn = new JButton("Select");
		scanBtn = new JButton("Scan");
		showBtn = new JButton("Show Image");
		rotateBtn = new JButton("Rotate 90");
		grayScaleBtn = new JButton("Gray Scale");
		brightBtn = new JButton("Brighter");
		darkBtn = new JButton("Darker");
		trimBtn = new JButton("Auto-Crop");
		undoBtn = new JButton("Undo Changes");
		readBtn = new JButton("Read Data");
		
		scannerTxt.setEditable(false);
		
		selectBtn.addActionListener(this);
		scanBtn.addActionListener(this);
		showBtn.addActionListener(this);
		rotateBtn.addActionListener(this);
		grayScaleBtn.addActionListener(this);
		brightBtn.addActionListener(this);
		darkBtn.addActionListener(this);
		trimBtn.addActionListener(this);
		undoBtn.addActionListener(this);
		readBtn.addActionListener(this);
		
		scannerUpperPane.setBackground(Color.WHITE);
		scannerUpperPane.setLayout(new BorderLayout());
		scannerUpperPane.add(scannerLab, BorderLayout.CENTER);
		
		scannerLowerPane.setBackground(Color.GREEN);
		scannerLowerPane.setLayout(new BorderLayout());
		scannerLowerPane.add(scannerTxt, BorderLayout.CENTER);
		scannerLowerPane.add(selectBtn, BorderLayout.EAST);
		
		buttonPane.setBackground(Color.LIGHT_GRAY);
		buttonPane.setLayout(new BorderLayout());
		JPanel upperButtonPane = new JPanel();
		JPanel lowerButtonPane = new JPanel();
		upperButtonPane.add(scanBtn);
		upperButtonPane.add(showBtn);
		upperButtonPane.add(rotateBtn);
		upperButtonPane.add(grayScaleBtn);
		upperButtonPane.add(brightBtn);
		lowerButtonPane.add(darkBtn);
		lowerButtonPane.add(trimBtn);
		lowerButtonPane.add(undoBtn);
		lowerButtonPane.add(readBtn);
		buttonPane.add(upperButtonPane, BorderLayout.NORTH);
		buttonPane.add(lowerButtonPane, BorderLayout.SOUTH);
		
		scannerPane.setBackground(Color.YELLOW);
		scannerPane.setLayout(new BorderLayout());
		scannerPane.add(scannerUpperPane, BorderLayout.NORTH);
		scannerPane.add(scannerLowerPane, BorderLayout.CENTER);
		scannerPane.add(buttonPane, BorderLayout.SOUTH);
		
		imagePane.setBackground(Color.BLACK);
		imagePane.setSize(400, 300);
		
		mainPane.setBackground(Color.BLACK);
		mainPane.setLayout(new BorderLayout());
		mainPane.add(scannerPane, BorderLayout.NORTH);
		mainPane.add(imagePane, BorderLayout.CENTER);
		
		nullNorthPane.setBackground(Color.RED);
		nullSouthPane.setBackground(Color.GREEN);
		testPane.setBackground(Color.BLUE);
		testPane.setLayout(new BorderLayout());
		//testPane.add(scannerPane, BorderLayout.NORTH);
		testPane.add(nullNorthPane, BorderLayout.NORTH);
		testPane.add(mainPane, BorderLayout.CENTER);
		testPane.add(nullSouthPane, BorderLayout.SOUTH);
		
		contentPane.add(testPane);
		
		testWindow.setVisible(true);
		
		// Initialize OCR object
		//testOCR = new RonCemerOCR(imagePane, "");
		
	}
	
	@Override
	public synchronized void actionPerformed(ActionEvent event) {
		if(event.getSource() == scanBtn){
			//System.out.println(" -+-+- Start Scanning -+-+- ");

			//cardImage = bcScanner.scan();
			scannedImage = bcScanner.scan();
			
			if(scannedImage != null){
				System.out.println(" -+-+- Get the Scanning Image -+-+-");
				
				scannedBCard = new BusinessCard(scannedImage);
				
				imagePane.setImage(scannedBCard.getPreviewImage().getImageData());
				imagePane.repaint();
				
				testWindow.setVisible(true);
			}
			System.out.println("Scanning is done");
			
		}else if(event.getSource() == selectBtn){
			scannerName = bcScanner.selectScanner();
			scannerTxt.setText(scannerName);
			
		}else if(event.getSource() == showBtn){
			System.out.println("Show Image");
			
			try {
				scannedImage = ImageIO.read(new File(filename + index + ".jpg"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			scannedBCard = new BusinessCard(scannedImage);
			
			imagePane.setImage(scannedBCard.getPreviewImage().getImageData());
			imagePane.repaint();
			
			testWindow.setVisible(true);
			
		}else if(event.getSource() == rotateBtn){
			System.out.println("Rotate 90 degrees");
			
			//scannedImage.rotate90();
			scannedBCard.rotate90();
			
			
			imagePane.setImage(scannedBCard.getPreviewImage().getImageData());
			imagePane.repaint();
			
			//cardImage = rotatedImage;
			
			testWindow.setVisible(true);
			
		}else if(event.getSource() == grayScaleBtn){
			
			scannedBCard.turnToBlackAndWhite();
			
			imagePane.setImage(scannedBCard.getPreviewImage().getImageData());
			imagePane.repaint();
			
			testWindow.setVisible(true);
			
		}else if(event.getSource() == brightBtn){
			
			scannedBCard.changeBrightness(scannedBCard.getPreviewImage().getBrightness() + 1);
			
			imagePane.setImage(scannedBCard.getPreviewImage().getImageData());
			imagePane.repaint();
			
			testWindow.setVisible(true);
				
		}else if(event.getSource() == trimBtn){
			
			System.out.println("Auto-Crop!!!");
			
			scannedBCard.trim();
			
			imagePane.setImage(scannedBCard.getPreviewImage().getImageData());
			//imagePane.setImage(scannedImage);
			
			imagePane.repaint();
			
			testWindow.setVisible(true);
				
		}else if(event.getSource() == darkBtn){
			
			scannedBCard.changeBrightness(scannedBCard.getPreviewImage().getBrightness() - 1);
			
			imagePane.setImage(scannedBCard.getPreviewImage().getImageData());
			imagePane.repaint();
			
			testWindow.setVisible(true);
			
			
		}else if(event.getSource() == undoBtn){
			scannedBCard.undoChanges();
			
			imagePane.setImage(scannedBCard.getPrimaryImage().getImageData());
			imagePane.repaint();
			
			testWindow.setVisible(true);
		}else if(event.getSource() == readBtn){
			System.out.println("RUN OCR");
			
			scannedBCard.setImageFileName(filename + index + ".jpg");
			//scannedBCard.initRonCemerOCR(imagePane);
			
			//scannedBCard.retrieveData("");
		}
	}

	
	
	public static void main(String[] args){
		System.out.println("TestUI");
		
		//for(int i = 0; i < 100; i++)
		//	System.out.println("Char(" + i + ") = " + ((char)i));
	
		TestUI2 scannerTest = new TestUI2();
		scannerTest.init();
	}
}
