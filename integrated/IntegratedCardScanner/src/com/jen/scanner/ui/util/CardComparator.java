/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jen.scanner.ui.util;

import com.hideoaki.scanner.db.model.Card;
import java.text.Collator;
import java.text.RuleBasedCollator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

/**
 *
 * @author Jenchote
 */
public class CardComparator implements Comparator{
    
    RuleBasedCollator rule = (RuleBasedCollator)Collator.getInstance(new Locale("th", "TH", ""));

    public int compare(Object o1, Object o2) {
        Card c1 = (Card)o1;
        Card c2 = (Card)o2;

        int result = 0;
        int i = 0;
        
        while(i<4){
            result = compareAt(c1,c2,i);
            i++;
        }

        return result;
    }

    private int compareAt(Card c1,Card c2,int index){
        int res = 0;
            switch(index){
                case 0 : res = rule.compare(replaceNull(c1.getFirstName()), replaceNull(c2.getFirstName())); break;
                case 1 : res = rule.compare(replaceNull(c1.getLastName()),replaceNull( c2.getLastName())); break;
                case 2 : res = rule.compare(replaceNull(c1.getCountry()), replaceNull(c2.getCountry())); break;
                case 3 : res = rule.compare(replaceNull(c1.getZip()), replaceNull(c2.getZip())); break;
                default: res = 0; break;
            }
        
        return res;
    }

    private String replaceNull(String s){
        if(s == null) s ="";
        return s;
    }
   public static void main (String[] args){
        Card c1 = new Card();
        Card c2 = new Card();

        c1.setFirstName("aaa");
        c2.setFirstName("aaa");

        ArrayList<Card> tree = new ArrayList<Card>();
        tree.add(c1);
        tree.add(c2);
        Collections.sort(tree,new CardComparator());
     
      for(Card c : tree){
        System.out.println(c.getFirstName()+" "+c.getLastName());
      }
      System.out.println("test".indexOf(""));
   }
}
