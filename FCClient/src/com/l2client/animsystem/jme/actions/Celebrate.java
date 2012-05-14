package com.l2client.animsystem.jme.actions;

import com.l2client.animsystem.Action;
import com.l2client.animsystem.Animation;
import com.l2client.animsystem.Mediator;
import com.l2client.animsystem.Channel.Channels;


public class Celebrate extends Action {
	@Override
	protected Animation evaluate(Mediator med){
		Animation ret = null;
		if(med.forceLock(Channels.AllChannels,1)){
			ret = med.getAnimation();
			ret.setLevel(1);
			ret.setChannel(med.getChannel(Channels.AllChannels));
			ret.setBlendTime(0.2f);
			ret.setLooping(false);
			ret.setKeep(1.0f);
			ret.setName("celebrate");
			return ret;
		}
		
		return null;
	}
}
