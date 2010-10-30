package org.monome.pages.gui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JInternalFrame;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JButton;

import org.monome.pages.configuration.MIDIPageChangeRule;
import org.monome.pages.configuration.MonomeConfiguration;
import java.awt.Dimension;

public class PageChangeConfigurationFrame extends JInternalFrame {

	private static final long serialVersionUID = 1L;
	private JLabel pageLabel = null;
	private JComboBox pageCB = null;
	private JLabel channelLBL = null;
	private JTextField channelTF = null;
	private JLabel noteLBL = null;
	private JTextField noteTF = null;
	private JCheckBox monomeChangeCB = null;
	private JCheckBox midiChangeCB = null;
	private JLabel monomeChangeLBL = null;
	private JLabel midiChangingLBL = null;
	private JPanel jContentPane;
	private JButton saveBtn = null;
	private JButton cancelBtn = null;
	private MonomeConfiguration monome;
	private int[] midiChannels = new int[255];
	private int[] midiNotes = new int[255];
	private int pageIndex = 0;

	/**
	 * This is the default constructor
	 */
	public PageChangeConfigurationFrame(MonomeConfiguration monome) {
		super();
		this.monome = monome;
		getMidiChangeCB().setSelected(monome.useMIDIPageChanging);
		getMonomeChangeCB().setSelected(monome.usePageChangeButton);
		initialize();
		initializeValues();
		populateTextFields();
		this.pack();
	}
	
	private void initializeValues() {
		ArrayList<MIDIPageChangeRule> midiPageChangeRules = monome.midiPageChangeRules;
		for (int i = 0; i < midiPageChangeRules.size(); i++) {
			midiChannels[i] = midiPageChangeRules.get(i).getChannel();
			midiNotes[i] = midiPageChangeRules.get(i).getNote();
		}
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setContentPane(getJContentPane());
		this.setSize(211, 241);
		this.setTitle("Page Change Configuration");
		this.setResizable(true);
	}
	
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getPageLabel(), null);
			jContentPane.add(getPageCB(), null);
			jContentPane.add(getChannelLBL(), null);
			jContentPane.add(getChannelTF(), null);
			jContentPane.add(getNoteLBL(), null);
			jContentPane.add(getNoteTF(), null);
			jContentPane.add(getMonomeChangeCB(), null);
			jContentPane.add(getMidiChangeCB(), null);
			jContentPane.add(getMonomeChangeLBL(), null);
			jContentPane.add(getMidiChangingLBL(), null);
			jContentPane.add(getSaveBtn(), null);
			jContentPane.add(getCancelBtn(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes pageLabel	
	 * 	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getPageLabel() {
		if (pageLabel == null) {
			pageLabel = new JLabel();
			pageLabel.setText("Page Change Configuration");
			pageLabel.setBounds(new Rectangle(5, 5, 191, 21));
		}
		return pageLabel;
	}

	/**
	 * This method initializes pageCB	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getPageCB() {
		if (pageCB == null) {
			pageCB = new JComboBox();
			pageCB.setBounds(new Rectangle(5, 30, 176, 21));
			pageCB.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					storeValues();
					populateTextFields();
				}
			});
			for (int i = 0; i < monome.pages.size(); i++) {
				pageCB.addItem("" + (i+1) + ": " + monome.pages.get(i).getName());
			}
		}
		return pageCB;
	}
	
	private void storeValues() {
		try {
			int value = Integer.parseInt(getChannelTF().getText());
			midiChannels[pageIndex] = value - 1;
			value = Integer.parseInt(getNoteTF().getText());
			midiNotes[pageIndex] = value;
		} catch (NumberFormatException ex) {
		}
	}
	
	private void populateTextFields() {
		String pageName = (String) getPageCB().getSelectedItem();
		String[] pieces = pageName.split(":");
		pageIndex = Integer.parseInt(pieces[0]) - 1;
		getChannelTF().setText(""+(midiChannels[pageIndex] + 1));
		getNoteTF().setText(""+midiNotes[pageIndex]);
	}

	/**
	 * This method initializes channelLBL	
	 * 	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getChannelLBL() {
		if (channelLBL == null) {
			channelLBL = new JLabel();
			channelLBL.setText("MIDI Channel");
			channelLBL.setBounds(new Rectangle(15, 55, 101, 21));
		}
		return channelLBL;
	}

	/**
	 * This method initializes channelTF	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getChannelTF() {
		if (channelTF == null) {
			channelTF = new JTextField();
			channelTF.setBounds(new Rectangle(120, 55, 51, 21));
		}
		return channelTF;
	}

	/**
	 * This method initializes noteLBL	
	 * 	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getNoteLBL() {
		if (noteLBL == null) {
			noteLBL = new JLabel();
			noteLBL.setText("MIDI Note #");
			noteLBL.setBounds(new Rectangle(15, 80, 101, 21));
		}
		return noteLBL;
	}

	/**
	 * This method initializes noteTF	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getNoteTF() {
		if (noteTF == null) {
			noteTF = new JTextField();
			noteTF.setBounds(new Rectangle(120, 80, 51, 21));
		}
		return noteTF;
	}

	/**
	 * This method initializes monomeChangeCB	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getMonomeChangeCB() {
		if (monomeChangeCB == null) {
			monomeChangeCB = new JCheckBox();
			monomeChangeCB.setBounds(new Rectangle(15, 105, 21, 21));
		}
		return monomeChangeCB;
	}

	/**
	 * This method initializes midiChangeCB	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getMidiChangeCB() {
		if (midiChangeCB == null) {
			midiChangeCB = new JCheckBox();
			midiChangeCB.setBounds(new Rectangle(15, 130, 21, 21));
		}
		return midiChangeCB;
	}

	/**
	 * This method initializes monomeChangeLBL	
	 * 	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getMonomeChangeLBL() {
		if (monomeChangeLBL == null) {
			monomeChangeLBL = new JLabel();
			monomeChangeLBL.setText("Page Change Button");
			monomeChangeLBL.setBounds(new Rectangle(40, 105, 141, 21));
		}
		return monomeChangeLBL;
	}

	/**
	 * This method initializes midiChangingLBL	
	 * 	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getMidiChangingLBL() {
		if (midiChangingLBL == null) {
			midiChangingLBL = new JLabel();
			midiChangingLBL.setText("MIDI Page Changing");
			midiChangingLBL.setBounds(new Rectangle(40, 130, 141, 21));
		}
		return midiChangingLBL;
	}

	/**
	 * This method initializes saveBtn	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSaveBtn() {
		if (saveBtn == null) {
			saveBtn = new JButton();
			saveBtn.setBounds(new Rectangle(20, 160, 76, 21));
			saveBtn.setText("Save");
			saveBtn.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					storeValues();
					monome.useMIDIPageChanging = getMidiChangeCB().isSelected();
					monome.usePageChangeButton = getMonomeChangeCB().isSelected();
					ArrayList<MIDIPageChangeRule> midiPageChangeRules = new ArrayList<MIDIPageChangeRule>();
					for (int i = 0; i < monome.pages.size(); i++) {
						MIDIPageChangeRule mpcr = new MIDIPageChangeRule(midiNotes[i], midiChannels[i], i);
						midiPageChangeRules.add(mpcr);
					}
					monome.midiPageChangeRules = midiPageChangeRules;
					cancel();
				}
			});
		}
		return saveBtn;
	}

	/**
	 * This method initializes cancelBtn	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCancelBtn() {
		if (cancelBtn == null) {
			cancelBtn = new JButton();
			cancelBtn.setBounds(new Rectangle(105, 160, 76, 21));
			cancelBtn.setText("Cancel");
			cancelBtn.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					cancel();
				}
			});
		}
		return cancelBtn;
	}
	
	private void cancel() {
		this.dispose();
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
