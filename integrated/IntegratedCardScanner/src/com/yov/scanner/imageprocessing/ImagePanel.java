package com.yov.scanner.imageprocessing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.*;

public class ImagePanel extends Component implements MouseListener, MouseMotionListener, ActionListener {

    private BufferedImage displayImage;
    private BufferedImage croppedImage;
    private boolean isMoved;
    private int originalX;
    private int originalY;
    private int clickX, clickY;
    private int dragX, dragY;
    private boolean isClicked, isDragged, isDragging, isFocused, isCropped;
    private int imgWidth;
    private int imgHeight;
    //private JFrame imgWin;
    public JFrame imgWin;
    private JButton confirmBtn;
    private JButton cancelBtn;
    private JButton focusBtn;
    private String imgFileName;

    public ImagePanel(BufferedImage newImage) {
        super();

        displayImage = newImage;
        imgWidth = displayImage.getWidth();
        imgHeight = displayImage.getHeight();
        croppedImage = null;

        originalX = 0;
        originalY = 0;
        isMoved = false;

        clickX = -1;
        clickY = -1;
        dragX = -1;
        dragY = -1;

        isClicked = false;
        isDragged = false;
        isDragging = false;
        isFocused = false;
        isCropped = false;

        addMouseListener(this);
        addMouseMotionListener(this);

        this.setSize(displayImage.getWidth(), displayImage.getHeight());
        this.setMaximumSize(new Dimension(displayImage.getWidth(), displayImage.getHeight()));
        this.setMinimumSize(new Dimension(displayImage.getWidth(), displayImage.getHeight()));
        this.setPreferredSize(new Dimension(displayImage.getWidth(), displayImage.getHeight()));

    }

    public ImagePanel(String newImageFileName) {
        super();

        try {
            displayImage = ImageIO.read(new File(newImageFileName));
        } catch (Exception e) {
            System.out.println("Can not open Image File");
            e.printStackTrace();
        }
        imgWidth = displayImage.getWidth();
        imgHeight = displayImage.getHeight();
        imgFileName = newImageFileName;
        croppedImage = null;

        originalX = 0;
        originalY = 0;
        isMoved = false;

        clickX = -1;
        clickY = -1;
        dragX = -1;
        dragY = -1;

        isClicked = false;
        isDragged = false;
        isDragging = false;
        isFocused = false;
        isCropped = false;

        addMouseListener(this);
        addMouseMotionListener(this);

        this.setSize(displayImage.getWidth(), displayImage.getHeight());
        this.setMaximumSize(new Dimension(displayImage.getWidth(), displayImage.getHeight()));
        this.setMinimumSize(new Dimension(displayImage.getWidth(), displayImage.getHeight()));
        this.setPreferredSize(new Dimension(displayImage.getWidth(), displayImage.getHeight()));
    }

    public ImagePanel() {
        super();
    }

    public void setImage(BufferedImage newImage) {
        displayImage = newImage;
        imgWidth = displayImage.getWidth();
        imgHeight = displayImage.getHeight();
    }

    public BufferedImage getCroppedImage(){
        return croppedImage;
    }

    public boolean isCropped(){
        return isCropped;
    }

    @Override
    public void paint(Graphics g) {
        //super.paint(g);

        if (displayImage != null) {

            if (!isMoved) {
                Point originalLoc = this.getLocation();

                originalX = (int) originalLoc.getX();
                originalY = (int) originalLoc.getY();

                isMoved = true;
            }

            int dispX = 0;
            int dispY = 0;

            int dragWidth = dragX - clickX;
            ;
            int dragHeight = dragY - clickY;

            int paneHeight = this.getHeight();
            int paneWidth = this.getWidth();

            if (!isFocused) {
                if ((paneHeight > imgHeight) && (paneWidth > imgWidth)) {
                    dispX = (int) ((paneWidth / 2.0f) - (imgWidth / 2.0f));
                    dispY = (int) ((paneHeight / 2.0f) - (imgHeight / 2.0f));
                }
            } else {
                if ((paneHeight > 2 * dragHeight) && (paneWidth > 2 * dragWidth)) {
                    dispX = (int) ((paneWidth / 2.0f) - (dragWidth));
                    dispY = (int) ((paneHeight / 2.0f) - (dragHeight));
                }
            }

            this.setLocation(dispX + originalX, dispY + originalY);

            if (!isFocused) {

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

                imgWin.setSize(winWidth, winHeight);

                g.drawImage(displayImage, 0, 0, null);

            } else {

                int winWidth = (2 * dragWidth);
                int winHeight = (2 * dragHeight);

                if ((2 * dragWidth) < 300) {
                    winWidth = Math.max(4 * dragWidth, 200);
                } else {
                    if ((2 * dragWidth) > 600) {
                        winWidth = Math.min((int) (1.2 * (2 * dragWidth)), 750);
                    } else {
                        winWidth = Math.min((int) (1.5 * (2 * dragWidth)), 600);
                    }
                }

                if ((2 * dragHeight) < 200) {
                    winHeight = Math.max(4 * dragHeight, 180);
                } else {
                    if ((2 * dragHeight) > 400) {
                        winHeight = Math.min((int) (1.2 * (2 * dragHeight)), 550);
                    } else {
                        winHeight = Math.min((int) (1.5 * (2 * dragHeight)), 400);
                    }
                }

                imgWin.setSize(winWidth, winHeight);

                g.drawImage(displayImage, 0, 0, 2 * dragWidth, 2 * dragHeight, clickX, clickY, dragX, dragY, null);

                System.out.println("In Focus, winWidth = " + winWidth + ", winHeight = " + winHeight);

                //isFocused = false;
            }


            if (isClicked) {

                System.out.println(" -- Paint { isClicked } --");

                System.out.println("Draw Clicked Oval");
                g.setColor(Color.BLUE);
                g.drawOval(clickX, clickY, 3, 3);
            }

            if (isDragging) {
                //System.out.println("Draw Dragging Rect");
                g.setXORMode(Color.CYAN);

                if (dragWidth >= 0 && dragHeight >= 0) {
                    g.drawRect(clickX, clickY, dragWidth, dragHeight);
                }

                if (dragWidth < 0 && dragHeight >= 0) {
                    g.drawRect(dragX, clickY, -dragWidth, dragHeight);
                }

                if (dragWidth >= 0 && dragHeight < 0) {
                    g.drawRect(clickX, dragY, dragWidth, -dragHeight);
                }

                if (dragWidth < 0 && dragHeight < 0) {
                    g.drawRect(dragX, dragY, -dragWidth, -dragHeight);
                }
            }


            if (isDragged && !isFocused) {

                g.setXORMode(Color.WHITE);

                if (dragWidth >= 0 && dragHeight >= 0) {
                    g.drawRect(clickX, clickY, dragWidth, dragHeight);
                }

                if (dragWidth < 0 && dragHeight >= 0) {
                    g.drawRect(dragX, clickY, -dragWidth, dragHeight);
                }

                if (dragWidth >= 0 && dragHeight < 0) {
                    g.drawRect(clickX, dragY, dragWidth, -dragHeight);
                }

                if (dragWidth < 0 && dragHeight < 0) {
                    g.drawRect(dragX, dragY, -dragWidth, -dragHeight);
                }
            }
        }

    }

    public synchronized void cropImage() {
        //System.out.println("Start ImagePanel's main method");

        isCropped = false;
        croppedImage = null;

        int winWidth = 0, winHeight = 0;

        imgWin = new JFrame("ImagePanel");
        
        Container contentPane = imgWin.getContentPane();
        JPanel mainPane = new JPanel();

        JPanel imgBackPane = new JPanel();
        JPanel buttonPane = new JPanel();

        confirmBtn = new JButton("Confirm");
        cancelBtn = new JButton("Cancel");
        focusBtn = new JButton("Focus");

        imgWidth = displayImage.getWidth();
        imgHeight = displayImage.getHeight();

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

        System.out.println("winWidth = " + winWidth + ", winHeight = " + winHeight);

        imgWin.setSize(winWidth, winHeight);
        imgWin.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        imgWin.setVisible(true);
        imgWin.setAlwaysOnTop(true);

        contentPane.setLayout(new BorderLayout());
        contentPane.add(mainPane, BorderLayout.CENTER);

        mainPane.setLayout(new BorderLayout());
        mainPane.add(imgBackPane, BorderLayout.CENTER);
        mainPane.add(buttonPane, BorderLayout.EAST);

        imgBackPane.setLayout(new BorderLayout());
        imgBackPane.setBackground(new Color(100, 100, 100));
        imgBackPane.add(this, BorderLayout.CENTER);

        confirmBtn.addActionListener(this);
        cancelBtn.addActionListener(this);
        focusBtn.addActionListener(this);

        int topSpace = (int) (0.1f * imgWin.getHeight());
        int inSpace = (int) (0.05f * imgWin.getHeight());
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

        //try{
        //    wait();
        //}catch(Exception e){
        //    e.printStackTrace();
        //}

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        clickX = e.getX();
        clickY = e.getY();
        isClicked = true;

        //isExitedDuringDragging = false;

        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //if(isDragging){
        //	isExitedDuringDragging = false;
        //}
    }

    @Override
    public void mouseExited(MouseEvent e) {

        //System.out.println(" ------- Mouse Exit ------- ");

        if (isDragging) {
            int exitX = e.getX();
            int exitY = e.getY();

            if (exitX < 0) {
                dragX = 0;
            }

            if (exitY < 0) {
                dragY = 0;
            }

            if (exitX >= imgWidth) {
                dragX = imgWidth - 1;
            }

            if (exitY >= imgHeight) {
                dragY = imgHeight - 1;
            }

            //isExitedDuringDragging = true;
            //System.out.println("----------- exited from dragging at (" + e.getX() + ", " + e.getY() + ")");
            repaint();
        }

    }

    @Override
    public void mousePressed(MouseEvent e) {
        clickX = e.getX();
        clickY = e.getY();

        isDragging = true;
        isDragged = false;
        //isExitedDuringDragging = false;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (isDragging) {

            isDragging = false;
            isDragged = true;

            repaint();

            //System.out.println("----------- released from dragging at (" + dragX + ", " + dragY + ")");
        }

    }

    @Override
    public void mouseDragged(MouseEvent e) {

        dragX = e.getX();
        dragY = e.getY();

        isClicked = false;

        if (isDragging) {

            if (dragX >= imgWidth) {
                dragX = imgWidth - 1;
            }

            if (dragY >= imgHeight) {
                dragY = imgHeight - 1;
            }

            if (dragX < 0) {
                dragX = 0;
            }

            if (dragY < 0) {
                dragY = 0;
            }

            repaint();
        }

        //System.out.println("----------- dragged at (" + dragX + ", " + dragY + ")");
    }

    @Override
    public void mouseMoved(MouseEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public synchronized void actionPerformed(ActionEvent e) {
        if (e.getSource() == confirmBtn) {

            if (isDragged) {

                int n = JOptionPane.showConfirmDialog(
                        new JFrame(),
                        "Do you want to crop this image?",
                        "Cropping Confirmation",
                        JOptionPane.YES_NO_OPTION);

                if (n == 0) {
                    croppedImage = null;

                    int dragWidth = dragX - clickX;
                    int dragHeight = dragY - clickY;

                    if (dragWidth >= 0 && dragHeight >= 0) {
                        croppedImage = displayImage.getSubimage(clickX, clickY,
                                dragWidth, dragHeight);
                    }

                    if (dragWidth < 0 && dragHeight >= 0) {
                        croppedImage = displayImage.getSubimage(dragX, clickY,
                                -dragWidth, dragHeight);
                    }

                    if (dragWidth >= 0 && dragHeight < 0) {
                        croppedImage = displayImage.getSubimage(clickX, dragY,
                                dragWidth, -dragHeight);
                    }

                    if (dragWidth < 0 && dragHeight < 0) {
                        croppedImage = displayImage.getSubimage(dragX, dragY,
                                -dragWidth, -dragHeight);
                    }

                    if (croppedImage != null) {
                        //displayImage = croppedImage.getSubimage(0, 0,
                        //		croppedImage.getWidth(), croppedImage
                        //				.getHeight());
                        //imgWidth = displayImage.getWidth();
                        //imgHeight = displayImage.getHeight();


                        try {
                            ImageIO.write(
                                    croppedImage,
                                    "jpg",
                                    new File(imgFileName));

                            isCropped = true;
                            
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        imgWin.dispose();
                        //notify();
                        //System.exit(0);
                    }

                    //isClicked = false;
                    //isDragging = false;
                    //isDragged = false;
                    //isFocused = false;

                    //imgWin.setVisible(true);

                } else {

                    imgWin.setVisible(true);

                }
            }
        }

        if (e.getSource() == cancelBtn) {

            if (isClicked) {
                isClicked = false;
            }

            if (isDragging) {
                isDragging = false;
            }

            if (isDragged && !isFocused) {
                isDragged = false;
            }

            if (isFocused) {
                isFocused = false;
            }

            imgWin.setVisible(true);
        }

        if (e.getSource() == focusBtn) {

            if (isDragged) {
                isFocused = true;
            }

            isClicked = false;
            isDragging = false;

            imgWin.setVisible(true);
        }

    }
}
