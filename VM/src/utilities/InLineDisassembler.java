package utilities;

import hardware.WorkingRegisterSet;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.EtchedBorder;
import javax.swing.text.AbstractDocument.BranchElement;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import memory.Core;

import java.awt.Dimension;

public class InLineDisassembler extends JPanel implements Runnable {

	private static final long serialVersionUID = 1L;

	private static InLineDisassembler instance = new InLineDisassembler();

	private static Core core = Core.getInstance();
	private static WorkingRegisterSet wrs = WorkingRegisterSet.getInstance();
	private static StyledDocument doc;

	@SuppressWarnings("unused")
	private static OpCodeMap opCodeMap = new OpCodeMap();

	SimpleAttributeSet historyAttributes;
	SimpleAttributeSet locationAttributes1;
	SimpleAttributeSet opCodeAttributes1;
	SimpleAttributeSet instructionAttributes1;
	SimpleAttributeSet functionAttributes1;

	SimpleAttributeSet boldAttributes;
//	SimpleAttributeSet boldNotAttributes;


	private static Position currentPosition;

	private static int priorProgramCounter; // value of previous update PC
	private static int nextProgramCounter; // value of future update PC if straight line code
	private static int futureProgramerCounter; // instruction +1 from last displayed

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
		newDisplay = true;
		doc = txtInstructions.getStyledDocument();
		priorProgramCounter = 0;
		makeStyles();
	}// appInit

	public StyledDocument getDoc() {
		return doc;
	}// getDoc
	
	public Element getCurrentElement(){
		return doc.getParagraphElement(currentPosition.getOffset());
	}//getCurrentElement

	private void makeStyles() {
		SimpleAttributeSet baseAttributes = new SimpleAttributeSet();
		StyleConstants.setFontFamily(baseAttributes, "Courier New");
		StyleConstants.setFontSize(baseAttributes, 16);
		
		historyAttributes = new SimpleAttributeSet(baseAttributes);
		StyleConstants.setForeground(historyAttributes, Color.GRAY);

		boldAttributes = new SimpleAttributeSet();
		StyleConstants.setBold(boldAttributes, true);

		locationAttributes1 = new SimpleAttributeSet(baseAttributes);
		StyleConstants.setForeground(locationAttributes1, COLOR_LOCATION);

		opCodeAttributes1 = new SimpleAttributeSet(baseAttributes);
		StyleConstants.setForeground(opCodeAttributes1, COLOR_OPCODE);

		instructionAttributes1 = new SimpleAttributeSet(baseAttributes);
		StyleConstants.setForeground(instructionAttributes1, COLOR_INSTRUCTION);

		functionAttributes1 = new SimpleAttributeSet(baseAttributes);
		StyleConstants.setForeground(functionAttributes1, COLOR_FUNCTION);

	}// makeStyles1

	/*----------------------------------------------------------------*/

	public void run() {
		updateDisplay();
	}// run()

	public void updateDisplay() {
		int programCounter = wrs.getProgramCounter();
		if (programCounter == priorProgramCounter) {
			return;
		} else if (programCounter == nextProgramCounter) {
			nextInstruction(programCounter);
		} else if (!newDisplay) {
			// updateTheDisplay(programCounter);
			notNextInstruction(programCounter);
		} else {
			try {
				doc.remove(0, doc.getLength());
				currentPosition = doc.createPosition(0);
				// activePosition = doc.createPosition(0);
			} catch (BadLocationException e) {
				JOptionPane.showMessageDialog(null, "Error clearing Disply Document", "UpdateDisplay",
						JOptionPane.ERROR_MESSAGE);
				return; // graceful exit
			} // try clear the contents of doc
			newDisplay = false;
			// nextProgramCounter = processCurrentLine(programCounter);
			futureProgramerCounter = processFutureLines(programCounter, 0);
			processCurrentLine();
			txtInstructions.setCaretPosition(0);

		}// if new display

		priorProgramCounter = programCounter; // remember for next update
		return;
	}// updateDisplay()

	private void notNextInstruction(int programCounter) {
		processHistoryLine();
		BranchElement rootElement = (BranchElement) doc.getDefaultRootElement();
		int lineNumber = rootElement.getElementIndex(currentPosition.getOffset());
		int removePoint = currentPosition.getOffset() - 1;
		try {
			doc.remove(removePoint, doc.getLength() - removePoint);
			processFutureLines(programCounter, lineNumber);
			currentPosition = doc.createPosition(removePoint + 1);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		processCurrentLine();
	}// notNextInstruction

	private void nextInstruction(int programCounter) {
		processHistoryLine();
		processCurrentLine();
		nextProgramCounter = getNextProgramCounter(programCounter);
		// processCurrentLine(nextProgramCounter,currentLineNumber+1);

	}// nextInstruction

	private void processHistoryLine() {
		Element paragraphElement = doc.getParagraphElement(currentPosition.getOffset());
		int paragraphStart = paragraphElement.getStartOffset();
		int paragraphLength = paragraphElement.getEndOffset() - paragraphStart;
		doc.setCharacterAttributes(paragraphStart, paragraphLength, historyAttributes, true);
		try {
			currentPosition = doc.createPosition(paragraphStart + paragraphLength + 1);
			BranchElement rootElement = (BranchElement) doc.getDefaultRootElement();
			if (rootElement.getElementIndex(currentPosition.getOffset()) > LINES_OF_HISTORY) {
				Element paragraphZero = doc.getParagraphElement(0);
				doc.remove(0, paragraphZero.getEndOffset());
				futureProgramerCounter = processFutureLines(futureProgramerCounter, LINES_TO_DISPLAY);
			}// if need to remove
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// try

		return;
	}// processHistoryLine

	private void processCurrentLine() {
		Element paragraphElement = doc.getParagraphElement(currentPosition.getOffset());
		int paragraphStart = paragraphElement.getStartOffset();
		int paragraphLength = paragraphElement.getEndOffset() - paragraphStart;
		doc.setCharacterAttributes(paragraphStart, paragraphLength, boldAttributes, false);
		return;
	}// processCurrentLine

	private int processFutureLines(int programCounter, int lineNumber) {
		int workingProgramCounter = programCounter;
		workingProgramCounter += insertCode(workingProgramCounter, LINE_FUTURE);
		nextProgramCounter = workingProgramCounter;

		for (int i = 0; i < LINES_TO_DISPLAY - (lineNumber); i++) {
			workingProgramCounter += insertCode(workingProgramCounter, LINE_FUTURE);
		}// for
		return workingProgramCounter; // futureProgramCounter.
	}// processCurrentLine

	private int getNextProgramCounter(int programCounter) {
		byte opCode = core.read(programCounter);
		int opCodeSize = OpCodeMap.getSize(opCode);
		return programCounter + opCodeSize;
	}// getNextProgramCounter

	private int insertCode(int workingProgramCounter, int when) {// int thisLineNumber, int workingProgramCounter
		// int workingPosition = thisLineNumber * LINE_WIDTH;
		byte opCode = core.read(workingProgramCounter);
		byte value1 = core.read(workingProgramCounter + 1);
		byte value2 = core.read(workingProgramCounter + 2);
		int opCodeSize = OpCodeMap.getSize(opCode);

		String locationPart = null;
		String opCodePart = null;
		String instructionPart = null;

		try {
			locationPart = makeLocationPart(workingProgramCounter);
//			doc.insertString(doc.getLength(), locationPart, locationAttributes[when]);
			doc.insertString(doc.getLength(), locationPart, locationAttributes1);
			switch (opCodeSize) {
			case 1:
				opCodePart = String.format("%02X%8s", opCode, "");
				doc.insertString(doc.getLength(), opCodePart, opCodeAttributes1);
				instructionPart = String.format("%-15s", OpCodeMap.getAssemblerCode(opCode));
				doc.insertString(doc.getLength(), instructionPart, instructionAttributes1);
				break;
			case 2:
				opCodePart = String.format("%02X%02X%6s", opCode, value1, "");
				doc.insertString(doc.getLength(), opCodePart, opCodeAttributes1);
				instructionPart = String.format("%-15s", OpCodeMap.getAssemblerCode(opCode, value1));
				doc.insertString(doc.getLength(), instructionPart, instructionAttributes1);
				break;
			case 3:
				opCodePart = String.format("%02X%02X%02X%4s", opCode, value1, value2, "");
				doc.insertString(doc.getLength(), opCodePart, opCodeAttributes1);
				instructionPart = String.format("%-15s", OpCodeMap.getAssemblerCode(opCode, value1, value2));
				doc.insertString(doc.getLength(), instructionPart, instructionAttributes1);
				break;
			default:

			}// switch opCode Size
			String functionPart = String.format("    %s", OpCodeMap.getFunction(opCode) + LINE_SEPARATOR);
			doc.insertString(doc.getLength(), functionPart, functionAttributes1);
			if (when == LINE_CURRENT) {
				currentPosition = doc.createPosition(doc.getLength() - 1); // current line position
			}//

		} catch (Exception e) {
			// TODO: handle exception
		}// try

		return opCodeSize;
	}// insertCode

	private String makeLocationPart(int location) {
		return String.format("%04X%s%4s", location, COLON, "");
	}// makeLocationPart

	/*----------------------------------------------------------------*/
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
	private final Color COLOR_LOCATION = Color.black;
	private final Color COLOR_OPCODE = Color.red;
	private final Color COLOR_INSTRUCTION = Color.blue;
	private final Color COLOR_FUNCTION = Color.green;

	private final static String COLON = ":";

	private final static int LINES_TO_DISPLAY = 64; // LTD-> Lines To Display
	private final static int LINES_OF_HISTORY = 5; // LTT-> Lines to Trail


	private static final int LINE_CURRENT = 1;
	private static final int LINE_FUTURE = 2;

	/*----------------------------------------------------------------*/

}// class InLineDisassembler
