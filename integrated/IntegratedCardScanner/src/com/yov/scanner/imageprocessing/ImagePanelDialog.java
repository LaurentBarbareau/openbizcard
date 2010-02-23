package com.yov.scanner.imageprocessing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.image.BufferedImage;

public class ImagePanelDialog extends JDialog implements ActionListener {

    // private JFrame imgWin;
    private JButton confirmBtn;
    private JButton cancelBtn;
    private JButton focusBtn;
    private ImagePanel imgPane;
    private int imgWidth;
    private int imgHeight;
    private int winWidth;
    private int winHeight;

    private static Font font;

    public ImagePanelDialog(String imageFileName) {
        super();
        imgPane = new ImagePanel(imageFileName);
    }

    public void init() {
        // ImagePanel testImgPane = new
        // ImagePanel("C:\\Users\\Yov\\Documents\\scannedImage0.jpg");
        // testImgPane.cropImage();

        // if(testImgPane.isCropped()){
        // System.out.println(" *****  Image is Cropped  *****");
        // }else{
        // System.out.println(" *****  Image is NOT Cropped  *****");
        // }

        // imgWin = new JFrame("Image Panel");

        this.setModal(true);
        // imgWin.setModalExclusionType(Dialog.ModalExclusionType.NO_EXCLUDE);

        Container contentPane = this.getContentPane();
        JPanel mainPane = new JPanel();

        JPanel imgBackPane = new JPanel();
        JPanel buttonPane = new JPanel();

        confirmBtn = new JButton();
        cancelBtn = new JButton();
        focusBtn = new JButton();

        if(font!=null)setDisplayFont(font);
        setAllText("Image Panel Dialog", "Confirm", "Cancel", "Focus");

        imgWidth = imgPane.getImageWidth();
        imgHeight = imgPane.getImageHeight();

        if (imgWidth < 300) {
            winWidth = 2 * imgWidth;
        } else {
            if (imgWidth > 600) {
                winWidth = Math.min((int) (1.2 * imgWidth), 750);
            } else {
                winWidth = Math.min((int) (1.5 * imgWidth), 600);
            }
        }

        if (imgHeight < 200) {
            winHeight = 2 * imgHeight;
        } else {
            if (imgHeight > 400) {
                winHeight = Math.min((int) (1.2 * imgHeight), 550);
            } else {
                winHeight = Math.min((int) (1.5 * imgHeight), 400);
            }
        }

        System.out.println("winWidth = " + winWidth + ", winHeight = "
                + winHeight);

        this.setSize(winWidth, winHeight);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // this.setVisible(true);
        this.setAlwaysOnTop(true);

        contentPane.setLayout(new BorderLayout());
        contentPane.add(mainPane, BorderLayout.CENTER);

        mainPane.setLayout(new BorderLayout());
        mainPane.add(imgBackPane, BorderLayout.CENTER);
        mainPane.add(buttonPane, BorderLayout.EAST);

        imgBackPane.setLayout(new BorderLayout());
        imgBackPane.setBackground(new Color(100, 100, 100));
        imgBackPane.add(imgPane, BorderLayout.CENTER);

        confirmBtn.addActionListener(this);
        cancelBtn.addActionListener(this);
        focusBtn.addActionListener(this);

        int topSpace = (int) (0.1f * this.getHeight());
        int inSpace = (int) (0.05f * this.getHeight());
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.Y_AXIS));
        buttonPane.add(Box.createRigidArea(new Dimension(1, topSpace)));
        confirmBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPane.add(confirmBtn);
        buttonPane.add(Box.createRigidArea(new Dimension(1, inSpace)));
        cancelBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPane.add(cancelBtn);
        buttonPane.add(Box.createRigidArea(new Dimension(1, inSpace)));
        focusBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPane.add(focusBtn);
    }

    private void setButtonFunction() {
        if (!imgPane.isDragged()) {
            confirmBtn.setEnabled(false);
        } else {
            confirmBtn.setEnabled(true);
        }


    }

    public static void setDefaultFont(Font f){
        font = f;
    }

    public void setDisplayFont(Font f){
        this.setFont(f);
        confirmBtn.setFont(f);
        cancelBtn.setFont(f);
        focusBtn.setFont(f);
    }

    public void setAllText(String title, String confirm, String cancel, String focus){
        this.setTitle(title);
        confirmBtn.setText(confirm);
        cancelBtn.setText(cancel);
        focusBtn.setText(focus);
    }

    private void resize() {
        if (!imgPane.isFocused()) {
            int winWidth = imgWidth;
            int winHeight = imgHeight;

            if (imgWidth < 300) {
                winWidth = Math.max(2 * imgWidth, 200);
            } else {
                if (imgWidth > 600) {
                    winWidth = Math.min((int) (1.2 * imgWidth), 750);
                } else {
                    winWidth = Math.min((int) (1.5 * imgWidth), 600);
                }
            }

            if (imgHeight < 200) {
                winHeight = Math.max(2 * imgHeight, 180);
            } else {
                if (imgHeight > 400) {
                    winHeight = Math.min((int) (1.2 * imgHeight), 550);
                } else {
                    winHeight = Math.min((int) (1.5 * imgHeight), 400);
                }
            }

            this.setSize(winWidth, winHeight);

        } else {

            int imgDragWidthX2 = imgPane.getDragWidth() * 2;
            int imgDragHeightX2 = imgPane.getHeight() * 2;
            int winWidth = imgDragWidthX2;
            int winHeight = imgDragHeightX2;

            if (imgDragWidthX2 < 300) {
                winWidth = Math.max(2 * imgDragWidthX2, 200);
            } else {
                if (imgDragWidthX2 > 600) {
                    winWidth = Math.min((int) (1.2 * imgDragWidthX2), 750);
                } else {
                    winWidth = Math.min((int) (1.5 * imgDragWidthX2), 600);
                }
            }

            if (imgDragHeightX2 < 200) {
                winHeight = Math.max(2 * imgDragHeightX2, 180);
            } else {
                if (imgDragHeightX2 > 400) {
                    winHeight = Math.min((int) (1.2 * imgDragHeightX2), 550);
                } else {
                    winHeight = Math.min((int) (1.5 * imgDragHeightX2), 400);
                }
            }

            this.setSize(winWidth, winHeight);

        }
    }

    @Override
    public synchronized void actionPerformed(ActionEvent e) {

        if (e.getSource() == confirmBtn) {
            System.out.println("Confirm is clicked");

            if (imgPane.isDragged()) {

                int n = JOptionPane.showConfirmDialog(this,
                        "Do you want to crop this image?",
                        "Cropping Confirmation", JOptionPane.YES_NO_OPTION);

                if (n == 0) {

                    System.out.println("Yes is Selected");

                    imgPane.cropImage();

                    this.dispose();

                } else {

                    // System.out.println("No is selected");
                    this.setVisible(true);

                }

            }

        }

        if (e.getSource() == cancelBtn) {
            System.out.println("Cancel is clicked");

            if (imgPane.isClicked()) {
                imgPane.setIsClicked(false);
            }

            if (imgPane.isDragging()) {
                imgPane.setIsDragging(false);
            }

            if (imgPane.isDragged() && !imgPane.isFocused()) {
                imgPane.setIsDragged(false);
            }

            if (imgPane.isFocused()) {
                imgPane.setIsFocused(false);
            }

            this.setVisible(true);

        }

        if (e.getSource() == focusBtn) {
            System.out.println("Focus is clicked");

            if (imgPane.isDragged()) {
                imgPane.setIsFocused(true);
            }

            imgPane.setIsClicked(false);
            imgPane.setIsDragging(false);

            this.setVisible(true);

        }

    }

    public boolean isImageCropped() {
        return imgPane.isCropped();
    }

    public BufferedImage getCroppedImage(){
        return imgPane.getCroppedImage();
    }
}
