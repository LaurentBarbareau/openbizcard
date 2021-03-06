/*
 * ScannerView.java
 */

package com.jen.scanner.ui;

import com.hideoaki.scanner.db.manager.CardLocalManager;
import com.hideoaki.scanner.db.model.Card;
import com.hideoaki.scanner.db.model.Group;
import com.hideoaki.scanner.db.utils.ScannerDBException;
import com.jen.scanner.ui.util.DBFileFilter;
import com.jen.scanner.ui.util.JPGFileFilter;
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
import java.util.Hashtable;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.util.Hashtable;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.table.DefaultTableModel;

// From Yov's part
import com.roncemer.ocr.*;
import com.yov.scanner.imageprocessing.CardScanner;
import com.yov.scanner.imageprocessing.BusinessCard;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * The application's main frame.
 */
public class ScannerView extends FrameView {

    public ScannerView(SingleFrameApplication app) {
        super(app);
  
        initComponents();
        
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
                statusMessageLabel.setText("");
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
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String)(evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer)(evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
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
        trainingImgFolder = "";
        File curDir = new File(".");
        try{
            scannedImageFileName = curDir.getCanonicalPath() + "scannedBCard";
            scannedImageFileNameBack = curDir.getCanonicalPath() + "scannedBCardBack";
            trainingImgFolder = curDir.getCanonicalPath() + "\\src\\com\\yov\\scanner\\images\\trainingImages";
        }catch(Exception e){
            e.printStackTrace();
        }
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
        blankPanel0 = new javax.swing.JPanel();
        blankPanel1 = new javax.swing.JPanel();
        nameLbTE1 = new javax.swing.JLabel();
        nameTfTE1 = new javax.swing.JTextField();
        blankPanel2 = new javax.swing.JPanel();
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
        blankPanel3 = new javax.swing.JPanel();
        upRightT1 = new javax.swing.JPanel();
        scannerPanel = new javax.swing.JPanel();
        scannerLbT1 = new javax.swing.JLabel();
        scannerBtnT1 = new javax.swing.JButton();
        scannerTxtT1 = new javax.swing.JTextField();
        brightPanelT1 = new javax.swing.JPanel();
        brightLbT1 = new javax.swing.JLabel();
        brightSldT1 = new javax.swing.JSlider();
        doubleSideBtnT1 = new javax.swing.JToggleButton();
        blackWhiteBtn = new javax.swing.JToggleButton();
        cropBtnT1 = new javax.swing.JToggleButton();
        rotateBtnT1 = new javax.swing.JButton();
        emailBtnT1 = new javax.swing.JButton();
        saveToDbBtnT1 = new javax.swing.JButton();
        scanBtn = new javax.swing.JButton();
        undoBtnT1 = new javax.swing.JButton();
        confirmBtnT1 = new javax.swing.JButton();
        readCardBtnT1 = new javax.swing.JButton();
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
        lowRightT1 = new javax.swing.JPanel();
        backTfT1 = new javax.swing.JTextField();
        backBtnT1 = new javax.swing.JButton();
        backLbT1 = new JLabel();
        backSpT1 = new javax.swing.JScrollPane(backLbT1);
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
        blankPanel4 = new javax.swing.JPanel();
        blankPanel11 = new javax.swing.JPanel();
        blankPanel12 = new javax.swing.JPanel();
        nameLbTE2 = new javax.swing.JLabel();
        nameTfTE2 = new javax.swing.JTextField();
        blankPanel13 = new javax.swing.JPanel();
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
        blankPanel14 = new javax.swing.JPanel();
        upRightT2 = new javax.swing.JPanel();
        genSearchPanelT2 = new javax.swing.JPanel();
        genSearchLbT2 = new javax.swing.JLabel();
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
        importTableT2 = new javax.swing.JTable();
        deleteEditPanelT2 = new javax.swing.JPanel();
        deletedBtnT2 = new javax.swing.JButton();
        editBtnT2 = new javax.swing.JButton();
        blankPanel6 = new javax.swing.JPanel();
        resultTab = new javax.swing.JPanel();
        upRightT3 = new javax.swing.JPanel();
        brightPanelT3 = new javax.swing.JPanel();
        brightLbT3 = new javax.swing.JLabel();
        brightSldT3 = new javax.swing.JSlider();
        blackWhiteBtnT3 = new javax.swing.JToggleButton();
        cropBtnT3 = new javax.swing.JToggleButton();
        rotateBtnT3 = new javax.swing.JButton();
        emailBtnT3 = new javax.swing.JButton();
        idPanelT3 = new javax.swing.JPanel();
        idLbT3 = new javax.swing.JLabel();
        idNameLbT3 = new javax.swing.JLabel();
        sideLbT3 = new javax.swing.JLabel();
        frontSideRdT3 = new javax.swing.JRadioButton();
        backSideRdT3 = new javax.swing.JRadioButton();
        undoBtnT3 = new javax.swing.JButton();
        confirmBtnT3 = new javax.swing.JButton();
        lowT3 = new javax.swing.JPanel();
        lowLeftT3 = new javax.swing.JPanel();
        frontTfT3 = new javax.swing.JTextField();
        frontBtnT3 = new javax.swing.JButton();
        frontLbT3 = new JLabel();
        frontSpT3 = new javax.swing.JScrollPane(frontLbT3);
        lowRightT3 = new javax.swing.JPanel();
        backTfT3 = new javax.swing.JTextField();
        backBtnT3 = new javax.swing.JButton();
        backLbT3 = new JLabel();
        backSpT3 = new javax.swing.JScrollPane(backLbT3);
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
        blankPanel5 = new javax.swing.JPanel();
        blankPanel15 = new javax.swing.JPanel();
        blankPanel16 = new javax.swing.JPanel();
        nameLbTE3 = new javax.swing.JLabel();
        nameTfTE3 = new javax.swing.JTextField();
        blankPanel17 = new javax.swing.JPanel();
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
        importExportTab = new javax.swing.JPanel();
        importPanel = new javax.swing.JPanel();
        importPanelT4 = new javax.swing.JPanel();
        importLbT4 = new javax.swing.JLabel();
        importTfT4 = new javax.swing.JTextField();
        importBrowseBtnT4 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        replaceBtnT4 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        addToLocalBtnT4 = new javax.swing.JButton();
        exportPanel = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        exportLbT4 = new javax.swing.JLabel();
        exportTfT4 = new javax.swing.JTextField();
        browseExportBtnT4 = new javax.swing.JButton();
        exportBtnT4 = new javax.swing.JButton();
        blankPanelT4 = new javax.swing.JPanel();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        databaseLb = new javax.swing.JLabel();
        databaseNameLb = new javax.swing.JLabel();

        mainPanel.setMaximumSize(new java.awt.Dimension(704, 616));
        mainPanel.setMinimumSize(new java.awt.Dimension(704, 616));
        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setPreferredSize(new java.awt.Dimension(704, 616));

        menuTab.setMaximumSize(null);
        menuTab.setName("menuTab"); // NOI18N
        menuTab.setPreferredSize(new java.awt.Dimension(665, 623));

        scannerTab.setMaximumSize(new java.awt.Dimension(610, 566));
        scannerTab.setName("scannerTab"); // NOI18N
        scannerTab.setLayout(new java.awt.GridBagLayout());

        upLeftScrollPaneT1.setName("upLeftScrollPaneT1"); // NOI18N
        upLeftScrollPaneT1.setPreferredSize(new java.awt.Dimension(350, 260));

        upLeftT1.setName("upLeftT1"); // NOI18N
        upLeftT1.setPreferredSize(new java.awt.Dimension(670, 250));
        upLeftT1.setLayout(new java.awt.GridBagLayout());

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(com.jen.scanner.ui.ScannerApp.class).getContext().getResourceMap(ScannerView.class);
        nameLbT1.setText(resourceMap.getString("nameLbT1.text")); // NOI18N
        nameLbT1.setName("nameLbT1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(nameLbT1, gridBagConstraints);

        titleLbT1.setText(resourceMap.getString("titleLbT1.text")); // NOI18N
        titleLbT1.setName("titleLbT1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(titleLbT1, gridBagConstraints);

        companyLbT1.setText(resourceMap.getString("companyLbT1.text")); // NOI18N
        companyLbT1.setName("companyLbT1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(companyLbT1, gridBagConstraints);

        disLbT1.setText(resourceMap.getString("disLbT1.text")); // NOI18N
        disLbT1.setName("disLbT1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(disLbT1, gridBagConstraints);

        codeLbT1.setText(resourceMap.getString("codeLbT1.text")); // NOI18N
        codeLbT1.setName("codeLbT1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(codeLbT1, gridBagConstraints);

        mobileLbT1.setText(resourceMap.getString("mobileLbT1.text")); // NOI18N
        mobileLbT1.setName("mobileLbT1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(mobileLbT1, gridBagConstraints);

        faxLbT1.setText(resourceMap.getString("faxLbT1.text")); // NOI18N
        faxLbT1.setName("faxLbT1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(faxLbT1, gridBagConstraints);

        adsLbT1.setText(resourceMap.getString("adsLbT1.text")); // NOI18N
        adsLbT1.setName("adsLbT1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(adsLbT1, gridBagConstraints);

        lastnameLbT1.setText(resourceMap.getString("lastnameLbT1.text")); // NOI18N
        lastnameLbT1.setName("lastnameLbT1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 4;
        upLeftT1.add(lastnameLbT1, gridBagConstraints);

        emailLbT1.setText(resourceMap.getString("emailLbT1.text")); // NOI18N
        emailLbT1.setName("emailLbT1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(emailLbT1, gridBagConstraints);

        webLbT1.setText(resourceMap.getString("webLbT1.text")); // NOI18N
        webLbT1.setName("webLbT1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(webLbT1, gridBagConstraints);

        subDisLbT1.setText(resourceMap.getString("subDisLbT1.text")); // NOI18N
        subDisLbT1.setName("subDisLbT1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(subDisLbT1, gridBagConstraints);

        provinceLbT1.setText(resourceMap.getString("provinceLbT1.text")); // NOI18N
        provinceLbT1.setName("provinceLbT1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(provinceLbT1, gridBagConstraints);

        phoneLbT1.setText(resourceMap.getString("phoneLbT1.text")); // NOI18N
        phoneLbT1.setName("phoneLbT1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(phoneLbT1, gridBagConstraints);

        noteLbT1.setText(resourceMap.getString("noteLbT1.text")); // NOI18N
        noteLbT1.setName("noteLbT1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(noteLbT1, gridBagConstraints);

        countryLbT1.setText(resourceMap.getString("countryLbT1.text")); // NOI18N
        countryLbT1.setName("countryLbT1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(countryLbT1, gridBagConstraints);

        nameTfT1.setColumns(10);
        nameTfT1.setText(resourceMap.getString("nameTfT1.text")); // NOI18N
        nameTfT1.setName("nameTfT1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(nameTfT1, gridBagConstraints);

        titleTfT1.setColumns(10);
        titleTfT1.setText(resourceMap.getString("titleTfT1.text")); // NOI18N
        titleTfT1.setName("titleTfT1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(titleTfT1, gridBagConstraints);

        companyTfT1.setColumns(10);
        companyTfT1.setText(resourceMap.getString("companyTfT1.text")); // NOI18N
        companyTfT1.setName("companyTfT1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(companyTfT1, gridBagConstraints);

        disTfT1.setColumns(10);
        disTfT1.setText(resourceMap.getString("disTfT1.text")); // NOI18N
        disTfT1.setName("disTfT1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(disTfT1, gridBagConstraints);

        codeTfT1.setColumns(10);
        codeTfT1.setText(resourceMap.getString("codeTfT1.text")); // NOI18N
        codeTfT1.setName("codeTfT1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(codeTfT1, gridBagConstraints);

        mobileTfT1.setColumns(10);
        mobileTfT1.setText(resourceMap.getString("mobileTfT1.text")); // NOI18N
        mobileTfT1.setName("mobileTfT1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(mobileTfT1, gridBagConstraints);

        faxTfT1.setColumns(10);
        faxTfT1.setText(resourceMap.getString("faxTfT1.text")); // NOI18N
        faxTfT1.setName("faxTfT1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(faxTfT1, gridBagConstraints);

        lastnameTfT1.setColumns(10);
        lastnameTfT1.setName("lastnameTfT1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(lastnameTfT1, gridBagConstraints);

        emailTfT1.setColumns(10);
        emailTfT1.setName("emailTfT1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(emailTfT1, gridBagConstraints);

        webTfT1.setColumns(10);
        webTfT1.setName("webTfT1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(webTfT1, gridBagConstraints);

        subDisTfT1.setColumns(10);
        subDisTfT1.setName("subDisTfT1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(subDisTfT1, gridBagConstraints);

        provinceTfT1.setColumns(10);
        provinceTfT1.setName("provinceTfT1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(provinceTfT1, gridBagConstraints);

        phoneTfT1.setColumns(10);
        phoneTfT1.setName("phoneTfT1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(phoneTfT1, gridBagConstraints);

        countryTfT1.setColumns(10);
        countryTfT1.setName("countryTfT1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(countryTfT1, gridBagConstraints);

        adsPanelT1.setName("adsPanelT1"); // NOI18N
        adsPanelT1.setPreferredSize(new java.awt.Dimension(99, 70));

        adsScrT1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        adsScrT1.setHorizontalScrollBar(null);
        adsScrT1.setName("adsScrT1"); // NOI18N

        adsTaT1.setColumns(30);
        adsTaT1.setFont(resourceMap.getFont("adsTaT1.font")); // NOI18N
        adsTaT1.setRows(3);
        adsTaT1.setAutoscrolls(false);
        adsTaT1.setName("adsTaT1"); // NOI18N
        adsScrT1.setViewportView(adsTaT1);

        javax.swing.GroupLayout adsPanelT1Layout = new javax.swing.GroupLayout(adsPanelT1);
        adsPanelT1.setLayout(adsPanelT1Layout);
        adsPanelT1Layout.setHorizontalGroup(
            adsPanelT1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(adsScrT1, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
        );
        adsPanelT1Layout.setVerticalGroup(
            adsPanelT1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(adsPanelT1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(adsScrT1, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridheight = 2;
        upLeftT1.add(adsPanelT1, gridBagConstraints);

        notePanelT1.setName("notePanelT1"); // NOI18N
        notePanelT1.setPreferredSize(new java.awt.Dimension(100, 70));

        noteScrT1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        noteScrT1.setHorizontalScrollBar(null);
        noteScrT1.setName("noteScrT1"); // NOI18N

        noteTaT1.setColumns(30);
        noteTaT1.setFont(resourceMap.getFont("noteTaT1.font")); // NOI18N
        noteTaT1.setRows(3);
        noteTaT1.setAutoscrolls(false);
        noteTaT1.setName("noteTaT1"); // NOI18N
        noteScrT1.setViewportView(noteTaT1);

        javax.swing.GroupLayout notePanelT1Layout = new javax.swing.GroupLayout(notePanelT1);
        notePanelT1.setLayout(notePanelT1Layout);
        notePanelT1Layout.setHorizontalGroup(
            notePanelT1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(notePanelT1Layout.createSequentialGroup()
                .addComponent(noteScrT1, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        notePanelT1Layout.setVerticalGroup(
            notePanelT1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(notePanelT1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(noteScrT1, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridheight = 2;
        upLeftT1.add(notePanelT1, gridBagConstraints);

        blankPanel0.setName("blankPanel0"); // NOI18N

        javax.swing.GroupLayout blankPanel0Layout = new javax.swing.GroupLayout(blankPanel0);
        blankPanel0.setLayout(blankPanel0Layout);
        blankPanel0Layout.setHorizontalGroup(
            blankPanel0Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        blankPanel0Layout.setVerticalGroup(
            blankPanel0Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        upLeftT1.add(blankPanel0, gridBagConstraints);

        blankPanel1.setMaximumSize(new java.awt.Dimension(10, 10));
        blankPanel1.setName("blankPanel1"); // NOI18N

        javax.swing.GroupLayout blankPanel1Layout = new javax.swing.GroupLayout(blankPanel1);
        blankPanel1.setLayout(blankPanel1Layout);
        blankPanel1Layout.setHorizontalGroup(
            blankPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        blankPanel1Layout.setVerticalGroup(
            blankPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        upLeftT1.add(blankPanel1, gridBagConstraints);

        nameLbTE1.setText(resourceMap.getString("nameLbTE1.text")); // NOI18N
        nameLbTE1.setName("nameLbTE1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT1.add(nameLbTE1, gridBagConstraints);

        nameTfTE1.setColumns(10);
        nameTfTE1.setName("nameTfTE1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(nameTfTE1, gridBagConstraints);

        blankPanel2.setName("blankPanel2"); // NOI18N

        javax.swing.GroupLayout blankPanel2Layout = new javax.swing.GroupLayout(blankPanel2);
        blankPanel2.setLayout(blankPanel2Layout);
        blankPanel2Layout.setHorizontalGroup(
            blankPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        blankPanel2Layout.setVerticalGroup(
            blankPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 0;
        upLeftT1.add(blankPanel2, gridBagConstraints);

        lastnameLbTE1.setText(resourceMap.getString("lastnameLbTE1.text")); // NOI18N
        lastnameLbTE1.setName("lastnameLbTE1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 4;
        upLeftT1.add(lastnameLbTE1, gridBagConstraints);

        lastnameTfTE1.setColumns(10);
        lastnameTfTE1.setName("lastnameTfTE1"); // NOI18N
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
        titleTfTE1.setName("titleTfTE1"); // NOI18N
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
        companyTfTE1.setName("companyTfTE1"); // NOI18N
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
        disTfTE1.setName("disTfTE1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(disTfTE1, gridBagConstraints);

        codeTfTE1.setColumns(10);
        codeTfTE1.setName("codeTfTE1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(codeTfTE1, gridBagConstraints);

        mobileTfTE1.setColumns(10);
        mobileTfTE1.setName("mobileTfTE1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(mobileTfTE1, gridBagConstraints);

        faxTfTE1.setColumns(10);
        faxTfTE1.setName("faxTfTE1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(faxTfTE1, gridBagConstraints);

        adsPanelTE1.setName("adsPanelTE1"); // NOI18N
        adsPanelTE1.setPreferredSize(new java.awt.Dimension(99, 70));

        adsScrTE1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        adsScrTE1.setHorizontalScrollBar(null);
        adsScrTE1.setName("adsScrTE1"); // NOI18N

        adsTaTE1.setColumns(30);
        adsTaTE1.setFont(resourceMap.getFont("adsTaTE1.font")); // NOI18N
        adsTaTE1.setRows(3);
        adsTaTE1.setAutoscrolls(false);
        adsTaTE1.setName("adsTaTE1"); // NOI18N
        adsScrTE1.setViewportView(adsTaTE1);

        javax.swing.GroupLayout adsPanelTE1Layout = new javax.swing.GroupLayout(adsPanelTE1);
        adsPanelTE1.setLayout(adsPanelTE1Layout);
        adsPanelTE1Layout.setHorizontalGroup(
            adsPanelTE1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(adsScrTE1, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
        );
        adsPanelTE1Layout.setVerticalGroup(
            adsPanelTE1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(adsPanelTE1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(adsScrTE1, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE))
        );

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
        subDisTfTE1.setName("subDisTfTE1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(subDisTfTE1, gridBagConstraints);

        provinceTfTE1.setColumns(10);
        provinceTfTE1.setName("provinceTfTE1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(provinceTfTE1, gridBagConstraints);

        phoneTfTE1.setColumns(10);
        phoneTfTE1.setName("phoneTfTE1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(phoneTfTE1, gridBagConstraints);

        countryTfTE1.setColumns(10);
        countryTfTE1.setName("countryTfTE1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT1.add(countryTfTE1, gridBagConstraints);

        notePanelTE1.setName("notePanelTE1"); // NOI18N
        notePanelTE1.setPreferredSize(new java.awt.Dimension(100, 70));

        noteScrTE1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        noteScrTE1.setHorizontalScrollBar(null);
        noteScrTE1.setName("noteScrTE1"); // NOI18N

        noteTaTE1.setColumns(30);
        noteTaTE1.setFont(resourceMap.getFont("noteTaTE1.font")); // NOI18N
        noteTaTE1.setRows(3);
        noteTaTE1.setAutoscrolls(false);
        noteTaTE1.setName("noteTaTE1"); // NOI18N
        noteScrTE1.setViewportView(noteTaTE1);

        javax.swing.GroupLayout notePanelTE1Layout = new javax.swing.GroupLayout(notePanelTE1);
        notePanelTE1.setLayout(notePanelTE1Layout);
        notePanelTE1Layout.setHorizontalGroup(
            notePanelTE1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(notePanelTE1Layout.createSequentialGroup()
                .addComponent(noteScrTE1, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        notePanelTE1Layout.setVerticalGroup(
            notePanelTE1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(notePanelTE1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(noteScrTE1, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridheight = 2;
        upLeftT1.add(notePanelTE1, gridBagConstraints);

        upLeftScrollPaneT1.setViewportView(upLeftT1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        scannerTab.add(upLeftScrollPaneT1, gridBagConstraints);

        blankPanel3.setName("blankPanel3"); // NOI18N

        javax.swing.GroupLayout blankPanel3Layout = new javax.swing.GroupLayout(blankPanel3);
        blankPanel3.setLayout(blankPanel3Layout);
        blankPanel3Layout.setHorizontalGroup(
            blankPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        blankPanel3Layout.setVerticalGroup(
            blankPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        scannerTab.add(blankPanel3, gridBagConstraints);

        upRightT1.setMinimumSize(new java.awt.Dimension(250, 250));
        upRightT1.setName("upRightT1"); // NOI18N
        upRightT1.setPreferredSize(new java.awt.Dimension(250, 250));

        scannerPanel.setName("scannerPanel"); // NOI18N

        scannerLbT1.setText(resourceMap.getString("scannerLbT1.text")); // NOI18N
        scannerLbT1.setName("scannerLbT1"); // NOI18N

        scannerBtnT1.setText(resourceMap.getString("scannerBtnT1.text")); // NOI18N
        scannerBtnT1.setMaximumSize(new java.awt.Dimension(90, 23));
        scannerBtnT1.setName("scannerBtnT1"); // NOI18N
        scannerBtnT1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scannerBtnT1ActionPerformed(evt);
            }
        });

        scannerTxtT1.setText(resourceMap.getString("scannerTxtT1.text")); // NOI18N
        scannerTxtT1.setName("scannerTxtT1"); // NOI18N

        javax.swing.GroupLayout scannerPanelLayout = new javax.swing.GroupLayout(scannerPanel);
        scannerPanel.setLayout(scannerPanelLayout);
        scannerPanelLayout.setHorizontalGroup(
            scannerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scannerPanelLayout.createSequentialGroup()
                .addComponent(scannerLbT1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scannerTxtT1, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scannerBtnT1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        scannerPanelLayout.setVerticalGroup(
            scannerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scannerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(scannerLbT1)
                .addComponent(scannerBtnT1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(scannerTxtT1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        brightPanelT1.setMinimumSize(new java.awt.Dimension(242, 40));
        brightPanelT1.setName("brightPanelT1"); // NOI18N
        brightPanelT1.setPreferredSize(new java.awt.Dimension(242, 40));
        brightPanelT1.setRequestFocusEnabled(false);
        brightPanelT1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        brightLbT1.setText(resourceMap.getString("brightLbT1.text")); // NOI18N
        brightLbT1.setName("brightLbT1"); // NOI18N
        brightPanelT1.add(brightLbT1);

        brightSldT1.setMajorTickSpacing(1);
        brightSldT1.setMaximum(5);
        brightSldT1.setMinimum(1);
        brightSldT1.setMinorTickSpacing(1);
        brightSldT1.setPaintLabels(true);
        brightSldT1.setSnapToTicks(true);
        brightSldT1.setValue(3);
        brightSldT1.setMaximumSize(new java.awt.Dimension(32767, 35));
        brightSldT1.setMinimumSize(new java.awt.Dimension(50, 35));
        brightSldT1.setName("brightSldT1"); // NOI18N
        brightSldT1.setPreferredSize(new java.awt.Dimension(150, 35));
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
        brightPanelT1.add(brightSldT1);

        doubleSideBtnT1.setText(resourceMap.getString("doubleSideBtnT1.text")); // NOI18N
        doubleSideBtnT1.setName("doubleSideBtnT1"); // NOI18N
        doubleSideBtnT1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doubleSideBtnT1ActionPerformed(evt);
            }
        });

        blackWhiteBtn.setText(resourceMap.getString("blackWhiteBtn.text")); // NOI18N
        blackWhiteBtn.setName("blackWhiteBtn"); // NOI18N
        blackWhiteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                blackWhiteBtnActionPerformed(evt);
            }
        });

        cropBtnT1.setText(resourceMap.getString("cropBtnT1.text")); // NOI18N
        cropBtnT1.setName("cropBtnT1"); // NOI18N
        cropBtnT1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cropBtnT1ActionPerformed(evt);
            }
        });

        rotateBtnT1.setText(resourceMap.getString("rotateBtnT1.text")); // NOI18N
        rotateBtnT1.setMaximumSize(new java.awt.Dimension(110, 23));
        rotateBtnT1.setMinimumSize(new java.awt.Dimension(110, 23));
        rotateBtnT1.setName("rotateBtnT1"); // NOI18N
        rotateBtnT1.setPreferredSize(new java.awt.Dimension(110, 23));
        rotateBtnT1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rotateBtnT1ActionPerformed(evt);
            }
        });

        emailBtnT1.setText(resourceMap.getString("emailBtnT1.text")); // NOI18N
        emailBtnT1.setName("emailBtnT1"); // NOI18N

        saveToDbBtnT1.setText(resourceMap.getString("saveToDbBtnT1.text")); // NOI18N
        saveToDbBtnT1.setName("saveToDbBtnT1"); // NOI18N
        saveToDbBtnT1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveToDbBtnT1ActionPerformed(evt);
            }
        });

        scanBtn.setText(resourceMap.getString("scanBtn.text")); // NOI18N
        scanBtn.setName("scanBtn"); // NOI18N
        scanBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scanBtnActionPerformed(evt);
            }
        });

        undoBtnT1.setText(resourceMap.getString("undoBtn.text")); // NOI18N
        undoBtnT1.setName("undoBtn"); // NOI18N
        undoBtnT1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoBtnT1ActionPerformed(evt);
            }
        });

        confirmBtnT1.setText(resourceMap.getString("confirmBtn.text")); // NOI18N
        confirmBtnT1.setName("confirmBtn"); // NOI18N
        confirmBtnT1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirmBtnT1ActionPerformed(evt);
            }
        });

        readCardBtnT1.setText(resourceMap.getString("readCardBtnT1.text")); // NOI18N
        readCardBtnT1.setName("readCardBtnT1"); // NOI18N
        readCardBtnT1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                readCardBtnT1ActionPerformed(evt);
            }
        });

        sideLbT1.setText(resourceMap.getString("sideLbT1.text")); // NOI18N
        sideLbT1.setName("sideLbT1"); // NOI18N

        frontSideRdT1.setText(resourceMap.getString("frontSideRdT1.text")); // NOI18N
        frontSideRdT1.setName("frontSideRdT1"); // NOI18N
        frontSideRdT1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                frontSideRdT1ActionPerformed(evt);
            }
        });

        backSideRdT1.setText(resourceMap.getString("backSideRdT1.text")); // NOI18N
        backSideRdT1.setName("backSideRdT1"); // NOI18N
        backSideRdT1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backSideRdT1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout upRightT1Layout = new javax.swing.GroupLayout(upRightT1);
        upRightT1.setLayout(upRightT1Layout);
        upRightT1Layout.setHorizontalGroup(
            upRightT1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, upRightT1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scannerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(upRightT1Layout.createSequentialGroup()
                .addGroup(upRightT1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(upRightT1Layout.createSequentialGroup()
                        .addComponent(rotateBtnT1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(emailBtnT1, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE))
                    .addGroup(upRightT1Layout.createSequentialGroup()
                        .addComponent(undoBtnT1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(confirmBtnT1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(upRightT1Layout.createSequentialGroup()
                        .addComponent(doubleSideBtnT1, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(blackWhiteBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                        .addGap(1, 1, 1))
                    .addGroup(upRightT1Layout.createSequentialGroup()
                        .addComponent(cropBtnT1, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(readCardBtnT1, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)))
                .addGap(18, 18, 18))
            .addGroup(upRightT1Layout.createSequentialGroup()
                .addComponent(brightPanelT1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(upRightT1Layout.createSequentialGroup()
                .addComponent(scanBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(saveToDbBtnT1)
                .addGap(20, 20, 20))
            .addGroup(upRightT1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sideLbT1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(frontSideRdT1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(backSideRdT1)
                .addContainerGap(77, Short.MAX_VALUE))
        );
        upRightT1Layout.setVerticalGroup(
            upRightT1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(upRightT1Layout.createSequentialGroup()
                .addComponent(scannerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(upRightT1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(doubleSideBtnT1)
                    .addComponent(blackWhiteBtn))
                .addGap(2, 2, 2)
                .addGroup(upRightT1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cropBtnT1)
                    .addComponent(readCardBtnT1))
                .addGap(7, 7, 7)
                .addGroup(upRightT1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rotateBtnT1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(emailBtnT1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(upRightT1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(undoBtnT1)
                    .addComponent(confirmBtnT1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(brightPanelT1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(upRightT1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(scanBtn)
                    .addComponent(saveToDbBtnT1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 6, Short.MAX_VALUE)
                .addGroup(upRightT1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sideLbT1)
                    .addComponent(frontSideRdT1)
                    .addComponent(backSideRdT1)))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        scannerTab.add(upRightT1, gridBagConstraints);

        low.setMaximumSize(null);
        low.setName("low"); // NOI18N
        low.setPreferredSize(new java.awt.Dimension(200, 300));
        low.setLayout(new java.awt.GridLayout(1, 2));

        lowLeftT1.setMaximumSize(null);
        lowLeftT1.setName("lowLeftT1"); // NOI18N

        frontPanelT1.setMaximumSize(null);
        frontPanelT1.setMinimumSize(new java.awt.Dimension(100, 50));
        frontPanelT1.setName("frontPanelT1"); // NOI18N
        frontPanelT1.setPreferredSize(new java.awt.Dimension(300, 50));

        frontTfT1.setText(resourceMap.getString("frontTfT1.text")); // NOI18N
        frontTfT1.setName("frontTfT1"); // NOI18N

        frontBtnT1.setText(resourceMap.getString("frontBtnT1.text")); // NOI18N
        frontBtnT1.setName("frontBtnT1"); // NOI18N
        frontBtnT1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                frontBtnT1ActionPerformed(evt);
            }
        });

        frontSpT1.setName("frontSpT1"); // NOI18N
        frontSpT1.setPreferredSize(new java.awt.Dimension(100, 260));

        javax.swing.GroupLayout frontPanelT1Layout = new javax.swing.GroupLayout(frontPanelT1);
        frontPanelT1.setLayout(frontPanelT1Layout);
        frontPanelT1Layout.setHorizontalGroup(
            frontPanelT1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(frontPanelT1Layout.createSequentialGroup()
                .addGroup(frontPanelT1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(frontPanelT1Layout.createSequentialGroup()
                        .addGap(81, 81, 81)
                        .addComponent(frontTfT1, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(frontBtnT1))
                    .addComponent(frontSpT1, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE))
                .addContainerGap())
        );
        frontPanelT1Layout.setVerticalGroup(
            frontPanelT1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(frontPanelT1Layout.createSequentialGroup()
                .addGroup(frontPanelT1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(frontTfT1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(frontBtnT1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(frontSpT1, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout lowLeftT1Layout = new javax.swing.GroupLayout(lowLeftT1);
        lowLeftT1.setLayout(lowLeftT1Layout);
        lowLeftT1Layout.setHorizontalGroup(
            lowLeftT1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(frontPanelT1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        lowLeftT1Layout.setVerticalGroup(
            lowLeftT1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(frontPanelT1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        low.add(lowLeftT1);

        lowRightT1.setMaximumSize(null);
        lowRightT1.setName("lowRightT1"); // NOI18N

        backTfT1.setText(resourceMap.getString("backTfT1.text")); // NOI18N
        backTfT1.setName("backTfT1"); // NOI18N

        backBtnT1.setText(resourceMap.getString("backBtnT1.text")); // NOI18N
        backBtnT1.setName("backBtnT1"); // NOI18N
        backBtnT1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backBtnT1ActionPerformed(evt);
            }
        });

        backSpT1.setName("backSpT1"); // NOI18N
        backSpT1.setPreferredSize(new java.awt.Dimension(100, 260));

        javax.swing.GroupLayout lowRightT1Layout = new javax.swing.GroupLayout(lowRightT1);
        lowRightT1.setLayout(lowRightT1Layout);
        lowRightT1Layout.setHorizontalGroup(
            lowRightT1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lowRightT1Layout.createSequentialGroup()
                .addGroup(lowRightT1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(lowRightT1Layout.createSequentialGroup()
                        .addGap(81, 81, 81)
                        .addComponent(backTfT1, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(backBtnT1))
                    .addGroup(lowRightT1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(backSpT1, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        lowRightT1Layout.setVerticalGroup(
            lowRightT1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lowRightT1Layout.createSequentialGroup()
                .addGroup(lowRightT1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(backTfT1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(backBtnT1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(backSpT1, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        low.add(lowRightT1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        scannerTab.add(low, gridBagConstraints);

        menuTab.addTab(resourceMap.getString("scannerTab.TabConstraints.tabTitle"), scannerTab); // NOI18N

        queryTab.setName("queryTab"); // NOI18N
        queryTab.setLayout(new java.awt.GridBagLayout());

        upLeftScrollPaneT2.setMinimumSize(new java.awt.Dimension(350, 260));
        upLeftScrollPaneT2.setName("upLeftScrollPaneT2"); // NOI18N
        upLeftScrollPaneT2.setPreferredSize(new java.awt.Dimension(350, 260));

        upLeftT2.setName("upLeftT2"); // NOI18N
        upLeftT2.setPreferredSize(new java.awt.Dimension(670, 250));
        upLeftT2.setLayout(new java.awt.GridBagLayout());

        nameLbT2.setText(resourceMap.getString("nameLbT2.text")); // NOI18N
        nameLbT2.setName("nameLbT2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(nameLbT2, gridBagConstraints);

        titleLbT2.setText(resourceMap.getString("titleLbT2.text")); // NOI18N
        titleLbT2.setName("titleLbT2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(titleLbT2, gridBagConstraints);

        companyLbT2.setText(resourceMap.getString("companyLbT2.text")); // NOI18N
        companyLbT2.setName("companyLbT2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(companyLbT2, gridBagConstraints);

        disLbT2.setText(resourceMap.getString("disLbT2.text")); // NOI18N
        disLbT2.setName("disLbT2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(disLbT2, gridBagConstraints);

        codeLbT2.setText(resourceMap.getString("codeLbT2.text")); // NOI18N
        codeLbT2.setName("codeLbT2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(codeLbT2, gridBagConstraints);

        mobileLbT2.setText(resourceMap.getString("mobileLbT2.text")); // NOI18N
        mobileLbT2.setName("mobileLbT2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(mobileLbT2, gridBagConstraints);

        faxLbT2.setText(resourceMap.getString("faxLbT2.text")); // NOI18N
        faxLbT2.setName("faxLbT2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(faxLbT2, gridBagConstraints);

        adsLbT2.setText(resourceMap.getString("adsLbT2.text")); // NOI18N
        adsLbT2.setName("adsLbT2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(adsLbT2, gridBagConstraints);

        lastnameLbT2.setText(resourceMap.getString("lastnameLbT2.text")); // NOI18N
        lastnameLbT2.setName("lastnameLbT2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 4;
        upLeftT2.add(lastnameLbT2, gridBagConstraints);

        emailLbT2.setText(resourceMap.getString("emailLbT2.text")); // NOI18N
        emailLbT2.setName("emailLbT2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(emailLbT2, gridBagConstraints);

        webLbT2.setText(resourceMap.getString("webLbT2.text")); // NOI18N
        webLbT2.setName("webLbT2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(webLbT2, gridBagConstraints);

        subDisLbT2.setText(resourceMap.getString("subDisLbT2.text")); // NOI18N
        subDisLbT2.setName("subDisLbT2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(subDisLbT2, gridBagConstraints);

        provinceLbT2.setText(resourceMap.getString("provinceLbT2.text")); // NOI18N
        provinceLbT2.setName("provinceLbT2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(provinceLbT2, gridBagConstraints);

        phoneLbT2.setText(resourceMap.getString("phoneLbT2.text")); // NOI18N
        phoneLbT2.setName("phoneLbT2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(phoneLbT2, gridBagConstraints);

        noteLbT2.setText(resourceMap.getString("noteLbT2.text")); // NOI18N
        noteLbT2.setName("noteLbT2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(noteLbT2, gridBagConstraints);

        countryLbT2.setText(resourceMap.getString("countryLbT2.text")); // NOI18N
        countryLbT2.setName("countryLbT2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(countryLbT2, gridBagConstraints);

        nameTfT2.setColumns(10);
        nameTfT2.setName("nameTfT2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(nameTfT2, gridBagConstraints);

        titleTfT2.setColumns(10);
        titleTfT2.setName("titleTfT2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(titleTfT2, gridBagConstraints);

        companyTfT2.setColumns(10);
        companyTfT2.setName("companyTfT2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(companyTfT2, gridBagConstraints);

        disTfT2.setColumns(10);
        disTfT2.setName("disTfT2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(disTfT2, gridBagConstraints);

        codeTfT2.setColumns(10);
        codeTfT2.setName("codeTfT2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(codeTfT2, gridBagConstraints);

        mobileTfT2.setColumns(10);
        mobileTfT2.setName("mobileTfT2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(mobileTfT2, gridBagConstraints);

        faxTfT2.setColumns(10);
        faxTfT2.setName("faxTfT2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(faxTfT2, gridBagConstraints);

        lastnameTfT2.setColumns(10);
        lastnameTfT2.setName("lastnameTfT2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(lastnameTfT2, gridBagConstraints);

        emailTfT2.setColumns(10);
        emailTfT2.setName("emailTfT2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(emailTfT2, gridBagConstraints);

        webTfT2.setColumns(10);
        webTfT2.setName("webTfT2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(webTfT2, gridBagConstraints);

        subDisTfT2.setColumns(10);
        subDisTfT2.setName("subDisTfT2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(subDisTfT2, gridBagConstraints);

        provinceTfT2.setColumns(10);
        provinceTfT2.setName("provinceTfT2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(provinceTfT2, gridBagConstraints);

        phoneTfT2.setColumns(10);
        phoneTfT2.setName("phoneTfT2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(phoneTfT2, gridBagConstraints);

        countryTfT2.setColumns(10);
        countryTfT2.setName("countryTfT2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(countryTfT2, gridBagConstraints);

        adsPanelT2.setName("adsPanelT2"); // NOI18N
        adsPanelT2.setPreferredSize(new java.awt.Dimension(99, 70));

        adsScrT2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        adsScrT2.setHorizontalScrollBar(null);
        adsScrT2.setName("adsScrT2"); // NOI18N

        adsTaT2.setColumns(30);
        adsTaT2.setFont(resourceMap.getFont("adsTaT2.font")); // NOI18N
        adsTaT2.setRows(3);
        adsTaT2.setAutoscrolls(false);
        adsTaT2.setName("adsTaT2"); // NOI18N
        adsScrT2.setViewportView(adsTaT2);

        javax.swing.GroupLayout adsPanelT2Layout = new javax.swing.GroupLayout(adsPanelT2);
        adsPanelT2.setLayout(adsPanelT2Layout);
        adsPanelT2Layout.setHorizontalGroup(
            adsPanelT2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(adsScrT2, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
        );
        adsPanelT2Layout.setVerticalGroup(
            adsPanelT2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(adsPanelT2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(adsScrT2, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridheight = 2;
        upLeftT2.add(adsPanelT2, gridBagConstraints);

        notePanelT2.setName("notePanelT2"); // NOI18N
        notePanelT2.setPreferredSize(new java.awt.Dimension(100, 70));

        noteScrT2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        noteScrT2.setHorizontalScrollBar(null);
        noteScrT2.setName("noteScrT2"); // NOI18N

        noteTaT2.setColumns(30);
        noteTaT2.setFont(resourceMap.getFont("noteTaT2.font")); // NOI18N
        noteTaT2.setRows(3);
        noteTaT2.setAutoscrolls(false);
        noteTaT2.setName("noteTaT2"); // NOI18N
        noteScrT2.setViewportView(noteTaT2);

        javax.swing.GroupLayout notePanelT2Layout = new javax.swing.GroupLayout(notePanelT2);
        notePanelT2.setLayout(notePanelT2Layout);
        notePanelT2Layout.setHorizontalGroup(
            notePanelT2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(notePanelT2Layout.createSequentialGroup()
                .addComponent(noteScrT2, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        notePanelT2Layout.setVerticalGroup(
            notePanelT2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(notePanelT2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(noteScrT2, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridheight = 2;
        upLeftT2.add(notePanelT2, gridBagConstraints);

        blankPanel4.setName("blankPanel4"); // NOI18N

        javax.swing.GroupLayout blankPanel4Layout = new javax.swing.GroupLayout(blankPanel4);
        blankPanel4.setLayout(blankPanel4Layout);
        blankPanel4Layout.setHorizontalGroup(
            blankPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        blankPanel4Layout.setVerticalGroup(
            blankPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        upLeftT2.add(blankPanel4, gridBagConstraints);

        blankPanel11.setName("blankPanel11"); // NOI18N

        javax.swing.GroupLayout blankPanel11Layout = new javax.swing.GroupLayout(blankPanel11);
        blankPanel11.setLayout(blankPanel11Layout);
        blankPanel11Layout.setHorizontalGroup(
            blankPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        blankPanel11Layout.setVerticalGroup(
            blankPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 8;
        upLeftT2.add(blankPanel11, gridBagConstraints);

        blankPanel12.setName("blankPanel12"); // NOI18N

        javax.swing.GroupLayout blankPanel12Layout = new javax.swing.GroupLayout(blankPanel12);
        blankPanel12.setLayout(blankPanel12Layout);
        blankPanel12Layout.setHorizontalGroup(
            blankPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        blankPanel12Layout.setVerticalGroup(
            blankPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        upLeftT2.add(blankPanel12, gridBagConstraints);

        nameLbTE2.setText(resourceMap.getString("nameLbTE2.text")); // NOI18N
        nameLbTE2.setName("nameLbTE2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT2.add(nameLbTE2, gridBagConstraints);

        nameTfTE2.setColumns(10);
        nameTfTE2.setName("nameTfTE2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(nameTfTE2, gridBagConstraints);

        blankPanel13.setName("blankPanel13"); // NOI18N

        javax.swing.GroupLayout blankPanel13Layout = new javax.swing.GroupLayout(blankPanel13);
        blankPanel13.setLayout(blankPanel13Layout);
        blankPanel13Layout.setHorizontalGroup(
            blankPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        blankPanel13Layout.setVerticalGroup(
            blankPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 0;
        upLeftT2.add(blankPanel13, gridBagConstraints);

        lastnameLbTE2.setText(resourceMap.getString("lastnameLbTE2.text")); // NOI18N
        lastnameLbTE2.setName("lastnameLbTE2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 4;
        upLeftT2.add(lastnameLbTE2, gridBagConstraints);

        lastnameTfTE2.setColumns(10);
        lastnameTfTE2.setName("lastnameTfTE2"); // NOI18N
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
        titleTfTE2.setName("titleTfTE2"); // NOI18N
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
        companyTfTE2.setName("companyTfTE2"); // NOI18N
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
        disTfTE2.setName("disTfTE2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(disTfTE2, gridBagConstraints);

        codeTfTE2.setColumns(10);
        codeTfTE2.setName("codeTfTE2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(codeTfTE2, gridBagConstraints);

        mobileTfTE2.setColumns(10);
        mobileTfTE2.setName("mobileTfTE2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(mobileTfTE2, gridBagConstraints);

        faxTfTE2.setColumns(10);
        faxTfTE2.setName("faxTfTE2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(faxTfTE2, gridBagConstraints);

        adsPanelTE2.setName("adsPanelTE2"); // NOI18N
        adsPanelTE2.setPreferredSize(new java.awt.Dimension(99, 70));

        adsScrTE2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        adsScrTE2.setHorizontalScrollBar(null);
        adsScrTE2.setName("adsScrTE2"); // NOI18N

        adsTaTE2.setColumns(30);
        adsTaTE2.setFont(resourceMap.getFont("adsTaTE2.font")); // NOI18N
        adsTaTE2.setRows(3);
        adsTaTE2.setAutoscrolls(false);
        adsTaTE2.setName("adsTaTE2"); // NOI18N
        adsScrTE2.setViewportView(adsTaTE2);

        javax.swing.GroupLayout adsPanelTE2Layout = new javax.swing.GroupLayout(adsPanelTE2);
        adsPanelTE2.setLayout(adsPanelTE2Layout);
        adsPanelTE2Layout.setHorizontalGroup(
            adsPanelTE2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(adsScrTE2, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
        );
        adsPanelTE2Layout.setVerticalGroup(
            adsPanelTE2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(adsPanelTE2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(adsScrTE2, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE))
        );

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
        subDisTfTE2.setName("subDisTfTE2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(subDisTfTE2, gridBagConstraints);

        provinceTfTE2.setColumns(10);
        provinceTfTE2.setName("provinceTfTE2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(provinceTfTE2, gridBagConstraints);

        phoneTfTE2.setColumns(10);
        phoneTfTE2.setName("phoneTfTE2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(phoneTfTE2, gridBagConstraints);

        countryTfTE2.setColumns(10);
        countryTfTE2.setName("countryTfTE2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT2.add(countryTfTE2, gridBagConstraints);

        notePanelTE2.setName("notePanelTE2"); // NOI18N
        notePanelTE2.setPreferredSize(new java.awt.Dimension(100, 70));

        noteScrTE2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        noteScrTE2.setHorizontalScrollBar(null);
        noteScrTE2.setName("noteScrTE2"); // NOI18N

        noteTaTE2.setColumns(30);
        noteTaTE2.setFont(resourceMap.getFont("noteTaTE2.font")); // NOI18N
        noteTaTE2.setRows(3);
        noteTaTE2.setAutoscrolls(false);
        noteTaTE2.setName("noteTaTE2"); // NOI18N
        noteScrTE2.setViewportView(noteTaTE2);

        javax.swing.GroupLayout notePanelTE2Layout = new javax.swing.GroupLayout(notePanelTE2);
        notePanelTE2.setLayout(notePanelTE2Layout);
        notePanelTE2Layout.setHorizontalGroup(
            notePanelTE2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(notePanelTE2Layout.createSequentialGroup()
                .addComponent(noteScrTE2, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        notePanelTE2Layout.setVerticalGroup(
            notePanelTE2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(notePanelTE2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(noteScrTE2, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridheight = 2;
        upLeftT2.add(notePanelTE2, gridBagConstraints);

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
            .addGap(0, 10, Short.MAX_VALUE)
        );
        blankPanel14Layout.setVerticalGroup(
            blankPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        queryTab.add(blankPanel14, gridBagConstraints);

        upRightT2.setMaximumSize(new java.awt.Dimension(350, 250));
        upRightT2.setMinimumSize(new java.awt.Dimension(310, 250));
        upRightT2.setName("upRightT2"); // NOI18N
        upRightT2.setPreferredSize(new java.awt.Dimension(310, 250));
        upRightT2.setLayout(new java.awt.GridBagLayout());

        genSearchPanelT2.setName("genSearchT2"); // NOI18N
        genSearchPanelT2.setPreferredSize(new java.awt.Dimension(310, 35));

        genSearchLbT2.setText(resourceMap.getString("genSearchLbT2.text")); // NOI18N
        genSearchLbT2.setName("genSearchLbT2"); // NOI18N

        genSearchTfT2.setText(resourceMap.getString("genSearchTfT2.text")); // NOI18N
        genSearchTfT2.setName("genSearchTfT2"); // NOI18N

        genSearchT2.setText(resourceMap.getString("genSearchT2.text")); // NOI18N
        genSearchT2.setName("genSearchT2"); // NOI18N

        javax.swing.GroupLayout genSearchPanelT2Layout = new javax.swing.GroupLayout(genSearchPanelT2);
        genSearchPanelT2.setLayout(genSearchPanelT2Layout);
        genSearchPanelT2Layout.setHorizontalGroup(
            genSearchPanelT2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(genSearchPanelT2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(genSearchLbT2)
                .addGap(16, 16, 16)
                .addComponent(genSearchTfT2, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(genSearchT2)
                .addGap(59, 59, 59))
        );
        genSearchPanelT2Layout.setVerticalGroup(
            genSearchPanelT2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(genSearchPanelT2Layout.createSequentialGroup()
                .addGroup(genSearchPanelT2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(genSearchTfT2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(genSearchT2)
                    .addComponent(genSearchLbT2))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        upRightT2.add(genSearchPanelT2, gridBagConstraints);

        quickPanelT2.setMinimumSize(new java.awt.Dimension(330, 200));
        quickPanelT2.setName("quickPanelT2"); // NOI18N
        quickPanelT2.setPreferredSize(new java.awt.Dimension(310, 200));
        quickPanelT2.setLayout(new java.awt.GridBagLayout());

        quickLb.setText(resourceMap.getString("quickLb.text")); // NOI18N
        quickLb.setName("quickLb"); // NOI18N
        quickLb.setPreferredSize(new java.awt.Dimension(150, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        quickPanelT2.add(quickLb, gridBagConstraints);

        btnPanel0T2.setMaximumSize(new java.awt.Dimension(280, 47));
        btnPanel0T2.setMinimumSize(new java.awt.Dimension(280, 47));
        btnPanel0T2.setName("btnPanel0T2"); // NOI18N
        btnPanel0T2.setPreferredSize(new java.awt.Dimension(280, 47));
        btnPanel0T2.setLayout(new java.awt.GridBagLayout());

        engBtn0T2.setActionCommand(resourceMap.getString("engBtn0T2.actionCommand")); // NOI18N
        engBtn0T2.setLabel(resourceMap.getString("engBtn0T2.label")); // NOI18N
        engBtn0T2.setName("engBtn0T2"); // NOI18N
        engBtn0T2.setPreferredSize(new java.awt.Dimension(53, 23));
        btnPanel0T2.add(engBtn0T2, new java.awt.GridBagConstraints());

        engBtn1T2.setText(resourceMap.getString("engBtn1T2.text")); // NOI18N
        engBtn1T2.setName("engBtn1T2"); // NOI18N
        engBtn1T2.setPreferredSize(new java.awt.Dimension(53, 23));
        btnPanel0T2.add(engBtn1T2, new java.awt.GridBagConstraints());

        engBtn2T2.setText(resourceMap.getString("engBtn2T2.text")); // NOI18N
        engBtn2T2.setName("engBtn2T2"); // NOI18N
        engBtn2T2.setPreferredSize(new java.awt.Dimension(53, 23));
        btnPanel0T2.add(engBtn2T2, new java.awt.GridBagConstraints());

        thBtn0T2.setText(resourceMap.getString("thBtn0T2.text")); // NOI18N
        thBtn0T2.setName("thBtn0T2"); // NOI18N
        thBtn0T2.setPreferredSize(new java.awt.Dimension(53, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        btnPanel0T2.add(thBtn0T2, gridBagConstraints);

        thBtn1T2.setText(resourceMap.getString("thBtn1T2.text")); // NOI18N
        thBtn1T2.setName("thBtn1T2"); // NOI18N
        thBtn1T2.setPreferredSize(new java.awt.Dimension(53, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        btnPanel0T2.add(thBtn1T2, gridBagConstraints);

        thBtn2T2.setText(resourceMap.getString("thBtn2T2.text")); // NOI18N
        thBtn2T2.setName("thBtn2T2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        btnPanel0T2.add(thBtn2T2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        quickPanelT2.add(btnPanel0T2, gridBagConstraints);

        btnPanel1T2.setMaximumSize(new java.awt.Dimension(280, 47));
        btnPanel1T2.setMinimumSize(new java.awt.Dimension(280, 47));
        btnPanel1T2.setName("btnPanel1T2"); // NOI18N
        btnPanel1T2.setPreferredSize(new java.awt.Dimension(280, 47));
        btnPanel1T2.setLayout(new java.awt.GridBagLayout());

        engBtn3T2.setText(resourceMap.getString("engBtn3T2.text")); // NOI18N
        engBtn3T2.setName("engBtn3T2"); // NOI18N
        engBtn3T2.setPreferredSize(new java.awt.Dimension(53, 23));
        btnPanel1T2.add(engBtn3T2, new java.awt.GridBagConstraints());

        thBtn3T2.setText(resourceMap.getString("thBtn3T2.text")); // NOI18N
        thBtn3T2.setName("thBtn3T2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        btnPanel1T2.add(thBtn3T2, gridBagConstraints);

        engBtn4T2.setText(resourceMap.getString("engBtn4T2.text")); // NOI18N
        engBtn4T2.setName("engBtn4T2"); // NOI18N
        btnPanel1T2.add(engBtn4T2, new java.awt.GridBagConstraints());

        thBtn4T2.setText(resourceMap.getString("thBtn4T2.text")); // NOI18N
        thBtn4T2.setName("thBtn4T2"); // NOI18N
        thBtn4T2.setPreferredSize(new java.awt.Dimension(53, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        btnPanel1T2.add(thBtn4T2, gridBagConstraints);

        engBtn5T2.setText(resourceMap.getString("engBtn5T2.text")); // NOI18N
        engBtn5T2.setName("engBtn5T2"); // NOI18N
        engBtn5T2.setPreferredSize(new java.awt.Dimension(53, 23));
        btnPanel1T2.add(engBtn5T2, new java.awt.GridBagConstraints());

        thBtn5T2.setText(resourceMap.getString("thBtn5T2.text")); // NOI18N
        thBtn5T2.setName("thBtn5T2"); // NOI18N
        thBtn5T2.setPreferredSize(new java.awt.Dimension(53, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        btnPanel1T2.add(thBtn5T2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        quickPanelT2.add(btnPanel1T2, gridBagConstraints);

        btnPanel2T2.setMaximumSize(new java.awt.Dimension(280, 47));
        btnPanel2T2.setMinimumSize(new java.awt.Dimension(280, 47));
        btnPanel2T2.setName("btnPanel2T2"); // NOI18N
        btnPanel2T2.setPreferredSize(new java.awt.Dimension(280, 47));
        btnPanel2T2.setLayout(new java.awt.GridBagLayout());

        engBtn6T2.setText(resourceMap.getString("engBtn6T2.text")); // NOI18N
        engBtn6T2.setMaximumSize(new java.awt.Dimension(30, 23));
        engBtn6T2.setMinimumSize(new java.awt.Dimension(30, 23));
        engBtn6T2.setName("engBtn6T2"); // NOI18N
        engBtn6T2.setPreferredSize(new java.awt.Dimension(53, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        btnPanel2T2.add(engBtn6T2, gridBagConstraints);

        engBtn7T2.setText(resourceMap.getString("engBtn7T2.text")); // NOI18N
        engBtn7T2.setName("engBtn7T2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        btnPanel2T2.add(engBtn7T2, gridBagConstraints);

        engBtn8T2.setText(resourceMap.getString("engBtn8T2.text")); // NOI18N
        engBtn8T2.setName("engBtn8T2"); // NOI18N
        engBtn8T2.setPreferredSize(new java.awt.Dimension(53, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        btnPanel2T2.add(engBtn8T2, gridBagConstraints);

        thBtn8T2.setText(resourceMap.getString("thBtn8T2.text")); // NOI18N
        thBtn8T2.setName("thBtn8T2"); // NOI18N
        thBtn8T2.setPreferredSize(new java.awt.Dimension(53, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        btnPanel2T2.add(thBtn8T2, gridBagConstraints);

        thBtn7T2.setText(resourceMap.getString("thBtn7T2.text")); // NOI18N
        thBtn7T2.setName("thBtn7T2"); // NOI18N
        thBtn7T2.setPreferredSize(new java.awt.Dimension(53, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        btnPanel2T2.add(thBtn7T2, gridBagConstraints);

        thBtn6T2.setText(resourceMap.getString("thBtn6T2.text")); // NOI18N
        thBtn6T2.setName("thBtn6T2"); // NOI18N
        thBtn6T2.setPreferredSize(new java.awt.Dimension(53, 23));
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

        importTableT2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "selected", "Name", "Lastname", "Title", "E-mail", "Company", "Mobile", "Phone", "Country"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        importTableT2.setName("importTableT2"); // NOI18N
        jScrollPane4.setViewportView(importTableT2);

        javax.swing.GroupLayout tablePanelT2Layout = new javax.swing.GroupLayout(tablePanelT2);
        tablePanelT2.setLayout(tablePanelT2Layout);
        tablePanelT2Layout.setHorizontalGroup(
            tablePanelT2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tablePanelT2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
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

        deleteEditPanelT2.setName("deleteEditPanelT2"); // NOI18N
        deleteEditPanelT2.setPreferredSize(new java.awt.Dimension(250, 35));

        deletedBtnT2.setText(resourceMap.getString("deletedBtnT2.text")); // NOI18N
        deletedBtnT2.setName("deletedBtnT2"); // NOI18N
        deletedBtnT2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deletedBtnT2ActionPerformed(evt);
            }
        });

        editBtnT2.setText(resourceMap.getString("editBtnT2.text")); // NOI18N
        editBtnT2.setName("editBtnT2"); // NOI18N
        editBtnT2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editBtnT2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout deleteEditPanelT2Layout = new javax.swing.GroupLayout(deleteEditPanelT2);
        deleteEditPanelT2.setLayout(deleteEditPanelT2Layout);
        deleteEditPanelT2Layout.setHorizontalGroup(
            deleteEditPanelT2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(deleteEditPanelT2Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(deletedBtnT2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 71, Short.MAX_VALUE)
                .addComponent(editBtnT2)
                .addGap(33, 33, 33))
        );
        deleteEditPanelT2Layout.setVerticalGroup(
            deleteEditPanelT2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(deleteEditPanelT2Layout.createSequentialGroup()
                .addGroup(deleteEditPanelT2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(deletedBtnT2)
                    .addComponent(editBtnT2))
                .addContainerGap(12, Short.MAX_VALUE))
        );

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
            .addGap(0, 10, Short.MAX_VALUE)
        );
        blankPanel6Layout.setVerticalGroup(
            blankPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        queryTab.add(blankPanel6, gridBagConstraints);

        menuTab.addTab(resourceMap.getString("queryTab.TabConstraints.tabTitle"), queryTab); // NOI18N

        resultTab.setMaximumSize(new java.awt.Dimension(650, 595));
        resultTab.setMinimumSize(new java.awt.Dimension(650, 595));
        resultTab.setName("resultTab"); // NOI18N
        resultTab.setPreferredSize(new java.awt.Dimension(650, 595));
        resultTab.setLayout(new java.awt.GridBagLayout());

        upRightT3.setMaximumSize(new java.awt.Dimension(230, 250));
        upRightT3.setMinimumSize(new java.awt.Dimension(230, 250));
        upRightT3.setName("upRightT3"); // NOI18N
        upRightT3.setPreferredSize(new java.awt.Dimension(230, 250));

        brightPanelT3.setMinimumSize(new java.awt.Dimension(242, 40));
        brightPanelT3.setName("brightPanelT3"); // NOI18N
        brightPanelT3.setPreferredSize(new java.awt.Dimension(242, 40));
        brightPanelT3.setRequestFocusEnabled(false);
        brightPanelT3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

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
        brightSldT3.setPreferredSize(new java.awt.Dimension(150, 35));
        brightSldT3.setLabelTable( labelTable );
        brightSldT3.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                brightSldT3StateChanged(evt);
            }
        });
        brightPanelT3.add(brightSldT3);

        blackWhiteBtnT3.setText(resourceMap.getString("blackWhiteBtnT3.text")); // NOI18N
        blackWhiteBtnT3.setName("blackWhiteBtnT3"); // NOI18N
        blackWhiteBtnT3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                blackWhiteBtnT3ActionPerformed(evt);
            }
        });

        cropBtnT3.setText(resourceMap.getString("cropBtnT3.text")); // NOI18N
        cropBtnT3.setName("cropBtnT3"); // NOI18N
        cropBtnT3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cropBtnT3ActionPerformed(evt);
            }
        });

        rotateBtnT3.setText(resourceMap.getString("rotateBtnT3.text")); // NOI18N
        rotateBtnT3.setMaximumSize(new java.awt.Dimension(110, 23));
        rotateBtnT3.setMinimumSize(new java.awt.Dimension(110, 23));
        rotateBtnT3.setName("rotateBtnT3"); // NOI18N
        rotateBtnT3.setPreferredSize(new java.awt.Dimension(110, 23));
        rotateBtnT3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rotateBtnT3ActionPerformed(evt);
            }
        });

        emailBtnT3.setText(resourceMap.getString("emailBtnT3.text")); // NOI18N
        emailBtnT3.setName("emailBtnT3"); // NOI18N

        idPanelT3.setMaximumSize(new java.awt.Dimension(150, 22));
        idPanelT3.setMinimumSize(new java.awt.Dimension(150, 22));
        idPanelT3.setName("idPanelT3"); // NOI18N

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

        sideLbT3.setText(resourceMap.getString("sideLbT3.text")); // NOI18N
        sideLbT3.setName("sideLbT3"); // NOI18N

        frontSideRdT3.setText(resourceMap.getString("frontSideRdT3.text")); // NOI18N
        frontSideRdT3.setName("frontSideRdT3"); // NOI18N
        frontSideRdT3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                frontSideRdT3ActionPerformed(evt);
            }
        });

        backSideRdT3.setText(resourceMap.getString("backSideRdT3.text")); // NOI18N
        backSideRdT3.setName("backSideRdT3"); // NOI18N
        backSideRdT3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backSideRdT3ActionPerformed(evt);
            }
        });

        undoBtnT3.setText(resourceMap.getString("undoBtnT3.text")); // NOI18N
        undoBtnT3.setName("undoBtnT3"); // NOI18N
        undoBtnT3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoBtnT3ActionPerformed(evt);
            }
        });

        confirmBtnT3.setText(resourceMap.getString("confirmBtnT3.text")); // NOI18N
        confirmBtnT3.setName("confirmBtnT3"); // NOI18N
        confirmBtnT3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirmBtnT3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout upRightT3Layout = new javax.swing.GroupLayout(upRightT3);
        upRightT3.setLayout(upRightT3Layout);
        upRightT3Layout.setHorizontalGroup(
            upRightT3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(upRightT3Layout.createSequentialGroup()
                .addGroup(upRightT3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(upRightT3Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(sideLbT3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(frontSideRdT3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(backSideRdT3))
                    .addGroup(upRightT3Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(upRightT3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(brightPanelT3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(idPanelT3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(upRightT3Layout.createSequentialGroup()
                                .addComponent(cropBtnT3, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(21, 21, 21)
                                .addComponent(blackWhiteBtnT3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(upRightT3Layout.createSequentialGroup()
                                .addGroup(upRightT3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(rotateBtnT3, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(undoBtnT3, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(20, 20, 20)
                                .addGroup(upRightT3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(emailBtnT3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(confirmBtnT3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        upRightT3Layout.setVerticalGroup(
            upRightT3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(upRightT3Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(idPanelT3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addGroup(upRightT3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cropBtnT3)
                    .addComponent(blackWhiteBtnT3))
                .addGap(7, 7, 7)
                .addGroup(upRightT3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rotateBtnT3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(emailBtnT3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(upRightT3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(undoBtnT3)
                    .addComponent(confirmBtnT3))
                .addGap(18, 18, 18)
                .addComponent(brightPanelT3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addGroup(upRightT3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sideLbT3)
                    .addComponent(frontSideRdT3)
                    .addComponent(backSideRdT3))
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        resultTab.add(upRightT3, gridBagConstraints);

        lowT3.setName("lowT3"); // NOI18N
        lowT3.setPreferredSize(new java.awt.Dimension(650, 300));
        lowT3.setLayout(new java.awt.GridBagLayout());

        lowLeftT3.setName("lowLeftT3"); // NOI18N

        frontTfT3.setName("frontTfT3"); // NOI18N

        frontBtnT3.setText(resourceMap.getString("frontBtnT3.text")); // NOI18N
        frontBtnT3.setName("frontBtnT3"); // NOI18N
        frontBtnT3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                frontBtnT3ActionPerformed(evt);
            }
        });

        frontSpT3.setName("frontSpT3"); // NOI18N
        frontSpT3.setPreferredSize(new java.awt.Dimension(100, 260));

        javax.swing.GroupLayout lowLeftT3Layout = new javax.swing.GroupLayout(lowLeftT3);
        lowLeftT3.setLayout(lowLeftT3Layout);
        lowLeftT3Layout.setHorizontalGroup(
            lowLeftT3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lowLeftT3Layout.createSequentialGroup()
                .addGroup(lowLeftT3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(lowLeftT3Layout.createSequentialGroup()
                        .addGap(81, 81, 81)
                        .addComponent(frontTfT3, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(frontBtnT3))
                    .addGroup(lowLeftT3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(frontSpT3, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        lowLeftT3Layout.setVerticalGroup(
            lowLeftT3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lowLeftT3Layout.createSequentialGroup()
                .addGroup(lowLeftT3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(frontTfT3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(frontBtnT3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(frontSpT3, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        lowT3.add(lowLeftT3, gridBagConstraints);

        lowRightT3.setName("lowRightT3"); // NOI18N

        backTfT3.setName("backTfT3"); // NOI18N

        backBtnT3.setText(resourceMap.getString("backBtnT3.text")); // NOI18N
        backBtnT3.setName("backBtnT3"); // NOI18N
        backBtnT3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backBtnT3ActionPerformed(evt);
            }
        });

        backSpT3.setName("backSpT3"); // NOI18N
        backSpT3.setPreferredSize(new java.awt.Dimension(100, 260));

        javax.swing.GroupLayout lowRightT3Layout = new javax.swing.GroupLayout(lowRightT3);
        lowRightT3.setLayout(lowRightT3Layout);
        lowRightT3Layout.setHorizontalGroup(
            lowRightT3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lowRightT3Layout.createSequentialGroup()
                .addGroup(lowRightT3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(lowRightT3Layout.createSequentialGroup()
                        .addGap(81, 81, 81)
                        .addComponent(backTfT3, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(backBtnT3))
                    .addGroup(lowRightT3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(backSpT3, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        lowRightT3Layout.setVerticalGroup(
            lowRightT3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lowRightT3Layout.createSequentialGroup()
                .addGroup(lowRightT3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(backTfT3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(backBtnT3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(backSpT3, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
        upLeftScrollPaneT3.setPreferredSize(new java.awt.Dimension(350, 260));

        upLeftT3.setName("upLeftT3"); // NOI18N
        upLeftT3.setPreferredSize(new java.awt.Dimension(670, 250));
        upLeftT3.setLayout(new java.awt.GridBagLayout());

        nameLbT3.setText(resourceMap.getString("nameLbT3.text")); // NOI18N
        nameLbT3.setName("nameLbT3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(nameLbT3, gridBagConstraints);

        titleLbT3.setText(resourceMap.getString("titleLbT3.text")); // NOI18N
        titleLbT3.setName("titleLbT3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(titleLbT3, gridBagConstraints);

        companyLbT3.setText(resourceMap.getString("companyLbT3.text")); // NOI18N
        companyLbT3.setName("companyLbT3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(companyLbT3, gridBagConstraints);

        disLbT3.setText(resourceMap.getString("disLbT3.text")); // NOI18N
        disLbT3.setName("disLbT3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(disLbT3, gridBagConstraints);

        codeLbT3.setText(resourceMap.getString("codeLbT3.text")); // NOI18N
        codeLbT3.setName("codeLbT3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(codeLbT3, gridBagConstraints);

        mobileLbT3.setText(resourceMap.getString("mobileLbT3.text")); // NOI18N
        mobileLbT3.setName("mobileLbT3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(mobileLbT3, gridBagConstraints);

        faxLbT3.setText(resourceMap.getString("faxLbT3.text")); // NOI18N
        faxLbT3.setName("faxLbT3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(faxLbT3, gridBagConstraints);

        adsLbT3.setText(resourceMap.getString("adsLbT3.text")); // NOI18N
        adsLbT3.setName("adsLbT3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(adsLbT3, gridBagConstraints);

        lastnameLbT3.setText(resourceMap.getString("lastnameLbT3.text")); // NOI18N
        lastnameLbT3.setName("lastnameLbT3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 4;
        upLeftT3.add(lastnameLbT3, gridBagConstraints);

        emailLbT3.setText(resourceMap.getString("emailLbT3.text")); // NOI18N
        emailLbT3.setName("emailLbT3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(emailLbT3, gridBagConstraints);

        webLbT3.setText(resourceMap.getString("webLbT3.text")); // NOI18N
        webLbT3.setName("webLbT3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(webLbT3, gridBagConstraints);

        subDisLbT3.setText(resourceMap.getString("subDisLbT3.text")); // NOI18N
        subDisLbT3.setName("subDisLbT3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(subDisLbT3, gridBagConstraints);

        provinceLbT3.setText(resourceMap.getString("provinceLbT3.text")); // NOI18N
        provinceLbT3.setName("provinceLbT3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(provinceLbT3, gridBagConstraints);

        phoneLbT3.setText(resourceMap.getString("phoneLbT3.text")); // NOI18N
        phoneLbT3.setName("phoneLbT3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(phoneLbT3, gridBagConstraints);

        noteLbT3.setText(resourceMap.getString("noteLbT3.text")); // NOI18N
        noteLbT3.setName("noteLbT3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(noteLbT3, gridBagConstraints);

        countryLbT3.setText(resourceMap.getString("countryLbT3.text")); // NOI18N
        countryLbT3.setName("countryLbT3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(countryLbT3, gridBagConstraints);

        nameTfT3.setColumns(10);
        nameTfT3.setName("nameTfT3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(nameTfT3, gridBagConstraints);

        titleTfT3.setColumns(10);
        titleTfT3.setName("titleTfT3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(titleTfT3, gridBagConstraints);

        companyTfT3.setColumns(10);
        companyTfT3.setName("companyTfT3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(companyTfT3, gridBagConstraints);

        disTfT3.setColumns(10);
        disTfT3.setName("disTfT3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(disTfT3, gridBagConstraints);

        codeTfT3.setColumns(10);
        codeTfT3.setName("codeTfT3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(codeTfT3, gridBagConstraints);

        mobileTfT3.setColumns(10);
        mobileTfT3.setName("mobileTfT3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(mobileTfT3, gridBagConstraints);

        faxTfT3.setColumns(10);
        faxTfT3.setName("faxTfT3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(faxTfT3, gridBagConstraints);

        lastnameTfT3.setColumns(10);
        lastnameTfT3.setName("lastnameTfT3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(lastnameTfT3, gridBagConstraints);

        emailTfT3.setColumns(10);
        emailTfT3.setName("emailTfT3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(emailTfT3, gridBagConstraints);

        webTfT3.setColumns(10);
        webTfT3.setName("webTfT3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(webTfT3, gridBagConstraints);

        subDisTfT3.setColumns(10);
        subDisTfT3.setName("subDisTfT3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(subDisTfT3, gridBagConstraints);

        provinceTfT3.setColumns(10);
        provinceTfT3.setName("provinceTfT3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(provinceTfT3, gridBagConstraints);

        phoneTfT3.setColumns(10);
        phoneTfT3.setName("phoneTfT3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(phoneTfT3, gridBagConstraints);

        countryTfT3.setColumns(10);
        countryTfT3.setName("countryTfT3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(countryTfT3, gridBagConstraints);

        adsPanelT3.setName("adsPanelT3"); // NOI18N
        adsPanelT3.setPreferredSize(new java.awt.Dimension(99, 70));

        adsScrT3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        adsScrT3.setHorizontalScrollBar(null);
        adsScrT3.setName("adsScrT3"); // NOI18N

        adsTaT3.setColumns(30);
        adsTaT3.setFont(resourceMap.getFont("adsTaT3.font")); // NOI18N
        adsTaT3.setRows(3);
        adsTaT3.setAutoscrolls(false);
        adsTaT3.setName("adsTaT3"); // NOI18N
        adsScrT3.setViewportView(adsTaT3);

        javax.swing.GroupLayout adsPanelT3Layout = new javax.swing.GroupLayout(adsPanelT3);
        adsPanelT3.setLayout(adsPanelT3Layout);
        adsPanelT3Layout.setHorizontalGroup(
            adsPanelT3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(adsScrT3, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
        );
        adsPanelT3Layout.setVerticalGroup(
            adsPanelT3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(adsPanelT3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(adsScrT3, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridheight = 2;
        upLeftT3.add(adsPanelT3, gridBagConstraints);

        notePanelT3.setName("notePanelT3"); // NOI18N
        notePanelT3.setPreferredSize(new java.awt.Dimension(100, 70));

        noteScrT3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        noteScrT3.setHorizontalScrollBar(null);
        noteScrT3.setName("noteScrT3"); // NOI18N

        noteTaT3.setColumns(30);
        noteTaT3.setFont(resourceMap.getFont("noteTaT3.font")); // NOI18N
        noteTaT3.setRows(3);
        noteTaT3.setAutoscrolls(false);
        noteTaT3.setName("noteTaT3"); // NOI18N
        noteScrT3.setViewportView(noteTaT3);

        javax.swing.GroupLayout notePanelT3Layout = new javax.swing.GroupLayout(notePanelT3);
        notePanelT3.setLayout(notePanelT3Layout);
        notePanelT3Layout.setHorizontalGroup(
            notePanelT3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(notePanelT3Layout.createSequentialGroup()
                .addComponent(noteScrT3, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        notePanelT3Layout.setVerticalGroup(
            notePanelT3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(notePanelT3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(noteScrT3, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridheight = 2;
        upLeftT3.add(notePanelT3, gridBagConstraints);

        blankPanel5.setName("blankPanel5"); // NOI18N

        javax.swing.GroupLayout blankPanel5Layout = new javax.swing.GroupLayout(blankPanel5);
        blankPanel5.setLayout(blankPanel5Layout);
        blankPanel5Layout.setHorizontalGroup(
            blankPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        blankPanel5Layout.setVerticalGroup(
            blankPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        upLeftT3.add(blankPanel5, gridBagConstraints);

        blankPanel15.setName("blankPanel15"); // NOI18N

        javax.swing.GroupLayout blankPanel15Layout = new javax.swing.GroupLayout(blankPanel15);
        blankPanel15.setLayout(blankPanel15Layout);
        blankPanel15Layout.setHorizontalGroup(
            blankPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        blankPanel15Layout.setVerticalGroup(
            blankPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 8;
        upLeftT3.add(blankPanel15, gridBagConstraints);

        blankPanel16.setName("blankPanel16"); // NOI18N

        javax.swing.GroupLayout blankPanel16Layout = new javax.swing.GroupLayout(blankPanel16);
        blankPanel16.setLayout(blankPanel16Layout);
        blankPanel16Layout.setHorizontalGroup(
            blankPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        blankPanel16Layout.setVerticalGroup(
            blankPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        upLeftT3.add(blankPanel16, gridBagConstraints);

        nameLbTE3.setText(resourceMap.getString("nameLbTE3.text")); // NOI18N
        nameLbTE3.setName("nameLbTE3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        upLeftT3.add(nameLbTE3, gridBagConstraints);

        nameTfTE3.setColumns(10);
        nameTfTE3.setName("nameTfTE3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(nameTfTE3, gridBagConstraints);

        blankPanel17.setName("blankPanel17"); // NOI18N

        javax.swing.GroupLayout blankPanel17Layout = new javax.swing.GroupLayout(blankPanel17);
        blankPanel17.setLayout(blankPanel17Layout);
        blankPanel17Layout.setHorizontalGroup(
            blankPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        blankPanel17Layout.setVerticalGroup(
            blankPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 0;
        upLeftT3.add(blankPanel17, gridBagConstraints);

        lastnameLbTE3.setText(resourceMap.getString("lastnameLbTE3.text")); // NOI18N
        lastnameLbTE3.setName("lastnameLbTE3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 4;
        upLeftT3.add(lastnameLbTE3, gridBagConstraints);

        lastnameTfTE3.setColumns(10);
        lastnameTfTE3.setName("lastnameTfTE3"); // NOI18N
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
        titleTfTE3.setName("titleTfTE3"); // NOI18N
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
        companyTfTE3.setName("companyTfTE3"); // NOI18N
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
        disTfTE3.setName("disTfTE3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(disTfTE3, gridBagConstraints);

        codeTfTE3.setColumns(10);
        codeTfTE3.setName("codeTfTE3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(codeTfTE3, gridBagConstraints);

        mobileTfTE3.setColumns(10);
        mobileTfTE3.setName("mobileTfTE3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(mobileTfTE3, gridBagConstraints);

        faxTfTE3.setColumns(10);
        faxTfTE3.setName("faxTfTE3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(faxTfTE3, gridBagConstraints);

        adsPanelTE3.setName("adsPanelTE3"); // NOI18N
        adsPanelTE3.setPreferredSize(new java.awt.Dimension(99, 70));

        adsScrTE3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        adsScrTE3.setHorizontalScrollBar(null);
        adsScrTE3.setName("adsScrTE3"); // NOI18N

        adsTaTE3.setColumns(30);
        adsTaTE3.setFont(resourceMap.getFont("adsTaTE3.font")); // NOI18N
        adsTaTE3.setRows(3);
        adsTaTE3.setAutoscrolls(false);
        adsTaTE3.setName("adsTaTE3"); // NOI18N
        adsScrTE3.setViewportView(adsTaTE3);

        javax.swing.GroupLayout adsPanelTE3Layout = new javax.swing.GroupLayout(adsPanelTE3);
        adsPanelTE3.setLayout(adsPanelTE3Layout);
        adsPanelTE3Layout.setHorizontalGroup(
            adsPanelTE3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(adsScrTE3, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
        );
        adsPanelTE3Layout.setVerticalGroup(
            adsPanelTE3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(adsPanelTE3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(adsScrTE3, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE))
        );

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
        subDisTfTE3.setName("subDisTfTE3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(subDisTfTE3, gridBagConstraints);

        provinceTfTE3.setColumns(10);
        provinceTfTE3.setName("provinceTfTE3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(provinceTfTE3, gridBagConstraints);

        phoneTfTE3.setColumns(10);
        phoneTfTE3.setName("phoneTfTE3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(phoneTfTE3, gridBagConstraints);

        countryTfTE3.setColumns(10);
        countryTfTE3.setName("countryTfTE3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        upLeftT3.add(countryTfTE3, gridBagConstraints);

        notePanelTE3.setName("notePanelTE3"); // NOI18N
        notePanelTE3.setPreferredSize(new java.awt.Dimension(100, 70));

        noteScrTE3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        noteScrTE3.setHorizontalScrollBar(null);
        noteScrTE3.setName("noteScrTE3"); // NOI18N

        noteTaTE3.setColumns(30);
        noteTaTE3.setFont(resourceMap.getFont("noteTaTE3.font")); // NOI18N
        noteTaTE3.setRows(3);
        noteTaTE3.setAutoscrolls(false);
        noteTaTE3.setName("noteTaTE3"); // NOI18N
        noteScrTE3.setViewportView(noteTaTE3);

        javax.swing.GroupLayout notePanelTE3Layout = new javax.swing.GroupLayout(notePanelTE3);
        notePanelTE3.setLayout(notePanelTE3Layout);
        notePanelTE3Layout.setHorizontalGroup(
            notePanelTE3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(notePanelTE3Layout.createSequentialGroup()
                .addComponent(noteScrTE3, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        notePanelTE3Layout.setVerticalGroup(
            notePanelTE3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(notePanelTE3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(noteScrTE3, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridheight = 2;
        upLeftT3.add(notePanelTE3, gridBagConstraints);

        upLeftScrollPaneT3.setViewportView(upLeftT3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        resultTab.add(upLeftScrollPaneT3, gridBagConstraints);

        menuTab.addTab(resourceMap.getString("resultTab.TabConstraints.tabTitle"), resultTab); // NOI18N

        importExportTab.setName("importExportTab"); // NOI18N
        importExportTab.setLayout(new java.awt.GridBagLayout());

        importPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("importPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), resourceMap.getColor("importPanel.border.titleColor"))); // NOI18N
        importPanel.setMinimumSize(new java.awt.Dimension(600, 120));
        importPanel.setName("importPanel"); // NOI18N
        importPanel.setPreferredSize(new java.awt.Dimension(600, 120));
        importPanel.setLayout(new java.awt.GridBagLayout());

        importPanelT4.setMinimumSize(new java.awt.Dimension(280, 40));
        importPanelT4.setName("importPanelT4"); // NOI18N
        importPanelT4.setPreferredSize(new java.awt.Dimension(280, 40));

        importLbT4.setText(resourceMap.getString("importLbT4.text")); // NOI18N
        importLbT4.setName("importLbT4"); // NOI18N

        importTfT4.setText(resourceMap.getString("importTfT4.text")); // NOI18N
        importTfT4.setName("importTfT4"); // NOI18N

        importBrowseBtnT4.setText(resourceMap.getString("importBrowseBtnT4.text")); // NOI18N
        importBrowseBtnT4.setName("importBrowseBtnT4"); // NOI18N

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
                .addContainerGap(17, Short.MAX_VALUE))
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

        replaceBtnT4.setText(resourceMap.getString("replaceBtnT4.text")); // NOI18N
        replaceBtnT4.setName("replaceBtnT4"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(37, Short.MAX_VALUE)
                .addComponent(replaceBtnT4)
                .addGap(32, 32, 32))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(replaceBtnT4)
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        importPanel.add(jPanel1, gridBagConstraints);

        jPanel2.setMinimumSize(new java.awt.Dimension(140, 40));
        jPanel2.setName("jPanel2"); // NOI18N

        addToLocalBtnT4.setText(resourceMap.getString("addToLocalBtnT4.text")); // NOI18N
        addToLocalBtnT4.setName("addToLocalBtnT4"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(17, Short.MAX_VALUE)
                .addComponent(addToLocalBtnT4)
                .addGap(32, 32, 32))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(addToLocalBtnT4)
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        importPanel.add(jPanel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        importExportTab.add(importPanel, gridBagConstraints);

        exportPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("exportPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), resourceMap.getColor("exportPanel.border.titleColor"))); // NOI18N
        exportPanel.setName("exportPanel"); // NOI18N
        exportPanel.setPreferredSize(new java.awt.Dimension(600, 100));

        jPanel5.setMinimumSize(new java.awt.Dimension(100, 40));
        jPanel5.setName("jPanel5"); // NOI18N
        jPanel5.setPreferredSize(new java.awt.Dimension(344, 40));

        exportLbT4.setText(resourceMap.getString("exportLbT4.text")); // NOI18N
        exportLbT4.setName("exportLbT4"); // NOI18N

        exportTfT4.setName("exportTfT4"); // NOI18N

        browseExportBtnT4.setText(resourceMap.getString("browseExportBtnT4.text")); // NOI18N
        browseExportBtnT4.setName("browseExportBtnT4"); // NOI18N
        browseExportBtnT4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseExportBtnT4ActionPerformed(evt);
            }
        });

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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, exportPanelLayout.createSequentialGroup()
                .addContainerGap(127, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(117, 117, 117))
        );
        exportPanelLayout.setVerticalGroup(
            exportPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(exportPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
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

        statusPanel.setMaximumSize(new java.awt.Dimension(700, 24));
        statusPanel.setMinimumSize(new java.awt.Dimension(700, 24));
        statusPanel.setName("statusPanel"); // NOI18N
        statusPanel.setPreferredSize(new java.awt.Dimension(700, 24));

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        databaseLb.setText(resourceMap.getString("databaseLb.text")); // NOI18N
        databaseLb.setName("databaseLb"); // NOI18N

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

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 704, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(statusMessageLabel)
                    .addGroup(statusPanelLayout.createSequentialGroup()
                        .addComponent(databaseLb)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(databaseNameLb, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 69, Short.MAX_VALUE)
                        .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(statusAnimationLabel)))
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(statusMessageLabel)
                        .addComponent(statusAnimationLabel)
                        .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(databaseNameLb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(databaseLb)))
                .addGap(1, 1, 1))
        );

        setComponent(mainPanel);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents


    private boolean isPasswordCorrect(String usr,char[] input) {
        boolean isCorrect = true;
        char[] adminPassword = { 'a', 'd', 'm', 'i', 'n' };
        char[] usrPassword = {'u','s','e','r'};

        if(usr.equals("admin")){
            if (input.length != adminPassword.length) {
                isCorrect = false;
            } else {
                isCorrect = Arrays.equals (input, adminPassword);
            }
            //Zero out the password.
            Arrays.fill(adminPassword,'0');
        }
    
        if(usr.equals("user")){
            if (input.length != usrPassword.length) {
                isCorrect = false;
            } else {
                isCorrect = Arrays.equals (input, usrPassword);
            }
            //Zero out the password.
            Arrays.fill(usrPassword,'0');
        }
        return isCorrect;
    }

    private String validatePath(String path){
        String newPath = "";
        String[] temp = path.split("\\\\");
        for(int i=0;i<temp.length;i++){
            if(i<temp.length-1) newPath += temp[i]+"/";
            else newPath+=temp[i];
        }
        return newPath;
    }

    private Hashtable<Integer,Card> updateTable(DefaultTableModel model){
        Hashtable<Integer, Card> resultMapped = new Hashtable<Integer, Card>();
        try {
            localCardList = CardLocalManager.loadLocalCard(defaultcard.getAbsolutePath());
            Object[][] tableArray = new Object[localCardList.size()][];
            int row = 0;
            for (Card card : localCardList) {
                resultMapped.put(row, card);
                tableArray[row] = new Object[]{false, card.getFirstName(), card.getLastName(), card.getPosition(), card.getEmail(), card.getCompany(), card.getMobile(), card.getTelephone(), card.getCountry()};
                row++;
            }
            model.setDataVector(tableArray, new Object[]{"selected", "Name", "Lastname", "Position", "E-mail", "Company", "Mobile", "Telephone", "Country"});
        } catch (ScannerDBException ex) {
            Logger.getLogger(ScannerView.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resultMapped;
    }

    private void saveToDbBtnT1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveToDbBtnT1ActionPerformed
        // TODO add your handling code here:
            try {
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

                Card newCard = new Card(name, lastName, title, email, company, web, ads, city, state, country, code, phone, fax, mobile, note, imgFront, imgBack, new Group(), 0);
                CardLocalManager.addLocalCard(newCard, defaultcard.getAbsolutePath());

            } catch (ScannerDBException ex) {
                Logger.getLogger(ScannerView.class.getName()).log(Level.SEVERE, null, ex);
            }
        JOptionPane.showMessageDialog(null, "Already Added","information", JOptionPane.INFORMATION_MESSAGE);        
    }//GEN-LAST:event_saveToDbBtnT1ActionPerformed

    private void frontBtnT1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_frontBtnT1ActionPerformed
        // TODO add your handling code here:
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
            } else {
            }
       }
    }//GEN-LAST:event_backBtnT1ActionPerformed

    private void deletedBtnT2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deletedBtnT2ActionPerformed
        // TODO add your handling code here:
        int response;//0 = yes, 1 = no
        response = javax.swing.JOptionPane.showConfirmDialog(null, "Do you delete?","Confirm, please",javax.swing.JOptionPane.YES_NO_OPTION);
        DefaultTableModel model = (DefaultTableModel) importTableT2.getModel();
        
        if(response ==0){            
            for(int i = 0;i<model.getRowCount();i++){
                if(((Boolean)model.getValueAt(i,0)).booleanValue()==true){
                    try {
                        CardLocalManager.deleteLocalCard(idMapped.get(i).getId(), defaultcard.getAbsolutePath());
                    } catch (ScannerDBException ex) {
                        Logger.getLogger(ScannerView.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        idMapped = updateTable(model);
    }//GEN-LAST:event_deletedBtnT2ActionPerformed

    private void editBtnT2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editBtnT2ActionPerformed
        // TODO add your handling code here:
        Card editCard = idMapped.get(importTableT2.getSelectedRow());
        menuTab.setSelectedIndex(3);

        String frontPath = validatePath(editCard.getImgFront());
        String backPath = validatePath(editCard.getImgBack());

        idNameLbT3.setText(editCard.getId()+"");
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
        frontTfT3.setText(frontPath);
        backTfT3.setText(backPath);
        
        frontLbT3.setIcon(new ImageIcon(frontPath));
        backLbT3.setIcon(new ImageIcon(backPath));

    }//GEN-LAST:event_editBtnT2ActionPerformed

    private void frontBtnT3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_frontBtnT3ActionPerformed
        // TODO add your handling code here:
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
            } else {
            }
       }
    }//GEN-LAST:event_frontBtnT3ActionPerformed

    private void browseExportBtnT4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseExportBtnT4ActionPerformed
        // TODO add your handling code here:
        String wd = System.getProperty("user.dir");
        String filename = "";
        JFileChooser fc = new JFileChooser(wd);
        fc.addChoosableFileFilter(new DBFileFilter());

        int rc = fc.showDialog(null, "Select");
        if (rc == JFileChooser.APPROVE_OPTION)
        {
            File file = fc.getSelectedFile();
            filename = file.getAbsolutePath();

            exportTfT4.setText(filename);
        // call your function here
        }
    }//GEN-LAST:event_browseExportBtnT4ActionPerformed

    private void exportBtnT4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportBtnT4ActionPerformed
        // TODO add your handling code here:
        try{
            CardLocalManager.saveLocalCard(localCardList, exportTfT4.getText());
        }catch(Exception e){}
    }//GEN-LAST:event_exportBtnT4ActionPerformed

    private void scanBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scanBtnActionPerformed
        // TODO add your handling code here:
        String path = "C:/Documents and Settings/Jenchote/Desktop/scanner/jenCard.jpg";
        frontTfT1.setText(path);
        frontLbT1.setIcon(new ImageIcon(path));

        String path1 = "C:/Documents and Settings/Jenchote/Desktop/scanner/oakCard.jpg";
        backTfT1.setText(path1);
        backLbT1.setIcon(new ImageIcon(path1));

        // Yov's part: Call scanner
        synchronized(this){

            if(isFrontSelected){
                bcScanner.setTargetFileName(scannedImageFileName);
            }else{
               bcScanner.setTargetFileName(scannedImageFileNameBack);
            }

            scannedImage = bcScanner.scan();
            if (scannedImage != null) {
                if(isFrontSelected){
                    scannedBCard = new BusinessCard(scannedImage);

                    frontLbT1.setIcon(new ImageIcon(scannedImage));
                }else{
                    scannedBCardBack = new BusinessCard(scannedImage);

                    backLbT1.setIcon(new ImageIcon(scannedImage));
                }
            }
        }
    }//GEN-LAST:event_scanBtnActionPerformed

    private void rotateBtnT1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rotateBtnT1ActionPerformed
        if(isFrontSelected){
            if (scannedBCard != null) {

                scannedBCard.rotate90();

                frontLbT1.setIcon(new ImageIcon(scannedBCard.getPreviewImage().getImageData()));
            }
        }else{
            if (scannedBCardBack != null) {

                scannedBCardBack.rotate90();

                backLbT1.setIcon(new ImageIcon(scannedBCardBack.getPreviewImage().getImageData()));
            }
        }
    }//GEN-LAST:event_rotateBtnT1ActionPerformed

    private void blackWhiteBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_blackWhiteBtnActionPerformed
        if(isFrontSelected){
            if (scannedBCard != null) {

                scannedBCard.turnToBlackAndWhite();

                frontLbT1.setIcon(new ImageIcon(scannedBCard.getPreviewImage().getImageData()));
            }
        }else{
            if (scannedBCardBack != null) {

                scannedBCardBack.turnToBlackAndWhite();

                backLbT1.setIcon(new ImageIcon(scannedBCardBack.getPreviewImage().getImageData()));
            }
        }
    }//GEN-LAST:event_blackWhiteBtnActionPerformed

    private void brightSldT1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_brightSldT1StateChanged
        if(isFrontSelected){
            if ((!brightSldT1.getValueIsAdjusting()) && (scannedBCard != null)) {

                scannedBCard.changeBrightness(brightSldT1.getValue());

                frontLbT1.setIcon(new ImageIcon(scannedBCard.getPreviewImage().getImageData()));
            }
        }else{
            if ((!brightSldT1.getValueIsAdjusting()) && (scannedBCardBack != null)) {

                scannedBCardBack.changeBrightness(brightSldT1.getValue());

                backLbT1.setIcon(new ImageIcon(scannedBCardBack.getPreviewImage().getImageData()));
            }
        }
    }//GEN-LAST:event_brightSldT1StateChanged

    private void cropBtnT1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cropBtnT1ActionPerformed
        if(isFrontSelected){
            if (scannedBCard != null) {

                scannedBCard.trim();

                frontLbT1.setIcon(new ImageIcon(scannedBCard.getPreviewImage().getImageData()));
            }
        }else{
            if (scannedBCardBack != null){

                scannedBCardBack.trim();

                backLbT1.setIcon(new ImageIcon(scannedBCardBack.getPreviewImage().getImageData()));
            }
        }
    }//GEN-LAST:event_cropBtnT1ActionPerformed

    private void scannerBtnT1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scannerBtnT1ActionPerformed
        synchronized(this){
            scannerTxtT1.setText(bcScanner.selectScanner());
        }
    }//GEN-LAST:event_scannerBtnT1ActionPerformed

    private void readCardBtnT1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readCardBtnT1ActionPerformed
        // Yov is working Here
        //System.out.println("TrainingImageFolder: " + trainingImgFolder);

        if(isFrontSelected){
            if(scannedBCard != null){
                if(scannedImageFileName.contains(".jpg")){
                    scannedBCard.setImageFileName(scannedImageFileName);
                }else{
                    scannedBCard.setImageFileName(scannedImageFileName +
                            bcScanner.getFileNameIndex() + ".jpg");
                }

                scannedBCard.initRonCemerOCR(new JTabbedPane());
                noteTaT1.setText( scannedBCard.retrieveData(trainingImgFolder) );
            }
        }else{
             if(scannedBCardBack != null){
                if(scannedImageFileName.contains(".jpg")){
                    scannedBCardBack.setImageFileName(scannedImageFileNameBack);
                }else{
                    scannedBCardBack.setImageFileName(scannedImageFileNameBack +
                            bcScanner.getFileNameIndex() + ".jpg");
                }

                scannedBCardBack.initRonCemerOCR(new JTabbedPane());
                noteTaT1.setText( scannedBCardBack.retrieveData(trainingImgFolder) );
            }
        }
    }//GEN-LAST:event_readCardBtnT1ActionPerformed

    private void frontSideRdT1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_frontSideRdT1ActionPerformed
        isFrontSelected = true;
        backSideRdT1.setSelected(false);
    }//GEN-LAST:event_frontSideRdT1ActionPerformed

    private void backSideRdT1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backSideRdT1ActionPerformed
        isFrontSelected = false;
        frontSideRdT1.setSelected(false);
    }//GEN-LAST:event_backSideRdT1ActionPerformed

    private void doubleSideBtnT1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doubleSideBtnT1ActionPerformed
        if(isFrontSelected){
            if(scannedBCard != null){
                scannedBCardBack = new BusinessCard(scannedBCard.getPreviewImage());

                backLbT1.setIcon(new ImageIcon(scannedBCardBack.getPreviewImage().getImageData()));
            }
        }else{
            if(scannedBCardBack != null){
                scannedBCard = new BusinessCard(scannedBCardBack.getPreviewImage());

                frontLbT1.setIcon(new ImageIcon(scannedBCard.getPreviewImage().getImageData()));
            }
        }
    }//GEN-LAST:event_doubleSideBtnT1ActionPerformed

    private void undoBtnT1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoBtnT1ActionPerformed
        if(isFrontSelected){
            if(scannedBCard != null){
                scannedBCard.undoChanges();

                frontLbT1.setIcon(new ImageIcon(scannedBCard.getPreviewImage().getImageData()));
            }
        }else{
            if(scannedBCardBack != null){
                scannedBCardBack.undoChanges();

                backLbT1.setIcon(new ImageIcon(scannedBCardBack.getPreviewImage().getImageData()));
            }
        }
    }//GEN-LAST:event_undoBtnT1ActionPerformed

    private void frontSideRdT3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_frontSideRdT3ActionPerformed
        isFrontSelectedResult = true;
        
        backSideRdT3.setSelected(false);
    }//GEN-LAST:event_frontSideRdT3ActionPerformed

    private void backSideRdT3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backSideRdT3ActionPerformed
        isFrontSelectedResult = false;
        
        frontSideRdT3.setSelected(false);
    }//GEN-LAST:event_backSideRdT3ActionPerformed

    private void undoBtnT3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoBtnT3ActionPerformed
        if(isFrontSelectedResult){
            if(resultBCard != null){
                resultBCard.undoChanges();

                frontLbT3.setIcon(new ImageIcon(resultBCard.getPreviewImage().getImageData()));
            }
        }else{
            if(resultBCardBack != null){
                resultBCardBack.undoChanges();

                backLbT3.setIcon(new ImageIcon(resultBCardBack.getPreviewImage().getImageData()));
            }
        }

        // Add your database-related method here
    }//GEN-LAST:event_undoBtnT3ActionPerformed

    private void confirmBtnT3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirmBtnT3ActionPerformed
        if(isFrontSelectedResult){
            if(resultBCard != null){
                resultBCard.confirmChange();

                frontLbT3.setIcon(new ImageIcon(resultBCard.getPreviewImage().getImageData()));
            }
        }else{
            if(resultBCardBack != null){
                resultBCardBack.confirmChange();

                backLbT3.setIcon(new ImageIcon(resultBCardBack.getPreviewImage().getImageData()));
            }
        }

        // Add your database-related method here
    }//GEN-LAST:event_confirmBtnT3ActionPerformed

    private void confirmBtnT1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirmBtnT1ActionPerformed
         if(isFrontSelected){
            if(scannedBCard != null){
                scannedBCard.confirmChange();

                frontLbT1.setIcon(new ImageIcon(scannedBCard.getPreviewImage().getImageData()));
            }
        }else{
            if(scannedBCard != null){
                scannedBCardBack.confirmChange();

                backLbT1.setIcon(new ImageIcon(scannedBCardBack.getPreviewImage().getImageData()));
            }
        }
    }//GEN-LAST:event_confirmBtnT1ActionPerformed

    private void rotateBtnT3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rotateBtnT3ActionPerformed
        if(isFrontSelectedResult){
            if (resultBCard != null) {

                resultBCard.rotate90();

                frontLbT3.setIcon(new ImageIcon(resultBCard.getPreviewImage().getImageData()));
            }
        }else{
            if (resultBCardBack != null) {

                resultBCardBack.rotate90();

                backLbT3.setIcon(new ImageIcon(resultBCardBack.getPreviewImage().getImageData()));
            }
        }
    }//GEN-LAST:event_rotateBtnT3ActionPerformed

    private void brightSldT3StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_brightSldT3StateChanged
        if(isFrontSelectedResult){
            if ((!brightSldT3.getValueIsAdjusting()) && (resultBCard != null)) {

                resultBCard.changeBrightness(brightSldT3.getValue());

                frontLbT3.setIcon(new ImageIcon(resultBCard.getPreviewImage().getImageData()));
            }
        }else{
            if ((!brightSldT3.getValueIsAdjusting()) && (resultBCardBack != null)) {

                resultBCardBack.changeBrightness(brightSldT3.getValue());

                backLbT3.setIcon(new ImageIcon(resultBCardBack.getPreviewImage().getImageData()));
            }
        }
    }//GEN-LAST:event_brightSldT3StateChanged

    private void blackWhiteBtnT3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_blackWhiteBtnT3ActionPerformed
        if (isFrontSelectedResult) {
            if (resultBCard != null) {

                resultBCard.turnToBlackAndWhite();

                frontLbT3.setIcon(new ImageIcon(resultBCard.getPreviewImage().getImageData()));
            }
        } else {
            if (resultBCardBack != null) {

                resultBCardBack.turnToBlackAndWhite();

                backLbT3.setIcon(new ImageIcon(resultBCardBack.getPreviewImage().getImageData()));
            }
        }
    }//GEN-LAST:event_blackWhiteBtnT3ActionPerformed

    private void cropBtnT3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cropBtnT3ActionPerformed
         if(isFrontSelectedResult){
            if (resultBCard != null) {

                resultBCard.trim();

                frontLbT3.setIcon(new ImageIcon(resultBCard.getPreviewImage().getImageData()));
            }
        }else{
            if (resultBCardBack != null){

                resultBCardBack.trim();

                backLbT3.setIcon(new ImageIcon(resultBCardBack.getPreviewImage().getImageData()));
            }
        }
    }//GEN-LAST:event_cropBtnT3ActionPerformed

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
    }//GEN-LAST:event_backBtnT3ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addToLocalBtnT4;
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
    private javax.swing.JButton backBtnT1;
    private javax.swing.JButton backBtnT3;
    private javax.swing.JRadioButton backSideRdT1;
    private javax.swing.JRadioButton backSideRdT3;
    private javax.swing.JScrollPane backSpT1;
    private javax.swing.JScrollPane backSpT3;
    private javax.swing.JTextField backTfT1;
    private javax.swing.JTextField backTfT3;
    private javax.swing.JToggleButton blackWhiteBtn;
    private javax.swing.JToggleButton blackWhiteBtnT3;
    private javax.swing.JPanel blankPanel0;
    private javax.swing.JPanel blankPanel1;
    private javax.swing.JPanel blankPanel11;
    private javax.swing.JPanel blankPanel12;
    private javax.swing.JPanel blankPanel13;
    private javax.swing.JPanel blankPanel14;
    private javax.swing.JPanel blankPanel15;
    private javax.swing.JPanel blankPanel16;
    private javax.swing.JPanel blankPanel17;
    private javax.swing.JPanel blankPanel2;
    private javax.swing.JPanel blankPanel3;
    private javax.swing.JPanel blankPanel4;
    private javax.swing.JPanel blankPanel5;
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
    private javax.swing.JToggleButton cropBtnT1;
    private javax.swing.JToggleButton cropBtnT3;
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
    private javax.swing.JToggleButton doubleSideBtnT1;
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
    private javax.swing.JPanel frontPanelT1;
    private javax.swing.JRadioButton frontSideRdT1;
    private javax.swing.JRadioButton frontSideRdT3;
    private javax.swing.JScrollPane frontSpT1;
    private javax.swing.JScrollPane frontSpT3;
    private javax.swing.JTextField frontTfT1;
    private javax.swing.JTextField frontTfT3;
    private javax.swing.JLabel genSearchLbT2;
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
    private javax.swing.JProgressBar progressBar;
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
    private javax.swing.JButton saveToDbBtnT1;
    private javax.swing.JButton scanBtn;
    private javax.swing.JButton scannerBtnT1;
    private javax.swing.JLabel scannerLbT1;
    private javax.swing.JPanel scannerPanel;
    private javax.swing.JPanel scannerTab;
    private javax.swing.JTextField scannerTxtT1;
    private javax.swing.JLabel sideLbT1;
    private javax.swing.JLabel sideLbT3;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
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
    final javax.swing.JFileChooser dbChooser = new javax.swing.JFileChooser();
    final javax.swing.JFileChooser imgChooser = new javax.swing.JFileChooser();
    private JLabel frontLbT1;
    private JLabel backLbT1;
    private JLabel frontLbT3;
    private JLabel backLbT3;
    //card local manager
    private ArrayList<Card> localCardList;
    private Hashtable<Integer,Card> idMapped;
    private File defaultcard;
    private JDialog aboutBox;

    // For Yov's part: image processing and scanner variables
    private CardScanner bcScanner;
    private BusinessCard scannedBCard, scannedBCardBack;
    private RonCemerOCR pixelBaxedOCR;
    private BufferedImage scannedImage;
    private String scannedImageFileName, scannedImageFileNameBack;
    private boolean isFrontSelected;

    private BusinessCard resultBCard, resultBCardBack;
    private BufferedImage resultImage;
    private boolean isFrontSelectedResult;

    private String trainingImgFolder;
}