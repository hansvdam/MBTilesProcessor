/**
 * 
 */
package org.damsoft.mbtiles.mergers;

import java.awt.Container;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.damsoft.mbtiles.ICancelRequestedProvider;
import org.damsoft.mbtiles.ProgressPanel;

/**
 * @author Hans van Dam
 *
 */
public class FileMerger extends Merger {

	/**
	 * @param progressPanel2
	 * @param form
	 * 
	 */
	public void merge(List<String> filePaths, ProgressPanel progressPanel1,
			final ProgressPanel progressPanel2,
			final ICancelRequestedProvider cancelProvider) {
		initJdbcDriver();
		List<String> sourceFiles = new ArrayList<String>(filePaths.subList(0, filePaths.size()-1));
		String targetPath = filePaths.get(filePaths.size()-1);
		if(!isInputOk(sourceFiles, targetPath)){
			return;
		}
		
		String largestFilePath = copyLargestAsTarget(sourceFiles,
				targetPath);
		sourceFiles.remove(largestFilePath);
		List<Statement> statements = new ArrayList<Statement>();
		List<String> bounds = new ArrayList<String>();
		progressPanel1.setText("total progress");
		for (final String fileName : sourceFiles) {
			try {

				List<String> names = new ArrayList<String>();

				Statement sourceStatement = createDbStatement(names, bounds, fileName);
				statements.add(sourceStatement);

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String targetBounds = combineBounds(bounds);
		ConnectionAndStatement connectionAndStatement = initTargetDb(
				targetPath, targetBounds, extractName(targetPath));
		Statement targetStat = connectionAndStatement.stat;
		merginTiles(connectionAndStatement.conn, statements,
				progressPanel1, progressPanel2, cancelProvider, sourceFiles);
		finishTargetDb(targetStat);
		try {
			targetStat.close();
			for (Statement statement : statements) {
				statement.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param conn
	 * @param collection
	 * @param progressPanel2
	 * @param cancelProvider 
	 */
	protected void merginTiles(Connection conn, Collection<Statement> collection,
			ProgressPanel progressPanel1, final ProgressPanel progressPanel2, final ICancelRequestedProvider cancelProvider,List<String> sourceFiles) {
		int counter = 0;
		double perFileProgress = 1.0 / collection.size();
		Iterator<String> sourceFileNameIterator = sourceFiles.iterator();
		for (Statement statement : collection) {
			final String fileName = sourceFileNameIterator.next();
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					progressPanel2.setText("merging " + fileName);
				}
			});
			mergeInTile(conn, progressPanel2, statement);
			SwingUtilities.invokeLater(new ProgressUpdater(progressPanel1,
					counter, perFileProgress));
			if (cancelProvider.isCancelrequested()) {
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						cancelProvider.cancelRequestExecuted();
					}
				});
				break;
			}
			counter++;
		}
	}


	/**
	 * @param targetPath
	 * @return
	 */
	private String extractName(String targetPath) {
		File file = new File(targetPath);
		return file.getName();
	}

	private boolean isInputOk(List<String> sourceFiles, String targetPath) {
		boolean shouldContinue = true;
		if(targetPath.equals("")){
			sendAlert("target file (for merge result) is not specified");
			shouldContinue = false;
		} 
		if(!targetPath.endsWith(".mbtiles")){
			sendAlert("target file (for merge result):" + targetPath + " should have .mbtiles extention");
			shouldContinue = false;
		}
		String falseString = null;
		for (String string : sourceFiles) {
			if(!string.endsWith(".mbtiles")){
				falseString = string;
			}
		}
		if(falseString!=null){
			sendAlert("sourceFile " + falseString+ " should have extension .mbtiles");
			shouldContinue = false;
		}
		return shouldContinue;
	}

	/**
	 * @param string
	 */
	private void sendAlert(final String string) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JOptionPane.showMessageDialog(null, string);
			}
		});
	}

}
