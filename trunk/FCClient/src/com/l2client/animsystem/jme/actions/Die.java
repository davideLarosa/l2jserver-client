package com.l2client.animsystem.jme.actions;

import com.l2client.animsystem.Action;
import com.l2client.animsystem.Animation;
import com.l2client.animsystem.Channel.Channels;
import com.l2client.animsystem.InputProvider;
import com.l2client.animsystem.Mediator;
import com.l2client.animsystem.jme.input.HurtVector;

/**
 * die at level 5
 * 
 */
public class Die extends Action {
	@Override
	protected Animation evaluate(Mediator med) {
		Animation ret = null;

			if (med.forceLockCheck(Channels.AllChannels, 5)) {
				ret = med.getAnimation();
				ret.setChannel(med.getChannel(Channels.AllChannels));
				ret.setLevel(5);
				ret.setBlendTime(0.1f);
				ret.setLooping(false);
				ret.setKeep(432000f);//we want him to lay down reaaalllly long
				ret.setName(getDeathAnimation(med.getInput()));
				log.info("Die:->"+ret.getName());
				return ret;
			}
		return null;
	}

	private String getDeathAnimation(InputProvider input) {
		switch(input.getInput(HurtVector.class)){
		case None:
		case Left:	return "die_to_back_right";
		case Right:	return "die_to_back_left";
		case Back:	return "die_backward";
		case Front: 
		default:	return "die_forward";
		}
	}
}
