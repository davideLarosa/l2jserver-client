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

public class TransparentLoginPanel extends JPanel {

	private static final long serialVersionUID = -1L;
	private float transparency = 0.5f;
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
		frame.setSize(200, 180);
		frame.setVisible(true);
		frame.setPreferredSize(new java.awt.Dimension(200, 180));
	}
	
	public TransparentLoginPanel() {
		super();
		setOpaque(false);
		initGUI();
	}
	
	private void initGUI() {
		try {
			this.setPreferredSize(new java.awt.Dimension(200, 140));
			this.setLayout(null);
			this.setOpaque(false);
			this.setSize(200, 140);
			{
				nameLabel = new TransparentLabel();
				this.add(nameLabel);
				nameLabel.setText("Username:");
				nameLabel.setBounds(12, 12, 72, 14);
			}
			{
				nameField = new TransparentTextField();
				this.add(nameField);
				nameField.setBounds(96, 9, 96, 21);
			}
			{
				jLabel1 = new TransparentLabel();
				this.add(jLabel1);
				jLabel1.setText("Password:");
				jLabel1.setBounds(12, 45, 72, 14);
			}
			{
				passwordField = new TransparentPasswordField();
				this.add(passwordField);
				passwordField.setBounds(96, 42, 96, 21);
			}
			{
				loginButton = new TransparentButton();
				this.add(loginButton);
				loginButton.setText("Login");
				loginButton.setBounds(12, 85, 82, 22);
			}
			{
				cancelButton = new TransparentButton();
				this.add(cancelButton);
				cancelButton.setText("Cancel");
				cancelButton.setBounds(115, 85, 82, 22);
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
}
