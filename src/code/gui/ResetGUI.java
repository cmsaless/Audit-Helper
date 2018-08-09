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

public class ResetGUI {

	public ResetGUI(DataModel dataModel) {
		
		JFrame frame = new JFrame("Reset");
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(2,1));
		
		JLabel label = new JLabel("Reset all the checked statuses?");
		
		JPanel buttonPanel = new JPanel();
		
		JButton yesButton = new JButton("Yes");
		JButton noButton = new JButton("No");
		
		yesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
				dataModel.resetCheckedStatus();
			}
		});
		
		noButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
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
