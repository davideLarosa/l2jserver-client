package com.l2client.animsystem.example;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.WindowConstants;

import com.jme3.scene.control.AbstractControl;
import com.l2client.animsystem.InputProvider;
import com.l2client.animsystem.example.jme3.JMESimpleController;


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
public class AnimationInputSwitchSidebar extends javax.swing.JPanel {
	private JComboBox jComboBox1;
	private JComboBox jComboBox2;
	private JComboBox jComboBox3;
	private JComboBox jComboBox4;
	private JSeparator jSeparator1;
	private JLabel jLabel4;
	private JLabel jLabel3;
	private JLabel jLabel2;
	private JLabel jLabel1;
	private JButton jButton3;
	private JButton jButton2;
	private JButton jButton1;
	private JButton jButton4;
	private JSeparator jSeparator3;
	private JLabel jLabel12;
	private JSeparator jSeparator2;
	private JMESimpleController target = null;

	private ActionListener listener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			onValuesChanged();
		}
	};
	private ActionListener woundedListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(target != null)
			target.callAction("DieAction", getInputFromModel());
		}
	};
	private ActionListener defendListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(target != null)
			target.callAction("BlockAction", getInputFromModel());
		}
	};
	private ActionListener attackListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(target != null)
			target.callAction("AttackAction", getInputFromModel());
		}
	};
	private ActionListener jumpListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(target != null)
			target.callAction("JumpAction", getInputFromModel());
		}
	};


	/**
	* Auto-generated main method to display this 
	* JPanel inside a new JFrame.
	*/
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.getContentPane().add(new AnimationInputSwitchSidebar());
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	public AnimationInputSwitchSidebar() {
		super();
		initGUI();
	}
	
	private void initGUI() {
		try {
			this.setPreferredSize(new java.awt.Dimension(180, 220));
			{
				ComboBoxModel jComboBox1Model = 
					new DefaultComboBoxModel(
							Acting.values());
				jComboBox1 = new JComboBox();
				this.add(getJSeparator2());
				this.add(getJLabel12());
				this.add(getJLabel1());
				this.add(jComboBox1);
				jComboBox1.setModel(jComboBox1Model);
				jComboBox1.setPreferredSize(new java.awt.Dimension(80, 21));
				jComboBox1.addActionListener(listener);
			}
			{
				ComboBoxModel jComboBox2Model = 
					new DefaultComboBoxModel(
							AttackType.values());
				jComboBox2 = new JComboBox();
				this.add(getJLabel2());
				this.add(jComboBox2);
				jComboBox2.setModel(jComboBox2Model);
				jComboBox2.setPreferredSize(new java.awt.Dimension(80, 21));
				jComboBox2.addActionListener(listener);
			}
			{
				ComboBoxModel jComboBox3Model = 
					new DefaultComboBoxModel(
							Movement.values());
				jComboBox3 = new JComboBox();
				this.add(getJLabel3());
				this.add(jComboBox3);
				jComboBox3.setModel(jComboBox3Model);
				jComboBox3.setPreferredSize(new java.awt.Dimension(80, 21));
				jComboBox3.addActionListener(listener);
			}
			{
				ComboBoxModel jComboBox4Model = 
					new DefaultComboBoxModel(
							Target.values());
				jComboBox4 = new JComboBox();
				this.add(getJLabel4());
				this.add(jComboBox4);
				jComboBox4.setModel(jComboBox4Model);
				jComboBox4.setPreferredSize(new java.awt.Dimension(80, 21));
				jComboBox4.addActionListener(listener);
				this.add(getJSeparator1());
			}
			{
				this.add(getJButton1());
				this.add(getJButton2());
				this.add(getJButton3());
				this.add(getJButton4());
				this.add(getJSeparator3());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void onValuesChanged() {
		if(target != null){
			InputProvider in = getInputFromModel();
			target.setInput(in);
		}
	}

	public void setTarget(AbstractControl con) {
		if(con instanceof JMESimpleController){
			target = (JMESimpleController)con;
		}
	}
	
	private InputProvider getInputFromModel(){
		InputProvider in = new InputProvider();
		in.setInput((Acting) jComboBox1.getSelectedItem());
		in.setInput((AttackType) jComboBox2.getSelectedItem());
		in.setInput((Movement) jComboBox3.getSelectedItem());
		in.setInput((Target) jComboBox4.getSelectedItem());
		return in;
	}
	
	private JButton getJButton1() {
		if(jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setText("Attack");
			jButton1.setPreferredSize(new java.awt.Dimension(80, 21));
			jButton1.addActionListener(attackListener);
		}
		return jButton1;
	}
	
	private JButton getJButton2() {
		if(jButton2 == null) {
			jButton2 = new JButton();
			jButton2.setText("Defend");
			jButton2.setPreferredSize(new java.awt.Dimension(80, 21));
			jButton2.addActionListener(defendListener);
		}
		return jButton2;
	}
	
	private JButton getJButton3() {
		if(jButton3 == null) {
			jButton3 = new JButton();
			jButton3.setText("Hit");
			jButton3.setPreferredSize(new java.awt.Dimension(80, 21));
			jButton3.addActionListener(woundedListener);
		}
		return jButton3;
	}
	
	
	private JLabel getJLabel1() {
		if(jLabel1 == null) {
			jLabel1 = new JLabel();
			jLabel1.setText("Acting:");
			jLabel1.setPreferredSize(new java.awt.Dimension(80, 14));
		}
		return jLabel1;
	}
	
	private JLabel getJLabel2() {
		if(jLabel2 == null) {
			jLabel2 = new JLabel();
			jLabel2.setText("AttackType:");
			jLabel2.setPreferredSize(new java.awt.Dimension(80, 14));
		}
		return jLabel2;
	}
	
	private JLabel getJLabel3() {
		if(jLabel3 == null) {
			jLabel3 = new JLabel();
			jLabel3.setText("Movement:");
			jLabel3.setPreferredSize(new java.awt.Dimension(80, 14));
		}
		return jLabel3;
	}
	
	private JLabel getJLabel4() {
		if(jLabel4 == null) {
			jLabel4 = new JLabel();
			jLabel4.setText("Target:");
			jLabel4.setPreferredSize(new java.awt.Dimension(80, 14));
		}
		return jLabel4;
	}
	

	private JSeparator getJSeparator1() {
		if(jSeparator1 == null) {
			jSeparator1 = new JSeparator();
			jSeparator1.setPreferredSize(new java.awt.Dimension(167, 1));
		}
		return jSeparator1;
	}
	
	private JSeparator getJSeparator2() {
		if(jSeparator2 == null) {
			jSeparator2 = new JSeparator();
			jSeparator2.setPreferredSize(new java.awt.Dimension(167, 4));
		}
		return jSeparator2;
	}
	
	private JLabel getJLabel12() {
		if(jLabel12 == null) {
			jLabel12 = new JLabel();
			jLabel12.setText("InputProvider values");
		}
		return jLabel12;
	}
	
	private JSeparator getJSeparator3() {
		if(jSeparator3 == null) {
			jSeparator3 = new JSeparator();
			jSeparator3.setPreferredSize(new java.awt.Dimension(161, 4));
		}
		return jSeparator3;
	}
	
	private JButton getJButton4() {
		if(jButton4 == null) {
			jButton4 = new JButton();
			jButton4.setText("Jump");
			jButton4.setPreferredSize(new java.awt.Dimension(80, 21));
			jButton4.addActionListener(jumpListener);
		}
		return jButton4;
	}

}
