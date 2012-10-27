package com.l2client.animsystem.jme.actions;

import com.l2client.animsystem.Action;
import com.l2client.animsystem.Animation;
import com.l2client.animsystem.Channel.Channels;
import com.l2client.animsystem.InputProvider;
import com.l2client.animsystem.Mediator;
import com.l2client.animsystem.jme.input.AttackResult;
import com.l2client.animsystem.jme.input.AttackVector;
import com.l2client.animsystem.jme.input.Weapon;

/**
 * Attack at level 3
 */
public class DefaultAttack extends Action {
	@Override
	protected Animation evaluate(Mediator med){

		Animation ret = null;
		if(med.forceLockCheck(Channels.AllChannels,3)){
			ret = med.getAnimation();
			ret.setChannel(med.getChannel(Channels.AllChannels));
			ret.setBlendTime(0.2f);
			ret.setLooping(false);
			ret.setLevel(3);
			InputProvider i = med.getInput();
			String ending = "_fail";
			if(AttackResult.Success.equals(i.getInput(AttackResult.class)))
					ending = "_success";
			if(Weapon.None.equals(i.getInput(Weapon.class)))
					ret.setName("at_hi_a_punch"+ending);
			
			switch(i.getInput(AttackVector.class)){

			case High_LeftRight:{ret.setName("at_hi_c_slashlr"+ending);break;}
			case High_RightLeft:{ret.setName("at_hi_c_slashrl"+ending);break;}
			case High_Front:{
					int n = rand.nextInt(1);
					if(n>0)
						ret.setName("at_hi_c_slashlr"+ending);
					else
						ret.setName("at_hi_c_slashrl"+ending);
					
					break;}
			case None://{ret.setName("");break;}//don't do this there is no anim "" this will blow
			case Mid_LeftRight:
			case Mid_RightLeft:
			case Mid_Front:
			case Low_LeftRight:
			case Low_RightLeft:
			case Low_Front:{ret.setName(getMidLowResult(i.getInput(AttackVector.class))+ending);break;}
			}
			ret.setName("at_mid_c_slashlr_v0_success");
			log.info("DefaultAttack:->"+ret.getName());
			return ret;
		}
		return null;
	}

	private String getMidLowResult(AttackVector values) {
		
		switch(values){
		case Mid_LeftRight:
		case Low_LeftRight:return "at_mid_c_slashlr_s0";
		case Mid_RightLeft:	
		case Low_RightLeft:return "at_mid_c_slashrl_s0";
		case Mid_Front:
		case Low_Front:
		default:
		{
//			int i = rand.nextInt(1);
//			if(i>0)
			return "at_mid_c_slashlr_v0";
//			else
//				return "at_mid_c_slashrl_s1_slashlr";
				//this one is in the middle of the slash doing the same as s0 so wft..
		}
		}
	}
}
