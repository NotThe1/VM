package utilities;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.BevelBorder;
import javax.swing.text.MaskFormatter;

import utilities.hexDecimalNumberPanel.HexDecimalNumberPanel;
import utilities.seekPanel.SeekPanel;
import utilities.seekPanel.SeekValueChangeEvent;
import utilities.seekPanel.SeekValueChangeListener;

public class SeekPanelTest {

	private SeekPanel seekPanel;
	private HexDecimalNumberPanel hexDecimalNumberDisplay;

	private JFrame frmTemplate;
	private JButton btnOne;
	private JButton btnTwo;
	private JButton btnThree;
	private JButton btnFour;
	private JSplitPane splitPane1;
	private JSpinner spinnerValue;
	private JSpinner spinnerMin;
	private JSpinner spinnerStep;
	private JSpinner spinnerMax;
	private HexDecimalNumberPanel panelHD;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SeekPanelTest window = new SeekPanelTest();
					window.frmTemplate.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				} // try
			}// run
		});
	}// main

	/* Standard Stuff */

	private void doBtnOne() {
		if (seekPanel.isDecimalDisplay()) {
			seekPanel.setHexDisplay();
		} else {
			seekPanel.setDecimalDisplay();
		}
	}// doBtnOne

	private void doBtnTwo() {
		SpinnerNumberModel snm = new SpinnerNumberModel((int) spinnerValue.getValue(), (int) spinnerMin.getValue(),
				(int) spinnerMax.getValue(), (int) spinnerStep.getValue());
		seekPanel.setNumberModel(snm);
	}// doBtnTwo

	private void doBtnThree() {
		if (panelHD.isDecimalDisplay()) {
			panelHD.setHexDisplay();
		} else {
			panelHD.setDecimalDisplay();
		} // if
	}// doBtnThree

	private void doBtnFour() {

	}// doBtnFour

	// ---------------------------------------------------------

	private void doFileNew() {

	}// doFileNew

	private void doFileOpen() {

	}// doFileOpen

	private void doFileSave() {

	}// doFileSave

	private void doFileSaveAs() {

	}// doFileSaveAs

	private void doFilePrint() {

	}// doFilePrint

	private void doFileExit() {
		appClose();
		System.exit(0);
	}// doFileExit

	private void doEditCut() {

	}// doEditCut

	private void doEditCopy() {

	}// doEditCopy

	private void doEditPaste() {

	}// doEditPaste

	private void appClose() {
		Preferences myPrefs = Preferences.userNodeForPackage(SeekPanelTest.class);
		Dimension dim = frmTemplate.getSize();
		myPrefs.putInt("Height", dim.height);
		myPrefs.putInt("Width", dim.width);
		Point point = frmTemplate.getLocation();
		myPrefs.putInt("LocX", point.x);
		myPrefs.putInt("LocY", point.y);
		myPrefs.putInt("Divider", splitPane1.getDividerLocation());
		myPrefs = null;
	}// appClose

	private void appInit() {
		Preferences myPrefs = Preferences.userNodeForPackage(SeekPanelTest.class);
		frmTemplate.setSize(653, 500);
		frmTemplate.setLocation(myPrefs.getInt("LocX", 100), myPrefs.getInt("LocY", 100));
		splitPane1.setDividerLocation(myPrefs.getInt("Divider", 250));
		myPrefs = null;
		MaskFormatter maskFormatter = new MaskFormatter();
		maskFormatter.setValidCharacters("0123456789");
		seekPanel.setDecimalDisplay();
		panelHD.setNumberModel(new SpinnerNumberModel(0, 0, 20, 1));
		// ftfDecimal.get
	}// appInit

	public SeekPanelTest() {
		initialize();
		appInit();
	}// Constructor

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmTemplate = new JFrame();
		frmTemplate.setTitle("SeekPanelTest");
		frmTemplate.setBounds(100, 100, 450, 300);
		frmTemplate.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 25, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
		frmTemplate.getContentPane().setLayout(gridBagLayout);

		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		GridBagConstraints gbc_toolBar = new GridBagConstraints();
		gbc_toolBar.fill = GridBagConstraints.HORIZONTAL;
		gbc_toolBar.insets = new Insets(0, 0, 5, 0);
		gbc_toolBar.gridx = 0;
		gbc_toolBar.gridy = 0;
		frmTemplate.getContentPane().add(toolBar, gbc_toolBar);

		btnOne = new JButton("H/D");
		btnOne.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doBtnOne();
			}
		});
		btnOne.setMaximumSize(new Dimension(70, 20));
		btnOne.setPreferredSize(new Dimension(50, 20));
		toolBar.add(btnOne);

		btnTwo = new JButton("Set Model");
		btnTwo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doBtnTwo();
			}
		});
		btnTwo.setPreferredSize(new Dimension(30, 20));
		btnTwo.setMaximumSize(new Dimension(70, 20));
		toolBar.add(btnTwo);

		btnThree = new JButton("Radix");
		btnThree.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doBtnThree();
			}
		});
		btnThree.setPreferredSize(new Dimension(30, 20));
		btnThree.setMaximumSize(new Dimension(70, 20));
		toolBar.add(btnThree);

		btnFour = new JButton("4");
		btnFour.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doBtnFour();
			}
		});
		btnFour.setPreferredSize(new Dimension(30, 20));
		btnFour.setMaximumSize(new Dimension(70, 20));
		toolBar.add(btnFour);

		splitPane1 = new JSplitPane();
		GridBagConstraints gbc_splitPane1 = new GridBagConstraints();
		gbc_splitPane1.insets = new Insets(0, 0, 5, 0);
		gbc_splitPane1.fill = GridBagConstraints.BOTH;
		gbc_splitPane1.gridx = 0;
		gbc_splitPane1.gridy = 1;
		frmTemplate.getContentPane().add(splitPane1, gbc_splitPane1);

		JPanel panelLeft = new JPanel();
		splitPane1.setLeftComponent(panelLeft);
		GridBagLayout gbl_panelLeft = new GridBagLayout();
		gbl_panelLeft.columnWidths = new int[] { 0, 0, 0 };
		gbl_panelLeft.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gbl_panelLeft.columnWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		gbl_panelLeft.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panelLeft.setLayout(gbl_panelLeft);

		seekPanel = new SeekPanel();
		seekPanel.addSeekValueChangedListener(new SeekValueChangeListener() {
			public void valueChanged(SeekValueChangeEvent seekValueChangeEvent) {
				int priorValue = seekValueChangeEvent.getOldValue();
				int value = seekValueChangeEvent.getNewValue();
				System.out.printf("[valueChanged] OldValue: %d, newValue: %d%n", priorValue, value);
			}
		});
		GridBagConstraints gbc_spinnerTest = new GridBagConstraints();
		gbc_spinnerTest.fill = GridBagConstraints.BOTH;
		gbc_spinnerTest.insets = new Insets(0, 0, 5, 5);
		gbc_spinnerTest.gridx = 0;
		gbc_spinnerTest.gridy = 0;
		panelLeft.add(seekPanel, gbc_spinnerTest);

		JPanel panelMain = new JPanel();
		GridBagConstraints gbc_panelMain = new GridBagConstraints();
		gbc_panelMain.insets = new Insets(0, 0, 5, 5);
		gbc_panelMain.fill = GridBagConstraints.BOTH;
		gbc_panelMain.gridx = 0;
		gbc_panelMain.gridy = 1;
		panelLeft.add(panelMain, gbc_panelMain);
		GridBagLayout gbl_panelMain = new GridBagLayout();
		gbl_panelMain.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panelMain.rowHeights = new int[] { 0, 0, 0 };
		gbl_panelMain.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		gbl_panelMain.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panelMain.setLayout(gbl_panelMain);

		JLabel lblNewLabel = new JLabel("Value");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.NORTH;
		gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 1;
		panelMain.add(lblNewLabel, gbc_lblNewLabel);

		spinnerValue = new JSpinner();
		spinnerValue.setPreferredSize(new Dimension(50, 20));
		GridBagConstraints gbc_spinnerValue = new GridBagConstraints();
		gbc_spinnerValue.insets = new Insets(0, 0, 0, 5);
		gbc_spinnerValue.gridx = 1;
		gbc_spinnerValue.gridy = 1;
		panelMain.add(spinnerValue, gbc_spinnerValue);

		JLabel lblHeight = new JLabel("min");
		GridBagConstraints gbc_lblHeight = new GridBagConstraints();
		gbc_lblHeight.anchor = GridBagConstraints.WEST;
		gbc_lblHeight.insets = new Insets(0, 0, 0, 5);
		gbc_lblHeight.gridx = 3;
		gbc_lblHeight.gridy = 1;
		panelMain.add(lblHeight, gbc_lblHeight);

		spinnerMin = new JSpinner();
		spinnerMin.setPreferredSize(new Dimension(50, 20));
		GridBagConstraints gbc_spinnerMin = new GridBagConstraints();
		gbc_spinnerMin.insets = new Insets(0, 0, 0, 5);
		gbc_spinnerMin.gridx = 4;
		gbc_spinnerMin.gridy = 1;
		panelMain.add(spinnerMin, gbc_spinnerMin);

		JLabel lblMax = new JLabel("Max");
		GridBagConstraints gbc_lblMax = new GridBagConstraints();
		gbc_lblMax.insets = new Insets(0, 0, 0, 5);
		gbc_lblMax.gridx = 6;
		gbc_lblMax.gridy = 1;
		panelMain.add(lblMax, gbc_lblMax);

		spinnerMax = new JSpinner();
		spinnerMax.setPreferredSize(new Dimension(50, 20));
		GridBagConstraints gbc_spinnerMax = new GridBagConstraints();
		gbc_spinnerMax.insets = new Insets(0, 0, 0, 5);
		gbc_spinnerMax.gridx = 7;
		gbc_spinnerMax.gridy = 1;
		panelMain.add(spinnerMax, gbc_spinnerMax);

		JLabel lblStep = new JLabel("step");
		GridBagConstraints gbc_lblStep = new GridBagConstraints();
		gbc_lblStep.insets = new Insets(0, 0, 0, 5);
		gbc_lblStep.gridx = 9;
		gbc_lblStep.gridy = 1;
		panelMain.add(lblStep, gbc_lblStep);

		spinnerStep = new JSpinner();
		spinnerStep.setPreferredSize(new Dimension(50, 20));
		GridBagConstraints gbc_spinnerStep = new GridBagConstraints();
		gbc_spinnerStep.gridx = 10;
		gbc_spinnerStep.gridy = 1;
		panelMain.add(spinnerStep, gbc_spinnerStep);

		panelHD = new HexDecimalNumberPanel();
		panelHD.setMinimumSize(new Dimension(200, 25));
		panelHD.setPreferredSize(new Dimension(200, 23));
		GridBagConstraints gbc_panelHD = new GridBagConstraints();
		gbc_panelHD.insets = new Insets(0, 0, 5, 5);
		gbc_panelHD.fill = GridBagConstraints.BOTH;
		gbc_panelHD.gridx = 0;
		gbc_panelHD.gridy = 2;
		panelLeft.add(panelHD, gbc_panelHD);
		GridBagLayout gbl_panelHD = new GridBagLayout();
		gbl_panelHD.columnWidths = new int[] { 0 };
		gbl_panelHD.rowHeights = new int[] { 0 };
		gbl_panelHD.columnWeights = new double[] { Double.MIN_VALUE };
		gbl_panelHD.rowWeights = new double[] { Double.MIN_VALUE };
		panelHD.setLayout(gbl_panelHD);
		
		JFormattedTextField formattedTextField = new JFormattedTextField();
		GridBagConstraints gbc_formattedTextField = new GridBagConstraints();
		gbc_formattedTextField.insets = new Insets(0, 0, 0, 5);
		gbc_formattedTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_formattedTextField.gridx = 0;
		gbc_formattedTextField.gridy = 3;
		panelLeft.add(formattedTextField, gbc_formattedTextField);

		JPanel panelRight = new JPanel();
		splitPane1.setRightComponent(panelRight);
		GridBagLayout gbl_panelRight = new GridBagLayout();
		gbl_panelRight.columnWidths = new int[] { 0 };
		gbl_panelRight.rowHeights = new int[] { 0 };
		gbl_panelRight.columnWeights = new double[] { Double.MIN_VALUE };
		gbl_panelRight.rowWeights = new double[] { Double.MIN_VALUE };
		panelRight.setLayout(gbl_panelRight);
		splitPane1.setDividerLocation(250);

		JPanel panelStatus = new JPanel();
		panelStatus.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_panelStatus = new GridBagConstraints();
		gbc_panelStatus.fill = GridBagConstraints.BOTH;
		gbc_panelStatus.gridx = 0;
		gbc_panelStatus.gridy = 2;
		frmTemplate.getContentPane().add(panelStatus, gbc_panelStatus);

		JMenuBar menuBar = new JMenuBar();
		frmTemplate.setJMenuBar(menuBar);

		JMenu mnuFile = new JMenu("File");
		menuBar.add(mnuFile);

		JMenuItem mnuFileNew = new JMenuItem("New");
		mnuFileNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doFileNew();
			}
		});
		mnuFile.add(mnuFileNew);

		JMenuItem mnuFileOpen = new JMenuItem("Open...");
		mnuFileOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doFileOpen();
			}
		});
		mnuFile.add(mnuFileOpen);

		JSeparator separator = new JSeparator();
		mnuFile.add(separator);

		JMenuItem mnuFileSave = new JMenuItem("Save...");
		mnuFileSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doFileSave();
			}
		});
		mnuFile.add(mnuFileSave);

		JMenuItem mnuFileSaveAs = new JMenuItem("Save As...");
		mnuFileSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doFileSaveAs();
			}
		});
		mnuFile.add(mnuFileSaveAs);

		JSeparator separator_2 = new JSeparator();
		mnuFile.add(separator_2);

		JMenuItem mnuFilePrint = new JMenuItem("Print...");
		mnuFilePrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doFilePrint();
			}
		});
		mnuFile.add(mnuFilePrint);

		JSeparator separator_1 = new JSeparator();
		mnuFile.add(separator_1);

		JMenuItem mnuFileExit = new JMenuItem("Exit");
		mnuFileExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doFileExit();
			}
		});
		mnuFile.add(mnuFileExit);

		JMenu mnuEdit = new JMenu("Edit");
		menuBar.add(mnuEdit);

		JMenuItem mnuEditCut = new JMenuItem("Cut");
		mnuEditCut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doEditCut();
			}
		});
		mnuEdit.add(mnuEditCut);

		JMenuItem mnuEditCopy = new JMenuItem("Copy");
		mnuEditCopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doEditCopy();
			}
		});
		mnuEdit.add(mnuEditCopy);

		JMenuItem mnuEditPaste = new JMenuItem("Paste");
		mnuEditPaste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doEditPaste();
			}
		});
		mnuEdit.add(mnuEditPaste);

		frmTemplate.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				appClose();
			}
		});
	}// initialize

}// class GUItemplate