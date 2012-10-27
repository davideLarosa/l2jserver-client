package com.l2client.animsystem.jme;

import com.jme3.animation.Skeleton;
import com.jme3.animation.SkeletonControl;

public class TimedSkeletonControl extends SkeletonControl {
	/**
	 * How many times per second will we be updated at all?
	 */
	protected static final float MAX_UPDATE_DELTA = 1.0f/30.0f;
	/**
	 * Accumulated delta time until MAX_UPDATE_DELTA is reached where it should be rest to 0f
	 */
	protected float lastUpdate = 0.0f;
	
    public TimedSkeletonControl(Skeleton skel) {
		super(skel);
	}

	@Override
    protected void controlUpdate(float tpf) {
       lastUpdate += tpf;
       if(lastUpdate > MAX_UPDATE_DELTA){
    	   super.controlUpdate(lastUpdate);
    	   lastUpdate = 0f;
       }
    }

}
