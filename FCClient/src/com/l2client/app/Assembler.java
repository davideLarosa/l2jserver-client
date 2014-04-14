package com.l2client.app;

import java.util.HashMap;
import java.util.HashSet;

import jme3tools.optimize.GeometryBatchFactory;

import com.jme3.animation.AnimControl;
import com.jme3.animation.Skeleton;
import com.jme3.animation.SkeletonControl;
import com.jme3.material.Material;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.VertexBuffer.Usage;
import com.l2client.animsystem.jme.BoundsUpdateControl;
import com.l2client.animsystem.jme.JMEAnimationController;

/**
 * A low level 3d model assembler. Add up meshes for body parts, a skeleton and the 
 * animation partset provider and assembles the final jme animated model.
 * 
 * Used to have several base meshes and create permutations thereof 
 */
public class Assembler {

	Node model = new Node();
	Skeleton skeleton = null;
	HashMap<String, Geometry> meshes = new HashMap<String, Geometry>();
	private String animSet;
	private boolean useOptimization = false;
	private boolean useHWSkinning = true;


	/**
	 * @param useHWSkinning true if hardware/vertex skinnig is to be used
	 */
	public void setUseHWSkinning(boolean useHWSkinning) {
		this.useHWSkinning = useHWSkinning;
	}


	/**
	 * @param useOptimization true if optimization should be used
	 */
	public void setUseOptimization(boolean useOptimization) {
		this.useOptimization = useOptimization;
	}


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
			
			compileModel(model, meshes.values().toArray(new Geometry[meshes.size()]), false, skeleton);//, animProvider.getClass());
		}
	}

	private Node compileModel(Node model, Geometry[] geoms, boolean/** not used atm*/ shared, final Skeleton skeleton) {

        if (geoms != null) {
            
            Mesh[] meshes = new Mesh[geoms.length];
            
            HashSet<Material> materials = new HashSet<Material>();

            // generate bind pose for mesh and add to skin-list
            // ONLY if not using shared geometry
            // This includes the shared geoemtry itself actually
            for (int i = 0; i < geoms.length; i++) {
            	meshes[i] = geoms[i].getMesh().cloneForAnim();

            	Geometry g = new Geometry(geoms[i].getName(), meshes[i]);
            	Material m = geoms[i].getMaterial();
            	g.setMaterial(m);
            	materials.add(m);
                model.attachChild(g);
            } 

            if(useOptimization) {
            	model = GeometryBatchFactory.optimize(model, false);
            	//fix for bug in batchfactory removing empty hw buffers
            	fixHWModels(model);
            }
            
            Skeleton skel = new Skeleton(skeleton);
        	//TODO move this out into AnimationManger
			AnimControl c = new AnimControl(skel);//skeleton, animParts, template
			c.createChannel();
			c.setAnimationProvider(Singleton.get().getAnimManager().getAnimationProvider(animSet));
            model.addControl(new JMEAnimationController(c,animSet));
            SkeletonControl skeletonControl = new SkeletonControl(skel);
            model.addControl(skeletonControl);

            skeletonControl.setHardwareSkinningPreferred(useHWSkinning);

            BoundsUpdateControl bc = new BoundsUpdateControl();
            bc.setSkeleton(skel);
            model.addControl(bc);
            model.setShadowMode(ShadowMode.CastAndReceive);
            
        }
        return model;
    }

	private void fixHWModels(Spatial model) {
		if(model instanceof Geometry){
			fixHWSkinningBuffers(((Geometry)model).getMesh());
			return;
		} else {
			if(model instanceof Node){
				Node n = (Node) model;
				for(Spatial c : n.getChildren())
					fixHWModels(c);
			}
		}
	}


	//FIXME for old mesh format create empty HWSkinning buffers
	private void fixHWSkinningBuffers(Mesh mesh) {
        //creating empty buffers for HW skinning 
        //the buffers will be setup if ever used.
		//setting usage to cpuOnly so that the buffer is not send empty to the GPU  
		VertexBuffer indicesHW = mesh.getBuffer(Type.HWBoneIndex);
		if(indicesHW == null){
			indicesHW = new VertexBuffer(Type.HWBoneIndex);
			indicesHW.setUsage(Usage.CpuOnly);
			mesh.setBuffer(indicesHW);
		}

        VertexBuffer weightsHW = mesh.getBuffer(Type.HWBoneWeight);
        if(weightsHW == null) {
        	weightsHW = new VertexBuffer(Type.HWBoneWeight);
            weightsHW.setUsage(Usage.CpuOnly);
            mesh.setBuffer(weightsHW);
        }
       
              
	}


	public Node getModel() {
		checkForUpdate();
		return model;
	}


	public void setAnimParts(String animSet) {
		this.animSet = animSet;
	}
}
