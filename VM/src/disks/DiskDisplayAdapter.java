package disks;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTextField;

public class DiskDisplayAdapter implements MouseListener {

	@Override
	public void mouseClicked(MouseEvent mouseEvent) {
		if(mouseEvent.getClickCount() >= 2){
//			System.out.printf(" %s: two or more clicks%n", Thread.currentThread().getName());
			JTextField source = (JTextField) mouseEvent.getComponent();
			String msg;
			if (source.getText().equals(DiskDisplay.NO_DISK)){
				msg = "Just mounted a disk";
				source.setText("Fake Disk");
			}else{
				msg = "Disk has been dismounted";
				source.setText(DiskDisplay.NO_DISK);
			}//if no disk
			System.out.printf("%s %n", msg);
		}//if 2 or more
	}//mouseClicked

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}//mouseEntered

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}//mouseExited

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}//mousePressed

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}//mouseReleased

}//DiskDisplayAdapter
