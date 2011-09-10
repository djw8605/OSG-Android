package com.example.helloandroid;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

public class HelloAndroid extends Activity {
	
	public OSGMonitoring osg_monitoring;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	this.osg_monitoring = new OSGMonitoring();
    	
    	super.onCreate(savedInstanceState);
    	
        
        setContentView(R.layout.main);
        
        
        
    	Button monitoring_button = (Button)findViewById(R.id.view_monitoring);
    	monitoring_button.setOnClickListener(this.osg_monitoring);
    }
    
    
    
}