package com.l2client.component;

import com.jme3.scene.Node;
import com.l2client.animsystem.InputProvider;
import com.l2client.animsystem.jme.JMEAnimationController;
import com.l2client.animsystem.jme.actions.CallActions;
import com.l2client.animsystem.jme.input.Acting;
import com.l2client.animsystem.jme.input.AttackResult;
import com.l2client.animsystem.jme.input.AttackVector;
import com.l2client.animsystem.jme.input.Direction;
import com.l2client.animsystem.jme.input.Enemy;
import com.l2client.animsystem.jme.input.Hurt;
import com.l2client.animsystem.jme.input.HurtVector;
import com.l2client.animsystem.jme.input.Morale;
import com.l2client.animsystem.jme.input.Speed;
import com.l2client.animsystem.jme.input.Target;
import com.l2client.animsystem.jme.input.Weapon;
import com.l2client.app.Singleton;
import com.l2client.controller.entity.Entity;

public class AnimationSystem extends ComponentSystem {

	private static AnimationSystem inst = null;

	private AnimationSystem() {
		inst = this;
	}

	public static AnimationSystem get() {
		if (inst != null)
			return inst;

		new AnimationSystem();

		return inst;
	}

	@Override
	public void onUpdateOf(Component c, float tpf) {
		if (c instanceof EnvironmentComponent) {
			EnvironmentComponent en = (EnvironmentComponent) c;
			//TODO add an environment evaluation system
			if (en.changed) {
				IdentityComponent e = Singleton.get().getEntityManager().getEntity(c);
				if (e != null) {
					// e.getEntity().setLocalTranslation(com.position.x,
					// com.position.y+com.heightOffset, com.position.z);
					Entity ent = e.getEntity();
					// FIXME damn ugly Ent -> NPCModel -> Vis -> Controller WILL BLOW ON PURE VISMODEL
					JMEAnimationController con = ((Node) ent.getChild(0))
							.getChild(0).getControl(
									JMEAnimationController.class);
					if (con != null) {
						InputProvider in = getInputFrom(en, ent);
						con.setInput(in);
//FIXME OEHAM twice done, see Attack sever packet
//						if(en.damageDealt > 0)
//							con.callAction(CallActions.DefaultAttack.toString(), in);
//						if(en.damageReceived > 0)
//							con.callAction(CallActions.Wounded.toString(), in);
					} 
//					else
						//TODO No JMEAnimationController this is the case on the troll model
//						System.out
//								.println("No JMEAnimationController below entity"
//										+ ent.getId());
				}
				en.changed = false;
			}
		}
	}
	
	/**
	 * Call an action directly
	 * @param a
	 * @param entityId
	 */
	public void callAction(CallActions a, int entityId) {
		IdentityComponent idc = (IdentityComponent) Singleton.get().getEntityManager()
				.getComponent(entityId, IdentityComponent.class);
		if (idc != null) {
			Entity e = idc.getEntity();
			VisualComponent comp = (VisualComponent) Singleton.get().getEntityManager().getComponent(e.getId(), VisualComponent.class);
			if(comp != null){
				//FIXME this can blow up!
				JMEAnimationController con = ((Node) e.getChild(0)).getChild(0)
						.getControl(JMEAnimationController.class);
				if (con != null) {
					EnvironmentComponent env = (EnvironmentComponent) Singleton.get().getEntityManager().getComponent(entityId,
									EnvironmentComponent.class);
					if (env != null) {
						log.fine(e.getId()+" callAction "+a);
						con.callAction(a.toString(), getInputFrom(env, e));
					}
				}
			}
		}

	}

	private InputProvider getInputFrom(EnvironmentComponent en, Entity ent) {
		InputProvider p = new InputProvider();
		if (en.hidden)
			p.setInput(Acting.Hidden);
		else
			p.setInput(Acting.Open);

		TargetComponent tgt = (TargetComponent) Singleton.get().getEntityManager().getComponent(ent.getId(), TargetComponent.class);
		if (tgt != null && tgt.hasTarget()) {
			
			p.setInput(Target.Front);

			float dist = ent.getLocalTranslation().distance(
					tgt.pos);
			if (dist < 5f)
				p.setInput(Enemy.Close);
			else if (dist < 10f)
				p.setInput(Enemy.Near);
			else if (dist < 30f)
				p.setInput(Enemy.Far);
			else
				p.setInput(Enemy.None);
		} else {
			p.setInput(Enemy.None);
		}

		if (en.damageReceived > 0) {
			if (en.damageReceived >= 0) {
				p.setInput(Hurt.Light);
				p.setInput(HurtVector.Front);
			} else if (en.damageReceived >= 32) {
				p.setInput(Hurt.Severe);
				p.setInput(HurtVector.Front);
			} else if (en.damageReceived >= 128) {
				p.setInput(Hurt.Deadly);
				p.setInput(HurtVector.Front);
			}
			//TODO who is the one to decide on resetting this? this should go into an EnvSystem!!
			en.damageReceived = 0;
		} else {
			p.setInput(Hurt.None);
			p.setInput(HurtVector.None);
		}
		
		if(en.damageDealt >= 0){
			if(en.damageDealt >0)
				p.setInput(AttackResult.Success);
			else
				p.setInput(AttackResult.Failure);
			
			p.setInput(AttackVector.Mid_Front);
			//TODO who is the one to decide on resetting this? this should go into an EnvSystem!!
			en.damageDealt = -1;
		} else {
			p.setInput(AttackResult.None);
		}

		if (en.teamHealthPercent > 80)
			p.setInput(Morale.High);
		else
			p.setInput(Morale.Normal);

		if(en.movement < 0){
			p.setInput(Direction.None);
			p.setInput(Speed.None);
		} else {
			p.setInput(Direction.Front);
			p.setInput(en.movement > 0 ? Speed.Run
						: Speed.Walk);
		}

		p.setInput(Weapon.OneHand);

		return p;
	}
}
