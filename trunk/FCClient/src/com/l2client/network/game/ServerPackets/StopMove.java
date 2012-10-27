package com.l2client.network.game.ServerPackets;

import com.jme3.math.Vector3f;
import com.l2client.app.Singleton;
import com.l2client.component.EnvironmentComponent;
import com.l2client.component.SimplePositionComponent;
import com.l2client.model.l2j.ServerValues;

/**
 * Stop right at the described spot
 */
public final class StopMove extends GameServerPacket
{

	/**
	 * Reads the objectId and the current and target vector 
	 */
	@Override
	public void handlePacket()
	{
		log.finer("Read from Server "+this.getClass().getSimpleName());
		int objId = readD();
		Vector3f tPos = new Vector3f();
		tPos.x = ServerValues.getClientCoord(readD());
		tPos.z = ServerValues.getClientCoord(readD());
		tPos.y = ServerValues.getClientCoord(readD());
log.info("Coords:"+tPos.x+","+tPos.y+","+tPos.z);
		float heading = ServerValues.getClientHeading(readD());

		SimplePositionComponent pos = (SimplePositionComponent) Singleton.get().getEntityManager().getComponent(objId, SimplePositionComponent.class);
		if (pos != null){
				log.finest("trigger STOP of " + objId + " at " + tPos.x + "," + tPos.y
							+ "," + tPos.z + " heading " + heading);
		
				synchronized (pos) {						
						pos.startPos.set(tPos.x, 0, tPos.z);//FIXME ignores height
						if(pos.currentPos.distance(pos.startPos)> .9f){
							log.warning("WARP !! stop of " + objId + " at " + pos.startPos +" but current position is "+pos.currentPos+" goal is "+pos.goalPos+" speed "+(pos.running?pos.runSpeed:pos.walkSpeed));
							pos.goalPos.set(pos.startPos);
							pos.startPos.set(pos.currentPos);
						} else {
							pos.goalPos.set(pos.startPos);
							pos.currentPos.set(pos.startPos);
						}
						pos.targetHeading = heading;
						log.finest("New Heading "+pos.targetHeading+" received for Ent "+objId);						
				}
				EnvironmentComponent env = (EnvironmentComponent) Singleton.get().getEntityManager().getComponent(objId, EnvironmentComponent.class);
				if (env != null){
					env.movement = -1;
					env.changed = true;
				}else
					log.severe("No EnvironmentComponent found with entity id "+objId+", perhaps just create one?");
		}
		else
			log.severe("No SimplePositioningComonent found with entity id "+objId+" unable to send ValidateLocation");		
	}
}
