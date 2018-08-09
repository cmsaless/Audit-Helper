package code.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import code.model.DataModel;

public class CollectGUI {

	public CollectGUI(DataModel dataModel, JFrame primeFrame) {

		JFrame frame = new JFrame("Collect");

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(2,1));

		JLabel label = new JLabel("Would you like to overwrite the data you already have?");

		JPanel buttonPanel = new JPanel();

		JButton yesButton = new JButton("Yes");
		JButton noButton = new JButton("No");

		yesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
				primeFrame.setVisible(false);
				dataModel.beginCollecting(true);
				primeFrame.setVisible(true);
			}
		});

		noButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				primeFrame.setVisible(false);
				dataModel.beginCollecting(false);
				primeFrame.setVisible(true);
			}
		});

		buttonPanel.add(yesButton);
		buttonPanel.add(noButton);

		mainPanel.add(label, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		frame.add(mainPanel);

		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

}
