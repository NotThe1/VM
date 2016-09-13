package utilities;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import javax.swing.AbstractButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

/**
 * handles the adding and removing of files on a menu, usually the "File" menu. It will place them between the two
 * separators with attribute "name" respectively : "separatorFiles" ( originally not visible) and "separatorExit"
 * followed by a menu Item used to clear the Recent File list. These are usually placed just above the Exit menu item.
 * See code below:
 * <p>
 * JSeparator separatorFileStart = new JSeparator();
 * <p>
 * separatorFileStart.setName(MenuUtility.RECENT_FILES_START);
 * <p>
 * mnuFile.add(separatorFileStart);
 * <p>
 * <p>
 * JSeparator separatorFileEnd = new JSeparator();
 * <p>
 * separatorFileEnd.setName(MenuUtility.RECENT_FILES_END);
 * <p>
 * mnuFile.add(separatorFileEnd);
 * <p>
 * <p>
 * JMenuItem mnuRemoveRecentFiles = new JMenuItem("Remove Recent Files");
 * <p>
 * mnuRemoveRecentFiles.addActionListener(new ActionListener() {
 * <p>
 * public void actionPerformed(ActionEvent arg0) {
 * <p>
 * MenuUtility.clearList(mnuFile);
 * <p>
 * <p>
 * );
 * <p>
 * mnuFile.add(mnuRemoveRecentFiles);
 * 
 * 
 * @author Frank Martyn September 2016
 *
 */

public class MenuUtility {
	public static final String RECENT_FILES_START = "recentFilesStart";
	public static final String RECENT_FILES_END = "recentFilesEnd";
	private static final String NUMBER_DELIM = ":";

	// private static final String NUMBER_DELIM_REDEX = "\\"

	public MenuUtility() {
		// TODO Auto-generated constructor stub
	}//

	/**
	 * Adds a JMenuItem to the menu's recent Files list with the file's absolute path for the text. Returns the new menu
	 * item so that an action listener can be added by the calling class
	 * 
	 * @param menu
	 *            - JMenu to have the JMenuItem added to ( usually "File"
	 * @param file
	 *            - File to be added to the menu's Recent Files List
	 * @return - The new JMenuItem ( so it can be manipulated by the calling class)
	 */

	public static JMenuItem addFile(JMenu menu, File file) {// change to Integer
		int menuCount = menu.getItemCount();
		int filesMenuStart = 0;
		int filesMenuEnd = 0;

		for (int i = 0; i < menuCount; i++) {
			if (menu.getMenuComponent(i).getName() == RECENT_FILES_START) {
				menu.getMenuComponent(i).setVisible(true); // Separator start
				menu.getMenuComponent(i + 1).setVisible(true);// Separator end
				menu.getMenuComponent(i + 2).setVisible(true);// menu Empty
				filesMenuStart = i + 1;
			}// if
			if (menu.getMenuComponent(i).getName() == RECENT_FILES_END) {
				filesMenuEnd = i;
				break;
			}// if
		}// for
		Integer removeIndex = null;
		int fileIndex = 2;
		String menuText, menuActionCommand;
		for (int j = filesMenuStart; j < filesMenuEnd; j++) {
			menuActionCommand = ((AbstractButton) menu.getMenuComponent(j)).getActionCommand();
			if (menuActionCommand.equals(file.getAbsolutePath())) {
				removeIndex = j; // remember for later
				break;
			}
			menuText = String.format("%2d%s  %s", fileIndex++, NUMBER_DELIM, menuActionCommand);
			((AbstractButton) menu.getMenuComponent(j)).setText(menuText);
		}// for

		if (removeIndex != null) {
			menu.remove(removeIndex);
		}// if remove

		menuText = String.format("%2d%s  %s", 1, NUMBER_DELIM, file.getAbsolutePath());
		JMenuItem newMenu = new JMenuItem(menuText);
		newMenu.setActionCommand(file.getAbsolutePath());
		menu.insert(newMenu, filesMenuStart);

		return newMenu;
	}// addFile

	/**
	 * Clears the recent File list and sets visible false for Separator Start,Separator End, and menu clearRecentFiles
	 * 
	 * @param menu
	 *            is the menu the recent File list is on
	 */
	public static void clearList(JMenu menu) {
		int menuCount = menu.getItemCount();
		int filesMenuStart = 0;
		int filesMenuEnd = 0;

		for (int i = 0; i < menuCount; i++) {
			if (menu.getMenuComponent(i).getName() == RECENT_FILES_START) {
				menu.getMenuComponent(i).setVisible(false); // Separator start
				filesMenuStart = i + 1;
			}// if
			if (menu.getMenuComponent(i).getName() == RECENT_FILES_END) {
				menu.getMenuComponent(i).setVisible(false);// Separator end
				menu.getMenuComponent(i + 1).setVisible(false);// menu Empty

				filesMenuEnd = i - 1;
				break;
			}// if
		}// for

		for (int j = filesMenuEnd; j >= filesMenuStart; j--) {
			menu.remove(j);
		}//
			// int k = filesMenuEnd;
		// menu.remove(k--);
	}// clearList

	/**
	 * returns an ArrayList with all the file paths currently on the Recent File List
	 * 
	 * @param menu
	 *            the menu that contains the recent File List
	 * @return an ArrayList<String> of the full paths of the files on the Recent File List
	 */
	public static ArrayList<String> getFilePaths(JMenu menu) {
		ArrayList<String> filePaths = new ArrayList<String>();
		int menuCount = menu.getItemCount();
		boolean isFile = false;

		for (int i = 0; i < menuCount; i++) {
			if (menu.getMenuComponent(i).getName() == RECENT_FILES_START) {
				isFile = true;
				continue; // start collecting paths
			}// if
			if (menu.getMenuComponent(i).getName() == RECENT_FILES_END) {
				break; // all done
			}// if
			if (isFile) {

				filePaths.add(((AbstractButton) menu.getMenuComponent(i)).getActionCommand() + System.lineSeparator());
			}// if isFile
		}// for
		return filePaths;
	}// getFilePaths

	/**
	 * save to the calling class' Preference fiel the count of file on the File List and the fullPaths of the fiels
	 * 
	 * @param myPrefs
	 *            A node in a hierarchical collection of preference data used by the calling class
	 * @param menu
	 *            where the Recent File List are listed
	 */
	public static void saveRecentFileList(Preferences myPrefs, JMenu menu) {
		ArrayList<String> filePaths = MenuUtility.getFilePaths(menu);
		myPrefs.putInt("RecentFileCount", filePaths.size());
		if (filePaths.isEmpty()) {
			return; // nothing to save;
		}// if is there any?

		int listIndex = 0;
		String key;
		for (int i = filePaths.size() - 1; i >= 0; i--) {
			key = String.format("RecentFile_%02d", listIndex++);
			myPrefs.put(key, filePaths.get(i));
		}// for each path

	}// saveRecentFileList

	/**
	 * Uses the calling class'sPreference loads the saved file onto the Receent File List
	 * 
	 * @param myPrefs
	 *            A node in a hierarchical collection of preference data used by the calling class
	 * @param menu
	 *            where the Recent File List is to be listed
	 */
	public static void loadRecentFileList(Preferences myPrefs, JMenu menu) {
		int fileCount = myPrefs.getInt("RecentFileCount", 0);
		if (fileCount == 0) {
			return; // no recentFiles saved
		}// if any ?

		String key;

		for (int i = 0; i < fileCount; i++) {
			key = String.format("RecentFile_%02d", i);
			addFile(menu, new File(myPrefs.get(key, "NO FILE")));
		}// for each path

	}// loadRecentFileList

}// class MenuFilesUtility
