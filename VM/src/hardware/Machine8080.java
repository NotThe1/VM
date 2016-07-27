package hardware;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;

import javax.swing.JFrame;

import java.awt.GridLayout;
import java.util.prefs.Preferences;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.SpinnerNumberModel;

import codeSupport.TableMaker;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JMenuItem;

import memory.Core;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.Font;

import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.JButton;

import disks.DiskDisplay;
import javax.swing.border.BevelBorder;
import javax.swing.border.MatteBorder;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Machine8080 {

	private Machine8080MenuAdapter menuAdapter;
	private DiskDisplay diskDisplay;
	private StateDisplay stateDisplay;
//	private Core core = Core.getInstance();
//	private CentralProcessingUnit cpu = CentralProcessingUnit.

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
				}
			}
		});
	}// main
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

private void appInit0(){
			menuAdapter = new Machine8080MenuAdapter();

}//appInit0
	private void appInit() {
		// manage preferences
		Preferences myPrefs = Preferences.userNodeForPackage(Machine8080.class);
		frmMachine.setSize(1115, 730);
		frmMachine.setLocation(myPrefs.getInt("LocX", 100), myPrefs.getInt("LocY", 100));
		myPrefs = null;
		

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
			}
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

		JMenu mnuDisks = new JMenu("Disks");
		menuBar.add(mnuDisks);

		JMenu mnuTools = new JMenu("Tools");
		menuBar.add(mnuTools);

		JMenu mnuWindows = new JMenu("Windows");
		menuBar.add(mnuWindows);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{310, 0, 20, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
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
		gbl_panelMiddle.columnWidths = new int[]{285, 0};
		gbl_panelMiddle.rowHeights = new int[]{300, 0};
		gbl_panelMiddle.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panelMiddle.rowWeights = new double[]{0.0, Double.MIN_VALUE};
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
		
		diskDisplay = new DiskDisplay();
		diskDisplay.setPreferredSize(new Dimension(275, 300));
		diskDisplay.setBounds(5, 5, 275, 300);
		panelDisks.add(diskDisplay);
		diskDisplay.setLayout(null);
		
		paneLeft = new JPanel();
		paneLeft.setBounds(0, 0, 610, 310);
		panelTop.add(paneLeft);
		GridBagLayout gbl_paneLeft = new GridBagLayout();
		gbl_paneLeft.columnWidths = new int[]{0, 0, 0};
		gbl_paneLeft.rowHeights = new int[]{0, 0};
		gbl_paneLeft.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_paneLeft.rowWeights = new double[]{1.0, Double.MIN_VALUE};
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
		
		stateDisplay  = new StateDisplay();
		stateDisplay.setPreferredSize(new Dimension(600, 300));
		stateDisplay.setMinimumSize(new Dimension(600, 300));
		stateDisplay.setBounds(5, 5, 600, 300);
		panelStateDisplay.add(stateDisplay);
		stateDisplay.setLayout(null);
		
		panelRun = new JPanel();
		panelRun.setBounds(930, 0, 160, 310);
		panelTop.add(panelRun);
		panelRun.setLayout(null);
		
		panel = new JPanel();
		panel.setBounds(5, 5, 160, 300);
		panelRun.add(panel);
		panel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel.setLayout(null);
		
		JSpinner spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		spinner.setBounds(51, 29, 45, 20);
		panel.add(spinner);
		
		btnStep = new JButton("Step");
		btnStep.setBounds(38, 60, 71, 34);
		panel.add(btnStep);
		
		btnRun = new JButton("Run");
		btnRun.setBounds(29, 185, 89, 59);
		panel.add(btnRun);
		
		panelBottom = new JPanel();
		panelBottom.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GridBagConstraints gbc_panelBottom = new GridBagConstraints();
		gbc_panelBottom.insets = new Insets(0, 0, 5, 0);
		gbc_panelBottom.fill = GridBagConstraints.BOTH;
		gbc_panelBottom.gridx = 0;
		gbc_panelBottom.gridy = 1;
		frmMachine.getContentPane().add(panelBottom, gbc_panelBottom);
		GridBagLayout gbl_panelBottom = new GridBagLayout();
		gbl_panelBottom.columnWidths = new int[]{547, 0};
		gbl_panelBottom.rowHeights = new int[]{5, 0};
		gbl_panelBottom.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panelBottom.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panelBottom.setLayout(gbl_panelBottom);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 0;
		panelBottom.add(tabbedPane, gbc_tabbedPane);
		
		tabMemory = new JPanel();
		tabMemory.setPreferredSize(new Dimension(50, 50));
		tabbedPane.addTab("Memory", null, tabMemory, null);
		GridBagLayout gbl_tabMemory = new GridBagLayout();
		gbl_tabMemory.columnWidths = new int[]{0};
		gbl_tabMemory.rowHeights = new int[]{0};
		gbl_tabMemory.columnWeights = new double[]{Double.MIN_VALUE};
		gbl_tabMemory.rowWeights = new double[]{Double.MIN_VALUE};
		tabMemory.setLayout(gbl_tabMemory);
		
		tabDisassembler = new JScrollPane();
		tabDisassembler.setViewportBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		tabDisassembler.setPreferredSize(new Dimension(600, 300));
		tabDisassembler.setMinimumSize(new Dimension(600, 300));
		tabbedPane.addTab("Disassembler", null, tabDisassembler, null);
		
		JLabel lblNewLabel = new JLabel(" Location             OpCode                   Instruction                                               Function\r\n");
		lblNewLabel.setForeground(Color.BLUE);
		tabDisassembler.setColumnHeaderView(lblNewLabel);
		
		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setMaximumSize(new Dimension(600, 2147483647));
		textArea.setMinimumSize(new Dimension(600, 300));
		textArea.setPreferredSize(new Dimension(600, 300));
		tabDisassembler.setViewportView(textArea);

		
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
		gbl_panelStatus.columnWidths = new int[]{0};
		gbl_panelStatus.rowHeights = new int[]{0};
		gbl_panelStatus.columnWeights = new double[]{Double.MIN_VALUE};
		gbl_panelStatus.rowWeights = new double[]{Double.MIN_VALUE};
		panelStatus.setLayout(gbl_panelStatus);
	}// initialize

	public static final String MNU_FILE_NEW = "mnuFileNew";
	public static final String MNU_MEMORY_LOAD_FROM_FILE = "mnuMemoryLoadFromFile";
	
	private JPanel panelMiddle;
	private JPanel panelDisks;
	
	private JPanel paneLeft;
	private JPanel panelStateDisplay;
	private JPanel panel;
	private JButton btnStep;
	private JButton btnRun;
	private JPanel panelRun;
	private JPanel panelTop;
	private JPanel panelBottom;
	private JPanel panelStatus;
	private JTabbedPane tabbedPane;
	private JPanel tabMemory;
	private JScrollPane tabDisassembler;
	
	private JMenuItem mnuFileNew;

}// class Machine8080
