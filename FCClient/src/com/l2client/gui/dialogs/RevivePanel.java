package com.l2client.gui.dialogs;

import java.awt.event.ActionEvent;
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
public class RevivePanel extends javax.swing.JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2965178341633221432L;
	private JButton clanButton;
	private JButton castleButton;
	private JButton itemButton;
	private JButton fixedButton;
	private JButton siegeButton;
	private JButton fortressButton;
	private JButton villageButton;
	private ActionListener externAction;
	private ActionListener action;

	/**
	* Auto-generated main method to display this 
	* JPanel inside a new JFrame.
	*/
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.getContentPane().add(new RevivePanel());
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setSize(200, 180);
		frame.setVisible(true);
		frame.setPreferredSize(new java.awt.Dimension(200, 180));
	}
	
	public RevivePanel() {
		super();
		initGUI();
	}
	
	private void initGUI() {
		try {
			{
				this.action = new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						if (externAction != null) {
							Object src = e.getSource();
							int act = 0;
							if (clanButton.equals(src))
								act = 1;
							else if (castleButton.equals(src))
								act = 2;
							else if (fortressButton.equals(src))
								act = 3;
							else if (siegeButton.equals(src))
								act = 4;
							else if (fixedButton.equals(src))
								act = 5;
							externAction.actionPerformed(new ActionEvent(e.getSource(),
									act, e.getActionCommand()));

						}
					}
				};
			}
			this.setPreferredSize(new java.awt.Dimension(200, 207));
			this.setLayout(null);
			this.setOpaque(false);
			this.setSize(200, 140);
			{
				villageButton = new JButton();
				this.add(villageButton);
				villageButton.setText("to village");
				villageButton.setBounds(12, 14, 170, 22);
				villageButton.addActionListener(this.action);
			}
			{
				clanButton = new JButton();
				this.add(clanButton);
				clanButton.setText("to clanhall");
				clanButton.setBounds(12, 38, 170, 22);
				clanButton.addActionListener(this.action);
			}
			{
				castleButton = new JButton();
				this.add(castleButton);
				castleButton.setText("to castle");
				castleButton.setBounds(12, 64, 170, 22);
				castleButton.addActionListener(this.action);
			}
			{
				fortressButton = new JButton();
				this.add(fortressButton);
				fortressButton.setText("to fortress");
				fortressButton.setBounds(12, 91, 170, 22);
				fortressButton.addActionListener(this.action);
			}
			{
				siegeButton = new JButton();
				this.add(siegeButton);
				siegeButton.setText("to siege HQ");
				siegeButton.setBounds(12, 118, 170, 22);
				siegeButton.addActionListener(this.action);
			}
			{
				fixedButton = new JButton();
				this.add(fixedButton);
				fixedButton.setText("fixed");
				fixedButton.setBounds(12, 146, 170, 22);
				fixedButton.addActionListener(this.action);
			}
			{
				itemButton = new JButton();
				this.add(itemButton);
				itemButton.setText("use Item");
				itemButton.setBounds(12, 173, 170, 22);
				itemButton.addActionListener(this.action);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Add an action listener which will receive the actionPerformed called with an ActionEvent containing the revive place as id (nulls other fields of the event)
	 * @param l	ActionListener to be called
	 */
	public void addActionListener(ActionListener l){	
		externAction = l;
	}
	
	/**
	 * sets which buttons to display for revive options
	 * @param opt	bitwise 0 village, 1 clan, 2 castle, 4 fort, 8 siege, 16 fixed, 32 item (use text to change which item)
	 * @param itemText optional text on item use otherwise "use Item" will eb displayed on button
	 */
	public void setButtonOptions(int opt, String itemText){
		villageButton.setVisible(true); //always on
		clanButton.setVisible(false);
		castleButton.setVisible(false);
		fortressButton.setVisible(false);
		siegeButton.setVisible(false);
		fixedButton.setVisible(false);
		itemButton.setVisible(false);
		
		if((opt & 1) > 0)
			clanButton.setVisible(true);
		if((opt & 2) > 0)
			castleButton.setVisible(true);
		if((opt & 4) > 0)
			fortressButton.setVisible(true);
		if((opt & 8) > 0)
			siegeButton.setVisible(true);
		if((opt & 16) > 0)
			fixedButton.setVisible(true);
		if((opt & 32) > 0){
			if(itemText != null && itemText.length()> 0)
				itemButton.setText("use "+itemText);
			else
				itemButton.setText("use item");
			itemButton.invalidate();
			itemButton.setVisible(true);
		}
	}
}
