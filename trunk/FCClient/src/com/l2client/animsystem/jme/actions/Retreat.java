package com.l2client.animsystem.jme.actions;

import com.l2client.animsystem.Action;
import com.l2client.animsystem.Animation;
import com.l2client.animsystem.Channel.Channels;
import com.l2client.animsystem.Mediator;

/**
 * retreat at level 2
 */
public class Retreat extends Action {
	@Override
	protected Animation evaluate(Mediator med){

		Animation ret = null;
		if(med.forceLockCheck(Channels.AllChannels,2)){
			ret = med.getAnimation();
			ret.setChannel(med.getChannel(Channels.AllChannels));
			ret.setLevel(2);
			ret.setBlendTime(0.1f);
			ret.setName("retreat");
			return ret;
		}
		
		return null;
	}
}
