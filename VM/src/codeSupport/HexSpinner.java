package codeSupport;

import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;

public class HexSpinner extends JSpinner {
	/**
 * 
 */
	private static final long serialVersionUID = 1L;
	
	HexSpinner(){
		//SpinnerNumberModel numberModel = new SpinnerNumberModel(0,0,Integer.MAX_VALUE,1);
		this(new SpinnerNumberModel(0,0,Integer.MAX_VALUE,1));
	}//Constructor

	HexSpinner(SpinnerNumberModel numberModel) {
		super(numberModel);
		JSpinner.DefaultEditor editor = (DefaultEditor) this.getEditor();
		JFormattedTextField ftf = editor.getTextField();
		ftf.setFormatterFactory(new MyFormatterFactory());
	}// constructor

	class HexFormatter extends DefaultFormatter {

		/**
	 * 
	 */
		private static final long serialVersionUID = 1L;

		public Object stringToValue(String text) throws ParseException {
			try {
				return Integer.valueOf(text, 16);
			} catch (NumberFormatException nfe) {
				throw new ParseException(text, 0);
			}// try
		}// stringToValue

		public String valueToString(Object value) throws ParseException {
			return String.format("%X", value);
		}// valueToString
	}// class HexFormatter
		// -----------------------------------------------------------------------------------------------

	class MyFormatterFactory extends DefaultFormatterFactory {
		private static final long serialVersionUID = 1L;

		public AbstractFormatter getDefaultFormatter() {
			return new HexFormatter();
		}// getDefaultFormatter
	}// class MyFormatterFactory
		// -----------------------------------------------------------------------------------------------
} // class HexSpinner
