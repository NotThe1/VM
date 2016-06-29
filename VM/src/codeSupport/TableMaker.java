package codeSupport;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.List;
import java.awt.Point;

import javax.swing.JFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
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
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JSpinner;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.print.PrinterException;

import javax.swing.border.TitledBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.MatteBorder;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.JTable;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JSeparator;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JTextField;
import javax.swing.JButton;

public class TableMaker {
	private JFrame frmOpcodeTableMaker;
	HashMap<Integer, Instruction> instructions;
	DefaultTableModel tableModel;
	String currentInstructionSet = "";
	public MyMenuAdapter menuAdapter;
	boolean isDirty = false;

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

	private void rowUpdate() {
		int opCode = (int) hsOpcode.getValue();
		int opCodeSize = (int) hsOpcodeSize.getValue();
		int instructionSize = (int) hsInstructionSize.getValue();
		ArgumentSignature argumentSignature = (ArgumentSignature) cbArgumentSignature.getSelectedItem();
		ArgumentType arg1 = (ArgumentType) cbArg1.getSelectedItem();
		ArgumentType arg2 = (ArgumentType) cbArg2.getSelectedItem();
		CCFlags flags = (CCFlags) cbFlags.getSelectedItem();
		Command command = (Command) cbCommand.getSelectedItem();
		String desc = txtDescription.getText();
		String func = txtFunction.getText();

		DefaultTableModel masterModel = (DefaultTableModel) tableMaster.getModel();

		Instruction ins = new Instruction(opCode,
				opCodeSize,
				instructionSize,
				argumentSignature,
				arg1,
				arg2,
				flags,
				command,
				desc,
				func);

		addInstructonToTable(ins, masterModel);

	}// rowUpdate

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

	private void setArguments(ArgumentSignature signature) {
		ArgumentType arg1 = ArgumentType.NONE;
		ArgumentType arg2 = ArgumentType.NONE;
		switch (signature) {
		case NONE:
			arg1 = ArgumentType.NONE;
			arg2 = ArgumentType.NONE;
			break;
		case ADDRESS:
			arg1 = ArgumentType.ADDRESS;
			arg2 = ArgumentType.NONE;
			break;
		case D8:
			arg1 = ArgumentType.D8;
			arg2 = ArgumentType.NONE;
			break;
		case D16:
			arg1 = ArgumentType.D16;
			arg2 = ArgumentType.NONE;
			break;
		case R8:
			arg1 = ArgumentType.A;
			arg2 = ArgumentType.NONE;
			break;
		case R16:
			arg1 = ArgumentType.AF;
			arg2 = ArgumentType.NONE;
			break;
		case VECTOR:
			arg1 = ArgumentType.VECTOR;
			arg2 = ArgumentType.NONE;
			break;
		case R8D8:
			arg1 = ArgumentType.A;
			arg2 = ArgumentType.D8;
			break;
		case R8R8:
			arg1 = ArgumentType.A;
			arg2 = ArgumentType.A;
			break;
		case R16D16:
			arg1 = ArgumentType.AF;
			arg2 = ArgumentType.D16;
			break;
		default:
			arg1 = ArgumentType.NONE;
			arg2 = ArgumentType.NONE;
		}// switch
		cbArg1.setSelectedItem(arg1);
		cbArg2.setSelectedItem(arg2);
	}// setRegisterPanels

	private void setInstructionAttributes(String instruction) {
		String[] instructionDetails = baseInstructions.get(instruction);
		cbFlags.setSelectedItem(getFlags(instructionDetails[1]));
		txtDescription.setText(instructionDetails[2]);
	}// setInstructionAttributes

	private CCFlags getFlags(String flags) {
		CCFlags ans = CCFlags.NONE;
		switch (flags) {
		case "NONE":
			ans = CCFlags.NONE;
			break;
		case "C":
			ans = CCFlags.CY;
			break;
		case "Z,S,P,Aux":
			ans = CCFlags.ZSPAC;
			break;
		case "Z,S,P,Aux,C":
			ans = CCFlags.ZSPACCY;
			break;
		case "Z,S,P,C":
			ans = CCFlags.ZSPCY;
			break;
		default:
			ans = CCFlags.NONE;
		}
		return ans;
	}// getFlags

	// private HashMap<Integer, Instruction> newInstructionSet() {
	// HashMap<Integer, Instruction> newInstructionSet = new HashMap<Integer, Instruction>();
	// Instruction instruction;
	// for (int i = 0; i < 256; i++) {
	// instruction = new Instruction(i, 1, 1, ArgumentSignature.NONE,
	// ArgumentType.NONE,
	// ArgumentType.NONE,
	// CCFlags.NONE,
	// Command.NOP,
	// "String Description",
	// "(XYZ)<- Acc");
	// newInstructionSet.put(i, instruction);
	// }// for
	//
	// return newInstructionSet;
	// }// newInstructionSet

	// private void setTableUp(JTable masterTable, DefaultTableModel tableModel) {
	//
	// Object[] columnNames = { "Hex", "Len", "Size", "Signature", "Arg1",
	// "Arg2", "CC affected", "Inst", "Desc", "Func" };
	//
	// tableModel.setColumnIdentifiers(columnNames);
	// setColumnAttributes(masterTable);
	// masterTable.setAutoCreateRowSorter(true);
	//
	// TableRowSorter<TableModel> sorter = new TableRowSorter(tableModel);
	// masterTable.setRowSorter(sorter);
	//
	// }// setTableUp

	private void changeInstructionSet() {

	}

	private HashMap<Integer, Instruction> setInstructionSet(String objectPath) {
		HashMap<Integer, Instruction> setInstructionSet = new HashMap<Integer, Instruction>();

		try (FileInputStream inStream = new FileInputStream(objectPath + FILE_EXT )) {
			ObjectInputStream ois = new ObjectInputStream(inStream);
			setInstructionSet = (HashMap<Integer, Instruction>) ois.readObject();
			currentInstructionSet = objectPath;
			ois.close();
		} catch (IOException | ClassNotFoundException e) {
			currentInstructionSet = ""; // signal its a new set
			System.out.printf("Could not find %s.%n %s", objectPath, e.getMessage());
			Instruction instruction;
			for (int i = 0; i < 256; i++) {
				instruction = new Instruction(i,
						1,
						1,
						ArgumentSignature.NONE,
						ArgumentType.NONE,
						ArgumentType.NONE,
						CCFlags.NONE,
						Command.NOP,
						"Instruction - does not exist",
						"(XYZ) <- Acc");
				setInstructionSet.put(i, instruction);
			}// for
		}// try

		// remember where this set came from
		Preferences myPrefs = Preferences.userNodeForPackage(TableMaker.class);
		myPrefs.put("instructionSet", currentInstructionSet);
		myPrefs = null;
		
		if (currentInstructionSet.equals("")) {
			frmOpcodeTableMaker.setTitle(NEW_SET);
		} else {
			frmOpcodeTableMaker.setTitle(currentInstructionSet);
		}// if
		return setInstructionSet;
	}// getInstructionSet

	private HashMap<Integer, Instruction> modelToInstructionSet(DefaultTableModel tabelModel) {
		HashMap<Integer, Instruction> modelToInstructionSet = new HashMap<Integer, Instruction>();
		Instruction instruction;

		for (int rowNumber = 0; rowNumber < tabelModel.getRowCount(); rowNumber++) {
			int opCode = Integer.parseInt((String) tabelModel.getValueAt(rowNumber, 0), 16);
			instruction = new Instruction(opCode,
					(int) tabelModel.getValueAt(rowNumber, 1),
					(int) tabelModel.getValueAt(rowNumber, 2),
					(ArgumentSignature) tabelModel.getValueAt(rowNumber, 3),
					(ArgumentType) tabelModel.getValueAt(rowNumber, 4),
					(ArgumentType) tabelModel.getValueAt(rowNumber, 5),
					(CCFlags) tabelModel.getValueAt(rowNumber, 6),
					(Command) tabelModel.getValueAt(rowNumber, 7),
					(String) tabelModel.getValueAt(rowNumber, 8),
					(String) tabelModel.getValueAt(rowNumber, 9)
					);

			// instruction.setOpCode(opCode);
			// instruction.setOpCodeSize((int) tabelModel.getValueAt(rowNumber, 1));
			// instruction.setInstructionSize((int) tabelModel.getValueAt(rowNumber, 2));
			// instruction.setArgumentSignature((ArgumentSignature) tabelModel.getValueAt(rowNumber, 3));
			// instruction.setArg1((ArgumentType) tabelModel.getValueAt(rowNumber, 4));
			// instruction.setArg2((ArgumentType) tabelModel.getValueAt(rowNumber, 5));
			// instruction.setCcFlags((CCFlags) tabelModel.getValueAt(rowNumber, 6));
			// instruction.setCommand((Command) tabelModel.getValueAt(rowNumber, 7));
			// instruction.setDescription((String) tabelModel.getValueAt(rowNumber, 8));
			// instruction.setFunction((String) tabelModel.getValueAt(rowNumber, 9));

		modelToInstructionSet.put(opCode, instruction);
		}// for
		return modelToInstructionSet;

	}// modelToInstructionSet

	private void setUpTableModel(JTable tableMaster) {
		tableModel = (DefaultTableModel) tableMaster.getModel();
		// set columns
		Object[] columnNames = { "Hex", "Len", "Size", "Signature", "Arg1",
				"Arg2", "CC affected", "Inst", "Desc", "Func" };
		tableModel.setColumnIdentifiers(columnNames);
		setColumnAttributes(tableMaster);
		// set sortcapabilities
		tableMaster.setAutoCreateRowSorter(true);
		TableRowSorter<TableModel> sorter = new TableRowSorter(tableModel);
		tableMaster.setRowSorter(sorter);
		return;
	}// setUpTableModel

	private void loadTableModel(HashMap<Integer, Instruction> instructionSet, JTable tableMaster) {
		DefaultTableModel dtm = (DefaultTableModel) tableMaster.getModel();
		dtm.setRowCount(0); // truncate table

		Instruction instruction;
		Object[] rowData;
		for (int i = 0; i < 256; i++) {
			instruction = instructionSet.get(i);
			rowData = new Object[] {
					(String) String.format("%02X", i),
					(int) instruction.getOpCodeSize(),
					(int) instruction.getInstructionSize(),
					(ArgumentSignature) instruction.getArgumentSignature(),
					(ArgumentType) instruction.getArg1(),
					(ArgumentType) instruction.getArg2(),
					(CCFlags) instruction.getCcFlags(),
					(Command) instruction.getCommand(),
					(String) instruction.getDescription(),
					(String) instruction.getFunction()
			};
			dtm.insertRow(i, rowData);
		}// for
		isDirty = false; // no edit yet
		return;
	}// loadTableModel

	private void setColumnAttributes(JTable table) {
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
		String hexValueStr = String.format("%02X", instruction.getOpCode());
		int hexValue = instruction.getOpCode();
		int opCodeLength = instruction.getOpCodeSize();
		int instructionize = instruction.getInstructionSize();
		ArgumentSignature argSignature = instruction.getArgumentSignature();
		ArgumentType arg1 = instruction.getArg1();
		ArgumentType arg2 = instruction.getArg2();

		CCFlags ccAffected = instruction.getCcFlags();
		Command cmd = instruction.getCommand();
		String desc = instruction.getDescription();
		String func = instruction.getFunction();

		masterModel.insertRow(hexValue, new Object[] {
				hexValueStr,
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
		masterModel.removeRow(hexValue + 1);

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

		myPrefs.put("instructionSet", currentInstructionSet);
		myPrefs = null;
	}

	private void appInit() {
		// manage preferences
		Preferences myPrefs = Preferences.userNodeForPackage(TableMaker.class);

		frmOpcodeTableMaker.setSize(myPrefs.getInt("Width", 650), myPrefs.getInt("Height", 722));
		frmOpcodeTableMaker.setLocation(myPrefs.getInt("LocX", 100), myPrefs.getInt("LocY", 100));
		currentInstructionSet = myPrefs.get("instructionSet", "");
		myPrefs = null;

		// manage Spinners
		SpinnerNumberModel smn = (SpinnerNumberModel) hsOpcode.getModel();
		smn.setMaximum(0XFF);
		SpinnerNumberModel is = (SpinnerNumberModel) hsInstructionSize.getModel();
		is.setMinimum(0X01);
		is.setMaximum(0X03);
		is.setValue(0X01);
		SpinnerNumberModel os = (SpinnerNumberModel) hsOpcodeSize.getModel();
		os.setMinimum(0X01);
		os.setMaximum(0X03);
		os.setValue(0X01);
		hsOpcodeSize.setEnabled(false);

		// manage ComboBoxes
		DefaultComboBoxModel<ArgumentSignature> argumentSignatureModel = new DefaultComboBoxModel<ArgumentSignature>(
				ArgumentSignature.values());
		cbArgumentSignature.setModel(argumentSignatureModel);

		DefaultComboBoxModel<ArgumentType> arg1Model = new DefaultComboBoxModel<ArgumentType>(ArgumentType.values());
		cbArg1.setModel(arg1Model);
		DefaultComboBoxModel<ArgumentType> arg2Model = new DefaultComboBoxModel<ArgumentType>(ArgumentType.values());
		cbArg2.setModel(arg2Model);

		DefaultComboBoxModel<Command> commandModel = new DefaultComboBoxModel<Command>(Command.values());
		cbCommand.setModel(commandModel);

		DefaultComboBoxModel<CCFlags> flagsModel = new DefaultComboBoxModel<CCFlags>(CCFlags.values());
		cbFlags.setModel(flagsModel);

		// Manage menus
		menuAdapter = new MyMenuAdapter();

		// set the table's column info etc.
		setUpTableModel(tableMaster);
		// Get the Instruction set
		// HashMap<Integer, Instruction> instructionSet = setInstructionSet(currentInstructionSet);
		// put it into the table's model
		loadTableModel(setInstructionSet(currentInstructionSet), tableMaster);



	}// appInit

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
		splitPane.setEnabled(false);
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
		panelTop.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		splitPane.setLeftComponent(panelTop);
		GridBagLayout gbl_panelTop = new GridBagLayout();
		gbl_panelTop.columnWidths = new int[] { 0, 0 };
		gbl_panelTop.rowHeights = new int[] { 0, 20, 0, 0, 0 };
		gbl_panelTop.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panelTop.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panelTop.setLayout(gbl_panelTop);

		JPanel panelTop1 = new JPanel();
		panelTop1.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Opcode",
				TitledBorder.LEFT, TitledBorder.ABOVE_TOP, null, new Color(0, 0, 0)));
		GridBagConstraints gbc_panelTop1 = new GridBagConstraints();
		gbc_panelTop1.insets = new Insets(0, 0, 5, 0);
		gbc_panelTop1.fill = GridBagConstraints.VERTICAL;
		gbc_panelTop1.gridx = 0;
		gbc_panelTop1.gridy = 0;
		panelTop.add(panelTop1, gbc_panelTop1);
		GridBagLayout gbl_panelTop1 = new GridBagLayout();
		gbl_panelTop1.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panelTop1.rowHeights = new int[] { 0, 0 };
		gbl_panelTop1.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panelTop1.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panelTop1.setLayout(gbl_panelTop1);

		JLabel lblHexValue = new JLabel("Hex Value :");
		GridBagConstraints gbc_lblHexValue = new GridBagConstraints();
		gbc_lblHexValue.insets = new Insets(0, 0, 0, 5);
		gbc_lblHexValue.gridx = 0;
		gbc_lblHexValue.gridy = 0;
		panelTop1.add(lblHexValue, gbc_lblHexValue);

		hsOpcode = new HexSpinner();
		hsOpcode.setMinimumSize(new Dimension(30, 20));
		hsOpcode.setPreferredSize(new Dimension(50, 20));
		GridBagConstraints gbc_hsOpCode = new GridBagConstraints();
		gbc_hsOpCode.insets = new Insets(0, 0, 0, 5);
		gbc_hsOpCode.gridx = 1;
		gbc_hsOpCode.gridy = 0;
		panelTop1.add(hsOpcode, gbc_hsOpCode);

		JLabel lblInstructionSize = new JLabel("Instruction Size :");
		GridBagConstraints gbc_lblInstructionSize = new GridBagConstraints();
		gbc_lblInstructionSize.insets = new Insets(0, 0, 0, 5);
		gbc_lblInstructionSize.gridx = 2;
		gbc_lblInstructionSize.gridy = 0;
		panelTop1.add(lblInstructionSize, gbc_lblInstructionSize);

		hsInstructionSize = new HexSpinner();
		hsInstructionSize.setPreferredSize(new Dimension(40, 20));
		hsInstructionSize.setMinimumSize(new Dimension(40, 20));
		GridBagConstraints gbc_hsInstructionSize = new GridBagConstraints();
		gbc_hsInstructionSize.insets = new Insets(0, 0, 0, 5);
		gbc_hsInstructionSize.gridx = 3;
		gbc_hsInstructionSize.gridy = 0;
		panelTop1.add(hsInstructionSize, gbc_hsInstructionSize);

		JLabel lblOpcodeSize = new JLabel("Opcode size");
		GridBagConstraints gbc_lblOpcodeSize = new GridBagConstraints();
		gbc_lblOpcodeSize.insets = new Insets(0, 0, 0, 5);
		gbc_lblOpcodeSize.gridx = 5;
		gbc_lblOpcodeSize.gridy = 0;
		panelTop1.add(lblOpcodeSize, gbc_lblOpcodeSize);

		hsOpcodeSize = new HexSpinner();
		hsOpcodeSize.setMinimumSize(new Dimension(40, 20));
		hsOpcodeSize.setPreferredSize(new Dimension(40, 20));
		GridBagConstraints gbc_hsOpcodeSize = new GridBagConstraints();
		gbc_hsOpcodeSize.gridx = 6;
		gbc_hsOpcodeSize.gridy = 0;
		panelTop1.add(hsOpcodeSize, gbc_hsOpcodeSize);

		panelTop2 = new JPanel();
		panelTop2.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Arguments",
				TitledBorder.LEADING, TitledBorder.ABOVE_TOP, null, new Color(0, 0, 0)));
		GridBagConstraints gbc_panelTop2 = new GridBagConstraints();
		gbc_panelTop2.insets = new Insets(0, 0, 5, 0);
		gbc_panelTop2.fill = GridBagConstraints.VERTICAL;
		gbc_panelTop2.gridx = 0;
		gbc_panelTop2.gridy = 1;
		panelTop.add(panelTop2, gbc_panelTop2);
		GridBagLayout gbl_panelTop2 = new GridBagLayout();
		gbl_panelTop2.columnWidths = new int[] { 0, 0, 0, 0, 0 };
		gbl_panelTop2.rowHeights = new int[] { 0, 0 };
		gbl_panelTop2.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panelTop2.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panelTop2.setLayout(gbl_panelTop2);

		JLabel lblArgumentSignature = new JLabel("Argument Signature :");
		GridBagConstraints gbc_lblArgumentSignature = new GridBagConstraints();
		gbc_lblArgumentSignature.insets = new Insets(0, 0, 0, 5);
		gbc_lblArgumentSignature.anchor = GridBagConstraints.EAST;
		gbc_lblArgumentSignature.gridx = 0;
		gbc_lblArgumentSignature.gridy = 0;
		panelTop2.add(lblArgumentSignature, gbc_lblArgumentSignature);

		cbArgumentSignature = new JComboBox<ArgumentSignature>();
		cbArgumentSignature.addActionListener(new comboAdapter());
		cbArgumentSignature.setActionCommand(CB_ARG_SIGNATURE);
		cbArgumentSignature.setMaximumRowCount(11);
		cbArgumentSignature.setPreferredSize(new Dimension(90, 20));
		cbArgumentSignature.setMinimumSize(new Dimension(90, 20));
		GridBagConstraints gbc_cbArgumentSignature = new GridBagConstraints();
		gbc_cbArgumentSignature.insets = new Insets(0, 0, 0, 5);
		gbc_cbArgumentSignature.fill = GridBagConstraints.HORIZONTAL;
		gbc_cbArgumentSignature.gridx = 1;
		gbc_cbArgumentSignature.gridy = 0;
		panelTop2.add(cbArgumentSignature, gbc_cbArgumentSignature);

		panelArg1 = new JPanel();
		GridBagConstraints gbc_panelArg1 = new GridBagConstraints();
		gbc_panelArg1.insets = new Insets(0, 0, 0, 5);
		gbc_panelArg1.fill = GridBagConstraints.BOTH;
		gbc_panelArg1.gridx = 2;
		gbc_panelArg1.gridy = 0;
		panelTop2.add(panelArg1, gbc_panelArg1);
		GridBagLayout gbl_panelArg1 = new GridBagLayout();
		gbl_panelArg1.columnWidths = new int[] { 0, 0, 0 };
		gbl_panelArg1.rowHeights = new int[] { 0, 0 };
		gbl_panelArg1.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl_panelArg1.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panelArg1.setLayout(gbl_panelArg1);

		JLabel lblArgument = new JLabel("Argument 1 :");
		GridBagConstraints gbc_lblArgument = new GridBagConstraints();
		gbc_lblArgument.insets = new Insets(0, 0, 0, 5);
		gbc_lblArgument.anchor = GridBagConstraints.EAST;
		gbc_lblArgument.gridx = 0;
		gbc_lblArgument.gridy = 0;
		panelArg1.add(lblArgument, gbc_lblArgument);

		cbArg1 = new JComboBox<ArgumentType>();
		cbArg1.setMinimumSize(new Dimension(90, 20));
		cbArg1.setPreferredSize(new Dimension(90, 20));
		GridBagConstraints gbc_cbArg1 = new GridBagConstraints();
		gbc_cbArg1.fill = GridBagConstraints.HORIZONTAL;
		gbc_cbArg1.gridx = 1;
		gbc_cbArg1.gridy = 0;
		panelArg1.add(cbArg1, gbc_cbArg1);

		panelArg2 = new JPanel();
		GridBagConstraints gbc_panelArg2 = new GridBagConstraints();
		gbc_panelArg2.fill = GridBagConstraints.BOTH;
		gbc_panelArg2.gridx = 3;
		gbc_panelArg2.gridy = 0;
		panelTop2.add(panelArg2, gbc_panelArg2);
		GridBagLayout gbl_panelArg2 = new GridBagLayout();
		gbl_panelArg2.columnWidths = new int[] { 0, 0, 0 };
		gbl_panelArg2.rowHeights = new int[] { 0, 0 };
		gbl_panelArg2.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl_panelArg2.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panelArg2.setLayout(gbl_panelArg2);

		JLabel lblArgument_1 = new JLabel("Argument 2 :");
		GridBagConstraints gbc_lblArgument_1 = new GridBagConstraints();
		gbc_lblArgument_1.insets = new Insets(0, 0, 0, 5);
		gbc_lblArgument_1.anchor = GridBagConstraints.EAST;
		gbc_lblArgument_1.gridx = 0;
		gbc_lblArgument_1.gridy = 0;
		panelArg2.add(lblArgument_1, gbc_lblArgument_1);

		cbArg2 = new JComboBox<ArgumentType>();
		cbArg2.setPreferredSize(new Dimension(90, 20));
		cbArg2.setMinimumSize(new Dimension(90, 20));
		GridBagConstraints gbc_cbArg2 = new GridBagConstraints();
		gbc_cbArg2.fill = GridBagConstraints.HORIZONTAL;
		gbc_cbArg2.gridx = 1;
		gbc_cbArg2.gridy = 0;
		panelArg2.add(cbArg2, gbc_cbArg2);

		JPanel panelTop3 = new JPanel();
		panelTop3.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Instruction Details",
				TitledBorder.LEADING, TitledBorder.ABOVE_TOP, null, null));
		GridBagConstraints gbc_panelTop3 = new GridBagConstraints();
		gbc_panelTop3.insets = new Insets(0, 0, 5, 0);
		gbc_panelTop3.fill = GridBagConstraints.VERTICAL;
		gbc_panelTop3.gridx = 0;
		gbc_panelTop3.gridy = 2;
		panelTop.add(panelTop3, gbc_panelTop3);
		GridBagLayout gbl_panelTop3 = new GridBagLayout();
		gbl_panelTop3.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		gbl_panelTop3.rowHeights = new int[] { 0, 0, 0, 0 };
		gbl_panelTop3.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panelTop3.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panelTop3.setLayout(gbl_panelTop3);

		JLabel lblCommand = new JLabel("Command :");
		GridBagConstraints gbc_lblCommand = new GridBagConstraints();
		gbc_lblCommand.insets = new Insets(0, 0, 5, 5);
		gbc_lblCommand.anchor = GridBagConstraints.EAST;
		gbc_lblCommand.gridx = 0;
		gbc_lblCommand.gridy = 0;
		panelTop3.add(lblCommand, gbc_lblCommand);

		cbCommand = new JComboBox<Command>();
		cbCommand.setActionCommand(CB_COMMAND);
		cbCommand.addActionListener(new comboAdapter());
		cbCommand.setPreferredSize(new Dimension(70, 20));
		cbCommand.setMinimumSize(new Dimension(70, 20));
		GridBagConstraints gbc_cbCommand = new GridBagConstraints();
		gbc_cbCommand.insets = new Insets(0, 0, 5, 5);
		gbc_cbCommand.fill = GridBagConstraints.HORIZONTAL;
		gbc_cbCommand.gridx = 1;
		gbc_cbCommand.gridy = 0;
		panelTop3.add(cbCommand, gbc_cbCommand);

		JLabel lblFlagsAffected = new JLabel("FLags Affected :");
		GridBagConstraints gbc_lblFlagsAffected = new GridBagConstraints();
		gbc_lblFlagsAffected.insets = new Insets(0, 0, 5, 5);
		gbc_lblFlagsAffected.anchor = GridBagConstraints.EAST;
		gbc_lblFlagsAffected.gridx = 3;
		gbc_lblFlagsAffected.gridy = 0;
		panelTop3.add(lblFlagsAffected, gbc_lblFlagsAffected);

		cbFlags = new JComboBox<CCFlags>();
		cbFlags.setMinimumSize(new Dimension(80, 20));
		cbFlags.setPreferredSize(new Dimension(80, 20));
		GridBagConstraints gbc_cbFlags = new GridBagConstraints();
		gbc_cbFlags.insets = new Insets(0, 0, 5, 5);
		gbc_cbFlags.fill = GridBagConstraints.HORIZONTAL;
		gbc_cbFlags.gridx = 4;
		gbc_cbFlags.gridy = 0;
		panelTop3.add(cbFlags, gbc_cbFlags);

		JLabel lblDescription = new JLabel("Description :");
		GridBagConstraints gbc_lblDescription = new GridBagConstraints();
		gbc_lblDescription.anchor = GridBagConstraints.EAST;
		gbc_lblDescription.insets = new Insets(0, 0, 5, 5);
		gbc_lblDescription.gridx = 0;
		gbc_lblDescription.gridy = 1;
		panelTop3.add(lblDescription, gbc_lblDescription);

		txtDescription = new JTextField();
		GridBagConstraints gbc_txtDescription = new GridBagConstraints();
		gbc_txtDescription.gridwidth = 5;
		gbc_txtDescription.insets = new Insets(0, 0, 5, 5);
		gbc_txtDescription.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtDescription.gridx = 1;
		gbc_txtDescription.gridy = 1;
		panelTop3.add(txtDescription, gbc_txtDescription);
		txtDescription.setColumns(40);

		JLabel lblFunction = new JLabel("Function :");
		GridBagConstraints gbc_lblFunction = new GridBagConstraints();
		gbc_lblFunction.anchor = GridBagConstraints.EAST;
		gbc_lblFunction.insets = new Insets(0, 0, 0, 5);
		gbc_lblFunction.gridx = 0;
		gbc_lblFunction.gridy = 2;
		panelTop3.add(lblFunction, gbc_lblFunction);

		txtFunction = new JTextField();
		txtFunction.setColumns(40);
		GridBagConstraints gbc_txtFunction = new GridBagConstraints();
		gbc_txtFunction.gridwidth = 5;
		gbc_txtFunction.insets = new Insets(0, 0, 0, 5);
		gbc_txtFunction.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtFunction.gridx = 1;
		gbc_txtFunction.gridy = 2;
		panelTop3.add(txtFunction, gbc_txtFunction);

		JPanel panelButtons = new JPanel();
		GridBagConstraints gbc_panelButtons = new GridBagConstraints();
		gbc_panelButtons.fill = GridBagConstraints.BOTH;
		gbc_panelButtons.gridx = 0;
		gbc_panelButtons.gridy = 3;
		panelTop.add(panelButtons, gbc_panelButtons);
		GridBagLayout gbl_panelButtons = new GridBagLayout();
		gbl_panelButtons.columnWidths = new int[] { 0, 0, 0 };
		gbl_panelButtons.rowHeights = new int[] { 0, 0 };
		gbl_panelButtons.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl_panelButtons.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panelButtons.setLayout(gbl_panelButtons);

		btnUpdate = new JButton("Update");
		btnUpdate.addActionListener(new buttonAdapter());
		btnUpdate.setActionCommand(BTN_UPDATE);
		GridBagConstraints gbc_btnUpdate = new GridBagConstraints();
		gbc_btnUpdate.insets = new Insets(0, 0, 0, 5);
		gbc_btnUpdate.gridx = 0;
		gbc_btnUpdate.gridy = 0;
		panelButtons.add(btnUpdate, gbc_btnUpdate);

		btnReset = new JButton("Reset");
		btnReset.addActionListener(new buttonAdapter());
		btnReset.setActionCommand(BTN_RESET);
		GridBagConstraints gbc_btnReset = new GridBagConstraints();
		gbc_btnReset.gridx = 1;
		gbc_btnReset.gridy = 0;
		panelButtons.add(btnReset, gbc_btnReset);
		splitPane.setDividerLocation(235);

		JMenuBar menuBar = new JMenuBar();
		frmOpcodeTableMaker.setJMenuBar(menuBar);

		JMenu mnuFile = new JMenu("File");
		menuBar.add(mnuFile);

		mnuFileNew = new JMenuItem("New ...");
		// mnuFileNew.setActionCommand("mnuFileNew");
		mnuFileNew.addActionListener(new MyMenuAdapter());
		mnuFileNew.setActionCommand(MNU_FILE_NEW);
		mnuFile.add(mnuFileNew);

		JMenuItem mnuFileOpen = new JMenuItem("Open ...");
		mnuFileOpen.setActionCommand(MNU_FILE_OPEN);
		mnuFileOpen.addActionListener(new MyMenuAdapter());
		mnuFile.add(mnuFileOpen);

		JSeparator separator = new JSeparator();
		mnuFile.add(separator);

		JMenuItem mnuFileSave = new JMenuItem("Save");
		mnuFileSave.setActionCommand(MNU_FILE_SAVE);
		mnuFileSave.addActionListener(new MyMenuAdapter());

		mnuFile.add(mnuFileSave);

		JMenuItem mnuFileSaveAs = new JMenuItem("Save As ...");
		mnuFileSaveAs.setActionCommand(MNU_FILE_SAVE_AS);
		mnuFileSaveAs.addActionListener(new MyMenuAdapter());
		mnuFile.add(mnuFileSaveAs);

		JSeparator separator_1 = new JSeparator();
		mnuFile.add(separator_1);

		JMenuItem mnuFilePrint = new JMenuItem("Print ...");
		mnuFilePrint.addActionListener(new MyMenuAdapter());
		mnuFilePrint.setActionCommand(MNU_FILE_PRINT);
		mnuFile.add(mnuFilePrint);

		JSeparator separator_2 = new JSeparator();
		mnuFile.add(separator_2);

		JMenuItem mnuFileClose = new JMenuItem("Close");
		mnuFile.add(mnuFileClose);

		JMenuItem mnuFileExit = new JMenuItem("Exit");
		mnuFileExit.setActionCommand(MNU_FILE_EXIT);
		mnuFileExit.addActionListener(new MyMenuAdapter());
		mnuFile.add(mnuFileExit);
	}// initialize
		// <><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>

	// <><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>
	// <><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>

	// Register[] byteRegisters = new Register[] { Register.A, Register.B, Register.C, Register.D,
	// Register.E, Register.H, Register.L };
	//
	// Register[] wordRegisters = new Register[] { Register.AF, Register.BC, Register.DE, Register.HL, Register.M,
	// Register.SP, Register.PC };
	// ArgumentSignature[] argumentSignatures = new ArgumentSignature[] { ArgumentSignature.NONE,
	// ArgumentSignature.ADDRESS, ArgumentSignature.D8, ArgumentSignature.D16, ArgumentSignature.R8,
	// ArgumentSignature.R8R8, ArgumentSignature.R16, ArgumentSignature.R8D8, ArgumentSignature.R16D16 };

	// -------------------------------
	// enum Register {
	// // Single Byte Registers
	// A, B, C, D, E, H, L,
	// // Double Byte Registers
	// // used for identification only
	// // nothing is stored directly into one of these
	// BC, DE, HL, M, SP, AF, PC
	// }// enum

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
		baseInstructions.put("INR", new String[] { "INR", "Z,S,P,Aux", "Increment Register/Memory" });
		baseInstructions.put("DCR", new String[] { "DCR", "Z,S,P,Aux", "Deccrement Register/Memory" });
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
		baseInstructions.put("ANA", new String[] { "ANA", "Z,S,P,Aux,C", "Logical AND Register/Memory with Acc" });
		baseInstructions.put("XRA", new String[] { "XRA", "Z,S,P,Aux,C", "Logical XOR Register/Memory with Acc" });
		baseInstructions.put("ORA", new String[] { "ORA", "Z,S,P,Aux,C", "Logical OR Register/Memory with Acc" });
		baseInstructions.put("CMP", new String[] { "CMP", "Z,S,P,Aux,C", "Compare Register/Memory with Acc" });
		baseInstructions.put("RLC", new String[] { "RLC", "C", "Rotate Left Acc" });
		baseInstructions.put("RRC", new String[] { "RRC", "C", "Rotate Right Acc" });
		baseInstructions.put("RAL", new String[] { "RAL", "C", "Rotate Left Acc Through Carry" });
		baseInstructions.put("RAR", new String[] { "RAR", "C", "Rotate Right Acc Through Carry" });
		baseInstructions.put("PUSH", new String[] { "PUSH", "None", "Push Data Onto Stack" });
		baseInstructions.put("POP", new String[] { "POP", "None", "Pop Data Off the Stack" });
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

	private static final String CB_ARG_SIGNATURE = "cbArgumentSignature";
	private static final String CB_ARG_1 = "cbArg1";
	private static final String CB_ARG_2 = "cbArg2";
	private static final String CB_FLAGS = "cbFlags";
	private static final String CB_COMMAND = "cbCommand";

	private static final String BTN_UPDATE = "btnUpdate";
	private static final String BTN_RESET = "btnReset";
	private JTable tableMaster;
	// private DefaultTableModel masterModel;
	private JScrollPane scrollPane;
	private JMenuItem mnuFileNew;
	private HexSpinner hsOpcode;
	private JPanel panelTop2;
	private HexSpinner hsInstructionSize;
	private HexSpinner hsOpcodeSize;
	private JComboBox<ArgumentSignature> cbArgumentSignature;
	private JComboBox<ArgumentType> cbArg1;
	private JComboBox<ArgumentType> cbArg2;
	private JComboBox<Command> cbCommand;
	private JComboBox<CCFlags> cbFlags;
	private JPanel panelArg2;
	private JPanel panelArg1;
	private JTextField txtDescription;
	private JTextField txtFunction;
	private JButton btnReset;
	private JButton btnUpdate;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	class buttonAdapter implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ae) {
			String actionCommand = ae.getActionCommand();
			switch (actionCommand) {
			case BTN_UPDATE:
				rowUpdate();
				break;
			case BTN_RESET:
				break;
			default:

			}// switch
		}// actionPerformed
	}// class butonAdapter

	class comboAdapter implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ae) {
			String actionCommand = ae.getActionCommand();
			switch (actionCommand) {
			case CB_ARG_SIGNATURE:
				setArguments((ArgumentSignature) cbArgumentSignature.getSelectedItem());
				break;
			case CB_ARG_1:
				break;
			case CB_ARG_2:
				break;
			case CB_FLAGS:
				break;
			case CB_COMMAND:
				Command cmd = (Command) cbCommand.getSelectedItem();
				setInstructionAttributes(cmd.toString());
				break;
			}// switch
		}// actionPerformed
	}// class comboAdapter

	public class MyMenuAdapter implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ae) {
			String menuItem = ae.getActionCommand();
			switch (menuItem) {
			case MNU_FILE_NEW:
				newFile();
				break;
			case MNU_FILE_OPEN:
				openFile();
				break;
			case MNU_FILE_SAVE:
				if (currentInstructionSet.equals("")) {
					fileSaveAs();
				} else
					fileSave(currentInstructionSet);
				break;
			case MNU_FILE_SAVE_AS:
				fileSaveAs();
				break;
			case MNU_FILE_PRINT:
				try {
					tableMaster.print();
				} catch (PrinterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case MNU_FILE_CLOSE:
				break;
			case MNU_FILE_EXIT:
				appClose();
				System.exit(0);
				break;
			default:
			}
		}// actionPerformed

		private void newFile() {
			currentInstructionSet = "";
			loadTableModel(setInstructionSet(currentInstructionSet), tableMaster);
		}// newFile

		private void openFile() {
			
			currentInstructionSet =getFilePathString(true);
			loadTableModel(setInstructionSet(currentInstructionSet), tableMaster);

		}// openFile

		private void fileSave(String targetSavePath) {
			if (targetSavePath.equals("")){
				System.out.printf(" Nothing saved %n");
			}else{
				
				try(FileOutputStream outStream = new FileOutputStream(targetSavePath + FILE_EXT)) {
					ObjectOutputStream oos = new ObjectOutputStream(outStream);
					oos.writeObject(modelToInstructionSet((DefaultTableModel) tableMaster.getModel()));
					oos.close();
					currentInstructionSet = targetSavePath;
				} catch (IOException e) {
					
					e.printStackTrace();
				}//try
			}//if

		}// fileSave

		private void fileSaveAs() {
			String savedFilePath =  getFilePathString(false);
			fileSave(savedFilePath);
		}// fileSaveAs

		private String getFilePathString(boolean open) {
			String currentPath = currentInstructionSet.equals("") ? "." : currentInstructionSet;
//			Path path = Paths.get(currentPath);
			JFileChooser fileChooser = new JFileChooser(currentPath);
			FileFilter filter = new FileNameExtensionFilter("Instruction file", "dat", "dat");
			fileChooser.addChoosableFileFilter(filter);
			fileChooser.setAcceptAllFileFilterUsed(true);
			int result;
			if (open) {
				result = fileChooser.showOpenDialog(null);
			} else {
				result = fileChooser.showSaveDialog(null);
			}// if

			String getFilePathString = "";
			if (result != JFileChooser.APPROVE_OPTION) {
				System.out.printf("You did not select a file%n");
				getFilePathString = "";
			} else {
				String fileName = fileChooser.getSelectedFile().getAbsolutePath();
				int dotInd = fileName.lastIndexOf('.');
				getFilePathString = (dotInd > 0) ? fileName.substring(0, dotInd) : fileName;
				System.out.printf("You select file %s:%n", getFilePathString);
			}// if

			return getFilePathString;
		}//

	}// class menuAdapter
	
		private final static String FILE_EXT = ".dat";

	private final static String NEW_SET = "<< New Instruction Set>>";
}// class TableMaker

