package code.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingConstants;

import code.utils.FileUtils;

public class SplashScreen {

	private final JFrame _splashScreen;
	
	/**
	 * The constructor creates a splash screen with some text and (if found) a picture.
	 */
	public SplashScreen() {
		
		_splashScreen = new JFrame();
		
		JPanel panel = new JPanel();
		
		JLabel textLabel = new JLabel("<html><center>Starting up...<br><br>This fantastic program was made in the summer<br>of 2018 by our beloved intern, Chris Saless!</html>", SwingConstants.CENTER);
		
		JLabel picLabel = new JLabel();
		try {
			ImageIcon icon = new ImageIcon(getClass().getResource(FileUtils.FACE_PIC_LOC));
			picLabel.setIcon(icon);
		} catch (NullPointerException e) {
			picLabel.setText("***No Pic***");
		}
		
		panel.add(textLabel, BorderLayout.CENTER);
		panel.add(picLabel, BorderLayout.SOUTH);
		
		_splashScreen.add(panel, BorderLayout.CENTER);
		
		_splashScreen.setSize(new Dimension(300, 300));
		_splashScreen.setLocationRelativeTo(null);
		_splashScreen.setAlwaysOnTop(true);
		_splashScreen.setUndecorated(true);
		_splashScreen.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
		_splashScreen.setVisible(true);
	}
	
	/**
	 * This function disposes the JFrame, thus closing the splash screen.
	 */
	public void close() {
		_splashScreen.dispose();
	}
	
}
