package us.ttyl.starship.movement.ships;

import us.ttyl.starship.core.AudioPlayer;
import us.ttyl.starship.core.Constants;
import us.ttyl.starship.core.GameState;
import us.ttyl.starship.movement.LineEngine;
import us.ttyl.starship.movement.MovementEngine;

/**
 * The parachute pilot from enemy bosses, gives 10 missiles, pickup 4 pilots to get to next level
 * @author kurt ishisaka
 *
 */
public class Parachute extends LineEngine
{
	public Parachute(int direction, int currentDirection, double currentX,
			double currentY, double currentSpeed, double maxSpeed,
			double acceleration, int turnMode, int name, MovementEngine origin,
			int endurance, int hitpoints) 
	{
		super(direction, currentDirection, currentX, currentY, currentSpeed, maxSpeed,
				acceleration, turnMode, name, origin, endurance, hitpoints);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCollision(MovementEngine engine1, MovementEngine engine2)
	{
		if (engine2.getWeaponName() == Constants.PLAYER)
		{
			engine2.setMissileCount(engine2.getMissileCount() + 10);
			engine1.decrementHitPoints(1);
			engine1.checkDestroyed();
			AudioPlayer.playParachutePickup();
			
			GameState.sParachutePickupCount = GameState.sParachutePickupCount + 1;
			
			// if we got 4 parachutes, remove all ships, change level! 
			if (GameState.sParachutePickupCount > 3)
			{
				Thread levelWait = new Thread(new Runnable()
				{
					@Override
					public void run() 
					{						
						try
						{
							GameState.mWaitTimeBetweenLevels = true;
							Thread.sleep(3500);
							GameState.mWaitTimeBetweenLevels = false;
						}
						catch(Exception e)
						{
							
						}
					}
				});
				levelWait.start();
				
				GameState.sParachutePickupCount = 0;
				GameState.sCurrentLevel = GameState.sCurrentLevel + 1;
				if (GameState.sCurrentLevel > 4)
				{
					GameState.sCurrentLevel = 1;				
				}							
				
				//destroy all enemy fighters
				for(int i = 1; i < GameState._weapons.size() ; i ++)
				{					
					MovementEngine ship = GameState._weapons.get(i);
					if (ship.getWeaponName() == Constants.ENEMY_FIGHTER || ship.getWeaponName() == Constants.ENEMY_BOSS)
					{
						ship.decrementHitPoints(ship.getHitpoints());
						ship.checkDestroyed();
										
						AudioPlayer.playShipDeath();
						
						// create particle explosion for shot down aircraft
						for(int particleCount = 0; particleCount < 15; particleCount ++)
						{
							int particleDirection = (int)(Math.random() * 360);
							int particleSpeed = (int)(Math.random() * 10);
							int particleEndurance = (int)(Math.random() * 50);
							MovementEngine explosionParticle = new ExplosionParticle(particleDirection, particleDirection
									, ship.getX(), ship.getY(), particleSpeed, 1, 1, 1, Constants.EXPLOSION_PARTICLE
									, null, particleEndurance, 1); 
							GameState._weapons.add(explosionParticle);
						}	
					}																	
				}
				
				
			}			
		}
	}
}
