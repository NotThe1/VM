package ioSystem.listDevice;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import utilities.FontChooser;

//import com.jgoodies.forms.factories.DefaultComponentFactory;

public class ListDevicePropertyDialog extends JDialog implements ActionListener {

	private final JPanel contentPanel = new JPanel();
	Component c;
	// private boolean setWideCarriage;
	// private int tabSize;
	// private int linesPerPage;

	private JRadioButton rbLimitColumns;
	private JSpinner spinnerTab;
	private JSpinner spinnerColumns;
	private JLabel lblNewLabel;
	private JLabel lblNewLabel_1;
	private JLabel lblNewLabel_2;
	private JPanel panelFont;
	private JLabel lblFontFamily;
	private JLabel lblFontStyle;
	private JLabel lblFontSize;

	private int dialogResultValue;
	public static final int DIALOG_ERROR = 0;
	public static final int DIALOG_OK = 1;
	public static final int DIALOG_CANCEL = 2;

	public int showDialog() {
		dialogResultValue = DIALOG_ERROR;
		this.setVisible(true);
		this.dispose();
		return dialogResultValue;
	}// showDialog

	private void saveProperties() {
		Preferences myPrefs = Preferences.userNodeForPackage(ListDevice.class).node("ListDevice");
		myPrefs.putBoolean("setWideCarriage", rbLimitColumns.isSelected());
		myPrefs.putInt("tabSize", (int) spinnerTab.getValue());
		myPrefs.putInt("linesPerPage", (int) spinnerColumns.getValue());

		try {
			myPrefs.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		} // try

		myPrefs = null;
	}// saveProperties

	private void readProperties() {
		Preferences myPrefs = Preferences.userNodeForPackage(ListDevice.class).node("ListDevice");
		rbLimitColumns.setSelected(myPrefs.getBoolean("setWideCarriage", false));
		spinnerTab.setValue(myPrefs.getInt("tabSize", 1)); // default for CP/M
		spinnerColumns.setValue(myPrefs.getInt("linesPerPage", 12));

		 lblFontFamily.setText(myPrefs.get("fontFamily", "Courier New"));
		 lblFontStyle.setText(myPrefs.get("fontStyle", "Plain"));
		 lblFontSize.setText(myPrefs.get("fontSize", "13"));

		myPrefs = null;

//		Font currentFont = c.getFont();
//		String currentStyle = "Plain";
//		;
//		switch (currentFont.getStyle()) {
//		case Font.PLAIN:
//			currentStyle = "Plain";
//			break;
//		case Font.BOLD:
//			currentStyle = "Bold";
//			break;
//		case Font.ITALIC:
//			currentStyle = "Italic";
//			break;
//		case Font.BOLD | Font.ITALIC:
//			currentStyle = "Bold Italic";
//			break;
//
//		}// switch

//		lblFontFamily.setText(currentFont.getFamily());
//		lblFontStyle.setText(currentStyle);
//		
//		lblFontSize.setText(Integer.toString(currentFont.getSize()));

	}// readProperties

	private void doBtnOK() {
		saveProperties();
		dialogResultValue = DIALOG_OK;
		dispose();
	}// doBtnOK

	private void doBtnCancel() {
		dialogResultValue = DIALOG_CANCEL;
		dispose();
	}// doBtnCancel

	private void doBtnNewFont() {
		
		FontChooser fontChooser = new FontChooser(lblFontFamily.getText(),lblFontStyle.getText(),Integer.valueOf(lblFontSize.getText()));
//		FontChooser fontChooser = new FontChooser();
		
		if (fontChooser.showDialog()== JOptionPane.OK_OPTION){
					Font newFont = fontChooser.selectedFont();
//---------------------------------
					
					
					String currentStyle = "Plain";
					;
					switch (newFont.getStyle()) {
					case Font.PLAIN:
						currentStyle = "Plain";
						break;
					case Font.BOLD:
						currentStyle = "Bold";
						break;
					case Font.ITALIC:
						currentStyle = "Italic";
						break;
					case Font.BOLD | Font.ITALIC:
						currentStyle = "Bold Italic";
						break;
			
					}// switch

					lblFontFamily.setText(newFont.getFamily());
					lblFontStyle.setText(currentStyle);
					
					lblFontSize.setText(Integer.toString(newFont.getSize()));
//---------------------------------
					
					
					
					
					
		}//if OK
		
		

		//System.out.printf("[**fontChooser.showDialog()] ans = %d%n",ans);
				
//		System.out.printf("[**fontChooser.showDialog()]%n\t Family = %s, Style = %d, Size = %d%n%n",
//				font.getFamily(),font.getStyle(),font.getSize());
		
		fontChooser = null;
		

	}// doBtnNewFont

	

	private void appInit() {
		readProperties();
		// loadDisplay();
	}// appInit

	private void appClose() {

	}// applClose

	private void appCancel() {

	}// appCancel

	/**
	 * Create the dialog.
	 */
	public ListDevicePropertyDialog(Component c) {
		super(SwingUtilities.getWindowAncestor(c), "List Device Propert Dialog", Dialog.DEFAULT_MODALITY_TYPE);
		this.c = c;
		// this.setLocationByPlatform(true);
		// this.setLocationRelativeTo(SwingUtilities.getWindowAncestor(c));
		initialize();
		appInit();
	}// Constructor

	private void initialize() {

		// setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
		setBounds(100, 100, 420, 253);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 50, 300, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 100, 30, 0, 33, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);

		panelFont = new JPanel();
		panelFont.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Font", TitledBorder.CENTER,
				TitledBorder.TOP, null, new Color(0, 0, 0)));
		GridBagConstraints gbc_panelFont = new GridBagConstraints();
		gbc_panelFont.insets = new Insets(0, 0, 5, 5);
		gbc_panelFont.fill = GridBagConstraints.HORIZONTAL;
		gbc_panelFont.gridx = 0;
		gbc_panelFont.gridy = 0;
		getContentPane().add(panelFont, gbc_panelFont);
		GridBagLayout gbl_panelFont = new GridBagLayout();
		gbl_panelFont.columnWidths = new int[] { 0, 0 };
		gbl_panelFont.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gbl_panelFont.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_panelFont.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panelFont.setLayout(gbl_panelFont);

		lblFontFamily = new JLabel("Font Family");
		GridBagConstraints gbc_lblFontFamily = new GridBagConstraints();
		gbc_lblFontFamily.insets = new Insets(0, 0, 5, 0);
		gbc_lblFontFamily.gridx = 0;
		gbc_lblFontFamily.gridy = 0;
		panelFont.add(lblFontFamily, gbc_lblFontFamily);

		lblFontStyle = new JLabel("Font Style");
		GridBagConstraints gbc_lblFontStyle = new GridBagConstraints();
		gbc_lblFontStyle.insets = new Insets(0, 0, 5, 0);
		gbc_lblFontStyle.gridx = 0;
		gbc_lblFontStyle.gridy = 1;
		panelFont.add(lblFontStyle, gbc_lblFontStyle);

		lblFontSize = new JLabel("Size");
		GridBagConstraints gbc_lblFontSize = new GridBagConstraints();
		gbc_lblFontSize.insets = new Insets(0, 0, 5, 0);
		gbc_lblFontSize.gridx = 0;
		gbc_lblFontSize.gridy = 2;
		panelFont.add(lblFontSize, gbc_lblFontSize);

		JButton btnNewFont = new JButton("New Font");
		btnNewFont.setName(BTN_NEW_FONT);
		btnNewFont.addActionListener(this);
		GridBagConstraints gbc_btnNewFont = new GridBagConstraints();
		gbc_btnNewFont.gridx = 0;
		gbc_btnNewFont.gridy = 4;
		panelFont.add(btnNewFont, gbc_btnNewFont);
		contentPanel
				.setBorder(new TitledBorder(null, "Column Limits", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_contentPanel = new GridBagConstraints();
		gbc_contentPanel.insets = new Insets(0, 0, 5, 5);
		gbc_contentPanel.gridx = 1;
		gbc_contentPanel.gridy = 0;
		getContentPane().add(contentPanel, gbc_contentPanel);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[] { 0, 0, 0 };
		gbl_contentPanel.rowHeights = new int[] { 0, 0, 0 };
		gbl_contentPanel.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		contentPanel.setLayout(gbl_contentPanel);

		rbLimitColumns = new JRadioButton("");
		rbLimitColumns.setToolTipText("Will truncate lines at max column");
		GridBagConstraints gbc_rbLimitColumns = new GridBagConstraints();
		gbc_rbLimitColumns.anchor = GridBagConstraints.EAST;
		gbc_rbLimitColumns.insets = new Insets(0, 0, 5, 5);
		gbc_rbLimitColumns.gridx = 0;
		gbc_rbLimitColumns.gridy = 0;
		contentPanel.add(rbLimitColumns, gbc_rbLimitColumns);

		lblNewLabel = new JLabel("Limit Width");
		lblNewLabel.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel.gridx = 1;
		gbc_lblNewLabel.gridy = 0;
		contentPanel.add(lblNewLabel, gbc_lblNewLabel);

		spinnerColumns = new JSpinner();
		spinnerColumns.setModel(new SpinnerNumberModel(new Integer(120), new Integer(10), null, new Integer(1)));
		GridBagConstraints gbc_spinnerColumns = new GridBagConstraints();
		gbc_spinnerColumns.anchor = GridBagConstraints.EAST;
		gbc_spinnerColumns.insets = new Insets(0, 0, 0, 5);
		gbc_spinnerColumns.gridx = 0;
		gbc_spinnerColumns.gridy = 1;
		contentPanel.add(spinnerColumns, gbc_spinnerColumns);

		lblNewLabel_2 = new JLabel("Columns");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_2.gridx = 1;
		gbc_lblNewLabel_2.gridy = 1;
		contentPanel.add(lblNewLabel_2, gbc_lblNewLabel_2);

		panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Tab Spacing", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.gridx = 2;
		gbc_panel.gridy = 0;
		getContentPane().add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		spinnerTab = new JSpinner();
		GridBagConstraints gbc_spinnerTab = new GridBagConstraints();
		gbc_spinnerTab.anchor = GridBagConstraints.SOUTH;
		gbc_spinnerTab.insets = new Insets(0, 0, 0, 5);
		gbc_spinnerTab.gridx = 0;
		gbc_spinnerTab.gridy = 0;
		panel.add(spinnerTab, gbc_spinnerTab);
		spinnerTab.setModel(new SpinnerNumberModel(1, 1, 40, 1));

		lblNewLabel_1 = new JLabel("Tab Size");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.fill = GridBagConstraints.VERTICAL;
		gbc_lblNewLabel_1.gridx = 1;
		gbc_lblNewLabel_1.gridy = 0;
		panel.add(lblNewLabel_1, gbc_lblNewLabel_1);
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.LEFT);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		GridBagConstraints gbc_buttonPane = new GridBagConstraints();
		gbc_buttonPane.anchor = GridBagConstraints.NORTH;
		gbc_buttonPane.fill = GridBagConstraints.HORIZONTAL;
		gbc_buttonPane.gridx = 2;
		gbc_buttonPane.gridy = 3;
		getContentPane().add(buttonPane, gbc_buttonPane);

		JButton btnOk = new JButton("OK");
		btnOk.setName(BTN_OK);
		btnOk.addActionListener(this);
		btnOk.setActionCommand("OK");
		buttonPane.add(btnOk);
		getRootPane().setDefaultButton(btnOk);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.setName(BTN_CANCEL);
		btnCancel.addActionListener(this);
		btnCancel.setActionCommand("Cancel");
		buttonPane.add(btnCancel);

	}// initialize

	private static final String BTN_OK = "btnOk";
	private static final String BTN_CANCEL = "btnCancel";
	private static final String BTN_NEW_FONT = "btnNewFont";
	private JPanel panel;

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		switch (((Component) actionEvent.getSource()).getName()) {
		case BTN_OK:
			doBtnOK();
			break;
		case BTN_CANCEL:
			doBtnCancel();
			break;
		case BTN_NEW_FONT:
			doBtnNewFont();
			break;
		default:
			System.err.printf("[ListDevicePropertyDialog] - actionPerformed %n Unknown Action %s%n%n",
					((Component) actionEvent.getSource()).getName());
		}// switch

	}// actionPerformed

}// class ListDevicePropertyDialog
