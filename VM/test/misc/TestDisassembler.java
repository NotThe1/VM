package misc;

import hardware.WorkingRegisterSet;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;
import java.util.prefs.Preferences;

import javax.swing.JFrame;

import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import java.awt.GridBagConstraints;

import javax.swing.JLabel;

import java.awt.Insets;

import codeSupport.HexSpinner;

import javax.swing.JButton;
import javax.swing.SwingConstants;

import memory.MemoryLoaderFromFile;
import utilities.FilePicker;
import utilities.InLineDisassembler;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JPanel;

import java.awt.Color;
import javax.swing.border.LineBorder;
import javax.swing.JTabbedPane;
import java.awt.GridLayout;

public class TestDisassembler {

	private JFrame frmTestInlineDisassembler;
	private JButton btnStart;
	private HexSpinner hsPC;
	private JLabel lblFileName;
	private JLabel lblTarget;
	private HexSpinner hsRun;
	private JButton btnRun;

	private static WorkingRegisterSet wrs = WorkingRegisterSet.getInstance();
//	private InLineDisassembler inLineDisassembler;
	private JTabbedPane tabbedPane;
	private JPanel tab0;
	private JPanel tab1;
	private JPanel fakePanel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TestDisassembler window = new TestDisassembler();
					window.frmTestInlineDisassembler.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}// main

	private void doLoadFile() {
		JFileChooser fc = FilePicker.getDataPicker("Memory Image File", "mem", "hex");
		if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			String fileName = fc.getSelectedFile().getAbsolutePath().toString();
			Preferences myPrefs = Preferences.userNodeForPackage(TestDisassembler.class);
			myPrefs.put("fileName", fileName);
			myPrefs = null;
			lblFileName.setText(fileName);
			MemoryLoaderFromFile.loadMemoryImage(fc.getSelectedFile());
		}// if
	}// loadFile

	private void doStart() {
		InLineDisassembler panelX = InLineDisassembler.getInstance();
		Preferences myPrefs = Preferences.userNodeForPackage(TestDisassembler.class);
		myPrefs.putInt("startLocation", (int) hsPC.getValue());
		myPrefs = null;
		wrs.setProgramCounter((int) hsPC.getValue());
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				panelX.updateDisplay();
			}
		});

	}// doStart

	private void doRun() {
//		wrs.setProgramCounter((int) hsRun.getValue());
		
		InLineDisassembler panelX = InLineDisassembler.getInstance();
		EventQueue.invokeLater(panelX);
		// EventQueue.invokeLater(new Runnable() {
		// public void run() {
		//
		// }
		// });

	}// doTarget

	// ----------------------------------------------------------------
	private void appClose() {
		Preferences myPrefs = Preferences.userNodeForPackage(TestDisassembler.class);
		Dimension dim = frmTestInlineDisassembler.getSize();
		myPrefs.putInt("Height", dim.height);
		myPrefs.putInt("Width", dim.width);
		Point point = frmTestInlineDisassembler.getLocation();
		myPrefs.putInt("LocX", point.x);
		myPrefs.putInt("LocY", point.y);
		myPrefs = null;
	}// appClose

	private void appInit() {
		Preferences myPrefs = Preferences.userNodeForPackage(TestDisassembler.class);
		frmTestInlineDisassembler.setSize(871, 647);
		frmTestInlineDisassembler.setLocation(myPrefs.getInt("LocX", 100), myPrefs.getInt("LocY", 100));
		String fileName = myPrefs.get("fileName", "noFile");
		if (!fileName.equals("noFile")) {
			lblFileName.setText(fileName);
			hsPC.setValue((int) myPrefs.getInt("startLocation", 0));
			File sourceFile = new File(fileName);
			MemoryLoaderFromFile.loadMemoryImage(sourceFile);
		}// if - use last file and program counter
		myPrefs = null;
		//fakePanel = InLineDisassembler.getInstance();
	}// appInit

	/**
	 * Create the application.
	 */
	public TestDisassembler() {
		initialize();
		appInit();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		// InLineDisassembler panelILD = panelInLineDisassembler.getInstance();
		frmTestInlineDisassembler = new JFrame();
		frmTestInlineDisassembler.setTitle("Test In-line Disassembler");
		frmTestInlineDisassembler.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				appClose();
			}
		});
		frmTestInlineDisassembler.setBounds(100, 100, 450, 300);
		frmTestInlineDisassembler.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 600, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		frmTestInlineDisassembler.getContentPane().setLayout(gridBagLayout);

		lblFileName = new JLabel("<no file>");
		GridBagConstraints gbc_lblFileName = new GridBagConstraints();
		gbc_lblFileName.insets = new Insets(0, 0, 5, 0);
		gbc_lblFileName.gridx = 2;
		gbc_lblFileName.gridy = 0;
		frmTestInlineDisassembler.getContentPane().add(lblFileName, gbc_lblFileName);

		JLabel lblProgramCounter = new JLabel("Program Counter");
		GridBagConstraints gbc_lblProgramCounter = new GridBagConstraints();
		gbc_lblProgramCounter.insets = new Insets(0, 0, 5, 5);
		gbc_lblProgramCounter.gridx = 0;
		gbc_lblProgramCounter.gridy = 2;
		frmTestInlineDisassembler.getContentPane().add(lblProgramCounter, gbc_lblProgramCounter);

		hsPC = new HexSpinner();
		hsPC.setMinimumSize(new Dimension(80, 20));
		hsPC.setPreferredSize(new Dimension(80, 20));
		GridBagConstraints gbc_hsPC = new GridBagConstraints();
		gbc_hsPC.insets = new Insets(0, 0, 5, 5);
		gbc_hsPC.gridx = 1;
		gbc_hsPC.gridy = 2;
		frmTestInlineDisassembler.getContentPane().add(hsPC, gbc_hsPC);

		btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doStart();
			}
		});

		lblTarget = new JLabel("Target");
		GridBagConstraints gbc_lblTarget = new GridBagConstraints();
		gbc_lblTarget.insets = new Insets(0, 0, 5, 5);
		gbc_lblTarget.gridx = 0;
		gbc_lblTarget.gridy = 3;
		frmTestInlineDisassembler.getContentPane().add(lblTarget, gbc_lblTarget);

		hsRun = new HexSpinner();
		hsRun.setPreferredSize(new Dimension(80, 20));
		hsRun.setMinimumSize(new Dimension(80, 20));
		GridBagConstraints gbc_hsRun = new GridBagConstraints();
		gbc_hsRun.insets = new Insets(0, 0, 5, 5);
		gbc_hsRun.gridx = 1;
		gbc_hsRun.gridy = 3;
		frmTestInlineDisassembler.getContentPane().add(hsRun, gbc_hsRun);
		btnStart.setVerticalAlignment(SwingConstants.TOP);
		GridBagConstraints gbc_btnStart = new GridBagConstraints();
		gbc_btnStart.anchor = GridBagConstraints.NORTH;
		gbc_btnStart.insets = new Insets(0, 0, 5, 5);
		gbc_btnStart.gridx = 0;
		gbc_btnStart.gridy = 4;
		frmTestInlineDisassembler.getContentPane().add(btnStart, gbc_btnStart);

		btnRun = new JButton("Run");
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doRun();
			}
		});
		btnRun.setVerticalAlignment(SwingConstants.TOP);
		GridBagConstraints gbc_btnRun = new GridBagConstraints();
		gbc_btnRun.anchor = GridBagConstraints.NORTH;
		gbc_btnRun.insets = new Insets(0, 0, 5, 5);
		gbc_btnRun.gridx = 1;
		gbc_btnRun.gridy = 4;
		frmTestInlineDisassembler.getContentPane().add(btnRun, gbc_btnRun);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 2;
		gbc_tabbedPane.gridy = 5;
		frmTestInlineDisassembler.getContentPane().add(tabbedPane, gbc_tabbedPane);

		tab0 = new JPanel();
		tab0.setBorder(new LineBorder(new Color(0, 0, 0)));
		tabbedPane.addTab("Disassembler", null, tab0, null);
		GridBagLayout gbl_tab0 = new GridBagLayout();
		gbl_tab0.columnWidths = new int[]{ 0};
		gbl_tab0.rowHeights = new int[]{ 0};
		gbl_tab0.columnWeights = new double[]{ Double.MIN_VALUE};
		gbl_tab0.rowWeights = new double[]{ Double.MIN_VALUE};
		tab0.setLayout(gbl_tab0);
		
		fakePanel = (JPanel)InLineDisassembler.getInstance();
		fakePanel.setBorder(new LineBorder(Color.RED, 1, true));
		GridBagConstraints gbc_fakePanel = new GridBagConstraints();
		gbc_fakePanel.fill = GridBagConstraints.BOTH;
		gbc_fakePanel.gridx = 0;
		gbc_fakePanel.gridy = 0;
		tab0.add(fakePanel, gbc_fakePanel);
//		GridBagLayout gbl_fakePanel = new GridBagLayout();
//		gbl_fakePanel.columnWidths = new int[]{0};
//		gbl_fakePanel.rowHeights = new int[]{0};
//		gbl_fakePanel.columnWeights = new double[]{Double.MIN_VALUE};
//		gbl_fakePanel.rowWeights = new double[]{Double.MIN_VALUE};
		fakePanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		tab1 = new JPanel();
		tabbedPane.addTab("tab1", null, tab1, null);
		GridBagLayout gbl_tab1 = new GridBagLayout();
		gbl_tab1.columnWidths = new int[]{0};
		gbl_tab1.rowHeights = new int[]{0};
		gbl_tab1.columnWeights = new double[]{Double.MIN_VALUE};
		gbl_tab1.rowWeights = new double[]{Double.MIN_VALUE};
		tab1.setLayout(gbl_tab1);

//		InLineDisassembler panelx = InLineDisassembler.getInstance();
//		panelx.setBorder(new LineBorder(Color.BLUE));
//		panelx.setLayout(new GridLayout(1, 0, 0, 0));

		JMenuBar menuBar = new JMenuBar();
		frmTestInlineDisassembler.setJMenuBar(menuBar);

		JMenu mnuTools = new JMenu("Tools");
		menuBar.add(mnuTools);

		JMenuItem mnuToolsLoadFromFile = new JMenuItem("Load from file ...");
		mnuToolsLoadFromFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doLoadFile();
			}// actionPerformed
		});
		mnuTools.add(mnuToolsLoadFromFile);
	}// initialize

}// class TestDisassembler
