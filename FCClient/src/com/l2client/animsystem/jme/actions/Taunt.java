package com.l2client.animsystem.jme.actions;

import com.l2client.animsystem.Action;
import com.l2client.animsystem.Animation;
import com.l2client.animsystem.Mediator;
import com.l2client.animsystem.Channel.Channels;

public class Taunt extends Action {
	@Override
	protected Animation evaluate(Mediator med){
		Animation ret = null;
		if(med.forceLock(Channels.AllChannels,1)){
			ret = med.getAnimation();
			ret.setChannel(med.getChannel(Channels.AllChannels));
			ret.setLevel(1);
			ret.setBlendTime(0.2f);
			ret.setLooping(false);
			ret.setKeep(1.0f);
			ret.setName("taunt");
			return ret;
		}
		return null;
	}
}
