
package disks.utility;

import java.awt.Color;
import java.awt.Component;
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
import java.awt.print.PrinterException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import disks.CPMDirectory;
import disks.CPMDirectoryEntry;
import disks.CPMFile;
import disks.Disk;
import disks.DiskMetrics;
import disks.RawDiskDrive;
import utilities.FilePicker;
import utilities.hdNumberBox.HDNumberBox;
import utilities.hdNumberBox.HDNumberValueChangeEvent;
import utilities.hdNumberBox.HDNumberValueChangeListener;
import utilities.hdNumberBox.HDSeekPanel;
import utilities.hexEdit.HexEditPanelSimple;

public class DiskUtility extends JDialog {

	private static DiskUtility instance = new DiskUtility();

	public static DiskUtility getInstance() {
		return instance;
	}// getInstance

	private static final long serialVersionUID = 1L;
	// File testDisk;

	// private JFrame frmDiskUtility;
	private HexEditPanelSimple panelFileHex;
	private HexEditPanelSimple panelSectorDisplay;

	private HDNumberBox hdnSector;
	private HDNumberBox hdnTrack;
	private HDNumberBox hdnHead;
	private JToggleButton tbBootable;
	private JToggleButton tbDecimalDisplay;
	private JTabbedPane tabbedPane;

	private File nativeFile;
	private JLabel lblDiskName;
	private String radixFormat;

	private DiskUtilityAdapter diskUtilityAdapter = new DiskUtilityAdapter();

	private ArrayList<HDNumberBox> hdNumberBoxs;
	private HDSeekPanel hdSeekPanel;
	boolean changeSectorInProgress = false;

	private FileCpmModel fileCpmModel;
	private String nativeDirectory;

	private CPMDirectory directory;
	private CPMFile cpmFile;
	private JTable dirTable;
	private DefaultTableModel modelDir;

	private DiskMetrics diskMetrics;
	private int heads;
	private int tracksPerHead;
	private int sectorsPerTrack;
	private int bytesPerSector;
	private int tracksBeforeDirectory;
	private int blockSizeInSectors;
	private int totalTracks;
	private int totalSectors;
	private int maxDirectoryEntry;
	private int maxBlockNumber;

	private byte[] diskSector;

	private RawDiskDrive diskDrive;

	private boolean validSearchFileName = false;

	private int fileMatchCount;
	private static StyledDocument doc;
	// SimpleAttributeSet diskAttributes;
	// SimpleAttributeSet cpmFileAttributes;

	private JTable catTable;
	private DefaultTableModel modelCat;
	private int catalogRow;

	// ---------------------------------------
	// ---------------------------------------

	private void diskSetup(String fileAbsolutePath) {
		diskDrive = new RawDiskDrive(fileAbsolutePath);
		haveDisk(true);

		// need the two display... methods run on different threads
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				displayPhysicalSector(0); // Physical View
			}// run
		});
		displayDirectoryView(); // Directory View
		// need the two display... methods run on different threads

	}// loadFile

	// }// useBackup
	// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	private void diskNew() {
		File newFile = MakeNewDisk.makeNewDisk();
		if (newFile == null) {
			System.out.printf("[DiskUtility.actionPerformed] No new file %s%n", "");
			return;
		} // if
		diskSetup(newFile.getAbsolutePath());
		manageFileMenus(MNU_TOOLS_NEW_DISK);
	}// diskNew

	public void diskLoad() {
		JFileChooser fc = FilePicker.getDiskPicker();
		if (fc.showOpenDialog(this) == JFileChooser.CANCEL_OPTION) {
			System.out.println("Bailed out of the open");
			return;
		} // if
		if (!fc.getSelectedFile().exists()) {
			return; // try again
		} // if
		diskSetup(fc.getSelectedFile().getAbsolutePath());
		manageFileMenus(MNU_DISK_LOAD_DISK);
	}// diskLoad

	private void diskClose() {
		if (diskDrive != null) {
			diskDrive.dismount();
			diskDrive = null;
		} // if diskDrive
		haveDisk(false);
	}// closeFile

	private void diskSave() {
		// what ???
	}// closeDisk

	private void diskSaveAs() {
		String diskType = diskDrive.getDiskType();
		JFileChooser fc = FilePicker.getDiskPicker("Disketts & Floppies", diskType.toUpperCase());
		if (fc.showOpenDialog(this) == JFileChooser.CANCEL_OPTION) {
			System.out.println("Bailed out of the open");
			return;
		} // if fc

		String fileName = fc.getSelectedFile().getName();
		if (!fileName.toUpperCase().endsWith(diskType)) {
			String msg = String.format("Disk name \"%s\" is not disk type \"%s\".%n", fileName, diskType);
			JOptionPane.showMessageDialog(null, msg);
			return;
		} // if diskType

		String sourceFileName = diskDrive.getFileAbsoluteName();
		String targetFileName = fc.getSelectedFile().getAbsolutePath();

		try {
			Files.copy(Paths.get(sourceFileName), Paths.get(targetFileName), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			// Auto-generated catch block
			e.printStackTrace();
		} // try

		if (diskDrive != null) {
			diskDrive.dismount();
			diskDrive = null;
		} // if diskDrive

		diskSetup(fc.getSelectedFile().getAbsolutePath());
		manageFileMenus(MNU_DISK_LOAD_DISK);
	}// saveAsDisk

	private void haveDisk(boolean state) {

		lblDiskName.setForeground(Color.black);

		refreshMetrics(state);
		btnNativeFile.setEnabled(state);
		manageFileMenus(state ? MNU_DISK_LOAD_DISK : MNU_DISK_CLOSE);

		if (!state) {
			manageFileMenus(MNU_DISK_CLOSE);
			panelSectorDisplay.loadData(NO_ACTIVE_FILE.getBytes());
			btnExport.setEnabled(false);
			btnImport.setEnabled(false);

			scrollDirectoryTable.setViewportView(null);
			showDirectoryDetail(new byte[32]);

			panelFileHex.loadData(NO_ACTIVE_FILE.getBytes());
			cbFileNames.setModel(new FileCpmModel());
			lblRecordCount.setText("0");
			lblReadOnly.setVisible(false);
			lblSystemFile.setVisible(false);
			nativeFile = null;
			txtNativeFileInOut.setText("");
			txtNativeFileInOut.setToolTipText("");
			cbCpmFileInOut.removeAllItems();
		} // if - state

	}// haveDisk

	private void refreshMetrics(boolean state) {
		// state = true we have a valid disk
		if (diskMetrics != null) {
			diskMetrics = null;
		} // if
		if (state) {
			diskMetrics = DiskMetrics.getDiskMetric(diskDrive.getDiskType());
		} // if

		setHeadTrackSectorSizes(diskDrive);

		heads = state ? diskMetrics.heads : 0;
		tracksPerHead = state ? diskMetrics.tracksPerHead : 0;
		sectorsPerTrack = state ? diskMetrics.sectorsPerTrack : 0;
		bytesPerSector = state ? diskMetrics.bytesPerSector : 0;
		totalTracks = state ? heads * tracksPerHead : 0;
		totalSectors = state ? diskMetrics.getTotalSectorsOnDisk() : 0;

		tracksBeforeDirectory = state ? diskMetrics.getOFS() : 0;
		blockSizeInSectors = state ? diskMetrics.sectorsPerBlock : 0;
		maxDirectoryEntry = state ? diskMetrics.getDRM() : 0;
		maxBlockNumber = state ? diskMetrics.getDSM() : 0;

		lblDiskName.setText(state ? diskDrive.getFileLocalName() : NO_ACTIVE_FILE);
		lblDiskName.setToolTipText(state ? diskDrive.getFileAbsoluteName() : NO_ACTIVE_FILE);

		setDisplayRadix();
	}// refreshMetrics

	public void setDisplayRadix() {
		radixFormat = tbDecimalDisplay.isSelected() ? "%,d" : "%X";
		lblHeads.setText(String.format(radixFormat, heads));
		lblTracksPerHead.setText(String.format(radixFormat, tracksPerHead));
		lblSectorsPerTrack.setText(String.format(radixFormat, sectorsPerTrack));
		lblTotalTracks.setText(String.format(radixFormat, totalTracks));
		lblTotalSectors.setText(String.format(radixFormat, totalSectors));

		lblTracksBeforeDirectory.setText(String.format(radixFormat, tracksBeforeDirectory));
		lblLogicalBlockSizeInSectors.setText(String.format(radixFormat, blockSizeInSectors));
		lblMaxDirectoryEntry.setText(String.format(radixFormat, maxDirectoryEntry));
		lblMaxBlockNumber.setText(String.format(radixFormat, maxBlockNumber));

		for (HDNumberBox hdNumberBox : hdNumberBoxs) {
			hdNumberBox.setDecimalDisplay(tbDecimalDisplay.isSelected());
		} // for

	}// setDisplayRadix

	public void fileExport() {
		if (cbCpmFileInOut.getItemCount() == 0) {
			return;
		} // if
		String fileName = ((String) cbCpmFileInOut.getSelectedItem()).toUpperCase();
		if (!fileCpmModel.exists(fileName)) {
			cbCpmFileInOut.setSelectedItem("");
			return;
		} // if
		cbCpmFileInOut.setSelectedItem(fileName);

		cpmFile = CPMFile.getCPMFile(diskDrive, directory, fileName);

		ByteArrayInputStream bis = new ByteArrayInputStream(cpmFile.readNet());
		try {
			Files.copy(bis, Paths.get(nativeFile.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // try

		System.out.printf("[DiskUtility.fileExport] CPM file: %15s nativeFile: %s%n", fileName, nativeFile);
	}// fileExport

	public void fileInport() {
		boolean deleteFile = false;

		String fileName = ((String) cbCpmFileInOut.getSelectedItem()).toUpperCase();
		cbCpmFileInOut.setSelectedItem(fileName);

		if (fileCpmModel.exists(fileName)) {
			int ans = JOptionPane.showConfirmDialog(null, "File Exits, Do you want to overwrite?",
					"Copying a Native File to a CPM file", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (ans == JOptionPane.NO_OPTION) {
				return;
			} // if
			deleteFile = true;
		} // if

		// do we have enough space on the CP/M disk to do this?
		if (!enoughSpaceOnCPM(deleteFile, fileName)) {
			return;
		} // if - space
		if (deleteFile) {
			directory.deleteFile(fileName);
		} // if

		// now we have all the pieces needed to actually move the file
		// now we need to get a directory entry and some storage for the file

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// ByteArrayInputStream bis = new ByteArrayInputStream(cpmFile.readNet());
		try {
			Files.copy(Paths.get(nativeFile.getAbsolutePath()), bos);
		} catch (IOException e) {
			// Auto-generated catch block
			e.printStackTrace();
		} // try

		byte[] dataToWrite = bos.toByteArray();

		CPMFile newFile = CPMFile.createCPMFile(diskDrive, directory, fileName);
		// boolean result =
		newFile.writeNewFile(dataToWrite);

		// need the two display... methods run on different threads
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				displayPhysicalSector(0); // Physical View
			}// run
		});
		displayDirectoryView(); // Directory View
		// need the two display... methods run on different threads

	}// fileInport
		// ...............................................................

	private boolean enoughSpaceOnCPM(boolean deleteFile, String fileName) {
		int blocksNeeded = (int) Math.ceil(nativeFile.length() / (float) diskMetrics.getBytesPerBlock());
		int availableBlocks = directory.getAvailableBlockCount();
		if (availableBlocks > blocksNeeded) {
			return true;
		} //
		availableBlocks = deleteFile ? availableBlocks + directory.getFileBlocksCount(fileName) : availableBlocks;
		boolean result = true;

		if (blocksNeeded > directory.getAvailableBlockCount()) {
			String msg = String.format("Not enough space on CPM disk%n" + "Blocks Available: %,d -Blocks  Need: %,d",
					availableBlocks, blocksNeeded);
			JOptionPane.showMessageDialog(null, msg, "Copying a file to CPM", JOptionPane.WARNING_MESSAGE);
			result = false;
		} // if
		return result;
	}// enoughSpaceOnCPM
		// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	// ---File View------------------------------
	private void displaySelectedFile() {

		System.out.printf("[displaySelectedFile] panelFileHex.isDataChanged(): %s%n", panelFileHex.isDataChanged());

		if (panelFileHex.isDataChanged()) {
			byte[] changedData = panelFileHex.unloadData();
			cpmFile.write(changedData);
			lblDiskName.setForeground(Color.RED);
		} // if dirty file

		if (cbFileNames.getItemCount() == 0) {
			return;
		} // if
		DirEntry de = (DirEntry) cbFileNames.getSelectedItem();
		String fileName = de.fileName;
		cpmFile = CPMFile.getCPMFile(diskDrive, directory, fileName);

		lblRecordCount.setText(String.format(radixFormat, cpmFile.getRecordCount()));
		lblReadOnly.setVisible(cpmFile.isReadOnly());
		lblSystemFile.setVisible(cpmFile.isSystemFile());

		byte[] dataToDisplay;
		if (cpmFile.getRecordCount() == 0) {
			dataToDisplay = NO_ACTIVE_FILE.getBytes();
		} else {
			dataToDisplay = cpmFile.readRaw();
		} // if

		panelFileHex.loadData(dataToDisplay);

	}// displaySelectedFile

	// ---File View------------------------------

	// ---Directory View-------------------------
	private void displayDirectoryView() {
		dirMakeDirectory();
		dirMakeDirectoryTable();
	}// displayDirectoryView

	private void dirMakeDirectory() {
		if (directory != null) {
			directory = null;
		} // if

		directory = new CPMDirectory(diskDrive.getDiskType(), diskMetrics.isBootDisk());
		int firstDirectorySector = diskMetrics.getDirectoryStartSector();
		int lastDirectorySector = diskMetrics.getDirectorysLastSector();
		int entriesPerSector = bytesPerSector / Disk.DIRECTORY_ENTRY_SIZE;

		int directoryIndex = 0;
		for (int s = firstDirectorySector; s < lastDirectorySector + 1; s++) {
			diskDrive.setCurrentAbsoluteSector(s);
			diskSector = diskDrive.read();
			for (int i = 0; i < entriesPerSector; i++) {
				directory.addEntry(dirExtractDirectoryEntry(diskSector, i), directoryIndex++);
			} // for - i
		} // for -s
	}// makeDirectory

	private byte[] dirExtractDirectoryEntry(byte[] sector, int index) {
		byte[] rawDirectory = new byte[Disk.DIRECTORY_ENTRY_SIZE];
		int startIndex = index * Disk.DIRECTORY_ENTRY_SIZE;
		for (int i = 0; i < Disk.DIRECTORY_ENTRY_SIZE; i++) {
			rawDirectory[i] = sector[startIndex + i];
		} // for
		return rawDirectory;
	}// extractDirectoryEntry

	private void dirMakeDirectoryTable() {
		if (fileCpmModel != null) {
			fileCpmModel = null;
		} // if
		fileCpmModel = new FileCpmModel();

		if (dirTable != null) {
			dirTable = null;
		} // if
		Object[] columnNames = { "index", "Name", "type", "User", "R/O", "Sys", "Seq", "Count", "Blocks" };
		dirTable = new JTable(new DefaultTableModel(columnNames, 0)) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		dirTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		dirTable.setFont(new Font("Tahoma", Font.PLAIN, 14));
		dirTable.getSelectionModel().addListSelectionListener(new RowListener());
		// dirTable.setName(AC_DIRECTORY_TABLE);
		dirAdjustTableLook(dirTable);

		scrollDirectoryTable.setViewportView(dirTable);
		dirFillDirectoryTable(dirTable);
		dirTable.setRowSelectionInterval(0, 0);

	}// makeDirectoryTable

	private void showDirectoryDetail(int entryNumber) {
		CPMDirectoryEntry entry = directory.getDirectoryEntry(entryNumber);
		byte[] rawDirectory = entry.getRawDirectory();
		showDirectoryDetail(rawDirectory);
	}// showDirectoryDetail

	private void showDirectoryDetail(byte[] rawDirectory) {

		lblRawUser.setText(String.format("%02X", rawDirectory[0]));

		lblRawName.setText(String.format("%02X %02X %02X %02X %02X %02X %02X %02X ", rawDirectory[1], rawDirectory[2],
				rawDirectory[3], rawDirectory[4], rawDirectory[5], rawDirectory[6], rawDirectory[7], rawDirectory[8]));
		lblRawType.setText(String.format("%02X %02X %02X", rawDirectory[9], rawDirectory[10], rawDirectory[11]));
		lblRawEX.setText(String.format("%02X", rawDirectory[12]));
		lblRawS1.setText(String.format("%02X", rawDirectory[13]));
		lblRawS2.setText(String.format("%02X", rawDirectory[14]));

		lblRawRC.setText(String.format("%02X", rawDirectory[15]));

		lblRawAllocation.setText(String.format(
				"%02X %02X %02X %02X %02X %02X %02X %02X %02X %02X %02X %02X %02X %02X %02X %02X", rawDirectory[16],
				rawDirectory[17], rawDirectory[18], rawDirectory[19], rawDirectory[20], rawDirectory[21],
				rawDirectory[22], rawDirectory[23], rawDirectory[24], rawDirectory[25], rawDirectory[26],
				rawDirectory[27], rawDirectory[28], rawDirectory[29], rawDirectory[30], rawDirectory[31]));
	}// showDirectoryDetail

	private void dirAdjustTableLook(JTable table) {
		Font realColumnFont = table.getFont();
		int charWidth = table.getFontMetrics(realColumnFont).getWidths()[0X57];

		TableColumnModel tableColumn = table.getColumnModel();
		tableColumn.getColumn(0).setPreferredWidth(charWidth * 3);
		tableColumn.getColumn(1).setPreferredWidth(charWidth * 9);
		tableColumn.getColumn(2).setPreferredWidth(charWidth * 4);
		tableColumn.getColumn(3).setPreferredWidth(charWidth * 3);
		tableColumn.getColumn(4).setPreferredWidth(charWidth * 4);
		tableColumn.getColumn(5).setPreferredWidth(charWidth * 4);
		tableColumn.getColumn(6).setPreferredWidth(charWidth * 3);
		tableColumn.getColumn(7).setPreferredWidth(charWidth * 3);
		tableColumn.getColumn(8).setPreferredWidth(charWidth * 3);

		DefaultTableCellRenderer rightAlign = new DefaultTableCellRenderer();
		rightAlign.setHorizontalAlignment(JLabel.RIGHT);
		tableColumn.getColumn(0).setCellRenderer(rightAlign);
		tableColumn.getColumn(3).setCellRenderer(rightAlign);
		tableColumn.getColumn(6).setCellRenderer(rightAlign);
		tableColumn.getColumn(7).setCellRenderer(rightAlign);
		tableColumn.getColumn(8).setCellRenderer(rightAlign);

		DefaultTableCellRenderer centerAlign = new DefaultTableCellRenderer();
		centerAlign.setHorizontalAlignment(JLabel.CENTER);
		tableColumn.getColumn(4).setCellRenderer(centerAlign);
		tableColumn.getColumn(5).setCellRenderer(centerAlign);
	}// adjustTableLook

	private void dirFillDirectoryTable(JTable table) {
		String name, type;
		int user, seqNumber, count, blocks;
		boolean readOnly, systemFile;
		modelDir = (DefaultTableModel) table.getModel();
		CPMDirectoryEntry entry;
		for (int i = 0; i < diskMetrics.getDRM() + 1; i++) {
			entry = directory.getDirectoryEntry(i);

			name = entry.getFileNameTrim();
			type = entry.getFileTypeTrim();
			user = entry.getUserNumberInt();
			readOnly = entry.isReadOnly();
			systemFile = entry.isSystemFile();
			seqNumber = entry.getActualExtentNumber();
			count = entry.getRcInt();
			blocks = entry.getBlockCount();
			modelDir.insertRow(i, new Object[] { i, name, type, user, readOnly, systemFile, seqNumber, count, blocks });
			if (!entry.isEmpty()) {
				dirFillFileChoosers(entry, i);
			} // if
		} // for
		cbFileNames.setModel(fileCpmModel);
		// cbCpmFileInOut.setModel(fileCpmModel);//
		if (fileCpmModel.getSize() != 0) {
			cbFileNames.setSelectedIndex(0);
			cbCpmFileInOut.setSelectedIndex(0);
			//
		} // if
	}// fillDirectoryTable

	private void dirFillFileChoosers(CPMDirectoryEntry entry, int index) {
		if (entry.getActualExtentNumber() > 1) {
			return; // only want one entry per file [0 or 1]
		} // if
		String entryName = entry.getNameAndTypePeriod();
		fileCpmModel.add(new DirEntry(entryName, index));
		cbCpmFileInOut.addItem(entryName);
	}// fillFileChoosers

	// ---Directory View-------------------------

	// ---Physical View-------------------------

	private void setHeadTrackSectorSizes(RawDiskDrive diskDrive) {

		((SpinnerNumberModel) hdnHead.getNumberModel()).setValue(0);
		((SpinnerNumberModel) hdnTrack.getNumberModel()).setValue(0);
		((SpinnerNumberModel) hdnSector.getNumberModel()).setValue(1);
		hdSeekPanel.mute(true);
		hdSeekPanel.setValue(0);
		hdSeekPanel.mute(false);

		if (diskDrive == null) {
			((SpinnerNumberModel) hdnHead.getNumberModel()).setMaximum(0);
			((SpinnerNumberModel) hdnTrack.getNumberModel()).setMaximum(0);
			((SpinnerNumberModel) hdnSector.getNumberModel()).setMaximum(1);
			((SpinnerNumberModel) hdnSector.getNumberModel()).setMinimum(1);
			hdSeekPanel.setMaxValue(0);

		} else {
			((SpinnerNumberModel) hdnHead.getNumberModel()).setMaximum(diskDrive.getHeads() - 1);
			((SpinnerNumberModel) hdnTrack.getNumberModel()).setMaximum(diskDrive.getTracksPerHead() - 1);
			((SpinnerNumberModel) hdnSector.getNumberModel()).setMaximum(diskDrive.getSectorsPerTrack());
			hdSeekPanel.setMaxValue(diskDrive.getTotalSectorsOnDisk() - 1);

		} // if
	}// setHeadTrackSectorSizes

	private void selectedNewPhysicalSector(boolean fromSeekPanel) {
		// int priorSector = diskDrive.getCurrentAbsoluteSector(); // currently
		// displayed
		if (panelSectorDisplay.isDataChanged()) {

			diskDrive.write(panelSectorDisplay.unloadData()); //

		} //
		int newSector = hdSeekPanel.getValue();

		if (fromSeekPanel) {
			hdnHead.mute(true);
			hdnTrack.mute(true);
			hdnSector.mute(true);

			diskDrive.setCurrentAbsoluteSector(newSector);
			hdnHead.setValue(diskDrive.getCurrentHead());
			hdnTrack.setValue(diskDrive.getCurrentTrack());
			hdnSector.setValue(diskDrive.getCurrentSector());

			hdnHead.mute(false);
			hdnTrack.mute(false);
			hdnSector.mute(false);

		} else {

			diskDrive.setCurrentAbsoluteSector(hdnHead.getValue(), hdnTrack.getValue(), hdnSector.getValue());
			newSector = diskDrive.getCurrentAbsoluteSector();
			hdSeekPanel.mute(true);
			hdSeekPanel.setValue(newSector);
			hdSeekPanel.mute(false);
		} // if
		displayPhysicalSector(newSector);

	}// selectedNewPhysicalSector

	private void displayPhysicalSector(int absoluteSector) {
		if ((0 > absoluteSector) || (diskDrive.getTotalSectorsOnDisk() < absoluteSector)) {
			absoluteSector = 0;
		} // if

		System.out.printf("[displayPhysicalSector] panelSectorDisplay.isDataChanged(): %s%n",
				panelSectorDisplay.isDataChanged());

		diskDrive.setCurrentAbsoluteSector(absoluteSector);
		panelSectorDisplay.loadData(diskDrive.read());
	}// displayPhysicalSector

	// ---Physical View-------------------------

	// ---Utilities View-------------------------
	private File getNativeFile() {
		// String nativeDirectory = System.getProperty(USER_HOME, THIS_DIR);

		JFileChooser nativeChooser = new JFileChooser(nativeDirectory);
		nativeChooser.setMultiSelectionEnabled(false);
		if (nativeChooser.showDialog(this, "Select the file") != JFileChooser.APPROVE_OPTION) {
			btnImport.setEnabled(false);
			btnExport.setEnabled(false);
			txtNativeFileInOut.setText("");
			txtNativeFileInOut.setToolTipText("");
			return null;
		} // if
		nativeDirectory = nativeChooser.getCurrentDirectory().getAbsolutePath();
		btnImport.setEnabled(true);
		btnExport.setEnabled(true);

		nativeFile = nativeChooser.getSelectedFile();

		txtNativeFileInOut.setText(nativeFile.getName());
		txtNativeFileInOut.setToolTipText(nativeFile.getAbsolutePath());

		// sourceSize = nativeFile.length();

		return nativeFile;
	}// getNativeFile
		// --------------------------------Catalog ----------------------------------------

	private void doChangeDiskType() {
		Object[] options = { "F5HD", "F5DD", "F3HD", "F3DD", "F5ED", "F8SS", "F8DS" };
		Object selectType = JOptionPane.showInputDialog(null, "Choose Disk Type", "Type",
				JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		lblDiskType.setText((String) selectType);
	}// changeDiskType

	private void doChangeDiskDirectory() {
		JFileChooser fc = new JFileChooser(lblDirectory.getText());
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			lblDirectory.setText(fc.getSelectedFile().getAbsolutePath());
		} // if
	}// changeDiskDirectory

	private void doFindFiles() {
		// makeStyles();
		scrollPane.setViewportView(txtCatalog);
		setAttributes();
		clearCatalog();
		fileMatchCount = 0;
		Pattern p = makePattern();
		File startDir = new File(lblDirectory.getText());
		findFiles(startDir, p);
		lblCatalogHeader
				.setText(String.format("%,d matches for search pattern %s", fileMatchCount, txtFindFileName.getText()));
		txtCatalog.setCaretPosition(0);
		btnPrintResult.setVisible(fileMatchCount == 0 ? false : true);
	}// doFindFiles

	private void doListFiles() {
		btnPrintResult.setVisible(false);
		catMakeCatalogTable();
	}// doListFiles

	private void clearCatalog() {
		try {
			doc.remove(0, doc.getLength());
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // try
		btnPrintResult.setVisible(false);
	}// clearCatalog

	private void doPrintResult() {
		if (scrollPane.getViewport().getView() instanceof JTable) {
			printCatalog(catTable);
		} else if (scrollPane.getViewport().getView() instanceof JTextPane) {
			printListing(txtCatalog, lblCatalogHeader.getText());
		} // if

	}// doPrintResult

	private void printCatalog(JTable table) {
		Font originalFont = table.getFont();
		String fontName = originalFont.getName();

		try {
//			table.setFont(new Font(fontName, Font.PLAIN, 12));
			MessageFormat header = new MessageFormat("Disk Catalog");
			MessageFormat footer = new MessageFormat(new Date().toString() + "           Page - {0}");

			table.print(JTable.PrintMode.FIT_WIDTH,header, footer);
			
			table.setFont(originalFont);
		} catch (PrinterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void printListing(JTextPane textPane, String name) {
		Font originalFont = textPane.getFont();
		String fontName = originalFont.getName();

		try {
			textPane.setFont(new Font(fontName, Font.PLAIN, 8));
			MessageFormat header = new MessageFormat(name);
			MessageFormat footer = new MessageFormat(new Date().toString() + "           Page - {0}");
			textPane.print(header, footer);
			textPane.setFont(originalFont);
		} catch (PrinterException e) {
			e.printStackTrace();
		} // try
	}// printListing

	private Pattern makePattern() {
		String pattern = new String();
		String name, ext;
		String sourceFileName = txtFindFileName.getText().trim();
		if (sourceFileName.contains(".")) {
			String[] targetSet = txtFindFileName.getText().split("\\.");
			name = targetSet[0].trim();
			ext = targetSet[1].trim();
		} else {
			name = sourceFileName;
			ext = "   ";
		} // if period or not
		pattern = getPattern(name, 8);
		pattern += getPattern(ext, 3);
		return Pattern.compile(pattern);
	}// makePattern

	private String getPattern(String s, int size) {
		StringBuilder sb = new StringBuilder();
		boolean foundStar = false;
		String subjectString = "";
		int i = 0;
		for (; i < s.length(); i++) {
			subjectString = s.substring(i, i + 1);
			if (subjectString.equals("*")) {
				foundStar = true;
				break;
			} // if *
			if (subjectString.equals("?")) {
				sb.append(".");
			} else
				sb.append(subjectString);
		} // for
		String padString = foundStar ? "." : " "; // either a period or space
		for (; i < size; i++) {
			sb.append(padString);
		} // fill out
		return sb.toString();
	}// getPattern

	private void findFiles(File enterFile, Pattern p) {

		File[] files = enterFile.listFiles();
		for (File file : files) {
			if (rbRecurse.isSelected() && file.isDirectory()) {
				findFiles(file, p);
			} // if for recursion
			processTheFile(file, p);
		} // for each file
	}// findFiles

	private void processTheFile(File file, Pattern p) {
		StringBuilder result = new StringBuilder();
		String fileName = file.getName().toUpperCase();
		if (!fileName.endsWith("." + lblDiskType.getText())) {
			// txtCatalog.append(String.format("\t\tskipped File %s%n", fileName));
			return; // only want right type of disk
		} // if right disk type
			// txtCatalog.append(String.format("%n\t\tDisk - %s:%n", fileName));
		RawDiskDrive diskDrive = new RawDiskDrive(file.getAbsolutePath());
		DiskMetrics diskMetrics = DiskMetrics.getDiskMetric(lblDiskType.getText());

		byte[] diskSector;

		int firstDirectorySector = diskMetrics.getDirectoryStartSector();
		int lastDirectorySector = diskMetrics.getDirectorysLastSector();
		int entriesPerSector = diskMetrics.bytesPerSector / Disk.DIRECTORY_ENTRY_SIZE;

		for (int s = firstDirectorySector; s < lastDirectorySector + 1; s++) {
			diskDrive.setCurrentAbsoluteSector(s);
			diskSector = diskDrive.read();
			for (int i = 0; i < entriesPerSector; i++) {
				String cpmFileName = extractName(diskSector, i);
				if (cpmFileName == null) {
					continue;
				} // if null
				Matcher m = p.matcher(cpmFileName);

				if (m.matches()) {
					result.append(String.format("%s%n", cpmFileName));
					fileMatchCount++;
					// txtCatalog.append(String.format("%s%n", cpmFileName));
				} else {
					// txtLog1.append(String.format("\t\t%s NOT A MATCH%n", cpmFileName));

				} // if match

			} // for - i
		} // for -s

		if (result.length() > 0) {
			try {
				doc.insertString(doc.getLength(), String.format("%n\t\tDisk -  %s:%n", fileName), attrTeal);
				doc.insertString(doc.getLength(), result.toString(), attrMaroon);
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// txtCatalog.append(String.format("%n\t\tDisk - %s:%n", fileName));
			// txtCatalog.append(result.toString());
		} // if

	}// processTheFile

	/**
	 * returns name if it is first occurrence and is an active file
	 * 
	 * @param sector
	 * @param index
	 * @return
	 */

	private String extractName(byte[] sector, int index) {
		int startIndex = index * Disk.DIRECTORY_ENTRY_SIZE;
		if (sector[startIndex] == Disk.EMPTY_ENTRY) {
			return null;
		} // if empty
		if (sector[startIndex + 12] != 0) {
			return null;
		} // not the first entry - only want it once
		byte[] nameArray = new byte[11];
		for (int i = 1; i < 12; i++) {
			nameArray[i - 1] = sector[startIndex + i];
		} // for

		return new String(nameArray);
	}// extractName

	private SimpleAttributeSet attrBlack = new SimpleAttributeSet();
	private SimpleAttributeSet attrBlue = new SimpleAttributeSet();
	private SimpleAttributeSet attrGray = new SimpleAttributeSet();
	private SimpleAttributeSet attrGreen = new SimpleAttributeSet();
	private SimpleAttributeSet attrRed = new SimpleAttributeSet();
	private SimpleAttributeSet attrSilver = new SimpleAttributeSet();
	private SimpleAttributeSet attrNavy = new SimpleAttributeSet();
	private SimpleAttributeSet attrMaroon = new SimpleAttributeSet();
	private SimpleAttributeSet attrTeal = new SimpleAttributeSet();

	private void setAttributes() {
		StyleConstants.setForeground(attrNavy, new Color(0, 0, 128));
		StyleConstants.setForeground(attrBlack, new Color(0, 0, 0));
		StyleConstants.setForeground(attrBlue, new Color(0, 0, 255));
		StyleConstants.setForeground(attrGreen, new Color(0, 128, 0));
		StyleConstants.setForeground(attrTeal, new Color(0, 128, 128));
		StyleConstants.setForeground(attrGray, new Color(128, 128, 128));
		StyleConstants.setForeground(attrSilver, new Color(192, 192, 192));
		StyleConstants.setForeground(attrRed, new Color(255, 0, 0));
		StyleConstants.setForeground(attrMaroon, new Color(128, 0, 0));
	}// setAttributes

	private void catMakeCatalogTable() {
		catalogRow = 0;
		Object[] columnNames = { "cpmFile", "Disk", "Location" };
		catTable = new JTable(new DefaultTableModel(columnNames, 0)) {
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		catTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		catTable.setFont(new Font("Courier New", Font.PLAIN, 14));
		modelCat = (DefaultTableModel) catTable.getModel();
		catAdjustTableLook(catTable);

		catTable.setAutoCreateRowSorter(true);
		TableRowSorter<TableModel> sorter = new TableRowSorter(modelCat);
		catTable.setRowSorter(sorter);

		scrollPane.setViewportView(catTable);
		// catFillCatalogTable(catTable);
		catGetEntries(new File(lblDirectory.getText()), modelCat);
		btnPrintResult.setVisible(catalogRow ==0?false:true);
	}// makeCatalogTable

	private void catAdjustTableLook(JTable table) {
		Font realColumnFont = table.getFont();
		int charWidth = table.getFontMetrics(realColumnFont).getWidths()[0X57];

		TableColumnModel tableColumn = table.getColumnModel();
		tableColumn.getColumn(0).setPreferredWidth(charWidth * 13); // cpmFile
		tableColumn.getColumn(1).setPreferredWidth(charWidth * 25); // Disk
		tableColumn.getColumn(2).setPreferredWidth(charWidth * 40); // Path

		DefaultTableCellRenderer leftAlign = new DefaultTableCellRenderer();
		leftAlign.setHorizontalAlignment(JLabel.LEFT);
		tableColumn.getColumn(0).setCellRenderer(leftAlign);
		tableColumn.getColumn(1).setCellRenderer(leftAlign);
		tableColumn.getColumn(2).setCellRenderer(leftAlign);

	}// adjustTableLook

	private void catGetEntries(File enterFile, DefaultTableModel model) {

		File[] files = enterFile.listFiles();
		for (File file : files) {
			if (rbRecurse.isSelected() && file.isDirectory()) {
				catGetEntries(file, model);
			} // if for recursion
			catGetDiskInfo(file, model);
		} // for each file
	}// findFiles

	private void catGetDiskInfo(File file, DefaultTableModel model) {
		// StringBuilder result = new StringBuilder();
		String fileName = file.getName().toUpperCase();
		if (!fileName.endsWith("." + lblDiskType.getText())) {
			// txtCatalog.append(String.format("\t\tskipped File %s%n", fileName));
			return; // only want right type of disk
		} // if right disk type
		String disk = file.getName();
		String path = file.getAbsolutePath();
		String location = path.substring(0, (path.length() - disk.length() - 1));

		RawDiskDrive diskDrive = new RawDiskDrive(file.getAbsolutePath());
		DiskMetrics diskMetrics = DiskMetrics.getDiskMetric(lblDiskType.getText());

		byte[] diskSector;

		int firstDirectorySector = diskMetrics.getDirectoryStartSector();
		int lastDirectorySector = diskMetrics.getDirectorysLastSector();
		int entriesPerSector = diskMetrics.bytesPerSector / Disk.DIRECTORY_ENTRY_SIZE;

		for (int s = firstDirectorySector; s < lastDirectorySector + 1; s++) {
			diskDrive.setCurrentAbsoluteSector(s);
			diskSector = diskDrive.read();
			for (int i = 0; i < entriesPerSector; i++) {
				String cpmFileName = extractName(diskSector, i);
				if (cpmFileName == null) {
					continue;
				} // if null
				model.insertRow(catalogRow++, new Object[] { cpmFileName, disk, location });
			} // for - i
		} // for -s
	}// processTheFile

	// --------------------------------Catalog ----------------------------------------
	// ---Utilities View-------------------------

	private void manageFileMenus(String source) {
		switch (source) {
		case MNU_TOOLS_NEW_DISK:
		case MNU_DISK_LOAD_DISK:
			mnuToolsNewDisk.setEnabled(false);
			mnuDiskLoad.setEnabled(false);
			mnuDiskClose.setEnabled(true);
			mnuDiskSave.setEnabled(true);
			mnuDiskSaveAs.setEnabled(true);
			mnuDiskExit.setEnabled(true);
			break;
		case MNU_DISK_CLOSE:
			mnuToolsNewDisk.setEnabled(true);
			mnuDiskLoad.setEnabled(true);
			mnuDiskClose.setEnabled(false);
			mnuDiskSave.setEnabled(false);
			mnuDiskSaveAs.setEnabled(false);
			mnuDiskExit.setEnabled(true);
		case MNU_DISK_SAVE:
		case MNU_DISK_SAVE_AS:
		case MNU_DISK_EXIT:
			break;
		default:
		}// switch
	}// manageFileMenus

	// .............................................................

	// -------------------------------------------------------------------
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DiskUtility window = new DiskUtility();
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}// run
		});
	}// main
		// -------

	private void appClose() {
		System.out.printf("[DiskUtility.appClose()]  %s%n", "closing");
		diskClose();
		Preferences myPrefs = Preferences.userNodeForPackage(DiskUtility.class).node(this.getClass().getSimpleName());
		Dimension dim = this.getSize();
		myPrefs.putInt("Height", dim.height);
		myPrefs.putInt("Width", dim.width);
		Point point = this.getLocation();
		myPrefs.putInt("LocX", point.x);
		myPrefs.putInt("LocY", point.y);
		myPrefs.putInt("Tab", tabbedPane.getSelectedIndex());
		myPrefs.put("nativeDirectory", nativeDirectory);

		myPrefs.put("DiskType", lblDiskType.getText());
		myPrefs.put("DiskDirectory", lblDirectory.getText());
		myPrefs.put("FindFileName", txtFindFileName.getText());
		myPrefs.putBoolean("Recurse", rbRecurse.isSelected());

		myPrefs = null;
		dispose();
	}// appClose

	// :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

	class PrefixFilter implements FilenameFilter {
		String prefix;

		public PrefixFilter(String prefix) {
			this.prefix = prefix;
		}// Constructor

		public boolean accept(File dir, String name) {
			boolean yes = name.startsWith(prefix);
			return yes;
			// return name.endsWith(ext);
		}// accept
	}// class PrefixFilter
		// :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

	private void appInit() {
		Preferences myPrefs = Preferences.userNodeForPackage(DiskUtility.class).node(this.getClass().getSimpleName());
		this.setSize(895, 895);
		this.setLocation(myPrefs.getInt("LocX", 100), myPrefs.getInt("LocY", 100));
		tabbedPane.setSelectedIndex(myPrefs.getInt("Tab", 0));
		nativeDirectory = myPrefs.get("nativeDirectory", System.getProperty(USER_HOME, THIS_DIR));

		lblDiskType.setText(myPrefs.get("DiskType", "F3HD"));
		lblDirectory.setText(myPrefs.get("DiskDirectory", "."));
		txtFindFileName.setText(myPrefs.get("FindFileName", ""));
		rbRecurse.setSelected(myPrefs.getBoolean("Recurse", false));

		myPrefs = null;
		hdNumberBoxs = new ArrayList<HDNumberBox>();
		hdNumberBoxs.add(hdnHead);
		hdNumberBoxs.add(hdnTrack);
		hdNumberBoxs.add(hdnSector);
		hdNumberBoxs.add(hdSeekPanel);

		// setDisplayRadix();
		haveDisk(false);
		manageFileMenus(MNU_DISK_CLOSE);

		doc = txtCatalog.getStyledDocument();

	}// appInit

	/**
	 * Create the application.
	 */
	private DiskUtility() {
		// appInit0();
		initialize();
		appInit();
	}// Constructor

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				appClose();
			}//
		});

		setTitle("Disk Utility");
		setBounds(100, 100, 450, 300);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);

		JToolBar toolBar = new JToolBar();
		GridBagConstraints gbc_toolBar = new GridBagConstraints();
		gbc_toolBar.anchor = GridBagConstraints.WEST;
		gbc_toolBar.insets = new Insets(0, 0, 5, 0);
		gbc_toolBar.gridx = 0;
		gbc_toolBar.gridy = 0;
		getContentPane().add(toolBar, gbc_toolBar);

		tbBootable = new JToggleButton("Bootable");
		tbBootable.setName(TB_BOOTABLE);
		tbBootable.addActionListener(diskUtilityAdapter);
		tbBootable.setHorizontalAlignment(SwingConstants.LEFT);
		toolBar.add(tbBootable);

		Component horizontalStrut = Box.createHorizontalStrut(20);
		toolBar.add(horizontalStrut);

		tbDecimalDisplay = new JToggleButton("Display Decimal");
		tbDecimalDisplay.setName(TB_DECIMAL_DISPLAY);
		tbDecimalDisplay.addActionListener(diskUtilityAdapter);
		toolBar.add(tbDecimalDisplay);

		JButton btnNewButton = new JButton("New button");
		btnNewButton.setVisible(false);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

			}
		});
		toolBar.add(btnNewButton);

		JPanel panelMain = new JPanel();
		GridBagConstraints gbc_panelMain = new GridBagConstraints();
		gbc_panelMain.fill = GridBagConstraints.VERTICAL;
		gbc_panelMain.gridx = 0;
		gbc_panelMain.gridy = 1;
		getContentPane().add(panelMain, gbc_panelMain);
		GridBagLayout gbl_panelMain = new GridBagLayout();
		gbl_panelMain.columnWidths = new int[] { 0, 0 };
		gbl_panelMain.rowHeights = new int[] { 0, 0, 0 };
		gbl_panelMain.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panelMain.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		panelMain.setLayout(gbl_panelMain);

		lblDiskName = new JLabel(NO_ACTIVE_FILE);
		lblDiskName.setToolTipText("<No File Active>");
		lblDiskName.setFont(new Font("Arial", Font.BOLD, 18));
		GridBagConstraints gbc_lblDiskName = new GridBagConstraints();
		gbc_lblDiskName.insets = new Insets(0, 0, 5, 0);
		gbc_lblDiskName.gridx = 0;
		gbc_lblDiskName.gridy = 0;
		panelMain.add(lblDiskName, gbc_lblDiskName);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 1;
		panelMain.add(tabbedPane, gbc_tabbedPane);

		JPanel tabDirectory = new JPanel();
		tabbedPane.addTab("Directory View", null, tabDirectory, null);
		GridBagLayout gbl_tabDirectory = new GridBagLayout();
		gbl_tabDirectory.columnWidths = new int[] { 0, 0 };
		gbl_tabDirectory.rowHeights = new int[] { 0, 0, 0 };
		gbl_tabDirectory.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_tabDirectory.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		tabDirectory.setLayout(gbl_tabDirectory);

		panelDirectory = new JPanel();
		GridBagConstraints gbc_panelDirectory = new GridBagConstraints();
		gbc_panelDirectory.insets = new Insets(0, 0, 5, 0);
		gbc_panelDirectory.fill = GridBagConstraints.VERTICAL;
		gbc_panelDirectory.gridx = 0;
		gbc_panelDirectory.gridy = 0;
		tabDirectory.add(panelDirectory, gbc_panelDirectory);
		GridBagLayout gbl_panelDirectory = new GridBagLayout();
		gbl_panelDirectory.columnWidths = new int[] { 0, 0 };
		gbl_panelDirectory.rowHeights = new int[] { 0, 0, 0 };
		gbl_panelDirectory.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_panelDirectory.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panelDirectory.setLayout(gbl_panelDirectory);

		panelDirRaw = new JPanel();
		GridBagConstraints gbc_panelDirRaw = new GridBagConstraints();
		gbc_panelDirRaw.insets = new Insets(0, 0, 5, 0);
		gbc_panelDirRaw.fill = GridBagConstraints.BOTH;
		gbc_panelDirRaw.gridx = 0;
		gbc_panelDirRaw.gridy = 0;
		panelDirectory.add(panelDirRaw, gbc_panelDirRaw);
		GridBagLayout gbl_panelDirRaw = new GridBagLayout();
		gbl_panelDirRaw.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panelDirRaw.rowHeights = new int[] { 0, 0 };
		gbl_panelDirRaw.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panelDirRaw.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panelDirRaw.setLayout(gbl_panelDirRaw);

		panelRawUser = new JPanel();
		panelRawUser.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GridBagConstraints gbc_panelRawUser = new GridBagConstraints();
		gbc_panelRawUser.insets = new Insets(0, 0, 0, 5);
		gbc_panelRawUser.fill = GridBagConstraints.BOTH;
		gbc_panelRawUser.gridx = 0;
		gbc_panelRawUser.gridy = 0;
		panelDirRaw.add(panelRawUser, gbc_panelRawUser);
		GridBagLayout gbl_panelRawUser = new GridBagLayout();
		gbl_panelRawUser.columnWidths = new int[] { 0, 0 };
		gbl_panelRawUser.rowHeights = new int[] { 0, 0, 0 };
		gbl_panelRawUser.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_panelRawUser.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panelRawUser.setLayout(gbl_panelRawUser);

		label = new JLabel("User [0]");
		label.setFont(new Font("Arial", Font.PLAIN, 12));
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.insets = new Insets(0, 0, 5, 0);
		gbc_label.gridx = 0;
		gbc_label.gridy = 0;
		panelRawUser.add(label, gbc_label);

		lblRawUser = new JLabel("00");
		lblRawUser.setForeground(Color.BLUE);
		lblRawUser.setFont(new Font("Courier New", Font.BOLD, 16));
		GridBagConstraints gbc_lblRawUser = new GridBagConstraints();
		gbc_lblRawUser.gridx = 0;
		gbc_lblRawUser.gridy = 1;
		panelRawUser.add(lblRawUser, gbc_lblRawUser);

		panelRawName = new JPanel();
		panelRawName.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GridBagConstraints gbc_panelRawName = new GridBagConstraints();
		gbc_panelRawName.insets = new Insets(0, 0, 0, 5);
		gbc_panelRawName.fill = GridBagConstraints.BOTH;
		gbc_panelRawName.gridx = 1;
		gbc_panelRawName.gridy = 0;
		panelDirRaw.add(panelRawName, gbc_panelRawName);
		GridBagLayout gbl_panelRawName = new GridBagLayout();
		gbl_panelRawName.columnWidths = new int[] { 27, 0 };
		gbl_panelRawName.rowHeights = new int[] { 15, 18, 0 };
		gbl_panelRawName.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_panelRawName.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panelRawName.setLayout(gbl_panelRawName);

		label_2 = new JLabel("Name [1-8]");
		label_2.setFont(new Font("Arial", Font.PLAIN, 12));
		GridBagConstraints gbc_label_2 = new GridBagConstraints();
		gbc_label_2.insets = new Insets(0, 0, 5, 0);
		gbc_label_2.gridx = 0;
		gbc_label_2.gridy = 0;
		panelRawName.add(label_2, gbc_label_2);

		lblRawName = new JLabel("00 00 00 00 00 00 00 00");
		lblRawName.setForeground(Color.BLUE);
		lblRawName.setFont(new Font("Courier New", Font.BOLD, 15));
		GridBagConstraints gbc_lblRawName = new GridBagConstraints();
		gbc_lblRawName.gridx = 0;
		gbc_lblRawName.gridy = 1;
		panelRawName.add(lblRawName, gbc_lblRawName);

		panelRawType = new JPanel();
		panelRawType.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GridBagConstraints gbc_panelRawType = new GridBagConstraints();
		gbc_panelRawType.insets = new Insets(0, 0, 0, 5);
		gbc_panelRawType.fill = GridBagConstraints.BOTH;
		gbc_panelRawType.gridx = 2;
		gbc_panelRawType.gridy = 0;
		panelDirRaw.add(panelRawType, gbc_panelRawType);
		GridBagLayout gbl_panelRawType = new GridBagLayout();
		gbl_panelRawType.columnWidths = new int[] { 0, 0 };
		gbl_panelRawType.rowHeights = new int[] { 0, 0, 0 };
		gbl_panelRawType.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_panelRawType.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panelRawType.setLayout(gbl_panelRawType);

		label_4 = new JLabel("Type [9-11]");
		label_4.setFont(new Font("Arial", Font.PLAIN, 12));
		GridBagConstraints gbc_label_4 = new GridBagConstraints();
		gbc_label_4.insets = new Insets(0, 0, 5, 0);
		gbc_label_4.gridx = 0;
		gbc_label_4.gridy = 0;
		panelRawType.add(label_4, gbc_label_4);

		lblRawType = new JLabel("00 00 00");
		lblRawType.setForeground(Color.BLUE);
		lblRawType.setFont(new Font("Courier New", Font.BOLD, 15));
		GridBagConstraints gbc_lblRawType = new GridBagConstraints();
		gbc_lblRawType.gridx = 0;
		gbc_lblRawType.gridy = 1;
		panelRawType.add(lblRawType, gbc_lblRawType);

		panelEX = new JPanel();
		panelEX.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GridBagConstraints gbc_panelEX = new GridBagConstraints();
		gbc_panelEX.insets = new Insets(0, 0, 0, 5);
		gbc_panelEX.fill = GridBagConstraints.BOTH;
		gbc_panelEX.gridx = 3;
		gbc_panelEX.gridy = 0;
		panelDirRaw.add(panelEX, gbc_panelEX);
		GridBagLayout gbl_panelEX = new GridBagLayout();
		gbl_panelEX.columnWidths = new int[] { 0, 0 };
		gbl_panelEX.rowHeights = new int[] { 0, 0, 0 };
		gbl_panelEX.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_panelEX.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panelEX.setLayout(gbl_panelEX);

		label_6 = new JLabel("EX [12]");
		label_6.setFont(new Font("Arial", Font.PLAIN, 12));
		GridBagConstraints gbc_label_6 = new GridBagConstraints();
		gbc_label_6.insets = new Insets(0, 0, 5, 0);
		gbc_label_6.gridx = 0;
		gbc_label_6.gridy = 0;
		panelEX.add(label_6, gbc_label_6);

		lblRawEX = new JLabel("00");
		lblRawEX.setForeground(Color.BLUE);
		lblRawEX.setFont(new Font("Courier New", Font.BOLD, 15));
		GridBagConstraints gbc_lblRawEX = new GridBagConstraints();
		gbc_lblRawEX.gridx = 0;
		gbc_lblRawEX.gridy = 1;
		panelEX.add(lblRawEX, gbc_lblRawEX);

		panelS1 = new JPanel();
		panelS1.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GridBagConstraints gbc_panelS1 = new GridBagConstraints();
		gbc_panelS1.insets = new Insets(0, 0, 0, 5);
		gbc_panelS1.fill = GridBagConstraints.BOTH;
		gbc_panelS1.gridx = 4;
		gbc_panelS1.gridy = 0;
		panelDirRaw.add(panelS1, gbc_panelS1);
		GridBagLayout gbl_panelS1 = new GridBagLayout();
		gbl_panelS1.columnWidths = new int[] { 0, 0 };
		gbl_panelS1.rowHeights = new int[] { 0, 0, 0 };
		gbl_panelS1.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_panelS1.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panelS1.setLayout(gbl_panelS1);

		label_8 = new JLabel("S1 [13]");
		label_8.setFont(new Font("Arial", Font.PLAIN, 12));
		GridBagConstraints gbc_label_8 = new GridBagConstraints();
		gbc_label_8.insets = new Insets(0, 0, 5, 0);
		gbc_label_8.gridx = 0;
		gbc_label_8.gridy = 0;
		panelS1.add(label_8, gbc_label_8);

		lblRawS1 = new JLabel("00");
		lblRawS1.setForeground(Color.BLUE);
		lblRawS1.setFont(new Font("Courier New", Font.BOLD, 15));
		GridBagConstraints gbc_lblRawS1 = new GridBagConstraints();
		gbc_lblRawS1.gridx = 0;
		gbc_lblRawS1.gridy = 1;
		panelS1.add(lblRawS1, gbc_lblRawS1);

		panelS2 = new JPanel();
		panelS2.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GridBagConstraints gbc_panelS2 = new GridBagConstraints();
		gbc_panelS2.insets = new Insets(0, 0, 0, 5);
		gbc_panelS2.fill = GridBagConstraints.BOTH;
		gbc_panelS2.gridx = 5;
		gbc_panelS2.gridy = 0;
		panelDirRaw.add(panelS2, gbc_panelS2);
		GridBagLayout gbl_panelS2 = new GridBagLayout();
		gbl_panelS2.columnWidths = new int[] { 0, 0 };
		gbl_panelS2.rowHeights = new int[] { 0, 0, 0 };
		gbl_panelS2.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_panelS2.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panelS2.setLayout(gbl_panelS2);

		label_10 = new JLabel("S2 [14]");
		label_10.setFont(new Font("Arial", Font.PLAIN, 12));
		GridBagConstraints gbc_label_10 = new GridBagConstraints();
		gbc_label_10.insets = new Insets(0, 0, 5, 0);
		gbc_label_10.gridx = 0;
		gbc_label_10.gridy = 0;
		panelS2.add(label_10, gbc_label_10);

		lblRawS2 = new JLabel("00");
		lblRawS2.setForeground(Color.BLUE);
		lblRawS2.setFont(new Font("Courier New", Font.BOLD, 15));
		GridBagConstraints gbc_lblRawS2 = new GridBagConstraints();
		gbc_lblRawS2.gridx = 0;
		gbc_lblRawS2.gridy = 1;
		panelS2.add(lblRawS2, gbc_lblRawS2);

		panelRC = new JPanel();
		panelRC.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GridBagConstraints gbc_panelRC = new GridBagConstraints();
		gbc_panelRC.fill = GridBagConstraints.BOTH;
		gbc_panelRC.gridx = 6;
		gbc_panelRC.gridy = 0;
		panelDirRaw.add(panelRC, gbc_panelRC);
		GridBagLayout gbl_panelRC = new GridBagLayout();
		gbl_panelRC.columnWidths = new int[] { 0, 0 };
		gbl_panelRC.rowHeights = new int[] { 0, 0, 0 };
		gbl_panelRC.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_panelRC.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panelRC.setLayout(gbl_panelRC);

		label_12 = new JLabel("RC [15]");
		label_12.setFont(new Font("Arial", Font.PLAIN, 12));
		GridBagConstraints gbc_label_12 = new GridBagConstraints();
		gbc_label_12.insets = new Insets(0, 0, 5, 0);
		gbc_label_12.gridx = 0;
		gbc_label_12.gridy = 0;
		panelRC.add(label_12, gbc_label_12);

		lblRawRC = new JLabel("00");
		lblRawRC.setForeground(Color.BLUE);
		lblRawRC.setFont(new Font("Courier New", Font.BOLD, 15));
		GridBagConstraints gbc_lblRawRC = new GridBagConstraints();
		gbc_lblRawRC.gridx = 0;
		gbc_lblRawRC.gridy = 1;
		panelRC.add(lblRawRC, gbc_lblRawRC);

		panelAllocationVector = new JPanel();
		panelAllocationVector.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GridBagConstraints gbc_panelAllocationVector = new GridBagConstraints();
		gbc_panelAllocationVector.fill = GridBagConstraints.VERTICAL;
		gbc_panelAllocationVector.gridx = 0;
		gbc_panelAllocationVector.gridy = 1;
		panelDirectory.add(panelAllocationVector, gbc_panelAllocationVector);
		GridBagLayout gbl_panelAllocationVector = new GridBagLayout();
		gbl_panelAllocationVector.columnWidths = new int[] { 0, 0 };
		gbl_panelAllocationVector.rowHeights = new int[] { 0, 0, 0 };
		gbl_panelAllocationVector.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_panelAllocationVector.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panelAllocationVector.setLayout(gbl_panelAllocationVector);

		label_14 = new JLabel("Allocation Vector [16-31]");
		label_14.setFont(new Font("Arial", Font.PLAIN, 12));
		GridBagConstraints gbc_label_14 = new GridBagConstraints();
		gbc_label_14.insets = new Insets(0, 0, 5, 0);
		gbc_label_14.gridx = 0;
		gbc_label_14.gridy = 0;
		panelAllocationVector.add(label_14, gbc_label_14);

		lblRawAllocation = new JLabel("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");
		lblRawAllocation.setForeground(Color.BLUE);
		lblRawAllocation.setFont(new Font("Courier New", Font.BOLD, 15));
		GridBagConstraints gbc_lblRawAllocation = new GridBagConstraints();
		gbc_lblRawAllocation.gridx = 0;
		gbc_lblRawAllocation.gridy = 1;
		panelAllocationVector.add(lblRawAllocation, gbc_lblRawAllocation);

		scrollDirectoryTable = new JScrollPane();
		scrollDirectoryTable.setPreferredSize(new Dimension(531, 0));
		GridBagConstraints gbc_scrollDirectoryTable = new GridBagConstraints();
		gbc_scrollDirectoryTable.fill = GridBagConstraints.VERTICAL;
		gbc_scrollDirectoryTable.gridx = 0;
		gbc_scrollDirectoryTable.gridy = 1;
		tabDirectory.add(scrollDirectoryTable, gbc_scrollDirectoryTable);

		JPanel tabFile = new JPanel();
		tabbedPane.addTab("File View", null, tabFile, null);
		GridBagLayout gbl_tabFile = new GridBagLayout();
		gbl_tabFile.columnWidths = new int[] { 0, 0 };
		gbl_tabFile.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gbl_tabFile.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_tabFile.rowWeights = new double[] { 0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		tabFile.setLayout(gbl_tabFile);

		panelFileSelection = new JPanel();
		GridBagConstraints gbc_panelFileSelection = new GridBagConstraints();
		gbc_panelFileSelection.insets = new Insets(0, 0, 5, 0);
		gbc_panelFileSelection.gridx = 0;
		gbc_panelFileSelection.gridy = 0;
		tabFile.add(panelFileSelection, gbc_panelFileSelection);
		GridBagLayout gbl_panelFileSelection = new GridBagLayout();
		gbl_panelFileSelection.columnWidths = new int[] { 200, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panelFileSelection.rowHeights = new int[] { 0 };
		gbl_panelFileSelection.columnWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		gbl_panelFileSelection.rowWeights = new double[] { 0.0 };
		panelFileSelection.setLayout(gbl_panelFileSelection);

		cbFileNames = new JComboBox<FileCpmModel>();
		cbFileNames.setPreferredSize(new Dimension(200, 20));
		GridBagConstraints gbc_cbFileNames = new GridBagConstraints();
		gbc_cbFileNames.insets = new Insets(0, 0, 0, 5);
		gbc_cbFileNames.fill = GridBagConstraints.HORIZONTAL;
		gbc_cbFileNames.gridx = 0;
		gbc_cbFileNames.gridy = 0;
		panelFileSelection.add(cbFileNames, gbc_cbFileNames);
		cbFileNames.setActionCommand(CB_FILE_NAMES);
		cbFileNames.setName(CB_FILE_NAMES);
		cbFileNames.addActionListener(diskUtilityAdapter);
		cbFileNames.setEnabled(true);
		cbFileNames.setEditable(false);
		cbFileNames.setMinimumSize(new Dimension(100, 20));

		horizontalStrut_3 = Box.createHorizontalStrut(20);
		horizontalStrut_3.setPreferredSize(new Dimension(30, 0));
		GridBagConstraints gbc_horizontalStrut_3 = new GridBagConstraints();
		gbc_horizontalStrut_3.insets = new Insets(0, 0, 0, 5);
		gbc_horizontalStrut_3.gridx = 1;
		gbc_horizontalStrut_3.gridy = 0;
		panelFileSelection.add(horizontalStrut_3, gbc_horizontalStrut_3);

		lblRecordCount = new JLabel("0");
		lblRecordCount.setFont(new Font("Arial", Font.BOLD, 15));
		GridBagConstraints gbc_lblRecordCount = new GridBagConstraints();
		gbc_lblRecordCount.insets = new Insets(0, 0, 0, 5);
		gbc_lblRecordCount.gridx = 2;
		gbc_lblRecordCount.gridy = 0;
		panelFileSelection.add(lblRecordCount, gbc_lblRecordCount);

		horizontalStrut_4 = Box.createHorizontalStrut(20);
		horizontalStrut_4.setPreferredSize(new Dimension(10, 0));
		GridBagConstraints gbc_horizontalStrut_4 = new GridBagConstraints();
		gbc_horizontalStrut_4.insets = new Insets(0, 0, 0, 5);
		gbc_horizontalStrut_4.gridx = 3;
		gbc_horizontalStrut_4.gridy = 0;
		panelFileSelection.add(horizontalStrut_4, gbc_horizontalStrut_4);

		label_1 = new JLabel("Record Count");
		label_1.setFont(new Font("Arial", Font.PLAIN, 13));
		GridBagConstraints gbc_label_1 = new GridBagConstraints();
		gbc_label_1.insets = new Insets(0, 0, 0, 5);
		gbc_label_1.gridx = 4;
		gbc_label_1.gridy = 0;
		panelFileSelection.add(label_1, gbc_label_1);

		horizontalStrut_5 = Box.createHorizontalStrut(20);
		horizontalStrut_5.setPreferredSize(new Dimension(60, 0));
		GridBagConstraints gbc_horizontalStrut_5 = new GridBagConstraints();
		gbc_horizontalStrut_5.insets = new Insets(0, 0, 0, 5);
		gbc_horizontalStrut_5.gridx = 5;
		gbc_horizontalStrut_5.gridy = 0;
		panelFileSelection.add(horizontalStrut_5, gbc_horizontalStrut_5);

		lblReadOnly = new JLabel("Read Only");
		lblReadOnly.setForeground(Color.RED);
		lblReadOnly.setFont(new Font("Arial", Font.BOLD, 15));
		GridBagConstraints gbc_lblReadOnly = new GridBagConstraints();
		gbc_lblReadOnly.insets = new Insets(0, 0, 0, 5);
		gbc_lblReadOnly.gridx = 6;
		gbc_lblReadOnly.gridy = 0;
		panelFileSelection.add(lblReadOnly, gbc_lblReadOnly);

		horizontalStrut_6 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_6 = new GridBagConstraints();
		gbc_horizontalStrut_6.insets = new Insets(0, 0, 0, 5);
		gbc_horizontalStrut_6.gridx = 7;
		gbc_horizontalStrut_6.gridy = 0;
		panelFileSelection.add(horizontalStrut_6, gbc_horizontalStrut_6);

		lblSystemFile = new JLabel("System File");
		lblSystemFile.setForeground(Color.RED);
		lblSystemFile.setFont(new Font("Arial", Font.BOLD, 15));
		GridBagConstraints gbc_lblSystemFile = new GridBagConstraints();
		gbc_lblSystemFile.gridx = 8;
		gbc_lblSystemFile.gridy = 0;
		panelFileSelection.add(lblSystemFile, gbc_lblSystemFile);

		panelFile0 = new JPanel();
		GridBagConstraints gbc_panelFile0 = new GridBagConstraints();
		gbc_panelFile0.insets = new Insets(0, 0, 5, 0);
		gbc_panelFile0.fill = GridBagConstraints.VERTICAL;
		gbc_panelFile0.gridx = 0;
		gbc_panelFile0.gridy = 1;
		tabFile.add(panelFile0, gbc_panelFile0);
		GridBagLayout gbl_panelFile0 = new GridBagLayout();
		gbl_panelFile0.columnWidths = new int[] { 0, 0, 0 };
		gbl_panelFile0.rowHeights = new int[] { 0, 0 };
		gbl_panelFile0.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_panelFile0.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		panelFile0.setLayout(gbl_panelFile0);

		horizontalStrut_7 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_7 = new GridBagConstraints();
		gbc_horizontalStrut_7.insets = new Insets(0, 0, 0, 5);
		gbc_horizontalStrut_7.gridx = 0;
		gbc_horizontalStrut_7.gridy = 0;
		panelFile0.add(horizontalStrut_7, gbc_horizontalStrut_7);

		panelFileHex = new HexEditPanelSimple();
		GridBagConstraints gbc_panelFileHex = new GridBagConstraints();
		// gbc_panelFileHex.insets = new Insets(0, 0, 5, 0);
		gbc_panelFileHex.fill = GridBagConstraints.BOTH;
		gbc_panelFileHex.gridx = 1;
		gbc_panelFileHex.gridy = 0;
		panelFile0.add(panelFileHex, gbc_panelFileHex);
		// GridBagLayout gbl_panelFileHex = new GridBagLayout();
		// gbl_panelFileHex.columnWidths = new int[] { 0 };
		// gbl_panelFileHex.rowHeights = new int[] { 0 };
		// gbl_panelFileHex.columnWeights = new double[] { Double.MIN_VALUE };
		// gbl_panelFileHex.rowWeights = new double[] { Double.MIN_VALUE };
		// panelFileHex.setLayout(gbl_panelFileHex);

		JPanel tabPhysical = new JPanel();
		tabbedPane.addTab("Physical View", null, tabPhysical, null);
		GridBagLayout gbl_tabPhysical = new GridBagLayout();
		gbl_tabPhysical.columnWidths = new int[] { 0, 0, 0 };
		gbl_tabPhysical.rowHeights = new int[] { 0, 0, 0, 40, 10, 0 };
		gbl_tabPhysical.columnWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		gbl_tabPhysical.rowWeights = new double[] { 0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		tabPhysical.setLayout(gbl_tabPhysical);

		JPanel panelHeadTrackSector = new JPanel();
		GridBagConstraints gbc_panelHeadTrackSector = new GridBagConstraints();
		gbc_panelHeadTrackSector.insets = new Insets(0, 0, 5, 5);
		gbc_panelHeadTrackSector.fill = GridBagConstraints.BOTH;
		gbc_panelHeadTrackSector.gridx = 0;
		gbc_panelHeadTrackSector.gridy = 0;
		tabPhysical.add(panelHeadTrackSector, gbc_panelHeadTrackSector);
		GridBagLayout gbl_panelHeadTrackSector = new GridBagLayout();
		gbl_panelHeadTrackSector.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panelHeadTrackSector.rowHeights = new int[] { 0, 0 };
		gbl_panelHeadTrackSector.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panelHeadTrackSector.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panelHeadTrackSector.setLayout(gbl_panelHeadTrackSector);

		JLabel lblHead = new JLabel("Head");
		GridBagConstraints gbc_lblHead = new GridBagConstraints();
		gbc_lblHead.insets = new Insets(0, 0, 0, 5);
		gbc_lblHead.gridx = 1;
		gbc_lblHead.gridy = 0;
		panelHeadTrackSector.add(lblHead, gbc_lblHead);

		hdnHead = new HDNumberBox();
		hdnHead.setName(HDN_HEAD);
		hdnHead.addHDNumberValueChangedListener(diskUtilityAdapter);
		hdnHead.setPreferredSize(new Dimension(50, 20));
		GridBagConstraints gbc_hdnHead = new GridBagConstraints();
		gbc_hdnHead.insets = new Insets(0, 0, 0, 5);
		gbc_hdnHead.gridx = 3;
		gbc_hdnHead.gridy = 0;
		panelHeadTrackSector.add(hdnHead, gbc_hdnHead);

		JLabel lblTrack = new JLabel("Track");
		GridBagConstraints gbc_lblTrack = new GridBagConstraints();
		gbc_lblTrack.insets = new Insets(0, 0, 0, 5);
		gbc_lblTrack.gridx = 5;
		gbc_lblTrack.gridy = 0;
		panelHeadTrackSector.add(lblTrack, gbc_lblTrack);

		hdnTrack = new HDNumberBox();
		hdnTrack.setName(HDN_TRACK);
		hdnTrack.addHDNumberValueChangedListener(diskUtilityAdapter);
		hdnTrack.setPreferredSize(new Dimension(50, 20));
		GridBagConstraints gbc_hdnTrack = new GridBagConstraints();
		gbc_hdnTrack.insets = new Insets(0, 0, 0, 5);
		gbc_hdnTrack.gridx = 7;
		gbc_hdnTrack.gridy = 0;
		panelHeadTrackSector.add(hdnTrack, gbc_hdnTrack);

		JLabel lblSector = new JLabel("Sector");
		GridBagConstraints gbc_lblSector = new GridBagConstraints();
		gbc_lblSector.insets = new Insets(0, 0, 0, 5);
		gbc_lblSector.gridx = 9;
		gbc_lblSector.gridy = 0;
		panelHeadTrackSector.add(lblSector, gbc_lblSector);

		hdnSector = new HDNumberBox();
		hdnSector.setName(HDN_SECTOR);
		hdnSector.addHDNumberValueChangedListener(diskUtilityAdapter);
		hdnSector.setPreferredSize(new Dimension(50, 20));
		GridBagConstraints gbc_hdnSector = new GridBagConstraints();
		gbc_hdnSector.insets = new Insets(0, 0, 0, 5);
		gbc_hdnSector.gridx = 11;
		gbc_hdnSector.gridy = 0;
		panelHeadTrackSector.add(hdnSector, gbc_hdnSector);

		panelPhysical0 = new JPanel();
		GridBagConstraints gbc_panelPhysical0 = new GridBagConstraints();
		gbc_panelPhysical0.insets = new Insets(0, 0, 5, 5);
		gbc_panelPhysical0.fill = GridBagConstraints.VERTICAL;
		gbc_panelPhysical0.gridx = 0;
		gbc_panelPhysical0.gridy = 1;
		tabPhysical.add(panelPhysical0, gbc_panelPhysical0);
		GridBagLayout gbl_panelPhysical0 = new GridBagLayout();
		gbl_panelPhysical0.columnWidths = new int[] { 0, 0, 0 };
		gbl_panelPhysical0.rowHeights = new int[] { 0, 0 };
		gbl_panelPhysical0.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_panelPhysical0.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		panelPhysical0.setLayout(gbl_panelPhysical0);

		horizontalStrut_2 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_2 = new GridBagConstraints();
		gbc_horizontalStrut_2.insets = new Insets(0, 0, 0, 5);
		gbc_horizontalStrut_2.gridx = 0;
		gbc_horizontalStrut_2.gridy = 0;
		panelPhysical0.add(horizontalStrut_2, gbc_horizontalStrut_2);

		panelSectorDisplay = new HexEditPanelSimple();
		GridBagConstraints gbc_panelSectorDisplay = new GridBagConstraints();
		gbc_panelSectorDisplay.fill = GridBagConstraints.BOTH;
		gbc_panelSectorDisplay.gridx = 1;
		gbc_panelSectorDisplay.gridy = 0;
		panelPhysical0.add(panelSectorDisplay, gbc_panelSectorDisplay);

		JPanel panelSeek = new JPanel();
		GridBagConstraints gbc_panelSeek = new GridBagConstraints();
		gbc_panelSeek.insets = new Insets(0, 0, 5, 5);
		gbc_panelSeek.fill = GridBagConstraints.HORIZONTAL;
		gbc_panelSeek.gridx = 0;
		gbc_panelSeek.gridy = 3;
		tabPhysical.add(panelSeek, gbc_panelSeek);
		GridBagLayout gbl_panelSeek = new GridBagLayout();
		gbl_panelSeek.columnWidths = new int[] { 0, 0 };
		gbl_panelSeek.rowHeights = new int[] { 0, 0 };
		gbl_panelSeek.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panelSeek.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panelSeek.setLayout(gbl_panelSeek);

		hdSeekPanel = new HDSeekPanel(new SpinnerNumberModel(0, 0, 0, 1));
		hdSeekPanel.setPreferredSize(new Dimension(260, 30));
		hdSeekPanel.setMinimumSize(new Dimension(280, 39));
		hdSeekPanel.setName(HD_SEEK_PANEL);
		hdSeekPanel.addHDNumberValueChangedListener(diskUtilityAdapter);
		GridBagConstraints gbc_seekPanel = new GridBagConstraints();
		gbc_seekPanel.gridx = 0;
		gbc_seekPanel.gridy = 0;
		panelSeek.add(hdSeekPanel, gbc_seekPanel);
		// seekPanel.setValue(0);
		// hdSeekPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null,
		// null, null, null));
		GridBagLayout gbl_seekPanel = new GridBagLayout();
		gbl_seekPanel.columnWidths = new int[] { 0 };
		gbl_seekPanel.rowHeights = new int[] { 0 };
		gbl_seekPanel.columnWeights = new double[] { Double.MIN_VALUE };
		gbl_seekPanel.rowWeights = new double[] { Double.MIN_VALUE };
		hdSeekPanel.setLayout(gbl_seekPanel);

		tabImportExport = new JPanel();
		tabbedPane.addTab("Import/Export", null, tabImportExport, null);
		GridBagLayout gbl_tabImportExport = new GridBagLayout();
		gbl_tabImportExport.columnWidths = new int[] { 0, 0 };
		gbl_tabImportExport.rowHeights = new int[] { 0, 0, 0 };
		gbl_tabImportExport.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_tabImportExport.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		tabImportExport.setLayout(gbl_tabImportExport);

		panelMetrics0 = new JPanel();
		panelMetrics0.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		GridBagConstraints gbc_panelMetrics0 = new GridBagConstraints();
		gbc_panelMetrics0.insets = new Insets(0, 0, 5, 0);
		gbc_panelMetrics0.fill = GridBagConstraints.BOTH;
		gbc_panelMetrics0.gridx = 0;
		gbc_panelMetrics0.gridy = 0;
		tabImportExport.add(panelMetrics0, gbc_panelMetrics0);
		GridBagLayout gbl_panelMetrics0 = new GridBagLayout();
		gbl_panelMetrics0.columnWidths = new int[] { 0, 0 };
		gbl_panelMetrics0.rowHeights = new int[] { 0, 0 };
		gbl_panelMetrics0.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panelMetrics0.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panelMetrics0.setLayout(gbl_panelMetrics0);

		panelMetrics = new JPanel();
		GridBagConstraints gbc_panelMetrics = new GridBagConstraints();
		gbc_panelMetrics.fill = GridBagConstraints.VERTICAL;
		gbc_panelMetrics.gridx = 0;
		gbc_panelMetrics.gridy = 0;
		panelMetrics0.add(panelMetrics, gbc_panelMetrics);
		panelMetrics.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, true),
				"Disk & File System Metrics", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, null, null));
		GridBagLayout gbl_panelMetrics = new GridBagLayout();
		gbl_panelMetrics.columnWidths = new int[] { 0, 0, 0, 0, 0 };
		gbl_panelMetrics.rowHeights = new int[] { 0, 0 };
		gbl_panelMetrics.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_panelMetrics.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		panelMetrics.setLayout(gbl_panelMetrics);

		panelDiskGeometry = new JPanel();
		panelDiskGeometry.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, true), "Disk Geometry",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		GridBagConstraints gbc_panelDiskGeometry = new GridBagConstraints();
		gbc_panelDiskGeometry.insets = new Insets(0, 0, 0, 5);
		gbc_panelDiskGeometry.anchor = GridBagConstraints.WEST;
		gbc_panelDiskGeometry.fill = GridBagConstraints.VERTICAL;
		gbc_panelDiskGeometry.gridx = 1;
		gbc_panelDiskGeometry.gridy = 0;
		panelMetrics.add(panelDiskGeometry, gbc_panelDiskGeometry);
		GridBagLayout gbl_panelDiskGeometry = new GridBagLayout();
		gbl_panelDiskGeometry.columnWidths = new int[] { 0, 50, 10, 150, 0 };
		gbl_panelDiskGeometry.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gbl_panelDiskGeometry.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panelDiskGeometry.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panelDiskGeometry.setLayout(gbl_panelDiskGeometry);

		lblHeads = new JLabel("0");
		lblHeads.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblHeads.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_lblHeads = new GridBagConstraints();
		gbc_lblHeads.anchor = GridBagConstraints.EAST;
		gbc_lblHeads.insets = new Insets(0, 0, 5, 5);
		gbc_lblHeads.gridx = 1;
		gbc_lblHeads.gridy = 0;
		panelDiskGeometry.add(lblHeads, gbc_lblHeads);

		lblNewLabel_1 = new JLabel("Heads");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_1.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_1.gridx = 3;
		gbc_lblNewLabel_1.gridy = 0;
		panelDiskGeometry.add(lblNewLabel_1, gbc_lblNewLabel_1);

		lblTracksPerHead = new JLabel("0");
		lblTracksPerHead.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTracksPerHead.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints gbc_lblTracksPerHead = new GridBagConstraints();
		gbc_lblTracksPerHead.anchor = GridBagConstraints.EAST;
		gbc_lblTracksPerHead.insets = new Insets(0, 0, 5, 5);
		gbc_lblTracksPerHead.gridx = 1;
		gbc_lblTracksPerHead.gridy = 1;
		panelDiskGeometry.add(lblTracksPerHead, gbc_lblTracksPerHead);

		lblTracksPerHead_1 = new JLabel("Tracks/Head");
		lblTracksPerHead_1.setHorizontalAlignment(SwingConstants.LEFT);
		lblTracksPerHead_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_lblTracksPerHead_1 = new GridBagConstraints();
		gbc_lblTracksPerHead_1.insets = new Insets(0, 0, 5, 0);
		gbc_lblTracksPerHead_1.anchor = GridBagConstraints.WEST;
		gbc_lblTracksPerHead_1.gridx = 3;
		gbc_lblTracksPerHead_1.gridy = 1;
		panelDiskGeometry.add(lblTracksPerHead_1, gbc_lblTracksPerHead_1);

		lblSectorsPerTrack = new JLabel("0");
		lblSectorsPerTrack.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSectorsPerTrack.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints gbc_lblSectorsPerTrack = new GridBagConstraints();
		gbc_lblSectorsPerTrack.anchor = GridBagConstraints.EAST;
		gbc_lblSectorsPerTrack.insets = new Insets(0, 0, 5, 5);
		gbc_lblSectorsPerTrack.gridx = 1;
		gbc_lblSectorsPerTrack.gridy = 2;
		panelDiskGeometry.add(lblSectorsPerTrack, gbc_lblSectorsPerTrack);

		lblNewLabel = new JLabel("Sectors/Track");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.gridx = 3;
		gbc_lblNewLabel.gridy = 2;
		panelDiskGeometry.add(lblNewLabel, gbc_lblNewLabel);

		lblTotalTracks = new JLabel("0");
		lblTotalTracks.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTotalTracks.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints gbc_lblTotalTracks = new GridBagConstraints();
		gbc_lblTotalTracks.anchor = GridBagConstraints.EAST;
		gbc_lblTotalTracks.insets = new Insets(0, 0, 5, 5);
		gbc_lblTotalTracks.gridx = 1;
		gbc_lblTotalTracks.gridy = 3;
		panelDiskGeometry.add(lblTotalTracks, gbc_lblTotalTracks);

		label4 = new JLabel("Total Tracks");
		label4.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_label4 = new GridBagConstraints();
		gbc_label4.insets = new Insets(0, 0, 5, 0);
		gbc_label4.anchor = GridBagConstraints.WEST;
		gbc_label4.gridx = 3;
		gbc_label4.gridy = 3;
		panelDiskGeometry.add(label4, gbc_label4);

		lblTotalSectors = new JLabel("0");
		lblTotalSectors.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTotalSectors.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints gbc_lblTotalSectors = new GridBagConstraints();
		gbc_lblTotalSectors.anchor = GridBagConstraints.EAST;
		gbc_lblTotalSectors.insets = new Insets(0, 0, 0, 5);
		gbc_lblTotalSectors.gridx = 1;
		gbc_lblTotalSectors.gridy = 4;
		panelDiskGeometry.add(lblTotalSectors, gbc_lblTotalSectors);

		lblTotalSectors_1 = new JLabel("Total Sectors");
		lblTotalSectors_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_lblTotalSectors_1 = new GridBagConstraints();
		gbc_lblTotalSectors_1.anchor = GridBagConstraints.WEST;
		gbc_lblTotalSectors_1.gridx = 3;
		gbc_lblTotalSectors_1.gridy = 4;
		panelDiskGeometry.add(lblTotalSectors_1, gbc_lblTotalSectors_1);

		panelFileSystemParameters = new JPanel();
		GridBagConstraints gbc_panelFileSystemParameters = new GridBagConstraints();
		gbc_panelFileSystemParameters.insets = new Insets(0, 0, 0, 5);
		gbc_panelFileSystemParameters.gridx = 2;
		gbc_panelFileSystemParameters.gridy = 0;
		panelMetrics.add(panelFileSystemParameters, gbc_panelFileSystemParameters);
		panelFileSystemParameters.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, true),
				"File System Parameters", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagLayout gbl_panelFileSystemParameters = new GridBagLayout();
		gbl_panelFileSystemParameters.columnWidths = new int[] { 0, 50, 10, 150, 0 };
		gbl_panelFileSystemParameters.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gbl_panelFileSystemParameters.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panelFileSystemParameters.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panelFileSystemParameters.setLayout(gbl_panelFileSystemParameters);

		lblTracksBeforeDirectory = new JLabel("0");
		lblTracksBeforeDirectory.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTracksBeforeDirectory.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints gbc_lblTracksBeforeDirectory = new GridBagConstraints();
		gbc_lblTracksBeforeDirectory.anchor = GridBagConstraints.EAST;
		gbc_lblTracksBeforeDirectory.insets = new Insets(0, 0, 5, 5);
		gbc_lblTracksBeforeDirectory.gridx = 1;
		gbc_lblTracksBeforeDirectory.gridy = 0;
		panelFileSystemParameters.add(lblTracksBeforeDirectory, gbc_lblTracksBeforeDirectory);

		JLabel label_3 = new JLabel("Tracks Before Directory");
		label_3.setHorizontalAlignment(SwingConstants.LEFT);
		label_3.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_label_3 = new GridBagConstraints();
		gbc_label_3.anchor = GridBagConstraints.WEST;
		gbc_label_3.insets = new Insets(0, 0, 5, 0);
		gbc_label_3.gridx = 3;
		gbc_label_3.gridy = 0;
		panelFileSystemParameters.add(label_3, gbc_label_3);

		lblLogicalBlockSizeInSectors = new JLabel("0");
		lblLogicalBlockSizeInSectors.setHorizontalAlignment(SwingConstants.RIGHT);
		lblLogicalBlockSizeInSectors.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints gbc_lblLogicalBlockSizeInSectors = new GridBagConstraints();
		gbc_lblLogicalBlockSizeInSectors.anchor = GridBagConstraints.EAST;
		gbc_lblLogicalBlockSizeInSectors.insets = new Insets(0, 0, 5, 5);
		gbc_lblLogicalBlockSizeInSectors.gridx = 1;
		gbc_lblLogicalBlockSizeInSectors.gridy = 1;
		panelFileSystemParameters.add(lblLogicalBlockSizeInSectors, gbc_lblLogicalBlockSizeInSectors);

		JLabel lblSectorsPerBlock = new JLabel("Sectors Per Block");
		lblSectorsPerBlock.setHorizontalAlignment(SwingConstants.LEFT);
		lblSectorsPerBlock.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_lblSectorsPerBlock = new GridBagConstraints();
		gbc_lblSectorsPerBlock.anchor = GridBagConstraints.WEST;
		gbc_lblSectorsPerBlock.insets = new Insets(0, 0, 5, 0);
		gbc_lblSectorsPerBlock.gridx = 3;
		gbc_lblSectorsPerBlock.gridy = 1;
		panelFileSystemParameters.add(lblSectorsPerBlock, gbc_lblSectorsPerBlock);

		lblMaxDirectoryEntry = new JLabel("0");
		lblMaxDirectoryEntry.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMaxDirectoryEntry.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints gbc_lblMaxDirectoryEntry = new GridBagConstraints();
		gbc_lblMaxDirectoryEntry.anchor = GridBagConstraints.EAST;
		gbc_lblMaxDirectoryEntry.insets = new Insets(0, 0, 5, 5);
		gbc_lblMaxDirectoryEntry.gridx = 1;
		gbc_lblMaxDirectoryEntry.gridy = 2;
		panelFileSystemParameters.add(lblMaxDirectoryEntry, gbc_lblMaxDirectoryEntry);

		JLabel label_7 = new JLabel("Max Directory Entry");
		label_7.setHorizontalAlignment(SwingConstants.LEFT);
		label_7.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_label_7 = new GridBagConstraints();
		gbc_label_7.anchor = GridBagConstraints.WEST;
		gbc_label_7.insets = new Insets(0, 0, 5, 0);
		gbc_label_7.gridx = 3;
		gbc_label_7.gridy = 2;
		panelFileSystemParameters.add(label_7, gbc_label_7);

		lblMaxBlockNumber = new JLabel("0");
		lblMaxBlockNumber.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints gbc_lblMaxBlockNumber = new GridBagConstraints();
		gbc_lblMaxBlockNumber.anchor = GridBagConstraints.EAST;
		gbc_lblMaxBlockNumber.insets = new Insets(0, 0, 0, 5);
		gbc_lblMaxBlockNumber.gridx = 1;
		gbc_lblMaxBlockNumber.gridy = 3;
		panelFileSystemParameters.add(lblMaxBlockNumber, gbc_lblMaxBlockNumber);

		JLabel label_9 = new JLabel("Max Block Number");
		label_9.setHorizontalAlignment(SwingConstants.LEFT);
		label_9.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_label_9 = new GridBagConstraints();
		gbc_label_9.anchor = GridBagConstraints.WEST;
		gbc_label_9.gridx = 3;
		gbc_label_9.gridy = 3;
		panelFileSystemParameters.add(label_9, gbc_label_9);

		panelCopy0 = new JPanel();
		panelCopy0.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		GridBagConstraints gbc_panelCopy0 = new GridBagConstraints();
		gbc_panelCopy0.fill = GridBagConstraints.BOTH;
		gbc_panelCopy0.gridx = 0;
		gbc_panelCopy0.gridy = 1;
		tabImportExport.add(panelCopy0, gbc_panelCopy0);
		GridBagLayout gbl_panelCopy0 = new GridBagLayout();
		gbl_panelCopy0.columnWidths = new int[] { 0, 0, 0 };
		gbl_panelCopy0.rowHeights = new int[] { 0, 0, 0 };
		gbl_panelCopy0.columnWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		gbl_panelCopy0.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panelCopy0.setLayout(gbl_panelCopy0);

		panel = new JPanel();
		panel.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, true), "Import / Export Files",
				TitledBorder.CENTER, TitledBorder.ABOVE_TOP, null, null));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.anchor = GridBagConstraints.NORTH;
		gbc_panel.insets = new Insets(0, 0, 5, 5);
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		panelCopy0.add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 0, 0, 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		lblNewLabel_3 = new JLabel("Native File :");
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_3.gridx = 1;
		gbc_lblNewLabel_3.gridy = 1;
		panel.add(lblNewLabel_3, gbc_lblNewLabel_3);

		btnNativeFile = new JButton("...");
		btnNativeFile.setName(BTN_NATIVE_FILE);
		btnNativeFile.addActionListener(diskUtilityAdapter);
		btnNativeFile.setMaximumSize(new Dimension(25, 23));
		btnNativeFile.setMinimumSize(new Dimension(25, 23));
		btnNativeFile.setPreferredSize(new Dimension(25, 23));
		GridBagConstraints gbc_btnNativeFile = new GridBagConstraints();
		gbc_btnNativeFile.insets = new Insets(0, 0, 5, 5);
		gbc_btnNativeFile.gridx = 2;
		gbc_btnNativeFile.gridy = 1;
		panel.add(btnNativeFile, gbc_btnNativeFile);

		txtNativeFileInOut = new JTextField();
		txtNativeFileInOut.setEditable(false);
		GridBagConstraints gbc_txtNativeFileInOut = new GridBagConstraints();
		gbc_txtNativeFileInOut.insets = new Insets(0, 0, 5, 0);
		gbc_txtNativeFileInOut.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtNativeFileInOut.gridx = 3;
		gbc_txtNativeFileInOut.gridy = 1;
		panel.add(txtNativeFileInOut, gbc_txtNativeFileInOut);
		txtNativeFileInOut.setColumns(10);

		lblCpmFile = new JLabel("CPM File :");
		GridBagConstraints gbc_lblCpmFile = new GridBagConstraints();
		gbc_lblCpmFile.anchor = GridBagConstraints.EAST;
		gbc_lblCpmFile.insets = new Insets(0, 0, 5, 5);
		gbc_lblCpmFile.gridx = 1;
		gbc_lblCpmFile.gridy = 2;
		panel.add(lblCpmFile, gbc_lblCpmFile);

		cbCpmFileInOut = new JComboBox<String>();
		cbCpmFileInOut.setName(CB_CPM_FILE_IN_OUT);
		cbCpmFileInOut.addActionListener(diskUtilityAdapter);
		cbCpmFileInOut.setEditable(true);
		cbCpmFileInOut.setPreferredSize(new Dimension(200, 20));
		cbCpmFileInOut.setMaximumSize(new Dimension(200, 20));
		cbCpmFileInOut.setMinimumSize(new Dimension(200, 20));
		GridBagConstraints gbc_cbCpmFileInOut = new GridBagConstraints();
		gbc_cbCpmFileInOut.anchor = GridBagConstraints.WEST;
		gbc_cbCpmFileInOut.insets = new Insets(0, 0, 5, 0);
		gbc_cbCpmFileInOut.gridx = 3;
		gbc_cbCpmFileInOut.gridy = 2;
		panel.add(cbCpmFileInOut, gbc_cbCpmFileInOut);

		btnExport = new JButton("Export to Native File");
		btnExport.setName(BTN_EXPORT);
		btnExport.addActionListener(diskUtilityAdapter);
		btnExport.setEnabled(false);
		GridBagConstraints gbc_btnExport = new GridBagConstraints();
		gbc_btnExport.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnExport.insets = new Insets(0, 0, 5, 5);
		gbc_btnExport.gridx = 1;
		gbc_btnExport.gridy = 3;
		panel.add(btnExport, gbc_btnExport);

		btnImport = new JButton("Import from Native File");
		btnImport.setName(BTN_IMPORT);
		btnImport.addActionListener(diskUtilityAdapter);
		btnImport.setEnabled(false);
		GridBagConstraints gbc_btnImport = new GridBagConstraints();
		gbc_btnImport.insets = new Insets(0, 0, 0, 5);
		gbc_btnImport.gridx = 1;
		gbc_btnImport.gridy = 4;
		panel.add(btnImport, gbc_btnImport);

		tabCatalog = new JPanel();
		tabbedPane.addTab("Catalog", null, tabCatalog, null);
		GridBagLayout gbl_tabCatalog = new GridBagLayout();
		gbl_tabCatalog.columnWidths = new int[] { 0, 0 };
		gbl_tabCatalog.rowHeights = new int[] { 0, 0, 0 };
		gbl_tabCatalog.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_tabCatalog.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		tabCatalog.setLayout(gbl_tabCatalog);

		panelCatalogTop = new JPanel();
		panelCatalogTop.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GridBagConstraints gbc_panelCatalogTop = new GridBagConstraints();
		gbc_panelCatalogTop.insets = new Insets(0, 0, 5, 0);
		gbc_panelCatalogTop.fill = GridBagConstraints.BOTH;
		gbc_panelCatalogTop.gridx = 0;
		gbc_panelCatalogTop.gridy = 0;
		tabCatalog.add(panelCatalogTop, gbc_panelCatalogTop);
		GridBagLayout gbl_panelCatalogTop = new GridBagLayout();
		gbl_panelCatalogTop.columnWidths = new int[] { 0, 0, 0, 0, 0 };
		gbl_panelCatalogTop.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gbl_panelCatalogTop.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_panelCatalogTop.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panelCatalogTop.setLayout(gbl_panelCatalogTop);

		JButton btnChangeDiskType = new JButton("Change Disk Type");
		btnChangeDiskType.setName(BTN_CHANGE_DISK_TYPE);
		btnChangeDiskType.addActionListener(diskUtilityAdapter);
		GridBagConstraints gbc_btnChangeDiskType = new GridBagConstraints();
		gbc_btnChangeDiskType.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnChangeDiskType.insets = new Insets(0, 0, 5, 5);
		gbc_btnChangeDiskType.gridx = 0;
		gbc_btnChangeDiskType.gridy = 0;
		panelCatalogTop.add(btnChangeDiskType, gbc_btnChangeDiskType);

		lblDiskType = new JLabel("F3HD");
		lblDiskType.setFont(new Font("Tahoma", Font.PLAIN, 14));
		GridBagConstraints gbc_lblDiskType = new GridBagConstraints();
		gbc_lblDiskType.anchor = GridBagConstraints.SOUTH;
		gbc_lblDiskType.insets = new Insets(0, 0, 5, 5);
		gbc_lblDiskType.gridx = 1;
		gbc_lblDiskType.gridy = 0;
		panelCatalogTop.add(lblDiskType, gbc_lblDiskType);

		JButton btnDiskDirectory = new JButton("Change Disk Directory");
		btnDiskDirectory.setName(BTN_DISK_DIRECTORY);
		btnDiskDirectory.addActionListener(diskUtilityAdapter);
		GridBagConstraints gbc_btnDiskDirectory = new GridBagConstraints();
		gbc_btnDiskDirectory.insets = new Insets(0, 0, 5, 5);
		gbc_btnDiskDirectory.gridx = 0;
		gbc_btnDiskDirectory.gridy = 1;
		panelCatalogTop.add(btnDiskDirectory, gbc_btnDiskDirectory);

		lblDirectory = new JLabel("Directory");
		lblDirectory.setHorizontalAlignment(SwingConstants.CENTER);
		lblDirectory.setForeground(Color.BLUE);
		lblDirectory.setFont(new Font("Tahoma", Font.PLAIN, 14));
		GridBagConstraints gbc_lblDirectory = new GridBagConstraints();
		gbc_lblDirectory.gridwidth = 3;
		gbc_lblDirectory.insets = new Insets(0, 0, 5, 0);
		gbc_lblDirectory.anchor = GridBagConstraints.SOUTH;
		gbc_lblDirectory.gridx = 1;
		gbc_lblDirectory.gridy = 1;
		panelCatalogTop.add(lblDirectory, gbc_lblDirectory);

		rbRecurse = new JRadioButton("Recurse Directory");
		GridBagConstraints gbc_rbRecurse = new GridBagConstraints();
		gbc_rbRecurse.anchor = GridBagConstraints.SOUTH;
		gbc_rbRecurse.insets = new Insets(0, 0, 5, 5);
		gbc_rbRecurse.gridx = 0;
		gbc_rbRecurse.gridy = 2;
		panelCatalogTop.add(rbRecurse, gbc_rbRecurse);

		btnFindFiles = new JButton("Find");
		btnFindFiles.setName(BTN_FIND_FILES);
		btnFindFiles.addActionListener(diskUtilityAdapter);
		btnFindFiles.setEnabled(false);
		GridBagConstraints gbc_btnFindFiles = new GridBagConstraints();
		gbc_btnFindFiles.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnFindFiles.insets = new Insets(0, 0, 5, 5);
		gbc_btnFindFiles.gridx = 0;
		gbc_btnFindFiles.gridy = 3;
		panelCatalogTop.add(btnFindFiles, gbc_btnFindFiles);

		txtFindFileName = new JTextField();
		txtFindFileName.setInputVerifier(new FileNameVerifier());
		GridBagConstraints gbc_txtFindFileName = new GridBagConstraints();
		gbc_txtFindFileName.insets = new Insets(0, 0, 5, 5);
		gbc_txtFindFileName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtFindFileName.gridx = 1;
		gbc_txtFindFileName.gridy = 3;
		panelCatalogTop.add(txtFindFileName, gbc_txtFindFileName);
		txtFindFileName.setColumns(10);

		btnListFiles = new JButton("List Files");
		btnListFiles.setName(BTN_LIST_FILES);
		btnListFiles.addActionListener(diskUtilityAdapter);

		btnPrintResult = new JButton("Print Result");
		btnPrintResult.setName(BTN_PRINT_RESULT);
		btnPrintResult.addActionListener(diskUtilityAdapter);
		btnPrintResult.setVisible(false);
		GridBagConstraints gbc_btnPrintResult = new GridBagConstraints();
		gbc_btnPrintResult.insets = new Insets(0, 0, 5, 5);
		gbc_btnPrintResult.gridx = 2;
		gbc_btnPrintResult.gridy = 3;
		panelCatalogTop.add(btnPrintResult, gbc_btnPrintResult);
		GridBagConstraints gbc_btnListFiles = new GridBagConstraints();
		gbc_btnListFiles.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnListFiles.insets = new Insets(0, 0, 0, 5);
		gbc_btnListFiles.gridx = 0;
		gbc_btnListFiles.gridy = 4;
		panelCatalogTop.add(btnListFiles, gbc_btnListFiles);

		scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		tabCatalog.add(scrollPane, gbc_scrollPane);

		txtCatalog = new JTextPane();
		txtCatalog.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getClickCount() > 1) {
					clearCatalog();
				}
			}
		});
		txtCatalog.setFont(new Font("Courier New", Font.PLAIN, 16));
		scrollPane.setViewportView(txtCatalog);

		lblCatalogHeader = new JLabel("");
		lblCatalogHeader.setHorizontalAlignment(SwingConstants.CENTER);
		lblCatalogHeader.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblCatalogHeader.setForeground(Color.LIGHT_GRAY);
		scrollPane.setColumnHeaderView(lblCatalogHeader);

		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);

		JMenu mnuDisk = new JMenu("Disk");
		menuBar.add(mnuDisk);

		mnuDiskLoad = new JMenuItem("Load Disk ...");
		mnuDiskLoad.setName(MNU_DISK_LOAD_DISK);
		mnuDiskLoad.addActionListener(diskUtilityAdapter);
		mnuDisk.add(mnuDiskLoad);

		JSeparator separator = new JSeparator();
		mnuDisk.add(separator);

		mnuDiskClose = new JMenuItem("Close");
		mnuDiskClose.setName(MNU_DISK_CLOSE);
		mnuDiskClose.addActionListener(diskUtilityAdapter);
		mnuDisk.add(mnuDiskClose);

		JSeparator separator_1 = new JSeparator();
		mnuDisk.add(separator_1);

		mnuDiskSave = new JMenuItem("Save");
		mnuDiskSave.setName(MNU_DISK_SAVE);
		mnuDiskSave.addActionListener(diskUtilityAdapter);
		mnuDisk.add(mnuDiskSave);

		mnuDiskSaveAs = new JMenuItem("Save As ...");
		mnuDiskSaveAs.setName(MNU_DISK_SAVE_AS);
		mnuDiskSaveAs.addActionListener(diskUtilityAdapter);
		mnuDisk.add(mnuDiskSaveAs);

		JSeparator separator_2 = new JSeparator();
		mnuDisk.add(separator_2);

		mnuDiskExit = new JMenuItem("Exit");
		mnuDiskExit.setName(MNU_DISK_EXIT);
		mnuDiskExit.addActionListener(diskUtilityAdapter);
		mnuDisk.add(mnuDiskExit);

		mnuTools = new JMenu("Tools");
		menuBar.add(mnuTools);

		mnuToolsNewDisk = new JMenuItem("New Disk");
		mnuTools.add(mnuToolsNewDisk);
		mnuToolsNewDisk.setName(MNU_TOOLS_NEW_DISK);

		mnuToolsUpdateSystem = new JMenuItem("Update System on Disks...");
		mnuToolsUpdateSystem.setName(MNU_TOOLS_UPDATE_SYSTEM);
		mnuToolsUpdateSystem.addActionListener(diskUtilityAdapter);
		mnuTools.add(mnuToolsUpdateSystem);
		mnuToolsNewDisk.addActionListener(diskUtilityAdapter);
	}// initialize

	// ----------------------------------------------------------------------------------------------------------

	private static class HexFormatterFactory extends DefaultFormatterFactory {
		private static final long serialVersionUID = 1L;

		public AbstractFormatter getDefaultFormatter() {
			return new HexFormatter();
		}// getDefaultFormatter

		// .................................................
		private static class HexFormatter extends DefaultFormatter {
			private static final long serialVersionUID = 1L;

			public Object stringToValue(String text) throws ParseException {
				try {
					return Integer.valueOf(text, 16);
				} catch (NumberFormatException nfe) {
					throw new ParseException(text, 0);
				} // try
			}// stringToValue

			public String valueToString(Object value) throws ParseException {
				return String.format("%X", value);
			}// valueToString
		}// class HexFormatter
			// .................................................

	}// class MyFormatterFactory
		// --------------------------------------------------------------

	public class SpinnerFormatSet {
		public JSpinner spinner;
		public JFormattedTextField formattedTextField;
		public AbstractFormatterFactory decimalFormatterFactory;
		public HexFormatterFactory hexFormatterFactory;

		public SpinnerFormatSet(JSpinner spinner, JFormattedTextField formattedTextField,
				AbstractFormatterFactory abstractFormatterFactory, HexFormatterFactory hexFormatterFactory) {
			this.spinner = spinner;
			this.formattedTextField = formattedTextField;
			this.decimalFormatterFactory = abstractFormatterFactory;
			this.hexFormatterFactory = hexFormatterFactory;
		}// Constructor

	}// class SpinnerFormatSets

	// ---------------------------------------------------------------

	public class DiskUtilityAdapter implements ActionListener, HDNumberValueChangeListener {
		// FocusListener, ?
		// ------ActionListener,
		@Override
		public void actionPerformed(ActionEvent actionEvent) {
			Object source = actionEvent.getSource();
			String name = ((JComponent) source).getName();

			switch (name) {
			// Menus
			case MNU_DISK_LOAD_DISK:
				diskLoad();
				break;
			case MNU_DISK_CLOSE:
				diskClose();
				break;
			case MNU_DISK_SAVE:
				diskSave();
				break;
			case MNU_DISK_SAVE_AS:
				diskSaveAs();
				break;
			case MNU_DISK_EXIT:
				appClose();
				break;

			case MNU_TOOLS_NEW_DISK:
				diskNew();
				break;
			case MNU_TOOLS_UPDATE_SYSTEM:
				UpdateSystemDisk.updateDisks();
				break;

			// Buttons
			case BTN_IMPORT:
				fileInport();
				break;
			case BTN_EXPORT:
				fileExport();
				break;
			case BTN_NATIVE_FILE:
				getNativeFile();
				break;

			case BTN_CHANGE_DISK_TYPE:
				doChangeDiskType();
				break;
			case BTN_DISK_DIRECTORY:
				doChangeDiskDirectory();
				break;
			case BTN_FIND_FILES:
				doFindFiles();
				break;
			case BTN_PRINT_RESULT:
				doPrintResult();
				break;
			case BTN_LIST_FILES:
				doListFiles();
				break;

			// Toggle Buttons
			case TB_DECIMAL_DISPLAY:
				setDisplayRadix();
				if (tbDecimalDisplay.isSelected()) {
					tbDecimalDisplay.setText(DISPLAY_HEX);
				} else {
					tbDecimalDisplay.setText(DISPLAY_DECIMAL);
				} // if
				break;
			case TB_BOOTABLE:
				break;

			// ComboBoxs
			case CB_FILE_NAMES:
				displaySelectedFile();
				break;
			case CB_CPM_FILE_IN_OUT:
				// displaySelectedFile();
				break;

			// case BTN_READ_PHYSICAL_SECTOR:
			// selectedNewPhysicalSector(false);
			// break;
			default:

			}// switch

		}// actionPerformed
			// ------ActionListener

		// // ------FocusListener
		//
		// @Override
		// public void focusLost(FocusEvent focusEvent) {
		//
		// }// focusLost
		//
		// @Override
		// public void focusGained(FocusEvent arg0) {
		// // do nothing
		//
		// }// focusGained
		// // ------FocusListener

		// ------valueChanged

		@Override
		public void valueChanged(HDNumberValueChangeEvent hDNumberValueChangeEvent) {

			String name = ((Component) hDNumberValueChangeEvent.getSource()).getName();

			switch (name) {
			case HDN_HEAD:
			case HDN_TRACK:
			case HDN_SECTOR:
				selectedNewPhysicalSector(false);
				break;
			case HD_SEEK_PANEL:
				selectedNewPhysicalSector(true);
				break;
			default:
			}// switch

		}// valueChanged

		// ------valueChanged

	}// class DiskUtilityAdapter

	public class FileNameVerifier extends InputVerifier {

		String fileNameRegex = "[\\w|\\?]{0,7}[\\w|\\?|*]{1}\\.?[\\w|\\?]{0,2}[\\w|\\?|*]?";
		Pattern p = Pattern.compile(fileNameRegex);

		private final Color INVALID_COLOR = Color.RED;
		private final Color VALID_COLOR = Color.BLACK;

		public FileNameVerifier() {

		}// Constructor - MemoryLimitVerifier(memorySize)

		@Override
		public boolean verify(JComponent jc) {
			JTextComponent textComponent = (JTextComponent) jc;
			String text = textComponent.getText();
			Matcher m = p.matcher(text);
			if (m.matches()) {
				textComponent.setForeground(VALID_COLOR);
				textComponent.setSelectedTextColor(VALID_COLOR);
				validSearchFileName = true;
				textComponent.setText(text.toUpperCase());
			} else {
				textComponent.setForeground(INVALID_COLOR);
				textComponent.setSelectedTextColor(INVALID_COLOR);
				validSearchFileName = false;
			} // if
			btnFindFiles.setEnabled(validSearchFileName);
			return validSearchFileName;
		}// verify

	}// class FileNameVerifier

	// ---------------------------------------------------------------

	public static final String NO_ACTIVE_FILE = "<No Active File>";

	public static final byte[] NO_FILE = NO_ACTIVE_FILE.getBytes();

	public static final String HDN_HEAD = "hdnHead";
	public static final String HDN_TRACK = "hdnTrack";
	public static final String HDN_SECTOR = "hdnSector";
	public static final String HD_SEEK_PANEL = "seekPanel";

	public static final String DISPLAY_HEX = "Display Hex";
	public static final String DISPLAY_DECIMAL = "Display Dicimal";
	// private final static int CHARACTERS_PER_LINE = 16;

	public static final String USER_HOME = "user.home";
	public static final String THIS_DIR = ".";
	public static final String TEMP_EV = "TEMP";
	public static final String TEMP_PREFIX = "DiskUtility_";
	public static final String FILE_SEPARTOR = File.separator;

	public static final String BTN_IMPORT = "btnImport";
	public static final String BTN_EXPORT = "btnExport";
	public static final String BTN_NATIVE_FILE = "btnNativeFile";

	public static final String BTN_CHANGE_DISK_TYPE = "btnChangeDiskType";
	public static final String BTN_DISK_DIRECTORY = "btnDiskDirectory";
	public static final String BTN_FIND_FILES = "btnFindFiles";
	public static final String BTN_PRINT_RESULT = "btnPrintResult";
	public static final String BTN_LIST_FILES = "btnListFiles";

	private final static String CB_FILE_NAMES = "cbFileNames";
	private final static String CB_CPM_FILE_IN_OUT = "cbCpmFIleInOut";

	public static final String TB_DECIMAL_DISPLAY = "tbDecimalDisplay";
	public static final String TB_BOOTABLE = "tbBootable";

	public static final String MNU_DISK_LOAD_DISK = "mnuDiskLoad";
	public static final String MNU_DISK_CLOSE = "mnuDiskClose";
	public static final String MNU_DISK_SAVE = "mnuDiskSave";
	public static final String MNU_DISK_SAVE_AS = "mnuDiskSaveAs";
	public static final String MNU_DISK_EXIT = "mnuDiskExit";
	public static final String MNU_TOOLS_NEW_DISK = "mnuToolsNewDisk";
	public static final String MNU_TOOLS_UPDATE_SYSTEM = "mnuUpdateSystem";

	private JPanel panelDirectory;
	private JPanel panelPhysical0;
	private JMenuItem mnuToolsNewDisk;
	private JMenuItem mnuDiskLoad;
	private JMenuItem mnuDiskClose;
	private JMenuItem mnuDiskSave;
	private JMenuItem mnuDiskSaveAs;
	private JMenuItem mnuDiskExit;
	private JPanel panelDirRaw;
	private JPanel panelRawUser;
	private JLabel label;
	private JLabel lblRawUser;
	private JPanel panelRawName;
	private JLabel label_2;
	private JLabel lblRawName;
	private JPanel panelRawType;
	private JLabel label_4;
	private JLabel lblRawType;
	private JPanel panelEX;
	private JLabel label_6;
	private JLabel lblRawEX;
	private JPanel panelS1;
	private JLabel label_8;
	private JLabel lblRawS1;
	private JPanel panelS2;
	private JLabel label_10;
	private JLabel lblRawS2;
	private JPanel panelRC;
	private JLabel label_12;
	private JLabel lblRawRC;
	private JPanel panelAllocationVector;
	private JLabel label_14;
	private JLabel lblRawAllocation;
	private JScrollPane scrollDirectoryTable;
	private Component horizontalStrut_2;
	private JPanel panelFileSelection;
	private JComboBox<FileCpmModel> cbFileNames;
	private Component horizontalStrut_3;
	private JLabel lblRecordCount;
	private JLabel label_1;
	private Component horizontalStrut_4;
	private Component horizontalStrut_5;
	private JLabel lblReadOnly;
	private Component horizontalStrut_6;
	private JLabel lblSystemFile;
	// private HexEditPanelSimple panelFileHex;
	private Component horizontalStrut_7;
	private JPanel panelFile0;
	private JPanel tabImportExport;
	private JPanel panelMetrics;
	private JPanel panelDiskGeometry;
	private JLabel lblHeads;
	private JLabel lblNewLabel_1;
	private JLabel lblTracksPerHead;
	private JLabel lblTracksPerHead_1;
	private JLabel lblNewLabel;
	private JLabel lblSectorsPerTrack;
	private JLabel lblTotalTracks;
	private JLabel label4;
	private JLabel lblTotalSectors;
	private JLabel lblTotalSectors_1;
	private JPanel panelFileSystemParameters;
	private JLabel lblTracksBeforeDirectory;
	private JLabel lblLogicalBlockSizeInSectors;
	private JLabel lblMaxDirectoryEntry;
	private JLabel lblMaxBlockNumber;
	private JPanel panelMetrics0;
	private JPanel panelCopy0;
	private JPanel panel;
	private JButton btnExport;
	private JButton btnImport;
	private JTextField txtNativeFileInOut;
	private JComboBox<String> cbCpmFileInOut;
	private JButton btnNativeFile;
	private JLabel lblNewLabel_3;
	private JLabel lblCpmFile;
	private JMenu mnuTools;
	private JMenuItem mnuToolsUpdateSystem;
	private JPanel tabCatalog;
	private JPanel panelCatalogTop;
	private JLabel lblDirectory;
	private JButton btnFindFiles;
	private JTextField txtFindFileName;
	private JRadioButton rbRecurse;
	private JLabel lblDiskType;
	private JButton btnListFiles;
	private JScrollPane scrollPane;
	private JTextPane txtCatalog;
	private JLabel lblCatalogHeader;
	private JButton btnPrintResult;

	// ----------------------------------------------------------------------

	private class RowListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent event) {
			if (event.getValueIsAdjusting()) {
				return;
			} // if
			showDirectoryDetail(dirTable.getSelectedRow());
		}// valueChanged
	}// class RowListener

}// class DiskUtility
