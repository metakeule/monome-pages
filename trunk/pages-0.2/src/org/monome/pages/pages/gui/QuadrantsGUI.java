package org.monome.pages.pages.gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.monome.pages.configuration.PagesRepository;
import org.monome.pages.configuration.QuadrantConfiguration;
import org.monome.pages.pages.QuadrantsPage;

public class QuadrantsGUI extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	QuadrantsPage page;
	private JLabel pageNameLBL;
	private ButtonGroup quadrantBG;
	private ArrayList<JRadioButton> quadrantRB;
	private int selectedQuadConf = 0;

	public QuadrantsGUI(QuadrantsPage page, int selectedQuadConf) {
		super();
		this.selectedQuadConf = selectedQuadConf;
		this.page = page;
		initialize();
	}
	
	private void initialize() {
		
		setLayout(null);		
		pageNameLBL = new JLabel("Page " + (page.getIndex() + 1) + ": Quadrants Page");
		pageNameLBL.setBounds(0, 0, 250, 14);
		add(pageNameLBL);
		setSize(700,400);
		JPanel subPanel = new JPanel();
		subPanel.setLayout(new GridLayout(1, 1));
		quadrantBG = new ButtonGroup();
		quadrantRB = new ArrayList<JRadioButton>();
		
		for (int i = 0; i < page.quadrantConfigurations.size(); i++) {
			QuadrantConfiguration quadConf = page.quadrantConfigurations.get(i);
			JRadioButton rb = new JRadioButton();
			rb.setText(quadConf.getPicture());
			rb.addActionListener(this);
			if (i == this.selectedQuadConf) {
				rb.setSelected(true);
			}
			
			this.quadrantBG.add(rb);
			this.quadrantRB.add(rb);
			subPanel.add(rb);
		}
		subPanel.setBounds(150, 0, 300, 40);
		add(subPanel);
		
		subPanel = new JPanel();
		subPanel.setLayout(new GridLayout(2, 2));
		subPanel.setBounds(0, 40, 300, 300);
		
		QuadrantConfiguration quadConf = page.quadrantConfigurations.get(this.selectedQuadConf);
		for (int i = 0; i < quadConf.getNumQuads(); i++) {
			JPanel quadPanel = new JPanel();
			quadPanel.setLayout(null);
			quadPanel.setSize(quadConf.getWidth(i) * 15, (quadConf.getHeight(i) * 15) + 40);
			//quadPanel.setPreferredSize(new java.awt.Dimension(200, 200));
			int[] quad = quadConf.getQuad(i);
			quadPanel.setBackground(new Color((int) (Math.random() * 8000)));
			quadPanel.setBounds(quad[0] * 15, (quad[2] * 15) + 40, quad[1] * 15, (quad[3] * 15) + 40);
			//quadConf.getMonomeConfiguration(i)
			
			JButton newPageButton = new JButton();
			newPageButton.setText("New Page " + i);
			newPageButton.setBounds(0, 0, 100, 20);
			newPageButton.addActionListener(this);
			quadPanel.add(newPageButton);
			subPanel.add(quadPanel);
		}
		add(subPanel);
	}
	
	public void actionPerformed(ActionEvent e) {
		System.out.println(e.getActionCommand());
		System.out.println(e.getID());
		if (e.getActionCommand().equals("New Page")) {
			String options[] = PagesRepository.getPageNames();
			
			//don't know if this is the best way to do this...but I was getting tired of the long messy classnames :)
			for (int i=0; i<options.length; i++) {
				options[i] = options[i].substring(23);
			}
			String name = (String)JOptionPane.showInputDialog(
					this,
					"Select a new page type",
					"New Page",
					JOptionPane.PLAIN_MESSAGE,
					null,
					options,
					"");
			if (name == null) {
				return;
			}
			name = "org.monome.pages.pages." + name;
			page.quadrantConfigurations.get(selectedQuadConf).getMonomeConfiguration(0).addPage(name);
		} else {
			for (int i = 0; i < page.quadrantConfigurations.size(); i++) {
				QuadrantConfiguration quadConf = page.quadrantConfigurations.get(i);
				if (quadConf.getPicture().equals(e.getActionCommand())) {
					selectedQuadConf = i;
					page.recreateGUI(selectedQuadConf);
				}
			}
		}

	}

}
