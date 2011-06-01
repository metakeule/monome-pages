package org.monome.pages.api;

import org.monome.pages.configuration.MonomeConfiguration;

public interface GroovyPageInterface {
    public void init();
    public void stop();
    public void press(int x, int y, int val);
    public void redraw();
    public void note(int num, int velo, int chan, int on);
    public void cc(int num, int val, int chan);
    public void clock();
    public void clockReset();
    public void setMonome(MonomeConfiguration monome);
    public void setPageIndex(int pageIndex);
    public MonomeConfiguration monome();
    public int sizeX();
    public int sizeY();
    public boolean redrawOnAbletonEvent();
}
