package com.l2client.animsystem.jme.actions;

/**
 * List of actions which should be called directly @see JMEAnimationController.callAction
 * instead of triggered by the environment
 *
 */
public enum CallActions {

	Celebrate,
	DefaultAttack,
	Defend,
	Taunt,
	Wounded;
}
