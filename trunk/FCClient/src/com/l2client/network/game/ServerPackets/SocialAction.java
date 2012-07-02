package com.l2client.network.game.ServerPackets;

import com.l2client.animsystem.jme.actions.CallActions;
import com.l2client.component.AnimationSystem;

/**
 * 		writeC(0x27);
		writeD(_charObjId);
		writeD(_actionId);
		
		
		From RequestActionUse:
		
		// Social Packets
			case 12: // Greeting
				tryBroadcastSocial(2);
				break;
			case 13: // Victory
				tryBroadcastSocial(3);
				break;
			case 14: // Advance
				tryBroadcastSocial(4);
				break;
			case 24: // Yes
				tryBroadcastSocial(6);
				break;
			case 25: // No
				tryBroadcastSocial(5);
				break;
			case 26: // Bow
				tryBroadcastSocial(7);
				break;
			case 29: // Unaware
				tryBroadcastSocial(8);
				break;
			case 30: // Social Waiting
				tryBroadcastSocial(9);
				break;
			case 31: // Laugh
				tryBroadcastSocial(10);
				break;
			case 33: // Applaud
				tryBroadcastSocial(11);
				break;
			case 34: // Dance
				tryBroadcastSocial(12);
				break;
			case 35: // Sorrow
				tryBroadcastSocial(13);
				break;
			case 62: // Charm
				tryBroadcastSocial(14);
				break;
			case 66: // Shyness
				tryBroadcastSocial(15);
				break;
 *
 */
public class SocialAction extends GameServerPacket {

	@Override
	public void handlePacket() {
		int entityId = readD();
		int action = readD();

		switch (action) {
		case 2: //Greetings
			AnimationSystem.get().callAction(CallActions.Taunt, entityId);
			break;
		case 3: // Victory
			AnimationSystem.get().callAction(CallActions.Celebrate, entityId);
			break;
		default: // nothing
			log.finer("Social action not implemented for id:" + action);
		}
	}

}
