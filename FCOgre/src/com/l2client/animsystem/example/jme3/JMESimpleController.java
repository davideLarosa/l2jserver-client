package com.l2client.animsystem.example.jme3;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.l2client.animsystem.Action;
import com.l2client.animsystem.IAnimationProvider;
import com.l2client.animsystem.InputProvider;
import com.l2client.animsystem.Mediator;
import com.l2client.animsystem.Transitions;
import com.l2client.animsystem.example.AttackAction;
import com.l2client.animsystem.example.BlockAction;
import com.l2client.animsystem.example.ClimbAction;
import com.l2client.animsystem.example.DefaultAction;
import com.l2client.animsystem.example.DieAction;
import com.l2client.animsystem.example.JumpAction;


public class JMESimpleController extends AbstractControl implements IAnimationProvider {
	
	private static final long serialVersionUID = 1L;
	private static final Action[] ACTIONS = new Action[] {
		new DefaultAction(),
		new AttackAction(),
		new BlockAction(),
		new ClimbAction(),
		new JumpAction(),
		new DieAction()		
	};
	private static final String[][]TRANSITIONS = new String[][]{};
	
	private Mediator mediator = new Mediator(ACTIONS, new Transitions(TRANSITIONS));
	private boolean singleStep = false;
	private AnimControl internalController;
	
	
	public JMESimpleController(AnimControl c) {
		mediator.setAnimationProvider(this);
		internalController = c;
	}
	
	@Override
	public void update(float tpf) {
		if(singleStep){
//			internalController.update(0f);
			mediator.update(0f);			
		} else {
//			internalController.update(tpf);
		    mediator.update(tpf);
		}
	}
	
	public void callAction(String action, InputProvider in){
		mediator.callAction(action, in);
	}
	
	public void setInput(InputProvider in) {
		mediator.setInput(in);
	}
		
	private class JMEAnimation extends com.l2client.animsystem.Animation{

		public JMEAnimation(IAnimationProvider animProvider) {
			super(animProvider);
		}

		@Override
		public Object getInternalAnimation() {
			return null; //not needed in this scenario
		}

		@Override
		public float getAnimationLength() {
			float l = internalController.getAnimationLength(getName());
			if(l<0f)
				l=0f;
			
			return l;
		}

		@Override
		public void setName(String name) {
			super.setName(name);
		}
		
	}

	@Override
	public com.l2client.animsystem.Animation createAnimation() {
		return new JMEAnimation(this);
	}

	@Override
	public void setInternalAnimation(com.l2client.animsystem.Animation anim) {
		if(anim != null){
			//FIXME jme2 -> jme3
			//Something to blend over at all? (JME2 does not handle setting the same animation gracefully
			AnimChannel channel = internalController.getChannel(0);
			if(channel != null){
				String name = channel.getAnimationName();
					if(name == null || !channel.getAnimationName().equals(anim.getName())){
						channel.setAnim(anim.getName(), anim.getBlendTime());
						channel.setLoopMode(anim.isLooping()?LoopMode.Loop:LoopMode.DontLoop);
						channel.setSpeed(anim.getPlayBackRate());
					}
			}
		}
	}

	@Override
	public Control cloneForSpatial(Spatial spatial) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void controlUpdate(float tpf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {
		// TODO Auto-generated method stub
	}
}
