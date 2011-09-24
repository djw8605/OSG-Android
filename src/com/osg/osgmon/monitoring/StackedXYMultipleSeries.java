package com.osg.osgmon.monitoring;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;


public class StackedXYMultipleSeries extends XYMultipleSeriesDataset {

	
	protected XYSeries last_series = null;
	protected boolean changed_since_sort = true;
	protected List<XYSeries> mtmp_series = new ArrayList<XYSeries>();
	protected int mSize = Integer.MAX_VALUE;
	protected boolean showOther = false;
	
	public StackedXYMultipleSeries() {
		super();
	}
	
	public synchronized void addSeries(XYSeries series) {
		this.changed_since_sort = true;
		this.mtmp_series.add(series);
	}
	
	public void setShowSize(int size) {
		this.mSize = size;
	}
	
	public void setShowOther(boolean showOther) {
		this.showOther = showOther;
	}
	
	public void StackSeries(XYSeries series) {
		
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
	
	/**
	 * Overload the getSeries to return only what we should, in the right order.
	 * @return Ordered, stacked series
	 * @overload
	 */
	public synchronized XYSeries[] getSeries() {
		// Calculate the order
		if (!this.changed_since_sort)
			return super.getSeries();
		
		// Deep copy the series
		ArrayList<XYSeries> series_copy = new ArrayList<XYSeries>(this.mtmp_series);

		// Sort the series
		Collections.sort(series_copy, new XYStackedComparator());

		// Remove extra and add to 'other' series
		XYSeries other_series = null;
		for (int i = 0; series_copy.size() > Math.min(series_copy.size(), this.mSize); i++) {
			if (this.showOther) {
				if (other_series == null) {
					other_series = series_copy.get(0);
					other_series.setTitle("Other");
				} else {
					XYSeries tmp_xy = series_copy.get(0);
					XYSeries sum_xy = new XYSeries("Other");
					for (int a = 0; a < tmp_xy.getItemCount(); a++)
						sum_xy.add(tmp_xy.getX(a), tmp_xy.getY(a) + other_series.getY(a));
					other_series = sum_xy;
				
				}  // End if other_series == null
			}  // End if showOther
			
			series_copy.remove(0);
		}  // End for
		
		series_copy.add(other_series);
		
		// Remove all the current series
		for (int i = 0; i < super.getSeriesCount(); i++)
			this.removeSeries(0);
		
		// Stack all the series (will go into super series)
		for (int i = 0; i < series_copy.size(); i++)
			this.StackSeries(series_copy.get(i));
		
		this.changed_since_sort = false;
		return super.getSeries();


	}



	public synchronized XYSeries getSeriesAt(int index) {
		if(this.changed_since_sort) {
			this.getSeries();
		}
		return super.getSeriesAt(index);
	}

	
	public synchronized int getSeriesCount() {
		if (this.showOther) {
			return Math.min(this.mSize+1, this.mtmp_series.size());
		} else {
			return Math.min(this.mSize, this.mtmp_series.size());
		}
	}


	public class XYStackedComparator implements Comparator<XYSeries> {
		public int compare(XYSeries o1, XYSeries o2) {
			return Double.compare(o1.getMaxY(), o2.getMaxY());
		}
	}

}
