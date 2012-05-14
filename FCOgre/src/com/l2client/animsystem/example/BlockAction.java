package com.l2client.animsystem.example;

import com.l2client.animsystem.Action;
import com.l2client.animsystem.Animation;
import com.l2client.animsystem.Mediator;
import com.l2client.animsystem.Channel.Channels;

public class BlockAction extends Action {
	@Override
	protected Animation evaluate(Mediator med){
		Animation ret = null;
		if(med.setLock(Channels.AllChannels,2)){
			ret = med.getAnimation();
			ret.setLevel(2);
			ret.setChannel(med.getChannel(Channels.AllChannels));
			ret.setName("Block");
			ret.setBlendTime(0.2f);
		}
		return ret;
	}
}