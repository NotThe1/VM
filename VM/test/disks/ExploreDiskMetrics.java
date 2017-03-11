package disks;

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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.text.JTextComponent;

import disks.utility.UpdateSystemDisk;

public class ExploreDiskMetrics {
	private String currentDiskType = "F3HD";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ExploreDiskMetrics window = new ExploreDiskMetrics();
					window.frmExploreDiskMetrics.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				} // try
			}// run
		});
	}// main

	/* Standard Stuff */

	private void doBtnOne() {
		tabbedPane.setSelectedIndex(TAB_DISK_METRICS);
		lblMetricsHeader.setText(currentDiskType);
		DiskMetrics dm = DiskMetrics.getDiskMetric(currentDiskType);

		txtDiskMetrics.setText(String.format("    Disk Type: %s %n%n", currentDiskType));

		txtDiskMetrics.append(String.format("Heads: \t\t%1$02X\t(%1$d)%n", dm.heads));
		txtDiskMetrics.append(String.format("Tracks\\Head: \t\t%1$04X\t(%1$d)%n", dm.tracksPerHead));
		txtDiskMetrics.append(String.format("Sectors\\Track: \t\t%1$04X\t(%1$d)%n", dm.sectorsPerTrack));
		txtDiskMetrics.append(String.format("Bytes\\Sector: \t\t%1$04X\t(%1$d)%n", dm.bytesPerSector));
		txtDiskMetrics.append(String.format("Sectors\\Block: \t\t%1$04X\t(%1$d)%n", dm.sectorsPerBlock));
		txtDiskMetrics.append(String.format("Directory Block Count: \t%1$04X\t(%1$d)%n%n", dm.directoryBlockCount));

		txtDiskMetrics.append(String.format("SPT: \t%1$04X\t(%1$d)\t\t# of cmpRecords / logical Track%n", dm.getSPT()));
		txtDiskMetrics.append(String.format("BSH: \t%1$04X\t(%1$d)\t\t Block Shift%n", dm.getBSH()));
		txtDiskMetrics.append(String.format("BLM: \t%1$04X\t(%1$d)\t\t Block Mask%n", dm.getBLM()));
		txtDiskMetrics.append(String.format("EXM: \t%1$04X\t(%1$d)\t\t Extent Mask%n", dm.getEXM()));
		txtDiskMetrics.append(String.format("DSM: \t%1$04X\t(%1$d)\t\t Highest Block Number%n", dm.getDSM()));
		txtDiskMetrics.append(String.format("DRM: \t%1$04X\t(%1$d)\t\t Highest Directory Entry Number%n", dm.getDRM()));
		txtDiskMetrics.append(String.format("AL01: \t%1$04X\t(%1$d)\t\t Allocation pattern%n", dm.getAL01()));
		txtDiskMetrics.append(String.format("CKS: \t%1$04X\t(%1$d)\t\t Checksum Vector size%n", dm.getCKS()));
		txtDiskMetrics.append(String.format("OFF*: \t%1$04X\t(%1$d)\t\t Number of reserved sectors%n%n",
				dm.getDirectoryStartSector()));

		txtDiskMetrics.append(String.format("TotalSectorsPerHead: \t%1$04X\t(%1$d)%n%n", dm.getTotalSectorsPerHead()));
		txtDiskMetrics.append(String.format("TotalSectorsOnDisk: \t%1$04X\t(%1$d)%n%n", dm.getTotalSectorsOnDisk()));

		// txtDiskMetrics.append(String.format("BytesPerBlock: \t\t%1$04X\t(%1$d)%n%n", dm.getBytesPerBlock()));
		// txtDiskMetrics.append(String.format("SectorsPerBlock: \t%1$04X\t(%1$d)%n%n", dm.getSectorsPerBlock()));

		txtDiskMetrics.append(String.format("DirectoryBlockCount: \t%1$04X\t(%1$d)%n%n", dm.getDirectoryBlockCount()));
		txtDiskMetrics
				.append(String.format("DirectoryStartSector: \t%1$04X\t(%1$d)%n%n", dm.getDirectoryStartSector()));
		txtDiskMetrics
				.append(String.format("DirectorysLastSector: \t%1$04X\t(%1$d)%n%n", dm.getDirectorysLastSector()));
		txtDiskMetrics.append(String.format("MaxDirectoryEntries: \t%1$04X\t(%1$d)%n%n", dm.getMaxDirectoryEntries()));

	}// doBtnOne

	private void doBtnTwo() {
		tabbedPane.setSelectedIndex(TAB_LOG1);
		lblLog1Header.setText(currentDiskType);
		DiskMetrics dm = DiskMetrics.getDiskMetric(currentDiskType);

		String displayLine;
		txtLog1.setText("");
		// CP/M Constants .................................................
		displayLine = String.format("\t\t\tCP//M Constants%n", "");
		txtLog1.append(displayLine);
		txtLog1.append(System.lineSeparator());

		txtLog1.append(String.format("RECORD_SIZE:\t\t%1$04X\t(%1$d)%n", Disk.RECORD_SIZE));
		txtLog1.append(String.format("DIRECTORY_ENTRY_SIZE:\t\t%1$04X\t(%1$d)%n", Disk.DIRECTORY_ENTRY_SIZE));
		txtLog1.append(String.format("SYSTEM_RECORDS:\t\t%1$04X\t(%1$d)%n", Disk.SYSTEM_RECORDS));
		txtLog1.append(
				String.format("DIRECTORY_ENTRYS_PER_RECORD:\t%1$04X\t(%1$d)%n", Disk.DIRECTORY_ENTRYS_PER_RECORD));
		txtLog1.append(System.lineSeparator());

		// Disk Metrics values - Preset - dm.xxxxx ........................
		displayLine = String.format("\t\t\tDisk Metrics values - Preset  -  dm.xxxxx%n", "");
		txtLog1.append(displayLine);
		txtLog1.append(System.lineSeparator());

		txtLog1.append(String.format("Heads:\t\t\t%1$04X\t(%1$d)%n", dm.heads));
		txtLog1.append(String.format("Tracks Per Head:\t\t%1$04X\t(%1$d)%n", dm.tracksPerHead));
		txtLog1.append(String.format("Sectors Per Track:\t\t%1$04X\t(%1$d)\t 1 head only%n", dm.sectorsPerTrack));
		txtLog1.append(String.format("Bytes per Sector:\t\t%1$04X\t(%1$d)%n", dm.bytesPerSector));
		txtLog1.append(String.format("Sectors per Block:\t\t%1$04X\t(%1$d)%n", dm.sectorsPerBlock));
		txtLog1.append(String.format("Directory Block Count:\t\t%1$04X\t(%1$d)%n", dm.directoryBlockCount));
		txtLog1.append(String.format("SPT: %1$04X\t(%1$d)%n", dm.getSPT()));
		txtLog1.append(System.lineSeparator());

		// Calculated values .................................................
		displayLine = String.format("\t\t\tCalculated values%n", "");
		txtLog1.append(displayLine);
		txtLog1.append(System.lineSeparator());

		/* AL0 */ /* allocate the directory blocks */
		int ALO = 0B00000000;
		int allocated = 0B1000000000000000;

		for (int i = 0; i < dm.directoryBlockCount; i++) {
			ALO = ALO >> 1;
			ALO = ALO | allocated;
		} // for
		ALO = ALO & 0XFFFF;
		displayLine = String.format("ALO Allocation Vector:\t\t%1$04X\t(%1$d)\t%2$s%n", ALO,
				"set Bits for Directory using dm.directoryBlockCount");
		txtLog1.append(displayLine);
		txtLog1.append(System.lineSeparator());

		/* recordsPerSector */
		int recordsPerSector = dm.bytesPerSector / Disk.RECORD_SIZE;
		displayLine = String.format("recordsPerSector:\t\t%1$04X\t(%1$d)\t%2$s%n", recordsPerSector,
				"dm.bytesPerSector / RECORD_SIZE");
		txtLog1.append(displayLine);

		/* SPT */ /* number of records per head per track */
		int SPT = recordsPerSector * dm.sectorsPerTrack;
		displayLine = String.format("SPT - number of records per head per track:\t%1$04X\t(%1$d)\t%2$s%n", SPT,
				"recordsPerSector * dm.sectorsPerTrack");
		txtLog1.append(displayLine);
		txtLog1.append(String.format("SPT: %1$04X\t(%1$d)%n", dm.getSPT()));

		// txtLog1.append(System.lineSeparator());

		/* OFS */ /* location of start of Block 0 */
		boolean bootDisk = true; // assume all these disks are system disks
		// If not system disk OFS = 0;
		// else first clean Cylinder after end of system records
		int OFS = 0;
		if (bootDisk) {
			float floatSize = (Disk.SYSTEM_LOGICAL_BLOCKS + 1) / (float) SPT;
			OFS = (int) Math.ceil(floatSize);
		} // if
		displayLine = String.format("OFS - Directory Block 0:\t\t%1$04X\t(%1$d)\t%2$s%n", OFS,
				"First clean track(clynder) after system records (SPT)");
		txtLog1.append(displayLine);
		txtLog1.append(String.format("OFS: %1$04X\t(%1$d)%n", dm.getOFS()));

		/* Directory Start Sector */
		int directoryStartSector = OFS;
		displayLine = String.format("directoryStartSector - Directory Block 0:\t%1$04X\t(%1$d)\t%2$s%n",
				directoryStartSector, "At OFS");
		txtLog1.append(displayLine);

		/* Directory EndSector */
		int directoryEndSector = (directoryStartSector + (dm.directoryBlockCount * dm.sectorsPerBlock)) - 1;
		displayLine = String.format("directoryEndSector:\t\t%1$04X\t(%1$d)\t%2$s%n", directoryEndSector,
				"directoryStartSector + (dm.directoryBlockCount * dm.sectorsPerBlock))-1");
		txtLog1.append(displayLine);
		txtLog1.append(System.lineSeparator());

		/* bytesPerBlock */
		int bytesPerBlock = dm.bytesPerSector * dm.sectorsPerBlock;
		displayLine = String.format("bytesPerBlock:\t\t\t%1$04X\t(%1$d)\t%2$s%n", bytesPerBlock,
				"dm.bytesPerSector * dm.sectorsPerBlock");
		txtLog1.append(displayLine);

		/* BSH */ /* Block Shift - Block Size = 128 * (2 ** BSH) */
		int numberBase = 2;
		int workingNumber = bytesPerBlock / Disk.RECORD_SIZE;
		int BSH = 1;
		while (workingNumber != numberBase) {
			BSH++;
			workingNumber /= numberBase;
		} // while
		displayLine = String.format("BSH - Block Shift  :\t\t%1$04X\t(%1$d)\t%2$s%n", BSH,
				"bytesPerBlock = 128 * (2 ** BSH)");
		txtLog1.append(displayLine);
		txtLog1.append(String.format("BSH: %1$04X\t(%1$d)%n", dm.getBSH()));

		/* BLM */ /* Block Mask - Block Size = 128 * (BLM +1) */
		int BLM = (bytesPerBlock / Disk.RECORD_SIZE) - 1;
		displayLine = String.format("BLM - Block Mask  :\t\t%1$04X\t(%1$d)\t%2$s%n", BLM,
				"Block Size = 128 * (BLM +1) |  (bytesPerBlock / Disk.RECORD_SIZE)-1");
		txtLog1.append(displayLine);
		txtLog1.append(String.format("BLM: %1$04X\t(%1$d)%n", dm.getBLM()));

		/* DRM */ /* Max Directory Entry Number */
		int DRM = ((bytesPerBlock * dm.directoryBlockCount) / Disk.DIRECTORY_ENTRY_SIZE) - 1;
		displayLine = String.format("DRM - Max Directory Entry:\t\t%1$04X\t(%1$d)\t%2$s%n", DRM,
				"((bytesPerBlock * dm.directoryBlockCount)/Disk.DIRECTORY_ENTRY_SIZE)-1");
		txtLog1.append(displayLine);
		txtLog1.append(String.format("DRM: %1$04X\t(%1$d)%n", dm.getDRM()));

		/* CKS */ /* Disk Work Area size - stores checksums */
		int CKS = (DRM + 1) / Disk.DIRECTORY_ENTRYS_PER_RECORD;
		displayLine = String.format("CKS - Disk Work Area size:\t\t%1$04X\t(%1$d)\t%2$s%n", CKS,
				"(DRM +1) /Disk.DIRECTORY_ENTRYS_PER_RECORD");
		txtLog1.append(displayLine);
		txtLog1.append(String.format("CKS: %1$04X\t(%1$d)%n", dm.getCKS()));

		txtLog1.append(System.lineSeparator());

		/* totalSectorsOnDisk */
		int totalSectorsOnDisk = dm.heads * dm.tracksPerHead * dm.sectorsPerTrack;
		displayLine = String.format("totalSectorsOnDisk:\t\t%1$04X\t(%1$d)\t%2$s%n", totalSectorsOnDisk,
				"dm.heads * dm.tracksPerHead * dm.sectorsPerTrack");
		txtLog1.append(displayLine);

		/* DSM */ /* Max Block Number */

		int DSM = ((totalSectorsOnDisk - OFS) / dm.sectorsPerBlock) - 1;
		displayLine = String.format("DSM - Highest Block Number:\t\t%1$04X\t(%1$d)\t%2$s%n", DSM,
				"((totalSectorsOnDisk - OFS)/dm.sectorsPerBlock)-1");
		txtLog1.append(displayLine);
		txtLog1.append(String.format("DSM: %1$04X\t(%1$d)%n", dm.getDSM()));

		/* EXM */ /* Extent Mask */
		// bytesPerBlock = 8192;DSM = 5;
		int divisor = DSM < 256 ? 1024 : 2048; // depends if allocation is an 8 or 16 bit value
		int EXM = (bytesPerBlock / divisor) - 1;

		EXM = EXM <= 0 ? 0 : EXM; // clean up case where bytesPerBlock = 1024 and DSM > 256
		displayLine = String.format("EXM - Extent Mask:\t\t%1$04X\t(%1$d)\t%2$s%n", EXM,
				"(bytesPerBlock/divisor) - 1  | divisor = DSM<256?1024:2048;");
		txtLog1.append(displayLine);
		txtLog1.append(String.format("EXM: %1$04X\t(%1$d)%n", dm.getEXM()));

	}// doBtnTwo

	private void doBtnThree() {
		byte[] a;
		a = Disk.EMPTY_DIRECTORY_ENTRY.clone();
		for (int i = 0; i < Disk.DIRECTORY_ENTRY_SIZE; i++) {
			txtLog1.append(String.format("directory entry %02X =  %02X%n", i, a[i]));
		} // for

		UpdateSystemDisk.updateDisks();
	}// doBtnThree

	private void doBtnFour() {

	}// doBtnFour

	// ---------------------------------------------------------

	private void doFileNew() {
		Object[] options = DiskMetrics.getDiskTypes();
		Object selectedValue = JOptionPane.showInputDialog(null, "Choose Disk Type", "Input",
				JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		currentDiskType = (String) selectedValue;

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

	private void doFindFiles() {
		Pattern p = makePattern();
		File startDir = new File(lblDirectory.getText());
		findFiles(startDir,p);
	}// doFindFiles

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

		pattern = getPattern(name,8);
		pattern += getPattern(ext,3);
		

		return Pattern.compile(pattern);
	}// makePattern
	
	private String getPattern(String s,int size){
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
		String padString = foundStar?".":" ";	// either a period or space
		for (; i < size;i++){
			sb.append(padString);
		}// fill out
		return sb.toString();
	}//getPattern

	private void findFiles(File enterFile,Pattern p) {

		File[] files = enterFile.listFiles();
		for (File file : files) {
			if (rbRecurse.isSelected() && file.isDirectory()) {
				findFiles(file,p);
			} // if for recursion

			processTheFile(file,p);

		} // for each file
	}// findFiles

	private void processTheFile(File file,Pattern p) {
		String fileName = file.getName().toUpperCase();
		if (!fileName.endsWith("." + lblDiskType.getText())) {
			txtLog1.append(String.format("\t\tskipped File  %s%n", fileName));
			return; // only want right type of disk
		} // if right disk type
		txtLog1.append(String.format("Disk name is %s%n", fileName));
		RawDiskDrive diskDrive = new RawDiskDrive(file.getAbsolutePath());
		DiskMetrics diskMetrics = DiskMetrics.getDiskMetric(lblDiskType.getText());

		byte[] diskSector;

//		CPMDirectory directory = new CPMDirectory(diskDrive.getDiskType(), true);
		int firstDirectorySector = diskMetrics.getDirectoryStartSector();
		int lastDirectorySector = diskMetrics.getDirectorysLastSector();
		// int entriesPerSector = bytesPerSector / Disk.DIRECTORY_ENTRY_SIZE;
		int entriesPerSector = 512 / Disk.DIRECTORY_ENTRY_SIZE;

		//
//		int directoryIndex = 0;
		for (int s = firstDirectorySector; s < lastDirectorySector + 1; s++) {
			diskDrive.setCurrentAbsoluteSector(s);
			diskSector = diskDrive.read();
			for (int i = 0; i < entriesPerSector; i++) {
				String cpmFileName = extractName(diskSector, i);
				if (cpmFileName == null) {
					continue;
				}//if null
				Matcher m = p.matcher(cpmFileName);
				
				if (m.matches()){
									txtLog1.append(String.format("\t%s is a match%n", cpmFileName));
				}else{
//					txtLog1.append(String.format("\t\t%s NOT A MATCH%n", cpmFileName));
					
				}//if match
				

			} // for - i
		} // for -s
			// }// makeDirectory

	}// processTheFile

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
	}//


	private void appClose() {
		Preferences myPrefs = Preferences.userNodeForPackage(ExploreDiskMetrics.class);
		Dimension dim = frmExploreDiskMetrics.getSize();
		myPrefs.putInt("Height", dim.height);
		myPrefs.putInt("Width", dim.width);
		Point point = frmExploreDiskMetrics.getLocation();
		myPrefs.putInt("LocX", point.x);
		myPrefs.putInt("LocY", point.y);
		myPrefs.putInt("Divider", splitPane1.getDividerLocation());
		myPrefs.put("currentDiskType", currentDiskType);
		myPrefs.put("DiskDirectory", lblDirectory.getText());
		myPrefs.put("DiskType", lblDiskType.getText());
		;
		myPrefs = null;
		System.exit(0);
	}// appClose

	private void appInit() {
		Preferences myPrefs = Preferences.userNodeForPackage(ExploreDiskMetrics.class);
		frmExploreDiskMetrics.setSize(965, 797);
		frmExploreDiskMetrics.setLocation(myPrefs.getInt("LocX", 100), myPrefs.getInt("LocY", 100));
		splitPane1.setDividerLocation(myPrefs.getInt("Divider", 250));
		lblDirectory.setText(myPrefs.get("DiskDirectory", "."));
		lblDiskType.setText(myPrefs.get("DiskType", "F3HD"));
		currentDiskType = myPrefs.get("currentDiskType", "F3HD");
		myPrefs = null;
		lblCurrentDiskType.setText(currentDiskType);
	}// appInit

	public ExploreDiskMetrics() {
		initialize();
		appInit();
	}// Constructor

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmExploreDiskMetrics = new JFrame();
		frmExploreDiskMetrics.setTitle("ExploreDiskMetrics");
		frmExploreDiskMetrics.setBounds(100, 100, 450, 300);
		frmExploreDiskMetrics.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				appClose();
			}
		});
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 25, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
		frmExploreDiskMetrics.getContentPane().setLayout(gridBagLayout);

		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		GridBagConstraints gbc_toolBar = new GridBagConstraints();
		gbc_toolBar.fill = GridBagConstraints.HORIZONTAL;
		gbc_toolBar.insets = new Insets(0, 0, 5, 0);
		gbc_toolBar.gridx = 0;
		gbc_toolBar.gridy = 0;
		frmExploreDiskMetrics.getContentPane().add(toolBar, gbc_toolBar);

		btnOne = new JButton("Disk Metrics");
		btnOne.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doBtnOne();
			}
		});
		btnOne.setMaximumSize(new Dimension(70, 20));
		btnOne.setPreferredSize(new Dimension(50, 20));
		toolBar.add(btnOne);

		btnTwo = new JButton("Calculate Log1");
		btnTwo.setMinimumSize(new Dimension(50, 23));
		btnTwo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doBtnTwo();
			}
		});
		btnTwo.setPreferredSize(new Dimension(50, 20));
		btnTwo.setMaximumSize(new Dimension(100, 20));
		toolBar.add(btnTwo);

		btnThree = new JButton("UpdateSystemDisk");
		btnThree.setToolTipText("UpdateSystemDisk");
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
		splitPane1.setOneTouchExpandable(true);
		GridBagConstraints gbc_splitPane1 = new GridBagConstraints();
		gbc_splitPane1.insets = new Insets(0, 0, 5, 0);
		gbc_splitPane1.fill = GridBagConstraints.BOTH;
		gbc_splitPane1.gridx = 0;
		gbc_splitPane1.gridy = 1;
		frmExploreDiskMetrics.getContentPane().add(splitPane1, gbc_splitPane1);

		JPanel panelLeft = new JPanel();
		splitPane1.setLeftComponent(panelLeft);
		GridBagLayout gbl_panelLeft = new GridBagLayout();
		gbl_panelLeft.columnWidths = new int[] { 0, 0 };
		gbl_panelLeft.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gbl_panelLeft.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panelLeft.rowWeights = new double[] { 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
		panelLeft.setLayout(gbl_panelLeft);

		JPanel panelDirectory = new JPanel();
		GridBagConstraints gbc_panelDirectory = new GridBagConstraints();
		gbc_panelDirectory.insets = new Insets(0, 0, 5, 0);
		gbc_panelDirectory.fill = GridBagConstraints.BOTH;
		gbc_panelDirectory.gridx = 0;
		gbc_panelDirectory.gridy = 0;
		panelLeft.add(panelDirectory, gbc_panelDirectory);
		GridBagLayout gbl_panelDirectory = new GridBagLayout();
		gbl_panelDirectory.columnWidths = new int[] { 0, 0 };
		gbl_panelDirectory.rowHeights = new int[] { 0, 0, 0, 0 };
		gbl_panelDirectory.columnWeights = new double[] { 0.0, 1.0 };
		gbl_panelDirectory.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panelDirectory.setLayout(gbl_panelDirectory);

		JButton btnDiskDirectory = new JButton("Change Disk Directory");
		btnDiskDirectory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc = new JFileChooser(lblDirectory.getText());
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (fc.showOpenDialog(frmExploreDiskMetrics) == JFileChooser.APPROVE_OPTION) {
					lblDirectory.setText(fc.getSelectedFile().getAbsolutePath());
				} // if
			}
		});
		GridBagConstraints gbc_btnDiskDirectory = new GridBagConstraints();
		gbc_btnDiskDirectory.insets = new Insets(0, 0, 5, 5);
		gbc_btnDiskDirectory.gridx = 0;
		gbc_btnDiskDirectory.gridy = 0;
		panelDirectory.add(btnDiskDirectory, gbc_btnDiskDirectory);

		lblDiskType = new JLabel("F3HD");
		GridBagConstraints gbc_lblDiskType = new GridBagConstraints();
		gbc_lblDiskType.insets = new Insets(0, 0, 5, 0);
		gbc_lblDiskType.gridx = 1;
		gbc_lblDiskType.gridy = 0;
		panelDirectory.add(lblDiskType, gbc_lblDiskType);

		rbRecurse = new JRadioButton("Recurse");
		GridBagConstraints gbc_rbRecurse = new GridBagConstraints();
		gbc_rbRecurse.insets = new Insets(0, 0, 5, 5);
		gbc_rbRecurse.gridx = 0;
		gbc_rbRecurse.gridy = 1;
		panelDirectory.add(rbRecurse, gbc_rbRecurse);

		lblDirectory = new JLabel("New label");
		GridBagConstraints gbc_lblDirectory = new GridBagConstraints();
		gbc_lblDirectory.gridwidth = 2;
		gbc_lblDirectory.gridx = 0;
		gbc_lblDirectory.gridy = 2;
		panelDirectory.add(lblDirectory, gbc_lblDirectory);

		JPanel panelFind = new JPanel();
		GridBagConstraints gbc_panelFind = new GridBagConstraints();
		gbc_panelFind.insets = new Insets(0, 0, 5, 0);
		gbc_panelFind.fill = GridBagConstraints.BOTH;
		gbc_panelFind.gridx = 0;
		gbc_panelFind.gridy = 1;
		panelLeft.add(panelFind, gbc_panelFind);
		GridBagLayout gbl_panelFind = new GridBagLayout();
		gbl_panelFind.columnWidths = new int[] { 0, 0, 0 };
		gbl_panelFind.rowHeights = new int[] { 0, 0, 0 };
		gbl_panelFind.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl_panelFind.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panelFind.setLayout(gbl_panelFind);

		btnFindFiles = new JButton("Find File(s)");
		btnFindFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doFindFiles();
			}
		});
		btnFindFiles.setEnabled(false);
		GridBagConstraints gbc_btnFindFiles = new GridBagConstraints();
		gbc_btnFindFiles.insets = new Insets(0, 0, 0, 5);
		gbc_btnFindFiles.gridx = 0;
		gbc_btnFindFiles.gridy = 1;
		panelFind.add(btnFindFiles, gbc_btnFindFiles);

		txtFindFileName = new JTextField();
		txtFindFileName.setInputVerifier(new FileNameVerifier());
		txtFindFileName.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				txtLog1.append("new value = " + txtFindFileName.getText() + System.lineSeparator());
			}
		});

		GridBagConstraints gbc_txtFindFileName = new GridBagConstraints();
		gbc_txtFindFileName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtFindFileName.gridx = 1;
		gbc_txtFindFileName.gridy = 1;
		panelFind.add(txtFindFileName, gbc_txtFindFileName);
		txtFindFileName.setColumns(10);

		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 2;
		panelLeft.add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		JButton btnListFiles = new JButton("List Files");
		GridBagConstraints gbc_btnListFiles = new GridBagConstraints();
		gbc_btnListFiles.gridx = 0;
		gbc_btnListFiles.gridy = 0;
		panel.add(btnListFiles, gbc_btnListFiles);

		JPanel panelRight = new JPanel();
		splitPane1.setRightComponent(panelRight);
		GridBagLayout gbl_panelRight = new GridBagLayout();
		gbl_panelRight.columnWidths = new int[] { 0, 0 };
		gbl_panelRight.rowHeights = new int[] { 0, 0 };
		gbl_panelRight.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panelRight.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		panelRight.setLayout(gbl_panelRight);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 0;
		panelRight.add(tabbedPane, gbc_tabbedPane);

		JScrollPane scrollPane = new JScrollPane();
		tabbedPane.addTab("Disk Metrics", null, scrollPane, null);

		txtDiskMetrics = new JTextArea();
		scrollPane.setViewportView(txtDiskMetrics);

		lblMetricsHeader = new JLabel("New label");
		lblMetricsHeader.setForeground(Color.BLUE);
		lblMetricsHeader.setHorizontalAlignment(SwingConstants.CENTER);
		lblMetricsHeader.setFont(new Font("Courier New", Font.BOLD | Font.ITALIC, 18));
		scrollPane.setColumnHeaderView(lblMetricsHeader);

		JScrollPane scrollPane_1 = new JScrollPane();
		tabbedPane.addTab("Log1", null, scrollPane_1, null);

		txtLog1 = new JTextArea();
		txtLog1.setFont(new Font("Courier New", Font.PLAIN, 14));
		txtLog1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getClickCount() > 1) {
					txtLog1.setText("");
				}
			}
		});
		scrollPane_1.setViewportView(txtLog1);

		lblLog1Header = new JLabel("New label");
		lblLog1Header.setHorizontalAlignment(SwingConstants.CENTER);
		lblLog1Header.setForeground(Color.BLUE);
		lblLog1Header.setFont(new Font("Courier New", Font.BOLD | Font.ITALIC, 18));
		scrollPane_1.setColumnHeaderView(lblLog1Header);
		splitPane1.setDividerLocation(200);

		JPanel panelStatus = new JPanel();
		panelStatus.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_panelStatus = new GridBagConstraints();
		gbc_panelStatus.fill = GridBagConstraints.BOTH;
		gbc_panelStatus.gridx = 0;
		gbc_panelStatus.gridy = 2;
		frmExploreDiskMetrics.getContentPane().add(panelStatus, gbc_panelStatus);

		lblCurrentDiskType = new JLabel("<<None>>");
		panelStatus.add(lblCurrentDiskType);

		JMenuBar menuBar = new JMenuBar();
		frmExploreDiskMetrics.setJMenuBar(menuBar);

		JMenu mnuFile = new JMenu("File");
		menuBar.add(mnuFile);

		JMenuItem mnuFileNew = new JMenuItem("New Disk Type");
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

	}// initialize

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

	}// class MemoryLimitVerifier

	private JFrame frmExploreDiskMetrics;
	private JButton btnOne;
	private JButton btnTwo;
	private JButton btnThree;
	private JButton btnFour;
	private JSplitPane splitPane1;
	private JTextArea txtDiskMetrics;
	private JLabel lblCurrentDiskType;
	private JLabel lblMetricsHeader;
	private JTextArea txtLog1;
	private JLabel lblLog1Header;
	private JTabbedPane tabbedPane;

	private static final int TAB_DISK_METRICS = 0;
	private static final int TAB_LOG1 = 1;
	private JTextField txtFindFileName;
	private JLabel lblDirectory;
	private JLabel lblDiskType;

	private boolean validSearchFileName;
	private JButton btnFindFiles;
	private JRadioButton rbRecurse;

}// class GUItemplate