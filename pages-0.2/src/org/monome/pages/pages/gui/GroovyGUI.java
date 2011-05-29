package org.monome.pages.pages.gui;

import java.awt.GridBagLayout;
import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.monome.pages.pages.GroovyPage;
import java.awt.Dimension;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JTextPane;
import javax.swing.JEditorPane;
import javax.swing.text.EditorKit;

public class GroovyGUI extends JPanel {

	private static final long serialVersionUID = 1L;
	private GroovyPage page;
	private JLabel pageLabel = null;
	private JButton runBtn = null;
	public JTextPane codePane = null;
	private JScrollPane scrollPane;
	private JButton stopButton = null;
	/**
	 * This is the default constructor
	 */
	public GroovyGUI(GroovyPage page) {
		super();
		this.page = page;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(669, 539);
		pageLabel = new JLabel();
		pageLabel.setBounds(new Rectangle(5, 5, 291, 21));
		setName("Groovy");
		this.setLayout(null);
		this.add(pageLabel, null);
		this.add(getRunBtn(), null);
		this.add(getCodePane(), null);
		this.add(getStopButton(), null);
		this.setLayout(null);
	}
	
	public void setName(String name) {
		pageLabel.setText((page.getIndex() + 1) + ": " + name);
	}

	/**
	 * This method initializes runBtn	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getRunBtn() {
		if (runBtn == null) {
			runBtn = new JButton();
			runBtn.setBounds(new Rectangle(500, 495, 69, 31));
			runBtn.setText("Run");
			runBtn.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					page.runCode();
				}
			});
		}
		return runBtn;
	}

	/**
	 * This method initializes codePane	
	 * 	
	 * @return javax.swing.JEditorPane	
	 */
	private JScrollPane getCodePane() {
		if (codePane == null) {
			codePane = new JTextPane();
			codePane.setBounds(new Rectangle(5, 30, 656, 381));
			scrollPane = new JScrollPane(codePane);
			scrollPane.setBounds(new Rectangle(5, 30, 656, 451));
		}
		return scrollPane;
	}

	/**
	 * This method initializes stopButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getStopButton() {
		if (stopButton == null) {
			stopButton = new JButton();
			stopButton.setBounds(new Rectangle(590, 495, 71, 31));
			stopButton.setText("Stop");
			stopButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					page.stopCode();
				}
			});
		}
		return stopButton;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
