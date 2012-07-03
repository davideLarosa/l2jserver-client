package com.l2client.app;

import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.Light;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.l2client.dao.UserPropertiesDAO;

/**
 * L2J uses z as up
 *
 */
public class L2JClient extends ExtendedApplication {

	private Singleton singles = Singleton.get();;

	/**
	 * Entry point for the test,
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		L2JClient app = new L2JClient();
		app.showSettings = true;
		//no startup settings screen
//		app.showSettings = false;
        app.start();
	}

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
		//load last used sever port and host from properties into system properties
		UserPropertiesDAO.loadProperties();
		settings.setTitle("L2J Client");
		flyCam.setEnabled(false);
		singles.getSceneManager().setRoot(rootNode);
		singles.getPartManager().loadParts("megaset.csv");
		singles.getInputController().initialize(inputManager);
		singles.getGuiController().initialize(settings, renderManager);
		singles.getGameController().initialize(cam,settings, viewPort);
		singles.getGameController().doLogin();

		inputManager.setCursorVisible(true);
		
        inputManager.addListener(new ActionListener() {
			
			@Override
			public void onAction(String name, boolean isPressed, float tpf) {
				if(name.equals("print_scenegraph") && !isPressed){
					printHierarchy(rootNode, "");
					System.out.println("Camera loc:"+cam.getLocation()+", dir:"+cam.getDirection());
				}
			}
		},  "print_scenegraph", "toggle_flycam");
        inputManager.addMapping("print_scenegraph", new KeyTrigger(KeyInput.KEY_F6));
	}

	@Override
	public void simpleUpdate(float tpf) {
//		if(GameController.getInstance().isFinished())
//			stop();
		singles.getPosSystem().update(tpf);
		singles.getAnimSystem().update(tpf);
		singles.getJmeSystem().update(tpf);
		singles.getCharController().simpleUpdate(tpf);
		singles.getSceneManager().update(tpf);
	}

    public void stop() {
    	super.stop();
    	singles.finit();
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
