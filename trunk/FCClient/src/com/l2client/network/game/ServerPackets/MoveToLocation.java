package com.l2client.network.game.ServerPackets;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.l2client.app.L2JClient;
import com.l2client.component.EnvironmentComponent;
import com.l2client.component.PositioningSystem;
import com.l2client.component.SimplePositionComponent;
import com.l2client.controller.entity.EntityManager;
import com.l2client.model.l2j.ServerValues;
import com.l2client.model.network.ClientFacade;
import com.l2client.network.game.ClientPackets.StartRotating;

/**
 * Movement packet from the server, move information contains:
 *  
 * the object id of the object to be moved
 * current position vector in integer format (which will be scaled down by ServerCoordinates.getScaleFactor()
 * target position vector in integer format (which will be scaled down by ServerCoordinates.getScaleFactor()
 * 
 * 
 */
public final class MoveToLocation extends GameServerPacket
{

	/**
	 * Reads the objectId and the current and target vector and triggers a move to
	 * action (should be moved out of the clientfacade to here
	 */
	@Override
	public void handlePacket()
	{
		log.fine("Read from Server "+this.getClass().getSimpleName());
		
		//getClientFacade().triggerMoveTo(
		int objId = readD();
		float tX = ServerValues.getClientCoord(readD());
		float tt = ServerValues.getClientCoord(readD());
		float tZ = ServerValues.getClientCoord(readD());
		float tY = tZ;
		tZ = tt;
		float cX = ServerValues.getClientCoord(readD());
		float ct = ServerValues.getClientCoord(readD());
		float cZ = ServerValues.getClientCoord(readD());
		float cY = cZ;
		cZ = ct;

		if(tX==cX && tY == cY && tZ == cZ)
			return;
		
		SimplePositionComponent pos = (SimplePositionComponent) EntityManager.get().getComponent(objId, SimplePositionComponent.class);
		if (pos != null){
			log.fine("trigger move of " + objId + " from " + cX + "," + cY
						+ "," + cZ + " to " + tX + "," + tY + "," + tZ);
	
			synchronized (pos) {	
				
//				//DEBUG of REISSUE OF MOVETO COMMANDS
//				Vector3f cP = new Vector3f(cX, 0, cZ);
//				
//				Vector3f dir = pos.goalPos.subtract(pos.startPos).normalizeLocal();
//				Vector3f dir2 = cP.subtract(pos.startPos).normalizeLocal();
//				if(dir.x != dir.y && dir.z != 0.0f) {
//
//					if(dir.distance(dir2)<0.1f)
//						log.warning("New Current Position lies on old path client:"+pos.startPos.distance(pos.currentPos)+" server:"+pos.startPos.distance(cP) );
//					else
//						log.info("WARP !! move of " + objId + " from " + cX + "," + cY
//								+ "," + cZ + " to " + tX + "," + tY + "," + tZ +" but current position is "+pos.currentPos+" speed:"+pos.walkSpeed);
//				}
				//currently ignore Heights!!
				//there can be several calls to move to the desired location
				//to prevent setting the start and current pos we check before if the goal is still the same

					
					pos.startPos.set(cX,0,cZ);
					if(pos.currentPos.distance(pos.startPos)> .9f){
						log.warning("WARP !! move of " + objId + " from " + cX + "," + cY
								+ "," + cZ + " to " + tX + "," + tY + "," + tZ +" but current position is "+pos.currentPos+" goal is "+pos.goalPos+" speed "+(pos.running?pos.runSpeed:pos.walkSpeed));
					}
					pos.goalPos.set(tX,0,tZ);
					pos.currentPos.set(pos.startPos);
					
					//FIXME simplify this calc of the degrees to rotate to
					float angle = pos.goalPos.subtract(pos.startPos).normalizeLocal().angleBetween(Vector3f.UNIT_Z);
					pos.targetHeading = angle;
					log.info("New Heading "+angle+" received for Ent "+objId);
					//It seems this is not needed, as the receiving packets will only be for players
//					float rot = PositioningSystem.angleBetween(angle, pos.heading*FastMath.RAD_TO_DEG);
//					log.severe("Sending rotation request for "+objId+" angle "+ServerValues.getServerHeading(rot));
//					ClientFacade.get().sendPacket(new StartRotating(ServerValues.getServerHeading(rot), rot <0f?0:1));
					
			}
			EnvironmentComponent env = (EnvironmentComponent) EntityManager.get().getComponent(objId, EnvironmentComponent.class);
			if (env != null){
				if(pos.running)
					env.movement = 1;
				else
					env.movement = 0;
				env.changed = true;
			}else
				log.severe("No EnvironmentComponent found with entity id "+objId+", perhaps just create one?");

		}
		else
			log.severe("No SimplePositioningComonent found with entity id "+objId+", perhaps just create one?");
		

		
	}
}
