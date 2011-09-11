package com.example.helloandroid;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Button;

public class HelloAndroid extends Activity {
	
	public OSGMonitoring osg_monitoring;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	this.osg_monitoring = new OSGMonitoring();
    	
    	super.onCreate(savedInstanceState);
    	
        
        setContentView(R.layout.main);
        
        WebView status = (WebView) findViewById(R.id.webView1);
        
        String jobs_web = "<html><body bgcolor=\"black\"><font color=\"white\">Jobs running: 24,000</font></body></html>";
		

		status.loadData(jobs_web, "text/html", "utf-8");
			
        
        //status.loadData(jobs_web, "text/html", "utf-8");
        //status.getSettings().setJavaScriptEnabled(true);
        //status.loadUrl("http://display.grid.iu.edu/osg_display/jobs_hourly.jpg");
        
    	Button monitoring_button = (Button)findViewById(R.id.view_monitoring);
    	monitoring_button.setOnClickListener(this.osg_monitoring);
    }
    
    
    
}