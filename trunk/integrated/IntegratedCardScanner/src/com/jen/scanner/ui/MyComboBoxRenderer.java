/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jen.scanner.ui;

/**
 *
 * @author Jenchote
 */
  public class MyComboBoxRenderer extends javax.swing.JComboBox implements javax.swing.table.TableCellRenderer {
        public MyComboBoxRenderer(String[] items) {
            super(items);
        }

        public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                super.setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }

            // Select the current value
            setSelectedItem(value);
            return this;
        }
  }