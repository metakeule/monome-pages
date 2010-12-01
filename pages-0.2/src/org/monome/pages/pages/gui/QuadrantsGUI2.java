package org.monome.pages.pages.gui;

import java.awt.GridBagLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import javax.swing.JRadioButton;
import java.awt.Dimension;
import javax.swing.JButton;

public class QuadrantsGUI2 extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel pageLabel = null;
	private JRadioButton quad1RB = null;
	private JLabel quad1LBL = null;
	private JRadioButton quad2RB = null;
	private JLabel quad2LBL = null;
	private JRadioButton quad3RB = null;
	private JLabel quad3LBL = null;
	private JRadioButton quad4RB = null;
	private JLabel quad4LBL = null;
	private JButton newPage1Btn = null;
	private JButton openPage1Btn = null;
	private JButton newPage2Btn = null;
	private JButton openPage2Btn = null;
	private JLabel page1LBL = null;
	private JLabel page2LBL = null;
	private JLabel page3LBL = null;
	private JButton newPage3BTN = null;
	private JButton openPage3BTN = null;
	private JLabel page4LBL = null;
	private JButton newPage4BTN = null;
	private JButton openPage4BTN = null;

	/**
	 * This is the default constructor
	 */
	public QuadrantsGUI2() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		page4LBL = new JLabel();
		page4LBL.setBounds(new Rectangle(255, 85, 71, 21));
		page4LBL.setText("Page 4");
		page3LBL = new JLabel();
		page3LBL.setBounds(new Rectangle(175, 85, 71, 21));
		page3LBL.setText("Page 3");
		page2LBL = new JLabel();
		page2LBL.setBounds(new Rectangle(95, 85, 71, 21));
		page2LBL.setText("Page 2");
		page1LBL = new JLabel();
		page1LBL.setBounds(new Rectangle(15, 85, 71, 21));
		page1LBL.setText("Page 1");
		quad4LBL = new JLabel();
		quad4LBL.setBounds(new Rectangle(280, 30, 36, 46));
		quad4LBL.setText("<html>[#][#]<br/>[#][#]</html>");
		quad3LBL = new JLabel();
		quad3LBL.setBounds(new Rectangle(200, 30, 36, 46));
		quad3LBL.setText("<html>[#][#]<br/>[###]</html>");
		quad2LBL = new JLabel();
		quad2LBL.setBounds(new Rectangle(120, 30, 36, 46));
		quad2LBL.setText("<html>[###]<br/>[#][#]</html>");
		quad1LBL = new JLabel();
		quad1LBL.setBounds(new Rectangle(40, 30, 36, 46));
		quad1LBL.setText("<html>[###]<br/>[###]</html>");
		this.setSize(343, 174);
		this.setLayout(null);
		this.add(getPageLabel(), null);
		this.add(getQuad1RB(), null);
		this.add(quad1LBL, null);
		this.add(getQuad2RB(), null);
		this.add(quad2LBL, null);
		this.add(getQuad3RB(), null);
		this.add(quad3LBL, null);
		this.add(getQuad4RB(), null);
		this.add(quad4LBL, null);
		this.add(getNewPage1Btn(), null);
		this.add(getOpenPage1Btn(), null);
		this.add(getNewPage2Btn(), null);
		this.add(getOpenPage2Btn(), null);
		this.add(page1LBL, null);
		this.add(page2LBL, null);
		this.add(page3LBL, null);
		this.add(getNewPage3BTN(), null);
		this.add(getOpenPage3BTN(), null);
		this.add(page4LBL, null);
		this.add(getNewPage4BTN(), null);
		this.add(getOpenPage4BTN(), null);
	}

	/**
	 * This method initializes pageLabel	
	 * 	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getPageLabel() {
		if (pageLabel == null) {
			pageLabel = new JLabel();
			pageLabel.setText("Quadrants Page");
			pageLabel.setBounds(new Rectangle(5, 5, 151, 16));
		}
		return pageLabel;
	}

	/**
	 * This method initializes quad1RB	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getQuad1RB() {
		if (quad1RB == null) {
			quad1RB = new JRadioButton();
			quad1RB.setBounds(new Rectangle(15, 40, 21, 21));
		}
		return quad1RB;
	}

	/**
	 * This method initializes quad2RB	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getQuad2RB() {
		if (quad2RB == null) {
			quad2RB = new JRadioButton();
			quad2RB.setBounds(new Rectangle(95, 40, 21, 21));
		}
		return quad2RB;
	}

	/**
	 * This method initializes quad3RB	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getQuad3RB() {
		if (quad3RB == null) {
			quad3RB = new JRadioButton();
			quad3RB.setBounds(new Rectangle(175, 40, 21, 21));
		}
		return quad3RB;
	}

	/**
	 * This method initializes quad4RB	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getQuad4RB() {
		if (quad4RB == null) {
			quad4RB = new JRadioButton();
			quad4RB.setBounds(new Rectangle(255, 40, 21, 21));
		}
		return quad4RB;
	}

	/**
	 * This method initializes newPage1Btn	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getNewPage1Btn() {
		if (newPage1Btn == null) {
			newPage1Btn = new JButton();
			newPage1Btn.setBounds(new Rectangle(15, 110, 71, 21));
			newPage1Btn.setText("New");
		}
		return newPage1Btn;
	}

	/**
	 * This method initializes openPage1Btn	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getOpenPage1Btn() {
		if (openPage1Btn == null) {
			openPage1Btn = new JButton();
			openPage1Btn.setBounds(new Rectangle(15, 135, 71, 21));
			openPage1Btn.setText("Open");
		}
		return openPage1Btn;
	}

	/**
	 * This method initializes newPage2Btn	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getNewPage2Btn() {
		if (newPage2Btn == null) {
			newPage2Btn = new JButton();
			newPage2Btn.setBounds(new Rectangle(95, 110, 71, 21));
			newPage2Btn.setText("New");
		}
		return newPage2Btn;
	}

	/**
	 * This method initializes openPage2Btn	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getOpenPage2Btn() {
		if (openPage2Btn == null) {
			openPage2Btn = new JButton();
			openPage2Btn.setBounds(new Rectangle(95, 135, 71, 21));
			openPage2Btn.setText("Open");
		}
		return openPage2Btn;
	}

	/**
	 * This method initializes newPage3BTN	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getNewPage3BTN() {
		if (newPage3BTN == null) {
			newPage3BTN = new JButton();
			newPage3BTN.setBounds(new Rectangle(175, 110, 71, 21));
			newPage3BTN.setText("New");
		}
		return newPage3BTN;
	}

	/**
	 * This method initializes openPage3BTN	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getOpenPage3BTN() {
		if (openPage3BTN == null) {
			openPage3BTN = new JButton();
			openPage3BTN.setBounds(new Rectangle(175, 135, 71, 21));
			openPage3BTN.setText("Open");
		}
		return openPage3BTN;
	}

	/**
	 * This method initializes newPage4BTN	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getNewPage4BTN() {
		if (newPage4BTN == null) {
			newPage4BTN = new JButton();
			newPage4BTN.setBounds(new Rectangle(255, 110, 71, 21));
			newPage4BTN.setText("New");
		}
		return newPage4BTN;
	}

	/**
	 * This method initializes openPage4BTN	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getOpenPage4BTN() {
		if (openPage4BTN == null) {
			openPage4BTN = new JButton();
			openPage4BTN.setBounds(new Rectangle(255, 135, 71, 21));
			openPage4BTN.setText("Open");
		}
		return openPage4BTN;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
