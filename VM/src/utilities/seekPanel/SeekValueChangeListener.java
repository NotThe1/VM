package utilities.seekPanel;

import java.util.EventListener;

public interface SeekValueChangeListener extends EventListener {
	/**
	 * listens for changes in the value in the SeekPanel
	 * @param seekValueChangeEvent
	 */
	public void valueChanged(SeekValueChangeEvent seekValueChangeEvent);
}//interface SeekValueChangeListener
