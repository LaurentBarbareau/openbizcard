/*
 * OCR.java
 *
 * Created on December 24, 2007, 12:38 AM
 */

package com.yov.scanner.ocr;

import java.io.*;
import java.util.*;

/**
 *
 * @author Quan Nguyen (nguyenq@users.sf.net)
 */
public class OCR {
    private final String LANG_OPTION = "-l";
    private final String EOL = System.getProperty("line.separator");
    
    private String tessPath;
    
    /** Creates a new instance of OCR */
    public OCR(String tessPath) {
        this.tessPath = tessPath;
    }
    
    public String recognizeText(File imageFile, int index, boolean all, String imageFormat, String lang) throws Exception {
        ArrayList<File> tempImages = ImageIOHelper.createImages(imageFile, index, all, imageFormat);
        
        File outputFile = new File(imageFile.getParentFile(), "output");
        StringBuffer strB = new StringBuffer();
        
        List<String> cmd = new ArrayList<String>();
        System.out.println("Tessaract path " +tessPath + File.separator+"tesseract" );
        if(File.separator.equals("/")){
            cmd.add("tesseract");
        }else{
             cmd.add(tessPath + File.separator+"tesseract");
        }
        cmd.add(""); // placeholder for inputfile
        cmd.add(outputFile.getName());
        System.out.println("outputFile.getName " + outputFile.getName() );

        cmd.add(LANG_OPTION);
        cmd.add(lang);
        // Command for thai
         List<String> cmdT = new ArrayList<String>();
        System.out.println("Tessaract path " +tessPath + File.separator+"tesseract" );
        if(File.separator.equals("/")){
            cmdT.add("tesseract");
        }else{
             cmdT.add(tessPath + File.separator+"tesseract");
        }
        cmdT.add(""); // placeholder for inputfile
        cmdT.add(outputFile.getName());
        System.out.println("outputFile.getName " + outputFile.getName() );

        cmdT.add(LANG_OPTION);
        cmdT.add("tha");
        //
        ProcessBuilder pb = new ProcessBuilder();
        pb.directory(imageFile.getParentFile());
            
        for (File tempImage : tempImages) {
            // actual output file will be "output.txt"
//            ProcessBuilder pb = new ProcessBuilder(tessPath + "/tesseract", tempImage.getAbsolutePath(), outputFile.getAbsolutePath(), LANG_OPTION, lang);            
          System.out.println("input file:" + tempImage.getName());
//            cmd.set(1, tempImage.getName());
           cmd.set(1, tempImage.getName());
           pb.command(cmd);
            pb.redirectErrorStream(true);
            Process process = pb.start();
//            Process process = Runtime.getRuntime().exec(cmd.toArray(new String[0]));
            
            int w = process.waitFor();
            System.out.println("Exit value = " + w);
            
            // delete temp working files
            //tempImage.delete();
            
            if (w == 0) {
                BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(outputFile.getAbsolutePath() + ".txt"), "UTF-8"));
                
                String str;
                
                while ((str = in.readLine()) != null) {
                    strB.append(str).append(EOL);
                }
                in.close();
            } else {
                String msg;
                switch (w) {
                    case 1:
                        msg = "Errors accessing files. There may be spaces in your image's filename.";
                        break;
                    case 29:
                        msg = "Cannot recognize the image or its selected region.";
                        break;
                    case 31:
                        msg = "Unsupported image format.";
                        break;                        
                    default:
                        msg = "Errors occurred.";
                }
                for (File image : tempImages) {
                    image.delete();
                }
                throw new RuntimeException(msg);
            }
            // Thai
             System.out.println("input file:" + tempImage.getName());
//            cmd.set(1, tempImage.getName());
           cmdT.set(1, tempImage.getName());
           pb.command(cmdT);
            pb.redirectErrorStream(true);
            Process process2 = pb.start();
//            Process process = Runtime.getRuntime().exec(cmd.toArray(new String[0]));

            int w2 = process2.waitFor();
            System.out.println("Exit value = " + w);

            // delete temp working files
            tempImage.delete();

            if (w2 == 0) {
                BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(outputFile.getAbsolutePath() + ".txt"), "UTF-8"));

                String str;

                while ((str = in.readLine()) != null) {
                    strB.append(str).append(EOL);
                }
                in.close();
            } else {
                String msg;
                switch (w2) {
                    case 1:
                        msg = "Errors accessing files. There may be spaces in your image's filename.";
                        break;
                    case 29:
                        msg = "Cannot recognize the image or its selected region.";
                        break;
                    case 31:
                        msg = "Unsupported image format.";
                        break;
                    default:
                        msg = "Errors occurred.";
                }
                for (File image : tempImages) {
                    image.delete();
                }
                throw new RuntimeException(msg);
            }

        }
        new File(outputFile.getAbsolutePath() + ".txt").delete();
        return strB.toString();
    }
}
