package code.gui;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;

import code.model.DataModel;

@SuppressWarnings("serial")
public class CustomRenderer extends DefaultListCellRenderer {

	private DataModel _audit;

	public CustomRenderer(DataModel audit) {
		_audit = audit;
	}

	/**
	 * This method gives each item in the JComboBox either a checked or unchecked icon next to their name 
	 */
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

		ArrayList<String> results = _audit.getFilteredResults();
		JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

		if (!results.isEmpty() && index != -1) {
			String name = results.get(index);
			boolean isChecked = _audit.getCheckedStatusFor(name);
			Icon icon = isChecked ? _audit.getCheckImage() : _audit.getBlankCheckImage();
			label.setIcon(icon);
		}

		return this;
	}

}
