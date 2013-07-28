package com.michaelstark.nwsdroid;

import android.database.Cursor;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class Forecast extends XmlWeatherObject implements Iterable<ForecastEntry> {
    private List<ForecastEntry> dailyForecasts;
    private boolean isIterable;
    private int dayCount;
    private Element timeRoot;
    Forecast(Element forecastElement, boolean isIterable) throws SAXException, XPathExpressionException
    {
        super(forecastElement);
        dailyForecasts = new LinkedList<ForecastEntry>();
        String expression = "./time-layout[count(./start-valid-time)>9]";
        timeRoot = (Element)getValueAsNode(expression);
        expression = "count(./start-valid-time)";
        dayCount = (int)((double)((Double)this.getXPath().evaluate(expression, timeRoot, XPathConstants.NUMBER)));
        this.isIterable = isIterable;
        if(!isIterable)
        {
            for(int i = 0; i < dayCount; i++)
            {
                ForecastEntry dayForecast = new ForecastEntry(i, forecastElement, timeRoot, 1);
                dailyForecasts.add(dayForecast);
            }
        }
    }
    public Forecast(Cursor cursor)
    {
        super(null);
        dailyForecasts = new LinkedList<ForecastEntry>();
        dailyForecasts.add(new ForecastEntry(cursor));
        while(cursor.moveToNext())
        {
            dailyForecasts.add(new ForecastEntry(cursor));
        }
    }
    public List<ForecastEntry> getDailyForecasts()
    {
        return dailyForecasts;
    }

    public Iterator<ForecastEntry> iterator() {
        if(!isIterable)
        {
            return dailyForecasts.iterator();
        }
        else
        {
            return new ForecastIterator(dayCount, this.getRootElement(), timeRoot, dailyForecasts);
        }
    }
}
