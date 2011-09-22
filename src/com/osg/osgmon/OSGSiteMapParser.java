package com.osg.osgmon;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class OSGSiteMapParser implements ContentHandler {

	protected boolean ReadingLat = false;
	protected boolean ReadingLog = false;
	protected boolean ReadingName = false;
	protected ArrayList<MapSiteElement> LatLogSite;
	protected float lat = 0;
	protected float log = 0;
	protected String siteName;
	protected String current_path = null;
	
	public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
		// TODO Auto-generated method stub
		if (ReadingLat)
			lat = Float.parseFloat(new String(arg0));
		else if (ReadingLog)
			log = Float.parseFloat(new String(arg0));
		
	}

	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub

	}

	public void endElement(String arg0, String arg1, String arg2)
			throws SAXException {
		// TODO Auto-generated method stub
		this.current_path =  this.current_path.split(".");
		
		if (ReadingLat)
			ReadingLat = false;
		if (ReadingLog)
			ReadingLog = false;
		if (ReadingName)
			ReadingName = false;
		
		if (arg0.equals("Name")) {
			
		}

	}
	
	

	public void endPrefixMapping(String prefix) throws SAXException {
		// TODO Auto-generated method stub

	}

	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub

	}

	public void processingInstruction(String target, String data)
			throws SAXException {
		// TODO Auto-generated method stub

	}

	public void setDocumentLocator(Locator locator) {
		// TODO Auto-generated method stub

	}

	public void skippedEntity(String name) throws SAXException {
		// TODO Auto-generated method stub

	}

	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub

	}

	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		// TODO Auto-generated method stub
		
		this.current_path += "." + qName;

	}

	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		// TODO Auto-generated method stub

	}

}
