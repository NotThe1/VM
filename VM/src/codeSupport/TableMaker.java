package codeSupport;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;

import javax.swing.JFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.prefs.Preferences;
import java.awt.GridBagLayout;

import javax.swing.JMenuBar;
import javax.swing.JLabel;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JList;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JSpinner;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

import javax.swing.border.TitledBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.MatteBorder;

import java.awt.Color;

import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.JTable;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JSeparator;

public class TableMaker {
	private JFrame frmOpcodeTableMaker;
	HashMap<Integer, Instruction> instructions;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TableMaker window = new TableMaker();
					window.frmOpcodeTableMaker.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void argumentTypeChanged(ArgumentType argumentType) {
		// switch (argumentType) {
		// case NONE:
		// case ADDRESS:
		// case D8: // No panels
		// case D16:
		// setRegisterPanels(0);
		// break;
		// case R8: // byte register list
		// cbRegister1.setModel(byteRegisterModel);
		// setRegisterPanels(1);
		// break;
		// case R16: // word register list
		// cbRegister1.setModel(wordRegisterModel);
		// setRegisterPanels(1);
		// break;
		// case R8D8: // byte register
		// cbRegister1.setModel(byteRegisterModel);
		// setRegisterPanels(1);
		// break;
		// case R8R8: // byte register list X 2
		// cbRegister1.setModel(byteRegisterModel);
		// cbRegister2.setModel(byteRegisterModel);
		// setRegisterPanels(2);
		// break;
		// case R16D16: // word register list
		// cbRegister1.setModel(wordRegisterModel);
		// setRegisterPanels(1);
		// break;
		// default:
		// setRegisterPanels(0);
		// }
	}// argumentTypeChanged

	private void setRegisterPanels(int numberOfRegisters) {
		// switch (numberOfRegisters) {
		// case 0:
		// panelRegisters.setVisible(false);
		// panelRegisterOne.setVisible(false);
		// panelRegisterTwo.setVisible(false);
		// break;
		// case 1:
		// panelRegisters.setVisible(true);
		// panelRegisterOne.setVisible(true);
		// panelRegisterTwo.setVisible(false);
		// break;
		// case 2:
		// panelRegisters.setVisible(true);
		// panelRegisterOne.setVisible(true);
		// panelRegisterTwo.setVisible(true);
		// break;
		// default:
		// panelRegisters.setVisible(false);
		// panelRegisterOne.setVisible(false);
		// panelRegisterTwo.setVisible(false);
		// }// switch
	}// setRegisterPanels

	private void setInstructionAttributes(String instruction) {
		// String[] instructionDetails = baseInstructions.get(instruction);
		// lblFlags.setText(instructionDetails[1]);
		// lblDescription.setText(instructionDetails[2]);
	}// setInstructionAttributes

	private void setTableUp(JTable masterTable) {
		Object[] columnNames = { "Hex", "Len", "Size", "Signature", "Arg1",
				"Arg2", "CC affected", "Inst", "Desc", "Func" };

		DefaultTableModel masterModel = (DefaultTableModel) masterTable.getModel();
		masterModel.setColumnIdentifiers(columnNames);
		adjustTableLook(masterTable);
		Instruction ins = new Instruction(00, 1, 1,
				ArgumentSignature.NONE, ArgumentType.NONE, ArgumentType.NONE, CCFlags.NONE,
				Command.NOP, "No Operation", "<-+->");
		addInstructonToTable(ins, masterModel);
		// masterModel.insertRow(0,new Object[] { String.format("%02X", 0),
		// 1,
		// 1,
		// ArgumentSignature.NONE,
		// ArgumentType.NONE,
		// ArgumentType.NONE,
		// CCFlags.NONE,
		// Command.NOP,
		// "No Operation",
		// "..." });
	}

	private void adjustTableLook(JTable table) {
		Font realColumnFont = table.getFont();
		FontMetrics fontMetrics = table.getFontMetrics(realColumnFont);

		int charWidth = fontMetrics.stringWidth("W");

		TableColumnModel tableColumn = table.getColumnModel();
		tableColumn.getColumn(0).setPreferredWidth(charWidth * 4); // Hex
		tableColumn.getColumn(1).setPreferredWidth(charWidth * 3); // Len
		tableColumn.getColumn(2).setPreferredWidth(charWidth * 4); // Size
		tableColumn.getColumn(3).setPreferredWidth(charWidth * 7); // Signature
		tableColumn.getColumn(4).setPreferredWidth(charWidth * 7); // Arg1
		tableColumn.getColumn(5).setPreferredWidth(charWidth * 5); // Arg2
		tableColumn.getColumn(6).setPreferredWidth(charWidth * 7); // CC affected
		tableColumn.getColumn(7).setPreferredWidth(charWidth * 4); // Inst
		tableColumn.getColumn(8).setPreferredWidth(charWidth * 8); // Desc
		tableColumn.getColumn(9).setPreferredWidth(charWidth * 8); // Func

		DefaultTableCellRenderer rightAlign = new DefaultTableCellRenderer();
		rightAlign.setHorizontalAlignment(JLabel.RIGHT);
		tableColumn.getColumn(0).setCellRenderer(rightAlign); // Hex
		tableColumn.getColumn(1).setCellRenderer(rightAlign); // Len
		tableColumn.getColumn(2).setCellRenderer(rightAlign); // Size

		DefaultTableCellRenderer centerAlign = new DefaultTableCellRenderer();
		centerAlign.setHorizontalAlignment(JLabel.CENTER);
		tableColumn.getColumn(3).setCellRenderer(centerAlign); // Signature
		tableColumn.getColumn(4).setCellRenderer(centerAlign); // Arg1
		tableColumn.getColumn(5).setCellRenderer(centerAlign); // Arg2
		tableColumn.getColumn(6).setCellRenderer(centerAlign); // CC affected
		tableColumn.getColumn(7).setCellRenderer(centerAlign); // Inst
		// tableColumn.getColumn(5).setCellRenderer(centerAlign);
	}// adjustTableLook

	private void addInstructonToTable(Instruction instruction, DefaultTableModel masterModel) {
		String hexValue = String.format("%02X", instruction.getOpCode());
		int opCodeLength = instruction.getOpCodeSize();
		int instructionize = instruction.getInstructionSize();
		ArgumentSignature argSignature = instruction.getArgumentSignature();
		ArgumentType arg1 = instruction.getArg1();
		ArgumentType arg2 = instruction.getArg2();

		CCFlags ccAffected = instruction.getCcFlags();
		Command cmd = instruction.getCommand();
		String desc = instruction.getDescription();
		String func = instruction.getFunction();

		masterModel.insertRow(0, new Object[] {
				String.format("%02X", instruction.getOpCode()),
				instruction.getOpCodeSize(),
				instruction.getInstructionSize(),
				instruction.getArgumentSignature(),
				instruction.getArg1(),
				instruction.getArg2(),
				instruction.getCcFlags(),
				instruction.getCommand(),
				instruction.getDescription(),
				instruction.getFunction() });

		masterModel.insertRow(1, new Object[] {
				hexValue,
				opCodeLength,
				instructionize,
				argSignature,
				arg1,
				arg2,
				ccAffected,
				cmd,
				desc,
				func
		});

	}// addInstructonToTable
		// <><><><><><><><><><><><><><><><><><><><><><><><><><><><><>

	private void appClose() {
		Preferences myPrefs = Preferences.userNodeForPackage(TableMaker.class);
		Dimension dim = frmOpcodeTableMaker.getSize();
		myPrefs.putInt("Height", dim.height);
		myPrefs.putInt("Width", dim.width);
		Point point = frmOpcodeTableMaker.getLocation();
		myPrefs.putInt("LocX", point.x);
		myPrefs.putInt("LocY", point.y);
		myPrefs = null;
	}

	@SuppressWarnings("unchecked")
	private void appInit() {
		Preferences myPrefs = Preferences.userNodeForPackage(TableMaker.class);
		frmOpcodeTableMaker.setSize(638, 722);
		frmOpcodeTableMaker.setLocation(myPrefs.getInt("LocX", 100), myPrefs.getInt("LocY", 100));
		myPrefs = null;

		// byteRegisterModel = new DefaultComboBoxModel(byteRegisters);
		// wordRegisterModel = new DefaultComboBoxModel(wordRegisters);

		setRegisterPanels(0);
		// Set<String> insKeys = baseInstructions.keySet();
		// baseInstructionModel = new DefaultComboBoxModel(insKeys.toArray((new String[insKeys.size()])));

		setTableUp(tableMaster);
	}

	/**
	 * Create the application.
	 */
	public TableMaker() {
		initialize();
		appInit();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmOpcodeTableMaker = new JFrame();
		frmOpcodeTableMaker.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				appClose();
			}
		});
		frmOpcodeTableMaker.setTitle("Opcode Table Maker");
		frmOpcodeTableMaker.setBounds(100, 100, 450, 300);
		frmOpcodeTableMaker.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		frmOpcodeTableMaker.getContentPane().setLayout(gridBagLayout);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerSize(10);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		GridBagConstraints gbc_splitPane = new GridBagConstraints();
		gbc_splitPane.insets = new Insets(0, 0, 0, 5);
		gbc_splitPane.fill = GridBagConstraints.BOTH;
		gbc_splitPane.gridx = 0;
		gbc_splitPane.gridy = 0;
		frmOpcodeTableMaker.getContentPane().add(splitPane, gbc_splitPane);

		JPanel panelBottom = new JPanel();
		splitPane.setRightComponent(panelBottom);
		GridBagLayout gbl_panelBottom = new GridBagLayout();
		gbl_panelBottom.columnWidths = new int[] { 0, 0 };
		gbl_panelBottom.rowHeights = new int[] { 0, 0 };
		gbl_panelBottom.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panelBottom.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		panelBottom.setLayout(gbl_panelBottom);

		// table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		// table.setFont(new Font("Tahoma", Font.PLAIN, 14));
		// table.getSelectionModel().addListSelectionListener(new RowListener());
		// DefaultTableModel modelDir = (DefaultTableModel) table.getModel();
		// adjustTableLook(table);
		// scrollPane.setViewportView(table);

		scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		panelBottom.add(scrollPane, gbc_scrollPane);
		scrollPane.setViewportBorder(new LineBorder(Color.BLUE, 4, true));

		tableMaster = new JTable();
		scrollPane.setViewportView(tableMaster);

		JPanel panelTop = new JPanel();
		splitPane.setLeftComponent(panelTop);
		GridBagLayout gbl_panelTop = new GridBagLayout();
		gbl_panelTop.columnWidths = new int[] { 0, 0 };
		gbl_panelTop.rowHeights = new int[] { 0, 80, 0 };
		gbl_panelTop.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panelTop.rowWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		panelTop.setLayout(gbl_panelTop);

		JPanel panelTop1 = new JPanel();
		GridBagConstraints gbc_panelTop1 = new GridBagConstraints();
		gbc_panelTop1.insets = new Insets(0, 0, 5, 0);
		gbc_panelTop1.fill = GridBagConstraints.BOTH;
		gbc_panelTop1.gridx = 0;
		gbc_panelTop1.gridy = 0;
		panelTop.add(panelTop1, gbc_panelTop1);
		GridBagLayout gbl_panelTop1 = new GridBagLayout();
		gbl_panelTop1.columnWidths = new int[] { 0, 0, 0, 0 };
		gbl_panelTop1.rowHeights = new int[] { 0, 0, 0 };
		gbl_panelTop1.columnWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panelTop1.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		panelTop1.setLayout(gbl_panelTop1);

		JPanel panelTop2 = new JPanel();
		GridBagConstraints gbc_panelTop2 = new GridBagConstraints();
		gbc_panelTop2.fill = GridBagConstraints.BOTH;
		gbc_panelTop2.gridx = 0;
		gbc_panelTop2.gridy = 1;
		panelTop.add(panelTop2, gbc_panelTop2);
		GridBagLayout gbl_panelTop2 = new GridBagLayout();
		gbl_panelTop2.columnWidths = new int[] { 0 };
		gbl_panelTop2.rowHeights = new int[] { 0 };
		gbl_panelTop2.columnWeights = new double[] { Double.MIN_VALUE };
		gbl_panelTop2.rowWeights = new double[] { Double.MIN_VALUE };
		panelTop2.setLayout(gbl_panelTop2);
		splitPane.setDividerLocation(250);

		JMenuBar menuBar = new JMenuBar();
		frmOpcodeTableMaker.setJMenuBar(menuBar);

		JMenu mnuFile = new JMenu("File");
		menuBar.add(mnuFile);

		mnuFileNew = new JMenuItem("New ...");
		mnuFileNew.setActionCommand("mnuFileNew");
		mnuFileNew.addActionListener(new menuAdapter());
		mnuFile.add(mnuFileNew);

		JMenuItem mnuFileOpen = new JMenuItem("Open ...");
		mnuFile.add(mnuFileOpen);

		JSeparator separator = new JSeparator();
		mnuFile.add(separator);

		JMenuItem mnuFileSave = new JMenuItem("Save");
		mnuFile.add(mnuFileSave);

		JMenuItem mnuFileSaveAs = new JMenuItem("Save As ...");
		mnuFile.add(mnuFileSaveAs);

		JSeparator separator_1 = new JSeparator();
		mnuFile.add(separator_1);

		JMenuItem mnuFilePrint = new JMenuItem("Print ...");
		mnuFile.add(mnuFilePrint);

		JSeparator separator_2 = new JSeparator();
		mnuFile.add(separator_2);

		JMenuItem mnuFileClose = new JMenuItem("Close");
		mnuFile.add(mnuFileClose);

		JMenuItem mnuFileExit = new JMenuItem("Exit");
		mnuFileExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				appClose();
			}
		});
		mnuFile.add(mnuFileExit);

		JMenuItem mntmNewMenuItem = new JMenuItem("New menu item");
		mnuFile.add(mntmNewMenuItem);
	}// initialize
		// <><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>

	// <><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>
	// <><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>

	Register[] byteRegisters = new Register[] { Register.A, Register.B, Register.C, Register.D,
			Register.E, Register.H, Register.L };

	Register[] wordRegisters = new Register[] { Register.AF, Register.BC, Register.DE, Register.HL, Register.M,
			Register.SP, Register.PC };
	ArgumentSignature[] argumentSignatures = new ArgumentSignature[] { ArgumentSignature.NONE,
			ArgumentSignature.ADDRESS, ArgumentSignature.D8, ArgumentSignature.D16, ArgumentSignature.R8,
			ArgumentSignature.R8R8, ArgumentSignature.R16, ArgumentSignature.R8D8, ArgumentSignature.R16D16 };

	// -------------------------------
	enum Register {
		// Single Byte Registers
		A, B, C, D, E, H, L,
		// Double Byte Registers
		// used for identification only
		// nothing is stored directly into one of these
		BC, DE, HL, M, SP, AF, PC
	}// enum

	// enum ArgumentType {
	// NONE,
	// ADDRESS,
	// D8,
	// D16,
	// R8,
	// R8R8,
	// R16,
	// R8D8,
	// R16D16,
	// VECTOR
	// }// enum

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private static HashMap<String, String[]> baseInstructions;
	static {
		baseInstructions = new HashMap<String, String[]>();
		baseInstructions.put("STC", new String[] { "STC", "C", "Set Carry" });
		baseInstructions.put("CMC", new String[] { "CMC", "C", "Complement Carry" });
		baseInstructions.put("INR", new String[] { "INR", "Z,S,P,Aux,C", "Increment Register/Memory" });
		baseInstructions.put("DCR", new String[] { "DCR", "Z,S,P,Aux,C", "Deccrement Register/Memory" });
		baseInstructions.put("CMA", new String[] { "CMA", "None", "Complement Acc" });
		baseInstructions.put("DAA", new String[] { "DAA", "Z,S,P,Aux,C", "Decimal Adjust Acc" });
		baseInstructions.put("NOP", new String[] { "NOP", "None", "No Operation" });
		baseInstructions.put("MOV", new String[] { "MOV", "None", "Move" });
		baseInstructions.put("STAX", new String[] { "STAX", "None", "Store Acc" });
		baseInstructions.put("LDAX", new String[] { "LDAX", "None", "Load Acc" });
		baseInstructions.put("ADD", new String[] { "ADD", "Z,S,P,Aux,C", "Add Register/Memory to Acc" });
		baseInstructions.put("ADC", new String[] { "ADC", "Z,S,P,Aux,C", "Add Register/Memory to Acc with Carry" });
		baseInstructions.put("SUB", new String[] { "SUB", "Z,S,P,Aux,C", "Subtract Register/Memory from Acc" });
		baseInstructions.put("SBB", new String[] { "SBB", "Z,S,P,Aux,C",
				"Subtract Register/Memory from Acc with Borrow" });
		baseInstructions.put("ANA", new String[] { "ANA", "Z,S,P,C, Aux*", "Logical AND Register/Memory with Acc" });
		baseInstructions.put("XRA", new String[] { "XRA", "Z,S,P,Aux,C", "Logical XOR Register/Memory with Acc" });
		baseInstructions.put("ORA", new String[] { "ORA", "Z,S,P,C, Aux*", "Logical OR Register/Memory with Acc" });
		baseInstructions.put("CMP", new String[] { "CMP", "Z,S,P,Aux,C", "Compare Register/Memory with Acc" });
		baseInstructions.put("RLC", new String[] { "RLC", "C", "Rotate Left Acc" });
		baseInstructions.put("RRC", new String[] { "RRC", "C", "Rotate Right Acc" });
		baseInstructions.put("RAL", new String[] { "RAL", "C", "Rotate Left Acc Through Carry" });
		baseInstructions.put("RAR", new String[] { "RAR", "C", "Rotate Right Acc Through Carry" });
		baseInstructions.put("PUSH", new String[] { "PUSH", "None", "Push Data Onto Stack" });
		baseInstructions.put("POP", new String[] { "POP", "None/Z,S,P,Aux,C", "Pop Data Off the Stack" });
		baseInstructions.put("DAD", new String[] { "DAD", "C", "Double Add" });
		baseInstructions.put("INX", new String[] { "INX", "None", "Increment register Pair" });
		baseInstructions.put("DCX", new String[] { "DCX", "None", "Decrement register Pair" });
		baseInstructions.put("XCHG", new String[] { "XCHG", "None", "Exchange Registers" });
		baseInstructions.put("XTHL", new String[] { "XTHL", "None", "Exchange Stack" });
		baseInstructions.put("SPHL", new String[] { "SPHL", "None", "Load SP from H and L" });
		baseInstructions.put("LXI", new String[] { "LXI", "None", "Load Register Pair Immediate" });
		baseInstructions.put("MVI", new String[] { "MVI", "None", "Move Immediate Data", "" });
		baseInstructions.put("ADI", new String[] { "ADI", "Z,S,P,Aux,C", "Add Immediate to Acc" });
		baseInstructions.put("ACI", new String[] { "ACI", "Z,S,P,Aux,C", "Add Immediate to Acc With Carry" });
		baseInstructions.put("SUI", new String[] { "SUI", "Z,S,P,Aux,C", "Subtract Immediate to Acc" });
		baseInstructions.put("SBI", new String[] { "SBI", "Z,S,P,Aux,C", "Subtract Immediate to Acc With Borrow" });
		baseInstructions.put("ANI", new String[] { "ANI", "Z,S,P,C", "AND Immediate with Acc" });
		baseInstructions.put("XRI", new String[] { "XRI", "Z,S,P,C", "XOR Immediate with Acc" });
		baseInstructions.put("ORI", new String[] { "ORI", "Z,S,P,C", "OR Immediate with Acc" });
		baseInstructions.put("CPI", new String[] { "CPI", "Z,S,P,Aux,C", "Compare Immediate with Acc" });
		baseInstructions.put("STA", new String[] { "STA", "None", "Store Acc Direct" });
		baseInstructions.put("LDA", new String[] { "LDA", "None", "Load Acc Direct" });
		baseInstructions.put("SHLD", new String[] { "SHLD", "None", "Store H and L Direct" });
		baseInstructions.put("LHLD", new String[] { "LHLD", "None", "Load H and L Direct" });
		baseInstructions.put("PCHL", new String[] { "PCHL", "None", "Load Program Counter from H and L" });
		baseInstructions.put("JMP", new String[] { "JMP", "None", "Jump" });
		baseInstructions.put("JC", new String[] { "JC", "None", "Jump if Carry" });
		baseInstructions.put("JNC", new String[] { "JNC", "None", "Jump if No Carry" });
		baseInstructions.put("JZ", new String[] { "JZ", "None", "Jump if Zero" });
		baseInstructions.put("JNZ", new String[] { "JNZ", "None", "Jump in Not Zero" });
		baseInstructions.put("JM", new String[] { "JM", "None", "Jump if Minus" });
		baseInstructions.put("JP", new String[] { "JP", "None", "Jump if Positive" });
		baseInstructions.put("JPE", new String[] { "JPE", "None", "Jump if Parity Even", "" });
		baseInstructions.put("JPO", new String[] { "JPO", "None", "Jump if Parity Odd" });
		baseInstructions.put("CALL", new String[] { "CALL", "None", "Call" });
		baseInstructions.put("CC", new String[] { "CC", "None", "Call if Carry" });
		baseInstructions.put("CNC", new String[] { "CNC", "None", "Call if No Carry" });
		baseInstructions.put("CZ", new String[] { "CZ", "None", "Call if Zero" });
		baseInstructions.put("CNZ", new String[] { "CNZ", "None", "Call in Not Zero" });
		baseInstructions.put("CM", new String[] { "CM", "None", "Call if Minus" });
		baseInstructions.put("CP", new String[] { "CP", "None", "Call if Positive" });
		baseInstructions.put("CPE", new String[] { "CPE", "None", "Call if Parity Even" });
		baseInstructions.put("CPO", new String[] { "CPO", "None", "Call if Parity Odd" });
		baseInstructions.put("RET", new String[] { "RET", "None", "Return" });
		baseInstructions.put("RC", new String[] { "RC", "None", "Return if Carry" });
		baseInstructions.put("RNC", new String[] { "RNC", "None", "Return if No Carry" });
		baseInstructions.put("RZ", new String[] { "RZ", "None", "Return if Zero" });
		baseInstructions.put("RNZ", new String[] { "RNZ", "None", "Return in Not Zero" });
		baseInstructions.put("RM", new String[] { "RM", "None", "Return if Minus" });
		baseInstructions.put("RP", new String[] { "RP", "None", "Return if Positive" });
		baseInstructions.put("RPE", new String[] { "RPE", "None", "Return if Parity Even" });
		baseInstructions.put("RPO", new String[] { "RPO", "None", "Return if Parity Odd" });
		baseInstructions.put("RST", new String[] { "RST", "None", "Restart" });
		baseInstructions.put("EI", new String[] { "EI", "None", "Enable Interrupts" });
		baseInstructions.put("DI", new String[] { "DI", "None", "Disable Interrupts" });
		baseInstructions.put("IN", new String[] { "IN", "None", "Input" });
		baseInstructions.put("OUT", new String[] { "OUT", "None", "Output" });
		baseInstructions.put("HLT", new String[] { "HLT", "None", "Halt" });

	} // static
		// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private static final String MNU_FILE_NEW = "mnuFileNew";
	private static final String MNU_FILE_OPEN = "mnuFileOpen";
	private static final String MNU_FILE_SAVE = "mnuFileSave";
	private static final String MNU_FILE_SAVE_AS = "mnuFileSaveAs";
	private static final String MNU_FILE_PRINT = "mnuFilePrint";
	private static final String MNU_FILE_CLOSE = "mnuFileClose";
	private static final String MNU_FILE_EXIT = "mnuFileExit";
	private JTable tableMaster;
	private DefaultTableModel masterModel;
	private JScrollPane scrollPane;
	private JMenuItem mnuFileNew;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	class menuAdapter implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ae) {
			String menuItem = ae.getActionCommand();
			switch (menuItem) {
			case MNU_FILE_NEW:
				break;
			case MNU_FILE_OPEN:
				break;
			case MNU_FILE_SAVE:
				break;
			case MNU_FILE_SAVE_AS:
				break;
			case MNU_FILE_PRINT:
				break;
			case MNU_FILE_CLOSE:
				break;
			case MNU_FILE_EXIT:
				break;
			default:
			}
		}// actionPerformed
	}// class menuAdapter
}// class TableMaker

