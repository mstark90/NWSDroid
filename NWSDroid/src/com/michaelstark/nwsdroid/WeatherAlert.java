package com.michaelstark.nwsdroid;

public class WeatherAlert
{
    private String hazardCode;
    private String phenomena;
    private String significance;
    private String hazardType;
    private String moreInformationUrl;
    private String headline;
    WeatherAlert()
    {

    }
    public String getMoreInformationUrl()
    {
        return moreInformationUrl;
    }
    void setMoreInformationUrl(String moreInformationUrl)
    {
        this.moreInformationUrl = moreInformationUrl;
    }
    public String getHazardType()
    {
        return hazardType;
    }
    void setHazardType(String hazardType)
    {
        this.hazardType = hazardType;
    }
    public String getSignificance()
    {
        return significance;
    }
    void setSignificance(String significance)
    {
        this.significance = significance;
    }
    public String getPhenomena()
    {
        return phenomena;
    }
    void setPhenomena(String phenomena)
    {
        this.phenomena = phenomena;
    }
    public String getHazardCode()
    {
        return hazardCode;
    }
    void setHazardCode(String hazardCode)
    {
        this.hazardCode = hazardCode;
    }
    public String getHeadline()
    {
        return this.headline;
    }
    void setHeadline(String headline)
    {
        this.headline = headline;
    }
}
