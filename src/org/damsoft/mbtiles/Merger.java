/**
 * 
 */
package org.damsoft.mbtiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

import org.apache.commons.io.FileUtils;

/**
 * @author Hans van Dam
 * 
 *         mbtilesspec can be found at https://github.com/mapbox/mbtiles-spec
 */
public class Merger {

	/**
	 * @param dirs
	 * @param f
	 */
	public Merger() {
	}

	/**
	 * @param progressPanel2
	 * @param form
	 * 
	 */
	public void mergeDirs(List<String> dirs, ProgressPanel progressPanel1,
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
				sourceStat1 = createDbStatement(path).stat;
				ResultSet metadata1 = sourceStat1
						.executeQuery("Select * from metadata");
				String bounds1 = getBounds(metadata1);
				String name1 = getName(metadata1);
				bounds.add(bounds1);
				names.add(name1);
				returnValue.put(path, sourceStat1);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return returnValue;
	}

	/**
	 * @param dbPaths
	 * @param string
	 * @return
	 */
	private String copyLargestAsTarget(List<String> dbPaths, String targetPath) {
		long largestSize = 0;
		File largestFile = null;
		String largestFilePath = null;
		for (String path : dbPaths) {
			File file = new File(path);
			long fileLength = file.length();
			if (fileLength > largestSize) {
				largestSize = fileLength;
				largestFile = file;
				largestFilePath = path;
			}
		}
		try {
			FileUtils.copyFile(largestFile, new File(targetPath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return largestFilePath;
	}

	/**
	 * @param conn
	 * @param collection
	 * @param progressPanel2
	 */
	private void merginTiles(Connection conn, Collection<Statement> collection,
			ProgressPanel progressPanel1, final ProgressPanel progressPanel2) {
		for (Statement statement : collection) {
			try {
				ResultSet countSet = statement
						.executeQuery("Select Count(*) from tiles");
				countSet.next();
				int numberOfRecords = countSet.getInt(1);
				ResultSet resultset = statement
						.executeQuery("Select * from tiles");
				String sql = "insert into tiles values (?,?,?,?)";
				PreparedStatement prep = conn.prepareStatement(sql);
				final double perResult = 1.0 / numberOfRecords;
				// setProgressMessage("merging ")
				int counter = 0;
				while (resultset.next()) {
					// prep.setBinaryStream(3, fis, fileLenght);
					SwingUtilities.invokeLater(new ProgressUpdater(
							progressPanel2, counter, perResult));
					prep.setInt(1, resultset.getInt("zoom_level"));
					prep.setInt(2, resultset.getInt("tile_column"));
					prep.setInt(3, resultset.getInt("tile_row"));
					byte[] bytes = resultset.getBytes("tile_data");
					prep.setBytes(4, bytes);
					prep.execute();
					counter++;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param metadata1
	 * @return
	 */
	private String getName(ResultSet metadata) {
		return getValueFromMetadata(metadata, "name");
	}

	/**
	 * @param metadata
	 * @return
	 */
	private String getBounds(ResultSet metadata) {
		return getValueFromMetadata(metadata, "bounds");
	}

	private String getValueFromMetadata(ResultSet metadata, String key) {
		String returnValue = null;
		try {
			while (metadata.next()) {
				String name = metadata.getString("name");
				if (name.equals(key)) {
					returnValue = metadata.getString("value");
					break;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnValue;
	}

	/**
	 * @param bounds1
	 * @param bounds2
	 * @return
	 */
	private String combineBounds(List<String> bounds) {
		String resultBounds = "";
		double[][] boundsMatrix = new double[4][bounds.size()];
		int outerCounter = 0;
		for (String resultSet : bounds) {
			if (resultSet != null) {
				String[] indivCoords = resultSet.split(",");
				int counter = 0;
				for (String string : indivCoords) {
					boundsMatrix[counter][outerCounter] = Double
							.parseDouble(string);
					counter++;
				}
			}
			outerCounter++;
		}
		double resultBound = getResultBound(boundsMatrix[0], -180);
		resultBounds += resultBound;
		resultBound = getResultBound(boundsMatrix[1], 90);
		resultBounds += "," + resultBound;
		resultBound = getResultBound(boundsMatrix[2], 180);
		resultBounds += "," + resultBound;
		resultBound = getResultBound(boundsMatrix[3], -90);
		resultBounds += "," + resultBound;

		return resultBounds;
	}

	/**
	 * @param bounds1
	 * @param bounds2
	 * @return
	 */
	private String combineNames(List<String> names) {
		String result = "";
		int counter = 0;
		for (String string : names) {
			if (counter < 2) {
				result += string;
				if (counter != names.size() - 1) {
					result += "-";
				}
			}
			counter++;
		}
		if (counter > 2) {
			result += "...";
		}
		return result;
	}

	/**
	 * @param bounds
	 * @param extreme
	 *            : example: when extreme = -180, this means that we are looking
	 *            for a minimum value, where the smallest possible value = -180.
	 * @return
	 */
	private double getResultBound(double[] bounds, double extreme) {
		double resultBound = -extreme;
		boolean useMax = extreme > 0;
		for (int i = 0; i < bounds.length; i++) {
			resultBound = (useMax ? max(bounds) : min(bounds));
		}
		return resultBound;
	}

	/**
	 * @param bounds
	 * @return
	 */
	private double min(double[] bounds) {
		double result = Double.MAX_VALUE;
		for (double d : bounds) {
			if (d < result) {
				result = d;
			}
		}
		return result;
	}

	/**
	 * @param bounds
	 * @return
	 */
	private double max(double[] bounds) {
		double result = Double.MIN_VALUE;
		for (double d : bounds) {
			if (d > result) {
				result = d;
			}
		}
		return result;
	}

	/**
	 * @param targetStat
	 */
	private void finishTargetDb(Statement targetStat) {
		// TODO Auto-generated method stub
		try {
			executeStatementsInFile(targetStat, "mbtilescreatorfinisher.sql",
					new String[] {});
			targetStat.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Set<String> createFileNamesSet(String sourceDirPath) {
		File sourceDir1 = new File(sourceDirPath);
		String[] filesNames = sourceDir1.list();
		Set<String> fileNamesSet = new HashSet<String>();
		for (String string : filesNames) {
			fileNamesSet.add(string);
		}
		return fileNamesSet;
	}

	private String[] readSqlStatements(String fileName) {
		InputStream fr;
		String[] inst = null;
		try {
			URL url = getClass().getResource(fileName);
			fr = url.openStream();
//			fr = new FileInputStream("resources/" + fileName);
			inst = readSqlFromStream(fr);
		} catch (final IOException e) {
		}
		return inst;
	}

	public static String[] readSqlFromStream(InputStream fr) throws IOException {
		final StringBuffer sb = new StringBuffer();
		String s;
		final InputStreamReader isr = new InputStreamReader(fr);
		final BufferedReader br = new BufferedReader(isr);

		while ((s = br.readLine()) != null) {
			sb.append(s);
		}
		br.close();

		// here is our splitter ! We use ";" as a delimiter for each request
		// then we are sure to have well formed statements
		final String[] inst = sb.toString().split(";");
		return inst;
	}

	private ConnectionAndStatement initTargetDb(String targetDbPath,
			String targetBounds, String targetName) {
		Statement stat = null;
		ConnectionAndStatement connectionAndStatement = null;
		try {
			connectionAndStatement = createDbStatement(targetDbPath);
			stat = connectionAndStatement.stat;
			executeStatementsInFile(stat, "mbtilescreator.sql", new String[] {
					targetBounds, targetName });
		} catch (final SQLException e) {
		}
		return connectionAndStatement;
	}

	/**
	 * @author Hans van Dam
	 * 
	 */
	private final class ProgressUpdater implements Runnable {
		/**
		 * 
		 */
		private final ProgressPanel progressPanel2;
		/**
		 * 
		 */
		private final int counter;
		/**
		 * 
		 */
		private final double perResult;

		/**
		 * @param progressPanel2
		 * @param counter
		 * @param perResult
		 */
		private ProgressUpdater(ProgressPanel progressPanel2, int counter,
				double perResult) {
			this.progressPanel2 = progressPanel2;
			this.counter = counter;
			this.perResult = perResult;
		}

		@Override
		public void run() {
			progressPanel2.getProgressBar().setValue(
					(int) (perResult * counter * 100));
		}
	}

	static class ConnectionAndStatement {
		Connection conn;
		Statement stat;

		/**
		 * @param conn
		 * @param stat
		 */
		public ConnectionAndStatement(Connection conn, Statement stat) {
			super();
			this.conn = conn;
			this.stat = stat;
		}

	}

	private static ConnectionAndStatement createDbStatement(String dbPath)
			throws SQLException {
		Statement stat;
		Connection conn = DriverManager.getConnection("jdbc:sqlite:"
				+ new File(dbPath).getAbsolutePath());
		conn.setAutoCommit(true);
		stat = conn.createStatement();
		return new ConnectionAndStatement(conn, stat);
	}

	private void executeStatementsInFile(Statement stat,
			String fileName, String[] strings) throws SQLException {
		String[] inst = readSqlStatements(fileName);

		for (int i = 0; i < inst.length; i++) {
			if (inst[i].trim().length() != 0) {
				inst[i] = replacePlaceHolder(inst[i], strings);
				stat.executeUpdate(inst[i]);
			}
		}
	}

	/**
	 * @param string
	 * @param strings
	 * @return
	 */
	private static String replacePlaceHolder(String string, String[] strings) {
		int counter = 1;
		for (String string2 : strings) {
			string = string.replace("$" + counter, string2);
			counter++;
		}
		return string;
	}

	/**
	 * necessary to initialize the Jdbc driver in the Jvm
	 */
	private static void initJdbcDriver() {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (final ClassNotFoundException e) {
		}
	}

}
