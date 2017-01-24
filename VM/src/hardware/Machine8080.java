package hardware;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
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
import java.io.File;
import java.net.URL;
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
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;

import disks.DCUActionEvent;
import disks.DCUActionListener;
import disks.DiskControlUnit;
import disks.diskPanel.DiskPanel;
import disks.utility.DiskUtility;
import memory.Core;
import memory.MemoryLoaderFromFile;
import utilities.FilePicker;
import utilities.hexEdit.HexEditPanelConcurrent;
import utilities.inLineDissembler.InLineDisassembler;

public class Machine8080 {

	private Machine8080MenuAdapter menuAdapter;
	private Machine8080ActionAdapter actionAdapter;
	private DiskPanelAdapter diskPanelAdapter;
	private DiskPanel diskDisplay;
	private StateDisplay stateDisplay;
	// private Core core = Core.getInstance();
	private CentralProcessingUnit cpu = CentralProcessingUnit.getInstance();
	private DiskControlUnit diskControlUnit = DiskControlUnit.getgetInstance();
	private InLineDisassembler disassembler = InLineDisassembler.getInstance();
	private HexEditPanelConcurrent hexEditPanelConcurrent = new HexEditPanelConcurrent();
	private JFrame frmMachine;

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
		System.out.println("actionPerformed: doStep");
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
	}//

	private void doRun() {
		if (btnRun1.isSelected()) {
			cpu.setError(ErrorType.NONE);
			System.out.println("actionPerformed: doRun");
			Thread t = new Thread(cpu);
			t.start();
		} else {
			System.out.println("actionPerformed: doStop");
			cpu.setError(ErrorType.STOP);
			updateView();
		} // if
	}// doRun

	private void updateView() {
		stateDisplay.updateDisplayAll();
		disassembler.updateDisplay();
	}// updateView

	private void addDisk(JTextField source, int diskNumber) {
		JFileChooser fc = FilePicker.getDiskPicker("Disketts & Floppies", "F3ED", "F5DD", "F3DD", "F3HD", "F5HD",
				"F8SS", "F8DS");
		if (fc.showOpenDialog(null) == JFileChooser.CANCEL_OPTION) {
			System.out.println("Bailed out of the open");
			return;
		} // if
		if (!fc.getSelectedFile().exists()) {
			return; // try again
		} //
		if (diskControlUnit.addDiskDrive(diskNumber, fc.getSelectedFile().getAbsolutePath())) {
			source.setText(fc.getSelectedFile().getName());
			source.setToolTipText(fc.getSelectedFile().getAbsolutePath());
		} // if added
	}// addDisk
	
	private void doDiskEffects(int diskIndex,int actionType){
		 JLabel target = diskDisplay.lblA;	// default
		switch(diskIndex){
		case 0:
			target = diskDisplay.lblA;
			break;
		case 1:
			target = diskDisplay.lblB;
			break;
		case 2:
			target = diskDisplay.lblC;
			break;
		case 3:
			target = diskDisplay.lblD;
			break;
		}//switch
		
		Blink blink = new Blink(target,Color.RED);
		Thread thread = new Thread(blink);
		thread.start();
	
		
		
		
	}//doDiskEffects
	
	public class Blink implements Runnable{
		JLabel label;
		Color color;
		public Blink(JLabel label,Color color){
			this.label= label;
			this.color=color;
		}//Constructor
		@Override
		public void run() {
			label.setForeground(color);
			try {
				TimeUnit.MILLISECONDS.sleep(400);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//try
			label.setForeground(Color.BLACK);
		}//run
		
	}//class blink

	private void removeDisk(JTextField source, int diskNumber) {
		diskControlUnit.removeDiskDrive(diskNumber);
		source.setText(DiskPanel.NO_DISK);
		source.setToolTipText(DiskPanel.NO_DISK_HELP);
	}// remove disk

	// -------------------------------------------------------------------
	private void appClose() {
		Preferences myPrefs = Preferences.userNodeForPackage(Machine8080.class);
		Dimension dim = frmMachine.getSize();
		myPrefs.putInt("Height", dim.height);
		myPrefs.putInt("Width", dim.width);
		Point point = frmMachine.getLocation();
		myPrefs.putInt("LocX", point.x);
		myPrefs.putInt("LocY", point.y);
		myPrefs = null;
	}// appClose

	private void appInit0() {
		menuAdapter = new Machine8080MenuAdapter();
		actionAdapter = new Machine8080ActionAdapter();
		diskPanelAdapter = new DiskPanelAdapter();
	}// appInit0

	private void appInit() {
		// manage preferences
		Preferences myPrefs = Preferences.userNodeForPackage(Machine8080.class);
		frmMachine.setSize(myPrefs.getInt("Width", 1352), myPrefs.getInt("Height", 730));
		frmMachine.setLocation(myPrefs.getInt("LocX", 100), myPrefs.getInt("LocY", 100));
		myPrefs = null;
		hexEditPanelConcurrent.loadData(Core.getInstance().getStorage());
		// menuAdapter.setHexPanel( hexEditPanelConcurrent);
		EventQueue.invokeLater(disassembler);
		// disassembler.updateDisplay();
		
		/* get resources  */
		Class thisClass = Machine8080.class;
		btnStep.setIcon(new ImageIcon(thisClass.getResource("/hardware/resources/Button-Next-icon-48.png")));
		btnRun1.setIcon(new ImageIcon(thisClass.getResource("/hardware/resources/Button-Turn-On-icon-64.png")));
		btnRun1.setSelectedIcon(
				new ImageIcon(thisClass.getResource("/hardware/resources/Button-Turn-Off-icon-64.png")));
		URL  rom = thisClass.getResource("/hardware/resources/ROM.mem");
		MemoryLoaderFromFile.loadMemoryImage(new File(rom.getFile()));
		

	}// appInit

	/**
	 * Create the application.
	 */
	public Machine8080() {
		appInit0();
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

		mnuFileNew = new JMenuItem("New");
		mnuFileNew.setName(MNU_FILE_NEW);
		mnuFileNew.addActionListener(menuAdapter);

		mnuFile.add(mnuFileNew);

		JMenu mnuMemory = new JMenu("Memory");
		menuBar.add(mnuMemory);

		JMenuItem mnuMemoryLoadFromFile = new JMenuItem("Load Memory From File...");
		mnuMemoryLoadFromFile.setName(MNU_MEMORY_LOAD_FROM_FILE);
		mnuMemoryLoadFromFile.addActionListener(menuAdapter);
		mnuMemory.add(mnuMemoryLoadFromFile);

		JSeparator separator = new JSeparator();
		mnuMemory.add(separator);

		JMenuItem mnuMemoryClearAllFiles = new JMenuItem("Clear All Files");
		mnuMemoryClearAllFiles.setName(MNU_CLEAR_ALL_FILES);
		mnuMemoryClearAllFiles.addActionListener(menuAdapter);
		mnuMemory.add(mnuMemoryClearAllFiles);

		JMenuItem mnuClearSelectedFiles = new JMenuItem("Clear Selected Files");
		mnuClearSelectedFiles.setName(MNU_CLEAR_SELECTED_FILES);
		mnuClearSelectedFiles.addActionListener(menuAdapter);
		mnuMemory.add(mnuClearSelectedFiles);

		JMenu mnuDisks = new JMenu("Disks");
		menuBar.add(mnuDisks);

		JMenu mnuTools = new JMenu("Tools");
		menuBar.add(mnuTools);

		JMenuItem mnuToolsDiskUtility = new JMenuItem("Disk Utility");
		mnuToolsDiskUtility.setName(MNU_DISK_UTILITY);
		mnuToolsDiskUtility.addActionListener(menuAdapter);
		mnuTools.add(mnuToolsDiskUtility);

		JMenu mnuWindows = new JMenu("Windows");
		menuBar.add(mnuWindows);
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

		diskDisplay = new DiskPanel(diskPanelAdapter);
		diskDisplay.setPreferredSize(new Dimension(275, 300));
		diskDisplay.setBounds(5, 5, 275, 300);
		panelDisks.add(diskDisplay);
		diskDisplay.setLayout(null);

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
		stateDisplay.setBounds(5, 5, 600, 300);
		panelStateDisplay.add(stateDisplay);
		stateDisplay.setLayout(null);
		
		JButton btnTest = new JButton("New button");
		btnTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doDiskEffects(0,1);
			}
		});
		btnTest.setBounds(925, 11, 89, 23);
		panelTop.add(btnTest);

		panelBottom = new JPanel();
		panelBottom.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GridBagConstraints gbc_panelBottom = new GridBagConstraints();
		gbc_panelBottom.insets = new Insets(0, 0, 5, 0);
		gbc_panelBottom.fill = GridBagConstraints.BOTH;
		gbc_panelBottom.gridx = 0;
		gbc_panelBottom.gridy = 1;
		frmMachine.getContentPane().add(panelBottom, gbc_panelBottom);
		GridBagLayout gbl_panelBottom = new GridBagLayout();
		gbl_panelBottom.columnWidths = new int[] { 700, 0, 0 };
		gbl_panelBottom.rowHeights = new int[] { 5, 0 };
		gbl_panelBottom.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_panelBottom.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		panelBottom.setLayout(gbl_panelBottom);

		panelBottomLeft = new JPanel();
		GridBagConstraints gbc_panelBottomLeft = new GridBagConstraints();
		gbc_panelBottomLeft.insets = new Insets(0, 0, 0, 5);
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
		// GridBagLayout gbl_tabMemory = new GridBagLayout();
		// gbl_tabMemory.columnWidths = new int[] { 0 };
		// gbl_tabMemory.rowHeights = new int[] { 0 };
		// gbl_tabMemory.columnWeights = new double[] { Double.MIN_VALUE };
		// gbl_tabMemory.rowWeights = new double[] { Double.MIN_VALUE };
		// tabMemory.setLayout(gbl_tabMemory);
		tabMemory.add(hexEditPanelConcurrent, gbc_hexPanel);

		panelRun = new JPanel();
		GridBagConstraints gbc_panelRun = new GridBagConstraints();
		gbc_panelRun.fill = GridBagConstraints.BOTH;
		gbc_panelRun.gridx = 1;
		gbc_panelRun.gridy = 0;
		panelBottom.add(panelRun, gbc_panelRun);
		panelRun.setLayout(null);

		panel = new JPanel();
		panel.setBounds(10, 10, 160, 300);
		panelRun.add(panel);
		panel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel.setLayout(null);

		spinnerStepCount = new JSpinner();
		spinnerStepCount.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		spinnerStepCount.setBounds(57, 29, 45, 20);
		panel.add(spinnerStepCount);

		btnStep = new JButton();
//		btnStep.setIcon(new ImageIcon(Machine8080.class.getResource("/hardware/resources/Button-Next-icon-48.png")));
		btnStep.setBorder(null);
		btnStep.setContentAreaFilled(false);
		btnStep.setOpaque(true);
		btnStep.setName(BTN_STEP);
		btnStep.addActionListener(actionAdapter);
		btnStep.setBounds(44, 60, 71, 63);
		panel.add(btnStep);

		btnRun1 = new JToggleButton();
		btnRun1.setName(BTN_RUN);
		btnRun1.addActionListener(actionAdapter);
		btnRun1.setContentAreaFilled(false);
		btnRun1.setBorder(null);
//		btnRun1.setIcon(new ImageIcon(Machine8080.class.getResource("/hardware/resources/Button-Turn-On-icon-64.png")));
//		btnRun1.setSelectedIcon(
//				new ImageIcon(Machine8080.class.getResource("/hardware/resources/Button-Turn-Off-icon-64.png")));
		btnRun1.setBounds(44, 181, 71, 71);
		panel.add(btnRun1);

		// InLineDisassembler disassembler = InLineDisassembler.getInstance();
		// GridBagLayout gbl_disassembler = new GridBagLayout();
		// gbl_disassembler.columnWidths = new int[]{0};
		// gbl_disassembler.rowHeights = new int[]{0};
		// gbl_disassembler.columnWeights = new double[]{1.0,Double.MIN_VALUE};
		// gbl_disassembler.rowWeights = new double[]{1.0,Double.MIN_VALUE};
		// disassembler.setLayout(new GridLayout(1, 0, 0, 0));

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
		gbl_panelStatus.columnWidths = new int[] { 0 };
		gbl_panelStatus.rowHeights = new int[] { 0 };
		gbl_panelStatus.columnWeights = new double[] { Double.MIN_VALUE };
		gbl_panelStatus.rowWeights = new double[] { Double.MIN_VALUE };
		panelStatus.setLayout(gbl_panelStatus);
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
			default:
				assert false : name + " is not a valid button name";
			}// switch name
		}// actionPerformed
	}// class Machine8080ActionAdapter
	/* ............................. */

	/* ............................. */
	protected class Machine8080MenuAdapter implements ActionListener {
		// private HexEditPanelConcurrent hexEditPanelConcurrent;
		//
		// public void setHexPanel(HexEditPanelConcurrent hexEditPanelConcurrent) {
		// this.hexEditPanelConcurrent = hexEditPanelConcurrent;
		// }//

		@Override
		public void actionPerformed(ActionEvent actionEvent) {
			System.out.println("actionPerformed");
			JMenuItem sourceMenu = null;
			String sourceName = null;

			if (actionEvent.getSource() instanceof JMenuItem) {
				sourceMenu = (JMenuItem) actionEvent.getSource();
				sourceName = sourceMenu.getName();
			} // if JMenuItem

			switch (sourceName) {
			case Machine8080.MNU_MEMORY_LOAD_FROM_FILE:
				doMemoryLoadFromFile(actionEvent);
				EventQueue.invokeLater(hexEditPanelConcurrent);
				InLineDisassembler.getInstance().refreshDisplay();
				break;
			case Machine8080.MNU_CLEAR_ALL_FILES:
				removeAllFileItems((JPopupMenu) sourceMenu.getParent());
				break;
			case Machine8080.MNU_CLEAR_SELECTED_FILES:
				removeSelectedFileItems((JPopupMenu) sourceMenu.getParent());
				break;
			case Machine8080.MNU_DISK_UTILITY:
				Thread diskUtility = new Thread(new DiskUtility());
				diskUtility.start();
				break;
			default:
				assert false : sourceName + " is not a valid menu item\n";
			}// Switch sourceName
		}// actionPerformed

		private void doMemoryLoadFromFile(ActionEvent actionEvent) {
			JFileChooser fc = FilePicker.getDataPicker("Memory Image Files", "mem", "hex");
			if (fc.showOpenDialog(null) == JFileChooser.CANCEL_OPTION) {
				System.out.println("Bailed out of the open");
				return;
			} // if - open
			String fileName = MemoryLoaderFromFile.loadMemoryImage(fc.getSelectedFile());
			System.out.printf("FileName: %s%n", fileName);

			JMenuItem sourceMenu = (JMenuItem) actionEvent.getSource();
			appendMenuItem(fileName, (JPopupMenu) sourceMenu.getParent());

			// InLineDisassembler.getInstance().updateDisplay();

		}// doMemoryLoadFromFile
			// ----------------------------------

		private void appendMenuItem(String name, JPopupMenu parentMenu) {

			for (int i = parentMenu.getComponentCount() - 1; i > 0; i--) {
				if (!(parentMenu.getComponent(i) instanceof JCheckBoxMenuItem)) {
					continue;
				} // if right type
				if (((JCheckBoxMenuItem) parentMenu.getComponent(i)).getName().equals(name)) {
					return;
				} // if
			} // for do we already have it?

			JCheckBoxMenuItem mnuNew = new JCheckBoxMenuItem(name);
			mnuNew.setName(name);
			mnuNew.setActionCommand(name);
			parentMenu.add(mnuNew);

		}// appendMenuItem

		private void removeSelectedFileItems(JPopupMenu parentMenu) {
			for (int i = parentMenu.getComponentCount() - 1; i > 0; i--) {
				if (!(parentMenu.getComponent(i) instanceof JCheckBoxMenuItem)) {
					continue;
				} // if right type
				if (((JCheckBoxMenuItem) parentMenu.getComponent(i)).isSelected()) {
					parentMenu.remove(i);
				} // if do we remove it?
			} // for

		}// removeSelectedFileItem

		private void removeAllFileItems(JPopupMenu parentMenu) {
			for (int i = parentMenu.getComponentCount() - 1; i > 0; i--) {
				if (parentMenu.getComponent(i) instanceof JCheckBoxMenuItem) {
					parentMenu.remove(i);
				} // if right type
			} // for
		}// removeMenuItem

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
		}//dcuAction

	}// DiskDisplayAdapter
	/* ............................. */

	/* classes */

	public static final String BTN_STEP = "btnStep";
	public static final String BTN_RUN = "btnRun";
	// public static final String BTN_RUN_TEXT = "Run";
	// public static final String BTN_STOP = "btnStop";
	// public static final String BTN_STOP_TEXT = "Stop";

	public static final String MNU_FILE_NEW = "mnuFileNew";

	public static final String MNU_MEMORY_LOAD_FROM_FILE = "mnuMemoryLoadFromFile";

	public static final String MNU_CLEAR_ALL_FILES = "mnuClearAllFiles";
	public static final String MNU_CLEAR_SELECTED_FILES = "mnuClearSelectedFiles";
	public static final String MNU_DISK_UTILITY = "mnuToolsDiskUtility";

	private JPanel panelMiddle;
	private JPanel panelDisks;

	private JPanel paneLeft;
	private JPanel panelStateDisplay;
	private JPanel panel;
	private JButton btnStep;
	private JPanel panelRun;
	private JPanel panelTop;
	private JPanel panelBottom;
	private JPanel panelStatus;
	private JTabbedPane tabbedPane;
	private JPanel tabMemory;

	private JMenuItem mnuFileNew;
	private JPanel tabDisassembler;
	// private InLineDisassembler disassembler;
	private JPanel panelBottomLeft;
	private JSpinner spinnerStepCount;
	private JToggleButton btnRun1;
}// class Machine8080
