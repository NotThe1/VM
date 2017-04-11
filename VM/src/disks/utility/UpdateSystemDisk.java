package disks.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import javax.swing.JFileChooser;

import disks.DiskMetrics;
import hardware.Machine8080;
import memory.MemoryLoaderFromFile;
import utilities.FilePicker;

public class UpdateSystemDisk {

	public static void updateDisk(File selectedFile) {
		String fileExtension = "F3HD";
		DiskMetrics diskMetric = DiskMetrics.getDiskMetric(fileExtension);
		if (diskMetric == null) {
			System.err.printf("Bad disk type: %s%n", fileExtension);
			return;
		} // if diskMetric
		
		try (FileChannel fileChannel = new RandomAccessFile(selectedFile, "rw").getChannel();) {
			MappedByteBuffer disk = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, diskMetric.getTotalBytes());

			/** set up as system disk **/
			Class<Machine8080> thisClass = Machine8080.class;
			/* Boot Sector */
//			URL rom = thisClass.getResource("/disks/resources/BootSector.mem");
			
			InputStream in = thisClass.getClass().getResourceAsStream("/BootSector.mem"); 
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));

			byte[] dataBoot = MemoryLoaderFromFile.loadMemoryImage(reader, 0x0200);
			disk.position(0);
			disk.put(dataBoot);
			
			
			 in = thisClass.getClass().getResourceAsStream("/CCP.mem"); 
			 reader = new BufferedReader(new InputStreamReader(in));
				byte[] dataCCP = MemoryLoaderFromFile.loadMemoryImage(reader, 0x0800);
				disk.put(dataCCP);

			
			 in = thisClass.getClass().getResourceAsStream("/BDOS.mem"); 
			 reader = new BufferedReader(new InputStreamReader(in));
				byte[] dataBDOS = MemoryLoaderFromFile.loadMemoryImage(reader, 0x0E00);
				disk.put(dataBDOS);

			
			 in = thisClass.getClass().getResourceAsStream("/BIOS.mem"); 
			 reader = new BufferedReader(new InputStreamReader(in));
				byte[] dataBIOS = MemoryLoaderFromFile.loadMemoryImage(reader, 0x0A00);
				disk.put(dataBIOS);

			
			
			
			
			
			
			
//			URL rom;
//			 rom = thisClass.getClassLoader().getResource("BootSector.mem");
//			byte[] dataBoot = MemoryLoaderFromFile.loadMemoryImage(new File(rom.getFile()), 0x0200);
//			disk.position(0);
//			disk.put(dataBoot);
			
			
			/* CCP */
//			rom = thisClass.getResource("/CCP.mem");
//			byte[] dataCCP = MemoryLoaderFromFile.loadMemoryImage(new File(rom.getFile()), 0x0800);
//			disk.put(dataCCP);
			/* BDOS */
//			rom = thisClass.getResource("/BDOS.mem");
//			byte[] dataBDOS = MemoryLoaderFromFile.loadMemoryImage(new File(rom.getFile()), 0x0E00);
//			disk.put(dataBDOS);
			/* BIOS */
//			rom = thisClass.getResource("/BIOS.mem");
//			byte[] dataBIOS = MemoryLoaderFromFile.loadMemoryImage(new File(rom.getFile()), 0x0A00);
//			disk.put(dataBIOS);

			fileChannel.force(true);
			fileChannel.close();
			disk = null;
		} catch (IOException e) {
			e.printStackTrace();
		} // try
	}

	public static void updateDisk(String diskPath) {

		File selectedFile = new File(diskPath);
		if (!selectedFile.exists()) {
			System.err.printf("this file does not exist: %s%n", diskPath);
			return;
		} // if

		updateDisk(selectedFile);

	}// updateDisk

	public static void updateDisks() {
		JFileChooser fc = FilePicker.getDiskPicker();
		fc.setMultiSelectionEnabled(true);  // Override the default single selection.
		if (fc.showOpenDialog(null) == JFileChooser.CANCEL_OPTION) {
			System.out.println("Bailed out of the open");
			return;
		} // if

		File[] files = fc.getSelectedFiles();
		for (File file : files) {
			System.out.printf("File: %s%n", file);
			updateDisk(file);
		}

	}// updateDisks

}// class UpdateSystemDisk
