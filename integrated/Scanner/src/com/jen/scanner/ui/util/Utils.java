/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jen.scanner.ui.util;

import java.io.File;

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
}