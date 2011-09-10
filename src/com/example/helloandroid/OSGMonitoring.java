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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class OSGMonitoring extends Activity implements OnClickListener {
	
	public OSGMonitoring() {
		
		
		
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.osg_monitoring);
		
		XYMultipleSeriesRenderer xyseriesrender = this.getDemoRenderer();
		XYMultipleSeriesDataset xyseries = new XYMultipleSeriesDataset();
		XYSeries xy = new XYSeries("Example series");
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


}

	

