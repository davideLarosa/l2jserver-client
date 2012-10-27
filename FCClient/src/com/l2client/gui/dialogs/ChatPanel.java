package com.l2client.gui.dialogs;

import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class ChatPanel extends javax.swing.JPanel {

	{
		//Set Look & Feel
		try {
			javax.swing.UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}


	private static final long serialVersionUID = 1L;
	private JTextField chatEnter;
	private JTextPane textAlliance;
	private JTextPane textClan;
	private JTextPane textTrade;
	private JScrollPane tabClan;
	private JTextPane textParty;
	private JScrollPane tabParty;
	private JScrollPane tabTrade;
	private JTextPane textAll;
	private JScrollPane tabAll;
	private JScrollPane tabAlliance;
	private JTabbedPane jTabbedPane1;
	private JLabel jLabel1;

	/**
	 * Auto-generated main method to display this JPanel inside a new JFrame.
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setUndecorated(true);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		final ChatPanel chatPanel_IL = new ChatPanel();
		frame.getContentPane().add(chatPanel_IL);
		frame.pack();
		frame.setVisible(true);
	}

	public ChatPanel() {
		super();
		initGUI();
	}

	private void initGUI() {
		try {
			this.setPreferredSize(new java.awt.Dimension(300, 200));
			this.setOpaque(false);
			this.setLayout(null);
			{
				jLabel1 = new JLabel();
				this.add(jLabel1);
				jLabel1.setText("Message:");
				jLabel1.setBounds(0, 180, 65, 20);
			}
			{
				chatEnter = new JTextField();
				this.add(chatEnter);
				chatEnter.setText("enter your text here");
				chatEnter.setBounds(62, 180, 239, 21);
			}
			{
				jTabbedPane1 = new JTabbedPane();
				this.add(jTabbedPane1);
				jTabbedPane1.setBounds(0, 0, 300, 174);
				{
					tabAll = new JScrollPane();
					jTabbedPane1.addTab("All", null, tabAll, null);
					{
						textAll = new JTextPane();
						tabAll.setViewportView(textAll);
						textAll.setEditable(false);
						// textAll.setFocusable(false);
						textAll.setForeground(new java.awt.Color(0,0,0));
						textAll
								.setBackground(new java.awt.Color(192, 192, 192));
					}
				}
				{
					tabTrade = new JScrollPane();
					jTabbedPane1.addTab("+ Trade", null, tabTrade, null);
					{
						textTrade = new JTextPane();
						tabTrade.setViewportView(textTrade);
						textTrade.setEditable(false);
						textTrade.setBackground(new java.awt.Color(192, 192,
								192));
						textTrade.setForeground(new java.awt.Color(255, 128,
								255));
						// textTrade.setFocusable(false);
					}
				}
				{
					tabParty = new JScrollPane();
					jTabbedPane1.addTab("# Party", null, tabParty, null);
					{
						textParty = new JTextPane();
						tabParty.setViewportView(textParty);
						// textParty.setFocusable(false);
						textParty.setBackground(new java.awt.Color(192, 192,
								192));
						textParty.setForeground(new java.awt.Color(0, 255, 0));
						textParty.setEditable(false);
					}
				}
				{
					tabClan = new JScrollPane();
					jTabbedPane1.addTab("@ Clan", null, tabClan, null);
					{
						textClan = new JTextPane();
						tabClan.setViewportView(textClan);
						textClan
								.setBackground(new java.awt.Color(192, 192, 192));
						textClan.setForeground(new java.awt.Color(0, 0, 255));
						textClan.setEditable(false);
						// textClan.setFocusable(false);
					}
				}
				{
					tabAlliance = new JScrollPane();
					jTabbedPane1.addTab("$ Alliance", null, tabAlliance, null);
					{
						textAlliance = new JTextPane();
						tabAlliance.setViewportView(textAlliance);
						textAlliance.setForeground(new java.awt.Color(128, 255,
								128));
						textAlliance.setBackground(new java.awt.Color(192, 192,
								192));
						// textAlliance.setFocusable(false);
						textAlliance.setEditable(false);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void clearAllTexts() {
		textAll.setText("type and hit Enter to send..");
		textTrade.setText("");
		textParty.setText("");
		textClan.setText("");
		textAlliance.setText("");
	}

	public void addTextAll(String te) {
		StringBuilder bu = new StringBuilder(te);
		bu.append('\n');
		bu.append(textAll.getText());
		bu.setLength(1024);
		bu.append("...");
		textAll.setText(bu.toString());
	}

	public void addTextTrade(String te) {
		StringBuilder bu = new StringBuilder(te);
		bu.append('\n');
		bu.append(textTrade.getText());
		bu.setLength(1024);
		bu.append("...");
		textTrade.setText(bu.toString());
	}

	public void addTextClan(String te) {
		StringBuilder bu = new StringBuilder(te);
		bu.append('\n');
		bu.append(textClan.getText());
		bu.setLength(1024);
		bu.append("...");
		textClan.setText(bu.toString());
	}

	public void addTextParty(String te) {
		StringBuilder bu = new StringBuilder(te);
		bu.append('\n');
		bu.append(textParty.getText());
		bu.setLength(1024);
		bu.append("...");
		textParty.setText(bu.toString());
	}

	public void addTextAlliance(String te) {
		StringBuilder bu = new StringBuilder(te);
		bu.append('\n');
		bu.append(textAlliance.getText());
		bu.setLength(1024);
		bu.append("...");
		textAlliance.setText(bu.toString());
	}

	public void addChatListener(KeyListener l) {
		this.chatEnter.addKeyListener(l);
	}

	public String getChatMessage() {
		String s = this.chatEnter.getText();
		this.chatEnter.setText("");
		return s;
	}
}
