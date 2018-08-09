package code.gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.SocketException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;

import org.openqa.selenium.WebDriverException;

import code.model.DataModel;

public class LoginGUI {

	public LoginGUI(DataModel dataModel) {

		JFrame frame = new JFrame("Login");

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(3,1));

		JLabel label = new JLabel("Enter password:");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		
		JPasswordField passwordField = new JPasswordField();
		
		JButton button = new JButton("Enter");

		passwordField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				button.doClick();
			}
		});

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				char[] array = passwordField.getPassword();
				passwordField.setText("");
				boolean loginSuccessful = false;
				
				try {
					loginSuccessful = dataModel.login(charArrayToString(array));
					if (loginSuccessful) {
						frame.dispose();
						PrimeGUI primeGUI = new PrimeGUI(dataModel);
						dataModel.addObserver(primeGUI);
					} else {
						label.setForeground(Color.RED);
						label.setText("Incorrect password");
					}
				} catch (SocketException | WebDriverException ex) {
					actionPerformed(e);
				}
			} 
		});

		panel.add(label);
		panel.add(passwordField);
		panel.add(button);

		frame.add(panel);
		
		frame.setSize(250, 100);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public String charArrayToString(char[] array) {
		String retval = "";
		for (int i=0 ; i<array.length ; ++i) {
			retval += array[i];
		}
		return retval;
	}

}
