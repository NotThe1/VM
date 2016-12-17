package disks.diskPanel;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import hardware.Machine8080.DiskPanelAdapter;

public class DiskPanel extends JPanel {
	private JPanel panelDisksAB;
	public JLabel lblA;
	public JLabel lblB;
	private JTextField txtDiskA;
	private JTextField txtDiskB;
	private JPanel panelDisksCD;
	public JLabel lblC;
	public JLabel lblD;
	private JTextField txtDiskC;
	private JTextField txtDiskD;
	

	/**
	 * Create the panel.
	 */
	public DiskPanel(DiskPanelAdapter diskPanelAdapter) {
//		DiskPanelAdapter diskDisplayAdapter = new DiskPanelAdapter();
		
		

		//------------------
		setFont(new Font("Tahoma", Font.PLAIN, 14));
		setBorder(new TitledBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), "Disks", TitledBorder.CENTER, TitledBorder.BELOW_TOP, null, new Color(0, 0, 255)));
		setLayout(null);
		
		panelDisksAB = new JPanel();
		panelDisksAB.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, true), "3.5 \" Disks", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, null, Color.BLUE));
		panelDisksAB.setBounds(10, 30, 255, 100);
		add(panelDisksAB);
		panelDisksAB.setLayout(null);
		
		lblA = new JLabel("A:");
		lblA.setHorizontalAlignment(SwingConstants.RIGHT);
		lblA.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblA.setBounds(2, 31, 25, 14);
		panelDisksAB.add(lblA);
		
		lblB = new JLabel("B:");
		lblB.setHorizontalAlignment(SwingConstants.RIGHT);
		lblB.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblB.setBounds(2, 56, 25, 14);
		panelDisksAB.add(lblB);
		
		txtDiskA = new JTextField();
		txtDiskA.setEditable(false);
		txtDiskA.setName(TXT_DISK_A);
		txtDiskA.addMouseListener(diskPanelAdapter);
		txtDiskA.setToolTipText(NO_DISK_HELP);
		txtDiskA.setText(NO_DISK);
		txtDiskA.setHorizontalAlignment(SwingConstants.CENTER);
		txtDiskA.setBounds(45, 30, 200, 20);
		panelDisksAB.add(txtDiskA);
		txtDiskA.setColumns(10);
		
		txtDiskB = new JTextField();
		txtDiskB.setEditable(false);
		txtDiskB.setName(TXT_DISK_B);
		txtDiskB.addMouseListener(diskPanelAdapter);
		txtDiskB.setToolTipText(NO_DISK_HELP);
		txtDiskB.setText(NO_DISK);
		txtDiskB.setHorizontalAlignment(SwingConstants.CENTER);
		txtDiskB.setColumns(10);
		txtDiskB.setBounds(45, 55, 200, 20);
		panelDisksAB.add(txtDiskB);
		
		panelDisksCD = new JPanel();
		panelDisksCD.setLayout(null);
		panelDisksCD.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, true), "5.25 \" Disks", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, null, new Color(0, 0, 255)));
		panelDisksCD.setBounds(10, 154, 255, 100);
		add(panelDisksCD);
		
		lblC = new JLabel("C:");
		lblC.setHorizontalAlignment(SwingConstants.RIGHT);
		lblC.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblC.setBounds(2, 31, 25, 14);
		panelDisksCD.add(lblC);
		
		lblD = new JLabel("D:");
		lblD.setHorizontalAlignment(SwingConstants.RIGHT);
		lblD.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblD.setBounds(2, 56, 25, 14);
		panelDisksCD.add(lblD);
		
		txtDiskC = new JTextField();
		txtDiskC.setEditable(false);
		txtDiskC.setName(TXT_DISK_C);
		txtDiskC.addMouseListener(diskPanelAdapter);
		txtDiskC.setToolTipText(NO_DISK_HELP);
		txtDiskC.setText(NO_DISK);
		txtDiskC.setHorizontalAlignment(SwingConstants.CENTER);
		txtDiskC.setColumns(10);
		txtDiskC.setBounds(45, 30, 200, 20);
		panelDisksCD.add(txtDiskC);
		
		txtDiskD = new JTextField();
		txtDiskD.setEditable(false);
		txtDiskD.setName(TXT_DISK_D);
		txtDiskD.addMouseListener(diskPanelAdapter);
		txtDiskD.setToolTipText(NO_DISK_HELP);
		txtDiskD.setText(NO_DISK);
		txtDiskD.setHorizontalAlignment(SwingConstants.CENTER);
		txtDiskD.setColumns(10);
		txtDiskD.setBounds(45, 55, 200, 20);
		panelDisksCD.add(txtDiskD);
	}//Constructor
	public static final String NO_DISK = "<No Disk>";
	public static final String NO_DISK_HELP = "Double click to mount a disk";
	public static final String TXT_DISK_A = "txtDiskA";
	public static final String TXT_DISK_B = "txtDiskB";
	public static final String TXT_DISK_C = "txtDiskC";
	public static final String TXT_DISK_D = "txtDiskD";
}//class DiskDisplay
