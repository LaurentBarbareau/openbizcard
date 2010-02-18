/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hideoaki.scanner.db.utils;

import com.hideoaki.scanner.db.model.Card;
import java.awt.geom.Arc2D;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author hideoaki
 */
public class ZipUtils {

    private static final String XML_FILE_NAME = "desc.xml";
    private static final String ZIP_FILE_NAME = "export.zip";
    private static final String ZIP_PIC_FILE_NAME = "pic.zip";
    private static final String IMAGE_FOLDER = "images";

    public static void main(String arg[]) {
        // testDeleteLocalCard();
        // System.out.println('oak');
        ArrayList<Card> cards = new ArrayList<Card>();

        Card c = new Card("หหหหหห", "Chalermsook", "Project Leader", "hideoaki@gmail.com", "Crie Company Limited", "http://www.hideoaki.com", "400/107", "bangkok", "cen", "Thailand", "10900", "025894284", "02555444455", "0805511559", "This is a note",
                "", "",
                "", "", "", "", "", "", "", "", "", "", "", "", "");
        c.setImgFront("./cardImages/scannedBCard0.jpg");
        c.setImgBack("./cardImages/scannedBCardBack0.jpg");
        Card c2 = new Card("กกกกก", "Chalermsook", "Project Leader", "hideoaki@gmail.com", "Crie Company Limited", "http://www.hideoaki.com", "400/107", "bangkok", "cen", "Thailand", "10900", "025894284", "02555444455", "0805511559", "This is a note",
                "", "",
                "", "", "", "", "", "", "", "", "", "", "", "", "");
        c2.setImgFront("./cardImages/scannedBCard1.jpg");
        c2.setImgBack("./cardImages/scannedBCardBack1.jpg");
        cards.add(c);
        cards.add(c2);

//        System.out.println(convertCardsToXML(cards));
//        ArrayList<Card> c1 = convertXMLToCards(testString);
//        System.out.println(c1);
        exportCards(cards, ZIP_FILE_NAME);
//       ArrayList<Card> cas =   importCards(ZIP_FILE_NAME);
//       exportCards(cas, "newexport.zip");
    }

    public static void exportCards(ArrayList<Card> cards, String zipFileName) {
        try {
            ///// Get All Picture file and zip it and update Card Detail
            FileOutputStream outZipPic = new FileOutputStream(ZIP_PIC_FILE_NAME);
            ZipOutputStream zipOutPic = new ZipOutputStream(outZipPic);
            int numPic = 0;
            ArrayList<Card> newCards = new  ArrayList<Card>();
            for (Iterator<Card> it = cards.iterator(); it.hasNext();) {
                try {
                    Card card = it.next();
                     Card newCard = new Card();
                     newCard.copy(card);
                    if (card.getImgFront() != null && !card.getImgFront().equals("")) {
                        File ff = new File(card.getImgFront());
                        if (ff.exists()) {
                            numPic++;
                            String newFileName = addFile(ff, zipOutPic, null);
                            newCard.setImgFront(newFileName);
                        }
                    }
                    if (card.getImgBack() != null && !card.getImgBack().equals("")) {
                        File ff = new File(card.getImgBack());
                        if (ff.exists()) {
                            numPic++;
                            String newFileName = addFile(ff, zipOutPic, null);
                            newCard.setImgBack(newFileName);
                        }
                    }
                     newCards.add(newCard);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (numPic > 0) {
                zipOutPic.finish();
                zipOutPic.close();

            }
            outZipPic.close();
            ///
            String xml = convertCardsToXML(newCards);


//            // Write XML Add Cards File
//            FileOutputStream out = new FileOutputStream(XML_FILE_NAME);
//            out.write(xml.getBytes("UTF-8"));
//            out.close();
            // Zip it
            FileOutputStream outZip = new FileOutputStream(zipFileName);
            ZipOutputStream zipOut = new ZipOutputStream(outZip);

            ZipEntry entry = new ZipEntry(XML_FILE_NAME);
            byte[] xmlBytes = xml.getBytes("UTF-8");
            int size = xmlBytes.length;
            System.out.println("size = " + size);
//            entry.setSize(size);
            zipOut.putNextEntry(entry);
            zipOut.write(xmlBytes);
            zipOut.closeEntry();
            /// Add Picture Zip file
            File zippicF = new File(ZIP_PIC_FILE_NAME);
            if (zippicF.exists()) {
                addFile(zippicF, zipOut, ZIP_PIC_FILE_NAME);
            }
            //// Deal with picture
            ////
            zipOut.finish();
            zipOut.close();
            outZip.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static ArrayList<Card> importCards(String zipFileName) {
        ArrayList<Card> cards = new ArrayList<Card>();
        try {

            ByteArrayOutputStream tempByteXML = new ByteArrayOutputStream();
            ByteArrayOutputStream tempBytePicZip = new ByteArrayOutputStream();
            FileInputStream zipFileIn = new FileInputStream(zipFileName);
            System.out.println(" availt " + zipFileIn.available());
            ZipInputStream inZip = new ZipInputStream(zipFileIn);
//            System.out.println("file in zip :" + inZip.getCount() + inZip.available());
            ///
            ZipEntry entry;
            byte[] buf = new byte[1024];
            int len;
            while ((entry = inZip.getNextEntry()) != null) {
                String fileName = entry.getName();
                System.out.println("filename " + fileName);
                /// It is XML File
                if (fileName.equals(XML_FILE_NAME)) {

                    while ((len = inZip.read(buf)) > 0) {
                        tempByteXML.write(buf, 0, len);
                    }
                }
                if (fileName.equals(ZIP_PIC_FILE_NAME)) {

                    while ((len = inZip.read(buf)) > 0) {
                        tempBytePicZip.write(buf, 0, len);
                    }
                }
                //// It is picture file
            }
            System.out.println("entry size :" + tempByteXML.toByteArray().length);
            String xml = new String(tempByteXML.toByteArray(), "UTF-8");
            cards = convertXMLToCards(xml);
//            System.out.println("xml " + xml);
            inZip.close();
            zipFileIn.close();
            tempByteXML.close();
            ///// Extract every pic
            ByteArrayInputStream picZipInput = new ByteArrayInputStream(tempBytePicZip.toByteArray());
            tempBytePicZip.close();
            ZipInputStream inZipPic = new ZipInputStream(picZipInput);
            // Create Or file Card image dir
            File imageFolder = new File(IMAGE_FOLDER);

            if (!imageFolder.exists()) {
                imageFolder.mkdir();
            }
            while ((entry = inZipPic.getNextEntry()) != null) {
                String fileName = entry.getName();
                System.out.println("filename " + fileName);
                String newFileName = IMAGE_FOLDER + File.separator + fileName;
                FileOutputStream newFileOut = new FileOutputStream(newFileName);
                /// Save to new path
                while ((len = inZipPic.read(buf)) > 0) {
                    newFileOut.write(buf, 0, len);
                }
                newFileOut.close();
                /// Find which card use that file
                for (Iterator<Card> it = cards.iterator(); it.hasNext();) {
                    Card card = it.next();
                    if (card.getImgFront().equals(fileName)) {
                        card.setImgFront(newFileName);
                    }
                    if (card.getImgBack().equals(fileName)) {
                        card.setImgBack(newFileName);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cards;
    }
    private static ArrayList<Integer> randomList = new ArrayList<Integer>();
    private static String addFile(File ff, ZipOutputStream zipOutPic, String filename) throws Exception {

        FileInputStream file = new FileInputStream(ff);
        String ext = getFileExt(ff.getName());
        if (!ext.equals("")) {
            ext = "." + ext;
        }

        int randomString  = ((int)(Math.random() * 1000000));
        while(randomList.contains(randomString)){
               randomString  = ((int)(Math.random() * 1000000));
        }
        randomList.add(randomString);
        String newFilename = filename == null ? randomString+ ext : filename;
        ZipEntry entryPic = new ZipEntry(newFilename);
//                        entryPic.setSize(pic.length);
        zipOutPic.putNextEntry(entryPic);
        byte[] buf = new byte[1024];
        int len;
        while ((len = file.read(buf)) > 0) {
            zipOutPic.write(buf, 0, len);
        }
//                        System.out.println("pic size " + pic.length);
        zipOutPic.closeEntry();
        return newFilename;
    }

    private static String getFileExt(String fileName) {
        String ext = "";
        try {
            int mid = fileName.lastIndexOf(".");
            String fname = fileName.substring(0, mid);
            ext = fileName.substring(mid + 1, fileName.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ext;
    }

    private static String convertCardsToXML(ArrayList<Card> cards) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(out));
        encoder.writeObject(cards);
        encoder.close();
        String outStr = "";
        try {
            outStr = new String(out.toByteArray(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            outStr = new String(out.toByteArray());
        }
        return outStr;
    }

    private static ArrayList<Card> convertXMLToCards(String xml) {
        ByteArrayInputStream in = null;
        try {
            in = new ByteArrayInputStream(xml.getBytes("UTF-8"));
        } catch (Exception e) {
            in = new ByteArrayInputStream(xml.getBytes());
        }
        XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(in));
        ArrayList<Card> o = (ArrayList<Card>) decoder.readObject();
        decoder.close();
        return o;
    }
    public static final String testString = "<?xml version='1.0' encoding='UTF-8'?><java version='1.6.0' class='java.beans.XMLDecoder'> <object class='java.util.ArrayList'>  <void method='add'>   <object class='com.hideoaki.scanner.db.model.Card'>    <void property='address'>     <string>400/107</string>    </void>    <void property='addressE'>     <string></string>    </void>    <void property='city'>     <string>bangkok</string>    </void>    <void property='cityE'>     <string></string>    </void>    <void property='company'>     <string>Crie Company Limited</string>    </void>    <void property='companyE'>     <string></string>    </void>    <void property='country'>     <string>Thailand</string>    </void>    <void property='countryE'>     <string></string>    </void>    <void property='email'>     <string>hideoaki@gmail.com</string>    </void>    <void property='fax'>     <string>02555444455</string>    </void>    <void property='faxE'>     <string></string>    </void>    <void property='firstName'>     <string>Krissada</string>    </void>    <void property='firstNameE'>     <string></string>    </void>    <void property='imgBack'>     <string></string>    </void>    <void property='imgFront'>     <string></string>    </void>    <void property='lastName'>     <string>Chalermsook</string>    </void>    <void property='lastNameE'>     <string></string>    </void>    <void property='mobile'>     <string>0805511559</string>    </void>    <void property='mobileE'>     <string></string>    </void>    <void property='note'>     <string>This is a note</string>    </void>    <void property='noteE'>     <string></string>    </void>    <void property='position'>     <string>Project Leader</string>    </void>    <void property='positionE'>     <string></string>    </void>    <void property='state'>     <string>cen</string>    </void>    <void property='stateE'>     <string></string>    </void>    <void property='telephone'>     <string>025894284</string>    </void>    <void property='telephoneE'>     <string></string>    </void>    <void property='website'>     <string>http://www.hideoaki.com</string>    </void>    <void property='zip'>     <string>10900</string>    </void>    <void property='zipE'>     <string></string>    </void>   </object>  </void>  <void method='add'>   <object class='com.hideoaki.scanner.db.model.Card'>    <void property='address'>     <string>400/107</string>    </void>    <void property='addressE'>     <string></string>    </void>    <void property='city'>     <string>bangkok</string>    </void>    <void property='cityE'>     <string></string>    </void>    <void property='company'>     <string>Crie Company Limited</string>    </void>    <void property='companyE'>     <string></string>    </void>    <void property='country'>     <string>Thailand</string>    </void>    <void property='countryE'>     <string></string>    </void>    <void property='email'>     <string>hideoaki@gmail.com</string>    </void>    <void property='fax'>     <string>02555444455</string>    </void>    <void property='faxE'>     <string></string>    </void>    <void property='firstName'>     <string>Krissada2</string>    </void>    <void property='firstNameE'>     <string></string>    </void>    <void property='imgBack'>     <string></string>    </void>    <void property='imgFront'>     <string></string>    </void>    <void property='lastName'>     <string>Chalermsook</string>    </void>    <void property='lastNameE'>     <string></string>    </void>    <void property='mobile'>     <string>0805511559</string>    </void>    <void property='mobileE'>     <string></string>    </void>    <void property='note'>     <string>This is a note</string>    </void>    <void property='noteE'>     <string></string>    </void>    <void property='position'>     <string>Project Leader</string>    </void>    <void property='positionE'>     <string></string>    </void>    <void property='state'>     <string>cen</string>    </void>    <void property='stateE'>     <string></string>    </void>    <void property='telephone'>     <string>025894284</string>    </void>    <void property='telephoneE'>     <string></string>    </void>    <void property='website'>     <string>http://www.hideoaki.com</string>    </void>    <void property='zip'>     <string>10900</string>    </void>    <void property='zipE'>     <string></string>    </void>   </object>  </void> </object></java> ";
}
