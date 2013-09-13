package com.l2client.controller.handlers;

import java.util.logging.Logger;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.l2client.app.Singleton;
import com.l2client.component.Component;
import com.l2client.component.EnvironmentComponent;
import com.l2client.component.IdentityComponent;
import com.l2client.component.L2JComponent;
import com.l2client.component.PositioningComponent;
import com.l2client.component.VisualComponent;
import com.l2client.controller.entity.Entity;
import com.l2client.controller.entity.EntityManager;
import com.l2client.model.jme.NPCModel;
import com.l2client.model.l2j.ServerValues;
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


	private void addNpc(final NpcData e) {
		if (e.getCharId() > 0)
			log.info("charinfo of " + e.getObjectId()
					+ " present coords are:" + e.getX() + "," + e.getY()
					+ "," + e.getZ()+" -> "+ServerValues.getServerString(e.getX(), e.getY(), e.getZ()));
		else{
			log.info("npcinfo of " + e.getObjectId()
					+ " present coords are:" + e.getX() + "," + e.getY()
					+ "," + e.getZ()+" -> "+ServerValues.getServerString(e.getX(), e.getY(), e.getZ()));
			if(e.getName()== null||e.getName().length()<=0 )
				e.setName(Singleton.get().getDataManager().getNpcName(((NpcData)e).getTemplateId()));
		}
		final IdentityComponent  id = (IdentityComponent) Singleton.get().getEntityManager().getComponent(e.getObjectId(), IdentityComponent.class);

////		//this could take a while so let it run async..
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
				if(id != null)
					updateNpc(id,e);
				else
					createNpc(e);
//			}
//		}).start();
		
	}


	private void updateNpc(IdentityComponent id, NpcData e) {

//		//update position
//		SimplePositionComponent pos = (SimplePositionComponent) Singleton.get().getEntityManager().getComponent(id.getId(), SimplePositionComponent.class);
//		//update l2j
		L2JComponent l2j = (L2JComponent) Singleton.get().getEntityManager().getComponent(id.getId(), L2JComponent.class);
		if(l2j != null){
			l2j.l2jEntity.updateFrom(e);
		} else {
			log.severe("FIXME update of NPC "+id+"received, but no L2JComponent found");
		}
//		//update env
//		EnvironmentComponent env = (EnvironmentComponent) Singleton.get().getEntityManager().getComponent(id.getId(), EnvironmentComponent.class);
//		//update visual
//		VisualComponent vis = (VisualComponent) Singleton.get().getEntityManager().getComponent(id.getId(), VisualComponent.class);
//
//		updateComponents(e, id.getEntity(), pos,l2j,env,vis);
	}


	private void updateComponents(final NpcData e, final Entity ent, final PositioningComponent pos,
			L2JComponent l2j, final EnvironmentComponent env, final VisualComponent vis) {
		
		ent.setLocalTranslation(pos.position);
		ent.setLocalRotation(new Quaternion().fromAngleAxis(e.getHeading(), Vector3f.UNIT_Y));
		
		pos.position.set(e.getX(), e.getY(), e.getZ());
		pos.startPos.set(pos.position);
		pos.goalPos.set(pos.startPos);
		pos.walkSpeed = e.getWalkSpeed();
		pos.runSpeed = e.getRunSpeed();
		pos.running = e.isRunning();
		pos.heading = e.getHeading();
		pos.targetHeading = pos.heading;
		pos.teleport = true;
		
		Singleton.get().getPosSystem().addComponentForUpdate(pos);
		
		if(e.getCharId() > 0)
			l2j.isPlayer = true;
		l2j.l2jEntity = e;

		//this could take a while so let it run async..
		new Thread(new Runnable() {
			
			@Override
			public void run() {
	
				// TODO visual representation
				NewCharSummary n = new NewCharSummary();
				n.name = e.getName();
				n.templateId = e.getTemplateId();
				n.objectId = e.getObjectId();
				// TODO char inventory for model equip
				n.race = e.getRace();
				
				NPCModel v = new NPCModel(n);
				v.attachVisuals();
				vis.vis = v;
				addEntityIdToGeoms(e.getObjectId(), v);
				ent.setName(v.getName());
				ent.attachChild(v);
				
				Singleton.get().getSceneManager().changeWalkerNode(ent,0);
				
				Singleton.get().getJmeSystem().addComponentForUpdate(pos);
				Singleton.get().getAnimSystem().addComponentForUpdate(env);
			}
		}).start();
	}


	private void createNpc(NpcData e) {
		
		EntityManager em = Singleton.get().getEntityManager();
		PositioningComponent pos = new PositioningComponent();
		L2JComponent l2j = new L2JComponent();
		VisualComponent vis = new VisualComponent();
		EnvironmentComponent env = new EnvironmentComponent();
		//FIXME parallel create problems, synchronize creation and essential components or at least create/check of components
		Entity ent = em.createEntity(e.getObjectId());
		em.addComponent(ent.getId(), pos);
		em.addComponent(ent.getId(), vis);
		em.addComponent(ent.getId(), env);
		em.addComponent(ent.getId(), l2j);
		
		updateComponents(e, ent, pos, l2j, env, vis);//vis might take some time so we already added pos for updates


	}
	
	//FIXME hmm, why have we done this ? Pre component system code?
	private void addEntityIdToGeoms(int id, Spatial spatial){
		if(spatial instanceof Geometry)
			spatial.setUserData(Entity.ENTITY_ID, new Integer(id));
		if(spatial instanceof Node){
			Node node = (Node)spatial;
			for(Spatial n  : node.getChildren())
				addEntityIdToGeoms(id, n);
		}
	}

	public void remove(int obj) {
		Singleton s = Singleton.get();
		IdentityComponent id = (IdentityComponent) s.getEntityManager().getComponent(obj, IdentityComponent.class);
		if(id != null){
			Entity e = id.getEntity();
			if(e != null)
				s.getSceneManager().changeWalkerNode(e,1);
			
			//FIXME check this is working correctly, what if we delete one which is currently updated, better queue for removal.
			Component pos = s.getEntityManager().getComponent(obj, PositioningComponent.class);
			s.getPosSystem().removeComponentForUpdate(pos);
			s.getJmeSystem().removeComponentForUpdate(pos);
			Component env = s.getEntityManager().getComponent(obj, EnvironmentComponent.class);
			s.getAnimSystem().removeComponentForUpdate(env);
		}
		s.getEntityManager().deleteEntity(obj);
		
	}
}
