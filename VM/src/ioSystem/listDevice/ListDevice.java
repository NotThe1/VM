package ioSystem.listDevice;

import java.awt.Font;
import java.awt.print.PrinterException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Date;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;

import codeSupport.ASCII_CODES;
import ioSystem.Device8080;
import utilities.FilePicker;
import utilities.FontChooser;

public class ListDevice extends Device8080 {

	private JTextArea textArea;
	private Document doc;
	private int linesPerPage = 66;
	private int maxColumn;
	private boolean limitColumns;
	private int tabSize; // default for CP/M is 9

	private Path newListingPath; // location to save listing to file

	public ListDevice(JTextArea textArea) {
		this("LST:", "Parallel", LIST_OUT, LIST_STATUS, textArea);
	}// Constructor

	public ListDevice(String name, String type, Byte addressOut, Byte addressStatus, JTextArea textArea) {
		super(name, type, false, null, true, addressOut, addressStatus);
		this.textArea = textArea;
		this.doc = textArea.getDocument();
		clearDoc();
		appInit();
	}// Constructor

	public void close() {
		appClose();
	}// close

	private void appClose() {
		Preferences myPrefs = Preferences.userNodeForPackage(ListDevice.class).node(this.getClass().getSimpleName());

		myPrefs.putInt("tabSize", tabSize);

		myPrefs.putInt("maxColumns", maxColumn);
		myPrefs.putBoolean("limitColumns", limitColumns);

		Font currentFont = textArea.getFont();
		String currentStyle = "Plain";
		;
		switch (currentFont.getStyle()) {
		case Font.PLAIN:
			currentStyle = "Plain";
			break;
		case Font.BOLD:
			currentStyle = "Bold";
			break;
		case Font.ITALIC:
			currentStyle = "Italic";
			break;
		case Font.BOLD | Font.ITALIC:
			currentStyle = "Bold Italic";
			break;
		}// switch

		myPrefs.put("fontFamily", currentFont.getFamily());
		myPrefs.put("fontStyle", currentStyle);
		myPrefs.put("fontSize", Integer.toString(currentFont.getSize()));

		myPrefs = null;
	}// appClose

	private void appInit() {
		loadProperties();
	}// appInit

	private void loadProperties() {
		Preferences myPrefs = Preferences.userNodeForPackage(ListDevice.class).node(this.getClass().getSimpleName());

		tabSize = myPrefs.getInt("tabSize", 9); // default for CP/M

		maxColumn = (myPrefs.getInt("maxColumns", 80));
		limitColumns = myPrefs.getBoolean("limitColumns", false);
		int style = FontChooser.getStyleFromText(myPrefs.get("fontFamily", "Plain"));

		Font newFont = new Font(myPrefs.get("fontFamily", "Courier New"), style, myPrefs.getInt("fontSize", 13));
		textArea.setFont(newFont);
		myPrefs = null;

		Font f = textArea.getFont();

		System.out.printf("[loadProperties] Font family = %s, Font Size = %d%n", f.getFamily(), f.getSize());
	}// loadProperties

	// private void setMaxColumn(boolean state) {
	// this.maxColumn = state ? COLUMN_120 : COLUMN_80;
	// }// setWideCarriage
	//
	// private void setTabSize(int size) {
	// this.tabSize = Math.max(TAB_MIN, size);
	// this.tabSize = Math.min(TAB_MAX, size);
	// }// setTabWidth

	// public int getTabSize(){
	// return this.tabSize;
	// }//getTabWidth

	public void lineFeed() {
		displayPrintable(Character.toString(ASCII_CODES.LF));
	}// lineFeed

	public void formFeed() {
		// Element rootElement = doc.getDefaultRootElement();
		int lineCount = (doc.getDefaultRootElement().getElementCount() - 1);
		int linesToSkip = linesPerPage - (lineCount % linesPerPage);

		for (int i = 0; i < linesToSkip; i++) {
			lineFeed();
		} // for

		System.out.printf("lineCount = %d, lines to skip = %d%n", lineCount, linesToSkip);
	}// formFeed

	public void clear() {
		clearDoc();
	}// clear

	public void showProperties() {
		// SwingUtilities.getWindowAncestor(textArea);
		ListDevicePropertyDialog listDevicePropertyDialog = new ListDevicePropertyDialog(textArea);

		if (listDevicePropertyDialog.showDialog() == JOptionPane.OK_OPTION) {
			loadProperties();
		} // if
		listDevicePropertyDialog = null;

	}// showProperties

	public void print() {
		String headerString = JOptionPane.showInputDialog("Input header (optional)\n Canel for no header");
		Font originalFont = textArea.getFont();
		try {
			textArea.setFont(originalFont.deriveFont(originalFont.getSize2D() * 0.75f));
			if (headerString == null) {
				textArea.print();
			} else {
				MessageFormat header = new MessageFormat(headerString);
				MessageFormat footer = new MessageFormat(new Date().toString() + "           Page - {0}");
				textArea.print(header, footer);
			} // if
			textArea.setFont(originalFont);
		} catch (PrinterException e) {
			e.printStackTrace();
		} // try
	}// print

	public void saveToFile() {
		// SwingUtilities.getRootPane(textArea);
		JFileChooser fc = (newListingPath) == null ? FilePicker.getListingPicker()
				: FilePicker.getListingPicker(newListingPath);
		if (fc.showSaveDialog(SwingUtilities.getRootPane(textArea)) == JFileChooser.CANCEL_OPTION) {
			System.out.println("Bailed out of the open");
			return;
		} // if - open
		newListingPath = Paths.get(fc.getSelectedFile().getParent());

		String listingFile = fc.getSelectedFile().getAbsolutePath();
		String completeSuffix = "." + FilePicker.LISTING_SUFFIX;
		listingFile = listingFile.replaceFirst("\\" + completeSuffix + "$", "");

		ElementIterator elementIterator = new ElementIterator(doc.getDefaultRootElement());

		try {
			FileWriter fileWriter = new FileWriter(listingFile + completeSuffix);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

			Element aLine = elementIterator.next();
			aLine = elementIterator.next(); // skip the rootElement
			int start, end;
			String theLine;
			while (aLine != null) {
				start = aLine.getStartOffset();
				end = aLine.getEndOffset() - 1;
				theLine = doc.getText(start, end - start); // Strip LF
				bufferedWriter.write(theLine + System.lineSeparator());
				System.out.printf("start = %d, end = %d, text = %s%n", start, end, doc.getText(start, end - start));
				aLine = elementIterator.next();
			} // while
			bufferedWriter.close();

		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		} // try

	}// saveToFile

	@Override
	public byte byteToCPU(Byte address) {
		return (byte) 0XFF;
	}// byteToCPU

	@Override
	public void byteFromCPU(Byte address, Byte value) {
		char c = (char) ((byte) value);

		if (c < 0X20) {
			switch (c) {
			case ASCII_CODES.TAB:
				doTab();
				break;
			case ASCII_CODES.LF: // 0X0A:
				display(Character.toString(c));
				break;
			case ASCII_CODES.CR: // ignore CR
				// Element rootElement = doc.getDefaultRootElement();
				// Element lastElement = rootElement.getElement(rootElement.getElementCount() - 1);
				// int start = lastElement.getStartOffset();
				// int end = lastElement.getEndOffset();
				// try {
				// System.out.printf("lastEmement = %s%n", doc.getText(start, end - start));
				// } catch (BadLocationException e) {
				//
				// e.printStackTrace();
				// }
				// System.out.printf("Start = %d, End =%d, size = %d%n%n", start, end, end - start);
				//
				// // display(Character.toString(c));
				break;
			default:

				break;
			}// switch

		} else if ((c >= 0X20) && (c <= 0X7F)) {
			// Printable characters
			displayPrintable(Character.toString(c));
		} else {
			// above ASCII
		} // if

	}// byteFromCPU

	private void doTab() {
		Element lastElement = getLastElement();
		int column = lastElement.getEndOffset() - lastElement.getStartOffset();
		int numberOfSpaces = tabSize - (column % tabSize);
		for (int i = 0; i < numberOfSpaces; i++) {
			displayPrintable(SPACE);
		} // for

	}// doTab

	private void displayPrintable(String s) {

		Element lastElement = getLastElement();

		if (!limitColumns) {// drop anything beyond the max column ??
			display(s);
		} else if ((lastElement.getEndOffset() - lastElement.getStartOffset()) < this.maxColumn) {
			display(s);
		} // if
		textArea.setCaretPosition(doc.getDefaultRootElement().getEndOffset() - 1);

	}// displayPrintable

	private Element getLastElement() {
		Element rootElement = doc.getDefaultRootElement();
		return rootElement.getElement(rootElement.getElementCount() - 1);
	}// getLastElement

	private void display(String s) {
		// Element[] elements = doc.getRootElements();
		try {
			doc.insertString(doc.getLength(), s, null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}// display

	private void clearDoc() {
		try {
			doc.remove(0, doc.getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}// clearDoc

	private static final String SPACE = " "; // Space

	// public static final int TAB_MIN = 1;
	// public static final int TAB_MAX = 40; // arbitrary for sure

	private static final byte LIST_OUT = 0X10;
	private static final byte LIST_STATUS = 0X11;

}// class ListDevice
