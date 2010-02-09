package com.yov.scanner.imageprocessing;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import uk.co.mmscomputing.device.scanner.Scanner;
import uk.co.mmscomputing.device.scanner.ScannerDevice;
import uk.co.mmscomputing.device.scanner.ScannerIOMetadata;
import uk.co.mmscomputing.device.scanner.ScannerListener;
import uk.co.mmscomputing.device.scanner.ScannerIOMetadata.Type;


public class CardScanner implements ScannerListener{

	private String name;
	private Scanner cardScanner;
	
	private String targetFileName;
	private int fileNameIndex;
	
	//private CardImage scanImage;
	private BufferedImage scanImage;
	
	public CardScanner(){
		System.out.println("CardScanner - Contructor");
		
		cardScanner = Scanner.getDevice();
		cardScanner.addListener(this);
	}
	
	public CardScanner(String cardFileName){
		System.out.println("CardScanner(String cardFileName) - Contructor");
		
		targetFileName = cardFileName;
		
		cardScanner = Scanner.getDevice();
		cardScanner.addListener(this);
	}
	
	/*
	public void addScannerListener(ScannerListener scannerListener){
		cardScanner.addListener(scannerListener);
	}
	*/
	
	public synchronized BufferedImage scan(){
		scanImage = null;
		System.out.println("CardScanner.scan()");
		
		try{
			cardScanner.acquire();
			
			wait();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return scanImage;
	}
	
	public synchronized String selectScanner(){
		System.out.println("CardScanner.selectScanner()");
		String scannerName = "";
		int extStrPos = 0;
		
		try{
			cardScanner.select();
			
			wait();
			
			scannerName = cardScanner.getSelectedDeviceName();
		}catch(Exception e){
			e.printStackTrace();
			
			scannerName = "No Device is selected";
		}
		
		// Remove "TWAIN" or "SANE" from scanner's name
		extStrPos = scannerName.lastIndexOf("TWAIN") + scannerName.lastIndexOf("SANE") + 1;
		
		if(extStrPos > -1)
			scannerName = scannerName.substring(0, extStrPos - 1);
		
		return scannerName;
	}
	
	// - Get Method(s) - //
	public String getName(){
            return name;
	}
	
	public String getTargetFileName(){
            return targetFileName;
	}

        public int getFileNameIndex(){
            return fileNameIndex;
        }
	
	// - Set Method(s) - //
	public String setName(String newName){
		String oldName = name;
		name = newName;
		System.out.println("CardScanner.setName(String newName)");
		
		return oldName;
	}
	
	public String setTargetFileName(String newFileName){
		String oldFileName = targetFileName;
		targetFileName = newFileName;
		System.out.println("CardScanner.setTargetFileName(String newFlieName)");
		
		return oldFileName;
	}

	
	
	@Override
	public synchronized void update(Type type, ScannerIOMetadata metadata) {
		
		System.out.println("CardScanner.update()");
		
		if(type.equals(ScannerIOMetadata.ACQUIRED)){
			System.out.println("  --  UPDATE - ACQUIRED");
			
			BufferedImage image = metadata.getImage();
			
			//scanImage.setImageData(image);
			scanImage = image;
			
			System.out.println("Have an image now!");
			
			notify();
			//TestUI2.class.notifyAll();
			
			try{
				ImageIO.write(image, "jpg", new File(targetFileName+fileNameIndex+".jpg"));
				fileNameIndex++;

				//	        new uk.co.mmscomputing.concurrent.Semaphore(0,true).tryAcquire(2000,null);

			}catch(Exception e){
				e.printStackTrace();
				
			}
		}else if(type.equals(ScannerIOMetadata.NEGOTIATE)){
			ScannerDevice device=metadata.getDevice();
			
			System.out.println("  --  UPDATE - NEGOTIATE");
			/*
	      try{
	        device.setResolution(100);
//	        device.setRegionOfInterest(0.0,0.0,40.0,50.0);       // top-left corner 40x50 mm
	        device.setRegionOfInterest(0,0,400,500);               // top-left corner 400x500 pixels
	        device.setShowUserInterface(false);
	        device.setShowProgressBar(false);
	      }catch(Exception e){
	        e.printStackTrace();
	      }
			 */
			
			
		}else if(type.equals(ScannerIOMetadata.STATECHANGE)){
			System.out.println("  --  UPDATE - STATECHANGE");
			
			// For the case that scan window popped up but cancel button is pushed
			if((metadata.getLastState() == 4) && (metadata.getState() == 3) && (metadata.getImage() == null))
				notify();
			
			// For the case that select window popped up but cancel or select button is pushed
			if((metadata.getLastState() == 3) && (metadata.getState() == 3))
				notify();
			
			System.err.println(metadata.getStateStr());
		}else if(type.equals(ScannerIOMetadata.EXCEPTION)){
			System.out.println("  --  UPDATE - EXCEPTION");
			
			metadata.getException().printStackTrace();
		}
		
		//notify();
	}
	
}
