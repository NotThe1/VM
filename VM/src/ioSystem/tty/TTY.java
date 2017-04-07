package ioSystem.tty;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.Queue;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;

import codeSupport.ASCII_CODES;
import ioSystem.Device8080;

public class TTY extends Device8080 implements ActionListener, KeyListener {

	private Document screen;
	private int maxColumn;
	private boolean limitColumns;
	private int tabSize; // default for CP/M is 9

	private char lastKey;

	Queue<Byte> keyboardBuffer;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TTY window = new TTY();
					window.frmTemplate.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				} // try
			}// run
		});
	}// main

	/* Standard Stuff */

	@Override
	public byte byteToCPU(Byte address) {
		byte ans = (byte) 0XFF;
		if (address.equals(TTY_IN)) {
			if (!keyboardBuffer.isEmpty()) {
				ans = keyboardBuffer.poll();
			} // if
		} else if (address.equals(TTY_STATUS)) {
			ans = (byte) ((byte) keyboardBuffer.size() + 1);
		} else {
			ans = (byte) 0XFF;
		} // if
		return ans;
	}// byteToCPU

	private void processKeyTyped(KeyEvent keyEvent) {
		// place to call key mapping

		lastKey = keyEvent.getKeyChar();

		byte keyByte = (byte) keyEvent.getKeyChar();
		keyboardBuffer.add((byte) keyByte);

		showStatus();

	}// doKeyTyped

	// ------------- byte from CPU

	@Override
	public void byteFromCPU(Byte address, Byte value) {
		char c = (char) ((byte) value);

		if (c < 0X20) {
			switch (c) {
			case ASCII_CODES.BS:
				doBackSpace();
				break;
			case ASCII_CODES.TAB:
				doTab();
				break;
			case ASCII_CODES.LF: // 0X0A:
				display(Character.toString(c));
				break;
			case ASCII_CODES.CR: // ignore CR
				// Element rootElement = doc.getDefaultRootElement();
				// Element lastElement = rootElement.getElement(rootElement.getElementCount() - 1);
				// int start = lastElement.getStartOffset();
				// int end = lastElement.getEndOffset();
				// try {
				// System.out.printf("lastEmement = %s%n", doc.getText(start, end - start));
				// } catch (BadLocationException e) {
				//
				// e.printStackTrace();
				// }
				// System.out.printf("Start = %d, End =%d, size = %d%n%n", start, end, end - start);
				//
				// // display(Character.toString(c));
				break;
			default:

				break;
			}// switch

		} else if ((c >= 0X20) && (c <= 0X7F)) {
			// Printable characters
			displayPrintable(Character.toString(c));
		} else {
			// above ASCII
		} // if

	}// byteFromCPU

	private void doBtnClearScreen() {
		clearDoc();
	}// doBtnClearScreen

	private void doBtnClearKeyboardBuffer() {
		keyboardBuffer.clear();
		lastKey = ' ';
		showStatus();
	}// doBtnClearScreen

	private void doTruncate() {
		System.err.printf("%s NOT IMPLEMENTED%n", "doTruncate");
	}//

	private void doExtend() {
		System.err.printf("%s NOT IMPLEMENTED%n", "doExtend");
	}//

	private void doWrap() {
		System.err.printf("%s NOT IMPLEMENTED%n", "doWrap");
	}//

	private void doColorCaret() {
		System.err.printf("%s NOT IMPLEMENTED%n", "doColorCaret");
	}//

	private void doColorText() {
		System.err.printf("%s NOT IMPLEMENTED%n", "doColorText");
	}//

	private void doColorBackground() {
		System.err.printf("%s NOT IMPLEMENTED%n", "doColorBackground");
	}//

	private void doFont() {
		System.err.printf("%s NOT IMPLEMENTED%n", "doFont");
	}//

	private void displayPrintable(String s) {

		Element lastElement = getLastElement();

		if (!limitColumns) {// drop anything beyond the max column ??
			display(s);
		} else if ((lastElement.getEndOffset() - lastElement.getStartOffset()) < this.maxColumn) {
			display(s);
		} // if
		textScreen.setCaretPosition(screen.getDefaultRootElement().getEndOffset() - 1);

	}// displayPrintable

	private Element getLastElement() {
		Element rootElement = screen.getDefaultRootElement();
		return rootElement.getElement(rootElement.getElementCount() - 1);
	}// getLastElement

	private void display(String s) {
		// Element[] elements = doc.getRootElements();
		try {
			screen.insertString(screen.getLength(), s, null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}// display

	private void doTab() {
		Element lastElement = getLastElement();
		int column = lastElement.getEndOffset() - lastElement.getStartOffset();
		int numberOfSpaces = tabSize - (column % tabSize);
		for (int i = 0; i < numberOfSpaces; i++) {
			displayPrintable(SPACE);
		} // for
	}// doTab

	private void doBackSpace() {
		int currentPosition = screen.getLength();
		Element lastElement = getLastElement();
		System.out.printf("currentPosition = %d, getStartOffset = %d,getEndOffset= %d %n", currentPosition,
				lastElement.getStartOffset(), lastElement.getEndOffset());
		if (currentPosition > lastElement.getStartOffset()) {
			try {
				screen.remove(currentPosition - 1, 1);
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // try
		} // if
	}// doBackSpace

	public void clear() {
		clearDoc();
	}// clear

	private void clearDoc() {
		try {
			screen.remove(0, screen.getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}// clearDoc

	public void close() {
		appClose();
	}// close

	private void showStatus() {

		lblKeyChar.setText(String.format("Last Char = %s", lastKey));
		lblKeyText.setText(String.format("Keyboard buffer size = %d", keyboardBuffer.size()));
	}// showStatus

	// ----------------------------------------------------------

	private void appClose() {
		Preferences myPrefs = Preferences.userNodeForPackage(TTY.class).node(this.getClass().getSimpleName());
		Dimension dim = frmTemplate.getSize();
		myPrefs.putInt("Height", dim.height);
		myPrefs.putInt("Width", dim.width);
		Point point = frmTemplate.getLocation();
		myPrefs.putInt("LocX", point.x);
		myPrefs.putInt("LocY", point.y);
		myPrefs = null;

	}// appClose

	private void appInit() {
		Preferences myPrefs = Preferences.userNodeForPackage(TTY.class).node(this.getClass().getSimpleName());
		frmTemplate.setSize(myPrefs.getInt("Width", 761), myPrefs.getInt("Height", 693));
		frmTemplate.setLocation(myPrefs.getInt("LocX", 100), myPrefs.getInt("LocY", 100));
		limitColumns = false;
		maxColumn = 80;
		tabSize = 9;
		myPrefs = null;

		// textScreen.setCaret(new FancyCaret());
		textScreen.setCaretColor(Color.RED);
		textScreen.setEditable(false);
		textScreen.getCaret().setVisible(true);
		textScreen.addKeyListener(this);
		keyboardBuffer = new LinkedList<Byte>();

		screen = textScreen.getDocument();
		clearDoc();
		frmTemplate.setVisible(true);

	}// appInit

	public TTY() {
		super("tty", "Character", true, TTY_IN, true, TTY_OUT, TTY_STATUS);

		initialize();
		appInit();
	}// Constructor

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmTemplate = new JFrame();
		frmTemplate.setTitle("TTY");
		frmTemplate.setBounds(100, 100, 450, 300);
		// frmTemplate.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmTemplate.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				appClose();
			}
		});
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 25, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
		frmTemplate.getContentPane().setLayout(gridBagLayout);

		JPanel panelForButtons = new JPanel();
		GridBagConstraints gbc_panelForButtons = new GridBagConstraints();
		gbc_panelForButtons.anchor = GridBagConstraints.NORTH;
		gbc_panelForButtons.insets = new Insets(0, 0, 5, 0);
		gbc_panelForButtons.fill = GridBagConstraints.HORIZONTAL;
		gbc_panelForButtons.gridx = 0;
		gbc_panelForButtons.gridy = 0;
		frmTemplate.getContentPane().add(panelForButtons, gbc_panelForButtons);
		GridBagLayout gbl_panelForButtons = new GridBagLayout();
		gbl_panelForButtons.columnWidths = new int[] { 0, 0, 0 };
		gbl_panelForButtons.rowHeights = new int[] { 0, 0 };
		gbl_panelForButtons.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl_panelForButtons.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		panelForButtons.setLayout(gbl_panelForButtons);

		panelClear = new JPanel();
		panelClear.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, true), "Clear", TitledBorder.CENTER,
				TitledBorder.TOP, null, new Color(0, 0, 0)));
		GridBagConstraints gbc_panelClear = new GridBagConstraints();
		gbc_panelClear.insets = new Insets(0, 0, 0, 5);
		gbc_panelClear.fill = GridBagConstraints.HORIZONTAL;
		gbc_panelClear.gridx = 0;
		gbc_panelClear.gridy = 0;
		panelForButtons.add(panelClear, gbc_panelClear);
		GridBagLayout gbl_panelClear = new GridBagLayout();
		gbl_panelClear.columnWidths = new int[] { 0, 0, 0 };
		gbl_panelClear.rowHeights = new int[] { 0, 0 };
		gbl_panelClear.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl_panelClear.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panelClear.setLayout(gbl_panelClear);

		btnClearScreen = new JButton("Screen");
		btnClearScreen.setName(BTN_CLEAR_SCREEN);
		btnClearScreen.addActionListener(this);
		GridBagConstraints gbc_btnClearScreen = new GridBagConstraints();
		gbc_btnClearScreen.insets = new Insets(0, 0, 0, 5);
		gbc_btnClearScreen.gridx = 0;
		gbc_btnClearScreen.gridy = 0;
		panelClear.add(btnClearScreen, gbc_btnClearScreen);
		btnClearScreen.setMinimumSize(new Dimension(100, 20));
		btnClearScreen.setAlignmentX(Component.RIGHT_ALIGNMENT);
		btnClearScreen.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));

		btnClearScreen.setMaximumSize(new Dimension(0, 0));
		btnClearScreen.setPreferredSize(new Dimension(100, 20));

		btnClearKeyboardBuffer = new JButton("KeyBoard Buffer");
		btnClearKeyboardBuffer.setName(BTN_CLEAR_KEYBOARD_BUFFER);
		btnClearKeyboardBuffer.addActionListener(this);
		GridBagConstraints gbc_btnClearKeyboardBuffer = new GridBagConstraints();
		gbc_btnClearKeyboardBuffer.gridx = 1;
		gbc_btnClearKeyboardBuffer.gridy = 0;
		panelClear.add(btnClearKeyboardBuffer, gbc_btnClearKeyboardBuffer);
		btnClearKeyboardBuffer.setPreferredSize(new Dimension(100, 20));
		btnClearKeyboardBuffer.setMinimumSize(new Dimension(100, 20));
		btnClearKeyboardBuffer.setMaximumSize(new Dimension(0, 0));
		btnClearKeyboardBuffer.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		btnClearKeyboardBuffer.setAlignmentX(1.0f);

		panelColumns = new JPanel();
		panelColumns.setPreferredSize(new Dimension(90, 40));
		panelColumns.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, true), "Columns",
				TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0, 0)));
		GridBagConstraints gbc_panelColumns = new GridBagConstraints();
		gbc_panelColumns.fill = GridBagConstraints.HORIZONTAL;
		gbc_panelColumns.gridx = 1;
		gbc_panelColumns.gridy = 0;
		panelForButtons.add(panelColumns, gbc_panelColumns);
		GridBagLayout gbl_panelColumns = new GridBagLayout();
		gbl_panelColumns.columnWidths = new int[] { 0, 0, 0 };
		gbl_panelColumns.rowHeights = new int[] { 0, 0 };
		gbl_panelColumns.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_panelColumns.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panelColumns.setLayout(gbl_panelColumns);

		spinnerColumns = new JSpinner();
		spinnerColumns.setModel(new SpinnerNumberModel(new Integer(132), new Integer(20), null, new Integer(1)));
		GridBagConstraints gbc_spinnerColumns = new GridBagConstraints();
		gbc_spinnerColumns.fill = GridBagConstraints.VERTICAL;
		gbc_spinnerColumns.gridx = 1;
		gbc_spinnerColumns.gridy = 0;
		panelColumns.add(spinnerColumns, gbc_spinnerColumns);

		panelScreen = new JPanel();
		GridBagConstraints gbc_panelScreen = new GridBagConstraints();
		gbc_panelScreen.insets = new Insets(10, 10, 15, 10);
		gbc_panelScreen.fill = GridBagConstraints.BOTH;
		gbc_panelScreen.gridx = 0;
		gbc_panelScreen.gridy = 1;
		frmTemplate.getContentPane().add(panelScreen, gbc_panelScreen);
		GridBagLayout gbl_panelScreen = new GridBagLayout();
		gbl_panelScreen.columnWidths = new int[] { 371, 0 };
		gbl_panelScreen.rowHeights = new int[] { 2, 0 };
		gbl_panelScreen.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panelScreen.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		panelScreen.setLayout(gbl_panelScreen);

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		panelScreen.add(scrollPane, gbc_scrollPane);

		textScreen = new JTextArea();
		textScreen.setFont(new Font("Courier New", Font.PLAIN, 13));
		textScreen.setForeground(Color.GREEN);
		textScreen.setBackground(Color.BLACK);
		scrollPane.setViewportView(textScreen);

		JPanel panelStatus = new JPanel();
		panelStatus.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_panelStatus = new GridBagConstraints();
		gbc_panelStatus.fill = GridBagConstraints.BOTH;
		gbc_panelStatus.gridx = 0;
		gbc_panelStatus.gridy = 2;
		frmTemplate.getContentPane().add(panelStatus, gbc_panelStatus);
		panelStatus.setLayout(new GridLayout(0, 4, 0, 0));

		lblKeyChar = new JLabel("char = ");
		panelStatus.add(lblKeyChar);

		lblKeyText = new JLabel("keyText = ");
		panelStatus.add(lblKeyText);

		lblReleased = new JLabel("New label");
		panelStatus.add(lblReleased);

		menuBar = new JMenuBar();
		frmTemplate.setJMenuBar(menuBar);

		mnuBehavior = new JMenu("Behavior");
		menuBar.add(mnuBehavior); // Behavior

		mnuBehaviorTruncate = new JRadioButtonMenuItem("Truncate");
		mnuBehaviorTruncate.setName(MNU_BEHAVIOR_TRUNCATE);
		mnuBehaviorTruncate.addActionListener(this);
		mnuBehavior.add(mnuBehaviorTruncate);

		mnuBehaviorWrap = new JRadioButtonMenuItem("Wrap");
		mnuBehaviorWrap.setName(MNU_BEHAVIOR_WRAP);
		mnuBehaviorWrap.addActionListener(this);
		mnuBehavior.add(mnuBehaviorWrap);

		mnuBehaviorExtend = new JRadioButtonMenuItem("Extend");
		mnuBehaviorExtend.setName(MNU_BEHAVIOR_EXTEND);
		mnuBehaviorExtend.addActionListener(this);
		mnuBehavior.add(mnuBehaviorExtend);

		mnuProperties = new JMenu("Properties");
		menuBar.add(mnuProperties);

		mnuPropertiesColorsText = new JMenuItem("Text Color...");
		mnuPropertiesColorsText.setName(MNU_PROP_COLOR_TEXT);
		mnuPropertiesColorsText.addActionListener(this);
		
				mnuPropertiesFont = new JMenuItem("Font...");
				mnuPropertiesFont.setName(MNU_PROP_COLOR_TEXT);
				mnuPropertiesFont.addActionListener(this);
				mnuProperties.add(mnuPropertiesFont);
		
		separator = new JSeparator();
		mnuProperties.add(separator);
		mnuProperties.add(mnuPropertiesColorsText);
		
				mnuPropertiesColorsBack = new JMenuItem("Background Color...");
				mnuPropertiesColorsBack.setName(MNU_PROP_COLOR_BACK);
				mnuPropertiesColorsBack.addActionListener(this);
				mnuProperties.add(mnuPropertiesColorsBack);

		mnuPropertiesColorsCaret = new JMenuItem("Caret Color...");
		mnuPropertiesColorsCaret.setName(MNU_PROP_COLOR_CARET);
		mnuPropertiesColorsCaret.addActionListener(this);
		mnuProperties.add(mnuPropertiesColorsCaret);

	}// initialize

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		switch (((Component) actionEvent.getSource()).getName()) {
		case BTN_CLEAR_SCREEN:
			doBtnClearScreen();
			break;
		case BTN_CLEAR_KEYBOARD_BUFFER:
			doBtnClearKeyboardBuffer();
			break;

		case MNU_BEHAVIOR_TRUNCATE:
			doTruncate();
			break;
		case MNU_BEHAVIOR_EXTEND:
			doExtend();
			break;

		case MNU_BEHAVIOR_WRAP:
			doWrap();
			break;

		case MNU_PROP_COLOR_CARET:
			doColorCaret();
			break;

		case MNU_PROP_COLOR_TEXT:
			doColorText();
			break;

		case MNU_PROP_COLOR_BACK:
			doColorBackground();
			break;

		case MNU_PROP_FONT:
			doFont();
			break;

		}//

	}// actionPerformed

	@Override
	public void keyTyped(KeyEvent keyEvent) {
		processKeyTyped(keyEvent);
	}// keyTyped

	private JFrame frmTemplate;
	private JButton btnClearScreen;
	private JPanel panelScreen;
	private JMenuBar menuBar;
	private JMenu mnuProperties;
	private JMenuItem mnuPropertiesColorsText;
	private JMenuItem mnuPropertiesFont;
	private JTextArea textScreen;
	private JButton btnClearKeyboardBuffer;
	private JPanel panelClear;

	private static final byte TTY_IN = (byte) 0X0EC;
	private static final byte TTY_OUT = (byte) 0X0EC;
	private static final byte TTY_STATUS = (byte) 0X0ED;

	private static final String BTN_CLEAR_SCREEN = "btnClearScreen";
	private static final String BTN_CLEAR_KEYBOARD_BUFFER = "btnClearKeyboardBuffer";

	private static final String MNU_BEHAVIOR_TRUNCATE = "mnuBehaviorTruncate";
	private static final String MNU_BEHAVIOR_EXTEND = "mnuBehaviorExtend";
	private static final String MNU_BEHAVIOR_WRAP = "mnuBehaviorWrap";

	private static final String MNU_PROP_COLOR_CARET = "mnuPropertiesColorCaret";
	private static final String MNU_PROP_COLOR_TEXT = "mnuPropertiesColorText";
	private static final String MNU_PROP_COLOR_BACK = "mnuPropertiesColorsBack";
	private static final String MNU_PROP_FONT = "mnuPropertiesFont";

	private static final String SPACE = " "; // Space
	private JLabel lblKeyChar;
	private JLabel lblKeyText;
	private JLabel lblReleased;
	private JMenu mnuBehavior;
	private JRadioButtonMenuItem mnuBehaviorWrap;
	private JRadioButtonMenuItem mnuBehaviorTruncate;
	private JRadioButtonMenuItem mnuBehaviorExtend;
	private JPanel panelColumns;
	private JSpinner spinnerColumns;
	private JMenuItem mnuPropertiesColorsBack;
	private JMenuItem mnuPropertiesColorsCaret;
	private JSeparator separator;

	@Override
	public void keyPressed(KeyEvent arg0) {
	}// keyPressed

	@Override
	public void keyReleased(KeyEvent keyEvent) {
	}// keyReleased

}// class GUItemplate