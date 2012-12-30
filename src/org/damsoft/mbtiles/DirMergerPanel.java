/**
 * 
 */
package org.damsoft.mbtiles;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.text.JTextComponent;

/**
 * @author Hans van Dam
 *
 */
public class DirMergerPanel extends FileSystemChooserPanel {

	/**
	 * @param labels
	 * @param mnemonics
	 * @param widths
	 * @param tips
	 * @param scrollTextArea 
	 */
	public DirMergerPanel(String[] labels, char[] mnemonics, 
			String[] tips, boolean[] scrollTextArea) {
		super(labels, mnemonics, tips, scrollTextArea, null);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setFileChooserProperties(JFileChooser fileChooser) {
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	}


}
