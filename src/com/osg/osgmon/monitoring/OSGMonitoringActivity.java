package com.osg.osgmon.monitoring;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.Spinner;

import com.osg.osgmon.R;
import com.osg.osgmon.monitoring.util.OSGSiteXMLParser;
import com.osg.osgmon.monitoring.util.OSGVOXMLParser;

public class OSGMonitoringActivity extends Activity implements OnClickListener, Runnable, OnDrawerCloseListener, OnDrawerOpenListener {
	
	// Graph activity
	private OSGSiteUsage osg_site_usage = null;
	
	public static String DEFAULT_VO = "Select VO";
	
	// Pointer to self
	// Used mostly in the message
	private Activity osg_monitoring = this;
	
	private Spinner vo_spinner = null;
	
	
	public OSGMonitoringActivity() {
		//osg_site_usage = new OSGSiteUsage(this);
		
		
	}
	

	private AutoCompleteTextView auto_textview = null;
	private AutoCompleteTextView vo_autotext = null;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.osg_monitoring);
		
		this.initializeAutocompletes();
		
		// Load the data from a configuration (orientation...) change
		final MonitoringActivitySavedState data = (MonitoringActivitySavedState) getLastNonConfigurationInstance();
		if (data == null) {

			StartProgressDialog("Loading Sites...");
			// Start the data thread
			Thread sites_thread = new Thread(this);
			sites_thread.start();

			SlidingDrawer sd = (SlidingDrawer) findViewById(R.id.slidingDrawer);
			sd.animateOpen();

		} else {
	    	// Load in the data from the saved state.
	    	this.auto_textview.setAdapter((ArrayAdapter) data.getSitesAdapter());
	    	this.auto_textview.setText(data.getSitesText());
	    	this.vo_spinner.setAdapter(data.getVOsAdapter());
	    	this.vo_spinner.setSelection(data.getVOsSelected());
	    	
	    	
	    	// Slider drawer doesn't work right.
	    	SlidingDrawer sd = (SlidingDrawer) findViewById(R.id.slidingDrawer);
	    	if (data.getSliderOpen())
	    		sd.open();
	    	else
	    		sd.close();
	    	
	    	if (data.getChartData() != null)
	    		this.osg_site_usage.updateGraph(this, sd.getContext(), data.getChartData());
	    	
	    	//this.updateGraph();
	    }
		
		
	}
	
	
	/**
	 * This function will only be called the first time the activity is shown
	 * 
	 * 
	 */
	protected void initializeAutocompletes() {
		
		
		this.auto_textview = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
		this.auto_textview.setThreshold(2);
		
		// Ignore enter key
		this.auto_textview.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				 if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
				            (keyCode == KeyEvent.KEYCODE_ENTER)) {
					 onClick(auto_textview);
					 return true;
				 }

				return false;
			}
		});
		
		this.vo_spinner = (Spinner) findViewById(R.id.vo_spinner);
		
		
		
		Button get_usage_button = (Button) findViewById(R.id.confirm_site);
		get_usage_button.setOnClickListener(this);
		
		ViewGroup slider_view_group = (ViewGroup) findViewById(R.id.sliderlayout);
		osg_site_usage = new OSGSiteUsage(slider_view_group);

		// Open the slider when the screen is shown for the first time.
		SlidingDrawer sd = (SlidingDrawer) findViewById(R.id.slidingDrawer);
		
		sd.setOnDrawerCloseListener(this);
		sd.setOnDrawerOpenListener(this);
		
	}
	
	/**
	 * Save the current state of the activity
	 * 
	 */
	 public Object onRetainNonConfigurationInstance() {
	    MonitoringActivitySavedState saved_state = new MonitoringActivitySavedState();
	    SlidingDrawer sd = (SlidingDrawer) findViewById(R.id.slidingDrawer);
	    saved_state.setSliderOpen(sd.isOpened());
	    saved_state.setSitesAdapter(this.auto_textview.getAdapter());
	    saved_state.setSitesText(this.auto_textview.getText().toString());
	    saved_state.setVOsAdapter(this.vo_spinner.getAdapter());
	    saved_state.setVOsSelected(this.vo_spinner.getSelectedItemPosition());
	    saved_state.setChartData(this.osg_site_usage.getCurrentData());
	    return saved_state;
	 }

	public void onDrawerClosed() {
		Context context = findViewById(R.id.sliderlayout).getContext();
		InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE); 
		IBinder focus_binder = findViewById(R.id.sliderlayout).getWindowToken();
		inputManager.hideSoftInputFromWindow(focus_binder, InputMethodManager.HIDE_NOT_ALWAYS);
		osg_site_usage.redraw();
		
	}
	
	public void onDrawerOpened() {
		this.auto_textview.selectAll();
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.osg_monitoring_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.refresh:
	    	//View v = findViewById(R.id.confirm_site);
	        this.updateGraph();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	private ProgressDialog p_dialog;
	
	private String [] site_names;
	private String [] vo_names;
	
	public void run() {
		String [] sites = GetSites();
		Message msg = Message.obtain(sitesMessage);
		msg.obj = sites;
		msg.sendToTarget();
		
		StopProgressDialog();
		
		msg = Message.obtain(vosMessage);
		msg.arg1 = 1;
		msg.sendToTarget();
		
		String [] vos = GetVOs();
		msg = Message.obtain(vosMessage);
		msg.obj = vos;
		msg.sendToTarget();
		
		StopProgressDialog();
		
	}
	
	Handler sitesMessage = new Handler() {
		
		public void handleMessage(Message msg) {

			site_names = (String []) msg.obj;

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(osg_monitoring, R.layout.list_item, site_names);
			auto_textview.setAdapter(adapter);
			
			
		}
		
	};
	
	Handler vosMessage = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.arg1 == 1) {
				StartProgressDialog("Loading VO's...");
			} else {
				vo_names = (String []) msg.obj;
				
				String [] default_vo = new String[1];
				default_vo[0] = OSGMonitoringActivity.DEFAULT_VO;
				
				// Get site names from message, and concat with defaults
				vo_names = (String []) msg.obj;
				String [] concat_vo_names = new String[vo_names.length + default_vo.length];
				int i = 0;
				for (i = 0; i < default_vo.length; i++)
					concat_vo_names[i] = default_vo[i];
				for (; i<vo_names.length+default_vo.length; i++)
					concat_vo_names[i] = vo_names[i-default_vo.length];
					
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(osg_monitoring, R.layout.list_item2, concat_vo_names);
				//vo_autotext.setAdapter(adapter);
				vo_spinner.setAdapter(adapter);
				StopProgressDialog();
			}
			
		}
	};
	
	private void StartProgressDialog(String msg) {
		this.p_dialog = new ProgressDialog(this);
		this.p_dialog.setCancelable(true);
		this.p_dialog.setMessage(msg);
		this.p_dialog.show();
		
	}
	
	private void StopProgressDialog() {
		this.p_dialog.dismiss();
	}

	public void onClick(View v) {
		
		InputMethodManager inputManager = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE); 
		inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		SlidingDrawer sd = (SlidingDrawer) findViewById(R.id.slidingDrawer);
		sd.animateClose();
		updateGraph();
	}
	
	public void updateGraph() {
		
		AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
		Spinner vo_spinner = (Spinner) findViewById(R.id.vo_spinner);
		
		
		String site = textView.getText().toString();
		String vo = (String) vo_spinner.getAdapter().getItem(vo_spinner.getSelectedItemPosition());
		if (vo.equals(OSGMonitoringActivity.DEFAULT_VO)) {
			vo = "";
		}
		
		
		osg_site_usage.updateGraph(this, vo_spinner.getContext(), site, vo);
		
		
		
		//Intent osg_monitoring_intent = new Intent(v.getContext(), OSGMonitoringActivity.class);
		//v.getContext().startActivity(osg_monitoring_intent);
		
	}
	
	
	/**
	 * Get Sites section
	 */
	private final static String SITE_CACHE_NAME = "SITE_CACHE";

	  private String [] GetSites() {
		  // Check if there's a cache file
		  String [] sites = null;
		  
		  BufferedReader in = this.GetInputCacheFile(OSGMonitoringActivity.SITE_CACHE_NAME);
		  if (in != null) {
			  ArrayList<String> string_list = new ArrayList<String>();
			  String buf = "";
			  try {
				  while ((buf = in.readLine()) != null) {
					  string_list.add(buf);
				  }
				  sites = (String []) string_list.toArray(new String[0]);
				  return sites;
			  } catch (Exception e) {
				  System.out.println(e.getMessage());
			  }

		  }
		  
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

		  sites = osg_parser.GetSites();

		  BufferedWriter out = this.GetOutputCacheFile(OSGMonitoringActivity.SITE_CACHE_NAME);
		  try{
			  if (out != null) {
				  for (int i = 0; i < sites.length; i++) {
					  out.write(sites[i] + "\n");
				  }
				  out.flush();
			  }
		  } catch (Exception ex) {
			  System.err.println(ex.getMessage());

		  }
		  return sites;


	  }

	  
	  /**
	   * VO Get section
	   */
	  private final static String VO_CACHE_NAME = "VO_CACHE";
	  
	  private String [] GetVOs() {
		  String [] vos = null;
		  
		  BufferedReader in = this.GetInputCacheFile(OSGMonitoringActivity.VO_CACHE_NAME);
		  if (in != null) {
			  ArrayList<String> string_list = new ArrayList<String>();
			  String buf = "";
			  try {
				  while ((buf = in.readLine()) != null) {
					  string_list.add(buf);
				  }
				  vos = (String []) string_list.toArray(new String[0]);
				  return vos;
			  } catch (Exception e) {
				  System.out.println(e.getMessage());
			  }

		  }
		  
		  OSGVOXMLParser osg_parser = new OSGVOXMLParser();

		  String url = "http://myosg.grid.iu.edu/vosummary/xml?all_vos=on&active=on&active_value=1&datasource=summary";
		  System.setProperty("org.xml.sax.driver","org.xmlpull.v1.sax2.Driver");
		  try {
			  XMLReader myReader = XMLReaderFactory.createXMLReader();
			  myReader.setContentHandler(osg_parser);
			  myReader.parse(new InputSource(new URL(url).openStream()));
		  } catch (Exception e) {
			  System.err.println(e.getMessage());
		  }
		  vos = osg_parser.GetSites();
		  BufferedWriter out = this.GetOutputCacheFile(OSGMonitoringActivity.VO_CACHE_NAME);
		  try{
			  if (out != null) {
				  for (int i = 0; i < vos.length; i++) {
					  out.write(vos[i] + "\n");
				  }
				  out.flush();
			  }
		  } catch (Exception ex) {
			  System.err.println(ex.getMessage());

		  }
		  return vos;

	  }

	  
	  private BufferedReader GetInputCacheFile(String cache_file_name) {
		  File tmp_file = new File(this.getCacheDir().getAbsolutePath() + "/" + cache_file_name);
		  BufferedReader toReturn = null;
		  try {
			  toReturn = new BufferedReader(new InputStreamReader(new FileInputStream(tmp_file)));
		  } catch (Exception ex) {
			  
		  }
		  return toReturn;
	  }

	  private BufferedWriter GetOutputCacheFile(String cache_file_name) {
		  File tmp_file = new File(this.getCacheDir().getAbsolutePath() + "/" + cache_file_name);
		  
		  BufferedWriter toReturn = null;
		  try {
			  tmp_file.createNewFile();
			  toReturn = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmp_file)));
		  } catch (Exception ex) {
			  
		  }
		  return toReturn;
	  }
	  
}

	

