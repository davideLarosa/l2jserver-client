package com.l2client.test;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.l2client.app.Singleton;
import com.l2client.asset.Asset;
import com.l2client.controller.area.IArea;
import com.l2client.controller.area.SimpleTerrainManager;
import com.l2client.controller.area.Tile;
import com.l2client.navigation.NavTestHelper;
import com.l2client.navigation.TiledNavMesh;

public class TestRelativeTile extends SimpleApplication implements ActionListener {

	
	Node debugNodes = new Node("debugs");

	private Singleton sin = Singleton.get();
	
    public static void main(String[] args){
        TestRelativeTile app = new TestRelativeTile();
        app.start();
    }

    @Override
    public void simpleInitApp() {
    	sin.init(SimpleTerrainManager.get());
    	cam.setLocation(new Vector3f(-9980f,50f,8450f));
    	cam.setFrustumFar(1000f);
    	cam.setFrustumNear(1f);
    	cam.lookAt(new Vector3f(-9880f,10f,8350), Vector3f.UNIT_Y);
    	flyCam.setMoveSpeed(50f);
    	
		assetManager = sin.getAssetManager().getJmeAssetMan();
		debugNodes.setLocalTranslation(Vector3f.UNIT_Y.mult(0.1f));
    	rootNode.attachChild(debugNodes);

        DirectionalLight light = new DirectionalLight();
        light.setDirection((new Vector3f(-0.5f, -1f, -0.5f)).normalize());
        rootNode.addLight(light);

        AmbientLight ambLight = new AmbientLight();
        ambLight.setColor(new ColorRGBA(0.2f, 0.2f, 0.2f, 1f));
        rootNode.addLight(ambLight);
    	

        Asset a = new Asset("/tile/121_177/121_177.jnv", "/tile/121_177/121_177.jnv");
        sin.getAssetManager().loadAsset(a, true);
        if(a.getBaseAsset() instanceof TiledNavMesh )
        	NavTestHelper.debugShowMesh(assetManager, debugNodes,(TiledNavMesh) a.getBaseAsset());
        
        Asset b = new Asset("/tile/121_177/121_177.j3o", "/tile/121_177/121_177.j3o");
        sin.getAssetManager().loadAsset(b, true);
        if(b.getBaseAsset() instanceof Spatial ) {
        	Spatial n = (Spatial)b.getBaseAsset();
//			Material mat = new Material(Singleton.get().getAssetManager().getJmeAssetMan(), "Common/MatDefs/Misc/Unshaded.j3md");
//	        mat.setColor("Color", ColorRGBA.randomColor());
		    Material mat = new Material(Singleton.get().getAssetManager().getJmeAssetMan(), "Common/MatDefs/Light/Lighting.j3md");
		    mat.setBoolean("UseMaterialColors",true);    
		    mat.setColor("Diffuse",ColorRGBA.randomColor());
		    n.setLocalTranslation(Tile.getWorldPositionOfXTile(121)+IArea.TERRAIN_SIZE_HALF, 0f,Tile.getWorldPositionOfZTile(177)-IArea.TERRAIN_SIZE_HALF);
	        n.setMaterial(mat);
        	rootNode.attachChild(n);
        }
        /*       System.out.println("tile x=121 should be world -9984:"+getWorldPositionOfXTile(121));
       System.out.println("tile x=122 should be world -9728:"+getWorldPositionOfXTile(122));
       System.out.println("tile z=176 should be world 8192:"+getWorldPositionOfZTile(176));
       System.out.println("tile z=177 should be world 8448:"+getWorldPositionOfZTile(177));*/
        NavTestHelper.debugShowBox(assetManager, debugNodes, new Vector3f(-9984f+IArea.TERRAIN_SIZE_HALF,0f,8448f-IArea.TERRAIN_SIZE_HALF), ColorRGBA.LightGray, IArea.TERRAIN_SIZE_HALF, 0.1f, IArea.TERRAIN_SIZE_HALF);

        inputManager.addListener(this, "print_scenegraph");
        inputManager.addMapping("print_scenegraph", new KeyTrigger(KeyInput.KEY_F6));

    }
    

	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		if(name.equals("print_scenegraph") && !isPressed){
			printHierarchy(rootNode, "");
		}
	}
	
    protected void printHierarchy(Spatial n, String indent) {
		System.out.println(indent+n.getName()+":"+n.getClass()+" at "+n.getWorldTranslation()+ " bounds:"+n.getWorldBound());
		if(n instanceof Node)
			for(Spatial c : ((Node)n).getChildren())
				printHierarchy(c, indent+" ");
		
		for(int i = 0; i<n.getNumControls(); i++)
			System.out.println(indent+"Controller:"+n.getControl(i).getClass());
	}
    
    @Override
	public void simpleUpdate(float tpf){
    	super.simpleUpdate(tpf);
    }
}
