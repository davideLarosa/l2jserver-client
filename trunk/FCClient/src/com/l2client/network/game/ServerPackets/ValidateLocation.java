package com.l2client.network.game.ServerPackets;

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
		Vector3f tPos = new Vector3f();
		int x = readD();
		int z = readD();
		int y = readD();
		tPos.x = ServerValues.getClientCoord(x);
		tPos.y = ServerValues.getClientCoord(y);
		tPos.z = ServerValues.getClientCoord(z);
//System.out.println("VALIDATE:"+objId+" at:("+x+","+y+","+z+") -> "+tPos);
		float heading = ServerValues.getClientHeading(readD());
		log.fine("Coords:"+tPos.x+","+tPos.y+","+tPos.z+" for "+objId);

		PositioningComponent pos = (PositioningComponent) Singleton.get().getEntityManager().getComponent(objId, PositioningComponent.class);
		if (pos != null){
//			float cHead;
//			Vector3f cPos = new Vector3f();
//			synchronized (pos) {	
//				cPos.x = pos.currentPos.x;
//				cPos.y = pos.currentPos.y;
//				cPos.z = pos.currentPos.z;
//				cHead = pos.heading;		
//			}
//			pos = null;
//			float savedHeight = tPos.y;//FIXME HEIGHT IGNORE
//			tPos.y = 0f;
//
//			//FIXME also do an adjustmenst to get closer to server loc
//			if(tPos.distance(cPos)>1f)
//				log.severe(objId+" positions differ by more than 1 unit cX:"+cPos.x+" cY:"+cPos.y+" cZ:"+cPos.z
//						+" tX:"+tPos.x+" tY:"+tPos.y+" tZ:"+tPos.z);
//			if(FastMath.abs(heading-cHead)> FastMath.PI/180f*5f)//difference by more than 5 degrees
//				log.severe(objId+" heads differ by more than 5 deg cHeading:"+cHead+" tHeading:"+heading);
//
//			cPos.y = savedHeight;
//			tPos.y = savedHeight;//FIXME we say we are on tgt !?!?!?
			Singleton.get().getPosSystem().initMoveTo(objId, tPos.x, tPos.y, tPos.z, pos.position.x, pos.position.y, pos.position.z);
			if(Singleton.get().getEntityManager().isPlayerComponent(pos)){
				ValidatePosition pack = new ValidatePosition(pos.position, pos.heading);
				_client.sendGamePacket(pack);
			}
		}
		else
			log.severe("No SimplePositioningComonent found with entity id "+objId+" unable to send ValidateLocation");		
	}
}
