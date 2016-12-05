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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
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
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;

import disks.RawDiskDrive;
import utilities.FilePicker;
import utilities.hdNumberBox.HDNumberBox;
import utilities.hdNumberBox.HDNumberValueChangeEvent;
import utilities.hdNumberBox.HDNumberValueChangeListener;
import utilities.hdNumberBox.HDSeekPanel;
import utilities.hexEdit.HexEditPanelSimple;

public class DiskUtility {

	private JFrame frmDiskUtility;
	private HDNumberBox hdnSector;
	private HDNumberBox hdnTrack;
	private HDNumberBox hdnHead;
	private JToggleButton tbBootable;
	private JToggleButton tbDecimalDisplay;
	private JTabbedPane tabbedPane;
	private File currentFile;
	private JLabel lblFileName;
	// ....
	private DiskUtilityAdapter diskUtilityAdapter;

	private ArrayList<HDNumberBox> hdNumberBoxs;
	private HDSeekPanel hdSeekPanel;

	boolean changeSectorInProgress = false;

	private int absoluteSector;
	private int currentAbsoluteSector;

	private RawDiskDrive diskDrive;

	private void loadFile(File file) {
		absoluteSector = 0;
		currentFile = file;
		diskDrive = new RawDiskDrive(file.getAbsolutePath());
		lblFileName.setText(file.getName());
		lblFileName.setToolTipText(file.getAbsolutePath());
		setHeadTrackSectorSizes(diskDrive);
		displayPhysicalSector(absoluteSector);
		setDisplayRadix();
	}// loadFile

	private void closeFile(File file) {
		absoluteSector = 0;

		currentFile = null;
		diskDrive.dismount();
//		setHeadTrackSectorSizes(diskDrive);
		currentFile = null;
		byte[] mtArray = new byte[16];
		panelSectorDisplay.loadData(mtArray);
		lblFileName.setText(NO_ACTIVE_FILE);
		lblFileName.setToolTipText(NO_ACTIVE_FILE);
	}// closeFile

	private void setHeadTrackSectorSizes(RawDiskDrive diskDrive) {

		((SpinnerNumberModel) hdnHead.getNumberModel()).setValue(0);
		((SpinnerNumberModel) hdnTrack.getNumberModel()).setValue(0);
		((SpinnerNumberModel) hdnSector.getNumberModel()).setValue(1);
		hdSeekPanel.setValue(0);

		if (diskDrive == null) {
			((SpinnerNumberModel) hdnHead.getNumberModel()).setMaximum(0);
			((SpinnerNumberModel) hdnTrack.getNumberModel()).setMaximum(0);
			((SpinnerNumberModel) hdnSector.getNumberModel()).setMaximum(1);
			((SpinnerNumberModel) hdnSector.getNumberModel()).setMinimum(1);
			hdSeekPanel.setMaxValue(0);

		} else {
			((SpinnerNumberModel) hdnHead.getNumberModel()).setMaximum(diskDrive.getHeads() - 1);
			((SpinnerNumberModel) hdnTrack.getNumberModel()).setMaximum(diskDrive.getTracksPerHead() - 1);
			((SpinnerNumberModel) hdnSector.getNumberModel()).setMaximum(diskDrive.getSectorsPerTrack());
			hdSeekPanel.setMaxValue(diskDrive.getTotalSectorsOnDisk() - 1);

		} // if
	}// setHeadTrackSectorSizes

	private void haveDisk(boolean yes) {
		if (yes) {

		} else {

		} //
	}// haveDisk

	private void selectedNewPhysicalSector(boolean fromSeekPanel) {
		int newSector = hdSeekPanel.getValue();

		if (fromSeekPanel) {
			hdnHead.mute(true);
			hdnTrack.mute(true);
			hdnSector.mute(true);
			
			diskDrive.setCurrentAbsoluteSector(newSector);
			hdnHead.setValue(diskDrive.getCurrentHead());
			hdnTrack.setValue(diskDrive.getCurrentTrack());
			hdnSector.setValue(diskDrive.getCurrentSector());
			
			hdnHead.mute(false);
			hdnTrack.mute(false);
			hdnSector.mute(false);

		} else {

			diskDrive.setCurrentAbsoluteSector(hdnHead.getValue(), hdnTrack.getValue(), hdnSector.getValue());
			newSector = diskDrive.getCurrentAbsoluteSector();
			hdSeekPanel.mute(true);
			hdSeekPanel.setValue(newSector);
			hdSeekPanel.mute(false);
		} // if
		displayPhysicalSector(newSector);

	}// selectedNewPhysicalSector

	private void displayPhysicalSector(int absoluteSector) {
		if ((0 > absoluteSector) | (diskDrive.getTotalSectorsOnDisk() < absoluteSector)) {
			absoluteSector = 0;
		} // if
		currentAbsoluteSector = absoluteSector;
		diskDrive.setCurrentAbsoluteSector(currentAbsoluteSector);
		// diskDrive.read();
		panelSectorDisplay.loadData(diskDrive.read());
		// displayPhysicalSector();
	}// displayPhysicalSector

	private void manageFileMenus(String source) {
		switch (source) {
		case MNU_FILE_NEW_DISK:
		case MNU_FILE_LOAD_DISK:
			mnuFileNewDisk.setEnabled(false);
			mnuFileLoadDisk.setEnabled(false);
			mnuFileClose.setEnabled(true);
			mnuFileSave.setEnabled(true);
			mnuFileSaveAs.setEnabled(true);
			mnuFileExit.setEnabled(true);
			break;
		case MNU_FILE_CLOSE:
			mnuFileNewDisk.setEnabled(true);
			mnuFileLoadDisk.setEnabled(true);
			mnuFileClose.setEnabled(false);
			mnuFileSave.setEnabled(false);
			mnuFileSaveAs.setEnabled(false);
			mnuFileExit.setEnabled(true);
		case MNU_FILE_SAVE:
		case MNU_FILE_SAVE_AS:
		case MNU_FILE_EXIT:
			break;
		default:
		}// switch
	}// manageFileMenus

	// .............................................................

	public void setDisplayRadix() {

		for (HDNumberBox hdNumberBox : hdNumberBoxs) {
			hdNumberBox.setDecimalDisplay(tbDecimalDisplay.isSelected());
		} // for

	}// setDisplayRadix

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
			}// run
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
		myPrefs.putInt("Tab", tabbedPane.getSelectedIndex());
		// myPrefs.putInt("Divider", splitPane1.getDividerLocation());
		myPrefs = null;
		cleanUp(currentFile);
		System.exit(0);
	}// appClose

	private void cleanUp(Object obj) {
		if (obj != null) {
			obj = null;
		} // cleanUp
	}

	private void appInit0() {
		diskUtilityAdapter = new DiskUtilityAdapter();
	}// appInit0

	private void appInit() {
		Preferences myPrefs = Preferences.userNodeForPackage(DiskUtility.class);
		frmDiskUtility.setSize(975, 773);
		frmDiskUtility.setLocation(myPrefs.getInt("LocX", 100), myPrefs.getInt("LocY", 100));
		tabbedPane.setSelectedIndex(myPrefs.getInt("Tab", 0));
		// splitPane1.setDividerLocation(myPrefs.getInt("Divider", 250));
		myPrefs = null;
		hdNumberBoxs = new ArrayList<HDNumberBox>();
		hdNumberBoxs.add(hdnHead);
		hdNumberBoxs.add(hdnTrack);
		hdNumberBoxs.add(hdnSector);
		hdNumberBoxs.add(hdSeekPanel);

		setDisplayRadix();
		manageFileMenus(MNU_FILE_CLOSE);
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
		tbBootable.setName(TB_BOOTABLE);
		tbBootable.addActionListener(diskUtilityAdapter);
		tbBootable.setHorizontalAlignment(SwingConstants.LEFT);
		toolBar.add(tbBootable);

		Component horizontalStrut = Box.createHorizontalStrut(20);
		toolBar.add(horizontalStrut);

		tbDecimalDisplay = new JToggleButton("Display Decimal");
		tbDecimalDisplay.setName(TB_DECIMAL_DISPLAY);
		tbDecimalDisplay.addActionListener(diskUtilityAdapter);
		toolBar.add(tbDecimalDisplay);

		JButton btnNewButton = new JButton("New button");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			//	panelSectorDisplay.clearDisplay();
			}
		});
		toolBar.add(btnNewButton);

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

		lblFileName = new JLabel(NO_ACTIVE_FILE);
		lblFileName.setToolTipText("<No File Active>");
		lblFileName.setFont(new Font("Arial", Font.BOLD, 18));
		GridBagConstraints gbc_lblFileName = new GridBagConstraints();
		gbc_lblFileName.insets = new Insets(0, 0, 5, 0);
		gbc_lblFileName.gridx = 0;
		gbc_lblFileName.gridy = 0;
		panelTitle.add(lblFileName, gbc_lblFileName);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 1;
		panelTitle.add(tabbedPane, gbc_tabbedPane);

		JPanel tabDirectory = new JPanel();
		tabbedPane.addTab("Directory View", null, tabDirectory, null);
		GridBagLayout gbl_tabDirectory = new GridBagLayout();
		gbl_tabDirectory.columnWidths = new int[] { 0, 0 };
		gbl_tabDirectory.rowHeights = new int[] { 0, 0 };
		gbl_tabDirectory.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_tabDirectory.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		tabDirectory.setLayout(gbl_tabDirectory);

		panelDirectory = new JPanel();
		GridBagConstraints gbc_panelDirectory = new GridBagConstraints();
		gbc_panelDirectory.fill = GridBagConstraints.BOTH;
		gbc_panelDirectory.gridx = 0;
		gbc_panelDirectory.gridy = 0;
		tabDirectory.add(panelDirectory, gbc_panelDirectory);
		GridBagLayout gbl_panelDirectory = new GridBagLayout();
		gbl_panelDirectory.columnWidths = new int[] { 0 };
		gbl_panelDirectory.rowHeights = new int[] { 0 };
		gbl_panelDirectory.columnWeights = new double[] { Double.MIN_VALUE };
		gbl_panelDirectory.rowWeights = new double[] { Double.MIN_VALUE };
		panelDirectory.setLayout(gbl_panelDirectory);

		JPanel tabFile = new JPanel();
		tabbedPane.addTab("File View", null, tabFile, null);
		GridBagLayout gbl_tabFile = new GridBagLayout();
		gbl_tabFile.columnWidths = new int[] { 0, 0 };
		gbl_tabFile.rowHeights = new int[] { 0, 0 };
		gbl_tabFile.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_tabFile.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		tabFile.setLayout(gbl_tabFile);

		JPanel panelFile = new JPanel();
		GridBagConstraints gbc_panelFile = new GridBagConstraints();
		gbc_panelFile.fill = GridBagConstraints.BOTH;
		gbc_panelFile.gridx = 0;
		gbc_panelFile.gridy = 0;
		tabFile.add(panelFile, gbc_panelFile);
		GridBagLayout gbl_panelFile = new GridBagLayout();
		gbl_panelFile.columnWidths = new int[] { 0 };
		gbl_panelFile.rowHeights = new int[] { 0 };
		gbl_panelFile.columnWeights = new double[] { Double.MIN_VALUE };
		gbl_panelFile.rowWeights = new double[] { Double.MIN_VALUE };
		panelFile.setLayout(gbl_panelFile);

		JPanel tabPhysical = new JPanel();
		tabbedPane.addTab("Physical View", null, tabPhysical, null);
		GridBagLayout gbl_tabPhysical = new GridBagLayout();
		gbl_tabPhysical.columnWidths = new int[] { 0, 0, 0 };
		gbl_tabPhysical.rowHeights = new int[] { 0, 0, 0, 40, 10, 0 };
		gbl_tabPhysical.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl_tabPhysical.rowWeights = new double[] { 0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		tabPhysical.setLayout(gbl_tabPhysical);

		JPanel panelHeadTrackSector = new JPanel();
		GridBagConstraints gbc_panelHeadTrackSector = new GridBagConstraints();
		gbc_panelHeadTrackSector.insets = new Insets(0, 0, 5, 5);
		gbc_panelHeadTrackSector.fill = GridBagConstraints.BOTH;
		gbc_panelHeadTrackSector.gridx = 0;
		gbc_panelHeadTrackSector.gridy = 0;
		tabPhysical.add(panelHeadTrackSector, gbc_panelHeadTrackSector);
		GridBagLayout gbl_panelHeadTrackSector = new GridBagLayout();
		gbl_panelHeadTrackSector.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panelHeadTrackSector.rowHeights = new int[] { 0, 0 };
		gbl_panelHeadTrackSector.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panelHeadTrackSector.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panelHeadTrackSector.setLayout(gbl_panelHeadTrackSector);

		JLabel lblHead = new JLabel("Head");
		GridBagConstraints gbc_lblHead = new GridBagConstraints();
		gbc_lblHead.insets = new Insets(0, 0, 0, 5);
		gbc_lblHead.gridx = 1;
		gbc_lblHead.gridy = 0;
		panelHeadTrackSector.add(lblHead, gbc_lblHead);

		hdnHead = new HDNumberBox();
		hdnHead.setName(HDN_HEAD);
		hdnHead.addHDNumberValueChangedListener(diskUtilityAdapter);
		hdnHead.setPreferredSize(new Dimension(50, 20));
		GridBagConstraints gbc_hdnHead = new GridBagConstraints();
		gbc_hdnHead.insets = new Insets(0, 0, 0, 5);
		gbc_hdnHead.gridx = 3;
		gbc_hdnHead.gridy = 0;
		panelHeadTrackSector.add(hdnHead, gbc_hdnHead);

		JLabel lblTrack = new JLabel("Track");
		GridBagConstraints gbc_lblTrack = new GridBagConstraints();
		gbc_lblTrack.insets = new Insets(0, 0, 0, 5);
		gbc_lblTrack.gridx = 5;
		gbc_lblTrack.gridy = 0;
		panelHeadTrackSector.add(lblTrack, gbc_lblTrack);

		hdnTrack = new HDNumberBox();
		hdnTrack.setName(HDN_TRACK);
		hdnTrack.addHDNumberValueChangedListener(diskUtilityAdapter);
		hdnTrack.setPreferredSize(new Dimension(50, 20));
		GridBagConstraints gbc_hdnTrack = new GridBagConstraints();
		gbc_hdnTrack.insets = new Insets(0, 0, 0, 5);
		gbc_hdnTrack.gridx = 7;
		gbc_hdnTrack.gridy = 0;
		panelHeadTrackSector.add(hdnTrack, gbc_hdnTrack);

		JLabel lblSector = new JLabel("Sector");
		GridBagConstraints gbc_lblSector = new GridBagConstraints();
		gbc_lblSector.insets = new Insets(0, 0, 0, 5);
		gbc_lblSector.gridx = 9;
		gbc_lblSector.gridy = 0;
		panelHeadTrackSector.add(lblSector, gbc_lblSector);

		hdnSector = new HDNumberBox();
		hdnSector.setName(HDN_SECTOR);
		hdnSector.addHDNumberValueChangedListener(diskUtilityAdapter);
		hdnSector.setPreferredSize(new Dimension(50, 20));
		GridBagConstraints gbc_hdnSector = new GridBagConstraints();
		gbc_hdnSector.insets = new Insets(0, 0, 0, 5);
		gbc_hdnSector.gridx = 11;
		gbc_hdnSector.gridy = 0;
		panelHeadTrackSector.add(hdnSector, gbc_hdnSector);

		panelHexDisplay0 = new JPanel();
		GridBagConstraints gbc_panelHexDisplay0 = new GridBagConstraints();
		gbc_panelHexDisplay0.insets = new Insets(0, 0, 5, 5);
		gbc_panelHexDisplay0.fill = GridBagConstraints.VERTICAL;
		gbc_panelHexDisplay0.gridx = 0;
		gbc_panelHexDisplay0.gridy = 1;
		tabPhysical.add(panelHexDisplay0, gbc_panelHexDisplay0);
		GridBagLayout gbl_panelHexDisplay0 = new GridBagLayout();
		gbl_panelHexDisplay0.columnWidths = new int[] { 0, 0 };
		gbl_panelHexDisplay0.rowHeights = new int[] { 0, 0 };
		gbl_panelHexDisplay0.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panelHexDisplay0.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		panelHexDisplay0.setLayout(gbl_panelHexDisplay0);

		panelSectorDisplay = new HexEditPanelSimple();
		GridBagConstraints gbc_panelSectorDisplay = new GridBagConstraints();
		gbc_panelSectorDisplay.fill = GridBagConstraints.BOTH;
		gbc_panelSectorDisplay.gridx = 0;
		gbc_panelSectorDisplay.gridy = 0;
		panelHexDisplay0.add(panelSectorDisplay, gbc_panelSectorDisplay);

		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 5);
		gbc_panel_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 3;
		tabPhysical.add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] { 0, 0 };
		gbl_panel_1.rowHeights = new int[] { 0, 0 };
		gbl_panel_1.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panel_1.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panel_1.setLayout(gbl_panel_1);

		hdSeekPanel = new HDSeekPanel(new SpinnerNumberModel(0, 0, 0, 1));
		hdSeekPanel.setPreferredSize(new Dimension(260, 30));
		hdSeekPanel.setMinimumSize(new Dimension(280, 39));
		hdSeekPanel.setName(HD_SEEK_PANEL);
		hdSeekPanel.addHDNumberValueChangedListener(diskUtilityAdapter);
		GridBagConstraints gbc_seekPanel = new GridBagConstraints();
		gbc_seekPanel.gridx = 0;
		gbc_seekPanel.gridy = 0;
		panel_1.add(hdSeekPanel, gbc_seekPanel);
		// seekPanel.setValue(0);
		// hdSeekPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null,
		// null, null, null));
		GridBagLayout gbl_seekPanel = new GridBagLayout();
		gbl_seekPanel.columnWidths = new int[] { 0 };
		gbl_seekPanel.rowHeights = new int[] { 0 };
		gbl_seekPanel.columnWeights = new double[] { Double.MIN_VALUE };
		gbl_seekPanel.rowWeights = new double[] { Double.MIN_VALUE };
		hdSeekPanel.setLayout(gbl_seekPanel);

		JMenuBar menuBar = new JMenuBar();
		frmDiskUtility.setJMenuBar(menuBar);

		JMenu mnuFile = new JMenu("File");
		menuBar.add(mnuFile);

		mnuFileNewDisk = new JMenuItem("New Disk");
		mnuFileNewDisk.setName(MNU_FILE_NEW_DISK);
		mnuFileNewDisk.addActionListener(diskUtilityAdapter);
		mnuFile.add(mnuFileNewDisk);

		mnuFileLoadDisk = new JMenuItem("Load Disk ...");
		mnuFileLoadDisk.setName(MNU_FILE_LOAD_DISK);
		mnuFileLoadDisk.addActionListener(diskUtilityAdapter);
		mnuFile.add(mnuFileLoadDisk);

		JSeparator separator = new JSeparator();
		mnuFile.add(separator);

		mnuFileClose = new JMenuItem("Close");
		mnuFileClose.setName(MNU_FILE_CLOSE);
		mnuFileClose.addActionListener(diskUtilityAdapter);
		mnuFile.add(mnuFileClose);

		JSeparator separator_1 = new JSeparator();
		mnuFile.add(separator_1);

		mnuFileSave = new JMenuItem("Save");
		mnuFileSave.setName(MNU_FILE_SAVE);
		mnuFileSave.addActionListener(diskUtilityAdapter);
		mnuFile.add(mnuFileSave);

		mnuFileSaveAs = new JMenuItem("Save As ...");
		mnuFileSaveAs.setName(MNU_FILE_SAVE_AS);
		mnuFileSaveAs.addActionListener(diskUtilityAdapter);
		mnuFile.add(mnuFileSaveAs);

		JSeparator separator_2 = new JSeparator();
		mnuFile.add(separator_2);

		mnuFileExit = new JMenuItem("Exit");
		mnuFileExit.setName(MNU_FILE_EXIT);
		mnuFileExit.addActionListener(diskUtilityAdapter);
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
		// --------------------------------------------------------------

	public class SpinnerFormatSet {
		public JSpinner spinner;
		public JFormattedTextField formattedTextField;
		public AbstractFormatterFactory decimalFormatterFactory;
		public HexFormatterFactory hexFormatterFactory;

		public SpinnerFormatSet(JSpinner spinner, JFormattedTextField formattedTextField,
				AbstractFormatterFactory abstractFormatterFactory, HexFormatterFactory hexFormatterFactory) {
			this.spinner = spinner;
			this.formattedTextField = formattedTextField;
			this.decimalFormatterFactory = abstractFormatterFactory;
			this.hexFormatterFactory = hexFormatterFactory;
		}// Constructor

	}// class SpinnerFormatSets

	// ---------------------------------------------------------------

	public class DiskUtilityAdapter implements ActionListener, FocusListener, HDNumberValueChangeListener {

		// ------ActionListener
		@Override
		public void actionPerformed(ActionEvent actionEvent) {
			Object source = actionEvent.getSource();
			String name = ((JComponent) source).getName();

			switch (name) {
			// Menus
			case MNU_FILE_NEW_DISK:
				manageFileMenus(name);
				break;
			case MNU_FILE_LOAD_DISK:
				JFileChooser fc = FilePicker.getDiskPicker("Disketts & Floppies", "F3ED", "F5DD", "F3DD", "F3HD",
						"F5HD", "F8SS", "F8DS");
				if (fc.showOpenDialog(null) == JFileChooser.CANCEL_OPTION) {
					System.out.println("Bailed out of the open");
					return;
				} //
				loadFile(fc.getSelectedFile());
				manageFileMenus(name);
				break;
			case MNU_FILE_CLOSE:
				closeFile(currentFile);
				manageFileMenus(name);
				break;
			case MNU_FILE_SAVE:
				break;
			case MNU_FILE_SAVE_AS:
				break;
			case MNU_FILE_EXIT:
				appClose();
				break;
			// ToggleButtons
			case TB_DECIMAL_DISPLAY:
				setDisplayRadix();
				if (tbDecimalDisplay.isSelected()) {
					tbDecimalDisplay.setText(DISPLAY_HEX);
				} else {
					tbDecimalDisplay.setText(DISPLAY_DECIMAL);
				} // if
				break;
			case TB_BOOTABLE:
				break;
			case BTN_READ_PHYSICAL_SECTOR:
				selectedNewPhysicalSector(false);
				break;
			default:

			}// switch

		}// actionPerformed
			// ------ActionListener

		// ------FocusListener

		@Override
		public void focusLost(FocusEvent focusEvent) {

		}// focusLost

		@Override
		public void focusGained(FocusEvent arg0) {
			// TODO Auto-generated method stub

		}// focusGained
			// ------FocusListener

		// ------valueChanged

		@Override
		public void valueChanged(HDNumberValueChangeEvent hDNumberValueChangeEvent) {

			String name = ((Component) hDNumberValueChangeEvent.getSource()).getName();

			switch (name) {
			case HDN_HEAD:
			case HDN_TRACK:
			case HDN_SECTOR:
				 selectedNewPhysicalSector(false);
				break;
			case HD_SEEK_PANEL:
				selectedNewPhysicalSector(true);
				break;
			default:
			}// switch

		}// valueChanged

		// ------valueChanged

	}// class DiskUtilityAdapter

	// ---------------------------------------------------------------

	public static final String NO_ACTIVE_FILE = "<No Active File>";

	public static final String HDN_HEAD = "hdnHead";
	public static final String HDN_TRACK = "hdnTrack";
	public static final String HDN_SECTOR = "hdnSector";
	public static final String HD_SEEK_PANEL = "seekPanel";

	public static final String DISPLAY_HEX = "Display Hex";
	public static final String DISPLAY_DECIMAL = "Display Dicimal";

	public static final String BTN_READ_PHYSICAL_SECTOR = "btnReadPhysicalSector";

	public static final String TB_DECIMAL_DISPLAY = "tbDecimalDisplay";
	public static final String TB_BOOTABLE = "tbBootable";

	public static final String MNU_FILE_NEW_DISK = "mnuFileNewDisk";
	public static final String MNU_FILE_LOAD_DISK = "mnuFileLoadDisk";
	public static final String MNU_FILE_CLOSE = "mnuFileClose";
	public static final String MNU_FILE_SAVE = "mnuFileSave";
	public static final String MNU_FILE_SAVE_AS = "mnuFileSaveAs";
	public static final String MNU_FILE_EXIT = "mnuFileExit";

	private JPanel panelDirectory;
	private JPanel panelHexDisplay0;
	private HexEditPanelSimple panelSectorDisplay;
	private JMenuItem mnuFileNewDisk;
	private JMenuItem mnuFileLoadDisk;
	private JMenuItem mnuFileClose;
	private JMenuItem mnuFileSave;
	private JMenuItem mnuFileSaveAs;
	private JMenuItem mnuFileExit;
	private Component horizontalStrut_1;

}// class DiskUtility
