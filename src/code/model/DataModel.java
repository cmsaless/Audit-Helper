package code.model;

import java.io.IOException;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Observable;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;

import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import code.gui.ErrorGUI;
import code.gui.LandingGUI;
import code.gui.LoginGUI;
import code.utils.FileUtils;
import code.utils.ParsingUtils;

public class DataModel extends Observable {

	private WebDriver _driver;
	private HashMap<String, User> _data;

	private ArrayList<String> _unfilteredResults;
	private ArrayList<String> _filteredResults;

	private Ternary _filterOutByCheckedStatus;
	private boolean _filterOutBlanks;

	private boolean _resultsHaveChanged;
	private boolean _offline;

	private String _csvFileName;
	private String _baseURL;
	private String _htmlLocation;

	/**
	 * The constructor initializes the array lists that will store results, 
	 * grabs the name of the CSV file and the URL we're using, starts up the
	 * web driver, and, finally, creates the landing GUI. 
	 */
	public DataModel() {

		_driver = new FirefoxDriver();
		_driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
		_driver.manage().window().setPosition(new Point(-2000, 0));

		_filteredResults = new ArrayList<>();
		_unfilteredResults = new ArrayList<>();

		_csvFileName = FileUtils.getFileSettings();
		_baseURL = FileUtils.getURLSettings();

		_resultsHaveChanged = false;
		_offline = false;

		new LandingGUI(this);
	}

	/**
	 * This method loads the data from the CSV file into the hash map and then
	 * attempts to open the URL for Snipe-IT. If successful, the program goes 
	 * to the login GUI.
	 */
	public void startup() {

		try {
			_data = FileUtils.readFromCSV(_csvFileName);
			_driver.get(_baseURL);
		} catch (IOException e) {
			_driver.close();
			new ErrorGUI("<html><center>The CSV file you attempted to use<br>doesn't appear to exist.");
			return;
		} catch (WebDriverException e) {
			_driver.close();
			new ErrorGUI("<html><center>The URL you entered appears to be malformed or the selenium web driver itself is experiencing issues.");
			return;
		}

		new LoginGUI(this);
	}

	/**
	 * This method takes the password entered into the password field from the
	 * login GUI and attempts to login into Snipe-IT. Should an incorrect 
	 * password be given, then the user will be notified in the login GUI.
	 * Should it be successful (the URL of the login page is no longer the 
	 * current URL), then the main primary GUI will be started. 
	 * 
	 * @param password - string taken from the password field in the login GUI
	 * @return true if login successful; false otherwise
	 * @throws SocketException 
	 */
	public boolean login(String password) throws SocketException {

		String loginURL = _driver.getCurrentUrl();

		WebElement user = _driver.findElement(By.xpath(
				"/html/body/form/div/div/div/div/div[2]/div/div/fieldset/div[1]/input"));
		WebElement pswd = _driver.findElement(By.xpath(
				"/html/body/form/div/div/div/div/div[2]/div/div/fieldset/div[2]/input"));
		WebElement enter = _driver.findElement(By.xpath(
				"/html/body/form/div/div/div/div/div[3]/button"));

		user.clear();
		user.sendKeys("scraper.scraper");
		pswd.clear();
		pswd.sendKeys(password);
		enter.click();
		
		return !loginURL.equals(_driver.getCurrentUrl());
	}

	/**
	 * This method takes the query from the search bar in the primary GUI and
	 * and does a partial search using every name in _data. It does this by 
	 * calling a method called stripAndLower on both the query string and each
	 * name in _data. stripAndLower simply removes all whitespace and 
	 * lower cases all letters in a string. If a name in data contains the 
	 * query, it is added to the array list of results, which is then filtered.
	 * Lastly, a call to updateGUI is made.
	 * 
	 * @param query - string pulled from the search bar in the primary GUI
	 * @return list of all names that contained the query
	 */
	public void generateListOfResults(String query) throws SocketException {

		System.out.println("\nquery: " + query);
		
//		ArrayList<String> check = new ArrayList<>();
		
		_unfilteredResults.clear();
		for (String name : _data.keySet()) {
			if (ParsingUtils.stripAndLower(name).contains(ParsingUtils.stripAndLower(query))) {
				_unfilteredResults.add(name);
			}
		}
		
		_filteredResults = filter(_unfilteredResults, _filterOutByCheckedStatus, _filterOutBlanks);

//		Collections.sort(check);
//		System.out.println(check);
		
		_resultsHaveChanged = true;
		updateGUI();
	}

	/**
	 * This method takes in a list of names and adds them to a new list.
	 * However, If a name in the original list has attributes that are being
	 * filtered out (has a blank page, is already checked, etc.), then it will 
	 * not be added. Finally, the filtered results are sorted alphabetically.
	 * 
	 * @param results - list of names to be filtered
	 * @return list of names whose attributes are allowed through the filters
	 */
	private ArrayList<String> filter(ArrayList<String> results, Ternary checkFilter, boolean blankFilter) {

		ArrayList<String> filteredResults = new ArrayList<>();
		for (String name : results) {
			boolean isBlank = _data.get(name).getPageIsBlank();
			boolean isChecked = _data.get(name).getChecked();
			if (blankFilter && isBlank || checkFilter == Ternary.CHECKED && isChecked || checkFilter == Ternary.UNCHECKED && !isChecked) {
				continue;
			}
			filteredResults.add(name);
		}

		Collections.sort(filteredResults);
		
		return filteredResults;
	}
	
	public ArrayList<String> filter(Set<String> results, Ternary checkFilter, boolean blankFilter) {
		ArrayList<String> list = new ArrayList<>();
		for (String name : results) {
			list.add(name);
		}
		return filter(list, checkFilter, blankFilter);
	}
//	private ArrayList<String> filter() {
//
//		_filteredResults.clear();
//		for (String name : _unfilteredResults) {
//			boolean isBlank = _data.get(name).getPageIsBlank();
//			boolean isChecked = _data.get(name).getChecked();
//			if (_filterOutBlanks && isBlank ||
//					_filterOutByCheckedStatus == Ternary.CHECKED && isChecked ||
//					_filterOutByCheckedStatus == Ternary.UNCHECKED && !isChecked) {
//
//				continue;
//			}
//			_filteredResults.add(name);
//		}
//
//		Collections.sort(_filteredResults);
//
//		return _filteredResults;
//	}

	/**
	 * This method is called when a new filter is selected the current list of
	 * results needs to have them applied. It takes the original list of
	 * results and calls the filter method on them again to apply the new
	 * filter. Then it switches the _resultsHaveChanged variable to true and
	 * calls the updateGUI method.
	 * 
	 * @return list of results with the new filter applied to them
	 */
	public void refresh() {
		filter(_unfilteredResults, _filterOutByCheckedStatus, _filterOutBlanks);
		_resultsHaveChanged = true;
		updateGUI();
	}

	/**
	 * This method returns whether or not the passing name has been checked. 
	 * 
	 * @param selectedName - name of the person being checked
	 * @return true is the person has been checked; false otherwise
	 */
	public boolean getCheckedStatusFor(String selectedName) {
		boolean checked = false;
		if (_data.containsKey(selectedName)) {
			User user = _data.get(selectedName);
			checked = user.getChecked();
		}
		return checked;
	}

	/**
	 * This method reverses the checked status of the passed in name. Lastly,
	 * it calls updateGUI.
	 * 
	 * @param selectedName - name of the person being checked
	 */
	public void toggleCheckFor(String selectedName) {
		if (_data.containsKey(selectedName)) {
			User user = _data.get(selectedName);
			boolean checked = user.getChecked();
			user.setChecked(!checked);
		}
		updateGUI();
	}

	/**
	 * This method goes through every name in _data and resets their checked
	 * status to false. Then it makes a call to updateGUI.
	 */
	public void resetCheckedStatus() {
		for (User user : _data.values()) {
			user.setChecked(false);
		}
		updateGUI();
	}

	/**
	 * This method grabs the HTML off the page of the passed in name. First it
	 * goes to the person's page in the web driver. Then it pulls the HTML off
	 * the page in plain text. It writes the HTML to the file, current.html, to
	 * make it easier to paste into the text pane in the primary GUI. Finally,
	 * it returns the HTML from current.html in URL form.
	 * 
	 * @param selectedName - name of the person being checked
	 * @return the HTML from the person's page in URL form
	 */
	public String getHTMLFor(String selectedName) {

		String retval = ""; 
		URL htmlURL = FileUtils.readFromHTML("html/" + selectedName + ".html");

		if (_data.containsKey(selectedName)) {
			if (_offline) {
				_htmlLocation = ParsingUtils.parseFilePath(htmlURL.getPath());
			} else {
				int index = _data.get(selectedName).getIndex();
				String url = _baseURL + "/users/" + index + "/print";
				_htmlLocation = url;
				_driver.get(url);
				String source = _driver.getPageSource();
				FileUtils.writeToHTML("html/" + selectedName + ".html", source);
				htmlURL = FileUtils.readFromHTML("html/" + selectedName + ".html");
			}
			retval = htmlURL.toString();
		}

		return retval;
	}
	
	public int getTotalNumberOfUsers() {
		return filter(_data.keySet(), Ternary.NEITHER, false).size();
	}
	
	public int getNumberOfUnchecked() {
		return filter(_data.keySet(), Ternary.CHECKED, false).size();
	}
	
	public int getNumberOfChecked() {
		return filter(_data.keySet(), Ternary.UNCHECKED, false).size();
	}
	
	public int getNumberOfNonBlanks() {
		return filter(_data.keySet(), Ternary.NEITHER, true).size();
	}
	
	public int getNumberOfBlanks() {
		return filter(_data.keySet(), Ternary.NEITHER, false).size() - 
				filter(_data.keySet(), Ternary.NEITHER, true).size();
	}
	
	/**
	 * Opens the page of every user in Snipe-IT and stores their index (page
	 * number) in a designated CSV file.
	 */
	public void beginCollecting(boolean overwrite) {
		new Collect(_driver, this, overwrite);
		HashMap<String, User> backup = _data;
		try {
			_data = FileUtils.readFromCSV(_csvFileName);
		} catch (IOException e) {
			_data = backup;
		}
	}
	
	/**
	 * This method returns the URL of the page the web driver is currently on.
	 * 
	 * @return the URL of the current page
	 * @throws SocketException
	 */
	public String getPageURL() {
		return _htmlLocation;
	}

	public void save() {
		FileUtils.writeToCSV(_csvFileName, _data, true);
	}

	/**
	 * This method writes the contents of _data to the CSV file and closes the
	 * web driver.
	 */
	public void saveAndExit() {
		FileUtils.writeToCSV(_csvFileName, _data, true);
		_driver.close();
	}

	/**
	 * This method returns the original, unfiltered results.
	 * 
	 * @return the list of original, unfiltered results.
	 */
	public ArrayList<String> getUnfilteredResults() {
		return _unfilteredResults;
	}

	/**
	 * This method returns the list of filtered results.
	 * 
	 * @return the list of filtered results
	 */
	public ArrayList<String> getFilteredResults() {
		return _filteredResults;
	}

	/**
	 * Returns an ImageIcon object that displays the "blankcheck.png" picture.
	 * 
	 * @return ImageIcon object with "blankcheck.png"
	 */
	public ImageIcon getBlankCheckImage() {
		return new ImageIcon(getClass().getResource(FileUtils.BLANKCHECK_PIC_LOC));
	}

	/**
	 * Returns an ImageIcon object that displays the "check.png" picture.
	 * 
	 * @return ImageIcon object with "check.png"
	 */
	public ImageIcon getCheckImage() {
		return new ImageIcon(getClass().getResource(FileUtils.CHECK_PIC_LOC));
	}

	/**
	 * This method sets what type of checked attribute should be filtered out.
	 * 
	 * @param t - enum type whose value corresponds to which attribute should
	 * be filtered out
	 */
	public void setFilterOutByCheckedStatus(Ternary t) {
		_filterOutByCheckedStatus = t;
	}

	/**
	 * This method sets whether or not people with blank pages should be
	 * displayed.
	 * 
	 * @param b - boolean value that, if true, means blank pages shouldn't be
	 * displayed
	 */
	public void setFilterOutBlanks(boolean b) {
		_filterOutBlanks = b;
	}

	/**
	 * Returns the file name of the CSV file currently being used.
	 * 
	 * @return name of the CSV file
	 */
	public String getFileName() {
		return _csvFileName;
	}

	/**
	 * Returns the URL of Snipe-IT.
	 * 
	 * @return URL of the CSV file
	 * @throws SocketException
	 */
	public String getBaseURL() {
		return _baseURL;
	}

	/**
	 * This method sets the passed in string as the new CSV file to be used.
	 * First it calls formatFileName to make sure the file has the proper
	 * ".csv" extension. Then, it writes it to a text file we can access later 
	 * if need be. Finally, the instance variable _fileName is set to the value 
	 * of newFileName.
	 * 
	 * @param newFileName - the name of the designated CSV file
	 */
	public void setFileName(String newCSVFileName) {
		newCSVFileName = ParsingUtils.formatCSVFileName(newCSVFileName);
		FileUtils.changeFileSettings(newCSVFileName);
		_csvFileName = newCSVFileName;
	}

	/**
	 * This method sets the passed in string as the new URL to be used for
	 * Snipe-IT. First it calls removeEndSlashes to make sure there are no
	 * additional forward slashes appended to the end of the URL. Then, it 
	 * writes it to a text file we can access later if need be. Finally, the
	 * instance variable _baseURL is set to the value of url. 
	 * 
	 * @param url - the URL for Snipe-IT
	 */
	public void setURL(String url) {
		url = ParsingUtils.removeEndSlashes(url);
		FileUtils.changeURLSettings(url);
		_baseURL = url;
	}

	public void setOffline(boolean b) {
		_offline = b;
	}

	public boolean getOffline() {
		return _offline;
	}

	/**
	 * This method lets its observers (the primary GUI) that it should check
	 * for any changes in this class and display them in the user interface.
	 * Note: the _resultsHaveChanged instance variable is switched back to 
	 * false before notifyObservers is called. This is because there may 
	 * additional calls to updateGUI while the primary GUI is updating its
	 * components. Basically, it ensures that the GUI won't repeatedly try and
	 * add the results to the JComboBox.
	 */
	public void updateGUI() {
		boolean formerValue = _resultsHaveChanged;
		_resultsHaveChanged = false;

		setChanged();
		notifyObservers(formerValue);
	}

}
