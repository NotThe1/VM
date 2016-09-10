package utilities;

import java.io.File;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToolBar.Separator;

/**
 * handles the adding and removing of files on the "File" menu. It will place them between the two separators named
 * respectively : "separatorFiles" ( originally not visible) and "separatorExit" usuallly placed just above the Exit
 * menu item
 * 
 * @author Frank Martyn September 2016
 *
 */

public class MenuUtility {
	static Separator exitSeparator;
	static Separator fileSeparator;

	// public static final String SEPARATOR_EXIT = "separatorExit";
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
			}//if remove ?
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
			}// if start
			if (menu.getMenuComponent(i).getName() == RECENT_FILES_END) {
				menu.getMenuComponent(i).setVisible(false);// Separator end
				menu.getMenuComponent(i + 1).setVisible(false);// menu Empty

				filesMenuEnd = i - 1;
				break;
			}// if end
		}// for

		for (int j = filesMenuEnd; j >= filesMenuStart; j--) {
			menu.remove(j);
		}//for remove
	}//clearList
	
	public static ArrayList<String> getFilePaths(JMenu menu){
		ArrayList<String> filePaths = new ArrayList<String>();
		int menuCount = menu.getItemCount();
		boolean isFile = false;

		for (int i = 0; i < menuCount; i++) {
			if (menu.getMenuComponent(i).getName() == RECENT_FILES_START) {
				isFile = true;
				continue; //start collecting paths
			}// if
			if (menu.getMenuComponent(i).getName() == RECENT_FILES_END) {
				break;	// all done
			}// if
			if (isFile){
				
			filePaths.add(((AbstractButton) menu.getMenuComponent(i)).getActionCommand()+ System.lineSeparator());	
			}//if isFile
		}// for
		return filePaths;
	}//getFilePaths

}// class MenuFilesUtility
