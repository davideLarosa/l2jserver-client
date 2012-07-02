package com.l2client.animsystem.jme.test;

import java.net.MalformedURLException;
import java.util.logging.Logger;

import junit.framework.TestCase;

import com.l2client.animsystem.Action;
import com.l2client.animsystem.Animation;
import com.l2client.animsystem.IAnimationProvider;
import com.l2client.animsystem.InputProvider;
import com.l2client.animsystem.Mediator;
import com.l2client.animsystem.Transitions;
import com.l2client.animsystem.jme.actions.Celebrate;
import com.l2client.animsystem.jme.actions.DefaultAction;
import com.l2client.animsystem.jme.actions.DefaultAttack;
import com.l2client.animsystem.jme.actions.Defend;
import com.l2client.animsystem.jme.actions.Retreat;
import com.l2client.animsystem.jme.actions.Taunt;
import com.l2client.animsystem.jme.actions.Wounded;
import com.l2client.animsystem.jme.input.Direction;
import com.l2client.animsystem.jme.input.Enemy;
import com.l2client.animsystem.jme.input.Hurt;
import com.l2client.animsystem.jme.input.HurtVector;
import com.l2client.animsystem.jme.input.Speed;


public class AnimationSystemTest extends TestCase implements IAnimationProvider{
	
	private static final Logger log = Logger.getLogger(AnimationSystemTest.class.getName());
	private String currentAnim = "";

	public void testSelf() throws MalformedURLException{
		Mediator mediator = new Mediator(getActions(), new Transitions(new String[][]{}));
		InputProvider in = new InputProvider();
		mediator.setAnimationProvider(this);
		mediator.update(0.5f);
		log.info("Animation name:"+currentAnim);
		assertTrue(currentAnim.startsWith("stand_"));
		mediator.update(10.0f);
		in.setInput(Direction.Front);
		in.setInput(Speed.Run);
		mediator.update(10.0f);
		//should still be stand, one frame coherence
		assertTrue(currentAnim.startsWith("stand_"));
		log.info("Animation name:"+currentAnim);
		mediator.setInput(in);
		mediator.update(10.0f);
		//now changed
		log.info("Animation name:"+currentAnim);
		assertTrue(currentAnim.startsWith("run"));
		mediator.update(10.0f);
		log.info("Animation name:"+currentAnim);
		assertTrue(currentAnim.startsWith("run"));
		in = new InputProvider();
		in.setInput(Direction.None);
		in.setInput(Speed.Run);//implizit overrule of speed (no direction, no speed)
		mediator.setInput(in);
		mediator.update(10.0f);
		//back to idle?
		assertTrue(currentAnim.startsWith("stand_"));
		in = new InputProvider();
		in.setInput(Enemy.Near);
		mediator.setInput(in);
		mediator.update(10.0f);
		//now changed
		log.info("Animation name:"+currentAnim);
		assertTrue(currentAnim.startsWith("ready"));
		
		mediator.update(10.0f);
		mediator.update(10.0f);
		in = new InputProvider();
		in.setInput(HurtVector.Front);
		in.setInput(Hurt.Light);
		mediator.callAction("Wounded", in);
		mediator.update(10f);
		log.info("Animation name:"+currentAnim);
		assertTrue(currentAnim.startsWith("knockback"));
		
	}

	private Action[] getActions() {
		return new Action[]{
			new DefaultAction(),
			new Celebrate(),
			new DefaultAttack(),
			new Defend(),
			new Retreat(),
			new Taunt(),
			new Wounded()
		};
	}

	@Override
	public Animation createAnimation() {
		// TODO Auto-generated method stub
		return new Animation(this) {
			
			@Override
			public Object getInternalAnimation() {
				//ok here as it is not needed
				return null;
			}
			
			@Override
			public float getAnimationLength() {
				//ok for the testcase all anims are 1.5 seconds long
				return 1.5f;
			}
		};
	}

	@Override
	public void setInternalAnimation(Animation a) {
		this.currentAnim = a.getName();
	}
}
