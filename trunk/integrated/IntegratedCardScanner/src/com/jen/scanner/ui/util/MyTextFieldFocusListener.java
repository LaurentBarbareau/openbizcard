/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jen.scanner.ui.util;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JScrollPane;

/**
 *
 * @author Jenchote
 */
public class MyTextFieldFocusListener implements FocusListener {

    JScrollPane jsc;
    int position;

    public MyTextFieldFocusListener(JScrollPane jsc,int pos){
        this.jsc = jsc;
        this.position = pos;
    }

    @Override
    public void focusGained(FocusEvent e) {
        this.jsc.getHorizontalScrollBar().setValue(position);
    }

    @Override
    public void focusLost(FocusEvent e) {
    }

}
