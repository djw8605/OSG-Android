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

public class OSGMonitoring extends Activity implements OnClickListener {
	
	public OSGMonitoring() {
		
		
		
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.osg_monitoring);
		
		XYMultipleSeriesRenderer xyseriesrender = this.getDemoRenderer();
		XYMultipleSeriesDataset xyseries = new XYMultipleSeriesDataset();
		XYSeries xy = new XYSeries("VO Usage");
		String [] site_names = GetSites();
		
		AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, site_names);
		textView.setAdapter(adapter);
		
		xy.add(1.0, 2.0);
		xy.add(3.0, 4.0);
		xy.add(10.0, 10.0);
		this.GetOSGVOUsage(5);

		xyseries.addSeries(xy);
		float[] stuff = new float[1];
		stuff[0] = 0;
		
		Intent intent = ChartFactory.getLineChartIntent(this, xyseries, xyseriesrender, "Stuff");
		startActivity(intent);
		
		
		//lc.drawSeries(draw_canvas, paint_canvas, stuff, new XYSeriesRenderer(), (float)15.0, 0);
		
		
	}

	public void onClick(View v) {
		
		Intent osg_monitoring_intent = new Intent(v.getContext(), OSGMonitoring.class);
		v.getContext().startActivity(osg_monitoring_intent);
		
	}
	
	

	  private XYMultipleSeriesRenderer getDemoRenderer() {
	    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
	    renderer.setAxisTitleTextSize(16);
	    renderer.setChartTitleTextSize(20);
	    renderer.setLabelsTextSize(15);
	    renderer.setLegendTextSize(15);
	    renderer.setPointSize(5f);
	    renderer.setMargins(new int[] {20, 30, 15, 0});
	    XYSeriesRenderer r = new XYSeriesRenderer();
	    r.setColor(Color.BLUE);
	    r.setPointStyle(PointStyle.SQUARE);
	    r.setFillBelowLine(true);
	    r.setFillBelowLineColor(Color.WHITE);
	    r.setFillPoints(true);
	    renderer.addSeriesRenderer(r);
	    r = new XYSeriesRenderer();
	    r.setPointStyle(PointStyle.CIRCLE);
	    r.setColor(Color.GREEN);
	    r.setFillPoints(true);
	    //renderer.addSeriesRenderer(r);
	    renderer.setAxesColor(Color.DKGRAY);
	    renderer.setLabelsColor(Color.LTGRAY);
	    return renderer;
	  }
	
	  
	  private XYMultipleSeriesDataset GetOSGVOUsage(int limit){
		  XYMultipleSeriesDataset xyseries = new XYMultipleSeriesDataset();
		  URL vo_url = null;
		  URLConnection urlConnection = null;
		  BufferedReader in = null;
		  String line_buffer = "";
		  String buffer = "";

		  try {
			  vo_url = new URL("http://gratiaweb.grid.iu.edu/gratia/csv/status_vo");
			  urlConnection = vo_url.openConnection();
			  in = new BufferedReader(new InputStreamReader(vo_url.openStream()));
			  try {
				  
				  while ((line_buffer = in.readLine()) != null)
					  buffer += line_buffer;
				  
			  } finally {
				  in.close();

			  }
		  } catch (IOException e) {
			  this.ErrorDialog(e.getMessage());
			  System.err.println(e.getMessage());

		  } finally {
			  if (in != null) {
				  try {
					  in.close();
				  } catch (IOException e) {
					  
				  } finally {
				  }
			  }
		  }
		  
		 
		  
		  return xyseries;
	  }


	  private void ErrorDialog(String message) {
		  
		  AlertDialog.Builder builder = new AlertDialog.Builder(this);
		  builder.setMessage(message)
		         .setCancelable(false)
		         .setNegativeButton("OK", new DialogInterface.OnClickListener() {
		             public void onClick(DialogInterface dialog, int id) {
		                  dialog.cancel();
		             }
		         });
		  AlertDialog alert = builder.create();
		  
		  
		  
	  }
	  
	  private String [] GetSites() {
		  OSGSiteXMLParser osg_parser = new OSGSiteXMLParser();
		  String url = "http://myosg.grid.iu.edu/rgsummary/?datasource=summary&gip_status_attrs_showtestresults=on&downtime_attrs_showpast=&account_type=cumulative_hours&ce_account_type=gip_vo&se_account_type=vo_transfer_volume&bdiitree_type=total_jobs&bdii_object=service&bdii_server=is-osg&start_type=7daysago&start_date=09%2F10%2F2011&end_type=now&end_date=09%2F10%2F2011&all_resources=on&gridtype=on&gridtype_1=on&service_central_value=0&service_hidden_value=0&active_value=1&disable_value=1";
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

	

