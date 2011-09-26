package com.osg.osgmon.monitoring;

import org.achartengine.chart.TimeChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.MotionEvent;
import android.view.View;

public class StackedTimeChart extends TimeChart {

	protected XYMultipleSeriesDataset series_dataset = null;
	
	public StackedTimeChart(XYMultipleSeriesDataset arg0,
			XYMultipleSeriesRenderer arg1) {
		super(arg0, arg1);
		series_dataset = arg0;
		// TODO Auto-generated constructor stub
	}
	
	protected float[] previous_floats = null;
	
	@Override
	public void drawSeries(Canvas canvas, Paint paint, float[] points,
			SimpleSeriesRenderer seriesRenderer, float yAxisValue, int seriesIndex) {

		int length = points.length;
		XYSeriesRenderer renderer = (XYSeriesRenderer) seriesRenderer;
		float lineWidth = paint.getStrokeWidth();
		paint.setStrokeWidth(renderer.getLineWidth());
		if (renderer.isFillBelowLine()) {
			paint.setColor(renderer.getFillBelowLineColor());
			int pLength = points.length;
			float[] fillPoints = null;
			if (seriesIndex == 0) {
				previous_floats = points;
				fillPoints = new float[pLength + 4];
				System.arraycopy(points, 0, fillPoints, 0, length);
				fillPoints[0] = points[0] + 1;
				fillPoints[length] = fillPoints[length - 2];
				fillPoints[length + 1] = yAxisValue;
				fillPoints[length + 2] = fillPoints[0];
				fillPoints[length + 3] = fillPoints[length + 1];
			} else {
				fillPoints = new float[length*2];
				StackedTimeChart.reverse(previous_floats);
				System.arraycopy(points, 0, fillPoints, 0, length);
				System.arraycopy(previous_floats, 0, fillPoints, length, length);

			}
			previous_floats = points;
			paint.setStyle(Style.FILL);
			drawPath(canvas, fillPoints, paint, true);
		}
		paint.setColor(seriesRenderer.getColor());
		paint.setStyle(Style.STROKE);
		drawPath(canvas, points, paint, false);
		paint.setStrokeWidth(lineWidth);

	}
	
	public void getStackedPrevious(int seriesIndex) {
		
		
	}

	
	public static void reverse(float[] b) {
		   int left  = 0;          // index of leftmost element
		   int right = b.length-2; // index of rightmost element
		  
		   while (left < right) {
		      // exchange the left and right elements
		      float temp1 = b[left]; 
		      float temp2 = b[left+1];
		      b[left]  = b[right];
		      b[left+1] = b[right+1];
		      b[right] = temp1;
		      b[right+1] = temp2;
		     
		      // move the bounds toward the center
		      left+=2;
		      right-=2;
		   }
		}
	
	
	
	
}

