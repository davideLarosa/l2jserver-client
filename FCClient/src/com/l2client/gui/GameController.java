package com.l2client.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.system.AppSettings;
import com.l2client.asset.AssetManager;
import com.l2client.controller.SceneManager;
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

	private static final Logger logger = Logger.getLogger(GameController.class
            .getName());
	
	private final static GameController instance = new GameController();
	
	private boolean finished = false;
	
	private final NewCharacterModel charSummary = new NewCharacterModel(null);

	private ClientFacade clientInfo;

	private LoginHandler loginHandler;

//	private SceneRoot sceneRoot;

	private Camera camera;

	private AppSettings settings;
	
	private GameController(){	
	}
	
	public static GameController getInstance(){
		return instance;
	}	
	
	public void initialize(Camera cam, AppSettings settings){
		//TODO check backdrop is removed properly
//		sceneRoot = SceneManager.get().getRoot();
		SceneManager.get().removeAll();
		camera = cam;
		this.settings = settings;
		
	    Quad b = new Quad(80f,60f);
	    b.updateBound();
	    Geometry geom = new Geometry("backdrop", b);
	    Material mat = new Material(AssetManager.getInstance().getJmeAssetMan(), "Common/MatDefs/Misc/Unshaded.j3md");
	    mat.setTexture("ColorMap", AssetManager.getInstance().getJmeAssetMan().loadTexture("start/backdrop.png"));
	    geom.setMaterial(mat);
	    geom.setLocalTranslation(-40f, -30f, -90f);
	    cam.setLocation(new Vector3f(0,0,0));  
	    
	    SceneManager.get().changeTerrainNode(geom,0);
	}
	
	public void doEnterWorld(){
//		if(sceneRoot==null)
//			return;
//		//reset scene
//		sceneRoot.cleanupScene();
		SceneManager.get().removeAll();
//		TextureManager.doTextureCleanup();
		//reset GUI
		GuiController.getInstance().removeAll();
		System.gc();
		//setup camera to be centered around player (char selecetd, or ingame object package?)
		//hook up game input controller
		CharacterController.getInstance().onEnterWorld(clientInfo.getCharHandler(), camera);
//		//setup in game GUI
		setupGameGUI();
//		//startup of asset loading for area around char
//		sceneRoot.updateModelBound();
//		sceneRoot.updateGeometricState();
	}
	
	private void setupGameGUI() {
		// Actions GUI (start loading somewhat earlier, but only here as in onEnterWorld
		//              the inGame inputHandler is created)
		ActionManager.getInstance().loadActions();
		//Chat GUI
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				final ChatPanel pan = GuiController.getInstance().displayChatJPanel();
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
		
		// Actions GUI
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					//FIXME move this out, actions should register themselves 
					int runs = 20;//max 20 sec
					while(!ActionManager.isLoaded()){
						Thread.sleep(1000);
						runs--;
						logger.finest("waiting for ActionManager to complete for "+runs);
						if(runs<=0)
							return;
					}
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							GuiController.getInstance().displayShortCutPanel();
							GuiController.getInstance().displaySkillAndActionsPanel();
						}
					});
				} catch (Exception e) {
					logger.log(Level.SEVERE, "Failed in action GUIs creation",e);
				}
			}
		}).start();
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
		SceneManager.get().removeAll();
		//display available chars + gui for creation of new one
		//if none present go directly for creation of new char
        if (clientInfo.getCharHandler().getCharCount() > 0) {
        	doCharPresentation();
		} else {
			doCharCreation();
		}
	}
	
	public void doCharCreation() {
//		//FIXME change quickfix to a real solution for headless tests
//		if(sceneRoot==null)
//			return;
//		//purge rootNode
//		this.sceneRoot.cleanupScene();
		
		SceneManager.get().removeAll();
		
		PointLight pl = new PointLight();
        pl.setColor(ColorRGBA.White);
        pl.setRadius(4f);
        pl.setPosition(new Vector3f(10,-50,20));
		
        SceneManager.get().changeRootLight(pl,0);
		
		//FIXME setup camera
		
		//FIXME move to own class
		//for the first just display the menu for char selection which steers the display
		//Name, Sex, Race, Class, (HairStyle, HairColor, Face) 
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				final CharCreateJPanel pan = GuiController.getInstance()
						.displayCharCreateJPanel();
				
				// action that gets executed in the update thread:
				pan.addCreateActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
								clientInfo.sendPacket(new CharacterCreate(pan.getNewCharSummary()));
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

						SceneManager.get().removeChar();
						
						//FIXME reevaluate model composition
						charSummary.setNewCharSummary(pan.getNewCharSummary());
						charSummary.attachVisuals();
						charSummary.setLocalTranslation(0f, -1f, -4f);
						SceneManager.get().changeCharNode(charSummary,0);
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
//		if(sceneRoot==null)
//			return;
//		
		
//		{
//			// FIXME choose char and not select first, remove in product code
//			clientInfo.getCharHandler().setSelected(0);
//			clientInfo.getCharHandler().onCharSelected();
//			doEnterWorld();
//			if(true)return;
//		}
		
		//purge root
//		sceneRoot.cleanupScene();
		SceneManager.get().removeAll();
		
		//TODO load the hall
		//load the x representations of the characters into the hall
		//add input handler for choosing a char and functionality to let him step to the front
		//add gui buttons for enter world, exit, options
		//on enter world start game with the chosen, on exit cleanup, on options show options pane

		// getPj().charSelectHandler.showDialog();
		
		//FIXME setup camera
		
		for (int i = clientInfo.getCharHandler().getCharCount()-1; i >= 0; i--) {
			NewCharacterModel v = new NewCharacterModel(clientInfo.getCharHandler().getCharSummary(i));
			v.attachVisuals();
//			v.setLocalTranslation(16f + i * 1f, -16f + (-1.0f
//					* (float) Math.sin(0.1 * i)), -16.5f);
			v.setLocalTranslation(i*1.25f, -1f, -4f);
//			v.setLocalScale(0.3f);
//			v.updateModelBound();
//			sceneRoot.getWalkerRoot().attachChild(v);
			SceneManager.get().changeCharNode(v,0);
//	        /** this blue box is our player character */
//	        Box b = new Box(Vector3f.ZERO, 1, 1, 2);
//	        Geometry g= new Geometry("blue cube", b);
//	        Material mat = new Material(AssetManager.getInstance().getJmeAssetMan(),
//	          "Common/MatDefs/Misc/Unshaded.j3md");
//	        mat.setColor("Color", ColorRGBA.Blue);
//	        g.setMaterial(mat);
//	        g.setLocalTranslation(i*2f, 0f, -20f);
//	        sceneRoot.getWalkerRoot().attachChild(g);
//	        /** this blue box is our player character */
//	        Box b2 = new Box(Vector3f.ZERO, 1, 2, 1);
//	        Geometry g2= new Geometry("blue cube", b2);
//	        Material mat2 = new Material(AssetManager.getInstance().getJmeAssetMan(),
//	          "Common/MatDefs/Light/Lighting.j3md");
//	        mat2.setFloat("Shininess", 1f);
//	        mat2.setBoolean("UseMaterialColors", true);
//	        mat2.setColor("Ambient", ColorRGBA.Black);
//	        mat2.setColor("Diffuse", ColorRGBA.Red);
//	        mat2.setColor("Specular", ColorRGBA.White.mult(0.6f));
//	        g2.setMaterial(mat2);
//	        g2.setLocalTranslation(i*2f, 0f, -20f);
//	        sceneRoot.getWalkerRoot().attachChild(g2);
		}
		
 
        DirectionalLight light = new DirectionalLight();
        light.setDirection(new Vector3f(0, -1, 0));
        light.setColor(ColorRGBA.White.mult(1.5f));
//        sceneRoot.addLight(light);
        
        AmbientLight am = new AmbientLight();
        am.setColor(ColorRGBA.White);
//        sceneRoot.addLight(am);
        SceneManager.get().changeRootLight(light,0);
        SceneManager.get().changeRootLight(am,0);

//		sceneRoot.removeAllLights();
//        
//        PointLight pl = new PointLight();
//        pl.setColor(new ColorRGBA(0.5f,0.5f,0.5f,1));
//        pl.setPosition(new Vector3f(0,-5,0));
//        sceneRoot.addLight(pl);
        
//		sceneRoot.updateGeometricState();
    	
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				// FIXME buttons + actions
				final JButton b = GuiController.getInstance().displayButton("select");
				b.setSize( b.getPreferredSize() );
				b.setLocation((settings.getWidth()/2)-200, settings.getHeight()-50);
				
				final JButton bb = GuiController.getInstance().displayButton("create");
				bb.setSize( b.getPreferredSize() );
				bb.setLocation((settings.getWidth()/2)-200+b.getWidth()+20, settings.getHeight()-50);
				
				b.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						// FIXME choose char and not select first, remove
						clientInfo.getCharHandler().setSelected(0);
						clientInfo.getCharHandler().onCharSelected();
						//moved to UserInfo.handlePacket() because at the moment the EntityData.id is 0 (will be provided with UserInfo)
//						doEnterWorld();
						//cleanup of the buttons
						GuiController.getInstance().removeButton(new JButton[]{b,bb});
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
//		initNetwork("ghoust", new char[]{'g','h','o','u','s','t'}, "localhost:2106");
//		if(true)return;
		
		
		
		//FIXME more natural way load and store in a webstartable way
		//############################################################
		//load server properties, ev. from user.home ?
		Properties servers = new Properties();

        FileInputStream in = null;
		try {
			in = new FileInputStream("cServer.properties");
			servers.load(in);
			System.getProperties().putAll(servers);
		} catch(Exception e) {//Ignore
		} 
		
		//get startup server
		final String host = System.getProperty("client.server.host","127.0.0.1");
		final String port = System.getProperty("client.server.port","2106");

		final FileInputStream stream = in;
		//#############################################################

		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				final TransparentLoginPanel pan = GuiController.getInstance()
						.displayUserPasswordJPanel();
				pan.setServer(host+":"+port);
				// action that gets executed in the update thread:
				pan.addLoginActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
								// this gets executed in jme thread
								// do 3d system calls in jme thread only!
//								SoundController.getInstance().playOnetime("sound/click.ogg", false, Vector3f.ZERO);

						String[] split = pan.getServer().split(":");

						if(split.length<2) {
							GuiController.getInstance().showErrorDialog("Check your server:port entry");
							return;
						}
						try {
							Integer port = Integer.parseInt(split[1]);
						} catch (NumberFormatException ex) {
							GuiController.getInstance().showErrorDialog("Your port is not a number entry");
							return;
						}
								if ( !initNetwork(pan.getUsername(), pan.getPassword(), pan.getServer()) ) {

									doLogin();
									GuiController
											.getInstance()
											.showErrorDialog(
													"Failed to Connect to login server");

								}
								//else save port and host to user.home ?
							}
						});
				pan.addCancelActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						// this gets executed in jme thread
						// do 3d system calls in jme thread only!
						finished = true;
//						SoundController.getInstance().playOnetime("sound/click.ogg", false, Vector3f.ZERO);
						try {
							Thread.sleep(1500);
						} catch (InterruptedException ex) {
						}
					}
				});
			}
		});
	}
	
	public boolean initNetwork(String user, char [] pwd, String hostport){

		String[] split = hostport.split(":");
		//verified by GUI already
		Integer port = Integer.parseInt(split[1]);
		
		this.clientInfo = ClientFacade.get();
		
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
            		final GameServerJPanel p = GuiController.getInstance().displayServerSelectionJPanel(servers);
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
            		GuiController.getInstance().showErrorDialog("Failed to Connect to login server");
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

	public final boolean isFinished() {
		return finished;
	}

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
		finished = true;
		if(clientInfo!= null){
			clientInfo.cleanup();
			logger.info("Released Network Connections");
		}
	}
}
