package com.l2client.animsystem.jme.actions;

import com.l2client.animsystem.Action;
import com.l2client.animsystem.Animation;
import com.l2client.animsystem.InputProvider;
import com.l2client.animsystem.Mediator;
import com.l2client.animsystem.Channel.Channels;
import com.l2client.animsystem.jme.input.Acting;
import com.l2client.animsystem.jme.input.Direction;
import com.l2client.animsystem.jme.input.Enemy;
import com.l2client.animsystem.jme.input.Speed;
import com.l2client.animsystem.jme.input.Target;

//TODO make idle ones lowest, than taunt celebrate, etc., then walks, hmm idel on level 0 and others on 1 ? wlka on 2 etc ?
public class DefaultAction extends Action {

	public DefaultAction() {
	}
	
	@Override
	protected Animation evaluate(Mediator med){
		Animation an = null;
		if(med.setLock(Channels.AllChannels,1)){
//			an = med.getAnimation();//FIXME not need at all
			InputProvider in = med.getInput();
			switch(in.getInput(Enemy.class)){
			case None:
			case VeryFar:
				{an = outCombatAnimation(med, in);break;}
			default:
				{an = inCombatAnimation(med,in); break;}
			}
			if(an != null){
				an.setChannel(med.getChannel(Channels.AllChannels));
				an.setLevel(1);
				return an;
			}
		}
		
		//c = a.getChannel(Channels.UpperBody);
		//if(c!= null && c.setLock(1)){}
		//do other body anims
		return null;
	}

	private Animation outCombatAnimation(Mediator a, InputProvider in) {
		Animation ret = a.getAnimation();
		if(Direction.None.equals(in.getInput(Direction.class))){
			if(rand.nextInt(9) > 4 ){
				//AnimationManager.get().getAnimation(assetName, path)
				ret.setName("stand_a_idle");
			} else{
				if(rand.nextInt(1) > 0){
					ret.setName("stand_b_idle");
				}else{
					ret.setName("stand_c_idle");
				}
			}
			ret.setBlendTime(0.2f);
		} else {
			if(Speed.Run.equals(in.getInput(Speed.class))){
				ret.setName("run");
				ret.setBlendTime(0.1f);
			} else {
				ret.setName("walk");
				ret.setBlendTime(0.1f);
			}
		}
		return ret;
	}

	private Animation inCombatAnimation(Mediator a, InputProvider in) {
		Animation ret = a.getAnimation();
		
		if(Direction.None.equals(in.getInput(Direction.class))){
			if(Acting.Open.equals(in.getInput(Acting.class)))
				ret.setName(getReadyResult(in));
			else
				ret.setName("hide_idle");
			
			ret.setBlendTime(1f);
		} else {
			if(Speed.Run.equals(in.getInput(Speed.class))){
				if(Acting.Open.equals(in.getInput(Acting.class))){
					switch(in.getInput(Enemy.class)){
					case Close:{ret.setName(getShuffleResult(in));break;}
					case Near:{ret.setName("charge");break;}
					case Far:
					case VeryFar:ret.setName("combat_jog");
					}
				} else
					ret.setName("stealthy_walk");
				ret.setBlendTime(1f);
			} else {
				if(Enemy.Close.equals(in.getInput(Enemy.class)))
					ret.setName(getShuffleResult(in));
				else
					ret.setName("advance");
				ret.setBlendTime(1f);
			}
		}
		return ret;
	}

	private String getReadyResult(InputProvider in) {
		int i = rand.nextInt(9);
		switch(in.getInput(Target.class)){
		case None:{
			switch(i){
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:return "ready_idle";
			case 6:return "ready_hf_idle";
			case 7:return "ready_lf_idle";
			case 8:return "shuffle_right";
			case 9:return "shuffle_left";
			}}
		case Left15:return "ready_lf_high_morale";
		case Left45:return "ready_lf_low_morale";
		case Left:return "ready_lf_idle";
		case Right15:return "ready_hf_high_morale";
		case Right45:return "ready_hf_low_morale";
		case Right:return "ready_hf_idle";
		case Front:
		case Back:return "ready_idle";
		}
		return "";
	}

	private String getShuffleResult(InputProvider in) {
		int i = rand.nextInt(9);
		switch(in.getInput(Direction.class)){
		case None:{
			switch(i){
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:return "shuffle_forward";
			case 7:return "shuffle_backward";
			case 8:return "shuffle_right";
			case 9:return "shuffle_left";
			}}
		case Left15:
		case Left45:
		case Left:return "shuffle_left";
		case Right15:
		case Right45:
		case Right:return "shuffle_right";
		case Front:return "shuffle_forward";
		case Back:return "shuffle_backward";
		}
		return "";
	}
}
