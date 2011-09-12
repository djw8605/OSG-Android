package com.example.helloandroid;

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
		
		GeoPoint point = new GeoPoint( 40820645, -96692843);
		OverlayItem overlayitem = new OverlayItem(point, "Nebraska", "Nebraska Site");
		
		itemizedOverlay.addOverlay(overlayitem);
		mapOverlays.add(itemizedOverlay);
		
	}
	
	
	
	
}
