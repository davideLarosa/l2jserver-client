package com.l2client.network.game.ServerPackets;

import com.jme3.math.ColorRGBA;
import com.l2client.animsystem.jme.actions.CallActions;
import com.l2client.app.Singleton;
import com.l2client.component.EnvironmentComponent;
import com.l2client.component.VisualComponent;
import com.l2client.controller.entity.EntityManager;

public class Attack extends GameServerPacket {

	@Override
	public void handlePacket() {
		log.fine("Read from Server "+this.getClass().getSimpleName());
		int id = readD(); //writeD(_attackerObjId);
		int tgt = readD(); //writeD(_targetObjId);
		int damage = readD();//writeD(_hits[0]._damage);
		int flags = readC();//writeC(_hits[0]._flags);
		readD();//writeD(_x);
		readD();//writeD(_y);
		readD();//writeD(_z);
		//TODO what should we do in this message which just says id starts targeting tgt
		//no need to chat this, there comes a systemmessage with details therafter from the server
//		_client.getChatHandler().receiveMessage(id,0 /*all*/,Integer.toString(id)," hit "+tgt+" doing "+damage+" pts "+flags+" dmg.");
		log.fine("-------->"+id+" hit "+tgt+" doing "+damage+" pts "+flags+" dmg.");
		updateComponents(id, tgt, damage, flags);
		int hits = readH();//writeH(_hits.length - 1);
		if (hits > 1)
		{
			for (int i = 0; i < hits; i++)
			{
				tgt = readD();
				damage = readD();
				flags = readC();
				//TODO what should we do in this message which just says id starts targeting tgt
//				_client.getChatHandler().receiveMessage(id,0 /*all*/,Integer.toString(id)," hit "+tgt+" doing "+damage+" pts "+flags+" dmg.");
				log.fine("-------->"+id+" hit "+tgt+" doing "+damage+" pts "+flags+" dmg.");
				updateComponents(id, tgt, damage, flags);
			}
		}

		readD();//writeD(_tx);
		readD();//writeD(_ty);
		readD();//writeD(_tz);
	}

	private void updateComponents(int attack, int defend, int damage, int type){
		EntityManager em = Singleton.get().getEntityManager();
		EnvironmentComponent com = (EnvironmentComponent) em.getComponent(attack, EnvironmentComponent.class);
		if(com != null){
			com.damageDealt += damage;
			com.damageDealtType  |= type;
			com.changed = true;	
			Singleton.get().getAnimSystem().callAction(CallActions.DefaultAttack, attack);
		}
		com = (EnvironmentComponent) em.getComponent(defend, EnvironmentComponent.class);
		if(com != null){
			com.damageReceived += damage;
			com.damageReceivedType  |= type;
			com.changed = true;
			if(damage > 0)
				Singleton.get().getAnimSystem().callAction(CallActions.Wounded, defend);
			else
				Singleton.get().getAnimSystem().callAction(CallActions.Defend, defend);
			if(damage != 0){
				VisualComponent comp = (VisualComponent) Singleton.get().getEntityManager().getComponent(defend, VisualComponent.class);
				ColorRGBA color;
				switch(type){
				case 1:color = ColorRGBA.Orange;break;
				case 2:color = ColorRGBA.Yellow;break;
				default:color = ColorRGBA.Red;
				}
				//color = ColorRGBA.Orange;
				//color = ColorRGBA.White;
				comp.vis.addMessageLabel(Integer.toString(damage), color, 2, 1.5f);
			}
		}	
		
	}
}
