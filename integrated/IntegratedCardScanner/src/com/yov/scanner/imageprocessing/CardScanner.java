package com.yov.scanner.imageprocessing;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import uk.co.mmscomputing.device.scanner.Scanner;
import uk.co.mmscomputing.device.scanner.ScannerDevice;
import uk.co.mmscomputing.device.scanner.ScannerIOException;
import uk.co.mmscomputing.device.scanner.ScannerIOMetadata;
import uk.co.mmscomputing.device.scanner.ScannerListener;
import uk.co.mmscomputing.device.scanner.ScannerIOMetadata.Type;


public class CardScanner implements ScannerListener{

        private final int SOURCE_UNDEFINED = 0;
        private final int SOURCE_SCAN = 1;
        private final int SOURCE_SELECT = 2;

	private String name;
	private Scanner cardScanner;
	
	private String targetFileName;
	private int fileNameIndex;
	
	//private CardImage scanImage;
	private BufferedImage scanImage;

        private boolean isNotified, isWaiting;
        private int waitSource;
	
	public CardScanner(){
		System.out.println("CardScanner - Contructor");
		
		cardScanner = Scanner.getDevice();
		 if(cardScanner != null){
		cardScanner.addListener(this);
                }else{
                    System.out.print("Display dialog card Scanner is null");
                }

                isNotified = false;
                isWaiting = false;
                waitSource = SOURCE_UNDEFINED;
	}
	
	public CardScanner(String cardFileName){
		System.out.println("CardScanner(String cardFileName) - Contructor");
		
		targetFileName = cardFileName;
		
		cardScanner = Scanner.getDevice();
                if(cardScanner != null){
		cardScanner.addListener(this);
                }else{
                    System.out.print("Display dialog card Scanner is null");
                }
                isNotified = false;
                isWaiting = false;
                waitSource = SOURCE_UNDEFINED;
	}
	
	/*
	public void addScannerListener(ScannerListener scannerListener){
		cardScanner.addListener(scannerListener);
	}
	*/
	
	public synchronized BufferedImage scan(){
		scanImage = null;
		System.out.println("CardScanner.scan()");
                
                if(name == null){
                    try {
                        name = cardScanner.getSelectedDeviceName();
                    } catch (ScannerIOException ex) {
                        Logger.getLogger(CardScanner.class.getName()).log(Level.SEVERE, null, ex);
                        ex.printStackTrace();
                    }
                }

		try{
			cardScanner.acquire();
                        System.out.println(" ---- Scanner Acquire -----");

                        isWaiting = true;
                        System.out.println("+++++++ Scanner Wait +++");
                        waitSource = SOURCE_SCAN;
			wait();
                        
                        isNotified = !isNotified;
                        
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

                        isWaiting = true;
                        System.out.println("+++++++ Select Wait +++");
                        waitSource = SOURCE_SELECT;
			wait();

                        isNotified = !isNotified;
			
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
			
			scanImage = metadata.getImage();
					
			System.out.println("Have an image now!");
			
			if(!isNotified && isWaiting){
                            notify();
                            isNotified = true;
                            isWaiting = false;
                            System.out.println("+++++++ Update Notify +++");
			}
			//TestUI2.class.notifyAll();
			
			try{
				ImageIO.write(scanImage, "jpg", new File(targetFileName+fileNameIndex+".jpg"));
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

                        System.out.println("metadata.getLastState() = " + metadata.getLastState());
                        System.out.println("metadata.getState() = " + metadata.getState());
			
			// For the case that scan window popped up but cancel button is pushed
			if((metadata.getLastState() == 4) && (metadata.getState() == 3) && (waitSource == SOURCE_SCAN)){
				if(!isNotified && isWaiting){
                                    notify();
                                    isNotified = true;
                                    isWaiting = false;
                                    System.out.println("+++++++ Update Notify +++");
                                }
                        }
			
			// For the case that select window popped up but cancel or select button is pushed
			if((metadata.getLastState() == 3) && (metadata.getState() == 3) && (waitSource == SOURCE_SELECT)){
				if(!isNotified && isWaiting){
                                    notify();
                                    isNotified = true;
                                    isWaiting = false;
                                    System.out.println("+++++++ Update Notify +++");
                                }
                        }
			
			System.err.println(metadata.getStateStr());
		}else if(type.equals(ScannerIOMetadata.EXCEPTION)){
			System.out.println("  --  UPDATE - EXCEPTION");
			
			metadata.getException().printStackTrace();
		}
		
		//notify();
	}
	
}
