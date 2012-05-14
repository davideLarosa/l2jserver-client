package com.l2client.gui.dialogs;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import com.jme3.swingGui.JMEDesktop;
import com.l2client.gui.actions.BaseUsable;
import com.l2client.gui.dnd.DragAction;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class SkillAndActionsJPanel extends javax.swing.JPanel {
	private static String BASIC_PANE= "Basic";
	private static String SOCIAL_PANE= "Social";
	private static String ADVANCED_PANE= "Advanced";
	private JTabbedPane jTabbedPane1;
	private JScrollPane jScrollPane1;
	private JScrollPane jScrollPane2;
	private JButton jButton1;
	private JPanel jPanel1;
	private JScrollPane jScrollPane3;
	private HashMap<String, JPanel> panes = new HashMap<String, JPanel>();
	private JPanel jPanel2;
	private JPanel jPanel3;
	private FlowLayout paneLayout;

	/**
	* Auto-generated main method to display this 
	* JPanel inside a new JFrame.
	*/
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.getContentPane().add(new SkillAndActionsJPanel());
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	public SkillAndActionsJPanel() {
		super();
		initGUI();
	}
	
	private void initGUI() {
		try {
			setPreferredSize(new Dimension(400, 300));
			this.setLayout(null);
			{
				jTabbedPane1 = new JTabbedPane();
				this.add(jTabbedPane1);
				paneLayout = new FlowLayout();
				paneLayout.setAlignment(FlowLayout.LEFT);
				jTabbedPane1.setBounds(0, 12, 395, 291);
				{
					jScrollPane1 = new JScrollPane();
					jTabbedPane1.addTab("Basic", null, jScrollPane1, null);
					{
						jPanel1 = new JPanel();
						jPanel1.setLayout(paneLayout);
						jScrollPane1.setViewportView(jPanel1);
					}
//					jScrollPane1.setPreferredSize(new java.awt.Dimension(370, 273));
				}
				{
					jScrollPane2 = new JScrollPane();
					jTabbedPane1.addTab("Social", null, jScrollPane2, null);
					{
						jPanel2 = new JPanel();
						jPanel2.setLayout(paneLayout);
						jScrollPane2.setViewportView(jPanel2);
					}
				}
				{
					jScrollPane3 = new JScrollPane();
					jTabbedPane1.addTab("Advanced", null, jScrollPane3, null);
					{
						jPanel3 = new JPanel();
						jPanel3.setLayout(paneLayout);
						jScrollPane3.setViewportView(jPanel3);
					}
//					jScrollPane3.setPreferredSize(new java.awt.Dimension(370, 247));
				}
			}
			{
				jButton1 = new JButton();
				this.add(jButton1);
				jButton1.setText("x");
				jButton1.setBounds(380, -1, 20, 20);
				jButton1.setFont(new java.awt.Font("Tahoma",0,6));
				jButton1.setMaximumSize(new java.awt.Dimension(14, 14));
				jButton1.setIconTextGap(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addUsable(BaseUsable[] useArr, JMEDesktop desktop){
		for(BaseUsable use : useArr){
		DragAction d = new DragAction(desktop, use);
		d.setPreferredSize(new java.awt.Dimension(32, 32));
		d.setContentAreaFilled(true);
		addComponentToPane(d, use.getCategory());
		}
	}

	private boolean addComponentToPane(Component comp, String pane) {
		if (BASIC_PANE.equals(pane)) {
			jPanel1.add(comp);
		} else if (SOCIAL_PANE.equals(pane)) {
			jPanel2.add(comp);
		} else if (ADVANCED_PANE.equals(pane)) {
			jPanel3.add(comp);
		} else {
			// try the procedural panes
			JPanel p = panes.get(pane);
			if (p != null) {
				p.add(comp);
				return true;
			} else {
				p = addPane(pane);
				if (p != null) {
					p.add(comp);
					return true;
				}
				return false;
			}
		}
		return false;
	}

	private JPanel addPane(String name) {
		if (!panes.containsKey(name)) {
			JScrollPane scroll = new JScrollPane();
			jTabbedPane1.addTab(name, null, scroll, null);
			JPanel pan = new JPanel();
			pan.setLayout(paneLayout);
			scroll.setViewportView(pan);
			panes.put(name, pan);
			return pan;
		}
		return null;
	}
}
