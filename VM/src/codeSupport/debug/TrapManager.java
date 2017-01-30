package codeSupport.debug;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpinnerNumberModel;

import memory.Core.Trap;
import memory.CpuBuss;
import utilities.hdNumberBox.HDNumberBox;
import utilities.hdNumberBox.HDNumberValueChangeEvent;
import utilities.hdNumberBox.HDNumberValueChangeListener;

public class TrapManager {
	private static TrapManager instance = new TrapManager();
	private static CpuBuss cpuBuss = CpuBuss.getInstance();

	public static TrapManager getInstance() {
		return instance;
	}// getInstance

	private JFrame frame;
	private HDNumberBox hdNumber;
	private JList listTraps;
	private DefaultListModel<String> trapModel = new DefaultListModel<String>();

	HDNumberValueChangeListener hdnListener = new HDNumberValueChangeListener() {
		@Override
		public void valueChanged(HDNumberValueChangeEvent hDNumberValueChangeEvent) {
			int newValue = hDNumberValueChangeEvent.getNewValue();
			cpuBuss.addTrap(newValue, Trap.DEBUG);
			loadList();
		}// valueChanged
	};

	private void loadList() {
		trapModel.clear();
		List<Integer> locs = cpuBuss.getTraps(Trap.DEBUG);
		Collections.sort(locs);
		for (Integer loc : locs) {
			trapModel.addElement(String.format("%04X", loc));
		} // for

	}// loadList

	// ======================================================================================

	private void appClose() {
		Preferences myPrefs = Preferences.userNodeForPackage(TrapManager.class);
		Dimension dim = frame.getSize();
		myPrefs.putInt("Height", dim.height);
		myPrefs.putInt("Width", dim.width);
		Point point = frame.getLocation();
		myPrefs.putInt("LocX", point.x);
		myPrefs.putInt("LocY", point.y);
		myPrefs = null;
		hdNumber.removeHDNumberValueChangedListener(hdnListener);
	}// appClose

	private void appInit() {
		Preferences myPrefs = Preferences.userNodeForPackage(TrapManager.class);
		frame.setSize(myPrefs.getInt("Width", 250), myPrefs.getInt("Height", 500));
		frame.setLocation(myPrefs.getInt("LocX", 100), myPrefs.getInt("LocY", 100));
		myPrefs = null;
		frame.setVisible(true);

		hdNumber.setNumberModel(new SpinnerNumberModel(0, 0, 0XFFFF, 1));
		hdNumber.setHexDisplay();
		hdNumber.addHDNumberValueChangedListener(hdnListener);
		trapModel.clear();
		listTraps.setModel(trapModel);

	}// appInit

	// -------------------------------------------------------------------------------------------------
	/**
	 * Create the application.
	 */
	private TrapManager() {
		initialize();
		appInit();
	}// Constructor

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				appClose();
			}
		});
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		frame.getContentPane().setLayout(gridBagLayout);

		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.VERTICAL;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		frame.getContentPane().add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 40, 55, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		JButton btnEnable = new JButton(BTN_ENABLE);
		btnEnable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				if (btnEnable.getText().equals(BTN_ENABLE)) {
					btnEnable.setText(BTN_DISABLE);
					cpuBuss.setDebugTrapEnabled(true);
				} else {
					btnEnable.setText(BTN_ENABLE);
					cpuBuss.setDebugTrapEnabled(false);
				} // if
			}
		});
		GridBagConstraints gbc_btnEnable = new GridBagConstraints();
		gbc_btnEnable.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnEnable.insets = new Insets(0, 0, 5, 0);
		gbc_btnEnable.anchor = GridBagConstraints.NORTH;
		gbc_btnEnable.gridx = 1;
		gbc_btnEnable.gridy = 1;
		panel.add(btnEnable, gbc_btnEnable);

		JButton btnReset = new JButton("Reset");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnEnable.setText(BTN_ENABLE);
				cpuBuss.setDebugTrapEnabled(false);
				cpuBuss.removeTraps(Trap.DEBUG);
				loadList();
			}
		});
		GridBagConstraints gbc_btnReset = new GridBagConstraints();
		gbc_btnReset.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnReset.insets = new Insets(0, 0, 5, 0);
		gbc_btnReset.gridx = 1;
		gbc_btnReset.gridy = 2;
		panel.add(btnReset, gbc_btnReset);

		JButton btnRemove = new JButton("Remove");
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Integer loc = Integer.valueOf((String) listTraps.getSelectedValue(), 16);
				System.out.printf("[actionPerformed]  %04X -  %n", loc);
				cpuBuss.removeTrap(loc, Trap.DEBUG);
				loadList();
			}// actionPerformed
		});
		GridBagConstraints gbc_btnRemove = new GridBagConstraints();
		gbc_btnRemove.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnRemove.insets = new Insets(0, 0, 5, 0);
		gbc_btnRemove.gridx = 1;
		gbc_btnRemove.gridy = 4;
		panel.add(btnRemove, gbc_btnRemove);

		JButton btnRemoveAll = new JButton("Remove All");
		btnRemoveAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cpuBuss.removeTraps(Trap.DEBUG);
				loadList();
			}
		});
		GridBagConstraints gbc_btnRemoveAll = new GridBagConstraints();
		gbc_btnRemoveAll.insets = new Insets(0, 0, 5, 0);
		gbc_btnRemoveAll.gridx = 1;
		gbc_btnRemoveAll.gridy = 5;
		panel.add(btnRemoveAll, gbc_btnRemoveAll);

		hdNumber = new HDNumberBox();
		hdNumber.setPreferredSize(new Dimension(65, 23));
		hdNumber.setMinimumSize(new Dimension(50, 25));
		GridBagLayout gridBagLayout_1 = (GridBagLayout) hdNumber.getLayout();
		gridBagLayout_1.columnWidths = new int[] { 50, 0 };
		GridBagConstraints gbc_hdNumber = new GridBagConstraints();
		gbc_hdNumber.insets = new Insets(0, 0, 5, 0);
		gbc_hdNumber.gridx = 1;
		gbc_hdNumber.gridy = 7;
		panel.add(hdNumber, gbc_hdNumber);

		JButton btnAdd = new JButton("Add");
		btnAdd.setVisible(false);
		GridBagConstraints gbc_btnAdd = new GridBagConstraints();
		gbc_btnAdd.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnAdd.gridx = 1;
		gbc_btnAdd.gridy = 8;
		panel.add(btnAdd, gbc_btnAdd);

		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 1;
		frame.getContentPane().add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] { 30, 85, 0 };
		gbl_panel_1.rowHeights = new int[] { 0, 10, 0 };
		gbl_panel_1.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel_1.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		panel_1.setLayout(gbl_panel_1);

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 1;
		gbc_scrollPane.gridy = 0;
		panel_1.add(scrollPane, gbc_scrollPane);

		listTraps = new JList();
		scrollPane.setViewportView(listTraps);

	}// initialize

	private static final String EMPTY_STRING = "";
	private static final String BTN_ENABLE = "Enable";
	private static final String BTN_DISABLE = "Disable";

}// class TrapManager
