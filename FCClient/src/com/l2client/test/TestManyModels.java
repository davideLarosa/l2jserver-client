package com.l2client.test;

import java.util.concurrent.Callable;

import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.l2client.animsystem.jme.JMEAnimationController;
import com.l2client.app.Assembler2;
import com.l2client.app.ExtendedApplication;
import com.l2client.util.PartSetManager;

public class TestManyModels extends ExtendedApplication implements ActionListener {


	int MAX_NODES = 10;
	
	PartSetManager man = PartSetManager.get();
	
	Node[] nodes = new Node[MAX_NODES];
	
	int currentNode = 0;
	
    public void simpleInitApp() {
    	man.loadParts("megaset.csv");

        DirectionalLight dr = new DirectionalLight();
        dr.setColor(ColorRGBA.White);
        dr.setDirection(new Vector3f(1, 0 , 1));
        
        AmbientLight am = new AmbientLight();
        am.setColor(ColorRGBA.White);
        rootNode.addLight(am);
        rootNode.addLight(dr);
 
        setupScene();
        
        inputManager.addListener(this, "print_scenegraph", "add_model", "remove_model", "toggle_singleStep", "advance_singleStep");
        inputManager.addMapping("print_scenegraph", new KeyTrigger(KeyInput.KEY_F6));
        inputManager.addMapping("add_model", new KeyTrigger(KeyInput.KEY_F9));
        inputManager.addMapping("remove_model", new KeyTrigger(KeyInput.KEY_F10));
        
        inputManager.addMapping("toggle_singleStep", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("advance_singleStep", new KeyTrigger(KeyInput.KEY_0));
        JMEAnimationController.singleStep = false;
    }
    

	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		if(name.equals("print_scenegraph") && !isPressed){
			printHierarchy(rootNode, "");
		}else if (name.equals("add_model")&& !isPressed){
			addModel();
		}else if (name.equals("remove_model")&& !isPressed){
			removeModel();
		}else if (name.equals("toggle_singleStep")&& !isPressed){
			JMEAnimationController.singleStep = !JMEAnimationController.singleStep;
			System.out.println("Animation singlestep ="+JMEAnimationController.singleStep);
		}else if (name.equals("advance_singleStep")&& !isPressed){
			enqueue(new Callable<Object>() {

				@Override
				public Object call() throws Exception {
					JMEAnimationController.singleStep = !JMEAnimationController.singleStep;
					rootNode.updateLogicalState(0.25f);
					System.out.println("Animation ++");
					JMEAnimationController.singleStep = !JMEAnimationController.singleStep;
					return null;
				}
				
			});

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
        super.update();
    }
 
    protected void printHierarchy(Spatial n, String indent) {
		System.out.println(indent+n.getName()+":"+n.getClass());
		if(n instanceof Node)
			for(Spatial c : ((Node)n).getChildren())
				printHierarchy(c, indent+" ");
		
		for(int i = 0; i<n.getNumControls(); i++)
			System.out.println(indent+"Controller:"+n.getControl(i).getClass());
	}
    
    protected void addModel(){   	
    	if(currentNode<0)
    		currentNode = 0;
    	if(currentNode >= MAX_NODES){
    		currentNode = 0;
    	}
    	Node n = nodes[currentNode];
    	if(n!= null){
    		rootNode.detachChild(n);
    		n = null;
    	}
    	
//    	if(currentNode%2!=0){
	    	n = Assembler2.getModel3("pelfmmage"); //goblin");
//    	} else{
//    		VisibleModel v = new VisibleModel(new NewCharSummary());
//    		v.attachVisuals();
//    		n = v;
//    	}
    	
    	if(n != null){
    		nodes[currentNode] = n;
    		currentNode++;
    		int x = currentNode;
    		int y = 2+((currentNode%2)*-1);//*currentNode;
    		System.out.println("New Model at:"+x+","+y);
    		n.setLocalTranslation(x, y, 0.0f);
  
    		rootNode.attachChild(n);
//    		n.updateGeometricState();
    	}
    }
    
    protected void removeModel(){
    	if(currentNode<0)
    		currentNode = 0;
    	
    	if(currentNode >= MAX_NODES)
    		currentNode = MAX_NODES-1; 
    	
    	Node n = nodes[currentNode];
    	
    	if(n != null){
    		rootNode.detachChild(n);
    		n = null;
    		currentNode--;
    	}
    }
    
	/**
     * Entry point
     */
    public static void main(String[] args) {
    	TestManyModels app = new TestManyModels();
        app.start();
    }
}
