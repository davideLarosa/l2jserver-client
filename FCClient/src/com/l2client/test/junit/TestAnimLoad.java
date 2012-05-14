package com.l2client.test.junit;

import junit.framework.TestCase;

import com.jme3.animation.Animation;
import com.jme3.animation.AnimationProvider;
import com.l2client.model.PartSet;
import com.l2client.util.AnimationManager;
import com.l2client.util.PartSetManager;

public class TestAnimLoad extends TestCase {

	public void testSelf()
    {
    	PartSetManager man = PartSetManager.get();
    	man.loadParts("megaset.csv");
        // create the geometry and attach it
//    	AssetManager assetMan = new DesktopAssetManager(true);
//    	AnimData aData = (AnimData) assetMan.loadAsset("ready_idle.anim.xml");
    	
    	PartSet p = man.getPart("anim");
    	assertNotNull("No animations found in megaset !?!", p);
    	
    	PartSet d = p.getPart("MTW2_Sword");
    	assertNotNull("MTW2_Sword animation not found in megaset !?!", d);
    	AnimationManager.get().precacheAnimations(d);
    	
    	d = p.getPart("MTW2_Knifeman");
    	assertNotNull("MTW2_Knifeman animation not found in megaset !?!", d);
    	AnimationManager.get().precacheAnimations(d);
    	
    	AnimationManager.get().setDefault("MTW2_Sword", "MTW2_Knifeman");
    	
    	AnimationProvider ap = AnimationManager.get().getAnimationProvider("MTW2_Sword");
    	assertNotNull("MTW2_Sword animation package not found",ap);
    	
    	 Animation a = ap.getAnimation("charge_jump_attack", null);
    	 assertNotNull("Animation charge_jump_attack not found",a);
    	 a = ap.getAnimation("ready_idle", null);
    	 assertNotNull("Substitutue animation ready_idle not found",a);
//    	if(aData.anims.size() == 1)
//    		aData.anims.get(0).getName();
    	if(a != null)
    		a.getName();

    }
}
