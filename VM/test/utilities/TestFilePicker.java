package utilities;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.JTextComponent;

import disks.DiskMetrics;

public class TestFilePicker implements ActionListener, MouseListener {

	private JFrame frmWindowOneSimple;
	private JFormattedTextField ftfOne;
	private JFormattedTextField ftfTwo;
	private JFormattedTextField ftfThree;
	private JFormattedTextField ftfFour;
	private JTextArea txtLog;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TestFilePicker window = new TestFilePicker();
					window.frmWindowOneSimple.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void doButtonOne(){
		JFileChooser fc = FilePicker.getAsmPicker();
		
		if (fc.showOpenDialog(null)== JFileChooser.APPROVE_OPTION){
			String msg = String.format("[doButton2] file name: %s %n", fc.getSelectedFile().getAbsolutePath());
			txtLog.append(msg);

		}else{
			int b= 0;
		}//if - open
		int c= 0;
		
	}//doButtonOne
	
	private void doDiskMetrics(){
		String diskType = "f5dd";
		txtLog.setText(String.format("    Disk Type: %s %n%n",  diskType ));
		DiskMetrics dm = DiskMetrics.getDiskMetric(diskType);
		
		txtLog.append(String.format("Heads: \t\t%1$02X\t(%1$d)%n", dm.heads));
		txtLog.append(String.format("Tracks\\Head: \t\t%1$04X\t(%1$d)%n", dm.tracksPerHead));
		txtLog.append(String.format("Sectors\\Track: \t\t%1$04X\t(%1$d)%n", dm.sectorsPerTrack));
		txtLog.append(String.format("Bytes\\Sector: \t\t%1$04X\t(%1$d)%n", dm.bytesPerSector));
		txtLog.append(String.format("Sectors\\Block: \t\t%1$04X\t(%1$d)%n", dm.sectorsPerBlock));
		txtLog.append(String.format("Directory Block Count: \t%1$04X\t(%1$d)%n%n", dm.directoryBlockCount));
		
		
		
		txtLog.append(String.format("SPT: \t%1$04X\t(%1$d)\t\t# of cmpRecords / logical Track%n", dm.getSPT()));
		txtLog.append(String.format("BSH: \t%1$04X\t(%1$d)\t\t Block Shift%n", dm.getBSH()));
		txtLog.append(String.format("BLM: \t%1$04X\t(%1$d)\t\t Block Mask%n", dm.getBLM()));
		txtLog.append(String.format("EXM: \t%1$04X\t(%1$d)\t\t Extent Mask%n", dm.getEXM()));
		txtLog.append(String.format("DSM: \t%1$04X\t(%1$d)\t\t Highest Block Number%n", dm.getDSM()));
		txtLog.append(String.format("DRM: \t%1$04X\t(%1$d)\t\t Highest Directory Entry Number%n", dm.getDRM()));
		txtLog.append(String.format("AL01: \t%1$04X\t(%1$d)\t\t Allocation pattern%n", dm.getAL01()));
		txtLog.append(String.format("CKS: \t%1$04X\t(%1$d)\t\t Checksum Vector size%n", dm.getCKS()));
		txtLog.append(String.format("OFF*: \t%1$04X\t(%1$d)\t\t Number of reserved sectors%n%n", dm.getDirectoryStartSector()));
		
		txtLog.append(String.format("TotalSectorsPerHead: \t%1$04X\t(%1$d)%n%n", dm.getTotalSectorsPerHead()));
		txtLog.append(String.format("TotalSectorsOnDisk: \t%1$04X\t(%1$d)%n%n", dm.getTotalSectorsOnDisk()));

		txtLog.append(String.format("BytesPerBlock: \t\t%1$04X\t(%1$d)%n%n", dm.getBytesPerBlock()));
		txtLog.append(String.format("SectorsPerBlock: \t%1$04X\t(%1$d)%n%n", dm.getSectorsPerBlock()));
		
		txtLog.append(String.format("DirectoryBlockCount: \t%1$04X\t(%1$d)%n%n", dm.getDirectoryBlockCount()));
		txtLog.append(String.format("DirectoryStartSector: \t%1$04X\t(%1$d)%n%n", dm.getDirectoryStartSector()));
		txtLog.append(String.format("DirectorysLastSector: \t%1$04X\t(%1$d)%n%n", dm.getDirectorysLastSector()));
		txtLog.append(String.format("MaxDirectoryEntries: \t%1$04X\t(%1$d)%n%n", dm.getMaxDirectoryEntries()));

		
		
		
		
		
		
		
		
		
		
	
	}//doDiskMetrics
	
	private void doButtonTwo(){
//		JFileChooser fc = new JFileChooser(FileSystemView.getFileSystemView());
		JFileChooser fc = new JFileChooser();
		
		/*    file chooser    */
		FileFilter filter = new FileNameExtensionFilter("Memory File","mem","hex");
//		fc.addChoosableFileFilter(filter);
		fc.setFileFilter(filter);
		/*    file chooser    */
		
//		if (fc.showDialog(null,"approvalButtonText")==JFileChooser.APPROVE_OPTION){
//		if (fc.showOpenDialog(null)==JFileChooser.APPROVE_OPTION){
		if (fc.showSaveDialog(null)==JFileChooser.APPROVE_OPTION){
			String msg = String.format("[doButton2] file name: %s %n", fc.getSelectedFile().getAbsolutePath());
			txtLog.append(msg);
		}else{
			int b= 0;
		}//if - open
		int c= 0;
		
	}//doButtonTwo
	private JFileChooser setFileNameExtensionFilter(JFileChooser fc){
		FileFilter filter = new FileNameExtensionFilter("Memory File","mem","hex");
		fc.addChoosableFileFilter(filter);
		return fc;
	}//setFileFilter

	@Override
	public void actionPerformed(ActionEvent ae) {
		String actionCommand = ae.getActionCommand();

		switch (actionCommand) {
		case "btnOne":
			doButtonOne();
			break;
		case "btnTwo":
			doButtonTwo();
			break;
		case "btnThree":
			break;
		case "btnReset":
			break;
		}

	}

	@Override
	public void mouseClicked(MouseEvent me) {
		if ((me.getClickCount() > 1) && (me.getComponent().getName() == "txtLog")) {
			((JTextComponent) me.getComponent()).setText("");
		} // if
	}// mouseClicked
		// ------------------------------------------------------------------------

	private void appClose() {
		Preferences myPrefs = Preferences.userNodeForPackage(TestFilePicker.class);
		Dimension dim = frmWindowOneSimple.getSize();
		myPrefs.putInt("Height", dim.height);
		myPrefs.putInt("Width", dim.width);
		Point point = frmWindowOneSimple.getLocation();
		myPrefs.putInt("LocX", point.x);
		myPrefs.putInt("LocY", point.y);
		myPrefs.put("ftfOne", ftfOne.getText());
		myPrefs.put("ftfTwo", ftfTwo.getText());
		myPrefs.put("ftfThree", ftfThree.getText());
		myPrefs.put("ftfFour", ftfFour.getText());
		myPrefs = null;
	}//

	private void initApp() {
		Preferences myPrefs = Preferences.userNodeForPackage(TestFilePicker.class);
		frmWindowOneSimple.setSize(968, 501);
		frmWindowOneSimple.setLocation(myPrefs.getInt("LocX", 100), myPrefs.getInt("LocY", 100));
		ftfOne.setText(myPrefs.get("ftfOne", EMPTY_SPACE));
		ftfTwo.setText(myPrefs.get("ftfTwo", EMPTY_SPACE));
		ftfThree.setText(myPrefs.get("ftfThree", EMPTY_SPACE));
		ftfFour.setText(myPrefs.get("ftfFour", EMPTY_SPACE));
		myPrefs = null;
		doDiskMetrics();
	}
	// ------------------------------------------------------------------------

	/**
	 * Create the application.
	 */
	public TestFilePicker() {
		initialize();
		initApp();
	}// WindowOneSimple

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmWindowOneSimple = new JFrame();
		frmWindowOneSimple.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				appClose();
			}
		});
		frmWindowOneSimple.setTitle("Test File Picker");
		frmWindowOneSimple.setBounds(100, 100, 662, 579);
		frmWindowOneSimple.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmWindowOneSimple.getContentPane().setLayout(null);
		// frame.getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 312, 429);
		frmWindowOneSimple.getContentPane().add(panel);
		panel.setLayout(null);

		ftfOne = new JFormattedTextField();
		ftfOne.setActionCommand("ftfOne");
		ftfOne.setName("ftfOne");
		ftfOne.addActionListener(this);
		ftfOne.setBounds(68, 40, 234, 20);
		panel.add(ftfOne);

		ftfTwo = new JFormattedTextField();
		ftfTwo.setActionCommand("ftfTwo");
		ftfTwo.setName("ftfTwo");
		ftfTwo.addActionListener(this);
		ftfTwo.setBounds(68, 82, 234, 20);
		panel.add(ftfTwo);

		ftfThree = new JFormattedTextField();
		ftfThree.setActionCommand("ftfThree");
		ftfThree.setName("ftfThree");
		ftfThree.addActionListener(this);
		ftfThree.setBounds(68, 126, 234, 20);
		panel.add(ftfThree);

		ftfFour = new JFormattedTextField();
		ftfFour.setActionCommand("ftfFour");
		ftfFour.setName("ftfFour");
		ftfFour.addActionListener(this);
		ftfFour.setBounds(68, 168, 234, 20);
		panel.add(ftfFour);

		JLabel lblOne = new JLabel("lblOne");
		lblOne.setBounds(12, 43, 46, 14);
		panel.add(lblOne);

		JLabel lblTwo = new JLabel("lblTwo");
		lblTwo.setBounds(12, 85, 46, 14);
		panel.add(lblTwo);

		JLabel lblThree = new JLabel("lblThree");
		lblThree.setBounds(12, 129, 46, 14);
		panel.add(lblThree);

		JLabel lblFour = new JLabel("lblFour");
		lblFour.setBounds(12, 171, 46, 14);
		panel.add(lblFour);

		JButton btnOne = new JButton("FilePicker");
		btnOne.setName("btnOne");
		btnOne.setActionCommand("btnOne");
		btnOne.addActionListener(this);
		btnOne.setBounds(128, 231, 91, 23);
		panel.add(btnOne);

		JButton btnTwo = new JButton("JFileChooser");
		btnTwo.setName("btnTwo");
		btnTwo.setActionCommand("btnTwo");
		btnTwo.addActionListener(this);
		btnTwo.setBounds(128, 280, 116, 23);
		panel.add(btnTwo);

		JButton btnThree = new JButton("btnThree");
		btnThree.setName("btnThree");
		btnThree.setActionCommand("btnThree");
		btnThree.addActionListener(this);
		btnThree.setBounds(128, 325, 91, 23);
		panel.add(btnThree);

		JButton btnReset = new JButton("Reset");
		btnReset.setName("bntReset");
		btnReset.setActionCommand("btnReset");
		btnReset.setBounds(211, 472, 91, 23);
		btnReset.addActionListener(this);
		panel.add(btnReset);

		JMenuBar menuBar = new JMenuBar();
		frmWindowOneSimple.setJMenuBar(menuBar);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(313, 0, 605, 429);
		frmWindowOneSimple.getContentPane().add(scrollPane);

		txtLog = new JTextArea();
		txtLog.setName("txtLog");
		txtLog.addMouseListener(this);
		txtLog.setEditable(false);
		scrollPane.setViewportView(txtLog);

	}
	// ------------------------------------------------------------------------

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}
	
	private final static String EMPTY_SPACE = "";

}//class TestFilePicker
