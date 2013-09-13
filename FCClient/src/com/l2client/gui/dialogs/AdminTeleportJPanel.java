package com.l2client.gui.dialogs;

import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
public class AdminTeleportJPanel extends javax.swing.JPanel {
	private JButton jButton1;
	private JButton jButton2;
	private JLabel jLabel1;

	/**
	* Auto-generated main method to display this 
	* JPanel inside a new JFrame.
	*/
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.getContentPane().add(new AdminTeleportJPanel());
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	public AdminTeleportJPanel() {
		super();
		initGUI();
	}
	
	private void initGUI() {
		try {
			setPreferredSize(new Dimension(240, 110));
			this.setLayout(null);
			{
				jButton1 = new JButton();
				this.add(jButton1);
				jButton1.setText("Teleport to test area");
				jButton1.setBounds(12, 12, 212, 34);
			}
			{
				jButton2 = new JButton();
				this.add(jButton2);
				jButton2.setText("Teleport to start area");
				jButton2.setBounds(12, 57, 212, 34);
			}
			{
				jLabel1 = new JLabel();
				this.add(jLabel1);
				jLabel1.setText("Make sure you are an admin");
				jLabel1.setBounds(12, 12, 212, 33);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addToTestAreaActionListener(ActionListener actionListener) {
		jButton1.addActionListener(actionListener);
		
	}
	
	public void addFromTestAreaActionListener(ActionListener actionListener) {
		jButton2.addActionListener(actionListener);
		
	}

}
