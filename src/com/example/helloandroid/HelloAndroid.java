package com.example.helloandroid;

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
        String jobs_web = "<html bgcolor=\"black\"><body bgcolor=\"black\"><font color=\"white\">Fake data <p/>Jobs running: 24,000</font></body></html>";
		

		status.loadData(jobs_web, "text/html", "utf-8");
			
        
        //status.loadData(jobs_web, "text/html", "utf-8");
        //status.getSettings().setJavaScriptEnabled(true);
        //status.loadUrl("http://display.grid.iu.edu/osg_display/jobs_hourly.jpg");
        
    	Button monitoring_button = (Button)findViewById(R.id.view_monitoring);
    	monitoring_button.setOnClickListener(this.osg_monitoring);
    }


	public void onClick(View v) {
		if (v.getId()  == R.id.view_map) {
			Intent osg_map_intent = new Intent(v.getContext(), OSGMapView.class);
			v.getContext().startActivity(osg_map_intent);
			
		}
		
	}
    
}