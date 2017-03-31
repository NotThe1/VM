package ioSystem.listDevice;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;

import codeSupport.ASCII_CODES;
import ioSystem.Device8080;

public class ListDevice extends Device8080 {

	private JTextArea textArea;
	private Document doc;
	private int maxColumn;
	private int tabSize = 9;		// default for CP/M
	private int tabMin = 1;
	private int tabMax = 40;		// arbitrary for sure
	
	public ListDevice(JTextArea textArea){
		this("LST:","Parallel",LIST_OUT,LIST_STATUS,textArea);
	}//Constructor

	public ListDevice(String name, String type, Byte addressOut, Byte addressStatus, JTextArea textArea) {
		super(name, type, false, null, true, addressOut, addressStatus);
		setWideCarriage(false);
		this.textArea = textArea;
		this.doc = textArea.getDocument();
		clearDoc();
	}// Constructor

	public void setWideCarriage(boolean state) {
		this.maxColumn = state ? COLUMN_120 : COLUMN_80;
	}// setWideCarriage
	
	public void setTabSize(int size){
		this.tabSize = Math.max(tabMin, size);
		this.tabSize = Math.min(tabMax, size);	
	}//setTabWidth
	
	public int getTabSize(){
		return this.tabSize;
	}//getTabWidth

	@Override
	public void byteFromCPU(Byte address, Byte value) {
		char c = (char) ((byte) value);

		if (c < 0X20) {
			switch (c) {
			case ASCII_CODES.TAB:
				break;
			case ASCII_CODES.LF: // 0X0A:
				display(Character.toString(c));
				break;
			case ASCII_CODES.CR: // ignore CR
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

	@Override
	public byte byteToCPU(Byte address) {
		// TODO Auto-generated method stub
		return (byte) 0XFF;
	}// byteToCPU
	
	private void displayPrintable(String s){
		Element rootElement = doc.getDefaultRootElement();
		Element lastElement = rootElement.getElement(rootElement.getElementCount() - 1);
		
		// drop anything beyond the max column
		if ((lastElement.getEndOffset()-lastElement.getStartOffset()) < this.maxColumn){
			display(s);
		}// if
		textArea.setCaretPosition(rootElement.getEndOffset()-1);
		
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
	
//	private static final char NUL	= 0X00;		//CTRL @	Null Prompt
//	private static final char SOH	= 0X01;		//CTRL A	Start of Heading
//	private static final char STX	= 0X02;		//CTRL B	Start of Text
//	private static final char ETX	= 0X03;		//CTRL C	End of Text
//	private static final char EOT	= 0X04;		//CTRL D	End of Transmission
//	private static final char ENQ	= 0X05;		//CTRL E	Enquiry
//	private static final char ACK	= 0X06;		//CTRL F	Acknowledgement
//	private static final char BEL	= 0X07;		//CTRL G	Bell
//	
//	private static final char BS	= 0X08;		//CTRL H	Backspace
//	private static final char TAB	= 0X09;		//CTRL I	Horizontal Tab
//	private static final char LF	= 0X0A;		//CTRL J	Line feed
//	private static final char VT	= 0X0B;		//CTRL K	Vertical Tab
//	private static final char FF	= 0X0C;		//CTRL L	Form Feed
//	private static final char CR	= 0X0D;		//CTRL M	Carriage Return
//	private static final char SO	= 0X0E;		//CTRL N	Shift out
//	private static final char SI	= 0X0F;		//CTRL O	Shift In
//	
//	private static final char DLE	= 0X10;		//CTRL P	Data Link Escape
//	private static final char XON	= 0X11;		//CTRL Q	X-ON
//	private static final char DC2	= 0X12;		//CTRL R	DC-2
//	private static final char XOFF	= 0X13;		//CTRL S	X-OFF
//	private static final char DC4	= 0X14;		//CTRL T	DC-4
//	private static final char NAK	= 0X15;		//CTRL U	No Acknowledge
//	private static final char SYN	= 0X16;		//CTRL V	Synchronous Idle
//	private static final char ETB	= 0X17;		//CTRL W	End transmission Block
//	                                    
//	private static final char CAN	= 0X18;		//CTRL X	Cancel
//	private static final char EM	= 0X19;		//CTRL Y	End of Medium
//	private static final char SUB	= 0X1A;		//CTRL Z	Substitute
//	private static final char ESC	= 0X1B;		//CTRL [	Escape
//	private static final char FS	= 0X1C;		//CTRL \	File Separator
//	private static final char GS	= 0X1D;		//CTRL ]	Group Separator
//	private static final char RS	= 0X1E;		//CTRL ^	Record Separator
//	private static final char US	= 0X1F;		//CTRL _	Unit Separator


	private static final int COLUMN_80 = 81;
	private static final int COLUMN_120 = 121;
	
	private static final byte LIST_OUT = 0X10;
	private static final byte LIST_STATUS = 0X11;

}// class ListDevice
