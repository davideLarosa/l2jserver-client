package com.l2client.animsystem;

/**
 * IAnimationProvider.java is used to bridge the low level implementation with
 * the animation system. It is here where the low level animation should be set,
 * where low level internal animations are created/looked up/loaded on
 * demand/fetched and provide information like the animation length. 
 */
public interface IAnimationProvider {

	Animation createAnimation();

	void setInternalAnimation(Animation a);
}
