package com.l2client.app;

import java.util.ArrayList;

import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.Light;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.WireBox;
import com.jme3.scene.shape.Box;
import com.l2client.component.Component;
import com.l2client.component.PositioningComponent;
import com.l2client.component.TargetComponent;
import com.l2client.controller.SceneManager.Action;
import com.l2client.controller.area.TileTerrainManager;
import com.l2client.dao.UserPropertiesDAO;
import com.l2client.navigation.TiledNavMesh;

/**
 * L2J uses z as up
 * @param <comp>
 *
 */
public class L2JClient<comp> extends ExtendedApplication {

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
		//disable free look
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
				} else if(name.equals("drop_a_box") && !isPressed){
					dropBox();
				} else if(name.equals("toggle_navbodermesh") && !isPressed){
					toggelNavBorderMeshes();
				} else if(name.equals("print_components") && !isPressed){
					printComponents();
				}
			}
		},  "print_scenegraph", "print_bboxes", "toggle_navmesh", "drop_a_box", "toggle_navbodermesh", "print_components");
        
        inputManager.addMapping("print_scenegraph", new KeyTrigger(KeyInput.KEY_F6));
        inputManager.addMapping("print_bboxes", new KeyTrigger(KeyInput.KEY_F7));
        inputManager.addMapping("toggle_navmesh", new KeyTrigger(KeyInput.KEY_F8));
        //TODO the box does problems with picking, suddenly it has no mesh when dropped !?!?! Thus commented out
//        inputManager.addMapping("drop_a_box", new KeyTrigger(KeyInput.KEY_F9));
        inputManager.addMapping("toggle_navbodermesh", new KeyTrigger(KeyInput.KEY_F10));
        inputManager.addMapping("print_components", new KeyTrigger(KeyInput.KEY_F11));
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
    	if(singles.getCharController() != null ) {
    		if(singles.getCharController().setPlayerNoTarget())
    			return;
    		//fallback if still in testarea move back
    		singles.getCharController().teleportFromTestArea();
    	}
    	singles.finit();
    	super.stop();
    }
  
	
    protected void printHierarchy(Spatial n, String indent) {
		System.out.println(indent+n.getName()+":"+n.getClass()+":"+n.getLocalTranslation()+" Shadow:"+n.getShadowMode());
		if(n instanceof Node)
			for(Spatial c : ((Node)n).getChildren())
				printHierarchy(c, indent+" ");
		
		for(int i = 0; i<n.getNumControls(); i++)
			System.out.println(indent+"Controller:"+n.getControl(i).getClass());
		
		for(Light l : n.getLocalLightList())
			System.out.println(indent+"Light:"+l);
		
	}
    
    private void toggelBBoxes(){
    	if(bboxes.getChildren().size()<=0){
    		addBBoxes(rootNode);
    	}
    	else if (bboxes.getChildren().size()>0){
    		removeBBoxesFromRoot();
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
    
    private void toggelNavMeshes(){
    	if(navs.getChild("NavMeshes") != null){
    		navs.detachChildNamed("NavMeshes");
    	} else {
    		Node node = new Node("NavMeshes");
    		TiledNavMesh[] array = Singleton.get().getNavManager().getNavMeshes();
    		for(TiledNavMesh t : array){
    			Geometry g = t.getDebugMesh();
    			g.setMaterial(matWireframe);
    			node.attachChild(g);
    		}
    		navs.attachChild(node);		
    	}
    }


    
    private void dropBox() {
    	Geometry g = new Geometry("Box",new Box());
    	g.setMaterial(matWireframe);
    	Vector3f pos = cam.getLocation().clone();
    	singles.getNavManager().snapToGround(pos);
    	singles.getSceneManager().changeAnyNode(rootNode, g, Action.ADD);
    	
	}

	private void toggelNavBorderMeshes() {
    	if(navs.getChild("NavBorderMeshes") != null){
    		navs.detachChildNamed("NavBorderMeshes");
    	} else {
    		Node node = new Node("NavBorderMeshes");
    		Material mat = matWireframe.clone();
            mat.setColor("Color", ColorRGBA.Blue);
    		TiledNavMesh[] array = Singleton.get().getNavManager().getNavMeshes();
    		for(TiledNavMesh t : array){
    			Geometry g = t.getDebugBorderMesh();
    			g.setMaterial(mat);
    			node.attachChild(g);
    		}
    		navs.attachChild(node);
    	}
    }
	
	private void printComponents(){
		int id = singles.getClientFacade().getCharHandler().getSelectedObjectId();
		ArrayList<Component> comps = singles.getEntityManager().getComponents(id);
		for(Component  comp :comps){
			System.out.println("-"+comp.toString());
			if(comp instanceof TargetComponent){
				TargetComponent tgt = (TargetComponent) comp;
				if(tgt.hasTarget()){
					ArrayList<Component> comps2 = singles.getEntityManager().getComponents(tgt.getCurrentTarget());
					for(Component  comp2 : comps2){
						System.out.println("---tgt-"+comp2.toString());
					}
				}
			}
		}
	}
}
