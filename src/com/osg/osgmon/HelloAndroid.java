package com.osg.osgmon;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

import com.osg.osgmon.map.OSGMapView;
import com.osg.osgmon.monitoring.OSGMonitoringActivity;

public class HelloAndroid extends Activity implements OnClickListener {
	
	public OSGMonitoringActivity osg_monitoring;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	this.osg_monitoring = new OSGMonitoringActivity();
    	
    	super.onCreate(savedInstanceState);
    	
        
        setContentView(R.layout.main);
        
        
        WebView status = (WebView) findViewById(R.id.webView1);
        status.setBackgroundColor(Color.BLACK);
        //String jobs_web = "<html bgcolor=\"black\"><body bgcolor=\"black\"><font color=\"white\">Fake data <p/>Jobs running: 24,000</font></body></html>";

        
        
        // Check if we're on a tablet
        // NOTE: Configuration class doesn't have xlarge for Android 2.2
        	String status_html = this.CreateStatusDisplay();
        	status.loadData(status_html, "text/html", "utf-8");

        
        
    	Button monitoring_button = (Button)findViewById(R.id.view_monitoring);
    	monitoring_button.setOnClickListener(this);
    	
    	Button map_button = (Button)findViewById(R.id.view_map);
    	map_button.setOnClickListener(this);
    	
    }


	public void onClick(View v) {
		if (v.getId()  == R.id.view_map) {
			Intent osg_map_intent = new Intent(v.getContext(), OSGMapView.class);
			v.getContext().startActivity(osg_map_intent);
			
		} else if (v.getId() == R.id.view_monitoring) {
			
			Intent osg_monitoring_intent = new Intent(v.getContext(), OSGMonitoringActivity.class);
			v.getContext().startActivity(osg_monitoring_intent);
			
		}
		
	}
	
	protected String CreateStatusDisplay() {
		// First, get json
		URL json_url = null;
		BufferedReader in = null;
		JSONParser parser=new JSONParser();
		String line_buf = "";
		String buf = "";
		String html_src = "";
		
		

		try {
			json_url = new URL("http://display1.grid.iu.edu/osg_display/display.json");
			//urlConnection = json_url.openConnection();
			json_url.openConnection();
			in = new BufferedReader(new InputStreamReader(json_url.openStream()));

			while ((line_buf = in.readLine()) != null) {
				buf += line_buf;

			}
			buf = buf.replace("'", "\"");
			JSONObject obj = (JSONObject)parser.parse(buf);
			Double cpu_hours_daily = (Double)obj.get("cpu_hours_hourly");
			long transfer_mb_daily = (Long)obj.get("transfer_volume_mb_hourly");
			long jobs_daily = (Long)obj.get("jobs_hourly");
			long transfers_daily = (Long)obj.get("transfers_hourly");

			html_src = "<html bgcolor=\"black\"><head><style type=\"text/css\">body {color:white;}</style></head><body bgcolor=\"black\">";
			DecimalFormat df = new DecimalFormat();
			DecimalFormatSymbols dfs = new DecimalFormatSymbols();
			dfs.setGroupingSeparator(',');
			df.setDecimalFormatSymbols(dfs);

			html_src += "<table><tr><td colspan=2><h3>In the last 24 Hours</h3></td></tr>";
			html_src += "<tr><td align=\"right\">" + df.format(jobs_daily) + "</td><td>Jobs</td></tr>";
			html_src += "<tr><td align=\"right\">" + df.format(cpu_hours_daily.longValue()) + "</td><td>CPU Hours</td></tr>";
			html_src += "<tr><td align=\"right\">" + df.format(transfers_daily) + "</td><td>Transfers</td></tr>";
			html_src += "<tr><td align=\"right\">" + df.format((transfer_mb_daily/(1024*1024))) + "</td><td>TBs Transferred</td></tr>";
			html_src += "</table>";
			html_src += "</body></html>";



		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		return html_src;

		
		
	}
    
}