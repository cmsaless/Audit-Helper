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
	 * The constructor starts the web driver, initializes the ArrayLists that 
	 * will store the results, grabs the name of the CSV file we'll be reading 
	 * and writing and the URL we'll go to in the web driver, and, finally, 
	 * creates the landing GUI. 
	 */
	public DataModel() {

		_driver = new FirefoxDriver();
		_driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
//		_driver.manage().window().setPosition(new Point(-2000, 0));

		_filteredResults = new ArrayList<>();
		_unfilteredResults = new ArrayList<>();

		_csvFileName = FileUtils.getFileSettings();
		_baseURL = FileUtils.getURLSettings();

		_resultsHaveChanged = false;
		_offline = false;

		new LandingGUI(this);
	}

	/**
	 * This method loads the data from the CSV file we entered in the options 
	 * menu into a HashMap and then attempts to open the URL for Snipe-IT. If 
	 * successful, the program goes to the login GUI. If the program fails to
	 * continue (either because the CSV file is invalid or the driver was
	 * unable to reach Snipe-IT, a custom error message is shown.
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
	 * This method takes the strings of characters entered into the username 
	 * and password fields from the login GUI and attempts to use them to log 
	 * into Snipe-IT. Should an incorrect username or password be given, then 
	 * the user will be notified in the login GUI. Should it be successful 
	 * (meaning the URL of the login page is no longer the current URL), then 
	 * the primary GUI will be started. 
	 * 
	 * @param password - string taken from the password field in the login GUI
	 * @return true if login successful; false otherwise
	 * @throws SocketException 
	 * @throws InterruptedException 
	 */
	public boolean login(String username, String password) throws SocketException, InterruptedException {

		String loginURL = _driver.getCurrentUrl();

		WebElement user = _driver.findElement(By.name("username"));
//		WebElement pswd = _driver.findElement(By.name("password"));
		WebElement pswd = _driver.findElement(By.xpath("//input[@name=\"password\"]"));
		WebElement enter = _driver.findElement(By.xpath("//button[text()= \"Login\"]"));
		
		user.clear();
		pswd.clear();
		
		user.click();
		user.sendKeys(username);
		
		Thread.sleep(1000);
		
		pswd.click();
//		pswd.sendKeys("");pswd.sendKeys("");pswd.sendKeys("");pswd.sendKeys("");
		pswd.sendKeys(password);
		
		Thread.sleep(500);
		enter.click();
		
		return !loginURL.equals(_driver.getCurrentUrl());
	}

	/**
	 * This method takes the query from the search bar in the primary GUI and
	 * and does a partial search using every name in _data. It does this by 
	 * calling a method called stripAndLower on both the query string and each
	 * name in _data. stripAndLower simply removes all whitespace and lower 
	 * cases all letters in a string. If a name in _data contains the query, it 
	 * is added to the ArrayList of results, which is then filtered. Lastly, a 
	 * call to updateGUI is made.
	 * 
	 * @param query - string pulled from the search bar in the primary GUI
	 * @return list of all names that contained the query
	 */
	public void generateListOfResults(String query) throws SocketException {

		_unfilteredResults.clear();
		for (String name : _data.keySet()) {
			if (ParsingUtils.stripAndLower(name).contains(ParsingUtils.stripAndLower(query))) {
				_unfilteredResults.add(name);
			}
		}
		
		_filteredResults = filter(_unfilteredResults, _filterOutByCheckedStatus, _filterOutBlanks);

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
	 * @param checkFilter - the check status that will be filtered out
	 * @param blankFilter - if true, blank pages will be filtered out
	 * @return list of names whose attributes are allowed through the filters
	 */
	private ArrayList<String> filter(ArrayList<String> unfilteredResults, Ternary checkFilter, boolean blankFilter) {

		ArrayList<String> filteredResults = new ArrayList<>();
		for (String name : unfilteredResults) {
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
	
	/**
	 * This method converts its Set of strings into an ArrayList and then calls
	 * filter but with the ArrayList instead.
	 * 
	 * @param results - set of names to be filtered
	 * @param checkFilter - the check status that will be filtered out
	 * @param blankFilter - if true, blank pages will be filtered out
	 * @return list of names whose attributes are allowed through the filters
	 */
	public ArrayList<String> filter(Set<String> results, Ternary checkFilter, boolean blankFilter) {
		ArrayList<String> list = new ArrayList<>();
		for (String name : results) {
			list.add(name);
		}
		return filter(list, checkFilter, blankFilter);
	}
	
	/**
	 * This method is called whenever the refresh button in the primary GUI is
	 * clicked. If any new filters have been selected then the current list of
	 * results needs to have them applied. It takes the original list of
	 * results (_unfilteredResults) and calls the filter method on them again
	 * to apply any of the new filters. Then it switches _resultsHaveChanged to
	 * true and calls the updateGUI method.
	 */
	public void refresh() {
		_filteredResults = filter(_unfilteredResults, _filterOutByCheckedStatus, _filterOutBlanks);
		_resultsHaveChanged = true;
		updateGUI();
	}

	/**
	 * This method returns true or false depending on the selected name having
	 * been checked or not. 
	 * 
	 * @param selectedName - name of the person being checked
	 * @return true if the person has been checked; false otherwise
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
	 * This method reverses the checked status of the passed in name. Then, it
	 * calls updateGUI.
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
	 * This method grabs the HTML off the page of the selected person and
	 * returns the location of the HTML file on the system in URL format. First,
	 * we check to make sure that the selected name is valid by seeing if _data
	 * contains it. If it does, then we check whether we are offline or online.
	 * If we are offline, then we simply read from the corresponding HTML file 
	 * and get its location in URL form and set the _htmlLocation instance
	 * variable to the path of the file.
	 * 
	 * If we are not offline then we get the corresponding page number for the 
	 * selected person in _data and then have the web driver navigate to that
	 * page and write the page's HTML to a file on the system (this way if we 
	 * go offline in the future we can still have the most up-to-date info). 
	 * Then we simply read from the corresponding HTML file and get its 
	 * location in URL form and set the _htmlLocation instance variable to the
	 * URL of the web page.
	 * 
	 * @param selectedName - name of the person being checked
	 * @return the HTML from the person's page in URL form
	 */
	public String getHTMLFor(String selectedName) {

		String retval = "";

		if (_data.containsKey(selectedName)) {
			URL htmlURL = FileUtils.readFromHTML("html/" + selectedName + ".html");
			if (_offline) {
				_htmlLocation = ParsingUtils.parseFilePath(htmlURL.getPath());
			} else {
				int index = _data.get(selectedName).getIndex();
				String url = _baseURL + "/users/" + index + "/print";
				_driver.get(url);
				String source = _driver.getPageSource();
				FileUtils.writeToHTML("html/" + selectedName + ".html", source);
				_htmlLocation = url;
			}
			retval = htmlURL.toString();
		}

		return retval;
	}
	
	/**
	 * Initiates an instance of the Collect class which will go to every page
	 * of every user in Snipe-IT and write their name, page number, and other 
	 * information to a CSV file.
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
	 * This method takes whatever is currently stored in _data and writes it to
	 * the CSV being used.
	 */
	public void save() {
		FileUtils.writeToCSV(_csvFileName, _data, true);
	}

	/**
	 * This method writes the contents of _data to the CSV file and closes the
	 * web driver.
	 */
	public void saveAndExit() {
		save();
		_driver.close();
	}
	
	/**
	 * @return total number of users in Snipe-IT.
	 */
	public int getTotalNumberOfUsers() {
		return filter(_data.keySet(), Ternary.NEITHER, false).size();
	}
	
	/**
	 * @return total number of users in Snipe-IT that are unchecked.
	 */
	public int getNumberOfUnchecked() {
		return filter(_data.keySet(), Ternary.CHECKED, false).size();
	}
	
	/**
	 * @return total number of users in Snipe-IT that are checked.
	 */
	public int getNumberOfChecked() {
		return filter(_data.keySet(), Ternary.UNCHECKED, false).size();
	}
	
	/**
	 * @return total number of users in Snipe-IT that have non-blanks pages.
	 */
	public int getNumberOfNonBlanks() {
		return filter(_data.keySet(), Ternary.NEITHER, true).size();
	}
	
	/**
	 * @return total number of users in Snipe-IT that have blank pages.
	 */
	public int getNumberOfBlanks() {
		return filter(_data.keySet(), Ternary.NEITHER, false).size() - 
				filter(_data.keySet(), Ternary.NEITHER, true).size();
	}

	/**
	 * @return the list of original, unfiltered results.
	 */
	public ArrayList<String> getUnfilteredResults() {
		return _unfilteredResults;
	}

	/**
	 * @return the list of filtered results
	 */
	public ArrayList<String> getFilteredResults() {
		return _filteredResults;
	}

	/**
	 * @return the base URL of Snipe-IT (http://<###.###.###.###>)
	 */
	public String getBaseURL() {
		return _baseURL;
	}
	
	/**
	 * @return ImageIcon object with "blankcheck.png"
	 */
	public ImageIcon getBlankCheckImage() {
		return new ImageIcon(getClass().getResource(FileUtils.BLANKCHECK_PIC_LOC));
	}

	/**
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
	 * This method sets the passed in string as the new CSV file to be used.
	 * First it calls formatFileName to make sure the file has the proper
	 * ".csv" extension. Then, it writes it to a text file so it will be the
	 * default file we use next time (we don't want to have to type in the file
	 * name every time). Finally, the instance variable, _fileName, is set to the 
	 * value of newFileName.
	 * 
	 * @param newFileName - the name of the new CSV file to be used
	 */
	public void setFileName(String newCSVFileName) {
		newCSVFileName = ParsingUtils.formatCSVFileName(newCSVFileName);
		FileUtils.changeFileSettings(newCSVFileName);
		_csvFileName = newCSVFileName;
	}
	
	/**
	 * @return the file name of the CSV file currently being used
	 */
	public String getFileName() {
		return _csvFileName;
	}

	/**
	 * This method sets the passed in string as the new URL to be used for
	 * Snipe-IT. First it calls removeEndSlashes to make sure there are no
	 * additional forward slashes appended to the end of the URL. Then, it 
	 * writes it to a text file so it will be the default file we use next time
	 * (we don't want to have to type in the file name every time). Finally, 
	 * the instance variable _baseURL is set to the value of URL. 
	 * 
	 * @param url - the URL for Snipe-IT
	 */
	public void setURL(String url) {
		url = ParsingUtils.removeEndSlashes(url);
		FileUtils.changeURLSettings(url);
		_baseURL = url;
	}
	
	/**
	 * @return the URL of the page the web driver is currently on
	 */
	public String getPageURL() {
		return _htmlLocation;
	}

	/**
	 * This method sets whether or not we want to be online or offline.
	 * 
	 * @param b - if true, then go offline; if false, stay online
	 */
	public void setOffline(boolean b) {
		_offline = b;
	}

	/**
	 * @return true if offline; false otherwise
	 */
	public boolean getOffline() {
		return _offline;
	}

	/**
	 * This method notifies its observers (the primary GUI) that it should 
	 * check for any changes in this class and display them in the user 
	 * interface. 
	 * 
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
