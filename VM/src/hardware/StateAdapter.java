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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFormattedTextField;
import javax.swing.JTextField;

public class StateAdapter implements PropertyChangeListener {

	@Override
	public void propertyChange(PropertyChangeEvent pce) {
		System.out.printf(" Changed Property is %s%n",pce.getPropertyName());

		if (pce.getOldValue() == null) {
			return;
		}// if null
		if (pce.getNewValue() == null) {
			return;
		}// if null
		
		JFormattedTextField ftfSource = (JFormattedTextField) pce.getSource();


		System.out.printf("Text: %S%n", ftfSource.getText());
		System.out.printf("OldValue: %s%n", pce.getOldValue());
		// System.out.printf("NewValue: %X%n", pce.getNewValue());

		// String valueText = ftfSource.getText();
//		int newValue = Integer.valueOf((String) pce.getNewValue(), 16);
		Object newValue = pce.getNewValue();
		ftfSource.setValue(pce.getNewValue());
		ftfSource.setText((String) pce.getNewValue());
	}

}// class StateAdapter
