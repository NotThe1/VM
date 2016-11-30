package disks.utility;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;

import utilities.seekPanel.SeekPanel;

public class DiskUtility {

	private JFrame frmDiskUtility;
	private JSpinner spinnerSector;
	private JSpinner spinnerTrack;
	private JSpinner spinnerHead;
	private JToggleButton tbBootable;
	private JToggleButton tbHexDisplay;
	// ....
	private DiskUtilityAdapter diskUtilityAdapter;
	
//	private ArrayList<JSpinner> spinners;
	private ArrayList<JFormattedTextField> formattedTextFields;
	JFormattedTextField.AbstractFormatterFactory decimalFormatterFactory;
	HexFormatterFactory hexFormatterFactory;
	private SeekPanel seekPanel;

	
	
	
	public void setDisplayRadix(){
		AbstractFormatterFactory factory = tbHexDisplay.isSelected()?hexFormatterFactory:decimalFormatterFactory;
		for (JFormattedTextField ftf:formattedTextFields){
			ftf.setFormatterFactory(factory);
		}//
	}//setDisplayRadix

	// -------------------------------------------------------------------
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DiskUtility window = new DiskUtility();
					window.frmDiskUtility.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}//run
		});
	}// main
		// -------

	private void appClose() {
		Preferences myPrefs = Preferences.userNodeForPackage(DiskUtility.class);
		Dimension dim = frmDiskUtility.getSize();
		myPrefs.putInt("Height", dim.height);
		myPrefs.putInt("Width", dim.width);
		Point point = frmDiskUtility.getLocation();
		myPrefs.putInt("LocX", point.x);
		myPrefs.putInt("LocY", point.y);
		// myPrefs.putInt("Divider", splitPane1.getDividerLocation());
		myPrefs = null;
	}// appClose
	
	private void appInit0(){
		 diskUtilityAdapter = new DiskUtilityAdapter();		
	}//appInit0

	private void appInit() {
		Preferences myPrefs = Preferences.userNodeForPackage(DiskUtility.class);
		frmDiskUtility.setSize(myPrefs.getInt("Width", 500), myPrefs.getInt("Height", 500));
		frmDiskUtility.setLocation(myPrefs.getInt("LocX", 100), myPrefs.getInt("LocY", 100));
		// splitPane1.setDividerLocation(myPrefs.getInt("Divider", 250));
		myPrefs = null;
		
		ArrayList<JSpinner> spinners = new ArrayList<JSpinner>();
		spinners.add(spinnerHead);
		spinners.add(spinnerSector);
		spinners.add(spinnerTrack);
		
		
		formattedTextFields = new ArrayList<JFormattedTextField>();
		JFormattedTextField ftf = null;
		JSpinner.DefaultEditor editor = null;;
		for(JSpinner s:spinners){
			editor = (DefaultEditor) s.getEditor();
			ftf = editor.getTextField();
			formattedTextFields.add(ftf);
		}//for
		decimalFormatterFactory = ftf.getFormatterFactory();
		hexFormatterFactory = new HexFormatterFactory();
		
		setDisplayRadix();
		ftf.setFormatterFactory(hexFormatterFactory);


	}// appInit

	/**
	 * Create the application.
	 */
	public DiskUtility() {
		appInit0();
		initialize();
		appInit();
	}// Constructor

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmDiskUtility = new JFrame();
		frmDiskUtility.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmDiskUtility.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				appClose();
			}
		});
		frmDiskUtility.setTitle("Disk Utility");
		frmDiskUtility.setBounds(100, 100, 450, 300);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		frmDiskUtility.getContentPane().setLayout(gridBagLayout);

		JToolBar toolBar = new JToolBar();
		GridBagConstraints gbc_toolBar = new GridBagConstraints();
		gbc_toolBar.anchor = GridBagConstraints.WEST;
		gbc_toolBar.insets = new Insets(0, 0, 5, 0);
		gbc_toolBar.gridx = 0;
		gbc_toolBar.gridy = 0;
		frmDiskUtility.getContentPane().add(toolBar, gbc_toolBar);

		tbBootable = new JToggleButton("Bootable");
		tbBootable.setName(DiskUtilityAdapter.TB_BOOTABLE);
		tbBootable.addActionListener(diskUtilityAdapter);
		tbBootable.setHorizontalAlignment(SwingConstants.LEFT);
		toolBar.add(tbBootable);

		Component horizontalStrut = Box.createHorizontalStrut(20);
		toolBar.add(horizontalStrut);

		tbHexDisplay = new JToggleButton("Display Hex");
		tbHexDisplay.setSelected(true);
		tbHexDisplay.setName(DiskUtilityAdapter.TB_HEX_DISPLAY);
		tbHexDisplay.addActionListener(diskUtilityAdapter);
		toolBar.add(tbHexDisplay);

		JPanel panelTitle = new JPanel();
		GridBagConstraints gbc_panelTitle = new GridBagConstraints();
		gbc_panelTitle.fill = GridBagConstraints.BOTH;
		gbc_panelTitle.gridx = 0;
		gbc_panelTitle.gridy = 1;
		frmDiskUtility.getContentPane().add(panelTitle, gbc_panelTitle);
		GridBagLayout gbl_panelTitle = new GridBagLayout();
		gbl_panelTitle.columnWidths = new int[] { 0, 0 };
		gbl_panelTitle.rowHeights = new int[] { 0, 0, 0 };
		gbl_panelTitle.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panelTitle.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		panelTitle.setLayout(gbl_panelTitle);

		JLabel lblFileName = new JLabel("<No File Active>");
		lblFileName.setToolTipText("<No File Active>");
		lblFileName.setFont(new Font("Arial", Font.BOLD, 18));
		GridBagConstraints gbc_lblFileName = new GridBagConstraints();
		gbc_lblFileName.insets = new Insets(0, 0, 5, 0);
		gbc_lblFileName.gridx = 0;
		gbc_lblFileName.gridy = 0;
		panelTitle.add(lblFileName, gbc_lblFileName);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 1;
		panelTitle.add(tabbedPane, gbc_tabbedPane);

		JPanel tabDirectory = new JPanel();
		tabbedPane.addTab("Directory View", null, tabDirectory, null);
		GridBagLayout gbl_tabDirectory = new GridBagLayout();
		gbl_tabDirectory.columnWidths = new int[] { 0 };
		gbl_tabDirectory.rowHeights = new int[] { 0 };
		gbl_tabDirectory.columnWeights = new double[] { Double.MIN_VALUE };
		gbl_tabDirectory.rowWeights = new double[] { Double.MIN_VALUE };
		tabDirectory.setLayout(gbl_tabDirectory);

		JPanel tabFile = new JPanel();
		tabbedPane.addTab("File View", null, tabFile, null);
		GridBagLayout gbl_tabFile = new GridBagLayout();
		gbl_tabFile.columnWidths = new int[] { 0 };
		gbl_tabFile.rowHeights = new int[] { 0 };
		gbl_tabFile.columnWeights = new double[] { Double.MIN_VALUE };
		gbl_tabFile.rowWeights = new double[] { Double.MIN_VALUE };
		tabFile.setLayout(gbl_tabFile);

		JPanel tabPhysical = new JPanel();
		tabbedPane.addTab("Physical View", null, tabPhysical, null);
		GridBagLayout gbl_tabPhysical = new GridBagLayout();
		gbl_tabPhysical.columnWidths = new int[] { 0, 0 };
		gbl_tabPhysical.rowHeights = new int[] { 0, 30, 0 };
		gbl_tabPhysical.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_tabPhysical.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		tabPhysical.setLayout(gbl_tabPhysical);

		JPanel panelHeadTrackSector = new JPanel();
		GridBagConstraints gbc_panelHeadTrackSector = new GridBagConstraints();
		gbc_panelHeadTrackSector.insets = new Insets(0, 0, 5, 0);
		gbc_panelHeadTrackSector.fill = GridBagConstraints.BOTH;
		gbc_panelHeadTrackSector.gridx = 0;
		gbc_panelHeadTrackSector.gridy = 0;
		tabPhysical.add(panelHeadTrackSector, gbc_panelHeadTrackSector);
		GridBagLayout gbl_panelHeadTrackSector = new GridBagLayout();
		gbl_panelHeadTrackSector.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panelHeadTrackSector.rowHeights = new int[] { 0, 0 };
		gbl_panelHeadTrackSector.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, Double.MIN_VALUE };
		gbl_panelHeadTrackSector.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panelHeadTrackSector.setLayout(gbl_panelHeadTrackSector);

		JLabel lblHead = new JLabel("Head");
		GridBagConstraints gbc_lblHead = new GridBagConstraints();
		gbc_lblHead.insets = new Insets(0, 0, 0, 5);
		gbc_lblHead.gridx = 1;
		gbc_lblHead.gridy = 0;
		panelHeadTrackSector.add(lblHead, gbc_lblHead);

		spinnerHead = new JSpinner();
		spinnerHead.setPreferredSize(new Dimension(50, 20));
		GridBagConstraints gbc_spinnerHead = new GridBagConstraints();
		gbc_spinnerHead.insets = new Insets(0, 0, 0, 5);
		gbc_spinnerHead.gridx = 3;
		gbc_spinnerHead.gridy = 0;
		panelHeadTrackSector.add(spinnerHead, gbc_spinnerHead);

		JLabel lblTrack = new JLabel("Track");
		GridBagConstraints gbc_lblTrack = new GridBagConstraints();
		gbc_lblTrack.insets = new Insets(0, 0, 0, 5);
		gbc_lblTrack.gridx = 5;
		gbc_lblTrack.gridy = 0;
		panelHeadTrackSector.add(lblTrack, gbc_lblTrack);

		spinnerTrack = new JSpinner();
		spinnerTrack.setPreferredSize(new Dimension(50, 20));
		GridBagConstraints gbc_spinnerTrack = new GridBagConstraints();
		gbc_spinnerTrack.insets = new Insets(0, 0, 0, 5);
		gbc_spinnerTrack.gridx = 7;
		gbc_spinnerTrack.gridy = 0;
		panelHeadTrackSector.add(spinnerTrack, gbc_spinnerTrack);

		JLabel lblSector = new JLabel("Sector");
		GridBagConstraints gbc_lblSector = new GridBagConstraints();
		gbc_lblSector.insets = new Insets(0, 0, 0, 5);
		gbc_lblSector.gridx = 9;
		gbc_lblSector.gridy = 0;
		panelHeadTrackSector.add(lblSector, gbc_lblSector);

		spinnerSector = new JSpinner();
		spinnerSector.setPreferredSize(new Dimension(50, 20));
		GridBagConstraints gbc_spinnerSector = new GridBagConstraints();
		gbc_spinnerSector.gridx = 11;
		gbc_spinnerSector.gridy = 0;
		panelHeadTrackSector.add(spinnerSector, gbc_spinnerSector);
		
		seekPanel = new SeekPanel();
		seekPanel.setValue(0);
		seekPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_seekPanel = new GridBagConstraints();
		gbc_seekPanel.anchor = GridBagConstraints.NORTH;
		gbc_seekPanel.gridx = 0;
		gbc_seekPanel.gridy = 1;
		tabPhysical.add(seekPanel, gbc_seekPanel);
		GridBagLayout gbl_seekPanel = new GridBagLayout();
		gbl_seekPanel.columnWidths = new int[]{0};
		gbl_seekPanel.rowHeights = new int[]{0};
		gbl_seekPanel.columnWeights = new double[]{Double.MIN_VALUE};
		gbl_seekPanel.rowWeights = new double[]{Double.MIN_VALUE};
		seekPanel.setLayout(gbl_seekPanel);

		JMenuBar menuBar = new JMenuBar();
		frmDiskUtility.setJMenuBar(menuBar);
		
		JMenu mnuFile = new JMenu("File");
		menuBar.add(mnuFile);
		
		JMenuItem mnuFileNewDisk = new JMenuItem("New Disk");
		mnuFile.add(mnuFileNewDisk);
		
		JMenuItem mnuFileLoadDisk = new JMenuItem("Load Disk ...");
		mnuFile.add(mnuFileLoadDisk);
		
		JSeparator separator = new JSeparator();
		mnuFile.add(separator);
		
		JMenuItem mnuFileClose = new JMenuItem("Close");
		mnuFile.add(mnuFileClose);
		
		JSeparator separator_1 = new JSeparator();
		mnuFile.add(separator_1);
		
		JMenuItem mnuFileSave = new JMenuItem("Save");
		mnuFile.add(mnuFileSave);
		
		JMenuItem mnuFileSaveAs = new JMenuItem("Save As ...");
		mnuFile.add(mnuFileSaveAs);
		
		JSeparator separator_2 = new JSeparator();
		mnuFile.add(separator_2);
		
		JMenuItem mnuFileExit = new JMenuItem("Exit");
		mnuFile.add(mnuFileExit);
	}// initialize
	
	// ----------------------------------------------------------------------------------------------------------

	private static class HexFormatterFactory extends DefaultFormatterFactory {
		private static final long serialVersionUID = 1L;

		public AbstractFormatter getDefaultFormatter() {
			return new HexFormatter();
		}// getDefaultFormatter

		// .................................................
		private static class HexFormatter extends DefaultFormatter {
			private static final long serialVersionUID = 1L;

			public Object stringToValue(String text) throws ParseException {
				try {
					return Integer.valueOf(text, 16);
				} catch (NumberFormatException nfe) {
					throw new ParseException(text, 0);
				} // try
			}// stringToValue

			public String valueToString(Object value) throws ParseException {
				return String.format("%X", value);
			}// valueToString
		}// class HexFormatter
			// .................................................

	}// class MyFormatterFactory
		// ----------------------------------------------------------------------------------------------------------
	//---------------------------------------------------------------
	public class DiskUtilityAdapter implements ActionListener {
//		DiskUtility diskUtility;
		
//		public DiskUtilityAdapter1(DiskUtility diskUtility){
//			//this.diskUtility =diskUtility;
//		}//Constructor

		@Override
		public void actionPerformed(ActionEvent actionEvent) {
			Object source = actionEvent.getSource();
			String name = ((JComponent) source).getName();

			switch (name) {
			case TB_HEX_DISPLAY:
			//	diskUtility.setDisplayRadix();
				setDisplayRadix();
				break;
			case TB_BOOTABLE:
				break;
			default:

			}// switch

		}// actionPerformed

		public static final String TB_HEX_DISPLAY = "tbHexDisplay";
		public static final String TB_BOOTABLE = "tbBootable";

	}// class DiskUtilityAdapter
	//---------------------------------------------------------------



}// class DiskUtility
