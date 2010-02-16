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
	private DBData primaryDBData;
	private CardImage previewImage;
	
	private boolean isBrightnessChanged = false;
	private CardImage tempImage;
	
	//private RonCemerOCR simpleOCR;
	
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
	
	public String retrieveData(String trainingFolder){

            String cardText = "";

            //primaryDBData = primaryImage.retrieveData();
            //cardText = simpleOCR.run(primaryImage.getFileName(), trainingFolder);


            try{
            File tessExe = new File(".");



                            OCR ocrEngine = new OCR("");



                            //cardText = ocrEngine.recognizeText(imageFile, index, allPages, imageFormat, langCodes[jComboBoxLang.getSelectedIndex()]);
            }catch(Exception e){
                e.printStackTrace();
            }

            /*
            try {
                //if (this.jComboBoxLang.getSelectedIndex() == -1) {
                //    JOptionPane.showMessageDialog(this, "Please select a language.", APP_NAME, JOptionPane.INFORMATION_MESSAGE);
                //    return;
                //}
                //if (this.jImageLabel.getIcon() == null) {
                //    JOptionPane.showMessageDialog(this, "Please load an image.", APP_NAME, JOptionPane.INFORMATION_MESSAGE);
                //    return;
                //}

                //getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                //getGlassPane().setVisible(true);

                String imageFileName = primaryImage.getFileName();
                final String imageFormat = imageFileName.substring(imageFileName.lastIndexOf('.') + 1);




                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        try {

                            File tessExe = new File(".");


        
                            OCR ocrEngine = new OCR("");
                            
                            
                            
                            //cardText = ocrEngine.recognizeText(imageFile, index, allPages, imageFormat, langCodes[jComboBoxLang.getSelectedIndex()]);
                            //jTextArea1.append(ocrEngine.recognizeText(imageFile, index, allPages, imageFormat, langCodes[jComboBoxLang.getSelectedIndex()]));
                            //jLabelStatus.setText("OCR completed.");
                        } catch (OutOfMemoryError oome) {
                            oome.printStackTrace();
                            //JOptionPane.showMessageDialog(null, APP_NAME
                            //        + myResources.getString("_has_run_out_of_memory.\nPlease_restart_") + APP_NAME
                            //        + myResources.getString("_and_try_again."), myResources.getString("Out_of_Memory"), JOptionPane.ERROR_MESSAGE);
                        } catch (FileNotFoundException fnfe) {
                            fnfe.printStackTrace();
                            //JOptionPane.showMessageDialog(null, "An exception occurred in Tesseract engine while recognizing this image.", APP_NAME, JOptionPane.ERROR_MESSAGE);
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                            //JOptionPane.showMessageDialog(null, "Cannot find Tesseract. Please set its path.", APP_NAME, JOptionPane.ERROR_MESSAGE);
                        } catch (RuntimeException re) {
                            re.printStackTrace();
                            //JOptionPane.showMessageDialog(null, re.getMessage(), APP_NAME, JOptionPane.ERROR_MESSAGE);
                        } catch (Exception exc) {
                            exc.printStackTrace();
                        }
                    }
                });

            } catch (Exception exc) {
                System.err.println(exc.getMessage());
            } finally {
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        //getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                        //getGlassPane().setVisible(false);
                    }
                });
            }
            */

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
