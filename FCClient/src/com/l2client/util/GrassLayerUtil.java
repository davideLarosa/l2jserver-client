package com.l2client.util;

import java.nio.ByteBuffer;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import jme3tools.optimize.GeometryBatchFactory;

import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingSphere;
import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.CollisionResults;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.shader.VarType;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;

/**
 * TODO description of working of the utility
 * 
 */
public class GrassLayerUtil {

	/**
	 * Creates a patch field of grass quads distributed on the y up surface of
	 * the given spatial
	 * 
	 * @param spatial
	 *            Spatial with a geometry attached on which the grass should be
	 *            planted on
	 * @param assetManager
	 *            Reference to the jme asset manager for loading of grass
	 *            texture
	 * @param texturePath
	 *            Path to the grass image ressource
	 * @param patchScaleVariation
	 *            Patches will be scaled randomly by a factor between
	 *            1/patchScaleVariation and patchScaleVariation
	 * @param patchWidth
	 *            Width of an unscaled patch quad
	 * @param patchHeight
	 *            Height of an unscaled patch quad
	 * @param inc
	 *            Space between quads
	 * @param fadeEnd
	 *            At what distance from the camera no patches should be visible
	 *            anymore, when 0 disabled
	 * @param fadeRange
	 *            At what distance from the fadeEnd fading should start
	 * @param minIntensity
	 * 			  Minimum intensity of texture color to be a valid placement position from 0-1.0
	 * @param channelId
	 * 			  Which texture channel should be used to check for placement
	 * @return
	 */
	public static Node createPatchField(Spatial spatial,
			AssetManager assetManager, String texturePath,
			float patchScaleVariation, float patchWidth, float patchHeight,
			float inc, float fadeEnd, float fadeRange, float minIntensity,
			int channelId, int clusters) {
		Node spatNode = (Node) spatial;
		Node grassLayer = new Node("grass_" + spatial.getName());
		grassLayer.setModelBound(new BoundingBox());

		Texture tex = assetManager.loadTexture(texturePath);
		Material faceMat = new Material(assetManager,
				"/com/l2client/materials/LightingGrass.j3md");
		faceMat.getAdditionalRenderState().setBlendMode(
				RenderState.BlendMode.Alpha);
		faceMat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
		faceMat.getAdditionalRenderState().setDepthWrite(true);
		faceMat.getAdditionalRenderState().setDepthTest(true);
		faceMat.setTransparent(true);
		faceMat.setTextureParam("DiffuseMap", VarType.Texture2D, tex);
		faceMat.setFloat("AlphaDiscardThreshold", 0.3f);
		// faceMat.setTextureParam("AlphaMap", VarType.Texture2D,tex);
		faceMat.setBoolean("UseAlpha", true);
		if (fadeEnd > 0f) {
			faceMat.setFloat("FadeEnd", fadeEnd);// 300f);
			faceMat.setFloat("FadeRange", fadeRange); // 50f);
			faceMat.setBoolean("FadeEnabled", true);
		}
		faceMat.setBoolean("Swaying", true);
		faceMat.setVector3("SwayData", new Vector3f(1.0f, 0.5f, 300f));// frequency,
																		// variation,
																		// third?
		faceMat.setVector2("Wind", new Vector2f(1f, 1f));

		Geometry terrain = null;

		if (spatial instanceof Geometry) {
			terrain = (Geometry) spatial;
		} else {
			for (Spatial currentSpatial : spatNode.getChildren()) {
				if (currentSpatial instanceof Geometry) {
					terrain = (Geometry) currentSpatial;
					break;
				}
			}
		}

		if (terrain == null || spatNode.getChildren().isEmpty()) {
			Logger.getLogger(GrassLayerUtil.class.getName()).log(Level.SEVERE,
					"Could not find terrain object.", new Exception());
			System.exit(0);
		}

		// Generate grass uniformly with random offset.
		float terrainWidth = 1f * 256; // get width length of terrain(assuming
										// its a square)
		BoundingVolume bounds = ((Spatial) terrain).getWorldBound();
		if (BoundingVolume.Type.AABB.equals(bounds.getType())) {
			BoundingBox bb = ((BoundingBox) bounds);
			terrainWidth = Math.max(bb.getXExtent(), bb.getZExtent());
			terrainWidth *= 2f;
		} else if (BoundingVolume.Type.Sphere.equals(bounds.getType())) {
			terrainWidth = ((BoundingSphere) bounds).getRadius();
			terrainWidth *= 2f;
		}
		Vector3f centre = bounds.getCenter(); // get the centr location of the
												// terrain
		Vector2f grassPatchRandomOffset = new Vector2f().zero();
		Vector3f candidateGrassPatchLocation = new Vector3f();

		Random rand = new Random();
		Ray ray = new Ray(Vector3f.ZERO, Vector3f.UNIT_Y.mult(-1f));
		CollisionResults results = new CollisionResults();
		float ax, az;
		for (float x = centre.x - terrainWidth / 2 + inc; x < centre.x
				+ terrainWidth / 2 - inc; x += inc) {
			for (float z = centre.z - terrainWidth / 2 + inc; z < centre.z
					+ terrainWidth / 2 - inc; z += inc) {
				grassPatchRandomOffset.set(inc, inc);
				grassPatchRandomOffset.multLocal(rand.nextFloat()); // make the
																	// off set
																	// length a
																	// random
																	// distance
																	// smaller
																	// than the
																	// increment
																	// size
				grassPatchRandomOffset
						.rotateAroundOrigin(
								(float) (((int) (rand.nextFloat() * 359)) * (Math.PI / 180)),
								true); // rotate the offset by a random angle

				ax = x + grassPatchRandomOffset.x;
				az = z + grassPatchRandomOffset.y;
				ray.setOrigin(new Vector3f(ax, centre.y + terrainWidth, az));
				terrain.collideWith(ray, results);

				if (results.size() <= 0)
					continue;

				try {
					if (results.size() > 0) {

						candidateGrassPatchLocation.set(ax, results
								.getCollision(0).getContactPoint().y, az);
						results.clear();

						if (isGrassLayer(candidateGrassPatchLocation, terrain,
								minIntensity, terrainWidth, channelId)) {
							// this will be in world coords, but we want it to
							// be in local
							candidateGrassPatchLocation.subtractLocal(terrain
									.getWorldTranslation());
							grassLayer.attachChild(createGrassPatch(
									candidateGrassPatchLocation, faceMat,
									patchScaleVariation, patchWidth,
									patchHeight, rand.nextFloat()));
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		GeometryBatchFactory.optimize(grassLayer);
		grassLayer.updateGeometricState();
		
//		DistanceLodControl c = new DistanceLodControl();
//		(grassLayer).getChild(0).addControl(c);
//		c.setDistTolerance(250f);

		return (Node) grassLayer;
	}

	private static Node createGrassPatch(Vector3f location, Material faceMat,
			float patchScaleVariation, float patchWidth, float patchHeight,
			float rand) {
		Node grassPatch = new Node();
		float selectedSizeVariation = (float) (rand * (patchScaleVariation - (1 / patchScaleVariation)))
				+ (1 / patchScaleVariation);
		Quad faceShape = new Quad((patchWidth * selectedSizeVariation),
				patchHeight * selectedSizeVariation, false);
		Geometry face1 = new Geometry("face1", faceShape);
		face1.move(-(patchWidth * selectedSizeVariation) / 2, 0, 0);
		grassPatch.attachChild(face1);

		Geometry face2 = new Geometry("face2", faceShape);
		face2.rotate(new Quaternion().fromAngleAxis(-FastMath.PI / 2,
				new Vector3f(0, 1, 0)));
		face2.move(0, 0, -(patchWidth * selectedSizeVariation) / 2);
		grassPatch.attachChild(face2);

		grassPatch.setCullHint(Spatial.CullHint.Dynamic);
		grassPatch.setQueueBucket(RenderQueue.Bucket.Transparent);

		face1.setMaterial(faceMat);
		face2.setMaterial(faceMat);

		grassPatch.rotate(new Quaternion().fromAngleAxis(
				(((int) (Math.random() * 359)) + 1) * (FastMath.PI / 190),
				new Vector3f(0, 1, 0)));
		grassPatch.setLocalTranslation(location);

		return grassPatch;
	}

	private static boolean isGrassLayer(Vector3f pos, Geometry terrain,
			float intensitiyThreshold, float scaledWidth, int channelId) {
		MatParam matParam = terrain.getMaterial().getParam("AlphaMap");
		if (matParam == null)// try to get the default one..
			matParam = terrain.getMaterial().getParam("DiffuseMap");
		if (matParam == null)
			return false;
		Texture tex = (Texture) matParam.getValue();
		Image image = tex.getImage();
		Vector2f uv = getPointPercentagePosition(terrain, pos, scaledWidth);

		ByteBuffer buf = image.getData(0);
		int width = image.getWidth();
		int height = image.getHeight();

		int x = (int) (uv.x * width);
		int y = (int) (uv.y * height);

		if (((TextureKey) tex.getKey()).isFlipY())
			y = height - y;
		// compute bytes of image
		int bytes = image.getFormat().getBitsPerPixel() / 8;
		int position = (y * width + x) * bytes;// image dependent..
		position += channelId;// move on by channel id offset

		if (position > buf.capacity())
			return false;

		buf.position(position);

		// This is file format dependent! For example I have an image in format
		// BGR8, with only 3 values and inverse order..
		// But we just read the first one here,..
		// TODO make selection of which value a parameter
		if (byte2float(buf.get()) >= intensitiyThreshold) {
			return true;
		} else {
			return false;
		}
	}

	private static Vector2f getPointPercentagePosition(Geometry terrain,
			Vector3f worldLoc, float scaledWidth) {
		Vector2f uv = new Vector2f(worldLoc.x, worldLoc.z);
		uv.subtractLocal(terrain.getWorldTranslation().x,
				terrain.getWorldTranslation().z); // center it on 0,0
		// float scaledSize = terrain.getTerrainSize()*scale;
		uv.addLocal(scaledWidth / 2, scaledWidth / 2); // shift the bottom left
														// corner up to 0,0
		uv.divideLocal(scaledWidth); // get the location as a percentage

		return uv;
	}

	private static float byte2float(byte b) {
		return ((float) (b & 0xFF)) / 255f;
	}

}
