package com.tssoftgroup.tmobile.screen;
/**
 *
 * HelloWorld.java
 * The sentinal sample!
 *
 * Copyright © 1998-2008 Research In Motion Ltd.
 *
 * Note: For the sake of simplicity, this sample application may not leverage
 * resource bundles and resource strings.  However, it is STRONGLY recommended
 * that application developers make use of the localization features available
 * within the BlackBerry development platform to ensure a seamless application
 * experience across a variety of languages and geographies.  For more information
 * on localizing your application, please refer to the BlackBerry Java Development
 * Environment Development Guide associated with this release.
 */

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;

import com.tssoftgroup.tmobile.component.ButtonListener;
import com.tssoftgroup.tmobile.component.MainListVerticalFieldManager;
import com.tssoftgroup.tmobile.component.MyButtonField;
import com.tssoftgroup.tmobile.component.NewVerticalFieldManager;
import com.tssoftgroup.tmobile.utils.Img;



/**
 * Create a new screen that extends MainScreen, which provides default standard
 * behavior for BlackBerry applications.
 */
/*
 * BlackBerry applications that provide a user interface must extend
 * UiApplication.
 */
/*package*/ class TrainingMain extends MainScreen
{
    NewVerticalFieldManager manager = new NewVerticalFieldManager();

    /**
     * HelloWorldScreen constructor.
     */
    TrainingMain()
    {
        super(Manager.NO_VERTICAL_SCROLL | Manager.NO_VERTICAL_SCROLLBAR);
        super.add(manager);

        // Add a field to the title region of the screen. We use a simple LabelField
        // here. The ELLIPSIS option truncates the label text with "..." if the text
        // is too long for the space available.
        //LabelField title = new LabelField("Hello World Demo" , LabelField.ELLIPSIS | LabelField.USE_ALL_WIDTH);
        //setTitle(title);

        // Add a read only text field (RichTextField) to the screen.  The RichTextField
        // is focusable by default.  In this case we provide a style to make the field
        // non-focusable.
       //add(new RichTextField("Hello World!" ,Field.NON_FOCUSABLE));
    }

    public void add(Field field){
        manager.add(field);
    }   

    /**
     * Display a dialog box to the user with "Goodbye!" when the application
     * is closed.
     *
     * @see net.rim.device.api.ui.Screen#close()
     */
    public void close()
    {
        // Display a farewell message before closing application.
        System.exit(0);

        super.close();
    }
}


class MyTrainingMain extends FixMainScreen {
    private MainItem _mainMenuItem = new MainItem();
    Img imgstock = Img.getInstance();
	public MyTrainingMain() {
		super(MODE_TRAIN);
        XYEdges edge = new XYEdges(24, 25, 8, 25);

        Bitmap img = imgstock.getHeader();
        BitmapField bf = new BitmapField(img, BitmapField.FIELD_HCENTER | BitmapField.USE_ALL_WIDTH);
        add(bf);

        try{
            edge = new XYEdges(5, 25, 5, 25);

            // EditField
            EditField edit = new EditField("Search: ", "");
            edit.setMaxSize(35);
//            edit.setBorder(BorderFactory.createSimpleBorder(edge,Border.STYLE_TRANSPARENT));
            add(edit);

            // ObjectChoiceField
            /*String choicestrs[] = {" New", " Old 1", " Very Old 1"};
            ObjectChoiceField choice = new ObjectChoiceField("", null, 0, ObjectChoiceField.FIELD_LEFT){
                private Bitmap button;

                protected void onFocus(int direction) {
                    invalidate();
                }

                protected void onUnfocus() {
                    invalidate();
                }
                public int getPreferredWidth() {
                    return 430;
                }

                public int getPreferredHeight() {
                    return 27;
                }

                protected void layout(int arg0, int arg1) {
                    setExtent(getPreferredWidth(), getPreferredHeight());
                }

                protected void drawFocus(Graphics graphics, boolean on) {
                //
                }
                
                protected void paint( Graphics graphics )
                {
                    //graphics.setBackgroundColor(Color.RED);
                    graphics.drawBitmap(0, 0, 430, 27, button, 0, 0);
                    //graphics.clear();

                    super.paint(graphics);
                }
            };
            choice.setChoices(choicestrs);
            choice.setBorder(BorderFactory.createSimpleBorder(edge,Border.STYLE_TRANSPARENT));
            add(choice);*/

            bf.setMargin(edge);
            edge = new XYEdges(2, 25, 2, 25);
//            bf.setBorder(BorderFactory.createSimpleBorder(edge,Border.STYLE_TRANSPARENT));
            add(bf);

            MainListVerticalFieldManager listVerticalManager = new MainListVerticalFieldManager();

            edge = new XYEdges(2, 25, 2, 25);
            // RadioButtonField (must be part of group)
            RadioButtonGroup rgrp = new RadioButtonGroup();
            //for(int i=0; i<2; i++){
                RadioButtonField radio = new RadioButtonField("Training 1", rgrp, true);
//                radio.setBorder(BorderFactory.createSimpleBorder(edge,Border.STYLE_TRANSPARENT));
                listVerticalManager.add(radio);
                radio = new RadioButtonField("Training 2", rgrp, false);
//                radio.setBorder(BorderFactory.createSimpleBorder(edge,Border.STYLE_TRANSPARENT));
                listVerticalManager.add(radio);
                radio = new RadioButtonField("Training 3", rgrp, false);
//                radio.setBorder(BorderFactory.createSimpleBorder(edge,Border.STYLE_TRANSPARENT));
                listVerticalManager.add(radio);
                radio = new RadioButtonField("Training 4", rgrp, false);
//                radio.setBorder(BorderFactory.createSimpleBorder(edge,Border.STYLE_TRANSPARENT));
                listVerticalManager.add(radio);
            //}
            add(listVerticalManager);

            edge = new XYEdges(2, 25, 2, 25);

            HorizontalFieldManager mainHorizontalManager = new HorizontalFieldManager(HorizontalFieldManager.FIELD_HCENTER | HorizontalFieldManager.USE_ALL_WIDTH);
//            mainHorizontalManager.setBorder(BorderFactory.createSimpleBorder(edge,Border.STYLE_TRANSPARENT));

            MyButtonField button = new MyButtonField("Download",ButtonField.ELLIPSIS);
            //stopButton.setBorder(BorderFactory.createSimpleBorder(edge,Border.STYLE_TRANSPARENT));
            button.setChangeListener(new ButtonListener(rgrp,11));
            mainHorizontalManager.add(button);

            button = new MyButtonField("View",ButtonField.ELLIPSIS);
            //stopButton.setBorder(BorderFactory.createSimpleBorder(edge,Border.STYLE_TRANSPARENT));
            button.setChangeListener(new ButtonListener(rgrp,12));
            mainHorizontalManager.add(button);

            button = new MyButtonField("Search",ButtonField.ELLIPSIS);
            //stopButton.setBorder(BorderFactory.createSimpleBorder(edge,Border.STYLE_TRANSPARENT));
            button.setChangeListener(new ButtonListener(rgrp,13));
            mainHorizontalManager.add(button);

            add(mainHorizontalManager);
        }catch(Exception e){
            System.out.println(""+e.toString());
        }

        edge = new XYEdges(5, 0, 0, 0);
        //bf = new BitmapField(img, Field.FIELD_BOTTOM | Field.USE_ALL_HEIGHT);
        //bf.setBorder(BorderFactory.createSimpleBorder(edge,Border.STYLE_TRANSPARENT));
        //add(bf);

        addMenuItem(_mainMenuItem);

	}
    private final class MainItem extends MenuItem
    {
        /**
         * Constructor.
         */
        private MainItem()
        {
            super("Main Menu" , 100, 1 );
        }

        /**
         * Attempts to save the screen's data to its associated memo. If successful,
         * the edit screen is popped from the display stack.
         */
        public void run()
        {
            UiApplication.getUiApplication().popScreen( UiApplication.getUiApplication().getActiveScreen() );
        }
    }
    
    public boolean keyChar(char c, int status, int time)
    {
        switch (c)
        {
            case Characters.ENTER:
            case Characters.DELETE:
            case Characters.BACKSPACE:
                return true;
            case Characters.ESCAPE:
                UiApplication.getUiApplication().popScreen( UiApplication.getUiApplication().getActiveScreen() );
                return true;
            default:
                return super.keyChar(c, status, time);
        }
    }
    protected boolean keyDown(int arg0, int arg1) {
        // TODO Auto-generated method stub
        try{
            switch (arg0)
           {
                case 1179648:
                    close();
                    break;
           }
        }catch(Exception e){
            e.printStackTrace();
        }
        return super.keyDown(arg0, arg1);
    }
}



















