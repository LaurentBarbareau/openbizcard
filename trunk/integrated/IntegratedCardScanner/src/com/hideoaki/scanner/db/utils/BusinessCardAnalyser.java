/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hideoaki.scanner.db.utils;

import com.hideoaki.scanner.db.model.Card;

/**
 *
 * @author hideoaki
 */
public class BusinessCardAnalyser {

    public static Card analyse(String str) {
        Card c = new Card();
        // Split line
        String[] lines = str.split("\r\n|\r|\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            // The line have numeric put it on mobile
            // Finish line with don't know the meaning of that line.. then Split word
            String[] words = line.split(" ");
            for (int j = 0; j < words.length; j++) {
                String word = words[j];
                // the word has web address
                if (isWebAddr(word)) {
                    c.setWebsite(word);
                    continue;
                }
                if (isEmail(word)) {
                    c.setEmail(word);
                    continue;
                }
                if (hasNumeric(word)) {
                    c.setMobile(word);
                    continue;
                }
            }
            // line
            if(line.indexOf("บริษัท") >=0){
                c.setCompany(line);
                continue;
            }
            if(line.toLowerCase().indexOf("company") >=0){
                c.setCompanyE(line);
                continue;
            }
        }
        return c;
    }

    public static void main(String[] args) {
       Card c =  analyse(s);
       System.out.println(c);
    }

    private static boolean isWebAddr(String word) {
        if (word.indexOf("www.") >= 0) {
            return true;
        }
        return false;
    }

    private static boolean isEmail(String word) {
        if (word.indexOf("@") >= 0) {
            return true;
        }
        return false;
    }

    private static final boolean hasNumeric(final String s) {
        for (int x = 0; x < s.length(); x++) {
            final char c = s.charAt(x);
            if ((c >= '0') && (c <= '9')) {
                return true;  // 0 - 9
            }
        }
        return false; // valid
    }
    static String s = "กฤษฎา เฉลิมสุข \n Krissada Chalermsook \n Project Leader \r\n 080-5511-559 krissada@crie.co.th";
}
