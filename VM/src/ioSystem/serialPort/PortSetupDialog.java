package ioSystem.serialPort;

import java.awt.FlowLayout;
//import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import jssc.SerialPortList;

public class PortSetupDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private SerialPortSettings serialPortSettings;

	public SerialPortSettings showDialog() {
		this.setVisible(true);
		return this.serialPortSettings;
	}// showDialog

	private void appInit() {
		cbBaudRate.setModel(new DefaultComboBoxModel<String>(SerialPortSettings.VALUES_BAUD_RATE));
		cbDataBits.setModel(new DefaultComboBoxModel<String>(SerialPortSettings.VALUES_DATA_BITS));
		cbStopBits.setModel(new DefaultComboBoxModel<String>(SerialPortSettings.VALUES_STOP_BITS));
		cbParity.setModel(new DefaultComboBoxModel<String>(SerialPortSettings.VALUES_PARITY));

		String[] ports = SerialPortList.getPortNames();
		for (String port : ports) {
			cbPort.addItem(port);
		} // for
			// set settings
		cbPort.setSelectedItem(serialPortSettings.getPortName());
		cbBaudRate.setSelectedItem("" + serialPortSettings.getBaudRate());
		cbDataBits.setSelectedItem("" + serialPortSettings.getDataBits());
		switch (serialPortSettings.getStopBits()) {
		case 1:
			cbStopBits.setSelectedItem("1");
			break;
		case 2:
			cbStopBits.setSelectedItem("2");
			break;
		case 3:
			cbStopBits.setSelectedItem("1.5");
			break;
		}
		cbParity.setSelectedIndex(serialPortSettings.getParity());

		cbPort.requestFocus();
	}//appInit

	/**
	 * Create the dialog.
	 */
	public PortSetupDialog(SerialPortSettings serialPortSettings) {
		this.serialPortSettings = (SerialPortSettings) serialPortSettings.clone();
		setModalityType(ModalityType.APPLICATION_MODAL);
		setResizable(false);
		setTitle("Setup Serial Connection");
		setBounds(100, 100, 612, 199);
		getContentPane().setLayout(null);
		contentPanel.setBounds(0, 0, 587, 148);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel);
		contentPanel.setLayout(null);

		JLabel lblPort = new JLabel("Port");
		lblPort.setBounds(49, 28, 46, 14);
		contentPanel.add(lblPort);

		cbPort = new JComboBox<String>();
		cbPort.setBounds(35, 58, 75, 20);
		contentPanel.add(cbPort);

		JLabel lblBaudRate = new JLabel("Baud rate");
		lblBaudRate.setBounds(151, 28, 62, 14);
		contentPanel.add(lblBaudRate);

		cbBaudRate = new JComboBox<String>();
		cbBaudRate.setMaximumRowCount(13);
		cbBaudRate.setBounds(145, 58, 75, 20);
		contentPanel.add(cbBaudRate);

		cbDataBits = new JComboBox<String>();
		cbDataBits.setBounds(255, 58, 75, 20);
		contentPanel.add(cbDataBits);

		cbStopBits = new JComboBox<String>();
		cbStopBits.setBounds(365, 58, 75, 20);
		contentPanel.add(cbStopBits);

		cbParity = new JComboBox<String>();
		cbParity.setBounds(475, 58, 75, 20);
		contentPanel.add(cbParity);

		JLabel lblParity = new JLabel("Parity");
		lblParity.setBounds(489, 28, 46, 14);
		contentPanel.add(lblParity);

		JLabel lblDataBits = new JLabel("Data bits");
		lblDataBits.setBounds(269, 28, 46, 14);
		contentPanel.add(lblDataBits);

		JLabel lblStopBits = new JLabel("Stop bits");
		lblStopBits.setBounds(379, 28, 46, 14);
		contentPanel.add(lblStopBits);

		JPanel buttonPane = new JPanel();
		buttonPane.setBounds(43, 102, 512, 33);
		contentPanel.add(buttonPane);
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JButton btnOK = new JButton("OK");
		btnOK.addActionListener(this);
		btnOK.setActionCommand("btnSave");
		buttonPane.add(btnOK);
		getRootPane().setDefaultButton(btnOK);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);
		btnCancel.setActionCommand("btnCancel");
		buttonPane.add(btnCancel);

		appInit();
	}

	// private Terminal terminal;
	private JComboBox<String> cbPort;
	private JComboBox<String> cbBaudRate;
	private JComboBox<String> cbDataBits;
	private JComboBox<String> cbStopBits;
	private JComboBox<String> cbParity;

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "btnCancel":
			dispose();
			break;
		case "btnSave":
			serialPortSettings.setPortName(cbPort.getSelectedItem().toString());
			serialPortSettings.setBaudRate(Integer.valueOf(cbBaudRate.getSelectedItem().toString()));
			serialPortSettings.setDataBits(Integer.valueOf(cbDataBits.getSelectedItem().toString()));

			switch (cbStopBits.getSelectedItem().toString()) {
			case "1":
				serialPortSettings.setStopBits(1);
				break;
			case "1.5":
				serialPortSettings.setStopBits(3);
				break;
			case "2":
				serialPortSettings.setStopBits(2);
			}// Inner switch stopBits
			serialPortSettings.setParity(cbParity.getSelectedIndex());
			dispose();
			break;
		}// Outer switch

	}// actionPerformed
}// class PortSetupDialog
