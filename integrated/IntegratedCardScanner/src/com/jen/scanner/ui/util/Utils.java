/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jen.scanner.ui.util;

import com.hideoaki.scanner.db.manager.CardLocalManager;
import com.hideoaki.scanner.db.model.Card;
import com.hideoaki.scanner.db.utils.ScannerDBException;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jenchote
 */
public class Utils {

    public final static String csv = "csv";
    public final static String jpg = "jpg";
    public final static String jpeg = "jpeg";

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

    public static ArrayList<Card> quickSearchCard(char c0,char c1,char c2,ArrayList<Card> allCard){
        ArrayList<Card> resultCards = new ArrayList<Card>();
        boolean correct = true;
        for(Card c : allCard){
            if(!checkIfContains(c.getFirstName(),c0+""))correct = false;
            if(!checkIfContains(c.getFirstName(),c1+""))correct = false;
            if(!checkIfContains(c.getFirstName(),c2+""))correct = false;
            if(correct)resultCards.add(c);
        }
        return resultCards;
    }

    public static ArrayList<Card> quickSearchCardE(char c0,char c1,char c2,ArrayList<Card> allCard){
        ArrayList<Card> resultCards = new ArrayList<Card>();
        boolean correct = true;
        for(Card c : allCard){
            if(!checkIfContains(c.getFirstNameE(),c0+""))correct = false;
            if(!checkIfContains(c.getFirstNameE(),c1+""))correct = false;
            if(!checkIfContains(c.getFirstNameE(),c2+""))correct = false;
            if(correct)resultCards.add(c);
        }
        return resultCards;
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
        System.out.println("param = "+s1+" , "+s2);
        if(s1!=null && s2!=null) return s1.indexOf(s2)>=0;
        else return false;
    }

    public static void main (String[] args){
        try {
            ArrayList<Card> test = CardLocalManager.loadLocalCard("C:\\netbeanProject\\integrated\\defaultcard.csv");
            Card c = new Card();
            c.setFirstName("เจน");
            c.setLastName("");
            test = searchCard(c, test);
            System.out.println(">>>> size is "+test.size());
            for(Card card:test){
                System.out.println(card.getFirstName());
            }
        } catch (ScannerDBException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}