package com.l2client.animsystem;

import java.util.Random;

/**
 * Actions are the place where the whole selection takes place. An actions tries
 * to get a lock on a channel and if gained is allowed to fill an animation
 * system owned animation descriptor. Normally a default action is used and
 * several other animation actions are provided for more important animations or
 * completely different selection algorithms. New Actions should be based on
 * Action.java and only override the evaluate(..) method.
 * 
 */
public abstract class Action {

	protected static Random rand = new Random();

	public Action() {
	}

	protected Animation evaluate(Mediator a) {
		return null;
	}
}
