package codeSupport.debug;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.text.StyledDocument;

import utilities.FilePicker;
import utilities.menus.MenuUtility;

public class ShowCode extends JDialog {
	
	private ShowFileAdapter showFileAdapter = new ShowFileAdapter();
	private StyledDocument doc;
	private Path newListPath;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ShowCode dialog = new ShowCode();
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}//main
	
	private void doAddFile(){
		JFileChooser fc = newListPath == null ? FilePicker.getMemPicker() : FilePicker.getMemPicker(newListPath);
		if (fc.showOpenDialog(null) == JFileChooser.CANCEL_OPTION) {
			System.out.println("Bailed out of the open");
			return;
		} // if - open
		newListPath = Paths.get(fc.getSelectedFile().getParent());
		addFile( newListPath);

	}//doAddFile()
	
	private void addFile(Path listFile){
		
	}//addFile
	
	private void doAddFilesFromList(){
		
	}//doAddFilesFromList
	
	private void doSaveSelectedToList(){
		
	}//doSaveSelectedTOList
	
	private void doClearSelectedFiles(){
		MenuUtility.clearListSelected(mnuFiles);
	}//doClearSelectedFiles
	
	private void doClearAllFiles(){
		MenuUtility.clearList(mnuFiles);
	}//doClearFiles
	
	// ===============================================================================
	public void close() {
		appClose();
	}// close

	private void appClose() {
		Preferences myPrefs = Preferences.userNodeForPackage(ShowCode.class);
		Dimension dim = getSize();
		myPrefs.putInt("Height", dim.height);
		myPrefs.putInt("Width", dim.width);
		Point point = getLocation();
		myPrefs.putInt("LocX", point.x);
		myPrefs.putInt("LocY", point.y);
		myPrefs = null;
		dispose();
	}// appClose

	private void appInit() {
		Preferences myPrefs = Preferences.userNodeForPackage(ShowCode.class);
		setSize(myPrefs.getInt("Height", 570), myPrefs.getInt("Width", 400));
		setLocation(myPrefs.getInt("LocX", 100), myPrefs.getInt("LocY", 100));
		myPrefs = null;
		this.setVisible(true);
		
		doc = tpDisplay.getStyledDocument();
	}// appInit

	// ---------------------------------------------------------------------------------------


	/**
	 * Create the dialog.
	 */
	public ShowCode() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				appClose();
			}//
		});
		initialize();
		appInit();
	}//
	
	public void initialize(){
		setTitle("Show Code");
//			setBounds(100, 100, 450, 300);
			GridBagLayout gridBagLayout = new GridBagLayout();
			gridBagLayout.columnWidths = new int[]{0, 0};
			gridBagLayout.rowHeights = new int[]{0, 30, 0};
			gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
			gridBagLayout.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
			getContentPane().setLayout(gridBagLayout);
			
			JScrollPane scrollPane = new JScrollPane();
			GridBagConstraints gbc_scrollPane = new GridBagConstraints();
			gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
			gbc_scrollPane.fill = GridBagConstraints.BOTH;
			gbc_scrollPane.gridx = 0;
			gbc_scrollPane.gridy = 0;
			getContentPane().add(scrollPane, gbc_scrollPane);
			
			tpDisplay = new JTextPane();
			tpDisplay.setFont(new Font("Courier New", Font.PLAIN, 14));
			scrollPane.setViewportView(tpDisplay);
			
			JLabel label = new JLabel("No Active File");
			label.setHorizontalAlignment(SwingConstants.CENTER);
			label.setForeground(Color.BLUE);
			label.setFont(new Font("Courier New", Font.BOLD, 16));
			scrollPane.setColumnHeaderView(label);
			
			JPanel panel = new JPanel();
			GridBagConstraints gbc_panel = new GridBagConstraints();
			gbc_panel.fill = GridBagConstraints.BOTH;
			gbc_panel.gridx = 0;
			gbc_panel.gridy = 1;
			getContentPane().add(panel, gbc_panel);
			GridBagLayout gbl_panel = new GridBagLayout();
			gbl_panel.columnWidths = new int[]{0};
			gbl_panel.rowHeights = new int[]{0};
			gbl_panel.columnWeights = new double[]{Double.MIN_VALUE};
			gbl_panel.rowWeights = new double[]{Double.MIN_VALUE};
			panel.setLayout(gbl_panel);
			
			JMenuBar menuBar = new JMenuBar();
			setJMenuBar(menuBar);
			
			mnuFiles = new JMenu("Files");
			menuBar.add(mnuFiles);
			
			JMenuItem mnuFilesAddFile = new JMenuItem("Add File");
			mnuFilesAddFile.addActionListener(showFileAdapter);
			mnuFiles.add(mnuFilesAddFile);
			
			JMenuItem mnuFilesAddFilesFromList = new JMenuItem("Add Files From List");
			mnuFilesAddFilesFromList.addActionListener(showFileAdapter);
			mnuFiles.add(mnuFilesAddFilesFromList);
			
			JSeparator separator = new JSeparator();
			mnuFiles.add(separator);
			
			JMenuItem mnuSaveSelectedToList = new JMenuItem("Save Selected To List");
			mnuSaveSelectedToList.addActionListener(showFileAdapter);
			mnuFiles.add(mnuSaveSelectedToList);
			
			JSeparator separator_1 = new JSeparator();
			separator_1.setName("recentFilesStart");
			mnuFiles.add(separator_1);
			
			JSeparator separator_2 = new JSeparator();
			separator_2.setName("recentFilesEnd");
			mnuFiles.add(separator_2);
			
			JMenuItem mnuClearSelectedFiles = new JMenuItem("Clear Selected Files");
			mnuClearSelectedFiles.addActionListener(showFileAdapter);
			mnuFiles.add(mnuClearSelectedFiles);
			
			JMenuItem mnuFilesClearAllFiles = new JMenuItem("Clear All Files");
			mnuFilesClearAllFiles.addActionListener(showFileAdapter);
			mnuFiles.add(mnuFilesClearAllFiles);
	
	}//initialize
	
	class ShowFileAdapter implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent actionEvent) {
			String name = ((ActionEvent) actionEvent.getSource()).getActionCommand();
			switch (name){
			case MNU_FILE_ADD_FILE:
				doAddFile();
				break;
				case MNU_FILE_ADD_FILES_FROM_LIST:
					doAddFilesFromList();
				break;
				case MNU_FILE_SAVE_SELECTED_TO_LIST:
					doSaveSelectedToList();
				break;
				case MNU_CLEAR_SELECTED_FILES:
					doClearSelectedFiles();
					
				break;
				case MNU_CLEAR_ALL_FILES:
					doClearAllFiles();
					
				break;
			}
			
		}//actionPerformed
		
	}//class ShowFileAdapter
	
	private static final String MNU_FILE_ADD_FILE = "mnuFileAddFile";
	private static final String MNU_FILE_ADD_FILES_FROM_LIST = "mnuFileAddFilesFromList";
	private static final String MNU_FILE_SAVE_SELECTED_TO_LIST = "mnuFilSaveSelectedToList";
	private static final String MNU_CLEAR_SELECTED_FILES = "mnuClearSelectedFiles";
	private static final String MNU_CLEAR_ALL_FILES = "mnuClearAllFiles";
	private JMenu mnuFiles;
	private JTextPane tpDisplay;

}//class ShowCode
