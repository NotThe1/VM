package hardware;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;

import javax.swing.JFrame;

import java.awt.GridLayout;
import java.util.prefs.Preferences;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.SpinnerNumberModel;

import codeSupport.TableMaker;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JMenuItem;

import memory.Core;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Machine8080 {

	private Machine8080MenuAdapter menuAdapter;
//	private Core core = Core.getInstance();
//	private CentralProcessingUnit cpu = CentralProcessingUnit.

	private JFrame frmMachine;
	private JMenuItem mnuFileNew;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Machine8080 window = new Machine8080();
					window.frmMachine.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}// main
		// -------------------------------------------------------------------

	private void appClose() {
		Preferences myPrefs = Preferences.userNodeForPackage(TableMaker.class);
		Dimension dim = frmMachine.getSize();
		myPrefs.putInt("Height", dim.height);
		myPrefs.putInt("Width", dim.width);
		Point point = frmMachine.getLocation();
		myPrefs.putInt("LocX", point.x);
		myPrefs.putInt("LocY", point.y);
		myPrefs = null;

	}// appClose

	private void appInit() {
		// manage preferences
		Preferences myPrefs = Preferences.userNodeForPackage(TableMaker.class);
		frmMachine.setSize(myPrefs.getInt("Width", 650), myPrefs.getInt("Height", 722));
		frmMachine.setLocation(myPrefs.getInt("LocX", 100), myPrefs.getInt("LocY", 100));
		myPrefs = null;
		
		
		
		
		menuAdapter = new Machine8080MenuAdapter();

	}// appInit

	/**
	 * Create the application.
	 */
	public Machine8080() {
		initialize();
		appInit();
	}// Constructor

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmMachine = new JFrame();
		frmMachine.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				appClose();
			}
		});

		frmMachine.setTitle("CP/M System on Intel 8080 Processor");
		frmMachine.setBounds(100, 100, 450, 300);
		frmMachine.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMachine.getContentPane().setLayout(new GridLayout(1, 0, 0, 0));

		JMenuBar menuBar = new JMenuBar();
		frmMachine.setJMenuBar(menuBar);

		JMenu mnuFile = new JMenu("File");
		menuBar.add(mnuFile);

		mnuFileNew = new JMenuItem("New");
		mnuFileNew.setName(MNU_FILE_NEW);
		mnuFileNew.addActionListener(menuAdapter);

		mnuFile.add(mnuFileNew);

		JMenu mnuMemory = new JMenu("Memory");
		menuBar.add(mnuMemory);

		JMenu mnuDisks = new JMenu("Disks");
		menuBar.add(mnuDisks);

		JMenu mnuTools = new JMenu("Tools");
		menuBar.add(mnuTools);

		JMenu mnuWindows = new JMenu("Windows");
		menuBar.add(mnuWindows);
	}// initialize

	private static final String MNU_FILE_NEW = "mnuFileNew";
}// class Machine8080
