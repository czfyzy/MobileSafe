package com.zengye.mobilesafe.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.zengye.mobilesafe.utils.DensityUtils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

/**
 * 水波纹效果
 * 
 * @author leo
 * 
 */
public class MyRingWave extends View {

	protected boolean isRunning = false;

	private List<Wave> wList;
	private Context context;

	public MyRingWave(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		wList = Collections.synchronizedList(new ArrayList<MyRingWave.Wave>());
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			addPoint(centerX, centerY);
			// 刷新数据
			flushData();
			// 刷新页面
			invalidate();
			// 循环动画
			if (isRunning) {
				handler.sendEmptyMessageDelayed(0, 5);
				addPoint(centerX, centerY);
			}

		};
	};

	@Override
	protected void onDraw(Canvas canvas) {
		for (int i = 0; i < wList.size(); i++) {
			Wave wave = wList.get(i);
			if(wave.cx == 0 && wave.cy == 0) {
				continue;
			}
			canvas.drawCircle(wave.cx, wave.cy, wave.r, wave.p);
		}
	}

	// @Override
	// public boolean onTouchEvent(MotionEvent event) {
	// super.onTouchEvent(event);
	//
	// switch (event.getAction()) {
	// case MotionEvent.ACTION_DOWN:
	// case MotionEvent.ACTION_MOVE:
	//
	// int x = (int) event.getX();
	// int y = (int) event.getY();
	//
	// addPoint(x,y);
	//
	// break;
	//
	// default:
	// break;
	// }
	//
	// return true;
	//
	// }

	/**
	 * 添加新的波浪中心点
	 * 
	 * @param x
	 * @param y
	 */
	private void addPoint(int x, int y) {
		if (wList.size() == 0) {
			/*
			 * 第一次启动动画
			 */
			isRunning = true;
			handler.sendEmptyMessage(50);
			addPoint2List(x, y);
		}
	}

	
	/**
	 * 添加新的波浪
	 * 
	 * @param x
	 * @param y
	 */
	private void addPoint2List(int x, int y) {
		Wave w = new Wave();
		w.cx = x;
		w.cy = y;
		Paint pa = new Paint();
		pa.setColor(Color.WHITE);
		pa.setAntiAlias(true);
		pa.setStyle(Style.STROKE);
	    pa.setAlpha(200);

		w.p = pa;

		wList.add(w);
	}

	private int centerX;

	private int centerY;

	private Timer timer;

	private TimerTask task;

	/**
	 * 刷新数据
	 */
	private void flushData() {

		for (int i = 0; i < wList.size(); i++) {

			Wave w = wList.get(i);

			// 如果透明度为 0 从集合中删除
			int alpha = w.p.getAlpha();
			if (alpha == 0) {
				wList.remove(i); // 删除i 以后，i的值应该再减1
									// 否则会漏掉一个对象，不过，在此处影响不大，效果上看不出来。
				continue;
			}

			alpha -= 1;
			if (alpha < 5) {
				alpha = 0;
			}
			// 降低透明度
			w.p.setAlpha(alpha);

			// 扩大半径
			w.r = w.r + 0.3f;
			// 设置半径厚度
			w.p.setStrokeWidth(DensityUtils.dip2px(context, 1));
		}

		/*
		 * 如果集合被清空，就停止刷新动画
		 */
		if (wList.size() == 0) {
			isRunning = false;
		}
	}

	/**
	 * 定义一个波浪
	 * 
	 * @author leo
	 */
	private class Wave {
		// 圆心
		int cx;
		int cy;

		// 画笔
		Paint p;
		// 半径
		float r = DensityUtils.dip2px(context, 80);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {

		centerX = w / 2;
		centerY = h / 2;
	
	}
	
	
	
	public void ringRun() {
		timer = new Timer();
		
		task = new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				addPoint2List(centerX, centerY);
			}
		};
		addPoint(centerX, centerY);
		timer.schedule(task, 200, 2500);
		
	}
	
	public void cancel() {
		timer.cancel();
		task.cancel();
		timer = null;
		task = null;
	}
}
