package com.l2client.model.jme;

import java.util.logging.Logger;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingSphere;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapFont.Align;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.BillboardControl;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.l2client.app.Singleton;
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
 * The loaded 3d model is based on the troll, which is a standin for an unknown/unconfigured 3d model
 */
//TODO animation controller, rigging of actions against animations
public class VisibleModel extends Node {

	public static final String ENTITY_PREFIX = "Entity_";

	Logger logger = Logger.getLogger(VisibleModel.class.getName());

	NewCharSummary charSelection;
	
	/**
	 * Node for the text label
	 */
	Node label;
	
	static Spatial selection = null;
	
	/**
	 * just one health bar visible, the one we have focus on
	 */
	static Node healthbar = null;

	/**
	 * In the demo all models look alike, so we store a base node
	 */
	protected static Node baseNode = null;
	/**
	 * The final individual node, including the name of then object above its head
	 */
	protected Node vis = null;


	/**
	 * Constructor for a vismodel
	 * 
	 * @param sel the NewCharSummary this model should be based on (used for assembling the final visual representation)
	 */
	public VisibleModel(NewCharSummary sel) {
		charSelection = sel;
		if(sel != null){
			name = ENTITY_PREFIX+sel.name+"_"+sel.objectId;
		} else {
			name = "Entity_troll_null";
		}
	}

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

		updateLabel();
	}


	public void updateLabel() {
		//The label is created in char space, so if your char is rotated into final position beware that
		//here z is your up vector and not y
		if (vis != null && charSelection != null && charSelection.name != null) {
			if(label != null)
				detachChild(label);

	        BitmapFont fnt = Singleton.get().getAssetManager().getJmeAssetMan().loadFont("Interface/Fonts/Default.fnt");
	        BitmapText txt = new BitmapText(fnt, false);
	        txt.setSize( 0.4f );
	        txt.setText(charSelection.name);
	        float w = txt.getLineWidth()+20f;
	        float off = w*0.5f;
	        txt.setBox(new Rectangle(-off, 0f, w, txt.getHeight()));
	        txt.setAlignment(Align.Center);
	        txt.setQueueBucket(Bucket.Transparent);
	        txt.addControl(new BillboardControl());
			
			label = new Node("label");
	        if(vis.getWorldBound() instanceof BoundingBox){
	        	BoundingBox bbox = (BoundingBox)vis.getWorldBound();
	        	label.setLocalTranslation(0f, bbox.getYExtent()+bbox.getYExtent()+0.5f, 0f);
	        	logger.finest("Label by BBox "+txt.getText()+" @ "+label.getLocalTranslation());
	        }
	        else if(vis.getWorldBound() instanceof BoundingSphere){
	        	BoundingSphere bound = (BoundingSphere)vis.getWorldBound();
	        	label.setLocalTranslation(0f, bound.getRadius()+bound.getRadius()+0.5f, 0f);
	        	logger.finest("Label by BSphere "+txt.getText()+" @ "+label.getLocalTranslation());
	        }
	        else {
	        	label.setLocalTranslation(0f, 2.5f, 0f);
	        	logger.finest("Label by Code "+txt.getText()+" @ "+label.getLocalTranslation());
	        }
	        label.attachChild(txt);
			attachChild(label);
		}
	}
	
	public void addSelectionMarker(ColorRGBA color){
//		if(selection != null){
//			Singleton.get().getSceneManager().changeAnyNode(this, selection, 0);
//		} else {
		if(selection == null)
			selection = createSelectionMarker();
			
			Singleton.get().getSceneManager().changeAnyNode(this, selection, 0);
			if(vis != null) {
				ColorRGBA cl;
				if(color != null)
				cl = color.clone();
				else
					cl = ColorRGBA.White.clone();
				//use an intensity of 12 for the rimlight
				cl.a = 4f;
				setRimLight(cl, vis);
			}
			
		if(healthbar == null) 
			healthbar = createHealthBar();
		
		Singleton.get().getSceneManager().changeAnyNode(this, healthbar, 0);
//		}
	}
	
	public void removeSelectionMarker(){
		if(selection != null){
			Singleton.get().getSceneManager().changeAnyNode(this, selection, 1);
			if(vis != null)
				setRimLight(ColorRGBA.BlackNoAlpha, vis);
			selection = null;
		}
		if(healthbar != null){
			Singleton.get().getSceneManager().changeAnyNode(this, healthbar, 1);
		}
	}
	
	/**
	 * Updates the health bar above a targeted model
	 * @param percent	value in 0-1.0 range to scale the health
	 */
	public void updateHealthbar(float percent){
		if(healthbar != null){
			healthbar.getChild("health_bar").setLocalScale(percent, 1f, 1f);
		}
	}
	
	private void setRimLight(ColorRGBA color, Node node){
		for(Spatial s : node.getChildren()){
			if(s instanceof Geometry){
				Material m = ((Geometry) s).getMaterial();
				m.setColor("RimLighting", color);//new ColorRGBA(1f, 0f, 0f, 12f));
			}
			if(s instanceof Node) {
				setRimLight(color, (Node) s);
			}
		}
		
	}
	
	private Geometry createSelectionMarker(){
		float size = 2f;
		Geometry selectionMarker = new Geometry("selection", new Quad(size, size));
		selectionMarker.setLocalTranslation(-0.5f*size, 0.2f, 0.5f*size); 
		selectionMarker.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));

	    com.jme3.asset.AssetManager am = Singleton.get().getAssetManager().getJmeAssetMan();
	    Material mat = new Material(am, "Common/MatDefs/Light/Lighting.j3md");
	    Texture sel = am.loadTexture("models/textures/flare4.png");
	    sel.setWrap(WrapMode.Repeat);
	    mat.setTexture("DiffuseMap", sel);
	    mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
	    mat.getAdditionalRenderState().setDepthWrite(false);
	    selectionMarker.setMaterial(mat);
	    selectionMarker.setQueueBucket(Bucket.Transparent);
	    selectionMarker.setShadowMode(ShadowMode.Receive);
	    
	    return selectionMarker;	
	}
	
	
	private Node createHealthBar(){
		Node n = new Node("health");
		Geometry frame = new Geometry("health_frame", new Quad(1f, 0.02f));
		Material mat = null;
	    com.jme3.asset.AssetManager am = Singleton.get().getAssetManager().getJmeAssetMan();
	    mat = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);
	    frame.setMaterial(mat);
	    frame.setQueueBucket(Bucket.Transparent);
	    frame.setShadowMode(ShadowMode.Off);
		frame.setLocalTranslation(-0.5f, 0.11f, 0f); 
	    n.attachChild(frame);
	    
		Geometry bar = new Geometry("health_bar", new Quad(1f, 0.1f));
	    mat = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        bar.setMaterial(mat);
	    bar.setQueueBucket(Bucket.Transparent);
        bar.setShadowMode(ShadowMode.Off);
        bar.setLocalTranslation(-0.5f, 0f, 0f);
	    n.attachChild(bar);
        if(vis.getWorldBound() instanceof BoundingBox){
        	BoundingBox bbox = (BoundingBox)vis.getWorldBound();
        	n.setLocalTranslation(0f, bbox.getYExtent()+0.6f, 0f);
        	logger.finest("Healthbar by BBox @ "+n.getLocalTranslation());
        }
        else if(vis.getWorldBound() instanceof BoundingSphere){
        	BoundingSphere bound = (BoundingSphere)vis.getWorldBound();
        	n.setLocalTranslation(0f, bound.getRadius()+0.6f, 0f);
        	logger.finest("Healthbar by BSphere @ "+n.getLocalTranslation());
        }
        else {
        	n.setLocalTranslation(0f, 2.8f, 0f);
        	logger.finest("Healthbar by Code @ "+n.getLocalTranslation());
        }
        n.addControl(new BillboardControl());
//        n.setLocalRotation(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_X));
//        n.updateGeometricState();

	    return n;	
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
//				com.l2client.asset.Singleton.get().getAssetManager().loadAsset(a,true);
				Spatial s = Singleton.get().getAssetManager().getJmeAssetMan().loadModel("models/troll2/troll.xml.mesh.xml");
//				baseNode = (Node) a.getBaseAsset();
				if(s instanceof Node ){
				baseNode = (Node) s;
				baseNode.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
				baseNode.setName(name);
				}
			}
			if (baseNode != null) {
//				//TODO check if still needed
//				//FIXME modelconverter should already have set this one, this is not the case -> NPE
//				baseNode.setModelBound(new BoundingBox());
//				baseNode.updateModelBound();
//				baseNode.updateGeometricState();
				vis = baseNode.clone(false);
				baseNode.setName(name);
				
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
					logger.severe("Vis animations are missing for troll");
				}
			}
		}

		return vis;
	}
	
//	/**
//	 * Adds a movement controller to move the model to the specified x,y coordinates (z is 0 here)
//	 * A previous move controller is removed
//	 * @param toX target position in world coords x
//	 * @param toY target position in world coords y
//	 * @param toZ ignored
//	 * @param speed   speed per second to be used for traveling
//	 */
//	// FIXME ignores height at the moment!
//	// FIXME add and remove should be done via scenemanager
//	public void initMoveTo(float toX, float toY, float toZ, float speed) {
//		if (speed > 0f) {
//			AbstractControl rem = getControl(MoveController.class);
//			if (rem != null)
//				removeControl(rem);
//			
//			addControl(new MoveController(this, new Vector3f(toX, 0, toZ), speed));
//		}
//	}
}
