package com.l2client.animsystem.jme;

import com.jme3.scene.control.AbstractControl;

public abstract class TimedControl extends AbstractControl {
		/**
		 * How many times per second will we be updated at all?
		 */
		protected static final float MAX_UPDATE_DELTA = 1.0f/30.0f;
		/**
		 * Accumulated delta time until MAX_UPDATE_DELTA is reached where it should be rest to 0f
		 */
		protected float lastUpdate = 0.0f;
		
	    public TimedControl() {
		}

	    /**
	     * Do not override but move your code to doTimedControlUpdate
	     */
		@Override
	    protected void controlUpdate(float tpf) {
	       lastUpdate += tpf;
	       if(lastUpdate > MAX_UPDATE_DELTA){
	    	   doTimedControlUpdate(lastUpdate);
	    	   lastUpdate = 0f;
	       }
	    }
		
		/**
		 * do whatever you normally would do during controlUpdate
		 * @param tpf
		 */
		protected abstract void doTimedControlUpdate(float tpf);
	}

