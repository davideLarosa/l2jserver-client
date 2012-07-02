package com.l2client.gui.dialogs;

import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
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
public class UserPasswordJPanel extends javax.swing.JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2965178341633221432L;
	private JLabel nameLabel;
	private JTextField nameField;
	private JPasswordField passwordField;
	private JButton cancelButton;
	private JButton loginButton;
	private JLabel jLabel1;

	/**
	* Auto-generated main method to display this 
	* JPanel inside a new JFrame.
	*/
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.getContentPane().add(new UserPasswordJPanel());
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setSize(200, 180);
		frame.setVisible(true);
		frame.setPreferredSize(new java.awt.Dimension(200, 180));
	}
	
	public UserPasswordJPanel() {
		super();
		initGUI();
	}
	
	private void initGUI() {
		try {
			this.setPreferredSize(new java.awt.Dimension(200, 140));
			this.setLayout(null);
			this.setOpaque(false);
			this.setSize(200, 140);
			{
				nameLabel = new JLabel();
				this.add(nameLabel);
				nameLabel.setText("Username:");
				nameLabel.setBounds(12, 12, 52, 14);
			}
			{
				nameField = new JTextField();
				this.add(nameField);
				nameField.setBounds(76, 9, 106, 21);
			}
			{
				jLabel1 = new JLabel();
				this.add(jLabel1);
				jLabel1.setText("Password:");
				jLabel1.setBounds(12, 45, 52, 14);
			}
			{
				passwordField = new JPasswordField();
				this.add(passwordField);
				passwordField.setBounds(76, 42, 106, 21);
			}
			{
				loginButton = new JButton();
				this.add(loginButton);
				loginButton.setText("Login");
				loginButton.setBounds(12, 85, 67, 22);
			}
			{
				cancelButton = new JButton();
				this.add(cancelButton);
				cancelButton.setText("Cancel");
				cancelButton.setBounds(115, 85, 67, 22);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addCancelActionListener(ActionListener l){	
		cancelButton.addActionListener(l);
	}
	
	public void addLoginActionListener(ActionListener l){	
		loginButton.addActionListener(l);
	}
	
	public char[] getPassword(){
		return passwordField.getPassword();
	}
	
	public String getUsername(){
		return nameField.getText();
	}
}
