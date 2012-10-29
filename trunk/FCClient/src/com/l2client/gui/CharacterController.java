package com.l2client.gui;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.jme3.input.ChaseCamera;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.l2client.app.Singleton;
import com.l2client.component.TargetComponent;
import com.l2client.controller.entity.Entity;
import com.l2client.controller.handlers.PlayerCharHandler;
import com.l2client.gui.actions.BaseUsable;
import com.l2client.gui.actions.GotoClickedInputAction;
import com.l2client.model.jme.NewCharacterModel;
import com.l2client.model.l2j.ItemInstance;
import com.l2client.model.network.EntityData;

//TODO refactor with charselecthandler, ev. remove coupling
//FIXME too much hard coded here
//TODO move most of the functionality into seperate actions
public final class CharacterController {

	private static final Logger logger = Logger.getLogger(CharacterController.class.getName());
	private final static CharacterController instance = new CharacterController();
	private ChaseCamera chaser;
	private PointLight pl;
	private EntityData data = null;
	private Entity visible = null;
	private Camera cam;
	private Spatial target;

	private CharacterController() {
	}

	public static CharacterController get() {
		return instance;
	}
	
	//FIXME test and check if other actions are also disabled!
	public void setInputEnabled(boolean enable){
		if(chaser != null){
			chaser.setEnabled(enable);
		}
	}

	//FIXME move input creation out to a seperate task so it can be performed early, and thus enable the dependent code (like ActionManager) to initialize earlier
	public void onEnterWorld(final PlayerCharHandler pcHandler,
			Camera cam) {

		data = pcHandler.getSelectedChar();
		visible = pcHandler.createPCComponents(data, new NewCharacterModel(pcHandler.getSelectedSummary()));
		logger.fine("Character initialized to:" + visible.getLocalTranslation());

		setupChaseCamera(visible, cam);
		
        AmbientLight al = new AmbientLight();
        al.setColor(new ColorRGBA(.8f, .8f, .8f, 1.0f));
		Singleton.get().getSceneManager().changeRootLight(al,0);

		pl = new PointLight();
		pl.setColor(ColorRGBA.White);
		pl.setRadius(15f);
		visible.addLight(pl);

		ArrayList<BaseUsable> acts = new ArrayList<BaseUsable>();
		acts.add(new GotoClickedInputAction(pcHandler, cam));

		Singleton.get().getInputController().pushInput(acts);
		
		Singleton.get().getSceneManager().removeChar();
		Singleton.get().getSceneManager().changeCharNode(visible, 0);
		Singleton.get().getTerrainManager().addSkyDome();

	}

//	//TODO reentrant safe, gamecontroller needed at all?
//	public void initialize(GameController gameController) {
//		chaser.setSpatial(null);
//		Singleton.get().getInputController().popInput();
//		
//		chaser = null;
//	}

	private void setupChaseCamera(Node n, Camera cam) {
		Vector3f targetOffset = new Vector3f();
//		float ex = ((BoundingBox) n.getWorldBound()).getYExtent();
		targetOffset.y = 2.2f;
//		cam.setAxes(Vector3f.UNIT_X, Vector3f.UNIT_Z, Vector3f.UNIT_Y.mult(-1f));
		chaser = new ChaseCamera(cam, n, Singleton.get().getInputController().getInputManager());
//		chaser.setUpVector(Vector3f.UNIT_Z);
//		  Comment this to disable smooth camera motion
		chaser.setSmoothMotion(true);
        
        //Uncomment this to disable trailing of the camera 
        //WARNING, trailing only works with smooth motion enabled and is the default behavior
//		chaser.setTrailingEnabled(false);

//		chaser.set
        //Uncomment this to look 3 world units above the target
		chaser.setLookAtOffset( targetOffset);//Vector3f.UNIT_Z.mult(targetOffset));

        //Uncomment this to enable rotation when the middle mouse button is pressed (like Blender)
        //WARNING : setting this trigger disable the rotation on right and left mouse button click
		chaser.setToggleRotationTrigger(new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
		chaser.setMaxDistance(20f);
		chaser.setMinDistance(5f);
		chaser.setDefaultDistance(12f);
		chaser.setZoomSensitivity(0.1f);
	}

	/**
	 * update the chase cam stuff
	 * @param tpf
	 */
	//FIXME this should be moved out or made more intuitive to use (to have it hook up in the game update loop is not intuitive)
	public void simpleUpdate(float tpf) {
		if (chaser != null) {
			chaser.update(tpf);
			if (target != null) {
				float camMinHeight = target.getLocalTranslation().y + 0.5f;
				if (!Float.isInfinite(camMinHeight)
						&& !Float.isNaN(camMinHeight)
						&& cam.getLocation().y <= camMinHeight) {
					cam.getLocation().y = camMinHeight;
					cam.update();
				}
				if (pl != null) {
					pl.setPosition(new Vector3f(target.getLocalTranslation().x, 4.0f, target.getLocalTranslation().z));
				}
			}

		}
	}

	public void addInventoryItem(ItemInstance inst) {
		// TODO Auto-generated method stub
		
	}

	public void addInventoryBlockItem(int itemId) {
		// TODO Auto-generated method stub
		
	}

	public boolean setPlayerNoTarget(){
		if(data != null)//this is null when user just closes app while not logged in at all
	    {	TargetComponent tc = (TargetComponent) Singleton.get().getEntityManager().getComponent(data.getObjectId(), TargetComponent.class);
	    	if(tc != null){
	    		if(tc.hasTarget()){
		    		tc.setTarget(TargetComponent.NO_TARGET);
		    		logger.info(data.getObjectId()+" Player target set to no target charID:"+data.getCharId());
		    		return true;
	    		}
	    	}
    	}
    	return false;
	}
}
