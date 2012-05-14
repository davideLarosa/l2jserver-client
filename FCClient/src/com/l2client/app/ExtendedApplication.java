package com.l2client.app;

import com.jme3.app.SimpleApplication;
import com.l2client.asset.AssetManager;

/**
 * Basic Application frame with early initialization of the l2client asset manager
 *
 */
public class ExtendedApplication extends SimpleApplication {

	public ExtendedApplication(){
		this.assetManager = AssetManager.getInstance().getJmeAssetMan();
	}

	@Override
	public void simpleInitApp() {
		// TODO Auto-generated method stub
		
	}
}
