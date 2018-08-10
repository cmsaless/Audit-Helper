package code;

import code.gui.SplashScreen;
import code.model.DataModel;

public class Driver {

	/**
	 * The main method creates a splash screen, the programs data model, and
	 * then (once the data model is done starting up) closes the splash screen
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		SplashScreen screen = new SplashScreen();
		new DataModel();
		screen.close();
		
	}
	
}


