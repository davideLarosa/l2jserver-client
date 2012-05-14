package com.l2client.animsystem.example;

import com.l2client.animsystem.Action;
import com.l2client.animsystem.Animation;
import com.l2client.animsystem.InputProvider;
import com.l2client.animsystem.Mediator;
import com.l2client.animsystem.Channel.Channels;

public class DefaultAction extends Action {

	public DefaultAction() {
	}
	
	@Override
	protected Animation evaluate(Mediator med){
		Animation ret = null;
		InputProvider in = med.getInput();
		if(Movement.None.equals(in.getInput(Movement.class))){
			if(med.setLock(Channels.AllChannels,1)){
				ret = med.getAnimation();
				if(rand.nextInt(9) > 5 ){
					ret.setName("Idle1");
				} else{
					if(rand.nextInt(1) > 0){
						ret.setName("Idle2");
					}else{
						ret.setName("Idle3");
					}
				}
				ret.setBlendTime(0.5f);
			}
		} else {
			if(med.setLock(Channels.AllChannels,1)){
				ret = med.getAnimation();
				if(Acting.Hidden.equals(in.getInput(Acting.class))){
					ret.setName("Stealth");
					ret.setBlendTime(0.5f);
				} else {
					ret.setName("Walk");
					ret.setBlendTime(0.5f);
				}
			}
		}

		if(ret != null){
			ret.setChannel(med.getChannel(Channels.AllChannels));
			ret.setLevel(1);
		}

		return ret;
	}
}