/*
 * ImageIOHelper.java
 *
 * Created on December 24, 2007, 1:15 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.yov.scanner.ocr;

import com.jen.scanner.ui.util.Utils;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.imageio.stream.*;
import javax.imageio.metadata.*;
import com.sun.media.imageio.plugins.tiff.*;
import java.awt.image.*;
import javax.swing.*;

/**
 *
 * @author Quan Nguyen (nguyenq@users.sf.net)
 */
public class ImageIOHelper {
    
    /**
     * Creates a new instance of ImageIOHelper
     */
    public ImageIOHelper() {
    }
    
    public static ArrayList<File> createImages(File imageFile, int index, boolean all, String imageFormat) {
        ArrayList<File> tempFileNames = new ArrayList<File>();
        
        try {
            
            Iterator readers = ImageIO.getImageReadersByFormatName(imageFormat);
            ImageReader reader = (ImageReader) readers.next();
            
            ImageInputStream iis = ImageIO.createImageInputStream(imageFile);
            reader.setInput(iis);
            //Read the stream metadata
            IIOMetadata streamMetadata = reader.getStreamMetadata();
            
            
            //Set up the writeParam
            TIFFImageWriteParam tiffWriteParam = new TIFFImageWriteParam(Locale.US);
            tiffWriteParam.setCompressionMode(ImageWriteParam.MODE_DISABLED);
            
            //Get tif writer and set output to file
            Iterator writers = ImageIO.getImageWritersByFormatName("tiff");
            ImageWriter writer = (ImageWriter)writers.next();
            
            if (all) {
                int imageTotal = reader.getNumImages(true);
                
                for (int i = 0; i < imageTotal; i++) {
//                BufferedImage bi = (BufferedImage) imageList.get(imageIndex).getImage();
                    BufferedImage bi = reader.read(i);
                    IIOImage image = new IIOImage(bi, null, reader.getImageMetadata(i));
                    File tempFile = tempImageFile(imageFile, i);
                    ImageOutputStream ios = ImageIO.createImageOutputStream(tempFile);
                    writer.setOutput(ios);
                    writer.write(streamMetadata, image, tiffWriteParam);
                    ios.close();
                    tempFileNames.add(tempFile);
                }
            } else {
                BufferedImage bi = reader.read(index);
                IIOImage image = new IIOImage(bi, null, reader.getImageMetadata(index));
                File tempFile = tempImageFile(imageFile, index);
                ImageOutputStream ios = ImageIO.createImageOutputStream(tempFile);
                writer.setOutput(ios);
                writer.write(streamMetadata, image, tiffWriteParam);
                ios.close();
                tempFileNames.add(tempFile);
            }
            writer.dispose();
            reader.dispose();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        return tempFileNames;
        
    }
    
    public static File tempImageFile(File imageFile, int index) {
        String path = imageFile.getPath();
        StringBuffer strB = new StringBuffer(path);
        strB.insert(path.lastIndexOf('.'), index);
        if(File.separator.equals("/")){
           String ext =  Utils.getExtension(imageFile);
           String filename = strB.toString();
           filename = filename.replaceAll(ext, "tif");
            return new File(filename);
        }else
        return new File(strB.toString().replaceFirst("(?<="+File.separator+".)("+File.separator+"w+)$", "tif"));
    }
        
    public static ArrayList<ImageIconScalable> getImageList(File imageFile) {
        ArrayList<ImageIconScalable> al = new ArrayList<ImageIconScalable>();
        try {
            String imageFileName = imageFile.getName();
            String imageFormat = imageFileName.substring(imageFileName.lastIndexOf('.') + 1);
            Iterator readers = ImageIO.getImageReadersByFormatName(imageFormat);
            ImageReader reader = (ImageReader) readers.next();
            
            if (reader == null) {
                JOptionPane.showConfirmDialog(null, "Need to install JAI Image I/O package.\nhttps://jai-imageio.dev.java.net");
                return null;
            }
            
            ImageInputStream iis = ImageIO.createImageInputStream(imageFile);
            reader.setInput(iis);
            int imageTotal = reader.getNumImages(true);
            
            for (int i = 0; i < imageTotal; i++) {
                al.add(new ImageIconScalable(reader.read(i)));
            }
            
            reader.dispose();
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        
        return al;
    }
    
}
