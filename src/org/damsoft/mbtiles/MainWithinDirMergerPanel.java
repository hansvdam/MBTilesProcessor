package org.damsoft.mbtiles;

import java.util.List;

import javax.swing.BoxLayout;

class MainWithinDirMergerPanel extends MainMergerPanel {


	/**
	 * 
	 */
	public MainWithinDirMergerPanel() {
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		String[] labels = { "Sources", "Target" };
		char[] mnemonics = { 'S', 'T' };
		int[] widths = { 15, 15, 15 };
		String[] descs = { "The directory with mbtiles",
				"The file that will contain all merged archives" };
		boolean[] scrollTextArea = {true,false};


		addExplanation("This left panel does not work yet (doesn't look good either)");
		form = new ArchiveMergerPanel(labels, mnemonics,
				widths, descs, scrollTextArea);
		add(form);
		buildRestGui();

	}

	public List<String> getSources(){
		return form.getDirs();
	}
	
}
