package com.l2client.test;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.jme3.animation.AnimControl;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.swingGui.JMEDesktop;
import com.l2client.app.Assembler2;
import com.l2client.app.ExtendedApplication;
import com.l2client.gui.AnimationSwitchSideBar;
import com.l2client.gui.InputController;
import com.l2client.gui.dialogs.CharCreateJPanel;
import com.l2client.model.PartSet;
import com.l2client.util.PartSetManager;

public class TestAnimations extends ExtendedApplication {


	int MAX_NODES = 10;
	
	PartSetManager man = PartSetManager.get();
	
	Node[] nodes = new Node[MAX_NODES];
	
	int currentNode = 0;

	private PartSet con;

	private JMEDesktop jmeDesktop;
	

    public void simpleInitApp() {
    	
    	man.loadParts("megaset.csv");
         
        //move cam a bit closer
        cam.setLocation(cam.getLocation().mult(0.5f));
        inputManager.setCursorVisible(true);
        flyCam.setEnabled(false);
        
        DirectionalLight dr = new DirectionalLight();
        dr.setColor(ColorRGBA.White);
        dr.setDirection(new Vector3f(1, 0 , 1));
        
        AmbientLight am = new AmbientLight();
        am.setColor(ColorRGBA.White);
        rootNode.addLight(am);
        rootNode.addLight(dr);

        setupGUI();
        setupScene();

    }
 
    private void setupGUI() {
		this.jmeDesktop = new JMEDesktop("Swing Desktop",settings.getWidth(),settings.getHeight(),
				FastMath.nearestPowerOfTwo(settings.getWidth()),FastMath.nearestPowerOfTwo(settings.getHeight()),
				false/*no mipmap*/,inputManager, 
				settings /*we pass it in so desktop will rescale on resizing*/, renderManager);
		jmeDesktop.getJDesktop().setBackground( new Color( 1, 1, 1, 0.0f ) );
		jmeDesktop.setCullHint( Spatial.CullHint.Never );
		//this is needed to offset the desktop into the view direction, which is in negative z, so we pull the desktop  a little bit before the cam
		//if you have some effects where your gui does not show up, but shows when the view is rotated (flycam) it could be a problem with this offset
		jmeDesktop.getLocalTranslation().set( 0, 0, -1 );
		jmeDesktop.updateGeometricState();
		jmeDesktop.getJDesktop().repaint();
		jmeDesktop.getJDesktop().revalidate();
		
		rootNode.attachChild(jmeDesktop);
		
	}

	/**
     * add terrain
     */
    private void setupScene() {

    	//TODO could use getTemplates to switch through models..
    	Node n = Assembler2.getModel3("dwarfwarrior"); //"pelffwarrior");//humanhalberd"); //goblin");

    	if(n != null){
    		rootNode.attachChild(n);
//    		n.updateGeometricState();
    	

    		final AnimControl conn = n.getControl(AnimControl.class);
    		SwingUtilities.invokeLater(new Runnable() {

    			public void run() {
    				final JDesktopPane desktopPane = jmeDesktop.getJDesktop();
    				final JInternalFrame internalFrame = new JInternalFrame();

    				internalFrame.setLocation(20, 20);
    				internalFrame.setResizable(false);
    				internalFrame.setFrameIcon(null);
    				AnimationSwitchSideBar bar = new AnimationSwitchSideBar();
    				bar.setTarget(conn, getAnimations());
    				internalFrame.add(bar);
    				
    				internalFrame.setVisible(true);
    				internalFrame.setSize(new java.awt.Dimension(200, 180));
    				internalFrame.pack();

    				desktopPane.add(internalFrame);
    			}
    		});   		
    	
    	}
    }
    
	/**
     * Entry point
     */
    public static void main(String[] args) {
    	TestAnimations app = new TestAnimations();
        app.start();
    }
    
    
    private ArrayList<String> getAnimations(){
    	ArrayList<String> ret = new ArrayList<String>();
		ret.add("advance");
		ret.add("advance_to_combat_jog");
		ret.add("advance_to_ready");
		ret.add("at_hi_a_punch_fail");
		ret.add("at_hi_a_punch_success");
		ret.add("at_hi_c_slashrl_fail");
		ret.add("at_hi_c_slashrl_success");
		ret.add("at_mid_c_slashlr_v0_fail");
		ret.add("at_mid_c_slashlr_v0_success");
		ret.add("at_mid_c_slashlr_v1_s0_fail");
		ret.add("at_mid_c_slashlr_v1_s0_success");
		ret.add("at_mid_c_slashrl_s0_fail");
		ret.add("at_mid_c_slashrl_s0_success");
		ret.add("at_mid_c_slashrl_s1_slashlr_fail");
		ret.add("at_mid_c_slashrl_s1_slashlr_success");
		ret.add("aztec_coyote_preist_druid_chant");
		ret.add("aztec_coyote_preist_druid_chant_to_idle");
		ret.add("aztec_coyote_preist_idle_to_druid_chant");
		ret.add("basepose");
		ret.add("celebrate");
		ret.add("charge");
		ret.add("charge_attack");
		ret.add("charge_to_ready");
		ret.add("climb_down");
		ret.add("climb_down_to_stand_a");
		ret.add("climb_idle");
		ret.add("climb_up");
		ret.add("climb_up_to_stand_a");
		ret.add("combat_jog");
		ret.add("combat_jog_to_advance");
		ret.add("combat_jog_to_ready");
		ret.add("defend_hi_slashlr");
		ret.add("defend_hi_slashrl");
		ret.add("defend_hi_stab");
		ret.add("defend_lo_slashlr");
		ret.add("defend_lo_slashrl");
		ret.add("defend_lo_stab");
		ret.add("defend_mid_slashlr");
		ret.add("defend_mid_slashrl");
		ret.add("defend_mid_stab");
		ret.add("die_backward");
		ret.add("die_flailing_cycle");
		ret.add("die_flailing_cycle_end");
		ret.add("die_forward");
		ret.add("die_to_back_left");
		ret.add("die_to_back_right");
		ret.add("fast_run");
		ret.add("hide_idle");
		ret.add("hide_to_stand_a");
		ret.add("hide_to_stealthy_walk");
		ret.add("knockback_from_back");
		ret.add("knockback_from_front");
		ret.add("knockback_from_left");
		ret.add("knockback_from_right");
		ret.add("knockback_move_from_back");
		ret.add("knockback_move_from_front");
		ret.add("knockback_move_from_left");
		ret.add("knockback_move_from_right");
		ret.add("knockdown_backward_launch");
		ret.add("knockdown_backward_lying");
		ret.add("knockdown_backward_recover");
		ret.add("knockdown_forward_launch");
		ret.add("knockdown_forward_lying");
		ret.add("knockdown_forward_recover");
		ret.add("ready_15_ccw");
		ret.add("ready_15_cw");
		ret.add("ready_45_ccw");
		ret.add("ready_45_cw");
		ret.add("ready_90_ccw");
		ret.add("ready_90_cw");
		ret.add("ready_hf_idle");
		ret.add("ready_idle");
		ret.add("ready_lf_high_morale");
		ret.add("ready_lf_low_morale");
		ret.add("ready_to_advance");
		ret.add("ready_to_charge");
		ret.add("ready_to_combat_jog");
		ret.add("ready_to_retreat");
		ret.add("ready_to_stand_a");
		ret.add("retreat");
		ret.add("retreat_to_ready");
		ret.add("run");
		ret.add("run_attack");
		ret.add("run_to_charge");
		ret.add("run_to_stand_a");
		ret.add("run_to_walk");
		ret.add("shuffle_backward");
		ret.add("shuffle_forward");
		ret.add("shuffle_left");
		ret.add("shuffle_right");
		ret.add("signal_charge");
		ret.add("signal_halt");
		ret.add("signal_move");
		ret.add("signal_reform");
		ret.add("stand_a_hf_idle");
		ret.add("stand_a_idle");
		ret.add("stand_a_lf_idle");
		ret.add("stand_a_step_backward");
		ret.add("stand_a_step_forward");
		ret.add("stand_a_step_left");
		ret.add("stand_a_step_right");
		ret.add("stand_a_to_charge");
		ret.add("stand_a_to_climb_down");
		ret.add("stand_a_to_climb_up");
		ret.add("stand_a_to_hide");
		ret.add("stand_a_to_ready");
		ret.add("stand_a_to_run");
		ret.add("stand_a_to_stand_b");
		ret.add("stand_a_to_stand_c");
		ret.add("stand_a_to_walk");
		ret.add("stand_a_turn_45_ccw");
		ret.add("stand_a_turn_45_cw");
		ret.add("stand_a_turn_90_ccw");
		ret.add("stand_a_turn_90_cw");
		ret.add("stand_b_hf_idle");
		ret.add("stand_b_idle");
		ret.add("stand_b_lf_idle");
		ret.add("stand_b_to_ready");
		ret.add("stand_b_to_run");
		ret.add("stand_b_to_stand_a");
		ret.add("stand_b_to_stand_c");
		ret.add("stand_b_to_walk");
		ret.add("stand_c_hf_idle");
		ret.add("stand_c_idle");
		ret.add("stand_c_lf_idle");
		ret.add("stand_c_to_ready");
		ret.add("stand_c_to_run");
		ret.add("stand_c_to_stand_a");
		ret.add("stand_c_to_stand_b");
		ret.add("stand_c_to_walk");
		ret.add("stealthy_walk");
		ret.add("stealthy_walk_to_hide");
		ret.add("stealthy_walk_to_stand_a");
		ret.add("stealthy_walk_to_walk");
		ret.add("taunt");
		ret.add("walk");
		ret.add("walk_to_run");
		ret.add("walk_to_stand_a");

    	return ret;
    }
}
