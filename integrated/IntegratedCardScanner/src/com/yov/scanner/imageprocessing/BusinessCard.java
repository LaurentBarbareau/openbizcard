package com.yov.scanner.imageprocessing;

import java.awt.Component;
import java.awt.image.BufferedImage;
import javax.swing.SwingUtilities;

import com.yov.scanner.ocr.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class BusinessCard { // DONE - But not tested yet

	private CardImage primaryImage;
	private CardImage previewImage;
	
	private boolean isBrightnessChanged = false;
	private CardImage tempImage;
	
	//private RonCemerOCR simpleOCR;
	
	public BusinessCard(CardImage scannedImage){
		primaryImage = new CardImage(scannedImage.getImageData());
		previewImage = new CardImage(scannedImage.getImageData());
		tempImage = new CardImage(scannedImage.getImageData());
	}
	
	public BusinessCard(BufferedImage scannedImage){
		primaryImage = new CardImage(scannedImage);
		previewImage = new CardImage(scannedImage);
		tempImage = new CardImage(scannedImage);
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
	
	public String retrieveData(){

            String cardText = "";

            try {
                File tessExe = new File(".");

                tessExe = new File(tessExe.getCanonicalPath() + "\\dis\\externalLib\\tesseract");
                //System.out.println("Tesseract Path(Absolute) = " + tessExe.getAbsolutePath());
                //System.out.println("Tesseract Path(Canonical) = " + tessExe.getCanonicalPath());

                OCR ocrEngine = new OCR(tessExe.getCanonicalPath());

                String imgFileName = primaryImage.getFileName();
                File imgFile = new File(imgFileName);
                String imgFormat = imgFileName.substring(imgFileName.lastIndexOf(".") + 1);

                //System.out.println("My imageFile = " + imgFile.getAbsolutePath());
                //System.out.println("My imageFile's parent = " + imgFile.getParentFile().toString());

                cardText = ocrEngine.recognizeText(imgFile, 0, false, imgFormat, "eng");

                //cardText = ocrEngine.recognizeText(imageFile, index, allPages, imageFormat, langCodes[jComboBoxLang.getSelectedIndex()]);

                //System.out.println("CardText = " + cardText);
                
            } catch (Exception e) {
                e.printStackTrace();
            }


            // Post Process from string read by OCR

            return cardText;
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
	
	
	
	// - Get Method(s) - //
	public CardImage getPrimaryImage(){
		System.out.println("BusinessCard.getPrimaryImage()");
		
		return primaryImage;
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
