package utilities;

import java.awt.Color;

import javax.lang.model.element.Element;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import memory.Core;

public class InLineDisassembler {

	private static InLineDisassembler instance = new InLineDisassembler();
	private static OpCodeMap opCodeMap = new OpCodeMap();
	private static Core core = Core.getInstance();
	private static StyledDocument doc = new JTextPane().getStyledDocument();

	private static SimpleAttributeSet[] simpleAttributes;
	private static SimpleAttributeSet[] categoryAttributes;
	private static int currentLine;
	private static int priorProgramCounter; // value of previous update PC
	private static int nextProgramCounter; // value of the location next instruction after update.

	private static boolean newDisplay;
	private static String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

	private InLineDisassembler() {
		simpleAttributes = makeSimpleAttributes();
		currentLine = -1;
		// workingProgramCounter = -1;
		nextProgramCounter = -1;
		newDisplay = true;
	}// Constructor

	public static InLineDisassembler getInstance() {
		return instance;
	}// getInstance
		// -------------------------------------------------

	/**
	 * locates formats and sets attributes for history lines
	 * 
	 * @param programCounter
	 * @return currentLine number;
	 */

	public StyledDocument updateDisplay(int programCounter) {
		if (newDisplay) {
			updateCleanDisplay(programCounter);
			newDisplay = false;
		} else if (programCounter == nextProgramCounter) {
			// updateTheDisplayNextInstruction(programCounter);
			updateTheDisplayAnyInstruction(programCounter);
		} else { // next instruction
			// updateTheDisplay(0XF836);
			updateTheDisplayAnyInstruction(programCounter);
		}// if new display
		priorProgramCounter = programCounter; // remember for next update
		return doc;
	}// updateDisplay()

	public void processCurrentAndFutureLines(int programCounter, int lineNumber) {
		int workingProgramCounter = programCounter;
		categoryAttributes = makeAttrsForCategory(1); // current line
		for (int i = 0; i < LINES_TO_DISPLAY - lineNumber; i++) {
			workingProgramCounter += insertCode(i, workingProgramCounter);
			nextProgramCounter = (i == 0) ? workingProgramCounter : nextProgramCounter;
			categoryAttributes = makeAttrsForCategory(2); // future lines
		}// for

	}// processCurrentAndFutureLines

	public StyledDocument updateCleanDisplay(int programCounter) {
		try {
			doc.remove(0, doc.getLength());
		} catch (BadLocationException e) {
			JOptionPane.showMessageDialog(null, "Error clearing Disply Document", "UpdateDisplay",
					JOptionPane.ERROR_MESSAGE);
			return null; // graceful exit
		} // try clear the contents of doc

		processCurrentAndFutureLines(programCounter, currentLine);
		return doc;
	}// updateCleanDisplay



	public StyledDocument updateTheDisplayAnyInstruction(int programCounter) {
		try {
			StringBuilder sbDoc = new StringBuilder(doc.getText(0, doc.getLength()));
			// find the limit of the history:
			String targetAddressString = String.format("%04X:", priorProgramCounter);
			int start = sbDoc.indexOf(targetAddressString);
			int end = sbDoc.indexOf(LINE_SEPARATOR, start) + LINE_SEPARATOR.length();
			int firstLineLength = 0;
			// do we need to scroll?
			int lineNumber = findLineNumber(sbDoc, start);
			if (lineNumber >= LINES_OF_HISTORY) {
				firstLineLength = removeFirstLine(sbDoc);
				lineNumber--; // decrement the line number
			}// if lineNumber

			// replace all earlier line attributes to ATTR_ADDRESS ( gray)
			categoryAttributes = makeAttrsForCategory(0); // history lines
			String history = sbDoc.substring(0, end - firstLineLength);
			doc.remove(0, doc.getLength());
			doc.insertString(0, history, categoryAttributes[ATTR_ADDRESS]);

			processCurrentAndFutureLines(programCounter, lineNumber);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// try
		byte opCode = core.read(programCounter);
		nextProgramCounter += OpCodeMap.getSize(opCode);
		// need to set nextProgramCounter
		return doc;
	}// updateTheDisplay

	private int findLineNumber(StringBuilder sbDoc, int lineStart) {
		String section = sbDoc.substring(0, lineStart);
		int lineNumber = -1;
		int fromIndex = 0;
		do {
			fromIndex = section.indexOf(COLON, fromIndex + 1);
			// fromIndex += position;
			lineNumber++;
		} while (fromIndex != -1);
		return lineNumber;
	}// findLineNumber

	private int removeFirstLine(StringBuilder sbDoc) {

		int firstLineLength = sbDoc.indexOf(LINE_SEPARATOR, 0) + 2;
		sbDoc.replace(0, firstLineLength, "");
		// try {
		// doc.remove(0, firstLineLength);
		// } catch (BadLocationException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }//

		return firstLineLength;

	}// removeFirstLine

	private int insertCode(int thisLineNumber, int workingProgramCounter) {
		// int workingPosition = thisLineNumber * LINE_WIDTH;
		byte opCode = core.read(workingProgramCounter);
		byte value1 = core.read(workingProgramCounter + 1);
		byte value2 = core.read(workingProgramCounter + 2);
		int opCodeSize = OpCodeMap.getSize(opCode);

		String linePart1 = null;
		String linePart2 = null;
		String linePart3 = null;

		try {
			linePart1 = String.format("%04X%s%4s", workingProgramCounter, COLON, "");
			doc.insertString(doc.getLength(), linePart1, categoryAttributes[ATTR_ADDRESS]);
			switch (opCodeSize) {
			case 1:
				linePart2 = String.format("%02X%8s", opCode, "");
				doc.insertString(doc.getLength(), linePart2, categoryAttributes[ATTR_OPCODE]);
				linePart3 = String.format("%-15s", OpCodeMap.getAssemblerCode(opCode));
				doc.insertString(doc.getLength(), linePart3, categoryAttributes[ATTR_INSTRUCTION]);
				break;
			case 2:
				linePart2 = String.format("%02X%02X%6s", opCode, value1, "");
				doc.insertString(doc.getLength(), linePart2, categoryAttributes[ATTR_OPCODE]);
				linePart3 = String.format("%-15s", OpCodeMap.getAssemblerCode(opCode, value1));
				doc.insertString(doc.getLength(), linePart3, categoryAttributes[ATTR_INSTRUCTION]);
				break;
			case 3:
				linePart2 = String.format("%02X%02X%02X%4s", opCode, value1, value2, "");
				doc.insertString(doc.getLength(), linePart2, categoryAttributes[ATTR_OPCODE]);
				linePart3 = String.format("%-15s", OpCodeMap.getAssemblerCode(opCode, value1, value2));
				doc.insertString(doc.getLength(), linePart3, categoryAttributes[ATTR_INSTRUCTION]);
				break;
			default:

			}// switch opCode Size

			int lineLength = linePart1.length() + linePart2.length() + linePart3.length();
			int extraSpaces = FUNCTION_START - lineLength;
			String xtra = String.format("%" + extraSpaces + "s", "");
			doc.insertString(doc.getLength(), xtra, categoryAttributes[ATTR_ADDRESS]);
			String functionString = String.format("%s%n", OpCodeMap.getFunction(opCode));
			doc.insertString(doc.getLength(), functionString, categoryAttributes[ATTR_ADDRESS]);

		} catch (Exception e) {
			// TODO: handle exception
		}

		return opCodeSize;
	}// insertCode

	// -------------------------------------------------

	private SimpleAttributeSet[] makeSimpleAttributes() {
		// make a base style that that has font type & size which is used by all the constructed Styles
		int baseFontSize = 16;
		SimpleAttributeSet baseAttribute = new SimpleAttributeSet();
		StyleConstants.setFontFamily(baseAttribute, "Courier New");
		StyleConstants.setFontSize(baseAttribute, baseFontSize);

		// make 4 simple attributes using the base style while differing only in color
		SimpleAttributeSet[] a = new SimpleAttributeSet[8]; // hand calculated value - fix it
		a[ATTR_BLACK] = new SimpleAttributeSet(baseAttribute);
		a[ATTR_BLUE] = new SimpleAttributeSet(baseAttribute);
		a[ATTR_GRAY] = new SimpleAttributeSet(baseAttribute);
		a[ATTR_RED] = new SimpleAttributeSet(baseAttribute);
		StyleConstants.setForeground(a[ATTR_BLACK], Color.BLACK);
		StyleConstants.setForeground(a[ATTR_BLUE], Color.BLUE);
		StyleConstants.setForeground(a[ATTR_GRAY], Color.GRAY);
		StyleConstants.setForeground(a[ATTR_RED], Color.RED);

		// make a new base style that changes only the font to Bold
		SimpleAttributeSet boldAttribute = new SimpleAttributeSet(baseAttribute);
		StyleConstants.setFontSize(boldAttribute, baseFontSize + 1);
		// StyleConstants.setBold(boldAttribute, true);

		// make 4 simple attributes using the modified base style also only differing in color
		a[ATTR_BLACK_BOLD] = new SimpleAttributeSet(boldAttribute);
		a[ATTR_BLUE_BOLD] = new SimpleAttributeSet(boldAttribute);
		a[ATTR_GRAY_BOLD] = new SimpleAttributeSet(boldAttribute);
		a[ATTR_RED_BOLD] = new SimpleAttributeSet(boldAttribute);
		StyleConstants.setForeground(a[ATTR_BLACK_BOLD], Color.BLACK);
		StyleConstants.setForeground(a[ATTR_BLUE_BOLD], Color.BLUE);
		StyleConstants.setForeground(a[ATTR_GRAY_BOLD], Color.GRAY);
		StyleConstants.setForeground(a[ATTR_RED_BOLD], Color.RED);

		StyleConstants.setBackground(a[ATTR_BLACK_BOLD], Color.yellow);
		StyleConstants.setBackground(a[ATTR_BLUE_BOLD], Color.yellow);
		StyleConstants.setBackground(a[ATTR_GRAY_BOLD], Color.yellow);
		StyleConstants.setBackground(a[ATTR_RED_BOLD], Color.yellow);

		return a;
	}// SimpleAttributeSet

	private SimpleAttributeSet[] makeAttrsForCategory(int set) {
		SimpleAttributeSet[] afc = new SimpleAttributeSet[4];

		switch (set) {
		case 0: // History
			afc[ATTR_ADDRESS] = simpleAttributes[ATTR_GRAY];
			afc[ATTR_OPCODE] = simpleAttributes[ATTR_GRAY];
			afc[ATTR_INSTRUCTION] = simpleAttributes[ATTR_GRAY];
			afc[ATTR_DESCRIPTION] = simpleAttributes[ATTR_GRAY];
			break;
		case 1: // Present
			afc[ATTR_ADDRESS] = simpleAttributes[ATTR_GRAY_BOLD];
			afc[ATTR_OPCODE] = simpleAttributes[ATTR_RED_BOLD];
			afc[ATTR_INSTRUCTION] = simpleAttributes[ATTR_BLACK_BOLD];
			afc[ATTR_DESCRIPTION] = simpleAttributes[ATTR_BLACK_BOLD];
			break;
		case 2: // Future
			afc[ATTR_ADDRESS] = simpleAttributes[ATTR_GRAY];
			afc[ATTR_OPCODE] = simpleAttributes[ATTR_RED];
			afc[ATTR_INSTRUCTION] = simpleAttributes[ATTR_BLACK];
			afc[ATTR_DESCRIPTION] = simpleAttributes[ATTR_GRAY];
			break;
		default:
			afc[ATTR_ADDRESS] = simpleAttributes[ATTR_GRAY];
			afc[ATTR_OPCODE] = simpleAttributes[ATTR_GRAY];
			afc[ATTR_INSTRUCTION] = simpleAttributes[ATTR_GRAY];
			afc[ATTR_DESCRIPTION] = simpleAttributes[ATTR_GRAY];
		}// switch

		return afc;
	}// makeAttrsForCategory
		// -------------------------------------------------

	private final static String COLON = ":";

	private final static int LINES_TO_DISPLAY = 120; // LTD-> Lines To Display
	private final static int LINES_OF_HISTORY = 7; // LTT-> Lines to Trail

	private final static int LINE_WIDTH = 54; // calculated by hand for now
	private final static int FUNCTION_START = LINE_WIDTH - 15; // calculated by hand for now

	private static final int ATTR_BLACK = 0;
	private static final int ATTR_BLUE = 1;
	private static final int ATTR_GRAY = 2;
	private static final int ATTR_RED = 3;
	private static final int ATTR_BLACK_BOLD = 4;
	private static final int ATTR_BLUE_BOLD = 5;
	private static final int ATTR_GRAY_BOLD = 6;
	private static final int ATTR_RED_BOLD = 7;

	private static final int ATTR_ADDRESS = 0; // LC -> Line category
	private static final int ATTR_OPCODE = 1;
	private static final int ATTR_INSTRUCTION = 2;
	private static final int ATTR_DESCRIPTION = 3;

	private static final int START_ADDR = 0;
	private static final int END_ADDR = START_ADDR + 8;
	private static final int START_HEX = END_ADDR + 1;
	private static final int END_HEX = START_HEX + 9;
	private static final int START_INS = END_HEX + 1;
	private static final int END_INS = START_INS + 19;
	private static final int START_FUNC = END_INS + 1;
	private static final int END_FUNC = LINE_WIDTH + 2;

}// class InLineDisassembler
