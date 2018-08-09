package code.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import code.model.DataModel;
import code.utils.FileUtils;

public class OptionsGUI {

	public OptionsGUI(DataModel dataModel) {

		JFrame frame = new JFrame("Options");

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(6,1));
		
		JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		JPanel urlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		JPanel applyPanel = new JPanel();

		JLabel fileLabel = new JLabel(" Enter CSV file name:", SwingConstants.LEFT);
		fileLabel.setVerticalAlignment(SwingConstants.BOTTOM);

		JTextField fileField = new JTextField(FileUtils.getFileSettings());
		fileField.setPreferredSize(new Dimension(200, 26));

		JLabel urlLabel = new JLabel(" Enter URL of snipe-it (http://xxx.xxx.x.xxx):", SwingConstants.LEFT);
		urlLabel.setVerticalAlignment(SwingConstants.BOTTOM);
		
		JTextField urlField = new JTextField(FileUtils.getURLSettings());
		urlField.setPreferredSize(new Dimension(200, 26));

		JButton browseButton = new JButton("Browse");
		browseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.showOpenDialog(null);
				File file = chooser.getSelectedFile();
				if (file != null) {
					fileField.setText(file.getAbsolutePath());
				}
			}
		});
		
		JButton applyButton = new JButton("Apply");
		applyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String fileInput = fileField.getText();
				String urlInput = urlField.getText();
				FileUtils.changeFileSettings(fileInput);
				FileUtils.changeURLSettings(urlInput);
				frame.dispose();
			}
		});

		filePanel.add(fileField);
		filePanel.add(browseButton);

		urlPanel.add(urlField);

		applyPanel.add(applyButton);

		mainPanel.add(fileLabel);
		mainPanel.add(filePanel);
		mainPanel.add(urlLabel);
		mainPanel.add(urlPanel);
		mainPanel.add(applyPanel);

		frame.add(mainPanel);

		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

}
