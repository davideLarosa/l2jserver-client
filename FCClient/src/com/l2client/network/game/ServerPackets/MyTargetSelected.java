package com.l2client.network.game.ServerPackets;

import com.jme3.math.ColorRGBA;
import com.l2client.app.Singleton;
import com.l2client.component.EnvironmentComponent;
import com.l2client.component.PositioningComponent;
import com.l2client.component.PositioningSystem;
import com.l2client.component.TargetComponent;
import com.l2client.controller.entity.EntityManager;

public class MyTargetSelected extends GameServerPacket {

	@Override
	public void handlePacket()
	{
		log.fine("Read from Server "+this.getClass().getSimpleName());
		int id = readD();
		int color = readH();
		//writeD(0x00);
		EntityManager em = Singleton.get().getEntityManager();
		int pID = _client.getCharHandler().getSelectedObjectId();
		TargetComponent tc = (TargetComponent) em.getComponent(pID, TargetComponent.class);
		if(tc != null){
			tc.setTarget(id);
			tc.color = toColorRGBA(color);
		}
		PositioningComponent pcpos = (PositioningComponent) em.getComponent(pID, PositioningComponent.class);
		PositioningComponent npcpos = (PositioningComponent) em.getComponent(id, PositioningComponent.class);
		if(pcpos != null && npcpos != null){
			pcpos.targetHeading = PositioningSystem.getHeading(pcpos.position, npcpos.position);
			//npcpos.targetHeading = PositioningSystem.getHeading(npcpos.position, pcpos.position);
System.out.println("MyTargetSelected: Set targetheading for "+pID+" to "+pcpos.targetHeading+" looking at "+id);			
		}
		EnvironmentComponent env = (EnvironmentComponent) Singleton.get().getEntityManager().getComponent(pID, EnvironmentComponent.class);
		if (env != null){
			env.changed = true;
		}else
			log.severe("No EnvironmentComponent found with entity id "+pID+", perhaps just create one?");

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
