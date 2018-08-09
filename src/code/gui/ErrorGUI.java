package code.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRootPane;
import javax.swing.SwingConstants;

public class ErrorGUI {

	public ErrorGUI(String message) {
		
		JFrame frame = new JFrame("ERROR");
		
		JLabel label = new JLabel(message, SwingConstants.CENTER);
		label.setForeground(Color.RED);
		
		JButton acceptDefeat = new JButton("Accept Defeat");
		acceptDefeat.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
				System.exit(0);
			}
		});
		
		frame.add(label, BorderLayout.CENTER);
		frame.add(acceptDefeat, BorderLayout.SOUTH);
		
		frame.setSize(new Dimension(200, 200));
		frame.setLocationRelativeTo(null);
		frame.setAlwaysOnTop(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setUndecorated(true);
		frame.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
		frame.setVisible(true);
	}
	
}
