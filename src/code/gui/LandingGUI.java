package code.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import code.model.DataModel;

public class LandingGUI {

	public LandingGUI(DataModel dataModel) {

		JFrame frame = new JFrame("Startup");

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2, 1));

		JButton startButton = new JButton("START UP");

		JButton optionsButton = new JButton("OPTIONS");

		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {	
				frame.dispose();
				dataModel.startup();
			}
		});

		optionsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {	
				new OptionsGUI(dataModel);
			}
		});

		panel.add(startButton);
		panel.add(optionsButton);

		frame.add(panel);

		frame.setSize(250, 100);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

}
