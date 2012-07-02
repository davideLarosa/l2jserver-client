package com.l2client.controller;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import com.jme3.light.Light;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 * The scene manager provides means to access to the visual hierarchy, for
 * attaching terrain tiles (used by the @see SimpleTerrainManager), or as a
 * general access to the scene root (for attachment of variable kind) All visual
 * elements should be attached to the scene root in a somewhat consistent way.
 * 
 * This class is a singleton and should be accessed by using SceneManager.get()
 * 
 */
// TODO improve the handling of the terrain and root nodes, can cause
// inconsistences in case someone removes all nodes below root
public final class SceneManager {

	private static Logger log = Logger.getLogger(SceneManager.class.getName());

	private static SceneManager singleton = null;
	
	private class Tuple{
		/**
		 * @param t target
		 * @param e element
		 * @param p element type
		 * @param a action
		 */
		Tuple(Object t, Object e, int p, int a){target=t;element=e;type=p;act=a;}
		public Object target;
		public Object element;
		public int type;//0 = Node, 1 = Control, 2 = Light
		public int act;//0 = add, 1 = remove
	}

	private volatile Node root = null;
	private volatile Node chars = new Node("players");
	private volatile Node terrain = new Node("terrains");
	private volatile Node walker = new Node("npcs");

	private ConcurrentLinkedQueue<Tuple> queue = new ConcurrentLinkedQueue<Tuple>();

	private boolean removeWalkers;

	private boolean removeChar;

	private boolean removeTerrains;

	private boolean removeLights;

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
		queue.add(new Tuple(root,chars,0,0));
		queue.add(new Tuple(root,terrain,0,0));
		queue.add(new Tuple(root,walker,0,0));
	}
	
	public Node getRoot(){
		return root;
	}
	
	public void update(float tpf){
		if(root != null){
			charUpdate();
			terrainUpdate();
			walkerUpdate();
			lightUpdate();
			queueUpdate();
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
			for(Light l :root.getLocalLightList())
				root.removeLight(l);
			
			removeLights = false;
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
	
	private void queueUpdate(){
		Tuple t = null;
		while((t=queue.poll()) != null){
			if(t.element != null && t.target != null){
			switch(t.type){
			case 0://node
				if(t.act != 1)
					((Node)t.target).attachChild((Spatial)t.element);
				else
					((Node)t.target).detachChild((Spatial)t.element);
					break;
			case 1://control
				if(t.act != 1)
					((Node)t.target).addControl((AbstractControl)t.element);
				else
					((Node)t.target).removeControl((AbstractControl)t.element);
				break;
			case 2://light
				if(t.act != 1)
					((Node)t.target).addLight((Light)t.element);
				else
					((Node)t.target).removeLight((Light)t.element);
				break;
			}}
		}
	}
	
	/**
	 * Queues n for action on root nodes
	 * @param n			node to be queued
	 * @param action	0 add node, 1 remove node
	 */
	public void changeRootNode(Spatial n, int action) {
		Tuple t = new Tuple(root,n,0, action);
		queue.add(t);
	}
	
	/**
	 * Queues n for action on tgt node
	 * @param tgt		target node of n's action
	 * @param n			node to be queued
	 * @param action	0 add node, 1 remove node
	 */
	public void changeAnyNode(Spatial tgt, Spatial n, int action) {
		Tuple t = new Tuple(tgt,n,0, action);
		queue.add(t);
	}
	
	/**
	 * Queues n for action on char nodes
	 * @param n			node to be queued
	 * @param action	0 add node, 1 remove node
	 */
	public void changeCharNode(Spatial n, int action) {
		Tuple t = new Tuple(chars,n,0, action);
		queue.add(t);
	}
	
	/**
	 * Queues n for action on terrain nodes
	 * @param n			node to be queued
	 * @param action	0 add node, 1 remove node
	 */
	public void changeTerrainNode(Spatial n, int action) {
		Tuple t = new Tuple(terrain,n,0, action);
		queue.add(t);
	}
	
	public void changeWalkerNode(Spatial n, int action) {
		Tuple t = new Tuple(walker,n,0, action);
		queue.add(t);
	}
	
	public void changeRootLight(Light pLight, int action){
		Tuple t = new Tuple(root,pLight,2,action);
		queue.add(t);
	}
	
	public void changeControl(Node tgt, AbstractControl pContr, int action){
		Tuple t = new Tuple(tgt,pContr,1, action);
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

	public void removeAll() {
		removeWalkers();
		removeChar();
		removeTerrains();
		removeLights();
		
	}

}
