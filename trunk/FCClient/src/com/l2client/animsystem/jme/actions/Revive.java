package com.l2client.animsystem.jme.actions;

import com.l2client.animsystem.Action;
import com.l2client.animsystem.Animation;
import com.l2client.animsystem.Channel.Channels;
import com.l2client.animsystem.Mediator;

/**
 * revive at level 5
 * 
 */
public class Revive extends Action {
	@Override
	protected Animation evaluate(Mediator med) {
		Animation ret = null;
		if (med.forceLockCheck(Channels.AllChannels, 5)) {
			ret = med.getAnimation();
			ret.setChannel(med.getChannel(Channels.AllChannels));
			ret.setLevel(5);
			ret.setBlendTime(0.3f);
			ret.setLooping(false);
			ret.setKeep(0.5f);
			ret.setName("stand_a_idle");
		}
		return ret;
	}
}
