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

public class GUItemplate {

	private JFrame frmReflec;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUItemplate window = new GUItemplate();
					window.frmReflec.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}// main

	/* Standard Stuff */

	private void appClose() {
		Preferences myPrefs = Preferences.userNodeForPackage(GUItemplate.class);
		Dimension dim = frmReflec.getSize();
		myPrefs.putInt("Height", dim.height);
		myPrefs.putInt("Width", dim.width);
		Point point = frmReflec.getLocation();
		myPrefs.putInt("LocX", point.x);
		myPrefs.putInt("LocY", point.y);
		myPrefs = null;
	}//appClose

	private void appInit() {
		Preferences myPrefs = Preferences.userNodeForPackage(GUItemplate.class);
		frmReflec.setSize(myPrefs.getInt("Width", 500), myPrefs.getInt("Height", 500));
		frmReflec.setLocation(myPrefs.getInt("LocX", 100), myPrefs.getInt("LocY", 100));
		myPrefs = null;
	}// appInit

	public GUItemplate() {
		initialize();
		appInit();
	}// Constructor

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmReflec = new JFrame();
		frmReflec.setTitle("GUItemplate");
		frmReflec.setBounds(100, 100, 450, 300);
		frmReflec.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0 };
		gridBagLayout.rowHeights = new int[] { 0 };
		gridBagLayout.columnWeights = new double[] { Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { Double.MIN_VALUE };
		frmReflec.getContentPane().setLayout(gridBagLayout);

		JMenuBar menuBar = new JMenuBar();
		frmReflec.setJMenuBar(menuBar);
		frmReflec.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				appClose();
			}
		});
	}// initialize

}// class GUItemplate