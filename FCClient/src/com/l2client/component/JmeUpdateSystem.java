package com.l2client.component;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.l2client.app.Singleton;
import com.l2client.controller.entity.Entity;
import com.l2client.controller.entity.EntityManager;
import com.l2client.gui.InputController;
import com.l2client.model.jme.VisibleModel;

/**
 * Singleton component system responsible for 
 * - synchronizing positions between the component computed an the jMonkeyEngine
 * - targeting and untargeting of player targets
 * 
 * for general working of a component system have a look at @see ComponentSystem
 */
public class JmeUpdateSystem extends ComponentSystem {
	
//	public static float MAX_TARGETING_DISTANCE = 30f;

	private static JmeUpdateSystem singleton = null;
	
	private JmeUpdateSystem(){
	}
	
	public static JmeUpdateSystem get(){
		if(singleton == null){
			synchronized (JmeUpdateSystem.class) {
				if(singleton == null){
					singleton = new JmeUpdateSystem();
				}
			}
		}
		return singleton;
	}

	@Override
	public void onUpdateOf(Component c, float tpf) {
		EntityManager em = Singleton.get().getEntityManager();
		if(c instanceof TargetComponent){
			TargetComponent tgt = (TargetComponent)c;
			L2JComponent l2j = (L2JComponent)em.getComponent(em.getEntityId(tgt),L2JComponent.class);
			if(l2j != null && !l2j.isPlayer) {
				//
				// NPC UPDATE
				if(tgt.getCurrentTarget() != tgt.getLastTarget()){	
					//reset targeting creation changes by setting old target to current 
					tgt.setTarget(tgt.getCurrentTarget());
				} else {
					//update position only
					//check if target is set and moved out of distance
					if(!tgt.hasTarget())
						return;
					//TODO distance check only on client side? server still has our last target?
					IdentityComponent e = (IdentityComponent) em.getComponent(tgt.getCurrentTarget(), IdentityComponent.class);
					if(e != null){
						IdentityComponent me = em.getEntity(tgt);
						Entity i = me.getEntity();
						Entity them = e.getEntity();
						if(them != null && i != null){
							//update our target positin (ev. used by others), ev. rotation to target in positioning system
							tgt.pos = them.getLocalTranslation().clone();
						}
					}
				}
				//
				// ENDOF NPC UPDATE
			} else {
				//----------- target changed to new target -----------
				if(tgt.getCurrentTarget() != tgt.getLastTarget()){	
					//
					// PLAYER UPDATE
						//----------- remove last marked ----------- 
						if(tgt.getLastTarget() != TargetComponent.NO_TARGET){
							log.fine("Player target changed, removing Selection on:"+tgt.getLastTarget());
							//target was set to a new entity
							//get visual and REMOVE selection from it
							VisualComponent vis = (VisualComponent) em
								.getComponent(tgt.getLastTarget(), VisualComponent.class);
							if(vis!=null){
								if (vis.vis != null) {
									((VisibleModel) vis.vis).removeSelectionMarker();
									log.finest("Player target changed, Selection removed:"+tgt.getLastTarget());
								} else {
									log.severe("Player target changed, Selection VIS not found on:"+tgt.getLastTarget());
								}
							} else 
								log.severe("Player target changed, Selection NO VIS not found on:"+tgt.getLastTarget());

//FIXME this causes side effects with entity deletion, it seems, l2j deletes dead entities quite fast
//							LoggingComponent l = (LoggingComponent) em.getComponent(tgt.getLastTarget(), LoggingComponent.class);
//							if(l != null) {
//								Singleton.get().getPosSystem().removeComponentForUpdate(l);
//								em.removeComponent(tgt.getLastTarget(), l);
//							}
						}
						//----------- set current ----------- 
						if(tgt.hasTarget()){
							log.fine("Player target changed, adding Selection on:"+tgt.getCurrentTarget());
							//target was set to a new entity
							//get visual and add selection to it
							VisualComponent vis = (VisualComponent) em
								.getComponent(tgt.getCurrentTarget(), VisualComponent.class);
							if (vis != null && vis.vis != null) {
								((VisibleModel) vis.vis).addSelectionMarker(tgt.color);
								log.fine("Player target changed, Selection added:"+tgt.getCurrentTarget());
							} else {
								log.severe("Player target changed, Selection VIS not found on on:"+tgt.getCurrentTarget());
							}

//FIXME this causes side effects with entity deletion, it seems, l2j deletes dead entities quite fast							
//							LoggingComponent l = (LoggingComponent) em.getComponent(tgt.getCurrentTarget(), LoggingComponent.class);
//							if(l == null){
//								l = new LoggingComponent();
//								em.addComponent(tgt.getCurrentTarget(), l);
//								Singleton.get().getPosSystem().addComponentForUpdate(l);
//							} else {
//								log.severe("Logging comp still on changed ent!!?");
//							}
						} else {
							//reset target creation by setting old target to current 
							tgt.setNoTarget();
							tgt.pos = Vector3f.ZERO;
							log.fine("Player target changed, set to NOTARGET");
						}
					//----------- reset targeting creation changes by setting old target to current ----------- 
					tgt.setTarget(tgt.getCurrentTarget());
					log.fine("Player target changed, setting last to current, current to current target");
				} else {
					//check if target is set and moved out of distance
					if(!tgt.hasTarget())
						return;
					//TODO distance check only on client side? server still has our last target?
					IdentityComponent e = (IdentityComponent) em.getComponent(tgt.getCurrentTarget(), IdentityComponent.class);
					if(e != null){
						IdentityComponent me = em.getEntity(tgt);
						Entity i = me.getEntity();
						Entity them = e.getEntity();
						if(them != null && i != null){
							//update our target positin (ev. used by others)
							tgt.pos = them.getLocalTranslation();
//							if(tgt.pos.distance(i.getLocalTranslation()) > MAX_TARGETING_DISTANCE) {
//								//remove next time in above code
//								tgt.setNoTarget();
//							} else {
								//update health bar of highlighted target
								VisualComponent vis = (VisualComponent) em
										.getComponent(tgt.getCurrentTarget(), VisualComponent.class);
								if (vis != null && vis.vis != null) {
									((VisibleModel) vis.vis).updateHealthbar((float)l2j.l2jEntity.getCurrentHp()/(float)l2j.l2jEntity.getMaxHp());
								}else
									log.severe("Player target changed, VIS not found on :"+tgt.getCurrentTarget()+" for HealthBar update");
//							}
							//update target information for environment
							EnvironmentComponent env = (EnvironmentComponent) Singleton.get().getEntityManager().getComponent(i.getId(), EnvironmentComponent.class);
							if (env != null) {
								env.changed = true;
								log.finer("Should update stance");
							} else
								log.severe("No EnvironmentComponent found with entity id "
												+ i.getId() + ", perhaps just create one?");
						} else {
							//remove next time in above code
							tgt.setNoTarget();
						}
					} else {
						//remove next time in above code
						tgt.setNoTarget();
					}
				}
			}
			//
			// END OF PLAYER UPDATE
		} else
			if(c instanceof PositioningComponent){
				PositioningComponent com = (PositioningComponent) c;
				IdentityComponent e = em.getEntity(com);
				if(e != null)
				{
					Entity ent = e.getEntity();
					ent.setLocalTranslation(com.position.x, com.position.y+com.heightOffset, com.position.z);
					ent.setLocalRotation(new Quaternion().fromAngleNormalAxis(com.heading, Vector3f.UNIT_Y.negate()));
				} else {
					log.severe("Positioning component without identity component found! comp:"+
							c+" at "+ com.position.x+ ", "+(com.position.y+com.heightOffset)+", "+com.position.z);
					//FIXME this is a workaround, investigate why this can happen! was it added twice? should we better use a hasmap for components
					dumpComponents();
					System.out.println("----- now em ----------");
					em.dumpComponents(em.getEntityId(c));
					em.dumpAllComponents();
					removeComponentForUpdate(com);
				}
//		}else 
//			if(c instanceof SimplePositionComponent){
//				SimplePositionComponent com = (SimplePositionComponent) c;
//				IdentityComponent e = em.getEntity(com);
//				if(e != null)
//				{
//					Entity ent = e.getEntity();
//					ent.setLocalTranslation(com.currentPos.x, 0f /* currently ignoring height com.currentPos.y */, com.currentPos.z);
//					ent.setLocalRotation(new Quaternion().fromAngleNormalAxis(com.heading, Vector3f.UNIT_Y.negate()));
//				}			
			}
	}
}
