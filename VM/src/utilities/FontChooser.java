package utilities;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.SystemColor;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class FontChooser extends JDialog implements ListSelectionListener {

	private static final long serialVersionUID = 1L;
	
	private final static Integer DEFAULT_SIZE = 13;
	private final static String DEFAULT_STYLE = "Plain";
	private final static String DEFAULT_FAMILY = "Tahoma";

	private final static String STYLE_PLAIN = "Plain";
	private final static String STYLE_BOLD = "Bold";
	private final static String STYLE_ITALIC = "Italic";
	private final static String STYLE_BOLD_ITALIC = "Bold Italic";

	
	
	private Font selectedFont;

	
	public static void main(String[] args) {
		try {
			FontChooser dialog = new FontChooser();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}//try
	}//main
	
	private void doSelection(){
		textFamily.setText((String) listFamily.getSelectedValue());
		textStyle.setText((String) listStyle.getSelectedValue());
		textSize.setText( listSize.getSelectedValue().toString());
		int style = style = Font.PLAIN;;
		switch(textStyle.getText()){
			case STYLE_PLAIN:
				style = Font.PLAIN;
				break;
			case STYLE_BOLD:
				style = Font.BOLD;
				break;
			case STYLE_ITALIC:
				style = Font.ITALIC;
				break;
			case STYLE_BOLD_ITALIC:
				style = Font.BOLD | Font.ITALIC;
				break;
		}//switch
		
		selectedFont = new Font(textFamily.getText(),style,Integer.valueOf(textSize.getText()));
		lblSelectedFont.setFont(selectedFont);
		
		
		String display = String.format("%s %s %s", textFamily.getText(),textStyle.getText(),textSize.getText());
		lblSelectedFont.setText(display);
		
	}//doSelection
	//---------------------------------------------------------------

	private void appInit() {
		DefaultListModel<Integer> sizeModel = new DefaultListModel<Integer>();
		for (Integer i = 5; i < 100; i++) {
			sizeModel.addElement(i);
		} //
		listSize.setModel(sizeModel);
		listSize.setSelectedValue(DEFAULT_SIZE, true);

		String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		DefaultListModel<String> familyModel = new DefaultListModel<String>();
		for (String f : fontNames) {
			familyModel.addElement(f);
		} // for
		listFamily.setModel(familyModel);
		listFamily.setSelectedValue(DEFAULT_FAMILY, true);
		
		String[] styles = new String[] { STYLE_PLAIN, STYLE_BOLD, STYLE_ITALIC, "Bold Italic" };
		DefaultListModel<String> styleModel = new DefaultListModel<String>();
		for (String s : styles) {
			styleModel.addElement(s);
		} // for
		listStyle.setModel(styleModel);
		listStyle.setSelectedValue(DEFAULT_STYLE, true);
		
		listSize.addListSelectionListener(this);
		listFamily.addListSelectionListener(this);
		listStyle.addListSelectionListener(this);
		
		doSelection();
	}// appInit

	private void appClose() {

	}// appClose

	public void close() {

	}// close

	public FontChooser() {
		initialize();
		appInit();
	}// initalize

	/**
	 * Create the dialog.
	 */
	private void initialize() {
		setTitle("Font chooser");
		setBounds(100, 100, 494, 500);
		getContentPane().setLayout(null);
		contentPanel.setBounds(0, 0, 478, 428);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel);
		contentPanel.setLayout(null);

		JPanel panelSelected = new JPanel();
		panelSelected.setBounds(10, 11, 458, 77);
		panelSelected.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Selected Font",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		contentPanel.add(panelSelected);
		panelSelected.setLayout(new BorderLayout(0, 0));

		lblSelectedFont = new JLabel("Selected Font");
		lblSelectedFont.setPreferredSize(new Dimension(90, 26));
		lblSelectedFont.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblSelectedFont.setHorizontalAlignment(SwingConstants.CENTER);
		panelSelected.add(lblSelectedFont);

		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setBounds(10, 114, 458, 290);
		contentPanel.add(panel);
		panel.setLayout(null);

		JLabel lblNewLabel = new JLabel("Family:");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel.setBounds(10, 11, 46, 14);
		panel.add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("Style:");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_1.setBounds(244, 11, 46, 14);
		panel.add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("Size:");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_2.setBounds(373, 11, 46, 14);
		panel.add(lblNewLabel_2);

		textFamily = new JTextField();
		textFamily.setEditable(false);
		textFamily.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textFamily.setBackground(UIManager.getColor("Panel.background"));
		textFamily.setBounds(10, 34, 221, 20);
		panel.add(textFamily);
		textFamily.setColumns(10);

		textStyle = new JTextField();
		textStyle.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textStyle.setEditable(false);
		textStyle.setColumns(10);
		textStyle.setBackground(SystemColor.menu);
		textStyle.setBounds(244, 34, 106, 20);
		panel.add(textStyle);

		textSize = new JTextField();
		textSize.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textSize.setEditable(false);
		textSize.setColumns(10);
		textSize.setBackground(SystemColor.menu);
		textSize.setBounds(373, 34, 75, 20);
		panel.add(textSize);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 65, 221, 214);
		panel.add(scrollPane);

		listFamily = new JList();
		scrollPane.setViewportView(listFamily);
		listFamily.setBorder(new LineBorder(new Color(0, 0, 0)));
		listFamily.setFont(new Font("Tahoma", Font.PLAIN, 11));
		listFamily.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(254, 65, 96, 214);
		panel.add(scrollPane_1);

		listStyle = new JList();
		listStyle.setModel(new AbstractListModel() {
			String[] values = new String[] { "Plain", "Bold", "Italic", "Bold Italic" };

			public int getSize() {
				return values.length;
			}

			public Object getElementAt(int index) {
				return values[index];
			}
		});
		scrollPane_1.setViewportView(listStyle);
		listStyle.setBorder(new LineBorder(new Color(0, 0, 0)));
		listStyle.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listStyle.setFont(new Font("Tahoma", Font.PLAIN, 12));

		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(373, 65, 75, 214);
		panel.add(scrollPane_2);

		listSize = new JList();
		scrollPane_2.setViewportView(listSize);
		listSize.setBorder(new LineBorder(new Color(0, 0, 0)));
		listSize.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listSize.setFont(new Font("Tahoma", Font.PLAIN, 11));

		JPanel buttonPane = new JPanel();
		buttonPane.setBounds(0, 428, 478, 33);
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane);

		JButton okButton = new JButton("OK");
		okButton.setPreferredSize(new Dimension(90, 26));
		okButton.setMinimumSize(new Dimension(100, 23));
		okButton.setMaximumSize(new Dimension(150, 23));
		okButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
		cancelButton.setMinimumSize(new Dimension(80, 23));
		cancelButton.setMaximumSize(new Dimension(80, 23));
		cancelButton.setPreferredSize(new Dimension(90, 26));
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);

	}// initialize
	private final JPanel contentPanel = new JPanel();
	private JLabel lblSelectedFont;
	private JTextField textFamily;
	private JTextField textStyle;
	private JTextField textSize;
	private JList<String> listFamily;
	private JList<String> listStyle;
	private JList<Integer> listSize;


	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		doSelection();
	}//valueChanged
}// class FontChooser
