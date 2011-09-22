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
	protected boolean read_site = false;
	
	
	public OSGSiteMapParser() {
		this.LatLogSite = new ArrayList<MapSiteElement>();
	}
	
	public ArrayList<MapSiteElement> getOverlays() {
		return this.LatLogSite;
	}
	
	public void characters(char[] arg0, int start, int length) throws SAXException {
		// TODO Auto-generated method stub
		if (ReadingLat)
			lat = Float.parseFloat(new String(arg0, start, length));
		else if (ReadingLog)
			log = Float.parseFloat(new String(arg0, start, length));
		else if (ReadingName) {
			siteName = new String(arg0, start, length);
			this.read_site = false;
		}
		
	}

	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub

	}

	public void endElement(String arg0, String arg1, String arg2)
			throws SAXException {
		
		if (ReadingLat)
			ReadingLat = false;
		if (ReadingLog)
			ReadingLog = false;
		if (ReadingName)
			ReadingName = false;
		
		if (arg1.equals("Site")) {
			this.LatLogSite.add(new MapSiteElement(this.siteName, this.lat, this.log));
			
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
		
		if (qName.equals("Site")) {
			this.read_site = true;
		} else if (qName.equals("Latitude")) {
			this.ReadingLat = true;
		} else if (qName.equals("Longitude")) {
			this.ReadingLog = true;
		}
		
		if (qName.equals("Name") && this.read_site) {
			this.ReadingName = true;
			
		}

	}

	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		// TODO Auto-generated method stub

	}

}
