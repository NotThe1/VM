package disks;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;

import javax.swing.JFrame;

import java.awt.GridBagLayout;

import javax.swing.JPanel;

import codeSupport.TableMaker;

import java.awt.GridBagConstraints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.Preferences;

public class DiskDisplayAndAdapterTest {

	private JFrame frameDisks;
	private JPanel panelDiskDisplay;
	
	private DiskDisplay diskDisplay;
//	private JPanel panel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DiskDisplayAndAdapterTest window = new DiskDisplayAndAdapterTest();
					window.frameDisks.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}//main
	
	private void appClose() {
		Preferences myPrefs = Preferences.userNodeForPackage(DiskDisplayAndAdapterTest.class);
		Dimension dim = frameDisks.getSize();
		myPrefs.putInt("Height", dim.height);
		myPrefs.putInt("Width", dim.width);
		Point point = frameDisks.getLocation();
		myPrefs.putInt("LocX", point.x);
		myPrefs.putInt("LocY", point.y);
		myPrefs = null;

	}// appClose

	private void appInit() {
		// manage preferences
		Preferences myPrefs = Preferences.userNodeForPackage(DiskDisplayAndAdapterTest.class);
		frameDisks.setSize(341, 345);
		frameDisks.setLocation(myPrefs.getInt("LocX", 100), myPrefs.getInt("LocY", 100));
		myPrefs = null;
}
		
	private void appInit0(){
		diskDisplay = new DiskDisplay();
	}//appInit

	/**
	 * Create the application.
	 */
	public DiskDisplayAndAdapterTest() {
		appInit0();
		initialize();
		appInit();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frameDisks = new JFrame();
		frameDisks.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				appClose();
			}
		});

		frameDisks.setBounds(100, 100, 469, 445);
		frameDisks.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		frameDisks.getContentPane().setLayout(gridBagLayout);
		
		panelDiskDisplay = new JPanel();
		panelDiskDisplay.setLayout(null);
		GridBagConstraints gbc_panelDiskDisplay = new GridBagConstraints();
		gbc_panelDiskDisplay.fill = GridBagConstraints.BOTH;
		gbc_panelDiskDisplay.gridx = 1;
		gbc_panelDiskDisplay.gridy = 1;
		frameDisks.getContentPane().add(panelDiskDisplay, gbc_panelDiskDisplay);
		
		//panel = new JPanel();
		diskDisplay.setBounds(2, 2, 260, 260);
		panelDiskDisplay.add(diskDisplay);
		diskDisplay.setLayout(null);
	}

}
