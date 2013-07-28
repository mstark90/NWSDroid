package com.michaelstark.nwsdroid;

import android.database.Cursor;
import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Element;

import android.util.Log;


public final class ForecastEntry extends XmlWeatherObject
{
    private String summary;
    private String overview;
    private String precipChance;
    private String forecastIconUrl;
    private String temperature;
    private String periodName;
    private Element timeRoot;
    private int index;
    private boolean isNight;
    private int offset;
    ForecastEntry(int index, Element forecastElement, Element timeRoot, int offset)
    {
        super(forecastElement);
        this.timeRoot = timeRoot;
        this.index = index;
        this.offset = offset;
        this.isNight = getPeriodName().toLowerCase().contains("night");
    }
    ForecastEntry(Cursor cursor)
    {
        super(null);
        this.periodName = cursor.getString(3);
        this.summary = cursor.getString(4);
        this.precipChance = cursor.getString(5);
        this.overview = cursor.getString(6);
        this.forecastIconUrl = cursor.getString(7);
    }
    public boolean isNightEntry()
    {
        return isNight;
    }
    public String getPeriodName()
    {
        if(periodName == null)
        {
            try
            {
                periodName = (String)(getXPath().evaluate("./start-valid-time["+ (index + offset) +"]/@period-name", timeRoot, XPathConstants.STRING));
            }
            catch(Exception e)
            {
                Log.d("XmlWeatherObject", "There was an error in parsing the element: ", e);
            }
        }
        return periodName;
    }
    public String getSummary()
    {
        if(summary == null)
        {
            summary = getValue("./parameters/weather/weather-conditions["+ (index + offset) +"]/@weather-summary");
        }
        return summary;
    }
    public String getOverview()
    {
        if(overview == null)
        {
            overview = getValue("./parameters/wordedForecast/text["+ (index + offset) +"]");
        }
        return overview;
    }
    public String getPrecipChance()
    {
        if(precipChance == null)
        {
            precipChance = getValue("./parameters/probability-of-precipitation/value["+ (index + offset) +"]/text()");
            if(precipChance.equals(""))
            {
                precipChance = "0";
            }
        }
        return precipChance;
    }
    public String getForecastIconUrl()
    {
        if(forecastIconUrl == null)
        {
            forecastIconUrl = getValue("./parameters/conditions-icon/icon-link["+ (index + offset) +"]");
        }
        return forecastIconUrl;
    }
    public String getTemperature()
    {
        if(temperature == null)
        {
            int elementIndex = ((int)Math.ceil((index + offset) / 2.0));
            if(isNight)
            {
                temperature = getValue("./parameters/temperature[@type=\"minimum\"]/value["+ elementIndex +"]");
            }
            else
            {
                temperature = getValue("./parameters/temperature[@type=\"maximum\"]/value["+ elementIndex +"]");
            }
        }
        return temperature;
    }
}
