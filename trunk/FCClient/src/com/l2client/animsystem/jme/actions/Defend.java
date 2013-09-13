package com.l2client.animsystem.jme.actions;

import com.l2client.animsystem.Action;
import com.l2client.animsystem.Animation;
import com.l2client.animsystem.Channel.Channels;
import com.l2client.animsystem.InputProvider;
import com.l2client.animsystem.Mediator;
import com.l2client.animsystem.jme.input.HurtVector;

/**
 * defend at level 3
 *
 */
public class Defend extends Action {
	@Override
	protected Animation evaluate(Mediator med){

		Animation ret = null;
		if(med.forceLockCheck(Channels.AllChannels,3)){
			ret = med.getAnimation();
			ret.setChannel(med.getChannel(Channels.AllChannels));
			ret.setLevel(3);
			ret.setBlendTime(0.2f);
			ret.setLooping(false);
			InputProvider i = med.getInput();
			int v = rand.nextInt(4);
			//diversity for horizon
			String dir = "mid";
			if(v<=0)
				dir="hi";
			else if(v>=3)
				dir="lo";
			//diversity in case Hurt is none for left right front back
			if(i.getInput(HurtVector.class).equals(HurtVector.None)){
				switch(rand.nextInt(4)){
				case 0:i.setInput(HurtVector.Left);break;
				case 1:i.setInput(HurtVector.Right);break;
				default:break;
				}
			}
			
			
			switch(i.getInput(HurtVector.class)){
			case None:
			case Front:
			case Back:{ret.setName("defend_"+dir+"_stab");break;}
			case Left:{ret.setName("defend_"+dir+"_slashlr");break;}
			case Right:{ret.setName("defend_"+dir+"_slashrl");break;}
			}
			log.info("Defend:->"+ret.getName());
			return ret;
		}
		return null;
	}
}