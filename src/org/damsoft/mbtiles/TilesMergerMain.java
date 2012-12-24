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
import javax.swing.JTextField;

public class TilesMergerMain extends JPanel implements CancelRequestedProvider {

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

	boolean cancelrequested = false;
	
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

	public static void main(String[] args) {
		String[] labels = { "Source 1", "Source 2", "Target" };
		char[] mnemonics = { 'S', 'O', 'T' };
		int[] widths = { 15, 15, 15 };
		String[] descs = { "The first source directory with mbtiles",
				"The second source directory with mbtiles",
				"The directory that will contian the merged archives" };

		final TilesMergerMain form = new TilesMergerMain(labels, mnemonics,
				widths, descs);

		final JFrame f = new JFrame("MBTiles merger");
		f.setLayout(new BorderLayout());
		JButton merge = new JButton("Merge");
		JButton cancel = new JButton("Cancel");
		cancel.setEnabled(false);
		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				form.setCancelrequested(true);
			}
		});

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(form);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(mainPanel);
		final ProgressPanel progressPanel1 = new ProgressPanel();
		mainPanel.add(progressPanel1);
		final ProgressPanel progressPanel2 = new ProgressPanel();
		mainPanel.add(progressPanel2);
		JPanel p = new JPanel();
		p.add(merge);
		p.add(cancel);
		p.setBackground(Color.red);
		mainPanel.add(p);
		merge.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread thread = new Thread(new Runnable() {

					@Override
					public void run() {
						new Merger().merge(form.getDirs(), progressPanel1,
								progressPanel2, form);
					}
				});
				thread.start();
			}
		});

		f.pack();
		// int paneWidth = f.getContentPane().getWidth();
		f.setVisible(true);
		// f.getContentPane().add(form, BorderLayout.NORTH);
		// JPanel p = new JPanel();
		// p.add(submit);
		// f.getContentPane().add(p, BorderLayout.SOUTH);
		// f.pack();
		// f.setVisible(true);
	}
}
