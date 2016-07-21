package misc;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;

import javax.swing.JFrame;

import java.awt.GridBagLayout;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import memory.MemoryLoaderFromFile;
import utilities.FilePicker;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GUItestMemoryLoaderFromFile {

	private JFrame frame;
	private JMenu mnuFile;
	private JMenuItem mnuFileOpen;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUItestMemoryLoaderFromFile window = new GUItestMemoryLoaderFromFile();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}//main
	private void doOpen(){
		JFileChooser fc = FilePicker.getDataPicker("Memory Files", "mem","hex");
		if ( fc.showOpenDialog(null)== JFileChooser.CANCEL_OPTION){
			System.out.println("Bailed out of the open");
		}//if - open
		
		String fileName = MemoryLoaderFromFile.loadMemoryImage(fc.getSelectedFile());
		System.out.printf("FileName: %s%n", fileName);
		
	}//doOpen
	//----------------------------------------------------------------
	private void appClose() {
		Preferences myPrefs = Preferences.userNodeForPackage(GUItestMemoryLoaderFromFile.class);
		Dimension dim = frame.getSize();
		myPrefs.putInt("Height", dim.height);
		myPrefs.putInt("Width", dim.width);
		Point point = frame.getLocation();
		myPrefs.putInt("LocX", point.x);
		myPrefs.putInt("LocY", point.y);
		myPrefs = null;
	}//appClose

	@SuppressWarnings("unchecked")
	private void appInit() {
		Preferences myPrefs = Preferences.userNodeForPackage(GUItestMemoryLoaderFromFile.class);
		frame.setSize(318, 395);
		frame.setLocation(myPrefs.getInt("LocX", 100), myPrefs.getInt("LocY", 100));
		myPrefs = null;
	}//appInit
//		watchButtons = new WatchButtons();

	/**
	 * Create the application.
	 */
	public GUItestMemoryLoaderFromFile() {
		initialize();
		appInit();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0};
		gridBagLayout.rowHeights = new int[]{0};
		gridBagLayout.columnWeights = new double[]{Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		mnuFile = new JMenu("File");
		menuBar.add(mnuFile);
		
		mnuFileOpen = new JMenuItem("Open");
		mnuFileOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doOpen();
			}
		});
		mnuFile.add(mnuFileOpen);
	}//initialize

}//class GUItestMemoryLoaderFromFile
