package com.michaelstark.nwsdroid;

import android.database.Cursor;
import android.util.Log;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by mstark on 7/28/13.
 */
public class NWSWeatherResponse extends XmlWeatherObject {
    private static final long serialVersionUID = -8396332474835708682L;

    private String currentTemperature, highTemperature, lowTemperature;
    private String relativeHumidity, dewpoint;
    private String windSpeed, windDirection;
    private String summary;
    private String visibility;
    private String windGust;
    private List<WeatherAlert> weatherAlerts;
    private List<WeatherCondition> weatherConditions;
    private String iconUrl;
    private Forecast forecast;
    private boolean triedHighTemp = false, triedLowTemp = false, triedGust = false;
    private Date retrievalTime;
    NWSWeatherResponse(Element rootElement)
    {
        super(rootElement);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        String retrievalTimeStr = null;
        try
        {
            retrievalTimeStr = getValue("/dwml/data[@type=\"current observations\"]/time-layout[@time-coordinate=\"local\"]/start-valid-time/text()");
            retrievalTime = dateFormat.parse(retrievalTimeStr);
        }
        catch(Exception e)
        {
            try
            {
                dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                retrievalTime = dateFormat.parse(retrievalTimeStr);
            }
            catch(Exception ex)
            {
                Log.d("NWSWeatherResponse", "Could not load the retrieval time: ", ex);
            }
        }
        this.weatherConditions = new LinkedList<WeatherCondition>();
        this.weatherAlerts = new LinkedList<WeatherAlert>();
    }
    public NWSWeatherResponse(Cursor c, Forecast f)
    {
        super(null);
        currentTemperature = c.getString(6);
        highTemperature = c.getString(7);
        lowTemperature = c.getString(8);
        windSpeed = c.getString(9);
        windDirection = c.getString(10);
        relativeHumidity = c.getString(11);
        dewpoint = c.getString(12);
        summary = c.getString(13);
        windGust = c.getString(16);
        visibility = c.getString(15);
        retrievalTime = new Date(c.getLong(6));
        iconUrl = c.getString(14);
        this.weatherConditions = new LinkedList<WeatherCondition>();
        this.weatherAlerts = new LinkedList<WeatherAlert>();
        this.forecast = f;
    }
    public boolean isOldData()
    {
        if(retrievalTime == null)
        {
            return false;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -60);
        return retrievalTime.before(calendar.getTime());
    }

    public Date getRetrievalTime()
    {
        return retrievalTime;
    }

    public Forecast getForecast()
    {
        if(forecast == null)
        {
            try
            {
                forecast = new Forecast((Element)getValueAsNode("/dwml/data[@type=\"forecast\"]"), false);
            }
            catch (Exception e)
            {
                Log.e("WeatherResponse", "Could not load the forecast: ", e);
            }
        }
        return forecast;
    }

    public Forecast getForecastIterable()
    {
        if(forecast == null)
        {
            try
            {
                forecast = new Forecast((Element)getValueAsNode("/dwml/data[@type=\"forecast\"]"), true);
            }
            catch (Exception e)
            {
                Log.e("WeatherResponse", "Could not load the forecast: ", e);
            }
        }
        return forecast;
    }

    public String getCurrentTemperature()
    {
        if(currentTemperature == null)
        {
            currentTemperature = getValue("/dwml/data[@type=\"current observations\"]/parameters/temperature[@type=\"apparent\"]/value[1]");
        }
        return currentTemperature;
    }

    public String getHighTemperature()
    {
        if(highTemperature == null && triedHighTemp == false)
        {
            highTemperature = getValue("/dwml/data[@type=\"forecast\"]/parameters/temperature[@type=\"maximum\"]/value[1]/text()");
            triedHighTemp = true;
        }
        return highTemperature;
    }

    public String getLowTemperature()
    {
        if(lowTemperature == null && triedLowTemp == false)
        {
            lowTemperature = getValue("/dwml/data[@type=\"forecast\"]/parameters/temperature[@type=\"minimum\"]/value[1]/text()");
            triedLowTemp = true;
        }
        return lowTemperature;
    }

    public String getDewpoint()
    {
        if(dewpoint == null)
        {
            dewpoint = getValue("/dwml/data[@type=\"current observations\"]/parameters/temperature[@type=\"dew point\"]/value[1]");
        }
        return dewpoint;
    }

    public String getRelativeHumidity()
    {
        if(relativeHumidity == null)
        {
            relativeHumidity = getValue("/dwml/data[@type=\"current observations\"]/parameters/humidity[@type=\"relative\"]/value[1]");
        }
        return relativeHumidity;
    }

    public String getWindSpeed()
    {
        if(windSpeed == null)
        {
            windSpeed = getValue("/dwml/data[@type=\"current observations\"]/parameters/wind-speed[@type=\"sustained\"]/value[1]");
        }
        return windSpeed;
    }

    public String getWindGust()
    {
        if(windGust == null && triedGust == false)
        {
            windGust = getValue("/dwml/data[@type=\"current observations\"]/parameters/wind-speed[@type=\"gust\"]/value[1]");
        }
        return windGust;
    }

    public String getWindDirection()
    {
        if(windDirection == null)
        {
            windDirection = getValue("/dwml/data[@type=\"current observations\"]/parameters/direction[@type=\"wind\"]/value[1]");
        }
        return windDirection;
    }

    public String getSummary()
    {
        if(summary == null)
        {
            summary = getValue("/dwml/data[@type=\"current observations\"]/parameters/weather/weather-conditions[1]/@weather-summary");
        }
        return this.summary;
    }

    public String getVisibility()
    {
        if(visibility == null)
        {
            visibility = getValue("/dwml/data[@type=\"current observations\"]/parameters/weather/weather-conditions/value/visibility");
        }
        return this.visibility;
    }

    public List<WeatherAlert> getWeatherAlerts()
    {
        if(weatherAlerts.isEmpty())
        {
            String expression = "/dwml/data[@type=\"forecast\"]/parameters/hazards/hazard-conditions";
            NodeList nodes = getValueAsNodeList(expression);
            for(int i = 0; i < nodes.getLength(); i++)
            {
                Element hazardElement = (Element)nodes.item(i);
                if(hazardElement.getFirstChild() != null)
                {
                    hazardElement = (Element)hazardElement.getElementsByTagName("hazard").item(0);
                    String hazardCode = hazardElement.getAttribute("hazardCode");
                    boolean canAdd = true;
                    for(WeatherAlert weatherAlert : weatherAlerts)
                    {
                        if(weatherAlert.getHazardCode().equals(hazardCode))
                        {
                            canAdd = false;
                            break;
                        }
                    }
                    if(canAdd)
                    {
                        WeatherAlert weatherAlert = new WeatherAlert();
                        weatherAlert.setHazardCode(hazardCode);
                        weatherAlert.setHazardType(hazardElement.getAttribute("hazardType"));
                        weatherAlert.setPhenomena(hazardElement.getAttribute("phenomena"));
                        weatherAlert.setSignificance(hazardElement.getAttribute("significance"));
                        weatherAlert.setMoreInformationUrl(hazardElement.getElementsByTagName("hazardTextURL").item(0).getTextContent());
                        weatherAlert.setHeadline(hazardElement.getAttribute("headline"));
                        weatherAlerts.add(weatherAlert);
                    }
                }
            }
        }
        return weatherAlerts;
    }

    public HazardIterator getWeatherAlertsIterator()
    {
        return new HazardIterator((Element)getValueAsNode("/dwml/data[@type=\"forecast\"]/parameters"), weatherAlerts);
    }

    public String getIconUrl()
    {
        if(iconUrl == null)
        {
            iconUrl = getValue("/dwml/data[@type=\"current observations\"]/parameters/conditions-icon/icon-link");
        }
        return iconUrl;
    }

    public List<WeatherCondition> getWeatherConditions()
    {
        return this.weatherConditions;
    }
}
