package org.damsoft.mbtiles;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

public abstract class FileSystemChooserPanel extends JPanel implements
		IFileChooserModifier {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected JTextComponent[] fields;

	// Create a form with the specified labels, tooltips, and sizes.
	public FileSystemChooserPanel(String[] labels, char[] mnemonics,
			int[] widths, String[] tips, boolean[] scrollTextArea) {
		super(new BorderLayout());
		JPanel labelPanel = new JPanel(new GridLayout(labels.length, 1));
		JPanel fieldPanel = new JPanel(new GridLayout(labels.length, 1));
		JPanel buttonPanel = new JPanel(new GridLayout(labels.length, 1));
		add(labelPanel, BorderLayout.WEST);
		add(fieldPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.EAST);
		fields = new JTextComponent[labels.length];
		Preferences prefs = Preferences.userNodeForPackage(getClass());
		for (int i = 0; i < labels.length; i += 1) {
			JScrollPane scPane = null;
			if (scrollTextArea[i]) {
				fields[i] = new JTextArea();
				((JTextArea)fields[i]).setLineWrap(true);
				((JTextArea)fields[i]).setWrapStyleWord(true);
				scPane = new JScrollPane(fields[i]);
				scPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			} else {
				fields[i] = new JTextField();
			}
			if (i < tips.length)
				fields[i].setToolTipText(tips[i]);
			if (i < widths.length)
				if (fields[i] instanceof JTextField)
					((JTextField) fields[i]).setColumns(widths[i]);
				else {
					Dimension dimension = new Dimension(200,100);
					scPane.setMaximumSize(dimension);
					scPane.setPreferredSize(new Dimension(300, 100));
				}
			fields[i].setText(prefs.get(getClass().toString() + i, ""));
			fields[i].setEditable(false);
			JLabel lab = new JLabel(labels[i], JLabel.RIGHT);
			lab.setLabelFor(fields[i]);
			if (i < mnemonics.length)
				lab.setDisplayedMnemonic(mnemonics[i]);

			labelPanel.add(lab);
			JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
			if (scrollTextArea[i]) {
				p.add(scPane);
			} else {
				p.add(fields[i]);
			}
			fieldPanel.add(p);
			JButton button = new JButton("Browse...");
			button.addActionListener(new FileSelectorListener(fields[i], i,
					prefs, this));
			buttonPanel.add(button);
		}

	}

	class FileSelectorListener implements ActionListener {
		JTextComponent textField;
		private Preferences prefs;
		private int index;
		private IFileChooserModifier fileChooserModifier;

		/**
		 * @param fields
		 * @param prefs
		 * @param tilesDirMergerPanel
		 */
		public FileSelectorListener(JTextComponent fields, int index,
				Preferences prefs, IFileChooserModifier fileChooserModifier) {
			super();
			this.fileChooserModifier = fileChooserModifier;
			this.textField = fields;
			this.prefs = prefs;
			this.index = index;
		}

		public void actionPerformed(ActionEvent e) {
			String currentDir = prefs.get(fileChooserModifier.getClass().toString() + index, "");
			JFileChooser c = new JFileChooser(currentDir);
			fileChooserModifier.setFileChooserProperties(c);
			// Demonstrate "Open" dialog:
			int rVal = c.showOpenDialog(FileSystemChooserPanel.this);
			if (rVal == JFileChooser.APPROVE_OPTION) {
				File[] files = c.getSelectedFiles();
				if (files.length == 0) {
					String currDir = c.getSelectedFile().toString();
					textField.setText(currDir);
					prefs.put(
							fileChooserModifier.getClass().toString() + index,
							textField.getText());
				} else {
					prefs.put(
							fileChooserModifier.getClass().toString() + index,
							files[0].getParent());
					String text = "";
					int counter = 0;
					for (File file : files) {
						text+=file.getAbsolutePath();
						if(counter!=files.length-1){
							text += ";";
						}
						counter++;
					}
					textField.setText(text);
				}
			}
		}

	}

	public abstract List<String> getDirs();

	public String getText(int i) {
		return (fields[i].getText());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.damsoft.mbtiles.IFileChooserModifier#setFileChooserProperties(javax
	 * .swing.JFileChooser)
	 */
	@Override
	public void setFileChooserProperties(JFileChooser fileChooser) {
	}

}
