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
import javax.swing.JLabel;
import javax.swing.JFormattedTextField;
import java.awt.Dimension;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.JSeparator;
import javax.swing.border.MatteBorder;

public class StateDisplay extends JPanel implements IStateDisplay{
	private JPanel panelControlRegisters;
	private JPanel panelGeneralPurposeRegisters;
	private JPanel panelConditionCodes;
	private static RoundIcon redLED = new RoundIcon(Color.RED);
	private static RoundIcon grayLED = new RoundIcon(Color.GRAY);
	private JRadioButton rbSign;
	private JRadioButton rbZero;
	private JRadioButton rdAuxCarry;
	private JRadioButton rdParity;
	private JRadioButton rdCarry;
	private JLabel lblPc;
	private JFormattedTextField ftfPC;
	private JLabel lblSp;
	private JFormattedTextField ftfSP;
	private JPanel panelRegA;
	private JLabel lblA;
	private JPanel panelRegAValue;
	private JFormattedTextField ftfRegA;
	private JPanel panelRegB;
	private JLabel lblB;
	private JPanel panelRegBValue;
	private JFormattedTextField ftfRegB;
	private JPanel panelRegC;
	private JLabel lblC;
	private JPanel panelRegCValue;
	private JFormattedTextField ftfRegC;
	private JPanel panelRegD;
	private JLabel lblD;
	private JPanel panelRegDValue;
	private JFormattedTextField ftfRegD;
	private JPanel panelRegE;
	private JLabel lblE;
	private JPanel panelRegEValue;
	private JFormattedTextField ftfRegE;
	private JPanel panelRegH;
	private JLabel lblH;
	private JPanel panelRegHValue;
	private JFormattedTextField ftfRegH;
	private JPanel panelRegL;
	private JLabel lblL;
	private JPanel panelRegLValue;
	private JFormattedTextField ftfRegL;
	private JPanel panelRegM;
	private JLabel lblM;
	private JPanel panelRegMValue;
	private JFormattedTextField ftfRegM;
	private JPanel panelPC;
	private JPanel panelSP;
	//--------------------------------------------------------------------------
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

	//--------------------------------------------------------------------------
	private void appClose(){
		
	}//appClose
	private void appInit(){
		
	}//appInit
	
	/**
	 * Create the panel.
	 */
	public StateDisplay() {
		setPreferredSize(new Dimension(600, 300));
		setMinimumSize(new Dimension(600, 300));
		setSize(new Dimension(600, 300));
		setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{589, 0};
		gridBagLayout.rowHeights = new int[]{80, 130, 53, 0};
		gridBagLayout.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		panelControlRegisters = new JPanel();
		panelControlRegisters.setBorder(new TitledBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null), "Control Registers", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, null, new Color(255, 0, 0)));
		GridBagConstraints gbc_panelControlRegisters = new GridBagConstraints();
		gbc_panelControlRegisters.anchor = GridBagConstraints.NORTH;
		gbc_panelControlRegisters.insets = new Insets(0, 0, 5, 0);
		gbc_panelControlRegisters.gridx = 0;
		gbc_panelControlRegisters.gridy = 0;
		add(panelControlRegisters, gbc_panelControlRegisters);
		GridBagLayout gbl_panelControlRegisters = new GridBagLayout();
		gbl_panelControlRegisters.columnWidths = new int[]{50, 80, 0, 0};
		gbl_panelControlRegisters.rowHeights = new int[]{0, 0};
		gbl_panelControlRegisters.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panelControlRegisters.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panelControlRegisters.setLayout(gbl_panelControlRegisters);
		
		panelPC = new JPanel();
		panelPC.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_panelPC = new GridBagConstraints();
		gbc_panelPC.fill = GridBagConstraints.BOTH;
		gbc_panelPC.insets = new Insets(0, 0, 0, 5);
		gbc_panelPC.gridx = 0;
		gbc_panelPC.gridy = 0;
		panelControlRegisters.add(panelPC, gbc_panelPC);
		GridBagLayout gbl_panelPC = new GridBagLayout();
		gbl_panelPC.columnWidths = new int[]{50, 0, 0};
		gbl_panelPC.rowHeights = new int[]{0, 0};
		gbl_panelPC.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_panelPC.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panelPC.setLayout(gbl_panelPC);
		
		lblPc = new JLabel("PC");
		GridBagConstraints gbc_lblPc = new GridBagConstraints();
		gbc_lblPc.insets = new Insets(0, 0, 0, 5);
		gbc_lblPc.gridx = 0;
		gbc_lblPc.gridy = 0;
		panelPC.add(lblPc, gbc_lblPc);
		lblPc.setHorizontalAlignment(SwingConstants.LEFT);
		lblPc.setFont(new Font("Tahoma", Font.PLAIN, 22));
		
		ftfPC = new JFormattedTextField();
		GridBagConstraints gbc_ftfPC = new GridBagConstraints();
		gbc_ftfPC.fill = GridBagConstraints.HORIZONTAL;
		gbc_ftfPC.gridx = 1;
		gbc_ftfPC.gridy = 0;
		panelPC.add(ftfPC, gbc_ftfPC);
		ftfPC.setText("0000");
		ftfPC.setFont(new Font("Tahoma", Font.PLAIN, 38));
		
		panelSP = new JPanel();
		panelSP.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_panelSP = new GridBagConstraints();
		gbc_panelSP.fill = GridBagConstraints.BOTH;
		gbc_panelSP.gridx = 2;
		gbc_panelSP.gridy = 0;
		panelControlRegisters.add(panelSP, gbc_panelSP);
		GridBagLayout gbl_panelSP = new GridBagLayout();
		gbl_panelSP.columnWidths = new int[]{50, 0, 0};
		gbl_panelSP.rowHeights = new int[]{0, 0};
		gbl_panelSP.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_panelSP.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panelSP.setLayout(gbl_panelSP);
		
		lblSp = new JLabel("SP");
		GridBagConstraints gbc_lblSp = new GridBagConstraints();
		gbc_lblSp.insets = new Insets(0, 0, 0, 5);
		gbc_lblSp.gridx = 0;
		gbc_lblSp.gridy = 0;
		panelSP.add(lblSp, gbc_lblSp);
		lblSp.setFont(new Font("Tahoma", Font.PLAIN, 22));
		
		ftfSP = new JFormattedTextField();
		GridBagConstraints gbc_ftfSP = new GridBagConstraints();
		gbc_ftfSP.fill = GridBagConstraints.HORIZONTAL;
		gbc_ftfSP.gridx = 1;
		gbc_ftfSP.gridy = 0;
		panelSP.add(ftfSP, gbc_ftfSP);
		ftfSP.setText("0000");
		ftfSP.setFont(new Font("Tahoma", Font.PLAIN, 38));
		
		panelGeneralPurposeRegisters = new JPanel();
		panelGeneralPurposeRegisters.setBorder(new TitledBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null), "General Purpose Registers", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, null, new Color(255, 0, 0)));
		GridBagConstraints gbc_panelGeneralPurposeRegisters = new GridBagConstraints();
		gbc_panelGeneralPurposeRegisters.anchor = GridBagConstraints.NORTHWEST;
		gbc_panelGeneralPurposeRegisters.insets = new Insets(0, 0, 5, 0);
		gbc_panelGeneralPurposeRegisters.gridx = 0;
		gbc_panelGeneralPurposeRegisters.gridy = 1;
		add(panelGeneralPurposeRegisters, gbc_panelGeneralPurposeRegisters);
		GridBagLayout gbl_panelGeneralPurposeRegisters = new GridBagLayout();
		gbl_panelGeneralPurposeRegisters.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panelGeneralPurposeRegisters.rowHeights = new int[]{0, 0, 0};
		gbl_panelGeneralPurposeRegisters.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panelGeneralPurposeRegisters.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panelGeneralPurposeRegisters.setLayout(gbl_panelGeneralPurposeRegisters);
		
		panelRegA = new JPanel();
		panelRegA.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_panelRegA = new GridBagConstraints();
		gbc_panelRegA.insets = new Insets(0, 0, 0, 5);
		gbc_panelRegA.fill = GridBagConstraints.BOTH;
		gbc_panelRegA.gridx = 0;
		gbc_panelRegA.gridy = 1;
		panelGeneralPurposeRegisters.add(panelRegA, gbc_panelRegA);
		GridBagLayout gbl_panelRegA = new GridBagLayout();
		gbl_panelRegA.columnWidths = new int[]{0, 0};
		gbl_panelRegA.rowHeights = new int[]{0, 0, 0};
		gbl_panelRegA.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panelRegA.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panelRegA.setLayout(gbl_panelRegA);
		
		lblA = new JLabel("A");
		lblA.setHorizontalAlignment(SwingConstants.CENTER);
		lblA.setFont(new Font("Tahoma", Font.PLAIN, 22));
		GridBagConstraints gbc_lblA = new GridBagConstraints();
		gbc_lblA.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblA.insets = new Insets(0, 0, 5, 0);
		gbc_lblA.gridx = 0;
		gbc_lblA.gridy = 0;
		panelRegA.add(lblA, gbc_lblA);
		
		panelRegAValue = new JPanel();
		GridBagConstraints gbc_panelRegAValue = new GridBagConstraints();
		gbc_panelRegAValue.fill = GridBagConstraints.BOTH;
		gbc_panelRegAValue.gridx = 0;
		gbc_panelRegAValue.gridy = 1;
		panelRegA.add(panelRegAValue, gbc_panelRegAValue);
		GridBagLayout gbl_panelRegAValue = new GridBagLayout();
		gbl_panelRegAValue.columnWidths = new int[]{0, 0};
		gbl_panelRegAValue.rowHeights = new int[]{0, 0};
		gbl_panelRegAValue.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panelRegAValue.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panelRegAValue.setLayout(gbl_panelRegAValue);
		
		ftfRegA = new JFormattedTextField();
		ftfRegA.setMinimumSize(new Dimension(60, 40));
		ftfRegA.setHorizontalAlignment(SwingConstants.CENTER);
		ftfRegA.setPreferredSize(new Dimension(60, 40));
		ftfRegA.setText("00");
		ftfRegA.setFont(new Font("Tahoma", Font.PLAIN, 38));
		GridBagConstraints gbc_ftfRegA = new GridBagConstraints();
		gbc_ftfRegA.gridx = 0;
		gbc_ftfRegA.gridy = 0;
		panelRegAValue.add(ftfRegA, gbc_ftfRegA);
		
		panelRegB = new JPanel();
		panelRegB.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_panelRegB = new GridBagConstraints();
		gbc_panelRegB.insets = new Insets(0, 0, 0, 5);
		gbc_panelRegB.fill = GridBagConstraints.BOTH;
		gbc_panelRegB.gridx = 1;
		gbc_panelRegB.gridy = 1;
		panelGeneralPurposeRegisters.add(panelRegB, gbc_panelRegB);
		GridBagLayout gbl_panelRegB = new GridBagLayout();
		gbl_panelRegB.columnWidths = new int[]{0, 0};
		gbl_panelRegB.rowHeights = new int[]{0, 0, 0};
		gbl_panelRegB.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panelRegB.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panelRegB.setLayout(gbl_panelRegB);
		
		lblB = new JLabel("B");
		lblB.setHorizontalAlignment(SwingConstants.CENTER);
		lblB.setFont(new Font("Tahoma", Font.PLAIN, 22));
		GridBagConstraints gbc_lblB = new GridBagConstraints();
		gbc_lblB.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblB.insets = new Insets(0, 0, 5, 0);
		gbc_lblB.gridx = 0;
		gbc_lblB.gridy = 0;
		panelRegB.add(lblB, gbc_lblB);
		
		panelRegBValue = new JPanel();
		GridBagConstraints gbc_panelRegBValue = new GridBagConstraints();
		gbc_panelRegBValue.fill = GridBagConstraints.BOTH;
		gbc_panelRegBValue.gridx = 0;
		gbc_panelRegBValue.gridy = 1;
		panelRegB.add(panelRegBValue, gbc_panelRegBValue);
		GridBagLayout gbl_panelRegBValue = new GridBagLayout();
		gbl_panelRegBValue.columnWidths = new int[]{0, 0};
		gbl_panelRegBValue.rowHeights = new int[]{0, 0};
		gbl_panelRegBValue.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panelRegBValue.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panelRegBValue.setLayout(gbl_panelRegBValue);
		
		ftfRegB = new JFormattedTextField();
		ftfRegB.setMinimumSize(new Dimension(60, 40));
		ftfRegB.setText("00");
		ftfRegB.setPreferredSize(new Dimension(60, 40));
		ftfRegB.setHorizontalAlignment(SwingConstants.CENTER);
		ftfRegB.setFont(new Font("Tahoma", Font.PLAIN, 38));
		GridBagConstraints gbc_ftfRegB = new GridBagConstraints();
		gbc_ftfRegB.gridx = 0;
		gbc_ftfRegB.gridy = 0;
		panelRegBValue.add(ftfRegB, gbc_ftfRegB);
		
		panelRegC = new JPanel();
		panelRegC.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_panelRegC = new GridBagConstraints();
		gbc_panelRegC.insets = new Insets(0, 0, 0, 5);
		gbc_panelRegC.fill = GridBagConstraints.BOTH;
		gbc_panelRegC.gridx = 2;
		gbc_panelRegC.gridy = 1;
		panelGeneralPurposeRegisters.add(panelRegC, gbc_panelRegC);
		GridBagLayout gbl_panelRegC = new GridBagLayout();
		gbl_panelRegC.columnWidths = new int[]{0, 0};
		gbl_panelRegC.rowHeights = new int[]{0, 0, 0};
		gbl_panelRegC.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panelRegC.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panelRegC.setLayout(gbl_panelRegC);
		
		lblC = new JLabel("C");
		lblC.setHorizontalAlignment(SwingConstants.CENTER);
		lblC.setFont(new Font("Tahoma", Font.PLAIN, 22));
		GridBagConstraints gbc_lblC = new GridBagConstraints();
		gbc_lblC.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblC.insets = new Insets(0, 0, 5, 0);
		gbc_lblC.gridx = 0;
		gbc_lblC.gridy = 0;
		panelRegC.add(lblC, gbc_lblC);
		
		panelRegCValue = new JPanel();
		GridBagConstraints gbc_panelRegCValue = new GridBagConstraints();
		gbc_panelRegCValue.fill = GridBagConstraints.BOTH;
		gbc_panelRegCValue.gridx = 0;
		gbc_panelRegCValue.gridy = 1;
		panelRegC.add(panelRegCValue, gbc_panelRegCValue);
		GridBagLayout gbl_panelRegCValue = new GridBagLayout();
		gbl_panelRegCValue.columnWidths = new int[]{0, 0};
		gbl_panelRegCValue.rowHeights = new int[]{0, 0};
		gbl_panelRegCValue.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panelRegCValue.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panelRegCValue.setLayout(gbl_panelRegCValue);
		
		ftfRegC = new JFormattedTextField();
		ftfRegC.setMinimumSize(new Dimension(60, 40));
		ftfRegC.setText("00");
		ftfRegC.setPreferredSize(new Dimension(60, 40));
		ftfRegC.setHorizontalAlignment(SwingConstants.CENTER);
		ftfRegC.setFont(new Font("Tahoma", Font.PLAIN, 38));
		GridBagConstraints gbc_ftfRegC = new GridBagConstraints();
		gbc_ftfRegC.gridx = 0;
		gbc_ftfRegC.gridy = 0;
		panelRegCValue.add(ftfRegC, gbc_ftfRegC);
		
		panelRegD = new JPanel();
		panelRegD.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_panelRegD = new GridBagConstraints();
		gbc_panelRegD.insets = new Insets(0, 0, 0, 5);
		gbc_panelRegD.fill = GridBagConstraints.BOTH;
		gbc_panelRegD.gridx = 3;
		gbc_panelRegD.gridy = 1;
		panelGeneralPurposeRegisters.add(panelRegD, gbc_panelRegD);
		GridBagLayout gbl_panelRegD = new GridBagLayout();
		gbl_panelRegD.columnWidths = new int[]{0, 0};
		gbl_panelRegD.rowHeights = new int[]{0, 0, 0};
		gbl_panelRegD.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panelRegD.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panelRegD.setLayout(gbl_panelRegD);
		
		lblD = new JLabel("D");
		lblD.setHorizontalAlignment(SwingConstants.CENTER);
		lblD.setFont(new Font("Tahoma", Font.PLAIN, 22));
		GridBagConstraints gbc_lblD = new GridBagConstraints();
		gbc_lblD.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblD.insets = new Insets(0, 0, 5, 0);
		gbc_lblD.gridx = 0;
		gbc_lblD.gridy = 0;
		panelRegD.add(lblD, gbc_lblD);
		
		panelRegDValue = new JPanel();
		GridBagConstraints gbc_panelRegDValue = new GridBagConstraints();
		gbc_panelRegDValue.fill = GridBagConstraints.BOTH;
		gbc_panelRegDValue.gridx = 0;
		gbc_panelRegDValue.gridy = 1;
		panelRegD.add(panelRegDValue, gbc_panelRegDValue);
		GridBagLayout gbl_panelRegDValue = new GridBagLayout();
		gbl_panelRegDValue.columnWidths = new int[]{0, 0};
		gbl_panelRegDValue.rowHeights = new int[]{0, 0};
		gbl_panelRegDValue.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panelRegDValue.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panelRegDValue.setLayout(gbl_panelRegDValue);
		
		ftfRegD = new JFormattedTextField();
		ftfRegD.setMinimumSize(new Dimension(60, 40));
		ftfRegD.setText("00");
		ftfRegD.setPreferredSize(new Dimension(60, 40));
		ftfRegD.setHorizontalAlignment(SwingConstants.CENTER);
		ftfRegD.setFont(new Font("Tahoma", Font.PLAIN, 38));
		GridBagConstraints gbc_ftfRegD = new GridBagConstraints();
		gbc_ftfRegD.gridx = 0;
		gbc_ftfRegD.gridy = 0;
		panelRegDValue.add(ftfRegD, gbc_ftfRegD);
		
		panelRegE = new JPanel();
		panelRegE.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_panelRegE = new GridBagConstraints();
		gbc_panelRegE.insets = new Insets(0, 0, 0, 5);
		gbc_panelRegE.fill = GridBagConstraints.BOTH;
		gbc_panelRegE.gridx = 4;
		gbc_panelRegE.gridy = 1;
		panelGeneralPurposeRegisters.add(panelRegE, gbc_panelRegE);
		GridBagLayout gbl_panelRegE = new GridBagLayout();
		gbl_panelRegE.columnWidths = new int[]{0, 0};
		gbl_panelRegE.rowHeights = new int[]{0, 0, 0};
		gbl_panelRegE.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panelRegE.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panelRegE.setLayout(gbl_panelRegE);
		
		lblE = new JLabel("E");
		lblE.setHorizontalAlignment(SwingConstants.CENTER);
		lblE.setFont(new Font("Tahoma", Font.PLAIN, 22));
		GridBagConstraints gbc_lblE = new GridBagConstraints();
		gbc_lblE.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblE.insets = new Insets(0, 0, 5, 0);
		gbc_lblE.gridx = 0;
		gbc_lblE.gridy = 0;
		panelRegE.add(lblE, gbc_lblE);
		
		panelRegEValue = new JPanel();
		GridBagConstraints gbc_panelRegEValue = new GridBagConstraints();
		gbc_panelRegEValue.fill = GridBagConstraints.BOTH;
		gbc_panelRegEValue.gridx = 0;
		gbc_panelRegEValue.gridy = 1;
		panelRegE.add(panelRegEValue, gbc_panelRegEValue);
		GridBagLayout gbl_panelRegEValue = new GridBagLayout();
		gbl_panelRegEValue.columnWidths = new int[]{0, 0};
		gbl_panelRegEValue.rowHeights = new int[]{0, 0};
		gbl_panelRegEValue.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panelRegEValue.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panelRegEValue.setLayout(gbl_panelRegEValue);
		
		ftfRegE = new JFormattedTextField();
		ftfRegE.setMinimumSize(new Dimension(60, 40));
		ftfRegE.setText("00");
		ftfRegE.setPreferredSize(new Dimension(60, 40));
		ftfRegE.setHorizontalAlignment(SwingConstants.CENTER);
		ftfRegE.setFont(new Font("Tahoma", Font.PLAIN, 38));
		GridBagConstraints gbc_ftfRegE = new GridBagConstraints();
		gbc_ftfRegE.gridx = 0;
		gbc_ftfRegE.gridy = 0;
		panelRegEValue.add(ftfRegE, gbc_ftfRegE);
		
		panelRegH = new JPanel();
		panelRegH.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_panelRegH = new GridBagConstraints();
		gbc_panelRegH.insets = new Insets(0, 0, 0, 5);
		gbc_panelRegH.fill = GridBagConstraints.BOTH;
		gbc_panelRegH.gridx = 5;
		gbc_panelRegH.gridy = 1;
		panelGeneralPurposeRegisters.add(panelRegH, gbc_panelRegH);
		GridBagLayout gbl_panelRegH = new GridBagLayout();
		gbl_panelRegH.columnWidths = new int[]{0, 0};
		gbl_panelRegH.rowHeights = new int[]{0, 0, 0};
		gbl_panelRegH.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panelRegH.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panelRegH.setLayout(gbl_panelRegH);
		
		lblH = new JLabel("H");
		lblH.setHorizontalAlignment(SwingConstants.CENTER);
		lblH.setFont(new Font("Tahoma", Font.PLAIN, 22));
		GridBagConstraints gbc_lblH = new GridBagConstraints();
		gbc_lblH.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblH.insets = new Insets(0, 0, 5, 0);
		gbc_lblH.gridx = 0;
		gbc_lblH.gridy = 0;
		panelRegH.add(lblH, gbc_lblH);
		
		panelRegHValue = new JPanel();
		GridBagConstraints gbc_panelRegHValue = new GridBagConstraints();
		gbc_panelRegHValue.fill = GridBagConstraints.BOTH;
		gbc_panelRegHValue.gridx = 0;
		gbc_panelRegHValue.gridy = 1;
		panelRegH.add(panelRegHValue, gbc_panelRegHValue);
		GridBagLayout gbl_panelRegHValue = new GridBagLayout();
		gbl_panelRegHValue.columnWidths = new int[]{0, 0};
		gbl_panelRegHValue.rowHeights = new int[]{0, 0};
		gbl_panelRegHValue.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panelRegHValue.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panelRegHValue.setLayout(gbl_panelRegHValue);
		
		ftfRegH = new JFormattedTextField();
		ftfRegH.setMinimumSize(new Dimension(60, 40));
		ftfRegH.setText("00");
		ftfRegH.setPreferredSize(new Dimension(60, 40));
		ftfRegH.setHorizontalAlignment(SwingConstants.CENTER);
		ftfRegH.setFont(new Font("Tahoma", Font.PLAIN, 38));
		GridBagConstraints gbc_ftfRegH = new GridBagConstraints();
		gbc_ftfRegH.gridx = 0;
		gbc_ftfRegH.gridy = 0;
		panelRegHValue.add(ftfRegH, gbc_ftfRegH);
		
		panelRegL = new JPanel();
		panelRegL.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_panelRegL = new GridBagConstraints();
		gbc_panelRegL.insets = new Insets(0, 0, 0, 5);
		gbc_panelRegL.fill = GridBagConstraints.BOTH;
		gbc_panelRegL.gridx = 6;
		gbc_panelRegL.gridy = 1;
		panelGeneralPurposeRegisters.add(panelRegL, gbc_panelRegL);
		GridBagLayout gbl_panelRegL = new GridBagLayout();
		gbl_panelRegL.columnWidths = new int[]{0, 0};
		gbl_panelRegL.rowHeights = new int[]{0, 0, 0};
		gbl_panelRegL.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panelRegL.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panelRegL.setLayout(gbl_panelRegL);
		
		lblL = new JLabel("L");
		lblL.setHorizontalAlignment(SwingConstants.CENTER);
		lblL.setFont(new Font("Tahoma", Font.PLAIN, 22));
		GridBagConstraints gbc_lblL = new GridBagConstraints();
		gbc_lblL.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblL.insets = new Insets(0, 0, 5, 0);
		gbc_lblL.gridx = 0;
		gbc_lblL.gridy = 0;
		panelRegL.add(lblL, gbc_lblL);
		
		panelRegLValue = new JPanel();
		GridBagConstraints gbc_panelRegLValue = new GridBagConstraints();
		gbc_panelRegLValue.fill = GridBagConstraints.BOTH;
		gbc_panelRegLValue.gridx = 0;
		gbc_panelRegLValue.gridy = 1;
		panelRegL.add(panelRegLValue, gbc_panelRegLValue);
		GridBagLayout gbl_panelRegLValue = new GridBagLayout();
		gbl_panelRegLValue.columnWidths = new int[]{0, 0};
		gbl_panelRegLValue.rowHeights = new int[]{0, 0};
		gbl_panelRegLValue.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panelRegLValue.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panelRegLValue.setLayout(gbl_panelRegLValue);
		
		ftfRegL = new JFormattedTextField();
		ftfRegL.setMinimumSize(new Dimension(60, 40));
		ftfRegL.setText("00");
		ftfRegL.setPreferredSize(new Dimension(60, 40));
		ftfRegL.setHorizontalAlignment(SwingConstants.CENTER);
		ftfRegL.setFont(new Font("Tahoma", Font.PLAIN, 38));
		GridBagConstraints gbc_ftfRegL = new GridBagConstraints();
		gbc_ftfRegL.gridx = 0;
		gbc_ftfRegL.gridy = 0;
		panelRegLValue.add(ftfRegL, gbc_ftfRegL);
		
		panelRegM = new JPanel();
		panelRegM.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_panelRegM = new GridBagConstraints();
		gbc_panelRegM.fill = GridBagConstraints.BOTH;
		gbc_panelRegM.gridx = 8;
		gbc_panelRegM.gridy = 1;
		panelGeneralPurposeRegisters.add(panelRegM, gbc_panelRegM);
		GridBagLayout gbl_panelRegM = new GridBagLayout();
		gbl_panelRegM.columnWidths = new int[]{0, 0};
		gbl_panelRegM.rowHeights = new int[]{0, 0, 0};
		gbl_panelRegM.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panelRegM.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panelRegM.setLayout(gbl_panelRegM);
		
		lblM = new JLabel("M");
		lblM.setHorizontalAlignment(SwingConstants.CENTER);
		lblM.setFont(new Font("Tahoma", Font.PLAIN, 22));
		GridBagConstraints gbc_lblM = new GridBagConstraints();
		gbc_lblM.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblM.insets = new Insets(0, 0, 5, 0);
		gbc_lblM.gridx = 0;
		gbc_lblM.gridy = 0;
		panelRegM.add(lblM, gbc_lblM);
		
		panelRegMValue = new JPanel();
		GridBagConstraints gbc_panelRegMValue = new GridBagConstraints();
		gbc_panelRegMValue.fill = GridBagConstraints.BOTH;
		gbc_panelRegMValue.gridx = 0;
		gbc_panelRegMValue.gridy = 1;
		panelRegM.add(panelRegMValue, gbc_panelRegMValue);
		GridBagLayout gbl_panelRegMValue = new GridBagLayout();
		gbl_panelRegMValue.columnWidths = new int[]{0, 0};
		gbl_panelRegMValue.rowHeights = new int[]{0, 0};
		gbl_panelRegMValue.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panelRegMValue.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panelRegMValue.setLayout(gbl_panelRegMValue);
		
		ftfRegM = new JFormattedTextField();
		ftfRegM.setMinimumSize(new Dimension(60, 40));
		ftfRegM.setText("00");
		ftfRegM.setPreferredSize(new Dimension(60, 40));
		ftfRegM.setHorizontalAlignment(SwingConstants.CENTER);
		ftfRegM.setFont(new Font("Tahoma", Font.PLAIN, 38));
		GridBagConstraints gbc_ftfRegM = new GridBagConstraints();
		gbc_ftfRegM.gridx = 0;
		gbc_ftfRegM.gridy = 0;
		panelRegMValue.add(ftfRegM, gbc_ftfRegM);
		
		panelConditionCodes = new JPanel();
		panelConditionCodes.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panelConditionCodes.setBorder(new TitledBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null), "Condition Codes", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, null, new Color(255, 0, 0)));
		GridBagConstraints gbc_panelConditionCodes = new GridBagConstraints();
		gbc_panelConditionCodes.anchor = GridBagConstraints.NORTH;
		gbc_panelConditionCodes.gridx = 0;
		gbc_panelConditionCodes.gridy = 2;
		add(panelConditionCodes, gbc_panelConditionCodes);
		GridBagLayout gbl_panelConditionCodes = new GridBagLayout();
		gbl_panelConditionCodes.columnWidths = new int[]{0, 0, 0, 0, 0, 0};
		gbl_panelConditionCodes.rowHeights = new int[]{0, 0};
		gbl_panelConditionCodes.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panelConditionCodes.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panelConditionCodes.setLayout(gbl_panelConditionCodes);
		
		rbSign = new JRadioButton("Sign");
		rbSign.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GridBagConstraints gbc_rbSign = new GridBagConstraints();
		gbc_rbSign.insets = new Insets(0, 0, 0, 5);
		gbc_rbSign.gridx = 0;
		gbc_rbSign.gridy = 0;
		panelConditionCodes.add(rbSign, gbc_rbSign);
		
		rbZero = new JRadioButton("Zero");
		rbZero.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GridBagConstraints gbc_rbZero = new GridBagConstraints();
		gbc_rbZero.insets = new Insets(0, 0, 0, 5);
		gbc_rbZero.fill = GridBagConstraints.BOTH;
		gbc_rbZero.gridx = 1;
		gbc_rbZero.gridy = 0;
		panelConditionCodes.add(rbZero, gbc_rbZero);
		
		rdAuxCarry = new JRadioButton("Aux Carry");
		rdAuxCarry.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GridBagConstraints gbc_rdAuxCarry = new GridBagConstraints();
		gbc_rdAuxCarry.insets = new Insets(0, 0, 0, 5);
		gbc_rdAuxCarry.gridx = 2;
		gbc_rdAuxCarry.gridy = 0;
		panelConditionCodes.add(rdAuxCarry, gbc_rdAuxCarry);
		
		rdParity = new JRadioButton("Parity");
		rdParity.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GridBagConstraints gbc_rdParity = new GridBagConstraints();
		gbc_rdParity.insets = new Insets(0, 0, 0, 5);
		gbc_rdParity.gridx = 3;
		gbc_rdParity.gridy = 0;
		panelConditionCodes.add(rdParity, gbc_rdParity);
		
		rdCarry = new JRadioButton("Carry");
		rdCarry.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GridBagConstraints gbc_rdCarry = new GridBagConstraints();
		gbc_rdCarry.gridx = 4;
		gbc_rdCarry.gridy = 0;
		panelConditionCodes.add(rdCarry, gbc_rdCarry);
	}//Constructor


}//class StateDisplay
