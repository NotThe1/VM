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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
				}//try
			}//run
		});
	}//main

	private void updateEditDisplay(int rowNumber) {
		DefaultTableModel tableModel = (DefaultTableModel) tableMaster.getModel();

		if (tableModel.getRowCount() == 0) {
			return; // During setup
		}// if

		hsOpcode.setValue(Integer.valueOf((String) tableModel.getValueAt(rowNumber, TBL_COL_OPCODE), 16));
		cbCommand.setSelectedItem((Command) tableModel.getValueAt(rowNumber, TBL_COL_INSTRUCTION));
		cbArgumentSignature.setSelectedItem((ArgumentSignature) tableModel.getValueAt(rowNumber, TBL_COL_SIGNATURE));
		cbArg1.setSelectedItem((ArgumentType) tableModel.getValueAt(rowNumber, TBL_COL_ARG1));
		cbArg2.setSelectedItem((ArgumentType) tableModel.getValueAt(rowNumber, TBL_COL_ARG2));
		cbFlags.setSelectedItem((CCFlags) tableModel.getValueAt(rowNumber, TBL_COL_FLAGS));
		txtDescription.setText((String) tableModel.getValueAt(rowNumber, TBL_COL_DESC));
		txtFunction.setText((String) tableModel.getValueAt(rowNumber, TBL_COL_FUNC));
		hsInstructionSize.setValue((int) tableModel.getValueAt(rowNumber, TBL_COL_INS_SIZE));
		hsOpcodeSize.setValue((int) tableModel.getValueAt(rowNumber, TBL_COL_OPCODE_SIZE));

		tableMaster.setRowSelectionInterval(rowNumber, rowNumber);
	}// updateEditDisplay

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

	private HashMap<Integer, Instruction> setInstructionSet(String objectPath) {
		HashMap<Integer, Instruction> setInstructionSet = new HashMap<Integer, Instruction>();

		try (FileInputStream inStream = new FileInputStream(objectPath + FILE_EXT)) {
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

					(int) tabelModel.getValueAt(rowNumber, TBL_COL_OPCODE_SIZE),
					(int) tabelModel.getValueAt(rowNumber, TBL_COL_INS_SIZE),
					(ArgumentSignature) tabelModel.getValueAt(rowNumber, TBL_COL_SIGNATURE),
					(ArgumentType) tabelModel.getValueAt(rowNumber, TBL_COL_ARG1),
					(ArgumentType) tabelModel.getValueAt(rowNumber, TBL_COL_ARG2),
					(CCFlags) tabelModel.getValueAt(rowNumber, TBL_COL_FLAGS),
					(Command) tabelModel.getValueAt(rowNumber, TBL_COL_INSTRUCTION),
					(String) tabelModel.getValueAt(rowNumber, TBL_COL_DESC),
					(String) tabelModel.getValueAt(rowNumber, TBL_COL_FUNC)
					);

			modelToInstructionSet.put(opCode, instruction);
		}// for
		return modelToInstructionSet;

	}// modelToInstructionSet

	private void setUpTableModel(JTable tableMaster) {
		tableModel = (DefaultTableModel) tableMaster.getModel();
		// set columns
		Object[] columnNames = { "opCode", "Inst", "Signature", "Arg1",
				"Arg2", "CC affected", "Description", "Function", "Len", "Size" };
		tableModel.setColumnIdentifiers(columnNames);

		setColumnAttributes(tableMaster);
		// set sort capabilities
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
					(Command) instruction.getCommand(),
					(ArgumentSignature) instruction.getArgumentSignature(),
					(ArgumentType) instruction.getArg1(),
					(ArgumentType) instruction.getArg2(),
					(CCFlags) instruction.getCcFlags(),
					(String) instruction.getDescription(),
					(String) instruction.getFunction(),
					(int) instruction.getInstructionSize(),
					(int) instruction.getOpCodeSize()
			};
			dtm.insertRow(i, rowData);
		}// for
		updateEditDisplay(0);
		isDirty = false; // no edit yet
		return;
	}// loadTableModel

	private void setColumnAttributes(JTable table) {
		Font realColumnFont = table.getFont();
		FontMetrics fontMetrics = table.getFontMetrics(realColumnFont);

		int charWidth = fontMetrics.stringWidth("W");

		TableColumnModel tableColumn = table.getColumnModel();
		tableColumn.getColumn(TBL_COL_OPCODE).setPreferredWidth(charWidth * 6);
		tableColumn.getColumn(TBL_COL_INSTRUCTION).setPreferredWidth(charWidth * 3);
		tableColumn.getColumn(TBL_COL_SIGNATURE).setPreferredWidth(charWidth * 4);
		tableColumn.getColumn(TBL_COL_ARG1).setPreferredWidth(charWidth * 5);
		tableColumn.getColumn(TBL_COL_ARG2).setPreferredWidth(charWidth * 5);
		tableColumn.getColumn(TBL_COL_FLAGS).setPreferredWidth(charWidth * 5);
		tableColumn.getColumn(TBL_COL_DESC).setPreferredWidth(charWidth * 20);
		tableColumn.getColumn(TBL_COL_FUNC).setPreferredWidth(charWidth * 10);
		tableColumn.getColumn(TBL_COL_INS_SIZE).setPreferredWidth(charWidth * 2);
		tableColumn.getColumn(TBL_COL_OPCODE_SIZE).setPreferredWidth(charWidth * 2);

		DefaultTableCellRenderer rightAlign = new DefaultTableCellRenderer();
		rightAlign.setHorizontalAlignment(JLabel.RIGHT);
		tableColumn.getColumn(TBL_COL_INS_SIZE).setCellRenderer(rightAlign);
		tableColumn.getColumn(TBL_COL_OPCODE_SIZE).setCellRenderer(rightAlign);

		DefaultTableCellRenderer centerAlign = new DefaultTableCellRenderer();
		centerAlign.setHorizontalAlignment(JLabel.CENTER);
		tableColumn.getColumn(TBL_COL_OPCODE).setCellRenderer(centerAlign);
		tableColumn.getColumn(TBL_COL_SIGNATURE).setCellRenderer(centerAlign);
		tableColumn.getColumn(TBL_COL_ARG1).setCellRenderer(centerAlign);
		tableColumn.getColumn(TBL_COL_ARG2).setCellRenderer(centerAlign);
		tableColumn.getColumn(TBL_COL_FLAGS).setCellRenderer(centerAlign);

		DefaultTableCellRenderer leftAlign = new DefaultTableCellRenderer();
		leftAlign.setHorizontalAlignment(JLabel.LEFT);
		tableColumn.getColumn(TBL_COL_DESC).setCellRenderer(leftAlign);
		tableColumn.getColumn(TBL_COL_FUNC).setCellRenderer(leftAlign);

	}// adjustTableLook

	private void addInstructonToTable(Instruction instruction, DefaultTableModel masterModel) {
		String hexValueStr = String.format("%02X", instruction.getOpCode());
		int hexValue = instruction.getOpCode();
		int opCodeLength = instruction.getOpCodeSize();
		int instructionSize = instruction.getInstructionSize();
		ArgumentSignature argSignature = instruction.getArgumentSignature();
		ArgumentType arg1 = instruction.getArg1();
		ArgumentType arg2 = instruction.getArg2();

		CCFlags ccAffected = instruction.getCcFlags();
		Command cmd = instruction.getCommand();
		String desc = instruction.getDescription();
		String func = instruction.getFunction();

		masterModel.insertRow(hexValue, new Object[] {
				hexValueStr,
				cmd,
				argSignature,
				arg1,
				arg2,
				ccAffected,
				desc,
				func,
				instructionSize,
				opCodeLength
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

		scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		panelBottom.add(scrollPane, gbc_scrollPane);
		scrollPane.setViewportBorder(new LineBorder(Color.BLUE, 4, true));

		tableMaster = new JTable();
		tableMaster.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// System.out.println("Just clicked in the table");
				updateEditDisplay(tableMaster.getSelectedRow());
			}// mouseClicked
		});

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
		hsOpcode.addChangeListener((e) -> updateEditDisplay((int) hsOpcode.getValue()));
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

			currentInstructionSet = getFilePathString(true);
			loadTableModel(setInstructionSet(currentInstructionSet), tableMaster);

		}// openFile

		private void fileSave(String targetSavePath) {
			if (targetSavePath.equals("")) {
				System.out.printf(" Nothing saved %n");
			} else {

				try (FileOutputStream outStream = new FileOutputStream(targetSavePath + FILE_EXT)) {
					ObjectOutputStream oos = new ObjectOutputStream(outStream);
					oos.writeObject(modelToInstructionSet((DefaultTableModel) tableMaster.getModel()));
					oos.close();
					currentInstructionSet = targetSavePath;
				} catch (IOException e) {

					e.printStackTrace();
				}// try
			}// if

		}// fileSave

		private void fileSaveAs() {
			String savedFilePath = getFilePathString(false);
			fileSave(savedFilePath);
		}// fileSaveAs

		private String getFilePathString(boolean open) {
			String currentPath = currentInstructionSet.equals("") ? "." : currentInstructionSet;
			// Path path = Paths.get(currentPath);
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

	private static final String MNU_FILE_NEW = "mnuFileNew";
	private static final String MNU_FILE_OPEN = "mnuFileOpen";
	private static final String MNU_FILE_SAVE = "mnuFileSave";
	private static final String MNU_FILE_SAVE_AS = "mnuFileSaveAs";
	private static final String MNU_FILE_PRINT = "mnuFilePrint";
	private static final String MNU_FILE_CLOSE = "mnuFileClose";
	private static final String MNU_FILE_EXIT = "mnuFileExit";

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
	private final static int TBL_COL_OPCODE = 0;
	private final static int TBL_COL_INSTRUCTION = 1;
	private final static int TBL_COL_SIGNATURE = 2;
	private final static int TBL_COL_ARG1 = 3;
	private final static int TBL_COL_ARG2 = 4;
	private final static int TBL_COL_FLAGS = 5;
	private final static int TBL_COL_DESC = 6;
	private final static int TBL_COL_FUNC = 7;
	private final static int TBL_COL_INS_SIZE = 8;
	private final static int TBL_COL_OPCODE_SIZE = 9;

	private final static String FILE_EXT = ".dat";

	private final static String NEW_SET = "<< New Instruction Set>>";
}// class TableMaker

