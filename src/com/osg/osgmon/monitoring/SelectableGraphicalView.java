package com.osg.osgmon.monitoring;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.achartengine.GraphicalView;
import org.achartengine.chart.AbstractChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.osg.osgmon.R;

public class SelectableGraphicalView extends GraphicalView {

	protected float selectedX = -1;
	protected float selectedY = -1;
	protected AbstractChart graph_chart;
	
	public final static int GRAPH_LEGEND_ID = 100;
	
	public SelectableGraphicalView(Context arg0, AbstractChart arg1) {
		super(arg0, arg1);
		this.graph_chart = arg1;
		// TODO Auto-generated constructor stub
	}
	
	
	
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint p = new Paint();
		p.setColor(Color.WHITE);
		if (selectedX > 0) {
			canvas.drawLine(selectedX, 0, selectedX, 1000, p);
		}
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		
		//super.onTouchEvent(event);
		
		selectedX = event.getX();
	    selectedY = event.getY();
		this.invalidate();
		
		
		LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup v = (ViewGroup) vi.inflate(R.layout.graphlegend, null);
		v.setId(R.layout.graphlegend);
		// fill in any details dynamically here
		TextView textView = new TextView(this.getContext());
		textView.setPadding(10, 10, 10, 10);
		ScrollView sv = new ScrollView(this.getContext());
		v.addView(sv);
		TableLayout layout = new TableLayout(this.getContext());
		//layout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
		//layout.setOrientation(layout.VERTICAL);
		sv.addView(layout);
		String toset = "";
		
		
		// Doesn't work to get the real point (don't know why)
		// So we have to get screen points, and start extrapolating
		double [] points = new double[2];
		double [] max_points = new double[2];
		XYMultipleSeriesDataset data = ((StackedTimeChart) this.graph_chart).getDataset();
		
		points[0] = data.getSeriesAt(0).getMinX();
		points[1] = 1;
		double [] min_screen_points = ((StackedTimeChart) this.graph_chart).toScreenPoint(points);
		max_points[0] = data.getSeriesAt(0).getMaxX();
		max_points[1] = 1.0;
		double [] max_screen_points = ((StackedTimeChart) this.graph_chart).toScreenPoint(max_points);
		double[] graph_points = new double[2];
		
		graph_points[0] = (((max_points[0] - points[0]) / (max_screen_points[0] - min_screen_points[0])) * (this.selectedX-min_screen_points[0])) + points[0];
		
		ArrayList<View> viewlist = new ArrayList<View>();
		double previous_y = 0;
		for(int i = 0; i < data.getSeriesCount(); i++) {
			XYSeries series = data.getSeriesAt(i);
			String title = series.getTitle();
			int min_distance_index = 0;
			double min_distance = Double.MAX_VALUE;
			for(int a = 0; a < data.getSeriesAt(0).getItemCount(); a++) {
				points[0] = data.getSeriesAt(0).getX(a);
				double tmp_distance = Math.abs(points[0] - graph_points[0]);
				if(tmp_distance < min_distance) {
					min_distance = tmp_distance;
					min_distance_index = a;
				}
			}
			double tmp_value = series.getY(min_distance_index) - previous_y;
			previous_y = series.getY(min_distance_index);
			String value = Integer.toString((int)tmp_value);
			TableRow newrow = new TableRow(this.getContext());
			textView = new TextView(this.getContext());
			textView.setPadding(0, 0, 10, 0);
			textView.setText(title);
			newrow.addView(textView);
			textView = new TextView(this.getContext());
			textView.setText(value);
			textView.setGravity(Gravity.RIGHT);
			newrow.addView(textView);
			viewlist.add(newrow);
			
			
		}
		TextView titleView = new TextView(this.getContext());
		titleView.setText("Graph Legend");
		titleView.setPadding(10, 10, 10, 10);
		titleView.setTypeface(Typeface.create("", Typeface.BOLD));
		layout.addView(titleView);
		
		for(int i = viewlist.size()-1; i >= 0; i--)
			layout.addView(viewlist.get(i));
				
		

		TextView timeView = new TextView(this.getContext());
		Date d = new Date((long)graph_points[0]);
		SimpleDateFormat date_format = new SimpleDateFormat("MM/dd/yy HH:mm aa");
		timeView.setText(date_format.format(d));
		layout.addView(timeView);
		//textView.setText(toset);
		
		
		

		// insert into main view
		View parent = (View) this.getParent().getParent();
		View insertPoint = parent.findViewById(R.id.osgmonitoring);
		
		((ViewGroup) insertPoint).removeView(parent.findViewById(R.layout.graphlegend));
		//AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(params);//ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		((ViewGroup) insertPoint).addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		v.bringToFront();

		Display display = ((WindowManager) this.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int height = display.getHeight();
		//LayoutParams params = mLayout.generateLayoutParams();
		
		//v.setX(40);
		//v.setY(10);
		

		return true;
	}

}
