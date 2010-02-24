/*
 * ScannerView.java
 */
package com.jen.scanner.ui;

import com.hideoaki.scanner.db.manager.CardLocalManager;
import com.hideoaki.scanner.db.model.Card;
import com.hideoaki.scanner.db.utils.ScannerDBException;
import com.hideoaki.scanner.db.utils.SendEmailUtil;
import com.hideoaki.scanner.db.utils.ZipUtils;
import com.jen.scanner.ui.util.CardComparator;
import com.jen.scanner.ui.util.FocusToNextTA;
import com.jen.scanner.ui.util.JPGFileFilter;
import com.jen.scanner.ui.util.MyTextFieldFocusListener;
import com.jen.scanner.ui.util.Utils;
import com.jen.scanner.ui.util.ZipFileFilter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.util.Hashtable;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import com.jen.scanner.ui.util.XTableColumnModel;
// From Yov's part
import com.yov.scanner.imageprocessing.CardScanner;
import com.yov.scanner.imageprocessing.BusinessCard;
import com.yov.scanner.imageprocessing.ImagePanelDialog;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Iterator;
import javax.swing.JTextArea;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

/**
 * The application's main frame.
 */
public class ScannerView extends FrameView {

    org.jdesktop.application.ResourceMap myResourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(ScannerView.class);

    public ScannerView(SingleFrameApplication app) {
        super(app);

        setDefaultFont();
        initComponents();
        menuTab.setEnabledAt(RESULT_TAB, false);
        saveBtnT3.setEnabled(false);
        setFocusListener();

        try {
            localCardList = CardLocalManager.loadLocalCard(defaultcard.getAbsolutePath());
        } catch (ScannerDBException ex) {
            Logger.getLogger(ScannerView.class.getName()).log(Level.SEVERE, null, ex);
        }
        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();

        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
//                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
//                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
//        statusAnimationLabel.setIcon(idleIcon);
//        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
//                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
//                    progressBar.setVisible(true);
//                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
//                    statusAnimationLabel.setIcon(idleIcon);
//                    progressBar.setVisible(false);
//                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
//                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
//                    progressBar.setVisible(true);
//                    progressBar.setIndeterminate(false);
//                    progressBar.setValue(value);
                }
            }
        });

        // Yov's part: init variables
        scannedImage = null;
        resultImage = null;
        scannedBCard = null;
        scannedBCardBack = null;
        resultBCard = null;
        resultBCardBack = null;
        // ! Should be changed to scanned image folder
        //scannedImageFileName = System.getProperty("user.home")+"\\My Documents\\scannedImage";
        scannedImageFileName = "";
        scannedImageFileNameBack = "";

        File curDir = new File(".");
        File imgFolder;

        try {
            imgFolder = new File("." + File.separator + "cardImages" + File.separator);
            if (!imgFolder.isDirectory()) {
                imgFolder.mkdir();
            }

            scannedImageFileName = curDir.getCanonicalPath() + File.separator + "cardImages" + File.separator + "scannedBCard";
            scannedImageFileNameBack = curDir.getCanonicalPath() + File.separator + "cardImages" + File.separator + "scannedBCardBack";
        } catch (Exception e) {
            e.printStackTrace();

            try {
                scannedImageFileName = curDir.getCanonicalPath() + File.separator + "cardImages" + File.separator + "scannedBCard";
                scannedImageFileNameBack = curDir.getCanonicalPath() + File.separator + "cardImages" + File.separator + "scannedBCardBack";
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
//        uncomment this
        bcScanner = new CardScanner(scannedImageFileName);

        isFrontSelected = true;
        isFrontSelectedResult = true;
        frontSideRdT1.setSelected(true);
        backSideRdT1.setSelected(false);
        frontSideRdT3.setSelected(true);
        backSideRdT3.setSelected(false);
        scannerTxtT1.setEditable(false);

        frontLbT1.setHorizontalAlignment(JLabel.CENTER);
        backLbT1.setHorizontalAlignment(JLabel.CENTER);
        frontLbT3.setHorizontalAlignment(JLabel.CENTER);
        backLbT3.setHorizontalAlignment(JLabel.CENTER);

        // State of GUI
        frontUIState = STATE_NO_IMAGE;
        backUIState = STATE_NO_IMAGE;
        setButtonsState(frontUIState);

        frontUIStateResult = STATE_NO_IMAGE;
        backUIStateResult = STATE_NO_IMAGE;
        setButtonsStateResult(frontUIStateResult);

        scannerTxtT1.setText(bcScanner.getName());

        isBrowsedFront = false;
        isBrowsedBack = false;
        isBrowsedFrontResult = false;
        isBrowsedBackResult = false;
    }

    public void showFirstTab() {
        menuTab.setSelectedIndex(SCAN_TAB);
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = ScannerApp.getApplication().getMainFrame();
            aboutBox = new ScannerAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        ScannerApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        mainPanel = new javax.swing.JPanel();
        menuTab = new javax.swing.JTabbedPane();
        scannerTab = new javax.swing.JPanel();
        upLeftScrollPaneT1 = new javax.swing.JScrollPane();
        upLeftT1 = new javax.swing.JPanel();
        nameLbT1 = new javax.swing.JLabel();
        titleLbT1 = new javax.swing.JLabel();
        companyLbT1 = new javax.swing.JLabel();
        disLbT1 = new javax.swing.JLabel();
        codeLbT1 = new javax.swing.JLabel();
        mobileLbT1 = new javax.swing.JLabel();
        faxLbT1 = new javax.swing.JLabel();
        adsLbT1 = new javax.swing.JLabel();
        lastnameLbT1 = new javax.swing.JLabel();
        emailLbT1 = new javax.swing.JLabel();
        webLbT1 = new javax.swing.JLabel();
        subDisLbT1 = new javax.swing.JLabel();
        provinceLbT1 = new javax.swing.JLabel();
        phoneLbT1 = new javax.swing.JLabel();
        noteLbT1 = new javax.swing.JLabel();
        countryLbT1 = new javax.swing.JLabel();
        nameTfT1 = new javax.swing.JTextField();
        titleTfT1 = new javax.swing.JTextField();
        companyTfT1 = new javax.swing.JTextField();
        disTfT1 = new javax.swing.JTextField();
        codeTfT1 = new javax.swing.JTextField();
        mobileTfT1 = new javax.swing.JTextField();
        faxTfT1 = new javax.swing.JTextField();
        lastnameTfT1 = new javax.swing.JTextField();
        emailTfT1 = new javax.swing.JTextField();
        webTfT1 = new javax.swing.JTextField();
        subDisTfT1 = new javax.swing.JTextField();
        provinceTfT1 = new javax.swing.JTextField();
        phoneTfT1 = new javax.swing.JTextField();
        countryTfT1 = new javax.swing.JTextField();
        adsPanelT1 = new javax.swing.JPanel();
        adsScrT1 = new javax.swing.JScrollPane();
        adsTaT1 = new javax.swing.JTextArea();
        notePanelT1 = new javax.swing.JPanel();
        noteScrT1 = new javax.swing.JScrollPane();
        noteTaT1 = new javax.swing.JTextArea();
        nameLbTE1 = new javax.swing.JLabel();
        nameTfTE1 = new javax.swing.JTextField();
        lastnameLbTE1 = new javax.swing.JLabel();
        lastnameTfTE1 = new javax.swing.JTextField();
        titleLbTE1 = new javax.swing.JLabel();
        titleTfTE1 = new javax.swing.JTextField();
        companyLbTE1 = new javax.swing.JLabel();
        companyTfTE1 = new javax.swing.JTextField();
        disLbTE1 = new javax.swing.JLabel();
        codeLbTE1 = new javax.swing.JLabel();
        mobileLbTE1 = new javax.swing.JLabel();
        faxLbTE1 = new javax.swing.JLabel();
        adsLbTE1 = new javax.swing.JLabel();
        disTfTE1 = new javax.swing.JTextField();
        codeTfTE1 = new javax.swing.JTextField();
        mobileTfTE1 = new javax.swing.JTextField();
        faxTfTE1 = new javax.swing.JTextField();
        adsPanelTE1 = new javax.swing.JPanel();
        adsScrTE1 = new javax.swing.JScrollPane();
        adsTaTE1 = new javax.swing.JTextArea();
        subDisLbTE1 = new javax.swing.JLabel();
        provinceLbTE1 = new javax.swing.JLabel();
        phoneLbTE1 = new javax.swing.JLabel();
        countryLbTE1 = new javax.swing.JLabel();
        noteLbTE1 = new javax.swing.JLabel();
        subDisTfTE1 = new javax.swing.JTextField();
        provinceTfTE1 = new javax.swing.JTextField();
        phoneTfTE1 = new javax.swing.JTextField();
        countryTfTE1 = new javax.swing.JTextField();
        notePanelTE1 = new javax.swing.JPanel();
        noteScrTE1 = new javax.swing.JScrollPane();
        noteTaTE1 = new javax.swing.JTextArea();
        blankLb0 = new javax.swing.JLabel("  ");
        blankLb1 = new javax.swing.JLabel("  ");
        blankLb2 = new javax.swing.JLabel("  ");
        upRightT1 = new javax.swing.JPanel();
        scannerPanel = new javax.swing.JPanel();
        scannerLbT1 = new javax.swing.JLabel();
        scannerBtnT1 = new javax.swing.JButton();
        scannerTxtT1 = new javax.swing.JTextField();
        doubleSideBtnT1 = new javax.swing.JButton();
        blackWhiteBtnT1 = new javax.swing.JButton();
        autoCropBtnT1 = new javax.swing.JButton();
        readCardBtnT1 = new javax.swing.JButton();
        rotateBtnT1 = new javax.swing.JButton();
        emailBtnT1 = new javax.swing.JButton();
        undoBtnT1 = new javax.swing.JButton();
        confirmBtnT1 = new javax.swing.JButton();
        brightPanelT1 = new javax.swing.JPanel();
        brightLbT1 = new javax.swing.JLabel();
        brightSldT1 = new javax.swing.JSlider();
        scanBtn = new javax.swing.JButton();
        saveBtnT1 = new javax.swing.JButton();
        selSidePanelT1 = new javax.swing.JPanel();
        sideLbT1 = new javax.swing.JLabel();
        frontSideRdT1 = new javax.swing.JRadioButton();
        backSideRdT1 = new javax.swing.JRadioButton();
        low = new javax.swing.JPanel();
        lowLeftT1 = new javax.swing.JPanel();
        frontPanelT1 = new javax.swing.JPanel();
        frontTfT1 = new javax.swing.JTextField();
        frontBtnT1 = new javax.swing.JButton();
        frontLbT1 = new JLabel();
        frontSpT1 = new javax.swing.JScrollPane(frontLbT1);
        frontCropBtnT1 = new javax.swing.JButton();
        lowRightT1 = new javax.swing.JPanel();
        backTfT1 = new javax.swing.JTextField();
        backBtnT1 = new javax.swing.JButton();
        backLbT1 = new JLabel();
        backSpT1 = new javax.swing.JScrollPane(backLbT1);
        backCropBtnT1 = new javax.swing.JButton();
        blankLb3 = new javax.swing.JLabel("  ");
        queryTab = new javax.swing.JPanel();
        upLeftScrollPaneT2 = new javax.swing.JScrollPane();
        upLeftT2 = new javax.swing.JPanel();
        nameLbT2 = new javax.swing.JLabel();
        titleLbT2 = new javax.swing.JLabel();
        companyLbT2 = new javax.swing.JLabel();
        disLbT2 = new javax.swing.JLabel();
        codeLbT2 = new javax.swing.JLabel();
        mobileLbT2 = new javax.swing.JLabel();
        faxLbT2 = new javax.swing.JLabel();
        adsLbT2 = new javax.swing.JLabel();
        lastnameLbT2 = new javax.swing.JLabel();
        emailLbT2 = new javax.swing.JLabel();
        webLbT2 = new javax.swing.JLabel();
        subDisLbT2 = new javax.swing.JLabel();
        provinceLbT2 = new javax.swing.JLabel();
        phoneLbT2 = new javax.swing.JLabel();
        noteLbT2 = new javax.swing.JLabel();
        countryLbT2 = new javax.swing.JLabel();
        nameTfT2 = new javax.swing.JTextField();
        titleTfT2 = new javax.swing.JTextField();
        companyTfT2 = new javax.swing.JTextField();
        disTfT2 = new javax.swing.JTextField();
        codeTfT2 = new javax.swing.JTextField();
        mobileTfT2 = new javax.swing.JTextField();
        faxTfT2 = new javax.swing.JTextField();
        lastnameTfT2 = new javax.swing.JTextField();
        emailTfT2 = new javax.swing.JTextField();
        webTfT2 = new javax.swing.JTextField();
        subDisTfT2 = new javax.swing.JTextField();
        provinceTfT2 = new javax.swing.JTextField();
        phoneTfT2 = new javax.swing.JTextField();
        countryTfT2 = new javax.swing.JTextField();
        adsPanelT2 = new javax.swing.JPanel();
        adsScrT2 = new javax.swing.JScrollPane();
        adsTaT2 = new javax.swing.JTextArea();
        notePanelT2 = new javax.swing.JPanel();
        noteScrT2 = new javax.swing.JScrollPane();
        noteTaT2 = new javax.swing.JTextArea();
        nameLbTE2 = new javax.swing.JLabel();
        nameTfTE2 = new javax.swing.JTextField();
        lastnameLbTE2 = new javax.swing.JLabel();
        lastnameTfTE2 = new javax.swing.JTextField();
        titleLbTE2 = new javax.swing.JLabel();
        titleTfTE2 = new javax.swing.JTextField();
        companyLbTE2 = new javax.swing.JLabel();
        companyTfTE2 = new javax.swing.JTextField();
        disLbTE2 = new javax.swing.JLabel();
        codeLbTE2 = new javax.swing.JLabel();
        mobileLbTE2 = new javax.swing.JLabel();
        faxLbTE2 = new javax.swing.JLabel();
        adsLbTE2 = new javax.swing.JLabel();
        disTfTE2 = new javax.swing.JTextField();
        codeTfTE2 = new javax.swing.JTextField();
        mobileTfTE2 = new javax.swing.JTextField();
        faxTfTE2 = new javax.swing.JTextField();
        adsPanelTE2 = new javax.swing.JPanel();
        adsScrTE2 = new javax.swing.JScrollPane();
        adsTaTE2 = new javax.swing.JTextArea();
        subDisLbTE2 = new javax.swing.JLabel();
        provinceLbTE2 = new javax.swing.JLabel();
        phoneLbTE2 = new javax.swing.JLabel();
        countryLbTE2 = new javax.swing.JLabel();
        noteLbTE2 = new javax.swing.JLabel();
        subDisTfTE2 = new javax.swing.JTextField();
        provinceTfTE2 = new javax.swing.JTextField();
        phoneTfTE2 = new javax.swing.JTextField();
        countryTfTE2 = new javax.swing.JTextField();
        notePanelTE2 = new javax.swing.JPanel();
        noteScrTE2 = new javax.swing.JScrollPane();
        noteTaTE2 = new javax.swing.JTextArea();
        blankLb4 = new javax.swing.JLabel("  ");
        blankLb5 = new javax.swing.JLabel("  ");
        blankLb6 = new javax.swing.JLabel("  ");
        blankPanel14 = new javax.swing.JPanel();
        upRightT2 = new javax.swing.JPanel();
        genSearchPanelT2 = new javax.swing.JPanel();
        genSearchTfT2 = new javax.swing.JTextField();
        genSearchT2 = new javax.swing.JButton();
        quickPanelT2 = new javax.swing.JPanel();
        quickLb = new javax.swing.JLabel();
        btnPanel0T2 = new javax.swing.JPanel();
        engBtn0T2 = new javax.swing.JToggleButton();
        engBtn1T2 = new javax.swing.JToggleButton();
        engBtn2T2 = new javax.swing.JToggleButton();
        thBtn0T2 = new javax.swing.JToggleButton();
        thBtn1T2 = new javax.swing.JToggleButton();
        thBtn2T2 = new javax.swing.JToggleButton();
        btnPanel1T2 = new javax.swing.JPanel();
        engBtn3T2 = new javax.swing.JToggleButton();
        thBtn3T2 = new javax.swing.JToggleButton();
        engBtn4T2 = new javax.swing.JToggleButton();
        thBtn4T2 = new javax.swing.JToggleButton();
        engBtn5T2 = new javax.swing.JToggleButton();
        thBtn5T2 = new javax.swing.JToggleButton();
        btnPanel2T2 = new javax.swing.JPanel();
        engBtn6T2 = new javax.swing.JToggleButton();
        engBtn7T2 = new javax.swing.JToggleButton();
        engBtn8T2 = new javax.swing.JToggleButton();
        thBtn8T2 = new javax.swing.JToggleButton();
        thBtn7T2 = new javax.swing.JToggleButton();
        thBtn6T2 = new javax.swing.JToggleButton();
        lowPanelT2 = new javax.swing.JPanel();
        tablePanelT2 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        xCol = new XTableColumnModel();
        importTableT2 = new javax.swing.JTable();
        importTableT2.setColumnModel(xCol);
        deleteEditPanelT2 = new javax.swing.JPanel();
        deletedBtnT2 = new javax.swing.JButton();
        editBtnT2 = new javax.swing.JButton();
        blankPanel6 = new javax.swing.JPanel();
        resultTab = new javax.swing.JPanel();
        upRightT3 = new javax.swing.JPanel();
        idPanelT3 = new javax.swing.JPanel();
        idLbT3 = new javax.swing.JLabel();
        idNameLbT3 = new javax.swing.JLabel();
        autoCropBtnT3 = new javax.swing.JButton();
        blackWhiteBtnT3 = new javax.swing.JButton();
        rotateBtnT3 = new javax.swing.JButton();
        emailBtnT3 = new javax.swing.JButton();
        undoBtnT3 = new javax.swing.JButton();
        confirmBtnT3 = new javax.swing.JButton();
        brightPanelT3 = new javax.swing.JPanel();
        brightLbT3 = new javax.swing.JLabel();
        brightSldT3 = new javax.swing.JSlider();
        selSidePanelT3 = new javax.swing.JPanel();
        sideLbT3 = new javax.swing.JLabel();
        frontSideRdT3 = new javax.swing.JRadioButton();
        backSideRdT3 = new javax.swing.JRadioButton();
        saveBtnT3 = new javax.swing.JButton();
        lowT3 = new javax.swing.JPanel();
        lowLeftT3 = new javax.swing.JPanel();
        frontTfT3 = new javax.swing.JTextField();
        frontBtnT3 = new javax.swing.JButton();
        frontLbT3 = new JLabel();
        frontSpT3 = new javax.swing.JScrollPane(frontLbT3);
        frontCropBtnT3 = new javax.swing.JButton();
        lowRightT3 = new javax.swing.JPanel();
        backTfT3 = new javax.swing.JTextField();
        backBtnT3 = new javax.swing.JButton();
        backLbT3 = new JLabel();
        backSpT3 = new javax.swing.JScrollPane(backLbT3);
        backCropBtnT3 = new javax.swing.JButton();
        upLeftScrollPaneT3 = new javax.swing.JScrollPane();
        upLeftT3 = new javax.swing.JPanel();
        nameLbT3 = new javax.swing.JLabel();
        titleLbT3 = new javax.swing.JLabel();
        companyLbT3 = new javax.swing.JLabel();
        disLbT3 = new javax.swing.JLabel();
        codeLbT3 = new javax.swing.JLabel();
        mobileLbT3 = new javax.swing.JLabel();
        faxLbT3 = new javax.swing.JLabel();
        adsLbT3 = new javax.swing.JLabel();
        lastnameLbT3 = new javax.swing.JLabel();
        emailLbT3 = new javax.swing.JLabel();
        webLbT3 = new javax.swing.JLabel();
        subDisLbT3 = new javax.swing.JLabel();
        provinceLbT3 = new javax.swing.JLabel();
        phoneLbT3 = new javax.swing.JLabel();
        noteLbT3 = new javax.swing.JLabel();
        countryLbT3 = new javax.swing.JLabel();
        nameTfT3 = new javax.swing.JTextField();
        titleTfT3 = new javax.swing.JTextField();
        companyTfT3 = new javax.swing.JTextField();
        disTfT3 = new javax.swing.JTextField();
        codeTfT3 = new javax.swing.JTextField();
        mobileTfT3 = new javax.swing.JTextField();
        faxTfT3 = new javax.swing.JTextField();
        lastnameTfT3 = new javax.swing.JTextField();
        emailTfT3 = new javax.swing.JTextField();
        webTfT3 = new javax.swing.JTextField();
        subDisTfT3 = new javax.swing.JTextField();
        provinceTfT3 = new javax.swing.JTextField();
        phoneTfT3 = new javax.swing.JTextField();
        countryTfT3 = new javax.swing.JTextField();
        adsPanelT3 = new javax.swing.JPanel();
        adsScrT3 = new javax.swing.JScrollPane();
        adsTaT3 = new javax.swing.JTextArea();
        notePanelT3 = new javax.swing.JPanel();
        noteScrT3 = new javax.swing.JScrollPane();
        noteTaT3 = new javax.swing.JTextArea();
        nameLbTE3 = new javax.swing.JLabel();
        nameTfTE3 = new javax.swing.JTextField();
        lastnameLbTE3 = new javax.swing.JLabel();
        lastnameTfTE3 = new javax.swing.JTextField();
        titleLbTE3 = new javax.swing.JLabel();
        titleTfTE3 = new javax.swing.JTextField();
        companyLbTE3 = new javax.swing.JLabel();
        companyTfTE3 = new javax.swing.JTextField();
        disLbTE3 = new javax.swing.JLabel();
        codeLbTE3 = new javax.swing.JLabel();
        mobileLbTE3 = new javax.swing.JLabel();
        faxLbTE3 = new javax.swing.JLabel();
        adsLbTE3 = new javax.swing.JLabel();
        disTfTE3 = new javax.swing.JTextField();
        codeTfTE3 = new javax.swing.JTextField();
        mobileTfTE3 = new javax.swing.JTextField();
        faxTfTE3 = new javax.swing.JTextField();
        adsPanelTE3 = new javax.swing.JPanel();
        adsScrTE3 = new javax.swing.JScrollPane();
        adsTaTE3 = new javax.swing.JTextArea();
        subDisLbTE3 = new javax.swing.JLabel();
        provinceLbTE3 = new javax.swing.JLabel();
        phoneLbTE3 = new javax.swing.JLabel();
        countryLbTE3 = new javax.swing.JLabel();
        noteLbTE3 = new javax.swing.JLabel();
        subDisTfTE3 = new javax.swing.JTextField();
        provinceTfTE3 = new javax.swing.JTextField();
        phoneTfTE3 = new javax.swing.JTextField();
        countryTfTE3 = new javax.swing.JTextField();
        notePanelTE3 = new javax.swing.JPanel();
        noteScrTE3 = new javax.swing.JScrollPane();
        noteTaTE3 = new javax.swing.JTextArea();
        blankLb7 = new javax.swing.JLabel("  ");
        blankLb8 = new javax.swing.JLabel("  ");
        blankLb9 = new javax.swing.JLabel("  ");
        importExportTab = new javax.swing.JPanel();
        importPanel = new javax.swing.JPanel();
        importPanelT4 = new javax.swing.JPanel();
        importLbT4 = new javax.swing.JLabel();
        importTfT4 = new javax.swing.JTextField();
        importBrowseBtnT4 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        replaceBtnT4 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        pendingBtnT4 = new javax.swing.JButton();
        exportPanel = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        exportLbT4 = new javax.swing.JLabel();
        exportTfT4 = new javax.swing.JTextField();
        browseExportBtnT4 = new javax.swing.JButton();
        exportBtnT4 = new javax.swing.JButton();
        blankPanelT4 = new javax.swing.JPanel();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        databaseLb = new javax.swing.JLabel();
        databaseNameLb = new javax.swing.JLabel();
        languageLb = new javax.swing.JLabel();
        langaugeChangeBtn = new javax.swing.JButton();

        mainPanel.setMaximumSize(new java.awt.Dimension(704, 674));
        mainPanel.setMinimumSize(new java.awt.Dimension(704, 674));
        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setPreferredSize(new java.awt.Dimension(704, 674));

        menuTab.setFont(getDefaultFont());
        menuTab.setMaximumSize(new java.awt.Dimension(720, 680));
        menuTab.setMinimumSize(new java.awt.Dimension(720, 680));
        menuTab.setName("menuTab"); // NOI18N
        menuTab.setPreferredSize(new java.awt.Dimension(720, 680));

        scannerTab.setMaximumSize(new java.awt.Dimension(720, 680));
        scannerTab.setMinimumSize(new java.awt.Dimension(720, 680));
        scannerTab.setName("scannerTab"); // NOI18N
        scannerTab.setPreferredSize(new java.awt.Dimension(720, 680));
        scannerTab.setLayout(new java.awt.GridBagLayout());

        upLeftScrollPaneT1.setAutoscrolls(true);
        upLeftScrollPaneT1.setMinimumSize(new java.awt.Dimension(380, 260));
        upLeftScrollPaneT1.setName("upLeftScrollPaneT1"); // NOI18N
        upLeftScrollPaneT1.setPreferredSize(new java.awt.Dimension(380, 260));

        upLeftT1.setMinimumSize(new java.awt.Dimension(720, 260));
        upLeftT1.setName("upLeftT1"); // NOI18N
        upLeftT1.setPreferredSize(new java.awt.Dimension(720, 260));
        upLeftT1.setLayout(new java.awt.GridBagLayout());

        nameLbT1.setFont(getDefaultFont());
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(ScannerView.class);
        nameLbT1.setText(resourceMap.getString("nameLbT1.text")); // NOI18N
        nameLbT1.setMaximumSize(new java.awt.Dimension(86, 15));
        nameLbT1.setMinimumSize(new java.awt.Dimension(86, 15));
        nameLbT1.setName("nameLbT1"); // NOI18N
        nameLbT1.setPreferredSize(new java.awt.Dimension(86, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(nameLbT1, gridBagConstraints);

        titleLbT1.setFont(getDefaultFont());
        titleLbT1.setText(resourceMap.getString("titleLbT1.text")); // NOI18N
        titleLbT1.setMaximumSize(new java.awt.Dimension(86, 15));
        titleLbT1.setMinimumSize(new java.awt.Dimension(86, 15));
        titleLbT1.setName("titleLbT1"); // NOI18N
        titleLbT1.setPreferredSize(new java.awt.Dimension(86, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(titleLbT1, gridBagConstraints);

        companyLbT1.setFont(getDefaultFont());
        companyLbT1.setText(resourceMap.getString("companyLbT1.text")); // NOI18N
        companyLbT1.setMaximumSize(new java.awt.Dimension(86, 15));
        companyLbT1.setMinimumSize(new java.awt.Dimension(86, 15));
        companyLbT1.setName("companyLbT1"); // NOI18N
        companyLbT1.setPreferredSize(new java.awt.Dimension(86, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(companyLbT1, gridBagConstraints);

        disLbT1.setFont(getDefaultFont());
        disLbT1.setText(resourceMap.getString("disLbT1.text")); // NOI18N
        disLbT1.setMaximumSize(new java.awt.Dimension(86, 15));
        disLbT1.setMinimumSize(new java.awt.Dimension(86, 15));
        disLbT1.setName("disLbT1"); // NOI18N
        disLbT1.setPreferredSize(new java.awt.Dimension(86, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(disLbT1, gridBagConstraints);

        codeLbT1.setFont(getDefaultFont());
        codeLbT1.setText(resourceMap.getString("codeLbT1.text")); // NOI18N
        codeLbT1.setMaximumSize(new java.awt.Dimension(86, 15));
        codeLbT1.setMinimumSize(new java.awt.Dimension(86, 15));
        codeLbT1.setName("codeLbT1"); // NOI18N
        codeLbT1.setPreferredSize(new java.awt.Dimension(86, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(codeLbT1, gridBagConstraints);

        mobileLbT1.setFont(getDefaultFont());
        mobileLbT1.setText(resourceMap.getString("mobileLbT1.text")); // NOI18N
        mobileLbT1.setMaximumSize(new java.awt.Dimension(86, 15));
        mobileLbT1.setMinimumSize(new java.awt.Dimension(86, 15));
        mobileLbT1.setName("mobileLbT1"); // NOI18N
        mobileLbT1.setPreferredSize(new java.awt.Dimension(86, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(mobileLbT1, gridBagConstraints);

        faxLbT1.setFont(getDefaultFont());
        faxLbT1.setText(resourceMap.getString("faxLbT1.text")); // NOI18N
        faxLbT1.setMaximumSize(new java.awt.Dimension(86, 15));
        faxLbT1.setMinimumSize(new java.awt.Dimension(86, 15));
        faxLbT1.setName("faxLbT1"); // NOI18N
        faxLbT1.setPreferredSize(new java.awt.Dimension(86, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(faxLbT1, gridBagConstraints);

        adsLbT1.setFont(getDefaultFont());
        adsLbT1.setText(resourceMap.getString("adsLbT1.text")); // NOI18N
        adsLbT1.setMaximumSize(new java.awt.Dimension(86, 15));
        adsLbT1.setMinimumSize(new java.awt.Dimension(86, 15));
        adsLbT1.setName("adsLbT1"); // NOI18N
        adsLbT1.setPreferredSize(new java.awt.Dimension(86, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(adsLbT1, gridBagConstraints);

        lastnameLbT1.setFont(getDefaultFont());
        lastnameLbT1.setText(resourceMap.getString("lastnameLbT1.text")); // NOI18N
        lastnameLbT1.setName("lastnameLbT1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(lastnameLbT1, gridBagConstraints);

        emailLbT1.setFont(getDefaultFont());
        emailLbT1.setText(resourceMap.getString("emailLbT1.text")); // NOI18N
        emailLbT1.setName("emailLbT1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(emailLbT1, gridBagConstraints);

        webLbT1.setFont(getDefaultFont());
        webLbT1.setText(resourceMap.getString("webLbT1.text")); // NOI18N
        webLbT1.setName("webLbT1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(webLbT1, gridBagConstraints);

        subDisLbT1.setFont(getDefaultFont());
        subDisLbT1.setText(resourceMap.getString("subDisLbT1.text")); // NOI18N
        subDisLbT1.setName("subDisLbT1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(subDisLbT1, gridBagConstraints);

        provinceLbT1.setFont(getDefaultFont());
        provinceLbT1.setText(resourceMap.getString("provinceLbT1.text")); // NOI18N
        provinceLbT1.setName("provinceLbT1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(provinceLbT1, gridBagConstraints);

        phoneLbT1.setFont(getDefaultFont());
        phoneLbT1.setText(resourceMap.getString("phoneLbT1.text")); // NOI18N
        phoneLbT1.setName("phoneLbT1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(phoneLbT1, gridBagConstraints);

        noteLbT1.setFont(getDefaultFont());
        noteLbT1.setText(resourceMap.getString("noteLbT1.text")); // NOI18N
        noteLbT1.setName("noteLbT1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(noteLbT1, gridBagConstraints);

        countryLbT1.setFont(getDefaultFont());
        countryLbT1.setText(resourceMap.getString("countryLbT1.text")); // NOI18N
        countryLbT1.setName("countryLbT1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(countryLbT1, gridBagConstraints);

        nameTfT1.setColumns(10);
        nameTfT1.setFont(getDefaultFont());
        nameTfT1.setText(resourceMap.getString("nameTfT1.text")); // NOI18N
        nameTfT1.setMinimumSize(new java.awt.Dimension(86, 25));
        nameTfT1.setName("nameTfT1"); // NOI18N
        nameTfT1.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(nameTfT1, gridBagConstraints);

        titleTfT1.setColumns(10);
        titleTfT1.setFont(getDefaultFont());
        titleTfT1.setText(resourceMap.getString("titleTfT1.text")); // NOI18N
        titleTfT1.setMinimumSize(new java.awt.Dimension(86, 25));
        titleTfT1.setName("titleTfT1"); // NOI18N
        titleTfT1.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(titleTfT1, gridBagConstraints);

        companyTfT1.setColumns(10);
        companyTfT1.setFont(getDefaultFont());
        companyTfT1.setText(resourceMap.getString("companyTfT1.text")); // NOI18N
        companyTfT1.setMinimumSize(new java.awt.Dimension(86, 25));
        companyTfT1.setName("companyTfT1"); // NOI18N
        companyTfT1.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(companyTfT1, gridBagConstraints);

        disTfT1.setColumns(10);
        disTfT1.setFont(getDefaultFont());
        disTfT1.setText(resourceMap.getString("disTfT1.text")); // NOI18N
        disTfT1.setMinimumSize(new java.awt.Dimension(86, 25));
        disTfT1.setName("disTfT1"); // NOI18N
        disTfT1.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(disTfT1, gridBagConstraints);

        codeTfT1.setColumns(10);
        codeTfT1.setFont(getDefaultFont());
        codeTfT1.setText(resourceMap.getString("codeTfT1.text")); // NOI18N
        codeTfT1.setMinimumSize(new java.awt.Dimension(86, 25));
        codeTfT1.setName("codeTfT1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(codeTfT1, gridBagConstraints);

        mobileTfT1.setColumns(10);
        mobileTfT1.setFont(getDefaultFont());
        mobileTfT1.setText(resourceMap.getString("mobileTfT1.text")); // NOI18N
        mobileTfT1.setMinimumSize(new java.awt.Dimension(86, 25));
        mobileTfT1.setName("mobileTfT1"); // NOI18N
        mobileTfT1.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(mobileTfT1, gridBagConstraints);

        faxTfT1.setColumns(10);
        faxTfT1.setFont(getDefaultFont());
        faxTfT1.setText(resourceMap.getString("faxTfT1.text")); // NOI18N
        faxTfT1.setMinimumSize(new java.awt.Dimension(86, 25));
        faxTfT1.setName("faxTfT1"); // NOI18N
        faxTfT1.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(faxTfT1, gridBagConstraints);

        lastnameTfT1.setColumns(10);
        lastnameTfT1.setFont(getDefaultFont());
        lastnameTfT1.setMinimumSize(new java.awt.Dimension(86, 25));
        lastnameTfT1.setName("lastnameTfT1"); // NOI18N
        lastnameTfT1.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(lastnameTfT1, gridBagConstraints);

        emailTfT1.setColumns(10);
        emailTfT1.setFont(getDefaultFont());
        emailTfT1.setMinimumSize(new java.awt.Dimension(86, 25));
        emailTfT1.setName("emailTfT1"); // NOI18N
        emailTfT1.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(emailTfT1, gridBagConstraints);

        webTfT1.setColumns(10);
        webTfT1.setFont(getDefaultFont());
        webTfT1.setMinimumSize(new java.awt.Dimension(86, 25));
        webTfT1.setName("webTfT1"); // NOI18N
        webTfT1.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(webTfT1, gridBagConstraints);

        subDisTfT1.setColumns(10);
        subDisTfT1.setFont(getDefaultFont());
        subDisTfT1.setMinimumSize(new java.awt.Dimension(86, 25));
        subDisTfT1.setName("subDisTfT1"); // NOI18N
        subDisTfT1.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(subDisTfT1, gridBagConstraints);

        provinceTfT1.setColumns(10);
        provinceTfT1.setFont(getDefaultFont());
        provinceTfT1.setMinimumSize(new java.awt.Dimension(86, 25));
        provinceTfT1.setName("provinceTfT1"); // NOI18N
        provinceTfT1.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(provinceTfT1, gridBagConstraints);

        phoneTfT1.setColumns(10);
        phoneTfT1.setFont(getDefaultFont());
        phoneTfT1.setMinimumSize(new java.awt.Dimension(86, 25));
        phoneTfT1.setName("phoneTfT1"); // NOI18N
        phoneTfT1.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(phoneTfT1, gridBagConstraints);

        countryTfT1.setColumns(10);
        countryTfT1.setFont(getDefaultFont());
        countryTfT1.setMinimumSize(new java.awt.Dimension(86, 25));
        countryTfT1.setName("countryTfT1"); // NOI18N
        countryTfT1.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(countryTfT1, gridBagConstraints);

        adsPanelT1.setMinimumSize(new java.awt.Dimension(99, 70));
        adsPanelT1.setName("adsPanelT1"); // NOI18N
        adsPanelT1.setPreferredSize(new java.awt.Dimension(99, 70));
        adsPanelT1.setLayout(new java.awt.GridLayout());

        adsScrT1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        adsScrT1.setHorizontalScrollBar(null);
        adsScrT1.setMinimumSize(new java.awt.Dimension(99, 52));
        adsScrT1.setName("adsScrT1"); // NOI18N
        adsScrT1.setPreferredSize(new java.awt.Dimension(99, 52));

        adsTaT1.setColumns(30);
        adsTaT1.setFont(getDefaultFont());
        adsTaT1.setLineWrap(true);
        adsTaT1.setRows(3);
        adsTaT1.setAutoscrolls(false);
        adsTaT1.setMaximumSize(new java.awt.Dimension(99, 52));
        adsTaT1.setMinimumSize(new java.awt.Dimension(99, 52));
        adsTaT1.setName("adsTaT1"); // NOI18N
        adsScrT1.setViewportView(adsTaT1);

        adsPanelT1.add(adsScrT1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridheight = 2;
        upLeftT1.add(adsPanelT1, gridBagConstraints);

        notePanelT1.setMinimumSize(new java.awt.Dimension(99, 70));
        notePanelT1.setName("notePanelT1"); // NOI18N
        notePanelT1.setPreferredSize(new java.awt.Dimension(99, 70));
        notePanelT1.setLayout(new java.awt.GridLayout());

        noteScrT1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        noteScrT1.setHorizontalScrollBar(null);
        noteScrT1.setMinimumSize(new java.awt.Dimension(99, 52));
        noteScrT1.setName("noteScrT1"); // NOI18N
        noteScrT1.setPreferredSize(new java.awt.Dimension(99, 52));

        noteTaT1.setColumns(30);
        noteTaT1.setFont(getDefaultFont());
        noteTaT1.setLineWrap(true);
        noteTaT1.setRows(3);
        noteTaT1.setAutoscrolls(false);
        noteTaT1.setMinimumSize(new java.awt.Dimension(99, 52));
        noteTaT1.setName("noteTaT1"); // NOI18N
        noteScrT1.setViewportView(noteTaT1);

        notePanelT1.add(noteScrT1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridheight = 2;
        upLeftT1.add(notePanelT1, gridBagConstraints);

        nameLbTE1.setText(resourceMap.getString("nameLbTE1.text")); // NOI18N
        nameLbTE1.setName("nameLbTE1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(nameLbTE1, gridBagConstraints);

        nameTfTE1.setColumns(10);
        nameTfTE1.setFont(getDefaultFont());
        nameTfTE1.setMinimumSize(new java.awt.Dimension(86, 25));
        nameTfTE1.setName("nameTfTE1"); // NOI18N
        nameTfTE1.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(nameTfTE1, gridBagConstraints);

        lastnameLbTE1.setText(resourceMap.getString("lastnameLbTE1.text")); // NOI18N
        lastnameLbTE1.setName("lastnameLbTE1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(lastnameLbTE1, gridBagConstraints);

        lastnameTfTE1.setColumns(10);
        lastnameTfTE1.setFont(getDefaultFont());
        lastnameTfTE1.setMinimumSize(new java.awt.Dimension(86, 25));
        lastnameTfTE1.setName("lastnameTfTE1"); // NOI18N
        lastnameTfTE1.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(lastnameTfTE1, gridBagConstraints);

        titleLbTE1.setText(resourceMap.getString("titleLbTE1.text")); // NOI18N
        titleLbTE1.setName("titleLbTE1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(titleLbTE1, gridBagConstraints);

        titleTfTE1.setColumns(10);
        titleTfTE1.setFont(getDefaultFont());
        titleTfTE1.setMinimumSize(new java.awt.Dimension(86, 25));
        titleTfTE1.setName("titleTfTE1"); // NOI18N
        titleTfTE1.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(titleTfTE1, gridBagConstraints);

        companyLbTE1.setText(resourceMap.getString("companyLbTE1.text")); // NOI18N
        companyLbTE1.setName("companyLbTE1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(companyLbTE1, gridBagConstraints);

        companyTfTE1.setColumns(10);
        companyTfTE1.setFont(getDefaultFont());
        companyTfTE1.setMinimumSize(new java.awt.Dimension(86, 25));
        companyTfTE1.setName("companyTfTE1"); // NOI18N
        companyTfTE1.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(companyTfTE1, gridBagConstraints);

        disLbTE1.setText(resourceMap.getString("disLbTE1.text")); // NOI18N
        disLbTE1.setName("disLbTE1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(disLbTE1, gridBagConstraints);

        codeLbTE1.setText(resourceMap.getString("codeLbTE1.text")); // NOI18N
        codeLbTE1.setName("codeLbTE1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(codeLbTE1, gridBagConstraints);

        mobileLbTE1.setText(resourceMap.getString("mobileLbTE1.text")); // NOI18N
        mobileLbTE1.setName("mobileLbTE1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(mobileLbTE1, gridBagConstraints);

        faxLbTE1.setText(resourceMap.getString("faxLbTE1.text")); // NOI18N
        faxLbTE1.setName("faxLbTE1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(faxLbTE1, gridBagConstraints);

        adsLbTE1.setText(resourceMap.getString("adsLbTE1.text")); // NOI18N
        adsLbTE1.setName("adsLbTE1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(adsLbTE1, gridBagConstraints);

        disTfTE1.setColumns(10);
        disTfTE1.setFont(getDefaultFont());
        disTfTE1.setMinimumSize(new java.awt.Dimension(86, 25));
        disTfTE1.setName("disTfTE1"); // NOI18N
        disTfTE1.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(disTfTE1, gridBagConstraints);

        codeTfTE1.setColumns(10);
        codeTfTE1.setFont(getDefaultFont());
        codeTfTE1.setMinimumSize(new java.awt.Dimension(86, 25));
        codeTfTE1.setName("codeTfTE1"); // NOI18N
        codeTfTE1.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(codeTfTE1, gridBagConstraints);

        mobileTfTE1.setColumns(10);
        mobileTfTE1.setFont(getDefaultFont());
        mobileTfTE1.setMinimumSize(new java.awt.Dimension(86, 25));
        mobileTfTE1.setName("mobileTfTE1"); // NOI18N
        mobileTfTE1.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(mobileTfTE1, gridBagConstraints);

        faxTfTE1.setColumns(10);
        faxTfTE1.setMinimumSize(new java.awt.Dimension(86, 25));
        faxTfTE1.setName("faxTfTE1"); // NOI18N
        faxTfTE1.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(faxTfTE1, gridBagConstraints);

        adsPanelTE1.setMinimumSize(new java.awt.Dimension(99, 70));
        adsPanelTE1.setName("adsPanelTE1"); // NOI18N
        adsPanelTE1.setPreferredSize(new java.awt.Dimension(99, 70));
        adsPanelTE1.setLayout(new java.awt.GridLayout());

        adsScrTE1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        adsScrTE1.setHorizontalScrollBar(null);
        adsScrTE1.setMinimumSize(new java.awt.Dimension(99, 52));
        adsScrTE1.setName("adsScrTE1"); // NOI18N
        adsScrTE1.setPreferredSize(new java.awt.Dimension(99, 52));

        adsTaTE1.setColumns(30);
        adsTaTE1.setFont(getDefaultFont());
        adsTaTE1.setRows(3);
        adsTaTE1.setAutoscrolls(false);
        adsTaTE1.setMinimumSize(new java.awt.Dimension(99, 52));
        adsTaTE1.setName("adsTaTE1"); // NOI18N
        adsScrTE1.setViewportView(adsTaTE1);

        adsPanelTE1.add(adsScrTE1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridheight = 2;
        upLeftT1.add(adsPanelTE1, gridBagConstraints);

        subDisLbTE1.setText(resourceMap.getString("subDisLbTE1.text")); // NOI18N
        subDisLbTE1.setName("subDisLbTE1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(subDisLbTE1, gridBagConstraints);

        provinceLbTE1.setText(resourceMap.getString("provinceLbTE1.text")); // NOI18N
        provinceLbTE1.setName("provinceLbTE1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(provinceLbTE1, gridBagConstraints);

        phoneLbTE1.setText(resourceMap.getString("phoneLbTE1.text")); // NOI18N
        phoneLbTE1.setName("phoneLbTE1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(phoneLbTE1, gridBagConstraints);

        countryLbTE1.setText(resourceMap.getString("countryLbTE1.text")); // NOI18N
        countryLbTE1.setName("countryLbTE1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(countryLbTE1, gridBagConstraints);

        noteLbTE1.setText(resourceMap.getString("noteLbTE1.text")); // NOI18N
        noteLbTE1.setName("noteLbTE1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(noteLbTE1, gridBagConstraints);

        subDisTfTE1.setColumns(10);
        subDisTfTE1.setMinimumSize(new java.awt.Dimension(86, 25));
        subDisTfTE1.setName("subDisTfTE1"); // NOI18N
        subDisTfTE1.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(subDisTfTE1, gridBagConstraints);

        provinceTfTE1.setColumns(10);
        provinceTfTE1.setMinimumSize(new java.awt.Dimension(86, 25));
        provinceTfTE1.setName("provinceTfTE1"); // NOI18N
        provinceTfTE1.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(provinceTfTE1, gridBagConstraints);

        phoneTfTE1.setColumns(10);
        phoneTfTE1.setMinimumSize(new java.awt.Dimension(86, 25));
        phoneTfTE1.setName("phoneTfTE1"); // NOI18N
        phoneTfTE1.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(phoneTfTE1, gridBagConstraints);

        countryTfTE1.setColumns(10);
        countryTfTE1.setMinimumSize(new java.awt.Dimension(86, 25));
        countryTfTE1.setName("countryTfTE1"); // NOI18N
        countryTfTE1.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(countryTfTE1, gridBagConstraints);

        notePanelTE1.setMinimumSize(new java.awt.Dimension(100, 70));
        notePanelTE1.setName("notePanelTE1"); // NOI18N
        notePanelTE1.setPreferredSize(new java.awt.Dimension(100, 70));
        notePanelTE1.setLayout(new java.awt.GridLayout());

        noteScrTE1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        noteScrTE1.setHorizontalScrollBar(null);
        noteScrTE1.setMinimumSize(new java.awt.Dimension(100, 52));
        noteScrTE1.setName("noteScrTE1"); // NOI18N
        noteScrTE1.setPreferredSize(new java.awt.Dimension(100, 52));

        noteTaTE1.setColumns(30);
        noteTaTE1.setFont(getDefaultFont());
        noteTaTE1.setRows(3);
        noteTaTE1.setAutoscrolls(false);
        noteTaTE1.setMinimumSize(new java.awt.Dimension(102, 52));
        noteTaTE1.setName("noteTaTE1"); // NOI18N
        noteScrTE1.setViewportView(noteTaTE1);

        notePanelTE1.add(noteScrTE1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridheight = 2;
        upLeftT1.add(notePanelTE1, gridBagConstraints);

        blankLb0.setName("blankLb0"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        upLeftT1.add(blankLb0, gridBagConstraints);

        blankLb1.setName("blankLb1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        upLeftT1.add(blankLb1, gridBagConstraints);

        blankLb2.setName("blankLb2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 0;
        upLeftT1.add(blankLb2, gridBagConstraints);

        upLeftScrollPaneT1.setViewportView(upLeftT1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        scannerTab.add(upLeftScrollPaneT1, gridBagConstraints);

        upRightT1.setMinimumSize(new java.awt.Dimension(285, 285));
        upRightT1.setName("upRightT1"); // NOI18N
        upRightT1.setPreferredSize(new java.awt.Dimension(285, 285));
        upRightT1.setLayout(new java.awt.GridBagLayout());

        scannerPanel.setName("scannerPanel"); // NOI18N

        scannerLbT1.setFont(getDefaultFont());
        scannerLbT1.setText(resourceMap.getString("scannerLbT1.text")); // NOI18N
        scannerLbT1.setName("scannerLbT1"); // NOI18N

        scannerBtnT1.setFont(getDefaultFont());
        scannerBtnT1.setText(resourceMap.getString("scannerBtnT1.text")); // NOI18N
        scannerBtnT1.setMaximumSize(new java.awt.Dimension(90, 23));
        scannerBtnT1.setName("scannerBtnT1"); // NOI18N
        scannerBtnT1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scannerBtnT1ActionPerformed(evt);
            }
        });

        scannerTxtT1.setText(resourceMap.getString("scannerTxtT1.text")); // NOI18N
        scannerTxtT1.setMinimumSize(new java.awt.Dimension(86, 25));
        scannerTxtT1.setName("scannerTxtT1"); // NOI18N
        scannerTxtT1.setPreferredSize(new java.awt.Dimension(86, 25));

        javax.swing.GroupLayout scannerPanelLayout = new javax.swing.GroupLayout(scannerPanel);
        scannerPanel.setLayout(scannerPanelLayout);
        scannerPanelLayout.setHorizontalGroup(
            scannerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scannerPanelLayout.createSequentialGroup()
                .addComponent(scannerLbT1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scannerTxtT1, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scannerBtnT1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        scannerPanelLayout.setVerticalGroup(
            scannerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scannerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(scannerLbT1)
                .addComponent(scannerTxtT1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(scannerBtnT1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        upRightT1.add(scannerPanel, gridBagConstraints);

        doubleSideBtnT1.setFont(getDefaultFont());
        doubleSideBtnT1.setText(resourceMap.getString("doubleSideBtnT1.text")); // NOI18N
        doubleSideBtnT1.setMaximumSize(new java.awt.Dimension(100, 31));
        doubleSideBtnT1.setMinimumSize(new java.awt.Dimension(100, 31));
        doubleSideBtnT1.setName("doubleSideBtnT1"); // NOI18N
        doubleSideBtnT1.setPreferredSize(new java.awt.Dimension(100, 31));
        doubleSideBtnT1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doubleSideBtnT1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        upRightT1.add(doubleSideBtnT1, gridBagConstraints);

        blackWhiteBtnT1.setFont(getDefaultFont());
        blackWhiteBtnT1.setText(resourceMap.getString("blackWhiteBtnT1.text")); // NOI18N
        blackWhiteBtnT1.setMaximumSize(new java.awt.Dimension(100, 31));
        blackWhiteBtnT1.setMinimumSize(new java.awt.Dimension(100, 31));
        blackWhiteBtnT1.setName("blackWhiteBtnT1"); // NOI18N
        blackWhiteBtnT1.setPreferredSize(new java.awt.Dimension(100, 31));
        blackWhiteBtnT1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                blackWhiteBtnT1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        upRightT1.add(blackWhiteBtnT1, gridBagConstraints);

        autoCropBtnT1.setFont(getDefaultFont());
        autoCropBtnT1.setText(resourceMap.getString("autoCropBtnT1.text")); // NOI18N
        autoCropBtnT1.setMaximumSize(new java.awt.Dimension(100, 31));
        autoCropBtnT1.setMinimumSize(new java.awt.Dimension(100, 31));
        autoCropBtnT1.setName("autoCropBtnT1"); // NOI18N
        autoCropBtnT1.setPreferredSize(new java.awt.Dimension(100, 31));
        autoCropBtnT1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoCropBtnT1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        upRightT1.add(autoCropBtnT1, gridBagConstraints);

        readCardBtnT1.setFont(getDefaultFont());
        readCardBtnT1.setText(resourceMap.getString("readCardBtnT1.text")); // NOI18N
        readCardBtnT1.setMaximumSize(new java.awt.Dimension(100, 31));
        readCardBtnT1.setMinimumSize(new java.awt.Dimension(100, 31));
        readCardBtnT1.setName("readCardBtnT1"); // NOI18N
        readCardBtnT1.setPreferredSize(new java.awt.Dimension(100, 31));
        readCardBtnT1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                readCardBtnT1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        upRightT1.add(readCardBtnT1, gridBagConstraints);

        rotateBtnT1.setFont(getDefaultFont());
        rotateBtnT1.setText(resourceMap.getString("rotateBtnT1.text")); // NOI18N
        rotateBtnT1.setMaximumSize(new java.awt.Dimension(100, 31));
        rotateBtnT1.setMinimumSize(new java.awt.Dimension(100, 31));
        rotateBtnT1.setName("rotateBtnT1"); // NOI18N
        rotateBtnT1.setPreferredSize(new java.awt.Dimension(100, 31));
        rotateBtnT1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rotateBtnT1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        upRightT1.add(rotateBtnT1, gridBagConstraints);

        emailBtnT1.setFont(getDefaultFont());
        emailBtnT1.setText(resourceMap.getString("emailBtnT1.text")); // NOI18N
        emailBtnT1.setMaximumSize(new java.awt.Dimension(100, 31));
        emailBtnT1.setMinimumSize(new java.awt.Dimension(100, 31));
        emailBtnT1.setName("emailBtnT1"); // NOI18N
        emailBtnT1.setPreferredSize(new java.awt.Dimension(100, 31));
        emailBtnT1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emailBtnT1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        upRightT1.add(emailBtnT1, gridBagConstraints);

        undoBtnT1.setFont(getDefaultFont());
        undoBtnT1.setText(resourceMap.getString("undoBtnT1.text")); // NOI18N
        undoBtnT1.setName("undoBtn"); // NOI18N
        undoBtnT1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoBtnT1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        upRightT1.add(undoBtnT1, gridBagConstraints);

        confirmBtnT1.setFont(getDefaultFont());
        confirmBtnT1.setText(resourceMap.getString("confirmBtnT1.text")); // NOI18N
        confirmBtnT1.setName("confirmBtn"); // NOI18N
        confirmBtnT1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirmBtnT1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        upRightT1.add(confirmBtnT1, gridBagConstraints);

        brightPanelT1.setMinimumSize(new java.awt.Dimension(242, 60));
        brightPanelT1.setName("brightPanelT1"); // NOI18N
        brightPanelT1.setPreferredSize(new java.awt.Dimension(242, 60));
        brightPanelT1.setRequestFocusEnabled(false);
        brightPanelT1.setLayout(new java.awt.GridBagLayout());

        brightLbT1.setFont(getDefaultFont());
        brightLbT1.setText(resourceMap.getString("brightLbT1.text")); // NOI18N
        brightLbT1.setName("brightLbT1"); // NOI18N
        brightPanelT1.add(brightLbT1, new java.awt.GridBagConstraints());

        brightSldT1.setMajorTickSpacing(1);
        brightSldT1.setMaximum(5);
        brightSldT1.setMinimum(1);
        brightSldT1.setMinorTickSpacing(1);
        brightSldT1.setPaintLabels(true);
        brightSldT1.setSnapToTicks(true);
        brightSldT1.setValue(3);
        brightSldT1.setMaximumSize(new java.awt.Dimension(150, 60));
        brightSldT1.setMinimumSize(new java.awt.Dimension(150, 60));
        brightSldT1.setName("brightSldT1"); // NOI18N
        brightSldT1.setPreferredSize(new java.awt.Dimension(150, 60));
        Hashtable labelTable = new Hashtable();
        labelTable.put( new Integer( 1 ), new JLabel("1") );
        labelTable.put( new Integer( 2 ), new JLabel("2") );
        labelTable.put( new Integer( 3 ), new JLabel("3") );
        labelTable.put( new Integer( 4 ), new JLabel("4") );
        labelTable.put( new Integer( 5 ), new JLabel("5") );
        brightSldT1.setLabelTable( labelTable );
        brightSldT1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                brightSldT1StateChanged(evt);
            }
        });
        brightPanelT1.add(brightSldT1, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        upRightT1.add(brightPanelT1, gridBagConstraints);

        scanBtn.setFont(getDefaultFont());
        scanBtn.setText(resourceMap.getString("scanBtn.text")); // NOI18N
        scanBtn.setMaximumSize(new java.awt.Dimension(100, 31));
        scanBtn.setMinimumSize(new java.awt.Dimension(100, 31));
        scanBtn.setName("scanBtn"); // NOI18N
        scanBtn.setPreferredSize(new java.awt.Dimension(100, 31));
        scanBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scanBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        upRightT1.add(scanBtn, gridBagConstraints);

        saveBtnT1.setFont(getDefaultFont());
        saveBtnT1.setText(resourceMap.getString("saveBtnT1.text")); // NOI18N
        saveBtnT1.setMaximumSize(new java.awt.Dimension(100, 31));
        saveBtnT1.setMinimumSize(new java.awt.Dimension(100, 31));
        saveBtnT1.setName("saveBtnT1"); // NOI18N
        saveBtnT1.setPreferredSize(new java.awt.Dimension(100, 31));
        saveBtnT1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveBtnT1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        upRightT1.add(saveBtnT1, gridBagConstraints);

        selSidePanelT1.setMinimumSize(new java.awt.Dimension(248, 30));
        selSidePanelT1.setName("selSidePanelT1"); // NOI18N
        selSidePanelT1.setPreferredSize(new java.awt.Dimension(248, 30));

        sideLbT1.setFont(getDefaultFont());
        sideLbT1.setText(resourceMap.getString("sideLbT1.text")); // NOI18N
        sideLbT1.setName("sideLbT1"); // NOI18N
        selSidePanelT1.add(sideLbT1);

        frontSideRdT1.setFont(getDefaultFont());
        frontSideRdT1.setText(resourceMap.getString("frontSideRdT1.text")); // NOI18N
        frontSideRdT1.setName("frontSideRdT1"); // NOI18N
        frontSideRdT1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                frontSideRdT1ActionPerformed(evt);
            }
        });
        selSidePanelT1.add(frontSideRdT1);

        backSideRdT1.setFont(getDefaultFont());
        backSideRdT1.setText(resourceMap.getString("backSideRdT1.text")); // NOI18N
        backSideRdT1.setName("backSideRdT1"); // NOI18N
        backSideRdT1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backSideRdT1ActionPerformed(evt);
            }
        });
        selSidePanelT1.add(backSideRdT1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        upRightT1.add(selSidePanelT1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        scannerTab.add(upRightT1, gridBagConstraints);

        low.setMaximumSize(null);
        low.setName("low"); // NOI18N
        low.setPreferredSize(new java.awt.Dimension(300, 340));
        low.setLayout(new java.awt.GridLayout(1, 2));

        lowLeftT1.setMaximumSize(null);
        lowLeftT1.setName("lowLeftT1"); // NOI18N

        frontPanelT1.setMaximumSize(null);
        frontPanelT1.setMinimumSize(new java.awt.Dimension(332, 340));
        frontPanelT1.setName("frontPanelT1"); // NOI18N
        frontPanelT1.setPreferredSize(new java.awt.Dimension(332, 340));

        frontTfT1.setEditable(false);
        frontTfT1.setFont(getDefaultFont());
        frontTfT1.setText(resourceMap.getString("frontTfT1.text")); // NOI18N
        frontTfT1.setName("frontTfT1"); // NOI18N
        frontTfT1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                frontTfT1MouseClicked(evt);
            }
        });

        frontBtnT1.setFont(getDefaultFont());
        frontBtnT1.setText(resourceMap.getString("frontBtnT1.text")); // NOI18N
        frontBtnT1.setName("frontBtnT1"); // NOI18N
        frontBtnT1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                frontBtnT1ActionPerformed(evt);
            }
        });

        frontSpT1.setMinimumSize(new java.awt.Dimension(320, 291));
        frontSpT1.setName("frontSpT1"); // NOI18N
        frontSpT1.setPreferredSize(new java.awt.Dimension(100, 260));

        frontCropBtnT1.setFont(getDefaultFont());
        frontCropBtnT1.setText(resourceMap.getString("frontCropBtnT1.text")); // NOI18N
        frontCropBtnT1.setName("frontCropBtnT1"); // NOI18N
        frontCropBtnT1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                frontCropBtnT1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout frontPanelT1Layout = new javax.swing.GroupLayout(frontPanelT1);
        frontPanelT1.setLayout(frontPanelT1Layout);
        frontPanelT1Layout.setHorizontalGroup(
            frontPanelT1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, frontPanelT1Layout.createSequentialGroup()
                .addGroup(frontPanelT1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(frontSpT1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                    .addGroup(frontPanelT1Layout.createSequentialGroup()
                        .addComponent(frontTfT1, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(frontBtnT1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(frontCropBtnT1)))
                .addContainerGap())
        );
        frontPanelT1Layout.setVerticalGroup(
            frontPanelT1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(frontPanelT1Layout.createSequentialGroup()
                .addGroup(frontPanelT1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(frontTfT1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(frontCropBtnT1)
                    .addComponent(frontBtnT1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addComponent(frontSpT1, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout lowLeftT1Layout = new javax.swing.GroupLayout(lowLeftT1);
        lowLeftT1.setLayout(lowLeftT1Layout);
        lowLeftT1Layout.setHorizontalGroup(
            lowLeftT1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(frontPanelT1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        lowLeftT1Layout.setVerticalGroup(
            lowLeftT1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(frontPanelT1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        low.add(lowLeftT1);

        lowRightT1.setMaximumSize(null);
        lowRightT1.setMinimumSize(new java.awt.Dimension(332, 340));
        lowRightT1.setName("lowRightT1"); // NOI18N

        backTfT1.setEditable(false);
        backTfT1.setFont(getDefaultFont());
        backTfT1.setText(resourceMap.getString("backTfT1.text")); // NOI18N
        backTfT1.setName("backTfT1"); // NOI18N
        backTfT1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                backTfT1MouseClicked(evt);
            }
        });

        backBtnT1.setFont(getDefaultFont());
        backBtnT1.setText(resourceMap.getString("backBtnT1.text")); // NOI18N
        backBtnT1.setName("backBtnT1"); // NOI18N
        backBtnT1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backBtnT1ActionPerformed(evt);
            }
        });

        backSpT1.setMinimumSize(new java.awt.Dimension(320, 291));
        backSpT1.setName("backSpT1"); // NOI18N
        backSpT1.setPreferredSize(new java.awt.Dimension(320, 291));

        backCropBtnT1.setFont(getDefaultFont());
        backCropBtnT1.setText(resourceMap.getString("backCropBtnT1.text")); // NOI18N
        backCropBtnT1.setName("backCropBtnT1"); // NOI18N
        backCropBtnT1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backCropBtnT1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout lowRightT1Layout = new javax.swing.GroupLayout(lowRightT1);
        lowRightT1.setLayout(lowRightT1Layout);
        lowRightT1Layout.setHorizontalGroup(
            lowRightT1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lowRightT1Layout.createSequentialGroup()
                .addGroup(lowRightT1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(lowRightT1Layout.createSequentialGroup()
                        .addComponent(backTfT1, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(backBtnT1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(backCropBtnT1))
                    .addComponent(backSpT1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        lowRightT1Layout.setVerticalGroup(
            lowRightT1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lowRightT1Layout.createSequentialGroup()
                .addGroup(lowRightT1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(backCropBtnT1)
                    .addComponent(backBtnT1)
                    .addComponent(backTfT1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(backSpT1, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
                .addContainerGap())
        );

        low.add(lowRightT1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        scannerTab.add(low, gridBagConstraints);

        blankLb3.setName("blankLb3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        scannerTab.add(blankLb3, gridBagConstraints);

        menuTab.addTab(resourceMap.getString("scannerTab.TabConstraints.tabTitle"), scannerTab); // NOI18N

        queryTab.setMaximumSize(new java.awt.Dimension(680, 630));
        queryTab.setMinimumSize(new java.awt.Dimension(680, 630));
        queryTab.setName("queryTab"); // NOI18N
        queryTab.setPreferredSize(new java.awt.Dimension(680, 630));
        queryTab.setLayout(new java.awt.GridBagLayout());

        upLeftScrollPaneT2.setMinimumSize(new java.awt.Dimension(350, 260));
        upLeftScrollPaneT2.setName("upLeftScrollPaneT2"); // NOI18N
        upLeftScrollPaneT2.setPreferredSize(new java.awt.Dimension(390, 260));

        upLeftT2.setMinimumSize(new java.awt.Dimension(720, 260));
        upLeftT2.setName("upLeftT2"); // NOI18N
        upLeftT2.setPreferredSize(new java.awt.Dimension(720, 260));
        upLeftT2.setLayout(new java.awt.GridBagLayout());

        nameLbT2.setFont(getDefaultFont());
        nameLbT2.setText(resourceMap.getString("nameLbT2.text")); // NOI18N
        nameLbT2.setMaximumSize(new java.awt.Dimension(86, 15));
        nameLbT2.setMinimumSize(new java.awt.Dimension(86, 15));
        nameLbT2.setName("nameLbT2"); // NOI18N
        nameLbT2.setPreferredSize(new java.awt.Dimension(86, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(nameLbT2, gridBagConstraints);

        titleLbT2.setFont(getDefaultFont());
        titleLbT2.setText(resourceMap.getString("titleLbT2.text")); // NOI18N
        titleLbT2.setMaximumSize(new java.awt.Dimension(86, 15));
        titleLbT2.setMinimumSize(new java.awt.Dimension(86, 15));
        titleLbT2.setName("titleLbT2"); // NOI18N
        titleLbT2.setPreferredSize(new java.awt.Dimension(86, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(titleLbT2, gridBagConstraints);

        companyLbT2.setFont(getDefaultFont());
        companyLbT2.setText(resourceMap.getString("companyLbT2.text")); // NOI18N
        companyLbT2.setMaximumSize(new java.awt.Dimension(86, 15));
        companyLbT2.setMinimumSize(new java.awt.Dimension(86, 15));
        companyLbT2.setName("companyLbT2"); // NOI18N
        companyLbT2.setPreferredSize(new java.awt.Dimension(86, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(companyLbT2, gridBagConstraints);

        disLbT2.setFont(getDefaultFont());
        disLbT2.setText(resourceMap.getString("disLbT2.text")); // NOI18N
        disLbT2.setMaximumSize(new java.awt.Dimension(86, 15));
        disLbT2.setMinimumSize(new java.awt.Dimension(86, 15));
        disLbT2.setName("disLbT2"); // NOI18N
        disLbT2.setPreferredSize(new java.awt.Dimension(86, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(disLbT2, gridBagConstraints);

        codeLbT2.setFont(getDefaultFont());
        codeLbT2.setText(resourceMap.getString("codeLbT2.text")); // NOI18N
        codeLbT2.setMaximumSize(new java.awt.Dimension(86, 15));
        codeLbT2.setMinimumSize(new java.awt.Dimension(86, 15));
        codeLbT2.setName("codeLbT2"); // NOI18N
        codeLbT2.setPreferredSize(new java.awt.Dimension(86, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(codeLbT2, gridBagConstraints);

        mobileLbT2.setFont(getDefaultFont());
        mobileLbT2.setText(resourceMap.getString("mobileLbT2.text")); // NOI18N
        mobileLbT2.setMaximumSize(new java.awt.Dimension(86, 15));
        mobileLbT2.setMinimumSize(new java.awt.Dimension(86, 15));
        mobileLbT2.setName("mobileLbT2"); // NOI18N
        mobileLbT2.setPreferredSize(new java.awt.Dimension(86, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(mobileLbT2, gridBagConstraints);

        faxLbT2.setFont(getDefaultFont());
        faxLbT2.setText(resourceMap.getString("faxLbT2.text")); // NOI18N
        faxLbT2.setMaximumSize(new java.awt.Dimension(86, 15));
        faxLbT2.setMinimumSize(new java.awt.Dimension(86, 15));
        faxLbT2.setName("faxLbT2"); // NOI18N
        faxLbT2.setPreferredSize(new java.awt.Dimension(86, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(faxLbT2, gridBagConstraints);

        adsLbT2.setFont(getDefaultFont());
        adsLbT2.setText(resourceMap.getString("adsLbT2.text")); // NOI18N
        adsLbT2.setMaximumSize(new java.awt.Dimension(86, 15));
        adsLbT2.setMinimumSize(new java.awt.Dimension(86, 15));
        adsLbT2.setName("adsLbT2"); // NOI18N
        adsLbT2.setPreferredSize(new java.awt.Dimension(86, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(adsLbT2, gridBagConstraints);

        lastnameLbT2.setFont(getDefaultFont());
        lastnameLbT2.setText(resourceMap.getString("lastnameLbT2.text")); // NOI18N
        lastnameLbT2.setName("lastnameLbT2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(lastnameLbT2, gridBagConstraints);

        emailLbT2.setFont(getDefaultFont());
        emailLbT2.setText(resourceMap.getString("emailLbT2.text")); // NOI18N
        emailLbT2.setName("emailLbT2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(emailLbT2, gridBagConstraints);

        webLbT2.setFont(getDefaultFont());
        webLbT2.setText(resourceMap.getString("webLbT2.text")); // NOI18N
        webLbT2.setName("webLbT2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(webLbT2, gridBagConstraints);

        subDisLbT2.setFont(getDefaultFont());
        subDisLbT2.setText(resourceMap.getString("subDisLbT2.text")); // NOI18N
        subDisLbT2.setName("subDisLbT2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(subDisLbT2, gridBagConstraints);

        provinceLbT2.setFont(getDefaultFont());
        provinceLbT2.setText(resourceMap.getString("provinceLbT2.text")); // NOI18N
        provinceLbT2.setName("provinceLbT2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(provinceLbT2, gridBagConstraints);

        phoneLbT2.setFont(getDefaultFont());
        phoneLbT2.setText(resourceMap.getString("phoneLbT2.text")); // NOI18N
        phoneLbT2.setName("phoneLbT2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(phoneLbT2, gridBagConstraints);

        noteLbT2.setFont(getDefaultFont());
        noteLbT2.setText(resourceMap.getString("noteLbT2.text")); // NOI18N
        noteLbT2.setName("noteLbT2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(noteLbT2, gridBagConstraints);

        countryLbT2.setFont(getDefaultFont());
        countryLbT2.setText(resourceMap.getString("countryLbT2.text")); // NOI18N
        countryLbT2.setName("countryLbT2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(countryLbT2, gridBagConstraints);

        nameTfT2.setColumns(10);
        nameTfT2.setMinimumSize(new java.awt.Dimension(86, 25));
        nameTfT2.setName("nameTfT2"); // NOI18N
        nameTfT2.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(nameTfT2, gridBagConstraints);

        titleTfT2.setColumns(10);
        titleTfT2.setFont(getDefaultFont());
        titleTfT2.setMinimumSize(new java.awt.Dimension(86, 25));
        titleTfT2.setName("titleTfT2"); // NOI18N
        titleTfT2.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(titleTfT2, gridBagConstraints);

        companyTfT2.setColumns(10);
        companyTfT2.setFont(getDefaultFont());
        companyTfT2.setMinimumSize(new java.awt.Dimension(86, 25));
        companyTfT2.setName("companyTfT2"); // NOI18N
        companyTfT2.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(companyTfT2, gridBagConstraints);

        disTfT2.setColumns(10);
        disTfT2.setFont(getDefaultFont());
        disTfT2.setMinimumSize(new java.awt.Dimension(86, 25));
        disTfT2.setName("disTfT2"); // NOI18N
        disTfT2.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(disTfT2, gridBagConstraints);

        codeTfT2.setColumns(10);
        codeTfT2.setFont(getDefaultFont());
        codeTfT2.setMinimumSize(new java.awt.Dimension(86, 25));
        codeTfT2.setName("codeTfT2"); // NOI18N
        codeTfT2.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(codeTfT2, gridBagConstraints);

        mobileTfT2.setColumns(10);
        mobileTfT2.setFont(getDefaultFont());
        mobileTfT2.setMinimumSize(new java.awt.Dimension(86, 25));
        mobileTfT2.setName("mobileTfT2"); // NOI18N
        mobileTfT2.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(mobileTfT2, gridBagConstraints);

        faxTfT2.setColumns(10);
        faxTfT2.setFont(getDefaultFont());
        faxTfT2.setMinimumSize(new java.awt.Dimension(86, 25));
        faxTfT2.setName("faxTfT2"); // NOI18N
        faxTfT2.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(faxTfT2, gridBagConstraints);

        lastnameTfT2.setColumns(10);
        lastnameTfT2.setFont(getDefaultFont());
        lastnameTfT2.setMinimumSize(new java.awt.Dimension(86, 25));
        lastnameTfT2.setName("lastnameTfT2"); // NOI18N
        lastnameTfT2.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(lastnameTfT2, gridBagConstraints);

        emailTfT2.setColumns(10);
        emailTfT2.setFont(getDefaultFont());
        emailTfT2.setMinimumSize(new java.awt.Dimension(86, 25));
        emailTfT2.setName("emailTfT2"); // NOI18N
        emailTfT2.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(emailTfT2, gridBagConstraints);

        webTfT2.setColumns(10);
        webTfT2.setFont(getDefaultFont());
        webTfT2.setMinimumSize(new java.awt.Dimension(86, 25));
        webTfT2.setName("webTfT2"); // NOI18N
        webTfT2.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(webTfT2, gridBagConstraints);

        subDisTfT2.setColumns(10);
        subDisTfT2.setFont(getDefaultFont());
        subDisTfT2.setMinimumSize(new java.awt.Dimension(86, 25));
        subDisTfT2.setName("subDisTfT2"); // NOI18N
        subDisTfT2.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(subDisTfT2, gridBagConstraints);

        provinceTfT2.setColumns(10);
        provinceTfT2.setFont(getDefaultFont());
        provinceTfT2.setMinimumSize(new java.awt.Dimension(86, 25));
        provinceTfT2.setName("provinceTfT2"); // NOI18N
        provinceTfT2.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(provinceTfT2, gridBagConstraints);

        phoneTfT2.setColumns(10);
        phoneTfT2.setFont(getDefaultFont());
        phoneTfT2.setMinimumSize(new java.awt.Dimension(86, 25));
        phoneTfT2.setName("phoneTfT2"); // NOI18N
        phoneTfT2.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(phoneTfT2, gridBagConstraints);

        countryTfT2.setColumns(10);
        countryTfT2.setFont(getDefaultFont());
        countryTfT2.setMinimumSize(new java.awt.Dimension(86, 25));
        countryTfT2.setName("countryTfT2"); // NOI18N
        countryTfT2.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(countryTfT2, gridBagConstraints);

        adsPanelT2.setMinimumSize(new java.awt.Dimension(99, 70));
        adsPanelT2.setName("adsPanelT2"); // NOI18N
        adsPanelT2.setPreferredSize(new java.awt.Dimension(99, 70));
        adsPanelT2.setLayout(new java.awt.GridLayout());

        adsScrT2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        adsScrT2.setHorizontalScrollBar(null);
        adsScrT2.setMinimumSize(new java.awt.Dimension(99, 52));
        adsScrT2.setName("adsScrT2"); // NOI18N
        adsScrT2.setPreferredSize(new java.awt.Dimension(99, 52));

        adsTaT2.setColumns(30);
        adsTaT2.setFont(getDefaultFont());
        adsTaT2.setLineWrap(true);
        adsTaT2.setRows(3);
        adsTaT2.setAutoscrolls(false);
        adsTaT2.setMinimumSize(new java.awt.Dimension(99, 52));
        adsTaT2.setName("adsTaT2"); // NOI18N
        adsScrT2.setViewportView(adsTaT2);

        adsPanelT2.add(adsScrT2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridheight = 2;
        upLeftT2.add(adsPanelT2, gridBagConstraints);

        notePanelT2.setMinimumSize(new java.awt.Dimension(100, 70));
        notePanelT2.setName("notePanelT2"); // NOI18N
        notePanelT2.setPreferredSize(new java.awt.Dimension(100, 70));
        notePanelT2.setLayout(new java.awt.GridLayout());

        noteScrT2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        noteScrT2.setHorizontalScrollBar(null);
        noteScrT2.setName("noteScrT2"); // NOI18N

        noteTaT2.setColumns(30);
        noteTaT2.setFont(getDefaultFont());
        noteTaT2.setLineWrap(true);
        noteTaT2.setRows(3);
        noteTaT2.setAutoscrolls(false);
        noteTaT2.setMinimumSize(new java.awt.Dimension(99, 52));
        noteTaT2.setName("noteTaT2"); // NOI18N
        noteScrT2.setViewportView(noteTaT2);

        notePanelT2.add(noteScrT2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridheight = 2;
        upLeftT2.add(notePanelT2, gridBagConstraints);

        nameLbTE2.setText(resourceMap.getString("nameLbTE2.text")); // NOI18N
        nameLbTE2.setName("nameLbTE2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(nameLbTE2, gridBagConstraints);

        nameTfTE2.setColumns(10);
        nameTfTE2.setFont(getDefaultFont());
        nameTfTE2.setMinimumSize(new java.awt.Dimension(86, 25));
        nameTfTE2.setName("nameTfTE2"); // NOI18N
        nameTfTE2.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(nameTfTE2, gridBagConstraints);

        lastnameLbTE2.setText(resourceMap.getString("lastnameLbTE2.text")); // NOI18N
        lastnameLbTE2.setName("lastnameLbTE2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(lastnameLbTE2, gridBagConstraints);

        lastnameTfTE2.setColumns(10);
        lastnameTfTE2.setFont(getDefaultFont());
        lastnameTfTE2.setMinimumSize(new java.awt.Dimension(86, 25));
        lastnameTfTE2.setName("lastnameTfTE2"); // NOI18N
        lastnameTfTE2.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(lastnameTfTE2, gridBagConstraints);

        titleLbTE2.setText(resourceMap.getString("titleLbTE2.text")); // NOI18N
        titleLbTE2.setName("titleLbTE2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(titleLbTE2, gridBagConstraints);

        titleTfTE2.setColumns(10);
        titleTfTE2.setFont(getDefaultFont());
        titleTfTE2.setMinimumSize(new java.awt.Dimension(86, 25));
        titleTfTE2.setName("titleTfTE2"); // NOI18N
        titleTfTE2.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(titleTfTE2, gridBagConstraints);

        companyLbTE2.setText(resourceMap.getString("companyLbTE2.text")); // NOI18N
        companyLbTE2.setName("companyLbTE2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(companyLbTE2, gridBagConstraints);

        companyTfTE2.setColumns(10);
        companyTfTE2.setFont(getDefaultFont());
        companyTfTE2.setMinimumSize(new java.awt.Dimension(86, 25));
        companyTfTE2.setName("companyTfTE2"); // NOI18N
        companyTfTE2.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(companyTfTE2, gridBagConstraints);

        disLbTE2.setText(resourceMap.getString("disLbTE2.text")); // NOI18N
        disLbTE2.setName("disLbTE2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(disLbTE2, gridBagConstraints);

        codeLbTE2.setText(resourceMap.getString("codeLbTE2.text")); // NOI18N
        codeLbTE2.setName("codeLbTE2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(codeLbTE2, gridBagConstraints);

        mobileLbTE2.setText(resourceMap.getString("mobileLbTE2.text")); // NOI18N
        mobileLbTE2.setName("mobileLbTE2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(mobileLbTE2, gridBagConstraints);

        faxLbTE2.setText(resourceMap.getString("faxLbTE2.text")); // NOI18N
        faxLbTE2.setName("faxLbTE2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(faxLbTE2, gridBagConstraints);

        adsLbTE2.setText(resourceMap.getString("adsLbTE2.text")); // NOI18N
        adsLbTE2.setName("adsLbTE2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(adsLbTE2, gridBagConstraints);

        disTfTE2.setColumns(10);
        disTfTE2.setFont(getDefaultFont());
        disTfTE2.setMinimumSize(new java.awt.Dimension(86, 25));
        disTfTE2.setName("disTfTE2"); // NOI18N
        disTfTE2.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(disTfTE2, gridBagConstraints);

        codeTfTE2.setColumns(10);
        codeTfTE2.setFont(getDefaultFont());
        codeTfTE2.setMinimumSize(new java.awt.Dimension(86, 25));
        codeTfTE2.setName("codeTfTE2"); // NOI18N
        codeTfTE2.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(codeTfTE2, gridBagConstraints);

        mobileTfTE2.setColumns(10);
        mobileTfTE2.setFont(getDefaultFont());
        mobileTfTE2.setMinimumSize(new java.awt.Dimension(86, 25));
        mobileTfTE2.setName("mobileTfTE2"); // NOI18N
        mobileTfTE2.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(mobileTfTE2, gridBagConstraints);

        faxTfTE2.setColumns(10);
        faxTfTE2.setFont(getDefaultFont());
        faxTfTE2.setMinimumSize(new java.awt.Dimension(86, 25));
        faxTfTE2.setName("faxTfTE2"); // NOI18N
        faxTfTE2.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(faxTfTE2, gridBagConstraints);

        adsPanelTE2.setMinimumSize(new java.awt.Dimension(99, 70));
        adsPanelTE2.setName("adsPanelTE2"); // NOI18N
        adsPanelTE2.setPreferredSize(new java.awt.Dimension(99, 70));
        adsPanelTE2.setLayout(new java.awt.GridLayout());

        adsScrTE2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        adsScrTE2.setHorizontalScrollBar(null);
        adsScrTE2.setMinimumSize(new java.awt.Dimension(99, 52));
        adsScrTE2.setName("adsScrTE2"); // NOI18N
        adsScrTE2.setPreferredSize(new java.awt.Dimension(99, 52));

        adsTaTE2.setColumns(30);
        adsTaTE2.setFont(getDefaultFont());
        adsTaTE2.setRows(3);
        adsTaTE2.setAutoscrolls(false);
        adsTaTE2.setMinimumSize(new java.awt.Dimension(99, 52));
        adsTaTE2.setName("adsTaTE2"); // NOI18N
        adsScrTE2.setViewportView(adsTaTE2);

        adsPanelTE2.add(adsScrTE2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridheight = 2;
        upLeftT2.add(adsPanelTE2, gridBagConstraints);

        subDisLbTE2.setText(resourceMap.getString("subDisLbTE2.text")); // NOI18N
        subDisLbTE2.setName("subDisLbTE2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(subDisLbTE2, gridBagConstraints);

        provinceLbTE2.setText(resourceMap.getString("provinceLbTE2.text")); // NOI18N
        provinceLbTE2.setName("provinceLbTE2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(provinceLbTE2, gridBagConstraints);

        phoneLbTE2.setText(resourceMap.getString("phoneLbTE2.text")); // NOI18N
        phoneLbTE2.setName("phoneLbTE2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(phoneLbTE2, gridBagConstraints);

        countryLbTE2.setText(resourceMap.getString("countryLbTE2.text")); // NOI18N
        countryLbTE2.setName("countryLbTE2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(countryLbTE2, gridBagConstraints);

        noteLbTE2.setText(resourceMap.getString("noteLbTE2.text")); // NOI18N
        noteLbTE2.setName("noteLbTE2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(noteLbTE2, gridBagConstraints);

        subDisTfTE2.setColumns(10);
        subDisTfTE2.setFont(getDefaultFont());
        subDisTfTE2.setMinimumSize(new java.awt.Dimension(86, 25));
        subDisTfTE2.setName("subDisTfTE2"); // NOI18N
        subDisTfTE2.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(subDisTfTE2, gridBagConstraints);

        provinceTfTE2.setColumns(10);
        provinceTfTE2.setFont(getDefaultFont());
        provinceTfTE2.setMinimumSize(new java.awt.Dimension(86, 25));
        provinceTfTE2.setName("provinceTfTE2"); // NOI18N
        provinceTfTE2.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(provinceTfTE2, gridBagConstraints);

        phoneTfTE2.setColumns(10);
        phoneTfTE2.setFont(getDefaultFont());
        phoneTfTE2.setMinimumSize(new java.awt.Dimension(86, 25));
        phoneTfTE2.setName("phoneTfTE2"); // NOI18N
        phoneTfTE2.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(phoneTfTE2, gridBagConstraints);

        countryTfTE2.setColumns(10);
        countryTfTE2.setFont(getDefaultFont());
        countryTfTE2.setMinimumSize(new java.awt.Dimension(86, 25));
        countryTfTE2.setName("countryTfTE2"); // NOI18N
        countryTfTE2.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(countryTfTE2, gridBagConstraints);

        notePanelTE2.setMinimumSize(new java.awt.Dimension(100, 70));
        notePanelTE2.setName("notePanelTE2"); // NOI18N
        notePanelTE2.setPreferredSize(new java.awt.Dimension(100, 70));
        notePanelTE2.setLayout(new java.awt.GridLayout());

        noteScrTE2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        noteScrTE2.setHorizontalScrollBar(null);
        noteScrTE2.setMinimumSize(new java.awt.Dimension(99, 52));
        noteScrTE2.setName("noteScrTE2"); // NOI18N
        noteScrTE2.setPreferredSize(new java.awt.Dimension(99, 52));

        noteTaTE2.setColumns(30);
        noteTaTE2.setFont(getDefaultFont());
        noteTaTE2.setRows(3);
        noteTaTE2.setAutoscrolls(false);
        noteTaTE2.setMinimumSize(new java.awt.Dimension(99, 52));
        noteTaTE2.setName("noteTaTE2"); // NOI18N
        noteScrTE2.setViewportView(noteTaTE2);

        notePanelTE2.add(noteScrTE2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridheight = 2;
        upLeftT2.add(notePanelTE2, gridBagConstraints);

        blankLb4.setName("blankLb4"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        upLeftT2.add(blankLb4, gridBagConstraints);

        blankLb5.setName("blankLb5"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        upLeftT2.add(blankLb5, gridBagConstraints);

        blankLb6.setName("blankLb6"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 0;
        upLeftT2.add(blankLb6, gridBagConstraints);

        upLeftScrollPaneT2.setViewportView(upLeftT2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        queryTab.add(upLeftScrollPaneT2, gridBagConstraints);

        blankPanel14.setMaximumSize(new java.awt.Dimension(10, 10));
        blankPanel14.setName("blankPanel14"); // NOI18N

        javax.swing.GroupLayout blankPanel14Layout = new javax.swing.GroupLayout(blankPanel14);
        blankPanel14.setLayout(blankPanel14Layout);
        blankPanel14Layout.setHorizontalGroup(
            blankPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        blankPanel14Layout.setVerticalGroup(
            blankPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        queryTab.add(blankPanel14, gridBagConstraints);

        upRightT2.setMaximumSize(new java.awt.Dimension(350, 250));
        upRightT2.setMinimumSize(new java.awt.Dimension(310, 250));
        upRightT2.setName("upRightT2"); // NOI18N
        upRightT2.setPreferredSize(new java.awt.Dimension(265, 250));
        upRightT2.setLayout(new java.awt.GridBagLayout());

        genSearchPanelT2.setMinimumSize(new java.awt.Dimension(260, 50));
        genSearchPanelT2.setName("genSearchT2"); // NOI18N
        genSearchPanelT2.setPreferredSize(new java.awt.Dimension(260, 50));

        genSearchTfT2.setText(resourceMap.getString("genSearchTfT2.text")); // NOI18N
        genSearchTfT2.setName("genSearchTfT2"); // NOI18N

        genSearchT2.setFont(getDefaultFont());
        genSearchT2.setText(resourceMap.getString("genSearchT2.text")); // NOI18N
        genSearchT2.setName("genSearchT2"); // NOI18N
        genSearchT2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genSearchT2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout genSearchPanelT2Layout = new javax.swing.GroupLayout(genSearchPanelT2);
        genSearchPanelT2.setLayout(genSearchPanelT2Layout);
        genSearchPanelT2Layout.setHorizontalGroup(
            genSearchPanelT2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(genSearchPanelT2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(genSearchTfT2, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(genSearchT2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        genSearchPanelT2Layout.setVerticalGroup(
            genSearchPanelT2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(genSearchPanelT2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(genSearchPanelT2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(genSearchTfT2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(genSearchT2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        upRightT2.add(genSearchPanelT2, gridBagConstraints);

        quickPanelT2.setMinimumSize(new java.awt.Dimension(330, 200));
        quickPanelT2.setName("quickPanelT2"); // NOI18N
        quickPanelT2.setPreferredSize(new java.awt.Dimension(250, 200));
        quickPanelT2.setLayout(new java.awt.GridBagLayout());

        quickLb.setFont(getDefaultFont());
        quickLb.setText(resourceMap.getString("quickLb.text")); // NOI18N
        quickLb.setName("quickLb"); // NOI18N
        quickLb.setPreferredSize(new java.awt.Dimension(150, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        quickPanelT2.add(quickLb, gridBagConstraints);

        btnPanel0T2.setMaximumSize(new java.awt.Dimension(230, 50));
        btnPanel0T2.setMinimumSize(new java.awt.Dimension(230, 50));
        btnPanel0T2.setName("btnPanel0T2"); // NOI18N
        btnPanel0T2.setPreferredSize(new java.awt.Dimension(230, 50));
        btnPanel0T2.setLayout(new java.awt.GridBagLayout());

        engBtn0T2.setActionCommand(resourceMap.getString("engBtn0T2.actionCommand")); // NOI18N
        engBtn0T2.setLabel(resourceMap.getString("engBtn0T2.label")); // NOI18N
        engBtn0T2.setMaximumSize(new java.awt.Dimension(53, 25));
        engBtn0T2.setMinimumSize(new java.awt.Dimension(53, 25));
        engBtn0T2.setName("engBtn0T2"); // NOI18N
        engBtn0T2.setPreferredSize(new java.awt.Dimension(53, 25));
        engBtn0T2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                engBtn0T2ActionPerformed(evt);
            }
        });
        btnPanel0T2.add(engBtn0T2, new java.awt.GridBagConstraints());

        engBtn1T2.setText(resourceMap.getString("engBtn1T2.text")); // NOI18N
        engBtn1T2.setMaximumSize(new java.awt.Dimension(53, 25));
        engBtn1T2.setMinimumSize(new java.awt.Dimension(53, 25));
        engBtn1T2.setName("engBtn1T2"); // NOI18N
        engBtn1T2.setPreferredSize(new java.awt.Dimension(53, 25));
        engBtn1T2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                engBtn1T2ActionPerformed(evt);
            }
        });
        btnPanel0T2.add(engBtn1T2, new java.awt.GridBagConstraints());

        engBtn2T2.setText(resourceMap.getString("engBtn2T2.text")); // NOI18N
        engBtn2T2.setMaximumSize(new java.awt.Dimension(53, 25));
        engBtn2T2.setMinimumSize(new java.awt.Dimension(53, 25));
        engBtn2T2.setName("engBtn2T2"); // NOI18N
        engBtn2T2.setPreferredSize(new java.awt.Dimension(53, 25));
        engBtn2T2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                engBtn2T2ActionPerformed(evt);
            }
        });
        btnPanel0T2.add(engBtn2T2, new java.awt.GridBagConstraints());

        thBtn0T2.setFont(getDefaultFont());
        thBtn0T2.setText(resourceMap.getString("thBtn0T2.text")); // NOI18N
        thBtn0T2.setMaximumSize(new java.awt.Dimension(53, 25));
        thBtn0T2.setMinimumSize(new java.awt.Dimension(53, 25));
        thBtn0T2.setName("thBtn0T2"); // NOI18N
        thBtn0T2.setPreferredSize(new java.awt.Dimension(53, 25));
        thBtn0T2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                thBtn0T2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        btnPanel0T2.add(thBtn0T2, gridBagConstraints);

        thBtn1T2.setFont(getDefaultFont());
        thBtn1T2.setText(resourceMap.getString("thBtn1T2.text")); // NOI18N
        thBtn1T2.setMaximumSize(new java.awt.Dimension(53, 25));
        thBtn1T2.setMinimumSize(new java.awt.Dimension(53, 25));
        thBtn1T2.setName("thBtn1T2"); // NOI18N
        thBtn1T2.setPreferredSize(new java.awt.Dimension(53, 25));
        thBtn1T2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                thBtn1T2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        btnPanel0T2.add(thBtn1T2, gridBagConstraints);

        thBtn2T2.setFont(getDefaultFont());
        thBtn2T2.setText(resourceMap.getString("thBtn2T2.text")); // NOI18N
        thBtn2T2.setMaximumSize(new java.awt.Dimension(53, 25));
        thBtn2T2.setMinimumSize(new java.awt.Dimension(53, 25));
        thBtn2T2.setName("thBtn2T2"); // NOI18N
        thBtn2T2.setPreferredSize(new java.awt.Dimension(53, 25));
        thBtn2T2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                thBtn2T2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        btnPanel0T2.add(thBtn2T2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        quickPanelT2.add(btnPanel0T2, gridBagConstraints);

        btnPanel1T2.setMaximumSize(new java.awt.Dimension(230, 50));
        btnPanel1T2.setMinimumSize(new java.awt.Dimension(230, 50));
        btnPanel1T2.setName("btnPanel1T2"); // NOI18N
        btnPanel1T2.setPreferredSize(new java.awt.Dimension(230, 50));
        btnPanel1T2.setLayout(new java.awt.GridBagLayout());

        engBtn3T2.setText(resourceMap.getString("engBtn3T2.text")); // NOI18N
        engBtn3T2.setMaximumSize(new java.awt.Dimension(53, 25));
        engBtn3T2.setMinimumSize(new java.awt.Dimension(53, 25));
        engBtn3T2.setName("engBtn3T2"); // NOI18N
        engBtn3T2.setPreferredSize(new java.awt.Dimension(53, 25));
        engBtn3T2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                engBtn3T2ActionPerformed(evt);
            }
        });
        btnPanel1T2.add(engBtn3T2, new java.awt.GridBagConstraints());

        thBtn3T2.setFont(getDefaultFont());
        thBtn3T2.setText(resourceMap.getString("thBtn3T2.text")); // NOI18N
        thBtn3T2.setMaximumSize(new java.awt.Dimension(53, 25));
        thBtn3T2.setMinimumSize(new java.awt.Dimension(53, 25));
        thBtn3T2.setName("thBtn3T2"); // NOI18N
        thBtn3T2.setPreferredSize(new java.awt.Dimension(53, 25));
        thBtn3T2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                thBtn3T2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        btnPanel1T2.add(thBtn3T2, gridBagConstraints);

        engBtn4T2.setText(resourceMap.getString("engBtn4T2.text")); // NOI18N
        engBtn4T2.setMaximumSize(new java.awt.Dimension(53, 25));
        engBtn4T2.setMinimumSize(new java.awt.Dimension(53, 25));
        engBtn4T2.setName("engBtn4T2"); // NOI18N
        engBtn4T2.setPreferredSize(new java.awt.Dimension(53, 25));
        engBtn4T2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                engBtn4T2ActionPerformed(evt);
            }
        });
        btnPanel1T2.add(engBtn4T2, new java.awt.GridBagConstraints());

        thBtn4T2.setFont(getDefaultFont());
        thBtn4T2.setText(resourceMap.getString("thBtn4T2.text")); // NOI18N
        thBtn4T2.setName("thBtn4T2"); // NOI18N
        thBtn4T2.setPreferredSize(new java.awt.Dimension(53, 25));
        thBtn4T2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                thBtn4T2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        btnPanel1T2.add(thBtn4T2, gridBagConstraints);

        engBtn5T2.setText(resourceMap.getString("engBtn5T2.text")); // NOI18N
        engBtn5T2.setMaximumSize(new java.awt.Dimension(53, 25));
        engBtn5T2.setMinimumSize(new java.awt.Dimension(53, 25));
        engBtn5T2.setName("engBtn5T2"); // NOI18N
        engBtn5T2.setPreferredSize(new java.awt.Dimension(53, 25));
        engBtn5T2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                engBtn5T2ActionPerformed(evt);
            }
        });
        btnPanel1T2.add(engBtn5T2, new java.awt.GridBagConstraints());

        thBtn5T2.setFont(getDefaultFont());
        thBtn5T2.setText(resourceMap.getString("thBtn5T2.text")); // NOI18N
        thBtn5T2.setMaximumSize(new java.awt.Dimension(53, 25));
        thBtn5T2.setMinimumSize(new java.awt.Dimension(53, 25));
        thBtn5T2.setName("thBtn5T2"); // NOI18N
        thBtn5T2.setPreferredSize(new java.awt.Dimension(53, 25));
        thBtn5T2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                thBtn5T2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        btnPanel1T2.add(thBtn5T2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        quickPanelT2.add(btnPanel1T2, gridBagConstraints);

        btnPanel2T2.setMaximumSize(new java.awt.Dimension(230, 50));
        btnPanel2T2.setMinimumSize(new java.awt.Dimension(230, 50));
        btnPanel2T2.setName("btnPanel2T2"); // NOI18N
        btnPanel2T2.setPreferredSize(new java.awt.Dimension(230, 50));
        btnPanel2T2.setLayout(new java.awt.GridBagLayout());

        engBtn6T2.setText(resourceMap.getString("engBtn6T2.text")); // NOI18N
        engBtn6T2.setMaximumSize(new java.awt.Dimension(53, 25));
        engBtn6T2.setMinimumSize(new java.awt.Dimension(53, 25));
        engBtn6T2.setName("engBtn6T2"); // NOI18N
        engBtn6T2.setPreferredSize(new java.awt.Dimension(53, 25));
        engBtn6T2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                engBtn6T2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        btnPanel2T2.add(engBtn6T2, gridBagConstraints);

        engBtn7T2.setText(resourceMap.getString("engBtn7T2.text")); // NOI18N
        engBtn7T2.setMaximumSize(new java.awt.Dimension(53, 25));
        engBtn7T2.setMinimumSize(new java.awt.Dimension(53, 25));
        engBtn7T2.setName("engBtn7T2"); // NOI18N
        engBtn7T2.setPreferredSize(new java.awt.Dimension(53, 25));
        engBtn7T2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                engBtn7T2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        btnPanel2T2.add(engBtn7T2, gridBagConstraints);

        engBtn8T2.setText(resourceMap.getString("engBtn8T2.text")); // NOI18N
        engBtn8T2.setMaximumSize(new java.awt.Dimension(53, 25));
        engBtn8T2.setMinimumSize(new java.awt.Dimension(53, 25));
        engBtn8T2.setName("engBtn8T2"); // NOI18N
        engBtn8T2.setPreferredSize(new java.awt.Dimension(53, 25));
        engBtn8T2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                engBtn8T2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        btnPanel2T2.add(engBtn8T2, gridBagConstraints);

        thBtn8T2.setFont(getDefaultFont());
        thBtn8T2.setText(resourceMap.getString("thBtn8T2.text")); // NOI18N
        thBtn8T2.setMaximumSize(new java.awt.Dimension(53, 25));
        thBtn8T2.setMinimumSize(new java.awt.Dimension(53, 25));
        thBtn8T2.setName("thBtn8T2"); // NOI18N
        thBtn8T2.setPreferredSize(new java.awt.Dimension(53, 25));
        thBtn8T2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                thBtn8T2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        btnPanel2T2.add(thBtn8T2, gridBagConstraints);

        thBtn7T2.setFont(getDefaultFont());
        thBtn7T2.setText(resourceMap.getString("thBtn7T2.text")); // NOI18N
        thBtn7T2.setMaximumSize(new java.awt.Dimension(53, 25));
        thBtn7T2.setMinimumSize(new java.awt.Dimension(53, 25));
        thBtn7T2.setName("thBtn7T2"); // NOI18N
        thBtn7T2.setPreferredSize(new java.awt.Dimension(53, 25));
        thBtn7T2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                thBtn7T2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        btnPanel2T2.add(thBtn7T2, gridBagConstraints);

        thBtn6T2.setFont(getDefaultFont());
        thBtn6T2.setText(resourceMap.getString("thBtn6T2.text")); // NOI18N
        thBtn6T2.setMaximumSize(new java.awt.Dimension(53, 25));
        thBtn6T2.setMinimumSize(new java.awt.Dimension(53, 25));
        thBtn6T2.setName("thBtn6T2"); // NOI18N
        thBtn6T2.setPreferredSize(new java.awt.Dimension(53, 25));
        thBtn6T2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                thBtn6T2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        btnPanel2T2.add(thBtn6T2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        quickPanelT2.add(btnPanel2T2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        upRightT2.add(quickPanelT2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        queryTab.add(upRightT2, gridBagConstraints);

        lowPanelT2.setName("lowPanelT2"); // NOI18N
        lowPanelT2.setPreferredSize(new java.awt.Dimension(650, 340));
        lowPanelT2.setLayout(new java.awt.GridBagLayout());

        tablePanelT2.setMinimumSize(new java.awt.Dimension(640, 270));
        tablePanelT2.setName("tablePanelT2"); // NOI18N
        tablePanelT2.setPreferredSize(new java.awt.Dimension(640, 270));

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        importTableT2.setFont(getDefaultFont());
        importTableT2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "เลือก", "ชื่อ", "นามสกุล", "Name", "Last Name", "บริษัท", "ตำแหน่ง", "โทรศัพท์", "โทรศัพท์มือถือ", "id"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Long.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        importTableT2.setName("importTableT2"); // NOI18N
        TableColumn col = importTableT2.getColumnModel().getColumn(9);
        xCol.setAllColumnsVisible();
        xCol.setColumnVisible(col, false);

        JTableHeader header = importTableT2.getTableHeader();
        final TableCellRenderer headerRenderer = header.getDefaultRenderer();
        header.setDefaultRenderer( new TableCellRenderer() {
            public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column ) {
                Component comp = headerRenderer.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );
                comp.setFont(getDefaultFont());
                return comp;
            }
        });
        importTableT2.setTableHeader(header);
        jScrollPane4.setViewportView(importTableT2);

        javax.swing.GroupLayout tablePanelT2Layout = new javax.swing.GroupLayout(tablePanelT2);
        tablePanelT2.setLayout(tablePanelT2Layout);
        tablePanelT2Layout.setHorizontalGroup(
            tablePanelT2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tablePanelT2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE)
                .addContainerGap())
        );
        tablePanelT2Layout.setVerticalGroup(
            tablePanelT2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tablePanelT2Layout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                .addGap(11, 11, 11))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        lowPanelT2.add(tablePanelT2, gridBagConstraints);

        deleteEditPanelT2.setMinimumSize(new java.awt.Dimension(200, 40));
        deleteEditPanelT2.setName("deleteEditPanelT2"); // NOI18N
        deleteEditPanelT2.setPreferredSize(new java.awt.Dimension(200, 40));
        deleteEditPanelT2.setLayout(new java.awt.GridLayout());

        deletedBtnT2.setFont(getDefaultFont());
        deletedBtnT2.setText(resourceMap.getString("deletedBtnT2.text")); // NOI18N
        deletedBtnT2.setMaximumSize(new java.awt.Dimension(50, 40));
        deletedBtnT2.setMinimumSize(new java.awt.Dimension(50, 40));
        deletedBtnT2.setName("deletedBtnT2"); // NOI18N
        deletedBtnT2.setPreferredSize(new java.awt.Dimension(50, 40));
        deletedBtnT2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deletedBtnT2ActionPerformed(evt);
            }
        });
        deleteEditPanelT2.add(deletedBtnT2);

        editBtnT2.setFont(getDefaultFont());
        editBtnT2.setText(resourceMap.getString("editBtnT2.text")); // NOI18N
        editBtnT2.setMaximumSize(new java.awt.Dimension(50, 40));
        editBtnT2.setMinimumSize(new java.awt.Dimension(50, 40));
        editBtnT2.setName("editBtnT2"); // NOI18N
        editBtnT2.setPreferredSize(new java.awt.Dimension(50, 40));
        editBtnT2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editBtnT2ActionPerformed(evt);
            }
        });
        deleteEditPanelT2.add(editBtnT2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        lowPanelT2.add(deleteEditPanelT2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        queryTab.add(lowPanelT2, gridBagConstraints);

        blankPanel6.setName("blankPanel6"); // NOI18N

        javax.swing.GroupLayout blankPanel6Layout = new javax.swing.GroupLayout(blankPanel6);
        blankPanel6.setLayout(blankPanel6Layout);
        blankPanel6Layout.setHorizontalGroup(
            blankPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        blankPanel6Layout.setVerticalGroup(
            blankPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        queryTab.add(blankPanel6, gridBagConstraints);

        menuTab.addTab(resourceMap.getString("queryTab.TabConstraints.tabTitle"), queryTab); // NOI18N

        resultTab.setMaximumSize(new java.awt.Dimension(680, 630));
        resultTab.setMinimumSize(new java.awt.Dimension(680, 630));
        resultTab.setName("resultTab"); // NOI18N
        resultTab.setPreferredSize(new java.awt.Dimension(680, 630));
        resultTab.setLayout(new java.awt.GridBagLayout());

        upRightT3.setMaximumSize(new java.awt.Dimension(230, 250));
        upRightT3.setMinimumSize(new java.awt.Dimension(230, 250));
        upRightT3.setName("upRightT3"); // NOI18N
        upRightT3.setPreferredSize(new java.awt.Dimension(230, 280));
        upRightT3.setLayout(new java.awt.GridBagLayout());

        idPanelT3.setMaximumSize(new java.awt.Dimension(150, 22));
        idPanelT3.setMinimumSize(new java.awt.Dimension(150, 22));
        idPanelT3.setName("idPanelT3"); // NOI18N

        idLbT3.setFont(getDefaultFont());
        idLbT3.setText(resourceMap.getString("idLbT3.text")); // NOI18N
        idLbT3.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        idLbT3.setName("idLbT3"); // NOI18N

        idNameLbT3.setMaximumSize(new java.awt.Dimension(400, 20));
        idNameLbT3.setMinimumSize(new java.awt.Dimension(300, 20));
        idNameLbT3.setName("idNameLbT3"); // NOI18N
        idNameLbT3.setPreferredSize(new java.awt.Dimension(400, 20));

        javax.swing.GroupLayout idPanelT3Layout = new javax.swing.GroupLayout(idPanelT3);
        idPanelT3.setLayout(idPanelT3Layout);
        idPanelT3Layout.setHorizontalGroup(
            idPanelT3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(idPanelT3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(idLbT3)
                .addGap(18, 18, 18)
                .addComponent(idNameLbT3, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        idPanelT3Layout.setVerticalGroup(
            idPanelT3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(idPanelT3Layout.createSequentialGroup()
                .addGroup(idPanelT3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(idLbT3)
                    .addComponent(idNameLbT3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        upRightT3.add(idPanelT3, gridBagConstraints);

        autoCropBtnT3.setFont(getDefaultFont());
        autoCropBtnT3.setText(resourceMap.getString("autoCropBtnT3.text")); // NOI18N
        autoCropBtnT3.setMaximumSize(new java.awt.Dimension(100, 31));
        autoCropBtnT3.setMinimumSize(new java.awt.Dimension(100, 31));
        autoCropBtnT3.setName("autoCropBtnT3"); // NOI18N
        autoCropBtnT3.setPreferredSize(new java.awt.Dimension(100, 31));
        autoCropBtnT3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoCropBtnT3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        upRightT3.add(autoCropBtnT3, gridBagConstraints);

        blackWhiteBtnT3.setFont(getDefaultFont());
        blackWhiteBtnT3.setText(resourceMap.getString("blackWhiteBtnT3.text")); // NOI18N
        blackWhiteBtnT3.setMaximumSize(new java.awt.Dimension(100, 31));
        blackWhiteBtnT3.setMinimumSize(new java.awt.Dimension(100, 31));
        blackWhiteBtnT3.setName("blackWhiteBtnT3"); // NOI18N
        blackWhiteBtnT3.setPreferredSize(new java.awt.Dimension(100, 31));
        blackWhiteBtnT3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                blackWhiteBtnT3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        upRightT3.add(blackWhiteBtnT3, gridBagConstraints);

        rotateBtnT3.setFont(getDefaultFont());
        rotateBtnT3.setText(resourceMap.getString("rotateBtnT3.text")); // NOI18N
        rotateBtnT3.setMaximumSize(new java.awt.Dimension(100, 31));
        rotateBtnT3.setMinimumSize(new java.awt.Dimension(100, 31));
        rotateBtnT3.setName("rotateBtnT3"); // NOI18N
        rotateBtnT3.setPreferredSize(new java.awt.Dimension(100, 31));
        rotateBtnT3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rotateBtnT3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        upRightT3.add(rotateBtnT3, gridBagConstraints);

        emailBtnT3.setFont(getDefaultFont());
        emailBtnT3.setText(resourceMap.getString("emailBtnT3.text")); // NOI18N
        emailBtnT3.setMaximumSize(new java.awt.Dimension(100, 31));
        emailBtnT3.setMinimumSize(new java.awt.Dimension(100, 31));
        emailBtnT3.setName("emailBtnT3"); // NOI18N
        emailBtnT3.setPreferredSize(new java.awt.Dimension(100, 31));
        emailBtnT3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emailBtnT3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        upRightT3.add(emailBtnT3, gridBagConstraints);

        undoBtnT3.setFont(getDefaultFont());
        undoBtnT3.setText(resourceMap.getString("undoBtnT3.text")); // NOI18N
        undoBtnT3.setName("undoBtnT3"); // NOI18N
        undoBtnT3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoBtnT3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        upRightT3.add(undoBtnT3, gridBagConstraints);

        confirmBtnT3.setFont(getDefaultFont());
        confirmBtnT3.setText(resourceMap.getString("confirmBtnT3.text")); // NOI18N
        confirmBtnT3.setName("confirmBtnT3"); // NOI18N
        confirmBtnT3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirmBtnT3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        upRightT3.add(confirmBtnT3, gridBagConstraints);

        brightPanelT3.setMinimumSize(new java.awt.Dimension(242, 60));
        brightPanelT3.setName("brightPanelT3"); // NOI18N
        brightPanelT3.setPreferredSize(new java.awt.Dimension(242, 60));
        brightPanelT3.setRequestFocusEnabled(false);
        brightPanelT3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        brightLbT3.setFont(getDefaultFont());
        brightLbT3.setText(resourceMap.getString("brightLbT3.text")); // NOI18N
        brightLbT3.setName("brightLbT3"); // NOI18N
        brightPanelT3.add(brightLbT3);

        brightSldT3.setMajorTickSpacing(1);
        brightSldT3.setMaximum(5);
        brightSldT3.setMinimum(1);
        brightSldT3.setMinorTickSpacing(1);
        brightSldT3.setPaintLabels(true);
        brightSldT3.setSnapToTicks(true);
        brightSldT3.setValue(3);
        brightSldT3.setMaximumSize(new java.awt.Dimension(32767, 35));
        brightSldT3.setMinimumSize(new java.awt.Dimension(50, 35));
        brightSldT3.setName("brightSldT3"); // NOI18N
        brightSldT3.setPreferredSize(new java.awt.Dimension(150, 60));
        brightSldT3.setLabelTable( labelTable );
        brightSldT3.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                brightSldT3StateChanged(evt);
            }
        });
        brightPanelT3.add(brightSldT3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        upRightT3.add(brightPanelT3, gridBagConstraints);

        selSidePanelT3.setMinimumSize(new java.awt.Dimension(248, 30));
        selSidePanelT3.setName("selSidePanelT3"); // NOI18N
        selSidePanelT3.setPreferredSize(new java.awt.Dimension(248, 30));

        sideLbT3.setFont(getDefaultFont());
        sideLbT3.setText(resourceMap.getString("sideLbT3.text")); // NOI18N
        sideLbT3.setName("sideLbT3"); // NOI18N
        selSidePanelT3.add(sideLbT3);

        frontSideRdT3.setFont(getDefaultFont());
        frontSideRdT3.setText(resourceMap.getString("frontSideRdT3.text")); // NOI18N
        frontSideRdT3.setName("frontSideRdT3"); // NOI18N
        frontSideRdT3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                frontSideRdT3ActionPerformed(evt);
            }
        });
        selSidePanelT3.add(frontSideRdT3);

        backSideRdT3.setFont(getDefaultFont());
        backSideRdT3.setText(resourceMap.getString("backSideRdT3.text")); // NOI18N
        backSideRdT3.setName("backSideRdT3"); // NOI18N
        backSideRdT3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backSideRdT3ActionPerformed(evt);
            }
        });
        selSidePanelT3.add(backSideRdT3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        upRightT3.add(selSidePanelT3, gridBagConstraints);

        saveBtnT3.setFont(getDefaultFont());
        saveBtnT3.setText(resourceMap.getString("saveBtnT3.text")); // NOI18N
        saveBtnT3.setMaximumSize(new java.awt.Dimension(100, 31));
        saveBtnT3.setMinimumSize(new java.awt.Dimension(100, 31));
        saveBtnT3.setName("saveBtnT3"); // NOI18N
        saveBtnT3.setPreferredSize(new java.awt.Dimension(100, 31));
        saveBtnT3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveBtnT3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        upRightT3.add(saveBtnT3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        resultTab.add(upRightT3, gridBagConstraints);

        lowT3.setName("lowT3"); // NOI18N
        lowT3.setPreferredSize(new java.awt.Dimension(650, 340));
        lowT3.setLayout(new java.awt.GridBagLayout());

        lowLeftT3.setMinimumSize(new java.awt.Dimension(335, 335));
        lowLeftT3.setName("lowLeftT3"); // NOI18N
        lowLeftT3.setPreferredSize(new java.awt.Dimension(337, 335));

        frontTfT3.setEditable(false);
        frontTfT3.setFont(getDefaultFont());
        frontTfT3.setName("frontTfT3"); // NOI18N
        frontTfT3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                frontTfT3MouseClicked(evt);
            }
        });

        frontBtnT3.setFont(getDefaultFont());
        frontBtnT3.setText(resourceMap.getString("frontBtnT3.text")); // NOI18N
        frontBtnT3.setName("frontBtnT3"); // NOI18N
        frontBtnT3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                frontBtnT3ActionPerformed(evt);
            }
        });

        frontSpT3.setName("frontSpT3"); // NOI18N
        frontSpT3.setPreferredSize(new java.awt.Dimension(100, 260));

        frontCropBtnT3.setFont(getDefaultFont());
        frontCropBtnT3.setText(resourceMap.getString("frontCropBtnT3.text")); // NOI18N
        frontCropBtnT3.setName("frontCropBtnT3"); // NOI18N
        frontCropBtnT3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                frontCropBtnT3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout lowLeftT3Layout = new javax.swing.GroupLayout(lowLeftT3);
        lowLeftT3.setLayout(lowLeftT3Layout);
        lowLeftT3Layout.setHorizontalGroup(
            lowLeftT3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lowLeftT3Layout.createSequentialGroup()
                .addGroup(lowLeftT3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, lowLeftT3Layout.createSequentialGroup()
                        .addComponent(frontTfT3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(frontBtnT3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(frontCropBtnT3))
                    .addComponent(frontSpT3, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        lowLeftT3Layout.setVerticalGroup(
            lowLeftT3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lowLeftT3Layout.createSequentialGroup()
                .addGroup(lowLeftT3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(frontCropBtnT3)
                    .addComponent(frontBtnT3)
                    .addComponent(frontTfT3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(frontSpT3, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        lowT3.add(lowLeftT3, gridBagConstraints);

        lowRightT3.setMinimumSize(new java.awt.Dimension(350, 335));
        lowRightT3.setName("lowRightT3"); // NOI18N
        lowRightT3.setPreferredSize(new java.awt.Dimension(350, 335));

        backTfT3.setEditable(false);
        backTfT3.setFont(getDefaultFont());
        backTfT3.setName("backTfT3"); // NOI18N
        backTfT3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                backTfT3MouseClicked(evt);
            }
        });

        backBtnT3.setFont(getDefaultFont());
        backBtnT3.setText(resourceMap.getString("backBtnT3.text")); // NOI18N
        backBtnT3.setName("backBtnT3"); // NOI18N
        backBtnT3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backBtnT3ActionPerformed(evt);
            }
        });

        backSpT3.setName("backSpT3"); // NOI18N
        backSpT3.setPreferredSize(new java.awt.Dimension(305, 260));

        backCropBtnT3.setFont(getDefaultFont());
        backCropBtnT3.setText(resourceMap.getString("backCropBtnT3.text")); // NOI18N
        backCropBtnT3.setName("backCropBtnT3"); // NOI18N
        backCropBtnT3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backCropBtnT3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout lowRightT3Layout = new javax.swing.GroupLayout(lowRightT3);
        lowRightT3.setLayout(lowRightT3Layout);
        lowRightT3Layout.setHorizontalGroup(
            lowRightT3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lowRightT3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lowRightT3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, lowRightT3Layout.createSequentialGroup()
                        .addComponent(backTfT3, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(backBtnT3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(backCropBtnT3)
                        .addGap(20, 20, 20))
                    .addGroup(lowRightT3Layout.createSequentialGroup()
                        .addComponent(backSpT3, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
                        .addGap(21, 21, 21))))
        );
        lowRightT3Layout.setVerticalGroup(
            lowRightT3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lowRightT3Layout.createSequentialGroup()
                .addGroup(lowRightT3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(backTfT3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(backBtnT3)
                    .addComponent(backCropBtnT3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(backSpT3, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        lowT3.add(lowRightT3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        resultTab.add(lowT3, gridBagConstraints);

        upLeftScrollPaneT3.setMinimumSize(new java.awt.Dimension(350, 260));
        upLeftScrollPaneT3.setName("upLeftScrollPaneT3"); // NOI18N
        upLeftScrollPaneT3.setPreferredSize(new java.awt.Dimension(390, 260));

        upLeftT3.setMinimumSize(new java.awt.Dimension(720, 260));
        upLeftT3.setName("upLeftT3"); // NOI18N
        upLeftT3.setPreferredSize(new java.awt.Dimension(720, 260));
        upLeftT3.setLayout(new java.awt.GridBagLayout());

        nameLbT3.setFont(getDefaultFont());
        nameLbT3.setText(resourceMap.getString("nameLbT3.text")); // NOI18N
        nameLbT3.setMaximumSize(new java.awt.Dimension(86, 15));
        nameLbT3.setMinimumSize(new java.awt.Dimension(86, 15));
        nameLbT3.setName("nameLbT3"); // NOI18N
        nameLbT3.setPreferredSize(new java.awt.Dimension(86, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(nameLbT3, gridBagConstraints);

        titleLbT3.setFont(getDefaultFont());
        titleLbT3.setText(resourceMap.getString("titleLbT3.text")); // NOI18N
        titleLbT3.setMaximumSize(new java.awt.Dimension(86, 15));
        titleLbT3.setMinimumSize(new java.awt.Dimension(86, 15));
        titleLbT3.setName("titleLbT3"); // NOI18N
        titleLbT3.setPreferredSize(new java.awt.Dimension(86, 15));
        titleLbT3.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(titleLbT3, gridBagConstraints);

        companyLbT3.setFont(getDefaultFont());
        companyLbT3.setText(resourceMap.getString("companyLbT3.text")); // NOI18N
        companyLbT3.setMaximumSize(new java.awt.Dimension(86, 15));
        companyLbT3.setMinimumSize(new java.awt.Dimension(86, 15));
        companyLbT3.setName("companyLbT3"); // NOI18N
        companyLbT3.setPreferredSize(new java.awt.Dimension(86, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(companyLbT3, gridBagConstraints);

        disLbT3.setFont(getDefaultFont());
        disLbT3.setText(resourceMap.getString("disLbT3.text")); // NOI18N
        disLbT3.setMaximumSize(new java.awt.Dimension(86, 15));
        disLbT3.setMinimumSize(new java.awt.Dimension(86, 15));
        disLbT3.setName("disLbT3"); // NOI18N
        disLbT3.setPreferredSize(new java.awt.Dimension(86, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(disLbT3, gridBagConstraints);

        codeLbT3.setFont(getDefaultFont());
        codeLbT3.setText(resourceMap.getString("codeLbT3.text")); // NOI18N
        codeLbT3.setMaximumSize(new java.awt.Dimension(86, 15));
        codeLbT3.setMinimumSize(new java.awt.Dimension(86, 15));
        codeLbT3.setName("codeLbT3"); // NOI18N
        codeLbT3.setPreferredSize(new java.awt.Dimension(86, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(codeLbT3, gridBagConstraints);

        mobileLbT3.setFont(getDefaultFont());
        mobileLbT3.setText(resourceMap.getString("mobileLbT3.text")); // NOI18N
        mobileLbT3.setMaximumSize(new java.awt.Dimension(86, 15));
        mobileLbT3.setMinimumSize(new java.awt.Dimension(86, 15));
        mobileLbT3.setName("mobileLbT3"); // NOI18N
        mobileLbT3.setPreferredSize(new java.awt.Dimension(86, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(mobileLbT3, gridBagConstraints);

        faxLbT3.setFont(getDefaultFont());
        faxLbT3.setText(resourceMap.getString("faxLbT3.text")); // NOI18N
        faxLbT3.setMaximumSize(new java.awt.Dimension(86, 15));
        faxLbT3.setMinimumSize(new java.awt.Dimension(86, 15));
        faxLbT3.setName("faxLbT3"); // NOI18N
        faxLbT3.setPreferredSize(new java.awt.Dimension(86, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(faxLbT3, gridBagConstraints);

        adsLbT3.setFont(getDefaultFont());
        adsLbT3.setText(resourceMap.getString("adsLbT3.text")); // NOI18N
        adsLbT3.setMaximumSize(new java.awt.Dimension(86, 15));
        adsLbT3.setMinimumSize(new java.awt.Dimension(86, 15));
        adsLbT3.setName("adsLbT3"); // NOI18N
        adsLbT3.setPreferredSize(new java.awt.Dimension(86, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(adsLbT3, gridBagConstraints);

        lastnameLbT3.setFont(getDefaultFont());
        lastnameLbT3.setText(resourceMap.getString("lastnameLbT3.text")); // NOI18N
        lastnameLbT3.setName("lastnameLbT3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(lastnameLbT3, gridBagConstraints);

        emailLbT3.setFont(getDefaultFont());
        emailLbT3.setText(resourceMap.getString("emailLbT3.text")); // NOI18N
        emailLbT3.setName("emailLbT3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(emailLbT3, gridBagConstraints);

        webLbT3.setFont(getDefaultFont());
        webLbT3.setText(resourceMap.getString("webLbT3.text")); // NOI18N
        webLbT3.setName("webLbT3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(webLbT3, gridBagConstraints);

        subDisLbT3.setFont(getDefaultFont());
        subDisLbT3.setText(resourceMap.getString("subDisLbT3.text")); // NOI18N
        subDisLbT3.setName("subDisLbT3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(subDisLbT3, gridBagConstraints);

        provinceLbT3.setFont(getDefaultFont());
        provinceLbT3.setText(resourceMap.getString("provinceLbT3.text")); // NOI18N
        provinceLbT3.setName("provinceLbT3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(provinceLbT3, gridBagConstraints);

        phoneLbT3.setFont(getDefaultFont());
        phoneLbT3.setText(resourceMap.getString("phoneLbT3.text")); // NOI18N
        phoneLbT3.setName("phoneLbT3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(phoneLbT3, gridBagConstraints);

        noteLbT3.setFont(getDefaultFont());
        noteLbT3.setText(resourceMap.getString("noteLbT3.text")); // NOI18N
        noteLbT3.setName("noteLbT3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(noteLbT3, gridBagConstraints);

        countryLbT3.setFont(getDefaultFont());
        countryLbT3.setText(resourceMap.getString("countryLbT3.text")); // NOI18N
        countryLbT3.setName("countryLbT3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(countryLbT3, gridBagConstraints);

        nameTfT3.setColumns(10);
        nameTfT3.setFont(getDefaultFont());
        nameTfT3.setMinimumSize(new java.awt.Dimension(86, 25));
        nameTfT3.setName("nameTfT3"); // NOI18N
        nameTfT3.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(nameTfT3, gridBagConstraints);

        titleTfT3.setColumns(10);
        titleTfT3.setFont(getDefaultFont());
        titleTfT3.setMinimumSize(new java.awt.Dimension(86, 25));
        titleTfT3.setName("titleTfT3"); // NOI18N
        titleTfT3.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(titleTfT3, gridBagConstraints);

        companyTfT3.setColumns(10);
        companyTfT3.setFont(getDefaultFont());
        companyTfT3.setMinimumSize(new java.awt.Dimension(86, 25));
        companyTfT3.setName("companyTfT3"); // NOI18N
        companyTfT3.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(companyTfT3, gridBagConstraints);

        disTfT3.setColumns(10);
        disTfT3.setFont(getDefaultFont());
        disTfT3.setMinimumSize(new java.awt.Dimension(86, 25));
        disTfT3.setName("disTfT3"); // NOI18N
        disTfT3.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(disTfT3, gridBagConstraints);

        codeTfT3.setColumns(10);
        codeTfT3.setFont(getDefaultFont());
        codeTfT3.setMinimumSize(new java.awt.Dimension(86, 25));
        codeTfT3.setName("codeTfT3"); // NOI18N
        codeTfT3.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(codeTfT3, gridBagConstraints);

        mobileTfT3.setColumns(10);
        mobileTfT3.setFont(getDefaultFont());
        mobileTfT3.setMinimumSize(new java.awt.Dimension(86, 25));
        mobileTfT3.setName("mobileTfT3"); // NOI18N
        mobileTfT3.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(mobileTfT3, gridBagConstraints);

        faxTfT3.setColumns(10);
        faxTfT3.setFont(getDefaultFont());
        faxTfT3.setMinimumSize(new java.awt.Dimension(86, 25));
        faxTfT3.setName("faxTfT3"); // NOI18N
        faxTfT3.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(faxTfT3, gridBagConstraints);

        lastnameTfT3.setColumns(10);
        lastnameTfT3.setFont(getDefaultFont());
        lastnameTfT3.setMinimumSize(new java.awt.Dimension(86, 25));
        lastnameTfT3.setName("lastnameTfT3"); // NOI18N
        lastnameTfT3.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(lastnameTfT3, gridBagConstraints);

        emailTfT3.setColumns(10);
        emailTfT3.setFont(getDefaultFont());
        emailTfT3.setMinimumSize(new java.awt.Dimension(86, 25));
        emailTfT3.setName("emailTfT3"); // NOI18N
        emailTfT3.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(emailTfT3, gridBagConstraints);

        webTfT3.setColumns(10);
        webTfT3.setFont(getDefaultFont());
        webTfT3.setMinimumSize(new java.awt.Dimension(86, 25));
        webTfT3.setName("webTfT3"); // NOI18N
        webTfT3.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(webTfT3, gridBagConstraints);

        subDisTfT3.setColumns(10);
        subDisTfT3.setFont(getDefaultFont());
        subDisTfT3.setMinimumSize(new java.awt.Dimension(86, 25));
        subDisTfT3.setName("subDisTfT3"); // NOI18N
        subDisTfT3.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(subDisTfT3, gridBagConstraints);

        provinceTfT3.setColumns(10);
        provinceTfT3.setFont(getDefaultFont());
        provinceTfT3.setMinimumSize(new java.awt.Dimension(86, 25));
        provinceTfT3.setName("provinceTfT3"); // NOI18N
        provinceTfT3.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(provinceTfT3, gridBagConstraints);

        phoneTfT3.setColumns(10);
        phoneTfT3.setFont(getDefaultFont());
        phoneTfT3.setMinimumSize(new java.awt.Dimension(86, 25));
        phoneTfT3.setName("phoneTfT3"); // NOI18N
        phoneTfT3.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(phoneTfT3, gridBagConstraints);

        countryTfT3.setColumns(10);
        countryTfT3.setFont(getDefaultFont());
        countryTfT3.setMinimumSize(new java.awt.Dimension(86, 25));
        countryTfT3.setName("countryTfT3"); // NOI18N
        countryTfT3.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(countryTfT3, gridBagConstraints);

        adsPanelT3.setMinimumSize(new java.awt.Dimension(99, 70));
        adsPanelT3.setName("adsPanelT3"); // NOI18N
        adsPanelT3.setPreferredSize(new java.awt.Dimension(99, 70));
        adsPanelT3.setLayout(new java.awt.GridLayout());

        adsScrT3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        adsScrT3.setHorizontalScrollBar(null);
        adsScrT3.setMinimumSize(new java.awt.Dimension(99, 52));
        adsScrT3.setName("adsScrT3"); // NOI18N
        adsScrT3.setPreferredSize(new java.awt.Dimension(99, 52));

        adsTaT3.setColumns(30);
        adsTaT3.setFont(getDefaultFont());
        adsTaT3.setLineWrap(true);
        adsTaT3.setRows(3);
        adsTaT3.setAutoscrolls(false);
        adsTaT3.setMinimumSize(new java.awt.Dimension(99, 52));
        adsTaT3.setName("adsTaT3"); // NOI18N
        adsScrT3.setViewportView(adsTaT3);

        adsPanelT3.add(adsScrT3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridheight = 2;
        upLeftT3.add(adsPanelT3, gridBagConstraints);

        notePanelT3.setMinimumSize(new java.awt.Dimension(100, 70));
        notePanelT3.setName("notePanelT3"); // NOI18N
        notePanelT3.setPreferredSize(new java.awt.Dimension(100, 70));
        notePanelT3.setLayout(new java.awt.GridLayout());

        noteScrT3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        noteScrT3.setHorizontalScrollBar(null);
        noteScrT3.setMinimumSize(new java.awt.Dimension(99, 52));
        noteScrT3.setName("noteScrT3"); // NOI18N
        noteScrT3.setPreferredSize(new java.awt.Dimension(99, 52));

        noteTaT3.setColumns(30);
        noteTaT3.setFont(getDefaultFont());
        noteTaT3.setLineWrap(true);
        noteTaT3.setRows(3);
        noteTaT3.setAutoscrolls(false);
        noteTaT3.setMinimumSize(new java.awt.Dimension(99, 52));
        noteTaT3.setName("noteTaT3"); // NOI18N
        noteScrT3.setViewportView(noteTaT3);

        notePanelT3.add(noteScrT3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridheight = 2;
        upLeftT3.add(notePanelT3, gridBagConstraints);

        nameLbTE3.setText(resourceMap.getString("nameLbTE3.text")); // NOI18N
        nameLbTE3.setName("nameLbTE3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(nameLbTE3, gridBagConstraints);

        nameTfTE3.setColumns(10);
        nameTfTE3.setFont(getDefaultFont());
        nameTfTE3.setMinimumSize(new java.awt.Dimension(86, 25));
        nameTfTE3.setName("nameTfTE3"); // NOI18N
        nameTfTE3.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(nameTfTE3, gridBagConstraints);

        lastnameLbTE3.setText(resourceMap.getString("lastnameLbTE3.text")); // NOI18N
        lastnameLbTE3.setName("lastnameLbTE3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(lastnameLbTE3, gridBagConstraints);

        lastnameTfTE3.setColumns(10);
        lastnameTfTE3.setFont(getDefaultFont());
        lastnameTfTE3.setMinimumSize(new java.awt.Dimension(86, 25));
        lastnameTfTE3.setName("lastnameTfTE3"); // NOI18N
        lastnameTfTE3.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(lastnameTfTE3, gridBagConstraints);

        titleLbTE3.setText(resourceMap.getString("titleLbTE3.text")); // NOI18N
        titleLbTE3.setName("titleLbTE3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(titleLbTE3, gridBagConstraints);

        titleTfTE3.setColumns(10);
        titleTfTE3.setFont(getDefaultFont());
        titleTfTE3.setMinimumSize(new java.awt.Dimension(86, 25));
        titleTfTE3.setName("titleTfTE3"); // NOI18N
        titleTfTE3.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(titleTfTE3, gridBagConstraints);

        companyLbTE3.setText(resourceMap.getString("companyLbTE3.text")); // NOI18N
        companyLbTE3.setName("companyLbTE3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(companyLbTE3, gridBagConstraints);

        companyTfTE3.setColumns(10);
        companyTfTE3.setFont(getDefaultFont());
        companyTfTE3.setMinimumSize(new java.awt.Dimension(86, 25));
        companyTfTE3.setName("companyTfTE3"); // NOI18N
        companyTfTE3.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(companyTfTE3, gridBagConstraints);

        disLbTE3.setText(resourceMap.getString("disLbTE3.text")); // NOI18N
        disLbTE3.setName("disLbTE3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(disLbTE3, gridBagConstraints);

        codeLbTE3.setText(resourceMap.getString("codeLbTE3.text")); // NOI18N
        codeLbTE3.setName("codeLbTE3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(codeLbTE3, gridBagConstraints);

        mobileLbTE3.setText(resourceMap.getString("mobileLbTE3.text")); // NOI18N
        mobileLbTE3.setName("mobileLbTE3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(mobileLbTE3, gridBagConstraints);

        faxLbTE3.setText(resourceMap.getString("faxLbTE3.text")); // NOI18N
        faxLbTE3.setName("faxLbTE3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(faxLbTE3, gridBagConstraints);

        adsLbTE3.setText(resourceMap.getString("adsLbTE3.text")); // NOI18N
        adsLbTE3.setName("adsLbTE3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(adsLbTE3, gridBagConstraints);

        disTfTE3.setColumns(10);
        disTfTE3.setFont(getDefaultFont());
        disTfTE3.setMinimumSize(new java.awt.Dimension(86, 25));
        disTfTE3.setName("disTfTE3"); // NOI18N
        disTfTE3.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(disTfTE3, gridBagConstraints);

        codeTfTE3.setColumns(10);
        codeTfTE3.setFont(getDefaultFont());
        codeTfTE3.setMinimumSize(new java.awt.Dimension(86, 25));
        codeTfTE3.setName("codeTfTE3"); // NOI18N
        codeTfTE3.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(codeTfTE3, gridBagConstraints);

        mobileTfTE3.setColumns(10);
        mobileTfTE3.setFont(getDefaultFont());
        mobileTfTE3.setMinimumSize(new java.awt.Dimension(86, 25));
        mobileTfTE3.setName("mobileTfTE3"); // NOI18N
        mobileTfTE3.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(mobileTfTE3, gridBagConstraints);

        faxTfTE3.setColumns(10);
        faxTfTE3.setFont(getDefaultFont());
        faxTfTE3.setMinimumSize(new java.awt.Dimension(86, 25));
        faxTfTE3.setName("faxTfTE3"); // NOI18N
        faxTfTE3.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(faxTfTE3, gridBagConstraints);

        adsPanelTE3.setMinimumSize(new java.awt.Dimension(99, 70));
        adsPanelTE3.setName("adsPanelTE3"); // NOI18N
        adsPanelTE3.setPreferredSize(new java.awt.Dimension(99, 70));
        adsPanelTE3.setLayout(new java.awt.GridLayout());

        adsScrTE3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        adsScrTE3.setHorizontalScrollBar(null);
        adsScrTE3.setMinimumSize(new java.awt.Dimension(99, 52));
        adsScrTE3.setName("adsScrTE3"); // NOI18N
        adsScrTE3.setPreferredSize(new java.awt.Dimension(99, 52));

        adsTaTE3.setColumns(30);
        adsTaTE3.setFont(getDefaultFont());
        adsTaTE3.setRows(3);
        adsTaTE3.setAutoscrolls(false);
        adsTaTE3.setMinimumSize(new java.awt.Dimension(99, 52));
        adsTaTE3.setName("adsTaTE3"); // NOI18N
        adsScrTE3.setViewportView(adsTaTE3);

        adsPanelTE3.add(adsScrTE3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridheight = 2;
        upLeftT3.add(adsPanelTE3, gridBagConstraints);

        subDisLbTE3.setText(resourceMap.getString("subDisLbTE3.text")); // NOI18N
        subDisLbTE3.setName("subDisLbTE3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(subDisLbTE3, gridBagConstraints);

        provinceLbTE3.setText(resourceMap.getString("provinceLbTE3.text")); // NOI18N
        provinceLbTE3.setName("provinceLbTE3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(provinceLbTE3, gridBagConstraints);

        phoneLbTE3.setText(resourceMap.getString("phoneLbTE3.text")); // NOI18N
        phoneLbTE3.setName("phoneLbTE3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(phoneLbTE3, gridBagConstraints);

        countryLbTE3.setText(resourceMap.getString("countryLbTE3.text")); // NOI18N
        countryLbTE3.setName("countryLbTE3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(countryLbTE3, gridBagConstraints);

        noteLbTE3.setText(resourceMap.getString("noteLbTE3.text")); // NOI18N
        noteLbTE3.setName("noteLbTE3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(noteLbTE3, gridBagConstraints);

        subDisTfTE3.setColumns(10);
        subDisTfTE3.setFont(getDefaultFont());
        subDisTfTE3.setMinimumSize(new java.awt.Dimension(86, 25));
        subDisTfTE3.setName("subDisTfTE3"); // NOI18N
        subDisTfTE3.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(subDisTfTE3, gridBagConstraints);

        provinceTfTE3.setColumns(10);
        provinceTfTE3.setFont(getDefaultFont());
        provinceTfTE3.setMinimumSize(new java.awt.Dimension(86, 25));
        provinceTfTE3.setName("provinceTfTE3"); // NOI18N
        provinceTfTE3.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(provinceTfTE3, gridBagConstraints);

        phoneTfTE3.setColumns(10);
        phoneTfTE3.setFont(getDefaultFont());
        phoneTfTE3.setMinimumSize(new java.awt.Dimension(86, 25));
        phoneTfTE3.setName("phoneTfTE3"); // NOI18N
        phoneTfTE3.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(phoneTfTE3, gridBagConstraints);

        countryTfTE3.setColumns(10);
        countryTfTE3.setFont(getDefaultFont());
        countryTfTE3.setMinimumSize(new java.awt.Dimension(86, 25));
        countryTfTE3.setName("countryTfTE3"); // NOI18N
        countryTfTE3.setPreferredSize(new java.awt.Dimension(86, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(countryTfTE3, gridBagConstraints);

        notePanelTE3.setMinimumSize(new java.awt.Dimension(99, 70));
        notePanelTE3.setName("notePanelTE3"); // NOI18N
        notePanelTE3.setPreferredSize(new java.awt.Dimension(99, 70));
        notePanelTE3.setLayout(new java.awt.GridLayout());

        noteScrTE3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        noteScrTE3.setHorizontalScrollBar(null);
        noteScrTE3.setMinimumSize(new java.awt.Dimension(99, 52));
        noteScrTE3.setName("noteScrTE3"); // NOI18N
        noteScrTE3.setPreferredSize(new java.awt.Dimension(99, 52));

        noteTaTE3.setColumns(30);
        noteTaTE3.setFont(getDefaultFont());
        noteTaTE3.setRows(3);
        noteTaTE3.setAutoscrolls(false);
        noteTaTE3.setMinimumSize(new java.awt.Dimension(99, 52));
        noteTaTE3.setName("noteTaTE3"); // NOI18N
        noteScrTE3.setViewportView(noteTaTE3);

        notePanelTE3.add(noteScrTE3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridheight = 2;
        upLeftT3.add(notePanelTE3, gridBagConstraints);

        blankLb7.setName("blankLb7"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        upLeftT3.add(blankLb7, gridBagConstraints);

        blankLb8.setName("blankLb8"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        upLeftT3.add(blankLb8, gridBagConstraints);

        blankLb9.setName("blankLb9"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 0;
        upLeftT3.add(blankLb9, gridBagConstraints);

        upLeftScrollPaneT3.setViewportView(upLeftT3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        resultTab.add(upLeftScrollPaneT3, gridBagConstraints);

        menuTab.addTab(resourceMap.getString("resultTab.TabConstraints.tabTitle"), resultTab); // NOI18N

        importExportTab.setBorder(BorderFactory.createTitledBorder(null, resourceMap.getString("importPanel.border.title"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, getDefaultFont(), resourceMap.getColor("importPanel.border.titleColor")));
        importExportTab.setFont(getDefaultFont());
        importExportTab.setMaximumSize(new java.awt.Dimension(680, 630));
        importExportTab.setMinimumSize(new java.awt.Dimension(680, 630));
        importExportTab.setName("importExportTab"); // NOI18N
        importExportTab.setPreferredSize(new java.awt.Dimension(680, 630));
        importExportTab.setLayout(new java.awt.GridBagLayout());

        importPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("importPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("DejaVu Sans", 0, 13), resourceMap.getColor("importPanel.border.titleColor"))); // NOI18N
        importPanel.setFont(getDefaultFont());
        importPanel.setMinimumSize(new java.awt.Dimension(600, 127));
        importPanel.setName("importPanel"); // NOI18N
        importPanel.setPreferredSize(new java.awt.Dimension(600, 127));
        importPanel.setLayout(new java.awt.GridBagLayout());

        importPanelT4.setMinimumSize(new java.awt.Dimension(280, 50));
        importPanelT4.setName("importPanelT4"); // NOI18N
        importPanelT4.setPreferredSize(new java.awt.Dimension(280, 50));

        importLbT4.setFont(getDefaultFont());
        importLbT4.setText(resourceMap.getString("importLbT4.text")); // NOI18N
        importLbT4.setName("importLbT4"); // NOI18N

        importTfT4.setEditable(false);
        importTfT4.setFont(getDefaultFont());
        importTfT4.setText(resourceMap.getString("importTfT4.text")); // NOI18N
        importTfT4.setMinimumSize(new java.awt.Dimension(86, 20));
        importTfT4.setName("importTfT4"); // NOI18N
        importTfT4.setPreferredSize(new java.awt.Dimension(86, 20));

        importBrowseBtnT4.setFont(getDefaultFont());
        importBrowseBtnT4.setText(resourceMap.getString("importBrowseBtnT4.text")); // NOI18N
        importBrowseBtnT4.setName("importBrowseBtnT4"); // NOI18N
        importBrowseBtnT4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importBrowseBtnT4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout importPanelT4Layout = new javax.swing.GroupLayout(importPanelT4);
        importPanelT4.setLayout(importPanelT4Layout);
        importPanelT4Layout.setHorizontalGroup(
            importPanelT4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(importPanelT4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(importLbT4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(importTfT4, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(importBrowseBtnT4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        importPanelT4Layout.setVerticalGroup(
            importPanelT4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(importPanelT4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(importPanelT4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(importTfT4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(importBrowseBtnT4)
                    .addComponent(importLbT4))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 0);
        importPanel.add(importPanelT4, gridBagConstraints);

        jPanel1.setMinimumSize(new java.awt.Dimension(140, 40));
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(140, 40));

        replaceBtnT4.setFont(getDefaultFont());
        replaceBtnT4.setText(resourceMap.getString("replaceBtnT4.text")); // NOI18N
        replaceBtnT4.setName("replaceBtnT4"); // NOI18N
        replaceBtnT4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceBtnT4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(replaceBtnT4, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(replaceBtnT4)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        importPanel.add(jPanel1, gridBagConstraints);

        jPanel2.setMinimumSize(new java.awt.Dimension(140, 40));
        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setPreferredSize(new java.awt.Dimension(140, 40));

        pendingBtnT4.setFont(getDefaultFont());
        pendingBtnT4.setText(resourceMap.getString("pendingBtnT4.text")); // NOI18N
        pendingBtnT4.setName("pendingBtnT4"); // NOI18N
        pendingBtnT4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pendingBtnT4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addComponent(pendingBtnT4, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(pendingBtnT4)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        importPanel.add(jPanel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        importExportTab.add(importPanel, gridBagConstraints);
        importPanel.getAccessibleContext().setAccessibleName(resourceMap.getString("importPanel.AccessibleContext.accessibleName")); // NOI18N

        exportPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("exportPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("DejaVu Sans", 0, 13), resourceMap.getColor("exportPanel.border.titleColor"))); // NOI18N
        exportPanel.setFont(getDefaultFont());
        exportPanel.setName("exportPanel"); // NOI18N
        exportPanel.setPreferredSize(new java.awt.Dimension(600, 100));

        jPanel5.setMinimumSize(new java.awt.Dimension(344, 40));
        jPanel5.setName("jPanel5"); // NOI18N
        jPanel5.setPreferredSize(new java.awt.Dimension(344, 40));

        exportLbT4.setFont(getDefaultFont());
        exportLbT4.setText(resourceMap.getString("exportLbT4.text")); // NOI18N
        exportLbT4.setName("exportLbT4"); // NOI18N

        exportTfT4.setEditable(false);
        exportTfT4.setFont(getDefaultFont());
        exportTfT4.setMinimumSize(new java.awt.Dimension(86, 20));
        exportTfT4.setName("exportTfT4"); // NOI18N
        exportTfT4.setPreferredSize(new java.awt.Dimension(86, 20));

        browseExportBtnT4.setFont(getDefaultFont());
        browseExportBtnT4.setText(resourceMap.getString("browseExportBtnT4.text")); // NOI18N
        browseExportBtnT4.setName("browseExportBtnT4"); // NOI18N
        browseExportBtnT4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseExportBtnT4ActionPerformed(evt);
            }
        });

        exportBtnT4.setFont(getDefaultFont());
        exportBtnT4.setText(resourceMap.getString("exportBtnT4.text")); // NOI18N
        exportBtnT4.setName("exportBtnT4"); // NOI18N
        exportBtnT4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportBtnT4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(exportLbT4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(exportTfT4, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(browseExportBtnT4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(exportBtnT4)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(exportTfT4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseExportBtnT4)
                    .addComponent(exportLbT4)
                    .addComponent(exportBtnT4))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout exportPanelLayout = new javax.swing.GroupLayout(exportPanel);
        exportPanel.setLayout(exportPanelLayout);
        exportPanelLayout.setHorizontalGroup(
            exportPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(exportPanelLayout.createSequentialGroup()
                .addGap(129, 129, 129)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(115, Short.MAX_VALUE))
        );
        exportPanelLayout.setVerticalGroup(
            exportPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, exportPanelLayout.createSequentialGroup()
                .addContainerGap(14, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        importExportTab.add(exportPanel, gridBagConstraints);

        blankPanelT4.setName("blankPanelT4"); // NOI18N
        blankPanelT4.setPreferredSize(new java.awt.Dimension(100, 150));

        javax.swing.GroupLayout blankPanelT4Layout = new javax.swing.GroupLayout(blankPanelT4);
        blankPanelT4.setLayout(blankPanelT4Layout);
        blankPanelT4Layout.setHorizontalGroup(
            blankPanelT4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        blankPanelT4Layout.setVerticalGroup(
            blankPanelT4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 150, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        importExportTab.add(blankPanelT4, gridBagConstraints);

        menuTab.addTab(resourceMap.getString("importExportTab.TabConstraints.tabTitle"), importExportTab); // NOI18N

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(menuTab, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 704, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(menuTab, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 674, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        menuTab.getAccessibleContext().setAccessibleName(resourceMap.getString("jTabbedPane1.AccessibleContext.accessibleName")); // NOI18N
        menuTab.getAccessibleContext().setAccessibleDescription(resourceMap.getString("jTabbedPane1.AccessibleContext.accessibleDescription")); // NOI18N

        statusPanel.setMaximumSize(new java.awt.Dimension(704, 24));
        statusPanel.setMinimumSize(new java.awt.Dimension(704, 24));
        statusPanel.setName("statusPanel"); // NOI18N
        statusPanel.setPreferredSize(new java.awt.Dimension(704, 24));

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        databaseLb.setFont(getDefaultFont());
        databaseLb.setText(resourceMap.getString("databaseLb.text")); // NOI18N
        databaseLb.setName("databaseLb"); // NOI18N

        databaseNameLb.setFont(getDefaultFont());
        databaseNameLb.setText(resourceMap.getString("databaseNameLb.text")); // NOI18N
        databaseNameLb.setMaximumSize(new java.awt.Dimension(50, 15));
        databaseNameLb.setMinimumSize(new java.awt.Dimension(50, 15));
        databaseNameLb.setName("databaseNameLb"); // NOI18N
        databaseNameLb.setPreferredSize(new java.awt.Dimension(50, 15));
        defaultcard = new File("defaultcard.csv");

        if(!defaultcard.exists()){
            try {
                defaultcard.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(ScannerView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        databaseNameLb.setText(defaultcard.getAbsolutePath());

        languageLb.setFont(getDefaultFont());
        languageLb.setText(resourceMap.getString("languageLb.text")); // NOI18N
        languageLb.setName("languageLb"); // NOI18N

        langaugeChangeBtn.setFont(getDefaultFont());
        langaugeChangeBtn.setText(resourceMap.getString("langaugeChangeBtn.text")); // NOI18N
        langaugeChangeBtn.setMaximumSize(new java.awt.Dimension(89, 25));
        langaugeChangeBtn.setMinimumSize(new java.awt.Dimension(89, 25));
        langaugeChangeBtn.setName("langaugeChangeBtn"); // NOI18N
        langaugeChangeBtn.setPreferredSize(new java.awt.Dimension(89, 25));
        langaugeChangeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                langaugeChangeBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(databaseLb)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(databaseNameLb, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(languageLb)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(langaugeChangeBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46)
                .addComponent(statusPanelSeparator))
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(languageLb)
                        .addComponent(langaugeChangeBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(statusPanelLayout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(databaseLb, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(databaseNameLb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setComponent(mainPanel);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    private boolean isPasswordCorrect(String usr, char[] input) {
        boolean isCorrect = true;
        char[] adminPassword = {'a', 'd', 'm', 'i', 'n'};
        char[] usrPassword = {'u', 's', 'e', 'r'};

        if (usr.equals("admin")) {
            if (input.length != adminPassword.length) {
                isCorrect = false;
            } else {
                isCorrect = Arrays.equals(input, adminPassword);
            }
            //Zero out the password.
            Arrays.fill(adminPassword, '0');
        }

        if (usr.equals("user")) {
            if (input.length != usrPassword.length) {
                isCorrect = false;
            } else {
                isCorrect = Arrays.equals(input, usrPassword);
            }
            //Zero out the password.
            Arrays.fill(usrPassword, '0');
        }
        return isCorrect;
    }

    private String validatePath(String path) {
        String newPath = "";
        String[] temp = path.split("\\\\");
        for (int i = 0; i < temp.length; i++) {
            if (i < temp.length - 1) {
                newPath += temp[i] + "/";
            } else {
                newPath += temp[i];
            }
        }
        return newPath;
    }

    private Font getDefaultFont(){
        return defaultFont;
    }

    private void setDefaultFont(){
      String os = "windows";
      String[] font = new String[2];
      int size = 11;
      if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1) {
        os = "windows";
      } else if (System.getProperty("os.name").toLowerCase().indexOf("linux") > -1) {
        os = "linux";
      } else if (System.getProperty("os.name").toLowerCase().indexOf("mac") > -1) {
        os = "mac";
      }
      System.out.println(">>>>>>>> OS "+os);
      font = myResourceMap.getString(os+".font").split(" ");
      System.out.println(">>>>>>>> font "+ font[0]+" "+font[1]);
      size = Integer.parseInt(font[1]);
      defaultFont = new Font(font[0],Font.PLAIN,size);

//      UIManager.put("Label.font",defaultFont);
//      UIManager.put("Button.font",defaultFont);

      ImagePanelDialog.setDefaultFont(defaultFont);

    }

    private void setFocusListener(){
        int front = 0;
        int back = 360;
        KeyStroke tabKey = KeyStroke.getKeyStroke("TAB");

        MyTextFieldFocusListener frontFocusT1 = new MyTextFieldFocusListener(upLeftScrollPaneT1, front);
        MyTextFieldFocusListener backFocusT1 = new MyTextFieldFocusListener(upLeftScrollPaneT1, back);

        nameTfT1.addFocusListener(frontFocusT1);
        lastnameTfT1.addFocusListener(frontFocusT1);
        titleTfT1.addFocusListener(frontFocusT1);
        emailTfT1.addFocusListener(frontFocusT1);
        companyTfT1.addFocusListener(frontFocusT1);
        webTfT1.addFocusListener(frontFocusT1);
        disTfT1.addFocusListener(frontFocusT1);
        subDisTfT1.addFocusListener(frontFocusT1);
        codeTfT1.addFocusListener(frontFocusT1);
        countryTfT1.addFocusListener(frontFocusT1);
        provinceTfT1.addFocusListener(frontFocusT1);
        mobileTfT1.addFocusListener(frontFocusT1);
        phoneTfT1.addFocusListener(frontFocusT1);
        faxTfT1.addFocusListener(frontFocusT1);
        adsTaT1.addFocusListener(frontFocusT1);
        noteTaT1.addFocusListener(frontFocusT1);
                
        adsTaT1.getInputMap().put(tabKey,new FocusToNextTA((noteTaT1)));
        noteTaT1.getInputMap().put(tabKey,new FocusToNextTA((adsTaTE1)));

        nameTfTE1.addFocusListener(backFocusT1);
        lastnameTfTE1.addFocusListener(backFocusT1);
        titleTfTE1.addFocusListener(backFocusT1);
        companyTfTE1.addFocusListener(backFocusT1);
        disTfTE1.addFocusListener(backFocusT1);
        subDisTfTE1.addFocusListener(backFocusT1);
        codeTfTE1.addFocusListener(backFocusT1);
        provinceTfTE1.addFocusListener(backFocusT1);
        mobileTfTE1.addFocusListener(backFocusT1);
        phoneTfTE1.addFocusListener(backFocusT1);
        faxTfTE1.addFocusListener(backFocusT1);
        adsTaTE1.addFocusListener(backFocusT1);
        noteTaTE1.addFocusListener(backFocusT1);

        adsTaTE1.getInputMap().put(tabKey,new FocusToNextTA((noteTaTE1)));  
        noteTaTE1.getInputMap().put(tabKey,new FocusToNextTA((nameTfT1)));

        MyTextFieldFocusListener frontFocusT2 = new MyTextFieldFocusListener(upLeftScrollPaneT2, front);
        MyTextFieldFocusListener backFocusT2 = new MyTextFieldFocusListener(upLeftScrollPaneT2, back);

        nameTfT2.addFocusListener(frontFocusT2);
        lastnameTfT2.addFocusListener(frontFocusT2);
        titleTfT2.addFocusListener(frontFocusT2);
        emailTfT2.addFocusListener(frontFocusT2);
        companyTfT2.addFocusListener(frontFocusT2);
        webTfT2.addFocusListener(frontFocusT2);
        disTfT2.addFocusListener(frontFocusT2);
        subDisTfT2.addFocusListener(frontFocusT2);
        codeTfT2.addFocusListener(frontFocusT2);
        countryTfT2.addFocusListener(frontFocusT2);
        provinceTfT2.addFocusListener(frontFocusT2);
        mobileTfT2.addFocusListener(frontFocusT2);
        phoneTfT2.addFocusListener(frontFocusT2);
        faxTfT2.addFocusListener(frontFocusT2);
        adsTaT2.addFocusListener(frontFocusT2);
        noteTaT2.addFocusListener(frontFocusT2);

        adsTaT2.getInputMap().put(tabKey,new FocusToNextTA((noteTaT2)));
        noteTaT2.getInputMap().put(tabKey,new FocusToNextTA((adsTaTE2)));

        nameTfTE2.addFocusListener(backFocusT2);
        lastnameTfTE2.addFocusListener(backFocusT2);
        titleTfTE2.addFocusListener(backFocusT2);
        companyTfTE2.addFocusListener(backFocusT2);
        disTfTE2.addFocusListener(backFocusT2);
        subDisTfTE2.addFocusListener(backFocusT2);
        codeTfTE2.addFocusListener(backFocusT2);
        provinceTfTE2.addFocusListener(backFocusT2);
        mobileTfTE2.addFocusListener(backFocusT2);
        phoneTfTE2.addFocusListener(backFocusT2);
        faxTfTE2.addFocusListener(backFocusT2);
        adsTaTE2.addFocusListener(backFocusT2);
        noteTaTE2.addFocusListener(backFocusT2);

        adsTaTE2.getInputMap().put(tabKey,new FocusToNextTA((noteTaTE2)));
        noteTaTE2.getInputMap().put(tabKey,new FocusToNextTA((nameTfT2)));

        MyTextFieldFocusListener frontFocusT3 = new MyTextFieldFocusListener(upLeftScrollPaneT3, front);
        MyTextFieldFocusListener backFocusT3 = new MyTextFieldFocusListener(upLeftScrollPaneT3, back);

        nameTfT3.addFocusListener(frontFocusT3);
        lastnameTfT3.addFocusListener(frontFocusT3);
        titleTfT3.addFocusListener(frontFocusT3);
        emailTfT3.addFocusListener(frontFocusT3);
        companyTfT3.addFocusListener(frontFocusT3);
        webTfT3.addFocusListener(frontFocusT3);
        disTfT3.addFocusListener(frontFocusT3);
        subDisTfT3.addFocusListener(frontFocusT3);
        codeTfT3.addFocusListener(frontFocusT3);
        countryTfT3.addFocusListener(frontFocusT3);
        provinceTfT3.addFocusListener(frontFocusT3);
        mobileTfT3.addFocusListener(frontFocusT3);
        phoneTfT3.addFocusListener(frontFocusT3);
        faxTfT3.addFocusListener(frontFocusT3);
        adsTaT3.addFocusListener(frontFocusT3);
        noteTaT3.addFocusListener(frontFocusT3);

        adsTaT3.getInputMap().put(tabKey,new FocusToNextTA((noteTaT3)));
        noteTaT3.getInputMap().put(tabKey,new FocusToNextTA((adsTaTE3)));

        nameTfTE3.addFocusListener(backFocusT3);
        lastnameTfTE3.addFocusListener(backFocusT3);
        titleTfTE3.addFocusListener(backFocusT3);
        companyTfTE3.addFocusListener(backFocusT3);
        disTfTE3.addFocusListener(backFocusT3);
        subDisTfTE3.addFocusListener(backFocusT3);
        codeTfTE3.addFocusListener(backFocusT3);
        provinceTfTE3.addFocusListener(backFocusT3);
        mobileTfTE3.addFocusListener(backFocusT3);
        phoneTfTE3.addFocusListener(backFocusT3);
        faxTfTE3.addFocusListener(backFocusT3);
        adsTaTE3.addFocusListener(backFocusT3);
        noteTaTE3.addFocusListener(backFocusT3);

        adsTaTE3.getInputMap().put(tabKey,new FocusToNextTA((noteTaTE3)));
        noteTaTE3.getInputMap().put(tabKey,new FocusToNextTA((nameTfT3)));
    }

    private void updateTable(ArrayList<Card> cardList, DefaultTableModel model) {
        Object[][] tableArray = new Object[cardList.size()][];
        Collections.sort(cardList, new CardComparator());
        int row = 0;
        for (Card card : cardList) {
            tableArray[row] = new Object[]{false, card.getFirstName(), card.getLastName(), card.getFirstNameE(), card.getLastNameE(), card.getCompany(), card.getPosition(), card.getTelephone(), card.getMobile(), card.getId()};
            row++;
        }
        model.setDataVector(tableArray, new Object[]{"เลือก", "ชื่อ", "นามสกุล", "Name", "Last Name", "บริษัท", "ตำแหน่ง", "โทรศัพท์", "โทรศัพท์มือถือ", "id"});
        xCol.setColumnVisible(importTableT2.getColumnModel().getColumn(9), false);
        // System.out.println(((XTableColumnModel)importTableT2.getColumnModel()).getColumn(9, false));
    }

    private void setButtonsState(int newButtonsState) {

        switch (newButtonsState) {
            case STATE_NO_IMAGE:
                doubleSideBtnT1.setEnabled(false);
                blackWhiteBtnT1.setEnabled(false);
                autoCropBtnT1.setEnabled(false);
                readCardBtnT1.setEnabled(false);
                rotateBtnT1.setEnabled(false);
                emailBtnT1.setEnabled(false);
                undoBtnT1.setEnabled(false);
                confirmBtnT1.setEnabled(false);
                brightSldT1.setEnabled(false);
                break;

            case STATE_WITH_IMAGE:
                doubleSideBtnT1.setEnabled(true);
                blackWhiteBtnT1.setEnabled(true);
                autoCropBtnT1.setEnabled(true);
                readCardBtnT1.setEnabled(true);
                rotateBtnT1.setEnabled(true);
                emailBtnT1.setEnabled(true);
                undoBtnT1.setEnabled(false);
                confirmBtnT1.setEnabled(false);
                brightSldT1.setEnabled(true);
                break;

            case STATE_IMAGE_EDITED:
                doubleSideBtnT1.setEnabled(true);
                blackWhiteBtnT1.setEnabled(true);
                autoCropBtnT1.setEnabled(true);
                readCardBtnT1.setEnabled(true);
                rotateBtnT1.setEnabled(true);
                emailBtnT1.setEnabled(true);
                undoBtnT1.setEnabled(true);
                confirmBtnT1.setEnabled(true);
                brightSldT1.setEnabled(true);
                break;
        }

        switch (frontUIState) {
            case STATE_NO_IMAGE:
                frontCropBtnT1.setEnabled(false);
                break;
            case STATE_WITH_IMAGE:
                frontCropBtnT1.setEnabled(true);
                break;
            case STATE_IMAGE_EDITED:
                frontCropBtnT1.setEnabled(true);
                break;
        }

        switch (backUIState) {
            case STATE_NO_IMAGE:
                backCropBtnT1.setEnabled(false);
                break;
            case STATE_WITH_IMAGE:
                backCropBtnT1.setEnabled(true);
                break;
            case STATE_IMAGE_EDITED:
                backCropBtnT1.setEnabled(true);
                break;
        }

    }

    private void setButtonsStateResult(int newButtonsState) {

        switch (newButtonsState) {
            case STATE_NO_IMAGE:
                blackWhiteBtnT3.setEnabled(false);
                autoCropBtnT3.setEnabled(false);
                rotateBtnT3.setEnabled(false);
                emailBtnT3.setEnabled(false);
                undoBtnT3.setEnabled(false);
                confirmBtnT3.setEnabled(false);
                brightSldT3.setEnabled(false);
                break;

            case STATE_WITH_IMAGE:
                blackWhiteBtnT3.setEnabled(true);
                autoCropBtnT3.setEnabled(true);
                rotateBtnT3.setEnabled(true);
                emailBtnT3.setEnabled(true);
                undoBtnT3.setEnabled(false);
                confirmBtnT3.setEnabled(false);
                brightSldT3.setEnabled(true);
                break;

            case STATE_IMAGE_EDITED:
                blackWhiteBtnT3.setEnabled(true);
                autoCropBtnT3.setEnabled(true);
                rotateBtnT3.setEnabled(true);
                emailBtnT3.setEnabled(true);
                undoBtnT3.setEnabled(true);
                confirmBtnT3.setEnabled(true);
                brightSldT3.setEnabled(true);
                break;
        }

        switch (frontUIStateResult) {
            case STATE_NO_IMAGE:
                frontCropBtnT3.setEnabled(false);
                break;
            case STATE_WITH_IMAGE:
                frontCropBtnT3.setEnabled(true);
                break;
            case STATE_IMAGE_EDITED:
                frontCropBtnT3.setEnabled(true);
                break;
        }

        switch (backUIStateResult) {
            case STATE_NO_IMAGE:
                backCropBtnT3.setEnabled(false);
                break;
            case STATE_WITH_IMAGE:
                backCropBtnT3.setEnabled(true);
                break;
            case STATE_IMAGE_EDITED:
                backCropBtnT3.setEnabled(true);
                break;
        }

    }

    private void saveBtnT1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveBtnT1ActionPerformed
        // TODO add your handling code here:
        Card newCard = getCardFromForm(SCAN_TAB);
        if (Utils.checkFirstName(newCard, MISSING_ALERT,getDefaultFont())) {
            try {
                //System.out.println(name+" "+lastName+" "+title+" "+email+" "+company+" "+web+" "+ads+" "+city+" "+state+" "+country+" "+code+" "+phone+" "+fax+" "+mobile+" "+note+" "+imgFront+" "+imgBack);
                CardLocalManager.addLocalCard(newCard, defaultcard.getAbsolutePath());

                // Yov's added code
                Long cardID = newCard.getId();
                String imgFileName = frontTfT1.getText();
                String imgBackFileName = backTfT1.getText();
                File imgFile = new File(imgFileName);
                File imgBackFile = new File(imgBackFileName);
                File curDir = new File("." + File.separator);
                if (imgFileName != null) {
                    if ((imgFileName.length() > 0) && imgFile.isFile()
                            && ((imgFileName.lastIndexOf(".jpg") == (imgFileName.length() - 4))
                            || (imgFileName.lastIndexOf(".jpeg") == (imgFileName.length() - 5)))) {

                        if (isBrowsedFront) {
                            ImageIO.write(scannedBCard.getPrimaryImage().getImageData(), "jpg", new File(curDir.getCanonicalPath() + File.separator + "cardImages" + File.separator + cardID + ".jpg"));
                            newCard.setImgFront("./cardImages/" + cardID + ".jpg");
                        } else {
                            String fname = curDir.getCanonicalPath() + File.separator + "cardImages" + File.separator + cardID + ".jpg";
//                            imgFile.renameTo(new File(fname));
                            copyfile(imgFileName, fname);
                            newCard.setImgFront(fname);
                        }


                    }
                }

                if (imgBackFileName != null) {
                    if ((imgBackFileName.length() > 0) && imgBackFile.isFile()
                            && ((imgBackFileName.lastIndexOf(".jpg") == (imgBackFileName.length() - 4))
                            || (imgBackFileName.lastIndexOf(".jpeg") == (imgBackFileName.length() - 5)))) {

                        if (isBrowsedBack) {
                            ImageIO.write(scannedBCardBack.getPrimaryImage().getImageData(), "jpg", new File(curDir.getCanonicalPath() + File.separator + "cardImages" + File.separator + cardID + "Back.jpg"));
                            newCard.setImgBack("./cardImages/" + cardID + "Back.jpg");
                        } else {
                            String fname = curDir.getCanonicalPath() + File.separator + "cardImages" + File.separator + cardID + "Back.jpg";
                            copyfile(imgBackFileName, fname);
//                            imgBackFile.renameTo(new File(fname));
                            newCard.setImgBack(fname);
                        }

                    }
                }

                CardLocalManager.editLocalCard(newCard, defaultcard.getAbsolutePath());
                localCardList.add(newCard);

            } catch (ScannerDBException ex) {
                Logger.getLogger(ScannerView.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ioEx) {
                ioEx.printStackTrace();
            }
            String txt = "<html><body><FONT FACE=\""+getDefaultFont().getName()+"\" >"+myResourceMap.getString(ADD_ALERT)+"</FONT></body></html>";
            JOptionPane.showMessageDialog(null, txt, "information", JOptionPane.INFORMATION_MESSAGE);
            clearFormT1();
        }
    }//GEN-LAST:event_saveBtnT1ActionPerformed
    private static void copyfile(String srFile, String dtFile) {
        try {
            File f1 = new File(srFile);
            File f2 = new File(dtFile);
            InputStream in = new FileInputStream(f1);

            //For Append the file.
//      OutputStream out = new FileOutputStream(f2,true);

            //For Overwrite the file.
            OutputStream out = new FileOutputStream(f2);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            System.out.println("File copied.");
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage() + " in the specified directory.");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    private void frontBtnT1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_frontBtnT1ActionPerformed
        // TODO add your handling code here:

        //imgChooser = new javax.swing.JFileChooser();

        if (evt.getSource() == frontBtnT1) {
            imgChooser.addChoosableFileFilter(new JPGFileFilter());
            imgChooser.setAcceptAllFileFilterUsed(false);
            int returnVal = imgChooser.showOpenDialog(null);
            if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
                java.io.File file = imgChooser.getSelectedFile();
                String path = file.getAbsolutePath();
                frontTfT1.setText(path);

                ImageIcon cardIcon = new ImageIcon(path);
                java.awt.Image cardImage = cardIcon.getImage();

                frontLbT1.setIcon(cardIcon);

                // Yov's added code
                scannedImage = new BufferedImage(cardImage.getWidth(null), cardImage.getHeight(null),
                        BufferedImage.TYPE_INT_ARGB);
                scannedImage.getGraphics().drawImage(cardImage, 0, 0, null);
                scannedBCard = new BusinessCard(scannedImage);

                scannedImageFileName = path;

                frontUIState = STATE_WITH_IMAGE;
                setButtonsState(frontUIState);

                isBrowsedFront = true;
            } else {
            }
        }
    }//GEN-LAST:event_frontBtnT1ActionPerformed

    private void backBtnT1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backBtnT1ActionPerformed
        // TODO add your handling code here:
        if (evt.getSource() == backBtnT1) {
            imgChooser.addChoosableFileFilter(new JPGFileFilter());
            imgChooser.setAcceptAllFileFilterUsed(false);
            int returnVal = imgChooser.showOpenDialog(null);
            if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
                java.io.File file = imgChooser.getSelectedFile();
                String path = file.getAbsolutePath();
                backTfT1.setText(path);

                ImageIcon cardIcon = new ImageIcon(path);
                java.awt.Image cardImage = cardIcon.getImage();

                backLbT1.setIcon(cardIcon);

                // Yov's added code
                scannedImage = new BufferedImage(cardImage.getWidth(null), cardImage.getHeight(null),
                        BufferedImage.TYPE_INT_ARGB);
                scannedImage.getGraphics().drawImage(cardImage, 0, 0, null);
                scannedBCardBack = new BusinessCard(scannedImage);

                scannedImageFileNameBack = path;

                backUIState = STATE_WITH_IMAGE;
                setButtonsState(backUIState);

                isBrowsedBack = true;
            } else {
            }
        }
    }//GEN-LAST:event_backBtnT1ActionPerformed

    private void deletedBtnT2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deletedBtnT2ActionPerformed
        // TODO add your handling code here:
        int response;//0 = yes, 1 = no
        String[] options = getOptionChoice();

        String txt = "<html><body><FONT FACE=\""+getDefaultFont().getName()+"\" >"+myResourceMap.getString(DELETE_ALERT)+"</FONT></body></html>";        
        
        response = JOptionPane.showOptionDialog(null, new JLabel(txt), myResourceMap.getString(CONFIRM_ALERT),
                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                null, options, options[0]);
//        response = javax.swing.JOptionPane.showConfirmDialog(null, DELETE_ALERT,CONFIRM_ALERT,javax.swing.JOptionPane.YES_NO_OPTION);
        DefaultTableModel model = (DefaultTableModel) importTableT2.getModel();
        long temp;
        Card tempCard = new Card();

        if (response == 0) {
            ArrayList<Card> ids = new ArrayList<Card>();
            for (int i = model.getRowCount() - 1; i >= 0; i--) {
                if (((Boolean) model.getValueAt(i, 0)).booleanValue() == true) {

                        temp = (Long) model.getValueAt(i, 9);
                        tempCard = new Card();
                        tempCard.setId(temp);
                        ids.add(tempCard);
//                        localCardList.remove(tempCard);
                        model.removeRow(i);
                    
                }
            }
            // Sort from high to low

            Collections.sort(ids);
            for (Iterator<Card> it = ids.iterator(); it.hasNext();) {
                try{
                Card card = it.next();
                localCardList =  (ArrayList<Card>) CardLocalManager.deleteLocalCard(card.getId(), defaultcard.getAbsolutePath());
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }//GEN-LAST:event_deletedBtnT2ActionPerformed

    private void editBtnT2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editBtnT2ActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) importTableT2.getModel();
        if (importTableT2.getSelectedRow() == -1) {
            return;
        }
        long id = (Long) model.getValueAt(importTableT2.getSelectedRow(), 9);
        Card editCard = getCardById(id, localCardList);
//        editCard = localCardList.g
        menuTab.setEnabledAt(RESULT_TAB, true);
        menuTab.setSelectedIndex(RESULT_TAB);

        saveBtnT3.setEnabled(true);

        String frontPath = validatePath(editCard.getImgFront());
        String backPath = validatePath(editCard.getImgBack());

        idNameLbT3.setText(editCard.getId() + "");
        nameTfT3.setText(editCard.getFirstName());
        lastnameTfT3.setText(editCard.getLastName());
        titleTfT3.setText(editCard.getPosition());
        emailTfT3.setText(editCard.getEmail());
        companyTfT3.setText(editCard.getCompany());
        webTfT3.setText(editCard.getWebsite());
        disTfT3.setText(editCard.getState());
        subDisTfT3.setText(editCard.getCity());
        codeTfT3.setText(editCard.getZip());
        countryTfT3.setText(editCard.getCountry());
        mobileTfT3.setText(editCard.getMobile());
        phoneTfT3.setText(editCard.getTelephone());
        faxTfT3.setText(editCard.getFax());
        adsTaT3.setText(editCard.getAddress());
        noteTaT3.setText(editCard.getNote());

        nameTfTE3.setText(editCard.getFirstNameE());
        lastnameTfTE3.setText(editCard.getLastNameE());
        titleTfTE3.setText(editCard.getPositionE());
        companyTfTE3.setText(editCard.getCompanyE());
        disTfTE3.setText(editCard.getStateE());
        subDisTfTE3.setText(editCard.getCityE());
        codeTfTE3.setText(editCard.getZipE());
        countryTfTE3.setText(editCard.getCountryE());
        mobileTfTE3.setText(editCard.getMobileE());
        phoneTfTE3.setText(editCard.getTelephoneE());
        faxTfTE3.setText(editCard.getFaxE());
        adsTaTE3.setText(editCard.getAddressE());
        noteTaTE3.setText(editCard.getNoteE());

        //frontTfT3.setText(frontPath);
        //backTfT3.setText(backPath);


        if ((frontPath != null) && (frontPath.length() > 0) && (frontPath.charAt(0) == '.')) {
            File curDir = new File(".");
            String curDirStr = "";
            try {
                curDirStr = curDir.getCanonicalPath();
                System.out.println("cur dir = " + curDirStr);
                curDirStr = curDirStr.replace('\\', '/');
                frontTfT3.setText(frontPath.replaceFirst(".", curDirStr));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            frontTfT3.setText(frontPath);
        }

        if ((backPath != null) && (backPath.length() > 0) && (backPath.charAt(0) == '.')) {
            File curDir = new File(".");
            String curDirStr = "";
            try {
                curDirStr = curDir.getCanonicalPath();
                System.out.println("cur dir = " + curDirStr);
                curDirStr = curDirStr.replace('\\', '/');
                backTfT3.setText(backPath.replaceFirst(".", curDirStr));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            backTfT3.setText(backPath);
        }


        frontLbT3.setIcon(new ImageIcon(frontPath));
        backLbT3.setIcon(new ImageIcon(backPath));

        if ((frontPath != null) && (frontPath.length() > 0)) {
            try {
                resultBCard = new BusinessCard(ImageIO.read(new File(frontPath)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if ((backPath != null) && (backPath.length() > 0)) {
            try {
                resultBCardBack = new BusinessCard(ImageIO.read(new File(backPath)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Yov's added code
        frontUIStateResult = STATE_NO_IMAGE;
        if ((editCard.getImgFront() != null) && (editCard.getImgFront().length() > 0)) {
            ImageIcon frontResult = new ImageIcon(frontPath);
            if ((frontResult != null) && (frontResult.getIconHeight() > 0) && (frontResult.getIconWidth() > 0)) {
                frontUIStateResult = STATE_WITH_IMAGE;
            }
        }

        backUIStateResult = STATE_NO_IMAGE;
        if ((editCard.getImgBack() != null) && (editCard.getImgBack().length() > 0)) {
            ImageIcon backResult = new ImageIcon(backPath);
            if ((backResult != null) && (backResult.getIconHeight() > 0) && (backResult.getIconWidth() > 0)) {
                backUIStateResult = STATE_WITH_IMAGE;
            }
        }

        isFrontSelectedResult = true;
        setButtonsStateResult(frontUIStateResult);
    }//GEN-LAST:event_editBtnT2ActionPerformed

    private void frontBtnT3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_frontBtnT3ActionPerformed
        if (evt.getSource() == frontBtnT3) {
            imgChooser.addChoosableFileFilter(new JPGFileFilter());
            imgChooser.setAcceptAllFileFilterUsed(false);
            int returnVal = imgChooser.showOpenDialog(null);
            if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
                java.io.File file = imgChooser.getSelectedFile();
                String path = file.getAbsolutePath();
                frontTfT3.setText(path);

                ImageIcon cardIcon = new ImageIcon(path);
                java.awt.Image cardImage = cardIcon.getImage();

                frontLbT3.setIcon(cardIcon);

                // Yov's added code
                resultImage = new BufferedImage(cardImage.getWidth(null), cardImage.getHeight(null),
                        BufferedImage.TYPE_INT_ARGB);
                resultImage.getGraphics().drawImage(cardImage, 0, 0, null);
                resultBCard = new BusinessCard(resultImage);

                frontUIStateResult = STATE_WITH_IMAGE;
                setButtonsStateResult(frontUIStateResult);

                isBrowsedFrontResult = true;
            } else {
            }
        }
    }//GEN-LAST:event_frontBtnT3ActionPerformed

    private void browseExportBtnT4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseExportBtnT4ActionPerformed
        // TODO add your handling code here:
        String wd = System.getProperty("user.dir");
        String filename = "";
        JFileChooser fc = new JFileChooser(wd);
        fc.addChoosableFileFilter(new ZipFileFilter());

        int rc = fc.showDialog(null, "Select");
        if (rc == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            filename = file.getAbsolutePath();

            exportTfT4.setText(filename);
            // call your function here
        }
    }//GEN-LAST:event_browseExportBtnT4ActionPerformed

    private void exportBtnT4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportBtnT4ActionPerformed
        // TODO add your handling code here:
        try {
            String fileName = exportTfT4.getText();
            if (fileName != null && !fileName.equals("")) {
                ZipUtils.exportCards(localCardList, fileName + ".zip");
                Font f = getDefaultFont();
                String[] options = getAcceptChoice();
                String[] alert = myResourceMap.getString(EXPORT_ALERT).split(",");
                String alertTxt = "<html><body><FONT FACE=\""+f.getName()+"\" >"+alert[0] + " " + localCardList.size() + " " + alert[1]+"</FONT></body></html>";                

                JOptionPane.showOptionDialog(null, alertTxt, myResourceMap.getString(NOTIFICATION_ALERT),
                        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                        null, options, options[0]);
            }
        } catch (Exception e) {
        }
    }//GEN-LAST:event_exportBtnT4ActionPerformed

    private void scanBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scanBtnActionPerformed
        // TODO add your handling code here:
        //String path = "C:/Documents and Settings/Jenchote/Desktop/scanner/jenCard.jpg";
        //frontTfT1.setText(path);
        //frontLbT1.setIcon(new ImageIcon(path));

        //String path1 = "C:/Documents and Settings/Jenchote/Desktop/scanner/oakCard.jpg";
        //backTfT1.setText(path1);
        //backLbT1.setIcon(new ImageIcon(path1));

        // Yov's part: Call scanner
        synchronized (this) {

            if (isFrontSelected) {
                bcScanner.setTargetFileName(scannedImageFileName);
            } else {
                bcScanner.setTargetFileName(scannedImageFileNameBack);
            }


            scannedImage = bcScanner.scan(this);
            scannerTxtT1.setText(bcScanner.getName());
            setScannedImage();

        }
    }//GEN-LAST:event_scanBtnActionPerformed
    public void setScannedImage() {
        if (scannedImage != null) {
            if (isFrontSelected) {
                scannedBCard = new BusinessCard(scannedImage);

                frontLbT1.setIcon(new ImageIcon(scannedImage));
                frontTfT1.setText(scannedImageFileName + bcScanner.getFileNameIndex() + ".jpg");

                frontUIState = STATE_WITH_IMAGE;
                setButtonsState(frontUIState);

                isBrowsedFront = false;
            } else {
                scannedBCardBack = new BusinessCard(scannedImage);

                backLbT1.setIcon(new ImageIcon(scannedImage));
                backTfT1.setText(scannedImageFileNameBack + bcScanner.getFileNameIndex() + ".jpg");

                backUIState = STATE_WITH_IMAGE;
                setButtonsState(backUIState);

                isBrowsedBack = false;
            }
        } else {
            System.err.println("!!! Scanned Image is NULL !!!");
        }
    }

    public void setScannedImage(String newFileName) {
        if (scannedImage != null) {
            if (isFrontSelected) {
                scannedBCard = new BusinessCard(scannedImage);

                frontLbT1.setIcon(new ImageIcon(scannedImage));
                frontTfT1.setText(newFileName);

                frontUIState = STATE_WITH_IMAGE;
                setButtonsState(frontUIState);

                isBrowsedFront = false;
            } else {
                scannedBCardBack = new BusinessCard(scannedImage);

                backLbT1.setIcon(new ImageIcon(scannedImage));
                backTfT1.setText(newFileName);

                backUIState = STATE_WITH_IMAGE;
                setButtonsState(backUIState);

                isBrowsedBack = false;
            }
        } else {
            System.err.println("!!! Scanned Image is NULL !!!");
        }
    }
    private void rotateBtnT1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rotateBtnT1ActionPerformed
        if (isFrontSelected) {
            if (scannedBCard != null) {

                scannedBCard.rotate90();

                frontLbT1.setIcon(new ImageIcon(scannedBCard.getPreviewImage().getImageData()));

                frontUIState = STATE_IMAGE_EDITED;
                setButtonsState(frontUIState);
            }
        } else {
            if (scannedBCardBack != null) {

                scannedBCardBack.rotate90();

                backLbT1.setIcon(new ImageIcon(scannedBCardBack.getPreviewImage().getImageData()));

                backUIState = STATE_IMAGE_EDITED;
                setButtonsState(backUIState);
            }
        }
    }//GEN-LAST:event_rotateBtnT1ActionPerformed

    private void brightSldT1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_brightSldT1StateChanged
        if (isFrontSelected) {
            if ((!brightSldT1.getValueIsAdjusting()) && (scannedBCard != null)) {

                scannedBCard.changeBrightness(brightSldT1.getValue());

                frontLbT1.setIcon(new ImageIcon(scannedBCard.getPreviewImage().getImageData()));

                frontUIState = STATE_IMAGE_EDITED;
                setButtonsState(frontUIState);
            }
        } else {
            if ((!brightSldT1.getValueIsAdjusting()) && (scannedBCardBack != null)) {

                scannedBCardBack.changeBrightness(brightSldT1.getValue());

                backLbT1.setIcon(new ImageIcon(scannedBCardBack.getPreviewImage().getImageData()));

                backUIState = STATE_IMAGE_EDITED;
                setButtonsState(backUIState);
            }
        }
    }//GEN-LAST:event_brightSldT1StateChanged

    private void scannerBtnT1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scannerBtnT1ActionPerformed
        synchronized (this) {
            scannerTxtT1.setText(bcScanner.selectScanner(scannerTxtT1));
        }
    }//GEN-LAST:event_scannerBtnT1ActionPerformed

    private void readCardBtnT1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readCardBtnT1ActionPerformed
        // Yov is working Here
        //System.out.println("TrainingImageFolder: " + trainingImgFolder);

        if (isFrontSelected) {
            if ((scannedBCard != null) && (scannedImageFileName != null)) {
                String filename = "";
                if (scannedImageFileName.contains(".jpg")) {
                    filename = scannedImageFileName;
                    scannedBCard.setImageFileName(scannedImageFileName);
                } else {
//                    scannedBCard.setImageFileName(scannedImageFileName
//                            + (bcScanner.getFileNameIndex()-1) + ".jpg");
                    filename = frontTfT1.getText();
                    scannedBCard.setImageFileName(frontTfT1.getText());
                    System.out.print("OCR Image file name back" + frontTfT1.getText());
                }
                System.out.println("Doing OCR file " +filename);
                //cannedBCard.initRonCemerOCR(new JTabbedPane());
                //noteTaT1.setText( scannedBCard.retrieveData() );

                JFrame textWin = new JFrame("OCR-Read Text");
                textWin.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                textWin.setLocation(100, 100);
                textWin.setSize(WIN_WIDTH, WIN_HEIGHT);
                Container contentPane = textWin.getContentPane();
                contentPane.setLayout(new BorderLayout());
                JTextArea ocrTxt = new JTextArea();
                ocrTxt.setText(scannedBCard.retrieveData());
                contentPane.add(ocrTxt, BorderLayout.CENTER);
                textWin.setVisible(true);

            }
        } else {
            if ((scannedBCardBack != null) && (scannedImageFileNameBack != null)) {
                if (scannedImageFileNameBack.contains(".jpg")) {
                    scannedBCardBack.setImageFileName(scannedImageFileNameBack);
                } else {
//                    scannedBCardBack.setImageFileName(scannedImageFileNameBack
//                            + (bcScanner.getFileNameIndex()-1) + ".jpg");
                    scannedBCardBack.setImageFileName(backTfT1.getText());
                    System.out.print("OCR Image file name back" + backTfT1.getText());
                }

                //scannedBCardBack.initRonCemerOCR(new JTabbedPane());
                //noteTaT1.setText( scannedBCardBack.retrieveData() );

                JFrame textWin = new JFrame("OCR-Read Text");
                textWin.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                textWin.setLocation(100, 100);
                textWin.setSize(WIN_WIDTH, WIN_HEIGHT);
                Container contentPane = textWin.getContentPane();
                contentPane.setLayout(new BorderLayout());
                JTextArea ocrTxt = new JTextArea();
                ocrTxt.setText(scannedBCardBack.retrieveData());
                contentPane.add(ocrTxt, BorderLayout.CENTER);
                textWin.setVisible(true);

            }
        }
    }//GEN-LAST:event_readCardBtnT1ActionPerformed

    private void frontSideRdT1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_frontSideRdT1ActionPerformed
        isFrontSelected = true;
        backSideRdT1.setSelected(false);

        setButtonsState(frontUIState);
    }//GEN-LAST:event_frontSideRdT1ActionPerformed

    private void backSideRdT1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backSideRdT1ActionPerformed
        isFrontSelected = false;
        frontSideRdT1.setSelected(false);

        setButtonsState(backUIState);
    }//GEN-LAST:event_backSideRdT1ActionPerformed

    private void undoBtnT1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoBtnT1ActionPerformed
        if (isFrontSelected) {
            if (scannedBCard != null) {
                scannedBCard.undoChanges();

                frontLbT1.setIcon(new ImageIcon(scannedBCard.getPreviewImage().getImageData()));

                frontUIState = STATE_WITH_IMAGE;
                setButtonsState(frontUIState);
            }
        } else {
            if (scannedBCardBack != null) {
                scannedBCardBack.undoChanges();

                backLbT1.setIcon(new ImageIcon(scannedBCardBack.getPreviewImage().getImageData()));

                backUIState = STATE_WITH_IMAGE;
                setButtonsState(backUIState);
            }
        }
    }//GEN-LAST:event_undoBtnT1ActionPerformed

    private void frontSideRdT3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_frontSideRdT3ActionPerformed
        isFrontSelectedResult = true;

        backSideRdT3.setSelected(false);

        setButtonsStateResult(frontUIStateResult);
    }//GEN-LAST:event_frontSideRdT3ActionPerformed

    private void backSideRdT3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backSideRdT3ActionPerformed
        isFrontSelectedResult = false;

        frontSideRdT3.setSelected(false);

        setButtonsStateResult(backUIStateResult);
    }//GEN-LAST:event_backSideRdT3ActionPerformed

    private void undoBtnT3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoBtnT3ActionPerformed
        if (isFrontSelectedResult) {
            if (resultBCard != null) {
                resultBCard.undoChanges();

                frontLbT3.setIcon(new ImageIcon(resultBCard.getPreviewImage().getImageData()));

                frontUIStateResult = STATE_WITH_IMAGE;
                setButtonsStateResult(frontUIStateResult);
            }
        } else {
            if (resultBCardBack != null) {
                resultBCardBack.undoChanges();

                backLbT3.setIcon(new ImageIcon(resultBCardBack.getPreviewImage().getImageData()));

                backUIStateResult = STATE_WITH_IMAGE;
                setButtonsStateResult(backUIStateResult);
            }
        }

        // Add your database-related method here
    }//GEN-LAST:event_undoBtnT3ActionPerformed

    private void confirmBtnT3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirmBtnT3ActionPerformed
        if (isFrontSelectedResult) {
            if (resultBCard != null) {
                resultBCard.confirmChange();

                frontLbT3.setIcon(new ImageIcon(resultBCard.getPreviewImage().getImageData()));

                frontUIStateResult = STATE_WITH_IMAGE;
                setButtonsStateResult(frontUIStateResult);
            }
        } else {
            if (resultBCardBack != null) {
                resultBCardBack.confirmChange();

                backLbT3.setIcon(new ImageIcon(resultBCardBack.getPreviewImage().getImageData()));

                backUIStateResult = STATE_WITH_IMAGE;
                setButtonsStateResult(backUIStateResult);
            }
        }

        // Add your database-related method here
    }//GEN-LAST:event_confirmBtnT3ActionPerformed

    private void confirmBtnT1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirmBtnT1ActionPerformed
        if (isFrontSelected) {
            if (scannedBCard != null) {
                scannedBCard.confirmChange();

                frontLbT1.setIcon(new ImageIcon(scannedBCard.getPreviewImage().getImageData()));

                frontUIState = STATE_WITH_IMAGE;
                setButtonsState(frontUIState);
            }
        } else {
            if (scannedBCard != null) {
                scannedBCardBack.confirmChange();

                backLbT1.setIcon(new ImageIcon(scannedBCardBack.getPreviewImage().getImageData()));

                backUIState = STATE_WITH_IMAGE;
                setButtonsState(backUIState);
            }
        }
    }//GEN-LAST:event_confirmBtnT1ActionPerformed

    private void rotateBtnT3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rotateBtnT3ActionPerformed
        if (isFrontSelectedResult) {
            if (resultBCard != null) {

                resultBCard.rotate90();

                frontLbT3.setIcon(new ImageIcon(resultBCard.getPreviewImage().getImageData()));

                frontUIStateResult = STATE_IMAGE_EDITED;
                setButtonsStateResult(frontUIStateResult);
            }
        } else {
            if (resultBCardBack != null) {

                resultBCardBack.rotate90();

                backLbT3.setIcon(new ImageIcon(resultBCardBack.getPreviewImage().getImageData()));

                backUIStateResult = STATE_IMAGE_EDITED;
                setButtonsStateResult(backUIStateResult);
            }
        }
    }//GEN-LAST:event_rotateBtnT3ActionPerformed

    private void brightSldT3StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_brightSldT3StateChanged
        if (isFrontSelectedResult) {
            if ((!brightSldT3.getValueIsAdjusting()) && (resultBCard != null)) {

                resultBCard.changeBrightness(brightSldT3.getValue());

                frontLbT3.setIcon(new ImageIcon(resultBCard.getPreviewImage().getImageData()));

                frontUIStateResult = STATE_IMAGE_EDITED;
                setButtonsStateResult(frontUIStateResult);
            }
        } else {
            if ((!brightSldT3.getValueIsAdjusting()) && (resultBCardBack != null)) {

                resultBCardBack.changeBrightness(brightSldT3.getValue());

                backLbT3.setIcon(new ImageIcon(resultBCardBack.getPreviewImage().getImageData()));

                backUIStateResult = STATE_IMAGE_EDITED;
                setButtonsStateResult(backUIStateResult);
            }
        }
    }//GEN-LAST:event_brightSldT3StateChanged

    private void backBtnT3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backBtnT3ActionPerformed
        // TODO add your handling code here:

        //ImageIcon cardIcon = new ImageIcon(path);
        //java.awt.Image cardImage = cardIcon.getImage();

        //backLbT3.setIcon(cardIcon);

        // Yov's added code
        //resultImage = new BufferedImage(cardImage.getWidth(null), cardImage.getHeight(null),
        //        BufferedImage.TYPE_INT_ARGB);
        //resultImage.getGraphics().drawImage(cardImage, 0, 0, null);
        //resultBCardBack = new BusinessCard(resultImage);

        if (evt.getSource() == backBtnT3) {
            imgChooser.addChoosableFileFilter(new JPGFileFilter());
            imgChooser.setAcceptAllFileFilterUsed(false);
            int returnVal = imgChooser.showOpenDialog(null);
            if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
                java.io.File file = imgChooser.getSelectedFile();
                String path = file.getAbsolutePath();
                backTfT3.setText(path);

                ImageIcon cardIcon = new ImageIcon(path);
                java.awt.Image cardImage = cardIcon.getImage();

                backLbT3.setIcon(cardIcon);

                // Yov's added code
                resultImage = new BufferedImage(cardImage.getWidth(null), cardImage.getHeight(null),
                        BufferedImage.TYPE_INT_ARGB);
                resultImage.getGraphics().drawImage(cardImage, 0, 0, null);
                resultBCardBack = new BusinessCard(resultImage);

                backUIStateResult = STATE_WITH_IMAGE;
                setButtonsStateResult(backUIStateResult);

                isBrowsedBackResult = true;
            } else {
            }
        }
    }//GEN-LAST:event_backBtnT3ActionPerformed

    private void doubleSideBtnT1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doubleSideBtnT1ActionPerformed
        // TODO add your handling code here:
        if (isFrontSelected) {
            if (scannedBCard != null) {
                scannedBCardBack = new BusinessCard(scannedBCard.getPreviewImage());

                backLbT1.setIcon(new ImageIcon(scannedBCardBack.getPreviewImage().getImageData()));

                backUIState = STATE_WITH_IMAGE;
                // Oak Add
                backTfT1.setText(frontTfT1.getText());
            }
        } else {
            if (scannedBCardBack != null) {
                scannedBCard = new BusinessCard(scannedBCardBack.getPreviewImage());

                frontLbT1.setIcon(new ImageIcon(scannedBCard.getPreviewImage().getImageData()));

                frontUIState = STATE_WITH_IMAGE;
                // Oak Add
                frontTfT1.setText(backTfT1.getText());
            }
        }
    }//GEN-LAST:event_doubleSideBtnT1ActionPerformed

    private void blackWhiteBtnT1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_blackWhiteBtnT1ActionPerformed
        // TODO add your handling code here:
        if (isFrontSelected) {
            if (scannedBCard != null) {

                scannedBCard.turnToBlackAndWhite();

                frontLbT1.setIcon(new ImageIcon(scannedBCard.getPreviewImage().getImageData()));

                frontUIState = STATE_IMAGE_EDITED;
                setButtonsState(frontUIState);
            }
        } else {
            if (scannedBCardBack != null) {

                scannedBCardBack.turnToBlackAndWhite();

                backLbT1.setIcon(new ImageIcon(scannedBCardBack.getPreviewImage().getImageData()));

                backUIState = STATE_IMAGE_EDITED;
                setButtonsState(backUIState);
            }
        }
    }//GEN-LAST:event_blackWhiteBtnT1ActionPerformed

    private void autoCropBtnT1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoCropBtnT1ActionPerformed
        // TODO add your handling code here:

        if (isFrontSelected) {
            if (scannedBCard != null) {

                scannedBCard.trim();

                frontLbT1.setIcon(new ImageIcon(scannedBCard.getPreviewImage().getImageData()));

                frontUIState = STATE_IMAGE_EDITED;
                setButtonsState(frontUIState);
            }
        } else {
            if (scannedBCardBack != null) {

                scannedBCardBack.trim();

                backLbT1.setIcon(new ImageIcon(scannedBCardBack.getPreviewImage().getImageData()));

                backUIState = STATE_IMAGE_EDITED;
                setButtonsState(backUIState);
            }
        }
    }//GEN-LAST:event_autoCropBtnT1ActionPerformed

    private void autoCropBtnT3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoCropBtnT3ActionPerformed
        // TODO add your handling code here:

        if (isFrontSelectedResult) {
            if (resultBCard != null) {

                resultBCard.trim();

                frontLbT3.setIcon(new ImageIcon(resultBCard.getPreviewImage().getImageData()));

                frontUIStateResult = STATE_IMAGE_EDITED;
                setButtonsStateResult(frontUIStateResult);
            }
        } else {
            if (resultBCardBack != null) {

                resultBCardBack.trim();

                backLbT3.setIcon(new ImageIcon(resultBCardBack.getPreviewImage().getImageData()));

                backUIStateResult = STATE_IMAGE_EDITED;
                setButtonsStateResult(backUIStateResult);
            }
        }
    }//GEN-LAST:event_autoCropBtnT3ActionPerformed

    private void blackWhiteBtnT3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_blackWhiteBtnT3ActionPerformed
        // TODO add your handling code here:

        if (isFrontSelectedResult) {
            if (resultBCard != null) {

                resultBCard.turnToBlackAndWhite();

                frontLbT3.setIcon(new ImageIcon(resultBCard.getPreviewImage().getImageData()));

                frontUIStateResult = STATE_IMAGE_EDITED;
                setButtonsStateResult(frontUIStateResult);
            }
        } else {
            if (resultBCardBack != null) {

                resultBCardBack.turnToBlackAndWhite();

                backLbT3.setIcon(new ImageIcon(resultBCardBack.getPreviewImage().getImageData()));

                backUIStateResult = STATE_IMAGE_EDITED;
                setButtonsStateResult(backUIStateResult);
            }
        }
    }//GEN-LAST:event_blackWhiteBtnT3ActionPerformed

    private void genSearchT2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genSearchT2ActionPerformed
        // TODO add your handling code here:
        disableToggleExcept(QUICK_NONE);
        Card criteria = getCardFromForm(QUERY_TAB);
        String genCriteria = genSearchTfT2.getText();
        ArrayList<Card> resultCards;

        if (genCriteria.equals("")) {
            resultCards = Utils.searchCard(criteria, localCardList);
        } else {
            ArrayList<Card> temporal = Utils.searchGenCard(genCriteria, localCardList);
            resultCards = Utils.searchCard(criteria, temporal);
        }

        System.out.println("size is " + resultCards.size());
        DefaultTableModel model = (DefaultTableModel) importTableT2.getModel();

        if (resultCards != null) {
            updateTable(resultCards, model);
        }
    }//GEN-LAST:event_genSearchT2ActionPerformed

    private void disableToggleExcept(int index) {
        if (index != QUICK_TH_0 || index == QUICK_NONE) {
            thBtn0T2.setSelected(false);
        }
        if (index != QUICK_TH_1 || index == QUICK_NONE) {
            thBtn1T2.setSelected(false);
        }
        if (index != QUICK_TH_2 || index == QUICK_NONE) {
            thBtn2T2.setSelected(false);
        }
        if (index != QUICK_TH_3 || index == QUICK_NONE) {
            thBtn3T2.setSelected(false);
        }
        if (index != QUICK_TH_4 || index == QUICK_NONE) {
            thBtn4T2.setSelected(false);
        }
        if (index != QUICK_TH_5 || index == QUICK_NONE) {
            thBtn5T2.setSelected(false);
        }
        if (index != QUICK_TH_6 || index == QUICK_NONE) {
            thBtn6T2.setSelected(false);
        }
        if (index != QUICK_TH_7 || index == QUICK_NONE) {
            thBtn7T2.setSelected(false);
        }
        if (index != QUICK_TH_8 || index == QUICK_NONE) {
            thBtn8T2.setSelected(false);
        }
        if (index != QUICK_EN_0 || index == QUICK_NONE) {
            engBtn0T2.setSelected(false);
        }
        if (index != QUICK_EN_1 || index == QUICK_NONE) {
            engBtn1T2.setSelected(false);
        }
        if (index != QUICK_EN_2 || index == QUICK_NONE) {
            engBtn2T2.setSelected(false);
        }
        if (index != QUICK_EN_3 || index == QUICK_NONE) {
            engBtn3T2.setSelected(false);
        }
        if (index != QUICK_EN_4 || index == QUICK_NONE) {
            engBtn4T2.setSelected(false);
        }
        if (index != QUICK_EN_5 || index == QUICK_NONE) {
            engBtn5T2.setSelected(false);
        }
        if (index != QUICK_EN_6 || index == QUICK_NONE) {
            engBtn6T2.setSelected(false);
        }
        if (index != QUICK_EN_7 || index == QUICK_NONE) {
            engBtn7T2.setSelected(false);
        }
        if (index != QUICK_EN_8 || index == QUICK_NONE) {
            engBtn8T2.setSelected(false);
        }
    }

    private void engBtn0T2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_engBtn0T2ActionPerformed
        // TODO add your handling code here:
        if (engBtn0T2.isSelected()) {
            disableToggleExcept(QUICK_EN_0);
            ArrayList<Card> resultCards = new ArrayList<Card>();
            resultCards = Utils.quickSearchCard("abc", localCardList);
            DefaultTableModel model = (DefaultTableModel) importTableT2.getModel();

            if (resultCards != null) {
                updateTable(resultCards, model);
            }
        } else {
            updateTable(new ArrayList<Card>(), (DefaultTableModel) importTableT2.getModel());
        }
    }//GEN-LAST:event_engBtn0T2ActionPerformed

    private void engBtn1T2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_engBtn1T2ActionPerformed
        // TODO add your handling code here:
        if (engBtn1T2.isSelected()) {
            disableToggleExcept(QUICK_EN_1);
            ArrayList<Card> resultCards = new ArrayList<Card>();
            resultCards = Utils.quickSearchCard("def", localCardList);
            DefaultTableModel model = (DefaultTableModel) importTableT2.getModel();

            if (resultCards != null) {
                updateTable(resultCards, model);
            }
        } else {
            updateTable(new ArrayList<Card>(), (DefaultTableModel) importTableT2.getModel());
        }
    }//GEN-LAST:event_engBtn1T2ActionPerformed

    private void engBtn2T2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_engBtn2T2ActionPerformed
        // TODO add your handling code here:
        if (engBtn2T2.isSelected()) {
            disableToggleExcept(QUICK_EN_2);
            ArrayList<Card> resultCards = new ArrayList<Card>();
            resultCards = Utils.quickSearchCard("ghi", localCardList);
            DefaultTableModel model = (DefaultTableModel) importTableT2.getModel();

            if (resultCards != null) {
                updateTable(resultCards, model);
            }
        } else {
            updateTable(new ArrayList<Card>(), (DefaultTableModel) importTableT2.getModel());
        }
    }//GEN-LAST:event_engBtn2T2ActionPerformed

    private void engBtn3T2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_engBtn3T2ActionPerformed
        // TODO add your handling code here:
        if (engBtn3T2.isSelected()) {
            disableToggleExcept(QUICK_EN_3);
            ArrayList<Card> resultCards = new ArrayList<Card>();
            resultCards = Utils.quickSearchCard("jkl", localCardList);
            DefaultTableModel model = (DefaultTableModel) importTableT2.getModel();

            if (resultCards != null) {
                updateTable(resultCards, model);
            }
        } else {
            updateTable(new ArrayList<Card>(), (DefaultTableModel) importTableT2.getModel());
        }
    }//GEN-LAST:event_engBtn3T2ActionPerformed

    private void engBtn4T2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_engBtn4T2ActionPerformed
        // TODO add your handling code here:
        if (engBtn4T2.isSelected()) {
            disableToggleExcept(QUICK_EN_4);
            ArrayList<Card> resultCards = new ArrayList<Card>();
            resultCards = Utils.quickSearchCard("mno", localCardList);
            DefaultTableModel model = (DefaultTableModel) importTableT2.getModel();

            if (resultCards != null) {
                updateTable(resultCards, model);
            }
        } else {
            updateTable(new ArrayList<Card>(), (DefaultTableModel) importTableT2.getModel());
        }
    }//GEN-LAST:event_engBtn4T2ActionPerformed

    private void engBtn5T2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_engBtn5T2ActionPerformed
        // TODO add your handling code here:
        if (engBtn5T2.isSelected()) {
            disableToggleExcept(QUICK_EN_5);
            ArrayList<Card> resultCards = new ArrayList<Card>();
            resultCards = Utils.quickSearchCard("pqr", localCardList);
            DefaultTableModel model = (DefaultTableModel) importTableT2.getModel();

            if (resultCards != null) {
                updateTable(resultCards, model);
            }
        } else {
            updateTable(new ArrayList<Card>(), (DefaultTableModel) importTableT2.getModel());
        }
    }//GEN-LAST:event_engBtn5T2ActionPerformed

    private void engBtn6T2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_engBtn6T2ActionPerformed
        // TODO add your handling code here:
        if (engBtn6T2.isSelected()) {
            disableToggleExcept(QUICK_EN_6);
            ArrayList<Card> resultCards = new ArrayList<Card>();
            resultCards = Utils.quickSearchCard("stu", localCardList);
            DefaultTableModel model = (DefaultTableModel) importTableT2.getModel();

            if (resultCards != null) {
                updateTable(resultCards, model);
            }
        } else {
            updateTable(new ArrayList<Card>(), (DefaultTableModel) importTableT2.getModel());
        }
    }//GEN-LAST:event_engBtn6T2ActionPerformed

    private void engBtn7T2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_engBtn7T2ActionPerformed
        // TODO add your handling code here:
        if (engBtn7T2.isSelected()) {
            disableToggleExcept(QUICK_EN_7);
            ArrayList<Card> resultCards = new ArrayList<Card>();
            resultCards = Utils.quickSearchCard("vwx", localCardList);
            DefaultTableModel model = (DefaultTableModel) importTableT2.getModel();

            if (resultCards != null) {
                updateTable(resultCards, model);
            }
        } else {
            updateTable(new ArrayList<Card>(), (DefaultTableModel) importTableT2.getModel());
        }
    }//GEN-LAST:event_engBtn7T2ActionPerformed

    private void engBtn8T2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_engBtn8T2ActionPerformed
        // TODO add your handling code here:
        if (engBtn8T2.isSelected()) {
            disableToggleExcept(QUICK_EN_8);
            ArrayList<Card> resultCards = new ArrayList<Card>();
            resultCards = Utils.quickSearchCard("yz", localCardList);
            DefaultTableModel model = (DefaultTableModel) importTableT2.getModel();

            if (resultCards != null) {
                updateTable(resultCards, model);
            }
        } else {
            updateTable(new ArrayList<Card>(), (DefaultTableModel) importTableT2.getModel());
        }
    }//GEN-LAST:event_engBtn8T2ActionPerformed

    private void frontCropBtnT1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_frontCropBtnT1ActionPerformed
        if (((frontUIState == STATE_WITH_IMAGE) || (frontUIState == STATE_IMAGE_EDITED))
                && (frontTfT1.getText() != null) && (frontTfT1.getText().length() > 0)) {

            ImagePanelDialog frontImgDialog = new ImagePanelDialog(frontTfT1.getText());

            frontImgDialog.init();
            frontImgDialog.setAllText(myResourceMap.getString(CROP_PANEL_TITLE),
                    myResourceMap.getString(CROP_CONFIRM),
                    myResourceMap.getString(CROP_CANCEL),
                    myResourceMap.getString(CROP_FOCUS));
            frontImgDialog.setVisible(true);

            if (frontImgDialog.isImageCropped()) {
                frontUIState = STATE_WITH_IMAGE;
                scannedImage = frontImgDialog.getCroppedImage();
                scannedBCard = new BusinessCard(frontImgDialog.getCroppedImage());

                frontLbT1.setIcon(new ImageIcon(scannedImage));
//                if (backLbT1.getIcon() != null) {
//                    backLbT1.setIcon(new ImageIcon(scannedImage));
//                    scannedBCardBack = new BusinessCard(frontImgDialog.getCroppedImage());
//                }
            }

        }
    }//GEN-LAST:event_frontCropBtnT1ActionPerformed

    private void backCropBtnT1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backCropBtnT1ActionPerformed
        if (((backUIState == STATE_WITH_IMAGE) || (backUIState == STATE_IMAGE_EDITED))
                && (backTfT1.getText() != null) && (backTfT1.getText().length() > 0)) {

            ImagePanelDialog backImgDialog = new ImagePanelDialog(backTfT1.getText());

            backImgDialog.init();
            backImgDialog.setAllText(myResourceMap.getString(CROP_PANEL_TITLE),
                    myResourceMap.getString(CROP_CONFIRM),
                    myResourceMap.getString(CROP_CANCEL),
                    myResourceMap.getString(CROP_FOCUS));
            backImgDialog.setVisible(true);

            if (backImgDialog.isImageCropped()) {
                backUIState = STATE_WITH_IMAGE;
                scannedImage = backImgDialog.getCroppedImage();
                scannedBCardBack = new BusinessCard(backImgDialog.getCroppedImage());

                backLbT1.setIcon(new ImageIcon(scannedImage));
//                if (frontLbT1.getIcon() != null) {
//                    frontLbT1.setIcon(new ImageIcon(scannedImage));
//                    scannedBCard = new BusinessCard(backImgDialog.getCroppedImage());
//                }
            }
        }
    }//GEN-LAST:event_backCropBtnT1ActionPerformed

    private void frontCropBtnT3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_frontCropBtnT3ActionPerformed
        if (((frontUIStateResult == STATE_WITH_IMAGE) || (frontUIStateResult == STATE_IMAGE_EDITED))
                && (frontTfT3.getText() != null) && (frontTfT3.getText().length() > 0)) {

            ImagePanelDialog frontImgDialogResult = new ImagePanelDialog(frontTfT3.getText());

            frontImgDialogResult.init();
            frontImgDialogResult.setAllText(myResourceMap.getString(CROP_PANEL_TITLE),
                    myResourceMap.getString(CROP_CONFIRM),
                    myResourceMap.getString(CROP_CANCEL),
                    myResourceMap.getString(CROP_FOCUS));
            frontImgDialogResult.setVisible(true);

            if (frontImgDialogResult.isImageCropped()) {
                frontUIStateResult = STATE_WITH_IMAGE;
                resultImage = frontImgDialogResult.getCroppedImage();
                resultBCard = new BusinessCard(frontImgDialogResult.getCroppedImage());

                frontLbT3.setIcon(new ImageIcon(resultImage));
//                if (backLbT3.getIcon() != null) {
//                    backLbT3.setIcon(new ImageIcon(scannedImage));
//                    scannedBCardBack = new BusinessCard(frontImgDialogResult.getCroppedImage());
//                }
            }
        }
    }//GEN-LAST:event_frontCropBtnT3ActionPerformed

    private void backCropBtnT3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backCropBtnT3ActionPerformed
        if (((backUIStateResult == STATE_WITH_IMAGE) || (backUIStateResult == STATE_IMAGE_EDITED))
                && (backTfT3.getText() != null) && (backTfT3.getText().length() > 0)) {
            System.out.println("backt3 : " + backTfT3.getText());
            ImagePanelDialog backImgDialogResult = new ImagePanelDialog(backTfT3.getText());

            backImgDialogResult.init();
            backImgDialogResult.setAllText(myResourceMap.getString(CROP_PANEL_TITLE),
                    myResourceMap.getString(CROP_CONFIRM),
                    myResourceMap.getString(CROP_CANCEL),
                    myResourceMap.getString(CROP_FOCUS));
            backImgDialogResult.setVisible(true);

            if (backImgDialogResult.isImageCropped()) {
                backUIStateResult = STATE_WITH_IMAGE;
                resultImage = backImgDialogResult.getCroppedImage();
                resultBCardBack = new BusinessCard(backImgDialogResult.getCroppedImage());

                backLbT3.setIcon(new ImageIcon(resultImage));
//                 if (frontLbT3.getIcon() != null) {
//                    frontLbT3.setIcon(new ImageIcon(scannedImage));
//                    scannedBCard = new BusinessCard(backImgDialogResult.getCroppedImage());
//                }
            }

        }
    }//GEN-LAST:event_backCropBtnT3ActionPerformed

    private void frontTfT1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_frontTfT1MouseClicked
        // TODO add your handling code here:
        imgChooser.addChoosableFileFilter(new JPGFileFilter());
        imgChooser.setAcceptAllFileFilterUsed(false);
        int returnVal = imgChooser.showOpenDialog(null);
        if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            java.io.File file = imgChooser.getSelectedFile();
            String path = file.getAbsolutePath();
            frontTfT1.setText(path);

            ImageIcon cardIcon = new ImageIcon(path);
            java.awt.Image cardImage = cardIcon.getImage();

            frontLbT1.setIcon(cardIcon);

            // Yov's added code
            scannedImage = new BufferedImage(cardImage.getWidth(null), cardImage.getHeight(null),
                    BufferedImage.TYPE_INT_ARGB);
            scannedImage.getGraphics().drawImage(cardImage, 0, 0, null);
            scannedBCard = new BusinessCard(scannedImage);

            scannedImageFileName = path;

            frontUIState = STATE_WITH_IMAGE;
            setButtonsState(frontUIState);
        }
    }//GEN-LAST:event_frontTfT1MouseClicked

    private void backTfT1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backTfT1MouseClicked
        // TODO add your handling code here:
        imgChooser.addChoosableFileFilter(new JPGFileFilter());
        imgChooser.setAcceptAllFileFilterUsed(false);
        int returnVal = imgChooser.showOpenDialog(null);
        if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            java.io.File file = imgChooser.getSelectedFile();
            String path = file.getAbsolutePath();
            backTfT1.setText(path);

            ImageIcon cardIcon = new ImageIcon(path);
            java.awt.Image cardImage = cardIcon.getImage();

            backLbT1.setIcon(cardIcon);

            // Yov's added code
            scannedImage = new BufferedImage(cardImage.getWidth(null), cardImage.getHeight(null),
                    BufferedImage.TYPE_INT_ARGB);
            scannedImage.getGraphics().drawImage(cardImage, 0, 0, null);
            scannedBCardBack = new BusinessCard(scannedImage);

            scannedImageFileNameBack = path;

            backUIState = STATE_WITH_IMAGE;
            setButtonsState(backUIState);
        }
    }//GEN-LAST:event_backTfT1MouseClicked

    private void frontTfT3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_frontTfT3MouseClicked
        // TODO add your handling code here:
        imgChooser.addChoosableFileFilter(new JPGFileFilter());
        imgChooser.setAcceptAllFileFilterUsed(false);
        int returnVal = imgChooser.showOpenDialog(null);
        if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            java.io.File file = imgChooser.getSelectedFile();
            String path = file.getAbsolutePath();
            frontTfT3.setText(path);

            ImageIcon cardIcon = new ImageIcon(path);
            java.awt.Image cardImage = cardIcon.getImage();

            frontLbT3.setIcon(cardIcon);

            // Yov's added code
            resultImage = new BufferedImage(cardImage.getWidth(null), cardImage.getHeight(null),
                    BufferedImage.TYPE_INT_ARGB);
            resultImage.getGraphics().drawImage(cardImage, 0, 0, null);
            resultBCard = new BusinessCard(resultImage);

            frontUIStateResult = STATE_WITH_IMAGE;
            setButtonsStateResult(frontUIStateResult);
        }
    }//GEN-LAST:event_frontTfT3MouseClicked

    private void backTfT3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backTfT3MouseClicked
        // TODO add your handling code here:
        imgChooser.addChoosableFileFilter(new JPGFileFilter());
        imgChooser.setAcceptAllFileFilterUsed(false);
        int returnVal = imgChooser.showOpenDialog(null);
        if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            java.io.File file = imgChooser.getSelectedFile();
            String path = file.getAbsolutePath();
            backTfT3.setText(path);

            ImageIcon cardIcon = new ImageIcon(path);
            java.awt.Image cardImage = cardIcon.getImage();

            backLbT3.setIcon(cardIcon);

            // Yov's added code
            resultImage = new BufferedImage(cardImage.getWidth(null), cardImage.getHeight(null),
                    BufferedImage.TYPE_INT_ARGB);
            resultImage.getGraphics().drawImage(cardImage, 0, 0, null);
            resultBCardBack = new BusinessCard(resultImage);

            backUIStateResult = STATE_WITH_IMAGE;
            setButtonsStateResult(backUIStateResult);
        }
    }//GEN-LAST:event_backTfT3MouseClicked

    private void saveBtnT3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveBtnT3ActionPerformed
        // TODO add your handling code here:
        Card newCard = getCardFromForm(RESULT_TAB);
        newCard.setId(Long.parseLong(idNameLbT3.getText()));
        if (Utils.checkFirstName(newCard, MISSING_ALERT,getDefaultFont())) {
            try {
                //System.out.println(name+" "+lastName+" "+title+" "+email+" "+company+" "+web+" "+ads+" "+city+" "+state+" "+country+" "+code+" "+phone+" "+fax+" "+mobile+" "+note+" "+imgFront+" "+imgBack);

                Long cardID = newCard.getId();
                String imgFileName = frontTfT3.getText();
                String imgBackFileName = backTfT3.getText();
                File imgFile = new File(imgFileName);
                File imgBackFile = new File(imgBackFileName);
                File curDir = new File("." + File.separator);
                if (imgFileName != null) {
                    if ((imgFileName.length() > 0) && imgFile.isFile()
                            && ((imgFileName.lastIndexOf(".jpg") == (imgFileName.length() - 4))
                            || (imgFileName.lastIndexOf(".jpeg") == (imgFileName.length() - 5)))) {


                        if (isBrowsedFrontResult) {

                            File originalImage = new File(curDir.getCanonicalPath() + File.separator + "cardImages" + File.separator + cardID + ".jpg");
                             if (originalImage.isFile()) {
                            originalImage.delete();
                             }
                            ImageIO.write(resultBCard.getPrimaryImage().getImageData(), "jpg", new File(curDir.getCanonicalPath() + File.separator + "cardImages" + File.separator + cardID + ".jpg"));

                        }

                        newCard.setImgFront("./cardImages/" + cardID + ".jpg");
                    }
                }

                if (imgBackFileName != null) {
                    if ((imgBackFileName.length() > 0) && imgBackFile.isFile()
                            && ((imgBackFileName.lastIndexOf(".jpg") == (imgBackFileName.length() - 4))
                            || (imgBackFileName.lastIndexOf(".jpeg") == (imgBackFileName.length() - 5)))) {


                        if (isBrowsedBackResult) {
                            File originalImage = new File(curDir.getCanonicalPath() + File.separator + "cardImages" + File.separator + cardID + "Back.jpg");
                            if (originalImage.isFile()) {
                                originalImage.delete();
                            }
                            ImageIO.write(resultBCardBack.getPrimaryImage().getImageData(), "jpg", new File(curDir.getCanonicalPath() + File.separator + "cardImages" + File.separator + cardID + "Back.jpg"));

                        }
                        newCard.setImgBack("./cardImages/" + cardID + "Back.jpg");
                    }
                }

                CardLocalManager.editLocalCard(newCard, defaultcard.getAbsolutePath());
                int index = localCardList.indexOf(newCard);
                localCardList.set(index, newCard);

                isBrowsedFrontResult = false;
                isBrowsedBackResult = false;

            } catch (ScannerDBException ex) {
                Logger.getLogger(ScannerView.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ioEx) {
                ioEx.printStackTrace();
            }
            
            String[] options = getAcceptChoice();
            String txt = "<html><body><FONT FACE=\""+getDefaultFont().getName()+"\" >"+myResourceMap.getString(UPDATE_ALERT)+"</FONT></body></html>";
            JOptionPane.showOptionDialog(null, txt, myResourceMap.getString(NOTIFICATION_ALERT),
                    JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                    null, options, options[0]);
            clearFormT3();
            saveBtnT3.setEnabled(false);
        }
    }//GEN-LAST:event_saveBtnT3ActionPerformed

    private void thBtn0T2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_thBtn0T2ActionPerformed
        // TODO add your handling code here:
        if (thBtn0T2.isSelected()) {
            disableToggleExcept(QUICK_TH_0);
            ArrayList<Card> resultCards = new ArrayList<Card>();
            resultCards = Utils.quickSearchCard("กขคฅ", localCardList);
            DefaultTableModel model = (DefaultTableModel) importTableT2.getModel();

            if (resultCards != null) {
                updateTable(resultCards, model);
            }
        } else {
            updateTable(new ArrayList<Card>(), (DefaultTableModel) importTableT2.getModel());
        }
    }//GEN-LAST:event_thBtn0T2ActionPerformed

    private void thBtn1T2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_thBtn1T2ActionPerformed
        // TODO add your handling code here:
        if (thBtn1T2.isSelected()) {
            disableToggleExcept(QUICK_TH_1);
            ArrayList<Card> resultCards = new ArrayList<Card>();
            resultCards = Utils.quickSearchCard("ฆงจฉ", localCardList);
            DefaultTableModel model = (DefaultTableModel) importTableT2.getModel();

            if (resultCards != null) {
                updateTable(resultCards, model);
            }
        } else {
            updateTable(new ArrayList<Card>(), (DefaultTableModel) importTableT2.getModel());
        }
    }//GEN-LAST:event_thBtn1T2ActionPerformed

    private void thBtn2T2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_thBtn2T2ActionPerformed
        // TODO add your handling code here:
        if (thBtn2T2.isSelected()) {
            disableToggleExcept(QUICK_TH_2);
            ArrayList<Card> resultCards = new ArrayList<Card>();
            resultCards = Utils.quickSearchCard("ชซฌญ", localCardList);
            DefaultTableModel model = (DefaultTableModel) importTableT2.getModel();

            if (resultCards != null) {
                updateTable(resultCards, model);
            }
        } else {
            updateTable(new ArrayList<Card>(), (DefaultTableModel) importTableT2.getModel());
        }
    }//GEN-LAST:event_thBtn2T2ActionPerformed

    private void thBtn3T2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_thBtn3T2ActionPerformed
        // TODO add your handling code here:
        if (thBtn3T2.isSelected()) {
            disableToggleExcept(QUICK_TH_3);
            ArrayList<Card> resultCards = new ArrayList<Card>();
            resultCards = Utils.quickSearchCard("ฎฏฐฑฒณ", localCardList);
            DefaultTableModel model = (DefaultTableModel) importTableT2.getModel();

            if (resultCards != null) {
                updateTable(resultCards, model);
            }
        } else {
            updateTable(new ArrayList<Card>(), (DefaultTableModel) importTableT2.getModel());
        }
    }//GEN-LAST:event_thBtn3T2ActionPerformed

    private void thBtn4T2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_thBtn4T2ActionPerformed
        // TODO add your handling code here:
        if (thBtn4T2.isSelected()) {
            disableToggleExcept(QUICK_TH_4);
            ArrayList<Card> resultCards = new ArrayList<Card>();
            resultCards = Utils.quickSearchCard("ดตถทธ", localCardList);
            DefaultTableModel model = (DefaultTableModel) importTableT2.getModel();

            if (resultCards != null) {
                updateTable(resultCards, model);
            }
        } else {
            updateTable(new ArrayList<Card>(), (DefaultTableModel) importTableT2.getModel());
        }
    }//GEN-LAST:event_thBtn4T2ActionPerformed

    private void thBtn5T2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_thBtn5T2ActionPerformed
        // TODO add your handling code here:
        if (thBtn5T2.isSelected()) {
            disableToggleExcept(QUICK_TH_5);
            ArrayList<Card> resultCards = new ArrayList<Card>();
            resultCards = Utils.quickSearchCard("นบปผฝ", localCardList);
            DefaultTableModel model = (DefaultTableModel) importTableT2.getModel();

            if (resultCards != null) {
                updateTable(resultCards, model);
            }
        } else {
            updateTable(new ArrayList<Card>(), (DefaultTableModel) importTableT2.getModel());
        }
    }//GEN-LAST:event_thBtn5T2ActionPerformed

    private void thBtn6T2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_thBtn6T2ActionPerformed
        // TODO add your handling code here:
        if (thBtn6T2.isSelected()) {
            disableToggleExcept(QUICK_TH_6);
            ArrayList<Card> resultCards = new ArrayList<Card>();
            resultCards = Utils.quickSearchCard("พฟภมย", localCardList);
            DefaultTableModel model = (DefaultTableModel) importTableT2.getModel();

            if (resultCards != null) {
                updateTable(resultCards, model);
            }
        } else {
            updateTable(new ArrayList<Card>(), (DefaultTableModel) importTableT2.getModel());
        }
    }//GEN-LAST:event_thBtn6T2ActionPerformed

    private void thBtn7T2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_thBtn7T2ActionPerformed
        // TODO add your handling code here:
        if (thBtn7T2.isSelected()) {
            disableToggleExcept(QUICK_TH_7);
            ArrayList<Card> resultCards = new ArrayList<Card>();
            resultCards = Utils.quickSearchCard("รลวศษ", localCardList);
            DefaultTableModel model = (DefaultTableModel) importTableT2.getModel();

            if (resultCards != null) {
                updateTable(resultCards, model);
            }
        } else {
            updateTable(new ArrayList<Card>(), (DefaultTableModel) importTableT2.getModel());
        }
    }//GEN-LAST:event_thBtn7T2ActionPerformed

    private void thBtn8T2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_thBtn8T2ActionPerformed
        // TODO add your handling code here:
        if (thBtn8T2.isSelected()) {
            disableToggleExcept(QUICK_TH_8);
            ArrayList<Card> resultCards = new ArrayList<Card>();
            resultCards = Utils.quickSearchCard("สหฬอฮ", localCardList);
            DefaultTableModel model = (DefaultTableModel) importTableT2.getModel();

            if (resultCards != null) {
                updateTable(resultCards, model);
            }
        } else {
            updateTable(new ArrayList<Card>(), (DefaultTableModel) importTableT2.getModel());
        }
    }//GEN-LAST:event_thBtn8T2ActionPerformed

    private void langaugeChangeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_langaugeChangeBtnActionPerformed
        // TODO add your handling code here:
        if (languageLb.getText().equals(myResourceMap.getString("languageLb.text"))) {
            changeLanguageTo(TH);

        } else {
            if (languageLb.getText().equals(myResourceMap.getString("languageLbTh.text"))) {
                changeLanguageTo(EN);
            }
        }
    }//GEN-LAST:event_langaugeChangeBtnActionPerformed

    private void emailBtnT1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emailBtnT1ActionPerformed
        // TODO add your handling code here:
        Card c = getCardFromForm(SCAN_TAB);
        if (Utils.checkFirstName(c, MISSING_ALERT,getDefaultFont())) {
            SendEmailUtil.sendEmail("", "", c, c.getImgFront(), c.getImgBack());
        }
    }//GEN-LAST:event_emailBtnT1ActionPerformed

    private void replaceBtnT4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replaceBtnT4ActionPerformed
        // TODO add your handling code here:
        String fileName = importTfT4.getText();
        if (fileName != null && !fileName.equals("")) {
            localCardList.clear();
            localCardList = ZipUtils.importCards(fileName);
            Font f = getDefaultFont();
            String[] options = getAcceptChoice();
            String[] alert = myResourceMap.getString(IMPORT_ALERT).split(",");
            String alertTxt = "<html><body><FONT FACE=\""+f.getName()+"\" >"+alert[0] + " " + localCardList.size() + " " + alert[1]+"</FONT></body></html>";
            
            JOptionPane.showOptionDialog(null, alertTxt, myResourceMap.getString(NOTIFICATION_ALERT),
                    JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                    null, options, options[0]);
        }
    }//GEN-LAST:event_replaceBtnT4ActionPerformed

    private void pendingBtnT4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pendingBtnT4ActionPerformed
        // TODO add your handling code here:
        String fileName = importTfT4.getText();
        if (fileName != null && !fileName.equals("")) {
            int size = localCardList.size();
            localCardList.addAll(ZipUtils.importCards(fileName));
            size = localCardList.size() - size;
            Font f = getDefaultFont();
            String[] options = getAcceptChoice();
            String[] alert = myResourceMap.getString(IMPORT_ALERT).split(",");
            String alertTxt = "<html><body><FONT FACE=\""+f.getName()+"\" >"+alert[0] + " " + localCardList.size() + " " + alert[1]+"</FONT></body></html>";
            
            JOptionPane.showOptionDialog(null, alertTxt, myResourceMap.getString(NOTIFICATION_ALERT),
                    JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                    null, options, options[0]);
        }
    }//GEN-LAST:event_pendingBtnT4ActionPerformed

    private void importBrowseBtnT4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importBrowseBtnT4ActionPerformed
        // TODO add your handling code here:
        String wd = System.getProperty("user.dir");
        String filename = "";
        JFileChooser fc = new JFileChooser(wd);
        fc.addChoosableFileFilter(new ZipFileFilter());

        int rc = fc.showDialog(null, "Select");
        if (rc == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            filename = file.getAbsolutePath();

            importTfT4.setText(filename);
            // call your function here
        }
    }//GEN-LAST:event_importBrowseBtnT4ActionPerformed

    private void emailBtnT3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emailBtnT3ActionPerformed
        Card c = getCardFromForm(RESULT_TAB);
        if (Utils.checkFirstName(c, MISSING_ALERT,getDefaultFont())) {
            SendEmailUtil.sendEmail("", "", c, c.getImgFront(), c.getImgBack());
        }
    }//GEN-LAST:event_emailBtnT3ActionPerformed

    private void changeLanguageTo(int lang) {
        switch (lang) {
            case EN:
                setLang("");
                break;
            case TH:
                setLang("Th");
                break;
            default:
                setLang("");
                break;
        }
    }

    private Card getCardById(long l, ArrayList<Card> cards) {
        Card card = null;
        for (Card c : cards) {
            if (c.getId() == l) {
                card = c;
            }
        }
        return card;
    }

    private void setLang(String s) {
        //set langauge for general component
        menuTab.setTitleAt(SCAN_TAB, myResourceMap.getString("scannerTab.TabConstraints.tabTitle" + s));
        menuTab.setTitleAt(QUERY_TAB, myResourceMap.getString("queryTab.TabConstraints.tabTitle" + s));
        menuTab.setTitleAt(RESULT_TAB, myResourceMap.getString("resultTab.TabConstraints.tabTitle" + s));
        menuTab.setTitleAt(IMPORT_EXPORT_TAB, myResourceMap.getString("importExportTab.TabConstraints.tabTitle" + s));

        languageLb.setText(myResourceMap.getString("languageLb" + s + ".text"));
        langaugeChangeBtn.setText(myResourceMap.getString("langaugeChangeBtn" + s + ".text"));

        databaseLb.setText(myResourceMap.getString("databaseLb" + s + ".text"));

        //set language for Scanner tab
        scannerLbT1.setText(myResourceMap.getString("scannerLbT1" + s + ".text"));
        scannerBtnT1.setText(myResourceMap.getString("scannerBtnT1" + s + ".text"));
        blackWhiteBtnT1.setText(myResourceMap.getString("blackWhiteBtnT1" + s + ".text"));
        doubleSideBtnT1.setText(myResourceMap.getString("doubleSideBtnT1" + s + ".text"));
        autoCropBtnT1.setText(myResourceMap.getString("autoCropBtnT1" + s + ".text"));
        readCardBtnT1.setText(myResourceMap.getString("readCardBtnT1" + s + ".text"));
        rotateBtnT1.setText(myResourceMap.getString("rotateBtnT1" + s + ".text"));
        emailBtnT1.setText(myResourceMap.getString("emailBtnT1" + s + ".text"));
        undoBtnT1.setText(myResourceMap.getString("undoBtnT1" + s + ".text"));
        confirmBtnT1.setText(myResourceMap.getString("confirmBtnT1" + s + ".text"));
        brightLbT1.setText(myResourceMap.getString("brightLbT1" + s + ".text"));

        scanBtn.setText(myResourceMap.getString("scanBtn" + s + ".text"));
        saveBtnT1.setText(myResourceMap.getString("saveBtnT1" + s + ".text"));

        sideLbT1.setText(myResourceMap.getString("sideLbT1" + s + ".text"));
        frontSideRdT1.setText(myResourceMap.getString("frontSideRdT1" + s + ".text"));
        backSideRdT1.setText(myResourceMap.getString("backSideRdT1" + s + ".text"));

        frontBtnT1.setText(myResourceMap.getString("frontBtnT1" + s + ".text"));
        backBtnT1.setText(myResourceMap.getString("backBtnT1" + s + ".text"));

        frontCropBtnT1.setText(myResourceMap.getString("frontCropBtnT1" + s + ".text"));
        backCropBtnT1.setText(myResourceMap.getString("backCropBtnT1" + s + ".text"));

        //set language for Query tab
        genSearchT2.setText(myResourceMap.getString("genSearchT2" + s + ".text"));
        quickLb.setText(myResourceMap.getString("quickLb" + s + ".text"));

        deletedBtnT2.setText(myResourceMap.getString("deletedBtnT2" + s + ".text"));
        editBtnT2.setText(myResourceMap.getString("editBtnT2" + s + ".text"));

        //set language for Result tab
        idLbT3.setText(myResourceMap.getString("idLbT3" + s + ".text"));
        blackWhiteBtnT3.setText(myResourceMap.getString("blackWhiteBtnT3" + s + ".text"));
        autoCropBtnT3.setText(myResourceMap.getString("autoCropBtnT3" + s + ".text"));
        rotateBtnT3.setText(myResourceMap.getString("rotateBtnT3" + s + ".text"));
        emailBtnT3.setText(myResourceMap.getString("emailBtnT3" + s + ".text"));
        undoBtnT3.setText(myResourceMap.getString("undoBtnT3" + s + ".text"));
        confirmBtnT3.setText(myResourceMap.getString("confirmBtnT3" + s + ".text"));
        brightLbT3.setText(myResourceMap.getString("brightLbT3" + s + ".text"));

        saveBtnT3.setText(myResourceMap.getString("saveBtnT3" + s + ".text"));

        sideLbT3.setText(myResourceMap.getString("sideLbT3" + s + ".text"));
        frontSideRdT3.setText(myResourceMap.getString("frontSideRdT3" + s + ".text"));
        backSideRdT3.setText(myResourceMap.getString("backSideRdT3" + s + ".text"));

        frontBtnT3.setText(myResourceMap.getString("frontBtnT3" + s + ".text"));
        backBtnT3.setText(myResourceMap.getString("backBtnT3" + s + ".text"));

        frontCropBtnT3.setText(myResourceMap.getString("frontCropBtnT3" + s + ".text"));
        backCropBtnT3.setText(myResourceMap.getString("backCropBtnT3" + s + ".text"));

        //set language for Import Export tab
        importPanel.setBorder(BorderFactory.createTitledBorder(null, myResourceMap.getString("importPanel.border.title" + s), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, getDefaultFont(), myResourceMap.getColor("importPanel.border.titleColor"))); // NOI18N
        exportPanel.setBorder(BorderFactory.createTitledBorder(null, myResourceMap.getString("exportPanel.border.title" + s), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, getDefaultFont(), myResourceMap.getColor("exportPanel.border.titleColor"))); // NOI18N

        importLbT4.setText(myResourceMap.getString("importLbT4" + s + ".text"));
        importBrowseBtnT4.setText(myResourceMap.getString("importBrowseBtnT4" + s + ".text"));
        replaceBtnT4.setText(myResourceMap.getString("replaceBtnT4" + s + ".text"));
        pendingBtnT4.setText(myResourceMap.getString("pendingBtnT4" + s + ".text"));

        exportLbT4.setText(myResourceMap.getString("exportLbT4" + s + ".text"));
        browseExportBtnT4.setText(myResourceMap.getString("browseExportBtnT4" + s + ".text"));
        exportBtnT4.setText(myResourceMap.getString("exportBtnT4" + s + ".text"));

        //set Alert language
        ADD_ALERT = "addAlertT1" + s + ".text";
        UPDATE_ALERT = "updateAlertT3" + s + ".text";
        MISSING_ALERT = "missingName" + s + ".text";
        DELETE_ALERT = "deleteAlert" + s + ".text";
        CONFIRM_ALERT = "confirmAlert" + s + ".text";
        YES_CHOICE = "yesChoice" + s + ".text";
        NO_CHOICE = "noChoice" + s + ".text";
        IMPORT_ALERT = "importAlert" + s + ".text";
        EXPORT_ALERT = "exportAlert" + s + ".text";
        ACCEPT_CHOICE = "acceptChoice" + s + ".text";
        NOTIFICATION_ALERT = "notificationAlert" + s + ".text";

        //set crop panel
        CROP_PANEL_TITLE = "cropPanelTitle" + s + ".text";
        CROP_CONFIRM = "cropConfirm" + s + ".text";
        CROP_CANCEL = "cropCancel" + s + ".text";
        CROP_FOCUS = "cropFocus" + s + ".text";
    }

    private String[] getOptionChoice() {
        Font f = getDefaultFont();
        String[] option = new String[]{"<html><body><FONT FACE=\""+f.getName()+"\" >"+myResourceMap.getString(YES_CHOICE)+"</FONT></body></html>",
                            "<html><body><FONT FACE=\""+f.getName()+"\" >"+myResourceMap.getString(NO_CHOICE)+"</FONT></body></html>"
                          };
        return option;
    }

    private String[] getAcceptChoice() {
        String[] option = new String[]{"<html><body><FONT FACE=\""+getDefaultFont().getName()+"\" >"+myResourceMap.getString(ACCEPT_CHOICE)+"</FONT></body></html>",};
        return option;
    }

    private Card getCardFromForm(int index) {
        Card newCard;
        switch (index) {
            case 0:
                newCard = getCardFromT1();
                break;
            case 1:
                newCard = getCardFromT2();
                break;
            case 2:
                newCard = getCardFromT3();
                break;
            default:
                newCard = new Card();
        }
        return newCard;
    }

    private Card getCardFromT1() {
        Card newCard;

        String name = nameTfT1.getText();
        String lastName = lastnameTfT1.getText();
        String title = titleTfT1.getText();
        String email = emailTfT1.getText();
        String company = companyTfT1.getText();
        String web = webTfT1.getText();
        String state = disTfT1.getText();
        String city = subDisTfT1.getText();
        String code = codeTfT1.getText();
        String country = provinceTfT1.getText();
        String mobile = mobileTfT1.getText();
        String phone = phoneTfT1.getText();
        String fax = faxTfT1.getText();
        String ads = adsTaT1.getText();
        String note = noteTaT1.getText();

        String imgFront = validatePath(frontTfT1.getText());
        String imgBack = validatePath(backTfT1.getText());

        String nameE = nameTfTE1.getText();
        String lastNameE = lastnameTfTE1.getText();
        String titleE = titleTfTE1.getText();
        String companyE = companyTfTE1.getText();
        String stateE = disTfTE1.getText();
        String cityE = subDisTfTE1.getText();
        String codeE = codeTfTE1.getText();
        String countryE = provinceTfTE1.getText();
        String mobileE = mobileTfTE1.getText();
        String phoneE = phoneTfTE1.getText();
        String faxE = faxTfTE1.getText();
        String adsE = adsTaTE1.getText();
        String noteE = noteTaTE1.getText();

        return newCard = new Card(name, lastName, title, email, company, web, ads, city, state, country, code, phone, fax, mobile, note,
                imgFront, imgBack,
                nameE, lastNameE, titleE, companyE, adsE, cityE, stateE, countryE, codeE, phoneE, faxE, mobileE, noteE);
    }

    private Card getCardFromT2() {
        Card newCard;

        String name = nameTfT2.getText();
        String lastName = lastnameTfT2.getText();
        String title = titleTfT2.getText();
        String email = emailTfT2.getText();
        String company = companyTfT2.getText();
        String web = webTfT2.getText();
        String state = disTfT2.getText();
        String city = subDisTfT2.getText();
        String code = codeTfT2.getText();
        String country = provinceTfT2.getText();
        String mobile = mobileTfT2.getText();
        String phone = phoneTfT2.getText();
        String fax = faxTfT2.getText();
        String ads = adsTaT2.getText();
        String note = noteTaT2.getText();

        String nameE = nameTfTE2.getText();
        String lastNameE = lastnameTfTE2.getText();
        String titleE = titleTfTE2.getText();
        String companyE = companyTfTE2.getText();
        String stateE = disTfTE2.getText();
        String cityE = subDisTfTE2.getText();
        String codeE = codeTfTE2.getText();
        String countryE = provinceTfTE2.getText();
        String mobileE = mobileTfTE2.getText();
        String phoneE = phoneTfTE2.getText();
        String faxE = faxTfTE2.getText();
        String adsE = adsTaTE2.getText();
        String noteE = noteTaTE2.getText();

        return newCard = new Card(name, lastName, title, email, company, web, ads, city, state, country, code, phone, fax, mobile, note,
                "", "",
                nameE, lastNameE, titleE, companyE, adsE, cityE, stateE, countryE, codeE, phoneE, faxE, mobileE, noteE);
    }

    private Card getCardFromT3() {
        Card newCard;

        String name = nameTfT3.getText();
        String lastName = lastnameTfT3.getText();
        String title = titleTfT3.getText();
        String email = emailTfT3.getText();
        String company = companyTfT3.getText();
        String web = webTfT3.getText();
        String state = disTfT3.getText();
        String city = subDisTfT3.getText();
        String code = codeTfT3.getText();
        String country = provinceTfT3.getText();
        String mobile = mobileTfT3.getText();
        String phone = phoneTfT3.getText();
        String fax = faxTfT3.getText();
        String ads = adsTaT3.getText();
        String note = noteTaT3.getText();

        String imgFront = validatePath(frontTfT3.getText());
        String imgBack = validatePath(backTfT3.getText());

        String nameE = nameTfTE3.getText();
        String lastNameE = lastnameTfTE3.getText();
        String titleE = titleTfTE3.getText();
        String companyE = companyTfTE3.getText();
        String stateE = disTfTE3.getText();
        String cityE = subDisTfTE3.getText();
        String codeE = codeTfTE3.getText();
        String countryE = provinceTfTE3.getText();
        String mobileE = mobileTfTE3.getText();
        String phoneE = phoneTfTE3.getText();
        String faxE = faxTfTE3.getText();
        String adsE = adsTaTE3.getText();
        String noteE = noteTaTE3.getText();

        return newCard = new Card(name, lastName, title, email, company, web, ads, city, state, country, code, phone, fax, mobile, note,
                imgFront, imgBack,
                nameE, lastNameE, titleE, companyE, adsE, cityE, stateE, countryE, codeE, phoneE, faxE, mobileE, noteE);
    }

    private void clearFormT1() {
        nameTfT1.setText("");
        lastnameTfT1.setText("");
        titleTfT1.setText("");
        emailTfT1.setText("");
        companyTfT1.setText("");
        webTfT1.setText("");
        disTfT1.setText("");
        subDisTfT1.setText("");
        codeTfT1.setText("");
        countryTfT1.setText("");
        provinceTfT1.setText("");
        mobileTfT1.setText("");
        phoneTfT1.setText("");
        faxTfT1.setText("");
        adsTaT1.setText("");
        noteTaT1.setText("");

        frontTfT1.setText("");
        backTfT1.setText("");

        nameTfTE1.setText("");
        lastnameTfTE1.setText("");
        titleTfTE1.setText("");
        companyTfTE1.setText("");
        disTfTE1.setText("");
        subDisTfTE1.setText("");
        codeTfTE1.setText("");
        provinceTfTE1.setText("");
        mobileTfTE1.setText("");
        phoneTfTE1.setText("");
        faxTfTE1.setText("");
        adsTaTE1.setText("");
        noteTaTE1.setText("");

        frontLbT1.setIcon(null);
        backLbT1.setIcon(null);
    }

    private void clearFormT3() {

        idNameLbT3.setText("");

        nameTfT3.setText("");
        lastnameTfT3.setText("");
        titleTfT3.setText("");
        emailTfT3.setText("");
        companyTfT3.setText("");
        webTfT3.setText("");
        disTfT3.setText("");
        subDisTfT3.setText("");
        codeTfT3.setText("");
        countryTfT3.setText("");
        provinceTfT3.setText("");
        mobileTfT3.setText("");
        phoneTfT3.setText("");
        faxTfT3.setText("");
        adsTaT3.setText("");
        noteTaT3.setText("");

        frontTfT3.setText("");
        backTfT3.setText("");

        nameTfTE3.setText("");
        lastnameTfTE3.setText("");
        titleTfTE3.setText("");
        companyTfTE3.setText("");
        disTfTE3.setText("");
        subDisTfTE3.setText("");
        codeTfTE3.setText("");
        provinceTfTE3.setText("");
        mobileTfTE3.setText("");
        phoneTfTE3.setText("");
        faxTfTE3.setText("");
        adsTaTE3.setText("");
        noteTaTE3.setText("");

        frontLbT3.setIcon(null);
        backLbT3.setIcon(null);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel adsLbT1;
    private javax.swing.JLabel adsLbT2;
    private javax.swing.JLabel adsLbT3;
    private javax.swing.JLabel adsLbTE1;
    private javax.swing.JLabel adsLbTE2;
    private javax.swing.JLabel adsLbTE3;
    private javax.swing.JPanel adsPanelT1;
    private javax.swing.JPanel adsPanelT2;
    private javax.swing.JPanel adsPanelT3;
    private javax.swing.JPanel adsPanelTE1;
    private javax.swing.JPanel adsPanelTE2;
    private javax.swing.JPanel adsPanelTE3;
    private javax.swing.JScrollPane adsScrT1;
    private javax.swing.JScrollPane adsScrT2;
    private javax.swing.JScrollPane adsScrT3;
    private javax.swing.JScrollPane adsScrTE1;
    private javax.swing.JScrollPane adsScrTE2;
    private javax.swing.JScrollPane adsScrTE3;
    private javax.swing.JTextArea adsTaT1;
    private javax.swing.JTextArea adsTaT2;
    private javax.swing.JTextArea adsTaT3;
    private javax.swing.JTextArea adsTaTE1;
    private javax.swing.JTextArea adsTaTE2;
    private javax.swing.JTextArea adsTaTE3;
    private javax.swing.JButton autoCropBtnT1;
    private javax.swing.JButton autoCropBtnT3;
    private javax.swing.JButton backBtnT1;
    private javax.swing.JButton backBtnT3;
    private javax.swing.JButton backCropBtnT1;
    private javax.swing.JButton backCropBtnT3;
    private javax.swing.JRadioButton backSideRdT1;
    private javax.swing.JRadioButton backSideRdT3;
    private javax.swing.JScrollPane backSpT1;
    private javax.swing.JScrollPane backSpT3;
    private javax.swing.JTextField backTfT1;
    private javax.swing.JTextField backTfT3;
    private javax.swing.JButton blackWhiteBtnT1;
    private javax.swing.JButton blackWhiteBtnT3;
    private javax.swing.JLabel blankLb0;
    private javax.swing.JLabel blankLb1;
    private javax.swing.JLabel blankLb2;
    private javax.swing.JLabel blankLb3;
    private javax.swing.JLabel blankLb4;
    private javax.swing.JLabel blankLb5;
    private javax.swing.JLabel blankLb6;
    private javax.swing.JLabel blankLb7;
    private javax.swing.JLabel blankLb8;
    private javax.swing.JLabel blankLb9;
    private javax.swing.JPanel blankPanel14;
    private javax.swing.JPanel blankPanel6;
    private javax.swing.JPanel blankPanelT4;
    private javax.swing.JLabel brightLbT1;
    private javax.swing.JLabel brightLbT3;
    private javax.swing.JPanel brightPanelT1;
    private javax.swing.JPanel brightPanelT3;
    private javax.swing.JSlider brightSldT1;
    private javax.swing.JSlider brightSldT3;
    private javax.swing.JButton browseExportBtnT4;
    private javax.swing.JPanel btnPanel0T2;
    private javax.swing.JPanel btnPanel1T2;
    private javax.swing.JPanel btnPanel2T2;
    private javax.swing.JLabel codeLbT1;
    private javax.swing.JLabel codeLbT2;
    private javax.swing.JLabel codeLbT3;
    private javax.swing.JLabel codeLbTE1;
    private javax.swing.JLabel codeLbTE2;
    private javax.swing.JLabel codeLbTE3;
    private javax.swing.JTextField codeTfT1;
    private javax.swing.JTextField codeTfT2;
    private javax.swing.JTextField codeTfT3;
    private javax.swing.JTextField codeTfTE1;
    private javax.swing.JTextField codeTfTE2;
    private javax.swing.JTextField codeTfTE3;
    private javax.swing.JLabel companyLbT1;
    private javax.swing.JLabel companyLbT2;
    private javax.swing.JLabel companyLbT3;
    private javax.swing.JLabel companyLbTE1;
    private javax.swing.JLabel companyLbTE2;
    private javax.swing.JLabel companyLbTE3;
    private javax.swing.JTextField companyTfT1;
    private javax.swing.JTextField companyTfT2;
    private javax.swing.JTextField companyTfT3;
    private javax.swing.JTextField companyTfTE1;
    private javax.swing.JTextField companyTfTE2;
    private javax.swing.JTextField companyTfTE3;
    private javax.swing.JButton confirmBtnT1;
    private javax.swing.JButton confirmBtnT3;
    private javax.swing.JLabel countryLbT1;
    private javax.swing.JLabel countryLbT2;
    private javax.swing.JLabel countryLbT3;
    private javax.swing.JLabel countryLbTE1;
    private javax.swing.JLabel countryLbTE2;
    private javax.swing.JLabel countryLbTE3;
    private javax.swing.JTextField countryTfT1;
    private javax.swing.JTextField countryTfT2;
    private javax.swing.JTextField countryTfT3;
    private javax.swing.JTextField countryTfTE1;
    private javax.swing.JTextField countryTfTE2;
    private javax.swing.JTextField countryTfTE3;
    private javax.swing.JLabel databaseLb;
    private javax.swing.JLabel databaseNameLb;
    private javax.swing.JPanel deleteEditPanelT2;
    private javax.swing.JButton deletedBtnT2;
    private javax.swing.JLabel disLbT1;
    private javax.swing.JLabel disLbT2;
    private javax.swing.JLabel disLbT3;
    private javax.swing.JLabel disLbTE1;
    private javax.swing.JLabel disLbTE2;
    private javax.swing.JLabel disLbTE3;
    private javax.swing.JTextField disTfT1;
    private javax.swing.JTextField disTfT2;
    private javax.swing.JTextField disTfT3;
    private javax.swing.JTextField disTfTE1;
    private javax.swing.JTextField disTfTE2;
    private javax.swing.JTextField disTfTE3;
    private javax.swing.JButton doubleSideBtnT1;
    private javax.swing.JButton editBtnT2;
    private javax.swing.JButton emailBtnT1;
    private javax.swing.JButton emailBtnT3;
    private javax.swing.JLabel emailLbT1;
    private javax.swing.JLabel emailLbT2;
    private javax.swing.JLabel emailLbT3;
    private javax.swing.JTextField emailTfT1;
    private javax.swing.JTextField emailTfT2;
    private javax.swing.JTextField emailTfT3;
    private javax.swing.JToggleButton engBtn0T2;
    private javax.swing.JToggleButton engBtn1T2;
    private javax.swing.JToggleButton engBtn2T2;
    private javax.swing.JToggleButton engBtn3T2;
    private javax.swing.JToggleButton engBtn4T2;
    private javax.swing.JToggleButton engBtn5T2;
    private javax.swing.JToggleButton engBtn6T2;
    private javax.swing.JToggleButton engBtn7T2;
    private javax.swing.JToggleButton engBtn8T2;
    private javax.swing.JButton exportBtnT4;
    private javax.swing.JLabel exportLbT4;
    private javax.swing.JPanel exportPanel;
    private javax.swing.JTextField exportTfT4;
    private javax.swing.JLabel faxLbT1;
    private javax.swing.JLabel faxLbT2;
    private javax.swing.JLabel faxLbT3;
    private javax.swing.JLabel faxLbTE1;
    private javax.swing.JLabel faxLbTE2;
    private javax.swing.JLabel faxLbTE3;
    private javax.swing.JTextField faxTfT1;
    private javax.swing.JTextField faxTfT2;
    private javax.swing.JTextField faxTfT3;
    private javax.swing.JTextField faxTfTE1;
    private javax.swing.JTextField faxTfTE2;
    private javax.swing.JTextField faxTfTE3;
    private javax.swing.JButton frontBtnT1;
    private javax.swing.JButton frontBtnT3;
    private javax.swing.JButton frontCropBtnT1;
    private javax.swing.JButton frontCropBtnT3;
    private javax.swing.JPanel frontPanelT1;
    private javax.swing.JRadioButton frontSideRdT1;
    private javax.swing.JRadioButton frontSideRdT3;
    private javax.swing.JScrollPane frontSpT1;
    private javax.swing.JScrollPane frontSpT3;
    private javax.swing.JTextField frontTfT1;
    private javax.swing.JTextField frontTfT3;
    private javax.swing.JPanel genSearchPanelT2;
    private javax.swing.JButton genSearchT2;
    private javax.swing.JTextField genSearchTfT2;
    private javax.swing.JLabel idLbT3;
    private javax.swing.JLabel idNameLbT3;
    private javax.swing.JPanel idPanelT3;
    private javax.swing.JButton importBrowseBtnT4;
    private javax.swing.JPanel importExportTab;
    private javax.swing.JLabel importLbT4;
    private javax.swing.JPanel importPanel;
    private javax.swing.JPanel importPanelT4;
    private javax.swing.JTable importTableT2;
    private javax.swing.JTextField importTfT4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JButton langaugeChangeBtn;
    private javax.swing.JLabel languageLb;
    private javax.swing.JLabel lastnameLbT1;
    private javax.swing.JLabel lastnameLbT2;
    private javax.swing.JLabel lastnameLbT3;
    private javax.swing.JLabel lastnameLbTE1;
    private javax.swing.JLabel lastnameLbTE2;
    private javax.swing.JLabel lastnameLbTE3;
    private javax.swing.JTextField lastnameTfT1;
    private javax.swing.JTextField lastnameTfT2;
    private javax.swing.JTextField lastnameTfT3;
    private javax.swing.JTextField lastnameTfTE1;
    private javax.swing.JTextField lastnameTfTE2;
    private javax.swing.JTextField lastnameTfTE3;
    private javax.swing.JPanel low;
    private javax.swing.JPanel lowLeftT1;
    private javax.swing.JPanel lowLeftT3;
    private javax.swing.JPanel lowPanelT2;
    private javax.swing.JPanel lowRightT1;
    private javax.swing.JPanel lowRightT3;
    private javax.swing.JPanel lowT3;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTabbedPane menuTab;
    private javax.swing.JLabel mobileLbT1;
    private javax.swing.JLabel mobileLbT2;
    private javax.swing.JLabel mobileLbT3;
    private javax.swing.JLabel mobileLbTE1;
    private javax.swing.JLabel mobileLbTE2;
    private javax.swing.JLabel mobileLbTE3;
    private javax.swing.JTextField mobileTfT1;
    private javax.swing.JTextField mobileTfT2;
    private javax.swing.JTextField mobileTfT3;
    private javax.swing.JTextField mobileTfTE1;
    private javax.swing.JTextField mobileTfTE2;
    private javax.swing.JTextField mobileTfTE3;
    private javax.swing.JLabel nameLbT1;
    private javax.swing.JLabel nameLbT2;
    private javax.swing.JLabel nameLbT3;
    private javax.swing.JLabel nameLbTE1;
    private javax.swing.JLabel nameLbTE2;
    private javax.swing.JLabel nameLbTE3;
    private javax.swing.JTextField nameTfT1;
    private javax.swing.JTextField nameTfT2;
    private javax.swing.JTextField nameTfT3;
    private javax.swing.JTextField nameTfTE1;
    private javax.swing.JTextField nameTfTE2;
    private javax.swing.JTextField nameTfTE3;
    private javax.swing.JLabel noteLbT1;
    private javax.swing.JLabel noteLbT2;
    private javax.swing.JLabel noteLbT3;
    private javax.swing.JLabel noteLbTE1;
    private javax.swing.JLabel noteLbTE2;
    private javax.swing.JLabel noteLbTE3;
    private javax.swing.JPanel notePanelT1;
    private javax.swing.JPanel notePanelT2;
    private javax.swing.JPanel notePanelT3;
    private javax.swing.JPanel notePanelTE1;
    private javax.swing.JPanel notePanelTE2;
    private javax.swing.JPanel notePanelTE3;
    private javax.swing.JScrollPane noteScrT1;
    private javax.swing.JScrollPane noteScrT2;
    private javax.swing.JScrollPane noteScrT3;
    private javax.swing.JScrollPane noteScrTE1;
    private javax.swing.JScrollPane noteScrTE2;
    private javax.swing.JScrollPane noteScrTE3;
    private javax.swing.JTextArea noteTaT1;
    private javax.swing.JTextArea noteTaT2;
    private javax.swing.JTextArea noteTaT3;
    private javax.swing.JTextArea noteTaTE1;
    private javax.swing.JTextArea noteTaTE2;
    private javax.swing.JTextArea noteTaTE3;
    private javax.swing.JButton pendingBtnT4;
    private javax.swing.JLabel phoneLbT1;
    private javax.swing.JLabel phoneLbT2;
    private javax.swing.JLabel phoneLbT3;
    private javax.swing.JLabel phoneLbTE1;
    private javax.swing.JLabel phoneLbTE2;
    private javax.swing.JLabel phoneLbTE3;
    private javax.swing.JTextField phoneTfT1;
    private javax.swing.JTextField phoneTfT2;
    private javax.swing.JTextField phoneTfT3;
    private javax.swing.JTextField phoneTfTE1;
    private javax.swing.JTextField phoneTfTE2;
    private javax.swing.JTextField phoneTfTE3;
    private javax.swing.JLabel provinceLbT1;
    private javax.swing.JLabel provinceLbT2;
    private javax.swing.JLabel provinceLbT3;
    private javax.swing.JLabel provinceLbTE1;
    private javax.swing.JLabel provinceLbTE2;
    private javax.swing.JLabel provinceLbTE3;
    private javax.swing.JTextField provinceTfT1;
    private javax.swing.JTextField provinceTfT2;
    private javax.swing.JTextField provinceTfT3;
    private javax.swing.JTextField provinceTfTE1;
    private javax.swing.JTextField provinceTfTE2;
    private javax.swing.JTextField provinceTfTE3;
    private javax.swing.JPanel queryTab;
    private javax.swing.JLabel quickLb;
    private javax.swing.JPanel quickPanelT2;
    private javax.swing.JButton readCardBtnT1;
    private javax.swing.JButton replaceBtnT4;
    private javax.swing.JPanel resultTab;
    private javax.swing.JButton rotateBtnT1;
    private javax.swing.JButton rotateBtnT3;
    private javax.swing.JButton saveBtnT1;
    private javax.swing.JButton saveBtnT3;
    private javax.swing.JButton scanBtn;
    private javax.swing.JButton scannerBtnT1;
    private javax.swing.JLabel scannerLbT1;
    private javax.swing.JPanel scannerPanel;
    private javax.swing.JPanel scannerTab;
    private javax.swing.JTextField scannerTxtT1;
    private javax.swing.JPanel selSidePanelT1;
    private javax.swing.JPanel selSidePanelT3;
    private javax.swing.JLabel sideLbT1;
    private javax.swing.JLabel sideLbT3;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JLabel subDisLbT1;
    private javax.swing.JLabel subDisLbT2;
    private javax.swing.JLabel subDisLbT3;
    private javax.swing.JLabel subDisLbTE1;
    private javax.swing.JLabel subDisLbTE2;
    private javax.swing.JLabel subDisLbTE3;
    private javax.swing.JTextField subDisTfT1;
    private javax.swing.JTextField subDisTfT2;
    private javax.swing.JTextField subDisTfT3;
    private javax.swing.JTextField subDisTfTE1;
    private javax.swing.JTextField subDisTfTE2;
    private javax.swing.JTextField subDisTfTE3;
    private javax.swing.JPanel tablePanelT2;
    private javax.swing.JToggleButton thBtn0T2;
    private javax.swing.JToggleButton thBtn1T2;
    private javax.swing.JToggleButton thBtn2T2;
    private javax.swing.JToggleButton thBtn3T2;
    private javax.swing.JToggleButton thBtn4T2;
    private javax.swing.JToggleButton thBtn5T2;
    private javax.swing.JToggleButton thBtn6T2;
    private javax.swing.JToggleButton thBtn7T2;
    private javax.swing.JToggleButton thBtn8T2;
    private javax.swing.JLabel titleLbT1;
    private javax.swing.JLabel titleLbT2;
    private javax.swing.JLabel titleLbT3;
    private javax.swing.JLabel titleLbTE1;
    private javax.swing.JLabel titleLbTE2;
    private javax.swing.JLabel titleLbTE3;
    private javax.swing.JTextField titleTfT1;
    private javax.swing.JTextField titleTfT2;
    private javax.swing.JTextField titleTfT3;
    private javax.swing.JTextField titleTfTE1;
    private javax.swing.JTextField titleTfTE2;
    private javax.swing.JTextField titleTfTE3;
    private javax.swing.JButton undoBtnT1;
    private javax.swing.JButton undoBtnT3;
    private javax.swing.JScrollPane upLeftScrollPaneT1;
    private javax.swing.JScrollPane upLeftScrollPaneT2;
    private javax.swing.JScrollPane upLeftScrollPaneT3;
    private javax.swing.JPanel upLeftT1;
    private javax.swing.JPanel upLeftT2;
    private javax.swing.JPanel upLeftT3;
    private javax.swing.JPanel upRightT1;
    private javax.swing.JPanel upRightT2;
    private javax.swing.JPanel upRightT3;
    private javax.swing.JLabel webLbT1;
    private javax.swing.JLabel webLbT2;
    private javax.swing.JLabel webLbT3;
    private javax.swing.JTextField webTfT1;
    private javax.swing.JTextField webTfT2;
    private javax.swing.JTextField webTfT3;
    // End of variables declaration//GEN-END:variables
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    //file chooser
    private/*final*/ javax.swing.JFileChooser dbChooser = new javax.swing.JFileChooser();
    private/*final*/ javax.swing.JFileChooser imgChooser = new javax.swing.JFileChooser();
    private JLabel frontLbT1;
    private JLabel backLbT1;
    private JLabel frontLbT3;
    private JLabel backLbT3;
    //card local manager
    private ArrayList<Card> localCardList;
    private File defaultcard;
    private JDialog aboutBox;
    // For Yov's part: image processing and scanner variables
    private CardScanner bcScanner;
    private BusinessCard scannedBCard, scannedBCardBack;
    public BufferedImage scannedImage;
    public String scannedImageFileName, scannedImageFileNameBack;
    private boolean isFrontSelected;
    private BusinessCard resultBCard, resultBCardBack;
    private BufferedImage resultImage;
    private boolean isFrontSelectedResult;
    private boolean isBrowsedFront, isBrowsedBack;
    private boolean isBrowsedFrontResult, isBrowsedBackResult;
    private int frontUIState, backUIState;
    private int frontUIStateResult, backUIStateResult;
    private String ADD_ALERT = "addAlertT1.text";
    private String UPDATE_ALERT = "updateAlertT3.text";
    private String NOTIFICATION_ALERT = "notificationAlert.text";
    private String MISSING_ALERT = "missingName.text";
    private String DELETE_ALERT = "deleteAlert.text";
    private String CONFIRM_ALERT = "confirmAlert.text";
    private String YES_CHOICE = "yesChoice.text";
    private String NO_CHOICE = "noChoice.text";
    private String IMPORT_ALERT = "importAlert.text";
    private String EXPORT_ALERT = "exportAlert.text";
    private String ACCEPT_CHOICE = "acceptChoice.text";
    private String CROP_PANEL_TITLE = "cropPanelTitle.text";
    private String CROP_CONFIRM = "cropConfirm.text";
    private String CROP_CANCEL = "cropCancel.text";
    private String CROP_FOCUS = "cropFocus.text";
    private final int STATE_NO_IMAGE = 0;
    private final int STATE_WITH_IMAGE = 1;
    private final int STATE_IMAGE_EDITED = 2;
    private final int SCAN_TAB = 0;
    private final int QUERY_TAB = 1;
    private final int RESULT_TAB = 2;
    private final int IMPORT_EXPORT_TAB = 3;
    private final int QUICK_NONE = -1;
    private final int QUICK_TH_0 = 0;
    private final int QUICK_TH_1 = 1;
    private final int QUICK_TH_2 = 2;
    private final int QUICK_TH_3 = 3;
    private final int QUICK_TH_4 = 4;
    private final int QUICK_TH_5 = 5;
    private final int QUICK_TH_6 = 6;
    private final int QUICK_TH_7 = 7;
    private final int QUICK_TH_8 = 8;
    private final int QUICK_EN_0 = 9;
    private final int QUICK_EN_1 = 10;
    private final int QUICK_EN_2 = 11;
    private final int QUICK_EN_3 = 12;
    private final int QUICK_EN_4 = 13;
    private final int QUICK_EN_5 = 14;
    private final int QUICK_EN_6 = 15;
    private final int QUICK_EN_7 = 16;
    private final int QUICK_EN_8 = 17;
    private final int EN = 0;
    private final int TH = 1;
    private final int WIN_WIDTH = 450;
    private final int WIN_HEIGHT = 180;
    private XTableColumnModel xCol;
    private Font defaultFont;

}
