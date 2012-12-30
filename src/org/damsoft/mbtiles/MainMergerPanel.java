/**
 * 
 */
package org.damsoft.mbtiles;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.damsoft.mbtiles.mergers.Merger;

/**
 * @author Hans van Dam
 *
 */
public abstract class MainMergerPanel extends JPanel implements ICancelRequestedProvider {

	protected FileSystemChooserPanel form;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean cancelrequested = false;
	protected JButton cancelButton;
	protected ProgressPanel progressPanel1;
	protected ProgressPanel progressPanel2;

	private Merger merger;

	/**
	 * @param string 
	 * 
	 */
	public MainMergerPanel(Merger merger) {
		super();
		this.merger = merger;  
	}

	protected void buildRestGui() {
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
//		JProgressBar progB = progressPanel1.getProgressBar();
//		Dimension preferredSize = progB.getPreferredSize();
//		preferredSize.width = 2000;
//		progB.setPreferredSize(preferredSize);
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
						mergeCommand();
					}

					private void mergeCommand() {
						merger.merge(getSources(), progressPanel1,
								progressPanel2, MainMergerPanel.this);
						progressPanel1.getProgressBar().setValue(100);
						progressPanel2.getProgressBar().setValue(100);
						progressPanel2.setText("Finished");
					}

				});
				thread.start();
			}
		});
	}

	/**
	 * @param string
	 */
	protected void addMainTitle(String string) {
		JLabel label = new JLabel(string.toUpperCase());
		label.setBackground(Color.red);
		label.setAlignmentX(CENTER_ALIGNMENT);
		add(label);

	}

	public abstract List<String> getSources();

	/**
	 * @param layout
	 */
	public MainMergerPanel(LayoutManager layout) {
		super(layout);
	}

	/**
	 * @param isDoubleBuffered
	 */
	public MainMergerPanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
	}

	/**
	 * @param layout
	 * @param isDoubleBuffered
	 */
	public MainMergerPanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
	}

	@Override
	public boolean isCancelrequested() {
		return cancelrequested;
	}

	@Override
	public void setCancelrequested(boolean cancelrequested) {
		this.cancelrequested = cancelrequested;
	}

	@Override
	public void cancelRequestExecuted() {
		cancelButton.setEnabled(false);
		progressPanel1.setText("Nothing is happening");
		progressPanel1.getProgressBar().setValue(0);
		progressPanel2.setText("Nothing is happening");
		progressPanel2.getProgressBar().setValue(0);
		cancelrequested = false;
	}

	protected void addExplanation(String string) {
		JTextArea textArea = new JTextArea(string);
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		add(textArea);
//		Component horizontalStrut = Box.createRigidArea(new Dimension(200, 200));
//		add(horizontalStrut);
	}

	public FileSystemChooserPanel getForm() {
		return form;
	}

	public void fixProgressBars(){
		fixProgressBar(progressPanel1);
		fixProgressBar(progressPanel2);
	}

	private void fixProgressBar(ProgressPanel progressPanel) {
		Dimension size = progressPanel.getSize();
		JProgressBar progBar = progressPanel.getProgressBar();
		Dimension preferredSize = progBar.getPreferredSize();
		preferredSize.width = size.width;
		progBar.setPreferredSize(preferredSize);
	}
}