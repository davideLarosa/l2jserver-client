package com.l2client.animsystem.jme;

import com.jme3.animation.Bone;
import com.jme3.animation.Skeleton;
import com.jme3.bounding.BoundingBox;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;

public class BoundsUpdateControl extends TimedControl {
	
	private Skeleton skeleton;

	public BoundsUpdateControl(){		
	}
	
	public void setSkeleton(Skeleton skeleton){
		this.skeleton = skeleton;
	}
	
	@Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
    }

	@Override
	public Control cloneForSpatial(Spatial spatial) {
        try {
            BoundsUpdateControl clone = (BoundsUpdateControl) super.clone();
            clone.spatial = spatial;
            return clone;
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError(ex);
        }
	}

	@Override
	protected void doTimedControlUpdate(float tpf) {
		if(skeleton != null && spatial != null) {
			Vector3f extents = new Vector3f();
	        for (int i = 0; i < skeleton.getBoneCount(); i++){
	            Bone bone = skeleton.getBone(i);
	            Vector3f bonePos = bone.getModelSpacePosition();
	            extents.x = Math.max(extents.x, FastMath.abs(bonePos.getX()));
	            extents.y = Math.max(extents.y, FastMath.abs(bonePos.getY()));
	            extents.z = Math.max(extents.z, FastMath.abs(bonePos.getZ()));
	        }

	        BoundingBox b = new BoundingBox(new Vector3f(0f, 0f, 0f),extents.x+0.2f, extents.y+0.2f, extents.z);
	        spatial.setModelBound(b);
		}
	}

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {
	}

}
