package hardware;

/**
 *  this class supports the StateDisplay Class act a s a VIEW component. It controls the update of the
 *   StateDisplay components to reflect the state of the machine. The items on the display are:
 *   <p>
 *   Working Registers A,B,C,D,E,H, & L.
 *   <P>
 *   The contents of the memory location pointed at by the HL register pair.
 *   <p>
 *   The control registers Program Counter & Stack Pointer
 *   <P>
 *   The Condition Codes Sign,Zero,Aux Carry,Parity, & Carry.
 *   
 */

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.JFormattedTextField;
import javax.swing.JRadioButton;

import utilities.InLineDisassembler;

//import javax.swing.text.JTextComponent;

public class StateAdapter implements PropertyChangeListener, FocusListener, ActionListener {

	private static RoundIcon1 redLED = new RoundIcon1(Color.GREEN);
	private static RoundIcon1 grayLED = new RoundIcon1(Color.GRAY);
	ConditionCodeRegister ccr = ConditionCodeRegister.getInstance();
	WorkingRegisterSet wrs = WorkingRegisterSet.getInstance();
	InLineDisassembler	inLineDisassembler = InLineDisassembler.getInstance();

	// ---------- PropertyChangeListener

	@Override
	public void propertyChange(PropertyChangeEvent propertyChangeEvent) {

//		System.out.printf(" Changed Property is %s%n", propertyChangeEvent.getPropertyName());

		if (propertyChangeEvent.getOldValue() == null) {
			return;
		}// if null
		if (propertyChangeEvent.getNewValue() == null) {
			return;
		}// if null
		if (propertyChangeEvent.getOldValue() == propertyChangeEvent.getNewValue()) {
			System.out.println("Old = New");
			return;
		}

		JFormattedTextField ftfSource = (JFormattedTextField) propertyChangeEvent.getSource();
		String sourceName = ftfSource.getName();

//		System.out.printf("sourceName: %S%n", sourceName);
//		System.out.printf("Text: %S%n", ftfSource.getText());
//		System.out.printf("Value: %d%n", ftfSource.getValue());
//
//		System.out.printf("OldValue: %s%n", propertyChangeEvent.getOldValue());
//		System.out.printf("Newalue: %s%n", propertyChangeEvent.getNewValue());
//		String newValueType = "Unknown";
//		if (propertyChangeEvent.getNewValue() instanceof Integer) {
//			newValueType = "Integer";
//		} else if (propertyChangeEvent.getNewValue() instanceof Byte) {
//			newValueType = "Byte";
//		}// if
//
//		System.out.printf("newValueType: %s%n", newValueType);
		
		

		switch (sourceName) {
		case StateDisplay.FTF_PC:
			wrs.setProgramCounter((int) propertyChangeEvent.getNewValue());
			EventQueue.invokeLater(inLineDisassembler);
			break;
		case StateDisplay.FTF_SP:
			wrs.setStackPointer((int) propertyChangeEvent.getNewValue());
			break;
		case StateDisplay.FTF_REG_A:
			wrs.setReg(Register.A, (byte) ((Object) propertyChangeEvent.getNewValue()));
			break;
		case StateDisplay.FTF_REG_B:
			wrs.setReg(Register.B, (byte) ((Object) propertyChangeEvent.getNewValue()));
			break;
		case StateDisplay.FTF_REG_C:
			wrs.setReg(Register.C, (byte) ((Object) propertyChangeEvent.getNewValue()));
			break;
		case StateDisplay.FTF_REG_D:
			wrs.setReg(Register.D, (byte) ((Object) propertyChangeEvent.getNewValue()));
			break;
		case StateDisplay.FTF_REG_E:
			wrs.setReg(Register.E, (byte) ((Object) propertyChangeEvent.getNewValue()));
			break;
		case StateDisplay.FTF_REG_H:
			wrs.setReg(Register.H, (byte) ((Object) propertyChangeEvent.getNewValue()));
			break;
		case StateDisplay.FTF_REG_L:
			wrs.setReg(Register.L, (byte) ((Object) propertyChangeEvent.getNewValue()));
			break;
		default:

		}// switch source Name

	}// propertyChange

	// ---------- FocusListener
	@Override
	public void focusGained(FocusEvent focusEvent) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				((javax.swing.text.JTextComponent) focusEvent.getSource()).selectAll();
			}// run
		});
	}// focusGained

	@Override
	public void focusLost(FocusEvent focusEvent) {
		// TODO Auto-generated method stub
	}// focusLost

	// ---------- ActionListener
	@Override
	public void actionPerformed(ActionEvent actionEvent) {

		JRadioButton rb = (JRadioButton) actionEvent.getSource();
		// upDateConditionFlag(rb);
		boolean isSelected = rb.isSelected();

		if (isSelected) {
			rb.setIcon(redLED);
		} else {
			rb.setIcon(grayLED);
		}// if selected

		String source = rb.getName();
		switch (source) {
		case StateDisplay.RB_SIGN:
			ccr.setSignFlag(isSelected);
			break;
		case StateDisplay.RB_ZERO:
			ccr.setZeroFlag(isSelected);
			break;
		case StateDisplay.RB_AUX_CARRY:
			ccr.setAuxilaryCarryFlag(isSelected);
			break;
		case StateDisplay.RB_PARITY:
			ccr.setParityFlag(isSelected);
			break;
		case StateDisplay.RB_CARRY:
			ccr.setCarryFlag(isSelected);
			break;
		default:
		}// switch
		upDateConditionFlag(rb);
	}// actionPerformed

	public void upDateConditionFlag(JRadioButton rb) {

		String source = rb.getName();
		switch (source) {
		case StateDisplay.RB_SIGN:
			rb.setSelected(ccr.isSignFlagSet());
			break;
		case StateDisplay.RB_ZERO:
			rb.setSelected(ccr.isZeroFlagSet());
			break;
		case StateDisplay.RB_AUX_CARRY:
			rb.setSelected(ccr.isAuxilaryCarryFlagSet());
			break;
		case StateDisplay.RB_PARITY:
			rb.setSelected(ccr.isParityFlagSet());
			break;
		case StateDisplay.RB_CARRY:
			rb.setSelected(ccr.isCarryFlagSet());
			break;
		default:
		}// switch

		if (rb.isSelected()) {
			rb.setIcon(redLED);
		} else {
			rb.setIcon(grayLED);
		}// if selected

	}// upDateConditionFlag

	class RoundIcon implements Icon {
		Color color;

		public RoundIcon(Color c) {
			color = c;
		}// Constructor

		@Override
		public void paintIcon(Component c, Graphics g,
				int x, int y) {
			g.setColor(color);
			g.fillOval(
					x, y, getIconWidth(), getIconHeight());
		}// paintIcon

		@Override
		public int getIconHeight() {
			return 11;
		}// getIconHeigt

		@Override
		public int getIconWidth() {
			return 11;
		}// getIconWidth

	}// class RoundIcon

}// class StateAdapter
