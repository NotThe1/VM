package codeSupport.debug;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import hardware.WorkingRegisterSet;
import utilities.FilePicker;
import utilities.menus.MenuUtility;

public class ShowCode extends JDialog implements Runnable {

	private static ShowCode instance = new ShowCode();

	public static ShowCode getInstance() {
		return instance;
	}// getInstance

	private WorkingRegisterSet wrs = WorkingRegisterSet.getInstance();

	private ShowFileAdapter showFileAdapter = new ShowFileAdapter();
	// private StyledDocument doc;
	private Path newListPath;
	private HashMap<String, Limits> fileList = new HashMap<String, Limits>();
	private HashMap<String, String> listings = new HashMap<String, String>();

	private int programCounter;
	private int currentStart, currentEnd;
	private String currentFilePath = null;
	private boolean fileIsCurrent;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ShowCode dialog = new ShowCode();
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				} // try
			}// run
		});
	}// main

	private void doAddFile() {
		JFileChooser fc = (newListPath) == null ? FilePicker.getAsmPicker() : FilePicker.getAsmPicker(newListPath);
		if (fc.showOpenDialog(null) == JFileChooser.CANCEL_OPTION) {
			System.out.println("Bailed out of the open");
			return;
		} // if - open
		newListPath = Paths.get(fc.getSelectedFile().getParent());
		addFile(fc.getSelectedFile().getAbsolutePath());
	}// doAddFile()

	private void addFile(String listFileFullPath) {
		int endAddress = -1, startAddress = -1;
		Pattern hotLineRegex = Pattern
				.compile("(?<lineNumber>\\d{4}: )(?<address>[A-Fa-f\\d]{4})(?<junk> [A-Fa-f\\d]{2})");
		Matcher matcherForHotLine;
		StringBuilder stringBuilder = new StringBuilder();

		try {
			FileReader fileReader;
			fileReader = new FileReader(listFileFullPath);
			BufferedReader reader = new BufferedReader(fileReader);
			String line;
			int addressOnThisLine;
			while ((line = reader.readLine()) != null) {
				matcherForHotLine = hotLineRegex.matcher(line);
				if (matcherForHotLine.find()) {
					// String addressStr = matcherForHotLine.group("address");
					addressOnThisLine = Integer.valueOf(matcherForHotLine.group("address"), 16);
					endAddress = addressOnThisLine;
					startAddress = startAddress == -1 ? addressOnThisLine : startAddress;
				} // if - its a hot line
				stringBuilder.append(line + System.lineSeparator());
			} // while
			reader.close();
			taDisplay.setText(stringBuilder.toString());
		} catch (FileNotFoundException fnfe) {
			JOptionPane.showMessageDialog(null, listFileFullPath + "not found", "unable to locate",
					JOptionPane.ERROR_MESSAGE);
			return; // exit gracefully
		} catch (IOException ie) {
			JOptionPane.showMessageDialog(null, listFileFullPath + ie.getMessage(), "IO error",
					JOptionPane.ERROR_MESSAGE);
			return; // exit gracefully
		} // try
		fileList.put(listFileFullPath, new Limits(startAddress, endAddress));
		listings.put(listFileFullPath, stringBuilder.toString());
		loadDisplay(listFileFullPath);
		taDisplay.setCaretPosition(0);
		MenuUtility.addItemToList(mnuFiles, new File(listFileFullPath), new JCheckBoxMenuItem());

	}// addFile

	private void loadDisplay(String filePath) {
		lblHeader.setText(new File(filePath).getName());
		lblHeader.setToolTipText(filePath);

		taDisplay.setText(listings.get(filePath));
		Limits limits = fileList.get(filePath);
		currentStart = limits.start;
		currentEnd = limits.end;
		currentFilePath = filePath;
	}// loadDisplay

	private void doAddFilesFromList() {
		JFileChooser fc = FilePicker.getListAsmPicker();
		if (fc.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
			System.out.printf("You cancelled the Load Asm from File List...%n", "");
		} else {
			FileReader fileReader;
			String filePathName = null;
			try {
				fileReader = new FileReader((fc.getSelectedFile().getAbsolutePath()));
				BufferedReader reader = new BufferedReader(fileReader);
				while ((filePathName = reader.readLine()) != null) {
					addFile(filePathName);
				} // while
				reader.close();
			} catch (IOException e1) {
				System.out.printf(e1.getMessage() + "%n", "");
			} // try
		} // if

		return;
	}// doAddFilesFromList

	private void doSaveSelectedToList() {
		JFileChooser fc = FilePicker.getListAsmPicker();
		if (fc.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
			System.out.printf("You cancelled Save Selected as List...%n", "");
			return;
		} // if
		String listFile = fc.getSelectedFile().getAbsolutePath();
		String completeSuffix = DOT + FilePicker.LIST_ASM_SUFFIX;
		listFile = listFile.replace("//" + completeSuffix + "$", EMPTY_STRING);
		try {
			FileWriter fileWriter = new FileWriter(listFile + completeSuffix);
			BufferedWriter writer = new BufferedWriter(fileWriter);

			ArrayList<String> selectedFiles = MenuUtility.getFilePathsSelected(mnuFiles);
			for (String selectedFile : selectedFiles) {
				writer.write(selectedFile + System.lineSeparator());
			} // for
			writer.close();

		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		} // try
	}// doSaveSelectedTOList

	private void doClearSelectedFiles() {
		ArrayList<String> filesToBeCleared = MenuUtility.getFilePathsSelected(mnuFiles);
		for (String fileToBeCleared : filesToBeCleared) {
			clearFile(fileToBeCleared);
		} // for each
		MenuUtility.clearListSelected(mnuFiles);
		adjustTheDisplay();
	}// doClearSelectedFiles

	private void doClearAllFiles() {
		ArrayList<String> filesToBeCleared = MenuUtility.getFilePaths(mnuFiles);
		for (String fileToBeCleared : filesToBeCleared) {
			clearFile(fileToBeCleared);
		} // for each
		MenuUtility.clearList(mnuFiles);
		adjustTheDisplay();
	}// doClearFiles

	private void clearFile(String filePath) {
		if (filePath.equals(currentFilePath)) {
			clearCurrentIndicaters();
		} // if current path
		fileList.remove(filePath);
		listings.remove(filePath);
	}// clearFile

	private void adjustTheDisplay() {
		if (fileList.isEmpty()) {
			lblHeader.setText(NO_ACTIVE_FILE);
			lblStatus.setText(NO_ACTIVE_FILE);
			taDisplay.setText(EMPTY_STRING);
		} else if (!fileList.containsKey(currentFilePath)) {

			Set<String> keys = fileList.keySet();
			String filePath = null;
			for (String key : keys) {
				filePath = key;
				break;
			} // for get a valid filePath
			loadDisplay(filePath);
		} // if
		else {
			// leave it alone.
		} //

	}// adjustTheDisplay

	// -------------------------------------------------------------------------------
	private void clearCurrentIndicaters() {
		currentFilePath = null;
		currentStart = -1;
		currentEnd = -1;
		fileIsCurrent = false;
	}// clearCurrentIndicaters

	public void setProgramCounter(int programCounter) {
		this.programCounter = programCounter & 0XFFFF;

		setFileToShow(programCounter);

		if (currentFilePath == null) {
			// notified user already.
			return; // not much do do
		} // if

		if (!fileIsCurrent) { // file is not current file
			loadDisplay(currentFilePath);
		} // if

		selectTheCorrectLine();
	}// setProgramCounter

	private void setFileToShow(int lineNumber) {
		// returns true if number is in the currently loaded file
		fileIsCurrent = false;
		if (isLineInCurrentFile(lineNumber)) {
			fileIsCurrent = true;
			return; // everything is in place
		} // if its in current file

		// Limits thisFilesLimit = new Limits();
		boolean weHaveAFile = false;

		Set<String> filePaths = fileList.keySet();
		for (String filePath : filePaths) {
			if (isLineInThisFile(lineNumber, filePath)) {
				weHaveAFile = true;
				currentFilePath = filePath;
				Limits thisLimits = fileList.get(filePath);
				currentStart = thisLimits.start;
				currentEnd = thisLimits.end;
				break;
			} // if
		} // for
		lblStatus.setText("-");
		if (!weHaveAFile) {
			clearCurrentIndicaters();
			String status = String.format("Target line: %04X Not In Any Currently Loaded Files%n", lineNumber);
			lblStatus.setText(status);
		} // if file not found
		return;
	}// getFileToShow

	private boolean isLineInCurrentFile(int lineNumber) {
		return ((lineNumber >= currentStart) && (lineNumber <= currentEnd));
	}// isLineInCurrentFile

	private boolean isLineInThisFile(int lineNumber, String filePath) {
		boolean isLineInThisFile = false;
		Limits thisFilesLimit = fileList.get(filePath);
		if ((lineNumber >= thisFilesLimit.start) && (lineNumber <= thisFilesLimit.end)) {
			currentFilePath = filePath;
			currentStart = thisFilesLimit.start;
			currentEnd = thisFilesLimit.end;
			isLineInThisFile = true;
		} // if
		return isLineInThisFile;
	}// isLineInThisFile

	private void selectTheCorrectLine() {
		String line;
		String targetAddressRegex = String.format("\\d{4}: %04X [A-Fa-f\\d]{2}.*\r", programCounter);
		Pattern targetAddressPattern = Pattern.compile(targetAddressRegex);
		Matcher targetAddressMatcher;
		targetAddressMatcher = targetAddressPattern.matcher(taDisplay.getText());
		if (targetAddressMatcher.find()) {
			// System.out.printf("[selectTheCorrectLine] |%s|%n", targetAddressMatcher.group(0));
			taDisplay.setSelectionStart(targetAddressMatcher.start());
			taDisplay.setSelectionEnd(targetAddressMatcher.end());
			lblStatus.setText("-");
		} else {
			String status = String.format("Target line: %04X Not Start of Instruction%n", programCounter);
			lblStatus.setText(status);
		} //

	}// selectTheCorrectLine
		// ''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''

	@Override
	public void run() {
		setProgramCounter(wrs.getProgramCounter());
	}// run

	// ===============================================================================
	public void close() {
		appClose();
	}// close

	private void appClose() {
		Preferences myPrefs = Preferences.userNodeForPackage(ShowCode.class);
		Dimension dim = getSize();
		myPrefs.putInt("Height", dim.height);
		myPrefs.putInt("Width", dim.width);
		Point point = getLocation();
		myPrefs.putInt("LocX", point.x);
		myPrefs.putInt("LocY", point.y);
		myPrefs = null;
		dispose();
	}// appClose

	private void appInit() {
		Preferences myPrefs = Preferences.userNodeForPackage(ShowCode.class);
		setSize(myPrefs.getInt("Height", 570), myPrefs.getInt("Width", 400));
		setLocation(myPrefs.getInt("LocX", 100), myPrefs.getInt("LocY", 100));
		myPrefs = null;
		this.setVisible(true);
		// addFile1("C:\\Users\\admin\\git\\assembler8080\\assembler8080\\Code\\currentOS\\ROM.list");
		taDisplay.setSelectedTextColor(Color.BLUE);
		//Highlighter h = new Highlighter();
		clearCurrentIndicaters();
	}// appInit

	// ---------------------------------------------------------------------------------------

	/**
	 * Create the dialog.
	 */
	private ShowCode() {
		initialize();
		appInit();
	}//

	public void initialize() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				appClose();
			}//
		});
		setTitle("Show Code");
		// setBounds(100, 100, 450, 300);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 30, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		getContentPane().add(scrollPane, gbc_scrollPane);

		taDisplay = new JTextArea();
		taDisplay.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				if(mouseEvent.getClickCount() > 1)
				setProgramCounter(programCounter);
			}
		});
		taDisplay.setEditable(false);
		taDisplay.setFont(new Font("Courier New", Font.PLAIN, 14));
		scrollPane.setViewportView(taDisplay);

		lblHeader = new JLabel(NO_ACTIVE_FILE);
		lblHeader.setHorizontalAlignment(SwingConstants.CENTER);
		lblHeader.setForeground(Color.BLUE);
		lblHeader.setFont(new Font("Courier New", Font.BOLD, 16));
		scrollPane.setColumnHeaderView(lblHeader);

		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 1;
		getContentPane().add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0 };
		gbl_panel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		lblStatus = new JLabel(NO_ACTIVE_FILE);
		lblStatus.setForeground(Color.RED);
		lblStatus.setFont(new Font("Arial", Font.BOLD, 17));
		GridBagConstraints gbc_lblStatus = new GridBagConstraints();
		gbc_lblStatus.anchor = GridBagConstraints.SOUTH;
		gbc_lblStatus.gridx = 0;
		gbc_lblStatus.gridy = 0;
		panel.add(lblStatus, gbc_lblStatus);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		mnuFiles = new JMenu("Files");
		menuBar.add(mnuFiles);

		JMenuItem mnuFilesAddFile = new JMenuItem("Add File");
		mnuFilesAddFile.setName(MNU_FILE_ADD_FILE);
		mnuFilesAddFile.addActionListener(showFileAdapter);
		mnuFiles.add(mnuFilesAddFile);

		JMenuItem mnuFilesAddFilesFromList = new JMenuItem("Add Files From List");
		mnuFilesAddFilesFromList.setName(MNU_FILE_ADD_FILES_FROM_LIST);
		mnuFilesAddFilesFromList.addActionListener(showFileAdapter);
		mnuFiles.add(mnuFilesAddFilesFromList);

		JSeparator separator = new JSeparator();
		mnuFiles.add(separator);

		JMenuItem mnuSaveSelectedToList = new JMenuItem("Save Selected To List");
		mnuSaveSelectedToList.setName(MNU_FILE_SAVE_SELECTED_TO_LIST);
		mnuSaveSelectedToList.addActionListener(showFileAdapter);
		mnuFiles.add(mnuSaveSelectedToList);

		JSeparator separator_1 = new JSeparator();
		separator_1.setName("recentFilesStart");
		mnuFiles.add(separator_1);

		JSeparator separator_2 = new JSeparator();
		separator_2.setName("recentFilesEnd");
		mnuFiles.add(separator_2);

		JMenuItem mnuClearSelectedFiles = new JMenuItem("Clear Selected Files");
		mnuClearSelectedFiles.setName(MNU_CLEAR_SELECTED_FILES);
		mnuClearSelectedFiles.addActionListener(showFileAdapter);
		mnuFiles.add(mnuClearSelectedFiles);

		JMenuItem mnuFilesClearAllFiles = new JMenuItem("Clear All Files");
		mnuFilesClearAllFiles.setName(MNU_CLEAR_ALL_FILES);
		mnuFilesClearAllFiles.addActionListener(showFileAdapter);
		mnuFiles.add(mnuFilesClearAllFiles);

	}// initialize

	class ShowFileAdapter implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent actionEvent) {
			String name = ((JMenuItem) actionEvent.getSource()).getName();
			switch (name) {
			case MNU_FILE_ADD_FILE:
				doAddFile();
				break;
			case MNU_FILE_ADD_FILES_FROM_LIST:
				doAddFilesFromList();
				break;
			case MNU_FILE_SAVE_SELECTED_TO_LIST:
				doSaveSelectedToList();
				break;
			case MNU_CLEAR_SELECTED_FILES:
				doClearSelectedFiles();
				break;
			case MNU_CLEAR_ALL_FILES:
				doClearAllFiles();
				break;
			}// switch

		}// actionPerformed

	}// class ShowFileAdapter

	class Limits {
		public int start;
		public int end;

		public Limits() {
			this(-1, -1);
		}// Constructor

		public Limits(int start, int end) {
			this.start = start;
			this.end = end;
		}// Constructor
	}// class Limits

	private static final String MNU_FILE_ADD_FILE = "mnuFileAddFile";
	private static final String MNU_FILE_ADD_FILES_FROM_LIST = "mnuFileAddFilesFromList";
	private static final String MNU_FILE_SAVE_SELECTED_TO_LIST = "mnuFilSaveSelectedToList";
	private static final String MNU_CLEAR_SELECTED_FILES = "mnuClearSelectedFiles";
	private static final String MNU_CLEAR_ALL_FILES = "mnuClearAllFiles";

	private static final String LEFT_P = "(";
	private static final String RIGHT_P = ")";
	private static final String MID_P = RIGHT_P + LEFT_P;

	private static final String NO_ACTIVE_FILE = "<< No Active FIle >>";
	private static final String EMPTY_STRING = "";
	private static final String DOT = ".";

	private JMenu mnuFiles;
	private JTextArea taDisplay;
	private JLabel lblHeader;
	private JLabel lblStatus;

}// class ShowCode
