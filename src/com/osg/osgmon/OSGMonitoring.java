package com.osg.osgmon;

import java.net.URL;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.osg.osgmon.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

public class OSGMonitoring extends Activity implements OnClickListener, Runnable {
	
	// Graph activity
	private OSGSiteUsage osg_site_usage = null;
	
	// Pointer to self
	// Used mostly in the message
	private Activity osg_monitoring = this;
	
	public OSGMonitoring() {
		osg_site_usage = new OSGSiteUsage(this);
		
		
	}
	
	private AutoCompleteTextView auto_textview = null;
	private AutoCompleteTextView vo_autotext = null;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.osg_monitoring);
		
		StartProgressDialog("Loading Sites...");
		
		this.auto_textview = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
		this.auto_textview.setThreshold(3);
		
		this.vo_autotext =  (AutoCompleteTextView) findViewById(R.id.vo_auto_complete);
		this.vo_autotext.setThreshold(2);
		
		Thread sites_thread = new Thread(this);
		sites_thread.start();
		
		Button get_usage_button = (Button) findViewById(R.id.confirm_site);
		get_usage_button.setOnClickListener(this.osg_site_usage);
		
		//lc.drawSeries(draw_canvas, paint_canvas, stuff, new XYSeriesRenderer(), (float)15.0, 0);
		
		
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
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(osg_monitoring, R.layout.list_item2, vo_names);
				vo_autotext.setAdapter(adapter);
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

	  private String [] GetVOs() {
		  OSGSiteXMLParser osg_parser = new OSGSiteXMLParser();

		  String url = "http://myosg.grid.iu.edu/vosummary/xml?all_vos=on&active=on&active_value=1&datasource=summary";

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

	

