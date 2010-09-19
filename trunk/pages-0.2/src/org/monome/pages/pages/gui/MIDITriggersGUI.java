package org.monome.pages.pages.gui;

import javax.swing.ButtonGroup;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Rectangle;
import javax.swing.JComboBox;

import org.monome.pages.pages.MIDITriggersPage;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;
import java.awt.Dimension;

public class MIDITriggersGUI extends JPanel {

	private static final long serialVersionUID = 1L;
	MIDITriggersPage page;
	private JLabel pageLabel = null;
	private JComboBox rowColCB = null;
	private JLabel rowColLBL = null;
	private String[] colChoices = {"Col 1", "Col 2", "Col 3", "Col 4", "Col 5", "Col 6",
			"Col 7", "Col 8", "Col 9", "Col 10", "Col 11", "Col 12", "Col 13", "Col 14",
			"Col 15", "Col 16"};
	private String[] rowChoices = {"Row 1", "Row 2", "Row 3", "Row 4", "Row 5", "Row 6",
			"Row 7", "Row 8", "Row 9", "Row 10", "Row 11", "Row 12", "Row 13", "Row 14",
			"Row 15", "Row 16"};
	public JRadioButton rowRB = null;
	private JLabel rowLBL = null;
	public JRadioButton colRB = null;
	private JLabel colLBL = null;
	public JCheckBox togglesCB = null;
	private JLabel togglesLBL = null;
	/**
	 * This is the default constructor
	 */
	public MIDITriggersGUI(MIDITriggersPage page) {
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
		pageLabel = new JLabel();
		pageLabel.setBounds(new Rectangle(5, 5, 186, 21));
		this.setSize(185, 141);
		this.setLayout(null);
		this.add(pageLabel, null);
		setName("MIDI Triggers Page");
		this.add(getRowColCB(), null);
		this.add(getRowColLBL(), null);
		this.add(getRowRB(), null);
		this.add(getRowLBL(), null);
		this.add(getColRB(), null);
		this.add(getColLBL(), null);
		this.add(getTogglesCB(), null);
		this.add(getTogglesLBL(), null);
		ButtonGroup colRowBG = new ButtonGroup();
		colRowBG.add(getRowRB());
		colRowBG.add(getColRB());
		rowRB.doClick();
	}
	
	public void setName(String name) {
		pageLabel.setText((page.getIndex() + 1) + ": " + name);
	}
		
	private void colMode() {
		rowColCB.removeAllItems();
		for (int i = 0; i < colChoices.length; i++) {
			rowColCB.addItem(colChoices[i]);
		}
		rowColLBL.setText("Col");
	}
	
	private void rowMode() {
		rowColCB.removeAllItems();
		for (int i = 0; i < rowChoices.length; i++) {
			rowColCB.addItem(rowChoices[i]);
		}
		rowColLBL.setText("Row");
	}

	/**
	 * This method initializes rowColCB	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getRowColCB() {
		if (rowColCB == null) {
			rowColCB = new JComboBox();
			rowColCB.setBounds(new Rectangle(45, 30, 96, 21));
			rowColCB.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String label = (String) rowColCB.getSelectedItem();
					if (label == null) {
						return;
					}
					String[] pieces;
					if (rowRB.isSelected()) {
						pieces = label.split("Row ");
					} else {
						pieces = label.split("Col ");
					}
					int index = Integer.parseInt(pieces[1]) - 1;
					togglesCB.setSelected(page.toggles[index]);
				}
			});
		}
		return rowColCB;
	}

	/**
	 * This method initializes rowColLBL	
	 * 	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getRowColLBL() {
		if (rowColLBL == null) {
			rowColLBL = new JLabel();
			rowColLBL.setText("Row");
			rowColLBL.setBounds(new Rectangle(20, 30, 26, 21));
		}
		return rowColLBL;
	}

	/**
	 * This method initializes rowRB	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getRowRB() {
		if (rowRB == null) {
			rowRB = new JRadioButton();
			rowRB.setBounds(new Rectangle(45, 60, 21, 21));
			rowRB.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					rowMode();
				}
			});
		}
		return rowRB;
	}

	/**
	 * This method initializes rowLBL	
	 * 	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getRowLBL() {
		if (rowLBL == null) {
			rowLBL = new JLabel();
			rowLBL.setText("Rows");
			rowLBL.setBounds(new Rectangle(70, 60, 46, 21));
		}
		return rowLBL;
	}

	/**
	 * This method initializes colRB	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getColRB() {
		if (colRB == null) {
			colRB = new JRadioButton();
			colRB.setBounds(new Rectangle(45, 80, 21, 21));
			colRB.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					colMode();
				}
			});
		}
		return colRB;
	}

	/**
	 * This method initializes colLBL	
	 * 	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getColLBL() {
		if (colLBL == null) {
			colLBL = new JLabel();
			colLBL.setText("Cols");
			colLBL.setBounds(new Rectangle(70, 80, 46, 21));
		}
		return colLBL;
	}

	/**
	 * This method initializes togglesCB	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getTogglesCB() {
		if (togglesCB == null) {
			togglesCB = new JCheckBox();
			togglesCB.setBounds(new Rectangle(45, 100, 21, 21));
			togglesCB.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String label = (String) rowColCB.getSelectedItem();
					if (label == null) {
						return;
					}
					String[] pieces;
					if (rowRB.isSelected()) {
						pieces = label.split("Row ");
					} else {
						pieces = label.split("Col ");
					}
					int index = Integer.parseInt(pieces[1]) - 1;
					page.toggles[index] = togglesCB.isSelected();
				}
			});
		}
		return togglesCB;
	}

	/**
	 * This method initializes togglesLBL	
	 * 	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getTogglesLBL() {
		if (togglesLBL == null) {
			togglesLBL = new JLabel();
			togglesLBL.setText("Toggles");
			togglesLBL.setBounds(new Rectangle(70, 100, 76, 21));
		}
		return togglesLBL;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
