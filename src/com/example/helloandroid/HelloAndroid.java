package com.example.helloandroid;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;

public class HelloAndroid extends Activity implements OnClickListener {
	
	public OSGMonitoring osg_monitoring;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	this.osg_monitoring = new OSGMonitoring();
    	
    	super.onCreate(savedInstanceState);
    	
        
        setContentView(R.layout.main);
        
        WebView status = (WebView) findViewById(R.id.webView1);
        status.setBackgroundColor(Color.BLACK);
        //String jobs_web = "<html bgcolor=\"black\"><body bgcolor=\"black\"><font color=\"white\">Fake data <p/>Jobs running: 24,000</font></body></html>";
		

		
		String status_html = this.CreateStatusDisplay();
        
		status.loadData(status_html, "text/html", "utf-8");
        //status.loadData(jobs_web, "text/html", "utf-8");
        //status.getSettings().setJavaScriptEnabled(true);
        //status.loadUrl("http://display.grid.iu.edu/osg_display/jobs_hourly.jpg");
        
    	Button monitoring_button = (Button)findViewById(R.id.view_monitoring);
    	monitoring_button.setOnClickListener(this.osg_monitoring);
    	
    	Button map_button = (Button)findViewById(R.id.view_map);
    	map_button.setOnClickListener(this);
    	
    }


	public void onClick(View v) {
		if (v.getId()  == R.id.view_map) {
			Intent osg_map_intent = new Intent(v.getContext(), OSGMapView.class);
			v.getContext().startActivity(osg_map_intent);
			
		}
		
	}
	
	protected String CreateStatusDisplay() {
		// First, get json
		URL json_url = null;
		URLConnection urlConnection = null;
		BufferedReader in = null;
		JSONParser parser=new JSONParser();
		String line_buf = "";
		String buf = "";
		String html_src = "";

		try {
			json_url = new URL("http://display1.grid.iu.edu/osg_display/display.json");
			urlConnection = json_url.openConnection();
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

			html_src = "<html bgcolor=\"black\"><head><style type=\"text/css\">body {color:white;}</style></head><body bgcolor=\"black\">Live Data:<br/>";
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