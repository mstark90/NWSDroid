package com.michaelstark.nwsdroid;

public class WeatherCondition
{
    private String name;
    private String value;
    private String units;
    WeatherCondition()
    {

    }
    public String getName()
    {
        return name;
    }
    void setName(String name)
    {
        this.name = name;
    }
    public String getValue()
    {
        return value;
    }
    void setValue(String value)
    {
        this.value = value;
    }
    public String getUnits()
    {
        return units;
    }
    void setUnits(String units)
    {
        this.units = units;
    }
}
