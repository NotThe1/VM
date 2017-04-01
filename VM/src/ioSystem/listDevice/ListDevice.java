package ioSystem.listDevice;

import java.util.prefs.Preferences;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;

import codeSupport.ASCII_CODES;
import ioSystem.Device8080;

public class ListDevice extends Device8080 {

	private JTextArea textArea;
	private Document doc;
	private int linesPerPage;
	private int maxColumn;
	private int tabSize;		// default for CP/M is 9
//	 int tabMin = 1;
//	 int tabMax = 40;		// arbitrary for sure
	
	public ListDevice(JTextArea textArea){
		this("LST:","Parallel",LIST_OUT,LIST_STATUS,textArea);
	}//Constructor

	public ListDevice(String name, String type, Byte addressOut, Byte addressStatus, JTextArea textArea) {
		super(name, type, false, null, true, addressOut, addressStatus);
		this.textArea = textArea;
		this.doc = textArea.getDocument();
		clearDoc();
		appInit();
	}// Constructor
	
	public void close(){
		appClose();
	}//close
	
	private void appClose(){
		Preferences myPrefs = Preferences.userNodeForPackage(ListDevice.class).node(this.getClass().getSimpleName());
		myPrefs.putBoolean("setWideCarriage", maxColumn==COLUMN_120?true:false);
		myPrefs.putInt("tabSize", tabSize);
		myPrefs.putInt("linesPerPage", linesPerPage);
		
		myPrefs = null;
	}//appClose
	
	private void appInit(){
		loadProperties();
	}//appInit
	
	private void loadProperties(){
		Preferences myPrefs = Preferences.userNodeForPackage(ListDevice.class).node(this.getClass().getSimpleName());
		setWideCarriage(myPrefs.getBoolean("setWideCarriage", false));
		tabSize = myPrefs.getInt("tabSize", 9);	// default for CP/M
		linesPerPage = myPrefs.getInt("linesPerPage", 66);
		
		myPrefs = null;
	}//loadProperties

	private void setWideCarriage(boolean state) {
		this.maxColumn = state ? COLUMN_120 : COLUMN_80;
	}// setWideCarriage
	
	private void setTabSize(int size){
		this.tabSize = Math.max(TAB_MIN, size);
		this.tabSize = Math.min(TAB_MAX, size);	
	}//setTabWidth
	
//	public int getTabSize(){
//		return this.tabSize;
//	}//getTabWidth
	
	public void lineFeed(){
		displayPrintable(Character.toString(ASCII_CODES.LF));
	}//lineFeed

	public void formFeed(){
//		Element rootElement = doc.getDefaultRootElement();
		int lineCount = (doc.getDefaultRootElement().getElementCount()- 1);
		int linesToSkip = linesPerPage - (lineCount % linesPerPage);
		
		for (int i = 0;i< linesToSkip;i++){
			lineFeed();
		}//for
		
		System.out.printf("lineCount = %d, lines to skip = %d%n", lineCount,linesToSkip);
	}//formFeed
	
	public void clear(){
		clearDoc();
	}//clear
	
	public void setProperties(){
		ListDevicePropertyDialog listDevicePropertyDialog = new ListDevicePropertyDialog();
		listDevicePropertyDialog.setVisible(true);
		
		System.out.printf("listDevicePropertyDialog = %s%n", !(listDevicePropertyDialog==null));
		listDevicePropertyDialog=null;
		System.out.printf("listDevicePropertyDialog = %s%n", !(listDevicePropertyDialog==null));

	}//setProperties
	
	
	
	public void print(){
		
	}//print
	
	public void saveToFile(){
		
	}//saveToFile
	
	@Override
	public byte byteToCPU(Byte address) {
		// TODO Auto-generated method stub
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
//				Element rootElement = doc.getDefaultRootElement();
//				Element lastElement = rootElement.getElement(rootElement.getElementCount() - 1);
//				int start = lastElement.getStartOffset();
//				int end = lastElement.getEndOffset();
//				try {
//					System.out.printf("lastEmement = %s%n", doc.getText(start, end - start));
//				} catch (BadLocationException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				System.out.printf("Start = %d,  End =%d, size = %d%n%n", start, end, end - start);
//
//				// display(Character.toString(c));
				break;
			default:
				
			break;
			}//switch

		} else if ((c >= 0X20) && (c <= 0X7F)) {
			// Printable characters
			displayPrintable(Character.toString(c));
		} else {
			// above ASCII
		} // if

	}// byteFromCPU
	
	private void doTab(){
		Element lastElement = getLastElement();
		int column = lastElement.getEndOffset()- lastElement.getStartOffset();
		int numberOfSpaces = tabSize - (column % tabSize);
		for (int i = 0; i < numberOfSpaces;i++){
			displayPrintable(SPACE);
		}// for
		
	}//doTab

	private void displayPrintable(String s){
		
		Element lastElement = getLastElement();
		
		// drop anything beyond the max column
		if ((lastElement.getEndOffset()-lastElement.getStartOffset()) < this.maxColumn){
			display(s);
		}// if
		textArea.setCaretPosition(doc.getDefaultRootElement().getEndOffset()-1);
		
	}//displayPrintable
	
	private Element getLastElement(){
		Element rootElement = doc.getDefaultRootElement();
		return rootElement.getElement(rootElement.getElementCount() - 1);
	}//getLastElement

	private void display(String s) {
		Element[] elements = doc.getRootElements();
		try {
			doc.insertString(doc.getLength(), s, null);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}// display

	private void clearDoc() {
		try {
			doc.remove(0, doc.getLength());
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}// clearDoc
	
	private static final String SPACE = " ";		//Space



	private static final int COLUMN_80 = 81;
	private static final int COLUMN_120 = 121;
	
	public static final int TAB_MIN = 1;
	public static final int TAB_MAX = 40;	// arbitrary for sure

	
	private static final byte LIST_OUT = 0X10;
	private static final byte LIST_STATUS = 0X11;

}// class ListDevice
