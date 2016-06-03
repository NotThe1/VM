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
import javax.swing.JTable;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;

public class TableMaker {

	private JFrame frmOpcodeTableMaker;

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
		switch (argumentType) {
		case NONE:
		case ADDRESS:
		case D8: // No panels
		case D16:
			setRegisterPanels(0);
			break;
		case R8: // byte register list
			cbRegister1.setModel(byteRegisterModel);
			setRegisterPanels(1);
			break;
		case R16: // word register list
			cbRegister1.setModel(wordRegisterModel);
			setRegisterPanels(1);
			break;
		case R8D8: // byte register
			cbRegister1.setModel(byteRegisterModel);
			setRegisterPanels(1);
			break;
		case R8R8: // byte register list X 2
			cbRegister1.setModel(byteRegisterModel);
			cbRegister2.setModel(byteRegisterModel);
			setRegisterPanels(2);
			break;
		case R16D16: // word register list
			cbRegister1.setModel(wordRegisterModel);
			setRegisterPanels(1);
			break;
		default:
			setRegisterPanels(0);
		}
	}// argumentTypeChanged

	private void setRegisterPanels(int numberOfRegisters) {
		switch (numberOfRegisters) {
		case 0:
			panelRegisters.setVisible(false);
			panelRegisterOne.setVisible(false);
			panelRegisterTwo.setVisible(false);
			break;
		case 1:
			panelRegisters.setVisible(true);
			panelRegisterOne.setVisible(true);
			panelRegisterTwo.setVisible(false);
			break;
		case 2:
			panelRegisters.setVisible(true);
			panelRegisterOne.setVisible(true);
			panelRegisterTwo.setVisible(true);
			break;
		default:
			panelRegisters.setVisible(false);
			panelRegisterOne.setVisible(false);
			panelRegisterTwo.setVisible(false);
		}// switch
	}// setRegisterPanels
	
	private void setInstructionAttributes(String instruction){
		String[] instructionDetails = baseInstructions.get(instruction);
		lblFlags.setText(instructionDetails[1]);
		lblDescription.setText(instructionDetails[2]);		
	}//setInstructionAttributes
	private void setTableUp(){
		Object[] columnNames = { "Hex", "Len", "Size", "Signature", "Args", "# Args",
				"CC affected", "Inst", "Desc","Func" };
		table = new JTable(new DefaultTableModel(columnNames, 0)) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setFont(new Font("Tahoma", Font.PLAIN, 14));
//		table.getSelectionModel().addListSelectionListener(new RowListener());
		DefaultTableModel modelDir = (DefaultTableModel) table.getModel();
		adjustTableLook(table);
		ArgumentType[] ats = new ArgumentType[]{ArgumentType.D16,ArgumentType.R8};
		modelDir.insertRow(0, new Object[] { 0X10,1,3,ArgumentSignature.R8D8,			
				new JComboBox()
				,2,CCFlags.ZSPAC,Command.INR,"Description","Function" });
		scrollPane.setViewportView(table);
		
	}
	private void adjustTableLook(JTable table) {
		Font realColumnFont = table.getFont();
		FontMetrics fontMetrics = table.getFontMetrics(realColumnFont);

		int charWidth = fontMetrics.stringWidth("W");

		TableColumnModel tableColumn = table.getColumnModel();
		tableColumn.getColumn(0).setPreferredWidth(charWidth * 4);	//Hex
		tableColumn.getColumn(1).setPreferredWidth(charWidth * 3);	//Len
		tableColumn.getColumn(2).setPreferredWidth(charWidth * 4);	// Size
		tableColumn.getColumn(3).setPreferredWidth(charWidth * 7);	// Signature
		tableColumn.getColumn(4).setPreferredWidth(charWidth * 5);	//Args
		tableColumn.getColumn(5).setPreferredWidth(charWidth * 4);	//# Args
		tableColumn.getColumn(6).setPreferredWidth(charWidth * 7);	//CC affected
		tableColumn.getColumn(7).setPreferredWidth(charWidth * 4);	//Inst
		tableColumn.getColumn(8).setPreferredWidth(charWidth * 8);	//Desc
		tableColumn.getColumn(9).setPreferredWidth(charWidth * 8);	//Func

		DefaultTableCellRenderer rightAlign = new DefaultTableCellRenderer();
		rightAlign.setHorizontalAlignment(JLabel.RIGHT);
		tableColumn.getColumn(0).setCellRenderer(rightAlign);
		tableColumn.getColumn(1).setCellRenderer(rightAlign);
		tableColumn.getColumn(2).setCellRenderer(rightAlign);
		tableColumn.getColumn(5).setCellRenderer(rightAlign);


		DefaultTableCellRenderer centerAlign = new DefaultTableCellRenderer();
		centerAlign.setHorizontalAlignment(JLabel.CENTER);
		tableColumn.getColumn(7).setCellRenderer(centerAlign);
//		tableColumn.getColumn(5).setCellRenderer(centerAlign);
	}// adjustTableLook
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

		byteRegisterModel = new DefaultComboBoxModel(byteRegisters);
		wordRegisterModel = new DefaultComboBoxModel(wordRegisters);
		
		setRegisterPanels(0);
		Set<String> insKeys= baseInstructions.keySet();
		baseInstructionModel = new DefaultComboBoxModel(insKeys.toArray((new String[insKeys.size()])));
		cbInstructions.setModel(baseInstructionModel);
		
		setTableUp();
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
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		frmOpcodeTableMaker.getContentPane().setLayout(gridBagLayout);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerSize(10);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		GridBagConstraints gbc_splitPane = new GridBagConstraints();
		gbc_splitPane.insets = new Insets(0, 0, 5, 5);
		gbc_splitPane.fill = GridBagConstraints.BOTH;
		gbc_splitPane.gridx = 0;
		gbc_splitPane.gridy = 0;
		frmOpcodeTableMaker.getContentPane().add(splitPane, gbc_splitPane);
																																																																																		
																																																																																		JPanel panelBottom = new JPanel();
																																																																																		splitPane.setRightComponent(panelBottom);
																																																																																		GridBagLayout gbl_panelBottom = new GridBagLayout();
																																																																																		gbl_panelBottom.columnWidths = new int[]{0, 0, 0, 0};
																																																																																		gbl_panelBottom.rowHeights = new int[]{0, 0};
																																																																																		gbl_panelBottom.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
																																																																																		gbl_panelBottom.rowWeights = new double[]{0.0, Double.MIN_VALUE};
																																																																																		panelBottom.setLayout(gbl_panelBottom);
																																																																																																																		
																																																																																																																		JPanel panelTop = new JPanel();
																																																																																																																		splitPane.setLeftComponent(panelTop);
																																																																																																																		GridBagLayout gbl_panelTop = new GridBagLayout();
																																																																																																																		gbl_panelTop.columnWidths = new int[]{0, 0};
																																																																																																																		gbl_panelTop.rowHeights = new int[]{0, 80, 0};
																																																																																																																		gbl_panelTop.columnWeights = new double[]{1.0, Double.MIN_VALUE};
																																																																																																																		gbl_panelTop.rowWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
																																																																																																																		panelTop.setLayout(gbl_panelTop);
																																																																																																																		
																																																																																																																		JPanel panelTop1 = new JPanel();
																																																																																																																		GridBagConstraints gbc_panelTop1 = new GridBagConstraints();
																																																																																																																		gbc_panelTop1.insets = new Insets(0, 0, 5, 0);
																																																																																																																		gbc_panelTop1.fill = GridBagConstraints.BOTH;
																																																																																																																		gbc_panelTop1.gridx = 0;
																																																																																																																		gbc_panelTop1.gridy = 0;
																																																																																																																		panelTop.add(panelTop1, gbc_panelTop1);
																																																																																																																		GridBagLayout gbl_panelTop1 = new GridBagLayout();
																																																																																																																		gbl_panelTop1.columnWidths = new int[]{0, 0, 0, 0};
																																																																																																																		gbl_panelTop1.rowHeights = new int[]{0, 0, 0};
																																																																																																																		gbl_panelTop1.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
																																																																																																																		gbl_panelTop1.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
																																																																																																																		panelTop1.setLayout(gbl_panelTop1);
																																																																																																																		
																																																																																																																		JPanel panelNumbers = new JPanel();
																																																																																																																		GridBagConstraints gbc_panelNumbers = new GridBagConstraints();
																																																																																																																		gbc_panelNumbers.insets = new Insets(0, 0, 0, 5);
																																																																																																																		gbc_panelNumbers.gridx = 0;
																																																																																																																		gbc_panelNumbers.gridy = 1;
																																																																																																																		panelTop1.add(panelNumbers, gbc_panelNumbers);
																																																																																																																		panelNumbers.setBorder(new TitledBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), "Opcode details", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, null, new Color(0, 0, 0)));
																																																																																																																		GridBagLayout gbl_panelNumbers = new GridBagLayout();
																																																																																																																		gbl_panelNumbers.columnWidths = new int[]{0, 50, 0};
																																																																																																																		gbl_panelNumbers.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
																																																																																																																		gbl_panelNumbers.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
																																																																																																																		gbl_panelNumbers.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
																																																																																																																		panelNumbers.setLayout(gbl_panelNumbers);
																																																																																																																		
																																																																																																																				JLabel lblNewLabel_1 = new JLabel("Opcode :");
																																																																																																																				GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
																																																																																																																				gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
																																																																																																																				gbc_lblNewLabel_1.gridx = 0;
																																																																																																																				gbc_lblNewLabel_1.gridy = 2;
																																																																																																																				panelNumbers.add(lblNewLabel_1, gbc_lblNewLabel_1);
																																																																																																																				
																																																																																																																						HexSpinner hsOpcode = new HexSpinner();
																																																																																																																						GridBagConstraints gbc_hsOpcode = new GridBagConstraints();
																																																																																																																						gbc_hsOpcode.fill = GridBagConstraints.HORIZONTAL;
																																																																																																																						gbc_hsOpcode.insets = new Insets(0, 0, 5, 0);
																																																																																																																						gbc_hsOpcode.gridx = 1;
																																																																																																																						gbc_hsOpcode.gridy = 2;
																																																																																																																						panelNumbers.add(hsOpcode, gbc_hsOpcode);
																																																																																																																						hsOpcode.setMinimumSize(new Dimension(50, 20));
																																																																																																																						hsOpcode.setPreferredSize(new Dimension(50, 20));
																																																																																																																						
																																																																																																																								JLabel lblOpcodeSize = new JLabel("OpCode Size :");
																																																																																																																								GridBagConstraints gbc_lblOpcodeSize = new GridBagConstraints();
																																																																																																																								gbc_lblOpcodeSize.insets = new Insets(0, 0, 5, 5);
																																																																																																																								gbc_lblOpcodeSize.gridx = 0;
																																																																																																																								gbc_lblOpcodeSize.gridy = 3;
																																																																																																																								panelNumbers.add(lblOpcodeSize, gbc_lblOpcodeSize);
																																																																																																																								
																																																																																																																										JSpinner spinOpcodeSize = new JSpinner();
																																																																																																																										GridBagConstraints gbc_spinOpcodeSize = new GridBagConstraints();
																																																																																																																										gbc_spinOpcodeSize.fill = GridBagConstraints.HORIZONTAL;
																																																																																																																										gbc_spinOpcodeSize.insets = new Insets(0, 0, 5, 0);
																																																																																																																										gbc_spinOpcodeSize.gridx = 1;
																																																																																																																										gbc_spinOpcodeSize.gridy = 3;
																																																																																																																										panelNumbers.add(spinOpcodeSize, gbc_spinOpcodeSize);
																																																																																																																										spinOpcodeSize.setModel(new SpinnerNumberModel(1, 1, 3, 1));
																																																																																																																										
																																																																																																																												JLabel lblInstructionSize = new JLabel("Instruction Size :");
																																																																																																																												GridBagConstraints gbc_lblInstructionSize = new GridBagConstraints();
																																																																																																																												gbc_lblInstructionSize.insets = new Insets(0, 0, 5, 5);
																																																																																																																												gbc_lblInstructionSize.gridx = 0;
																																																																																																																												gbc_lblInstructionSize.gridy = 4;
																																																																																																																												panelNumbers.add(lblInstructionSize, gbc_lblInstructionSize);
																																																																																																																												
																																																																																																																														JSpinner spinInstructionSize = new JSpinner();
																																																																																																																														GridBagConstraints gbc_spinInstructionSize = new GridBagConstraints();
																																																																																																																														gbc_spinInstructionSize.fill = GridBagConstraints.HORIZONTAL;
																																																																																																																														gbc_spinInstructionSize.insets = new Insets(0, 0, 5, 0);
																																																																																																																														gbc_spinInstructionSize.gridx = 1;
																																																																																																																														gbc_spinInstructionSize.gridy = 4;
																																																																																																																														panelNumbers.add(spinInstructionSize, gbc_spinInstructionSize);
																																																																																																																														spinInstructionSize.setModel(new SpinnerNumberModel(1, 1, 15, 1));
																																																																																																																														
																																																																																																																																JLabel lblArguments = new JLabel("Arguments");
																																																																																																																																GridBagConstraints gbc_lblArguments = new GridBagConstraints();
																																																																																																																																gbc_lblArguments.anchor = GridBagConstraints.EAST;
																																																																																																																																gbc_lblArguments.insets = new Insets(0, 0, 0, 5);
																																																																																																																																gbc_lblArguments.gridx = 0;
																																																																																																																																gbc_lblArguments.gridy = 6;
																																																																																																																																panelNumbers.add(lblArguments, gbc_lblArguments);
																																																																																																																																
																																																																																																																																		cbArguments = new JComboBox(argumentTypes);
																																																																																																																																		cbArguments.setMaximumRowCount(10);
																																																																																																																																		GridBagConstraints gbc_cbArguments = new GridBagConstraints();
																																																																																																																																		gbc_cbArguments.fill = GridBagConstraints.HORIZONTAL;
																																																																																																																																		gbc_cbArguments.gridx = 1;
																																																																																																																																		gbc_cbArguments.gridy = 6;
																																																																																																																																		panelNumbers.add(cbArguments, gbc_cbArguments);
																																																																																																																																		
																																																																																																																																				panelRegisters = new JPanel();
																																																																																																																																				GridBagConstraints gbc_panelRegisters = new GridBagConstraints();
																																																																																																																																				gbc_panelRegisters.insets = new Insets(0, 0, 0, 5);
																																																																																																																																				gbc_panelRegisters.anchor = GridBagConstraints.NORTH;
																																																																																																																																				gbc_panelRegisters.gridx = 1;
																																																																																																																																				gbc_panelRegisters.gridy = 1;
																																																																																																																																				panelTop1.add(panelRegisters, gbc_panelRegisters);
																																																																																																																																				panelRegisters.setBorder(new TitledBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), "Registers", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, null, null));
																																																																																																																																				GridBagLayout gbl_panelRegisters = new GridBagLayout();
																																																																																																																																				gbl_panelRegisters.columnWidths = new int[] { 0, 0 };
																																																																																																																																				gbl_panelRegisters.rowHeights = new int[] { 0, 0, 0, 0 };
																																																																																																																																				gbl_panelRegisters.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
																																																																																																																																				gbl_panelRegisters.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
																																																																																																																																				panelRegisters.setLayout(gbl_panelRegisters);
																																																																																																																																						
																																																																																																																																								panelRegisterOne = new JPanel();
																																																																																																																																								GridBagConstraints gbc_panelRegisterOne = new GridBagConstraints();
																																																																																																																																								gbc_panelRegisterOne.insets = new Insets(0, 0, 5, 0);
																																																																																																																																								gbc_panelRegisterOne.fill = GridBagConstraints.BOTH;
																																																																																																																																								gbc_panelRegisterOne.gridx = 0;
																																																																																																																																								gbc_panelRegisterOne.gridy = 1;
																																																																																																																																								panelRegisters.add(panelRegisterOne, gbc_panelRegisterOne);
																																																																																																																																								GridBagLayout gbl_panelRegisterOne = new GridBagLayout();
																																																																																																																																								gbl_panelRegisterOne.columnWidths = new int[] { 0, 0, 0 };
																																																																																																																																								gbl_panelRegisterOne.rowHeights = new int[] { 0, 0 };
																																																																																																																																								gbl_panelRegisterOne.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
																																																																																																																																								gbl_panelRegisterOne.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
																																																																																																																																								panelRegisterOne.setLayout(gbl_panelRegisterOne);
																																																																																																																																								
																																																																																																																																										JLabel lblRegister = new JLabel("Register 1 :");
																																																																																																																																										GridBagConstraints gbc_lblRegister = new GridBagConstraints();
																																																																																																																																										gbc_lblRegister.insets = new Insets(0, 0, 0, 5);
																																																																																																																																										gbc_lblRegister.anchor = GridBagConstraints.EAST;
																																																																																																																																										gbc_lblRegister.gridx = 0;
																																																																																																																																										gbc_lblRegister.gridy = 0;
																																																																																																																																										panelRegisterOne.add(lblRegister, gbc_lblRegister);
																																																																																																																																										
																																																																																																																																												cbRegister1 = new JComboBox();
																																																																																																																																												GridBagConstraints gbc_cbRegister1 = new GridBagConstraints();
																																																																																																																																												gbc_cbRegister1.fill = GridBagConstraints.HORIZONTAL;
																																																																																																																																												gbc_cbRegister1.gridx = 1;
																																																																																																																																												gbc_cbRegister1.gridy = 0;
																																																																																																																																												panelRegisterOne.add(cbRegister1, gbc_cbRegister1);
																																																																																																																																												
																																																																																																																																														panelRegisterTwo = new JPanel();
																																																																																																																																														GridBagConstraints gbc_panelRegisterTwo = new GridBagConstraints();
																																																																																																																																														gbc_panelRegisterTwo.fill = GridBagConstraints.BOTH;
																																																																																																																																														gbc_panelRegisterTwo.gridx = 0;
																																																																																																																																														gbc_panelRegisterTwo.gridy = 2;
																																																																																																																																														panelRegisters.add(panelRegisterTwo, gbc_panelRegisterTwo);
																																																																																																																																														GridBagLayout gbl_panelRegisterTwo = new GridBagLayout();
																																																																																																																																														gbl_panelRegisterTwo.columnWidths = new int[] { 0, 0, 0 };
																																																																																																																																														gbl_panelRegisterTwo.rowHeights = new int[] { 0, 0 };
																																																																																																																																														gbl_panelRegisterTwo.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
																																																																																																																																														gbl_panelRegisterTwo.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
																																																																																																																																														panelRegisterTwo.setLayout(gbl_panelRegisterTwo);
																																																																																																																																														
																																																																																																																																																JLabel lblRegister_2 = new JLabel("Register 2 :");
																																																																																																																																																GridBagConstraints gbc_lblRegister_2 = new GridBagConstraints();
																																																																																																																																																gbc_lblRegister_2.insets = new Insets(0, 0, 0, 5);
																																																																																																																																																gbc_lblRegister_2.anchor = GridBagConstraints.EAST;
																																																																																																																																																gbc_lblRegister_2.gridx = 0;
																																																																																																																																																gbc_lblRegister_2.gridy = 0;
																																																																																																																																																panelRegisterTwo.add(lblRegister_2, gbc_lblRegister_2);
																																																																																																																																																
																																																																																																																																																		cbRegister2 = new JComboBox();
																																																																																																																																																		GridBagConstraints gbc_cbRegister2 = new GridBagConstraints();
																																																																																																																																																		gbc_cbRegister2.fill = GridBagConstraints.HORIZONTAL;
																																																																																																																																																		gbc_cbRegister2.gridx = 1;
																																																																																																																																																		gbc_cbRegister2.gridy = 0;
																																																																																																																																																		panelRegisterTwo.add(cbRegister2, gbc_cbRegister2);
																																																																																																																																																		
																																																																																																																																																		JPanel panelInstruction = new JPanel();
																																																																																																																																																		GridBagConstraints gbc_panelInstruction = new GridBagConstraints();
																																																																																																																																																		gbc_panelInstruction.gridx = 2;
																																																																																																																																																		gbc_panelInstruction.gridy = 1;
																																																																																																																																																		panelTop1.add(panelInstruction, gbc_panelInstruction);
																																																																																																																																																		panelInstruction.setBorder(new TitledBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), "Instruction Info", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, null, null));
																																																																																																																																																		GridBagLayout gbl_panelInstruction = new GridBagLayout();
																																																																																																																																																		gbl_panelInstruction.columnWidths = new int[]{0, 50, 0, 0, 0};
																																																																																																																																																		gbl_panelInstruction.rowHeights = new int[]{0, 0, 0, 0, 0};
																																																																																																																																																		gbl_panelInstruction.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
																																																																																																																																																		gbl_panelInstruction.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
																																																																																																																																																		panelInstruction.setLayout(gbl_panelInstruction);
																																																																																																																																																		
																																																																																																																																																		cbInstructions = new JComboBox();
																																																																																																																																																		cbInstructions.addActionListener(new ActionListener() {
																																																																																																																																																			public void actionPerformed(ActionEvent arg0) {
																																																																																																																																																				JComboBox jcb = (JComboBox) arg0.getSource();
																																																																																																																																																				setInstructionAttributes((String) jcb.getSelectedItem());
																																																																																																																																																			//String instruction = (JComboBox) arg0.getSource().;	
																																																																																																																																																			}
																																																																																																																																																		});
																																																																																																																																																		
																																																																																																																																																		JLabel lblInstruction = new JLabel("Instruction : ");
																																																																																																																																																		GridBagConstraints gbc_lblInstruction = new GridBagConstraints();
																																																																																																																																																		gbc_lblInstruction.insets = new Insets(0, 0, 5, 5);
																																																																																																																																																		gbc_lblInstruction.anchor = GridBagConstraints.EAST;
																																																																																																																																																		gbc_lblInstruction.gridx = 0;
																																																																																																																																																		gbc_lblInstruction.gridy = 1;
																																																																																																																																																		panelInstruction.add(lblInstruction, gbc_lblInstruction);
																																																																																																																																																		
																																																																																																																																																		JLabel lblFlagsAffected = new JLabel("Flags");
																																																																																																																																																		GridBagConstraints gbc_lblFlagsAffected = new GridBagConstraints();
																																																																																																																																																		gbc_lblFlagsAffected.insets = new Insets(0, 0, 5, 5);
																																																																																																																																																		gbc_lblFlagsAffected.gridx = 1;
																																																																																																																																																		gbc_lblFlagsAffected.gridy = 1;
																																																																																																																																																		panelInstruction.add(lblFlagsAffected, gbc_lblFlagsAffected);
																																																																																																																																																		GridBagConstraints gbc_cbInstructions = new GridBagConstraints();
																																																																																																																																																		gbc_cbInstructions.insets = new Insets(0, 0, 5, 5);
																																																																																																																																																		gbc_cbInstructions.fill = GridBagConstraints.HORIZONTAL;
																																																																																																																																																		gbc_cbInstructions.gridx = 0;
																																																																																																																																																		gbc_cbInstructions.gridy = 2;
																																																																																																																																																		panelInstruction.add(cbInstructions, gbc_cbInstructions);
																																																																																																																																																		
																																																																																																																																																		lblFlags = new JLabel("New label");
																																																																																																																																																		GridBagConstraints gbc_lblFlags = new GridBagConstraints();
																																																																																																																																																		gbc_lblFlags.insets = new Insets(0, 0, 5, 5);
																																																																																																																																																		gbc_lblFlags.gridx = 1;
																																																																																																																																																		gbc_lblFlags.gridy = 2;
																																																																																																																																																		panelInstruction.add(lblFlags, gbc_lblFlags);
																																																																																																																																																		
																																																																																																																																																		lblDescription = new JLabel("New label");
																																																																																																																																																		GridBagConstraints gbc_lblDescription = new GridBagConstraints();
																																																																																																																																																		gbc_lblDescription.gridwidth = 3;
																																																																																																																																																		gbc_lblDescription.insets = new Insets(0, 0, 0, 5);
																																																																																																																																																		gbc_lblDescription.gridx = 0;
																																																																																																																																																		gbc_lblDescription.gridy = 3;
																																																																																																																																																		panelInstruction.add(lblDescription, gbc_lblDescription);
																																																																																																																																		cbArguments.addActionListener(new ActionListener(){
																																																																																																																																			@Override
																																																																																																																																			public void actionPerformed(ActionEvent arg0) {
																																																																																																																																				JComboBox jcb = (JComboBox) arg0.getSource();
																																																																																																																																				argumentTypeChanged((ArgumentType) jcb.getSelectedItem());
																																																																																																																																				System.out.println("StateChanged, item selected = " + jcb.getSelectedItem());		
																																																																																																																																			}//actionPerformed
																																																																																																																																		});
																																																																																																																		
																																																																																																																		JPanel panelTop2 = new JPanel();
																																																																																																																		GridBagConstraints gbc_panelTop2 = new GridBagConstraints();
																																																																																																																		gbc_panelTop2.fill = GridBagConstraints.BOTH;
																																																																																																																		gbc_panelTop2.gridx = 0;
																																																																																																																		gbc_panelTop2.gridy = 1;
																																																																																																																		panelTop.add(panelTop2, gbc_panelTop2);
																																																																																																																		GridBagLayout gbl_panelTop2 = new GridBagLayout();
																																																																																																																		gbl_panelTop2.columnWidths = new int[]{0};
																																																																																																																		gbl_panelTop2.rowHeights = new int[]{0};
																																																																																																																		gbl_panelTop2.columnWeights = new double[]{Double.MIN_VALUE};
																																																																																																																		gbl_panelTop2.rowWeights = new double[]{Double.MIN_VALUE};
																																																																																																																		panelTop2.setLayout(gbl_panelTop2);
		splitPane.setDividerLocation(250);
		
		scrollPane = new JScrollPane();
		scrollPane.setViewportBorder(new LineBorder(new Color(0, 0, 0)));
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 4;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 3;
		frmOpcodeTableMaker.getContentPane().add(scrollPane, gbc_scrollPane);
		
		table = new JTable();
		scrollPane.setViewportView(table);
		
		JLabel lblNewLabel = new JLabel("Instruction Table");
		scrollPane.setColumnHeaderView(lblNewLabel);

		JMenuBar menuBar = new JMenuBar();
		frmOpcodeTableMaker.setJMenuBar(menuBar);
		
		JMenu mnuFile = new JMenu("File");
		menuBar.add(mnuFile);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("New menu item");
		mnuFile.add(mntmNewMenuItem);
	}// initialize
		// <><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>

	Register[] byteRegisters = new Register[] { Register.A, Register.B, Register.C, Register.D,
			Register.E, Register.H, Register.L };

	Register[] wordRegisters = new Register[] { Register.AF, Register.BC, Register.DE, Register.HL, Register.M,
			Register.SP, Register.PC };
	ArgumentType[] argumentTypes = new ArgumentType[] { ArgumentType.NONE, ArgumentType.ADDRESS, ArgumentType.D8,
			ArgumentType.D16, ArgumentType.R8, ArgumentType.R8R8, ArgumentType.R16, ArgumentType.R8D8,
			ArgumentType.R16D16 };

	private JComboBox cbArguments;
	private JPanel panelRegisters;
	private JComboBox cbRegister1;
	private JPanel panelRegisterOne;
	private JPanel panelRegisterTwo;
	private JComboBox cbRegister2;
	private DefaultComboBoxModel byteRegisterModel;
	private DefaultComboBoxModel wordRegisterModel;
	private DefaultComboBoxModel baseInstructionModel;

	// -------------------------------
	enum Register {
		// Single Byte Registers
		A, B, C, D, E, H, L,
		// Double Byte Registers
		// used for identification only
		// nothing is stored directly into one of these
		BC, DE, HL, M, SP, AF, PC
	}// enum

	enum ArgumentType {
		NONE,
		ADDRESS,
		D8,
		D16,
		R8,
		R8R8,
		R16,
		R8D8,
		R16D16,
		VECTOR
	}//enum
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private static HashMap<String, String[]> baseInstructions;
	private JComboBox cbInstructions;
	private JLabel lblFlags;
	private JLabel lblDescription;
	private JTable table;
	private JScrollPane scrollPane;
	static {
		baseInstructions = new  HashMap<String, String[]>();
		baseInstructions.put("STC",new String[]{"STC","C","Set Carry"});
		baseInstructions.put("CMC",new String[]{"CMC","C","Complement Carry"});
		baseInstructions.put("INR",new String[]{"INR","Z,S,P,Aux,C","Increment Register/Memory"});
		baseInstructions.put("DCR",new String[]{"DCR","Z,S,P,Aux,C","Deccrement Register/Memory"});
		baseInstructions.put("CMA",new String[]{"CMA","None","Complement Acc"});
		baseInstructions.put("DAA",new String[]{"DAA","Z,S,P,Aux,C","Decimal Adjust Acc"});
		baseInstructions.put("NOP",new String[]{"NOP","None","No Operation"});
		baseInstructions.put("MOV",new String[]{"MOV","None","Move"});
		baseInstructions.put("STAX",new String[]{"STAX","None","Store Acc"});
		baseInstructions.put("LDAX",new String[]{"LDAX","None","Load Acc"});
		baseInstructions.put("ADD",new String[]{"ADD","Z,S,P,Aux,C","Add Register/Memory to Acc"});
		baseInstructions.put("ADC",new String[]{"ADC","Z,S,P,Aux,C","Add Register/Memory to Acc with Carry"});
		baseInstructions.put("SUB",new String[]{"SUB","Z,S,P,Aux,C","Subtract Register/Memory from Acc"});
		baseInstructions.put("SBB",new String[]{"SBB","Z,S,P,Aux,C","Subtract Register/Memory from Acc with Borrow"});
		baseInstructions.put("ANA",new String[]{"ANA","Z,S,P,C, Aux*","Logical AND Register/Memory with Acc"});
		baseInstructions.put("XRA",new String[]{"XRA","Z,S,P,Aux,C","Logical XOR Register/Memory with Acc"});
		baseInstructions.put("ORA",new String[]{"ORA","Z,S,P,C, Aux*","Logical OR Register/Memory with Acc"});
		baseInstructions.put("CMP",new String[]{"CMP","Z,S,P,Aux,C","Compare Register/Memory with Acc"});
		baseInstructions.put("RLC",new String[]{"RLC","C","Rotate Left Acc"});
		baseInstructions.put("RRC",new String[]{"RRC","C","Rotate Right Acc"});
		baseInstructions.put("RAL",new String[]{"RAL","C","Rotate Left Acc Through Carry"});
		baseInstructions.put("RAR",new String[]{"RAR","C","Rotate Right Acc Through Carry"});
		baseInstructions.put("PUSH",new String[]{"PUSH","None","Push Data Onto Stack"});
		baseInstructions.put("POP",new String[]{"POP","None/Z,S,P,Aux,C","Pop Data Off the Stack"});
		baseInstructions.put("DAD",new String[]{"DAD","C","Double Add"});
		baseInstructions.put("INX",new String[]{"INX","None","Increment register Pair"});
		baseInstructions.put("DCX",new String[]{"DCX","None","Decrement register Pair"});
		baseInstructions.put("XCHG",new String[]{"XCHG","None","Exchange Registers"});
		baseInstructions.put("XTHL",new String[]{"XTHL","None","Exchange Stack"});
		baseInstructions.put("SPHL",new String[]{"SPHL","None","Load SP from H and L"});
		baseInstructions.put("LXI",new String[]{"LXI","None","Load Register Pair Immediate"});
		baseInstructions.put("MVI",new String[]{"MVI","None","Move Immediate Data",""});
		baseInstructions.put("ADI",new String[]{"ADI","Z,S,P,Aux,C","Add Immediate to Acc"});
		baseInstructions.put("ACI",new String[]{"ACI","Z,S,P,Aux,C","Add Immediate to Acc With Carry"});
		baseInstructions.put("SUI",new String[]{"SUI","Z,S,P,Aux,C","Subtract Immediate to Acc"});
		baseInstructions.put("SBI",new String[]{"SBI","Z,S,P,Aux,C","Subtract Immediate to Acc With Borrow"});
		baseInstructions.put("ANI",new String[]{"ANI","Z,S,P,C","AND Immediate with Acc"});
		baseInstructions.put("XRI",new String[]{"XRI","Z,S,P,C","XOR Immediate with Acc"});
		baseInstructions.put("ORI",new String[]{"ORI","Z,S,P,C","OR Immediate with Acc"});
		baseInstructions.put("CPI",new String[]{"CPI","Z,S,P,Aux,C","Compare Immediate with Acc"});
		baseInstructions.put("STA",new String[]{"STA","None","Store Acc Direct"});
		baseInstructions.put("LDA",new String[]{"LDA","None","Load Acc Direct"});
		baseInstructions.put("SHLD",new String[]{"SHLD","None","Store H and L Direct"});
		baseInstructions.put("LHLD",new String[]{"LHLD","None","Load H and L Direct"});
		baseInstructions.put("PCHL",new String[]{"PCHL","None","Load Program Counter from H and L"});
		baseInstructions.put("JMP",new String[]{"JMP","None","Jump"});
		baseInstructions.put("JC",new String[]{"JC","None","Jump if Carry"});
		baseInstructions.put("JNC",new String[]{"JNC","None","Jump if No Carry"});
		baseInstructions.put("JZ",new String[]{"JZ","None","Jump if Zero"});
		baseInstructions.put("JNZ",new String[]{"JNZ","None","Jump in Not Zero"});
		baseInstructions.put("JM",new String[]{"JM","None","Jump if Minus"});
		baseInstructions.put("JP",new String[]{"JP","None","Jump if Positive"});
		baseInstructions.put("JPE",new String[]{"JPE","None","Jump if Parity Even",""});
		baseInstructions.put("JPO",new String[]{"JPO","None","Jump if Parity Odd"});
		baseInstructions.put("CALL",new String[]{"CALL","None","Call"});
		baseInstructions.put("CC",new String[]{"CC","None","Call if Carry"});
		baseInstructions.put("CNC",new String[]{"CNC","None","Call if No Carry"});
		baseInstructions.put("CZ",new String[]{"CZ","None","Call if Zero"});
		baseInstructions.put("CNZ",new String[]{"CNZ","None","Call in Not Zero"});
		baseInstructions.put("CM",new String[]{"CM","None","Call if Minus"});
		baseInstructions.put("CP",new String[]{"CP","None","Call if Positive"});
		baseInstructions.put("CPE",new String[]{"CPE","None","Call if Parity Even"});
		baseInstructions.put("CPO",new String[]{"CPO","None","Call if Parity Odd"});
		baseInstructions.put("RET",new String[]{"RET","None","Return"});
		baseInstructions.put("RC",new String[]{"RC","None","Return if Carry"});
		baseInstructions.put("RNC",new String[]{"RNC","None","Return if No Carry"});
		baseInstructions.put("RZ",new String[]{"RZ","None","Return if Zero"});
		baseInstructions.put("RNZ",new String[]{"RNZ","None","Return in Not Zero"});
		baseInstructions.put("RM",new String[]{"RM","None","Return if Minus"});
		baseInstructions.put("RP",new String[]{"RP","None","Return if Positive"});
		baseInstructions.put("RPE",new String[]{"RPE","None","Return if Parity Even"});
		baseInstructions.put("RPO",new String[]{"RPO","None","Return if Parity Odd"});
		baseInstructions.put("RST",new String[]{"RST","None","Restart"});
		baseInstructions.put("EI",new String[]{"EI","None","Enable Interrupts"});
		baseInstructions.put("DI",new String[]{"DI","None","Disable Interrupts"});
		baseInstructions.put("IN",new String[]{"IN","None","Input"});
		baseInstructions.put("OUT",new String[]{"OUT","None","Output"});
		baseInstructions.put("HLT",new String[]{"HLT","None","Halt"});
			
	} //static
		//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

}// class TableMaker
