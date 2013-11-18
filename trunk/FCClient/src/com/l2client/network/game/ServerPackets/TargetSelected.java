package com.l2client.network.game.ServerPackets;

import com.jme3.math.ColorRGBA;
import com.l2client.app.Singleton;
import com.l2client.component.EnvironmentComponent;
import com.l2client.component.PositioningComponent;
import com.l2client.component.PositioningSystem;
import com.l2client.component.TargetComponent;
import com.l2client.controller.entity.EntityManager;

public class TargetSelected extends GameServerPacket {

	@Override
	public void handlePacket()
	{
		log.fine("Read from Server "+this.getClass().getSimpleName());
		int id = readD();
		int target = readD();
//		readD();//x
//		readD();//y
//		readD();//z
//		readD();//0x00
		//player receives an own my target selected in addition so we drop this
		int pID = _client.getCharHandler().getSelectedObjectId();
		if(id != pID){
			//TODO what should we do in this message which just says id starts targeting tgt
			_client.getChatHandler().receiveMessage(id,0 /*all*/,Integer.toString(id),"I see you baggard! <"+target+">");
			EntityManager em = Singleton.get().getEntityManager();
			TargetComponent tc = (TargetComponent) em.getComponent(id, TargetComponent.class);
			if(tc != null){
				tc.setTarget(target);
			}
			PositioningComponent pcpos = (PositioningComponent) em.getComponent(target, PositioningComponent.class);
			PositioningComponent npcpos = (PositioningComponent) em.getComponent(id, PositioningComponent.class);
			if(pcpos != null && npcpos != null){
				//pcpos.targetHeading = PositioningSystem.getHeading(pcpos.position, npcpos.position);
				npcpos.targetHeading = PositioningSystem.getHeading(npcpos.position, pcpos.position);
System.out.println("TargetSelected: Set heading for "+id+" to "+npcpos.targetHeading+" looking at "+target);
			}
			EnvironmentComponent env = (EnvironmentComponent) em.getComponent(id, EnvironmentComponent.class);
			if (env != null){
				env.changed = true;
			}else
				log.severe("No EnvironmentComponent found with entity id "+id+", perhaps just create one?");
		}
	}
	
	/**
	 *color 	-xx -> -9 	red<p>
 * 			-8  -> -6	light-red<p>
 * 			-5	-> -3	yellow<p>
 * 			-2	-> 2    white<p>
 * 			 3	-> 5	green<p>
 * 			 6	-> 8	light-blue<p>
 * 			 9	-> xx	blue<p>
 * <p>
 * usually the color equals the level difference to the selected target
	 * @param color
	 * @return
	 */
	private ColorRGBA toColorRGBA(int color){
		if(color <= -9)
			return ColorRGBA.Red;
		if(color <= -6)
			return ColorRGBA.Pink;
		if(color <= -3)
			return ColorRGBA.Yellow;
		if(color <= 2)
			return ColorRGBA.White;
		if(color <= 5)
			return ColorRGBA.Green;
		if(color <= 8)
			return ColorRGBA.Cyan;
		
		return ColorRGBA.Blue;
	}

}
