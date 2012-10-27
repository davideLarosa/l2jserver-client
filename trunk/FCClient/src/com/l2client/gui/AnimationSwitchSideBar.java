package com.l2client.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import com.jme3.animation.AnimControl;

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
public class AnimationSwitchSideBar extends javax.swing.JPanel {
	private JButton prev;
	private JButton next;
	private JLabel label;
	private ArrayList<String> animations;
	private int current = 0;
	private AnimControl control;

	/**
	* Auto-generated main method to display this 
	* JPanel inside a new JFrame.
	*/
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.getContentPane().add(new AnimationSwitchSideBar());
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	public AnimationSwitchSideBar() {
		super();
		initGUI();
	}
	
	private void initGUI() {
		try {
			setPreferredSize(new Dimension(600, 50));
			this.setLayout(null);
			{
				prev = new JButton();
				this.add(prev);
				prev.setText("<");
				prev.setBounds(12, 12, 29, 27);
				prev.setFont(new java.awt.Font("Tahoma",0,16));
				prev.setToolTipText("previous");
				prev.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						onPrevious();
					}
				});
			}
			{
				next = new JButton();
				this.add(next);
				next.setText(">");
				next.setFont(new java.awt.Font("Tahoma",0,16));
				next.setBounds(541, 12, 29, 27);
				next.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						onNext();
					}
				});
			}
			{
				label = new JLabel();
				this.add(label);
				label.setBounds(48, 12, 488, 27);
				label.setFont(new java.awt.Font("Tahoma",0,18));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setTarget(AnimControl con, ArrayList<String> animations){
		this.control = con;
		this.animations = animations;
	}

	protected void onNext() {
		if(this.control != null){
			if(this.animations != null){
				current++;
				if(current >= this.animations.size())
					current = 0;
				if(this.animations.size() <= 0){
					label.setText("No animations available");
					return;
				}
					
				String anim = this.animations.get(current);
				label.setText(anim);
				
				control.getChannel(0).setAnim(anim, 1.0f);//TODO ev. customizable blendtime, currently long (1.0sec) fix blendtime
			}
		}else{
			label.setText("No target selected for animations");
		}
	}

	protected void onPrevious() {
		if(this.control != null){
			if(this.animations != null){
				current--;
				if(current < 0)
					current = this.animations.size()-1;
				if(this.animations.size() <= 0){
					label.setText("No animations available");
					return;
				}
					
				String anim = this.animations.get(current);
				label.setText(anim);
				control.getChannel(0).setAnim(anim, 1.0f);//TODO ev. customizable blendtime, currently long (1.0sec) fix blendtime
			}
		}else{
			label.setText("No target selected for animations");
		}
	}

	public void updateLabel(String txt){
		label.setText(txt);
	}
}
