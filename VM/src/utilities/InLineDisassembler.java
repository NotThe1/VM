package utilities;

import hardware.WorkingRegisterSet;

import java.awt.Color;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import java.awt.GridBagConstraints;

import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import memory.Core;

import java.awt.Dimension;

public class InLineDisassembler extends JPanel implements Runnable {
	private static InLineDisassembler instance = new InLineDisassembler();

	private static OpCodeMap opCodeMap = new OpCodeMap();
	private static Core core = Core.getInstance();
	private static WorkingRegisterSet wrs = WorkingRegisterSet.getInstance();
	private static StyledDocument doc;

	private static SimpleAttributeSet[] simpleAttributes;
	private static SimpleAttributeSet[] categoryAttributes;

	private static int priorProgramCounter; // value of previous update PC
	private static boolean newDisplay;
	private static String LINE_SEPARATOR = System.getProperty("line.separator", "\n");
	private JTextPane txtInstructions;

	public static InLineDisassembler getInstance() {
		return instance;
	}// getInstance

	private InLineDisassembler() {
		super();
		initialize();
		appInit();
	}// Constructor

	private void appInit() {
		simpleAttributes = makeSimpleAttributes();
		newDisplay = true;
		doc = txtInstructions.getStyledDocument();
	}// appInit

	/*----------------------------------------------------------------*/
	
	public void run(){
		updateDisplay(wrs.getProgramCounter());
	}//run()
	
	public void updateDisplay(int programCounter) {
		if (newDisplay) {
			try {
				doc.remove(0, doc.getLength());
			} catch (BadLocationException e) {
				JOptionPane.showMessageDialog(null, "Error clearing Disply Document", "UpdateDisplay",
						JOptionPane.ERROR_MESSAGE);
				return; // graceful exit
			} // try clear the contents of doc
			newDisplay = false;
			processCurrentAndFutureLines(programCounter, 0);
			txtInstructions.setCaretPosition(0);
		} else {
			updateTheDisplay(programCounter);
		}// if new display

		priorProgramCounter = programCounter; // remember for next update
		return;
	}// updateDisplay()

	public void processCurrentAndFutureLines(int programCounter, int lineNumber) {
		int workingProgramCounter = programCounter;
		categoryAttributes = makeAttrsForCategory(1); // current line

		for (int i = 0; i < LINES_TO_DISPLAY - lineNumber; i++) {
			workingProgramCounter += insertCode(workingProgramCounter);
			categoryAttributes = makeAttrsForCategory(2); // future lines
		}// for
	}// processCurrentAndFutureLines

	public Document updateTheDisplay(int programCounter) {
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
			int cursorPosition  = doc.getLength();
			processCurrentAndFutureLines(programCounter, lineNumber);
			txtInstructions.setCaretPosition(cursorPosition);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// try
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
		return firstLineLength;
	}// removeFirstLine

	private int insertCode(int workingProgramCounter) {// int thisLineNumber, int workingProgramCounter
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

	/*----------------------------------------------------------------*/
	private SimpleAttributeSet[] makeSimpleAttributes() {
		// make a base style that that has font type & size which is used by all the constructed Styles
		int baseFontSize = 16;
		SimpleAttributeSet baseAttribute = new SimpleAttributeSet();
		StyleConstants.setFontFamily(baseAttribute, "Courier New");
		StyleConstants.setFontSize(baseAttribute, baseFontSize);

		// make 4 simple attributes using the base style while differing only in color
		SimpleAttributeSet[] sas = new SimpleAttributeSet[8]; // hand calculated value - fix it
		sas[ATTR_BLACK] = new SimpleAttributeSet(baseAttribute);
		sas[ATTR_BLUE] = new SimpleAttributeSet(baseAttribute);
		sas[ATTR_GRAY] = new SimpleAttributeSet(baseAttribute);
		sas[ATTR_RED] = new SimpleAttributeSet(baseAttribute);
		StyleConstants.setForeground(sas[ATTR_BLACK], Color.BLACK);
		StyleConstants.setForeground(sas[ATTR_BLUE], Color.BLUE);
		StyleConstants.setForeground(sas[ATTR_GRAY], Color.GRAY);
		StyleConstants.setForeground(sas[ATTR_RED], Color.RED);

		// make a new base style that changes only the font to Bold
		SimpleAttributeSet boldAttribute = new SimpleAttributeSet(baseAttribute);
		StyleConstants.setFontSize(boldAttribute, baseFontSize + 1);
		// StyleConstants.setBold(boldAttribute, true);

		// make 4 simple attributes using the modified base style also only differing in color
		sas[ATTR_BLACK_BOLD] = new SimpleAttributeSet(boldAttribute);
		sas[ATTR_BLUE_BOLD] = new SimpleAttributeSet(boldAttribute);
		sas[ATTR_GRAY_BOLD] = new SimpleAttributeSet(boldAttribute);
		sas[ATTR_RED_BOLD] = new SimpleAttributeSet(boldAttribute);
		StyleConstants.setForeground(sas[ATTR_BLACK_BOLD], Color.BLACK);
		StyleConstants.setForeground(sas[ATTR_BLUE_BOLD], Color.BLUE);
		StyleConstants.setForeground(sas[ATTR_GRAY_BOLD], Color.GRAY);
		StyleConstants.setForeground(sas[ATTR_RED_BOLD], Color.RED);

		StyleConstants.setBackground(sas[ATTR_BLACK_BOLD], Color.yellow);
		StyleConstants.setBackground(sas[ATTR_BLUE_BOLD], Color.yellow);
		StyleConstants.setBackground(sas[ATTR_GRAY_BOLD], Color.yellow);
		StyleConstants.setBackground(sas[ATTR_RED_BOLD], Color.yellow);

		return sas;
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

	/*----------------------------------------------------------------*/
	/**
	 * Create the panel.
	 */
	private void initialize() {

		setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setMinimumSize(new Dimension(600, 20));
		scrollPane.setPreferredSize(new Dimension(600, 100));
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		add(scrollPane, gbc_scrollPane);

		txtInstructions = new JTextPane();
		txtInstructions.setEditable(false);
		scrollPane.setViewportView(txtInstructions);
		JLabel lblNewLabel = new JLabel(
				" Location             OpCode                   Instruction                                               Function\r\n");
		lblNewLabel.setForeground(Color.BLUE);
		scrollPane.setColumnHeaderView(lblNewLabel);

	}// initialize

	/*-------------------CONSTANTS-------------------------------*/
	private final static String COLON = ":";

	private final static int LINES_TO_DISPLAY = 80; // LTD-> Lines To Display
	private final static int LINES_OF_HISTORY = 8; // LTT-> Lines to Trail

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
	/*----------------------------------------------------------------*/

}// class InLineDisassembler
