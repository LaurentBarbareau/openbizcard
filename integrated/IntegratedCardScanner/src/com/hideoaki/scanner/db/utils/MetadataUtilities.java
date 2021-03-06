/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hideoaki.scanner.db.utils;

/**
 *
 * @author hideoaki
 */
 import org.w3c.dom.NamedNodeMap;
 import org.w3c.dom.Node;
    
 public class MetadataUtilities {

 public static void displayMetadata(Node root) {
 displayMetadata(root, 0);
 }

 public static void changeDPI(int dpi)
 {

 }

 static void indent(int level) {
 for (int i = 0; i < level; i++) {
 System.out.print("  ");
 }
 }

 static void displayMetadata(Node node, int level) {
 indent(level); // emit open tag
 System.out.print("<" + node.getNodeName());
 NamedNodeMap map = node.getAttributes();
 if (map != null) { // print attribute values
 int length = map.getLength();
 for (int i = 0; i < length; i++) {
 Node attr = map.item(i);
 System.out.print(" " + attr.getNodeName() +
                  "=\"" + attr.getNodeValue() + "\"");
 }
 }

 Node child = node.getFirstChild();
 if (child != null) {
 System.out.println(">"); // close current tag
 while (child != null) { // emit child tags recursively
 displayMetadata(child, level + 1);
 child = child.getNextSibling();
 }
 indent(level); // emit close tag
 System.out.println("</" + node.getNodeName() + ">");
 } else {
 System.out.println("/>");
 }
 }
 }
