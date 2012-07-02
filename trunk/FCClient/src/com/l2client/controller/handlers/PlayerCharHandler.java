package com.l2client.controller.handlers;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.l2client.component.AnimationSystem;
import com.l2client.component.EnvironmentComponent;
import com.l2client.component.JmeUpdateSystem;
import com.l2client.component.L2JComponent;
import com.l2client.component.PositioningSystem;
import com.l2client.component.SimplePositionComponent;
import com.l2client.component.VisualComponent;
import com.l2client.controller.area.IArea;
import com.l2client.controller.area.SimpleTerrainManager;
import com.l2client.controller.entity.Entity;
import com.l2client.controller.entity.EntityManager;
import com.l2client.gui.GameController;
import com.l2client.model.jme.VisibleModel;
import com.l2client.model.l2j.ServerValues;
import com.l2client.model.network.ClientFacade;
import com.l2client.model.network.EntityData;
import com.l2client.model.network.NewCharSummary;
import com.l2client.network.game.ClientPackets.CharacterSelect;
import com.l2client.network.game.ClientPackets.MoveBackwardToLocation;

/**
 * Handler for player character related tasks. Stores the currently selected character, the available characters, handles creation/deletion, etc. of existing chars
 * 
 * 
 *
 */
public class PlayerCharHandler {

	private static Logger log = Logger.getLogger(PlayerCharHandler.class.getName());
	
	/**
	 * currently selected char (the one in use or the one selected in the char select startup screen)
	 */
	private byte selected;
	/**
	 * the object id of the currently selected char, -1 if none is selected
	 */
    private int selectedObjectId = -1;
    /**
     * the array of EntityData of possible chars for selection
     */
    private ArrayList<EntityData> charSelections = new ArrayList<EntityData>();

    /**
     * 
     */
    //TODO move this out to an own action?
    public void onCharSelected(){
    	if(this.selectedObjectId>=0)
    		ClientFacade.get().sendPacket(new CharacterSelect(selected));
    	else
    		if(this.charSelections.size()>0)
    			log.warning("Trying to send a character selection to sever, but no characters selected so far");
    		else
    			log.warning("Trying to send a character selection to sever, but no characters loaded so far");
    }
    
    /**
     * the currently selected character
     * @return byte value with the # of the currently selected char
     */
	public final byte getSelectedIndex() {
		return selected;
	}
	
	/**
	 * the currently selected char
	 * @return EntityData of the currently selected char, or null if none selected
	 */
	public final EntityData getSelectedChar(){
		synchronized(charSelections){//could be omitted as the char amount does not change in between creation and play
			return charSelections.get(getSelectedIndex());
		}
	}
	
	/**
	 * Sets the selected char to be used by the Handler
	 * @param i byte value of the to be selected char ( currently not checked against bounds)
	 */
	public final void setSelected(int i) {
		this.selected = (byte)i;
		
		if(this.charSelections.size()>0)
			selectedObjectId = getSelectedChar().getObjectId();
		else
			log.warning("Tried to select character #"+i+" but 0 loaded");
	}
	
	/**
	 * Adds EntityData to the array of available chars, encapsulated in synchronized block
	 * @param c
	 */
	public void addChar(EntityData c){
		synchronized(charSelections){
		charSelections.add(c);
		}
	}
	/**
	 * Clears the array of EntityData
	 */
	public void clearChars(){
		synchronized(charSelections){
		charSelections.clear();
		selected = 0;
		selectedObjectId =-1;
		
		}
	}
	/**
	 * pass through to the array of EntityData 
	 * @param i the i'th EntityData to return
	 * @return EntityData of the requested char, or null
	 */
    public EntityData getChar(int i){
    	synchronized(charSelections){
    	return charSelections.get(i);
    	}
    }
    /**
     * Creates a copy of the internal EntityData ArrayList 
     * @return Fixed size array of currently available EntityData entries
     */
    public EntityData[] getChars(){
    	synchronized(charSelections){
    	EntityData[] er = charSelections.toArray(new EntityData[charSelections.size()]);
    	return er;
    	}
    }
    
    /**
     * Creates a summary for creation of a new character from an existing EntityData object
     * @param i the char data to be used for the new char
     * @return a filled 
     */
    public NewCharSummary getCharSummary(int i){
    	NewCharSummary sum = new NewCharSummary();
		EntityData c = null;
		synchronized (charSelections) {
			c = charSelections.get(i);
		}
		if (c != null) {
			sum.objectId = c.getObjectId();
			sum.classId = c.getClassId();
			sum.name = c.getName();
			sum.race = c.getRace();
			sum.sex = c.getSex();
			sum.hair = c.getHairStyle();
			sum.hairColor = c.getHairColor();
			
		}
		return sum;
    }
    
    public NewCharSummary getSelectedSummary(){
    	return getCharSummary(selected);
    }
    
//    public EntityData getLastUsedChar(){
//    	EntityData ret = null;
//    	synchronized(charSelections){
//    		for(EntityData p : charSelections)
//    			if(ret == null ||(ret != null && (p.getLastAccess()>ret.getLastAccess())))
//    				ret = p;
//    	}
//    	return ret;
//    }
    
    /**
     * Returns the # of EntityData in the internal array 
     */
    public int getCharCount(){
    	return charSelections.size();
    }
    
	/**
	 * Updates the selected character information with the passed @see CharSelectionInfo
	 * On new char replaces the character, otherwise updates the changed fields
	 * @param info The new CharSelectionInfo for the current character
	 * @return returns true if the existing char was updated, false if char was switched
	 * @throws Exception 
	 */
	public boolean updateUserInfo(EntityData info){
		EntityData selected = getSelectedChar();
		if(selected!=null){

			log.info("Updating Char " + " from " + selected.getX() + ","
					+ selected.getY() + "," + selected.getZ() + " to "
					+ info.getX() + "," + info.getY() + "," + info.getZ());
			selected.updateFrom(info);
			selectedObjectId = selected.getObjectId();
			GameController.getInstance().doEnterWorld();
			return true;				
		} else {
			log.severe("No char selected but received a CharSelectInfopackage");
			return false;
		}
	}
	
	/**
	 * returns the object id of the currently selected char
	 * @return
	 */
	public int getSelectedObjectId() {
		return selectedObjectId;
	}

	//TODO some might only have charIDs filled as they are not instantiated, this should only be during pre onEnterWorld
	public Integer[] getObjectIDs() {
		Integer[] ret = new Integer[charSelections.size()];
		for(int i=0;i<ret.length;i++)
			ret[i] = charSelections.get(i).getObjectId();
		return ret;
	}


	/**
	 * Send a request to move the player to the backend
	 * @param x
	 * @param y
	 * @param z
	 */
	public void requestMoveToAction(float x, float y, float z) {
		// get current pos
		EntityData e = getSelectedChar();
		SimplePositionComponent pos = (SimplePositionComponent) EntityManager.get().getComponent(e.getObjectId(), SimplePositionComponent.class);
		if(pos != null){
		//revert jme uses y as up, l2j uses z as up, so we change y and z here
		ClientFacade.get().sendPacket(
				new MoveBackwardToLocation(x, z, /*put the char a bit above the current height just a fake*/ ServerValues
						.getClientCoord(e.getServerZ() + 8), pos.currentPos.x, pos.currentPos.y, pos.currentPos.z, false));		
//						.getClientCoord(e.getServerZ() + 8), e.getX(), e.getY(), e.getZ(), false));
		log.info("Player "+e.getObjectId()+ " requests to move to:"+x+" "+z+" "+ServerValues
						.getClientCoord(e.getServerZ() + 8)+" from:"+pos.currentPos.x+" "+pos.currentPos.y+" "+pos.currentPos.z);
		} else {
			log.severe("Player "+e.getObjectId()+"is missing SimplePositioningComponent!");
		}
	}
	
	//FIXME this is a copy from NPCHandler move this out to the entity Manager !!
	public Entity createPCComponents(EntityData e, VisibleModel visible) {
		
		
		final Entity ent = EntityManager.get().createEntity(e.getObjectId());
		SimplePositionComponent pos = new SimplePositionComponent();
		L2JComponent l2j = new L2JComponent();
		VisualComponent vis = new VisualComponent();
		EnvironmentComponent env = new EnvironmentComponent();
		
		//done here extra as in update values will be left untouched
		pos.startPos.set(e.getX(), e.getY(), e.getZ());
		pos.currentPos.set(pos.startPos);
		pos.goalPos.set(pos.currentPos);
		pos.walkSpeed = e.getWalkSpeed();
		pos.runSpeed = e.getRunSpeed();
		pos.running = e.isRunning();
		pos.heading = e.getHeading();
		pos.targetHeading = pos.heading;
		
		vis.vis = visible;
		visible.attachVisuals();
		
		l2j.isPlayer  = true;
		l2j.l2jEntity = e;

		ent.setLocalTranslation(pos.currentPos);
		ent.setLocalRotation(new Quaternion().fromAngleAxis(e.getHeading(), Vector3f.UNIT_Y));
		ent.attachChild(visible);
		
		EntityManager.get().addComponent(ent.getId(), env);
		EntityManager.get().addComponent(ent.getId(), l2j);
		EntityManager.get().addComponent(ent.getId(), pos);		
		EntityManager.get().addComponent(ent.getId(), vis);

		
		
		PositioningSystem.get().addComponentForUpdate(pos);
		JmeUpdateSystem.get().addComponentForUpdate(pos);
		AnimationSystem.get().addComponentForUpdate(env);


		
		//hook up of the terrain swapping @see SimpleTerrainManager
		ent.addControl(new AbstractControl(){

			@Override
			public Control cloneForSpatial(Spatial spatial) {
				return null;
			}

			@Override
			protected void controlUpdate(float tpf) {
				//FIXME  move into SimpleTerrainManager
				int x = (int)ent.getLocalTranslation().x/IArea.TERRAIN_SIZE;
				int z = (int)ent.getLocalTranslation().z/IArea.TERRAIN_SIZE;
				SimpleTerrainManager.get().setCenter(x, z);
			}

			@Override
			protected void controlRender(RenderManager rm, ViewPort vp) {
			}}

		);
		
		return ent;
	}
}
