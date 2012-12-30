/**
 * 
 */
package org.damsoft.mbtiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;

/**
 * @author Hans van Dam
 * 
 */
public class FileMergerPanel extends FileSystemChooserPanel {

	/**
	 * @param labels
	 * @param mnemonics
	 * @param widths
	 * @param tips
	 */
	public FileMergerPanel(String[] labels, char[] mnemonics, 
			String[] tips, boolean[] scollTextAreas, boolean[] editableFields) {
		super(labels, mnemonics, tips, scollTextAreas, editableFields);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setFileChooserProperties(JFileChooser fileChooser) {
		fileChooser.setMultiSelectionEnabled(true);
	}

	public List<String> getSources() {
		List<String> returnValue = new ArrayList<String>();
		String[] paths = fields[0].getText().split("\n"); 
		for (String string : paths) {
			returnValue.add(string);
		}
		returnValue.add(fields[1].getText());
		return returnValue;
	}

}
