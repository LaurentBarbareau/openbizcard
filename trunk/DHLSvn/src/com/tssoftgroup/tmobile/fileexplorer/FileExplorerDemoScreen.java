package com.tssoftgroup.tmobile.fileexplorer;

/*
 * FileExplorerDemoScreen.java
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

import java.util.Enumeration;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;

import com.tssoftgroup.tmobile.component.BrowseVideoTitleDescriptionDialog;
import com.tssoftgroup.tmobile.component.ShareDocVideoTitleDescriptionDialog;
import com.tssoftgroup.tmobile.screen.MyDocumentShare;
import com.tssoftgroup.tmobile.screen.MyMovieShare;
import com.tssoftgroup.tmobile.screen.TMobile;
import com.tssoftgroup.tmobile.utils.Const;

import net.rim.blackberry.api.invoke.CameraArguments;
import net.rim.blackberry.api.invoke.Invoke;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.KeypadListener;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * Main screen to show the listing of all directories/files.
 */
/* package */public class FileExplorerDemoScreen extends MainScreen {
	private TMobile _uiApp;
	private FileExplorerDemoListFieldImpl _list;
	private FileExplorerDemoJournalListener _fileListener;
	private String _parentRoot;
	TextField tf;

	/**
	 * Constructor
	 */
	public FileExplorerDemoScreen(TextField tf) {
		this.tf = tf;
		setTitle("Share a document");

		_list = new FileExplorerDemoListFieldImpl();
		add(_list);
		readRoots(null);

		_uiApp = (TMobile) UiApplication.getUiApplication();
		_fileListener = new FileExplorerDemoJournalListener(this);
		_uiApp.addFileSystemJournalListener(_fileListener);
	}

	/**
	 * Overrides super. Removes listener before closing the screen.
	 * 
	 * @see net.rim.device.api.ui.Screen#close()
	 */
	public void close() {
		_uiApp.removeFileSystemJournalListener(_fileListener);
		super.close();
	}

	/**
	 * Deletes the selected file or directory.
	 */
	private void deleteAction() {
		int index = _list.getSelectedIndex();
		FileExplorerDemoFileHolder fileholder = (FileExplorerDemoFileHolder) _list
				.get(_list, index);

		if (fileholder != null) {
			String filename = fileholder.getPath() + fileholder.getFileName();

			if (Dialog.ask(Dialog.D_DELETE) == Dialog.DELETE) {
				FileConnection fc = null;

				try {
					fc = (FileConnection) Connector.open("file:///" + filename);
					fc.delete();
					_list.remove(index);
				} catch (Exception ex) {
					Dialog.alert("Unable to delete file or directory: "
							+ filename);
				} finally {
					try {
						if (fc != null) {
							fc.close();
							fc = null;
						}
					} catch (Exception ioex) {
					}
				}
			}
		}
	}

	/**
	 * Overrides default. Enter key will take action on directory/file. Escape
	 * key will go up one directory or close application if at top level.
	 * 
	 * @see net.rim.device.api.ui.Screen#keyChar(char,int,int)
	 * 
	 */
	public boolean keyChar(char c, int status, int time) {
		switch (c) {
		case Characters.ENTER:
			return selectAction();

		case Characters.DELETE:

		case Characters.BACKSPACE:
			deleteAction();
			return true;

		case Characters.ESCAPE:
			if (goBack()) {
				return true;
			}

		default:
			return super.keyChar(c, status, time);
		}
	}

	/**
	 * Creates the menu to be used in the application.
	 * 
	 * @see net.rim.device.api.ui.Screen#makeMenu(Menu,int)
	 */
	public void makeMenu(Menu menu, int instance) {
		// Only display the menu if no actions are performed.
		if (instance == Menu.INSTANCE_DEFAULT) {
			menu.add(_selectItem);

			if (DeviceInfo.hasCamera()) {
				menu.add(_cameraItem);
			}

			menu.add(_deleteItem);

			if (_parentRoot != null) {
				menu.add(_backItem);
			}
		}

		super.makeMenu(menu, instance);
	}

	/**
	 * Overrides default implementation. Performs the select action if the
	 * trackwheel was clicked; otherwise, the default action occurs.
	 * 
	 * @see net.rim.device.api.ui.Screen#navigationClick(int,int)
	 */
	public boolean navigationClick(int status, int time) {
		if ((status & KeypadListener.STATUS_TRACKWHEEL) != KeypadListener.STATUS_TRACKWHEEL) {
			return selectAction();
		}

		return super.navigationClick(status, time);
	}

	/**
	 * Reads the path that was passed in and enumerates through it.
	 * 
	 * @param root
	 *            Path to be read.
	 */
	private void readRoots(String root) {
		_parentRoot = root;

		// Clear whats in the list.
		_list.removeAll();

		FileConnection fc = null;
		Enumeration rootEnum = null;

		if (root != null) {
			// Open the file system and get the list of directories/files.
			try {
				fc = (FileConnection) Connector.open("file:///" + root);
				rootEnum = fc.list();
			} catch (Exception ioex) {
			} finally {

				if (fc != null) {
					// Everything is read, make sure to close the connection.
					try {
						fc.close();
						fc = null;
					} catch (Exception ioex) {
					}
				}
			}
		}

		// There was no root to read, so now we are reading the system roots.
		if (rootEnum == null) {
			rootEnum = FileSystemRegistry.listRoots();
		}

		// Read through the list of directories/files.
		while (rootEnum.hasMoreElements()) {
			String file = (String) rootEnum.nextElement();

			if (root != null) {
				file = root + file;
			}

			readSubroots(file);
		}
	}

	/**
	 * Reads all the directories and files from the provided path.
	 * 
	 * @param file
	 *            Upper directory to be read.
	 */
	private void readSubroots(String file) {
		FileConnection fc = null;

		try {
			fc = (FileConnection) Connector.open("file:///" + file);

			// Create a file holder from the FileConnection so that the
			// connection is not left open.
			FileExplorerDemoFileHolder fileholder = new FileExplorerDemoFileHolder(
					file);
			fileholder.setDirectory(fc.isDirectory());
			if (fc.isDirectory()
					|| (!fc.isDirectory() && (file.toLowerCase().endsWith(
							".ppt")
							|| file.toLowerCase().endsWith(".xls")
							|| file.toLowerCase().endsWith(".doc") || file
							.toLowerCase().endsWith(".pdf")|| file
							.toLowerCase().endsWith(".txt")
							|| file.toLowerCase().endsWith(".zip")))) {
				_list.add(fileholder);
			}
		} catch (Exception ioex) {
		} finally {
			if (fc != null) {
				// Everything is read, make sure to close the connection.
				try {
					fc.close();
					fc = null;
				} catch (Exception ioex) {
				}
			}
		}
	}

	/**
	 * Displays information on the selected file.
	 * 
	 * @return True.
	 */
	private boolean selectAction() {
		FileExplorerDemoFileHolder fileholder = (FileExplorerDemoFileHolder) _list
				.get(_list, _list.getSelectedIndex());

		if (fileholder != null) {
			// If it's a directory then show what's in the directory.
			if (fileholder.isDirectory()) {
				readRoots(fileholder.getPath());
			} else {

				// It's a file so display information on it.
				Const.fileToUpload = fileholder.getPath()
						+ fileholder.getFileName();
				MyMovieShare.getInstance();
				if (tf != null) {
					tf.setText(fileholder.getPath() + fileholder.getFileName());
				}
				// _uiApp.pushScreen(MyMovieShare.getInstance());
				_uiApp.popScreen(this);
				ShareDocVideoTitleDescriptionDialog.getInstance().myshow();
			}
		}

		return true;
	}

	/**
	 * Updates the list of files.
	 */
	/* package */void updateList() {
		synchronized (_uiApp.getAppEventLock()) {
			readRoots(_parentRoot);
		}
		;
	}

	/**
	 * Goes back one directory in the directory hierarchy, if possible.
	 * 
	 * @return True if went back a directory; false otherwise.
	 */
	private boolean goBack() {
		if (_parentRoot != null) {
			String backParentRoot = _parentRoot.substring(0, _parentRoot
					.lastIndexOf('/'));
			backParentRoot = backParentRoot.substring(0, backParentRoot
					.lastIndexOf('/') + 1);

			if (backParentRoot.length() > 0) {
				readRoots(backParentRoot);
			} else {
				readRoots(null);
			}

			return true;
		}

		return false;
	}

	// ///////////////////////////////////////////////////////////
	// Menu Items //
	// ///////////////////////////////////////////////////////////

	/**
	 * Menu item for invoking the camera application. This provides a convenient
	 * method of adding a file to the device file system in order to demonstrate
	 * the FileSystemJournalListener.
	 */
	private MenuItem _cameraItem = new MenuItem("Camera", 500, 500) {
		public void run() {
			Invoke.invokeApplication(Invoke.APP_TYPE_CAMERA,
					new CameraArguments());
		}
	};

	/**
	 * Menu item for deleting the selected file.
	 */
	private MenuItem _deleteItem = new MenuItem("Delete", 500, 500) {
		public void run() {
			deleteAction();
		}
	};

	/**
	 * Menu item for displaying information on the selected file.
	 */
	private MenuItem _selectItem = new MenuItem("Select", 500, 500) {
		public void run() {
			selectAction();
		}
	};

	/**
	 * Menu item for going back one directory in the directory hierarchy.
	 */
	private MenuItem _backItem = new MenuItem("Go Back", 500, 500) {
		public void run() {
			goBack();
		}
	};
}
