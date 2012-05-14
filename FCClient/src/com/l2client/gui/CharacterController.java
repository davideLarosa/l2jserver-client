package com.l2client.gui;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.jme3.bounding.BoundingBox;
import com.jme3.input.ChaseCamera;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.l2client.controller.SceneManager;
import com.l2client.controller.area.IArea;
import com.l2client.controller.area.SimpleTerrainManager;
import com.l2client.controller.handlers.PlayerCharHandler;
import com.l2client.gui.actions.BaseUsable;
import com.l2client.gui.actions.GotoClickedInputAction;
import com.l2client.model.jme.NewCharacterModel;
import com.l2client.model.jme.VisibleModel;
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
	private VisibleModel visible = null;
	private Camera cam;
	private Spatial target;

	private CharacterController() {
	}

	public static CharacterController getInstance() {
		return instance;
	}
	
	//FIXME test and check if other actions are also disabled!
	public void setInputEnabled(boolean enable){
		if(chaser != null){
			chaser.setEnabled(enable);
		}
	}

	//FIXME move input creation out to a seperate task so it can be performed early, and thus enable the dependent code (like ActionManager) to initialize earlier
	public void onEnterWorld(final PlayerCharHandler charSelectHandler, Camera cam) {
//		SceneRoot root = GameController.getInstance().getSceneRoot();
//		Node player =root.getCharRoot();
		data = charSelectHandler.getSelectedChar();
		
//		if (player != null) {
//			player.detachAllChildren();

		SceneManager.get().removeChar();
			
			visible = new NewCharacterModel(charSelectHandler.getSelectedSummary());
			visible.attachVisuals();
			visible.setLocalTranslation(data.getX(), data.getY(), data.getZ());
			logger.fine("Character initialized to:"+visible.getLocalTranslation());

			setupChaseCamera(visible, cam);
			
			//hook up of the terrain swapping @see SimpleTerrainManager
			visible.addControl(new AbstractControl(){

				@Override
				public Control cloneForSpatial(Spatial spatial) {
					return null;
				}

				@Override
				protected void controlUpdate(float tpf) {
					//FIXME  move into SimpleTerrainManager
					int x = (int)visible.getLocalTranslation().x/IArea.TERRAIN_SIZE;
					int z = (int)visible.getLocalTranslation().z/IArea.TERRAIN_SIZE;
					SimpleTerrainManager.get().setCenter(x, z);
				}

				@Override
				protected void controlRender(RenderManager rm, ViewPort vp) {
				}}
	
			);

			ArrayList<BaseUsable> acts = new ArrayList<BaseUsable>();
			acts.add(new GotoClickedInputAction(charSelectHandler, cam));
			
			InputController.get().pushInput(acts);
			
//			player.attachChild(visible);

			pl = new PointLight();
			pl.setColor(ColorRGBA.White);
			pl.setRadius(15f);
			visible.addLight(pl);
			
			SceneManager.get().changeCharNode(visible,0);
//			root.updateGeometricState();
//		}
	}

	//TODO reentrant safe, gamecontroller needed at all?
	public void initialize(GameController gameController) {
		chaser.setSpatial(null);
		InputController.get().popInput();
		
		chaser = null;
	}

	private void setupChaseCamera(Node n, Camera cam) {
		Vector3f targetOffset = new Vector3f();
		float ex = ((BoundingBox) n.getWorldBound()).getZExtent();
		targetOffset.y = ex+ex + 0.2f;
//		cam.setAxes(Vector3f.UNIT_X, Vector3f.UNIT_Z, Vector3f.UNIT_Y.mult(-1f));
		chaser = new ChaseCamera(cam, n, InputController.get().getInputManager());
//		chaser.setUpVector(Vector3f.UNIT_Z);
		  //Comment this to disable smooth camera motion
//		chaser.setSmoothMotion(true);
        
        //Uncomment this to disable trailing of the camera 
        //WARNING, trailing only works with smooth motion enabled and is the default behavior
//		chaser.setTrailingEnabled(false);

//		chaser.set
        //Uncomment this to look 3 world units above the target
//		chaser.setLookAtOffset(Vector3f.UNIT_Z.mult(targetOffset));

        //Uncomment this to enable rotation when the middle mouse button is pressed (like Blender)
        //WARNING : setting this trigger disable the rotation on right and left mouse button click
		chaser.setToggleRotationTrigger(new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
		chaser.setMaxDistance(10f);
		chaser.setMinDistance(2f);
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
}
