package com.l2client.app;

import java.util.HashMap;

import com.jme3.animation.AnimControl;
import com.jme3.animation.Skeleton;
import com.jme3.animation.SkeletonControl;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.l2client.animsystem.jme.JMEAnimationController;
import com.l2client.util.AnimationManager;

public class Assembler {

	Node model = new Node();
	Skeleton skeleton = null;
	HashMap<String, Geometry> meshes = new HashMap<String, Geometry>();
	private String animSet;


	private boolean isModelComplete(){
		if(skeleton != null && meshes.size() > 0 )
			return true;
		
		return false;
	}


	public void setSkeleton(Skeleton skel) {
		this.skeleton = skel;
		checkForUpdate();
	}

	public void addMesh(String name, Geometry mesh, boolean fireCheck) {
		this.meshes.put(name, mesh);
		if(fireCheck)
			checkForUpdate();
	}

	public void removeMesh(String name) {
		this.meshes.remove(name);
		checkForUpdate();
	}
	
	public HashMap<String, Geometry> getMeshes(){
		return meshes;
	}

	private void checkForUpdate() {
		if(isModelComplete()){
			int cntrls = model.getNumControls();
			for(int i = 0;i<cntrls;i++)
				model.removeControl(model.getControl(0));

			model.detachAllChildren();
			
			compileModel(model, meshes.values().toArray(new Geometry[meshes.size()]), false, false, skeleton);//, animProvider.getClass());
		}
	}

	private Node compileModel(Node model, Geometry[] geoms, boolean/** not used atm*/ shared, boolean hwSkin, 
			final Skeleton skeleton) {

        if (geoms != null) {
            Mesh[] meshes = new Mesh[geoms.length];

            // generate bind pose for mesh and add to skin-list
            // ONLY if not using shared geometry
            // This includes the shared geoemtry itself actually
            for (int i = 0; i < geoms.length; i++) {
            	meshes[i] = geoms[i].getMesh().cloneForAnim();
            	Geometry g = new Geometry(geoms[i].getName(), meshes[i]);
            	g.setMaterial(geoms[i].getMaterial());
                model.attachChild(g);
            } 

            Skeleton skel = new Skeleton(skeleton);
        	//TODO move this out into AnimationManger
			AnimControl c = new AnimControl(skel);//skeleton, animParts, template
			c.createChannel();
			c.setAnimationProvider(AnimationManager.get().getAnimationProvider(animSet));
//			model.addControl(c);
            model.addControl(new JMEAnimationController(c,animSet));
            SkeletonControl skeletonControl = new SkeletonControl(skel);//new SkeletonControl(meshes, skel);
//            skeletonControl.setSpatial(model);
            model.addControl(skeletonControl);		
        }
        return model;
    }


	public Node getModel() {
		checkForUpdate();
		return model;
	}


	public void setAnimParts(String animSet) {
		this.animSet = animSet;
	}

}
