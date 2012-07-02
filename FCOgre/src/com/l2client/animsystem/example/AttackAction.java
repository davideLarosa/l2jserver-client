package com.l2client.animsystem.example;

import com.l2client.animsystem.Action;
import com.l2client.animsystem.Animation;
import com.l2client.animsystem.InputProvider;
import com.l2client.animsystem.Mediator;
import com.l2client.animsystem.Channel.Channels;

public class AttackAction extends Action {

	@Override
	protected Animation evaluate(Mediator med) {
		Animation ret = null;
		InputProvider in = med.getInput();
		if (AttackType.None.equals(in.getInput(AttackType.class))) {
			if (med.setLockCheck(Channels.AllChannels, 2)) {
				ret = med.getAnimation();
				ret.setLevel(2);
				ret.setChannel(med.getChannel(Channels.AllChannels));
				if (rand.nextInt(9) > 5) {
					ret.setName("Kick");
				} else {
					if (rand.nextInt(1) > 0) {
						ret.setName("SideKick");
					} else {
						ret.setName("Spin");
					}
				}
				ret.setBlendTime(0.5f);
			}
		} else {
			if (med.setLockCheck(Channels.AllChannels, 2)) {
				ret = med.getAnimation();
				ret.setLevel(2);
				ret.setChannel(med.getChannel(Channels.AllChannels));
				if (rand.nextInt(9) > 4) {
					ret.setName("Attack1");
				} else {
					if (rand.nextInt(1) > 0) {
						ret.setName("Attack2");
					} else {
						ret.setName("Attack3");
					}
					ret.setBlendTime(0.5f);
				}
			}
		}
		return ret;
	}
}