package misc;

import java.awt.EventQueue;

import javax.swing.JFrame;

import java.awt.GridBagLayout;

import javax.swing.JRadioButton;

import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Insets;

import hardware.StateAdapter;
import hardware.StateDisplay;

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

public class ButtonsGUI {

	private JFrame frame;
	private JPanel panel;
	private StateAdapter stateAdapter;
	private JFormattedTextField ftfTest;
	MaskFormatter format2HexDigits,format4HexDigits;


public class MyFormatter extends JFormattedTextField.AbstractFormatter{

	@Override
	public Object stringToValue(String string) throws ParseException {
		int value = Integer.valueOf(string, 16);
		return null;
	}

	@Override
	public String valueToString(Object value) throws ParseException {
		String v2s = String.format("%04X", (int) value);
		return null;
	}
	
}//MyFormatter
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ButtonsGUI window = new ButtonsGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void appInit() {
		stateAdapter = new StateAdapter();
		MyFormatter myFormatter = new MyFormatter();
		myFormatter.install(ftfTest);
	}

	/**
	 * Create the application.
	 */
	public ButtonsGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		try {
			format2HexDigits = new MaskFormatter("HH");
			format2HexDigits.setPlaceholder("0");
			format4HexDigits = new MaskFormatter("HHHH");
			format4HexDigits.setPlaceholder("0");
		} catch (Exception e) {
			// TODO: handle exception
		}

		
		
		frame = new JFrame();
		frame.setBounds(100, 100, 831, 639);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		frame.getContentPane().setLayout(gridBagLayout);

		JRadioButton rb1 = new JRadioButton("Rb1");
		rb1.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				System.out.printf("ftfTest: %d , %X%n", ftfTest.getValue(),ftfTest.getValue());
			}
		});
		rb1.addActionListener(new ButtonsWatch());
		GridBagConstraints gbc_rb1 = new GridBagConstraints();
		gbc_rb1.insets = new Insets(0, 0, 5, 5);
		gbc_rb1.gridx = 0;
		gbc_rb1.gridy = 1;
		frame.getContentPane().add(rb1, gbc_rb1);

		JRadioButton rb2 = new JRadioButton("Rb2");
		rb2.addActionListener(new ButtonsWatch());

		ftfTest = new JFormattedTextField(format4HexDigits);

		GridBagConstraints gbc_ftfTest = new GridBagConstraints();
		gbc_ftfTest.anchor = GridBagConstraints.NORTH;
		gbc_ftfTest.insets = new Insets(0, 0, 5, 0);
		gbc_ftfTest.fill = GridBagConstraints.HORIZONTAL;
		gbc_ftfTest.gridx = 1;
		gbc_ftfTest.gridy = 1;
		frame.getContentPane().add(ftfTest, gbc_ftfTest);
		GridBagConstraints gbc_rb2 = new GridBagConstraints();
		gbc_rb2.insets = new Insets(0, 0, 5, 5);
		gbc_rb2.gridx = 0;
		gbc_rb2.gridy = 3;
		frame.getContentPane().add(rb2, gbc_rb2);

		panel = new JPanel();
		panel.setBorder(null);
		panel.setLayout(null);
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 4;
		frame.getContentPane().add(panel, gbc_panel);

		StateDisplay stateDisplay = new StateDisplay();
		stateDisplay.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				System.out.printf("stateDisplay: %n");
			}
		});
		stateDisplay.setBounds(0, 11, 600, 300);

		panel.add(stateDisplay);
		stateDisplay.setLayout(null);
	}
}
