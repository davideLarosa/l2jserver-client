package com.l2client.model.jme;

import java.util.HashMap;
import java.util.logging.Level;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.l2client.animsystem.InputProvider;
import com.l2client.animsystem.jme.JMEAnimationController;
import com.l2client.app.Assembler;
import com.l2client.app.Assembler2;
import com.l2client.app.Singleton;
import com.l2client.asset.Asset;
import com.l2client.model.PartSet;
import com.l2client.model.WeaponSet;
import com.l2client.model.l2j.Race;
import com.l2client.model.network.NewCharSummary;

/**
 * Representation of a 3d model based on l2j player information
 */
public class NewCharacterModel extends VisibleModel {
	
	protected NewCharSummary lastCharSummary = null;

	private Assembler assembler = null;
	
	private String hair;

	private PartSet top;

	public NewCharacterModel(NewCharSummary sel) {
		super(sel);
		if(lastCharSummary == null)
			lastCharSummary = sel;
	}
	
	public void setNewCharSummary(NewCharSummary sel){
		if(charSelection != null)
			lastCharSummary = charSelection;
		
		charSelection = sel;
		createVisuals();
	}
	
	/**
	 * Builds the visual representation of the model by loading it currently.
	 * The anim controller seems to be missing from the jme stored version, so currently 
	 * we load the model from the plain ogre xml definition (which is a bit slower)
	 * 
	 * 
	 * @return the loaded model on a @see Node
	 */
	protected Node createVisuals() {
		Singleton.get().getSceneManager().changeAnyNode(this,vis, 1);
		if(assembler != null)
			createChangedChar();
		else
			createInitialChar();
		if (vis != null){	
			JMEAnimationController animControl = (JMEAnimationController) vis
					.getControl(JMEAnimationController.class);
			if(animControl != null)
				animControl.setInput(InputProvider.NOINPUT);

			Singleton.get().getSceneManager().changeAnyNode(this,vis, 0);
		} else
			logger.severe("Vis animations are missing");
		
		return vis;
	}
	
	/**
	 * Decides on which model should be used
	 * @param sum	Summary describing the new character
	 * @return The name of the model to be used for that character
	 */
	private String raceToEntity(NewCharSummary sum){
//		//FIXME check assets are present & working
//		return "pelfmmage";
		StringBuilder ret = new StringBuilder();
		Race[] races = Race.values();
		Race race = races[sum.race];
		switch(race){
		case Dwarf:{ ret.append("DwarfWarriorM");break;}
		case DarkElf:
		case Elf:{
			{
				switch(sum.classId){
				case 0x0a:
				case 0x19:
				case 0x26:
				case 0x31:
				case 0x7c:ret.append("ElfWizard");break;
				default:ret.append("ElfWarrior");
				}
				if(sum.sex != 0)
					ret.append("F");
				else
					ret.append("M");
				}
			break;
		}
		case Human:
		case Kamael:{ ret.append("HumanWarriorM");break;}
		case Orc:{ ret.append("Orc");break;}
		}
		return ret.toString();
	}
	
	private void createInitialChar(){
		if (charSelection != null) {
				String template = raceToEntity(charSelection);
				top = Assembler2.getTopPart(template);
				if(top == null){
					template = "DwarfWarriorM";
					top = Assembler2.getTopPart(template);
					if(top == null)
						return;
				}
				
				assembler  = new Assembler();

				try {
					//Skel
					assembler.setSkeleton(Assembler2.getSkeleton(top));
					assembler.setUseOptimization(true);
					assembler.setUseHWSkinning(true);
					//Materials
					HashMap<String, Asset> mats = Assembler2.getMaterials(top);
//					for(Asset a : mats.values()){
//						Material m = (Material) a.getBaseAsset();
//						assembler.addMaterial(a.getName(), m);
//					}
					
					//Meshes start
					HashMap<String, Geometry> geoms = assembler.getMeshes();
					HashMap<Asset, String> me = Assembler2.getMeshesWithMaterial(top);
					//weapons + anim defintion
					WeaponSet w = Assembler2.getWeaponset(top);
					Assembler2.addWeaponSetToMeshMap(top, w, me);
					//meshes end
					Assembler2.unpackMeshesIntoAssembler(me, mats, assembler);
					for(Asset a : me.keySet()){
						if(a.getName().startsWith("hair"))
							hair = a.getName();
					}
					Assembler2.getAnimations(/*top, */w.getAnimSet(), w.getDefaultSet());
					assembler.setAnimParts(w.getAnimSet());
					
					
				} catch (Exception ex) {
					logger.log(Level.SEVERE, null, ex);
				}
			
				replaceModel();
		}
	}
	
	private void replaceModel() {
		Node n = assembler.getModel();
		// the models are rotated so z is up
		n.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
//		// FIXME modelconverter should already have set this one, this is
//		// not the case -> NPE
//		n.setModelBound(new BoundingBox());
//		n.updateModelBound();
//		if (vis != null && vis.getParent() != null) {
//			Singleton.get().getSceneManager().changeAnyNode(this,vis, 1);
//			vis.removeFromParent();
//		}
		vis = n;
	}

	private void createChangedChar(){
//		Singleton.get().getSceneManager().changeAnyNode(this,vis, 1);
		//significant chage?
		if(lastCharSummary.classId != charSelection.classId || 
				lastCharSummary.race != charSelection.race ||
				lastCharSummary.sex != charSelection.sex){
			createInitialChar();
		} 
		//hair change ??
		if(lastCharSummary.hair != charSelection.hair && top != null){
			
			Asset a = Assembler2.getHair(top, -1);
			if(a != null){
				assembler.getMeshes().remove(hair);
				Node me = (Node) (a.getBaseAsset());
				hair = me.getName();
				assembler.getMeshes().put(hair, (Geometry)me.getChild(0));
				replaceModel();				
			}
		}
//		Singleton.get().getSceneManager().changeAnyNode(this,vis, 0);
		//TODO hair color change ??
	}

}
