package com.l2client.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import com.jme3.light.AmbientLight;
import com.jme3.light.Light;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.system.AppSettings;
import com.l2client.app.Singleton;
import com.l2client.dao.UserPropertiesDAO;
import com.l2client.gui.dialogs.CharCreateJPanel;
import com.l2client.gui.dialogs.ChatPanel;
import com.l2client.gui.dialogs.GameServerJPanel;
import com.l2client.gui.dialogs.TransparentLoginPanel;
import com.l2client.model.jme.NewCharacterModel;
import com.l2client.model.network.ClientFacade;
import com.l2client.model.network.GameServerInfo;
import com.l2client.network.game.ClientPackets.CharacterCreate;
import com.l2client.network.login.LoginHandler;


/**
 * game controller for switching game states;
 * start screen
 * login
 * char selection/creation
 * server selection
 * in game
 */
//FIXME check if jme gamestates could replace this
public final class GameController {

	private static final String SCENES_CREATE = "scenes/create/create.j3o";

	private static final Logger logger = Logger.getLogger(GameController.class
            .getName());
	
	private final static GameController instance = new GameController();
	
//	private boolean finished = false;
	
	private boolean worldEntered = false;
	
	private final NewCharacterModel charSummary = new NewCharacterModel(null);

	private ClientFacade clientInfo;

	private LoginHandler loginHandler;

//	private SceneRoot sceneRoot;

	private Camera camera;

	private AppSettings settings;

	/**
	 * viewport needed for post process filter integration
	 */
//	private ViewPort viewPort;
	
	private GameController(){	
	}
	
	public static GameController get(){
		return instance;
	}	
	
	public void initialize(Camera cam, AppSettings settings /*, ViewPort viewPort*/){
		//TODO check backdrop is removed properly
//		sceneRoot = Singleton.get().getSceneManager().getRoot();
		Singleton.get().getSceneManager().removeAll();
		camera = cam;
//		this.viewPort = viewPort;
		this.settings = settings;
	}
	
	public void doEnterWorld(){
		if(worldEntered)
			return;
		
		worldEntered = true;
//		if(sceneRoot==null)
//			return;
//		//reset scene
//		sceneRoot.cleanupScene();
		Singleton.get().getSceneManager().removeAll();
//		TextureManager.doTextureCleanup();
		//reset GUI
		Singleton.get().getGuiController().removeAll();
		System.gc();
		//setup camera to be centered around player (char selecetd, or ingame object package?)
		//hook up game input controller
		Singleton.get().getCharController().onEnterWorld(clientInfo.getCharHandler(), camera);
//		//setup in game GUI
		setupGameGUI();
//		//startup of asset loading for area around char
//		sceneRoot.updateModelBound();
//		sceneRoot.updateGeometricState();
		FilterPostProcessor fpp = new FilterPostProcessor(Singleton.get().getAssetManager().getJmeAssetMan());
		SSAOFilter ssaoFilter = new SSAOFilter(12.940201f, 43.928635f,
				0.32999992f, 0.6059958f);
		fpp.addFilter(ssaoFilter);
		logger.severe("Adding SSAO");
		Singleton.get().getSceneManager().changePostProcessor(fpp, 0);
	}
	
	private void setupGameGUI() {
		// Actions GUI (start loading somewhat earlier, but only here as in onEnterWorld
		//              the inGame inputHandler is created)
//		ActionManager.getInstance().loadActions();
		//Chat GUI
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				
				Singleton.get().getGuiController().displayAdminTelePanel();
				
				final ChatPanel pan = Singleton.get().getGuiController().displayChatJPanel();
				clientInfo.getChatHandler().setChatPanel(pan);
				pan.addChatListener(new KeyListener() {
					
					@Override
					public void keyTyped(KeyEvent e) {
					}
					
					@Override
					public void keyReleased(KeyEvent e) {			
					}
					
					@Override
					public void keyPressed(KeyEvent e) {
						if(KeyEvent.VK_ENTER == e.getKeyCode()){
							clientInfo.getChatHandler().sendMessage(pan.getChatMessage());
						}
					}
				});
			}
		});
		
//		Removed here done in ShortCutInit packet
//		// Actions GUI
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				try {
//					//FIXME move this out, actions should register themselves 
//					int runs = 20;//max 20 sec
//					while(!ActionManager.isLoaded()){
//						Thread.sleep(1000);
//						runs--;
//						logger.finest("waiting for ActionManager to complete for "+runs);
//						if(runs<=0)
//							return;
//					}
//					SwingUtilities.invokeLater(new Runnable() {
//
//						@Override
//						public void run() {
//							Singleton.get().getGuiController().displayShortCutPanel();
//							Singleton.get().getGuiController().displaySkillAndActionsPanel();
//						}
//					});
//				} catch (Exception e) {
//					logger.log(Level.SEVERE, "Failed in action GUIs creation",e);
//				}
//			}
//		}).start();
		// System GUI
	}

	/**
	 * Initialize the character selection based on the characters stored in the 
	 * {@link ClientFacade} CharSelectHandler
	 */
	public void doCharSelection(){
//		if(sceneRoot==null)
//			return;
//		//reset scene
//		sceneRoot.cleanupScene();
		Singleton.get().getSceneManager().removeAll();

		FilterPostProcessor fpp = new FilterPostProcessor(Singleton.get().getAssetManager().getJmeAssetMan());
		SSAOFilter ssaoFilter = new SSAOFilter(12.940201f, 43.928635f,
				0.32999992f, 0.6059958f);
		fpp.addFilter(ssaoFilter);
		Singleton.get().getSceneManager().changePostProcessor(fpp, 0);

		//display available chars + gui for creation of new one
		//if none present go directly for creation of new char
        if (clientInfo.getCharHandler().getCharCount() > 0) {
        	doCharPresentation();
		} else {
			doCharCreation();
		}
	}
	
	public void doCharCreation() {
		
		Singleton.get().getSceneManager().removeAll();
		
		try{
			Spatial n = Singleton.get().getAssetManager().getJmeAssetMan()
					.loadModel(SCENES_CREATE);
			Singleton.get().getSceneManager().changeTerrainNode(n, 0);
			
			//this is needed as the ssao pass will other wise only render 
			//the shadow of our attached chars as they are on a different 
			//root (nice for a ghost effect or so)
			for(Light l : n.getLocalLightList()){
				if(l instanceof AmbientLight){
					n.removeLight(l);
					l.setColor(new ColorRGBA(0.6f,0.6f,0.8f,1.0f));
					Singleton.get().getSceneManager().changeRootLight(l, 0);
				}
			}
				
		} catch (Exception e1) {
			logger.log(Level.SEVERE, "Failed to load creation scene file "+SCENES_CREATE, e1);
		}
		
		camera.setLocation(new Vector3f(2.1353703f, 0.10786462f, 14.364603f));
		camera.lookAtDirection(new Vector3f(-0.1764535f, 0.27474004f, -0.94518876f), Vector3f.UNIT_Y);
		
		//FIXME move to own class
		//for the first just display the menu for char selection which steers the display
		//Name, Sex, Race, Class, (HairStyle, HairColor, Face) 
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				final CharCreateJPanel pan = Singleton.get().getGuiController()
						.displayCharCreateJPanel();
				
				// action that gets executed in the update thread:
				pan.addCreateActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
								clientInfo.sendGamePacket(new CharacterCreate(pan.getNewCharSummary()));
								//dialog will stay open, will be closed on
								//create ok package or cancel				
							}
						});
				pan.addCancelActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// this gets executed in jme thread
						// do 3d system calls in jme thread only!
						doCharPresentation();
					}
				});
				pan.addModelchangedListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// this gets executed in jme thread
						// do 3d system calls in jme thread only!

						//FIXME check first what changed, only the label, the race (basemodel), the hair/face (components)

						Singleton.get().getSceneManager().removeChar();
						
						//FIXME reevaluate model composition
						charSummary.setNewCharSummary(pan.getNewCharSummary());
						charSummary.attachVisuals();
						charSummary.setLocalTranslation(.126f, -0.1224f, 7.76f);
						Singleton.get().getSceneManager().changeCharNode(charSummary,0);
						//FIXME end of move this out
					}
				});
				pan.afterDisplayInit();
			}
		});
		//NICE TO HAVE:
		//display x characters for x races
		//change input handler to only allow left,right, escape and enter
		//add input handler for clicking on one of the chars for selection
		//add functionality to zoom around or fade away not used chars
		//display a choose current char window on down
		//if chosen display the char customization window and an accept/cancel
		
		//exit to charPresentation on accept of a char or a cancel (if charCount > 0)
		//purge root on exit
		
	}

	/**
	 * comparable with the display of the characters in the lobby a player has for entering the world
	 */
	private void doCharPresentation() {	
//		{
//			// FIXME choose char and not select first, remove in product code
//			clientInfo.getCharHandler().setSelected(0);
//			clientInfo.getCharHandler().onCharSelected();
//			doEnterWorld();
//			if(true)return;
//		}
		
		//purge root
		Singleton.get().getSceneManager().removeAll();
		
		//TODO load the hall
		//load the x representations of the characters into the hall
		//add input handler for choosing a char and functionality to let him step to the front
		//add gui buttons for enter world, exit, options
		//on enter world start game with the chosen, on exit cleanup, on options show options pane

		AmbientLight al = new AmbientLight();
	    al.setColor(new ColorRGBA(.8f, .8f, .8f, 1.0f));
		Singleton.get().getSceneManager().changeRootLight(al,0);

		//setup camera	
		camera.setLocation(new Vector3f(3f,0f,4f));
		camera.lookAtDirection(Vector3f.UNIT_Z.mult(-1f), Vector3f.UNIT_Y);
		
		for (int i = clientInfo.getCharHandler().getCharCount()-1; i >= 0; i--) {
			NewCharacterModel v = new NewCharacterModel(clientInfo.getCharHandler().getCharSummary(i));
			v.attachVisuals();
			v.setLocalTranslation(i*1.25f, -1f, -4f);
			Singleton.get().getSceneManager().changeCharNode(v,0);
		}
    	
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				final JButton b = Singleton.get().getGuiController().displayButton("select", 80, 30, (settings.getWidth()/2)-140, settings.getHeight()-50);
				final JButton bb = Singleton.get().getGuiController().displayButton("create", 80, 30, (settings.getWidth()/2)+60, settings.getHeight()-50);
				
				b.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						// FIXME choose char and not select first, remove
						clientInfo.getCharHandler().setSelected(0);
						clientInfo.getCharHandler().onCharSelected();
						//cleanup of the buttons
						Singleton.get().getGuiController().removeButton(new JButton[]{b,bb});
					}
				});
				
				bb.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						doCharCreation();
					}
				});
				bb.setMultiClickThreshhold(1000L);
			}
		});
	}

	public void doLogin(){
//		if(sceneRoot==null)
//			return;
//		
//		//TODO test settings for fast dev test
//		initNetwork("ghoust", new char[]{'g','h','o','u','s','t'}, "127.0.0.1:2106");
//		if(true)return;
		
	    camera.setLocation(new Vector3f(0,0,0));  
		
		Singleton.get().getSceneManager().removeAll();
	    Quad b = new Quad(80f,60f);
	    b.updateBound();
	    Geometry geom = new Geometry("backdrop", b);
	    Material mat = new Material(Singleton.get().getAssetManager().getJmeAssetMan(), "Common/MatDefs/Misc/Unshaded.j3md");
	    mat.setTexture("ColorMap", Singleton.get().getAssetManager().getJmeAssetMan().loadTexture("start/backdrop.png"));
	    geom.setMaterial(mat);
	    geom.setLocalTranslation(-40f, -30f, -90f);	    
	    Singleton.get().getSceneManager().changeTerrainNode(geom,0);
	    
	    Quad b2 = new Quad(38f,29f);
	    b2.updateBound();
	    Geometry geom2 = new Geometry("wolf", b2);
	    Material mat2 = new Material(Singleton.get().getAssetManager().getJmeAssetMan(), "Common/MatDefs/Misc/Unshaded.j3md");
	    mat2.setTexture("ColorMap", Singleton.get().getAssetManager().getJmeAssetMan().loadTexture("start/wolf.png"));
	    mat2.getAdditionalRenderState().setBlendMode(BlendMode.Alpha); // activate transparency
	    geom2.setMaterial(mat2);
	    geom2.setQueueBucket(Bucket.Transparent);
	    geom2.setLocalTranslation(-39f, -15f, -90f);	
	    Singleton.get().getSceneManager().changeTerrainNode(geom2,0);
	    
		AmbientLight al = new AmbientLight();
	    al.setColor(new ColorRGBA(.8f, .8f, .8f, 1.0f));
		Singleton.get().getSceneManager().changeRootLight(al,0);
		//#############################################################

		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				final TransparentLoginPanel pan = Singleton.get().getGuiController()
						.displayUserPasswordJPanel();
				// get properties initialized from file or by defaut 
				pan.setServer(System.getProperty(UserPropertiesDAO.SERVER_HOST_PROPERTY)+":"+System.getProperty(UserPropertiesDAO.SERVER_PORT_PROPERTY));
				// action that gets executed in the update thread:
				pan.addLoginActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
								// this gets executed in jme thread
								// do 3d system calls in jme thread only!
//								SoundController.getInstance().playOnetime("sound/click.ogg", false, Vector3f.ZERO);

						String[] split = pan.getServer().split(":");

						if(split.length<2) {
							Singleton.get().getGuiController().showErrorDialog("Check your server:port entry");
							return;
						}
						try {
							//store setting in user property for later usage
							System.setProperty(UserPropertiesDAO.SERVER_HOST_PROPERTY,split[0]);
							System.setProperty(UserPropertiesDAO.SERVER_PORT_PROPERTY,split[1]);
							//intentionally not used
							Integer.parseInt(split[1]);
						} catch (NumberFormatException ex) {
							Singleton.get().getGuiController().showErrorDialog("Your port is not a number entry");
							return;
						}
								if ( !initNetwork(pan.getUsername(), pan.getPassword(), pan.getServer()) ) {

									doLogin();
									Singleton.get().getGuiController()
											.showErrorDialog(
													"Failed to Connect to login server");

								} else {
									//save port and host to user.home on a successfull login
									UserPropertiesDAO.saveProperties();
								}
							}
						});
				pan.addCancelActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						// this gets executed in jme thread
						// do 3d system calls in jme thread only!
//						finished = true;
////						SoundController.getInstance().playOnetime("sound/click.ogg", false, Vector3f.ZERO);
//						try {
//							Thread.sleep(1500);
//						} catch (InterruptedException ex) {
//						}
					}
				});
			}
		});
	}
	
	public boolean initNetwork(String user, char [] pwd, String hostport){

		String[] split = hostport.split(":");
		//verified by GUI already
		Integer port = Integer.parseInt(split[1]);
		
		this.clientInfo = Singleton.get().getClientFacade();
		
		clientInfo.init(user);
		
		//try connection to login server
        this.loginHandler = new LoginHandler(port, split[0]){
            @Override
            public void onDisconnect(boolean todoOk,String host, int port){
                if(todoOk){
                 	clientInfo.connectToGameServer(host, port, loginOK1, loginOK2, playOK1, playOK2);
                }
            }
            @Override
            public void onServerListReceived(GameServerInfo[] servers){
            	
////            	FIXME for testcase use first one, remove later
//            	requestServerLogin(0);
//            	if(true) return;
            	
            	
            	//game server selection
            	if(servers != null && servers.length >0){
            		final GameServerJPanel p = Singleton.get().getGuiController().displayServerSelectionJPanel(servers);
            		p.addCancelActionListener(new ActionListener(){
    					@Override
    					public void actionPerformed(ActionEvent e) {
    						// this gets executed in jme thread
    						// do 3d system calls in jme thread only!
    						doDisconnect(false, "", -1);
    						//FIXME this is just for the testcase
    						doLogin();
    					}
    				});
            		p.addSelectActionListener(new ActionListener(){
    					@Override
    					public void actionPerformed(ActionEvent e) {
    						// this gets executed in jme thread
    						// do 3d system calls in jme thread only!
    						requestServerLogin(p.getSelectedServer());
    					}
    				});
            	}
            	else {
            		Singleton.get().getGuiController().showErrorDialog("Failed to Connect to login server");
            		logger.severe("Loginserver returned no gameservers to login to");
            		doDisconnect(false, "", -1);
            	}
            }
        };
        if(!loginHandler.connected)
        	return false;
        
        loginHandler.setLoginInfo(user,pwd);
        return true;
	}
//
//	public final boolean isFinished() {
//		return finished;
//	}

//	/**
//	 * Top root of complete scene, including, statics, dynamics, player, etc.
//	 * @return
//	 */
//	public SceneRoot getSceneRoot() {
//		if(sceneRoot != null)
//			return sceneRoot;
//		else {
//			//FIXME better use dummy gamecontroller triggered by injection
//			logger.warning("SceneRoot requested but none set so far, please initialize first, creating DUMMY root");
//			sceneRoot = new SceneRoot("Sceneroot");
//			return sceneRoot;
//		}
//	}

	public Camera getCamera() {
		return camera;
	}
	
	public void finish(){
//		finished = true;
		if(clientInfo!= null){
			clientInfo.cleanup();
			logger.info("Released Network Connections");
		}
	}
}
