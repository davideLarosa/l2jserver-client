package com.l2client.app;

import com.jme3.app.SimpleApplication;
import com.l2client.asset.AssetManager;

/**
 * Basic Application frame with early initialization of the l2client asset manager
 *
 */
public class ExtendedApplication extends SimpleApplication {

	public ExtendedApplication(){
		showSettings = false;
		this.assetManager = AssetManager.get().getJmeAssetMan();
		//jme3 only displaying the jme settings screen eats heap
		System.gc();
	}

	@Override
	public void simpleInitApp() {
	}
}
