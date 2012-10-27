package com.l2client.animsystem.jme.actions;

import com.l2client.animsystem.Action;
import com.l2client.animsystem.Animation;
import com.l2client.animsystem.Channel.Channels;
import com.l2client.animsystem.Mediator;
import com.l2client.animsystem.jme.input.Speed;

/**
 * taunt at level 1
 * 
 */
public class StopMove extends Action {
	@Override
	protected Animation evaluate(Mediator med) {
		Animation ret = null;
		log.info("evaluating StopMove");
		if (Speed.None.equals(med.getInput().getInput(Speed.class))) {
			if (med.forceLockCheck(Channels.AllChannels, 2)) {
				ret = med.getAnimation();
				ret.setChannel(med.getChannel(Channels.AllChannels));
				ret.setLevel(1);
				ret.setBlendTime(0f);
				ret.setName("stand_a_idle");
				log.info("StopMove OK");
				return ret;
			}else
				log.info("StopMove forceLock Level 2 FAILED");
		} else
			log.severe("StopMove Speed not None!!, eval FAILED");
		return null;
	}
}
