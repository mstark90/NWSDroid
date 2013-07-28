/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.michaelstark.nwsdroid;

import java.util.Iterator;
import java.util.List;
import org.w3c.dom.Element;

/**
 *
 * @author mstark
 */
public class ForecastIterator implements Iterator<ForecastEntry> {
    private int forecastCount;
    private int position;
    private Element forecastRoot, timeRoot;
    private List<ForecastEntry> forecastList;
    public ForecastIterator(int forecastCount, Element forecastRoot, Element timeRoot,
            List<ForecastEntry> forecastList)
    {
        this.forecastCount = forecastCount;
        this.position = 0;
        this.forecastRoot = forecastRoot;
        this.timeRoot = timeRoot;
        this.forecastList = forecastList;
    }
    public boolean hasNext() {
        return position < forecastCount;
    }

    public ForecastEntry next() {
        if(hasNext())
        {
            ForecastEntry entry = new ForecastEntry(position, forecastRoot, timeRoot, 1);
            if(position >= forecastList.size())
            {
                forecastList.add(entry);
            }
            position++;
            return entry;
        }
        else
        {
            return null;
        }
    }

    public void remove() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
