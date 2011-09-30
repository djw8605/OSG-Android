package com.osg.osgmon.monitoring;

import org.achartengine.GraphicalView;
import org.achartengine.chart.AbstractChart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.osg.osgmon.R;

public class SelectableGraphicalView extends GraphicalView {

	protected float selectedX = -1;
	protected float selectedY = -1;
	
	public final static int GRAPH_LEGEND_ID = 100;
	
	public SelectableGraphicalView(Context arg0, AbstractChart arg1) {
		super(arg0, arg1);
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
		
		selectedX = event.getX();
	    selectedY = event.getY();
		this.invalidate();
		
		
		LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup v = (ViewGroup) vi.inflate(R.layout.graphlegend, null);
		v.setId(R.layout.graphlegend);
		// fill in any details dynamically here
		TextView textView = new TextView(this.getContext());
		textView.setText("your text");
		v.addView(textView);

		// insert into main view
		View parent = (View) this.getParent().getParent();
		View insertPoint = parent.findViewById(R.id.osgmonitoring);
		
		((ViewGroup) insertPoint).removeView(parent.findViewById(R.layout.graphlegend));
		((ViewGroup) insertPoint).addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		v.bringToFront();

		
		v.setX(selectedX - 100);
		v.setY(selectedY);
		

		return true;
	}

}
