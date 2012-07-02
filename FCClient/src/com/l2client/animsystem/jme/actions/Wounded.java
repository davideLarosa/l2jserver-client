package com.l2client.animsystem.jme.actions;

import com.l2client.animsystem.Action;
import com.l2client.animsystem.Animation;
import com.l2client.animsystem.InputProvider;
import com.l2client.animsystem.Mediator;
import com.l2client.animsystem.Channel.Channels;
import com.l2client.animsystem.jme.input.Hurt;
import com.l2client.animsystem.jme.input.HurtVector;

/**
 * wounded at level 4, should be highest prio
 *
 */
public class Wounded extends Action {
	@Override
	protected Animation evaluate(Mediator med){
		Animation ret = null;
		if(med.forceLockCheck(Channels.AllChannels,3)){
			ret = med.getAnimation();
			ret.setChannel(med.getChannel(Channels.AllChannels));
			ret.setLevel(4);	
			ret.setLooping(false);
			ret.setKeep(1.0f);
			InputProvider i = med.getInput();
			switch(i.getInput(Hurt.class)){
			case Light:{
				int j = rand.nextInt(9);
				String name; 
				if(rand.nextInt(9)>7){
					name = "knockback_";
				} else {
					name = "knockback_move_";			
				}

				switch(i.getInput(HurtVector.class)){
				case None:
				case Front:
					switch(j){
					case 0:
					case 1:
					case 2:
					case 3:
					case 4:
					case 5:	
						ret.setName(name+"from_front");
						break;
					case 6:
					case 7:
						ret.setName(name+"from_left");
						break;
					case 8:
					case 9:
						ret.setName(name+"from_right");
						break;
					}
					break;
				case Back:
					switch(j){
					case 0:
					case 1:
					case 2:
					case 3:
					case 4:
					case 5:	
						ret.setName(name+"from_back");
						break;
					case 6:
					case 7:
						ret.setName(name+"from_left");
						break;
					case 8:
					case 9:
						ret.setName(name+"from_right");
						break;
					}
					break;
				case Left:
					switch(j){
					case 0:
					case 1:
					case 2:
					case 3:
					case 4:
					case 5:	
						ret.setName(name+"from_left");
						break;
					case 6:
					case 7:
						ret.setName(name+"from_front");
						break;
					case 8:
					case 9:
						ret.setName(name+"from_back");
						break;
					}
					break;
				case Right:
					switch(j){
					case 0:
					case 1:
					case 2:
					case 3:
					case 4:
					case 5:	
						ret.setName(name+"from_right");
						break;
					case 6:
					case 7:
						ret.setName(name+"from_front");
						break;
					case 8:
					case 9:
						ret.setName(name+"from_back");
						break;
					}
				}				
				
				break;
			}
			case Severe:{
				ret.setLooping(false);
				ret.setKeep(1.0f);
				switch(i.getInput(HurtVector.class)){
				case None:
				case Left:
				case Right:
					if(rand.nextInt(1)>0)
						ret.setName("knockdown_backward_launch");
					else
						ret.setName("knockdown_forward_launch");
					break;
				case Front:
					ret.setName("knockdown_backward_launch");
					break;
				case Back:
					ret.setName("knockdown_forward_launch");
					break;

				}
				break;				
			}
			case Deadly:{
				ret.setLooping(false);
				ret.setKeep(1.0f);
				switch(i.getInput(HurtVector.class)){
				case None:
				case Front:
					ret.setName("die_backward");
					break;
				case Back:
					ret.setName("die_forward");
					break;
				case Left:
					ret.setName("die_to_back_left");
					break;
				case Right:
					ret.setName("die_to_back_right");
					break;
				}
				break;
			}
			}
//			return ret;
		}
		return ret;
	}
}
