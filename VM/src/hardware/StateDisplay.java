package hardware;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.JTextComponent;

//import memory.Core;

public class StateDisplay extends JPanel implements IStateDisplay, Runnable {
	private static final long serialVersionUID = 1L;

	ConditionCodeRegister ccr = ConditionCodeRegister.getInstance();
	WorkingRegisterSet wrs = WorkingRegisterSet.getInstance();
	// Core core = Core.getInstance();
	StateAdapter stateAdapter;

	// --------------------------------------------------------------------------
	@Override
	public void run() {
		updateDisplayAll();
	}// run

	@Override
	public void updateDisplayAll() {
		updateDisplayAllFlags();
		updateDisplayAllRegisters();
	}// updateDisplayAll

	@Override
	public void updateDisplayAllFlags() {
		updateDisplaySignFlag();
		updateDisplayZeroFlag();
		updateDisplayAuxCarryFlag();
		updateDisplayParityFlag();
		updateDisplayCarryFlag();
	}// updateDisplayAllFlags

	@Override
	public void updateDisplayAllRegisters() {
		updateDisplayStackPointer();
		updateDisplayProgramCounter();
		updateDisplayRegisterA();
		updateDisplayRegisterB();
		updateDisplayRegisterC();
		updateDisplayRegisterD();
		updateDisplayRegisterE();
		updateDisplayRegisterH();
		updateDisplayRegisterL();
	}// updateDisplayAllRegisters

	@Override
	public void updateDisplayStackPointer() {
		ftfSP.setValue(wrs.getStackPointer());
	}// updateDisplayStackPointer

	@Override
	public void updateDisplayProgramCounter() {
		ftfPC.setValue(wrs.getProgramCounter());
	}// updateDisplayProgramCounter

	@Override
	public void updateDisplaySignFlag() {
		stateAdapter.upDateConditionFlag(rbSign);
	}// updateDisplaySignFlag

	@Override
	public void updateDisplayZeroFlag() {
		stateAdapter.upDateConditionFlag(rbZero);
	}// updateDisplayZeroFlag

	@Override
	public void updateDisplayAuxCarryFlag() {
		stateAdapter.upDateConditionFlag(rbAuxCarry);
	}// updateDisplayAuxCarryFlag

	@Override
	public void updateDisplayParityFlag() {
		stateAdapter.upDateConditionFlag(rbParity);
	}// updateDisplayParityFlag

	@Override
	public void updateDisplayCarryFlag() {
		stateAdapter.upDateConditionFlag(rbCarry);
	}// updateDisplayCarryFlag

	@Override
	public void updateDisplayAcc() {
		updateDisplayRegisterA();
	}// updateAcc

	@Override
	public void updateDisplayRegisterA() {
		ftfRegA.setValue((byte) wrs.getReg(Register.A));
	}// updateDisplayRegisterA

	@Override
	public void updateDisplayRegisterB() {
		ftfRegB.setValue(wrs.getReg(Register.B));
	}// updateDisplayRegisterB

	@Override
	public void updateDisplayRegisterC() {
		ftfRegC.setValue(wrs.getReg(Register.C));
	}// updateDisplayRegisterC

	@Override
	public void updateDisplayRegisterD() {
		ftfRegD.setValue(wrs.getReg(Register.D));
	}// updateDisplayRegisterD

	@Override
	public void updateDisplayRegisterE() {
		ftfRegE.setValue(wrs.getReg(Register.E));
	}// updateDisplayRegisterE

	@Override
	public void updateDisplayRegisterH() {
		ftfRegH.setValue(wrs.getReg(Register.H));
	}// updateDisplayRegisterH

	@Override
	public void updateDisplayRegisterL() {
		ftfRegL.setValue(wrs.getReg(Register.L));
	}// updateDisplayRegisterL

	// --------------------------------------------------------------------------
	// private void appClose() {
	//
	// }// appClose
	//
	// private void appInit() {
	// }// appInit

	/**
	 * Create the panel.
	 */
	public StateDisplay() {
		
		Font fontDigital = null;
		URL fontUrl = StateDisplay.class.getResource("/hardware/resources/Digit.ttf");
		try {
			 fontDigital = Font.createFont(Font.TRUETYPE_FONT,fontUrl.openStream());
			 fontDigital = fontDigital.deriveFont(Font.BOLD,DIGITAL_FONT_SIZE);
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		ge.registerFont(fontDigital);
		} catch (FontFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//try

		stateAdapter = new StateAdapter();
		HexFormatter format2HexDigits = new HexFormatter(2);
		HexFormatter format4HexDigits = new HexFormatter(4);
		RegisterLimitVerifier registerLimitVerifierByte = new RegisterLimitVerifier(0XFF);
		RegisterLimitVerifier registerLimitVerifierWord = new RegisterLimitVerifier(0XFFFF);

		setPreferredSize(new Dimension(600, 300));
		setMinimumSize(new Dimension(600, 300));
		setSize(new Dimension(600, 300));
		setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		setLayout(null);

		JPanel panelControlRegisters = new JPanel();
		panelControlRegisters.setBounds(97, 10, 405, 80);
		panelControlRegisters.setBorder(new TitledBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null),
				"Control Registers", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, null, Color.BLUE));
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

		ftfPC = new JFormattedTextField(format4HexDigits);
		ftfPC.setForeground(new Color(220, 20, 60));
		ftfPC.setInputVerifier(registerLimitVerifierWord);
		ftfPC.addPropertyChangeListener("value", stateAdapter);
		ftfPC.addFocusListener(stateAdapter);
		ftfPC.setName(FTF_PC);
		ftfPC.setHorizontalAlignment(SwingConstants.LEFT);
		ftfPC.setBounds(52, 2, 110, 52);
		panelPC.add(ftfPC);
		ftfPC.setValue(0000);
//		ftfPC.setFont(new Font("Tahoma", Font.PLAIN, 38));
		ftfPC.setFont(fontDigital);

		JPanel panelSP = new JPanel();
		panelSP.setBounds(235, 18, 165, 56);
		panelSP.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelControlRegisters.add(panelSP);
		panelSP.setLayout(null);

		JLabel lblSp = new JLabel("SP");
		lblSp.setBounds(12, 14, 24, 27);
		panelSP.add(lblSp);
		lblSp.setFont(new Font("Tahoma", Font.PLAIN, 22));

		ftfSP = new JFormattedTextField(format4HexDigits);
		ftfSP.setForeground(new Color(220, 20, 60));
		ftfSP.setInputVerifier(registerLimitVerifierWord);
		ftfSP.addPropertyChangeListener("value", stateAdapter);
		ftfSP.addFocusListener(stateAdapter);
		ftfSP.setName(FTF_SP);
		ftfSP.setHorizontalAlignment(SwingConstants.LEFT);
		ftfSP.setBounds(52, 2, 110, 52);
		panelSP.add(ftfSP);
		ftfSP.setValue(0000);
//		ftfSP.setFont(new Font("Tahoma", Font.PLAIN, 38));
		ftfSP.setFont(fontDigital);

		JPanel panelGeneralPurposeRegisters = new JPanel();
		panelGeneralPurposeRegisters.setBounds(45, 100, 510, 115);
		panelGeneralPurposeRegisters.setBorder(new TitledBorder(new BevelBorder(BevelBorder.RAISED, null, null, null,
				null), "General Purpose Registers", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, null, Color.BLUE));
		add(panelGeneralPurposeRegisters);
		panelGeneralPurposeRegisters.setLayout(null);

		JPanel panelRegA = new JPanel();
		panelRegA.setBounds(10, 20, 64, 85);
		panelRegA.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelGeneralPurposeRegisters.add(panelRegA);
		panelRegA.setLayout(null);

		JLabel lblA = new JLabel("A");
		lblA.setBounds(2, 2, 60, 27);
		lblA.setHorizontalAlignment(SwingConstants.CENTER);
		lblA.setFont(new Font("Tahoma", Font.PLAIN, 22));
		panelRegA.add(lblA);

		ftfRegA = new JFormattedTextField(format2HexDigits);
		ftfRegA.setForeground(new Color(220, 20, 60));
		ftfRegA.setInputVerifier(registerLimitVerifierByte);
		ftfRegA.addPropertyChangeListener("value", stateAdapter);
		ftfRegA.addFocusListener(stateAdapter);
		ftfRegA.setName(FTF_REG_A);
		ftfRegA.setHorizontalAlignment(SwingConstants.LEFT);
//		ftfRegA.setFont(new Font("Tahoma", Font.PLAIN, 38));
		ftfRegA.setFont(fontDigital);
		ftfRegA.setBounds(2, 34, 60, 52);
		ftfRegA.setValue((byte) 00);
		panelRegA.add(ftfRegA);

		JPanel panelRegB = new JPanel();
		panelRegB.setBounds(81, 20, 64, 85);
		panelRegB.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelGeneralPurposeRegisters.add(panelRegB);
		panelRegB.setLayout(null);

		JLabel lblB = new JLabel("B");
		lblB.setBounds(2, 2, 60, 27);
		lblB.setHorizontalAlignment(SwingConstants.CENTER);
		lblB.setFont(new Font("Tahoma", Font.PLAIN, 22));
		panelRegB.add(lblB);

		ftfRegB = new JFormattedTextField(format2HexDigits);
		ftfRegB.setForeground(new Color(220, 20, 60));
		ftfRegB.setInputVerifier(registerLimitVerifierByte);
		ftfRegB.addPropertyChangeListener("value", stateAdapter);
		ftfRegB.addFocusListener(stateAdapter);
		ftfRegB.setName(FTF_REG_B);
		ftfRegB.setHorizontalAlignment(SwingConstants.LEFT);
//		ftfRegB.setFont(new Font("Tahoma", Font.PLAIN, 38));
		ftfRegB.setFont(fontDigital);
		ftfRegB.setBounds(2, 34, 60, 52);
		ftfRegB.setValue((byte) 00);
		panelRegB.add(ftfRegB);

		JPanel panelRegC = new JPanel();
		panelRegC.setBounds(152, 20, 64, 85);
		panelRegC.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelGeneralPurposeRegisters.add(panelRegC);
		panelRegC.setLayout(null);

		JLabel lblC = new JLabel("C");
		lblC.setBounds(2, 2, 60, 27);
		lblC.setHorizontalAlignment(SwingConstants.CENTER);
		lblC.setFont(new Font("Tahoma", Font.PLAIN, 22));
		panelRegC.add(lblC);

		ftfRegC = new JFormattedTextField(format2HexDigits);
		ftfRegC.setForeground(new Color(220, 20, 60));
		ftfRegC.setInputVerifier(registerLimitVerifierByte);
		ftfRegC.addPropertyChangeListener("value", stateAdapter);
		ftfRegC.addFocusListener(stateAdapter);
		ftfRegC.setName(FTF_REG_C);
		ftfRegC.setHorizontalAlignment(SwingConstants.LEFT);
//		ftfRegC.setFont(new Font("Tahoma", Font.PLAIN, 38));
		ftfRegC.setFont(fontDigital);
		ftfRegC.setBounds(2, 34, 60, 52);
		ftfRegC.setValue((byte) 00);
		panelRegC.add(ftfRegC);

		JPanel panelRegD = new JPanel();
		panelRegD.setBounds(223, 20, 64, 85);
		panelRegD.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelGeneralPurposeRegisters.add(panelRegD);
		panelRegD.setLayout(null);

		JLabel lblD = new JLabel("D");
		lblD.setBounds(2, 2, 60, 27);
		lblD.setHorizontalAlignment(SwingConstants.CENTER);
		lblD.setFont(new Font("Tahoma", Font.PLAIN, 22));
		panelRegD.add(lblD);

		ftfRegD = new JFormattedTextField(format2HexDigits);
		ftfRegD.setForeground(new Color(220, 20, 60));
		ftfRegD.setInputVerifier(registerLimitVerifierByte);
		ftfRegD.addPropertyChangeListener("value", stateAdapter);
		ftfRegD.addFocusListener(stateAdapter);
		ftfRegD.setName(FTF_REG_D);
		ftfRegD.setHorizontalAlignment(SwingConstants.LEFT);
//		ftfRegD.setFont(new Font("Tahoma", Font.PLAIN, 38));
		ftfRegD.setFont(fontDigital);
		ftfRegD.setBounds(2, 34, 60, 52);
		ftfRegD.setValue((byte) 00);
		panelRegD.add(ftfRegD);

		JPanel panelRegE = new JPanel();
		panelRegE.setBounds(294, 20, 64, 85);
		panelRegE.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelGeneralPurposeRegisters.add(panelRegE);
		panelRegE.setLayout(null);

		JLabel lblE = new JLabel("E");
		lblE.setBounds(2, 2, 60, 27);
		lblE.setHorizontalAlignment(SwingConstants.CENTER);
		lblE.setFont(new Font("Tahoma", Font.PLAIN, 22));
		panelRegE.add(lblE);

		ftfRegE = new JFormattedTextField(format2HexDigits);
		ftfRegE.setForeground(new Color(220, 20, 60));
		ftfRegE.setInputVerifier(registerLimitVerifierByte);
		ftfRegE.addPropertyChangeListener("value", stateAdapter);
		ftfRegE.addFocusListener(stateAdapter);
		ftfRegE.setName(FTF_REG_E);
		ftfRegE.setHorizontalAlignment(SwingConstants.LEFT);
//		ftfRegE.setFont(new Font("Tahoma", Font.PLAIN, 38));
		ftfRegE.setFont(fontDigital);
		ftfRegE.setBounds(2, 34, 60, 52);
		ftfRegE.setValue((byte) 00);
		panelRegE.add(ftfRegE);

		JPanel panelRegH = new JPanel();
		panelRegH.setBounds(365, 20, 64, 85);
		panelRegH.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelGeneralPurposeRegisters.add(panelRegH);
		panelRegH.setLayout(null);

		JLabel lblH = new JLabel("H");
		lblH.setBounds(2, 2, 60, 27);
		lblH.setHorizontalAlignment(SwingConstants.CENTER);
		lblH.setFont(new Font("Tahoma", Font.PLAIN, 22));
		panelRegH.add(lblH);

		ftfRegH = new JFormattedTextField(format2HexDigits);
		ftfRegH.setForeground(new Color(220, 20, 60));
		ftfRegH.setInputVerifier(registerLimitVerifierByte);
		ftfRegH.addPropertyChangeListener("value", stateAdapter);
		ftfRegH.addFocusListener(stateAdapter);
		ftfRegH.setName(FTF_REG_H);
		ftfRegH.setHorizontalAlignment(SwingConstants.LEFT);
//		ftfRegH.setFont(new Font("Tahoma", Font.PLAIN, 38));
		ftfRegH.setFont(fontDigital);
		ftfRegH.setBounds(2, 34, 60, 52);
		ftfRegH.setValue((byte) 00);
		panelRegH.add(ftfRegH);

		JPanel panelRegL = new JPanel();
		panelRegL.setBounds(436, 20, 64, 85);
		panelRegL.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelGeneralPurposeRegisters.add(panelRegL);
		panelRegL.setLayout(null);

		JLabel lblL = new JLabel("L");
		lblL.setBounds(2, 2, 60, 27);
		lblL.setHorizontalAlignment(SwingConstants.CENTER);
		lblL.setFont(new Font("Tahoma", Font.PLAIN, 22));
		panelRegL.add(lblL);

		ftfRegL = new JFormattedTextField(format2HexDigits);
		ftfRegL.setForeground(new Color(220, 20, 60));
		ftfRegL.setInputVerifier(registerLimitVerifierByte);
		ftfRegL.addPropertyChangeListener("value", stateAdapter);
		ftfRegL.addFocusListener(stateAdapter);
		ftfRegL.setName(FTF_REG_L);
		ftfRegL.setHorizontalAlignment(SwingConstants.LEFT);
//		ftfRegL.setFont(new Font("Tahoma", Font.PLAIN, 38));
		ftfRegL.setFont(fontDigital);
		ftfRegL.setBounds(2, 34, 60, 52);
		ftfRegL.setValue((byte) 00);
		panelRegL.add(ftfRegL);

		JPanel panelConditionCodes = new JPanel();
		panelConditionCodes.setBounds(114, 222, 371, 53);
		panelConditionCodes.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panelConditionCodes.setBorder(new TitledBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null),
				"Condition Codes", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, null, Color.BLUE));
		add(panelConditionCodes);
		panelConditionCodes.setLayout(null);

		rbSign = new JRadioButton("Sign");
		rbSign.setName(RB_SIGN);
		rbSign.addActionListener(stateAdapter);
		rbSign.setBounds(6, 18, 57, 29);
		rbSign.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelConditionCodes.add(rbSign);

		rbZero = new JRadioButton("Zero");
		rbZero.setName(RB_ZERO);
		rbZero.addActionListener(stateAdapter);
		rbZero.setBounds(68, 18, 57, 29);
		rbZero.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelConditionCodes.add(rbZero);

		rbAuxCarry = new JRadioButton("Aux Carry");
		rbAuxCarry.setName(RB_AUX_CARRY);
		rbAuxCarry.addActionListener(stateAdapter);
		rbAuxCarry.setBounds(130, 18, 97, 29);
		rbAuxCarry.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelConditionCodes.add(rbAuxCarry);

		rbParity = new JRadioButton("Parity");
		rbParity.setName(RB_PARITY);
		rbParity.addActionListener(stateAdapter);
		rbParity.setBounds(232, 18, 65, 29);
		rbParity.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelConditionCodes.add(rbParity);

		rbCarry = new JRadioButton("Carry");
		rbCarry.setName(RB_CARRY);
		rbCarry.addActionListener(stateAdapter);
		rbCarry.setBounds(302, 18, 63, 29);
		rbCarry.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelConditionCodes.add(rbCarry);
	}// Constructor
		// ----------------------------- Classes ----------------------------------------

	/**
	 * This formatter is used to handle byte and word values in JFormattedTextFields. If it is constructed with
	 * parameters set to 2 the return value is a byte. Otherwise it is an Integer
	 * 
	 * @author Frank Martyn
	 *
	 */

	private static class HexFormatter extends DefaultFormatter {
		private static final long serialVersionUID = 1L;
		private String formatString;
		private int numberOfDigits;

		public HexFormatter(int numberOfDigits) {
			// default to 4 digits bad argument
			this.numberOfDigits = numberOfDigits <= 0 ? 4 : numberOfDigits;
			formatString = "%0" + numberOfDigits + "X";
		}// Constructor

		// public HexFormatter() {
		// this(4);
		// }// Constructor

		// public Object stringToValue(String text) throws ParseException {
		public Number stringToValue(String text) throws ParseException {

			String workingText = text.length() > 4 ? text.substring(0, 4) : text;
			try {
				if (this.numberOfDigits == 2) {
					return (byte) ((int) (Integer.valueOf(workingText, 16)));
				} else {
					return Integer.valueOf(workingText, 16);
				}// if Byte or Integer

			} catch (NumberFormatException nfe) {
				throw new ParseException(text, 0);
			}// try
		}// stringToValue

		public String valueToString(Object value) throws ParseException {
			String ans = String.format(formatString, value);
			if (ans.length() > numberOfDigits) {
				ans =ans.substring(ans.length() - numberOfDigits);
			}// if
			return ans;
		}// valueToString
	}// class HexFormatter

	// ---
	/**
	 * 
	 * @author Frank Martyn
	 * 
	 *         This verifier is used to limit the input value to only Hex values.
	 *
	 */

	public class RegisterLimitVerifier extends InputVerifier {

		int Kbytes = 1024;
		private final Color INVALID_COLOR = Color.BLACK;
		private final Color VALID_COLOR = new Color(220, 20, 60);
		private int maxValue; // maximum value

		public RegisterLimitVerifier(int registerSize) {
			this.setMaxValue(registerSize); // Register Size is in bytes
		}// Constructor - MemoryLimitVerifier(memorySize)

		@Override
		public boolean verify(JComponent jc) {
			JTextComponent textComponent = (JTextComponent) jc;
			try {
				String text = textComponent.getText();
				Integer val = Integer.valueOf(text, 16);
				if (val > maxValue) {
					textComponent.setForeground(INVALID_COLOR);
					textComponent.selectAll();
					textComponent.setSelectedTextColor(INVALID_COLOR);
					return false;
				}// if
			} catch (Exception e) {
				textComponent.setForeground(INVALID_COLOR);
				textComponent.selectAll();
				textComponent.setSelectedTextColor(INVALID_COLOR);
				return false;
			}// try - catch
			textComponent.setForeground(VALID_COLOR);
			textComponent.setSelectedTextColor(VALID_COLOR);

			return true;
		}// verify

		public int getMaxValue() {
			return maxValue;
		}// getMaxValue

		public void setMaxValue(int maxValue) {
			this.maxValue = maxValue;
		}// setMaxValue

	}// class MemoryLimitVerifier

	// ---

	// class RoundIcon implements Icon {
	// Color color;
	//
	// public RoundIcon(Color c) {
	// color = c;
	// }// Constructor
	//
	// @Override
	// public void paintIcon(Component c, Graphics g,
	// int x, int y) {
	// g.setColor(color);
	// g.fillOval(
	// x, y, getIconWidth(), getIconHeight());
	// }// paintIcon
	//
	// @Override
	// public int getIconHeight() {
	// return 10;
	// }// getIconHeigt
	//
	// @Override
	// public int getIconWidth() {
	// return 10;
	// }// getIconWidth
	//
	// }// class RoundIcon

	// ----------------------------- Classes ----------------------------------------

	public final static int DIGITAL_FONT_SIZE = 40;
	
	public final static String FTF_PC = "ftfPC";
	public final static String FTF_SP = "ftfSP";
	public final static String FTF_REG_A = "ftfRegA";
	public final static String FTF_REG_B = "ftfRegB";
	public final static String FTF_REG_C = "ftfRegC";
	public final static String FTF_REG_D = "ftfRegD";
	public final static String FTF_REG_E = "ftfRegE";
	public final static String FTF_REG_H = "ftfRegH";
	public final static String FTF_REG_L = "ftfRegL";
	public final static String FTF_REG_M = "ftfRegM";

	public final static String RB_SIGN = "rbSign";
	public final static String RB_ZERO = "rbZero";
	public final static String RB_AUX_CARRY = "rdAuxCarry";
	public final static String RB_PARITY = "rbParity";
	public final static String RB_CARRY = "rbCarry";
	private JRadioButton rbCarry;
	private JRadioButton rbParity;
	private JRadioButton rbAuxCarry;
	private JRadioButton rbZero;
	private JRadioButton rbSign;
	private JFormattedTextField ftfRegL;
	private JFormattedTextField ftfRegH;
	private JFormattedTextField ftfRegE;
	private JFormattedTextField ftfRegD;
	private JFormattedTextField ftfRegC;
	private JFormattedTextField ftfRegB;
	private JFormattedTextField ftfRegA;
	private JFormattedTextField ftfSP;
	private JFormattedTextField ftfPC;

}// class StateDisplay
// ----------------------------- Classes class StateDisplay end ----------------------------------------

