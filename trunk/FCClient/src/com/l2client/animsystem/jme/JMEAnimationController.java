package com.l2client.animsystem.jme;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
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
import com.l2client.animsystem.jme.actions.Celebrate;
import com.l2client.animsystem.jme.actions.DefaultAction;
import com.l2client.animsystem.jme.actions.DefaultAttack;
import com.l2client.animsystem.jme.actions.Defend;
import com.l2client.animsystem.jme.actions.Die;
import com.l2client.animsystem.jme.actions.Retreat;
import com.l2client.animsystem.jme.actions.Revive;
import com.l2client.animsystem.jme.actions.StopMove;
import com.l2client.animsystem.jme.actions.Taunt;
import com.l2client.animsystem.jme.actions.Wounded;
import com.l2client.app.Singleton;

public class JMEAnimationController extends AbstractControl implements
		IAnimationProvider {

//	private static HashMap<String, PartSet> noAnims = new HashMap<String, PartSet>();
	private static final Action[] ACTIONS = new Action[] { new DefaultAction(),
			new Celebrate(), new DefaultAttack(), new Defend(), new Die(), new Retreat(),
			new Revive(), new StopMove(), new Taunt(), new Wounded()

	};
	private static final String[][] TRANSITIONS = new String[][] {
			{ "run", "walk", "run_to_walk" }, { "walk", "run", "walk_to_run" },
			{ "walk", "stand_a_idle", "walk_to_stand_a" },
			{ "run", "stand_a_idle", "run_to_stand_a" },
			{"advance", "combat_jog", "advance_to_combat_jog"},
			{"advance", "ready_idle", "advance_to_ready"},
			};

	private Mediator mediator = new Mediator(ACTIONS, new Transitions(
			TRANSITIONS));
	public static boolean singleStep = false;

	private AnimControl internalController;

	public JMEAnimationController(AnimControl c, String anims) {

		mediator.setAnimationProvider(this);
		internalController = c;
		internalController.setAnimationProvider(Singleton.get().getAnimManager()
				.getAnimationProvider(anims));
	}
//
//	@Override
//	public void update(float tpf) {
//		if (singleStep) {
//			// internalController.update(0f);
//			mediator.update(0f);
//		} else {
//			// internalController.update(tpf);
//			mediator.update(tpf);
//		}
//	}

	public void callAction(String action, InputProvider in) {
		mediator.callAction(action, in);
	}

	public void setInput(InputProvider in) {
		mediator.setInput(in);
	}

//	private class JMEAnimation extends com.l2client.animsystem.Animation {
//
//		private Future<Animation> internal = null;
//
//		public JMEAnimation(IAnimationProvider animProvider) {
//			super(animProvider);
//		}
//
//		@Override
//		public Object getInternalAnimation() {
//			if (internal != null)
//				try {
//					return internal.get();
//				} catch (Exception e) {
//					return null;
//				}
//			else
//				return null;
//		}
//
//		@Override
//		public float getAnimationLength() {
//			if (internal != null) {
//				try {
//					return internal.get().getLength();
//				} catch (Exception e) {
//					return 0f;
//				}
//			} else
//				return 0f;
//		}
//
//		@Override
//		public void setName(final String name) {
//			super.setName(name);
//			internal = Executors.newSingleThreadExecutor().submit(
//					new Callable<Animation>() {
//						@Override
//						public Animation call() throws Exception {
//							return (Animation) internalController.getAnim(name);
//						}
//					});
//
//		}
//	}
	
	private class JMEAnimation2 extends com.l2client.animsystem.Animation {

		private Animation internal = null;

		public JMEAnimation2(IAnimationProvider animProvider) {
			super(animProvider);
		}

		@Override
		public Object getInternalAnimation() {
			return internal;
		}

		@Override
		public float getAnimationLength() {
			if (internal != null) {
				return internal.getLength()*getKeep();
			} else
				return 0f;
		}

		@Override
		public void setName(final String name) {
			super.setName(name);
			internal = internalController.getAnim(name);
		}
	}	

	@Override
	public com.l2client.animsystem.Animation createAnimation() {
		return new JMEAnimation2(this);
	}

	@Override
	public void setInternalAnimation(com.l2client.animsystem.Animation anim) {
		if (anim != null) {
			String newAnim = anim.getName();
			// FIXME jme2 -> jme3
			// Something to blend over at all? (JME2 does not handle setting the
			// same animation gracefully
			AnimChannel channel = internalController.getChannel(0);
			if (channel != null && newAnim != null && newAnim.length()>0) {
				String name = channel.getAnimationName();
				if (name == null
						|| !channel.getAnimationName().equals(newAnim)) {
					channel.setAnim(newAnim, anim.getBlendTime());
					channel.setLoopMode(anim.isLooping() ? LoopMode.Loop
							: LoopMode.DontLoop);
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
		if (singleStep) {
			// internalController.update(0f);
			mediator.update(0f);
			internalController.update(0f);
		} else {
			// internalController.update(tpf);
			mediator.update(tpf);
			internalController.update(tpf);
		}
	}

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {
		// TODO Auto-generated method stub
	}

}
