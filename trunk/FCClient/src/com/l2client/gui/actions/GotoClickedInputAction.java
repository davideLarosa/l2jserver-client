package com.l2client.gui.actions;

import java.util.logging.Logger;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.l2client.app.Singleton;
import com.l2client.component.PositioningComponent;
import com.l2client.controller.entity.Entity;
import com.l2client.controller.handlers.PlayerCharHandler;
import com.l2client.model.l2j.ServerValues;
import com.l2client.navigation.Path;

/**
 * Action for triggering the movement of the player. Just an example.
 * 
 * 
 */
public class GotoClickedInputAction extends Action {
	
	protected static Logger log = Logger.getLogger(GotoClickedInputAction.class.getName());

	private Camera camera;
	private InputManager inputManager;
	private PlayerCharHandler handler;

	/**
	 * constructor of the action
	 * @param pcHandler 
	 * 
	 * @param charSelectHandler
	 *            The PlayerCharHandler is needed to send the final move request
	 * @param cam
	 *            The camera object
	 * @param target
	 *            The player object (target of the move)
	 * @param button
	 *            The button this action should trigger (0 is left, 1 is right,
	 *            2 is middle ...)
	 */
	public GotoClickedInputAction(PlayerCharHandler pcHandler, Camera cam) {
		super(-10, "GotoClickedInputAction");
		camera = cam;
		handler = pcHandler;
	}

	/**
	 * Execute the action, called ev. several times in a second, so we store a
	 * delay. Basically we create a lookat vector and check for intersection
	 * with the terrain tiles then the position of the intersection is passed
	 * over to the client connection to request a move to the found position
	 * (This is just the request, the confirmation is needed to trigger the real
	 * move)
	 */
	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		// only execute on button/key release
		if (!isPressed) {
			Singleton sin = Singleton.get();
			Vector3f origin = camera.getWorldCoordinates(
					inputManager.getCursorPosition(), 0.0f);
			Vector3f direction = camera.getWorldCoordinates(
					inputManager.getCursorPosition(), 0.3f);
			direction.subtractLocal(origin).normalizeLocal();

			Ray ray = new Ray(origin, direction);
			CollisionResults results = new CollisionResults();

			sin.getSceneManager().getRoot().collideWith(ray, results);

			if (results.size() > 0) {
				Geometry geom = null;
				for (CollisionResult res : results) {
					geom = res.getGeometry();
					if (geom != null) {
						log.fine("picked " + geom.getName());
						Integer id = geom.getUserData(Entity.ENTITY_ID);
						if (id != null) {
							//Just create a target component rest is done in the jmeupdatesystem
							Node n = res.getGeometry().getParent();
							String na = n.getName();
							log.fine("picked " + na + " id:"+ id);
							Vector3f loc = n.getLocalTranslation();
							sin.getClientFacade().sendAction(id, loc.x, loc.y, loc.z, false, true);
							results.clear();
							return;
						} else {//FIXME click on anything, check nav, then send request
							// this is the one
							Vector3f location = res.getContactPoint();
							if(location != null){
								Path p = new Path();
								PositioningComponent pos = (PositioningComponent) sin.getEntityManager().getComponent(handler.getSelectedObjectId(), PositioningComponent.class);
								if(pos != null){
									if(sin.getNavManager().buildNavigationPath(p, pos.position, location.clone())) {
										log.fine("new loc:" + location
												+ " sent:"+ ServerValues.getServerCoordX(location.x)
												+ ","+ ServerValues.getServerCoordY(location.z)
												+ ","+ ServerValues.getServerCoordZ(location.y));
										sin.getClientFacade().sendMoveToAction(handler.getSelectedObjectId(), pos.position.x, pos.position.y, pos.position.z, location.x, location.y,
												location.z);
										results.clear();
										return;
									}
								}
							}
							
							log.warning("invalid loc on click:" + location);
							return;
						}
					}

				}
				results.clear();
			} else 
				log.info("picked nothing");
		}
	}

	@Override
	public void addKeyMapping(InputManager man) {
		this.inputManager = man;
		man.addMapping("GotoClickedInputAction", new MouseButtonTrigger(
				MouseInput.BUTTON_LEFT));
		man.addListener(this, "GotoClickedInputAction");
	}

	@Override
	public void removeKeyMapping(InputManager man) {
		this.inputManager = null;
		man.deleteMapping("GotoClickedInputAction");
		man.removeListener(this);
	}

}
