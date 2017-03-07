
package disks.utility;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import disks.DiskMetrics;
import hardware.Machine8080;
import memory.MemoryLoaderFromFile;
import utilities.FilePicker;

public class MakeNewDisk {

	public static File makeNewDisk() {
		
		String fileExtension = "F3HD";
//		JFileChooser fc = FilePicker.getDiskPicker("Disketts & Floppies", "F3ED", "F5DD", "F3DD", "F3HD", "F5HD",
//				"F8SS", "F8DS");
		JFileChooser fc = FilePicker.getDiskPicker();
		if (fc.showOpenDialog(null) == JFileChooser.CANCEL_OPTION) {
			System.out.println("Bailed out of the open");
			return null;
		} // if
		
		File pickedFile = fc.getSelectedFile();
			
		
		DiskMetrics diskMetric = DiskMetrics.getDiskMetric(fileExtension);
		if (diskMetric == null) {
			return null;
		} // if diskMetric

		String targetRawAbsoluteFileName = pickedFile.getAbsolutePath();
		String[] fileNameComponents = targetRawAbsoluteFileName.split("\\.");
		String targetAbsoluteFileName =  fileNameComponents[0] + "." + fileExtension;
		
		
		File selectedFile = new File(targetAbsoluteFileName);
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

		try (FileChannel fileChannel = new RandomAccessFile(selectedFile, "rw").getChannel();){
			MappedByteBuffer disk = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, diskMetric.getTotalBytes());
			ByteBuffer sector = ByteBuffer.allocate(diskMetric.bytesPerSector);
			int sectorCount = 0;
			while (disk.hasRemaining()) {
				sector = setUpBuffer(sector, sectorCount++);
				disk.put(sector);
			}//while
			
			
			/** set up as system disk **/
			
			Class<Machine8080> thisClass = Machine8080.class;
			/* Boot Sector */
			URL rom = thisClass.getResource("/disks/resources/BootSector.mem");
			byte[] dataBoot = MemoryLoaderFromFile.loadMemoryImage(new File(rom.getFile()),0x0200);
			disk.position(0);
			disk.put(dataBoot);
			/* CCP */
			 rom = thisClass.getResource("/disks/resources/CCP.mem");
			byte[] dataCCP = MemoryLoaderFromFile.loadMemoryImage(new File(rom.getFile()),0x0800);
			disk.put(dataCCP);
			/* BDOS */
			 rom = thisClass.getResource("/disks/resources/BDOS.mem");
			byte[] dataBDOS = MemoryLoaderFromFile.loadMemoryImage(new File(rom.getFile()),0x0E00);
			disk.put(dataBDOS);
			/* BIOS */
			 rom = thisClass.getResource("/disks/resources/BIOS.mem");
			byte[] dataBIOS = MemoryLoaderFromFile.loadMemoryImage(new File(rom.getFile()),0x0A00);
			disk.put(dataBIOS);
			
			fileChannel.force(true);
			fileChannel.close();
			disk = null;
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
