package com.l2client.test;

import java.util.ArrayList;

import com.jme3.animation.SkeletonControl;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.SkeletonDebugger;
import com.jme3.scene.shape.Box;
import com.l2client.animsystem.jme.JMEAnimationController;
import com.l2client.app.Assembler2;
import com.l2client.app.ExtendedApplication;
import com.l2client.util.PartSetManager;


public class TestEndlessModels extends ExtendedApplication implements ActionListener {

	PartSetManager man = PartSetManager.get();
	
	ArrayList<Node> nodes = new ArrayList<Node>() ;


	private String[] templates;
	int template_index = 0;
	int currentNode=0;

	private boolean renderDebug = false;
	
	Node bboxes = new Node("debug bboxes");
	
	Node skeletons = new Node("skeletons");
	
	private Material matWireframe;
	
    public void simpleInitApp() {
    	
        matWireframe = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matWireframe.setColor("Color", ColorRGBA.Green);
        matWireframe.getAdditionalRenderState().setWireframe(true);
    
    	man.loadParts("megaset.csv");
    	templates = man.getTemplates();

        DirectionalLight dr = new DirectionalLight();
        dr.setColor(ColorRGBA.White);
        dr.setDirection(new Vector3f(1, 0 , 1));
        
        AmbientLight am = new AmbientLight();
        am.setColor(ColorRGBA.White);
        rootNode.addLight(am);
        rootNode.addLight(dr);
        rootNode.attachChild(bboxes);
        
        flyCam.setMoveSpeed(3.3333f);
 
        setupScene();
        
        inputManager.addListener(this, "print_bboxes", "add_model", "remove_model",  "next_entity", "prev_entity");
        
        inputManager.addMapping("print_bboxes", new KeyTrigger(KeyInput.KEY_F6));
        
        inputManager.addMapping("add_model", new KeyTrigger(KeyInput.KEY_F9));
        inputManager.addMapping("remove_model", new KeyTrigger(KeyInput.KEY_F10));
        
        inputManager.addMapping("next_entity", new KeyTrigger(KeyInput.KEY_ADD));
        inputManager.addMapping("prev_entity", new KeyTrigger(KeyInput.KEY_SUBTRACT));
    }
    

	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		if(name.equals("print_bboxes") && !isPressed){
			renderDebug = !renderDebug;
		}else if (name.equals("prev_entity")&& !isPressed){
			prevTemplate();
		}else if (name.equals("next_entity")&& !isPressed){
			nextTemplate();
		}else if (name.equals("add_model")&& !isPressed){
			addModel();
		}else if (name.equals("remove_model")&& !isPressed){
			removeModel();
		}
	}
 
    /**
     * add terrain
     */
    private void setupScene() {
    	addModel();
    }
 
    @Override
    public void update() {
    	if(renderDebug && bboxes.getChildren().size()<=0){
//    		addBBoxes(rootNode);
    		addSkeletons();
    	}
    	else if (!renderDebug && bboxes.getChildren().size()>0){
    		removeBBoxesFromRoot();
    		removeSkeletons();
    	}
    	
        super.update();
    }
 
    private void removeBBoxesFromRoot() {
    	System.out.println("remove boxes");
		bboxes.detachAllChildren();
	}


	private void addBBoxes(Node n) {
		System.out.println("add boxes");
		for(Spatial s : n.getChildren()){
			if(s instanceof Geometry){
				Node a = s.getParent();
				if(a==null)return;
				BoundingVolume bound = ((Geometry) s).getModelBound();
				if(bound instanceof BoundingBox) {
				Box b = new Box(bound.getCenter(), ((BoundingBox) bound).getXExtent(), ((BoundingBox) bound).getYExtent(), ((BoundingBox) bound).getZExtent());
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
	
	protected void addSkeletons(){
		for(Node n : nodes){
			SkeletonControl con = n.getControl(SkeletonControl.class);
			if(con != null) {
		        SkeletonDebugger skeletonDebug = new SkeletonDebugger("skeletondebug", con.getSkeleton());
		        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		        mat.getAdditionalRenderState().setWireframe(true);
		        mat.setColor("Color", ColorRGBA.Red);
		        mat.getAdditionalRenderState().setDepthTest(false);
		        skeletonDebug.setMaterial(mat);
		        n.attachChild(skeletonDebug);
			}
		}
	}
	
	protected void removeSkeletons() {
		for(Node n : nodes){
			n.detachChildNamed("skeletondebug");
		}
	}


	protected void nextTemplate(){ template_index++; if(template_index>=templates.length)template_index=templates.length-1; System.out.println("next template:"+templates[template_index]); }
    
    protected void prevTemplate(){ template_index--; if(template_index<0)template_index=0;  System.out.println("prev template:"+templates[template_index]);}
    
    protected void addModel(){   	

    	Node n = Assembler2.getModel3(templates[template_index]);
    	
    	if(n != null){
//    		n.removeControl(JMEAnimationController.class);
//    		n.removeControl(SkeletonControl.class);
    		nodes.add(n);
    		currentNode++;
    		int x = currentNode;
    		int y = 2+((currentNode%2)*-1);//*currentNode;
    		n.setLocalTranslation(x, 0.0f, y);
    		rootNode.attachChild(n);
    	}
    }
    
    protected void removeModel(){
    	if(currentNode<0)
    		currentNode = 0;
    	if(nodes.size()<=0)
    		return;
    	
    	Node n = nodes.remove(nodes.size()-1);
    	if(n != null){
    		rootNode.detachChild(n);
    		n = null;
    	}
    	currentNode--;
    }
    
	/**
     * Entry point
     */
    public static void main(String[] args) {
    	TestEndlessModels app = new TestEndlessModels();
        app.start();
    }
}
