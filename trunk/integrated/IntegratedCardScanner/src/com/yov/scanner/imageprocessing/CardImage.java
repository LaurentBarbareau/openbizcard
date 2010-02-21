package com.yov.scanner.imageprocessing;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CardImage {

	private boolean isColored;
	private int brightness;
	private String fileName;
	private BufferedImage imageData;

	public CardImage(String cardFileName){
		fileName = cardFileName;
		
		brightness = 3;
	}
	
	public CardImage(BufferedImage newImage){
		imageData = newImage;
		
		brightness = 3;
	}
	
	public boolean trim(){
		System.out.println("CardImage.trim()");
		boolean isChanged = false;
		
		//-----------------------------------------------------------------//
		try{
			// Get color of every pixel
			int imgWidth = imageData.getWidth();
			int imgHeight = imageData.getHeight();
			int[][] pixelRGB = new int[imgWidth][imgHeight];
			HashMap<Integer, Integer> colorCount = new HashMap<Integer, Integer>();
			Integer count = new Integer(0);
			
			for(int w = 0; w < imgWidth; w++){
				for(int h = 0; h < imgHeight; h++){
					pixelRGB[w][h] = imageData.getRGB(w, h);
					
					count = colorCount.get(pixelRGB[w][h]);
					colorCount.put(pixelRGB[w][h], count == null? 1 : count + 1);
				}
			}
			
			// Determine background (dominant) color
			int background = 0;
			int max = 0;
			int value = 0;
			for(Map.Entry<Integer, Integer> entry: colorCount.entrySet()){
				value = entry.getValue();
				if(value > max){
					max = value;
					background = entry.getKey();
				}
			}
			
			Color bgColor = new Color(background);
			System.out.println(" --- Background RGB = " + background);
			System.out.println(" --- Background Color = " + bgColor.toString() + 
					"   (white = " + Color.WHITE.toString() + ")");
			System.out.println(" --- + Background Color Count = " + max + " pixels");
			System.out.println(" --- + Background Red = " + bgColor.getRed() + 
					"   (white's red = " + Color.WHITE.getRed() + ")");
			System.out.println(" --- + Background Green = " + bgColor.getGreen() + 
					"   (white's green = " + Color.WHITE.getGreen() + ")");
			System.out.println(" --- + Background Blue = " + bgColor.getBlue() + 
					"   (white's blue = " + Color.WHITE.getBlue() + ")");
			System.out.println(" --- + Background Alpha = " + bgColor.getAlpha() +
					"   (white's alpha = " + Color.WHITE.getAlpha() + ")");
			
			// Search for 4 corners of the image that are not dominant color
			int topX = 0;
			int topY = 0;
			int leftX = 0;
			int leftY = 0;
			int rightX = imgWidth;
			int rightY = imgHeight;
			int bottomX = imgWidth;
			int bottomY = imgHeight;
			
			System.out.println(" * Image Width = " + imgWidth + ", Height = " + imgHeight);
			int x = 0;
			int y = 0;
			Color pixelColor;
			int diffRGBA = 0;
			double threshold = 0.10; // Threshold for the difference is 5 percent
			
			// - Go down from the top to search for the first pixel that is not the background
			for(y = 0; y < imgHeight; y++){
				for(x = 0; x < imgWidth; x++){
					pixelColor = new Color(pixelRGB[x][y]);
					diffRGBA = Math.abs(pixelColor.getRed() - bgColor.getRed());
					diffRGBA += Math.abs(pixelColor.getGreen() - bgColor.getGreen());
					diffRGBA += Math.abs(pixelColor.getBlue() - bgColor.getBlue());
					diffRGBA += Math.abs(pixelColor.getAlpha() - bgColor.getAlpha());
					
					if(diffRGBA >= threshold * 4.0 * 255.0){
						topX = x;
						topY = y;
						break;
					}
				}
				if(diffRGBA >= threshold * 4.0 * 255.0){
					break;
				}
			}
			System.out.println(" ** Top = (" + topX + ", " + topY + ")");
			//Color topColor = new Color(pixelRGB[topX][topY]);
			//System.out.println(" --- Top RGB = " + pixelRGB[topX][topY]);
			//System.out.println(" --- Top Color = " + topColor.toString());
			//System.out.println(" --- Top Alpha = " + topColor.getAlpha());
			//scannedImage.setRGB(topX, topY, Color.RED.getRGB());
			
			// - Go right from the left-side to search for the first pixel that is not the background
			for(x = 0; x < imgWidth; x++){
				for(y = 0; y < imgHeight; y++){
					pixelColor = new Color(pixelRGB[x][y]);
					diffRGBA = Math.abs(pixelColor.getRed() - bgColor.getRed());
					diffRGBA += Math.abs(pixelColor.getGreen() - bgColor.getGreen());
					diffRGBA += Math.abs(pixelColor.getBlue() - bgColor.getBlue());
					diffRGBA += Math.abs(pixelColor.getAlpha() - bgColor.getAlpha());
					
					if(diffRGBA >= threshold * 4.0 * 255.0){
						leftX = x;
						leftY = y;
						break;
					}
				}
				if(diffRGBA >= threshold * 4.0 * 255.0){
					break;
				}
			}
			System.out.println(" ** Left = (" + leftX + ", " + leftY + ")");
			//Color leftColor = new Color(pixelRGB[leftX][leftY]);
			//System.out.println(" --- Left RGB = " + pixelRGB[leftX][leftY]);
			//System.out.println(" --- Left Color = " + leftColor.toString());
			//System.out.println(" --- Left Alpha = " + leftColor.getAlpha());
			//scannedImage.setRGB(leftX, leftY, Color.RED.getRGB());
			
			// - Go up from the bottom to search for the first pixel that is not the background
			for(y = imgHeight - 1; y >= 0; y--){
				for(x = imgWidth - 1; x >= 0; x--){
					pixelColor = new Color(pixelRGB[x][y]);
					diffRGBA = Math.abs(pixelColor.getRed() - bgColor.getRed());
					diffRGBA += Math.abs(pixelColor.getGreen() - bgColor.getGreen());
					diffRGBA += Math.abs(pixelColor.getBlue() - bgColor.getBlue());
					diffRGBA += Math.abs(pixelColor.getAlpha() - bgColor.getAlpha());
					
					if(diffRGBA >= threshold * 4.0 * 255.0){
						bottomX = x;
						bottomY = y;
						break;
					}
				}
				if(diffRGBA >= threshold * 4.0 * 255.0){
					break;
				}
			}
			System.out.println(" ** Bottom = (" + bottomX + ", " + bottomY + ")");
			//Color bottomColor = new Color(pixelRGB[bottomX][bottomY]);
			//System.out.println(" --- Bottom RGB = " + pixelRGB[bottomX][bottomY]);
			//System.out.println(" --- Bottom Color = " + bottomColor.toString());
			//System.out.println(" --- Bottom Alpha = " + bottomColor.getAlpha());
			//scannedImage.setRGB(bottomX, bottomY, Color.RED.getRGB());
			
			// - Go left from the right-side to search for the first pixel that is not the background
			for(x = imgWidth - 1; x >= 0; x--){
				for(y = imgHeight - 1; y >= 0; y--){
					pixelColor = new Color(pixelRGB[x][y]);
					diffRGBA = Math.abs(pixelColor.getRed() - bgColor.getRed());
					diffRGBA += Math.abs(pixelColor.getGreen() - bgColor.getGreen());
					diffRGBA += Math.abs(pixelColor.getBlue() - bgColor.getBlue());
					diffRGBA += Math.abs(pixelColor.getAlpha() - bgColor.getAlpha());
					
					if(diffRGBA >= threshold * 4.0 * 255.0){
						rightX = x;
						rightY = y;
						break;
					}
				}
				if(diffRGBA >= threshold * 4.0 * 255.0){
					break;
				}
			}
			System.out.println(" ** Right = (" + rightX + ", " + rightY + ")");
			//Color rightColor = new Color(pixelRGB[rightX][rightY]);
			//System.out.println(" --- Right RGB = " + pixelRGB[rightX][rightY]);
			//System.out.println(" --- Right Color = " + rightColor.toString());
			//System.out.println(" --- Right Alpha = " + rightColor.getAlpha());
			//scannedImage.setRGB(rightX, rightY, Color.RED.getRGB());
			
			// Crop from the original image based on the 4 corners
			int shortMargin = 2;
			int longMargin = 5;
			int subX = 0, subY = 0, subWidth = 0, subHeight = 0;
			if((leftX == topX) && (leftY == topY)){
				subX = Math.max(leftX - shortMargin, 0);
				subY = Math.max(topY - shortMargin, 0);
			}else{
				subX = Math.max(leftX - longMargin, 0);
				subY = Math.max(topY - longMargin, 0);
			}
			
			if((rightX == bottomX) && (rightY == bottomY)){
				subWidth = Math.min((rightX + shortMargin) - subX, imgWidth - subX);
				subHeight = Math.min((bottomY + shortMargin) - subY, imgHeight - subY);
			}else{
				subWidth = Math.min((rightX + longMargin) - subX, imgWidth - subX);
				subHeight = Math.min((bottomY + longMargin) - subY, imgHeight - subY);
			}
			
			System.out.println(" + Sub = (" + subX + ", " + subY + ") ");
			System.out.println(" ++ Sub Width = " + subWidth + ", Height = " + subHeight);
			imageData = imageData.getSubimage(subX, subY, subWidth, subHeight);
			
		}catch(Exception e){
			System.out.println("!!!!ERROR in Auto-Crop!!!!!");
			e.printStackTrace();
		}
		
		//-----------------------------------------------------------------//
		
		
		return isChanged;
	}
	
	public void rotate90(){
		System.out.println("CardImage.rotate90()");


		int imgWidth = imageData.getWidth();
		int imgHeight = imageData.getHeight();
		
//		BufferedImage rotatedImage = new BufferedImage(imgHeight, imgWidth, imageData.getType());
		BufferedImage rotatedImage = new BufferedImage(imgHeight, imgWidth,BufferedImage.TYPE_INT_BGR);
		Graphics2D rotatedGraphics = rotatedImage.createGraphics();
		
		double tranX = (imgHeight - imgWidth) / 2.0;
		double tranY = (imgWidth - imgHeight) / 2.0;
		
		AffineTransform rotator = AffineTransform.getTranslateInstance(tranX, tranY);
		rotator.rotate(Math.toRadians(-90), imgWidth / 2.0, imgHeight / 2.0);
		
		rotatedGraphics.drawRenderedImage(imageData, rotator);
		rotatedGraphics.dispose();
		
		imageData = rotatedImage;
		
	}
	
	public boolean turnToBlackAndWhite(){ 
		System.out.println("CardImage.turnToBlackAndWhite");
		boolean oldIsColored = isColored;
		isColored = false;
		
		BufferedImage grayScale = new BufferedImage(imageData.getWidth(), imageData.getHeight(), 
				BufferedImage.TYPE_BYTE_GRAY);
		Graphics grayScaleGraphics = grayScale.getGraphics();
		grayScaleGraphics.drawImage(imageData, 0, 0, null);
		grayScaleGraphics.dispose();
		imageData = grayScale;
		
		return oldIsColored;
	}
	
	public int changeBrightness(int newBrightness){
		System.out.println("CardImage.changeBrightness()");
		if((newBrightness == brightness) || (newBrightness < 1 || newBrightness > 5))
			return -1;
		
		int oldBrightness = brightness;
		brightness = newBrightness;
		
		float brightRatio = 1.0f;
		switch(newBrightness){
			case 1:
				brightRatio = 0.6f;
				break;
			case 2:
				brightRatio = 0.75f;
				break;
			case 3:
				brightRatio = 1.0f;
				break;
			case 4:
				brightRatio = 1.15f;
				break;
			case 5:
				brightRatio = 1.3f;
				break;
		};
		
		BufferedImage brightImage = new BufferedImage(imageData.getWidth(), imageData.getHeight(),
				BufferedImage.TYPE_INT_BGR);
		ConvolveOp brightOp = new ConvolveOp(new Kernel(1, 1, new float[]{brightRatio}));
		brightOp.filter(imageData, brightImage);
		
		imageData = brightImage;
		
		return oldBrightness;
	}
	
	public void exportPDF(String fileName){
		System.out.println("CardImage.exportPDF(String fileName)");
		File pdfFile = null;
		
		// TODO: Create a PDF file from the image
		
	}
	
	public CardImage clone(){
		CardImage newCardImage = new CardImage(imageData);
		newCardImage.setBrightness(brightness);
		newCardImage.setFileName(fileName);
		newCardImage.setIsColored(isColored);
		
		return newCardImage;
	}
	
	// - Get Method(s) - //
	public boolean getIsColored(){
		System.out.println("CardImage.getIsColored()");
		
		return isColored;
	}
	
	public BufferedImage getImageData(){
		System.out.println("CardImage.getImageData()");
		
		return imageData;
	}
	
	public int getBrightness(){
		System.out.println("CardImage.getBrightness()");
		
		return brightness;
	}
	
	public String getFileName(){
		System.out.println("CardImage.getFileName()");
		
		return fileName;
	}
	
	// - Set Method(s) - //
	public boolean setIsColored(boolean newIsColored){
		System.out.println("CardImage.setIsColored(boolean newIsColored)");
		boolean oldIsColored = isColored;
		isColored = newIsColored;
		
		return oldIsColored;
	}
	
	public BufferedImage setImageData(BufferedImage newImageData){ 
		System.out.println("CardImage.setImageData(int newImageData)");
		BufferedImage oldImageData = imageData;
		imageData = newImageData;
		
		return oldImageData;
	}
	
	public int setBrightness(int newBrightness){
		System.out.println("CardImage.setBrightness(int newBrightness)");
		if(newBrightness < 1 || newBrightness > 5)
			return -1;
		
		int oldBrightness = brightness;
		brightness = newBrightness;
		
		return oldBrightness;
	}
	
	public String setFileName(String newFileName){
		System.out.println("CardImage.setFileName(String newFileName)");
		String oldFileName = fileName;
		fileName = newFileName;
		
		return oldFileName;
	}
}
