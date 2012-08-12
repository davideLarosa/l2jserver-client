package com.l2client.animsystem.example;


import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.jme3.animation.AnimControl;
import com.jme3.app.SimpleApplication;
import com.jme3.input.RawInputListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MotionSensorEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.l2client.animsystem.example.jme3.JMESimpleController;

/**
 * A GUI test for the animation system.
 * The jme ninja example is used as a base. 
 * Get sure the jme data is in your execution path!
 *
 */
public class AnimationInputTest extends SimpleApplication {
	
	private JFrame frame;

    /*
     * (non-Javadoc)
     *
     * @see com.jme.app.SimpleGame#initGame()
     */
    public void simpleInitApp() {
       setupScene();
       //override mouse input, otherwise you will have a hard time switching to the menu
       //without rotating the model
		mouseInput.setInputListener(new RawInputListener() {

			@Override
			public void onTouchEvent(TouchEvent evt) {
			}

			@Override
			public void onMouseMotionEvent(MouseMotionEvent evt) {
			}

			@Override
			public void onMouseButtonEvent(MouseButtonEvent evt) {
			}

			@Override
			public void onKeyEvent(KeyInputEvent evt) {
			}

			@Override
			public void onJoyButtonEvent(JoyButtonEvent evt) {
			}

			@Override
			public void onJoyAxisEvent(JoyAxisEvent evt) {
			}

			@Override
			public void endInput() {
			}

			@Override
			public void beginInput() {
			}

			@Override
			public void onMotionSensorEvent(MotionSensorEvent evt) {
				// TODO Auto-generated method stub
				
			}
		});
	}
    
    /*
     * replace controller
     * initialize gui
     */
    private void setupScene() {

        /** Load a Ninja model (OgreXML + material + texture from test_data) */
        Spatial ninja = assetManager.loadModel("Models/Ninja/Ninja.mesh.xml");
        ninja.scale(0.05f, 0.05f, 0.05f);
        ninja.rotate(0.0f, -3.0f, 0.0f);
        ninja.setLocalTranslation(0.0f, -5.0f, -2.0f);
        rootNode.attachChild(ninja);
        /** You must add a light to make the model visible */
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f).normalizeLocal());
        rootNode.addLight(sun);
        
    	AnimControl c = (AnimControl)rootNode.getChild(0).getControl(0);
    	c.createChannel();
    	final JMESimpleController con = new JMESimpleController(c);
    	rootNode.getChild(0).addControl(con);

    	if(con!= null ){
    		SwingUtilities.invokeLater(new Runnable() {

				public void run() {
					AnimationInputSwitchSidebar bar = new AnimationInputSwitchSidebar();
    				bar.setTarget(con);
    				frame = new JFrame();
    				frame.getContentPane().add(bar);
    				frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    				frame.pack();
    				frame.setVisible(true);
    			}
    		});   		
    	}
    }
    
	/**
     * Entry point
     */
    public static void main(String[] args) {
    	AnimationInputTest app = new AnimationInputTest();
    	//This is needed otherwise the swing menu will freeze the display until it gets focus back
    	app.setPauseOnLostFocus(false);
        app.start();
    }
    
    @Override
    public void destroy(){
    	super.destroy();
    	//Cleanup of swing window
    	SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				frame.dispose();
			}
    	});
    }

}
