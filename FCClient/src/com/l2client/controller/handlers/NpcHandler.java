package com.l2client.controller.handlers;

import java.util.logging.Logger;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.l2client.app.Singleton;
import com.l2client.component.AnimationSystem;
import com.l2client.component.EnvironmentComponent;
import com.l2client.component.IdentityComponent;
import com.l2client.component.JmeUpdateSystem;
import com.l2client.component.L2JComponent;
import com.l2client.component.PositioningSystem;
import com.l2client.component.SimplePositionComponent;
import com.l2client.component.VisualComponent;
import com.l2client.controller.SceneManager;
import com.l2client.controller.entity.Entity;
import com.l2client.controller.entity.EntityManager;
import com.l2client.dao.derby.DatastoreDAO;
import com.l2client.model.jme.NPCModel;
import com.l2client.model.network.NewCharSummary;
import com.l2client.model.network.NpcData;

/**
 * Administration of npc data. An npc is every entity besides the player character. From the current player
 * other players are npcs too.
 * 
 */
//TODO implement removal of npcs (DeleteObject message from server ??) and cache clearing
//TODO do we really need this, or is it sufficient to have a npc factory as everything else is in entitymanager?
public class NpcHandler {
	
	private static Logger log = Logger.getLogger(NpcHandler.class.getName());

	/**
	 * Add an entity to the internal storage. Currently the visua creation of an entity is
	 * present here too which should be handled by an NpcBuilder
	 * 
	 * @param e The EntityData the npc is based on
	 */
	// TODO move generation of visual out
	public void add(NpcData e) {
		if (e.getObjectId() > 0) {
			addNpc(e);
		}
	}


	private void addNpc(NpcData e) {
		if (e.getCharId() > 0)
			log.info("charinfo of " + e.getObjectId()
					+ " present coords are:" + e.getX() + "," + e.getY()
					+ "," + e.getZ());
		else{
			log.info("npcinfo of " + e.getObjectId()
					+ " present coords are:" + e.getX() + "," + e.getY()
					+ "," + e.getZ());
			if(e.getName()== null||e.getName().length()<=0 )
				e.setName(Singleton.get().getDataManager().getNpcName(((NpcData)e).getTemplateId()));
		}
		IdentityComponent  id = (IdentityComponent) EntityManager.get().getComponent(e.getObjectId(), IdentityComponent.class);

		
		if(id != null)
			updateNpc(id,e);
		else
			createNpc(e);		
	}


	private void updateNpc(IdentityComponent id, NpcData e) {
System.out.println("FIXME update of NPC received, but so far not fully implemented");
		//update position
		SimplePositionComponent pos = (SimplePositionComponent) EntityManager.get().getComponent(id.getId(), SimplePositionComponent.class);
		//update l2j
		L2JComponent l2j = (L2JComponent) EntityManager.get().getComponent(id.getId(), L2JComponent.class);
		//update env
		EnvironmentComponent env = (EnvironmentComponent) EntityManager.get().getComponent(id.getId(), EnvironmentComponent.class);
		//update visual
		VisualComponent vis = (VisualComponent) EntityManager.get().getComponent(id.getId(), VisualComponent.class);

		updateComponents(e, id.getEntity(), pos,l2j,env,vis);
	}


	private void updateComponents(NpcData e, Entity ent, SimplePositionComponent pos,
			L2JComponent l2j, EnvironmentComponent env, VisualComponent vis) {
		
		
		if(e.getCharId() > 0)
			l2j.isPlayer = true;
		l2j.l2jEntity = e;
		
		// TODO visual representation
		NewCharSummary n = new NewCharSummary();
		n.name = e.getName();
		n.templateId = e.getTemplateId();
		// TODO char inventory for model equip
		n.race = e.getRace();
		
		NPCModel v = new NPCModel(n);
		v.attachVisuals();
		//FIXME why has visual and ent its own location ?!?!? 
//		v.setLocalTranslation(pos.currentPos);
		vis.vis = v;
		
		//FIXME why has visual and ent its own location ?!?!? 
		ent.setLocalTranslation(pos.currentPos);
		ent.setLocalRotation(new Quaternion().fromAngleAxis(e.getHeading(), Vector3f.UNIT_Y));
		ent.attachChild(v);
		
		SceneManager.get().changeWalkerNode(ent,0);
	}


	private void createNpc(NpcData e) {
		
		
		Entity ent = EntityManager.get().createEntity(e.getObjectId());
		SimplePositionComponent pos = new SimplePositionComponent();
		L2JComponent l2j = new L2JComponent();
		VisualComponent vis = new VisualComponent();
		EnvironmentComponent env = new EnvironmentComponent();
		
		//done here extra as in update values will be left untouched
		pos.startPos.set(e.getX(), e.getY(), e.getZ());
		pos.currentPos.set(pos.startPos);
		pos.goalPos.set(pos.currentPos);
		pos.walkSpeed = e.getWalkSpeed();
		pos.runSpeed = e.getRunSpeed();
		pos.running = e.isRunning();
		pos.heading = e.getHeading();
		pos.targetHeading = pos.heading;
		
		updateComponents(e, ent, pos, l2j, env, vis);
		
		EntityManager.get().addComponent(ent.getId(), env);
		EntityManager.get().addComponent(ent.getId(), l2j);
		EntityManager.get().addComponent(ent.getId(), pos);		
		EntityManager.get().addComponent(ent.getId(), vis);

		
		
		PositioningSystem.get().addComponentForUpdate(pos);
		JmeUpdateSystem.get().addComponentForUpdate(pos);
		AnimationSystem.get().addComponentForUpdate(env);

	}
}
