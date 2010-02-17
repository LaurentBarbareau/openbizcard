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
        result = compareAt(c1,c2,0);
        if(result==0)result = compareAt(c1,c2,1);
        if(result==0)result = compareAt(c1,c2,2);
        if(result==0)result = compareAt(c1,c2,3);

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
        Card c3 = new Card();
        Card c4 = new Card();

        c1.setFirstName("andrew");
        c4.setFirstName("เจนโชติ");
        c3.setFirstName("กอๆ");
        c2.setFirstName("aaa");

        ArrayList<Card> tree = new ArrayList<Card>();
        tree.add(c1);
        tree.add(c2);
        tree.add(c3);
        tree.add(c4);
        Collections.sort(tree,new CardComparator());
     
      for(Card c : tree){
        System.out.println(c.getFirstName()+" "+c.getLastName());
      }
   }
}
