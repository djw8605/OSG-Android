package com.osg.osgmon.monitoring;

import java.io.BufferedReader;
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

public class OSGSiteUsage extends Activity implements OnClickListener, Runnable {

	public Context context;

	private Activity act;

	/** The encapsulated graphical view. */
	private GraphicalView mView;
	/** The chart to be drawn. */
	private AbstractChart mChart;

	private ViewGroup view_to_append;
	
	private GraphicalView current_view;
	
	public OSGSiteUsage this_ptr = this;
	
	protected XYMultipleSeriesDataset current_data;
	
	public OSGSiteUsage(ViewGroup viewGroup) {
		this.view_to_append = viewGroup;
		//this.act = this;
	}
	
	public OSGSiteUsage(Activity act) {
		this.act = act;

	}
	

	public XYMultipleSeriesDataset getCurrentData() {
		return this.current_data;
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
	
	public void updateGraph(Activity act, Context context, XYMultipleSeriesDataset current_data) {
		this.act = act;
		this.context = context;
		createChart(current_data);
		
	}
	
	
	public void onClick(View v) {
		
		if(v.getId() == this.current_view.getId()) {
			
		}

	}
	

	protected void createChart(XYMultipleSeriesDataset xyseries) {
		this.current_data = xyseries;
		XYMultipleSeriesRenderer xyseriesrender = getDemoRenderer(xyseries.getSeriesCount());
		XYChart chart = new StackedTimeChart(xyseries, xyseriesrender);
		GraphicalView mView = new SelectableGraphicalView(context, chart);

		view_to_append.removeAllViews();
		view_to_append.addView(mView);
		mView.setId(1);
		//mView.setOnClickListener(this_ptr);
		current_view = mView;
	    //act.startActivity(intent);
		
		mView.repaint();
		
	}
	
	Handler usage_handler = new Handler() {
		
		public void handleMessage(Message msg) {
			
			XYMultipleSeriesDataset xyseries = (XYMultipleSeriesDataset) msg.obj; 
			
			p_dialog.dismiss();
			if (xyseries.getSeriesCount() == 0) {
				ErrorDialog("Didn't get any usage.");
			} else {
				createChart(xyseries);
			}
			
		}
		
	};

	
	Handler usage_progress_handler = new Handler() {

		public void handleMessage(Message msg) {
			if (msg.arg2 == 1) {
				p_dialog.setMessage((String)msg.obj);
			}
			p_dialog.setProgress(msg.arg1);
			

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
		this.p_dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
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
		  StackedXYMultipleSeries xyseries = new StackedXYMultipleSeries();
		  xyseries.setShowOther(true);
		  xyseries.setShowSize(10);
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
		  int content_length = 0;
		  int progress = 0;
		  int previous_counter = 0;
		  Message msg;

		  try {

			  vo_url = new URL("http://gratiaweb.grid.iu.edu/gratia/csv/status_vo?facility=" + site + "&vo=" + vo);
			  urlConnection = vo_url.openConnection();
			  in = new BufferedReader(new InputStreamReader(vo_url.openStream()));
			  if (urlConnection.getContentLength() > 0)
				  content_length = urlConnection.getContentLength()*10;

			  String vo_key = "";
			  TimeSeries xy = null;
			  String [] entries = null;
			  while ((line_buffer = in.readLine()) != null) {
				  try {
					  progress += line_buffer.length();
					  int tmp_progress =  (int) ( ((float)progress /  (float)content_length )*100.0);
					  if (tmp_progress > previous_counter+5) {
						  msg = Message.obtain(this.usage_progress_handler);
						  msg.arg1 = tmp_progress;
						  msg.sendToTarget();
						  previous_counter = tmp_progress;
					  }
					  
				  } catch (Exception e) {
					  System.err.println(e.getMessage());
				  }
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
			  if (xy != null) {
				  xyseries.addSeries(xy);
			  }


		  }catch (Exception e) {
			  System.err.println(e.getMessage());
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

	  public void redraw() {
		  if (this.current_view != null)
			  this.current_view.repaint();
		  
	  }

	
	
}
