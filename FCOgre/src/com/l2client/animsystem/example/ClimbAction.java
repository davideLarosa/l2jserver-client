package com.l2client.animsystem.example;

import com.l2client.animsystem.Action;
import com.l2client.animsystem.Animation;
import com.l2client.animsystem.Mediator;
import com.l2client.animsystem.Channel.Channels;

public class ClimbAction extends Action {

	public ClimbAction() {
	}
	
	@Override
	protected Animation evaluate(Mediator med){
		Animation ret = null;
		if(med.setLock(Channels.AllChannels,1)){
			ret = med.getAnimation();
			ret.setLevel(1);
			ret.setChannel(med.getChannel(Channels.AllChannels));
			ret.setName("Climb");
			ret.setBlendTime(1f);
		}
		return ret;
	}
}