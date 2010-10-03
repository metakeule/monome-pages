package org.monome.pages.pages.gui;

import java.awt.GridBagLayout;
import javax.swing.JPanel;

import org.monome.pages.pages.MIDIGeneratorPage;
import javax.swing.JLabel;
import java.awt.Rectangle;
import java.awt.Dimension;

public class MIDIGeneratorGUI extends JPanel {

	private static final long serialVersionUID = 1L;
	private MIDIGeneratorPage page;
	private JLabel pageLabel = null;

	/**
	 * This is the default constructor
	 */
	public MIDIGeneratorGUI(MIDIGeneratorPage page) {
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
		this.setSize(207, 196);
		this.setLayout(null);
		this.add(getPageLabel(), null);
		setName("MIDI Generator Page");
	}
	
	public void setName(String name) {
		pageLabel.setText((page.getIndex() + 1) + ": " + name);
	}


	/**
	 * This method initializes pageLabel	
	 * 	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getPageLabel() {
		if (pageLabel == null) {
			pageLabel = new JLabel();
			pageLabel.setName("MIDI Generator Page");
			pageLabel.setBounds(new Rectangle(5, 5, 191, 21));
		}
		return pageLabel;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
