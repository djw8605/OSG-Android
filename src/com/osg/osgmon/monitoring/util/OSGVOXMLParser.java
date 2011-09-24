package com.osg.osgmon.monitoring.util;

import org.xml.sax.Attributes;

public class OSGVOXMLParser extends OSGSiteXMLParser {

	public void startElement(String uri, String localName, String qName, Attributes atts) {

		if (qName.equals("Name")) {
			reading_name = true;

		}


	}

}
