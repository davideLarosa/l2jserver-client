package com.l2client.model.jme;

import com.jme3.bounding.BoundingBox;
import com.jme3.scene.Node;
import com.l2client.animsystem.InputProvider;
import com.l2client.animsystem.jme.JMEAnimationController;
import com.l2client.app.Assembler2;
import com.l2client.model.network.NewCharSummary;

public class NPCModel extends VisibleModel {

	private static final long serialVersionUID = 1L;

	public NPCModel(NewCharSummary sel) {
		super(sel);
		// TODO Auto-generated constructor stub
System.out.println("NPC with name "+sel.name+" and template "+sel.templateId);
	}

	protected Node createVisuals() {

		if (charSelection != null) {
	
			
			Node n = null;
			//Assembler2.getModel3("dwarfwarrior");
			//FIXME move this out to client db
			switch(charSelection.templateId){
			case 18342: n = Assembler2.getModel3("goblin");break;//Gremlin
			case 30340: n = Assembler2.getModel3("pelfmmage");break;//Newbie Helper
			case 30370: n = Assembler2.getModel3("pelffmage");break;//Nerupa
			case 31848: n = Assembler2.getModel3("goblin");break;//Pixy
			default: n = super.createVisuals();
			}
			
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
				else
					System.out.println("ERROR no JMEAnimationController on Model");
			}
		}

		return vis;
	}

}
