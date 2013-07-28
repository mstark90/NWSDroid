package com.michaelstark.nwsdroid;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class NWSWeatherRequest implements Serializable {
	/**
	 * 
	 */
    private static final long serialVersionUID = 635888803862503938L;

    private Date startDate;
    private Date endDate;
    public NWSWeatherRequest()
    {

    }
    public Date getStartDate() {
        return startDate;
    }
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    public Date getEndDate() {
        return endDate;
    }
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    public NWSWeatherResponse processRequest(Context context, double latitude, double longitude)
                    throws IOException, MalformedURLException,
                    ParserConfigurationException, XPathExpressionException,
                    SAXException
    {

        return processRequest(context, latitude, longitude, false);
    }
    public NWSWeatherResponse processRequest(Context context, String cityName, String state)
                    throws IOException, MalformedURLException,
                    ParserConfigurationException, XPathExpressionException,
                    SAXException
    {
        return processRequest(context, cityName, state, false);
    }
    public NWSWeatherResponse processRequest(Context context, double latitude, double longitude,
            boolean forceRefresh)
                    throws IOException, MalformedURLException,
                    ParserConfigurationException, XPathExpressionException,
                    SAXException
    {
        InputStream is;
        File dataFile = new File(context.getFilesDir(), String.format("weatherData_%.3f_%.3f.xml",
                latitude, longitude));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -60);
        Date retrievalTime = new Date(dataFile.lastModified());
        if(dataFile.exists() == false || forceRefresh ||
                retrievalTime.before(calendar.getTime()))
        {
            StringBuilder urlBuilder = new StringBuilder(
                    "http://forecast.weather.gov/MapClick.php?FcstType=dwml");
            urlBuilder.append("&lat=").append(latitude);
            urlBuilder.append("&lon=").append(longitude);
            java.net.URLConnection urlConn = new java.net.URL(urlBuilder.toString()).openConnection();
            is = urlConn.getInputStream();
            OutputStream os = new FileOutputStream(dataFile);
            byte[] buffer = new byte[4096];
            int read;
            while((read = is.read(buffer)) > 0)
            {
                os.write(buffer, 0, read);
            }
            os.close();
            is.close();
        }
        is = new FileInputStream(dataFile);
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = docBuilder.parse(is);
        is.close();
        NWSWeatherResponse response = new NWSWeatherResponse(doc.getDocumentElement());
        dataFile.setLastModified(response.getRetrievalTime().getTime());
        return response;
    }
    public NWSWeatherResponse processRequest(Context context, String cityName, String state,
            boolean forceRefresh)
                    throws IOException, MalformedURLException,
                    ParserConfigurationException, XPathExpressionException,
                    SAXException
    {
        Geocoder geoCoder = new Geocoder(context);
        List<Address> loadedAddresses = geoCoder.getFromLocationName(cityName +", "+ state, 1);
        if(loadedAddresses.size() > 0)
        {
            Address address = loadedAddresses.get(0);
            return processRequest(context, address.getLatitude(), address.getLongitude(), forceRefresh);
        }
        else
        {
            return null;
        }
    }
}
