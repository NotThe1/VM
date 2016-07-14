package hardware;

import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import java.awt.GridBagConstraints;

import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;

import java.awt.Color;
import java.awt.Insets;

import javax.swing.JRadioButton;

import java.awt.Font;

import javax.swing.AbstractButton;
import javax.swing.JLabel;
import javax.swing.JFormattedTextField;

import java.awt.Dimension;

import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.JSeparator;
import javax.swing.border.MatteBorder;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;

import memory.Core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.text.ParseException;

public class StateDisplay extends JPanel implements IStateDisplay {
	ConditionCodeRegister ccr = ConditionCodeRegister.getInstance();
	WorkingRegisterSet wrs = WorkingRegisterSet.getInstance();
	Core core = Core.getInstance();
	StateAdapter stateAdapter;

	private static RoundIcon redLED = new RoundIcon(Color.RED);
	private static RoundIcon grayLED = new RoundIcon(Color.GRAY);
	MaskFormatter format2HexDigits, format4HexDigits;

	// --------------------------------------------------------------------------
	@Override
	public void updateAll() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateFlags() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateRegisters() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateStackPointer() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateProgramCounter() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateSignFlag() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateZeroFlag() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateAuxCarryFlag() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateParityFlag() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateCarryFlag() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateRegisterA() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateRegisterB() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateRegisterC() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateRegisterD() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateRegisterE() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateRegisterH() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateRegisterL() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateRegisterM() {
		// TODO Auto-generated method stub

	}

	// --------------------------------------------------------------------------
	private void appClose() {

	}// appClose

	private void appInit0() {
		stateAdapter = new StateAdapter();
		
	}// appInit

	private void appInit() {
	}// appInit

	/**
	 * Create the panel.
	 */
	public StateDisplay() {
		
		try {
			format4HexDigits = new MaskFormatter("HHHH");
			format2HexDigits = new MaskFormatter("HH");
		} catch (Exception e) {
			// TODO: handle exception
		}//try
		appInit0();

		setPreferredSize(new Dimension(600, 300));
		setMinimumSize(new Dimension(600, 300));
		setSize(new Dimension(600, 300));
		setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		setLayout(null);

		JPanel panelControlRegisters = new JPanel();
		panelControlRegisters.setBounds(104, 10, 405, 80);
		panelControlRegisters.setBorder(new TitledBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null),
				"Control Registers", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, null, new Color(255, 0, 0)));
		add(panelControlRegisters);
		panelControlRegisters.setLayout(null);

		JPanel panelPC = new JPanel();
		panelPC.setBounds(6, 18, 165, 56);
		panelPC.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelControlRegisters.add(panelPC);
		panelPC.setLayout(null);

		JLabel lblPc = new JLabel("PC");
		lblPc.setBounds(12, 14, 25, 27);
		panelPC.add(lblPc);
		lblPc.setHorizontalAlignment(SwingConstants.LEFT);
		lblPc.setFont(new Font("Tahoma", Font.PLAIN, 22));

		JFormattedTextField ftfPC = new JFormattedTextField(format4HexDigits);
		ftfPC.addPropertyChangeListener("value", stateAdapter);
		ftfPC.setName(FTF_PC);
		ftfPC.setHorizontalAlignment(SwingConstants.RIGHT);	
		ftfPC.setBounds(52, 2, 110, 52);
		panelPC.add(ftfPC);
		ftfPC.setValue("0000");
		ftfPC.setFont(new Font("Tahoma", Font.PLAIN, 38));

		JPanel panelSP = new JPanel();
		panelSP.setBounds(235, 18, 165, 56);
		panelSP.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelControlRegisters.add(panelSP);
		panelSP.setLayout(null);

		JLabel lblSp = new JLabel("SP");
		lblSp.setBounds(12, 14, 24, 27);
		panelSP.add(lblSp);
		lblSp.setFont(new Font("Tahoma", Font.PLAIN, 22));

		JFormattedTextField ftfSP = new JFormattedTextField(format4HexDigits);
		ftfSP.addPropertyChangeListener("value", stateAdapter);
		ftfPC.setName(FTF_SP);
		ftfSP.setHorizontalAlignment(SwingConstants.RIGHT);
		ftfSP.setBounds(52, 2, 110, 52);
		panelSP.add(ftfSP);
		ftfSP.setValue("0000");
		ftfSP.setFont(new Font("Tahoma", Font.PLAIN, 38));

		JPanel panelGeneralPurposeRegisters = new JPanel();
		panelGeneralPurposeRegisters.setBounds(2, 110, 590, 105);
		panelGeneralPurposeRegisters.setBorder(new TitledBorder(new BevelBorder(BevelBorder.RAISED, null, null, null,
				null), "General Purpose Registers", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, null, new Color(255,
				0, 0)));
		add(panelGeneralPurposeRegisters);
		panelGeneralPurposeRegisters.setLayout(null);

		JPanel panelRegA = new JPanel();
		panelRegA.setBounds(10, 20, 64, 76);
		panelRegA.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelGeneralPurposeRegisters.add(panelRegA);
		panelRegA.setLayout(null);

		JLabel lblA = new JLabel("A");
		lblA.setBounds(2, 2, 60, 27);
		lblA.setHorizontalAlignment(SwingConstants.CENTER);
		lblA.setFont(new Font("Tahoma", Font.PLAIN, 22));
		panelRegA.add(lblA);

		JFormattedTextField ftfRegA = new JFormattedTextField(format2HexDigits);
		ftfRegA.addPropertyChangeListener("value", stateAdapter);
		ftfRegA.setName(FTF_REG_A);
		ftfRegA.setHorizontalAlignment(SwingConstants.CENTER);
		ftfRegA.setFont(new Font("Tahoma", Font.PLAIN, 38));
		ftfRegA.setBounds(2, 34, 60, 40);
		ftfRegA.setValue("00");
		panelRegA.add(ftfRegA);

		JPanel panelRegB = new JPanel();
		panelRegB.setBounds(80, 20, 64, 76);
		panelRegB.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelGeneralPurposeRegisters.add(panelRegB);
		panelRegB.setLayout(null);

		JLabel lblB = new JLabel("B");
		lblB.setBounds(2, 2, 60, 27);
		lblB.setHorizontalAlignment(SwingConstants.CENTER);
		lblB.setFont(new Font("Tahoma", Font.PLAIN, 22));
		panelRegB.add(lblB);

		JFormattedTextField ftfRegB = new JFormattedTextField(format2HexDigits);
		ftfRegB.addPropertyChangeListener("value", stateAdapter);
		ftfRegB.setName(FTF_REG_B);
		ftfRegB.setHorizontalAlignment(SwingConstants.CENTER);
		ftfRegB.setFont(new Font("Tahoma", Font.PLAIN, 38));
		ftfRegB.setBounds(2, 34, 60, 40);
		ftfRegB.setValue("00");
		panelRegB.add(ftfRegB);

		JPanel panelRegC = new JPanel();
		panelRegC.setBounds(150, 20, 64, 76);
		panelRegC.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelGeneralPurposeRegisters.add(panelRegC);
		panelRegC.setLayout(null);

		JLabel lblC = new JLabel("C");
		lblC.setBounds(2, 2, 60, 27);
		lblC.setHorizontalAlignment(SwingConstants.CENTER);
		lblC.setFont(new Font("Tahoma", Font.PLAIN, 22));
		panelRegC.add(lblC);

		JFormattedTextField ftfRegC = new JFormattedTextField(format2HexDigits);
		ftfRegC.addPropertyChangeListener("value", stateAdapter);
		ftfRegC.setName(FTF_REG_C);
		ftfRegC.setHorizontalAlignment(SwingConstants.CENTER);
		ftfRegC.setFont(new Font("Tahoma", Font.PLAIN, 38));
		ftfRegC.setBounds(2, 34, 60, 40);
		ftfRegC.setValue("00");
		panelRegC.add(ftfRegC);

		JPanel panelRegD = new JPanel();
		panelRegD.setBounds(220, 20, 64, 76);
		panelRegD.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelGeneralPurposeRegisters.add(panelRegD);
		panelRegD.setLayout(null);

		JLabel lblD = new JLabel("D");
		lblD.setBounds(2, 2, 60, 27);
		lblD.setHorizontalAlignment(SwingConstants.CENTER);
		lblD.setFont(new Font("Tahoma", Font.PLAIN, 22));
		panelRegD.add(lblD);

		JFormattedTextField ftfRegD = new JFormattedTextField(format2HexDigits);
		ftfRegD.addPropertyChangeListener("value", stateAdapter);
		ftfRegD.setName(FTF_REG_D);
		ftfRegD.setHorizontalAlignment(SwingConstants.CENTER);
		ftfRegD.setFont(new Font("Tahoma", Font.PLAIN, 38));
		ftfRegD.setBounds(2, 34, 60, 40);
		ftfRegD.setValue("00");
		panelRegD.add(ftfRegD);

		JPanel panelRegE = new JPanel();
		panelRegE.setBounds(290, 20, 64, 76);
		panelRegE.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelGeneralPurposeRegisters.add(panelRegE);
		panelRegE.setLayout(null);

		JLabel lblE = new JLabel("E");
		lblE.setBounds(2, 2, 60, 27);
		lblE.setHorizontalAlignment(SwingConstants.CENTER);
		lblE.setFont(new Font("Tahoma", Font.PLAIN, 22));
		panelRegE.add(lblE);

		JFormattedTextField ftfRegE = new JFormattedTextField(format2HexDigits);
		ftfRegE.addPropertyChangeListener("value", stateAdapter);
		ftfRegE.setName(FTF_REG_E);
		ftfRegE.setHorizontalAlignment(SwingConstants.CENTER);
		ftfRegE.setFont(new Font("Tahoma", Font.PLAIN, 38));
		ftfRegE.setBounds(2, 34, 60, 40);
		ftfRegE.setValue("00");
		panelRegE.add(ftfRegE);

		JPanel panelRegH = new JPanel();
		panelRegH.setBounds(360, 20, 64, 76);
		panelRegH.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelGeneralPurposeRegisters.add(panelRegH);
		panelRegH.setLayout(null);

		JLabel lblH = new JLabel("H");
		lblH.setBounds(2, 2, 60, 27);
		lblH.setHorizontalAlignment(SwingConstants.CENTER);
		lblH.setFont(new Font("Tahoma", Font.PLAIN, 22));
		panelRegH.add(lblH);

		JFormattedTextField ftfRegH = new JFormattedTextField(format2HexDigits);
		ftfRegH.addPropertyChangeListener("value", stateAdapter);
		ftfRegH.setName(FTF_REG_H);
		ftfRegH.setHorizontalAlignment(SwingConstants.CENTER);
		ftfRegH.setFont(new Font("Tahoma", Font.PLAIN, 38));
		ftfRegH.setBounds(2, 34, 60, 40);
		ftfRegH.setValue("00");
		panelRegH.add(ftfRegH);

		JPanel panelRegL = new JPanel();
		panelRegL.setBounds(430, 20, 64, 76);
		panelRegL.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelGeneralPurposeRegisters.add(panelRegL);
		panelRegL.setLayout(null);

		JLabel lblL = new JLabel("L");
		lblL.setBounds(2, 2, 60, 27);
		lblL.setHorizontalAlignment(SwingConstants.CENTER);
		lblL.setFont(new Font("Tahoma", Font.PLAIN, 22));
		panelRegL.add(lblL);

		JFormattedTextField ftfRegL = new JFormattedTextField(format2HexDigits);
		ftfRegL.addPropertyChangeListener("value", stateAdapter);
		ftfRegL.setName(FTF_REG_L);
		ftfRegL.setHorizontalAlignment(SwingConstants.CENTER);
		ftfRegL.setFont(new Font("Tahoma", Font.PLAIN, 38));
		ftfRegL.setBounds(2, 34, 60, 40);
		ftfRegL.setValue("00");
		panelRegL.add(ftfRegL);

		JPanel panelRegM = new JPanel();
		panelRegM.setBounds(515, 20, 64, 76);
		panelRegM.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelGeneralPurposeRegisters.add(panelRegM);
		panelRegM.setLayout(null);

		JLabel lblM = new JLabel("M");
		lblM.setBounds(2, 2, 60, 27);
		lblM.setHorizontalAlignment(SwingConstants.CENTER);
		lblM.setFont(new Font("Tahoma", Font.PLAIN, 22));
		panelRegM.add(lblM);

		JFormattedTextField ftfRegM = new JFormattedTextField(format2HexDigits);
		ftfRegM.addPropertyChangeListener("value", stateAdapter);
		ftfRegM.setName(FTF_REG_M);
		ftfRegM.setHorizontalAlignment(SwingConstants.CENTER);
		ftfRegM.setFont(new Font("Tahoma", Font.PLAIN, 38));
		ftfRegM.setBounds(2, 34, 60, 40);
		ftfRegM.setValue("00");
		panelRegM.add(ftfRegM);
		

		JPanel panelConditionCodes = new JPanel();
		panelConditionCodes.setBounds(111, 222, 371, 53);
		panelConditionCodes.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panelConditionCodes.setBorder(new TitledBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null),
				"Condition Codes", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, null, new Color(255, 0, 0)));
		add(panelConditionCodes);
		panelConditionCodes.setLayout(null);

		JRadioButton rbSign = new JRadioButton("Sign");
		rbSign.setBounds(6, 18, 57, 29);
		rbSign.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelConditionCodes.add(rbSign);

		JRadioButton rbZero = new JRadioButton("Zero");
		rbZero.setBounds(68, 18, 57, 29);
		rbZero.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelConditionCodes.add(rbZero);

		JRadioButton rdAuxCarry = new JRadioButton("Aux Carry");
		rdAuxCarry.setBounds(130, 18, 97, 29);
		rdAuxCarry.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelConditionCodes.add(rdAuxCarry);

		JRadioButton rdParity = new JRadioButton("Parity");
		rdParity.setBounds(232, 18, 65, 29);
		rdParity.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelConditionCodes.add(rdParity);

		JRadioButton rdCarry = new JRadioButton("Carry");
		rdCarry.setBounds(302, 18, 63, 29);
		rdCarry.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelConditionCodes.add(rdCarry);
	}// Constructor

	
	private final static String FTF_PC = "ftfPC"; 
	private final static String FTF_SP = "ftfPC"; 
	private final static String FTF_REG_A = "ftfRegA"; 
	private final static String FTF_REG_B = "ftfRegB"; 
	private final static String FTF_REG_C = "ftfRegC"; 
	private final static String FTF_REG_D = "ftfRegD"; 
	private final static String FTF_REG_E = "ftfRegE"; 
	private final static String FTF_REG_H = "ftfRegH"; 
	private final static String FTF_REG_L = "ftfRegL"; 
	private final static String FTF_REG_M = "ftfRegM"; 


}// class StateDisplay
