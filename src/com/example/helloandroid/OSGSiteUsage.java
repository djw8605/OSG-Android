package com.example.helloandroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;

public class OSGSiteUsage  implements OnClickListener {

	public Context context;
	
	private Activity act;
	
	public OSGSiteUsage(Activity act) {
		this.act = act;
		
	}
	
	public void onClick(View v) {
		AutoCompleteTextView textView = (AutoCompleteTextView) this.act.findViewById(R.id.autoCompleteTextView1);
		AutoCompleteTextView vo_textView = (AutoCompleteTextView) this.act.findViewById(R.id.vo_auto_complete);
		
		String site = textView.getText().toString();
		String vo = vo_textView.getText().toString();
		
		context = v.getContext();
		
		
		XYMultipleSeriesDataset xyseries = this.GetOSGVOUsage(site, vo);
		XYMultipleSeriesRenderer xyseriesrender = this.getDemoRenderer(xyseries.getSeriesCount());
		Intent intent = ChartFactory.getLineChartIntent(this.context, xyseries, xyseriesrender, site);
		
	    act.startActivity(intent);
		
		
	}
	
	private void ShowProgressBar() {
		
		
		
		
	}


	  private XYMultipleSeriesRenderer getDemoRenderer(int seriesRequested) {
		Random rand = new Random();
	    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
	    renderer.setAxisTitleTextSize(16);
	    renderer.setChartTitleTextSize(20);
	    renderer.setLabelsTextSize(15);
	    renderer.setLegendTextSize(15);
	    renderer.setPointSize(5f);
	    renderer.setMargins(new int[] {20, 30, 15, 0});
	    XYSeriesRenderer r = new XYSeriesRenderer();
	    for (int i = 0; i < seriesRequested; i++) {
	    	r = new XYSeriesRenderer();
	    	r.setColor(Color.rgb(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)));
	    	//r.setColor((i*200)+16);
	    	r.setPointStyle(PointStyle.SQUARE);
	    	r.setFillPoints(true);
	    	renderer.addSeriesRenderer(r);
	    }

	    //renderer.addSeriesRenderer(r);
	    renderer.setAxesColor(Color.DKGRAY);
	    renderer.setLabelsColor(Color.LTGRAY);
	    return renderer;
	  }
	
	  
	  private XYMultipleSeriesDataset GetOSGVOUsage(String site, String vo){
		  XYMultipleSeriesDataset xyseries = new XYMultipleSeriesDataset();
		  URL vo_url = null;
		  URLConnection urlConnection = null;
		  BufferedReader in = null;
		  String line_buffer = "";
		  String buffer = "";
		  if (site.equals(""))
			  site = ".*";
		  if (vo.equals(""))
			  vo = ".*";

		  try {
			  
			  vo_url = new URL("http://gratiaweb.grid.iu.edu/gratia/csv/status_vo?facility=" + site + "&vo=" + vo);
			  urlConnection = vo_url.openConnection();
			  in = new BufferedReader(new InputStreamReader(vo_url.openStream()));
			  try {
				  String vo_key = "";
				  XYSeries xy = null;
				  String [] entries = null;
				  while ((line_buffer = in.readLine()) != null) {
					  entries = line_buffer.split(",");
					  if (entries[0].equals("VO"))
						  continue;
					  
					  if (!vo_key.equals(entries[0])) {
						  vo_key = entries[0];
						  if (xy != null) {
							  if(xy.getItemCount() > 0)
								  xyseries.addSeries(xy);
						  }
						  xy = new XYSeries(vo_key);
					  }
					  SimpleDateFormat simple_date = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
					  Date d = null;
					  try {
						  d = simple_date.parse(entries[1], new ParsePosition(0));
						  xy.add((double)d.getTime()/(3600*24*365), Double.parseDouble(entries[2]));
					  } catch (Exception e) {
						  System.err.println(e.getMessage());
					  } finally {
						  continue;
					  }
				
					  
				  }
					  
				  
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
		  
		  AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
		  builder.setMessage(message)
		         .setCancelable(false)
		         .setNegativeButton("OK", new DialogInterface.OnClickListener() {
		             public void onClick(DialogInterface dialog, int id) {
		                  dialog.cancel();
		             }
		         });
		  AlertDialog alert = builder.create();
		  
		  
		  
	  }
	  
	
	
}
