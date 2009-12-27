/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jen.scanner.ui.util;

import java.io.File;
import javax.swing.filechooser.*;

public class JPGFileFilter extends FileFilter {

    //Accept all directories and all gif, jpg, tiff, or png files.
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = Utils.getExtension(f);
        if (extension != null) {
            if (extension.toLowerCase().equals(Utils.jpg)||extension.toLowerCase().equals(Utils.jpeg))  {
                    return true;
            } else {
                return false;
            }
        }

        return false;
    }

    //The description of this filter
    public String getDescription() {
        return "jpg or jpeg extension";
    }
}
