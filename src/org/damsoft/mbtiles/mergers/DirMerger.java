/**
 * 
 */
package org.damsoft.mbtiles.mergers;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.damsoft.mbtiles.ICancelRequestedProvider;
import org.damsoft.mbtiles.ProgressPanel;

/**
 * @author Hans van Dam
 *
 */
public class DirMerger extends Merger {

	/**
	 * @param progressPanel2
	 * @param form
	 * 
	 */
	public void merge(List<String> dirs, ProgressPanel progressPanel1,
			ProgressPanel progressPanel2,
			final ICancelRequestedProvider cancelProvider) {
		initJdbcDriver();
		System.out.println("hoi");
		Set<String> filenamesS1 = createFileNamesSet(dirs.get(0));
		Set<String> filenamesS2 = createFileNamesSet(dirs.get(1));
		Set<String> missingFiles = new HashSet<String>(filenamesS1);
		missingFiles.removeAll(filenamesS2);
		System.out
				.println("The following files from dir 1 were not merged, because they don't exist in sourceDir 2");
		for (String string : missingFiles) {
			System.out.println(string);
		}
		Set<String> crossSection = new HashSet<String>(filenamesS1);
		crossSection.retainAll(filenamesS2);
		double perFileProgress = 1.0 / crossSection.size();
		int counter = 0;
		for (String fileName : crossSection) {
			try {
				SwingUtilities.invokeLater(new ProgressUpdater(progressPanel1,
						counter, perFileProgress));

				progressPanel1.setText("total progress");
				progressPanel2.setText("merging " + fileName);
				List<String> dbPaths = new ArrayList<String>();
				dbPaths.add(dirs.get(0) + "/" + fileName);
				dbPaths.add(dirs.get(1) + "/" + fileName);

				String targetPath = dirs.get(2) + "/" + fileName;
				String largestFilePath = copyLargestAsTarget(dbPaths,
						targetPath);

				List<String> names = new ArrayList<String>();
				List<String> bounds = new ArrayList<String>();

				Map<String, Statement> statements = fillNamesAndBounds(dbPaths,
						names, bounds);

				dbPaths.remove(largestFilePath);
				statements.remove(largestFilePath);

				String targetBounds = combineBounds(bounds);
				String targetName = combineNames(names);
				ConnectionAndStatement connectionAndStatement = initTargetDb(
						targetPath, targetBounds, targetName);
				Statement targetStat = connectionAndStatement.stat;

				merginTiles(connectionAndStatement.conn, statements.values(),
						null, progressPanel2);
				finishTargetDb(targetStat);
				targetStat.close();
				for (Statement statement : statements.values()) {
					statement.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			counter++;
			if (cancelProvider.isCancelrequested()) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						cancelProvider.cancelRequestExecuted();
					}
				});
				break;
			}
		}
	}


	/**
	 * @param dbPaths
	 * @param names
	 * @param bounds
	 * @return
	 */
	private Map<String, Statement> fillNamesAndBounds(List<String> dbPaths,
			List<String> names, List<String> bounds) {
		Map<String, Statement> returnValue = new HashMap<String, Statement>();
		for (String path : dbPaths) {
			Statement sourceStat1;
			try {
				sourceStat1 = createDbStatement(names, bounds, path);
				returnValue.put(path, sourceStat1);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return returnValue;
	}

}
