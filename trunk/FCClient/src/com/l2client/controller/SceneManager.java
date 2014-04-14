package com.l2client.controller;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import com.jme3.light.Light;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.SceneProcessor;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 * The scene manager provides means to access to the visual hierarchy, for
 * attaching terrain tiles (used by the @see SimpleTerrainManager), or as a
 * general access to the scene root (for attachment of variable kind) All visual
 * elements should be attached to the scene root in a somewhat consistent way.
 * 
 * This class is a singleton and should be accessed by using Singleton.get().getSceneManager()
 * 
 */
// TODO improve the handling of the terrain and root nodes, can cause
// inconsistences in case someone removes all nodes below root
public final class SceneManager {

	private static Logger log = Logger.getLogger(SceneManager.class.getName());

	private static SceneManager singleton = null;
	
	private boolean onlyOneChangePerFrame = false;
	
	public enum Action {
		ADD,
		REMOVE;
	}
	
	public enum Type {
		NODE,
		CONTROL,
		LIGHT, 
		POSTPROCESSOR;
	}
	
	private class Tuple{
		/**
		 * @param t target
		 * @param e element
		 * @param p element type
		 * @param a action
		 */
		Tuple(Object t, Object e, Type p, Action a){target=t;element=e;type=p;act=a;}
		public Object target;
		public Object element;
		public Type type;//0 = Node, 1 = Control, 2 = Light
		public Action act;//0 = add, 1 = remove
	}

	private volatile ViewPort viewPort = null;
	private volatile Node root = null;
	private volatile Node chars = new Node("players");
	private volatile Node terrain = new Node("terrains");
	private volatile Node walker = new Node("npcs");
	private volatile Node items = new Node("items");

	private ConcurrentLinkedQueue<Tuple> queue = new ConcurrentLinkedQueue<Tuple>();

	private boolean removeWalkers = false;

	private boolean removeChar = false;

	private boolean removeTerrains = false;

	private boolean removeLights = false;

	private boolean removePostProcessors;

	private boolean removeItems;

	/**
	 * Internal constructor which also creates the terrain root, but does not
	 * attach it
	 */
	private SceneManager() {
		singleton = this;
	}

	/**
	 * Fetch the singleton instance (created in case not done so far)
	 * 
	 * @return The SceneManager instance
	 */
	public static SceneManager get() {
		if (singleton != null)
			return singleton;
		else {
			return new SceneManager();
		}
	}

	/**
	 * Sets the node of the scene to be used as visual root, anything rendered
	 * should be placed below. Terrain tiles should go to a special root, as
	 * they are swapped in and out dynamically.
	 * 
	 * @param n
	 *            node to be used as root (should be the one hooked up in the
	 *            rendering system)
	 */
	public void setRoot(Node n) {
		root = n;
		queue.add(new Tuple(root,chars,Type.NODE,Action.ADD));
		queue.add(new Tuple(root,terrain,Type.NODE,Action.ADD));
		queue.add(new Tuple(root,walker,Type.NODE,Action.ADD));
		queue.add(new Tuple(root,items,Type.NODE,Action.ADD));
	}
	
	public Node getRoot(){
		return root;
	}
	
	public void update(float tpf){
		if(root != null){
			lightUpdate();
			postProcessorUpdate();
			charUpdate();
			terrainUpdate();
			walkerUpdate();
			itemUpdate();
			queueUpdate();
		}
		
	}
	
	private void postProcessorUpdate(){
		if(removePostProcessors){
			log.fine("REMOVING ALL POSTPOROCESSORS");
			viewPort.getProcessors().clear();
			removePostProcessors = false;
		}
	}
	
	private void charUpdate(){
		if(removeChar){
			chars.detachAllChildren();
			removeChar = false;
		}
	}
	
	private void lightUpdate(){
		if(removeLights){	
			root.getLocalLightList().clear();
		}
	}
	
	private void terrainUpdate(){
		if(removeTerrains){
			terrain.detachAllChildren();
			removeTerrains = false;
		}
	}
	
	private void walkerUpdate(){
		if(removeWalkers){
			walker.detachAllChildren();
			removeWalkers = false;
		}
	}
	
	private void itemUpdate(){
		if(removeItems){
			items.detachAllChildren();
			removeItems = false;
		}
	}
	
	private void queueUpdate(){
		Tuple t = null;
		int nodes = 0;
		int controls = 0;
		int lights = 0;
		int postprocs = 0;
		while ((t = queue.poll()) != null) {
			if (t.element != null && t.target != null) {
				switch (t.type) {
				case NODE:// node
					if (t.act != Action.REMOVE) {
						((Node) t.target).attachChild((Spatial) t.element);
//						((Node)t.target).updateGeometricState();
					} else {
						((Node) t.target).detachChild((Spatial) t.element);
//						((Node)t.target).updateGeometricState();
					}
					nodes++;
					break;
				case CONTROL:// control
					if (t.act != Action.REMOVE)
						((Node) t.target)
								.addControl((AbstractControl) t.element);
					else
						((Node) t.target)
								.removeControl((AbstractControl) t.element);
					controls++;
					break;
				case LIGHT:// light
					if (t.act != Action.REMOVE) {
						((Node) t.target).addLight((Light) t.element);					
					}
					else {
						((Node) t.target).removeLight((Light) t.element);
					}
					lights++;
					break;
				case POSTPROCESSOR:// postprocessor
					if (t.act != Action.REMOVE)
						((ViewPort) t.target)
								.addProcessor((SceneProcessor) t.element);
					else
						((ViewPort) t.target)
								.removeProcessor((SceneProcessor) t.element);
					postprocs++;
					break;
				}
				if(onlyOneChangePerFrame)
					break;
			}
		}

		if (nodes > 0 || controls > 0 || lights > 0 || postprocs > 0)
			log.info("Updated " + nodes + " nodes, " + controls + " controls, "
					+ lights + " lights, " + postprocs + " postprocesses");
	}
	
	/**
	 * Queues n for action on root nodes
	 * @param n			node to be queued
	 * @param action	0 add node, 1 remove node
	 */
	public void changeRootNode(Spatial n, Action action) {
		Tuple t = new Tuple(root,n,Type.NODE, action);
		queue.add(t);
	}
	
	/**
	 * Queues n for action on tgt node
	 * @param tgt		target node of n's action
	 * @param n			node to be queued
	 * @param action	0 add node, 1 remove node
	 */
	public void changeAnyNode(Spatial tgt, Spatial n, Action action) {
		Tuple t = new Tuple(tgt,n,Type.NODE, action);
		queue.add(t);
	}
	
	/**
	 * Queues n for action on char nodes
	 * @param n			node to be queued
	 * @param action	0 add node, 1 remove node
	 */
	public void changeCharNode(Spatial n, Action action) {
		Tuple t = new Tuple(chars,n,Type.NODE, action);
		queue.add(t);
	}
	
	/**
	 * Queues n for action on terrain nodes
	 * @param n			node to be queued
	 * @param action	0 add node, 1 remove node
	 */
	public void changeTerrainNode(Spatial n, Action action) {
		Tuple t = new Tuple(terrain,n,Type.NODE, action);
		queue.add(t);
	}
	
	public void changeWalkerNode(Spatial n, Action action) {
		Tuple t = new Tuple(walker,n,Type.NODE, action);
		queue.add(t);
	}
	
	public void changeItemNode(Spatial n, Action action) {
		Tuple t = new Tuple(items,n,Type.NODE, action);
		queue.add(t);
	}
	
	public void changeRootLight(Light pLight, Action action){
		Tuple t = new Tuple(root,pLight,Type.LIGHT,action);
		queue.add(t);
	}
	
	public void changeControl(Node tgt, AbstractControl pContr, Action action){
		Tuple t = new Tuple(tgt,pContr, Type.CONTROL, action);
		queue.add(t);
	}
	
	public void removeWalkers(){
		this.removeWalkers = true;
	}
	
	public void removeChar(){
		this.removeChar = true;
	}
	public void removeTerrains(){
		this.removeTerrains = true;
	}
	
	public void removeLights(){
		this.removeLights = true;		
	}
	
	public void removeItems(){
		this.removeItems = true;		
	}

	public void removeAll() {
		removeWalkers();
		removeChar();
		removeTerrains();
		removeLights();
		removeItems();
		removePostProcessors();
	}

	public void removePostProcessors() {
		this.removePostProcessors = true;
	}
	
	public void changePostProcessor(SceneProcessor fpp, Action action){
		Tuple t = new Tuple(viewPort,fpp,Type.POSTPROCESSOR,action);
		queue.add(t);
	}

	public void setViewPort(ViewPort view) {
		this.viewPort = view;
		
	}

}
