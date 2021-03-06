package code.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;

import org.openqa.selenium.WebDriverException;

import code.model.DataModel;
import code.model.Ternary;
import code.utils.FileUtils;

public class PrimeGUI implements Observer {

	private DataModel _dataModel;

	private JFrame _frame;

	private JScrollPane _scrollPane;
	private JTextPane _textPane;

	private JComboBox<String> _comboBox;

	private JLabel _infoTotal;
	private JLabel _infoUnchecked;
	private JLabel _infoChecked;
	private JLabel _infoBlanks;
	private JLabel _infoNonBlanks;

	private JLabel _checkLabel;
	private JButton _checkButton;

	private JButton _downloadButton;
	private JTextField _urlDispField;

	/**
	 * The constructor creates an associative relationship between the
	 * _dataModel instance variable and the passed in instance of the DataModel
	 * class. Then we create a JFrame and add some various components to it
	 * before setting its size and making it visible.
	 * 
	 * @param dataModel - instance of the DataModel class
	 */
	public PrimeGUI(DataModel dataModel) {

		_dataModel = dataModel;

		_frame = new JFrame("Audit Assistant");

		_frame.setJMenuBar(getMenuBar());
		_frame.getContentPane().add(getControlPanel(), BorderLayout.NORTH);
		_frame.getContentPane().add(getDisplayPanel(), BorderLayout.CENTER);
		_frame.getContentPane().add(getURLPanel(), BorderLayout.SOUTH);

		_frame.setSize(new Dimension(1050, 775));
		_frame.setLocationRelativeTo(null);
		_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		_frame.setVisible(true);
	}

	/**
	 * This method creates a JMenuBar with various JMenus and JMenuItems to 
	 * populate it. Here are some comments on the various components.
	 * 
	 * fileMenu:
	 * --- initiateCollectItem: creates an instance of the CollectGUI
	 * --- resetItem: creates an instance of the ResetGUI
	 * --- offlineItem: if checked, we only use files we have saved locally;
	 * 					if unchecked, we use the web driver to grab user info 
	 * --- exitItem: saves the currently stored data and closes the program
	 * 
	 * filtersMenu:
	 * --- showAllButton: tells the DataModel not to filter by checked status
	 * --- showUncheckedButton: tells the DataModel to filter out checked users
	 * --- showCheckedButton: tells the DataModel to filter out unchecked users
	 * --- filterBlanksCheckBox: if checked, the DataModel will filter out
	 * 							 blank pages
	 * 
	 * infoMenu:
	 * --- _infoTotal: displays the total number of users
	 * --- _infoUnchecked: displays the number of users who are unchecked
	 * --- _infoChecked: displays the number of users who are checked
	 * --- _infoBlanks: displays the number of users who have blank pages
	 * --- _infoNonBlanks: displays the number of users who have non-blank 
	 * 					   pages
	 * 
	 * @return the menu bar for the JFrame
	 */
	public JMenuBar getMenuBar() {

		JMenuBar menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu("File");

		JMenuItem initiateCollectItem = new JMenuItem("Update Data");
		JMenuItem resetItem = new JMenuItem("Reset Checks");
		JCheckBoxMenuItem offlineItem = new JCheckBoxMenuItem("Go Offline");
		JMenuItem exitItem = new JMenuItem("Save & Exit");

		initiateCollectItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new CollectGUI(_dataModel, _frame);
			}
		});

		resetItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new ResetGUI(_dataModel);
			}
		});

		offlineItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (offlineItem.isSelected()) {
					_dataModel.setOffline(true);
				} else {
					_dataModel.setOffline(false);
				}
			}
		});

		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_frame.dispose();
				_dataModel.saveAndExit();
			}
		});

		fileMenu.add(initiateCollectItem);
		fileMenu.add(resetItem);
		fileMenu.addSeparator();
		fileMenu.add(offlineItem);
		fileMenu.addSeparator();
		fileMenu.add(exitItem);

		JMenu filtersMenu = new JMenu("Filters");

		JRadioButtonMenuItem showAllButton = new JRadioButtonMenuItem("Show All");
		JRadioButtonMenuItem showUncheckedButton = new JRadioButtonMenuItem("Show Unchecked");
		JRadioButtonMenuItem showCheckedButton = new JRadioButtonMenuItem("Show Checked");
		JCheckBoxMenuItem filterBlanksCheckBox = new JCheckBoxMenuItem("Filter Out Blanks");

		showAllButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_dataModel.setFilterOutByCheckedStatus(Ternary.NEITHER);
			}
		});

		showUncheckedButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_dataModel.setFilterOutByCheckedStatus(Ternary.CHECKED);
			}
		});

		showCheckedButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {	
				_dataModel.setFilterOutByCheckedStatus(Ternary.UNCHECKED);
			}
		});

		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(showAllButton);
		buttonGroup.add(showUncheckedButton);
		buttonGroup.add(showCheckedButton);

		showAllButton.doClick();

		filterBlanksCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean filter = filterBlanksCheckBox.isSelected();
				_dataModel.setFilterOutBlanks(filter);
			}
		});

		filtersMenu.add(showAllButton);
		filtersMenu.add(showUncheckedButton);
		filtersMenu.add(showCheckedButton);
		filtersMenu.addSeparator();
		filtersMenu.add(filterBlanksCheckBox);

		JMenu infoMenu = new JMenu("Info");

		_infoTotal = new JLabel(" Total: " + _dataModel.getTotalNumberOfUsers());
		_infoUnchecked = new JLabel(" Unchecked: " + _dataModel.getNumberOfUnchecked());
		_infoChecked = new JLabel(" Checked: " + _dataModel.getNumberOfChecked());
		_infoBlanks = new JLabel(" Blanks: " + _dataModel.getNumberOfBlanks());
		_infoNonBlanks = new JLabel(" Non-blanks: " + _dataModel.getNumberOfNonBlanks());

		infoMenu.add(_infoTotal);
		infoMenu.addSeparator();
		infoMenu.add(_infoUnchecked);
		infoMenu.add(_infoChecked);
		infoMenu.addSeparator();
		infoMenu.add(_infoBlanks);
		infoMenu.add(_infoNonBlanks);

		menuBar.add(fileMenu);
		menuBar.add(filtersMenu);
		menuBar.add(infoMenu);

		return menuBar;
	}

	/**
	 * This method creates the panel that contains the search bar, the search
	 * results, and various button. Here are some comments on the various
	 * components:
	 * 
	 * --- textField: this JTextField serves as a search bar for finding names
	 * --- searchButton: takes the text from textField and uses it to search
	 * 					 for names
	 * --- refreshButton: gets new results based on any changes to the filters
	 * 					  that may have been applied
	 * --- _checkLabel: displays a check mark if the currently selected person
	 * 				   has been checked or just a blank box if they are not
	 * --- _comboBox: contains the filtered search results from the DataModel
	 * 				  object
	 * --- _checkButton: sets the currently selected person's checked status to
	 * 					 either true or false 
	 * 
	 * @return JPanel containing various components that allow search 
	 * functionality 
	 */
	public JPanel getControlPanel() {

		JPanel parentPanel = new JPanel();
		parentPanel.setLayout(new GridLayout(2,1));

		JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		JTextField textField = new JTextField();
		textField.setPreferredSize(new Dimension(200, 26));

		JButton searchButton = new JButton("Search");

		JButton refreshButton = new JButton("Refresh");

		searchPanel.add(textField);
		searchPanel.add(searchButton);
		searchPanel.add(refreshButton);

		JPanel selectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		_checkLabel = new JLabel();
		_checkLabel.setIcon(_dataModel.getBlankCheckImage());

		_comboBox = new JComboBox<String>();
		_comboBox.setSize(new Dimension(200, 26));
		_comboBox.setRenderer(new CustomRenderer(_dataModel));

		_checkButton = new JButton("Check");
		_checkButton.setEnabled(false);

		selectPanel.add(_checkLabel);
		selectPanel.add(_comboBox);
		selectPanel.add(_checkButton);

		parentPanel.add(searchPanel);
		parentPanel.add(selectPanel);

		textField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) { searchButton.doClick(); }
		});

		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String query = textField.getText();
					_dataModel.generateListOfResults(query);
				} catch (SocketException | WebDriverException ex) {
					actionPerformed(e);
				}
			}
		});

		refreshButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_dataModel.refresh();
			}
		});

		_comboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("adjusting according to combobox");
				_dataModel.updateGUI();

			}
		});

		_checkButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String selectedName = (String) _comboBox.getSelectedItem();
				_dataModel.toggleCheckFor(selectedName);
				_dataModel.save();
			}
		});

		return parentPanel;
	}

	/**
	 * This method creates and returns a JPanel that contains a JScrollPane.
	 * When we select and user in order to see their page, this is where the
	 * HTML will be displayed. 
	 * 
	 * @return a JPanel that contains a scrollPane
	 */
	public JPanel getDisplayPanel() {

		JPanel displayPanel = new JPanel();

		_scrollPane = new JScrollPane(_textPane);
		_scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		_scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		_scrollPane.setPreferredSize(new Dimension(1000, 600));

		displayPanel.add(_scrollPane);

		return displayPanel;
	}

	/**
	 * This method creates the panel that contains the download button and a
	 * label that shows the location (either the URL or the file path) from
	 * where we got the HTML. Here are some comments on the various components:
	 * 
	 * @return
	 */
	public JPanel getURLPanel() {

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		_downloadButton = new JButton("Download");
		_downloadButton.setEnabled(false);
		_downloadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.showSaveDialog(null);

				String path = chooser.getSelectedFile().getAbsolutePath();
				FileUtils.exportHTML(_textPane.getPage().getPath(), path + "/" + _comboBox.getSelectedItem() + ".html");
				System.out.println(_textPane.getPage().getPath());
			}
		});

		_urlDispField = new JTextField();
		_urlDispField.setEditable(false);
		_urlDispField.setBackground(null);
		_urlDispField.setBorder(null);

		Font font = new Font(_urlDispField.getFont().getName(), Font.ITALIC + Font.BOLD, _urlDispField.getFont().getSize());

		_urlDispField.setFont(font);
		_urlDispField.setForeground(Color.GRAY);

		panel.add(_downloadButton);
		panel.add(_urlDispField);

		return panel;
	}

	public void displayInformation(String selectedName) throws IOException {

//		_dataModel.getHTMLFor(selectedName);

		_textPane = new JTextPane();
		_textPane.setEditable(false);
		_textPane.setContentType("text/html");
		try {
			_textPane.setPage(_dataModel.getHTMLFor(selectedName));
		} catch (MalformedURLException e) {

		}

		_scrollPane.setViewportView(_textPane);
		_urlDispField.setText(_dataModel.getPageURL());

		_frame.validate();
	}

	@Override
	public void update(Observable o, Object obj) {

		boolean resultsChanged = (boolean) obj;

		if (resultsChanged) {
			_comboBox.removeAllItems();
			for (String name : _dataModel.getFilteredResults()) {
				_comboBox.addItem(name);
			}
			System.out.println("# of results: " + _comboBox.getItemCount());
		}

		_infoTotal.setText(" Total: " + _dataModel.getTotalNumberOfUsers());
		_infoUnchecked.setText(" Unchecked: " + _dataModel.getNumberOfUnchecked());
		_infoChecked.setText(" Checked: " + _dataModel.getNumberOfChecked());
		_infoBlanks.setText(" Blanks: " + _dataModel.getNumberOfBlanks());
		_infoNonBlanks.setText(" Non-blanks: " + _dataModel.getNumberOfNonBlanks());

		String selectedName = "";
		selectedName = (String) _comboBox.getSelectedItem();
		boolean isChecked = _dataModel.getCheckedStatusFor(selectedName);

		ImageIcon icon = isChecked ? _dataModel.getCheckImage() : _dataModel.getBlankCheckImage();
		_checkLabel.setIcon(icon);

		String buttonText = isChecked? "Uncheck" : "Check";
		_checkButton.setText(buttonText);

		Color color = isChecked ? Color.GREEN : Color.RED;
		_scrollPane.setBorder(new LineBorder(color, 7));

		try {
			displayInformation(selectedName);
		} catch (IOException e) {
			e.printStackTrace();
		}

		boolean enabled = _comboBox.getItemCount() == 0 ? false : true; 
		_checkButton.setEnabled(enabled);
		_downloadButton.setEnabled(enabled);
	}

}
