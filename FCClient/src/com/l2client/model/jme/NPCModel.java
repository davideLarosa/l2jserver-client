package com.l2client.model.jme;

import com.jme3.bounding.BoundingBox;
import com.jme3.scene.Node;
import com.l2client.animsystem.InputProvider;
import com.l2client.animsystem.jme.JMEAnimationController;
import com.l2client.app.Assembler2;
import com.l2client.app.Singleton;
import com.l2client.model.network.NewCharSummary;

public class NPCModel extends VisibleModel {

	private static final long serialVersionUID = 1L;

	public NPCModel(NewCharSummary sel) {
		super(sel);
		logger.info("Creating NPC with name "+sel.name+" and template "+sel.templateId);
	}

	protected Node createVisuals() {

		if (charSelection != null) {
	
			
			Node n = null;
			String gamemodel = Singleton.get().getDataManager().getNpcGameModel(charSelection.templateId);
			if(gamemodel != null)
				n = Assembler2.getModel3(gamemodel);
			else
				n = super.createVisuals();

			
			if (n != null) {
//				//FIXME modelconverter should already have set this one, this is not the case -> NPE
				n.setModelBound(new BoundingBox());
				n.updateModelBound();
				n.updateGeometricState();
				vis = n; //MeshCloner.cloneMesh(n);
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
