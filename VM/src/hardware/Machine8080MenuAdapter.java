package hardware;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import memory.MemoryLoaderFromFile;
import utilities.FilePicker;

public class Machine8080MenuAdapter implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		System.out.println("actionPerformed");

		String sourceName = ((JMenuItem) actionEvent.getSource()).getName();
		switch (sourceName) {
		case Machine8080.MNU_MEMORY_LOAD_FROM_FILE:
			doMemoryLoadFromFile(actionEvent);
			break;
		default:
			assert false : sourceName + " is not a valid menu item\n";
		}// Switch sourceName

	}// actionPerformed

	private void doMemoryLoadFromFile(ActionEvent actionEvent) {
		JFileChooser fc = FilePicker.getDataPicker("Memory Image Files", "mem", "hex");
		if (fc.showOpenDialog(null) == JFileChooser.CANCEL_OPTION) {
			System.out.println("Bailed out of the open");
			return;
		}// if - open

		String fileName = MemoryLoaderFromFile.loadMemoryImage(fc.getSelectedFile());
		System.out.printf("FileName: %s%n", fileName);

		JMenuItem sourceMenu = (JMenuItem) actionEvent.getSource();

		appendMenuItem(fileName, (JPopupMenu) sourceMenu.getParent());
	}// doMemoryLoadFromFile
		// ----------------------------------

	private void appendMenuItem(String name, JPopupMenu parentMenu) {
		JCheckBoxMenuItem mnuNew = new JCheckBoxMenuItem(name);
		mnuNew.setName(name);
		mnuNew.setActionCommand(name);
		parentMenu.add(mnuNew);

	}// appendMenuItem

}// class Machine8080MenuAdapter.actionPerformed
