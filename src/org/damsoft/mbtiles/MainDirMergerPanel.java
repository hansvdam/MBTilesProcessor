package org.damsoft.mbtiles;

import java.util.List;

import javax.swing.BoxLayout;

class MainDirMergerPanel extends MainMergerPanel {

	private FileSystemChooserPanel form;

	/**
	 * 
	 */
	public MainDirMergerPanel() {
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		String[] labels = { "Source 1", "Source 2", "Target" };
		char[] mnemonics = { 'S', 'O', 'T' };
		int[] widths = { 15, 15, 15 };
		String[] descs = { "The first source directory with mbtiles",
				"The second source directory with mbtiles",
				"The directory that will contian the merged archives" };


		addExplanation("In the following form select three directories: two source directories containing equally named mbtile-archives: probably about the same are, but containing different zoom levels. The 'target' directory is where the merges end up. This way if you have a number of archives, but some zoomlevels are missing, you only need to create archives with the same names containing the missing zoomlevels, and they can be merged with the original archives.");
		form = new TileDirMergerPanel(labels, mnemonics,
				widths, descs);
		add(form);
		buildRestGui();

	}

	public List<String> getSources(){
		return form.getDirs();
	}
	
}
