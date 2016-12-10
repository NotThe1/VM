package disks.utility;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
//import java.nio.file.Path;
//import java.nio.file.Paths;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
//import javax.swing.filechooser.FileNameExtensionFilter;

import disks.DiskMetrics;
import utilities.FilePicker;

public class MakeNewDisk {

	public static File makeNewDisk() {

		JFileChooser fc = FilePicker.getDiskPicker("Disketts & Floppies", "F3ED", "F5DD", "F3DD", "F3HD", "F5HD",
				"F8SS", "F8DS");
		if (fc.showOpenDialog(null) == JFileChooser.CANCEL_OPTION) {
			System.out.println("Bailed out of the open");
			return null;
		} // if

		File selectedFile = fc.getSelectedFile();
		String fileName = selectedFile.getName();
		String fileExtension = "";
		String[] fileNameComponents = fileName.split("\\.");
		try {
			fileExtension = fileNameComponents[1].toUpperCase();
		} catch (Exception e) {
			return null;
		} // try

		DiskMetrics diskMetric = DiskMetrics.getDiskMetric(fileExtension);
		if (diskMetric == null) {
			return null;
		} // if diskMetric

		String targetAbsoluteFileName = selectedFile.getAbsolutePath();
		if (selectedFile.exists()) {
			if (JOptionPane.showConfirmDialog(null, "File already exists do you want to overwrite it?",
					"YES - Continue, NO - Cancel", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
				return null;
			} else {
				selectedFile.delete();
				selectedFile = null;
				selectedFile = new File(targetAbsoluteFileName);
			} // inner if
		} // if - file exists

		try {
			@SuppressWarnings("resource")
			FileChannel fileChannel = new RandomAccessFile(selectedFile, "rw").getChannel();
			MappedByteBuffer disk = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, diskMetric.getTotalBytes());
			ByteBuffer sector = ByteBuffer.allocate(diskMetric.bytesPerSector);
			int sectorCount = 0;
			while (disk.hasRemaining()) {
				sector = setUpBuffer(sector, sectorCount++);
				disk.put(sector);
			}//while
			fileChannel.force(true);
			fileChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}//try

		return selectedFile;
	}// makeNewDisk

	private static ByteBuffer setUpBuffer(ByteBuffer sector, int value) {
		sector.clear();
		// set value to be put into sector
		Byte byteValue = (byte) 0x00; // default to null
		Byte MTfileVlaue = (byte) 0xE5; // deleted file value
		Byte workingValue;
		while (sector.hasRemaining()) {
			workingValue = ((sector.position() % 0x20) == 0) ? MTfileVlaue : byteValue;
			sector.put(workingValue);
		} // while
		sector.flip();
		return sector;
	}//setUpBuffer

}// class MakeNewDisk
