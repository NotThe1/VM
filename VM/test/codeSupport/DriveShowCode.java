package codeSupport;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;

import codeSupport.debug.ShowCode;
import utilities.hdNumberBox.HDNumberBox;

public class DriveShowCode {
	
	private static ShowCode showCode;

	private JFrame frame;
	private JButton btnTxtLog;
	private JScrollPane scrollPane;
	private JTextArea txtLog;
	private HDNumberBox hdNumber;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DriveShowCode window = new DriveShowCode();
					window.frame.setVisible(true);
					showCode = ShowCode.getInstance();
					showCode.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	

	/**
	 * Create the application.
	 */
	public DriveShowCode() {
		

	/**
	 * Initialize the contents of the frame.
	 */
	
		frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				showCode.close();
			}
		});
		frame.setBounds(100, 100, 429, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 20, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		
		JButton btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Thread task = new Thread(showCode);
				task.start();
			}
		});
		GridBagConstraints gbc_btnStart = new GridBagConstraints();
		gbc_btnStart.insets = new Insets(0, 0, 5, 5);
		gbc_btnStart.gridx = 1;
		gbc_btnStart.gridy = 0;
		frame.getContentPane().add(btnStart, gbc_btnStart);
		
		JButton btnPC = new JButton("Send");
		btnPC.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
//				showCode.setProgramCounter((int)spinner.getValue());
				showCode.setProgramCounter((int)hdNumber.getValue());
			}
		});
		
		btnTxtLog = new JButton("Text Log");
		btnTxtLog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			txtLog.setVisible(!txtLog.isVisible());
			}
		});
		GridBagConstraints gbc_btnTxtLog = new GridBagConstraints();
		gbc_btnTxtLog.anchor = GridBagConstraints.ABOVE_BASELINE_TRAILING;
		gbc_btnTxtLog.insets = new Insets(0, 0, 5, 5);
		gbc_btnTxtLog.gridx = 1;
		gbc_btnTxtLog.gridy = 1;
		frame.getContentPane().add(btnTxtLog, gbc_btnTxtLog);
		
		scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 4;
		gbc_scrollPane.gridy = 2;
		frame.getContentPane().add(scrollPane, gbc_scrollPane);
		
		txtLog = new JTextArea();
		scrollPane.setViewportView(txtLog);
		GridBagConstraints gbc_btnPC = new GridBagConstraints();
		gbc_btnPC.insets = new Insets(0, 0, 0, 5);
		gbc_btnPC.gridx = 1;
		gbc_btnPC.gridy = 3;
		frame.getContentPane().add(btnPC, gbc_btnPC);
		
//		hexSpinner = new Hex64KSpinner();
//		GridBagConstraints gbc_hexSpinner = new GridBagConstraints();
//		gbc_hexSpinner.insets = new Insets(0, 0, 0, 5);
//		gbc_hexSpinner.gridx = 2;
//		gbc_hexSpinner.gridy = 3;
//		frame.getContentPane().add(hexSpinner, gbc_hexSpinner);
		
		hdNumber = new HDNumberBox();
		hdNumber.setPreferredSize(new Dimension(50, 23));
		hdNumber.setMinimumSize(new Dimension(20, 25));
		hdNumber.setHexDisplay();
		hdNumber.setNumberModel(new SpinnerNumberModel(0,0,0XFFFF,1));
		GridBagConstraints gbc_hdNumber = new GridBagConstraints();
		gbc_hdNumber.insets = new Insets(0, 0, 0, 5);
		gbc_hdNumber.fill = GridBagConstraints.HORIZONTAL;
		gbc_hdNumber.gridx = 2;
		gbc_hdNumber.gridy = 3;
		frame.getContentPane().add(hdNumber, gbc_hdNumber);
	}

}
