package code.utils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;

import code.gui.WarningGUI;
import code.model.User;

public class FileUtils {

	public static final String FILE_SETTINGS_FILE_LOC = "settings/file.txt";
	public static final String URL_SETTINGS_FILE_LOC = "settings/url.txt"; 
	public static final String OFFLINE_SETTINGS_FILE_LOC = "settings/offline.txt";
	public static final String HTML_FILE_LOC = "html/current.html";

	public static final String BLANKCHECK_PIC_LOC = "/images/blankcheck.png";
	public static final String CHECK_PIC_LOC = "/images/check.png";
	public static final String FACE_PIC_LOC = "/images/face.jpg";

	public FileUtils() {}

	/**
	 * This method writes the data from the HashMap to the designated CSV file.
	 * 
	 * @param fileName - name of CSV file
	 * @param data - data being written to file
	 */
	public static void writeToCSV(String fileName, HashMap<String, User> data, boolean overwrite) {

		String textToWrite = "NAME,INDEX,BLANK,CHECKED\n";

		HashMap<String, User> prevData;
		try {
			prevData = readFromCSV(fileName);
		} catch (IOException e) {
			prevData = new HashMap<String, User>();
		}

		for (String name : data.keySet()) {
			User user = data.get(name);
			if (!overwrite && prevData.containsKey(name)) {
				user = prevData.get(name);
			}
			textToWrite += user.getName() + "," + user.getIndex() + "," + user.getPageIsBlank() + "," + user.getChecked() + "\n";
		}

		System.out.println("writing: " + fileName);
		try {
			Files.write(Paths.get(fileName), textToWrite.getBytes());
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * This method reads in data from a CSV file and places it into a HashMap.
	 * 
	 * @param fileName - CSV file being read
	 * @return the data from the CSV file
	 * @throws IOException
	 */
	public static HashMap<String, User> readFromCSV(String fileName) throws IOException {

		HashMap<String, User> data = new HashMap<String, User>();
		int rowCount = 0;
		boolean firstLine = true;
		boolean releaseWarning = false;
		ArrayList<Integer> afflictedRows = new ArrayList<>();

		File file = new File(fileName);
		if (!(file.exists() && !file.isDirectory())) {
			writeToCSV(fileName, data, true);
		}

		for(String line : Files.readAllLines(Paths.get(fileName))) {
			++rowCount;
			if (firstLine) {
				firstLine = false;
				continue;
			} 
			try {
				String[] values = line.split(",");
				String name = values[0];
				int index = new Integer(values[1]);
				boolean isBlank = new Boolean(values[2]);
				boolean isChecked = new Boolean(values[3]);
				data.put(name, new User(name, index, isBlank, isChecked));
			} catch (NumberFormatException e) {
				releaseWarning = true;
				afflictedRows.add(rowCount);
				continue;
			}
		}

		if (releaseWarning) {
			new WarningGUI("<html><center>One or several of the rows<br>in the CSV file had formatting issues.<br>" + "Row(s): " + afflictedRows + "</html>");
		}

		return data;
	}

	public static void changeFileSettings(String textToWrite) {
		writeToTXT(FILE_SETTINGS_FILE_LOC, textToWrite);
	}

	public static void changeURLSettings(String textToWrite) {
		writeToTXT(URL_SETTINGS_FILE_LOC, textToWrite);
	}

	public static void changeOfflineSettings(String textToWrite) {
		writeToTXT(OFFLINE_SETTINGS_FILE_LOC, textToWrite);
	}

	/**
	 * This method writes the textToWrite to the designated file.
	 * 
	 * @param fileName - TXT file being written on
	 * @param textToWrite - text that is being to the file
	 */
	private static void writeToTXT(String fileName, String textToWrite) {
		try {
			Files.write(Paths.get(fileName), textToWrite.getBytes());
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}

	public static String getFileSettings() {
		return readFromTXT(FILE_SETTINGS_FILE_LOC);
	}

	public static String getURLSettings() {
		return readFromTXT(URL_SETTINGS_FILE_LOC);
	}

	public static String getOfflineSettings() {
		return readFromTXT(OFFLINE_SETTINGS_FILE_LOC);
	}

	/**
	 * This method returns the contents from the designated TXT files.
	 * 
	 * @param fileName - the TXT file being read
	 * @return the contents of teh TXT file
	 */
	private static String readFromTXT(String fileName) {

		String retval = "";

		try {
			for (String line : Files.readAllLines(Paths.get(fileName))) {
				retval = line;
				break;
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return retval;
	}

	/**
	 * This method takes the HTML from the most recently opened URL (which is 
	 * stored in the file named current.html) and copies it to file whose
	 * name is passed in through the argument list.
	 * 
	 * @param fileName - name of the file the HTML will be copied to
	 */
	public static void exportHTML(String from, String to) {
		try {
			Files.copy(Paths.get(from.substring(3).replace("%20", " ")), Paths.get(to), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method takes the string passed in through the argument list (which
	 * is actually the HTML from the most recently opened page) and saves it to
	 * current.html.
	 * 
	 * @param source - html to be saved
	 */
	public static void writeToHTML(String fileName, String source) {
		try {
			Files.write(Paths.get(fileName), source.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * This method reads in the HTML from current.html and returns it in a URL
	 * format.
	 * 
	 * @return the HTML in URL format
	 */
	public static URL readFromHTML(String fileName) {
		File htmlFile = new File(fileName);
		URL url = null;
		try {
			url = htmlFile.toURI().toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}

}
