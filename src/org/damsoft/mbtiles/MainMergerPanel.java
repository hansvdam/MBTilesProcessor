/**
 * 
 */
package org.damsoft.mbtiles;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

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

	/**
	 * @param string 
	 * 
	 */
	public MainMergerPanel() {
		super();

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
						new Merger().mergeDirs(getSources(), progressPanel1,
								progressPanel2, MainMergerPanel.this);
					}

				});
				thread.start();
			}
		});
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
		Component horizontalStrut = Box.createRigidArea(new Dimension(200, 200));
		add(horizontalStrut);
	}

}