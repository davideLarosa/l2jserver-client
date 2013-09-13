package com.l2client.test;

import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.font.BitmapText;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.VertexBuffer.Usage;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.util.SkyFactory;
import com.jme3.util.TangentBinormalGenerator;

/**
 * Uses the terrain's lighting texture with normal maps and lights.
 *
 * @author bowens
 */
public class TestTerrainLightingParallaxShader extends SimpleApplication {

    private TerrainQuad terrain;
    Material matTerrain;
    protected BitmapText hintText;
//    PointLight pl;
    Geometry  g00;
    private float grassScale = 5;
    private float dirtScale = 5;
    private float rockScale = 5;
    private float brickScale = 5;
    Vector3f lightDir = new Vector3f();
    DirectionalLight dl;
    

    public static void main(String[] args) {
        TestTerrainLightingParallaxShader app = new TestTerrainLightingParallaxShader();
        app.start();
    }

    @Override
    public void initialize() {
        super.initialize();
//
//        loadHintText();
    }

    @Override
    public void simpleInitApp() {
        setupKeys();

        cam.setFrustumNear(1);
        cam.setFrustumFar(5000);
        
        rootNode.detachAllChildren();

        // First, we load up our textures and the heightmap texture for the terrain

//        // TERRAIN TEXTURE material
////        matTerrain = new Material(assetManager, "com/l2client/materials/TerrainLightingParallax0.j3md");
////        matTerrain = new Material(assetManager, "com/l2client/materials/TerrainLighting4.j3md");
//        matTerrain = new Material(assetManager, "com/l2client/materials/TLP.j3md");
////        matTerrain = new Material(assetManager, "com/l2client/materials/TerrainLightingParallax.j3md");
//
//        // ALPHA map (for splat textures) Red = dif0, Green= dif1, Blue=dif2 Alpha=dif3
//        matTerrain.setTexture("AlphaMap", assetManager.loadTexture("com/l2client/test/material/terrain_blend.png"));
//
//        // HEIGHTMAP image (for the terrain heightmap)
//        Texture heightMapImage = assetManager.loadTexture("Textures/Terrain/splat/mountains512.png");
//
//        // GRASS texture
//        Texture grass = assetManager.loadTexture("com/l2client/test/material/asphaltwasteland03.dds");
//        grass.setWrap(WrapMode.Repeat);
//        matTerrain.setTexture("DiffuseMap_0", grass);
//        matTerrain.setFloat("DiffuseMap_0_scale", grassScale);
//
//        // DIRT texture
//        Texture dirt = assetManager.loadTexture("com/l2client/test/material/chemicalwastes01.dds");
//        dirt.setWrap(WrapMode.Repeat);
//        matTerrain.setTexture("DiffuseMap_1", dirt);
//        matTerrain.setFloat("DiffuseMap_1_scale", dirtScale);
//
//        // ROCK texture
//        Texture rock = assetManager.loadTexture("com/l2client/test/material/grassgreensuburb01.dds");
//        rock.setWrap(WrapMode.Repeat);
//        matTerrain.setTexture("DiffuseMap_2", rock);
//        matTerrain.setFloat("DiffuseMap_2_scale", rockScale);
//
//        // BRICK texture
//        Texture brick = assetManager.loadTexture("com/l2client/test/material/pebblesdirtwasteland01.dds");
//        brick.setWrap(WrapMode.Repeat);
//        matTerrain.setTexture("DiffuseMap_3", brick);
//        matTerrain.setFloat("DiffuseMap_3_scale", brickScale);
//
//        Texture normalMap0 = assetManager.loadTexture("com/l2client/test/material/asphaltwasteland03_n.dds");
//        normalMap0.setWrap(WrapMode.Repeat);
//        Texture normalMap1 = assetManager.loadTexture("com/l2client/test/material/chemicalwastes01_n.dds");
//        normalMap1.setWrap(WrapMode.Repeat);
//        Texture normalMap2 = assetManager.loadTexture("com/l2client/test/material/grassgreensuburb01_n.dds");
//        normalMap2.setWrap(WrapMode.Repeat);
//        Texture normalMap3 = assetManager.loadTexture("com/l2client/test/material/pebblesdirtwasteland01_n.dds");
//        normalMap3.setWrap(WrapMode.Repeat);
//        matTerrain.setTexture("NormalMap_0", normalMap0);
//        matTerrain.setTexture("NormalMap_1", normalMap1);
//        matTerrain.setTexture("NormalMap_2", normalMap2);
//        matTerrain.setTexture("NormalMap_3", normalMap3);
        
        
        
        // TERRAIN TEXTURE material
//        matTerrain = new Material(assetManager, "Common/MatDefs/Terrain/TerrainLighting.j3md");//yellow
//      matTerrain = new Material(assetManager, "com/l2client/materials/TerrainLightingParallax0.j3md");//nearly black
//      matTerrain = new Material(assetManager, "com/l2client/materials/TerrainLighting4.j3md");//wrong yellow
//      matTerrain = new Material(assetManager, "com/l2client/materials/TLP.j3md");//OK, but to light
      matTerrain = new Material(assetManager, "com/l2client/materials/TLP2.j3md");//OK, but to light
//        matTerrain = new Material(assetManager, "com/l2client/materials/TLP2.j3md");//quite dark
//      matTerrain = new Material(assetManager, "com/l2client/materials/TerrainLightingParallax.j3md");//mate painting, wrong but somewhat interresting

      // ALPHA map (for splat textures) Red = dif0, Green= dif1, Blue=dif2 Alpha=dif3
      matTerrain.setTexture("AlphaMap", assetManager.loadTexture("com/l2client/test/material/splat.png"));

      // GRASS texture
      Texture grass = assetManager.loadTexture("com/l2client/test/material/chterraindirt01.dds");
      grass.setWrap(WrapMode.Repeat);
      matTerrain.setTexture("DiffuseMap_0", grass);
      matTerrain.setFloat("DiffuseMap_0_scale", grassScale);

      // DIRT texture
      Texture dirt = assetManager.loadTexture("com/l2client/test/material/jmrock01.dds");
      dirt.setWrap(WrapMode.Repeat);
      matTerrain.setTexture("DiffuseMap_1", dirt);
      matTerrain.setFloat("DiffuseMap_1_scale", dirtScale);

      // ROCK texture
      Texture rock = assetManager.loadTexture("com/l2client/test/material/chterraingrass01.dds");
      rock.setWrap(WrapMode.Repeat);
      matTerrain.setTexture("DiffuseMap_2", rock);
      matTerrain.setFloat("DiffuseMap_2_scale", rockScale);

      // BRICK texture
      Texture brick = assetManager.loadTexture("com/l2client/test/material/cobblestone01.dds");
      brick.setWrap(WrapMode.Repeat);
      matTerrain.setTexture("DiffuseMap_3", brick);
      matTerrain.setFloat("DiffuseMap_3_scale", brickScale);

      Texture normalMap0 = assetManager.loadTexture("com/l2client/test/material/chterraindirt01_n.dds");
      normalMap0.setWrap(WrapMode.Repeat);
      Texture normalMap1 = assetManager.loadTexture("com/l2client/test/material/jmrock01_n.dds");
      normalMap1.setWrap(WrapMode.Repeat);
      Texture normalMap2 = assetManager.loadTexture("com/l2client/test/material/chterraingrass01_n.dds");
      normalMap2.setWrap(WrapMode.Repeat);
      Texture normalMap3 = assetManager.loadTexture("com/l2client/test/material/cobblestone01_n.dds");
      normalMap3.setWrap(WrapMode.Repeat);
      matTerrain.setTexture("NormalMap_0", normalMap0);
      matTerrain.setTexture("NormalMap_1", normalMap1);
      matTerrain.setTexture("NormalMap_2", normalMap2);
      matTerrain.setTexture("NormalMap_3", normalMap3);

        createSky();

        
boolean old = false;

if(old){
	    // HEIGHTMAP image (for the terrain heightmap)
	    Texture heightMapImage = assetManager.loadTexture("Textures/Terrain/splat/mountains512.png");
        // CREATE HEIGHTMAP
        AbstractHeightMap heightmap = null;
        try {
            //heightmap = new HillHeightMap(1025, 1000, 50, 100, (byte) 3);

            heightmap = new ImageBasedHeightMap(heightMapImage.getImage(), .5f);
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
        terrain = new TerrainQuad("terrain", 65, 513, heightmap.getHeightMap());//, new LodPerspectiveCalculatorFactory(getCamera(), 4)); // add this in to see it use entropy for LOD calculations
        TerrainLodControl control = new TerrainLodControl(terrain, getCamera());
        terrain.addControl(control);
        terrain.setMaterial(matTerrain);
        terrain.setModelBound(new BoundingBox());
        terrain.updateModelBound();
        terrain.setLocalTranslation(0, -10, 0);
        terrain.setLocalScale(1f, 1f, 1f);
        rootNode.attachChild(terrain);
}
else
{

//		g00 = (Geometry) ((Node)assetManager.loadModel("untitled.mesh.xml")).getChild(0);
//        TangentBinormalGenerator.generate(g00.getMesh());
		//small = 9x9
//	g00 = (Geometry) ((Node)assetManager.loadModel("Models/Terrain/Terrain.mesh.xml")).getChild(0);
       g00 = (Geometry) assetManager.loadModel("com/l2client/test/material/130_153.obj");
//       TangentBinormalGenerator.generateFaceNormalsAndTangents(g00.getMesh());
//       ModelConverter.optimize(g00, true);
        TangentBinormalGenerator.generate(g00.getMesh());
//		TangentUtil.addTangentsToMesh(g00.getMesh());
        g00.getMesh().clearBuffer(Type.Binormal);
        g00.getMesh().clearBuffer(Type.Color);
        g00.getMesh().getBuffer(Type.Tangent).setUsage(Usage.Static);
        g00.setMaterial(matTerrain);
		  g00.setModelBound(new BoundingBox());
		  g00.updateModelBound();
		  g00.setLocalTranslation(0, -100, 0);
		  g00.setLocalScale(1f, 1f, 1f);
//		  g00.updateGeometricState();
		  
		  rootNode.attachChild(g00); 
//		  
//	        Geometry debug = new Geometry(
//	                "Debug Teapot",
//	                TangentBinormalGenerator.genTbnLines(((Geometry) g00).getMesh(), 0.5f)
//	        );
//	        Material debugMat = assetManager.loadMaterial("Common/Materials/VertexColor.j3m");
//	        debug.setMaterial(debugMat);
//	        debug.setCullHint(Spatial.CullHint.Never);
//	        debug.getLocalTranslation().set(g00.getLocalTranslation());
//	        debug.getLocalScale().set(g00.getLocalScale());
//	        rootNode.attachChild(debug);
}

        dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        dl.setDirection((new Vector3f(0f, -1f, 0f)).normalizeLocal());
        rootNode.addLight(dl);

//
        AmbientLight ambLight = new AmbientLight();
        ambLight.setColor(ColorRGBA.Gray);
        rootNode.addLight(ambLight);
		
        


//        cam.setLocation(new Vector3f(0, 10, -10));
//        cam.lookAtDirection(new Vector3f(0, -1.5f, -1).normalizeLocal(), Vector3f.UNIT_Y);
        cam.setLocation(new Vector3f(0, 300, 0));
        cam.lookAtDirection(new Vector3f(0, -1f, 0), Vector3f.UNIT_Y);
    }

//    public void loadHintText() {
//        hintText = new BitmapText(guiFont, false);
//        hintText.setSize(guiFont.getCharSet().getRenderedSize());
//        hintText.setLocalTranslation(0, getCamera().getHeight(), 0);
//        hintText.setText("Hit T to switch to wireframe,  P to switch to tri-planar texturing");
//        guiNode.attachChild(hintText);
//    }

    private void setupKeys() {
        flyCam.setMoveSpeed(50);
    }

    private void createSky() {
        Texture west = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_west.jpg");
        Texture east = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_east.jpg");
        Texture north = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_north.jpg");
        Texture south = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_south.jpg");
        Texture up = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_up.jpg");
        Texture down = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_down.jpg");

        Spatial sky = SkyFactory.createSky(assetManager, west, east, north, south, up, down);
        rootNode.attachChild(sky);

    }
    static float time = 0f;
    @Override
    public void simpleUpdate(float tpf) {
        time+=(tpf*0.2f);
        lightDir.set(FastMath.sin(time), -1, FastMath.cos(time));
        dl.setDirection(lightDir);
    }
}
