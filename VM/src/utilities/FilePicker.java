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
 * 
 *         examples: JFileChooser fc = FilePicker.getDataPicker("Memory Image Files", "mem", "hex");
 */

public class FilePicker {
	static String userDirectory;
	static Path dataPath = null;
	static Path diskPath = null;
	static Path memoryPath = null;
	static Path asmPath = null;
	static Path listPath = null;

	private FilePicker() {

	}// Constructor

	private static void setTargetPaths(String subjectName) {
		userDirectory = System.getProperty("user.home", ".");

		dataPath = Paths.get(userDirectory, DATA_NAME);
		if (!Files.exists(dataPath, LinkOption.NOFOLLOW_LINKS)) {
			System.out.println(dataPath.toAbsolutePath().toString() + " DOES NOT EXIST, creating ...");
			try {
				Files.createDirectory(dataPath);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, dataPath.toAbsolutePath().toString() + "not Created",
						"unable to Create", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
				return; // exit gracefully
			} // try - create
		} // if dataPath does not exist

		if (subjectName != null) {
			setSpecificTargetPath(subjectName);
		} // if

		// diskPath = Paths.get(userDirectory, DATA_NAME, DISK_NAME);
		// if (!Files.exists(diskPath, LinkOption.NOFOLLOW_LINKS)) {
		// System.out.println(diskPath.toAbsolutePath().toString() + " DOES NOT EXISTS creating ...");
		// try {
		// Files.createDirectory(diskPath);
		// } catch (IOException e) {
		// JOptionPane.showMessageDialog(null, diskPath.toAbsolutePath().toString()
		// + "not Created", "unable to Create",
		// JOptionPane.ERROR_MESSAGE);
		// e.printStackTrace();
		// return; // exit gracefully
		// }// try - create
		// }// if diskPath does not exist

	}// setTargetPaths

	private static void setSpecificTargetPath(String subjectName) {
		Path subjectPath = Paths.get(userDirectory, DATA_NAME, subjectName);
		switch (subjectName) {

		case DISK_NAME:
			diskPath = subjectPath;
			// diskPath = Paths.get(userDirectory, DATA_NAME, subjectName);
			break;
		case LISTS:
			listPath = subjectPath;
			// listAsmPath = Paths.get(userDirectory, DATA_NAME, subjectName);
			break;
		}// switch

		if (!Files.exists(subjectPath, LinkOption.NOFOLLOW_LINKS)) {
			System.out.println(subjectPath.toAbsolutePath().toString() + " DOES NOT EXISTS creating ...");
			try {
				Files.createDirectory(subjectPath);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, subjectPath.toAbsolutePath().toString() + "not Created",
						"unable to Create", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
				return; // exit gracefully
			} // try - create
		} // if diskPath does not exist
	}// setSpecificTargetPath

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
		setTargetPaths(null);
		return customizeChooser(dataPath, filterDescription, filterExtensions);
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
		setTargetPaths(DISK_NAME);
		return customizeChooser(diskPath, filterDescription, filterExtensions);
	}// getDiskPicker customize
	
	public static JFileChooser getDiskPicker(){
		setTargetPaths(DISK_NAME);
		return customizeChooser(diskPath,"Disketts & Floppies","F3ED", "F5DD", "F3DD", "F3HD", "F5HD","F8SS", "F8DS");
	}//getDiskPicker default
	
	//"Disketts & Floppies", "F3ED", "F5DD", "F3DD", "F3HD", "F5HD","F8SS", "F8DS"

	public static JFileChooser getListAsmPicker() {
		setTargetPaths(LISTS);
		return customizeChooser(listPath, "Listing Files Lists", LIST_ASM_SUFFIX);
	}// getDiskPicker customize

	public static JFileChooser getListMemPicker() {
		setTargetPaths(LISTS);
		return customizeChooser(listPath, "Memory Files Lists", LIST_MEM_SUFFIX);
	}// getDiskPicker customize
	
	public static JFileChooser getAnyListPicker() {
		setTargetPaths(LISTS);
		return customizeChooser(listPath, "Memory and Listing Files Lists", LIST_MEM_SUFFIX, LIST_ASM_SUFFIX);
	}// getDiskPicker customize
	
	public static JFileChooser getMemPicker() {
		memoryPath = Paths.get(CODE_PATH);
		return customizeChooser(memoryPath, "Memory Files ", "mem","hex");
	}// getDiskPicker customize
	
	public static JFileChooser getMemPicker(Path newMemoryPath) {
		memoryPath = newMemoryPath;
		return customizeChooser(memoryPath, "Memory Files ", "mem","hex");
	}// getDiskPicker customize
	
	public static JFileChooser getAsmPicker() {
		asmPath = Paths.get(CODE_PATH);
		return customizeChooser(asmPath,  "Listing Files ", "list");
	}// getDiskPicker customize
	
	public static JFileChooser getAsmPicker(Path newMemoryPath) {
		asmPath = newMemoryPath;
		return customizeChooser(asmPath, "Listing Files ", "list");
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
	private static JFileChooser customizeChooser(Path path, String filterDescription, String... filterExtensions) {
		JFileChooser customChooser = new JFileChooser(path.toString());
		customChooser.setMultiSelectionEnabled(false);
		customChooser.addChoosableFileFilter(new FileNameExtensionFilter(filterDescription, filterExtensions));
		customChooser.setAcceptAllFileFilterUsed(false);
		return customChooser;
	}// customizeChooser

	private static final String DATA_NAME = "VMdata";
	private static final String DISK_NAME = "Disks";
	private static final String MEMORY_NAME = "Memory";
	private static final String ASM_NAME = "Asm";
	private static final String LISTS = "Lists";
//	private static final String LIST_MEM = "listMem";
	private static final String CODE_PATH = "C:\\Users\\admin\\git\\assembler8080\\assembler8080\\Code";

	public static final String LIST_ASM_SUFFIX = "ListAsm";
	public static final String LIST_MEM_SUFFIX = "ListMem";

}// class FilePicker
