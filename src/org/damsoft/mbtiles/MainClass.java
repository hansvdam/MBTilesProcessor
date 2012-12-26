/**
 * 
 */
package org.damsoft.mbtiles;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * @author Hans van Dam
 *
 */
public class MainClass {

	public static void main(String[] args) {

		final JFrame f = new JFrame("MBTiles merger");

		JPanel surroundingPanel = new JPanel();
		surroundingPanel.setLayout(new BoxLayout(surroundingPanel, BoxLayout.X_AXIS));

		MainDirMergerPanel dirMergerPanel = new MainDirMergerPanel();
		surroundingPanel.add(dirMergerPanel);
		Dimension prefSize = dirMergerPanel.getForm().getPreferredSize();
		dirMergerPanel.getForm().setMaximumSize(prefSize);

		surroundingPanel.add(new JSeparator(SwingConstants.VERTICAL));
		surroundingPanel.add(new JSeparator(SwingConstants.VERTICAL));
		surroundingPanel.add(new JSeparator(SwingConstants.VERTICAL));
		surroundingPanel.add(new JSeparator(SwingConstants.VERTICAL));

		MainMergerPanel archiveMergerPanel = new MainWithinDirMergerPanel();
		surroundingPanel.add(archiveMergerPanel);
		archiveMergerPanel.getForm().setMaximumSize(prefSize);

		f.setLayout(new BorderLayout());
		f.getContentPane().add(surroundingPanel);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
		f.setVisible(true);
		// don't knwo why, but layout needs the following, because at the first attempt to packk apparently not everything
		// can be considered properly....:
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				f.pack();
			}
		});
	}

}
