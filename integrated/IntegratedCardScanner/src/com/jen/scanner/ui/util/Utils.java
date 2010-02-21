/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jen.scanner.ui.util;

import com.hideoaki.scanner.db.manager.CardLocalManager;
import com.hideoaki.scanner.db.model.Card;
import com.hideoaki.scanner.db.utils.ScannerDBException;
import com.jen.scanner.ui.ScannerView;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Jenchote
 */
public class Utils {

    public final static String csv = "csv";
    public final static String jpg = "jpg";
    public final static String jpeg = "jpeg";
    public final static String zip = "zip";

    /*
     * Get the extension of a file.
     */
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

    public static boolean checkFirstName(Card c,String alert){
        boolean chk = false;
        if(c.getFirstName()!=null && !c.getFirstName().equals("")) chk = true;
        else{
            org.jdesktop.application.ResourceMap myResourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(ScannerView.class);
            chk = false;
            JOptionPane.showMessageDialog(null,myResourceMap.getString(alert),"information", JOptionPane.INFORMATION_MESSAGE);
        }
        return chk;
    }

    public static ArrayList<Card> quickSearchCard(String str,ArrayList<Card> allCard){
        ArrayList<Card> resultCards = new ArrayList<Card>();
        String[] s = str.split("");
        for(Card c : allCard){
            for(int i=1;i<s.length;i++){
                if(isLeadByThai(c.getFirstName(),s[i]))resultCards.add(c);
                else if(isLeadBy(c.getFirstNameE(),s[i]))resultCards.add(c);
            }
        }
        return resultCards;
    }

//    public static ArrayList<Card> quickSearchCardE(String str,ArrayList<Card> allCard){
//        ArrayList<Card> resultCards = new ArrayList<Card>();
//        String[] s = str.split("");
//        for(Card c : allCard){
//            for(int i=1;i<s.length;i++){
//                if(isLeadBy(c.getFirstNameE(),s[i]))resultCards.add(c);
//            }
//        }
//        return resultCards;
//    }

    private static boolean isLeadBy(String s1,String s2){
        return s1.toLowerCase().indexOf(s2.toLowerCase())==0;
    }

    private static boolean isLeadByThai(String s1,String s2){
        int leadIndex = 0;
        if(leadBySara(s1,"เไโใ")) leadIndex = 1;
        return s1.toLowerCase().indexOf(s2.toLowerCase())== leadIndex;
    }

    private static boolean leadBySara(String s1, String s2){
        boolean result = false;
        String[] str = s2.split("");
        for(int i =1;i<str.length;i++){
            if(s1.indexOf(str[i])==0)result = true;
        }
        return result;
    }

    public static ArrayList<Card> searchGenCard(String criteria,ArrayList<Card> allCard){
        ArrayList<Card> resultCards = new ArrayList<Card>();
        int i = 0;
        System.out.println(allCard.size());

        for(Card c : allCard){
           System.out.println(">>>>>>>>>>>>>>>>> gen round "+i);
           i++;
            if(checkIfContains(c.getFirstName(),criteria)){
                resultCards.add(c);
                continue;
            }

            if(checkIfContains(c.getLastName(),criteria)){
                resultCards.add(c);
                continue;
            }

            if(checkIfContains(c.getPosition(),criteria)){
                resultCards.add(c);
                continue;
            }

            if(checkIfContains(c.getEmail(),criteria)){
                resultCards.add(c);
                continue;
            }

            if(checkIfContains(c.getCompany(),criteria)){
                resultCards.add(c);
                continue;
            }

            if(checkIfContains(c.getWebsite(),criteria)){
                resultCards.add(c);
                continue;
            }

            if(checkIfContains(c.getAddress(),criteria)){
                resultCards.add(c);
                continue;
            }

            if(checkIfContains(c.getCity(),criteria)){
                resultCards.add(c);
                continue;
            }

            if(checkIfContains(c.getState(),criteria)){
                resultCards.add(c);
                continue;
            }

            if(checkIfContains(c.getCountry(),criteria)){
                resultCards.add(c);
                continue;
            }

            if(checkIfContains(c.getZip(),criteria)){
                resultCards.add(c);
                continue;
            }

            if(checkIfContains(c.getTelephone(),criteria)){
                resultCards.add(c);
                continue;
            }

            if(checkIfContains(c.getFax(),criteria)){
                resultCards.add(c);
                continue;
            }

            if(checkIfContains(c.getMobile(),criteria)){
                resultCards.add(c);
                continue;
            }

            if(checkIfContains(c.getNote(),criteria)){
                resultCards.add(c);
                continue;
            }

            if(checkIfContains(c.getFirstNameE(),criteria)){
                resultCards.add(c);
                continue;
            }

            if(checkIfContains(c.getLastNameE(),criteria)){
                resultCards.add(c);
                continue;
            }

            if(checkIfContains(c.getPositionE(),criteria)){
                resultCards.add(c);
                continue;
            }

            if(checkIfContains(c.getCompanyE(),criteria)){
                resultCards.add(c);
                continue;
            }

            if(checkIfContains(c.getAddressE(),criteria)){
                resultCards.add(c);
                continue;
            }

            if(checkIfContains(c.getCityE(),criteria)){
                resultCards.add(c);
                continue;
            }

            if(checkIfContains(c.getStateE(),criteria)){
                resultCards.add(c);
                continue;
            }

            if(checkIfContains(c.getCountryE(),criteria)){
                resultCards.add(c);
                continue;
            }

            if(checkIfContains(c.getZipE(),criteria)){
                resultCards.add(c);
                continue;
            }

            if(checkIfContains(c.getTelephoneE(),criteria)){
                resultCards.add(c);
                continue;
            }

            if(checkIfContains(c.getFaxE(),criteria)){
                resultCards.add(c);
                continue;
            }

            if(checkIfContains(c.getMobileE(),criteria)){
                resultCards.add(c);
                continue;
            }

            if(checkIfContains(c.getNoteE(),criteria)){
                resultCards.add(c);
                continue;
            }
        }
        return resultCards;
    }

    public static ArrayList<Card> searchCard(Card criteria,ArrayList<Card> allCard){
        ArrayList<Card> resultCards = new ArrayList<Card>();

        int i = 0;
        boolean correct;
        System.out.println(allCard.size());

        for(Card c : allCard){
           System.out.println(">>>>>>>>>>>>>>>>> round "+i);
           i++;
           correct = true;
            if(!checkIfContains(c.getFirstName(),criteria.getFirstName())){
                correct = false;
            }

            if(!checkIfContains(c.getLastName(),criteria.getLastName())){
                correct = false;
            }

            if(!checkIfContains(c.getPosition(),criteria.getPosition())){
                correct = false;
            }

            if(!checkIfContains(c.getEmail(),criteria.getEmail())){
                correct = false;
            }

            if(!checkIfContains(c.getCompany(),criteria.getCompany())){
                correct = false;
            }

            if(!checkIfContains(c.getWebsite(),criteria.getWebsite())){
                correct = false;
            }

            if(!checkIfContains(c.getAddress(),criteria.getAddress())){
                correct = false;
            }

            if(!checkIfContains(c.getCity(),criteria.getCity())){
                correct = false;
            }

            if(!checkIfContains(c.getState(),criteria.getState())){
                correct = false;
            }

            if(!checkIfContains(c.getCountry(),criteria.getCountry())){
                correct = false;
            }

            if(!checkIfContains(c.getZip(),criteria.getZip())){
                correct = false;
            }

            if(!checkIfContains(c.getTelephone(),criteria.getTelephone())){
                correct = false;
            }

            if(!checkIfContains(c.getFax(),criteria.getFax())){
                correct = false;
            }

            if(!checkIfContains(c.getMobile(),criteria.getMobile())){
                correct = false;
            }

            if(!checkIfContains(c.getNote(),criteria.getNote())){
                correct = false;
            }

            if(!checkIfContains(c.getFirstNameE(),criteria.getFirstNameE())){
                correct = false;
            }

            if(!checkIfContains(c.getLastNameE(),criteria.getLastNameE())){
                correct = false;
            }

            if(!checkIfContains(c.getPositionE(),criteria.getPositionE())){
                correct = false;
            }

            if(!checkIfContains(c.getCompanyE(),criteria.getCompanyE())){
                correct = false;
            }

            if(!checkIfContains(c.getAddressE(),criteria.getAddressE())){
                correct = false;
            }

            if(!checkIfContains(c.getCityE(),criteria.getCityE())){
                correct = false;
            }

            if(!checkIfContains(c.getStateE(),criteria.getStateE())){
                correct = false;
            }

            if(!checkIfContains(c.getCountryE(),criteria.getCountryE())){
                correct = false;
            }

            if(!checkIfContains(c.getZipE(),criteria.getZipE())){
                correct = false;
            }

            if(!checkIfContains(c.getTelephoneE(),criteria.getTelephoneE())){
                correct = false;
            }

            if(!checkIfContains(c.getFaxE(),criteria.getFaxE())){
                correct = false;
            }

            if(!checkIfContains(c.getMobileE(),criteria.getMobileE())){
                correct = false;
            }

            if(!checkIfContains(c.getNoteE(),criteria.getNoteE())){
                correct = false;
            }
            
           System.out.println(correct);
           if(correct) resultCards.add(c);
        }
        return resultCards;
    }

    private static boolean checkIfContains(String s1,String s2){
//        System.out.println("param = "+s1+" , "+s2);
        if(s1!=null && s2!=null) return s1.toLowerCase().indexOf(s2.toLowerCase())>=0;
        else return false;
    }


    public static void main (String[] args){
        try {
            ArrayList<Card> test = CardLocalManager.loadLocalCard("C:\\netbeanProject\\integrated\\defaultcard.csv");
//            test = Utils.quickSearchCard("abc", test);
//            System.out.println(test.size());
        String[] s = "pqr".split("");
            for(int i=0;i<s.length;i++){
               System.out.println(s[i]);
            }

        } catch (ScannerDBException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}