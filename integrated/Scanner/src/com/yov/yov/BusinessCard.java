package yov;

import java.awt.Component;
import java.awt.image.BufferedImage;

import com.roncemer.ocr.*;


public class BusinessCard { // DONE - But not tested yet

	private CardImage primaryImage;
	private DBData primaryDBData;
	private CardImage previewImage;
	
	private boolean isBrightnessChanged = false;
	private CardImage tempImage;
	
	private RonCemerOCR simpleOCR;
	
	public BusinessCard(CardImage scannedImage){
		primaryImage = new CardImage(scannedImage.getImageData());
		previewImage = new CardImage(scannedImage.getImageData());
		tempImage = new CardImage(scannedImage.getImageData());
		primaryDBData = new DBData();
	}
	
	public BusinessCard(BufferedImage scannedImage){
		primaryImage = new CardImage(scannedImage);
		previewImage = new CardImage(scannedImage);
		tempImage = new CardImage(scannedImage);
		primaryDBData = new DBData();
	}
	
	public boolean trim(){
		System.out.println("BusinessCard.trim()");
		
		return previewImage.trim();
	}
	
	public void rotate90(){
		System.out.println("BusinessCard.rotate90()");
		
		previewImage.rotate90();
	}
	
	public boolean turnToBlackAndWhite(){
		System.out.println("BusinessCard.turnToBlackAndWhite()");
		
		return previewImage.turnToBlackAndWhite();
	}
	
	public int changeBrightness(int newBrightnessBC){
		System.out.println("BusinessCard.changeBrightness(newBrightnessBC)");
		
		// Store original preview image in temp image
		if(!isBrightnessChanged && (newBrightnessBC != previewImage.getBrightness())){
			isBrightnessChanged = true;
			
			tempImage = previewImage.clone();
		}
		
		if(isBrightnessChanged && (newBrightnessBC != previewImage.getBrightness())){
			previewImage = tempImage.clone();
			
			//System.out.println("--- Use temp Image ---");
		}
		
		if(newBrightnessBC < 1)
			newBrightnessBC = 1;
		
		if(newBrightnessBC > 5)
			newBrightnessBC = 5;
		
		return previewImage.changeBrightness(newBrightnessBC);
	}
	
	public void exportPDF(String fileNameBC){
		System.out.println("BusinessCard.exportPDF()");
		
		primaryImage.exportPDF(fileNameBC);
	}
	
	public void retrieveData(){
		System.out.println("BusinessCard.retrieveData()");
		
		//primaryDBData = primaryImage.retrieveData();
		simpleOCR.run(primaryImage.getFileName(), null);
	}
	
	public void confirmChange(){
		primaryImage = previewImage.clone();
		tempImage = previewImage.clone();
		
		isBrightnessChanged = false;
	}
	
	public void undoChanges(){
		System.out.println("BusinessCard.undoChanges");
		previewImage = primaryImage.clone();
		
		isBrightnessChanged = false;
	}
	
	public void initRonCemerOCR(Component newDisplayComponent){
		simpleOCR = new RonCemerOCR(newDisplayComponent);
	}
	
	// - Get Method(s) - //
	public CardImage getPrimaryImage(){
		System.out.println("BusinessCard.getPrimaryImage()");
		
		return primaryImage;
	}
	
	public DBData getPrimaryDBData(){
		System.out.println("BusinessCard.getPrimaryDBData()");
		
		return primaryDBData;
	}
	
	public CardImage getPreviewImage(){
		System.out.println("BusinessCard.getPreviewImage()");
		
		return previewImage;
	}
	
	// - Set Method(s) - //
	public CardImage setPrimaryImage(CardImage newPrimaryImage){
		System.out.println("BusinessCard.setPrimaryImage(newPrimaryImage)");
		CardImage oldPrimaryImage = primaryImage;
		primaryImage = newPrimaryImage;
		previewImage = newPrimaryImage;
		
		return oldPrimaryImage;
	}
	
	public DBData setPrimaryDBData(DBData newPrimaryDBData){
		System.out.println("BusinessCard.setPrimaryDBData(newPrimaryDBData)");
		DBData oldPrimaryDBData = primaryDBData;
		primaryDBData = newPrimaryDBData;
		
		return oldPrimaryDBData;
	}
	
	public CardImage setPreviewImage(CardImage newPreviewImage){
		System.out.println("BusinessCard.setPreviewImage()");
		CardImage oldPreviewImage = previewImage;
		previewImage = newPreviewImage;
		
		return oldPreviewImage;
	}

	public String setImageFileName(String newImageFileName){
		System.out.println("BusinessCard.setImageFileName()");
		String oldImageFileName = primaryImage.setFileName(newImageFileName);
		
		return oldImageFileName;
	}
	
}
