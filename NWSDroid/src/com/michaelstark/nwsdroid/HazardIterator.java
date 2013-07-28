/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.michaelstark.nwsdroid;

import java.util.Iterator;
import java.util.List;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Element;

/**
 *
 * @author mstark
 */
public class HazardIterator implements Iterator<WeatherAlert> {
    private int hazardCount;
    private int position;
    private Element hazardRoot;
    private List<WeatherAlert> hazardList;
    private XPath xPath;
    public HazardIterator(Element hazardRoot, List<WeatherAlert> hazardList)
    {
        xPath = XPathFactory.newInstance().newXPath();
        hazardCount = getValue("count(./hazards)");
        this.position = 0;
        this.hazardRoot = hazardRoot;
        this.hazardList = hazardList;
    }
    
    private <T> T getValue(String path)
    {
        try
        {
            return (T)xPath.evaluate(path, hazardRoot);
        }
        catch(Exception e)
        {
            return null;
        }
    }
    
    public boolean hasNext() {
        return position < hazardCount;
    }

    public WeatherAlert next() {
        if(hasNext())
        {
            WeatherAlert entry = new WeatherAlert();
            entry.setMoreInformationUrl(getValue(String.format("./hazards[%d]/hazard-conditions/hazard/hazardTextURL/text()", position)).toString());
            entry.setHeadline(getValue(String.format("./hazards[%d]/hazard-conditions/hazard/@headline", position)).toString());
            if(position >= hazardList.size())
            {
                hazardList.add(entry);
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
