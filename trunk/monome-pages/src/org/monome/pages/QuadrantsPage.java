package org.monome.pages;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.sound.midi.MidiMessage;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.w3c.dom.Element;

public class QuadrantsPage implements Page, ActionListener {
	
	/**
	 * The MonomeConfiguration that this page belongs to
	 */
	MonomeConfiguration monome;

	/**
	 * The index of this page (the page number) 
	 */
	int index;

	/**
	 * The GUI for this page
	 */
	private JPanel panel;
	private JLabel pageNameLBL;
	private ButtonGroup quadrantBG;
	private ArrayList<JRadioButton> quadrantRB;
	private int selectedQuadConf = 0;
	
	private ArrayList<QuadrantConfiguration> quadrantConfigurations;
	
	public QuadrantsPage(MonomeConfiguration monome, int index) {
		this.monome = monome;
		this.index = index;
		this.quadrantConfigurations = new ArrayList<QuadrantConfiguration>();
		this.createQuadrantConfigurations();
	}
	
	private void createQuadrantConfigurations() {		
		// 128 possible configurations
		if (this.monome.sizeX == 16 && this.monome.sizeY == 8) {
			QuadrantConfiguration quadConf = new QuadrantConfiguration();
			quadConf.addQuad(0, 8, 0, 8);
			quadConf.addQuad(8, 16, 0, 8);
			quadConf.setPicture("<html>[#][#]</html>");
			quadrantConfigurations.add(quadConf);			
		}
		
		// 256 possible configurations
		if (this.monome.sizeX == 16 && this.monome.sizeY == 16) {
			QuadrantConfiguration quadConf = new QuadrantConfiguration();
			quadConf.addQuad(0, 16, 0, 8);
			quadConf.addQuad(0, 16, 8, 16);
			quadConf.setPicture("<html>[###]<br/>[###]</html>");
			quadrantConfigurations.add(quadConf);
			
			quadConf = new QuadrantConfiguration();
			quadConf.addQuad(0, 16, 0, 8);
			quadConf.addQuad(0, 8, 8, 16);
			quadConf.addQuad(8, 16, 8, 16);
			quadConf.setPicture("<html>[###]<br/>[#][#]</html>");
			quadrantConfigurations.add(quadConf);
			
			quadConf = new QuadrantConfiguration();
			quadConf.addQuad(0, 8, 0, 8);
			quadConf.addQuad(8, 16, 0, 8);
			quadConf.addQuad(0, 16, 8, 16);
			quadConf.setPicture("<html>[#][#]<br/>[###]</html>");
			quadrantConfigurations.add(quadConf);
			
			quadConf = new QuadrantConfiguration();
			quadConf.addQuad(0, 8, 0, 8);
			quadConf.addQuad(8, 16, 0, 8);
			quadConf.addQuad(0, 8, 8, 16);
			quadConf.addQuad(8, 16, 8, 16);
			quadConf.setPicture("<html>[#][#]<br/>[#][#]</html>");
			quadrantConfigurations.add(quadConf);
		}
	}

	public void actionPerformed(ActionEvent e) {
		System.out.println(e.getActionCommand());
		System.out.println(e.getID());
		if (e.getActionCommand().equals("New Page")) {
			String options[] = PagesRepository.getPageNames();
			
			//don't know if this is the best way to do this...but I was getting tired of the long messy classnames :)
			for (int i=0; i<options.length; i++) {
				options[i] = options[i].substring(17);					
			}
			String name = (String)JOptionPane.showInputDialog(
					this.panel,
					"Select a new page type",
					"New Page",
					JOptionPane.PLAIN_MESSAGE,
					null,
					options,
					"");
			if (name == null) {
				return;
			}
			name = "org.monome.pages." + name; 
		} else {
			for (int i = 0; i < this.quadrantConfigurations.size(); i++) {
				QuadrantConfiguration quadConf = this.quadrantConfigurations.get(i);
				if (quadConf.getPicture().equals(e.getActionCommand())) {
					this.selectedQuadConf = i;
					this.panel = null;
					this.panel = this.getPanel();
					this.monome.switchPage(this.monome.pages.get(this.index), this.index, true);
				}
			}
		}

	}

	public void addMidiOutDevice(String deviceName) {
		// TODO Auto-generated method stub

	}

	public void clearPanel() {
		// TODO Auto-generated method stub

	}

	public void configure(Element pageElement) {
		// TODO Auto-generated method stub

	}

	public void destroyPage() {
		// TODO Auto-generated method stub

	}

	public ADCOptions getAdcOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean getCacheDisabled() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getName() {
		return "Quadrants Page";
	}

	public JPanel getPanel() {
		JPanel panel = new JPanel();
			
		panel.setLayout(null);
		panel.setPreferredSize(new java.awt.Dimension(700, 400));
		
		pageNameLBL = new JLabel("Page " + (this.index + 1) + ": Quadrants Page");
		pageNameLBL.setBounds(0, 0, 250, 14);
		panel.add(pageNameLBL);
				
		JPanel subPanel = new JPanel();
		subPanel.setLayout(new GridLayout(1, 1));
		this.quadrantBG = new ButtonGroup();
		this.quadrantRB = new ArrayList<JRadioButton>();
		
		for (int i = 0; i < this.quadrantConfigurations.size(); i++) {
			QuadrantConfiguration quadConf = this.quadrantConfigurations.get(i);
			JRadioButton rb = new JRadioButton();
			rb.setText(quadConf.getPicture());
			rb.addActionListener(this);
			
			this.quadrantBG.add(rb);
			this.quadrantRB.add(rb);
			subPanel.add(rb);
		}
		subPanel.setBounds(150, 0, 300, 40);
		panel.add(subPanel);
		
		subPanel = new JPanel();
		subPanel.setLayout(new GridLayout(2, 2));
		subPanel.setBounds(0, 40, 300, 300);
		
		QuadrantConfiguration quadConf = this.quadrantConfigurations.get(this.selectedQuadConf);
		for (int i = 0; i < quadConf.getNumQuads(); i++) {
			JPanel quadPanel = new JPanel();
			quadPanel.setLayout(null);
			quadPanel.setPreferredSize(new java.awt.Dimension(200, 200));
			int[] quad = quadConf.getQuad(i);
			quadPanel.setBackground(new Color((int) (Math.random() * 8000)));
			quadPanel.setBounds(quad[0] * 10, (quad[2] * 10) + 40, quad[1] * 10, (quad[3] * 10) + 40);
			
			JButton newPageButton = new JButton();
			newPageButton.setText("New Page");
			newPageButton.setBounds(0, 0, 100, 20);
			newPageButton.addActionListener(this);
			quadPanel.add(newPageButton);
			subPanel.add(quadPanel);
		}
		panel.add(subPanel);

		
		this.panel = panel;
		return panel;
				
	}

	public void handleADC(int adcNum, float value) {
		// TODO Auto-generated method stub

	}

	public void handleADC(float x, float y) {
		// TODO Auto-generated method stub

	}

	public void handlePress(int x, int y, int value) {
		// TODO Auto-generated method stub

	}

	public void handleReset() {
		// TODO Auto-generated method stub

	}

	public void handleTick() {
		// TODO Auto-generated method stub

	}

	public boolean isTiltPage() {
		// TODO Auto-generated method stub
		return false;
	}

	public void redrawMonome() {
		// TODO Auto-generated method stub

	}

	public void send(MidiMessage message, long timeStamp) {
		// TODO Auto-generated method stub

	}

	public void setAdcOptions(ADCOptions options) {
		// TODO Auto-generated method stub

	}

	public void setIndex(int index) {
		// TODO Auto-generated method stub

	}

	public void setName(String name) {
		// TODO Auto-generated method stub

	}

	public String toXml() {
		String xml = "";
		xml += "      <name>Quadrants Page</name>\n";
		return xml;
	}

}
