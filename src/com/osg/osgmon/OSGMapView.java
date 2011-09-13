package com.osg.osgmon;

import java.util.List;

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
	
	protected boolean isRouteDisplayed() {
	    return false;
	    
	}
	
	LinearLayout linearLayout;
	MapView mapView;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maplayout);
		
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
	
	
	
	
}
