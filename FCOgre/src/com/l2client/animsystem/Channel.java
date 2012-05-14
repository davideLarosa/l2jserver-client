package com.l2client.animsystem;

import java.util.logging.Logger;

/**
 * The animation system was designed for multi channel animations. For example
 * you could differentiate between upper body, lower body, left arm, right arm,
 * etc. If you would need different channels tha the ones provided, the change
 * Channel.java A Channel holds a lock for a level for a set time where no other
 * animation of the same level can overide the lock easily.
 * 
 */
public final class Channel {

	private static Logger log = Logger.getLogger(Channel.class.getName());

	/**
	 * The animation channels available, add any channels which should basically
	 * be available for usage in actions
	 */
	public static enum Channels {

		AllChannels, UpperBody, LowerBody, LeftArm, RightArm, LeftLeg, RightLeg, Head;
	};

	private float bindingTime = 0.0f;
	private int bindingLevel = 0;
	private Animation boundAnimation = null;
	// TODO this should be an array, otherwise not usefull
	private Channels usedChannel = Channels.AllChannels;
	private Animation nextAnimation = null;

	public Channel(Channels c) {
		usedChannel = c;
	}

	// if time >0 count down binding time if time reaches 0 set binding level to
	// 0
	public void update(float dTime /* delta time */) {
		if (bindingTime >= 0.0f) {
			bindingTime -= dTime;
		} else {
			if (bindingTime != 0.0f && boundAnimation != null) {
				bindingTime = 0.0f;
				if (nextAnimation != null) {
					log.fine("found next animation:" + nextAnimation.getName());
					setAnimation(nextAnimation, null);
				} else
					bindingLevel = 0;
			}
		}
	}

	// only works if new binding level > current, returns true/false , uses
	// assign if really done
	public boolean setLock(int level) {
		if (level > bindingLevel) {
			bindingLevel = level;
			bindingTime = 0.0f;
			log.fine("SetLock to:" + level);
			return true;
		} else {
			return false;
		}
	}

	// only works if new binding level >= current, returns true/false , uses
	// assign if really done
	public boolean forceLock(int level) {
		if (level >= bindingLevel) {
			bindingLevel = level;
			bindingTime = 0.0f;
			log.fine("ForceLock to:" + level);
			return true;
		} else {
			return false;
		}
	}

	// should be used when set/force was successful
	public void setAnimation(Animation anim, Animation transition) {
		if (anim != null) {
			if (transition != null) {
				this.boundAnimation = transition;
				this.nextAnimation = anim;
			} else {
				this.boundAnimation = anim;
				this.nextAnimation = null;
			}
			this.boundAnimation.setInternalAnimation();
//			log.fine("setAnimation to:" + boundAnimation.getName() + " length:"
//					+ boundAnimation.getAnimationLength() + " blend:"
//					+ boundAnimation.getBlendTime());
			this.bindingLevel = boundAnimation.getLevel();
			this.bindingTime = boundAnimation.getAnimationLength();

		}
	}

	public String getCurrentAnimation() {
		if (boundAnimation != null)
			return boundAnimation.getName();
		else
			return "";
	}

}
