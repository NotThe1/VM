package ioSystem.listDevice;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;

import ioSystem.Device8080;

public class ListDevice extends Device8080 {

	Document doc;
	int maxColumn;
	
	public ListDevice(Document doc){
		this("LST:","Parallel",LIST_OUT,LIST_STATUS,doc);
	}//Constructor

	public ListDevice(String name, String type, Byte addressOut, Byte addressStatus, Document doc) {
		super(name, type, false, null, true, addressOut, addressStatus);
		setWideCarriage(false);
		this.doc = doc;
		clearDoc();
	}// Constructor

	public void setWideCarriage(boolean state) {
		this.maxColumn = state ? COLUMN_120 : COLUMN_80;
	}// setWideCarriage

	@Override
	public void byteFromCPU(Byte address, Byte value) {
		char c = (char) ((byte) value);

		if (c < 0X20) {
			switch (c) {
			case 0X0A: // LF
				display(Character.toString(c));
				break;
			case 0X0D: // ignore CR
				Element rootElement = doc.getDefaultRootElement();
				Element lastElement = rootElement.getElement(rootElement.getElementCount() - 1);
				int start = lastElement.getStartOffset();
				int end = lastElement.getEndOffset();
				try {
					System.out.printf("lastEmement = %s%n", doc.getText(start, end - start));
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.printf("Start = %d,  End =%d, size = %d%n%n", start, end, end - start);

				// display(Character.toString(c));
				break;
			}

		} else if ((c >= 0X20) && (c <= 0X7F)) {
			// Printable characters
			displayPrintable(Character.toString(c));
		} else {
			// above ASCII
		} // if

	}// byteFromCPU

	@Override
	public byte byteToCPU(Byte address) {
		// TODO Auto-generated method stub
		return 0;
	}// byteToCPU
	
	private void displayPrintable(String s){
		Element rootElement = doc.getDefaultRootElement();
		Element lastElement = rootElement.getElement(rootElement.getElementCount() - 1);
		
		// drop anything beyond the max column
		if ((lastElement.getEndOffset()-lastElement.getStartOffset()) < this.maxColumn){
			display(s);
		}// if
		
	}//displayPrintable

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

	private static final int COLUMN_80 = 81;
	private static final int COLUMN_120 = 121;
	
	private static final byte LIST_OUT = 0X10;
	private static final byte LIST_STATUS = 0X11;

}// class ListDevice
