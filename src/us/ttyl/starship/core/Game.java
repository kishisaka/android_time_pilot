package us.ttyl.starship.core;

import us.ttyl.starship.display.MapDisplay;
import us.ttyl.starship.display.MovementDisplay;
import us.ttyl.starship.movement.FreeEngine;
import us.ttyl.starship.movement.MovementEngine;

/**
 * sprite size is 36x36. 
 * @author test
 *
 */
public class Game extends Thread
{
	/*
	MovementDisplay mMovementDisplay = null;
	MapDisplay mMapDisplay = null;
	MainLoop mMainLoop = null;
	
	public Game()
	{
		init();
		mMainLoop = new MainLoop();		
		mMapDisplay = new MapDisplay();
		mMovementDisplay = new MovementDisplay(mMapDisplay);
		start();
	}
	
	public void init()
	{		
		GameState._sprites = GameUtils.getTilesFromFile();
		MovementEngine player = new FreeEngine(0, 0, 0d, 0d, 2d, 2d, 5, .1d, 0, "player", -1); 
		GameState._weapons.add(player);		 			
	}
	
	public void run()
	{
		while (true)
	    {
	    	try
	      	{
	    		mMovementDisplay.updateDisplay();
	    		mMapDisplay.repaint();
	        	sleep(16);
	      	}
	      	catch (Exception e)
	      	{
	      		
	        	e.printStackTrace();
	      	}
	    }
	}
	
	public static void main(String args[])
	{
		new Game();
	}
	*/
}
