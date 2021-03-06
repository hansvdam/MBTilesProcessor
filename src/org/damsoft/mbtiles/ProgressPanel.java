/**
 * 
 */
package org.damsoft.mbtiles;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.TitledBorder;

/**
 * @author Hans van Dam
 *
 */
public class ProgressPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JProgressBar progressBar;
	private TitledBorder border;

	/**
	 * 
	 */
	public ProgressPanel() {
		super();
	    progressBar = new JProgressBar();
	    progressBar.setStringPainted(true);
	    border = BorderFactory.createTitledBorder("No Progress Yet");
	    progressBar.setBorder(border);
	    add(progressBar);//, BorderLayout.CENTER);
//	    textArea = new JTextArea();
//	    add(textArea, BorderLayout.SOUTH);
	}


	public JProgressBar getProgressBar() {
		return progressBar;
	}

	public void setText(String string){
		border.setTitle(string);
	}
	
}
