package code.model;

public class User {

	private String _name;
	private int _index;
	private boolean _pageIsBlank;
	private boolean _checked;
	
	/**
	 * Creates an associative relationship between the passed in arguments and
	 * their corresponding instance variables.
	 * 
	 * @param name - name of the person
	 * @param index - page number
	 * @param pageIsBlank - true if page is blank; false otherwise
	 * @param isChecked - true is checked; false otherwise
	 */
	public User(String name, int index, boolean pageIsBlank, boolean isChecked) {
		_name = name;
		_index = index;
		_pageIsBlank = pageIsBlank;
		_checked = isChecked;
	}
	
	public void setName(String name) {
		_name = name;
	}
	
	public void setIndex(int index) {
		_index = index;
	}
	
	public void setPageIsBlank(boolean pageIsBlank) {
		_pageIsBlank = pageIsBlank;
	}
	
	public void setChecked(boolean checked) {
		_checked = checked;
	}
	
	public String getName() {
		return _name;
	}
	
	public int getIndex() {
		return _index;
	}
	
	public boolean getPageIsBlank() {
		return _pageIsBlank;
	}
	
	public boolean getChecked() {
		return _checked;
	}
	
	@Override
	public String toString() {
		String blankMessage = _pageIsBlank ? "Page is Blank" : "Page is not Blank";
		String checkedMessage = _checked ? "Is Checked" : "Is Unchecked";
		return _name + " - " + _index + " - " + blankMessage + " - " + checkedMessage;
	}
	
}