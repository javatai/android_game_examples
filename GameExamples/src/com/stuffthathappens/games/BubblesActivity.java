package com.stuffthathappens.games;

import java.util.List;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnTouchListener;

/**
 * When you tap the screen, bubbles appear on the screen. They expand and eventually pop.
 */
public class BubblesActivity extends Activity implements Callback, OnTouchListener {
	private SurfaceView surface;
	private SurfaceHolder holder;
	private final BubblesModel model = new BubblesModel();
	private GameLoop gameLoop;
	private Paint backgroundPaint;
	private Paint bubblePaint;

	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	setContentView(R.layout.bubbles);
    	
    	surface = (SurfaceView) findViewById(R.id.bubbles_surface);
    	holder = surface.getHolder();
    	surface.getHolder().addCallback(this);
    	
    	backgroundPaint = new Paint();
		backgroundPaint.setColor(Color.BLUE);

		bubblePaint = new Paint();
		bubblePaint.setColor(Color.WHITE);
		bubblePaint.setAntiAlias(true);
		
		surface.setOnTouchListener(this);
    }
    
	@Override
	protected void onPause() {
		model.onPause(this);		
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		model.onResume(this);
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		model.setSize(width, height);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		gameLoop = new GameLoop();
		gameLoop.start();
	}
	
	private void draw() {
		// TODO thread safety - the SurfaceView could go away while we are drawing
		
		Canvas c = null;
		try {
			// NOTE: in the LunarLander they don't have any synchronization here,
			// so I guess this is OK. It will return null if the holder is not ready
			c = holder.lockCanvas();
			
			// TODO this needs to synchronize on something
			if (c != null) {
				doDraw(c);
			}
		} finally {
			if (c != null) {
				holder.unlockCanvasAndPost(c);
			}
		}
	}
	
	private void doDraw(Canvas c) {
		int width = c.getWidth();
		int height = c.getHeight();
		c.drawRect(0, 0, width, height, backgroundPaint);
		
		List<BubblesModel.Bubble> bubbles = model.getBubbles();
		for (BubblesModel.Bubble bubble : bubbles) {
			c.drawCircle(bubble.x, bubble.y, bubble.radius, bubblePaint);
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		try {
			model.setSize(0,0);
			gameLoop.safeStop();
		} finally {
			gameLoop = null;
		}
	}
    
	private class GameLoop extends Thread {
		private volatile boolean running = true;
		
		public void run() {
			while (running) {
				try {
					// TODO don't like this hardcoding
					TimeUnit.MILLISECONDS.sleep(5);
					
					draw();
					model.updateBubbles();

				} catch (InterruptedException ie) {
					running = false;
				}
			}
		}
		
		public void safeStop() {
			running = false;
			interrupt();
		}
	}

	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			model.addBubble(event.getX(), event.getY());
			return true;
		}
		return false;
	}


}
