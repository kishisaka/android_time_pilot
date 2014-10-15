package us.ttyl.starship.env;

import us.ttyl.starship.core.GameState;
import us.ttyl.starship.movement.CircleEngine;
import us.ttyl.starship.movement.LineEngine;
import us.ttyl.starship.movement.MovementEngine;

public class EnvBuilder 
{
	private static void generateShip(double planetX, double planetY, int turnmode, int speed)
	{
		/*
			 public CircleEngine(
					   	int direction, 
					   	int currentDirection, 
					   	double currentX, 
					   	double currentY, 
					   	double currentSpeed, 
					   	double maxSpeed, 
					   	double acceleration, 
					   	double turnMode,
						String name, 
						int endurance)
					   {
		*/
		GameState._weapons.add(new CircleEngine(0, 0, planetX, planetY, 1
				, 1, 1, turnmode, ("enemy"),-1));		
	}
	
	public static void generateEnemy(double playerPositionX, double playerPositionY)
	{
		// determine the track
		int track = (int)(Math.random() * 359);
		
		
		
		// the enemy
		int offsetX = 300; 
		int offsetY = 300;
		if ((int)(Math.random() * 100) < 50)
		{
			offsetX = offsetX * -1;
			offsetX = offsetY * -1;
		}
		generateShip(playerPositionX + offsetX, playerPositionY + offsetY, 0, 10);
	}
	
	public static void generateCloud(double playerPositionX, double playerPositionY, int playerTrack)
	{
		/*
		int direction, 
	  	int currentDirection, 
	  	double currentX, 
	  	double currentY, 
	  	double currentSpeed, 
	  	double maxSpeed, 
	  	double acceleration, 
	  	int turnMode, 
	  	String name,
	  	MovementEngine origin, 
	  	int endurance)
	  	*/
		// the cloud
		double offsetX = 250;		
		double offsetY = (playerPositionY - 250) + (int)(Math.random() * 500);
		if (playerTrack > 180 && playerTrack < 359)
		{
			offsetX = -250;
		}
		GameState._weapons.add(new LineEngine(180, 180, offsetX + playerPositionX
				, offsetY+playerPositionY, 1d
				, .1d, .1d, 0, "cloud", null, -1));	
	}
}
