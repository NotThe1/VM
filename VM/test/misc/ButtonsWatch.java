package misc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

public class ButtonsWatch implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		ButtonsValue.showMessage();
		ButtonsValue.showMessage(e.getSource().toString());
		
	}//actionPerformed

	

}//class WatchButtons
