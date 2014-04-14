package com.l2client.test;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;

public class TestScrollingTexture extends SimpleApplication{

	    private TerrainQuad terrain;
	    PointLight pl;
	    Geometry lightMdl;


	    public static void main(String[] args) {
	    	TestScrollingTexture app = new TestScrollingTexture();
	        app.start();
	    }

	    @Override
	    public void initialize() {
	        super.initialize();
	    }

	    @Override
	    public void simpleInitApp() {

	        Material material = new Material(assetManager, "com/l2client/materials/LightingScrolling.j3md");

	        Texture grass = assetManager.loadTexture("Textures/Terrain/splat/grass.jpg");
	        grass.setWrap(WrapMode.Repeat);
	        material.setTexture("DiffuseMap", grass);
	        Texture normal = assetManager.loadTexture("Textures/Terrain/splat/grass_normal.jpg");
	        normal.setWrap(WrapMode.Repeat);
	        material.setTexture("NormalMap", normal);
	        material.setFloat("ScrollSpeed", 0.1f);
	        material.setVector2("ScrollDirection", new Vector2f(0.1f,0.3f));

	        // CREATE HEIGHTMAP
	        AbstractHeightMap heightmap = null;
	        try {
	            //heightmap = new HillHeightMap(1025, 1000, 50, 100, (byte) 3);
	        	Texture heightMapImage = assetManager.loadTexture("Textures/Terrain/splat/mountains512.png");
	            heightmap = new ImageBasedHeightMap(heightMapImage.getImage(), 1f);
	            heightmap.load();

	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        /*
	         * Here we create the actual terrain. The tiles will be 65x65, and the total size of the
	         * terrain will be 513x513. It uses the heightmap we created to generate the height values.
	         */
	        /**
	         * Optimal terrain patch size is 65 (64x64).
	         * The total size is up to you. At 1025 it ran fine for me (200+FPS), however at
	         * size=2049, it got really slow. But that is a jump from 2 million to 8 million triangles...
	         */
	        terrain = new TerrainQuad("terrain", 65, 513, heightmap.getHeightMap());
	        TerrainLodControl control = new TerrainLodControl(terrain, getCamera());
	        control.setLodCalculator( new DistanceLodCalculator(65, 2.7f) ); // patch size, and a multiplier
	        terrain.addControl(control);
	        terrain.setMaterial(material);
	        terrain.setLocalTranslation(0, -100, 0);
	        terrain.setLocalScale(2f, 0.5f, 2f);
	        rootNode.attachChild(terrain);

	        DirectionalLight light = new DirectionalLight();
	        light.setDirection((new Vector3f(-0.5f, -1f, -0.5f)).normalize());
	        rootNode.addLight(light);

	        cam.setLocation(new Vector3f(0, 10, -10));
	        cam.lookAtDirection(new Vector3f(0, -1.5f, -1).normalizeLocal(), Vector3f.UNIT_Y);
	    }
	}

