/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jen.scanner.ui.util;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Jenchote
 */
public class FocusToNextTA extends AbstractAction {

    JTextArea ta;
    JTextField tf;

    public FocusToNextTA(JTextArea ta){
        this.ta = ta;
    }
    public FocusToNextTA(JTextField tf){
        this.tf = tf;
    }

    public void actionPerformed(ActionEvent e) {
        if(ta!=null)ta.requestFocusInWindow();
        if(tf!=null)tf.requestFocusInWindow();
    }
}
