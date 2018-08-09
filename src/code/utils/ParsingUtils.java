package code.utils;

public class ParsingUtils {
	
	/**
	 * This method takes in a string returns that same string with all of its 
	 * whitespace removed and all of its upper case letters turned to lower 
	 * case.
	 * 
	 * @param s - string to be stripped and lowered
	 * @return input string but no spaces or upper case letters
	 */
	public static String stripAndLower(String s) {
		return s.replace(" ", "").toLowerCase();
	}
	
	/**
	 * This method takes in a string and returns true if it can be converted to
	 * an integer; false otherwise.
	 * 
	 * @param s - string to be converted
	 * @return true if s can be converted to an integer; false otherwise.
	 */
	public static boolean isInt(String s) {
		try {
			new Integer(s);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}
	
	/**
	 * This method is for getting rid of any slashes that may be at the end of
	 * a URL. It does this by repeatedly checking that last char in the passed
	 * in string. if the last char is a '/', then it removes it and keeps
	 * checking. Otherwise, it returns the string.
	 * 
	 * @param url - string to have forward slashes to be removed 
	 * @return the string with no end forward slashes 
	 */
	public static String removeEndSlashes(String url) {
		if (url.charAt(url.length()-1) == '/') {
			String newURL = url.substring(0, url.length()-1);
			return removeEndSlashes(newURL);
		} else {
			return url;
		}
	}
	
	/**
	 * This method takes in a string that represents a file name and checks to
	 * make sure it ends with ".csv". If it does not, it is appended to it.
	 * 
	 * @param fileName - the name of the CSV file
	 * @return the file name with ".csv" appended to it if it didn't have it
	 */
	public static String formatCSVFileName(String fileName) {
		int beginIndexOfExt = fileName.length() - 4;
		String extension = fileName.substring(beginIndexOfExt);
		if (!extension.equals(".csv")) {
			fileName += ".csv";
		}
		return fileName;
	}
	
	/**
	 * The passed in string has the format of "Showing [x] to [y] of [z] rows".
	 * We want the number that corresponds to z. We split the string along " ".
	 * From there, we know the index, 5, in the array will get us the number as
	 * a string and we can then convert it to an integer. 
	 * 
	 * @param span - a string that needs to be parsed
	 * @return an integer of the number of users in Snipe-IT
	 */
	public static int getNumberOfUsersFromSpan(String span) {
		String[] words = span.split(" ");
		return new Integer(words[5]);
	}
	
	public static String parseFilePath(String path) {
		return path.substring(1).replace("%20", " ");
	}
	
}
