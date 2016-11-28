package hardware;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import memory.MemoryLoaderFromFile;
import utilities.FilePicker;
import utilities.InLineDisassembler;
import utilities.hexEdit.HexEditPanelConcurrent;

public class Machine8080MenuAdapter implements ActionListener {
	private HexEditPanelConcurrent hexEditPanelConcurrent;

	public void setHexPanel(HexEditPanelConcurrent hexEditPanelConcurrent) {
		this.hexEditPanelConcurrent = hexEditPanelConcurrent;
	}//

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		System.out.println("actionPerformed");
		JMenuItem sourceMenu = null;
		String sourceName = null;

		if (actionEvent.getSource() instanceof JMenuItem) {
			sourceMenu = (JMenuItem) actionEvent.getSource();
			sourceName = sourceMenu.getName();
		} // if JMenuItem

		switch (sourceName) {
		case Machine8080.MNU_MEMORY_LOAD_FROM_FILE:
			doMemoryLoadFromFile(actionEvent);
			
			
			EventQueue.invokeLater(this.hexEditPanelConcurrent);
			 InLineDisassembler.getInstance().refreshDisplay();
			break;
		case Machine8080.MNU_CLEAR_ALL_FILES:
			removeAllFileItems((JPopupMenu) sourceMenu.getParent());
			break;
		case Machine8080.MNU_CLEAR_SELECTED_FILES:
			removeSelectedFileItems((JPopupMenu) sourceMenu.getParent());
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
		} // if - open
		String fileName = MemoryLoaderFromFile.loadMemoryImage(fc.getSelectedFile());
		System.out.printf("FileName: %s%n", fileName);

		JMenuItem sourceMenu = (JMenuItem) actionEvent.getSource();
		appendMenuItem(fileName, (JPopupMenu) sourceMenu.getParent());

	//	InLineDisassembler.getInstance().updateDisplay();

	}// doMemoryLoadFromFile
		// ----------------------------------

	private void appendMenuItem(String name, JPopupMenu parentMenu) {

		for (int i = parentMenu.getComponentCount() - 1; i > 0; i--) {
			if (!(parentMenu.getComponent(i) instanceof JCheckBoxMenuItem)) {
				continue;
			} // if right type
			if (((JCheckBoxMenuItem) parentMenu.getComponent(i)).getName().equals(name)) {
				return;
			} // if
		} // for do we already have it?

		JCheckBoxMenuItem mnuNew = new JCheckBoxMenuItem(name);
		mnuNew.setName(name);
		mnuNew.setActionCommand(name);
		parentMenu.add(mnuNew);

	}// appendMenuItem

	private void removeSelectedFileItems(JPopupMenu parentMenu) {
		for (int i = parentMenu.getComponentCount() - 1; i > 0; i--) {
			if (!(parentMenu.getComponent(i) instanceof JCheckBoxMenuItem)) {
				continue;
			} // if right type
			if (((JCheckBoxMenuItem) parentMenu.getComponent(i)).isSelected()) {
				parentMenu.remove(i);
			} // if do we remove it?
		} // for

	}// removeSelectedFileItem

	private void removeAllFileItems(JPopupMenu parentMenu) {
		for (int i = parentMenu.getComponentCount() - 1; i > 0; i--) {
			if (parentMenu.getComponent(i) instanceof JCheckBoxMenuItem) {
				parentMenu.remove(i);
			} // if right type
		} // for
	}// removeMenuItem

}// class Machine8080MenuAdapter.actionPerformed
