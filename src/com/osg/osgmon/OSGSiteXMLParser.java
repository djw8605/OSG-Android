package com.osg.osgmon;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;


public class OSGSiteXMLParser implements ContentHandler {

	private ArrayList<String> sites;
	
	private boolean reading_name = false;
	
	public OSGSiteXMLParser() {
		
		this.sites = new ArrayList<String>();
		
	}
	
	public void startElement(String uri, String localName, String qName, Attributes atts) {
		
		if (qName.equals("Name")) {
			reading_name = true;
			
		}
		
		
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (reading_name) {
			sites.add(new String(ch, start, length));
		}
		
	}
	
	public void endElement(String uri, String localName,
			String qName) throws SAXException {
		if (reading_name)
			reading_name = false;
		
		
	}
	
	public String [] GetSites() {
		//Object [] objArray = sites.toArray();
		String [] strArray = new String[sites.size()];
		for (int i = 0; i < sites.size(); i++) {
			strArray[i] = sites.get(i);
		}
		
		return strArray;
		
	}
	
	public void ignorableWhitespace(char[] ch,
            int start,
            int length)
            throws SAXException {
		
		
	}
	
	public void skippedEntity(String name)
            throws SAXException {
		
	}
	
	public void processingInstruction(String target,
            String data)
            throws SAXException {
		
	}
	
	public void endPrefixMapping(String prefix)
            throws SAXException {
		
	}
	
	public void startPrefixMapping(String prefix,
            String uri)
            throws SAXException {
		
	}
	
	public void endDocument()
            throws SAXException {
		
	}
	
	public void startDocument()
            throws SAXException {
		
	}
	
	public void setDocumentLocator(Locator locator) {
		
	}
	
}
