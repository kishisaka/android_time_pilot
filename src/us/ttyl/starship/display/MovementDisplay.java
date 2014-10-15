package us.ttyl.starship.display;

//import java.awt.Button;
//import java.awt.Component;
//import java.awt.Frame;
//import java.awt.GridBagConstraints;
//import java.awt.GridBagLayout;
//import java.awt.Label;
//import java.awt.TextArea;
//import java.awt.TextField;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.WindowAdapter;
//import java.awt.event.WindowEvent;
import java.util.Hashtable;

import us.ttyl.starship.core.GameState;
import us.ttyl.starship.movement.MovementEngine;

public class MovementDisplay // extends Frame
{

	/*
  private static final long serialVersionUID = 1L;
  Hashtable <String,Component> _devices = new Hashtable<String,Component>();
  MapDisplay _md;

  public MovementDisplay(MapDisplay md)
  {
	 _md = md;
	 
    // build screen
    setTitle("controls");
    Button b = null;
    GridBagLayout layout = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();

    c.fill = GridBagConstraints.BOTH;
    c.gridwidth = 1;
    c.gridheight = 3;
    c.weighty = 1.0;
    makeLabel("Track", layout, c);
    makeTextBox(4, 50, layout, c, "track box");

    c.weighty = 0.0;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.gridheight = 1;
    makeButton("Increase Scale", layout, c);
    makeButton("Decrease Scale", layout, c);
    makeButton("Change Target", layout, c);

    c.weightx = 0.0;
    makeTextLine(50, layout, c, "command line");
    setLayout(layout);

    // add listeners
    addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent e)
      {
        System.exit(0);
      }
    });
    
    b = (Button)_devices.get("Increase Scale");
    b.addActionListener(new ActionListener() 
    {
      public void actionPerformed(ActionEvent e)
      {
        _md.increaseScale();
        TextField t = (TextField)_devices.get("command line");
        t.setText("increase scale: " + _md._scale);
      }
    });
    
    b = (Button)_devices.get("Decrease Scale");
    b.addActionListener(new ActionListener() 
    {
      public void actionPerformed(ActionEvent e)
      {
        _md.decreaseScale();
        TextField t = (TextField)_devices.get("command line");
        t.setText("decrease scale: " + _md._scale);
      }
    });
    
    b = (Button)_devices.get("Change Target");
    b.addActionListener(new ActionListener() 
    {
      public void actionPerformed(ActionEvent e)
      {
        _md.changeTarget();
        TextField t = (TextField)_devices.get("command line");
        MovementEngine mei = (MovementEngine)GameState._weapons.elementAt(_md._selected);
        t.setText("target change: " + mei.getWeaponName());
      }
    });

    // show console
    setSize(200,300);
    pack();
    setVisible(true);
  }
  
  public void setMapDisplay(MapDisplay md)
  {
    _md = md;
  }

  public void makeLabel(String label, GridBagLayout gridbag, GridBagConstraints c)
  {
    Label l = new Label(label);
    gridbag.setConstraints(l, c);
    add(l);
  }

  public void makeTextBox(int columns, int rows, GridBagLayout gridbag, GridBagConstraints c, String name)
  {
    TextArea area = new TextArea(columns, rows);
    gridbag.setConstraints(area, c);
    add(area);
    _devices.put(name, area);
  }

  public void makeTextLine(int columns, GridBagLayout gridbag, GridBagConstraints c, String name)
  {
    TextField field = new TextField(columns);
    gridbag.setConstraints(field, c);
    add(field);
    _devices.put(name, field);
  }

  public void makeButton(String name, GridBagLayout gridbag, GridBagConstraints c)
  {
    Button button = new Button(name);
    gridbag.setConstraints(button, c);
    add(button);
    _devices.put(name, button);
  }

  public void updateDisplay()
  {
    StringBuffer sb = new StringBuffer();
    TextArea area = (TextArea)_devices.get("track box");
    for (int i = 0; i < GameState._weapons.size(); i ++)
    {
      MovementEngine me = (MovementEngine)GameState._weapons.elementAt(i);
      sb.append("Name: " + me.getWeaponName() + " Speed: " + me.getCurrentSpeed() + " Coord X: " + Math.round(me.getX()) + " Coord Y: " + Math.round(me.getY()) + " Course: " + me.getCurrentDirection() + "\n");
    }
    area.setText(sb.toString());
  }
  */
}