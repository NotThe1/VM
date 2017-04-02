package ioSystem.listDevice;

import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

//import com.jgoodies.forms.factories.DefaultComponentFactory;

public class ListDevicePropertyDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();

//	private boolean setWideCarriage;
//	private int tabSize;
//	private int linesPerPage;
	
	private JRadioButton rbWideCarriage;
	private JSpinner spinnerTab;
	private JSpinner spinnerPageSize;
	private JLabel lblNewLabel;
	private JLabel lblNewLabel_1;
	private JLabel lblNewLabel_2;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ListDevicePropertyDialog dialog = new ListDevicePropertyDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			
		} catch (Exception e) {
			e.printStackTrace();
		} // try
	}// main

	private void saveProperties() {
		Preferences myPrefs = Preferences.userNodeForPackage(ListDevice.class).node("ListDevice");
		myPrefs.putBoolean("setWideCarriage", rbWideCarriage.isSelected());
		myPrefs.putInt("tabSize", (int) spinnerTab.getValue());
		myPrefs.putInt("linesPerPage", (int) spinnerPageSize.getValue());

		try {
			myPrefs.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}//try
		
		myPrefs = null;			
	}// saveProperties

	private void readProperties() {
		Preferences myPrefs = Preferences.userNodeForPackage(ListDevice.class).node("ListDevice");
		rbWideCarriage.setSelected( myPrefs.getBoolean("setWideCarriage", false));
		spinnerTab.setValue(myPrefs.getInt("tabSize", 1)); // default for CP/M
		spinnerPageSize.setValue(myPrefs.getInt("linesPerPage", 12));

		myPrefs = null;
	}// readProperties
	
//	private void loadDisplay(){
//		rbWideCarriage.setSelected(setWideCarriage);
//		spinnerTab.setValue(tabSize);
//		spinnerPageSize.setValue(linesPerPage);
//	}

	private void appInit() {
		readProperties();
//		loadDisplay();
	}// appInit

	private void appClose() {

	}// applClose

	private void appCancel() {

	}// appCancel

	/**
	 * Create the dialog.
	 */
	public ListDevicePropertyDialog() {
		setTitle("List Device Propert Dialog");
		initialize();
		appInit();
	}// Constructor
	

	private void initialize() {
		
		setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
		setBounds(100, 100, 351, 300);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 434, 0 };
		gridBagLayout.rowHeights = new int[] { 228, 33, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		GridBagConstraints gbc_contentPanel = new GridBagConstraints();
		gbc_contentPanel.fill = GridBagConstraints.BOTH;
		gbc_contentPanel.insets = new Insets(0, 0, 5, 0);
		gbc_contentPanel.gridx = 0;
		gbc_contentPanel.gridy = 0;
		getContentPane().add(contentPanel, gbc_contentPanel);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[] { 0, 0, 0, 0 };
		gbl_contentPanel.rowHeights = new int[] { 0, 0, 0, 0 };
		gbl_contentPanel.columnWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		contentPanel.setLayout(gbl_contentPanel);

		rbWideCarriage = new JRadioButton("");
		rbWideCarriage.setToolTipText("Wide Carriage = 120 Columns, Standard = 80");
		GridBagConstraints gbc_rbWideCarriage = new GridBagConstraints();
		gbc_rbWideCarriage.anchor = GridBagConstraints.EAST;
		gbc_rbWideCarriage.insets = new Insets(0, 0, 5, 5);
		gbc_rbWideCarriage.gridx = 1;
		gbc_rbWideCarriage.gridy = 0;
		contentPanel.add(rbWideCarriage, gbc_rbWideCarriage);
		
		lblNewLabel = new JLabel("Wide Carriage");
		lblNewLabel.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel.gridx = 2;
		gbc_lblNewLabel.gridy = 0;
		contentPanel.add(lblNewLabel, gbc_lblNewLabel);
		
		spinnerTab = new JSpinner();
		spinnerTab.setModel(new SpinnerNumberModel(1, 1, 40, 1));
//		spinnerTab.setModel(new SpinnerNumberModel(9, ListDevice.TAB_MIN, ListDevice.TAB_MAX, 1));
		
		GridBagConstraints gbc_spinnerTab = new GridBagConstraints();
		gbc_spinnerTab.anchor = GridBagConstraints.EAST;
		gbc_spinnerTab.insets = new Insets(0, 0, 5, 5);
		gbc_spinnerTab.gridx = 1;
		gbc_spinnerTab.gridy = 1;
		contentPanel.add(spinnerTab, gbc_spinnerTab);
		
		lblNewLabel_1 = new JLabel("Tab Size");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.SOUTHWEST;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_1.gridx = 2;
		gbc_lblNewLabel_1.gridy = 1;
		contentPanel.add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		spinnerPageSize = new JSpinner();
		spinnerPageSize.setModel(new SpinnerNumberModel(10, 10, 2560, 1));
		GridBagConstraints gbc_spinnerPageSize = new GridBagConstraints();
		gbc_spinnerPageSize.anchor = GridBagConstraints.EAST;
		gbc_spinnerPageSize.insets = new Insets(0, 0, 0, 5);
		gbc_spinnerPageSize.gridx = 1;
		gbc_spinnerPageSize.gridy = 2;
		contentPanel.add(spinnerPageSize, gbc_spinnerPageSize);
		
		lblNewLabel_2 = new JLabel("Page Size");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_2.gridx = 2;
		gbc_lblNewLabel_2.gridy = 2;
		contentPanel.add(lblNewLabel_2, gbc_lblNewLabel_2);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		GridBagConstraints gbc_buttonPane = new GridBagConstraints();
		gbc_buttonPane.anchor = GridBagConstraints.NORTH;
		gbc_buttonPane.fill = GridBagConstraints.HORIZONTAL;
		gbc_buttonPane.gridx = 0;
		gbc_buttonPane.gridy = 1;
		getContentPane().add(buttonPane, gbc_buttonPane);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveProperties();
				dispose();
			}
		});
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);
		

	}// initialize

}// class ListDevicePropertyDialog