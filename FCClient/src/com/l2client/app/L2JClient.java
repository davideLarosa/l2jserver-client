package com.l2client.app;

import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.Light;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.WireBox;
import com.l2client.controller.area.TileTerrainManager;
import com.l2client.dao.UserPropertiesDAO;
import com.l2client.navigation.TiledNavMesh;

/**
 * L2J uses z as up
 *
 */
public class L2JClient extends ExtendedApplication {

	private Singleton singles = Singleton.get();
	
	
	Node bboxes = new Node("debug bboxes");
	Node navs = new Node("debug navs");
	private Material matWireframe;

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
    	this.settings.setTitle("L2J Client");
        super.initialize();
    }
    
	@Override
	public void simpleInitApp() {
		setPauseOnLostFocus(false);
		TileTerrainManager tm = TileTerrainManager.get();
		tm.setLoadedAtOrigin(true);
		singles.getNavManager().USE_OPTIMZED_PATH = true;
		singles.init(tm);
		this.initGui();
		
		
        matWireframe = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matWireframe.setColor("Color", ColorRGBA.Green);
        matWireframe.getAdditionalRenderState().setWireframe(true);
        rootNode.attachChild(bboxes);
        rootNode.attachChild(navs);
	}
	


	private void initGui(){
		//load last used sever port and host from properties into system properties
		UserPropertiesDAO.loadProperties();
		flyCam.setEnabled(false);

		singles.getSceneManager().setRoot(rootNode);
		singles.getSceneManager().setViewPort(viewPort);
		singles.getPartManager().loadParts("megaset.csv");
		singles.getInputController().initialize(inputManager);
		singles.getGuiController().initialize(settings, renderManager);
		singles.getGameController().initialize(cam,settings);
		singles.getGameController().doLogin();

		inputManager.setCursorVisible(true);
		
        inputManager.addListener(new ActionListener() {
			
			@Override
			public void onAction(String name, boolean isPressed, float tpf) {
				if(name.equals("print_scenegraph") && !isPressed){
					printHierarchy(rootNode, "");
					System.out.println("Camera loc:"+cam.getLocation()+", dir:"+cam.getDirection());
				} else if(name.equals("print_bboxes") && !isPressed){
					toggelBBoxes();
				} else if(name.equals("toggle_navmesh") && !isPressed){
					toggelNavMeshes();
				}
			}
		},  "print_scenegraph", "print_bboxes", "toggle_navmesh");
        
        inputManager.addMapping("print_scenegraph", new KeyTrigger(KeyInput.KEY_F6));
        inputManager.addMapping("print_bboxes", new KeyTrigger(KeyInput.KEY_F7));
        inputManager.addMapping("toggle_navmesh", new KeyTrigger(KeyInput.KEY_F8));
	}

	@Override
	public void simpleUpdate(float tpf) {
////		if(GameController.getInstance().isFinished())
////			stop();
		//ITileManager updated via charcontrol
		singles.getPosSystem().update(tpf);
		singles.getAnimSystem().update(tpf);
		singles.getJmeSystem().update(tpf);
		singles.getCharController().simpleUpdate(tpf);
		singles.getSceneManager().update(tpf);
	}

    public void stop() {
    	//FIXME add exit shield (do you really want to quit)
    	//FIXME add ESC as cancel of current action (selected target, menu open -> closes menu)
    	if(singles.getCharController() != null && singles.getCharController().setPlayerNoTarget())
    		return;
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
    
    private void toggelBBoxes(){
    	if(bboxes.getChildren().size()<=0){
    		addBBoxes(rootNode);
    	}
    	else if (bboxes.getChildren().size()>0){
    		removeBBoxesFromRoot();
    	}
    }
    
    private void toggelNavMeshes(){
    	if(navs.getChildren().size()<=0){
    		addNavs(rootNode);
    	}
    	else if (navs.getChildren().size()>0){
    		removeNavs();
    	}
    }

	private void removeBBoxesFromRoot() {
		bboxes.detachAllChildren();
	}


	private void addBBoxes(Node n) {
		for(Spatial s : n.getChildren()){
			if(s instanceof Geometry){
				Node a = s.getParent();
				if(a==null)return;
				BoundingVolume bound = ((Geometry) s).getModelBound();
				if(bound instanceof BoundingBox) {
				WireBox b = new WireBox(((BoundingBox) bound).getXExtent(), ((BoundingBox) bound).getYExtent(), ((BoundingBox) bound).getZExtent());
				Geometry g = new Geometry(null, b);
				g.setLocalTransform(s.getWorldTransform());
				g.setMaterial(matWireframe);
				bboxes.attachChild(g);
				}
			}
			if(s instanceof Node) {
				addBBoxes((Node) s);
			}
		}
	}
	
	private void addNavs(Node n) {
		TiledNavMesh[] array = Singleton.get().getNavManager().getNavMeshes().toArray(new TiledNavMesh[0]);
		for(TiledNavMesh t : array){
			Geometry g = t.getDebugMesh();
			g.setMaterial(matWireframe);
			navs.attachChild(g);
		}
	}
	
    private void removeNavs() {
		navs.detachAllChildren();
	}
}
