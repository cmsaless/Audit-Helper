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
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.openqa.selenium.WebDriverException;

import code.model.DataModel;

public class LoginGUI {

	public LoginGUI(DataModel dataModel) {

		JFrame frame = new JFrame("Login");

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(4,1));

		JLabel label = new JLabel("Enter username and password:");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		
		JTextField userField = new JTextField();
		
		JPasswordField passwordField = new JPasswordField();
		
		JButton button = new JButton("Enter");

		userField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				passwordField.requestFocus();
			}
		});
		
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
					System.out.println(charArrayToString(array));
					loginSuccessful = dataModel.login(userField.getText(), charArrayToString(array));
					if (loginSuccessful) {
						passwordField.setText("");
						frame.dispose();
						PrimeGUI primeGUI = new PrimeGUI(dataModel);
						dataModel.addObserver(primeGUI);
					} else {
						passwordField.setText("");
						label.setForeground(Color.RED);
						label.setText("Incorrect username and/or password");
					}
				} catch (SocketException | WebDriverException ex) {
					System.out.println("Socket Error");
					passwordField.setText(charArrayToString(array));
					actionPerformed(e);
				} catch (InterruptedException ex1) {
					ex1.printStackTrace();
				}
			}
		});

		panel.add(label);
		panel.add(userField);
		panel.add(passwordField);
		panel.add(button);

		frame.add(panel);
		
		frame.setSize(250, 150);
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
