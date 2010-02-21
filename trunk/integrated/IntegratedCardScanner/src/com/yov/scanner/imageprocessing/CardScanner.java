package com.yov.scanner.imageprocessing;

import com.jen.scanner.ui.ScannerView;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import javax.swing.JComponent;
import javax.swing.JTextField;
import uk.co.mmscomputing.device.sane.SaneScanner;
import uk.co.mmscomputing.device.scanner.Scanner;
import uk.co.mmscomputing.device.scanner.ScannerDevice;
import uk.co.mmscomputing.device.scanner.ScannerIOException;
import uk.co.mmscomputing.device.scanner.ScannerIOMetadata;
import uk.co.mmscomputing.device.scanner.ScannerListener;
import uk.co.mmscomputing.device.scanner.ScannerIOMetadata.Type;

public class CardScanner implements ScannerListener {

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

    public CardScanner() {
        System.out.println("CardScanner - Contructor");

        cardScanner = Scanner.getDevice();
        if (cardScanner != null) {
            cardScanner.addListener(this);
        } else {
            System.out.print("Display dialog card Scanner is null");
        }

        isNotified = false;
        isWaiting = false;
        waitSource = SOURCE_UNDEFINED;

        try {
            name = cardScanner.getSelectedDeviceName();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CardScanner(String cardFileName) {
        System.out.println("CardScanner(String cardFileName) - Contructor");

        targetFileName = cardFileName;

        cardScanner = Scanner.getDevice();
        if (cardScanner != null) {
            cardScanner.addListener(this);
        } else {
            System.out.print("Display dialog card Scanner is null");
        }
        isNotified = false;
        isWaiting = false;
        waitSource = SOURCE_UNDEFINED;

        try {
            name = cardScanner.getSelectedDeviceName();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    public void addScannerListener(ScannerListener scannerListener){
    cardScanner.addListener(scannerListener);
    }
     */
    ScannerView scannerView = null;

    public synchronized BufferedImage scan(ScannerView scannerView) {
        this.scannerView = scannerView;
        scanImage = null;
        System.out.println("CardScanner.scan()");

        if (name == null) {
            try {
                name = cardScanner.getSelectedDeviceName();
            } catch (ScannerIOException ex) {
                Logger.getLogger(CardScanner.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
            }
        }

        try {
            cardScanner.acquire();
            System.out.println(" ---- Scanner Acquire -----");

            isWaiting = true;
            System.out.println("+++++++ Scanner Wait +++");
            waitSource = SOURCE_SCAN;
            if (cardScanner instanceof SaneScanner) {
            } else {
//                wait();

            }

            isNotified = !isNotified;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return scanImage;
    }

    class SaneChecker implements Runnable {

        JTextField tf;
        public boolean mTrucking = true;

        public SaneChecker(JTextField tf) {
            this.tf = tf;
        }

        public void run() {
            int count = 0;

            while (mTrucking) {
                try {
                    try {
//                                System.out.println("SaneScanner  count" + count  + cardScanner.);
//                                     System.out.println(count+"Setting scaner name" +cardScanner.getSelectedDeviceName() );
                        String scannerName = cardScanner.getSelectedDeviceName();
                        // Remove "TWAIN" or "SANE" from scanner's name
                        int extStrPos = scannerName.lastIndexOf("TWAIN") + scannerName.lastIndexOf("SANE") + 1;

                        if (extStrPos > -1) {
                            scannerName = scannerName.substring(0, extStrPos - 1);
                        }
                        tf.setText(scannerName);
                    } catch (ScannerIOException ex) {
                        Logger.getLogger(CardScanner.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    Thread.sleep(200);
                    count++;
                    if (count == 300) {
                        mTrucking = false;
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    SaneChecker sanechecker = null;

    public synchronized String selectScanner(JTextField tf) {
        System.out.println("CardScanner.selectScanner()");
        String scannerName = "";
        int extStrPos = 0;

        try {
            cardScanner.select();
            isWaiting = true;
            System.out.println("+++++++ Select Wait +++");
            waitSource = SOURCE_SELECT;
            if (cardScanner instanceof SaneScanner) {
                if (sanechecker != null) {
                    sanechecker.mTrucking = false;
                }
                sanechecker = new SaneChecker(tf);
                new Thread(sanechecker).start();
                return cardScanner.getSelectedDeviceName();
            } else {

//                wait();
                if (sanechecker != null) {
                    sanechecker.mTrucking = false;
                }
                sanechecker = new SaneChecker(tf);
                new Thread(sanechecker).start();
            }

            //wait();

            isNotified = !isNotified;

            scannerName = cardScanner.getSelectedDeviceName();
        } catch (Exception e) {
            e.printStackTrace();

            scannerName = "No Device is selected";
        }

        // Remove "TWAIN" or "SANE" from scanner's name
        extStrPos = scannerName.lastIndexOf("TWAIN") + scannerName.lastIndexOf("SANE") + 1;

        if (extStrPos > -1) {
            scannerName = scannerName.substring(0, extStrPos - 1);
        }
        System.out.println("New Scanner is " + scannerName);
        return scannerName;
    }

    // - Get Method(s) - //
    public String getName() {
        return name;
    }

    public String getTargetFileName() {
        return targetFileName;
    }

    public int getFileNameIndex() {
        return fileNameIndex;
    }

    // - Set Method(s) - //
    public String setName(String newName) {
        String oldName = name;
        name = newName;
        System.out.println("CardScanner.setName(String newName)");

        return oldName;
    }

    public void setTargetFileName(String newFileName) {
         File curDir = new File(".");
        try {
            String scannedImageFileName = curDir.getCanonicalPath() + File.separator + "cardImages" + File.separator;
             targetFileName = scannedImageFileName;
             return;
        } catch (IOException ex) {
            Logger.getLogger(CardScanner.class.getName()).log(Level.SEVERE, null, ex);
        }
         
        String oldFileName = targetFileName;
        targetFileName = newFileName;
        System.out.println("CardScanner.setTargetFileName " + targetFileName);
        // Find from Image file Name
//          return oldFileName;
    }

    @Override
    public synchronized void update(Type type, ScannerIOMetadata metadata) {

        System.out.println("CardScanner.update()");

        if (type.equals(ScannerIOMetadata.ACQUIRED)) {
            System.out.println("  --  UPDATE - ACQUIRED");

            scanImage = metadata.getImage();

            System.out.println("Have an image now!" + scanImage.getWidth());
            String newFileName = targetFileName + fileNameIndex + ".jpg";
            if (cardScanner instanceof SaneScanner) {
                if (scannerView != null) {
                    scannerView.scannedImage = scanImage;
                    scannerView.setScannedImage(newFileName);
                }
            } else {
//                if (!isNotified && isWaiting) {
//                    notify();
//                    isNotified = true;
//                    isWaiting = false;
//                    System.out.println("+++++++ Update Notify +++");
//                }
                 if (scannerView != null) {
                    scannerView.scannedImage = scanImage;
                    scannerView.setScannedImage(newFileName);
                }
            }


            //TestUI2.class.notifyAll();

            try {

                System.out.println("Writing image to " +newFileName );
                ImageIO.write(scanImage, "jpg", new File(newFileName));
                fileNameIndex++;

                //	        new uk.co.mmscomputing.concurrent.Semaphore(0,true).tryAcquire(2000,null);

            } catch (Exception e) {
                e.printStackTrace();

            }
        } else if (type.equals(ScannerIOMetadata.NEGOTIATE)) {
            ScannerDevice device = metadata.getDevice();

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


        } else if (type.equals(ScannerIOMetadata.STATECHANGE)) {
            System.out.println("  --  UPDATE - STATECHANGE");

            System.out.println("metadata.getLastState() = " + metadata.getLastState());
            System.out.println("metadata.getState() = " + metadata.getState());

            // For the case that scan window popped up but cancel button is pushed
            if ((metadata.getLastState() == 4) && (metadata.getState() == 3) && (waitSource == SOURCE_SCAN)) {
                if (!isNotified && isWaiting) {
                    notify();
                    isNotified = true;
                    isWaiting = false;
                    System.out.println("+++++++ Update Notify +++");
                }
            }

            // For the case that select window popped up but cancel or select button is pushed
            if ((metadata.getLastState() == 3) && (metadata.getState() == 3) && (waitSource == SOURCE_SELECT)) {
                if (!isNotified && isWaiting) {
                    notify();
                    isNotified = true;
                    isWaiting = false;
                    System.out.println("+++++++ Update Notify +++");
                }
            }

            System.err.println(metadata.getStateStr());
        } else if (type.equals(ScannerIOMetadata.EXCEPTION)) {
            System.out.println("  --  UPDATE - EXCEPTION");

            metadata.getException().printStackTrace();
        }

        //notify();
    }
}
