package com.l2client.animsystem.jme.actions;

import com.l2client.animsystem.Action;
import com.l2client.animsystem.Animation;
import com.l2client.animsystem.InputProvider;
import com.l2client.animsystem.Mediator;
import com.l2client.animsystem.Channel.Channels;
import com.l2client.animsystem.jme.input.AttackVector;

/**
 * defend at level 3
 *
 */
public class Defend extends Action {
	@Override
	protected Animation evaluate(Mediator med){

		Animation ret = null;
		if(med.forceLockCheck(Channels.AllChannels,2)){
			ret = med.getAnimation();
			ret.setChannel(med.getChannel(Channels.AllChannels));
			ret.setLevel(3);
			ret.setBlendTime(0.2f);
			ret.setLooping(false);
			ret.setKeep(1.0f);
			InputProvider i = med.getInput();
			switch(i.getInput(AttackVector.class)){
			case None:{ret.setName("defend_mid_stab");break;}
			case High_LeftRight:{ret.setName("defend_hi_slashlr");break;}
			case High_RightLeft:{ret.setName("defend_hi_slashrl");break;}
			case High_Front:{ret.setName("defend_hi_stab");break;}
			case Mid_LeftRight:{ret.setName("defend_mid_slashlr");break;}
			case Mid_RightLeft:{ret.setName("defend_mid_slashrl");break;}
			case Mid_Front:{ret.setName("defend_mid_stab");break;}
			case Low_LeftRight:{ret.setName("defend_lo_slashlr");break;}
			case Low_RightLeft:{ret.setName("defend_lo_slashrl");break;}
			case Low_Front:{ret.setName("defend_lo_stab");break;}
			}
			return ret;
		}
		return null;
	}
}