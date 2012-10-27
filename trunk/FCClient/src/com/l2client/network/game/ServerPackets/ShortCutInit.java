package com.l2client.network.game.ServerPackets;

import java.util.HashMap;

import javax.swing.SwingUtilities;

import com.l2client.app.Singleton;
import com.l2client.gui.actions.BaseUsable;


public class ShortCutInit extends GameServerPacket {

	@Override
	public void handlePacket()
	{
		log.finer("Read from Server "+this.getClass().getSimpleName());
		int shortCuts = readD();
		if(shortCuts > 0){
			final HashMap<Integer, BaseUsable> map = new HashMap<Integer, BaseUsable>();
		for(int i = 0; i<shortCuts;i++)
		{
			int type = readD();
			int slot = readD();
			switch(type){
			case 1://Item
				int itemId = readD();//writeD(sc.getId());
				readD();//writeD(0x01);
				int reuseGroup = readD();//writeD(sc.getSharedReuseGroup());
				readD();//writeD(0x00);
				readD();//writeD(0x00);
				readH();//writeH(0x00);
				readH();//writeH(0x00);
				log.finer("Shortcut "+i+" slot:"+slot+" Item:"+itemId);
				break;
			case 2://Skill
				int skillId = readD();//writeD(sc.getId());
				int skillLevel = readD();//writeD(0x01);
				readC();//writeC(0x00);
				readD();//writeD(0x01);
				log.finer("Shortcut "+i+" slot:"+slot+" Skill:"+skillId);
				map.put(slot, Singleton.get().getActionManager().getAction(skillId));
				break;
			case 3://Action
				int actionId = readD();
				log.finer("Shortcut "+i+" slot:"+slot+" Action:"+actionId);
				readD();//readD();
				map.put(slot, Singleton.get().getActionManager().getAction(actionId));
				break;
			case 4://Macro
			case 5://Recipe
			default:		
				int id = readD();
				readD();
				log.finer("Shortcut "+i+" slot:"+slot+": todo type:"+type+" id:"+id);
			}
		}
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				Singleton.get().getGuiController().displayShortCutPanel(map);
//				Singleton.get().getGuiController().displayShortCutPanel();
				Singleton.get().getGuiController().displaySkillAndActionsPanel(map);
			}
		});
		} else {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					Singleton.get().getGuiController().displayShortCutPanel();
					Singleton.get().getGuiController().displaySkillAndActionsPanel();
				}
			});
		}
	}
}
