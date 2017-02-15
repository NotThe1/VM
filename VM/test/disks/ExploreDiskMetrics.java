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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

public class ExploreDiskMetrics {
	private String currentDiskType = "F3HD";
	private static final int RECORD_SIZE = 128;

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
		displayLine = String.format("\t\t\tCP//M Constants%n", "");
		txtLog1.append(displayLine);
		txtLog1.append(System.lineSeparator());

		txtLog1.append(String.format("RECORD_SIZE:\t\t%1$04X\t(%1$d)%n", RECORD_SIZE));
		txtLog1.append(
				String.format("DIRECTORY_ENTRY_SIZE:\t\t%1$04X\t(%1$d)%n", Disk.DIRECTORY_ENTRY_SIZE));
		txtLog1.append(String.format("SYSTEM_RECORDS:\t\t%1$04X\t(%1$d)%n", Disk.SYSTEM_RECORDS));
		txtLog1.append(System.lineSeparator());
		
		displayLine = String.format("\t\t\tDisk Metrics values - Preset  -  dm.xxxxx%n", "");
		txtLog1.append(displayLine);
		txtLog1.append(System.lineSeparator());

		txtLog1.append(String.format("Heads:\t\t\t%1$04X\t(%1$d)%n", dm.heads));
		txtLog1.append(String.format("Tracks Per Head:\t\t%1$04X\t(%1$d)%n", dm.tracksPerHead));
		txtLog1.append(
				String.format("Sectors Per Track:\t\t%1$04X\t(%1$d)\t 1 head only%n", dm.sectorsPerTrack));
		txtLog1.append(String.format("Bytes per Sector:\t\t%1$04X\t(%1$d)%n", dm.bytesPerSector));
		txtLog1.append(String.format("Sectors per Block:\t\t%1$04X\t(%1$d)%n", dm.sectorsPerBlock));
		txtLog1.append(String.format("Directory Block Count:\t\t%1$04X\t(%1$d)%n", dm.directoryBlockCount));
		txtLog1.append(System.lineSeparator());

		displayLine = String.format("\t\t\tCalculated values%n", "");
		txtLog1.append(displayLine);
		txtLog1.append(System.lineSeparator());

		/* recordsPerSector */
		int recordsPerSector = dm.bytesPerSector / RECORD_SIZE;
		displayLine = String.format("recordsPerSector:\t\t%1$04X\t(%1$d)\t%2$s%n", recordsPerSector,
				"dm.bytesPerSector / RECORD_SIZE");
		txtLog1.append(displayLine);

		/* SPT */  /* number of records per Cylinder */
		int SPT = recordsPerSector * dm.sectorsPerTrack * dm.heads;
		displayLine = String.format("SPT - records Per Logical Track Sector:\t%1$04X\t(%1$d)\t%2$s%n", SPT,
				"recordsPerSector * dm.sectorsPerTrack  * dm.heads");
		txtLog1.append(displayLine);
		txtLog1.append(System.lineSeparator());

		/*  OFS  */ /* location of start of Block 0 */
		boolean bootDisk = true;		// assume all these disks are system disks
							// If not system disk OFS = 0;
							// else first clean Cylinder after end of system records
		int OFS = 0;
		if (bootDisk) {
			float floatSize = (Disk.SYSTEM_LOGICAL_BLOCKS + 1) / (float) SPT;
			OFS = (int) Math.ceil(floatSize) * dm.sectorsPerTrack * dm.heads;
		}//if
		displayLine = String.format("OFS - Directory Block 0:\t\t%1$04X\t(%1$d)\t%2$s%n", OFS,
				"0 if not system disk, else first clean track(clynder) after system records");
		txtLog1.append(displayLine);
		
		/* Directory Start Sector */
		int directoryStartSector = OFS;
		displayLine = String.format("directoryStartSector - Directory Block 0:\t%1$04X\t(%1$d)\t%2$s%n", directoryStartSector,
				"At OFS");
		txtLog1.append(displayLine);
	
		/* Directory EndSector  */
		int directoryEndSector = (directoryStartSector + (dm.directoryBlockCount * dm.sectorsPerBlock))-1;
		displayLine = String.format("directoryEndSector:\t\t%1$04X\t(%1$d)\t%2$s%n", directoryEndSector,
				"directoryStartSector + (dm.directoryBlockCount * dm.sectorsPerBlock))-1");
		txtLog1.append(displayLine);
		txtLog1.append(System.lineSeparator());


		/* bytesPerBlock */
		int bytesPerBlock = dm.bytesPerSector * dm.sectorsPerBlock;
		displayLine = String.format("bytesPerBlock:\t\t\t%1$04X\t(%1$d)\t%2$s%n", bytesPerBlock,
				"dm.bytesPerSector * dm.sectorsPerBlock");
		txtLog1.append(displayLine);

		/* DRM */  /* Max Directory Entry Number */
		int DRM = ((bytesPerBlock * dm.directoryBlockCount) / Disk.DIRECTORY_ENTRY_SIZE) - 1;
		displayLine = String.format("DRM - Max Directory Entry:\t\t%1$04X\t(%1$d)\t%2$s%n", DRM,
				"((bytesPerBlock * dm.directoryBlockCount)/Disk.DIRECTORY_ENTRY_SIZE)-1");
		txtLog1.append(displayLine);
		

		txtLog1.append(System.lineSeparator());

		

		/* totalSectorsOnDisk */
		int  totalSectorsOnDisk = dm.heads * dm.tracksPerHead * dm.sectorsPerTrack;
		displayLine = String.format("totalSectorsOnDisk:\t\t%1$04X\t(%1$d)\t%2$s%n", totalSectorsOnDisk,
				"dm.heads * dm.tracksPerHead * dm.sectorsPerTrack");
		txtLog1.append(displayLine);

		
		/* DSM */ /*   Max Block Number*/

		 int DSM =((totalSectorsOnDisk - OFS)/dm.sectorsPerBlock)-1;
		// // int drm = (this.blockSizeInBytes * this.getDirectoryBlockCount()) / Disk.DIRECTORY_ENTRY_SIZE;
		//
		 displayLine = String.format("DSM:\t\t\t%1$04X\t(%1$d)\t%2$s%n",
				 DSM,"((totalSectorsOnDisk - OFS)/dm.sectorsPerBlock)-1");
		 txtLog1.append(displayLine);
		 
//			int totalPhysicalSectorsOnDisk = this.getTotalSectorsOnDisk();
//			int totalPhysicalSectorsOnOFS = getOFS() * this.heads * this.sectorsPerTrack;
//			return ((totalPhysicalSectorsOnDisk - totalPhysicalSectorsOnOFS) / sectorsPerBlock) - 1;


		//
		//
		// int totalSectorsPerHead = dm.tracksPerHead * dm.sectorsPerTrack;
		// displayLine = String.format("totalSectorsPerHead => \t%1$04X\t(%1$d)\t\t%2$s%n",
		// totalSectorsPerHead,"dm.tracksPerHead * dm.sectorsPerTrack");
		// txtLog1.append(displayLine);
		//
		// int totalSectorsOnDisk = totalSectorsPerHead * dm.heads;
		// displayLine = String.format("totalSectorsOnDisk => \t%1$04X\t(%1$d)\t\t%2$s%n",
		// totalSectorsOnDisk,"totalSectorsPerHead * dm.heads");
		// txtLog1.append(displayLine);
		// txtLog1.append(System.lineSeparator());
		//
		// int directoryStartSector = totalSectorsPerHead * dm.heads;
		// displayLine = String.format("totalSectorsOnDisk => \t%1$04X\t(%1$d)\t\t%2$s%n",
		// totalSectorsOnDisk,"totalSectorsPerHead * dm.heads");
		// txtLog1.append(displayLine);

	}// doBtnTwo

	private void doBtnThree() {

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
		;
		myPrefs = null;
		System.exit(0);
	}// appClose

	private void appInit() {
		Preferences myPrefs = Preferences.userNodeForPackage(ExploreDiskMetrics.class);
		frmExploreDiskMetrics.setSize(myPrefs.getInt("Width", 500), myPrefs.getInt("Height", 500));
		frmExploreDiskMetrics.setLocation(myPrefs.getInt("LocX", 100), myPrefs.getInt("LocY", 100));
		splitPane1.setDividerLocation(myPrefs.getInt("Divider", 250));
		currentDiskType = myPrefs.get("currentDiskType", "F3HD");
		lblCurrentDiskType.setText(currentDiskType);
		myPrefs = null;
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
		gbl_panelLeft.columnWidths = new int[] { 0 };
		gbl_panelLeft.rowHeights = new int[] { 0 };
		gbl_panelLeft.columnWeights = new double[] { Double.MIN_VALUE };
		gbl_panelLeft.rowWeights = new double[] { Double.MIN_VALUE };
		panelLeft.setLayout(gbl_panelLeft);

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
		scrollPane_1.setViewportView(txtLog1);

		lblLog1Header = new JLabel("New label");
		lblLog1Header.setHorizontalAlignment(SwingConstants.CENTER);
		lblLog1Header.setForeground(Color.BLUE);
		lblLog1Header.setFont(new Font("Courier New", Font.BOLD | Font.ITALIC, 18));
		scrollPane_1.setColumnHeaderView(lblLog1Header);
		splitPane1.setDividerLocation(250);

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

}// class GUItemplate