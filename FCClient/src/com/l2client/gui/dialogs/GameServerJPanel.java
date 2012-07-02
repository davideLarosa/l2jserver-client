package com.l2client.gui.dialogs;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.l2client.model.network.GameServerInfo;


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
public class GameServerJPanel extends javax.swing.JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2480776251040617319L;
	private JList serverList;
	private JLabel infoLabel;
	private JScrollPane jScrollPane1;
	private JButton cancelButton;
	private JButton selectButton;
	private JSeparator jSeparator1;

	/**
	* Auto-generated main method to display this 
	* JPanel inside a new JFrame.
	*/
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.getContentPane().add(new GameServerJPanel());
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setSize(400, 350);
		frame.setVisible(true);
		frame.setPreferredSize(new java.awt.Dimension(400, 350));
	}

	private GameServerInfo[] serverInfos;
	private int server;
	
	public GameServerJPanel() {
		super();
		initGUI();
	}
	
	public GameServerJPanel(GameServerInfo [] serverInfos) {
		super();
		this.serverInfos = serverInfos;
		this.server = 0;
		initGUI();
		if(serverInfos == null || serverInfos.length<=0){
			infoLabel.setText("No Gameservers availabe, please check your connection...");
			return;
		}
			
		String[] servers = new String[serverInfos.length];
		for(int i = 0; i< serverInfos.length;i++ ){
			servers[i] = serverInfos[i].toString();
		}
		ListModel serverListModel = 
			new DefaultComboBoxModel(servers);
		serverList.setModel(serverListModel);
	}
	
	
	private void initGUI() {
		try {
			this.setPreferredSize(new java.awt.Dimension(400, 350));
			this.setLayout(null);
			this.setSize(400, 350);	
			{
				ListModel serverListModel = 
					new DefaultComboBoxModel(
							new String[] {});
				serverList = new JList();
				serverList.setModel(serverListModel);
				serverList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

				serverList.addListSelectionListener(new ListSelectionListener(){
					
					@Override
					public void valueChanged(ListSelectionEvent e) {
						if(e != null && e.getSource() != null){
							if(serverList.getSelectedIndex()>-1){
								selectButton.setEnabled(true);
								server = serverList.getSelectedIndex();
								infoLabel.setText("Server ID is "+serverInfos[serverList.getSelectedIndex()].id+" with currently "+serverInfos[serverList.getSelectedIndex()].players+" players online");
							}
						}
						else {
							selectButton.setEnabled(false);
							infoLabel.setText("No Gameservers availabe, please check your connection...");
						}
					}
					
				});
			}		
			{
				jScrollPane1 = new JScrollPane(serverList);
				this.add(jScrollPane1);
				jScrollPane1.setBounds(23, 12, 351, 141);
//				jScrollPane1.setLayout(null);
			}
			{
				jSeparator1 = new JSeparator();
				this.add(jSeparator1);
				jSeparator1.setBounds(23, 171, 351, 10);
			}
			{
				infoLabel = new JLabel();
				this.add(infoLabel);
				infoLabel.setText("Please selct a server for connection...");
				infoLabel.setBounds(23, 181, 351, 67);
			}
			{
				selectButton = new JButton();
				this.add(selectButton);
				selectButton.setText("Connect");
				selectButton.setBounds(37, 266, 100, 31);
				//initially disabled enabled by list selection
				selectButton.setEnabled(false);
			}
			{
				cancelButton = new JButton();
				this.add(cancelButton);
				cancelButton.setText("Cancel");
				cancelButton.setBounds(255, 266, 100, 31);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addCancelActionListener(ActionListener actionListener) {
		cancelButton.addActionListener(actionListener);
	}

	public void addSelectActionListener(ActionListener actionListener) {
		selectButton.addActionListener(actionListener);
		
	}

	public final int getSelectedServer() {
		return server;
	}

}
