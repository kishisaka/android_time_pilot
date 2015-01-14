package us.ttyl.starship.env;

import us.ttyl.starship.core.Constants;
import us.ttyl.starship.core.GameState;
import us.ttyl.starship.core.GameUtils;
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
		MovementEngine circleEngine = new CircleEngine(0, 0, planetX, planetY, 1
				, 1, 1, turnmode, (Constants.ENEMY),-1);
		circleEngine.setHitPoints(5);
		GameState._weapons.add(circleEngine);		
	}
	
	private static void generateShipBoss(double playerPositionX, double playerPositionY, int turnmode, int speed)
	{
		/*
			public LineEngine(
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
		int track = ((int)(Math.random() * 359));
		double[] coord = GameUtils.getCoordsGivenTrackAndDistance(track, 300);
		int direction = 0;
		GameState._weapons.add(new LineEngine(direction, direction, coord[0] + playerPositionX
				, coord[1]+playerPositionY, 1d
				, .1d, .1d, 0, Constants.ENEMY, null, -1));	
	}
	
	public static void generateEnemy(double playerPositionX, double playerPositionY)
	{
		// the enemies
		int track = ((int)(Math.random() * 359));
		double[] coord = GameUtils.getCoordsGivenTrackAndDistance(track, 300);
		generateShip((int)playerPositionX + coord[0], (int)playerPositionY + coord[1], 0, 10);
	}
	
	public static void generateEnemyBoss(double playerPositionX, double playerPositionY)
	{
		// the enemy boss
		int track = ((int)(Math.random() * 359));
		double[] coord = GameUtils.getCoordsGivenTrackAndDistance(track, 300);
		generateShipBoss((int)playerPositionX + coord[0], (int)playerPositionY + coord[1], 0, 10);
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
		int track = ((int)(Math.random() * 359));
		double[] coord = GameUtils.getCoordsGivenTrackAndDistance(track, 300);
		int direction = 0;
		if (((int)Math.random()*100) > 50)
		{
			direction = 180;
		}
		GameState._weapons.add(new LineEngine(direction, direction, coord[0] + playerPositionX
				, coord[1]+playerPositionY, 1d
				, .1d, .1d, 0, Constants.CLOUD, null, -1));	
	}
}
