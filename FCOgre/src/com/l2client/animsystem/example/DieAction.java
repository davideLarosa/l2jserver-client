package com.l2client.animsystem.example;

import com.l2client.animsystem.Action;
import com.l2client.animsystem.Animation;
import com.l2client.animsystem.Mediator;
import com.l2client.animsystem.Channel.Channels;

public class DieAction extends Action {

	public DieAction() {
	}
	
	@Override
	protected Animation evaluate(Mediator med){
		Animation ret = null;
		if(med.setLock(Channels.AllChannels,3)){
			ret = med.getAnimation();
			ret.setLevel(3);
			ret.setChannel(med.getChannel(Channels.AllChannels));
			if(rand.nextInt(1) > 0)
				ret.setName("Death1");
			else
				ret.setName("Death2");

			ret.setBlendTime(0f);
		}
		return ret;
	}
}