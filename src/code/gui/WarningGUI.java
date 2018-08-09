package code.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class WarningGUI {

	public WarningGUI(String message) {

		JFrame frame = new JFrame("WARNING");

		JLabel label = new JLabel(message, SwingConstants.CENTER);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
			}
		});

		frame.add(label, BorderLayout.CENTER);
		frame.add(okButton, BorderLayout.SOUTH);

		frame.setSize(new Dimension(200, 200));
		frame.setLocationRelativeTo(null);
		frame.setAlwaysOnTop(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

}
