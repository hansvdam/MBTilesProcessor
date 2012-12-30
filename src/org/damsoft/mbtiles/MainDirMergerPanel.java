package org.damsoft.mbtiles;

import java.util.List;

import javax.swing.BoxLayout;

import org.damsoft.mbtiles.mergers.DirMerger;

class MainDirMergerPanel extends MainMergerPanel {

	/**
	 * 
	 */
	public MainDirMergerPanel() {
		super(new DirMerger());
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		String[] labels = { "Source 1", "Source 2", "Target" };
		char[] mnemonics = { 'S', 'O', 'T' };
		boolean[] scrollTextArea = {false,false,false};
		String[] descs = { "The first source directory with mbtiles",
				"The second source directory with mbtiles",
				"The directory that will contian the merged archives" };

		addMainTitle("Merging amounts of mbtiles-archives of same area but differetn zoomlevels.");
		addExplanation("In the following form select three directories: two source directories containing equally named mbtile-archives: probably about the same are, but containing different zoom levels. The 'target' directory is where the merges end up. This way if you have a number of archives, but some zoomlevels are missing, you only need to create archives with the same names containing the missing zoomlevels, and they can be merged with the original archives.");
		form = new DirMergerPanel(labels, mnemonics, descs, scrollTextArea);
		add(form);
		buildRestGui();
		
	}

	public List<String> getSources(){
		return form.getSources();
	}

	
}
