package code;

import code.gui.SplashScreen;
import code.model.DataModel;

public class Driver {

	public static void main(String[] args) {

		SplashScreen screen = new SplashScreen();
		new DataModel();
		screen.close();
		
	}
	
}


