package code.model;

import java.net.SocketException;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import code.utils.FileUtils;
import code.utils.ParsingUtils;

public class Collect {

	/**
	 * The constructor does three things:
	 * 1. Puts the web driver window in the top left corner of the screen so
	 *    the user can see it.
	 * 2. Grabs the index of every user in Snipe-IT and stores it in the
	 *    HashMap, data.
	 * 3. Writes the contents of data to the designated CSV file.
	 * 
	 * @param driver - the web driver which Snipe-IT is open in
	 * @param dataModel - instance of the DataModel class
	 */
	public Collect(WebDriver driver, DataModel dataModel, boolean overwrite) {
		driver.manage().window().setPosition(new Point(0, 0));
		try {
			int numUsers = getNumberOfUsers(driver, dataModel);
			HashMap<String, User> data = collectInformation(driver, dataModel, new HashMap<>(), 1, numUsers);
			FileUtils.writeToCSV(dataModel.getFileName(), data, overwrite);
		} catch (SocketException e) {
			new Collect(driver, dataModel, overwrite);
		}
		driver.manage().window().setPosition(new Point(-2000, 0));
	}

	/**
	 * This method goes to the users page on Snipe-IT. When you navigate to 
	 * this page on Snipe-IT it will display the total number of users in the
	 * system (it takes a second to appear which is why the thread sleeps
	 * before looking for it. Once the text is received, it is in the format of
	 * "Showing [x] to [y] of [z] rows". We use the getNumberOfUsersFromSpan
	 * method in the ParsingUtils class to retrieve the number corresponding to
	 * [z] (which represents the total number of users). 
	 * 
	 * @param driver - the web driver which Snipe-IT is open in
	 * @param dataModel - instance of the DataModel class
	 * @return the total number of users in the Snipe-IT system
	 */
	public int getNumberOfUsers(WebDriver driver, DataModel dataModel) throws SocketException {

		int numUsers = 0;

		driver.get(dataModel.getBaseURL() + "/users");

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			return getNumberOfUsers(driver, dataModel);
		}

		String text = driver.findElement(By.xpath("//*[@id=\"bulkForm\"]/div[1]/div[2]/div[1]/span[1]")).getAttribute("innerHTML");
		numUsers = ParsingUtils.getNumberOfUsersFromSpan(text);

		return numUsers;
	}

	/**
	 * This recursive function goes to every user page in Snipe-IT. Firstly, it
	 * grabs the person's name from the header and the page number from the
	 * URL. Then, it checks whether or not if the page is a duplicate (i.e. 
	 * there exists another page in Snipe-IT with the same name). If a 
	 * duplicate is found, it will be added in with a number next to the name
	 * denoting which copy it is (first, second, third, etc.). Whenever the
	 * total number of keys (names) in the hash map equals the numUsers
	 * parameter, then the last user must've been checked in and the method 
	 * returns the hash map.
	 * 
	 * @param driver - the web driver which Snipe-IT is open in
	 * @param dataModel - instance of the DataModel class
	 * @param data - HashMap containing the data pulled from Snipe-IT
	 * @param index - the page number in the URL
	 * @param numUsers - total number of users in Snipe-IT
	 * @return all of the data pulled from Snipe-IT
	 */
	public HashMap<String, User> collectInformation(WebDriver driver, DataModel dataModel, HashMap<String, User> data, int index, int numUsers) throws SocketException {

		if (numUsers == data.keySet().size()) {
			return data;
		}

		String header = "";
		try {
			driver.get(dataModel.getBaseURL() + "/users/" + index + "/print");
			header = driver.findElement(By.xpath("//body/*[contains(text(), \"Assigned to\")]")).getText();
			String name = header.substring(12);

			boolean nameAlreadyExists = data.containsKey(name);
			boolean prevPageWasBlank = nameAlreadyExists ? data.get(name).getPageIsBlank() : false;
			boolean currentPageIsBlank = pageIsBlank(driver);

			if ((!nameAlreadyExists) || (nameAlreadyExists && !prevPageWasBlank && !currentPageIsBlank)) {
				name = appendCopyNumber(data, name);
				data.put(name, new User(name, index, pageIsBlank(driver), false));
				FileUtils.writeToHTML("html/" + name + ".html", driver.getPageSource());
			}
		} catch (NoSuchElementException ex) {

		}

		return collectInformation(driver, dataModel, data, ++index, numUsers);
	}

	/**
	 * A page is considered to be blank if it doesn't contain and HTML table
	 * tags. 
	 * 
	 * @param driver - the web driver which Snipe-IT is open in
	 * @return true if the page contains any html table tags; false otherwise
	 */
	public boolean pageIsBlank(WebDriver driver) {
		List<WebElement> elems = driver.findElements(By.tagName("table"));
		return elems.size() <= 1;
	}

	/**
	 * This method checks if the passed in string, the name, already exists. If
	 * it does, the method calls the overloaded appendCopyNumber method with an
	 * integer parameter. If not, then the name is returned as it is. 
	 * 
	 * @param data - HashMap containing the data pulled from Snipe-IT
	 * @param name - name of person
	 * @return name with a possible copy number appended to it 
	 */
	public String appendCopyNumber(HashMap<String, User> data, String name) {
		if (data.containsKey(name)) {
			name = appendCopyNumber(data, name, 1);
		}
		return name;
	}

	/**
	 * This method checks if the name being passed in already exists in data. 
	 * If it does, a number is appended to the end of it that shows which
	 * number copy it is.
	 *   
	 * @param HashMap containing the data pulled from Snipe-IT
	 * @param name - name of person
	 * @param num - denotes which copy we're on
	 * @return name with a copy number appended to it
	 */
	public String appendCopyNumber(HashMap<String, User> data, String name, int num) {
		String copyName = name + "(" + num + ")";
		if (data.containsKey(copyName)) {
			name = appendCopyNumber(data, name, ++num);
		} else {
			name = copyName;
		}
		return name;
	}

}
