package hardware;

import java.awt.EventQueue;

import javax.swing.JFrame;

import java.awt.GridBagLayout;

import javax.swing.JRadioButton;

import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Insets;

import hardware.ConditionCodeRegister;
import hardware.Register;
import hardware.StateAdapter;
import hardware.StateDisplay;
import hardware.WorkingRegisterSet;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.border.LineBorder;
import javax.swing.text.MaskFormatter;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;

import java.awt.Font;

import codeSupport.HexSpinner;

import javax.swing.SpinnerNumberModel;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;

public class StateDisplayAndAdapterTest {
	
	static WorkingRegisterSet wrs = WorkingRegisterSet.getInstance();
	static ConditionCodeRegister ccr = ConditionCodeRegister.getInstance();
	private StateDisplay stateDisplay;


	private JFrame frmStatedisplayAndAdapter;
	private JPanel StateDisplay;
	private JPanel panelSource;
	private JLabel lblPc;
	private JLabel lblSp;
	private JLabel lblA;
	private JLabel lblB;
	private JLabel lblC;
	private JLabel lblD;
	private JLabel lblE;
	private JLabel lblH;
	private JLabel lblL;
	private HexSpinner hsPC;
	private HexSpinner hsSP;
	private HexSpinner hsA;
	private HexSpinner hsB;
	private HexSpinner hsC;
	private HexSpinner hsD;
	private HexSpinner hsE;
	private HexSpinner hsH;
	private HexSpinner hsL;
	private JPanel panelAction;
	private JCheckBox cbSign;
	private JCheckBox cbZero;
	private JCheckBox cbAC;
	private JCheckBox cbParity;
	private JCheckBox cbCarry;
	private JButton btnSetAllToA;
	private JButton btnSetFlagsFromCBs;
	private JButton btnLoadSpinnersFromRegiters;
	private JButton btnLoadCheckBox;
	private JButton btnSetEachReg;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					StateDisplayAndAdapterTest window = new StateDisplayAndAdapterTest();
					window.frmStatedisplayAndAdapter.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}//try
			}//run
		});
	}//main

//	public void appInit() {
//		stateAdapter = new StateAdapter();
//	}

	/**
	 * Create the application.
	 */
	public StateDisplayAndAdapterTest() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		frmStatedisplayAndAdapter = new JFrame();
		frmStatedisplayAndAdapter.setTitle("StateDisplay And Adapter Test");
		frmStatedisplayAndAdapter.setBounds(100, 100, 743, 639);
		frmStatedisplayAndAdapter.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		frmStatedisplayAndAdapter.getContentPane().setLayout(gridBagLayout);
		
		panelSource = new JPanel();
		GridBagConstraints gbc_panelSource = new GridBagConstraints();
		gbc_panelSource.insets = new Insets(0, 0, 5, 5);
		gbc_panelSource.fill = GridBagConstraints.BOTH;
		gbc_panelSource.gridx = 0;
		gbc_panelSource.gridy = 1;
		frmStatedisplayAndAdapter.getContentPane().add(panelSource, gbc_panelSource);
		GridBagLayout gbl_panelSource = new GridBagLayout();
		gbl_panelSource.columnWidths = new int[]{0, 0, 0};
		gbl_panelSource.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panelSource.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_panelSource.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panelSource.setLayout(gbl_panelSource);
		
		lblPc = new JLabel("PC");
		lblPc.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPc.setPreferredSize(new Dimension(30, 16));
		lblPc.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GridBagConstraints gbc_lblPc = new GridBagConstraints();
		gbc_lblPc.insets = new Insets(0, 0, 5, 5);
		gbc_lblPc.gridx = 0;
		gbc_lblPc.gridy = 0;
		panelSource.add(lblPc, gbc_lblPc);
		
		hsPC = new HexSpinner();
		hsPC.setPreferredSize(new Dimension(60, 20));
		SpinnerNumberModel smn = (SpinnerNumberModel) hsPC.getModel();
		smn.setMaximum(0XFFFF);
		GridBagConstraints gbc_hsPC = new GridBagConstraints();
		gbc_hsPC.insets = new Insets(0, 0, 5, 0);
		gbc_hsPC.gridx = 1;
		gbc_hsPC.gridy = 0;
		panelSource.add(hsPC, gbc_hsPC);
		
		lblSp = new JLabel("SP");
		lblSp.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSp.setPreferredSize(new Dimension(30, 16));
		lblSp.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GridBagConstraints gbc_lblSp = new GridBagConstraints();
		gbc_lblSp.insets = new Insets(0, 0, 5, 5);
		gbc_lblSp.gridx = 0;
		gbc_lblSp.gridy = 1;
		panelSource.add(lblSp, gbc_lblSp);
		
		hsSP = new HexSpinner();
		hsSP.setPreferredSize(new Dimension(60, 20));
		smn = (SpinnerNumberModel) hsSP.getModel();
		smn.setMaximum(0XFFFF);
		GridBagConstraints gbc_hsSP = new GridBagConstraints();
		gbc_hsSP.insets = new Insets(0, 0, 5, 0);
		gbc_hsSP.gridx = 1;
		gbc_hsSP.gridy = 1;
		panelSource.add(hsSP, gbc_hsSP);
		
		lblA = new JLabel("A");
		lblA.setHorizontalAlignment(SwingConstants.RIGHT);
		lblA.setPreferredSize(new Dimension(30, 16));
		lblA.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GridBagConstraints gbc_lblA = new GridBagConstraints();
		gbc_lblA.insets = new Insets(0, 0, 5, 5);
		gbc_lblA.gridx = 0;
		gbc_lblA.gridy = 3;
		panelSource.add(lblA, gbc_lblA);
		
		hsA = new HexSpinner();
		hsA.setPreferredSize(new Dimension(40, 20));
		smn = (SpinnerNumberModel) hsA.getModel();
		smn.setMaximum(0XFF);
		GridBagConstraints gbc_hsA = new GridBagConstraints();
		gbc_hsA.insets = new Insets(0, 0, 5, 0);
		gbc_hsA.gridx = 1;
		gbc_hsA.gridy = 3;
		panelSource.add(hsA, gbc_hsA);
		
		lblB = new JLabel("B");
		lblB.setHorizontalAlignment(SwingConstants.RIGHT);
		lblB.setPreferredSize(new Dimension(30, 16));
		lblB.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GridBagConstraints gbc_lblB = new GridBagConstraints();
		gbc_lblB.insets = new Insets(0, 0, 5, 5);
		gbc_lblB.gridx = 0;
		gbc_lblB.gridy = 4;
		panelSource.add(lblB, gbc_lblB);
		
		hsB = new HexSpinner();
		hsB.setPreferredSize(new Dimension(40, 20));
		smn = (SpinnerNumberModel) hsB.getModel();
		smn.setMaximum(0XFF);
		GridBagConstraints gbc_hsB = new GridBagConstraints();
		gbc_hsB.insets = new Insets(0, 0, 5, 0);
		gbc_hsB.gridx = 1;
		gbc_hsB.gridy = 4;
		panelSource.add(hsB, gbc_hsB);
		
		lblC = new JLabel("C");
		lblC.setHorizontalAlignment(SwingConstants.RIGHT);
		lblC.setPreferredSize(new Dimension(30, 16));
		lblC.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GridBagConstraints gbc_lblC = new GridBagConstraints();
		gbc_lblC.insets = new Insets(0, 0, 5, 5);
		gbc_lblC.gridx = 0;
		gbc_lblC.gridy = 5;
		panelSource.add(lblC, gbc_lblC);
		
		hsC = new HexSpinner();
		hsC.setPreferredSize(new Dimension(40, 20));
		smn = (SpinnerNumberModel) hsC.getModel();
		smn.setMaximum(0XFF);
		GridBagConstraints gbc_hsC = new GridBagConstraints();
		gbc_hsC.insets = new Insets(0, 0, 5, 0);
		gbc_hsC.gridx = 1;
		gbc_hsC.gridy = 5;
		panelSource.add(hsC, gbc_hsC);
		
		lblD = new JLabel("D");
		lblD.setHorizontalAlignment(SwingConstants.RIGHT);
		lblD.setPreferredSize(new Dimension(30, 16));
		lblD.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GridBagConstraints gbc_lblD = new GridBagConstraints();
		gbc_lblD.insets = new Insets(0, 0, 5, 5);
		gbc_lblD.gridx = 0;
		gbc_lblD.gridy = 6;
		panelSource.add(lblD, gbc_lblD);
		
		hsD = new HexSpinner();
		hsD.setPreferredSize(new Dimension(40, 20));
		smn = (SpinnerNumberModel) hsD.getModel();
		smn.setMaximum(0XFF);
		GridBagConstraints gbc_hsD = new GridBagConstraints();
		gbc_hsD.insets = new Insets(0, 0, 5, 0);
		gbc_hsD.gridx = 1;
		gbc_hsD.gridy = 6;
		panelSource.add(hsD, gbc_hsD);
		
		lblE = new JLabel("E");
		lblE.setHorizontalAlignment(SwingConstants.RIGHT);
		lblE.setPreferredSize(new Dimension(30, 16));
		lblE.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GridBagConstraints gbc_lblE = new GridBagConstraints();
		gbc_lblE.insets = new Insets(0, 0, 5, 5);
		gbc_lblE.gridx = 0;
		gbc_lblE.gridy = 7;
		panelSource.add(lblE, gbc_lblE);
		
		hsE = new HexSpinner();
		hsE.setPreferredSize(new Dimension(40, 20));
		smn = (SpinnerNumberModel) hsE.getModel();
		smn.setMaximum(0XFF);
		GridBagConstraints gbc_hsE = new GridBagConstraints();
		gbc_hsE.insets = new Insets(0, 0, 5, 0);
		gbc_hsE.gridx = 1;
		gbc_hsE.gridy = 7;
		panelSource.add(hsE, gbc_hsE);
		
		lblH = new JLabel("H");
		lblH.setHorizontalAlignment(SwingConstants.RIGHT);
		lblH.setPreferredSize(new Dimension(30, 16));
		lblH.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GridBagConstraints gbc_lblH = new GridBagConstraints();
		gbc_lblH.insets = new Insets(0, 0, 5, 5);
		gbc_lblH.gridx = 0;
		gbc_lblH.gridy = 8;
		panelSource.add(lblH, gbc_lblH);
		
		hsH = new HexSpinner();
		hsH.setPreferredSize(new Dimension(40, 20));
		smn = (SpinnerNumberModel) hsH.getModel();
		smn.setMaximum(0XFF);
		GridBagConstraints gbc_hsH = new GridBagConstraints();
		gbc_hsH.insets = new Insets(0, 0, 5, 0);
		gbc_hsH.gridx = 1;
		gbc_hsH.gridy = 8;
		panelSource.add(hsH, gbc_hsH);
		
		lblL = new JLabel("L");
		lblL.setHorizontalAlignment(SwingConstants.RIGHT);
		lblL.setPreferredSize(new Dimension(30, 16));
		lblL.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GridBagConstraints gbc_lblL = new GridBagConstraints();
		gbc_lblL.insets = new Insets(0, 0, 5, 5);
		gbc_lblL.gridx = 0;
		gbc_lblL.gridy = 9;
		panelSource.add(lblL, gbc_lblL);
		
		hsL = new HexSpinner();
		hsL.setPreferredSize(new Dimension(40, 20));
		smn = (SpinnerNumberModel) hsL.getModel();
		smn.setMaximum(0XFF);
		GridBagConstraints gbc_hsL = new GridBagConstraints();
		gbc_hsL.insets = new Insets(0, 0, 5, 0);
		gbc_hsL.gridx = 1;
		gbc_hsL.gridy = 9;
		panelSource.add(hsL, gbc_hsL);
		
		cbSign = new JCheckBox("Sign");
		GridBagConstraints gbc_cbSign = new GridBagConstraints();
		gbc_cbSign.gridwidth = 2;
		gbc_cbSign.insets = new Insets(0, 0, 5, 5);
		gbc_cbSign.gridx = 0;
		gbc_cbSign.gridy = 11;
		panelSource.add(cbSign, gbc_cbSign);
		
		cbZero = new JCheckBox("Zero");
		GridBagConstraints gbc_cbZero = new GridBagConstraints();
		gbc_cbZero.gridwidth = 2;
		gbc_cbZero.insets = new Insets(0, 0, 5, 5);
		gbc_cbZero.gridx = 0;
		gbc_cbZero.gridy = 12;
		panelSource.add(cbZero, gbc_cbZero);
		
		cbAC = new JCheckBox("Aux");
		GridBagConstraints gbc_cbAC = new GridBagConstraints();
		gbc_cbAC.gridwidth = 2;
		gbc_cbAC.insets = new Insets(0, 0, 5, 5);
		gbc_cbAC.gridx = 0;
		gbc_cbAC.gridy = 13;
		panelSource.add(cbAC, gbc_cbAC);
		
		cbParity = new JCheckBox("Parity");
		GridBagConstraints gbc_cbParity = new GridBagConstraints();
		gbc_cbParity.gridwidth = 2;
		gbc_cbParity.insets = new Insets(0, 0, 5, 5);
		gbc_cbParity.gridx = 0;
		gbc_cbParity.gridy = 14;
		panelSource.add(cbParity, gbc_cbParity);
		
		cbCarry = new JCheckBox("Carry");
		GridBagConstraints gbc_cbCarry = new GridBagConstraints();
		gbc_cbCarry.gridwidth = 2;
		gbc_cbCarry.insets = new Insets(0, 0, 0, 5);
		gbc_cbCarry.gridx = 0;
		gbc_cbCarry.gridy = 15;
		panelSource.add(cbCarry, gbc_cbCarry);

		StateDisplay = new JPanel();
		StateDisplay.setBorder(null);
		StateDisplay.setLayout(null);
		GridBagConstraints gbc_StateDisplay = new GridBagConstraints();
		gbc_StateDisplay.insets = new Insets(0, 0, 5, 0);
		gbc_StateDisplay.fill = GridBagConstraints.BOTH;
		gbc_StateDisplay.gridx = 1;
		gbc_StateDisplay.gridy = 1;
		frmStatedisplayAndAdapter.getContentPane().add(StateDisplay, gbc_StateDisplay);

		stateDisplay = new StateDisplay();
		stateDisplay.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				System.out.printf("stateDisplay: %n");
			}
		});
		stateDisplay.setBounds(0, 11, 600, 285);

		StateDisplay.add(stateDisplay);
		stateDisplay.setLayout(null);
		
		panelAction = new JPanel();
		panelAction.setBorder(new LineBorder(new Color(0, 0, 0), 2, true));
		GridBagConstraints gbc_panelAction = new GridBagConstraints();
		gbc_panelAction.fill = GridBagConstraints.BOTH;
		gbc_panelAction.gridx = 1;
		gbc_panelAction.gridy = 2;
		frmStatedisplayAndAdapter.getContentPane().add(panelAction, gbc_panelAction);
		GridBagLayout gbl_panelAction = new GridBagLayout();
		gbl_panelAction.columnWidths = new int[]{0, 0};
		gbl_panelAction.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gbl_panelAction.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panelAction.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panelAction.setLayout(gbl_panelAction);
		
		btnSetAllToA = new JButton("set All to A's value");
		btnSetAllToA.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int value =  (int) hsA.getValue();
				byte byteValue = (byte) value;
				wrs.setReg(Register.A, byteValue);
				wrs.setReg(Register.B, byteValue);
				wrs.setReg(Register.C, byteValue);
				wrs.setReg(Register.D, byteValue);
				wrs.setReg(Register.E, byteValue);
				wrs.setReg(Register.H, byteValue);
				wrs.setReg(Register.L, byteValue);
				
				int wordValue = ( (value * 0X100)) +  value;
				wrs.setProgramCounter(wordValue);
				wrs.setStackPointer(wordValue);
				stateDisplay.updateDisplayAllRegisters();
				
			}
		});
		GridBagConstraints gbc_btnSetAllToA = new GridBagConstraints();
		gbc_btnSetAllToA.insets = new Insets(0, 0, 5, 0);
		gbc_btnSetAllToA.anchor = GridBagConstraints.NORTH;
		gbc_btnSetAllToA.gridx = 0;
		gbc_btnSetAllToA.gridy = 0;
		panelAction.add(btnSetAllToA, gbc_btnSetAllToA);
		
		btnSetFlagsFromCBs = new JButton("Set Flags from Check Boxes");
		btnSetFlagsFromCBs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ccr.setSignFlag(cbSign.isSelected());
				ccr.setZeroFlag(cbZero.isSelected());
				ccr.setAuxilaryCarryFlag(cbAC.isSelected());
				ccr.setParityFlag(cbParity.isSelected());
				ccr.setCarryFlag(cbCarry.isSelected());
				stateDisplay.updateDisplayAllFlags();
			}
		});
		
		btnSetEachReg = new JButton("Set Each Reg from Spinners");
		btnSetEachReg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				wrs.setProgramCounter((int) hsPC.getValue());
				wrs.setStackPointer((int) hsSP.getValue());
				
				wrs.setReg(Register.A,(byte) ((int) hsA.getValue()));
				wrs.setReg(Register.B,(byte) ((int) hsB.getValue()));
				wrs.setReg(Register.C,(byte) ((int) hsC.getValue()));
				wrs.setReg(Register.D,(byte) ((int) hsD.getValue()));
				wrs.setReg(Register.E,(byte) ((int) hsE.getValue()));
				wrs.setReg(Register.H,(byte) ((int) hsH.getValue()));
				wrs.setReg(Register.L,(byte) ((int) hsL.getValue()));
				
				stateDisplay.updateDisplayAllRegisters();
	
			}
		});
		GridBagConstraints gbc_btnSetEachReg = new GridBagConstraints();
		gbc_btnSetEachReg.insets = new Insets(0, 0, 5, 0);
		gbc_btnSetEachReg.gridx = 0;
		gbc_btnSetEachReg.gridy = 1;
		panelAction.add(btnSetEachReg, gbc_btnSetEachReg);
		GridBagConstraints gbc_btnSetFlagsFromCBs = new GridBagConstraints();
		gbc_btnSetFlagsFromCBs.insets = new Insets(0, 0, 5, 0);
		gbc_btnSetFlagsFromCBs.gridx = 0;
		gbc_btnSetFlagsFromCBs.gridy = 2;
		panelAction.add(btnSetFlagsFromCBs, gbc_btnSetFlagsFromCBs);
		
		btnLoadSpinnersFromRegiters = new JButton("Load Spinners from WRS");
		btnLoadSpinnersFromRegiters.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hsPC.setValue(wrs.getProgramCounter());
				hsSP.setValue(wrs.getStackPointer());
				
				hsA.setValue(wrs.getReg(Register.A));
				hsB.setValue(wrs.getReg(Register.B));
				hsC.setValue(wrs.getReg(Register.C));
				hsD.setValue(wrs.getReg(Register.D));
				hsE.setValue(wrs.getReg(Register.E));
				hsH.setValue(wrs.getReg(Register.H));
				hsL.setValue(wrs.getReg(Register.L));

			}
		});
		GridBagConstraints gbc_btnLoadSpinnersFromRegiters = new GridBagConstraints();
		gbc_btnLoadSpinnersFromRegiters.insets = new Insets(0, 0, 5, 0);
		gbc_btnLoadSpinnersFromRegiters.gridx = 0;
		gbc_btnLoadSpinnersFromRegiters.gridy = 3;
		panelAction.add(btnLoadSpinnersFromRegiters, gbc_btnLoadSpinnersFromRegiters);
		
		btnLoadCheckBox = new JButton("Load Check Box from CCR");
		btnLoadCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cbSign.setSelected(ccr.isSignFlagSet());
				cbZero.setSelected(ccr.isZeroFlagSet());
				cbAC.setSelected(ccr.isAuxilaryCarryFlagSet());
				cbParity.setSelected(ccr.isParityFlagSet());
				cbCarry.setSelected(ccr.isCarryFlagSet());
			}
		});
		GridBagConstraints gbc_btnLoadCheckBox = new GridBagConstraints();
		gbc_btnLoadCheckBox.gridx = 0;
		gbc_btnLoadCheckBox.gridy = 4;
		panelAction.add(btnLoadCheckBox, gbc_btnLoadCheckBox);
	}
}
