package com.stuffthathappens.games;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

/**
 * This data model tracks bubbles on the screen.
 * 
 * @see BubblesActivity
 */
public class BubblesModel implements OnCompletionListener {
	
	private static final float INITIAL_RADIUS = 20f;
	private static final float MAX_RADIUS = 100f;
	
	// higher numbers make the balls expand faster
	private static final float RADIUS_CHANGE_PER_MS = .08f;
	
	private final List<MediaPlayer> players = new LinkedList<MediaPlayer>();
	private boolean running = false;
	
	public static final class Bubble {
		float x, y, radius;
		
		public Bubble(float x, float y) {
			this.x = x;
			this.y = y;
			radius = INITIAL_RADIUS;
		}
	}
	
	private final List<Bubble> bubbles = new LinkedList<Bubble>();
	

    private volatile long lastTimeMs = -1;
	
	public final Object LOCK = new Object();
	
	public BubblesModel() {		
	}
	
	public void onResume(Context context) {
		synchronized (LOCK) {
			for (int i=0; i<4; i++) {
				MediaPlayer mp = MediaPlayer.create(context, R.raw.pop);
				mp.setVolume(1f, 1f);
				players.add(mp);
				try {
					mp.setLooping(false);
					mp.setOnCompletionListener(this);
					
					// TODO: there is a serious bug here. After a few seconds of
					// inactivity, we see this in LogCat:
					//   AudioHardwareMSM72xx Going to standby 
					// then the sounds don't play until you click several more
					// times, then it starts working again
					
				} catch (Exception e) {
					e.printStackTrace();
					players.remove(mp);
				}
			}
			running = true;
		}
	}
	
	public void onPause(Context context) {
		synchronized (LOCK) {
			running = false;
			for (MediaPlayer p : players) {
				p.release();
			}
			players.clear();
		}
	}
	
	public List<Bubble> getBubbles() {
		synchronized (LOCK) {
			return new ArrayList<Bubble>(bubbles);
		}
	}
	
	public void addBubble(float x, float y) {
		synchronized (LOCK) {
			bubbles.add(new Bubble(x,y));
		}
	}
	
	public void setSize(int width, int height) {
		// TODO ignore this for now...we could hide bubbles that
		// are out of bounds, for example
	}

    public void updateBubbles() {
        long curTime = System.currentTimeMillis();
        if (lastTimeMs < 0) {
            lastTimeMs = curTime;
            // this is the first reading, so don't change anything
            return;
        }
        long elapsedMs = curTime - lastTimeMs;
        lastTimeMs = curTime;
        
        final float radiusChange = elapsedMs * RADIUS_CHANGE_PER_MS;

        MediaPlayer mp = null;

    	synchronized (LOCK) {
    		Set<Bubble> victims = new HashSet<Bubble>();
    		
    		for (Bubble b : bubbles) {
    			b.radius += radiusChange;
    			if (b.radius > MAX_RADIUS) {
    				victims.add(b);
    			}
    		}
    		
    		if (victims.size() > 0) {
    			bubbles.removeAll(victims);
    			// since a bubble popped, try to get a media player
    			if (!players.isEmpty()) {    				
    				mp = players.remove(0);
    			}
    		}
    	}
    	
    	if (mp != null) {
    		//System.out.println("**pop**");
    		mp.start(); 
    	}
    }

	public void onCompletion(MediaPlayer mp) {
		synchronized (LOCK) {
			if (running) {
	    		mp.seekTo(0);
				//System.out.println("on completion!");
	    		// return the player to the pool of available instances
				players.add(mp);
			}
		}
	}
}
