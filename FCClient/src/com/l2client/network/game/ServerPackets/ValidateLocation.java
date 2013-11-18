package com.l2client.network.game.ServerPackets;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.l2client.app.Singleton;
import com.l2client.component.PositioningComponent;
import com.l2client.model.l2j.ServerValues;
import com.l2client.network.game.ClientPackets.ValidatePosition;

/**
 * Validate the position of the client requested, sends as ValidatePosition in return
 */
public final class ValidateLocation extends GameServerPacket
{

	/**
	 * Reads the objectId and the current and target vector 
	 */
	@Override
	public void handlePacket()
	{
		log.finer("Read from Server "+this.getClass().getSimpleName());
		int objId = readD();

		int x = readD();
		int y = readD();
		int z = readD();
		Vector3f tPos = ServerValues.getClientCoords(x, y, z);
System.out.println("VALIDATE:"+objId+" at:("+x+","+y+","+z+") -> "+tPos);
		float heading = ServerValues.getClientHeading(readD());
		log.fine("Coords:"+tPos.x+","+tPos.y+","+tPos.z+" for "+objId);

		PositioningComponent pos = (PositioningComponent) Singleton.get().getEntityManager().getComponent(objId, PositioningComponent.class);
		if (pos != null){
			//TODO check heading is really of any relevance
			if(FastMath.abs(heading-pos.heading)> FastMath.PI/180f*5f)//difference by more than 5 degrees
				log.severe(objId+" heads differ by more than 5 deg cHeading:"+pos.heading+" tHeading:"+heading);
			
			//also do an adjustment to get closer to server location
			if(new Vector2f(tPos.x, tPos.z).distance(new Vector2f(pos.position.x,pos.position.z)) > 1f) {
				log.severe(objId+" positions differ in x/z by more than 1f current:"+pos.position+" target:"+tPos);

				Singleton.get().getPosSystem().initMoveTo(objId, tPos.x, tPos.y, tPos.z, pos.position.x, pos.position.y, pos.position.z);
			}
			if(Singleton.get().getEntityManager().isPlayerComponent(pos)){
				ValidatePosition pack = new ValidatePosition(pos.position, pos.heading);
				_client.sendGamePacket(pack);
			}
		}
		else
			log.severe("No SimplePositioningComonent found with entity id "+objId+" unable to send ValidateLocation");		
	}
}
