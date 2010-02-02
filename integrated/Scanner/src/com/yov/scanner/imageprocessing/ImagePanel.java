package yov;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.*;


public class ImagePanel extends JPanel{

	private BufferedImage displayImage;
	private boolean isMoved;
	int originalX;
	int originalY;
	
	
	public ImagePanel(BufferedImage newImage){
		displayImage = newImage;
		
		originalX = 0;
		originalY = 0;
		isMoved = false;
	}
	
	public ImagePanel(){
		super();
	}

	
	public void setImage(BufferedImage newImage){
		displayImage = newImage;
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		if(displayImage != null){
			
			if(!isMoved){		
				Point originalLoc = this.getLocation();
				
				originalX = (int)originalLoc.getX();
				originalY = (int)originalLoc.getY();
				
				isMoved = true;
			}
			
			int dispX = 0;
			int dispY = 0;

			int imgHeight = displayImage.getHeight();
			int imgWidth = displayImage.getWidth();
			int paneHeight = this.getHeight();
			int paneWidth = this.getWidth();

			if((paneHeight > imgHeight) && (paneWidth > imgWidth)){
				dispX = (int)((paneWidth / 2.0f) - (imgWidth / 2.0f));
				dispY = (int)((paneHeight / 2.0f) - (imgHeight / 2.0f));
			}

			this.setLocation(dispX + originalX, dispY + originalY);
			
			g.drawImage(displayImage, 0, 0, null);
		}
		
	}
	
	
}
