package com.l2client.model.jme;

import java.util.logging.Logger;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingSphere;
import com.jme3.bounding.BoundingVolume;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.BillboardControl;
import com.jme3.scene.control.Control;
import com.l2client.asset.AssetManager;
import com.l2client.controller.SceneManager;
import com.l2client.model.network.NewCharSummary;

/**
 * visual representation of a model in 3d space
 * has a controller for 
 * 0) animation
 * 1) movement (optionally) 
 * 
 * should be subclassed for individual representations of npc's characters, etc.
 * A NpcBuilder should construct the individual visual specific detail representations.
 * 
 * Currently all models are based on the same visual model, a modelcache or AssetManager should be used to speed loading of new models (which should be done by a builder/factory patterns anyway)
 *
 */
//TODO animation controller, rigging of actions against animations
public class VisibleModel extends Node {

	private static final long serialVersionUID = 1L;

	Logger logger = Logger.getLogger(VisibleModel.class.getName());

	NewCharSummary charSelection;
	
	BitmapText label;

	/**
	 * In the demo all models look alike, so we store a base node
	 */
	protected static Node baseNode = null;
	/**
	 * The final individual node, including the name of then object above its head
	 */
	protected Node vis = null;

	/**
	 * Internal class representing an simple movement controller for moving models around.
	 * The controller is attached on the visual and moves the visual around, on reaching its goal
	 * the controller removes itself, no turning at the moment
	 *
	 */
	protected class MoveController extends AbstractControl{

		private static final long serialVersionUID = 1L;
		/// target position 
		private Vector3f target = null;
		/// direction to travel along
		private Vector3f direction = null;
		/// node to be moved
		private Spatial parent = null;
		
		private float speed = 0f;
		
		MoveController(Spatial _parent, Vector3f _target, float _speed) {
			target = _target;
			parent = _parent;
			speed = _speed;
			if(parent != null && target != null)
				direction = target.subtract(parent.getLocalTranslation()).normalizeLocal();
		}

		@Override
		public Control cloneForSpatial(Spatial spatial) {
			//we do not need this
			return null;
		}

		/**
		 * Updates parent local Translation and removes itself on reaching the goal.
		 * The target is moved time * speed * normalized direction vector.
		 */
		@Override
		protected void controlUpdate(float tpf) {
			if(target != null){
				Vector3f parentPos = parent.getLocalTranslation();
	            //target reached then remove self
	            if (target.subtract(parentPos).lengthSquared() <= (0.1f)) {
	            	speed = 0f;
	                parent.setLocalTranslation(target);
	            	parent.removeControl(this);	            	
	                this.target = null;
	                this.direction = null;
	                this.parent = null;
	                return;
	            }
	            //TODO the npcdata should be updated too
	            //move
	            //TODO turn towards target direction and move
	            parent.setLocalTranslation(parentPos.add(direction.mult(tpf * speed)));
			}
		}

		@Override
		protected void controlRender(RenderManager rm, ViewPort vp) {
			//not used
		}
	};

	/**
	 * Constructor for a vismodel
	 * 
	 * @param sel the NewCharSummary this model should be based on (used for assembling the final visual representation)
	 */
	public VisibleModel(NewCharSummary sel) {
		charSelection = sel;
	}
	
	
//	/**
//	 * In the demo we drop out the z value (height) as all walk on the plane for simplicity
//	 */
//	//FIXME actually ignore height values
//	@Override
//	public void setLocalTranslation(float x, float y, float z){
//		super.setLocalTranslation(x, y, 0f);
//	}
//	
//	/**
//	 * In the demo we drop out the z value (height) as all walk on the plane for simplicity
//	 */
//	//FIXME actually ignore height values
//	@Override
//	public void setLocalTranslation(Vector3f vec){
//		super.setLocalTranslation(vec.x, vec.y, 0f);
//	}

	/**
	 * Creates the visual if needed by loading it and places the name label above its head
	 */
	public void attachVisuals() {
		if (vis != null)
			attachChild(vis);
		else {
			createVisuals();
			if (vis != null)
				attachChild(vis);
		}
//		if(vis != null){
//			//done here for triggering an update
//			updateGeometricState();
//		}

		updateLabel();
	}


	public void updateLabel() {
		//The label is created in char space, so if your char is rotated into final position beware that
		//here z is your up vector and not y
		if (vis != null && charSelection != null && charSelection.name != null) {
			if(label != null)
				vis.detachChild(label);
			//FIXME center label above character
	        BitmapFont fnt = AssetManager.getInstance().getJmeAssetMan().loadFont("Interface/Fonts/Default.fnt");
	        label = new BitmapText(fnt, false);
//	        label.setBox(new Rectangle(0, 0, 6, 3));
	        label.setQueueBucket(Bucket.Transparent);
	        label.setSize( 0.5f );
	        label.setText(charSelection.name);

	        label.addControl(new BillboardControl());
//	        label.updateModelBound();
//	        label.updateGeometricState();
	        if(vis.getWorldBound() instanceof BoundingBox){
	        	BoundingBox bbox = (BoundingBox)vis.getWorldBound();
	        	label.setLocalTranslation(-0.15f*charSelection.name.length(), 0f, bbox.getXExtent()+bbox.getXExtent()+0.2f); 
	        	logger.finest("Label by BBox "+label.getText()+" @ "+label.getLocalTranslation());
	        }
	        else if(vis.getWorldBound() instanceof BoundingSphere){
	        	BoundingSphere bound = (BoundingSphere)vis.getWorldBound();
	        	label.setLocalTranslation(-0.15f*charSelection.name.length(),  0f, bound.getRadius()+bound.getRadius()+0.2f);
	        	logger.finest("Label by BSphere "+label.getText()+" @ "+label.getLocalTranslation());
	        }
	        else {
	        	label.setLocalTranslation(-0.15f*charSelection.name.length(),0f, 2.5f);
	        	logger.finest("Label by Code "+label.getText()+" @ "+label.getLocalTranslation());
	        }
			vis.attachChild(label);
			vis.updateGeometricState();
		}
	}
	
	/**
	 * Builds the visual representation (no text decorator, ec.) of the model by loading it currently.
	 * The anim controller seems to be missing from the jme stored version, so currently 
	 * we load the model from the plain ogre xml definition (which is a bit slower)
	 * 
	 * 
	 * @return the loaded model on a @see Node
	 */
	//FIXME move to a builder, whcih should know what assets to load for which visual (stored in the DB)
	protected Node createVisuals() {

		if (charSelection != null) {
			if (baseNode == null) {	
//				Asset a = new Asset("troll2/troll.xml.mesh.xml","troll");
//				com.l2client.asset.AssetManager.getInstance().loadAsset(a,true);
				Spatial s = AssetManager.getInstance().getJmeAssetMan().loadModel("models/troll2/troll.xml.mesh.xml");
//				baseNode = (Node) a.getBaseAsset();
				if(s instanceof Node ){
				baseNode = (Node) s;
				baseNode.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
				}
			}
			if (baseNode != null) {
//				//TODO check if still needed
//				//FIXME modelconverter should already have set this one, this is not the case -> NPE
				baseNode.setModelBound(new BoundingBox());
				baseNode.updateModelBound();
				baseNode.updateGeometricState();
				vis = baseNode.clone(false);

				AnimControl animControl = vis.getControl(AnimControl.class);
				AnimChannel chan;
				if(animControl != null) {
					
					if (animControl.getNumChannels()<=0){
						animControl.createChannel();
					}
					chan = animControl.getChannel(0);
						chan.setAnim("idle");
						chan.setTime(animControl.getAnimationLength("idle")
						* FastMath.nextRandomFloat());
					
				} else {
					logger.severe("Vis animations are missing for toll");
				}
			}
		}

		return vis;
	}
	
	/**
	 * Adds a movement controller to move the model to the specified x,y coordinates (z is 0 here)
	 * A previous move controller is removed
	 * @param toX target position in world coords x
	 * @param toY target position in world coords y
	 * @param toZ ignored
	 * @param speed   speed per second to be used for traveling
	 */
	// FIXME ignores height at the moment!
	// FIXME add and remove should be done via scenemanager
	public void initMoveTo(float toX, float toY, float toZ, float speed) {
		if (speed > 0f) {
			AbstractControl rem = getControl(MoveController.class);
			if (rem != null)
				removeControl(rem);
			
			addControl(new MoveController(this, new Vector3f(toX, 0, toZ), speed));
		}
	}
}
