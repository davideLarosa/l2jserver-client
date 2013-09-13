package com.l2client.controller.area;

import java.io.IOException;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

public class DistanceLodControl extends AbstractControl implements Cloneable {

	float distance = IArea.TERRAIN_SIZE;
	private transient Camera lastCam;
	private transient CullHint orgHint = null;
	
    /**
     * Creates a new
     * <code>LodControl</code>.
     */
    public DistanceLodControl() {
    }

    /**
     * Returns the distance tolerance for changing LOD.
     */
    public float getDistTolerance() {
        return distance;
    }

    /**
     * Specifies the distance tolerance for changing the LOD level on the
     */
    public void setDistTolerance(float dist) {
        this.distance = dist;
    }


    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
    }

    @Override
    public Control cloneForSpatial(Spatial spatial) {
    	DistanceLodControl clone = (DistanceLodControl) super.cloneForSpatial(spatial);
        clone.distance = distance;
        return clone;
   }

    @Override
    protected void controlUpdate(float tpf) {
    	if(lastCam != null){
	        float dist = lastCam.getLocation().distance(spatial.getWorldTranslation());
	        if(dist < distance){
	        	if(orgHint != null){
	        		//we are again in the view distance
	        		spatial.setCullHint(orgHint);
	        		orgHint = null;
	        	} //else do nothing we are in visible range
	        } else {
	        	if(orgHint != null){
	        		//do nothing we already replaced the original
	        	} else {
	        		//replace original with empty
	        		orgHint = spatial.getLocalCullHint();
	        		spatial.setCullHint(CullHint.Always);
	        	}
	        }
    	}
    }

    protected void controlRender(RenderManager rm, ViewPort vp) {
    	lastCam = vp.getCamera();
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(distance, "distance", IArea.TERRAIN_SIZE);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule ic = im.getCapsule(this);
        distance = ic.readFloat("distance", IArea.TERRAIN_SIZE);
    }
}