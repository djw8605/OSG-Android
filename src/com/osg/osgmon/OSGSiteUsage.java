package com.osg.osgmon;

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
import org.achartengine.GraphicalView;
import org.achartengine.chart.AbstractChart;
import org.achartengine.chart.XYChart;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

public class OSGSiteUsage extends Activity implements OnClickListener, Runnable {

	public Context context;

	private Activity act;

	/** The encapsulated graphical view. */
	private GraphicalView mView;
	/** The chart to be drawn. */
	private AbstractChart mChart;

	private ViewGroup view_to_append;
	
	public OSGSiteUsage(ViewGroup viewGroup) {
		this.view_to_append = viewGroup;
		//this.act = this;
	}
	
	public OSGSiteUsage(Activity act) {
		this.act = act;

	}
	

	

	private Thread data_thread;
	private String site = "";
	private String vo = "";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		mChart = (AbstractChart) extras.getSerializable(ChartFactory.CHART);
		mView = new GraphicalView(this, mChart);
		//StackedTimeChart stacked_chart = (StackedTimeChart) mChart;
		//ViewGroup vg = (ViewGroup) mView.getParent();
		//vg.removeView(mView);
		
		setContentView(mView);

	}

	public void updateGraph(Activity act, Context context, String site, String vo) {
		this.act = act;
		this.context = context;
		this.ShowProgressBar();
		this.site = site;
		this.vo = vo;
		this.data_thread = new Thread(this);
		this.data_thread.start();
	}
	
	
	public void onClick(View v) {
		AutoCompleteTextView textView = (AutoCompleteTextView) this.act.findViewById(R.id.autoCompleteTextView1);
		Spinner vo_spinner = (Spinner) this.act.findViewById(R.id.vo_spinner);
		
		
		site = textView.getText().toString();
		vo = (String) vo_spinner.getAdapter().getItem(vo_spinner.getSelectedItemPosition());
		if (vo.equals(OSGMonitoringActivity.DEFAULT_VO)) {
			vo = "";
		}
		
		context = v.getContext();
		
		
		
	}
	
	
	Handler usage_handler = new Handler() {
		
		public void handleMessage(Message msg) {
			
			XYMultipleSeriesDataset xyseries = (XYMultipleSeriesDataset) msg.obj; 
			XYMultipleSeriesRenderer xyseriesrender = getDemoRenderer(xyseries.getSeriesCount());
			
			//Intent intent = new Intent(context, OSGSiteUsage.class);
		    //XYChart chart = new StackedTimeChart(xyseries, xyseriesrender);
		   
		    //intent.putExtra("chart", chart);
		    //intent.putExtra("title" );
		    
		    //act.startActivity(intent);
		    
			//Intent intent = ChartFactory.getTimeChartIntent(context, xyseries, xyseriesrender, site);
			
			p_dialog.dismiss();
			XYChart chart = new StackedTimeChart(xyseries, xyseriesrender);
			View mView = new GraphicalView(context, chart);
			view_to_append.removeAllViews();
			view_to_append.addView(mView);
		    //act.startActivity(intent);
		    
		    
			
		}
		
	};
	
	public void run() {
		// TODO Auto-generated method stub
		Message msg = Message.obtain(usage_handler);
		
		msg.obj = this.GetOSGVOUsage(site, vo);
		msg.sendToTarget();
		
	}
	  
	
	private ProgressDialog p_dialog = null;
	
	private void ShowProgressBar() {
		this.p_dialog = new ProgressDialog(this.act);
		this.p_dialog.setCancelable(true);
		this.p_dialog.setMessage("Loading usage...");
		this.p_dialog.show();
		
		
		
	}


	  private XYMultipleSeriesRenderer getDemoRenderer(int seriesRequested) {
		Random rand = new Random();
	    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
	    renderer.setAxisTitleTextSize(16);
	    renderer.setChartTitleTextSize(20);
	    renderer.setLabelsTextSize(15);
	    renderer.setLegendTextSize(15);
	    
	    //renderer.setPointSize(5f);
	    renderer.setMargins(new int[] {20, 30, 15, 0});
	    XYSeriesRenderer r = new XYSeriesRenderer();
	    for (int i = 0; i < seriesRequested; i++) {
	    	r = new XYSeriesRenderer();
	    	int color = Color.rgb(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
	    	r.setColor(color);
	    	//r.setColor((i*200)+16);
	    	//r.setPointStyle(PointStyle.SQUARE);
	    	r.setFillBelowLine(true);
	    	r.setFillBelowLineColor(color);
	    	r.setFillPoints(true);
	    	r.setLineWidth(5);
	    	renderer.addSeriesRenderer(r);
	    }

	    //renderer.addSeriesRenderer(r);
	    renderer.setAxesColor(Color.DKGRAY);
	    renderer.setLabelsColor(Color.LTGRAY);
	    return renderer;
	  }
	
	  
	  private XYMultipleSeriesDataset GetOSGVOUsage(String site, String vo){
		  //XYMultipleSeriesDataset xyseries = new XYMultipleSeriesDataset();
		  XYMultipleSeriesDataset xyseries = new StackedXYMultipleSeries();
		  URL vo_url = null;
		  URLConnection urlConnection = null;
		  BufferedReader in = null;
		  String line_buffer = "";
		  String buffer = "";
		  if (site.equals(""))
			  site = ".*";
		  if (vo.equals(""))
			  vo = ".*";
		  vo = vo.toLowerCase();

		  try {
			  
			  vo_url = new URL("http://gratiaweb.grid.iu.edu/gratia/csv/status_vo?facility=" + site + "&vo=" + vo);
			  urlConnection = vo_url.openConnection();
			  in = new BufferedReader(new InputStreamReader(vo_url.openStream()));
			  try {
				  String vo_key = "";
				  TimeSeries xy = null;
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
						  xy = new TimeSeries(vo_key);
					  }
					  SimpleDateFormat simple_date = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
					  Date d = null;
					  try {
						  d = simple_date.parse(entries[1], new ParsePosition(0));
						  xy.add(d, Double.parseDouble(entries[2]));
					  } catch (Exception e) {
						  System.err.println(e.getMessage());
						  continue;
					  } finally {
						  continue;
					  }
				
					  
				  }
				  if (xy != null)
					  xyseries.addSeries(xy);
					  
			  } catch (Exception e) {
				  System.err.println(e.getMessage());
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
