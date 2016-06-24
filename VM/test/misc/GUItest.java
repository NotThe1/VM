package misc;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;

import codeSupport.TableMaker;

import javax.swing.JMenuBar;

import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComboBox;

import java.awt.GridBagConstraints;

import javax.swing.JMenu;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;

import java.awt.Insets;

import javax.swing.JMenuItem;

public class GUItest {
	class menuAdapter implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent ae) {
			System.out.format("toString: %s%n", ae.toString());
		}//actionPerformed
	}//class menuAdapter


	private JFrame frmTest;
	DefaultComboBoxModel baseInstructionModel;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUItest window = new GUItest();
					window.frmTest.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}//main
	// <><><><><><><><><><><><><><><><><><><><><><><><><><><><><>

	private void appClose() {
		Preferences myPrefs = Preferences.userNodeForPackage(GUItest.class);
		Dimension dim = frmTest.getSize();
		myPrefs.putInt("Height", dim.height);
		myPrefs.putInt("Width", dim.width);
		Point point = frmTest.getLocation();
		myPrefs.putInt("LocX", point.x);
		myPrefs.putInt("LocY", point.y);
		myPrefs = null;
	}

	@SuppressWarnings("unchecked")
	private void appInit() {
		Preferences myPrefs = Preferences.userNodeForPackage(GUItest.class);
		frmTest.setSize(318, 395);
		frmTest.setLocation(myPrefs.getInt("LocX", 100), myPrefs.getInt("LocY", 100));
		myPrefs = null;
		Set<String> insKeys= baseInstructions.keySet();
		baseInstructionModel = new DefaultComboBoxModel(insKeys.toArray((new String[insKeys.size()])));
		cbIns.setModel(baseInstructionModel);
	}
	/**
	 * Create the application.
	 */
	public GUItest() {
		initialize();
		appInit();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmTest = new JFrame();
		frmTest.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				appClose();
			}
		});
		frmTest.setTitle("GUI Test");
		frmTest.setBounds(100, 100, 450, 300);
		frmTest.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frmTest.setJMenuBar(menuBar);
		
		mnuFile = new JMenu("File");
		
		menuBar.add(mnuFile);
		
		mnuFileOpen = new JMenuItem("Open...");
		mnuFileOpen.addActionListener(new ActionListener() {
			
				public void actionPerformed(ActionEvent arg0) {
//					FileSystem fs = new FileSystem();
					Path path1 = Paths.get("stuff");
					lblPath.setText(System.getProperty("user.home"));
					lblPath.setText(System.getProperty("user.dir"));
					 Path path = path1.toAbsolutePath();
					System.out.format("toString: %s%n", path.toString());
					System.out.format("getFileName: %s%n", path.getFileName());
					System.out.format("getName(0): %s%n", path.getName(0));
					System.out.format("getNameCount: %d%n", path.getNameCount());
					System.out.format("subpath(0,2): %s%n", path.subpath(0,2));
					System.out.format("getParent: %s%n", path.getParent());
					System.out.format("getRoot: %s%n", path.getRoot());
		}
		});
		mnuFile.add(mnuFileOpen);
		
		mntmAdapter = new JMenuItem("adapter");
		mntmAdapter.addActionListener(new menuAdapter());;
		
		mnuFile.add(mntmAdapter);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		frmTest.getContentPane().setLayout(gridBagLayout);
		
		cbIns = new JComboBox();
		GridBagConstraints gbc_cbIns = new GridBagConstraints();
		gbc_cbIns.insets = new Insets(0, 0, 5, 0);
		gbc_cbIns.fill = GridBagConstraints.HORIZONTAL;
		gbc_cbIns.gridx = 0;
		gbc_cbIns.gridy = 1;
		frmTest.getContentPane().add(cbIns, gbc_cbIns);
		
		lblPath = new JLabel("lblPath");
		GridBagConstraints gbc_lblPath = new GridBagConstraints();
		gbc_lblPath.gridx = 0;
		gbc_lblPath.gridy = 2;
		frmTest.getContentPane().add(lblPath, gbc_lblPath);
	}//initialize
	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private static HashMap<String, String[]> baseInstructions;
	private JComboBox cbIns;
	private JMenu mnuFile;
	private JLabel lblPath;
	private JMenuItem mnuFileOpen;
	private JMenuItem mntmAdapter;
	static {
		baseInstructions = new  HashMap<String, String[]>();
		baseInstructions.put("STC",new String[]{"STC","C","Set Carry"});
		baseInstructions.put("CMC",new String[]{"CMC","C","Complement Carry"});
		baseInstructions.put("INR",new String[]{"INR","Z,S,P,Aux,C","Increment Register/Memory"});
		baseInstructions.put("DCR",new String[]{"DCR","Z,S,P,Aux,C","Deccrement Register/Memory"});
		baseInstructions.put("CMA",new String[]{"CMA","None","Complement Acc"});
		baseInstructions.put("DAA",new String[]{"DAA","Z,S,P,Aux,C","Decimal Adjust Acc"});
		baseInstructions.put("NOP",new String[]{"NOP","None","No Operation"});
		baseInstructions.put("MOV",new String[]{"MOV","None","Move"});
		baseInstructions.put("STAX",new String[]{"STAX","None","Store Acc"});
		baseInstructions.put("LDAX",new String[]{"LDAX","None","Load Acc"});
		baseInstructions.put("ADD",new String[]{"ADD","Z,S,P,Aux,C","Add Register/Memory to Acc"});
		baseInstructions.put("ADC",new String[]{"ADC","Z,S,P,Aux,C","Add Register/Memory to Acc with Carry"});
	}//static
}// HashMap<String, String[]> baseCode;
