package codeSupport.debug;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import memory.Core.Trap;
import memory.CpuBuss;
import utilities.hdNumberBox.HDNumberBox;
import utilities.hdNumberBox.HDNumberValueChangeEvent;
import utilities.hdNumberBox.HDNumberValueChangeListener;

public class TrapManager extends JDialog {
	
	private static final long serialVersionUID = 1L;

	private DefaultListModel<String> trapModel = new DefaultListModel<String>();

	private static CpuBuss cpuBuss = CpuBuss.getInstance();
	private static TrapManager instance = new TrapManager();

	public static TrapManager getInstance() {
		return instance;
	}// getInstance

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			// TrapManager dialog = new TrapManager();
			TrapManager dialog = TrapManager.getInstance();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			// dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		} // try
	}// main
		// -------------------------------------------------------------------------------

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

	// ===============================================================================
	public void close() {
		appClose();
	}// close

	private void appClose() {
		Preferences myPrefs = Preferences.userNodeForPackage(TrapManager0.class);
		Dimension dim = getSize();
		myPrefs.putInt("Height", dim.height);
		myPrefs.putInt("Width", dim.width);
		Point point = getLocation();
		myPrefs.putInt("LocX", point.x);
		myPrefs.putInt("LocY", point.y);
		myPrefs = null;
		dispose();
		 hdNumber.removeHDNumberValueChangedListener(hdnListener);
	}// appClose

	private void appInit() {
		Preferences myPrefs = Preferences.userNodeForPackage(TrapManager.class);
		setSize(myPrefs.getInt("Width", 150), myPrefs.getInt("Height", 520));
		setLocation(myPrefs.getInt("LocX", 100), myPrefs.getInt("LocY", 100));
		myPrefs = null;
		this.setVisible(true);

		 hdNumber.setNumberModel(new SpinnerNumberModel(0, 0, 0XFFFF, 1));
		 hdNumber.setHexDisplay();
		 hdNumber.addHDNumberValueChangedListener(hdnListener);
		 trapModel.clear();
		 listTraps.setModel(trapModel);

	}// appInit

	// ---------------------------------------------------------------------------------------
	/**
	 * Create the dialog.
	 */
	private TrapManager() {
		initialize();
		appInit();
	}//Constructor

	private void initialize() {
		// setBounds(100, 100, 150, 522);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 140, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 10, 0, 0, 20, 0, 20, 33, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);
		panelTop.setBorder(new EmptyBorder(5, 5, 5, 5));
		GridBagConstraints gbc_panelTop = new GridBagConstraints();
		gbc_panelTop.anchor = GridBagConstraints.NORTH;
		gbc_panelTop.insets = new Insets(0, 0, 5, 0);
		gbc_panelTop.gridx = 0;
		gbc_panelTop.gridy = 0;
		getContentPane().add(panelTop, gbc_panelTop);
		panelTop.setLayout(new GridLayout(1, 0, 0, 0));

		btnEnable = new JButton(BTN_ENABLE);
		btnEnable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				if (btnEnable.getText().equals(BTN_ENABLE)) {
					btnEnable.setText(BTN_DISABLE);
					cpuBuss.setDebugTrapEnabled(true);
				} else {
					btnEnable.setText(BTN_ENABLE);
					cpuBuss.setDebugTrapEnabled(false);
				} // if
			}//actionPerformed
		});
		btnEnable.setMinimumSize(new Dimension(85, 23));
		btnEnable.setMaximumSize(new Dimension(85, 23));
		btnEnable.setPreferredSize(new Dimension(85, 23));
		panelTop.add(btnEnable);

		JButton btnReset = new JButton("Reset");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnEnable.setText(BTN_ENABLE);
				cpuBuss.setDebugTrapEnabled(false);
				cpuBuss.removeTraps(Trap.DEBUG);
				loadList();
			}//actionPerformed
		});
		btnReset.setMinimumSize(new Dimension(85, 23));
		btnReset.setMaximumSize(new Dimension(85, 23));
		btnReset.setPreferredSize(new Dimension(85, 23));
		GridBagConstraints gbc_btnReset = new GridBagConstraints();
		gbc_btnReset.anchor = GridBagConstraints.NORTH;
		gbc_btnReset.insets = new Insets(0, 0, 5, 0);
		gbc_btnReset.gridx = 0;
		gbc_btnReset.gridy = 1;
		getContentPane().add(btnReset, gbc_btnReset);

		JButton btnRemove = new JButton("Remove");
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Integer loc = Integer.valueOf((String) listTraps.getSelectedValue(), 16);
				System.out.printf("[actionPerformed]  %04X -  %n", loc);
				cpuBuss.removeTrap(loc, Trap.DEBUG);
				loadList();
			}// actionPerformed
		});
		btnRemove.setMinimumSize(new Dimension(85, 23));
		btnRemove.setMaximumSize(new Dimension(85, 23));
		btnRemove.setPreferredSize(new Dimension(85, 23));
		GridBagConstraints gbc_btnRemove = new GridBagConstraints();
		gbc_btnRemove.anchor = GridBagConstraints.NORTH;
		gbc_btnRemove.insets = new Insets(0, 0, 5, 0);
		gbc_btnRemove.gridx = 0;
		gbc_btnRemove.gridy = 3;
		getContentPane().add(btnRemove, gbc_btnRemove);

		JButton btnRemoveAll = new JButton("Remove All");
		btnRemoveAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cpuBuss.removeTraps(Trap.DEBUG);
				loadList();
			}
		});
		GridBagConstraints gbc_btnRemoveAll = new GridBagConstraints();
		gbc_btnRemoveAll.anchor = GridBagConstraints.NORTH;
		gbc_btnRemoveAll.insets = new Insets(0, 0, 5, 0);
		gbc_btnRemoveAll.gridx = 0;
		gbc_btnRemoveAll.gridy = 4;
		getContentPane().add(btnRemoveAll, gbc_btnRemoveAll);

		hdNumber = new HDNumberBox();
		hdNumber.setValue(1234);
		hdNumber.setMinimumSize(new Dimension(50, 23));
		GridBagConstraints gbc_hdNumber = new GridBagConstraints();
		gbc_hdNumber.insets = new Insets(0, 0, 5, 0);
		gbc_hdNumber.gridx = 0;
		gbc_hdNumber.gridy = 6;
		getContentPane().add(hdNumber, gbc_hdNumber);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setMinimumSize(new Dimension(75, 23));
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.VERTICAL;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 7;
		getContentPane().add(scrollPane, gbc_scrollPane);

		listTraps = new JList<String>();
		listTraps.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		scrollPane.setViewportView(listTraps);

		JPanel buttonPane = new JPanel();
		GridBagConstraints gbc_buttonPane = new GridBagConstraints();
		gbc_buttonPane.anchor = GridBagConstraints.NORTH;
		gbc_buttonPane.fill = GridBagConstraints.HORIZONTAL;
		gbc_buttonPane.gridx = 0;
		gbc_buttonPane.gridy = 8;
		getContentPane().add(buttonPane, gbc_buttonPane);

		GridBagLayout gbl_buttonPane = new GridBagLayout();
		gbl_buttonPane.columnWidths = new int[] { 65, 0 };
		gbl_buttonPane.rowHeights = new int[] { 23, 0 };
		gbl_buttonPane.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_buttonPane.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		buttonPane.setLayout(gbl_buttonPane);

		JButton btnHide = new JButton("Hide");
		btnHide.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ActionEvent) {
			instance.setVisible(false);
			}
		});
		btnHide.setPreferredSize(new Dimension(85, 23));
		btnHide.setMinimumSize(new Dimension(85, 23));
		btnHide.setMaximumSize(new Dimension(85, 23));
		btnHide.setActionCommand("Cancel");
		GridBagConstraints gbc_btnHide = new GridBagConstraints();
		gbc_btnHide.anchor = GridBagConstraints.NORTH;
		gbc_btnHide.gridx = 0;
		gbc_btnHide.gridy = 0;
		buttonPane.add(btnHide, gbc_btnHide);

	}// intialize

	private final JPanel panelTop = new JPanel();
	private HDNumberBox hdNumber;
	private JButton btnEnable;
	private JList<String> listTraps;
	
	private static final String BTN_ENABLE = "Enable";
	private static final String BTN_DISABLE = "Disable";


}//class TrapManager
