package com.l2client.animsystem;


/**
 * An abstract animation system internal animation bean. Should be overridden
 * 
 *
 */
public abstract class Animation {

	/**
	 * animation name,  "" results in no animation (just stay at the last frame?)
	 */
	private String name = "";
	
	/**
	 * blend time in seconds to blend between animations, default 0.05f to prevent glitches with the basepose in jme code
	 */
	private float blendTime = 0.05f;
	/**
	 * animation playback rate used as multiplier to playback speed of animation, default 1.0
	 */
	private float playBackRate = 1.0f; 
	/**
	 * loping playback or onetime playback of animation, default looping
	 */
	private boolean looping = true; 
	/**
	 * binding time of channel, expressd as multiple of playback length, default 1 which means animation length. 0 would be no time, 
	 * but one frame is guaranteed
	 */
	private float keep = 1f;

	/**
	 * channel this animation should be played on
	 */
	private Channel channel;	
	
	/**
	 * Locklevel
	 */
	private int level = 0;

	private IAnimationProvider animationProvider;

	public Animation(IAnimationProvider animProvider) {
		this.animationProvider = animProvider;
	}

	/**
	 * Should return an animation used by the real low level system (your 3d engine).
	 * Please override if late loading etc. should be used @see JMEAnimationController
	 */
	public abstract Object getInternalAnimation();
	
	/**
	 * Should return the length of the encapsulated animation or 0f otherwise.
	 * Please override if late loading etc. should be used @see JMEAnimationController
	 */
	public abstract float getAnimationLength();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getBlendTime() {
		return blendTime;
	}

	public void setBlendTime(float blendTime) {
		this.blendTime = blendTime;
	}

	public float getPlayBackRate() {
		return playBackRate;
	}

	public void setPlayBackRate(float playBackRate) {
		this.playBackRate = playBackRate;
	}

	public boolean isLooping() {
		return looping;
	}

	public void setLooping(boolean looping) {
		this.looping = looping;
	}

	public float getKeep() {
		return keep;
	}

	public void setKeep(float keep) {
		this.keep = keep;
	}

	public void setChannel(Channel c) {
		this.channel = c;		
	}

	public Channel getChannel(){
		return this.channel;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getLevel() {
		return level;
	}

	public void setInternalAnimation() {
		this.animationProvider.setInternalAnimation(this);
	}
}
