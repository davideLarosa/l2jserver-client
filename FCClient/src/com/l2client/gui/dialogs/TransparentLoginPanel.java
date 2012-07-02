package com.l2client.gui.dialogs;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.l2client.gui.transparent.TransparentButton;
import com.l2client.gui.transparent.TransparentLabel;
import com.l2client.gui.transparent.TransparentPasswordField;
import com.l2client.gui.transparent.TransparentTextField;


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
public class TransparentLoginPanel extends JPanel {

	private static final long serialVersionUID = -1L;
	private float transparency = 0.7f;
	private TransparentTextField transparentServerField;
	private TransparentLabel transparentLabel1;
	private TransparentLabel nameLabel;
	private TransparentTextField nameField;
	private TransparentPasswordField passwordField;
	private TransparentButton cancelButton;
	private TransparentButton loginButton;
	private TransparentLabel jLabel1;

	/**
	* Auto-generated main method to display this 
	* JPanel inside a new JFrame.
	*/
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.getContentPane().add(new UserPasswordJPanel());
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setSize(310, 204);
		frame.setVisible(true);
		frame.setPreferredSize(new java.awt.Dimension(310, 204));
	}
	
	public TransparentLoginPanel() {
		super();
		setOpaque(false);
		initGUI();
	}
	
	private void initGUI() {
		try {
			this.setPreferredSize(new java.awt.Dimension(310, 204));
			this.setLayout(null);
			this.setOpaque(false);
			this.setSize(310, 204);
			{
				nameLabel = new TransparentLabel();
				this.add(nameLabel);
				nameLabel.setText("Username:");
				nameLabel.setBounds(12, 12, 89, 23);
			}
			{
				nameField = new TransparentTextField();
				this.add(nameField);
				nameField.setBounds(119, 9, 168, 26);
			}
			{
				jLabel1 = new TransparentLabel();
				this.add(jLabel1);
				jLabel1.setText("Password:");
				jLabel1.setBounds(12, 45, 89, 23);
			}
			{
				passwordField = new TransparentPasswordField();
				this.add(passwordField);
				passwordField.setBounds(119, 42, 168, 26);
			}
			{
				loginButton = new TransparentButton();
				this.add(loginButton);
				loginButton.setText("Login");
				loginButton.setBounds(12, 150, 121, 31);
			}
			{
				cancelButton = new TransparentButton();
				this.add(cancelButton);
				cancelButton.setText("Cancel");
				cancelButton.setBounds(166, 150, 121, 31);
			}
			{
				transparentLabel1 = new TransparentLabel();
				this.add(transparentLabel1);
				transparentLabel1.setTransparency(transparency);
				transparentLabel1.setText("Server:");
				transparentLabel1.setBounds(12, 106, 89, 23);
			}
			{
				transparentServerField = new TransparentTextField();
				this.add(transparentServerField);
				transparentServerField.setTransparency(transparency);
				transparentServerField.setBounds(119, 103, 168, 26);
				transparentServerField.setToolTipText("<Server>:<Port> e.g localhost:2106");
				transparentServerField.setVerifyInputWhenFocusTarget(true);
//				//Not working with swing gui, focus is passed anyway
//				transparentServerField.setInputVerifier(new ServerStringVerifier());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void paint(Graphics g){
		Graphics2D g2 = (Graphics2D)g.create();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
		super.paint(g2);
		g2.dispose();
	}

	public void setTransparency(float transparency) {
		this.transparency = transparency;
		this.nameField.setTransparency(transparency);
		this.nameLabel.setTransparency(transparency);
		this.passwordField.setTransparency(transparency);
		this.cancelButton.setTransparency(transparency);
		this.loginButton.setTransparency(transparency);
		this.jLabel1.setTransparency(transparency);
	}

	public float getTransparency() {
		return transparency;
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
	
	public String getServer(){
		return transparentServerField.getText();
	}
	
	public void setServer(String server){
		transparentServerField.setText(server);
	}
}
