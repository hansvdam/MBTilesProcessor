package org.damsoft.mbtiles;

import java.util.List;

import javax.swing.BoxLayout;

import org.damsoft.mbtiles.mergers.FileMerger;

class MainFileMergerPanel extends MainMergerPanel {


	/**
	 * 
	 */
	public MainFileMergerPanel() {
		super(new FileMerger());
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		String[] labels = { "Sources", "Target" };
		char[] mnemonics = { 'S', 'T' };
//		int[] widths = { 15, 15, 15 };
		String[] descs = { "The directory with mbtiles",
				"The file that will contain all merged archives" };
		boolean[] scrollTextArea = {true,false};
		boolean[] editableFields = {false,true};
		
		addMainTitle("Merging mbtiles-archives in one directory");
		addExplanation("In this panel select the source-files that you want to merge and the directory and filename of the new archive to be created (Target) by the merge operation. Then press merge and wait.");
		form = new FileMergerPanel(labels, mnemonics,descs, scrollTextArea, editableFields);
		add(form);
		buildRestGui();

	}

	public List<String> getSources(){
		return form.getSources();
	}
	
}
