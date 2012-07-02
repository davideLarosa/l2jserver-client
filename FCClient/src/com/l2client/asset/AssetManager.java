package com.l2client.asset;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import com.jme3.asset.DesktopAssetManager;

//FIXME refactor into callable and future
public class AssetManager {

	private static final Logger logger = Logger.getLogger(AssetManager.class
			.getName());

	private com.jme3.asset.AssetManager jmeAssetMan;

	private static AssetManager instance = null;

	private ExecutorService highPrioExec =  Executors.newFixedThreadPool(2);//Executors.newSingleThreadExecutor();
	private ExecutorService lowPrioExec =  Executors.newSingleThreadExecutor();

	private AssetManager() {
		try {
			jmeAssetMan = new DesktopAssetManager(Thread.currentThread().getContextClassLoader().getResource("com/l2client/asset/loader.cfg"));
		} catch (Exception e) {
			logger.severe("Failed to load com/l2client/asset/loader.cfg, using default assetmanager");
			jmeAssetMan = new DesktopAssetManager(true);
		}
		//FIXME we have to do this somehow better.. ev. in read megaset?
		jmeAssetMan.registerLocator("textures/entity/", "com.jme3.asset.plugins.ClasspathLocator");
	}

	public static AssetManager getInstance() {
		if (AssetManager.instance != null)
			return AssetManager.instance;
		else {
			AssetManager.instance = new AssetManager();
			return AssetManager.instance;
		}
	}

	public Asset loadAsset(String location, String name, boolean immediate){
		Asset a = new Asset(location, name);
		loadAsset(a, immediate);
		return a;
	}
	public void loadAsset(final Asset a, boolean immediate) {
		logger.fine("requested " + a.getLocation());
		Callable<Object> call = new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				Object n = null;

				try {
					n = jmeAssetMan.loadAsset(a.getLocation());
				} catch(IllegalStateException ex){
					ex.printStackTrace();
				}catch (Exception e) {
				}
				if (n != null) {
					a.setBaseAsset(n);
					logger.fine("requested " + a.getLocation() + " was loaded");
					a.afterLoad();
				} else
					logger.warning("requested " + a.getLocation()
							+ " was not found");

				return n;
			}

		};
		Future<Object> ret;
		if(immediate)
			ret = highPrioExec.submit(call);
		else
			ret = lowPrioExec.submit(call);
		
		a.setFuture(ret);

	}
	
	public com.jme3.asset.AssetManager getJmeAssetMan(){
		return jmeAssetMan;
	}
	
	public void shutdown(){
		highPrioExec.shutdown();
		lowPrioExec.shutdown();
	}
}
