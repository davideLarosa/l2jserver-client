package com.l2client.gui.actions;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.l2client.controller.SceneManager;
import com.l2client.controller.handlers.PlayerCharHandler;
import com.l2client.gui.GameController;
import com.l2client.model.l2j.ServerValues;

/**
 * Action for triggering the movement of the player. Just an example.
 * 
 * 
 */
public class GotoClickedInputAction extends Action {

	private Camera camera;
	private PlayerCharHandler handler;
	private InputManager inputManager;

	/**
	 * constructor of the action
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
	// TODO refactor to be not dependant on the PlayerCharHandler
	public GotoClickedInputAction(PlayerCharHandler charSelectHandler,
			Camera cam) {
		super(-10, "GotoClickedInputAction");
		camera = cam;
		handler = charSelectHandler;
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
	public void onAnalog(String name, float value, float tpf) {
		Vector3f origin = camera.getWorldCoordinates(
				inputManager.getCursorPosition(), 0.0f);
		Vector3f direction = camera.getWorldCoordinates(
				inputManager.getCursorPosition(), 0.3f);
		direction.subtractLocal(origin).normalizeLocal();

		Ray ray = new Ray(origin, direction);
		CollisionResults results = new CollisionResults();

		SceneManager.get().getRoot().collideWith(ray, results);

		if (results.size() > 0) {
			for (CollisionResult res : results) {

				if ("bottom".equals(res.getGeometry().getName())) {
					// this is the one

					Vector3f location = res.getContactPoint();
					System.out
							.println("new loc:"
									+ location
									+ " sent:"
									+ ServerValues.getServerCoord(location.x)
									+ ","
									+ ServerValues.getServerCoord(location.y)
									+ ","
									+ ServerValues.getServerCoord(location.z));
					handler.requestMoveToAction(location.x, location.y,
							location.z);
					results.clear();
					return;
				}
			}
			results.clear();
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
