package org.damsoft.mbtiles;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
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
			String[] tips, boolean[] scrollTextArea, boolean[] editableFields) {
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		// JPanel labelPanel = new JPanel(new GridLayout(labels.length, 1));
		// JPanel fieldPanel = new JPanel(new GridLayout(labels.length, 1));
		// JPanel buttonPanel = new JPanel(new GridLayout(labels.length, 1));
		// add(labelPanel, BorderLayout.WEST);
		// add(fieldPanel, BorderLayout.CENTER);
		// add(buttonPanel, BorderLayout.EAST);
		JLabel[] guilabels = new JLabel[labels.length];
		fields = new JTextComponent[labels.length];
		JButton[] buttons = new JButton[labels.length];
		List<JScrollPane> scPanes = new ArrayList<JScrollPane>();
		Preferences prefs = Preferences.userNodeForPackage(getClass());
		for (int i = 0; i < labels.length; i += 1) {
			JPanel linePanel = new JPanel();
			add(linePanel);
			JScrollPane scPane = null;
			if (scrollTextArea[i]) {
				fields[i] = new JTextArea();
				((JTextArea) fields[i]).setLineWrap(true);
				((JTextArea) fields[i]).setWrapStyleWord(true);
				scPane = new JScrollPane(fields[i]);
				scPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
				scPanes.add(scPane);
			} else {
				fields[i] = new JTextField();
			}
			if (i < tips.length)
				fields[i].setToolTipText(tips[i]);
			// if (i < widths.length)
			if (fields[i] instanceof JTextField)
				((JTextField) fields[i]).setColumns(30);
			else {
				// Dimension dimension = new Dimension(200,100);
				// scPane.setMaximumSize(dimension);
				scPane.setPreferredSize(new Dimension(300, 100));
			}
			String prefString = getClass().toString() + i;
			fields[i].setText(prefs.get(prefString, ""));
			if (editableFields==null || !editableFields[i]) {
				fields[i].setEditable(false);
			} else {
				fields[i].getDocument().addDocumentListener(new MyDocumentListener(prefs, prefString));
			}
			Dimension prefWidth = fields[i].getPreferredSize();
			JLabel lab = new JLabel(labels[i], JLabel.RIGHT);
			guilabels[i] = lab;
			lab.setLabelFor(fields[i]);
			if (i < mnemonics.length)
				lab.setDisplayedMnemonic(mnemonics[i]);

			linePanel.add(lab);
			JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
			if (scrollTextArea[i]) {
				p.add(scPane);
			} else {
				p.add(fields[i]);
			}
			linePanel.add(p);
			JButton button = new JButton("Browse...");
			buttons[i] = button;
			button.addActionListener(new FileSelectorListener(fields[i], i,
					prefs, this));
			linePanel.add(button);
		}
		int maxLabel = getMaxWith(guilabels);
		setPrefWidth(guilabels, maxLabel);
		int maxField = getMaxWith(fields);
		setPrefWidth(scPanes.toArray(new JComponent[0]), (maxField == 0 ? 169
				: maxField));
		// setPrefWidth(fields, 100);
	}

	/**
	 * @param components
	 * @param maxLabel
	 */
	private void setPrefWidth(JComponent[] components, int maxLabel) {
		for (JComponent component : components) {
			Dimension prefSize = component.getPreferredSize();
			prefSize.setSize(maxLabel, prefSize.getHeight());
			component.setPreferredSize(prefSize);
			// Dimension maxSize = jLabel.getMaximumSize();
			// prefSize.setSize(maxLabel, maxSize.getHeight());
			// jLabel.setMaximumSize(maxSize);
		}

	}

	/**
	 * @param components
	 * @return
	 */
	private int getMaxWith(JComponent[] components) {
		int max = 0;
		for (JComponent component : components) {
			Dimension prefSize = component.getPreferredSize();
			if (prefSize.width > max) {
				max = prefSize.width;
			}
		}
		return max;
	}

	/**
	 * @author Hans van Dam
	 *
	 */
	private final class MyDocumentListener implements DocumentListener {
		private Preferences prefs;
		private String prefString;

		/**
		 * @param prefs
		 * @param prefString
		 */
		public MyDocumentListener(Preferences prefs, String prefString) {
			this.prefs = prefs;
			this.prefString = prefString;
		}

		@Override
		public void removeUpdate(DocumentEvent arg0) {
			updatePref(arg0);
		}

		private void updatePref(DocumentEvent arg0) {
			Document document = arg0.getDocument();
			try {
				prefs.put(prefString, document.getText(0, document.getLength()));
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void insertUpdate(DocumentEvent arg0) {
			updatePref(arg0);
		}

		@Override
		public void changedUpdate(DocumentEvent arg0) {
			updatePref(arg0);
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
			String currentDir = prefs.get(fileChooserModifier.getClass()
					.toString() + index, "");
			String[] dirComponents = currentDir.split("\n");
			if(dirComponents.length>1){
				currentDir = new File(dirComponents[0]).getParent();
			}
			JFileChooser c = new JFileChooser(currentDir);
			fileChooserModifier.setFileChooserProperties(c);
			c.setPreferredSize(new Dimension(1000, 1000));
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
					String text = "";
					int counter = 0;
					for (File file : files) {
						text += file.getAbsolutePath();
						if (counter != files.length - 1) {
							text += "\n";
						}
						counter++;
					}
					textField.setText(text);
					prefs.put(
							fileChooserModifier.getClass().toString() + index,
							text);
				}
			}
		}

	}

	public List<String> getSources() {
		List<String> returnValue = new ArrayList<String>();
		for (JTextComponent textField : fields) {
			returnValue.add(textField.getText());
		}
		return returnValue;
	}

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
