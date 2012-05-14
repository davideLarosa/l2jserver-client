package com.l2client.app;

import java.util.logging.Logger;

import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.Light;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.l2client.asset.AssetManager;
import com.l2client.component.AnimationSystem;
import com.l2client.component.JmeUpdateSystem;
import com.l2client.component.PositioningSystem;
import com.l2client.controller.SceneManager;
import com.l2client.controller.area.SimpleTerrainManager;
import com.l2client.controller.entity.EntityManager;
import com.l2client.dao.DatastoreDAO;
import com.l2client.gui.CharacterController;
import com.l2client.gui.GameController;
import com.l2client.gui.GuiController;
import com.l2client.gui.InputController;
import com.l2client.navigation.EntityNavigationManager;
import com.l2client.util.AnimationManager;
import com.l2client.util.PartSetManager;

/**
 * L2J uses z as up
 *
 */
public class L2JClient extends ExtendedApplication {
	
	private static final Logger logger = Logger.getLogger(L2JClient.class
            .getName());
	
	private class SingletonHolder{
		private CharacterController charCon;
		private GameController gameCon;
		private SceneManager sceneMan;
		private DatastoreDAO dataMan;
		private PartSetManager partMan;
		private InputController inputCon;
		//FIXME only one openal allowed, so now paulscode and jme3 are rivals for the openal device
//		public SoundController soundCon;
		private GuiController guiCon;
		private SimpleTerrainManager terMan;
		private AnimationManager animMan;
		private EntityNavigationManager navMan;
		private EntityManager entityMan;
		private JmeUpdateSystem jmeSystem;
		private PositioningSystem posSystem;
		private AnimationSystem animSystem;
		private AssetManager assetMan;
		
		public void init() {

			charCon = CharacterController.getInstance();
			dataMan = DatastoreDAO.getInstance();
			sceneMan = SceneManager.get();
			partMan = PartSetManager.get();
			inputCon = InputController.get();
			guiCon = GuiController.getInstance();
			gameCon = GameController.getInstance();
//			soundCon = SoundController.getInstance();
			terMan = SimpleTerrainManager.get();
			terMan.initialize();
			animMan = AnimationManager.get();
			
	    	navMan = EntityNavigationManager.get();
	    	entityMan = EntityManager.get();
	    	jmeSystem = JmeUpdateSystem.get();
	    	posSystem = PositioningSystem.get();
	    	animSystem = AnimationSystem.get();
	    	assetMan = AssetManager.getInstance();
		}
	}
	
	private SingletonHolder singles = new SingletonHolder();

	/**
	 * Entry point for the test,
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		L2JClient app = new L2JClient();
		//no startup settings screen
//		app.showSettings = false;
        app.start();
	}

//	private ClientFacade clientInfo;
//	private L2JLoginHandler loginSocket;
//	private SceneRoot scene;
//private float timeUp;


    @Override
    public void initialize() {
    	//needed as audiorenderer.cleanup blows wild
    	this.settings.setAudioRenderer(null);
        super.initialize();
    }
    
	@Override
	public void simpleInitApp() {
		singles.init();
		this.initGui();
	}
	


	private void initGui(){
		settings.setTitle("L2J Client");
		flyCam.setEnabled(false);
		singles.sceneMan.setRoot(rootNode);
		
		singles.partMan.loadParts("megaset.csv");
		singles.inputCon.initialize(inputManager);
		singles.guiCon.initialize(settings, renderManager);
		singles.gameCon.initialize(cam,settings);
		singles.gameCon.doLogin();
//		singles.soundCon.playBackground( "background1", "sound/background1.ogg", true );
		// show system cursor
		inputManager.setCursorVisible(true);
		
        inputManager.addListener(new ActionListener() {
			
			@Override
			public void onAction(String name, boolean isPressed, float tpf) {
				if(name.equals("print_scenegraph") && !isPressed){
					printHierarchy(rootNode, "");
					System.out.println("Camera loc:"+cam.getLocation()+", dir:"+cam.getDirection());
				}else if (name.equals("toggle_flycam")&& !isPressed){
					toggelFlyCam();
				}
			}
		},  "print_scenegraph", "toggle_flycam");
        inputManager.addMapping("print_scenegraph", new KeyTrigger(KeyInput.KEY_F6));
        inputManager.addMapping("toggle_flycam", new KeyTrigger(KeyInput.KEY_F7));

	}
	

	protected void toggelFlyCam() {
		flyCam.setEnabled(!flyCam.isEnabled());
		System.out.println("Toggled Flycam");
	}

	@Override
	public void simpleUpdate(float tpf) {
//		if(GameController.getInstance().isFinished())
//			stop();
		singles.posSystem.update(tpf);
		singles.animSystem.update(tpf);
		singles.jmeSystem.update(tpf);
		singles.charCon.simpleUpdate(tpf);
		singles.sceneMan.update(tpf);
	}

    public void stop() {
    	super.stop();
    	//TODO consistent naming
    	singles.gameCon.finish();
    	
    	singles.dataMan.release();
    	
    	singles.assetMan.shutdown();

//    	singles.soundCon.cleanup();
    }
  
	
    protected void printHierarchy(Spatial n, String indent) {
		System.out.println(indent+n.getName()+":"+n.getClass()+":"+n.getLocalTranslation());
		if(n instanceof Node)
			for(Spatial c : ((Node)n).getChildren())
				printHierarchy(c, indent+" ");
		
		for(int i = 0; i<n.getNumControls(); i++)
			System.out.println(indent+"Controller:"+n.getControl(i).getClass());
		
		for(Light l : n.getLocalLightList())
			System.out.println(indent+"Light"+l);
		
	}
}
