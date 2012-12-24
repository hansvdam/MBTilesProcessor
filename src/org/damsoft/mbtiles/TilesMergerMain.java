package org.damsoft.mbtiles;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class TilesMergerMain extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField[] fields;

	// Create a form with the specified labels, tooltips, and sizes.
	public TilesMergerMain(String[] labels, char[] mnemonics, int[] widths,
			String[] tips) {
		super(new BorderLayout());
		JPanel labelPanel = new JPanel(new GridLayout(labels.length, 1));
		JPanel fieldPanel = new JPanel(new GridLayout(labels.length, 1));
		JPanel buttonPanel = new JPanel(new GridLayout(labels.length, 1));
		add(labelPanel, BorderLayout.WEST);
		add(fieldPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.EAST);
		fields = new JTextField[labels.length];
		Preferences prefs = Preferences.userNodeForPackage(getClass());
		for (int i = 0; i < labels.length; i += 1) {
			fields[i] = new JTextField();
			if (i < tips.length)
				fields[i].setToolTipText(tips[i]);
			if (i < widths.length)
				fields[i].setColumns(widths[i]);
			fields[i].setText(prefs.get("" + i, ""));
			fields[i].setEditable(false);
			JLabel lab = new JLabel(labels[i], JLabel.RIGHT);
			lab.setLabelFor(fields[i]);
			if (i < mnemonics.length)
				lab.setDisplayedMnemonic(mnemonics[i]);

			labelPanel.add(lab);
			JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
			p.add(fields[i]);
			fieldPanel.add(p);
			JButton button = new JButton("Browse...");
			button.addActionListener(new FileSelectorListener(fields[i], i,
					prefs));
			buttonPanel.add(button);
		}

	}

	class FileSelectorListener implements ActionListener {
		JTextField textField;
		private Preferences prefs;
		private int index;

		/**
		 * @param textField
		 * @param prefs
		 */
		public FileSelectorListener(JTextField textField, int index,
				Preferences prefs) {
			super();
			this.textField = textField;
			this.prefs = prefs;
			this.index = index;
		}

		public void actionPerformed(ActionEvent e) {
			String currentDir = prefs.get("" + index, "");
			JFileChooser c = new JFileChooser(currentDir);
			c.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			// Demonstrate "Open" dialog:
			int rVal = c.showOpenDialog(TilesMergerMain.this);
			if (rVal == JFileChooser.APPROVE_OPTION) {
				String currDir = c.getSelectedFile().toString();
				textField.setText(currDir);
				prefs.put("" + index, textField.getText());
			}
		}
	}

	public List<String> getDirs() {
		List<String> returnValue = new ArrayList<String>();
		for (JTextField textField : fields) {
			returnValue.add(textField.getText());
		}
		return returnValue;
	}

	public String getText(int i) {
		return (fields[i].getText());
	}


	static class MainPanel extends JPanel implements ICancelRequestedProvider {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * 
		 */
		public MainPanel() {
			super();
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			String[] labels = { "Source 1", "Source 2", "Target" };
			char[] mnemonics = { 'S', 'O', 'T' };
			int[] widths = { 15, 15, 15 };
			String[] descs = { "The first source directory with mbtiles",
					"The second source directory with mbtiles",
					"The directory that will contian the merged archives" };


			JTextArea textArea = new JTextArea("In the following form select three directories: two source directories containing equally named mbtile-archives: probably about the same are, but containing different zoom levels. The 'target' directory is where the merges end up. This way if you have a number of archives, but some zoomlevels are missing, you only need to create archives with the same names containing the missing zoomlevels, and they can be merged with the original archives.");
			textArea.setLineWrap(true);
			textArea.setEditable(false);
			add(textArea);
			final TilesMergerMain form = new TilesMergerMain(labels, mnemonics,
					widths, descs);
			add(form);
			JButton merge = new JButton("Merge");
			cancelButton = new JButton("Cancel");
			cancelButton.setEnabled(false);
			cancelButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					setCancelrequested(true);
				}
			});
			progressPanel1 = new ProgressPanel();
			add(progressPanel1);
			progressPanel2 = new ProgressPanel();
			add(progressPanel2);
			JPanel p = new JPanel();
			p.add(merge);
			p.add(cancelButton);
			p.setBackground(Color.red);
			add(p);
			merge.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Thread thread = new Thread(new Runnable() {
						
						@Override
						public void run() {
							cancelButton.setEnabled(true);
							new Merger().merge(form.getDirs(), progressPanel1,
									progressPanel2, MainPanel.this);
						}
					});
					thread.start();
				}
			});
			
		}
		boolean cancelrequested = false;
		private JButton cancelButton;
		private ProgressPanel progressPanel1;
		private ProgressPanel progressPanel2;
		
		/* (non-Javadoc)
		 * @see org.damsoft.mbtiles.CancelRequestedProvider#isCancelrequested()
		 */
		@Override
		public boolean isCancelrequested() {
			return cancelrequested;
		}
		
		/* (non-Javadoc)
		 * @see org.damsoft.mbtiles.CancelRequestedProvider#setCancelrequested(boolean)
		 */
		@Override
		public void setCancelrequested(boolean cancelrequested) {
			this.cancelrequested = cancelrequested;
		}

		/* (non-Javadoc)
		 * @see org.damsoft.mbtiles.ICancelRequestedProvider#cancelRequestedExecuted()
		 */
		@Override
		public void cancelRequestExecuted() {
			cancelButton.setEnabled(false);
			progressPanel1.setText("Nothing is happening");
			progressPanel1.getProgressBar().setValue(0);
			progressPanel2.setText("Nothing is happening");
			progressPanel2.getProgressBar().setValue(0);
			cancelrequested = false;
		}
		
	}
	
	public static void main(String[] args) {

		JPanel mainPanel = new MainPanel();

		final JFrame f = new JFrame("MBTiles merger");
		f.setLayout(new BorderLayout());
		f.getContentPane().add(mainPanel);
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
