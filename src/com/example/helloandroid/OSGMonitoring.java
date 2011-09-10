package com.example.helloandroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

public class OSGMonitoring extends Activity implements OnClickListener {
	
	private OSGSiteUsage osg_site_usage = null;
	
	public OSGMonitoring() {
		osg_site_usage = new OSGSiteUsage(this);
		
		
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.osg_monitoring);
		

		
		String [] site_names = GetSites();
		
		AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
		textView.setThreshold(3);
		Button get_usage_button = (Button) findViewById(R.id.confirm_site);
		get_usage_button.setOnClickListener(this.osg_site_usage);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, site_names);
		textView.setAdapter(adapter);
		

	
		

		
		
		//lc.drawSeries(draw_canvas, paint_canvas, stuff, new XYSeriesRenderer(), (float)15.0, 0);
		
		
	}

	public void onClick(View v) {
		
		Intent osg_monitoring_intent = new Intent(v.getContext(), OSGMonitoring.class);
		v.getContext().startActivity(osg_monitoring_intent);
		
	}
	
	

	  private String [] GetSites() {
		  System.setProperty("org.xml.sax.driver","org.xmlpull.v1.sax2.Driver");
		  OSGSiteXMLParser osg_parser = new OSGSiteXMLParser();
		  String url = "http://myosg.grid.iu.edu/rgsummary/xml?datasource=summary&gip_status_attrs_showtestresults=on&downtime_attrs_showpast=&account_type=cumulative_hours&ce_account_type=gip_vo&se_account_type=vo_transfer_volume&bdiitree_type=total_jobs&bdii_object=service&bdii_server=is-osg&start_type=7daysago&start_date=09%2F10%2F2011&end_type=now&end_date=09%2F10%2F2011&all_resources=on&gridtype=on&gridtype_1=on&service_central_value=0&service_hidden_value=0&active_value=1&disable_value=1%22";
		  //String [] toReturn = null;
		  
		  try {
		  XMLReader myReader = XMLReaderFactory.createXMLReader();
		  myReader.setContentHandler(osg_parser);
		  myReader.parse(new InputSource(new URL(url).openStream()));
		  } catch (Exception e) {
			  System.err.println(e.getMessage());
		  }
		  
		  return osg_parser.GetSites();
		  
		  
	  }
	  
}

	

