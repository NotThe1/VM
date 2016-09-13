package misc;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.Preferences;

import javax.swing.JFrame;

import java.awt.GridBagLayout;

import javax.swing.JMenuBar;
import javax.swing.JToolBar;

import java.awt.GridBagConstraints;

import javax.swing.JPanel;

import java.awt.Insets;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTextPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JSplitPane;
import javax.swing.JScrollPane;

//import utilities.FilePicker;
import utilities.MenuUtility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.Color;

import javax.swing.border.BevelBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class HexEditorPrototype {

	private File activeFile = null;

	private StyledDocument doc;
	
	private String addressFormat;
	private int addressBias;
	private int addressSize;

	SimpleAttributeSet addressAttributes;
	SimpleAttributeSet dataAttributes;
	SimpleAttributeSet asciiAttributes;

	private JFrame frmTemplate;
	private JButton btnOne;
	private JButton btnTwo;
	private JButton btnThree;
	private JButton btnFour;
	private JTextPane textPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HexEditorPrototype window = new HexEditorPrototype();
					window.frmTemplate.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}// try
			}// run
		});
	}// main
	
	private void addFileToMenu(JMenu menu,File file){
		MenuUtility.addFile(mnuFile, file);
//		menu.get
	}//

	/* Standard Stuff */

	private void doButton1() {
		Preferences myPrefs = Preferences.userNodeForPackage(HexEditorPrototype.class);
		String name = myPrefs.get("fileName", "C:\\Users\\admin\\TestLogger0.log");
		myPrefs = null;

		activeFile = new File(name);
		setFileProfile(activeFile);
		loadDocument(activeFile);
	}// doButton1

	private void doButton2() {
		setAddressSize(12);
	}// doButton2

	private void doButton3() {
		Action[] actions = textPane.getActions();
		for (Action action : actions) {
			System.out.printf("%s | %s%n", action.getValue(Action.NAME), action.getValue(Action.LONG_DESCRIPTION));
		}
	}// doButton3

	private void doFileNew() {

	}// doFileNew

	private void doFileOpen() {
		JFileChooser fc = new JFileChooser(System.getProperty("user.home", "."));
		fc.setMultiSelectionEnabled(false);
		if (fc.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
			return;
		}// if quit - get out
		activeFile = fc.getSelectedFile();
		setFileProfile(activeFile);
		loadDocument(activeFile);
		addFileToMenu( mnuFile,activeFile);
		// MemoryLoaderFromFile.loadMemoryImage(fc.getSelectedFile());
	}// doFileOpen

	private void loadDocument(File sourceFile) {
		String fmtData = "%-" + ((BYTES_PER_LINE * 3) + 2) + "s";
		FileInputStream fin = null;

		try {
			fin = new FileInputStream(sourceFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FileChannel sourceChannel = fin.getChannel();
		ByteBuffer sourceBuffer = ByteBuffer.allocate(BYTES_PER_LINE);

		int bytesRead = 16;
		int bufferAddress = addressBias;
		StringBuilder sbData = new StringBuilder();
		String bufferAddressStr = null;
		String dataStr = null;
		String asciiStr = null;
		
		resetDocumentFilter(doc);		
		resetNavigationFilter();		
		clearDocument(doc);
//		int lastData = 0;
		while (bytesRead == 16) {
			sbData.setLength(0);
			try {
				bytesRead = sourceChannel.read(sourceBuffer);
				if (bytesRead==-1){
					break;
				}// no need to add to the doc

				for (int i = 0; i < bytesRead; i++) {
					if ((i % 8) == 0) {
						sbData.append(SPACE);
					}// if data extra space
					sbData.append(String.format("%02X ", sourceBuffer.get(i)));
				}// for

				bufferAddressStr = String.format(addressFormat, bufferAddress);
				dataStr = String.format(fmtData, sbData.toString());
				sourceBuffer.rewind();
				asciiStr = getASCII(sourceBuffer, bytesRead);

				doc.insertString(doc.getLength(), bufferAddressStr, addressAttributes);
				doc.insertString(doc.getLength(), dataStr, dataAttributes);
//				lastData = doc.getLength();
				doc.insertString(doc.getLength(), asciiStr, asciiAttributes);

//				System.out.printf("%s", bufferAddressStr);
//				System.out.printf("%s", dataStr);
//				System.out.printf("%s", asciiStr);

				sourceBuffer.clear();
				bufferAddress += bytesRead;
			} catch (IOException | BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}// try read
		}// while
		setDocumentFilter(doc);
		setNavigationFilter(doc,bytesRead);
		textPane.setCaretPosition(0);
//		System.out.printf("bytesRead: %d  ", bytesRead);

	}// loadDocument
	private void resetNavigationFilter(){
		textPane.setNavigationFilter(null);
	}//resetDocumentFilter
	
	private void setNavigationFilter(StyledDocument doc,int lastDataCount){
		TestNavigationFilter testNavigationFilter = new TestNavigationFilter(doc,lastDataCount);
		textPane.setNavigationFilter(testNavigationFilter);
	}//resetDocumentFilter
	
	
	
	private void resetDocumentFilter(StyledDocument doc){
		((AbstractDocument) doc).setDocumentFilter(null);
	}//resetDocumentFilter
	
	private void setDocumentFilter(StyledDocument doc){
		if(doc ==null){
			((AbstractDocument) doc).setDocumentFilter(null);
			return;
		}//if null
		
		Element rootElement = doc.getDefaultRootElement();
		Element paragraphElement = rootElement.getElement(0);
		Element element = paragraphElement.getElement(0);
		int addressEnd = element.getEndOffset();

		 element = paragraphElement.getElement(1);
		int dataEnd = element.getEndOffset();
		
		 element = paragraphElement.getElement(2);
		int asciiEnd = element.getEndOffset();

//		System.out.printf("[setDocumentFilter]\t0 address end = %d %n", addressEnd);
//		System.out.printf("[setDocumentFilter]\t1 data end = %d %n", dataEnd);
//		System.out.printf("[setDocumentFilter]\t1 ascii end = %d %n", asciiEnd);
		
		TestDocumentFilter testFilter = new TestDocumentFilter(doc,addressEnd,dataEnd,asciiEnd);
		testFilter.setAsciiAttributes(asciiAttributes);
		testFilter.setDataAttributes(dataAttributes);
		((AbstractDocument) doc).setDocumentFilter(testFilter);
	}//setDocumentFilter

	private void clearDocument(StyledDocument doc) {
		try {
			doc.remove(0, doc.getLength());
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}// clearDocument

	private String getASCII(ByteBuffer sourceBuffer, int size) {
		StringBuilder sbASCII = new StringBuilder("  ");
		char c;
		for (int i = 0; i < size; i++) {
			c = (char) sourceBuffer.get(i);
			sbASCII.append((c >= 0X20) && (c <= 0X7F) ? c : NON_PRINTABLE_CHAR);
		}// for
		sbASCII.append(System.lineSeparator());
		return sbASCII.toString();
	}// getASCII

	private void setFileProfile(File selectedFile) {
		String fullFileName = selectedFile.getAbsolutePath().toString();
		String simpleFileName = selectedFile.getName();
		lblFileName.setText(simpleFileName);
		lblFileName.setToolTipText(fullFileName);

		Preferences myPrefs = Preferences.userNodeForPackage(HexEditorPrototype.class);
		myPrefs.put("fileName", fullFileName);
		myPrefs = null;

		long fileSize = selectedFile.length();
		lblFileSize.setText(String.format("%,d Bytes", fileSize));
		adjustAddressSize(fileSize);
	}// setFileProfile

	private void adjustAddressSize(long size) {
		int addressSizeCurrent = this.addressSize;
		if (size <= ADDRESS_4_MAX) {
			setAddressSize(Math.max(ADDRESS_4, addressSizeCurrent));
		} else if (size <= ADDRESS_6_MAX) {
			setAddressSize(Math.max(ADDRESS_6, addressSizeCurrent));
		} else if (size <= ADDRESS_8_MAX) {
			setAddressSize(Math.max(ADDRESS_8, addressSizeCurrent));
		} else {
			this.addressSize = ADDRESS_4;
		}// if fileSize
	}// adjustAddressSize

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

	// --------------------------------------------------------------------------------
	public void setAddressBias(int addressBias) {
		this.addressBias = addressBias < 0 ? 0 : addressBias;
	}// setAddressBias

	public int getAddressBias() {
		return this.addressBias;
	}// getAddressBias

	public void setAddressSize(int addressSize) {

		switch (addressSize) {
		case ADDRESS_4:
		case ADDRESS_6:
		case ADDRESS_8:
			this.addressSize = addressSize;
			break;
		default:
			Object possibleValues[] = { "4 - 65 Kilobytes ", "6 - 16 Megabytes", "8 - 2 Gigabytes" };
			Object selectedValue = JOptionPane.showInputDialog(null, "Choose one", "Address Size",
					JOptionPane.INFORMATION_MESSAGE, null, possibleValues, possibleValues[0]);
			if (selectedValue != null) {
				int value = Integer.valueOf(selectedValue.toString().substring(0, 1));
				this.addressSize = value;
			}// if
		}// switch
		System.out.printf("addressSize = %d %n", this.addressSize);
		addressFormat = "%0" + this.addressSize + "X: ";

	}// setAddressSize

	public int getAddressSize() {
		return this.addressSize;
	}// getAddressSize

	private void makeStyles() {
		SimpleAttributeSet baseAttributes = new SimpleAttributeSet();
		StyleConstants.setFontFamily(baseAttributes, "Courier New");
		StyleConstants.setFontSize(baseAttributes, 16);

		addressAttributes = new SimpleAttributeSet(baseAttributes);
		StyleConstants.setForeground(addressAttributes, Color.GRAY);

		dataAttributes = new SimpleAttributeSet(baseAttributes);
		StyleConstants.setForeground(dataAttributes, Color.BLACK);

		asciiAttributes = new SimpleAttributeSet(baseAttributes);
		StyleConstants.setForeground(asciiAttributes, Color.BLUE);

	}// makeStyles1

	public Action findAction(String key) {
		Action actions[] = textPane.getActions();
		Action result = null;

		for (Action action : actions) {
			if (action.getValue(Action.NAME).equals(key)) {
				result = action;
				break;
			}// if - found
		}// for
		return result;
	}// findAction

	private void appClose() {
		Preferences myPrefs = Preferences.userNodeForPackage(HexEditorPrototype.class);
		Dimension dim = frmTemplate.getSize();
		myPrefs.putInt("Height", dim.height);
		myPrefs.putInt("Width", dim.width);
		Point point = frmTemplate.getLocation();
		myPrefs.putInt("LocX", point.x);
		myPrefs.putInt("LocY", point.y);

		myPrefs.putInt("addressSize", addressSize);
		myPrefs.putInt("addressBias", addressBias);
		MenuUtility.saveRecentFileList(myPrefs, mnuFile);
		myPrefs = null;
	}// appClose

	private void appInit() {
		Preferences myPrefs = Preferences.userNodeForPackage(HexEditorPrototype.class);
		frmTemplate.setSize(myPrefs.getInt("Height",730), myPrefs.getInt("Width",652));
		frmTemplate.setLocation(myPrefs.getInt("LocX", 100), myPrefs.getInt("LocY", 100));

		addressSize = myPrefs.getInt("addressSize", DEFAULT_ADDRESS_SIZE);
		addressBias = myPrefs.getInt("addressBias", 0);
		MenuUtility.loadRecentFileList(myPrefs, mnuFile);
		myPrefs = null;

		makeStyles();

		Action cutAction = findAction(DefaultEditorKit.cutAction);
		mnuEditCut.addActionListener(cutAction);
		Action copyAction = findAction(DefaultEditorKit.copyAction);
		mnuEditCopy.addActionListener(copyAction);
		Action pasteAction = findAction(DefaultEditorKit.pasteAction);
		mnuEditPaste.addActionListener(pasteAction);
		

		doc = textPane.getStyledDocument();
//		defaultFilter = doc.
//		testFilter = new TestFilter();

//		((AbstractDocument) doc).setDocumentFilter(testFilter);

		
//		Action endAction = findAction(DefaultEditorKit.endLineAction);
//		KeyStroke ksEnd = KeyStroke.getKeyStroke("end");
//		InputMap inputMap = textPane.getInputMap();
//		inputMap.put(ksEnd, "end");
//		ActionMap actionMap = textPane.getActionMap();
//		actionMap.put("end", endAction);
	}// appInit

	public HexEditorPrototype() {
		initialize();
		appInit();
	}// Constructor

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmTemplate = new JFrame();
		frmTemplate.setTitle("Hex Editor Prototype");
		frmTemplate.setBounds(100, 100, 450, 300);
		frmTemplate.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 40, 0 };
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

		btnOne = new JButton("1");
		btnOne.setToolTipText("Run last file load");
		btnOne.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doButton1();
			}
		});
		btnOne.setMaximumSize(new Dimension(30, 20));
		btnOne.setPreferredSize(new Dimension(30, 20));
		toolBar.add(btnOne);

		btnTwo = new JButton("2");
		btnTwo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doButton2();
			}
		});
		btnTwo.setToolTipText("Set Address Size");
		btnTwo.setPreferredSize(new Dimension(30, 20));
		btnTwo.setMaximumSize(new Dimension(30, 20));
		toolBar.add(btnTwo);

		btnThree = new JButton("3");
		btnThree.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doButton3();
			}
		});
		btnThree.setToolTipText("list Actions");
		btnThree.setPreferredSize(new Dimension(30, 20));
		btnThree.setMaximumSize(new Dimension(30, 20));
		toolBar.add(btnThree);

		btnFour = new JButton("4");
		btnFour.setPreferredSize(new Dimension(30, 20));
		btnFour.setMaximumSize(new Dimension(30, 20));
		toolBar.add(btnFour);

		JSplitPane splitPaneMajor = new JSplitPane();
		splitPaneMajor.setOneTouchExpandable(true);
		splitPaneMajor.setOrientation(JSplitPane.VERTICAL_SPLIT);
		GridBagConstraints gbc_splitPaneMajor = new GridBagConstraints();
		gbc_splitPaneMajor.insets = new Insets(0, 0, 5, 0);
		gbc_splitPaneMajor.fill = GridBagConstraints.BOTH;
		gbc_splitPaneMajor.gridx = 0;
		gbc_splitPaneMajor.gridy = 1;
		frmTemplate.getContentPane().add(splitPaneMajor, gbc_splitPaneMajor);

		JScrollPane scrollPaneEditor = new JScrollPane();
		splitPaneMajor.setRightComponent(scrollPaneEditor);

		textPane = new JTextPane();
		scrollPaneEditor.setViewportView(textPane);

		lblNewLabel = new JLabel("New label");
		scrollPaneEditor.setColumnHeaderView(lblNewLabel);
		splitPaneMajor.setDividerLocation(75);

		panelStatus = new JPanel();
		panelStatus.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_panelStatus = new GridBagConstraints();
		gbc_panelStatus.fill = GridBagConstraints.HORIZONTAL;
		gbc_panelStatus.gridx = 0;
		gbc_panelStatus.gridy = 2;
		frmTemplate.getContentPane().add(panelStatus, gbc_panelStatus);
		GridBagLayout gbl_panelStatus = new GridBagLayout();
		gbl_panelStatus.columnWidths = new int[] { 0, 46, 0, 60, 0 };
		gbl_panelStatus.rowHeights = new int[] { 14, 0 };
		gbl_panelStatus.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panelStatus.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panelStatus.setLayout(gbl_panelStatus);

		lblFileName = new JLabel("File Name");
		lblFileName.setPreferredSize(new Dimension(120, 30));
		lblFileName.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblFileName = new GridBagConstraints();
		gbc_lblFileName.ipadx = 5;
		gbc_lblFileName.ipady = 5;
		gbc_lblFileName.insets = new Insets(0, 0, 0, 5);
		gbc_lblFileName.anchor = GridBagConstraints.WEST;
		gbc_lblFileName.gridx = 1;
		gbc_lblFileName.gridy = 0;
		panelStatus.add(lblFileName, gbc_lblFileName);

		lblFileSize = new JLabel("0");
		GridBagConstraints gbc_lblFileSize = new GridBagConstraints();
		gbc_lblFileSize.gridx = 3;
		gbc_lblFileSize.gridy = 0;
		panelStatus.add(lblFileSize, gbc_lblFileSize);

		JMenuBar menuBar = new JMenuBar();
		frmTemplate.setJMenuBar(menuBar);

		mnuFile = new JMenu("File");
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
		
		JSeparator separatorFileStart = new JSeparator();
		separatorFileStart.setName(MenuUtility.RECENT_FILES_START);
		mnuFile.add(separatorFileStart);
		
		JSeparator separatorFileEnd = new JSeparator();
		separatorFileEnd.setName(MenuUtility.RECENT_FILES_END);
		mnuFile.add(separatorFileEnd);
		
		JMenuItem mnuRemoveRecentFiles = new JMenuItem("Remove Recent Files");
		mnuRemoveRecentFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MenuUtility.clearList(mnuFile);
			}
		});
		mnuFile.add(mnuRemoveRecentFiles);

		JSeparator separator_1 = new JSeparator();
		mnuFile.add(separator_1);

		mnuFileExit = new JMenuItem("Exit");
		mnuFileExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doFileExit();
			}
		});
		mnuFile.add(mnuFileExit);

		JMenu mnuEdit = new JMenu("Edit");
		mnuEdit.setVisible(false);
		menuBar.add(mnuEdit);

		mnuEditCut = new JMenuItem("Cut");
		// mnuEditCut.
		// mnuEditCut.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent arg0) {
		// doEditCut();
		// }
		// });
		mnuEdit.add(mnuEditCut);

		mnuEditCopy = new JMenuItem("Copy");
		// mnuEditCopy.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent arg0) {
		// doEditCopy();
		// }
		// });
		mnuEdit.add(mnuEditCopy);

		mnuEditPaste = new JMenuItem("Paste");
		// mnuEditPaste.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent arg0) {
		// doEditPaste();
		// }
		// });
		mnuEdit.add(mnuEditPaste);

		frmTemplate.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				appClose();
			}
		});
	}// initialize

	private static final String TITLE = "";
	private static final String SPACE = " ";
	private static final String NON_PRINTABLE_CHAR = ".";
	private static final int BYTES_PER_LINE = 16;
	private static final int DEFAULT_ADDRESS_SIZE = 4;

	private static final int ADDRESS_4 = 4;
	private static final int ADDRESS_6 = 6;
	private static final int ADDRESS_8 = 8;

	private static final int ADDRESS_4_MAX = 0XFFFF; // ‭FFFF 65535‬
	private static final int ADDRESS_6_MAX = 0XFFFFFF; // FF FFFF 16,777,215‬
	private static final int ADDRESS_8_MAX = Integer.MAX_VALUE; // 7FFF FFFF 2,147,483,647‬

	private JPanel panelStatus;
	private JLabel lblFileName;
	private JLabel lblFileSize;
	private JLabel lblNewLabel;
	private JMenuItem mnuEditCut;
	private JMenuItem mnuEditCopy;
	private JMenuItem mnuEditPaste;
	private JMenuItem mnuFileExit;
	private JMenu mnuFile;
//	private JSeparator separatorFileStart;
//	private JSeparator separatorFileEnd;
//	private JMenuItem mnuRemoveRecentFiles;

}// class GUItemplate