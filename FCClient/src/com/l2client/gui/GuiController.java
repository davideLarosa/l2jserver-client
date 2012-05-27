package com.l2client.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.jme3.math.FastMath;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.swingGui.JMEDesktop;
import com.jme3.swingGui.dnd.JMEDragAndDrop;
import com.jme3.system.AppSettings;
import com.l2client.controller.SceneManager;
import com.l2client.gui.actions.BaseUsable;
import com.l2client.gui.actions.SkillAndActionsPanelToggel;
import com.l2client.gui.dialogs.CharCreateJPanel;
import com.l2client.gui.dialogs.ChatPanel;
import com.l2client.gui.dialogs.GameServerJPanel;
import com.l2client.gui.dialogs.MinMaxListener;
import com.l2client.gui.dialogs.MoveByBackgroundListener;
import com.l2client.gui.dialogs.ShortCutPanel;
import com.l2client.gui.dialogs.SkillAndActionsJPanel;
import com.l2client.gui.dialogs.TransparentLoginPanel;
import com.l2client.gui.dnd.ActionButton;
import com.l2client.gui.dnd.DnDSlotAction;
import com.l2client.gui.transparent.TransparentInternalFrame;
import com.l2client.model.network.GameServerInfo;


/**
 * GuiController for shuffling GUI elements around. This should just be used to hook up the
 * created gui components onto the desktop and removing the from it. No direct user actions should be defined here
 * 
 * Specific user actions should be added to the components in the @see GameController which should
 * be more specific what should be done, and also should link any needed data in.
 * 
 * used as a singleton by calling GuiController.getInstance()
 * 
 * GuiController glues the swing gui components and the JME Desktop system together, if you use
 * a different rendering system you will have to replace this on with corresponding gui calls
 * 
 * All window based gui components should use JInternalFrame to be placd on.
 */
public final class GuiController {

	private final static GuiController instance = new GuiController();
	
	private JMEDesktop jmeDesktop;
	private Node desktopNode;
	private JMEDragAndDrop jmeDragAndDrop;

	protected boolean rewire = false;
	
	private GuiController(){	
	}
	
	public static GuiController getInstance(){
		return instance;
	}
	
	/**
	 * Initialize the gui controller, creates a new JMEDesktop, attaches this one to the @see SceneRoot without anything visible
	 * 
	 * @param root The SceneRoot to be used for attaching the desktop node
	 * @param input The default input handler to be used (will be put on the base stack of the InputController)
	 */
	public void initialize(AppSettings settings, RenderManager renderMan) {
		this.jmeDesktop = new JMEDesktop("Swing Desktop",settings.getWidth(),settings.getHeight(),
				FastMath.nearestPowerOfTwo(settings.getWidth()),FastMath.nearestPowerOfTwo(settings.getHeight()),
				false/*no mipmap*/,InputController.get().getInputManager(), 
				settings /*we pass it in so desktop will rescale on resizing*/, renderMan);
		jmeDesktop.getJDesktop().setBackground( new Color( 1, 1, 1, 0.0f ) );
		jmeDesktop.setCullHint( Spatial.CullHint.Never );
		//this is needed to offset the desktop into the view direction, which is in negative z, so we pull the desktop  a little bit before the cam
		//if you have some effects where your gui does not show up, but shows when the view is rotated (flycam) it could be a problem with this offset
		jmeDesktop.getLocalTranslation().set( 0, 0, -1 );
		jmeDesktop.updateGeometricState();
		jmeDesktop.getJDesktop().repaint();
		jmeDesktop.getJDesktop().revalidate();
		
		SceneManager.get().changeRootNode(jmeDesktop,0);
		
		//add drag n drop support (used by shortcut panel, inventory, etc.)
		jmeDragAndDrop = new JMEDragAndDrop(jmeDesktop);
	}
	
	//TODO save and store slot actions
	public ShortCutPanel displayShortCutPanel(){
		final JDesktopPane desktopPane = jmeDesktop.getJDesktop();
		final JInternalFrame internalFrame = new TransparentInternalFrame();
		final ShortCutPanel pan = new ShortCutPanel();

		ActionButton[] arr = new ActionButton[10];
		arr[0]=new DnDSlotAction(jmeDesktop, ActionManager.getInstance().getAction(10000));
		arr[1]=new DnDSlotAction(jmeDesktop, ActionManager.getInstance().getAction(10002));
		arr[2]=new DnDSlotAction(jmeDesktop, ActionManager.getInstance().getAction(10003));
		for(int i=3;i<10;i++){
			arr[i]=new DnDSlotAction(jmeDesktop, null);
		}
		
		pan.setSlots(arr);
		pan.validate();
		
		internalFrame.add(pan);
		internalFrame.setVisible(true);
		internalFrame.pack();
		internalFrame.setLocation(desktopPane.getWidth()-internalFrame.getWidth(),desktopPane.getHeight()-internalFrame.getHeight());

		desktopPane.add(internalFrame);
		
		ArrayList<BaseUsable> acts = new ArrayList<BaseUsable>();
		acts.add(ActionManager.getInstance().getAction(10000));
		acts.add(ActionManager.getInstance().getAction(10002));
		acts.add(ActionManager.getInstance().getAction(10003));
		wireInputSwitch(acts, pan);
		
		desktopPane.repaint();
		desktopPane.revalidate();
		return pan;
	}
	
	public SkillAndActionsJPanel displaySkillAndActionsPanel(){
		final JDesktopPane desktopPane = jmeDesktop.getJDesktop();
		final JInternalFrame internalFrame = new TransparentInternalFrame();
		final SkillAndActionsJPanel pan = new SkillAndActionsJPanel();

		pan.validate();
		pan.addUsable(ActionManager.getInstance().getActions(), jmeDesktop);
		internalFrame.add(pan);
		internalFrame.setVisible(true);
		internalFrame.pack();
		internalFrame.setLocation(desktopPane.getWidth()
				- internalFrame.getWidth(), 20);

		ArrayList<BaseUsable> acts = new ArrayList<BaseUsable>();
		acts.add(new SkillAndActionsPanelToggel(-10, "SkillAndActionsPanelToggel") {
			@Override
			public void onAnalog(String name, float value, float tpf) {
				if (internalFrame.isVisible()) {
					internalFrame.setVisible(false);
					desktopPane.remove(internalFrame);
					desktopPane.repaint();
					desktopPane.revalidate();
				} else {
					internalFrame.setVisible(true);
					desktopPane.add(internalFrame);
					desktopPane.repaint();
					desktopPane.revalidate();
				}
			}
		});
		InputController.get().addInput(acts);

		wireInputSwitch(acts, pan);
		MoveByBackgroundListener mover = new MoveByBackgroundListener(pan, internalFrame);
		pan.addMouseListener(mover);
		pan.addMouseMotionListener(mover);
		
		internalFrame.setVisible(false);

		return pan;
	}
	
	/**
	 * Used to display a username password entry dialog. @see UserPasswordJPanel
	 * A name field, a masked password entry field and an ok and cancel button
	 * The dialog will be removed automatically on login/cancel
	 * 
	 * wire actions in the <b>returned</b> control
	 * 
		// action that gets executed in the update thread:
		pan.addLoginActionListener(new JMEAction("my action", input) {
			public void performAction(InputActionEvent evt) {
				// this gets executed in jme thread
				// do 3d system calls in jme thread only!
				initNetwork(pan.getUsername(), pan.getPassword());
			}
		});
	 *
	 *
	 * @param input The input handler to handle the input on focus
	 * @return the initialized dialog for action wireing
	 */
	//TODO store username in the config and preenter it for the user on next display
	public TransparentLoginPanel displayUserPasswordJPanel(){

		final JDesktopPane desktopPane = jmeDesktop.getJDesktop();
		desktopPane.removeAll();

//		final JInternalFrame internalFrame = new TransparentInternalFrame();
//		internalFrame.setLocation(500, 200);

		final TransparentLoginPanel pan = new TransparentLoginPanel();
//		internalFrame.add(pan);
		//FIXME if pan is used it is the one to be removed on close
		//how do we do dragging of window?
		pan.setTransparency(0.8f);
//		internalFrame.add(pan);
//		internalFrame.setSize(new java.awt.Dimension(200, 140));
//		internalFrame.pack();
//		internalFrame.setVisible(true);
//		desktopPane.add(internalFrame);
		
		//interresting feature too, no borders, just the panel, works too and looks ok without borders
		pan.setLocation(jmeDesktop.getJDesktop().getWidth()/2+50, jmeDesktop.getJDesktop().getHeight()/2-100);

		//commented out, as we have now a check going on if the input is valid
//		//these are the actions for the gui, thy do not define what should be executed on login/cancel
//		 // standard swing action:
//		pan.addCancelActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				// this gets executed in swing thread
//				// alter swing components only in swing thread!
//				pan.setVisible(false);
//				desktopPane.remove(pan);
//			}
//		});
//		pan.addLoginActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				// this gets executed in swing thread
//				// alter swing components ony in swing thread!
//				
//				pan.setVisible(false);
//				desktopPane.remove(pan);
//			}
//		});

		pan.setVisible(true);
		desktopPane.add(pan);
		
		return pan;
	}
	
	/**
	 * Creates a window with a gameserver list showing a status info line for each server,
	 * once the user selects a server the login button is enabled. @see GameServerJPanel
	 * The dialog will be removed automatically on cancel or select
	 * @param input			InputHandler to be used with this GUI
	 * @param serverInfos	The GameServerInfo to be displayed 
	 * @return The created gui component @see GameServerJPanel
	 */
	public GameServerJPanel displayServerSelectionJPanel(final GameServerInfo[] serverInfos){

		final JDesktopPane desktopPane = jmeDesktop.getJDesktop();
		desktopPane.removeAll();

		final JInternalFrame internalFrame = new TransparentInternalFrame();

		internalFrame.setLocation(300, 50);
		final GameServerJPanel pan = new GameServerJPanel(serverInfos);
		internalFrame.add(pan);
		
		internalFrame.setVisible(true);
		internalFrame.setSize(new java.awt.Dimension(400, 300));
		internalFrame.pack();

		desktopPane.add(internalFrame);
		
//		wireInputSwitch(input, pan);
		
		 // standard swing action:
		pan.addCancelActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// this gets executed in swing thread
				// alter swing components only in swing thread!
				internalFrame.setVisible(false);
				desktopPane.remove(internalFrame);
			}
		});
		pan.addSelectActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// this gets executed in swing thread
				// alter swing components ony in swing thread!
				internalFrame.setVisible(false);
				desktopPane.remove(internalFrame);
			}
		});

		desktopPane.repaint();
		desktopPane.revalidate();
		return pan;
	}
	
	/**
	 * Creates a window with a gameserver list showing a status info line for each server,
	 * once the user selects a server the login button is enabled. @see GameServerJPanel
	 * The dialog will be removed automatically on cancel or select
	 * @param input			InputHandler to be used with this GUI
	 * @param serverInfos	The GameServerInfo to be displayed 
	 * @return The created gui component @see GameServerJPanel
	 */
	public ChatPanel displayChatJPanel(){

		final JDesktopPane desktopPane = jmeDesktop.getJDesktop();
//		desktopPane.removeAll();

		final JInternalFrame internalFrame = new TransparentInternalFrame();		
		final ChatPanel pan = new ChatPanel();
		pan.validate();
		internalFrame.add(pan);
		internalFrame.setVisible(true);
		internalFrame.setSize(new java.awt.Dimension(320, 250));
		internalFrame.pack();
		internalFrame.setLocation(0,desktopPane.getHeight()-internalFrame.getHeight());

//System.out.println("tFrame: "+internalFrame.getSize()+"Pos:"+internalFrame.getLocation());
//System.out.println("Desktop: "+desktopPane.getSize()+"Settings: width="+settings.getWidth()+" height="+settings.getHeight());
		
		desktopPane.add(internalFrame);
		
		MinMaxListener mima = new MinMaxListener(internalFrame, "Chatwindow", pan, desktopPane);
		pan.addMouseListener(mima);
		
		wireInputSwitch(null, pan);

		desktopPane.repaint();
		desktopPane.revalidate();
		return pan;
	}
	
	
	/**
	 * Draft of a character creation panel, a race field to choose from, a name, a class, a create and cancel button
	 * The dialog will be removed automatically on cancel, but stay open on create
	 * @param input The InputHandler to be used with this gui
	 * @return		The created @see CharCreateJPanel where the game logic can be added to
	 */
	public CharCreateJPanel displayCharCreateJPanel(){

		final JDesktopPane desktopPane = jmeDesktop.getJDesktop();
		desktopPane.removeAll();
		
		final JInternalFrame internalFrame = new JInternalFrame();

		internalFrame.setLocation(20, 20);
		internalFrame.setResizable(false);
		internalFrame.setFrameIcon(null);
		final CharCreateJPanel pan = new CharCreateJPanel();
		internalFrame.add(pan);
		
		internalFrame.setVisible(true);
		internalFrame.setSize(new java.awt.Dimension(200, 180));
		internalFrame.pack();

		desktopPane.add(internalFrame);
		
//		wireInputSwitch(input, pan);
		
		 // standard swing action:
		pan.addCancelActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// this gets executed in swing thread
				// alter swing components only in swing thread!
				internalFrame.setVisible(false);
				desktopPane.remove(internalFrame);
			}
		});
		
		//nothing done in swing thread, pane stays open and will be closed on
		//create ok or cancel case only!
//		pan.addCreateActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				// this gets executed in swing thread
//				// alter swing components ony in swing thread!
//				internalFrame.setVisible(false);
//				desktopPane.remove(internalFrame);
//			}
//		});

		desktopPane.repaint();
		desktopPane.revalidate();
		return pan;
	}

	/**
	 * Convenience functionality to be able to display a modal Error Dialog based on a JOptionPane.
	 * The dialog will be removed automatically on close,calls SwingUtilities.invokeLater by itself
	 * 
	 * @param messageText the error message to be displayed
	 */
	public void showErrorDialog(final String messageText) {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				final JDesktopPane desktopPane = jmeDesktop.getJDesktop();
				final JInternalFrame modalDialog = new JInternalFrame("Error");

				JOptionPane optionPane = new JOptionPane(messageText, JOptionPane.ERROR_MESSAGE);
				modalDialog.getContentPane().add(optionPane);
				jmeDesktop.setModalComponent(modalDialog);
				desktopPane.add(modalDialog, 0);
				modalDialog.setVisible(true);
				modalDialog.setSize(modalDialog.getPreferredSize());
				modalDialog
						.setLocation((desktopPane.getWidth() - modalDialog
								.getWidth()) / 2,
								(desktopPane.getHeight() - modalDialog
										.getHeight()) / 2);
				jmeDesktop.setFocusOwner(optionPane);

				optionPane.addPropertyChangeListener(
						JOptionPane.VALUE_PROPERTY,
						new PropertyChangeListener() {
							public void propertyChange(PropertyChangeEvent evt) {
								modalDialog.setVisible(false);
								jmeDesktop.setModalComponent(null);
								desktopPane.remove(modalDialog);
							}
						});
			}
		});
    }

	/**
	 * removes all gui components (but not buttons)
	 */
	//FIXME does not remove buttons though
	public void removeAll() {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				final JDesktopPane desktopPane = jmeDesktop.getJDesktop();
				desktopPane.removeAll();
			}
		});
	}
	
	/**
	 * Convenience functionality to be able to display a modal info Dialog based on a JOptionPane.
	 * The dialog will be removed automatically on close, calls SwingUtilities.invokeLater by itself
	 * @param messageText the info message to be displayed
	 */
	//TODO could be refactored to be one code with showErrorDialog
	public void showInfoDialog(final String messageText) {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				final JDesktopPane desktopPane = jmeDesktop.getJDesktop();
				final JInternalFrame modalDialog = new JInternalFrame("Info");

				JOptionPane optionPane = new JOptionPane(messageText, JOptionPane.INFORMATION_MESSAGE);
				modalDialog.getContentPane().add(optionPane);
				jmeDesktop.setModalComponent(modalDialog);
				desktopPane.add(modalDialog, 0);
				modalDialog.setVisible(true);
				modalDialog.setSize(modalDialog.getPreferredSize());
				modalDialog
						.setLocation((desktopPane.getWidth() - modalDialog
								.getWidth()) / 2,
								(desktopPane.getHeight() - modalDialog
										.getHeight()) / 2);
				jmeDesktop.setFocusOwner(optionPane);

				optionPane.addPropertyChangeListener(
						JOptionPane.VALUE_PROPERTY,
						new PropertyChangeListener() {
							public void propertyChange(PropertyChangeEvent evt) {
								modalDialog.setVisible(false);
								jmeDesktop.setModalComponent(null);
								desktopPane.remove(modalDialog);
							}
						});
			}
		});
    }

	/**
	 * creates a button on the desktop, caller is responsible to clean up the buttons (removeButton or removeAll)
	 * @param name
	 * @return
	 */
	public JButton displayButton(String name) {
		final JButton b = new JButton(name);
		final JDesktopPane desktopPane = jmeDesktop.getJDesktop();
		desktopPane.add(b);
		b.setVisible(true);
		desktopPane.repaint();
		desktopPane.revalidate();
		return b;
	}
	
	/**
	 * removes the passed buttons from the desktop, calls SwingUtilities.invokeLater by itself
	 * @param b
	 */
	public void removeButton(final JButton[] b) {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				final JDesktopPane desktopPane = jmeDesktop.getJDesktop();
				for (JButton j : b)
					desktopPane.remove(j);

				desktopPane.repaint();
				desktopPane.revalidate();
			}
		});
	}
	
	private void wireInputSwitch(final ArrayList<BaseUsable> actions, Component comp){
		comp.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				//this stuff is needed so we remember what to do if a drag & drop happened
				//drag & drop must keep the input from updating but as soon as it finishes
				//it must perform the correct action, so,
//				if(pushOrPop>0)
//					InputController.getInstance().popInput();
//				else if(pushOrPop<0)
//					InputController.getInstance().pushInput(input);
//				
//				pushOrPop = 0;
//				if(pushOrPop >0){
//					CharacterController.getInstance().setInputEnabled(true);
//				
//				pushOrPop = 0;}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
//				pushOrPop = 0;
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
//					if(jmeDragAndDrop.isDragging())
//						pushOrPop++;
//					else
				if(actions != null)
					InputController.get().popInput();
//					if(jmeDragAndDrop.isDragging()){
//						pushOrPop = 1;
//						CharacterController.getInstance().setInputEnabled(false);
//					}
					if(jmeDragAndDrop.isDragging()){
						rewire=true;
						CharacterController.getInstance().setInputEnabled(false);
					}
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
//				if(jmeDragAndDrop.isDragging())
//					pushOrPop--;
//				else
				if(actions != null)
					InputController.get().pushInput(actions);
//					if(jmeDragAndDrop.isDragging())
//						pushOrPop = 0;
					if(jmeDragAndDrop.isDragging())
						rewire=false;
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {				
			}
		});
	}
	
	public boolean isDragging(){
		return jmeDragAndDrop.isDragging();
	}
	
	public void rewire(){
		if(rewire){
			CharacterController.getInstance().setInputEnabled(true);
			rewire = false;
		}
	}
	
//	public void testrun(){
//		if(jmeDesktop != null){
//			jmeDesktop.getJDesktop().repaint();
//			jmeDesktop.getJDesktop().revalidate();
//		}
//	}
}
