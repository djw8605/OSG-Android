package com.osg.osgmon;

import java.net.URL;
import java.util.List;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class OSGMapView extends MapActivity {

	List<Overlay> mapOverlays;
	Drawable drawable;
	OSGSiteItemizedOverlay itemizedOverlay;
	
	private Thread UpdateSitesThread = null;
	
	protected boolean isRouteDisplayed() {
	    return false;
	    
	}
	
	public final static String SITE_URL = "http://myosg-itb.grid.iu.edu/map/xml?map_attrs_shownr=on&all_sites=on&active=on&active_value=1&disable_value=1&gridtype=on&gridtype_1=on";
	
	LinearLayout linearLayout;
	MapView mapView;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maplayout);
		
		this.ParseSites();
		
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		
		mapOverlays = mapView.getOverlays();
		drawable = this.getResources().getDrawable(R.drawable.nebraskan);
		itemizedOverlay = new OSGSiteItemizedOverlay(drawable, mapView);
		
		GeoPoint point = new GeoPoint( (int)(40.820645*1E6), (int)(-96.692843*1E6));
		OverlayItem overlayitem = new OverlayItem(point, "Nebraska", "Nebraska Site");
		overlayitem.setMarker(drawable);
		itemizedOverlay.addOverlay(overlayitem);
		mapOverlays.add(itemizedOverlay);
		
	}

	public void ParseSites() {
		
		this.UpdateSitesThread = new Thread(new Runnable() {
			public void run() {
				System.setProperty("org.xml.sax.driver","org.xmlpull.v1.sax2.Driver");
				OSGSiteMapParser osg_parser = new OSGSiteMapParser();
				String url = OSGMapView.SITE_URL;


				try {
					XMLReader myReader = XMLReaderFactory.createXMLReader();
					myReader.setContentHandler(osg_parser);
					myReader.parse(new InputSource(new URL(url).openStream()));
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
				
			}
			
			
			
		});
		
		this.UpdateSitesThread.start();


	}



}
