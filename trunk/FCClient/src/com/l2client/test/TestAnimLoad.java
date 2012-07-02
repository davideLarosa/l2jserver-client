package com.l2client.test;

import com.jme3.asset.AssetManager;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.scene.plugins.ogre.AnimData;

public class TestAnimLoad
{
    public static void main(String[] args)
    {
        // create the geometry and attach it
    	AssetManager assetMan = new DesktopAssetManager(true);
    	AnimData aData = (AnimData) assetMan.loadAsset("ready_idle.anim.xml");
    	if(aData.anims.size() == 1)
    		aData.anims.get(0).getName();

    }
}
