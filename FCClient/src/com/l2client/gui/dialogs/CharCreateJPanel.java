package com.l2client.gui.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import com.l2client.model.l2j.Race;
import com.l2client.model.network.NewCharSummary;

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
//TODO the races, classes etc. are hard wired to some extent.
//TODO look & feel refactoring
public class CharCreateJPanel extends javax.swing.JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1955028459349984551L;
	
	private JLabel jLabel1;
	private JLabel jLabel2;
	private JLabel jLabel3;
	private JLabel jLabel4;
	private JButton jButton1;
	private JButton hairButton;
	private JComboBox classBox;
	private JComboBox genderBox;
	private JComboBox raceBox;
	private JTextField nameField;
	private JButton jButton2;
	private int hair = 0;
	
	private static String FIGHTER = "Warrior";
	private static String WIZARD = "Wizard";
	private static String SOLDIER = "Soldier";
	/**
	* Auto-generated main method to display this 
	* JPanel inside a new JFrame.
	*/
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.getContentPane().add(new CharCreateJPanel());
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	public CharCreateJPanel() {
		super();
		initGUI();
	}
	
	private void initGUI() {
		try {
			this.setPreferredSize(new java.awt.Dimension(220, 265));
			this.setLayout(null);
			{
				jLabel1 = new JLabel();
				this.add(jLabel1);
				jLabel1.setText("Name:");
				jLabel1.setBounds(12, 12, 76, 17);
			}
			{
				jLabel2 = new JLabel();
				this.add(jLabel2);
				jLabel2.setText("Race:");
				jLabel2.setBounds(12, 43, 65, 14);
			}
			{
				jLabel3 = new JLabel();
				this.add(jLabel3);
				jLabel3.setText("Gender:");
				jLabel3.setBounds(12, 76, 60, 14);
			}
			{
				jLabel4 = new JLabel();
				this.add(jLabel4);
				jLabel4.setText("Class:");
				jLabel4.setBounds(12, 109, 65, 14);
			}
			{
				jButton1 = new JButton();
				this.add(jButton1);
				jButton1.setText("Accept");
				jButton1.setBounds(12, 227, 89, 25);
			}
			{
				jButton2 = new JButton();
				this.add(jButton2);
				jButton2.setText("Cancel");
				jButton2.setBounds(120, 227, 88, 25);
			}
			{
				nameField = new JTextField();
				this.add(nameField);
				nameField.setBounds(77, 10, 128, 21);
				nameField.addCaretListener(new CaretListener(){

					@Override
					public void caretUpdate(CaretEvent e) {
						if(nameField.getText().length()>0)
							jButton1.setEnabled(true);
						else
							jButton1.setEnabled(false);
					}});
			}
			{
				
				ComboBoxModel raceBoxModel = 
					new DefaultComboBoxModel(Race.getRaces());
				raceBox = new JComboBox();
				this.add(raceBox);
				raceBox.setModel(raceBoxModel);
				raceBox.setBounds(77, 40, 128, 21);
				
				raceBox.addItemListener(new ItemListener(){

					@Override
					public void itemStateChanged(ItemEvent e) {
						Race r = Race.valueOf(e.getItem().toString());
						switch(r.getStartClass()){
						case 1: classBox.setModel(new DefaultComboBoxModel(
								new String[] { FIGHTER }));break;
						case 3: classBox.setModel(new DefaultComboBoxModel(
									new String[] { FIGHTER, WIZARD }));break;
						case 12: classBox.setModel(new DefaultComboBoxModel(
								new String[] { SOLDIER }));break;
						default:classBox.setModel(new DefaultComboBoxModel(
							new String[] { FIGHTER, WIZARD }));
						}
					}
					
				});
			}
			{
				ComboBoxModel genderBoxModel = 
					new DefaultComboBoxModel(
							new String[] { "Male", "Female" });
				genderBox = new JComboBox();
				this.add(genderBox);
				genderBox.setModel(genderBoxModel);
//				genderBox.setBounds(77, 131, 128, 20);
				genderBox.setBounds(77, 73, 126, 21);
			}
			{
				ComboBoxModel classBoxModel = 
					new DefaultComboBoxModel(
							new String[] { "" });
				classBox = new JComboBox();
				this.add(classBox);
				classBox.setModel(classBoxModel);
				classBox.setBounds(77, 106, 126, 21);
			}
			{
				hairButton = new JButton();
				hairButton.setText("change hairstyle");
				hairButton.setBounds(12, 141, 191, 24);
				this.add(hairButton);
				hairButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						hair++;
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addCreateActionListener(ActionListener l) {
		this.jButton1.addActionListener(l);
	}

	public void addCancelActionListener(ActionListener l) {
		this.jButton2.addActionListener(l);
	}

	public NewCharSummary getNewCharSummary(){
		NewCharSummary ch = new NewCharSummary();
		ch.name = nameField.getText();
		ch.race = raceBox.getSelectedIndex();
		ch.sex = genderBox.getSelectedIndex();
		ch.classId = fixClassID(Race.values()[ch.race], classBox.getSelectedItem().toString());
		ch.hair = hair;
		return ch;
	}

	public void addModelchangedListener(final ActionListener l) {
		this.nameField.addActionListener(l);
		//FIXME the name field should only update the name of the new char, not the whole vis
		this.nameField.addCaretListener(new CaretListener(){

			@Override
			public void caretUpdate(CaretEvent e) {
				l.actionPerformed(new ActionEvent(e.getSource(), -1, e.toString()));
			}}
		);
		this.genderBox.addActionListener(l);
		this.raceBox.addActionListener(l);
		this.genderBox.addActionListener(l);
		this.classBox.addActionListener(l);
		this.hairButton.addActionListener(l);
	}
	
	private int fixClassID(Race r, String classString){
		switch(r){
		case Human:
			if(FIGHTER.equals(classString))
				return 0x0;
			else if(WIZARD.equals(classString))
				return 0x0a;
		case Elf:
			if(FIGHTER.equals(classString))
				return 0x12;
			else if(WIZARD.equals(classString))
				return 0x19;
		case DarkElf:
			if(FIGHTER.equals(classString))
				return 0x1f;
			else if(WIZARD.equals(classString))
				return 0x26;
		case Orc:
			if(FIGHTER.equals(classString))
				return 0x2c;
			else if(WIZARD.equals(classString))
				return 0x31;
		case Dwarf:
			if(FIGHTER.equals(classString))
				return 0x35;
		case Kamael:
			if(FIGHTER.equals(classString))
				return 0x7b;
			else if(WIZARD.equals(classString))
				return 0x7c;
		}
		return -1;
	}
	
	public void afterDisplayInit(){
		//TODO check this
		raceBox.setSelectedIndex(0);
		genderBox.setSelectedIndex(0);
		classBox.setSelectedIndex(0);
	}
}
