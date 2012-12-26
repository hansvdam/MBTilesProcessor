/**
 * 
 */
package org.damsoft.mbtiles;

import java.awt.TextField;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;

/**
 * @author Hans van Dam
 * 
 */
public class ArchiveMergerPanel extends FileSystemChooserPanel {

	/**
	 * @param labels
	 * @param mnemonics
	 * @param widths
	 * @param tips
	 */
	public ArchiveMergerPanel(String[] labels, char[] mnemonics, int[] widths,
			String[] tips, boolean[] scollTextAreas) {
		super(labels, mnemonics, widths, tips, scollTextAreas);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setFileChooserProperties(JFileChooser fileChooser) {
		fileChooser.setMultiSelectionEnabled(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.damsoft.mbtiles.FileSystemChooserPanel#getDirs()
	 */
	@Override
	public List<String> getDirs() {
		return Arrays.asList(fields[0].getText().split(";"));
	}

}
