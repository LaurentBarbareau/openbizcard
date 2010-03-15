/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jen.scanner.ui;

import com.jen.scanner.ui.ScannerView.PopClickListener;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author Jenchote
 */
public class OCRTextFrame extends JFrame{
    private static OCRTextFrame ocr;
    private final int WIN_WIDTH = 450;
    private final int WIN_HEIGHT = 400;
    private JTextArea ocrTxt;
    private OCRTextFrame(){
        JFrame textWin = new JFrame("OCR-Read Text");
        textWin.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        textWin.setLocation((dim.width/2)-(WIN_WIDTH/2), (dim.height/2)-(WIN_HEIGHT/2));

        textWin.setSize(WIN_WIDTH, WIN_HEIGHT);
        Container contentPane = textWin.getContentPane();
        contentPane.setLayout(new BorderLayout());
        ocrTxt = new JTextArea("OCR Running ...");

        // End Oak
        contentPane.add(new JScrollPane(ocrTxt),BorderLayout.CENTER);
        textWin.setVisible(true);
    }

    public JTextArea getTxtArea(){
        return ocrTxt;
    }

    public static OCRTextFrame createTextFrame(){
        if(ocr!=null){
            return ocr;
        }else{
            return new OCRTextFrame();
        }
    }
}
