package com.michaelstark.nwsdroid;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

public abstract class XmlWeatherObject
{
    private XPath xPath;
    private Element root;

    protected XmlWeatherObject(Element root)
    {
        try
        {
            xPath = XPathFactory.newInstance().newXPath();
            this.root = root;
        }
        catch(Exception e)
        {
            Log.e("XmlWeatherObject", "Could not load the XPath parser: ", e);
        }
    }
    
    protected Element getRootElement()
    {
        return root;
    }

    protected XPath getXPath()
    {
        return xPath;
    }

    protected String getValue(String expression)
    {
        try
        {
            return (String)xPath.evaluate(expression, root, XPathConstants.STRING);
        }
        catch(Exception e)
        {
            Log.d("XmlWeatherObject", "There was an error in parsing the element: ", e);
            return null;
        }
    }

    protected Node getValueAsNode(String expression)
    {
        try
        {
            return (Node)xPath.evaluate(expression, root, XPathConstants.NODE);
        }
        catch(Exception e)
        {
            Log.d("XmlWeatherObject", "There was an error in parsing the element: ", e);
            return null;
        }
    }

    protected NodeList getValueAsNodeList(String expression)
    {
        try
        {
            return (NodeList)xPath.evaluate(expression, root, XPathConstants.NODESET);
        }
        catch(Exception e)
        {
            Log.d("XmlWeatherObject", "There was an error in parsing the element: ", e);
            return null;
        }
    }
	
}
