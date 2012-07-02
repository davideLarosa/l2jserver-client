package com.l2client.gui.dialogs;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.WindowConstants;


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
//TODO images and actions for expand button and lock button
//TODO key wiring
public class ShortCutPanel extends javax.swing.JPanel {
	private JButton lock;
	private JButton expand;
	private static int SLOTS = 10;
	private JButton [] slots = null;//new JButton[SLOTS];
	private ShortCutPanel expandPanel = null; 


	/**
	* Auto-generated main method to display this 
	* JPanel inside a new JFrame.
	*/
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.getContentPane().add(new ShortCutPanel());
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	public ShortCutPanel() {
		super();
//		for(int i =0; i<SLOTS;i++)
//			slots[i] = new JButton();

		initGUI();
	}
	
	private void initGUI() {
		try {
			if(slots == null || slots.length <=0)
				return;
			this.removeAll();
			this.setPreferredSize(new java.awt.Dimension(372, 38));
			this.setSize(372, 38);
			FlowLayout l = new FlowLayout();
			l.setHgap(2);
			this.setLayout(l);
			for(int i =0; i<SLOTS;i++){
				this.add(slots[i]);
//				slots[i].setLayout(null);
				slots[i].setContentAreaFilled(true);
				slots[i].setPreferredSize(new java.awt.Dimension(32, 32));
				if(slots[i].getIcon() != null)
					slots[i].setEnabled(true);
				else
					slots[i].setEnabled(false);
			}
			{
				expand = new JButton();
				this.add(expand);
//				expand.setLayout(null);
				expand.setContentAreaFilled(true);
				expand.setPreferredSize(new java.awt.Dimension(14, 32));
			}
			{
				lock = new JButton();
				this.add(lock);
//				lock.setLayout(null);
				lock.setContentAreaFilled(true);
				lock.setPreferredSize(new java.awt.Dimension(14, 32));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//FIXME add also lock and expand action listeners!!!
	public void setSlots(JButton[] arr){
		if(arr != null && arr.length != SLOTS)
			return;
		
		slots = arr;
		removeAll();
		initGUI();
		
	}
	
	public void addExpandAction(ActionListener l){
		expand.addActionListener(l);
	}
	
	public void addLockAction(ActionListener l){
		lock.addActionListener(l);
	}
}
