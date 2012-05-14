package com.l2client.animsystem.example;

import com.l2client.animsystem.Action;
import com.l2client.animsystem.Animation;
import com.l2client.animsystem.InputProvider;
import com.l2client.animsystem.Mediator;
import com.l2client.animsystem.Channel.Channels;

public class JumpAction extends Action {

	public JumpAction() {
	}
	
	@Override
	protected Animation evaluate(Mediator med){
		Animation ret = null;
		if(med.forceLock(Channels.AllChannels,1)){
			ret = med.getAnimation();
			ret.setLevel(1);
			ret.setChannel(med.getChannel(Channels.AllChannels));
			InputProvider in = med.getInput();
			switch(in.getInput(Target.class)){
			case Mid:	ret.setName("Jump");break;
			case High: ret.setName("HighJump");break;
			case Back: ret.setName("Backflip");break;
			default: ret.setName("JumpNoHeight");break;
			}
			ret.setBlendTime(0.2f);
		}
		return ret;
	}
}