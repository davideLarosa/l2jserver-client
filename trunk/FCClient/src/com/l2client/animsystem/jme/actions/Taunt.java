package com.l2client.animsystem.jme.actions;

import com.l2client.animsystem.Action;
import com.l2client.animsystem.Animation;
import com.l2client.animsystem.Channel.Channels;
import com.l2client.animsystem.Mediator;
import com.l2client.animsystem.jme.input.Direction;

/**
 * taunt at level 1
 * 
 */
public class Taunt extends Action {
	@Override
	protected Animation evaluate(Mediator med) {
		Animation ret = null;
		if (!Direction.None.equals(med.getInput().getInput(Direction.class))) {
			if (med.forceLockCheck(Channels.AllChannels, 1)) {
				ret = med.getAnimation();
				ret.setChannel(med.getChannel(Channels.AllChannels));
				ret.setLevel(1);
				ret.setBlendTime(0.2f);
				ret.setLooping(false);
				ret.setName("taunt");
				return ret;
			}
		}
		return null;
	}
}
