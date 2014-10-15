package us.ttyl.starship.core;

import us.ttyl.starship.movement.MovementEngine;

public class SpeedController extends Thread
{
	public void run()
	{
		while (true)
		{
			try
			{
				MovementEngine player = GameState._weapons.get(0);
				if (GameState.mIsThrottlePressed == true)
				{
					player.updateSpeedIncrease();
				}
				else
				{
					player.updateSpeedDecrease();
				}
				sleep(10);
			}
			catch(Exception e)
			{	
				e.printStackTrace();
			}
		}
	}
}
