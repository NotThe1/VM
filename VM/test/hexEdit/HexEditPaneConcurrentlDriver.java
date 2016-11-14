package hexEdit;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
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

public class HexEditPaneConcurrentlDriver {

	int sourceSize = (3 * 16) - 6;

	private JFrame frmTemplate;
	private JButton btnOne;
	private JButton btnTwo;
	private JButton btnThree;
	private JButton btnFour;
	private JSplitPane splitPane1;
	private byte[] source;
	private HexEditPanelConcurrent hexEditPanel;
	private HexEditPanelConcurrent hexEditPanel_1;
	private JSpinner spinnerRows;
	private JLabel lblPlus;
	private JSpinner spinnerPlus;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HexEditPaneConcurrentlDriver window = new HexEditPaneConcurrentlDriver();
					window.frmTemplate.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				} // try
			}// run
		});
	}// main

	private void logMessage(String label, String message) {
		String msg = String.format(label + " = %s%n", message);
		System.out.println(msg);
		// textLog.append(msg);
	}// logMessage

	private void logMessage(String label, int value) {
		String msg = String.format(label + " = %d%n", value);
		System.out.println(msg);
		// textLog.append(msg);
	}// logMessage

	/* Standard Stuff */

	private void doBtnOne() {
		int numberOfBytes = (HexEditMetrics.BYTES_PER_LINE * (int) spinnerRows.getValue())
				+ (int) spinnerPlus.getValue();
		source = new byte[numberOfBytes];

		for (int i = 0; i < numberOfBytes; i++) {
			source[i] = (byte) (i % 256);
		} // for
		hexEditPanel.loadData(source);
	}// doBtnOne

	private void doBtnTwo() {
//		byte[] unloadedData = hexEditPanel.unloadData();	
		hexEditPanel_1.loadData(source);
		
	}// doBtnTwo

	private void doBtnThree() {

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
		Preferences myPrefs = Preferences.userNodeForPackage(HexEditPaneConcurrentlDriver.class);
		Dimension dim = frmTemplate.getSize();
		myPrefs.putInt("Height", dim.height);
		myPrefs.putInt("Width", dim.width);
		Point point = frmTemplate.getLocation();
		myPrefs.putInt("LocX", point.x);
		myPrefs.putInt("LocY", point.y);
		myPrefs.putInt("Divider", splitPane1.getDividerLocation());
		myPrefs.putInt("Rows", (int) spinnerRows.getValue());
		myPrefs.putInt("Plus", (int) spinnerPlus.getValue());
		myPrefs = null;
	}// appClose

	private void appInit() {
		Preferences myPrefs = Preferences.userNodeForPackage(HexEditPaneConcurrentlDriver.class);
		frmTemplate.setSize(myPrefs.getInt("Width",1096),myPrefs.getInt("Height", 516));
		frmTemplate.setLocation(myPrefs.getInt("LocX", 100), myPrefs.getInt("LocY", 100));
		splitPane1.setDividerLocation(myPrefs.getInt("Divider", 250));
		spinnerRows.setValue((int) myPrefs.getInt("Rows", 10));
		spinnerPlus.setValue((int) myPrefs.getInt("Plus", 10));
		myPrefs = null;

		// hexEditPanel.loadDocument(core);

	}// appInit

	public HexEditPaneConcurrentlDriver() {
		initialize();
		appInit();
	}// Constructor

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmTemplate = new JFrame();
		frmTemplate.setTitle("HexEditPaneConcurrentlDriver");
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

		btnOne = new JButton("Fill");
		btnOne.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doBtnOne();
			}
		});
		btnOne.setMaximumSize(new Dimension(70, 20));
		btnOne.setPreferredSize(new Dimension(50, 20));
		toolBar.add(btnOne);

		btnTwo = new JButton("Unload");
		btnTwo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doBtnTwo();
			}
		});
		btnTwo.setPreferredSize(new Dimension(30, 20));
		btnTwo.setMaximumSize(new Dimension(70, 20));
		toolBar.add(btnTwo);

		btnThree = new JButton("3");
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
		gbl_panelLeft.columnWidths = new int[] { 0, 0 };
		gbl_panelLeft.rowHeights = new int[] { 0, 0 };
		gbl_panelLeft.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panelLeft.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		panelLeft.setLayout(gbl_panelLeft);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		GridBagConstraints gbc_splitPane = new GridBagConstraints();
		gbc_splitPane.fill = GridBagConstraints.BOTH;
		gbc_splitPane.gridx = 0;
		gbc_splitPane.gridy = 0;
		panelLeft.add(splitPane, gbc_splitPane);

		JPanel panel_top = new JPanel();
		splitPane.setLeftComponent(panel_top);
		GridBagLayout gbl_panel_top = new GridBagLayout();
		gbl_panel_top.columnWidths = new int[] { 0, 0, 0, 0, 0, 0 };
		gbl_panel_top.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gbl_panel_top.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel_top.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel_top.setLayout(gbl_panel_top);
		
				JLabel lblRows = new JLabel("Rows");
				GridBagConstraints gbc_lblRows = new GridBagConstraints();
				gbc_lblRows.insets = new Insets(0, 0, 5, 5);
				gbc_lblRows.gridx = 1;
				gbc_lblRows.gridy = 0;
				panel_top.add(lblRows, gbc_lblRows);
		
				spinnerRows = new JSpinner();
				spinnerRows.setMinimumSize(new Dimension(80, 20));
				spinnerRows.setModel(new SpinnerNumberModel(new Integer(1), new Integer(0), null, new Integer(1)));
				spinnerRows.setPreferredSize(new Dimension(80, 20));
				GridBagConstraints gbc_spinnerRows = new GridBagConstraints();
				gbc_spinnerRows.insets = new Insets(0, 0, 5, 5);
				gbc_spinnerRows.gridx = 1;
				gbc_spinnerRows.gridy = 1;
				panel_top.add(spinnerRows, gbc_spinnerRows);
								
										lblPlus = new JLabel("+");
										GridBagConstraints gbc_lblPlus = new GridBagConstraints();
										gbc_lblPlus.insets = new Insets(0, 0, 5, 5);
										gbc_lblPlus.gridx = 1;
										gbc_lblPlus.gridy = 2;
										panel_top.add(lblPlus, gbc_lblPlus);
								
										spinnerPlus = new JSpinner();
										GridBagConstraints gbc_spinnerPlus = new GridBagConstraints();
										gbc_spinnerPlus.insets = new Insets(0, 0, 5, 5);
										gbc_spinnerPlus.gridx = 1;
										gbc_spinnerPlus.gridy = 3;
										panel_top.add(spinnerPlus, gbc_spinnerPlus);
										spinnerPlus.setModel(new SpinnerNumberModel(0, 0, 15, 1));
										spinnerPlus.setPreferredSize(new Dimension(35, 20));

		JPanel panel_Bottom = new JPanel();
		splitPane.setRightComponent(panel_Bottom);
		GridBagLayout gbl_panel_Bottom = new GridBagLayout();
		gbl_panel_Bottom.columnWidths = new int[] { 0, 0 };
		gbl_panel_Bottom.rowHeights = new int[] { 0, 0 };
		gbl_panel_Bottom.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_panel_Bottom.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panel_Bottom.setLayout(gbl_panel_Bottom);
		splitPane.setDividerLocation(150);

		JPanel panelRight = new JPanel();
		splitPane1.setRightComponent(panelRight);
		GridBagLayout gbl_panelRight = new GridBagLayout();
		gbl_panelRight.columnWidths = new int[] { 0, 0, 0, 0 };
		gbl_panelRight.rowHeights = new int[] { 0, 0 };
		gbl_panelRight.columnWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_panelRight.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		panelRight.setLayout(gbl_panelRight);

		hexEditPanel = new HexEditPanelConcurrent();
		GridBagConstraints gbc_hexEditPanel = new GridBagConstraints();
		gbc_hexEditPanel.insets = new Insets(0, 0, 0, 5);
		gbc_hexEditPanel.anchor = GridBagConstraints.WEST;
		gbc_hexEditPanel.fill = GridBagConstraints.VERTICAL;
		gbc_hexEditPanel.gridx = 0;
		gbc_hexEditPanel.gridy = 0;
		panelRight.add(hexEditPanel, gbc_hexEditPanel);
		
		hexEditPanel_1 = new HexEditPanelConcurrent();
		GridBagLayout gbl_hexEditPanel_1 = (GridBagLayout) hexEditPanel_1.getLayout();
		gbl_hexEditPanel_1.rowWeights = new double[]{0.0, 1.0};
		gbl_hexEditPanel_1.rowHeights = new int[]{25, 0};
		gbl_hexEditPanel_1.columnWeights = new double[]{0.0, 0.0};
		gbl_hexEditPanel_1.columnWidths = new int[]{780, 25};
		GridBagConstraints gbc_hexEditPanel_1 = new GridBagConstraints();
		gbc_hexEditPanel_1.fill = GridBagConstraints.BOTH;
		gbc_hexEditPanel_1.gridx = 2;
		gbc_hexEditPanel_1.gridy = 0;
		panelRight.add(hexEditPanel_1, gbc_hexEditPanel_1);
		splitPane1.setDividerLocation(250);

		JPanel panelStatus = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panelStatus.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
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