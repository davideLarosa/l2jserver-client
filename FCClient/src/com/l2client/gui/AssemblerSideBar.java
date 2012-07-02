package com.l2client.gui;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import com.jme3.animation.Skeleton;
import com.jme3.export.Savable;
import com.jme3.export.binary.BinaryImporter;
import com.jme3.scene.Geometry;
import com.l2client.app.Assembler;

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
public class AssemblerSideBar extends javax.swing.JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton jButton1;
	private JScrollPane jScrollPane1;
	private JScrollPane jScrollPane2;
	private JScrollPane jScrollPane3;
	private JList animations;
	private JLabel jLabel1;
	private JTextField jTextField1;
	private JSeparator jSeparator3;
	private JButton jButton5;
	private JButton jButton4;
	private JSeparator jSeparator2;
	private JList meshes;
	private JLabel jLabel3;
	private JButton jButton3;
	private JButton jButton2;
	private JSeparator jSeparator1;
	private JList bones;
	private JLabel jLabel2;
	private Assembler assembler;
	private DefaultComboBoxModel meshModel = new DefaultComboBoxModel(
			new String[] { });
	private DefaultComboBoxModel animModel = new DefaultComboBoxModel(
			new String[] { });
	private File lastPath;
//	private InputHandler inputHandler;


	/**
	* Auto-generated main method to display this 
	* JPanel inside a new JFrame.
	*/
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.getContentPane().add(new AssemblerSideBar());
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	public AssemblerSideBar() {
		super();
		initGUI();
		assembler = new Assembler();
	}
	
	public AssemblerSideBar(Assembler as) {
		this();
		if(as != null)
			assembler = as;
	}
	
	private void initGUI() {
		try {
			setPreferredSize(new Dimension(245, 600));
			this.setLayout(null);
			final Component parent = this;
			{
				jTextField1 = new JTextField();
				this.add(jTextField1);
				jTextField1.setText("please load a skelton first");
				jTextField1.setEditable(false);
				jTextField1.setBounds(12, 49, 217, 21);
			}
			{
				jSeparator1 = new JSeparator();
				this.add(jSeparator1);
				jSeparator1.setBounds(12, 171, 219, 10);
			}
			{				
				bones = new JList();
				this.add(bones);
				bones.setModel(new DefaultComboBoxModel(new String[] {}));
//				bones.setBounds(12, 96, 219, 63);
				jScrollPane1 = new JScrollPane(bones);
				jScrollPane1.setBounds(12, 96, 219, 63);
				this.add(jScrollPane1);
			}
			{
				jLabel2 = new JLabel();
				this.add(jLabel2);
				jLabel2.setText("Bones:");
				jLabel2.setBounds(12, 76, 47, 14);
			}
			{
				jButton1 = new JButton();
				this.add(jButton1);
				jButton1.setText("load skeleton");
				jButton1.setBounds(12, 19, 217, 25);
				jButton1.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
					    JFileChooser chooser = new JFileChooser();
					    if(lastPath != null)
					    	chooser.setCurrentDirectory(lastPath);
					    int returnVal = chooser.showOpenDialog(parent);
					    if(returnVal == JFileChooser.APPROVE_OPTION) {
					    	lastPath = chooser.getCurrentDirectory();
					    	onSkeletonChanged(chooser.getSelectedFile());
					    }

					}
				});
			}
			{
				jButton2 = new JButton();
				this.add(jButton2);
				jButton2.setText("add mesh");
				jButton2.setBounds(12, 187, 221, 27);
				jButton2.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
					    JFileChooser chooser = new JFileChooser();
					    if(lastPath != null)
					    	chooser.setCurrentDirectory(lastPath);
					    int returnVal = chooser.showOpenDialog(parent);
					    if(returnVal == JFileChooser.APPROVE_OPTION) {
					    	lastPath = chooser.getCurrentDirectory();
					    	onMeshAdded(chooser.getSelectedFile());
					    }

					}
				});
			}
			{
				jButton3 = new JButton();
				this.add(jButton3);
				jButton3.setText("remove mesh");
				jButton3.setBounds(12, 219, 221, 26);
				jButton3.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
					    onMeshRemoved();
					}
				});
			}
			{
				jLabel3 = new JLabel();
				this.add(jLabel3);
				jLabel3.setText("Meshes:");
				jLabel3.setBounds(12, 250, 53, 14);
			}
			{
				meshes = new JList();
				this.add(meshes);
				meshes.setModel(meshModel);
//				Meshes.setBounds(12, 270, 219, 63);
				jScrollPane2 = new JScrollPane(meshes);
				jScrollPane2.setBounds(12, 270, 219, 63);
				this.add(jScrollPane2);
			}
			{
				jSeparator2 = new JSeparator();
				this.add(jSeparator2);
				jSeparator2.setBounds(12, 389, 218, 6);
			}
			{
				jButton4 = new JButton();
				this.add(jButton4);
				jButton4.setText("add animation");
				jButton4.setBounds(12, 401, 221, 26);
				jButton4.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
					    JFileChooser chooser = new JFileChooser();
					    if(lastPath != null)
					    	chooser.setCurrentDirectory(lastPath);
					    int returnVal = chooser.showOpenDialog(parent);
					    if(returnVal == JFileChooser.APPROVE_OPTION) {
					    	lastPath = chooser.getCurrentDirectory();
					    	onAnimationAdded(chooser.getSelectedFile());
					    }

					}
				});
			}
			{
				jButton5 = new JButton();
				this.add(jButton5);
				jButton5.setText("remove animation");
				jButton5.setBounds(12, 432, 221, 26);
				jButton5.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
					   onAnimationRemoved();
					}
				});
			}
			{
				animations = new JList();
				this.add(animations);
				animations.setModel(animModel);
				animations.setPreferredSize(new java.awt.Dimension(216, 60));
//				jList1.setBounds(12, 438, 219, 63);
				jScrollPane3 = new JScrollPane(animations);
				jScrollPane3.setBounds(14, 483, 219, 63);
				this.add(jScrollPane3);
			}
			{
				jSeparator3 = new JSeparator();
				this.add(jSeparator3);
				jSeparator3.setBounds(14, 556, 221, 8);
			}
			{
				jLabel1 = new JLabel();
				this.add(jLabel1);
				jLabel1.setText("Animations:");
				jLabel1.setBounds(12, 463, 89, 14);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void onAnimationAdded(File selectedFile) {
//we do no longer support this
	}

	protected void onAnimationRemoved() {
		//we do no longer support this
	}

	protected void onMeshRemoved() {
		String sel = (String) meshes.getSelectedValue();
		if(sel != null){
			assembler.removeMesh(sel);
			meshModel.removeElement(sel);
			updateJME();
		}
	}

	protected void onMeshAdded(File selectedFile) {
		//check if the file is a jme OgreMesh file by trying to load it
		try {
			Savable load = BinaryImporter.getInstance().load(selectedFile);
			
			if(load instanceof Geometry)
			{
				Geometry m = (Geometry) load;
				assembler.addMesh(m.getName(),m, true);
				meshModel.addElement(m.getName());
				updateJME();
			} else {
				JOptionPane.showMessageDialog(null, "The jme file is not an OgreMesh but a "+(load != null?load.getClass().getSimpleName():"Null file"),"Error",JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Failed to load the file, perhaps not a JME file?","Error",JOptionPane.ERROR_MESSAGE);
		}
	}

	private void onSkeletonChanged(File newFile){
		//check if the file is a jme skeleton file by trying to load it
		try {
			Savable load = BinaryImporter.getInstance().load(newFile);
			
			if(load instanceof Skeleton)
			{
				Skeleton skel = (Skeleton)load;
				assembler.setSkeleton(skel);
				this.jTextField1.setText(newFile.getPath().substring(0,8)+"..."+newFile.getName());
				String[] names = new String[skel.getBoneCount()];
				for(int i=0;i<skel.getBoneCount();i++){
					names[i]= skel.getBone(i).getName();
				}
				bones.setModel(new DefaultComboBoxModel(names));
				updateJME();
			} else {

				JOptionPane.showMessageDialog(null, "The jme file is not a Skeleton but a "+(load != null?load.getClass().getSimpleName():"Null file"),"Error",JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Failed to load the file, perhaps not a JME file?","Error",JOptionPane.ERROR_MESSAGE);
		}
	}

	private void updateJME() {
		// TODO Auto-generated method stub
		
	}
}
