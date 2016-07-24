package misc;

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
import javax.swing.JSpinner;

import java.awt.GridBagConstraints;

import javax.swing.JLabel;

import java.awt.Insets;

import codeSupport.HexSpinner;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import memory.Core;
import memory.MemoryLoaderFromFile;
import utilities.FilePicker;
import utilities.InLineDisassembler;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JPanel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class TestDisassembler {

	private JFrame frmTestInlineDisassembler;
	private JButton btnStart;
	private JTextPane txtInstructions;
	private HexSpinner hsPC;
	private JLabel lblFileName;
	private JLabel lblTarget;
	private HexSpinner hsTarget;
	private JButton btnTarget;

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
		InLineDisassembler disass = InLineDisassembler.getInstance();

		Preferences myPrefs = Preferences.userNodeForPackage(TestDisassembler.class);
		myPrefs.putInt("startLocation", (int) hsPC.getValue());
		myPrefs = null;

		txtInstructions.setDocument(disass.updateDisplay((int) hsPC.getValue()));
		txtInstructions.setCaretPosition(0);

	}// doStart

	private void doTarget() {
		String lineSeparator = System.getProperty("line.separator", "\n");
		Document doc = txtInstructions.getDocument();
		try {
			String contents = doc.getText(0, doc.getLength());
			int targetValue = (int) hsTarget.getValue();
			String targetString = String.format("%04X:", targetValue);
			int start = contents.indexOf(targetString);
			int end = contents.indexOf(lineSeparator, start);
			int a = start + 1;

		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// try

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
		gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
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

		hsTarget = new HexSpinner();
		hsTarget.setPreferredSize(new Dimension(80, 20));
		hsTarget.setMinimumSize(new Dimension(80, 20));
		GridBagConstraints gbc_hsTarget = new GridBagConstraints();
		gbc_hsTarget.insets = new Insets(0, 0, 5, 5);
		gbc_hsTarget.gridx = 1;
		gbc_hsTarget.gridy = 3;
		frmTestInlineDisassembler.getContentPane().add(hsTarget, gbc_hsTarget);
		btnStart.setVerticalAlignment(SwingConstants.TOP);
		GridBagConstraints gbc_btnStart = new GridBagConstraints();
		gbc_btnStart.anchor = GridBagConstraints.NORTH;
		gbc_btnStart.insets = new Insets(0, 0, 0, 5);
		gbc_btnStart.gridx = 0;
		gbc_btnStart.gridy = 4;
		frmTestInlineDisassembler.getContentPane().add(btnStart, gbc_btnStart);

		btnTarget = new JButton("Target");
		btnTarget.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doTarget();
			}
		});
		btnTarget.setVerticalAlignment(SwingConstants.TOP);
		GridBagConstraints gbc_btnTarget = new GridBagConstraints();
		gbc_btnTarget.anchor = GridBagConstraints.NORTH;
		gbc_btnTarget.insets = new Insets(0, 0, 0, 5);
		gbc_btnTarget.gridx = 1;
		gbc_btnTarget.gridy = 4;
		frmTestInlineDisassembler.getContentPane().add(btnTarget, gbc_btnTarget);

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 2;
		gbc_scrollPane.gridy = 4;
		frmTestInlineDisassembler.getContentPane().add(scrollPane, gbc_scrollPane);

		txtInstructions = new JTextPane();
		scrollPane.setViewportView(txtInstructions);

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
