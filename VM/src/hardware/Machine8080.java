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

public class Machine8080 {

	private Machine8080MenuAdapter menuAdapter;
	private DiskDisplay diskDisplay;
	private StateDisplay stateDisplay;
//	private Core core = Core.getInstance();
//	private CentralProcessingUnit cpu = CentralProcessingUnit.

	private JFrame frmMachine;
	private JMenuItem mnuFileNew;

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
		Preferences myPrefs = Preferences.userNodeForPackage(TableMaker.class);
		Dimension dim = frmMachine.getSize();
		myPrefs.putInt("Height", dim.height);
		myPrefs.putInt("Width", dim.width);
		Point point = frmMachine.getLocation();
		myPrefs.putInt("LocX", point.x);
		myPrefs.putInt("LocY", point.y);
		myPrefs = null;

	}// appClose

	private void appInit() {
		// manage preferences
		Preferences myPrefs = Preferences.userNodeForPackage(TableMaker.class);
		frmMachine.setSize(1115, 730);
		frmMachine.setLocation(myPrefs.getInt("LocX", 100), myPrefs.getInt("LocY", 100));
		myPrefs = null;
		
		
		
		
		menuAdapter = new Machine8080MenuAdapter();

	}// appInit

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

		JMenu mnuDisks = new JMenu("Disks");
		menuBar.add(mnuDisks);

		JMenu mnuTools = new JMenu("Tools");
		menuBar.add(mnuTools);

		JMenu mnuWindows = new JMenu("Windows");
		menuBar.add(mnuWindows);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{310, 0};
		gridBagLayout.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		frmMachine.getContentPane().setLayout(gridBagLayout);
		
		panelTop = new JPanel();
		GridBagConstraints gbc_panelTop = new GridBagConstraints();
		gbc_panelTop.fill = GridBagConstraints.BOTH;
		gbc_panelTop.gridx = 0;
		gbc_panelTop.gridy = 0;
		frmMachine.getContentPane().add(panelTop, gbc_panelTop);
		GridBagLayout gbl_panelTop = new GridBagLayout();
		gbl_panelTop.columnWidths = new int[]{0, 0, 160, 0};
		gbl_panelTop.rowHeights = new int[]{310, 0};
		gbl_panelTop.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panelTop.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panelTop.setLayout(gbl_panelTop);
		
		panelLeft = new JPanel();
		GridBagConstraints gbc_panelLeft = new GridBagConstraints();
		gbc_panelLeft.fill = GridBagConstraints.BOTH;
		gbc_panelLeft.insets = new Insets(0, 0, 0, 5);
		gbc_panelLeft.gridx = 0;
		gbc_panelLeft.gridy = 0;
		panelTop.add(panelLeft, gbc_panelLeft);
		GridBagLayout gbl_panelLeft = new GridBagLayout();
		gbl_panelLeft.columnWidths = new int[]{285, 0};
		gbl_panelLeft.rowHeights = new int[]{300, 0};
		gbl_panelLeft.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panelLeft.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panelLeft.setLayout(gbl_panelLeft);
		
		panelDisks = new JPanel();
		panelDisks.setPreferredSize(new Dimension(285, 303));
		panelDisks.setMinimumSize(new Dimension(285, 300));
		panelDisks.setBorder(null);
		panelDisks.setLayout(null);
		GridBagConstraints gbc_panelDisks = new GridBagConstraints();
		gbc_panelDisks.gridx = 0;
		gbc_panelDisks.gridy = 0;
		panelLeft.add(panelDisks, gbc_panelDisks);
		
		diskDisplay = new DiskDisplay();
		diskDisplay.setPreferredSize(new Dimension(275, 300));
		diskDisplay.setBounds(5, 5, 275, 300);
		panelDisks.add(diskDisplay);
		diskDisplay.setLayout(null);
		
		panelMiddle = new JPanel();
		GridBagConstraints gbc_panelMiddle = new GridBagConstraints();
		gbc_panelMiddle.fill = GridBagConstraints.BOTH;
		gbc_panelMiddle.insets = new Insets(0, 0, 0, 5);
		gbc_panelMiddle.gridx = 1;
		gbc_panelMiddle.gridy = 0;
		panelTop.add(panelMiddle, gbc_panelMiddle);
		GridBagLayout gbl_panelMiddle = new GridBagLayout();
		gbl_panelMiddle.columnWidths = new int[]{0, 0, 0};
		gbl_panelMiddle.rowHeights = new int[]{0, 0};
		gbl_panelMiddle.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panelMiddle.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panelMiddle.setLayout(gbl_panelMiddle);
		
		panelStateDisplay = new JPanel();
		panelStateDisplay.setMinimumSize(new Dimension(600, 300));
		panelStateDisplay.setPreferredSize(new Dimension(600, 300));
		panelStateDisplay.setLayout(null);
		GridBagConstraints gbc_panelStateDisplay = new GridBagConstraints();
		gbc_panelStateDisplay.insets = new Insets(0, 0, 0, 5);
		gbc_panelStateDisplay.fill = GridBagConstraints.BOTH;
		gbc_panelStateDisplay.gridx = 0;
		gbc_panelStateDisplay.gridy = 0;
		panelMiddle.add(panelStateDisplay, gbc_panelStateDisplay);
		
		stateDisplay  = new StateDisplay();
		stateDisplay.setPreferredSize(new Dimension(600, 300));
		stateDisplay.setMinimumSize(new Dimension(600, 300));
		stateDisplay.setBounds(5, 5, 600, 300);
		panelStateDisplay.add(stateDisplay);
		stateDisplay.setLayout(null);
		
		panelRun = new JPanel();
		GridBagConstraints gbc_panelRun = new GridBagConstraints();
		gbc_panelRun.fill = GridBagConstraints.BOTH;
		gbc_panelRun.gridx = 2;
		gbc_panelRun.gridy = 0;
		panelTop.add(panelRun, gbc_panelRun);
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
	}// initialize

	private static final String MNU_FILE_NEW = "mnuFileNew";
	private JPanel panelLeft;
	private JPanel panelDisks;
	
	private JPanel panelMiddle;
	private JPanel panelStateDisplay;
	private JPanel panel;
	private JButton btnStep;
	private JButton btnRun;
	private JPanel panelRun;
	private JPanel panelTop;
}// class Machine8080
