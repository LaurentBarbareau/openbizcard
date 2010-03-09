package com.hideoaki.scanner.db.utils;

import com.hideoaki.scanner.db.model.Card;
import com.jen.scanner.ui.ScannerView;
import com.jen.scanner.ui.util.Utils;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SendEmailUtil {
    private static String EMAIL_ALERT = "emailAlert.text";

//    public static void sendEmail(String topic, String body,
//            BufferedImage imageFront, BufferedImage imageBack) {
//        try {
//            Desktop desktop = null;
//            if (Desktop.isDesktopSupported()) {
//                desktop = Desktop.getDesktop();
//                // Message message = new Message();
//                // desktop.browse(new URI("http://www.google.com"));
//                URI uriMailTo = null;
//                uriMailTo = new URI("mailto", "hideoaki@gmail.com?SUBJECT=" + topic + "&BODY=" + body + "&attachment=\"C:\\\\001.jpg\"", null);
//                desktop.mail(uriMailTo);
////				uri.
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (URISyntaxException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//    }
    public static void main(String[] args) {
//        String[] receivers = {"hideoaki@gmail.com", "hideoaki@hotmail.com"};
        //sendEmail("this is title", "thisis body", null, null);
        Card c = new Card("Krissada", "Chalermsook", "Project Leader", "hideoaki@gmail.com", "Crie Company Limited", "http://www.hideoaki.com", "400/107", "bangkok", "cen", "Thailand", "10900", "025894284", "02555444455", "0805511559", "This is a note",
                "", "",
                "", "", "", "", "", "", "", "", "", "", "", "", "");
        sendEmail("Test ", "Desc", c, "c:/001.jpg", "c:/001.jpg");
    }

    public static void setAlertMsg(String newAlert){
        EMAIL_ALERT = newAlert;
    }

    public static void sendEmail(String topic, String body, Card c, String pathToFrontImage, String pathToBackImage) {
        deleteTempVcfEml();
        String cardFileName = genVCardThai(c);
        String subject = topic;
        String bodyText = body;

        Properties properties = new Properties();
        properties.put("mail.smtp.host", "localhost");
        properties.put("mail.smtp.port", "25");
        Session session = Session.getDefaultInstance(properties, null);

        try {
            MimeMessage message = new MimeMessage(session);
            message.setSubject(subject);
            message.setSentDate(new Date());

            //
            // Set the email message text.
            //
            MimeBodyPart messagePart = new MimeBodyPart();
            messagePart.setText(bodyText);
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messagePart);
            //
            // Set the email attachment file
            //
            // Font
            if (pathToFrontImage != null && !pathToFrontImage.equals("")) {
                MimeBodyPart attachmentPartFront = new MimeBodyPart();
                FileDataSource fileDataSourceFront = new FileDataSource(pathToFrontImage) {

                    @Override
                    public String getContentType() {
                        return "application/octet-stream";
                    }
                };
                attachmentPartFront.setDataHandler(new DataHandler(fileDataSourceFront));
                attachmentPartFront.setFileName(fileDataSourceFront.getFile().getName());
                attachmentPartFront.setDataHandler(new DataHandler(fileDataSourceFront));
                attachmentPartFront.setFileName(fileDataSourceFront.getFile().getName());
                multipart.addBodyPart(attachmentPartFront);
            }
            // Back
            if (pathToBackImage != null && !pathToBackImage.equals("")) {
                MimeBodyPart attachmentPartBack = new MimeBodyPart();
                FileDataSource fileDataSourceBack = new FileDataSource(pathToBackImage) {

                    @Override
                    public String getContentType() {
                        return "application/octet-stream";
                    }
                };
                attachmentPartBack.setDataHandler(new DataHandler(fileDataSourceBack));
                attachmentPartBack.setFileName(fileDataSourceBack.getFile().getName());
                attachmentPartBack.setDataHandler(new DataHandler(fileDataSourceBack));
                attachmentPartBack.setFileName(fileDataSourceBack.getFile().getName());
                multipart.addBodyPart(attachmentPartBack);
            }
            // Attach Card
            if (cardFileName != null && !cardFileName.equals("")) {
                MimeBodyPart attachmentPartCard = new MimeBodyPart();
                FileDataSource fileDataSourceBack = new FileDataSource(cardFileName) {

                    @Override
                    public String getContentType() {
                        return "application/octet-stream";
                    }
                };
                attachmentPartCard.setDataHandler(new DataHandler(fileDataSourceBack));
                attachmentPartCard.setFileName(fileDataSourceBack.getFile().getName());
                attachmentPartCard.setDataHandler(new DataHandler(fileDataSourceBack));
                attachmentPartCard.setFileName(fileDataSourceBack.getFile().getName());
                multipart.addBodyPart(attachmentPartCard);
            }

            message.setContent(multipart);
            try {
                String emlFileName = System.currentTimeMillis() + ".eml";
                message.writeTo(new FileOutputStream(emlFileName));
                Desktop desktop = null;
                if (Desktop.isDesktopSupported()) {
                    desktop = Desktop.getDesktop();
                    System.out.println("finish writestring");
                    // Transport.send(message);
                    File file = new File(emlFileName);
                    Utils.mailClientNotify(EMAIL_ALERT, file.getAbsolutePath(), ScannerView.getDefaultFont());
                    //desktop.open(new File(emlFileName));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            System.out.println("finish");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private static String genVCardThai(Card c) {
        String name = c.getFirstName() + (c.getLastName().equals("") ? "" : " " + c.getLastName());
        String title = c.getPosition();
        String organisation = c.getCompany();
        String address = c.getAddress() + (c.getCity().equals("") ? "" : " " + c.getCity()) + (c.getState().equals("") ? "" : " " + c.getState()) + (c.getCountry().equals("") ? "" : " " + c.getCountry()) + (c.getZip().equals("") ? "" : " " + c.getZip());
        String phone = c.getTelephone();
        String email = c.getEmail();
        String fax = c.getFax();
        String comment = c.getNote();
        String mobile = c.getMobile();
        String vCard = "BEGIN:VCARD\n"
                + "FN:" + name + "\n"
                //                + "N:" + nName + "\n" // added - 16 Aug 01
                + "NOTE;ENCODING=QUOTED-PRINTABLE:" + comment + "\n" // added - 16 Aug 01
                + "TITLE:" + title + "\n"
                + "ORG:" + organisation + "\n"
                + "ADR;POSTAL;WORK:;" + address + "\n"
                + "TEL;Work:" + phone + "\n"
                + "TEL;Fax:" + fax + "\n"
                + "TEL;Cell:" + mobile + "\n"
                + "EMAIL;Internet:" + email + "\n"
                + "VERSION:2.1\n"
                + "END:VCARD\n";
        String fileName = System.currentTimeMillis() + ".vcf";
        try {
            PrintWriter out = new PrintWriter(new FileOutputStream(fileName));
            out.println(vCard);
            out.close();
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }

        return fileName;
    }

    private static void deleteTempVcfEml() {
        try {
            File f = new File(".");
            if (f.isDirectory()) {
                String[] allFiles = f.list();
                for (int i = 0; i < allFiles.length; i++) {
                    String fname = allFiles[i];
                    File file = new File(fname);
                    if (file.isFile()) {
                        if (file.getName().indexOf("vcf") >= 0 || file.getName().indexOf("eml") >= 0) {
                            try {
                                file.delete();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                System.out.println("f = " + f);
                System.out.println("allFiles = " + allFiles.length);

            }
            System.out.println("finihs");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
