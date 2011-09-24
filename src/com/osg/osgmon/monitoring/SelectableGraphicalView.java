package com.osg.osgmon.monitoring;

import org.achartengine.GraphicalView;
import org.achartengine.chart.AbstractChart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;

public class SelectableGraphicalView extends GraphicalView {

	protected float selectedX = -1;
	protected float selectedY = -1;
	
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

		return true;
	}

}
