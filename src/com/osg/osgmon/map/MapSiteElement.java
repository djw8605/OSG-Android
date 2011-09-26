package com.osg.osgmon.map;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class MapSiteElement extends OverlayItem {

	protected String mSiteName;
	protected float lat;
	protected float log;

	
	
	public MapSiteElement(String site_name, float lat, float log) {
		super(new GeoPoint((int)(lat * 1E6), (int)(log * 1E6)), site_name, site_name);
		this.mSiteName = site_name;
		this.lat = lat;
		this.log = log;
		
	}
	
	
}
