package hardware;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import codeSupport.debug.ShowCode;
import codeSupport.debug.TrapManager;
import disks.DCUActionEvent;
import disks.DCUActionListener;
import disks.DiskControlUnit;
import disks.diskPanel.DiskPanel;
import disks.utility.DiskUtility;
import disks.utility.MakeNewDisk;
import ioSystem.IOController;
import memory.Core;
import memory.Core.Trap;
import memory.CpuBuss;
import memory.MemoryLoaderFromFile;
import memory.MemoryTrapEvent;
import utilities.FilePicker;
import utilities.hexEdit.HexEditPanelConcurrent;
import utilities.inLineDissembler.InLineDisassembler;
import utilities.menus.MenuUtility;

public class Machine8080 implements Observer {

	private Machine8080MenuAdapter menuAdapter = new Machine8080MenuAdapter();
	private Machine8080ActionAdapter actionAdapter = new Machine8080ActionAdapter();
	private DiskPanelAdapter diskPanelAdapter = new DiskPanelAdapter();
	private DiskPanel diskPanel;
	private StateDisplay stateDisplay;
	// private Core core = Core.getInstance();
	private CentralProcessingUnit cpu = CentralProcessingUnit.getInstance();
	private DiskControlUnit diskControlUnit = DiskControlUnit.getgetInstance();
	private InLineDisassembler disassembler = InLineDisassembler.getInstance();
	private HexEditPanelConcurrent hexEditPanelConcurrent = new HexEditPanelConcurrent();
	private IOController ioController = IOController.getInstance();
	private CpuBuss cpuBuss = CpuBuss.getInstance();

	private TrapManager trapManager;
	private ShowCode showCode;

	private Path pathMemLoad = null;

	private boolean addSerialTerminal;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Machine8080 window = new Machine8080();
					window.frmMachine.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				} // try
			}// run
		});
	}// main
		// -------------------------------------------------------------------

	private void doStep() {
		// System.out.println("actionPerformed: doStep");
		cpu.setError(ErrorType.NONE);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				int stepCount = (int) spinnerStepCount.getValue();
				for (int i = 0; i < stepCount; i++) {
					if (!cpu.startInstruction()) {
						break;
					} // if
				} // for step count
				updateView();
			}// run
		});

	}// doStep

	private void doRunTimeError() {
		System.out.println("doRunTimeError!");
	}// doRunTimeError

	private void doRun() {
		if (btnRun1.isSelected()) {
			cpu.setError(ErrorType.NONE);
			System.out.println("actionPerformed: doRun");
			Thread t = new Thread(cpu);
			t.start();
			stateDisplay.setDisplayComponentsEnabled(false);
			// hexEditPanelConcurrent.setEnabled(false);
		} else {
			stateDisplay.setDisplayComponentsEnabled(true);
			// hexEditPanelConcurrent.setEnabled(true);

			System.out.println("actionPerformed: doStop");
			cpu.setError(ErrorType.STOP);
			updateView();
		} // if
	}// doRun

	private void doBoot() {
		Core.getInstance().initialize();
		loadROM();
		MenuUtility.clearList(mnuMemory);
		WorkingRegisterSet.getInstance().setProgramCounter(0);
		if (DiskControlUnit.getgetInstance().isBootDiskLoaded()) {
			btnRun1.setSelected(true);
			doRun();
		} else {
			JOptionPane.showMessageDialog(frmMachine, "There is no disk in drive A", "Boot attempt",
					JOptionPane.WARNING_MESSAGE);
			updateView();
		} // if
	}// doBoot

	private void doListLineFeed() {
		if (!(IOController.getInstance().getListDevice() == null)) {
			IOController.getInstance().getListDevice().lineFeed();
		} // if
	}// doListLineFeed

	private void doListFormFeed() {
		if (!(IOController.getInstance().getListDevice() == null)) {
			IOController.getInstance().getListDevice().formFeed();
		} // if
	}// doListFrmFeed

	private void doListSaveToFile() {
		if (!(IOController.getInstance().getListDevice() == null)) {
			IOController.getInstance().getListDevice().saveToFile();
			;
		} // if
	}// doListSaveToFile

	private void doListPrint() {
		if (!(IOController.getInstance().getListDevice() == null)) {
			IOController.getInstance().getListDevice().print();
		} // if
	}// doListPrint

	private void doListProperties() {
		if (!(IOController.getInstance().getListDevice() == null)) {
			IOController.getInstance().getListDevice().showProperties();
		} // if
	}// doListProperties

	private void doListClear() {
		if (!(IOController.getInstance().getListDevice() == null)) {
			IOController.getInstance().getListDevice().clear();
		} // if
	}// doListClear

	private void updateView() {
		stateDisplay.updateDisplayAll();
		disassembler.updateDisplay();

		if (showCode == null) {
			return;
		} else if (!showCode.isVisible()) {
			return;
		} else {
			Thread task = new Thread(showCode);
			task.start();
		} // if ....
	}// updateView

	private void addDisk(JTextField source, int diskNumber) {
		JFileChooser fc = FilePicker.getDiskPicker();
		if (fc.showOpenDialog(frmMachine) == JFileChooser.CANCEL_OPTION) {
			System.out.println("Bailed out of the open");
			return;
		} // if
		if (!fc.getSelectedFile().exists()) {
			return; // try again
		} //
		if (diskControlUnit.addDiskDrive(diskNumber, fc.getSelectedFile().getAbsolutePath())) {
			source.setText(fc.getSelectedFile().getName());
			source.setToolTipText(fc.getSelectedFile().getAbsolutePath());
			// source.setForeground(new Color(128,0,0));
			source.setForeground(new Color(0, 64, 0));
		} // if added
	}// addDisk

	private void doDiskEffects(int diskIndex, int actionType) {
		ArrayList<String> selectedFiles = MenuUtility.getFilePathsSelected(mnuMemory);
		for (String s : selectedFiles) {
			System.out.printf("[doDiskEffects]  %s%n", s);
		}
		JLabel target = diskPanel.lblA; // default
		switch (diskIndex) {
		case 0:
			target = diskPanel.lblA;
			break;
		case 1:
			target = diskPanel.lblB;
			break;
		case 2:
			target = diskPanel.lblC;
			break;
		case 3:
			target = diskPanel.lblD;
			break;
		}// switch

		Blink blink = new Blink(target, Color.RED);
		Thread thread = new Thread(blink);
		thread.start();

	}// doDiskEffects

	public class Blink implements Runnable {
		JLabel label;
		Color color;

		public Blink(JLabel label, Color color) {
			this.label = label;
			this.color = color;
		}// Constructor

		@Override
		public void run() {
			label.setForeground(color);
			try {
				TimeUnit.MILLISECONDS.sleep(400);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} // try
			label.setForeground(Color.BLACK);
		}// run

	}// class blink

	private void removeDisk(JTextField source, int diskNumber) {
		diskControlUnit.removeDiskDrive(diskNumber);
		source.setForeground(new Color(255, 127, 80));
		source.setText(DiskPanel.NO_DISK);
		source.setToolTipText(DiskPanel.NO_DISK_HELP);
	}// remove disk

	private void removeAllDisks() {
		diskControlUnit.removeAllDiskDrives(diskPanel);
	}// removeAllDisks

	private void loadROM() {
		Class<Machine8080> thisClass = Machine8080.class;
		URL rom = thisClass.getResource("/hardware/resources/ROM.mem");
		MemoryLoaderFromFile.loadMemoryImage(new File(rom.getFile()));

		hexEditPanelConcurrent.loadData(Core.getInstance().getStorage());
		InLineDisassembler.getInstance().refreshDisplay();

	}// loadROM

	// private File getListFile(){
	//
	// }//getListFile

	@Override
	public void update(Observable cpuBuss, Object mte) {
		if (((MemoryTrapEvent) mte).getTrap().equals(Trap.DEBUG)) {
			System.out.printf("[update - DEBUG]  Location %04X%n", ((MemoryTrapEvent) mte).getLocation());
			stateDisplay.setDisplayComponentsEnabled(true);
			btnRun1.setSelected(false);
			cpu.setError(ErrorType.STOP);
			updateView();
		} // if - debug

	}// update

	// -------------------------------------------------------------------
	private void appClose() {
		Preferences myPrefs = Preferences.userNodeForPackage(Machine8080.class).node(this.getClass().getSimpleName());
		Dimension dim = frmMachine.getSize();
		myPrefs.putInt("Height", dim.height);
		myPrefs.putInt("Width", dim.width);
		Point point = frmMachine.getLocation();
		myPrefs.putInt("LocX", point.x);
		myPrefs.putInt("LocY", point.y);
		myPrefs.putInt("tabbedPaneIndex", tabbedPane.getSelectedIndex());
		myPrefs = null;
		cleanupObjects();
	}// appClose

	private void cleanupObjects() {
		removeAllDisks();
		cpuBuss.deleteObserver(this);
		if (trapManager != null) {
			trapManager.close();
			trapManager = null;
		} // if trapManager
		if (ioController != null) {
			// ioController.closeConnection();
			ioController.close();
			ioController = null;
		} // if ioController
		if (trapManager != null) {
			trapManager = null;
		} // if trapManager
		if (showCode != null) {
			showCode = null;
		} // if showCode

	}// cleanupObjects

	private void appInit() {
		// manage preferences
		Preferences myPrefs = Preferences.userNodeForPackage(Machine8080.class).node(this.getClass().getSimpleName());
		frmMachine.setSize(myPrefs.getInt("Width", 1086), myPrefs.getInt("Height", 875));// (1086, 875);
		frmMachine.setLocation(myPrefs.getInt("LocX", 100), myPrefs.getInt("LocY", 100));
		tabbedPane.setSelectedIndex(myPrefs.getInt("tabbedPaneIndex", 0));
		myPrefs = null;

		mnuMemorySeparatorFileStart.setName(MenuUtility.RECENT_FILES_START);
		mnuMemorySeparatorFileEnd.setName(MenuUtility.RECENT_FILES_END);
		hexEditPanelConcurrent.loadData(Core.getInstance().getStorage());
		// menuAdapter.setHexPanel( hexEditPanelConcurrent);
		EventQueue.invokeLater(disassembler);
		// disassembler.updateDisplay();

		/* get resources */
		Class<Machine8080> thisClass = Machine8080.class;
		btnStep.setIcon(new ImageIcon(thisClass.getResource("/hardware/resources/Button-Next-icon-48.png")));
		btnRun1.setIcon(new ImageIcon(thisClass.getResource("/hardware/resources/Button-Turn-On-icon-64.png")));
		btnRun1.setSelectedIcon(
				new ImageIcon(thisClass.getResource("/hardware/resources/Button-Turn-Off-icon-64.png")));
		loadROM();

		addSerialTerminal = false;
		lblSerialConnection.setText(NO_CONNECTION);
		if (addSerialTerminal) {
			installSerialTerminal();
		}//if

		IOController.getInstance().addListDevice(txtList);
		IOController.getInstance().addTTY();

		cpuBuss.addObserver(this);
	}// appInit
	
	private void installSerialTerminal(){
		IOController.getInstance().addSerialTerminal();
		lblSerialConnection.setText(ioController.getConnectionString());
	}//installSerialTerminal

	/**
	 * Create the application.
	 */
	public Machine8080() {
		initialize();
		appInit();
	}// Constructor

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmMachine = new JFrame();
		frmMachine.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				appClose();
			}// windowClosing
		});

		frmMachine.setTitle("CP/M System on Intel 8080 Processor");
		frmMachine.setBounds(100, 100, 450, 300);
		frmMachine.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		frmMachine.setJMenuBar(menuBar);

		JMenu mnuFile = new JMenu("File");
		menuBar.add(mnuFile);

		mnuFileBoot = new JMenuItem("Boot");
		mnuFileBoot.setToolTipText("Reload Rom");
		mnuFileBoot.setName(MNU_FILE_BOOT);
		mnuFileBoot.addActionListener(menuAdapter);

		mnuFile.add(mnuFileBoot);

		mnuMemory = new JMenu("Memory");
		menuBar.add(mnuMemory);

		JMenuItem mnuMemoryLoadFromFile = new JMenuItem("Load Memory From File...");
		mnuMemoryLoadFromFile.setName(MNU_MEMORY_LOAD_FROM_FILE);
		mnuMemoryLoadFromFile.addActionListener(menuAdapter);
		mnuMemory.add(mnuMemoryLoadFromFile);

		JMenuItem mnuMemoryLoadFromList = new JMenuItem("Load Memory From List");
		mnuMemoryLoadFromList.setName(MNU_MEMORY_LOAD_FROM_LIST);
		mnuMemoryLoadFromList.addActionListener(menuAdapter);
		mnuMemory.add(mnuMemoryLoadFromList);

		JMenuItem mnuMemorySaveSelectedToList = new JMenuItem("Save Selected to List");
		mnuMemorySaveSelectedToList.setName(MNU_MEMORY_SAVE_TO_LIST);
		mnuMemorySaveSelectedToList.addActionListener(menuAdapter);

		JSeparator separator = new JSeparator();
		mnuMemory.add(separator);
		mnuMemory.add(mnuMemorySaveSelectedToList);

		// JMenuItem mnuMemoryRemoveSelectedFromList = new JMenuItem("Remove Selected From List");
		// mnuMemoryRemoveSelectedFromList.setName(MNU_MEMORY_REMOVE_FROM_LIST);
		// mnuMemoryRemoveSelectedFromList.addActionListener(menuAdapter);
		// mnuMemory.add(mnuMemoryRemoveSelectedFromList);

		mnuMemorySeparatorFileStart = new JSeparator();
		mnuMemorySeparatorFileStart.setVisible(false);
		mnuMemorySeparatorFileStart.setName("recentFilesStart");
		mnuMemory.add(mnuMemorySeparatorFileStart);

		mnuMemorySeparatorFileEnd = new JSeparator();
		mnuMemorySeparatorFileEnd.setName("recentFilesEnd");
		mnuMemory.add(mnuMemorySeparatorFileEnd);

		JMenuItem mnuClearSelectedFiles = new JMenuItem("Clear Selected Files");
		mnuClearSelectedFiles.setName(MNU_MEMORY_CLEAR_SELECTED_FILES);
		mnuClearSelectedFiles.addActionListener(menuAdapter);
		mnuMemory.add(mnuClearSelectedFiles);

		JMenuItem mnuMemoryClearAllFiles = new JMenuItem("Clear All Files");
		mnuMemoryClearAllFiles.setName(MNU_MEMORY_CLEAR_ALL_FILES);
		mnuMemoryClearAllFiles.addActionListener(menuAdapter);
		mnuMemory.add(mnuMemoryClearAllFiles);

		JMenu mnuDisks = new JMenu("Disks");
		menuBar.add(mnuDisks);

		JMenuItem mnuMakeNewDisk = new JMenuItem("Make New Disk ...");
		mnuMakeNewDisk.setName(MNU_DISKS_MAKE_NEW_DISK);
		mnuMakeNewDisk.addActionListener(menuAdapter);

		JMenuItem mnuDisksDiskUtility = new JMenuItem("Disk Utility");
		mnuDisks.add(mnuDisksDiskUtility);
		mnuDisksDiskUtility.setName(MNU_DISKS_DISK_UTILITY);
		mnuDisksDiskUtility.addActionListener(menuAdapter);

		JSeparator separator_2 = new JSeparator();
		mnuDisks.add(separator_2);
		mnuDisks.add(mnuMakeNewDisk);

		JMenu mnuTools = new JMenu("Tools");
		menuBar.add(mnuTools);

		JMenuItem mnuToolsTrapManager = new JMenuItem("Trap Manager");
		mnuToolsTrapManager.setName(MNU_TOOLS_TRAP_MANAGER);
		mnuToolsTrapManager.addActionListener(menuAdapter);

		JMenuItem mnuToolsDebug = new JMenuItem("Debug");
		mnuToolsDebug.setName(MNU_TOOLS_DEBUG);
		mnuToolsDebug.addActionListener(menuAdapter);
		mnuToolsDebug.setToolTipText("Load Both Trap Manager and Show Listing");
		mnuTools.add(mnuToolsDebug);
		mnuTools.add(mnuToolsTrapManager);

		JMenuItem mntmNewMenuItem = new JMenuItem("Show Listing");
		mntmNewMenuItem.setName(MNU_TOOLS_SHOW_LISTING);
		mntmNewMenuItem.addActionListener(menuAdapter);
		mnuTools.add(mntmNewMenuItem);

		JSeparator separator_1 = new JSeparator();
		mnuTools.add(separator_1);

		JMenuItem mnuToolsReset = new JMenuItem("Reset");
		mnuToolsReset.setName(MNU_TOOLS_RESET);
		mnuToolsReset.addActionListener(menuAdapter);
		mnuToolsReset.setToolTipText("Clear Memory and reload Rom");
		mnuTools.add(mnuToolsReset);

		JMenu mnuWindows = new JMenu("Windows");
		menuBar.add(mnuWindows);

		mnuWindowsTBP = new JMenuItem("Toggle Bottom Panel");
		mnuWindowsTBP.setName(MNU_WINDOWS_TBP);
		mnuWindowsTBP.addActionListener(menuAdapter);
		mnuWindows.add(mnuWindowsTBP);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 310, 0, 20, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
		frmMachine.getContentPane().setLayout(gridBagLayout);

		panelTop = new JPanel();
		panelTop.setBorder(null);
		panelTop.setLayout(null);
		GridBagConstraints gbc_panelTop = new GridBagConstraints();
		gbc_panelTop.insets = new Insets(0, 0, 5, 0);
		gbc_panelTop.fill = GridBagConstraints.BOTH;
		gbc_panelTop.gridx = 0;
		gbc_panelTop.gridy = 0;
		frmMachine.getContentPane().add(panelTop, gbc_panelTop);

		panelMiddle = new JPanel();
		panelMiddle.setBounds(630, 0, 285, 310);
		panelTop.add(panelMiddle);
		GridBagLayout gbl_panelMiddle = new GridBagLayout();
		gbl_panelMiddle.columnWidths = new int[] { 285, 0 };
		gbl_panelMiddle.rowHeights = new int[] { 300, 0 };
		gbl_panelMiddle.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_panelMiddle.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panelMiddle.setLayout(gbl_panelMiddle);

		panelDisks = new JPanel();
		panelDisks.setPreferredSize(new Dimension(285, 303));
		panelDisks.setMinimumSize(new Dimension(285, 300));
		panelDisks.setBorder(null);
		panelDisks.setLayout(null);
		GridBagConstraints gbc_panelDisks = new GridBagConstraints();
		gbc_panelDisks.gridx = 0;
		gbc_panelDisks.gridy = 0;
		panelMiddle.add(panelDisks, gbc_panelDisks);

		diskPanel = new DiskPanel(diskPanelAdapter);
		diskPanel.setPreferredSize(new Dimension(275, 300));
		diskPanel.setBounds(5, 5, 275, 290);
		panelDisks.add(diskPanel);
		diskPanel.setLayout(null);

		paneLeft = new JPanel();
		paneLeft.setBounds(0, 0, 610, 310);
		panelTop.add(paneLeft);
		GridBagLayout gbl_paneLeft = new GridBagLayout();
		gbl_paneLeft.columnWidths = new int[] { 0, 0, 0 };
		gbl_paneLeft.rowHeights = new int[] { 0, 0 };
		gbl_paneLeft.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_paneLeft.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		paneLeft.setLayout(gbl_paneLeft);

		panelStateDisplay = new JPanel();
		panelStateDisplay.setMinimumSize(new Dimension(600, 300));
		panelStateDisplay.setPreferredSize(new Dimension(600, 300));
		panelStateDisplay.setLayout(null);
		GridBagConstraints gbc_panelStateDisplay = new GridBagConstraints();
		gbc_panelStateDisplay.insets = new Insets(0, 0, 0, 5);
		gbc_panelStateDisplay.fill = GridBagConstraints.VERTICAL;
		gbc_panelStateDisplay.gridx = 0;
		gbc_panelStateDisplay.gridy = 0;
		paneLeft.add(panelStateDisplay, gbc_panelStateDisplay);

		stateDisplay = new StateDisplay();
		stateDisplay.setPreferredSize(new Dimension(600, 300));
		stateDisplay.setMinimumSize(new Dimension(600, 300));
		stateDisplay.setBounds(5, 5, 600, 290);
		panelStateDisplay.add(stateDisplay);
		stateDisplay.setLayout(null);

		panel = new JPanel();
		panel.setBounds(931, 5, 120, 290);
		panelTop.add(panel);
		panel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel.setLayout(null);

		spinnerStepCount = new JSpinner();
		spinnerStepCount.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		spinnerStepCount.setBounds(37, 243, 45, 20);
		panel.add(spinnerStepCount);

		btnStep = new JButton();
		// btnStep.setIcon(new ImageIcon(Machine8080.class.getResource("/hardware/resources/Button-Next-icon-48.png")));
		btnStep.setBorder(null);
		btnStep.setContentAreaFilled(false);
		btnStep.setOpaque(true);
		btnStep.setName(BTN_STEP);
		btnStep.addActionListener(actionAdapter);
		btnStep.setBounds(24, 169, 71, 63);
		panel.add(btnStep);

		btnRun1 = new JToggleButton();
		btnRun1.setName(BTN_RUN);
		btnRun1.addActionListener(actionAdapter);
		btnRun1.setContentAreaFilled(false);
		btnRun1.setBorder(null);
		// btnRun1.setIcon(new
		// ImageIcon(Machine8080.class.getResource("/hardware/resources/Button-Turn-On-icon-64.png")));
		// btnRun1.setSelectedIcon(
		// new ImageIcon(Machine8080.class.getResource("/hardware/resources/Button-Turn-Off-icon-64.png")));
		btnRun1.setBounds(24, 54, 71, 71);
		panel.add(btnRun1);

		panelBottom = new JPanel();
		panelBottom.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GridBagConstraints gbc_panelBottom = new GridBagConstraints();
		gbc_panelBottom.insets = new Insets(0, 0, 5, 0);
		gbc_panelBottom.fill = GridBagConstraints.BOTH;
		gbc_panelBottom.gridx = 0;
		gbc_panelBottom.gridy = 1;
		frmMachine.getContentPane().add(panelBottom, gbc_panelBottom);
		GridBagLayout gbl_panelBottom = new GridBagLayout();
		gbl_panelBottom.columnWidths = new int[] { 700, 0 };
		gbl_panelBottom.rowHeights = new int[] { 5, 0 };
		gbl_panelBottom.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panelBottom.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		panelBottom.setLayout(gbl_panelBottom);

		panelBottomLeft = new JPanel();
		GridBagConstraints gbc_panelBottomLeft = new GridBagConstraints();
		gbc_panelBottomLeft.fill = GridBagConstraints.BOTH;
		gbc_panelBottomLeft.gridx = 0;
		gbc_panelBottomLeft.gridy = 0;
		panelBottom.add(panelBottomLeft, gbc_panelBottomLeft);
		GridBagLayout gbl_panelBottomLeft = new GridBagLayout();
		gbl_panelBottomLeft.columnWidths = new int[] { 850, 0 };
		gbl_panelBottomLeft.rowHeights = new int[] { 5, 0 };
		gbl_panelBottomLeft.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panelBottomLeft.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		panelBottomLeft.setLayout(gbl_panelBottomLeft);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 0;
		panelBottomLeft.add(tabbedPane, gbc_tabbedPane);

		tabDisassembler = new JPanel();
		tabbedPane.addTab("Disassembler", null, tabDisassembler, null);
		// GridBagLayout gbl_tabDisassembler = new GridBagLayout();
		// gbl_tabDisassembler.columnWidths = new int[]{0, 0};
		// gbl_tabDisassembler.rowHeights = new int[]{0, 0};
		// gbl_tabDisassembler.columnWeights = new double[]{1.0,
		// Double.MIN_VALUE};
		// gbl_tabDisassembler.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		GridBagConstraints gbc_disassembler = new GridBagConstraints();
		gbc_disassembler.fill = GridBagConstraints.BOTH;
		gbc_disassembler.gridx = 0;
		gbc_disassembler.gridy = 0;
		tabDisassembler.setLayout(new GridLayout(0, 1, 0, 0));
		tabDisassembler.add(disassembler, gbc_disassembler);
		// tabbedPane.setFocusTraversalPolicy(new FocusTraversalOnArray(new
		// Component[] { tabDisassembler, tabMemory }));

		tabMemory = new JPanel();
		tabMemory.setPreferredSize(new Dimension(50, 50));
		tabbedPane.addTab("Memory", null, tabMemory, null);
		GridBagConstraints gbc_hexPanel = new GridBagConstraints();
		gbc_hexPanel.fill = GridBagConstraints.BOTH;
		gbc_hexPanel.gridx = 0;
		gbc_hexPanel.gridy = 0;
		tabMemory.setLayout(new GridLayout(0, 1, 0, 0));
		tabMemory.add(hexEditPanelConcurrent, gbc_hexPanel);

		panelList = new JPanel();
		tabbedPane.addTab("List Device", null, panelList, null);
		GridBagLayout gbl_panelList = new GridBagLayout();
		gbl_panelList.columnWidths = new int[] { 917, 0, 0 };
		gbl_panelList.rowHeights = new int[] { 0, 0 };
		gbl_panelList.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		gbl_panelList.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		panelList.setLayout(gbl_panelList);

		panelListDisplay = new JScrollPane();
		GridBagConstraints gbc_panelListDisplay = new GridBagConstraints();
		gbc_panelListDisplay.insets = new Insets(0, 10, 0, 5);
		gbc_panelListDisplay.fill = GridBagConstraints.BOTH;
		gbc_panelListDisplay.gridx = 0;
		gbc_panelListDisplay.gridy = 0;
		panelList.add(panelListDisplay, gbc_panelListDisplay);

		txtList = new JTextArea();
		txtList.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent propertyChangeEvent) {

			}
		});
		txtList.setText(
				"         1         2         3         4         5         6         7         8\r\n12345678901234567890123456789012345678901234567890123456789012345678901234567890\r\n\r\n\r\n 5\r\n 6\r\n 7\r\n 8\r\n 9\r\n10\r\n11\r\n12\r\n13\r\n14\r\n15\r\n16\r\n17\r\n18\r\n19\r\n20\r\n21\r\n22\r\n23\r\n24");
		txtList.setFont(new Font("Courier New", Font.PLAIN, 13));
		panelListDisplay.setViewportView(txtList);

		panelListControls = new JPanel();
		GridBagConstraints gbc_panelListControls = new GridBagConstraints();
		gbc_panelListControls.fill = GridBagConstraints.BOTH;
		gbc_panelListControls.gridx = 1;
		gbc_panelListControls.gridy = 0;
		panelList.add(panelListControls, gbc_panelListControls);
		GridBagLayout gbl_panelListControls = new GridBagLayout();
		gbl_panelListControls.columnWidths = new int[] { 0, 0, 0 };
		gbl_panelListControls.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panelListControls.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl_panelListControls.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		panelListControls.setLayout(gbl_panelListControls);

		btnListLineFeed = new JButton("Line Feed");
		btnListLineFeed.setName(BTN_LIST_LINE_FEED);
		btnListLineFeed.addActionListener(actionAdapter);
		GridBagConstraints gbc_btnListLineFeed = new GridBagConstraints();
		gbc_btnListLineFeed.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnListLineFeed.insets = new Insets(0, 0, 5, 0);
		gbc_btnListLineFeed.gridx = 1;
		gbc_btnListLineFeed.gridy = 1;
		panelListControls.add(btnListLineFeed, gbc_btnListLineFeed);

		btnListFormFeed = new JButton("Form Feed");
		btnListFormFeed.setName(BTN_LIST_FORM_FEED);
		btnListFormFeed.addActionListener(actionAdapter);
		GridBagConstraints gbc_btnListFormFeed = new GridBagConstraints();
		gbc_btnListFormFeed.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnListFormFeed.insets = new Insets(0, 0, 5, 0);
		gbc_btnListFormFeed.gridx = 1;
		gbc_btnListFormFeed.gridy = 3;
		panelListControls.add(btnListFormFeed, gbc_btnListFormFeed);

		btnListSaveToFile = new JButton("Save To FIle...");
		btnListSaveToFile.setName(BTN_LIST_SAVE_TO_FILE);
		btnListSaveToFile.addActionListener(actionAdapter);
		GridBagConstraints gbc_btnListSaveToFile = new GridBagConstraints();
		gbc_btnListSaveToFile.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnListSaveToFile.insets = new Insets(0, 0, 5, 0);
		gbc_btnListSaveToFile.gridx = 1;
		gbc_btnListSaveToFile.gridy = 5;
		panelListControls.add(btnListSaveToFile, gbc_btnListSaveToFile);

		btnListPrint = new JButton("Print...");
		btnListPrint.setName(BTN_LIST_PRINT);
		btnListPrint.addActionListener(actionAdapter);
		GridBagConstraints gbc_btnListPrint = new GridBagConstraints();
		gbc_btnListPrint.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnListPrint.insets = new Insets(0, 0, 5, 0);
		gbc_btnListPrint.gridx = 1;
		gbc_btnListPrint.gridy = 7;
		panelListControls.add(btnListPrint, gbc_btnListPrint);

		btnListProperties = new JButton("List Properties...");
		btnListProperties.setName(BTN_LIST_PROPERTIES);
		btnListProperties.addActionListener(actionAdapter);
		GridBagConstraints gbc_btnListProperties = new GridBagConstraints();
		gbc_btnListProperties.insets = new Insets(0, 0, 5, 0);
		gbc_btnListProperties.gridx = 1;
		gbc_btnListProperties.gridy = 9;
		panelListControls.add(btnListProperties, gbc_btnListProperties);

		btnListClear = new JButton("Clear");
		btnListClear.setName(BTN_LIST_CLEAR);
		btnListClear.addActionListener(actionAdapter);
		GridBagConstraints gbc_btnListClear = new GridBagConstraints();
		gbc_btnListClear.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnListClear.gridx = 1;
		gbc_btnListClear.gridy = 11;
		panelListControls.add(btnListClear, gbc_btnListClear);

		panelStatus = new JPanel();
		panelStatus.setPreferredSize(new Dimension(10, 25));
		panelStatus.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_panelStatus = new GridBagConstraints();
		gbc_panelStatus.anchor = GridBagConstraints.SOUTH;
		gbc_panelStatus.fill = GridBagConstraints.HORIZONTAL;
		gbc_panelStatus.gridx = 0;
		gbc_panelStatus.gridy = 2;
		frmMachine.getContentPane().add(panelStatus, gbc_panelStatus);
		GridBagLayout gbl_panelStatus = new GridBagLayout();
		gbl_panelStatus.columnWidths = new int[] { 0, 0 };
		gbl_panelStatus.rowHeights = new int[] { 0, 0 };
		gbl_panelStatus.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_panelStatus.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panelStatus.setLayout(gbl_panelStatus);

		lblSerialConnection = new JLabel("New label");
		GridBagConstraints gbc_lblSerialConnection = new GridBagConstraints();
		gbc_lblSerialConnection.gridx = 0;
		gbc_lblSerialConnection.gridy = 0;
		panelStatus.add(lblSerialConnection, gbc_lblSerialConnection);
	}// initialize

	/* classes */
	/* ............................. */
	protected class Machine8080ActionAdapter implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent actionEvent) {
			String name = ((Component) actionEvent.getSource()).getName();
			switch (name) {
			case BTN_STEP:
				doStep();
				break;
			case BTN_RUN:
				doRun();
				break;

			case BTN_LIST_LINE_FEED:
				doListLineFeed();
				break;
			case BTN_LIST_FORM_FEED:
				doListFormFeed();
				break;
			case BTN_LIST_SAVE_TO_FILE:
				doListSaveToFile();
				break;
			case BTN_LIST_PRINT:
				doListPrint();
				break;
			case BTN_LIST_PROPERTIES:
				doListProperties();
				break;
			case BTN_LIST_CLEAR:
				doListClear();
				break;

			default:
				assert false : name + " is not a valid button name";
			}// switch name
		}// actionPerformed
	}// class Machine8080ActionAdapter
	/* ............................. */

	/* ............................. */
	protected class Machine8080MenuAdapter implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent actionEvent) {
			System.out.println("actionPerformed");
			JMenuItem sourceMenu = null;
			String sourceName = EMPTY_STRING;

			if (actionEvent.getSource() instanceof JMenuItem) {
				sourceMenu = (JMenuItem) actionEvent.getSource();
				sourceName = sourceMenu.getName();
			} // if JMenuItem

			switch (sourceName) {
			// MNU_FILE_NEW
			case Machine8080.MNU_FILE_BOOT:
				doBoot();
				break;

			case Machine8080.MNU_MEMORY_LOAD_FROM_FILE:
				doMemoryLoadFromFile(actionEvent);
				EventQueue.invokeLater(hexEditPanelConcurrent);
				InLineDisassembler.getInstance().refreshDisplay();
				break;
			case Machine8080.MNU_MEMORY_CLEAR_ALL_FILES:
				MenuUtility.clearList(mnuMemory);
				// removeAllFileItems((JPopupMenu) sourceMenu.getParent());
				break;
			case Machine8080.MNU_MEMORY_CLEAR_SELECTED_FILES:
				MenuUtility.clearListSelected(mnuMemory);
				// removeSelectedFileItems((JPopupMenu) sourceMenu.getParent());
				break;
			case Machine8080.MNU_MEMORY_LOAD_FROM_LIST:
				doMemoryLoadFromList(actionEvent);
				break;
			case Machine8080.MNU_MEMORY_SAVE_TO_LIST:
				doMemoryAddSelectedToList(actionEvent);
				break;

			case Machine8080.MNU_DISKS_MAKE_NEW_DISK:
				MakeNewDisk.makeNewDisk();
				break;
			case Machine8080.MNU_DISKS_DISK_UTILITY:
				DiskUtility diskUtility = DiskUtility.getInstance();
				diskUtility.setVisible(true);
				break;

			case Machine8080.MNU_TOOLS_DEBUG:// MNU_TOOLS_DEBUG
				TrapManager trapManager = TrapManager.getInstance();
				trapManager.setVisible(true);
				showCode = ShowCode.getInstance();
				showCode.setVisible(true);
				break;

			case Machine8080.MNU_TOOLS_TRAP_MANAGER://
				trapManager = TrapManager.getInstance();
				trapManager.setVisible(true);
				break;
			case Machine8080.MNU_TOOLS_SHOW_LISTING:
				showCode = ShowCode.getInstance();
				showCode.setVisible(true);
				break;
			case Machine8080.MNU_TOOLS_RESET:
				doBoot();
				break;
			case Machine8080.MNU_WINDOWS_TBP:
				// Dimension dim = frmMachine.getSize();
				// System.out.printf("Height = %d, Width = %d%n", (int)dim.getHeight(),(int)dim.getWidth());
				// panelBottom.setVisible(!panelBottom.isVisible());
				Document docList = txtList.getDocument();
				try {
					docList.remove(0, docList.getLength());
					docList.insertString(docList.getLength(), "abcdef", null);
					docList.insertString(docList.getLength(), System.lineSeparator(), null);
					docList.insertString(docList.getLength(), "ABCDEF", null);
					docList.insertString(docList.getLength(), System.lineSeparator(), null);
					docList.insertString(docList.getLength(), "1234567890", null);

				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			default:
				assert false : sourceName + " is not a valid menu item\n";
			}// Switch sourceName
		}// actionPerformed

		private void doMemoryLoadFromFile(ActionEvent actionEvent) {

			JFileChooser fc = pathMemLoad == null ? FilePicker.getMemPicker() : FilePicker.getMemPicker(pathMemLoad);
			if (fc.showOpenDialog(frmMachine) == JFileChooser.CANCEL_OPTION) {
				System.out.println("Bailed out of the open");
				return;
			} // if - open
			pathMemLoad = Paths.get(fc.getSelectedFile().getParent());
			loadMemoryFromFile(fc.getSelectedFile());
		}// doMemoryLoadFromFile

		private void loadMemoryFromFile(File selectedFile) {
			MemoryLoaderFromFile.loadMemoryImage(selectedFile);
			MenuUtility.addItemToList(mnuMemory, selectedFile, new JCheckBoxMenuItem());
		}// loadMemoryFromFile

		private void doMemoryLoadFromList(ActionEvent actionEvent) {
			// JFileChooser fc = FilePicker.getListMemPicker();
			JFileChooser fc = FilePicker.getAnyListPicker();
			if (fc.showOpenDialog(frmMachine) != JFileChooser.APPROVE_OPTION) {
				System.out.println("You cancelled the Load Memory from File List...");
			} else {
				FileReader fileReader;
				String filePathName = null;
				File currentFile;
				try {
					fileReader = new FileReader((fc.getSelectedFile().getAbsolutePath()));
					BufferedReader reader = new BufferedReader(fileReader);
					while ((filePathName = reader.readLine()) != null) {
						filePathName = filePathName.replaceFirst("(?i)\\.list$", "\\.mem");
						currentFile = new File(filePathName);
						loadMemoryFromFile(currentFile);
					} // while
					reader.close();
				} catch (IOException e1) {
					System.out.printf(e1.getMessage() + "%n", "");
				} // try
			} // if

			return;
		}

		private void doMemoryAddSelectedToList(ActionEvent actionEvent) {
			JFileChooser fc = FilePicker.getListMemPicker();
			if (fc.showSaveDialog(frmMachine) != JFileChooser.APPROVE_OPTION) {
				System.out.println("You cancelled Save Selected as List...");
				return;
			} // if
			String listFile = fc.getSelectedFile().getAbsolutePath();
			String completeSuffix = DOT + FilePicker.LIST_MEM_SUFFIX;
			listFile = listFile.replaceFirst("//" + completeSuffix + "$", EMPTY_STRING);
			try {
				FileWriter fileWriter = new FileWriter(listFile + completeSuffix);
				BufferedWriter writer = new BufferedWriter(fileWriter);

				ArrayList<String> selectedFiles = MenuUtility.getFilePathsSelected(mnuMemory);
				for (String selectedFile : selectedFiles) {
					writer.write(selectedFile + System.lineSeparator());
				} // for
				writer.close();

			} catch (Exception e1) {
				e1.printStackTrace();
				return;
			} // try
		}// doMemoryAddSelectedToList

		// ----------------------------------

	}// class Machine8080MenuAdapter.actionPerformed
	/* ............................. */
	/* ............................. */

	public class DiskPanelAdapter implements MouseListener, DCUActionListener {

		@Override
		public void mouseClicked(MouseEvent mouseEvent) {
			if (mouseEvent.getClickCount() >= 2) {
				JTextField source = (JTextField) mouseEvent.getComponent();
				int index = getIndex(source);
				if (source.getText().equals(DiskPanel.NO_DISK)) {
					addDisk(source, index);
				} else {
					removeDisk(source, index);
				} // if no disk
			} // if 2 or more

		}// mouseClicked

		private int getIndex(JTextField source) {
			int index = 0;
			String name = source.getName();
			switch (name) {
			case DiskPanel.TXT_DISK_A:
				index = 0;
				break;
			case DiskPanel.TXT_DISK_B:
				index = 1;
				break;
			case DiskPanel.TXT_DISK_C:
				index = 2;
				break;
			case DiskPanel.TXT_DISK_D:
				index = 3;
				break;
			}// switch
			return index;
		}// getIndex

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
		}// mouseEntered

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
		}// mouseExited

		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
		}// mousePressed

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
		}// mouseReleased

		/* DCU Actions */

		@Override
		public void dcuAction(DCUActionEvent dcuEvent) {
			doDiskEffects(dcuEvent.getDiskIndex(), dcuEvent.getActionType());
		}// dcuAction

	}// DiskDisplayAdapter
	/* ............................. */

	/* classes */

	public static final String DOT = "."; // Period
	public static final String PERIOD = "."; // Period
	public static final String EMPTY_STRING = "";

	public static final String BTN_STEP = "btnStep";
	public static final String BTN_RUN = "btnRun";

	public static final String NO_CONNECTION = "<<No Connection>>";

	public static final String MNU_FILE_BOOT = "mnuFileBoot";

	public static final String MNU_MEMORY_LOAD_FROM_FILE = "mnuMemoryLoadFromFile";
	public static final String MNU_MEMORY_LOAD_FROM_LIST = "mnuMemoryLoadFromList";
	public static final String MNU_MEMORY_SAVE_TO_LIST = "mnuMemorySaveSelectedToList";
	public static final String MNU_MEMORY_CLEAR_ALL_FILES = "mnuClearAllFiles";
	public static final String MNU_MEMORY_CLEAR_SELECTED_FILES = "mnuClearSelectedFiles";

	public static final String MNU_DISKS_MAKE_NEW_DISK = "mnuMakeNewDisk";
	public static final String MNU_DISKS_DISK_UTILITY = "mnuDisksDiskUtility";

	public static final String MNU_TOOLS_DEBUG = "mnuToolsDebug";
	public static final String MNU_TOOLS_TRAP_MANAGER = "mnuToolsTrapManager";
	public static final String MNU_TOOLS_SHOW_LISTING = "mnuToolsShowListing";
	public static final String MNU_TOOLS_RESET = "mnuToolsReset";

	public static final String MNU_WINDOWS_TBP = "mnuWindowsTBP";

	public static final String BTN_LIST_LINE_FEED = "btnListLineFeed";
	public static final String BTN_LIST_FORM_FEED = "btnListFormFeed";
	public static final String BTN_LIST_SAVE_TO_FILE = "btnListSaveToFile";
	public static final String BTN_LIST_PRINT = "btnListPrint";
	public static final String BTN_LIST_PROPERTIES = "btnListProperties";
	public static final String BTN_LIST_CLEAR = "btnListCLEAR";

	private JFrame frmMachine;
	private JPanel panelMiddle;
	private JPanel panelDisks;

	private JPanel paneLeft;
	private JPanel panelStateDisplay;
	private JPanel panel;
	private JButton btnStep;
	private JPanel panelTop;
	private JPanel panelBottom;
	private JPanel panelStatus;
	private JTabbedPane tabbedPane;
	private JPanel tabMemory;

	private JMenuItem mnuFileBoot;
	private JPanel tabDisassembler;
	// private InLineDisassembler disassembler;
	private JPanel panelBottomLeft;
	private JSpinner spinnerStepCount;
	private JToggleButton btnRun1;
	private JLabel lblSerialConnection;
	private JSeparator mnuMemorySeparatorFileStart;
	private JSeparator mnuMemorySeparatorFileEnd;
	private JMenu mnuMemory;
	private JMenuItem mnuWindowsTBP;
	private JPanel panelList;
	private JPanel panelListControls;
	private JScrollPane panelListDisplay;
	private JTextArea txtList;
	private JButton btnListLineFeed;
	private JButton btnListFormFeed;
	private JButton btnListSaveToFile;
	private JButton btnListPrint;
	private JButton btnListProperties;
	private JButton btnListClear;
}// class Machine8080
