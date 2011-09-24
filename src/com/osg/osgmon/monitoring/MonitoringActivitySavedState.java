package com.osg.osgmon.monitoring;

import org.achartengine.model.XYMultipleSeriesDataset;

import android.widget.ListAdapter;
import android.widget.SpinnerAdapter;

public class MonitoringActivitySavedState {

	public MonitoringActivitySavedState() {
		
		
	}
	
	protected boolean sliderOpen = false;
	public void setSliderOpen(boolean sliderOpen) {
		this.sliderOpen = sliderOpen;
	}
	public boolean getSliderOpen() {
		return this.sliderOpen;
	}
	
	
	protected ListAdapter sites_adapter;
	public void setSitesAdapter(ListAdapter sites_adapter) {
		this.sites_adapter = sites_adapter;
	}
	public ListAdapter getSitesAdapter() {
		return this.sites_adapter;
	}
	
	protected String sites_text;
	public void setSitesText(String sites_text) {
		this.sites_text = sites_text;
	}
	public String getSitesText() {
		return this.sites_text;
	}
	
	
	protected SpinnerAdapter vos_adapter;
	public void setVOsAdapter(SpinnerAdapter vos_adapter) {
		this.vos_adapter = vos_adapter;
	}
	public SpinnerAdapter getVOsAdapter() {
		return this.vos_adapter;
	}
	
	protected int vo_selected;
	public void setVOsSelected(int vo_selected) {
		this.vo_selected = vo_selected;
	}
	public int getVOsSelected() {
		return this.vo_selected;
	}
	
	protected XYMultipleSeriesDataset chart_data;
	public void setChartData(XYMultipleSeriesDataset chart_data) {
		this.chart_data = chart_data;
	}
	public XYMultipleSeriesDataset getChartData() {
		return this.chart_data;
	}
	
}
