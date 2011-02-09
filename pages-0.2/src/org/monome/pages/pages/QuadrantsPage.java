package org.monome.pages.pages;

import java.util.ArrayList;

import javax.sound.midi.MidiMessage;
import javax.swing.JPanel;

import org.monome.pages.configuration.FakeMonomeConfiguration;
import org.monome.pages.configuration.MonomeConfiguration;
import org.monome.pages.configuration.MonomeConfigurationFactory;
import org.monome.pages.configuration.QuadrantConfiguration;
import org.monome.pages.pages.gui.QuadrantsGUI256;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class QuadrantsPage implements Page {
	
	/**
	 * The MonomeConfiguration that this page belongs to
	 */
	public MonomeConfiguration monome;

	/**
	 * The index of this page (the page number) 
	 */
	int index;
	
	QuadrantsGUI256 gui;
	
	public ArrayList<QuadrantConfiguration> quadrantConfigurations;
	
	public QuadrantsPage(MonomeConfiguration monome, int index) {
		this.monome = monome;
		this.index = index;
		this.quadrantConfigurations = new ArrayList<QuadrantConfiguration>();
		this.createQuadrantConfigurations();
		gui = new QuadrantsGUI256(this, 0);
	}
	
	private void createQuadrantConfigurations() {
		// 40h/64 etc possible configurations
		if (this.monome.sizeX == 8 && this.monome.sizeY == 8) {
			QuadrantConfiguration quadConf = new QuadrantConfiguration(index, monome);
			quadConf.addQuad(0, 8, 0, 8);
			quadConf.setPicture("<html>[#]</html>");
			quadrantConfigurations.add(quadConf);			
		}

		// 128 possible configurations
		if (this.monome.sizeX == 16 && this.monome.sizeY == 8) {
			QuadrantConfiguration quadConf = new QuadrantConfiguration(index, monome);
			quadConf.addQuad(0, 8, 0, 8);
			quadConf.addQuad(8, 16, 0, 8);
			quadConf.setPicture("<html>[#][#]</html>");
			quadrantConfigurations.add(quadConf);			
		}
		
		// 256 possible configurations
		if (this.monome.sizeX == 16 && this.monome.sizeY == 16) {
			QuadrantConfiguration quadConf = new QuadrantConfiguration(index, monome);
			quadConf.addQuad(0, 16, 0, 8);
			quadConf.addQuad(0, 16, 8, 16);
			quadConf.setPicture("<html>[###]<br/>[###]</html>");
			quadrantConfigurations.add(quadConf);
			
			quadConf = new QuadrantConfiguration(index, monome);
			quadConf.addQuad(0, 16, 0, 8);
			quadConf.addQuad(0, 8, 8, 16);
			quadConf.addQuad(8, 16, 8, 16);
			quadConf.setPicture("<html>[###]<br/>[#][#]</html>");
			quadrantConfigurations.add(quadConf);
			
			quadConf = new QuadrantConfiguration(index, monome);
			quadConf.addQuad(0, 8, 0, 8);
			quadConf.addQuad(8, 16, 0, 8);
			quadConf.addQuad(0, 16, 8, 16);
			quadConf.setPicture("<html>[#][#]<br/>[###]</html>");
			quadrantConfigurations.add(quadConf);
			
			quadConf = new QuadrantConfiguration(index, monome);
			quadConf.addQuad(0, 8, 0, 8);
			quadConf.addQuad(8, 16, 0, 8);
			quadConf.addQuad(0, 8, 8, 16);
			quadConf.addQuad(8, 16, 8, 16);
			quadConf.setPicture("<html>[#][#]<br/>[#][#]</html>");
			quadrantConfigurations.add(quadConf);
		}
	}

	public void configure(Element pageElement) {
		this.setSelectedQuadConf(this.monome.readConfigValue(pageElement, "selectedQuadConf"));
		NodeList page1NL = pageElement.getElementsByTagName("page1");
		if (page1NL != null) {
			Element page1EL = (Element) page1NL.item(0);
			String className = page1EL.getAttribute("class");
			System.out.println("className is " + className);
		}
	}

	private void setSelectedQuadConf(String readConfigValue) {
		int val = 0;
		try {
			val = Integer.parseInt(readConfigValue);
		} catch (NumberFormatException e) {
			return;
		}
		switch(val) {
			case 0:
				gui.getQuad1RB().doClick();
				break;
			case 1:
				gui.getQuad2RB().doClick();
				break;
			case 2:
				gui.getQuad3RB().doClick();
				break;
			case 3:
				gui.getQuad4RB().doClick();
				break;
		}
	}

	public void destroyPage() {
		for (int j = 0; j < quadrantConfigurations.get(gui.selectedQuadConf).getNumQuads(); j++) {
			FakeMonomeConfiguration monomeConfig = quadrantConfigurations.get(gui.selectedQuadConf).getMonomeConfiguration(j);
			MonomeConfigurationFactory.removeMonomeConfiguration(monomeConfig.index);
		}
	}

	public boolean getCacheDisabled() {
		return false;
	}

	public String getName() {
		return "Quadrants Page";
	}

	public JPanel getPanel() {
		return gui;
	}

	public void handleADC(int adcNum, float value) {
	}

	public void handleADC(float x, float y) {
	}

	public void handlePress(int x, int y, int value) {
		int quadNum = getQuadNum(x, y);
		FakeMonomeConfiguration monomeConfig = quadrantConfigurations.get(gui.selectedQuadConf).getMonomeConfiguration(quadNum);
		monomeConfig.handlePress(x, y, value);
	}
	
	private int getQuadNum(int x, int y) {
		int quadNum = 0;
		for (int i = 0; i < quadrantConfigurations.get(gui.selectedQuadConf).getNumQuads(); i++) {
			int[] quad = quadrantConfigurations.get(gui.selectedQuadConf).getQuad(i);
			if (x >= quad[0] && x < quad[1]) {
				if (y >= quad[2] && y < quad[3]) {
					quadNum = i;
					break;
				}
			}
		}
		return quadNum;
	}

	public void handleReset() {
		for (int j = 0; j < quadrantConfigurations.get(gui.selectedQuadConf).getNumQuads(); j++) {
			FakeMonomeConfiguration monomeConfig = quadrantConfigurations.get(gui.selectedQuadConf).getMonomeConfiguration(j);
			if (monomeConfig.pages != null) {
				for (int k = 0; k < monomeConfig.pages.size(); k++) {
					monomeConfig.pages.get(k).handleReset();
				}
			}
		}
	}

	public void handleTick() {
		for (int i = 0; i < quadrantConfigurations.size(); i++) {
			for (int j = 0; j < quadrantConfigurations.get(i).getNumQuads(); j++) {
				FakeMonomeConfiguration monomeConfig = quadrantConfigurations.get(i).getMonomeConfiguration(j);
				if (monomeConfig.pages != null) {
					for (int k = 0; k < monomeConfig.pages.size(); k++) {
						monomeConfig.pages.get(k).handleTick();
					}
				}
			}
		}
	}

	public boolean isTiltPage() {
		return false;
	}

	public void redrawMonome() {
		boolean didRedraw = false;
		for (int j = 0; j < quadrantConfigurations.get(gui.selectedQuadConf).getNumQuads(); j++) {
			FakeMonomeConfiguration monomeConfig = quadrantConfigurations.get(gui.selectedQuadConf).getMonomeConfiguration(j);
			if (monomeConfig.pages != null) {
				for (int k = 0; k < monomeConfig.pages.size(); k++) {
					monomeConfig.pages.get(k).redrawMonome();
					didRedraw = true;
				}
			}
		}
		if (!didRedraw) {
			monome.clearMonome();
		}
	}

	public void send(MidiMessage message, long timeStamp) {
		for (int i = 0; i < quadrantConfigurations.size(); i++) {
			for (int j = 0; j < quadrantConfigurations.get(i).getNumQuads(); j++) {
				FakeMonomeConfiguration monomeConfig = quadrantConfigurations.get(i).getMonomeConfiguration(j);
				if (monomeConfig.pages != null) {
					for (int k = 0; k < monomeConfig.pages.size(); k++) {
						monomeConfig.pages.get(k).send(message, timeStamp);
					}
				}
			}
		}		
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setName(String name) {
		// TODO: implement name change on GUI
	}

	public String toXml() {
		// TODO: implement save
		String xml = "";
		xml += "      <name>Quadrants Page</name>\n";
		xml += "      <selectedQuadConf>" + gui.selectedQuadConf + "</selectedQuadConf>\n";
		for (int quadConfNum = 0; quadConfNum < this.quadrantConfigurations.size(); quadConfNum++) {
			QuadrantConfiguration quadConf = this.quadrantConfigurations.get(quadConfNum);
			for (int quadNum = 0; quadNum < quadConf.getNumQuads(); quadNum++) {
				FakeMonomeConfiguration monomeConfig = quadConf.getMonomeConfiguration(quadNum);
				if (monomeConfig.pages.isEmpty()) {
					continue;
				}
				Page page = monomeConfig.pages.get(0);
				String pageConfig = page.toXml();
				xml += "      <page" + quadNum + " class=\"" + page.getClass().toString().replace("class ", "") + "\">\n  ";
				// fix indent
				pageConfig = pageConfig.replace("\n", "\n  ");
				xml += pageConfig;
				xml += "    </page" + quadNum + ">\n";
			}
		}
		return xml;
	}

	public int getIndex() {
		return index;
	}

	public void redrawPage(int selectedQuadConf) {
		MonomeConfigurationFactory.getMonomeConfiguration(index).monomeFrame.redrawPagePanel(this);
	}
	
	public boolean redrawOnAbletonEvent() {
		return true;
	}
}
