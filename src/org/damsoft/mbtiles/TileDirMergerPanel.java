/**
 * 
 */
package org.damsoft.mbtiles;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

/**
 * @author Hans van Dam
 *
 */
public class TileDirMergerPanel extends FileSystemChooserPanel {

	/**
	 * @param labels
	 * @param mnemonics
	 * @param widths
	 * @param tips
	 * @param scrollTextArea 
	 */
	public TileDirMergerPanel(String[] labels, char[] mnemonics, int[] widths,
			String[] tips, boolean[] scrollTextArea) {
		super(labels, mnemonics, widths, tips, scrollTextArea);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setFileChooserProperties(JFileChooser fileChooser) {
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	}

	public List<String> getDirs() {
		List<String> returnValue = new ArrayList<String>();
		for (JTextComponent textField : fields) {
			returnValue.add(textField.getText());
		}
		return returnValue;
	}

}
