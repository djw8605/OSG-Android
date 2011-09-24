package com.osg.osgmon;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;


public class StackedXYMultipleSeries extends XYMultipleSeriesDataset {

	
	protected XYSeries last_series = null;
	
	public StackedXYMultipleSeries() {
		super();
	}
	
	public synchronized void addSeries(XYSeries series) {
	    XYSeries adjusted_series = new XYSeries(series.getTitle());
	    
	    if (last_series == null) {
	    	last_series = series;
	    	super.addSeries(series);
	    	return;
	    }
	    
	    for (int i = 0; i < series.getItemCount(); i++) {
	    	// Find a last_series with same X
	    	if (last_series.getX(i) == series.getX(i)) {
	    		adjusted_series.add(series.getX(i), last_series.getY(i) + series.getY(i));
	    	} else {
	    		// Start looking for the X's that surround the series' X
	    		int looking_counter = 0;
	    		while(looking_counter < last_series.getItemCount() && last_series.getX(looking_counter) < series.getX(i))
	    			looking_counter++;
	    		
	    		if (looking_counter > last_series.getItemCount())
	    			adjusted_series.add(series.getX(i), last_series.getY(last_series.getItemCount()-1) + series.getY(i));
	    		else {
	    			double average_addition = (last_series.getY(looking_counter) + last_series.getY(looking_counter-1)) / 2;
	    			adjusted_series.add(series.getX(i), average_addition + series.getX(i));
	    		}
	    	}
	    }
	    last_series = adjusted_series;
	    super.addSeries(adjusted_series);
		
	  }
	
}
