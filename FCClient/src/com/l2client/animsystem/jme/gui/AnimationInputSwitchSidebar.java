package com.l2client.animsystem.jme.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;

import com.jme3.scene.control.AbstractControl;
import com.l2client.animsystem.InputProvider;
import com.l2client.animsystem.jme.JMEAnimationController;
import com.l2client.animsystem.jme.input.Acting;
import com.l2client.animsystem.jme.input.AttackResult;
import com.l2client.animsystem.jme.input.AttackVector;
import com.l2client.animsystem.jme.input.Direction;
import com.l2client.animsystem.jme.input.Enemy;
import com.l2client.animsystem.jme.input.Hurt;
import com.l2client.animsystem.jme.input.HurtVector;
import com.l2client.animsystem.jme.input.Morale;
import com.l2client.animsystem.jme.input.Speed;
import com.l2client.animsystem.jme.input.Target;
import com.l2client.animsystem.jme.input.Weapon;

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
	private JComboBox jComboBox10;
	private JComboBox jComboBox11;
	private JTextPane jTextPane1;
	private JSeparator jSeparator1;
	private JLabel jLabel11;
	private JLabel jLabel10;
	private JLabel jLabel9;
	private JLabel jLabel8;
	private JLabel jLabel7;
	private JLabel jLabel6;
	private JLabel jLabel5;
	private JComboBox jComboBox9;
	private JComboBox jComboBox8;
	private JComboBox jComboBox7;
	private JComboBox jComboBox6;
	private JComboBox jComboBox5;
	private JComboBox jComboBox4;
	private JComboBox jComboBox3;
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
			target.callAction("Wounded", getInputFromModel());
		}
	};
	private ActionListener defendListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(target != null)
			target.callAction("Defend", getInputFromModel());
		}
	};
	private ActionListener attackListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(target != null)
			target.callAction("DefaultAttack", getInputFromModel());
		}
	};
	private JLabel jLabel4;
	private JLabel jLabel3;
	private JLabel jLabel2;
	private JLabel jLabel1;
	private JButton jButton5;
	private JButton jButton4;
	private JButton jButton3;
	private JButton jButton2;
	private JButton jButton1;
	private JLabel jLabel12;
	private JSeparator jSeparator2;
	private JMEAnimationController target = null;

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
			this.setPreferredSize(new java.awt.Dimension(180, 600));
			{
				ComboBoxModel jComboBox1Model = 
					new DefaultComboBoxModel(
							Acting.values());
				jComboBox1 = new JComboBox();
				this.add(getJSeparator2());
				this.add(getJLabel12());
				this.add(getJLabel1());
				this.add(jComboBox1);
				this.add(getJLabel2());
				jComboBox1.setModel(jComboBox1Model);
				jComboBox1.setPreferredSize(new java.awt.Dimension(80, 21));
				jComboBox1.addActionListener(listener);
			}
			{
				ComboBoxModel jComboBox2Model = 
					new DefaultComboBoxModel(
							AttackResult.values());
				jComboBox2 = new JComboBox();
				this.add(jComboBox2);
				this.add(getJLabel3());
				jComboBox2.setModel(jComboBox2Model);
				jComboBox2.setPreferredSize(new java.awt.Dimension(80, 21));
				jComboBox2.addActionListener(listener);
			}
			{
				ComboBoxModel jComboBox3Model = 
					new DefaultComboBoxModel(
							AttackVector.values());
				jComboBox3 = new JComboBox();
				this.add(jComboBox3);
				this.add(getJLabel4());
				jComboBox3.setModel(jComboBox3Model);
				jComboBox3.setPreferredSize(new java.awt.Dimension(80, 21));
				jComboBox3.addActionListener(listener);
			}
			{
				ComboBoxModel jComboBox4Model = 
					new DefaultComboBoxModel(
							Direction.values());
				jComboBox4 = new JComboBox();
				this.add(jComboBox4);
				this.add(getJLabel5());
				jComboBox4.setModel(jComboBox4Model);
				jComboBox4.setPreferredSize(new java.awt.Dimension(80, 21));
				jComboBox4.addActionListener(listener);
			}
			{
				ComboBoxModel jComboBox5Model = 
					new DefaultComboBoxModel(
							Enemy.values());
				jComboBox5 = new JComboBox();
				this.add(jComboBox5);
				this.add(getJLabel6());
				jComboBox5.setModel(jComboBox5Model);
				jComboBox5.setPreferredSize(new java.awt.Dimension(80, 21));
				jComboBox5.addActionListener(listener);
			}
			{
				ComboBoxModel jComboBox6Model = 
					new DefaultComboBoxModel(
							Hurt.values());
				jComboBox6 = new JComboBox();
				this.add(jComboBox6);
				this.add(getJLabel7());
				jComboBox6.setModel(jComboBox6Model);
				jComboBox6.setPreferredSize(new java.awt.Dimension(80, 21));
				jComboBox6.addActionListener(listener);
			}
			{
				ComboBoxModel jComboBox7Model = 
					new DefaultComboBoxModel(
							HurtVector.values());
				jComboBox7 = new JComboBox();
				this.add(jComboBox7);
				this.add(getJLabel8());
				jComboBox7.setModel(jComboBox7Model);
				jComboBox7.setPreferredSize(new java.awt.Dimension(80, 21));
				jComboBox7.addActionListener(listener);
			}
			{
				ComboBoxModel jComboBox8Model = 
					new DefaultComboBoxModel(
							Morale.values());
				jComboBox8 = new JComboBox();
				this.add(jComboBox8);
				this.add(getJLabel9());
				jComboBox8.setModel(jComboBox8Model);
				jComboBox8.setPreferredSize(new java.awt.Dimension(80, 21));
				jComboBox8.addActionListener(listener);
			}
			{
				ComboBoxModel jComboBox9Model = 
					new DefaultComboBoxModel(
							Speed.values());
				jComboBox9 = new JComboBox();
				this.add(jComboBox9);
				this.add(getJLabel10());
				jComboBox9.setModel(jComboBox9Model);
				jComboBox9.setPreferredSize(new java.awt.Dimension(80, 21));
				jComboBox9.addActionListener(listener);
			}
			{
				ComboBoxModel jComboBox10Model = 
					new DefaultComboBoxModel(
							Target.values());
				jComboBox10 = new JComboBox();
				this.add(jComboBox10);
				this.add(getJLabel11());
				jComboBox10.setModel(jComboBox10Model);
				jComboBox10.setPreferredSize(new java.awt.Dimension(80, 21));
				jComboBox10.addActionListener(listener);
			}
			{
				ComboBoxModel jComboBox11Model = 
					new DefaultComboBoxModel(
							Weapon.values());
				jComboBox11 = new JComboBox();
				this.add(jComboBox11);
				this.add(getJSeparator1());
				this.add(getJTextPane1());
				jComboBox11.setModel(jComboBox11Model);
				jComboBox11.setPreferredSize(new java.awt.Dimension(80, 21));
				jComboBox11.addActionListener(listener);
			}
			{
				this.add(getJButton1());
				this.add(getJButton2());
				this.add(getJButton3());
				this.add(getJButton4());
				this.add(getJButton5());
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
		if(con instanceof JMEAnimationController){
			target = (JMEAnimationController)con;
		}
	}
	
	private InputProvider getInputFromModel(){
		InputProvider in = new InputProvider();
		in.setInput((Acting) jComboBox1.getSelectedItem());
		in.setInput((AttackResult) jComboBox2.getSelectedItem());
		in.setInput((AttackVector) jComboBox3.getSelectedItem());
		in.setInput((Direction) jComboBox4.getSelectedItem());
		in.setInput((Enemy) jComboBox5.getSelectedItem());
		in.setInput((Hurt) jComboBox6.getSelectedItem());
		in.setInput((HurtVector) jComboBox7.getSelectedItem());
		in.setInput((Morale) jComboBox8.getSelectedItem());
		in.setInput((Speed) jComboBox9.getSelectedItem());
		in.setInput((Target) jComboBox10.getSelectedItem());
		in.setInput((Weapon) jComboBox11.getSelectedItem());
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
	
	private JButton getJButton4() {
		if(jButton4 == null) {
			jButton4 = new JButton();
			jButton4.setText("Taunt");
			jButton4.setPreferredSize(new java.awt.Dimension(80, 21));
			jButton4.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if(target != null)
					target.callAction("Taunt", InputProvider.NOINPUT);
				}
			});
		}
		return jButton4;
	}
	
	private JButton getJButton5() {
		if(jButton5 == null) {
			jButton5 = new JButton();
			jButton5.setText("Celebrate");
			jButton5.setPreferredSize(new java.awt.Dimension(80, 21));
			jButton5.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if(target != null)
					target.callAction("Celebrate", InputProvider.NOINPUT);
				}
			});
		}
		return jButton5;
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
			jLabel2.setText("AttackResult:");
			jLabel2.setPreferredSize(new java.awt.Dimension(80, 14));
		}
		return jLabel2;
	}
	
	private JLabel getJLabel3() {
		if(jLabel3 == null) {
			jLabel3 = new JLabel();
			jLabel3.setText("AttackVector:");
			jLabel3.setPreferredSize(new java.awt.Dimension(80, 14));
		}
		return jLabel3;
	}
	
	private JLabel getJLabel4() {
		if(jLabel4 == null) {
			jLabel4 = new JLabel();
			jLabel4.setText("Direction:");
			jLabel4.setPreferredSize(new java.awt.Dimension(80, 14));
		}
		return jLabel4;
	}
	
	private JLabel getJLabel5() {
		if(jLabel5 == null) {
			jLabel5 = new JLabel();
			jLabel5.setText("Enemy:");
			jLabel5.setPreferredSize(new java.awt.Dimension(80, 14));
		}
		return jLabel5;
	}
	
	private JLabel getJLabel6() {
		if(jLabel6 == null) {
			jLabel6 = new JLabel();
			jLabel6.setText("Hurt:");
			jLabel6.setPreferredSize(new java.awt.Dimension(80, 14));
		}
		return jLabel6;
	}
	
	private JLabel getJLabel7() {
		if(jLabel7 == null) {
			jLabel7 = new JLabel();
			jLabel7.setText("HurtVector:");
			jLabel7.setPreferredSize(new java.awt.Dimension(80, 14));
		}
		return jLabel7;
	}
	
	private JLabel getJLabel8() {
		if(jLabel8 == null) {
			jLabel8 = new JLabel();
			jLabel8.setText("Morale:");
			jLabel8.setPreferredSize(new java.awt.Dimension(80, 14));
		}
		return jLabel8;
	}
	
	private JLabel getJLabel9() {
		if(jLabel9 == null) {
			jLabel9 = new JLabel();
			jLabel9.setText("Speed:");
			jLabel9.setPreferredSize(new java.awt.Dimension(80, 14));
		}
		return jLabel9;
	}
	
	private JLabel getJLabel10() {
		if(jLabel10 == null) {
			jLabel10 = new JLabel();
			jLabel10.setText("Target:");
			jLabel10.setPreferredSize(new java.awt.Dimension(80, 14));
		}
		return jLabel10;
	}
	
	private JLabel getJLabel11() {
		if(jLabel11 == null) {
			jLabel11 = new JLabel();
			jLabel11.setText("Weapon:");
			jLabel11.setPreferredSize(new java.awt.Dimension(80, 14));
		}
		return jLabel11;
	}
	
	private JSeparator getJSeparator1() {
		if(jSeparator1 == null) {
			jSeparator1 = new JSeparator();
			jSeparator1.setPreferredSize(new java.awt.Dimension(167, 1));
		}
		return jSeparator1;
	}
	
	private JTextPane getJTextPane1() {
		if(jTextPane1 == null) {
			jTextPane1 = new JTextPane();
			jTextPane1.setText("OneShot Actions, set Input above accordingly");
			jTextPane1.setPreferredSize(new java.awt.Dimension(167, 51));
			jTextPane1.setEditable(false);
			jTextPane1.setOpaque(false);
		}
		return jTextPane1;
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
}
