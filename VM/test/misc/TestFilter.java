package misc;

import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;

public class TestFilter extends DocumentFilter {
	StyledDocument doc;
	int addressEnd, dataEnd, asciiEnd;
	int row, column, elementPosition;
	boolean isAddress, isData, isASCII;
	boolean isInsert;
	Pattern hexPattern;
	Pattern printablePattern;

	public TestFilter() {
		// TODO Auto-generated constructor stub
	}// Constructor

	public TestFilter(StyledDocument doc, int addressEnd, int dataEnd, int asciiEnd) {
		this.doc = doc;
		this.addressEnd = addressEnd;
		this.dataEnd = dataEnd;
		this.asciiEnd = asciiEnd;
		appInit();
	}// Constructor

	public void appInit() {
		hexPattern = Pattern.compile(PATTERN_HEX);
		printablePattern = Pattern.compile(PATTERN_PRINTABLE);
	}// appInit

	public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr)
			throws BadLocationException {
		System.out.printf("[insertString]\tattr: %s \toffset: %4d , string: %s %n", attr.toString(), offset, string);
		fb.insertString(offset, string, attr);
	}// insertString

	public void remove(DocumentFilter.FilterBypass fb, int offset, int length)
			throws BadLocationException {
		System.out.printf("[remove]\toffset: %d , length: %d %n", offset, length);
		setCoordinates(fb, offset);
		if (isAddress) {
			return; // no changes to the address field
		} else if (isData) {
			//
		} else if (isASCII) {
			//
		} else {
			return; // unknown location
		}// if type

		fb.remove(offset, length);
	}// remove

	public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
			throws BadLocationException {
		System.out.printf("[replace]\toffset: %d ,length: %d, text: %s %n", offset, length, text);
		setCoordinates(fb, offset);
		int netLength = length == 0 ? 1 : length;// length of 0 equals a insert; length of 1 equals a replace

		// if (isAddress) {
		// return; // no changes to the address field
		// }// if address

		netLength = 1;// TODO this is a 1 char edit, look to make it multiple char edit
		if (isData) {
			replaceData(fb, offset, netLength, text, attrs);
		} else if (isASCII) {
			replaceASCII(fb, offset, netLength, text, attrs);
		} else {
			return; // either isAddress or unknown location
		}// if type

		// fb.replace(offset, length, text, attrs);
	}// replace

	private void setCoordinates(DocumentFilter.FilterBypass fb, int offset) {
		Element rootElement = doc.getDefaultRootElement();
		row = rootElement.getElementIndex(offset);
		Element paragraphElement = rootElement.getElement(row);
		column = offset - paragraphElement.getStartOffset();

		if (column < addressEnd) {
			isAddress = true;
			isData = false;
			isASCII = false;
			elementPosition = column;
		} else if (column < dataEnd) {
			isAddress = false;
			isData = true;
			isASCII = false;
			elementPosition = column - addressEnd;
		} else {
			isAddress = false;
			isData = false;
			isASCII = true;
			elementPosition = column - dataEnd;
		}//

		System.out.printf("isAddress: %s, isData: %s, isASCII: %s, %n", isAddress, isData, isASCII);
		System.out.printf("offset %4d, row: %3d, column: %2d , elementPosition: %d, %n%n",
				offset, row, column, elementPosition);
	}// setCoordinates

	public void replaceData(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
			throws BadLocationException {
		if (!isTextData(text)) {
			return; // address will only accept hex characters and space
		}// if - need Hex data
		boolean isValue = false;
		int newCharacterIndex = -1;
		int dataKind = dataKinds[elementPosition];
		switch (dataKind) {
		case BLANK1:
			text = SPACE;
			break;
		case BLANK2:
			text = SPACE + SPACE;
			length = 2;
			break;
		case BLANK3:
			// text = SPACE;
			break;
		case BLANK4:
			text = SPACE;
			// length = 2;
			break;
		case HEX1:
			if (!isTextHex(text)) {
				return;
			}// if - want only hex characters
			newCharacterIndex = offset;
			break;
		case HEX2:
			if (!isTextHex(text)) {
				return;
			}// if - want only hex characters
			text += SPACE;
			length = 2;
			newCharacterIndex = offset-1;
			break;
		case HEX3:
			if (!isTextHex(text)) {
				return;
			}// if - want only hex characters
			text += SPACE + SPACE;
			length = 3;
			newCharacterIndex = offset-1;
			break;
		case HEX4:
			moveToNextDataLine(offset);
			break;
		default:
		}// switch
		fb.replace(offset, length, text, attrs);
		if(newCharacterIndex != -1){
			String newChar = convertToPrintable( newCharacterIndex);
			int asciiIndex = dataToASCII[elementPosition];
			int toEndOfData = dataEnd - elementPosition;
			int docOffset = offset + toEndOfData + asciiIndex - addressEnd + 2;
			fb.replace(docOffset, 1, newChar, attrs);
			System.out.printf("Add - \"%s\" into location %d%n", newChar,docOffset);
			System.out.printf("asciiIndex: %d ,toEndOfData: %d ,docOffset %d %n", asciiIndex,toEndOfData,docOffset);
		}//if
	}// replaceData
	

	
	public String convertToPrintable(int offset){
		String HexString=null;
		try {
			HexString = doc.getText(offset, 2);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Integer i  = Integer.valueOf(HexString,16);
		char[] c = new char[1];
		c[0] = (char) (i & 0XFF);
		return ((c[0] >= 0X20) && (c[0] <= 0X7F)) ? new String(c) : NON_PRINTABLE_CHAR;
	}//

	public void replaceASCII(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
			throws BadLocationException {
		if (!isTextPrintable(text)) {
			return; // ASCII will only accept printable characters
		}// if - need printable chars
		int asciiKind = asciiKinds[elementPosition];
		switch (asciiKind) {
		case BLANK1:
			text = SPACE;
			break;
		case BLANK2:
			text = SPACE + SPACE;
			length = 2;
		case ASCII:
			break;
		case EOL:
			return;
			// break;
		default:
		}// switch
		fb.replace(offset, length, text, attrs);

	}// replaceASCII

	private void moveToNextDataLine(int offset) {
		Element rootElement = doc.getDefaultRootElement();
		row = rootElement.getElementIndex(offset);
		if ((row + 2) >= rootElement.getElementCount()) {
			System.out.printf("%s%n", "row >= rootElement");
		}// if
		Element paragraphElement = rootElement.getElement(row + 1);
		Element dataElement = paragraphElement.getElement(INDEX_DATA);
		int targetPosition = dataElement.getStartOffset();

	}// moveToNextLine

	private boolean isTextHex(String text) {
		return text.matches(PATTERN_HEX);
	}// isTextHex

	private boolean isTextData(String text) {
		return text.matches(PATTERN_HEX_SPACE);
	}// isTextData

	private boolean isTextPrintable(String text) {
		return text.matches(PATTERN_PRINTABLE);
	}// isTextPrintable

	private final static String PATTERN_HEX = "[A-F|a-f|0-9]+";
	private final static String PATTERN_HEX_SPACE = "[A-F|a-f| |0-9]+";
	private static String PATTERN_PRINTABLE = "^([a-zA-Z0-9!@#$%^&amp;*()-_=+;:'&quot;|~`&lt;&gt;?/{}]{1,1})$";

	private final static String SPACE = " ";
	private final static String NON_PRINTABLE_CHAR = ".";

	private final static int BLANK1 = 0;
	private final static int HEX1 = 1;
	private final static int HEX2 = 2;
	private final static int HEX3 = 3;
	private final static int HEX4 = 4;
	private final static int BLANK2 = 5;
	private final static int BLANK3 = 6;
	private final static int BLANK4 = 7;

	private final static int ASCII = 8;
	private final static int EOL = 9;

	private int[] dataKinds = new int[] { BLANK1, HEX1, HEX2, BLANK1, HEX1, HEX2, BLANK1, HEX1, HEX2, BLANK1, HEX1,
			HEX2,
			BLANK1, HEX1, HEX2, BLANK1, HEX1, HEX2, BLANK1, HEX1, HEX2, BLANK1, HEX1, HEX3, BLANK2,
			BLANK1, HEX1, HEX2, BLANK1, HEX1, HEX2, BLANK1, HEX1, HEX2, BLANK1, HEX1, HEX2,
			BLANK1, HEX1, HEX2, BLANK1, HEX1, HEX2, BLANK1, HEX1, HEX2, BLANK1, HEX1, HEX4, BLANK4 };

	private int[] asciiKinds = new int[] { BLANK2, BLANK1,
			ASCII, ASCII, ASCII, ASCII, ASCII, ASCII, ASCII, ASCII,
			ASCII, ASCII, ASCII, ASCII, ASCII, ASCII, ASCII, ASCII,
			EOL, EOL };

	private Integer[] dataToASCII = new Integer[] { null, 0, 0, null, 1, 1, null, 2, 2, null, 3, 3,
			null, 4, 4, null, 5, 5, null, 6, 6, null, 7, 7, null,
			null, 8, 8, null, 9, 9, null, 10, 10, null, 11, 11,
			null, 12, 12, null, 13, 13, null, 14, 14, null, 15, 15, null };

	private Integer[] asciiToData = new Integer[] { null, null, 1, 4, 7, 10, 13, 16, 19, 22, 26, 29, 32, 35, 38, 41, 44, 47 };

	private final static int INDEX_ADDRESS = 0;
	private final static int INDEX_DATA = 1;
	private final static int INDEX_ASCII = 2;

}// class TestFilter
