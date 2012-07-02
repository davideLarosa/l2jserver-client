package com.l2client.gui.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class ShortCutMultiPanel extends JPanel {

	private ShortCutPanel top;
	private ShortCutPanel middle;
	private ShortCutPanel bottom;
	private JButton topClose;
	private JButton middleClose;

	//
	public ShortCutMultiPanel(){
		top = new ShortCutPanel();
		middle = new ShortCutPanel();
		bottom = new ShortCutPanel();
		doMyLayout();
	}
	
	protected void doMyLayout(){
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(top);
		add(middle);
		add(bottom);
		addVisibilityButtons();
		resetVisibility();
	}

	private void addVisibilityButtons() {
		top.addExpandAction(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				resetVisibility();
			}
		});
		
		middle.addExpandAction(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				resetVisibility();
			}
		});
	}

	private void resetVisibility() {
				
	}
	
}
