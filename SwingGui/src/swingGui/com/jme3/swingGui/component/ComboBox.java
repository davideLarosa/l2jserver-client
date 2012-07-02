package com.jme3.swingGui.component;

import java.io.*;
import java.applet.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.event.*;
import com.jme3.swingGui.JMEDesktop;

public class ComboBox extends JButton implements ActionListener
{
  ArrayList<Object> items = new ArrayList<Object>();
  ArrayList<ItemListener> iListeners = new ArrayList<ItemListener>();
  Object selectedItem;
 /**
 * This class creates a Component similar to JComboBox.
 * Its useful by problems with JComboBox (Popupmenu closes automatically)
 * @author by user starcom "Paul Kashofer Austria"
 */
  public ComboBox()
  {
    super("empty");
    addActionListener(this);
    String dir = this.getClass().getResource("").getPath()+"cb.png";
    ImageIcon pfeil = new ImageIcon(dir);
    if (pfeil!=null)
    {
      setHorizontalTextPosition(JButton.LEFT);
      setIcon(pfeil);
    }
  }

  public Dimension getPreferredSize()
  { // Dont hide the slider.
    Dimension ret = super.getPreferredSize();
    ret.setSize(ret.getWidth(),30);
    return ret;
  }

  public void addItem(Object obj)
  {
    items.add(obj);
    if (selectedItem==null) {setSelectedIndex(0);}
  }
  public void addItemListener(ItemListener i) {iListeners.add(i);}
  public void actionPerformed(ActionEvent e)
  {
    createMenu();
  }
  public Object getSelectedItem() {return selectedItem;}
  public int getSelectedIndex() {return getIndex(selectedItem);}
  public int getIndex(Object obj)
  {
    for (int i=0; i<items.size(); i++)
    {
      if (items.get(i).equals(obj)) {return i;}
    }
    return -1;
  }
  public void setSelectedItem(Object obj)
  {
    setSelectedIndex(getIndex(obj));
  }
  public void setSelectedIndex(int i)
  {
    Object last = selectedItem;
    if (items.get(i)==last) {return;}
    setText(""+items.get(i));
    selectedItem = items.get(i);
    fireItemStateChanged(last,selectedItem);
  }

  void fireItemStateChanged(Object deselect, Object select)
  {
    if (deselect!=null)
    {
      ItemEvent e = new ItemEvent(new JButton(),0,deselect,ItemEvent.DESELECTED);
      for (int i=0; i<iListeners.size(); i++) {iListeners.get(i).itemStateChanged(e);}
    }
    if (select!=null)
    {
      ItemEvent e = new ItemEvent(new JButton(),0,select,ItemEvent.SELECTED);
      for (int i=0; i<iListeners.size(); i++) {iListeners.get(i).itemStateChanged(e);}
    }
  }

  void createMenu()
  {
    PopMenu tmp = new PopMenu(getLocationOnScreen())
    {
      public void selected(int index)
      {
        setSelectedIndex(index);
      }
    };
    for (int i=0; i<items.size(); i++)
    {
      if (items.get(i)==selectedItem) {tmp.addMenuItem(""+items.get(i),true);}
      else {tmp.addMenuItem(""+items.get(i),false);}
    }
//    starcom.jme3d.Referenz.window.add(tmp); // Insert tmp-Frame into JMEDesktop
  }

  abstract class PopMenu extends JInternalFrame implements ActionListener
  {
    int index = 0;
    JPanel main = new JPanel();
    double prefSize = 0;
    public PopMenu(Point loc) {super(); initNow(loc);}

    void initNow(Point loc)
    {
      this.setLocation(loc);
      main.setLayout(new GridLayout(0,1));
      add(new JScrollPane(main));
      setVisible(true);
      addMouseListener(new MouseAdapter()
      {
        @Override
        public void mouseExited(MouseEvent e)
        {
          setVisible(false);
          dispose();
        }
      });
    }

    public void addMenuItem(String txt, boolean selected)
    {
      JButton tmp = new JButton(txt);
      tmp.setActionCommand(""+index);
      tmp.addActionListener(this);
      if (selected) {tmp.setBackground(Color.BLUE.brighter());}
      int thisSize = (int)tmp.getPreferredSize().getWidth();
      if (thisSize>prefSize)
      {
        prefSize=thisSize;
        setSize((int)prefSize+30,300);
      }
      main.add(tmp);
      index++;
    }
    public void actionPerformed(ActionEvent e)
    {
      selected(Integer.parseInt(e.getActionCommand()));
      setVisible(false);
      dispose();
    }
    public abstract void selected(int index);
  }
}