package org.monome.pages.pages.gui;

import java.awt.GridBagLayout;
import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.monome.pages.configuration.ConfigurationFactory;
import org.monome.pages.gui.Main;
import org.monome.pages.pages.GroovyPage;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
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
	private JButton saveButton = null;
	private JButton loadButton = null;
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
		this.add(getSaveButton(), null);
		this.add(getLoadButton(), null);
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

	/**
	 * This method initializes saveButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSaveButton() {
		if (saveButton == null) {
			saveButton = new JButton();
			saveButton.setBounds(new Rectangle(10, 495, 76, 31));
			saveButton.setText("Save");
			saveButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					saveScript();
				}
			});
		}
		return saveButton;
	}
	
	public void saveScript() {
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			try {
				if (ConfigurationFactory.getConfiguration() != null) {
					FileWriter fw = new FileWriter(file);
					fw.write(codePane.getText());
					fw.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * This method initializes loadButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getLoadButton() {
		if (loadButton == null) {
			loadButton = new JButton();
			loadButton.setBounds(new Rectangle(110, 495, 71, 31));
			loadButton.setText("Load");
			loadButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					loadScript();
				}
			});
		}
		return loadButton;
	}
	
	private void loadScript() {
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(fis));
			String code = "";
			try {
				while (in.ready()) {
					code += in.readLine() + "\n";
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.codePane.setText(code);
		}
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
