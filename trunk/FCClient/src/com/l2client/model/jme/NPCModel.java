package com.l2client.model.jme;

import com.jme3.scene.Node;
import com.l2client.animsystem.InputProvider;
import com.l2client.animsystem.jme.JMEAnimationController;
import com.l2client.app.Assembler2;
import com.l2client.app.Singleton;
import com.l2client.model.network.NewCharSummary;

public class NPCModel extends VisibleModel {

	public NPCModel(NewCharSummary sel) {
		super(sel);
		logger.info("Creating NPC with name "+sel.name+" and template "+sel.templateId);
	}

	protected Node createVisuals() {

		if (charSelection != null) {
	
			
			Node n = null;
			String gamemodel = Singleton.get().getDataManager().getNpcGameModel(charSelection.templateId);
			if(gamemodel != null && gamemodel.length() > 0)
				n = Assembler2.getModel4(gamemodel, true, true);
			else {
				logger.warning("No game model found in npc table for entity:"+charSelection.templateId+" name:"+Singleton.get().getDataManager().getNpcName(charSelection.templateId));
				n = super.createVisuals();
			}

			
			if (n != null) {
				vis = n; //MeshCloner.cloneMesh(n);
				vis.setName(name);
//				vis.setLocalRotation(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_X));

				JMEAnimationController animControl = (JMEAnimationController) vis
							.getControl(JMEAnimationController.class);
				if(animControl != null)
					animControl.setInput(InputProvider.NOINPUT);
				//FIXME this is the case with the troll model
//				else
//					logger.severe("ERROR no JMEAnimationController on Model");
			}
		}

		return vis;
	}

}
