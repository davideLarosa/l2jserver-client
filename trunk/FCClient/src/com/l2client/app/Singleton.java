package com.l2client.app;

import com.l2client.asset.AssetManager;
import com.l2client.component.AnimationSystem;
import com.l2client.component.JmeUpdateSystem;
import com.l2client.component.PositioningSystem;
import com.l2client.controller.SceneManager;
import com.l2client.controller.area.SimpleTerrainManager;
import com.l2client.controller.entity.EntityManager;
import com.l2client.dao.IDAO;
import com.l2client.dao.csv.CSVDatastoreDAO;
import com.l2client.gui.ActionManager;
import com.l2client.gui.CharacterController;
import com.l2client.gui.GameController;
import com.l2client.gui.GuiController;
import com.l2client.gui.InputController;
import com.l2client.model.network.ClientFacade;
import com.l2client.navigation.EntityNavigationManager;
import com.l2client.util.AnimationManager;
import com.l2client.util.PartSetManager;
import com.l2client.util.SkeletonManger;

public class Singleton {

	private CharacterController charController;
	private GameController gameController;
	private SceneManager sceneManager;
	private IDAO dataManager;
	private PartSetManager partManager;
	private InputController inputController;
	//FIXME only one openal allowed, so now paulscode and jme3 are rivals for the openal device
//	public SoundController soundCon;
	private GuiController guiController;
	private SimpleTerrainManager terrainManager;
	private AnimationManager animManager;
	private EntityNavigationManager navManager;
	private EntityManager entityManager;
	private JmeUpdateSystem jmeSystem;
	private PositioningSystem posSystem;
	private AnimationSystem animSystem;
	private AssetManager assetManager;
	private SkeletonManger skeletonManager;
	private ActionManager actionManager;
	private ClientFacade client;
	
	private Singleton(){
	}
	
	private static class SingletonHolder {
		public static final Singleton instance = new Singleton();
	}
	
	public static Singleton get(){
		return SingletonHolder.instance;
	}
	
	public void init(){
    	assetManager = AssetManager.get();//this one goes first
		charController = CharacterController.get();
		dataManager = CSVDatastoreDAO.get();//DatastoreDAO.get();
		dataManager.init();
		sceneManager = SceneManager.get();
		partManager = PartSetManager.get();
		inputController = InputController.get();
		guiController = GuiController.get();
		gameController = GameController.get();
//		soundCon = SoundController.getInstance();
		terrainManager = SimpleTerrainManager.get();
		terrainManager.initialize();
		animManager = AnimationManager.get();
		
    	navManager = EntityNavigationManager.get();
    	entityManager = EntityManager.get();
    	jmeSystem = JmeUpdateSystem.get();
    	posSystem = PositioningSystem.get();
    	animSystem = AnimationSystem.get();
    	skeletonManager = SkeletonManger.get();
    	client = ClientFacade.get();
    	actionManager = ActionManager.getInstance();
    	actionManager.loadActions();
	}
	
	public void finit(){
    	guiController.finit();
    	gameController.finish();
    	dataManager.finit();
    	assetManager.shutdown();

//    	soundCon.cleanup();	
	}
	
	/**
	 * @return the actionManager
	 */
	public ActionManager getActionManager(){
		return actionManager;
	}
	
	/**
	 * @return the charController
	 */
	public CharacterController getCharController() {
		return charController;
	}

	/**
	 * @return the gameController
	 */
	public GameController getGameController() {
		return gameController;
	}

	/**
	 * @return the sceneManager
	 */
	public SceneManager getSceneManager() {
		return sceneManager;
	}

	/**
	 * @return the dataManager
	 */
	public IDAO getDataManager() {
		return dataManager;
	}

	/**
	 * @return the partManager
	 */
	public PartSetManager getPartManager() {
		return partManager;
	}

	/**
	 * @return the inputController
	 */
	public InputController getInputController() {
		return inputController;
	}

	/**
	 * @return the guiController
	 */
	public GuiController getGuiController() {
		return guiController;
	}

	/**
	 * @return the terrainManager
	 */
	public SimpleTerrainManager getTerrainManager() {
		return terrainManager;
	}

	/**
	 * @return the animManager
	 */
	public AnimationManager getAnimManager() {
		return animManager;
	}

	/**
	 * @return the navManager
	 */
	public EntityNavigationManager getNavManager() {
		return navManager;
	}

	/**
	 * @return the entityManager
	 */
	public EntityManager getEntityManager() {
		return entityManager;
	}

	/**
	 * @return the jmeSystem
	 */
	public JmeUpdateSystem getJmeSystem() {
		return jmeSystem;
	}

	/**
	 * @return the posSystem
	 */
	public PositioningSystem getPosSystem() {
		return posSystem;
	}

	/**
	 * @return the animSystem
	 */
	public AnimationSystem getAnimSystem() {
		return animSystem;
	}

	/**
	 * @return the assetManager
	 */
	public AssetManager getAssetManager() {
		return assetManager;
	}

	public SkeletonManger getSkeletonManager() {
		return skeletonManager;
	}
	
	public ClientFacade getClientFacade(){
		return client;
	}

}
