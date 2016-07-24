package utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * 
 * @author Frank Martyn
 *
 *         This Class follows the design pattern for a Factory class. It generates a specialized JFileChooser, that
 *         handles the identification of various files used by the VM application. All the file are found in the user's
 *         directory, or path passed on the constructor, in a directory called VMdata. Virtual disks are in a sub
 *         directory called "Disks". Each type of file is identified by its suffix.
 */

public class FilePicker {
	static String userDirectory;
	static Path dataPath = null;
	static Path diskPath = null;

	public FilePicker() {

	}// Constructor

	private static void setTargetPaths() {
		userDirectory = System.getProperty("user.home", ".");

		dataPath = Paths.get(userDirectory, DATA_NAME);

		if (!Files.exists(dataPath, LinkOption.NOFOLLOW_LINKS)) {
			System.out.println(dataPath.toAbsolutePath().toString() + " DOES NOT EXIST, creating ...");
			try {
				Files.createDirectory(dataPath);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, dataPath.toAbsolutePath().toString()
						+ "not Created", "unable to Create",
						JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
				return; // exit gracefully
			}// try - create
		}// if dataPath does not exist

		diskPath = Paths.get(userDirectory, DATA_NAME, DISK_NAME);
		if (!Files.exists(diskPath, LinkOption.NOFOLLOW_LINKS)) {
			System.out.println(diskPath.toAbsolutePath().toString() + " DOES NOT EXISTS creating ...");
			try {
				Files.createDirectory(diskPath);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, diskPath.toAbsolutePath().toString()
						+ "not Created", "unable to Create",
						JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
				return; // exit gracefully
			}// try - create
		}// if diskPath does not exist

	}// setTargetPaths

	/**
	 * getDataPicker will return a JFileChooser. The CurrentDirectory will be pointing at the data directory. It will
	 * build and populate the chooser with an extension filter configured to according to the arguments. The returned
	 * chooser is a single file chooser.
	 * 
	 * @param filterDescription
	 *            - a description of the type of file for the chooser
	 * @param filterExtensions
	 *            - a series of file extensions. One is required, the rest are optional
	 * @return a JFileChooser that will be enforce the file description/extensions filters
	 */
	public static JFileChooser getDataPicker(String filterDescription, String... filterExtensions) {
		setTargetPaths();
		JFileChooser chooser = new JFileChooser(dataPath.toString());
		return customizeChooser(chooser, filterDescription, filterExtensions);
	}// getDataPicker

	/**
	 * getDiskPicker will return a JFileChooser. The CurrentDirectory will be pointing at the data directory. It will
	 * build and populate the chooser with an extension filter configured to according to the arguments. The returned
	 * chooser is a single file chooser.
	 * 
	 * @param filterDescription
	 *            - a description of the type of virtual disk for the chooser
	 * @param filterExtensions
	 *            - a series of file extensions. One is required, the rest are optional
	 * @return a JFileChooser that will be enforce the file description/extensions filters
	 */
	public static JFileChooser getDiskPicker(String filterDescription, String... filterExtensions) {
		setTargetPaths();
		JFileChooser chooser = new JFileChooser(diskPath.toString());
		return customizeChooser(chooser, filterDescription, filterExtensions);

	}// getDiskPicker customize

	/**
	 * this is a utility function that does the common tasks for the methods: getDataPicker & getDiskPicker
	 * 
	 * @param chooser
	 *            - created by the above identified user functions, with directory set
	 * @param filterDescription
	 *            - a description of the type of file for the chooser
	 * @param filterExtensions
	 *            - a series of file extensions. One is required, the rest are optional
	 * @return the JFileChooser that will be enforce the file description/extensions filters
	 */
	private static JFileChooser customizeChooser(JFileChooser chooser, String filterDescription,
			String... filterExtensions) {
		JFileChooser customChooser = chooser;
		customChooser.setMultiSelectionEnabled(false);
		customChooser.addChoosableFileFilter(new FileNameExtensionFilter(filterDescription, filterExtensions));
		customChooser.setAcceptAllFileFilterUsed(false);
		return customChooser;
	}// customizeChooser

	private static final String DATA_NAME = "VMdata";
	private static final String DISK_NAME = "Disks";

}// class FilePicker
