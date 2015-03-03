package us.ttyl.starship.core;

import us.ttyl.starship.env.EnvBuilder;
import us.ttyl.starship.movement.MovementEngine;
import us.ttyl.starship.movement.ships.Bullet;
import us.ttyl.starship.movement.ships.ExplosionParticle;
import us.ttyl.starship.movement.ships.Missile;

/**
 * The main game loop, game states is updated here. State is updated to the 
 * GameState holder which AsteroidView will use to render the player view. 
 * 
 * @author kurt ishisaka
 *
 */
public class MainLoop extends Thread
{
	private int _gunModifier = 0;
	private int _gunModifierSwivel = 3;
	private int _density;
	
	public MainLoop(int density)
	{		
		_density = density;
		// SpeedController controller = new SpeedController();
		// controller.start();
		initGame();
		start();
	}
	
	/**
	 * the main game loop! 
	 */
	@Override
	public void run()
	{
		long startTime = System.currentTimeMillis();
		long startTimeGun = startTime;
		long startTimeEnemyGun = startTime;
		long startTimeClouds = startTime;
		long startTimeBoss = startTime;
		
		//start player gun sound, this will run till player dies
		if (GameState._weapons.get(0).getWeaponName()==(Constants.PLAYER) && GameState._weapons.get(0).getDestroyedFlag() == false)
		{
			AudioPlayer.resumePlayerGun();
		}
		
		//main game loop
		while(GameState.mIsRunning == true)
		{	
			long currentTime = System.currentTimeMillis();
			try
			{				
		    	//generate a new ship. 	
		    	int enemyCount = GameUtils.getTypeCount(Constants.ENEMY_FIGHTER); 
		    	// System.out.println("enemyCount: " + enemyCount);
				if (GameState.mWaitTimeBetweenLevels == false && (currentTime - startTime) > 300 && enemyCount < 4)
				{					
			    	EnvBuilder.generateEnemy(GameState._weapons.get(0).getX()
			    			, GameState._weapons.get(0).getY());		
			    	startTime = currentTime;
				}
				
				//generate a boss ship				
				if (GameState.mWaitTimeBetweenLevels == false && (currentTime - startTimeBoss) > 6000)
				{
					EnvBuilder.generateEnemyBoss(GameState._weapons.get(0).getX()
					 		, GameState._weapons.get(0).getY());
					startTimeBoss = currentTime;
				}				
				
				// fire enemy guns constantly	
				long currentTimeEnemyGun = System.currentTimeMillis();
				if (currentTimeEnemyGun - startTimeEnemyGun > 850 && GameState._weapons.get(0).getWeaponName()==(Constants.PLAYER) == true && GameState.sFireEnemyGuns == true)
				{
					for(int i = 0; i < GameState._weapons.size(); i ++)
					{
						if ((int)(Math.random() * 100) > 98)
						{
							if (Math.random() * 100 > 20)
							{
								if (GameState._weapons.get(i).getWeaponName()==(Constants.ENEMY_FIGHTER))
								{
									startTimeEnemyGun = currentTimeEnemyGun;
									
									// get player track
									int targetTrack = (int)GameUtils.getTargetTrack(GameState._weapons.get(i), GameState._weapons.get(0));
																							
									MovementEngine bullet = new Bullet(targetTrack, targetTrack
											, (int)GameState._weapons.get(i).getX() 
											, (int)GameState._weapons.get(i).getY()
											, 3, 3, 1, 1
											, Constants.GUN_ENEMY, GameState._weapons.get(i), 100, 1);  
									GameState._weapons.add(bullet);
								}
							}
							else
							{					
								if (GameState._weapons.get(i).getWeaponName()==(Constants.ENEMY_FIGHTER))
								{
									startTimeEnemyGun = currentTimeEnemyGun;
									
									// get player track
									int targetTrack = (int)GameUtils.getTargetTrack(GameState._weapons.get(i), GameState._weapons.get(0));								
									
									MovementEngine missile = new Missile(targetTrack, targetTrack
											, (int)GameState._weapons.get(i).getX()
											, (int)GameState._weapons.get(i).getY()
											, GameState._weapons.get(i).getCurrentSpeed(), 3, 1, 1
											, Constants.MISSILE_ENEMY, GameState._weapons.get(0), GameState._weapons.get(i), 200);  
									GameState._weapons.add(missile);
									if (GameState._muted == false)
									{
										AudioPlayer.playMissileSound();
									}
								}	
							}
						}
					}
				}
				
				// fire gun constantly
				// System.out.println("gunModifier:" + _gunModifier);
				if (GameState._weapons.get(0).getDestroyedFlag() == false && (GameState._weapons.get(0).getWeaponName()==(Constants.PLAYER)))
				{
					long currentTimeGun = currentTime;
					if (currentTimeGun - startTimeGun > 40)
					{
						startTimeGun = currentTimeGun;
						MovementEngine bullet = new Bullet(GameState._weapons.get(0).getCurrentDirection() + _gunModifier, GameState._weapons.get(0).getCurrentDirection() + _gunModifier
								, (int)GameState._weapons.get(0).getX()
								, (int)GameState._weapons.get(0).getY(),8, 8, 1, 1, Constants.GUN_PLAYER, GameState._weapons.get(0), 200, 1);  
						GameState._weapons.add(bullet);
						gunModifier();
					}					
				}
				
				//create clouds 
				int cloundCount = GameUtils.getTypeCount(Constants.CLOUD); 
				long currentTimeClouds = currentTime;
				if (currentTimeClouds - startTimeClouds > 1000 && cloundCount < 6)
				{
					startTimeClouds = currentTimeClouds;
					EnvBuilder.generateCloud(GameState._weapons.get(0).getX(), GameState._weapons.get(0).getY(), GameState._weapons.get(0).getCurrentDirection());
				}
				
				// move the ships around, check for collisions.
				for(int i = 0; i < GameState._weapons.size(); i ++)
		    	{		    		
		    		MovementEngine ship = GameState._weapons.get(i);
		    		if(ship.getWeaponName()==(Constants.MISSILE_PLAYER) || ship.getWeaponName()==(Constants.MISSILE_ENEMY))
					{
		    			// make smoke trail
		    			MovementEngine missileSmokeTrail = new ExplosionParticle(ship.getCurrentDirection(), ship.getCurrentDirection()
								, (int)ship.getX()
								, (int)ship.getY(),0, 0, 0, 0, Constants.MISSILE_SMOKE, null, 5, 1);   		    			
		    			GameState._weapons.add(missileSmokeTrail);
					}

		    		// make boss damage smoke trail
		    		if (ship.getWeaponName()==(Constants.ENEMY_BOSS))
		    		{
			    		if (ship.getHitpoints() < 10 )
			    		{			    		
							int particleDirection = (int)(Math.random() * 360);
							int particleSpeed = (int)(Math.random() * 10);
							int particleEndurance = (int)(Math.random() * 50);
							MovementEngine explosionParticle = new ExplosionParticle(particleDirection, particleDirection
									, ship.getX(), ship.getY(), particleSpeed, 1, 1, 1, Constants.BOSS_SMOKE
									, null, particleEndurance, 1); 
							GameState._weapons.add(explosionParticle);
			    		}
		    		}
		    		ship.run();	
		    		
		    		//TODO O(n^2) function unfortunately, any other way to make this faster? 
		    		checkCollisions(ship);
		    	}
				
				//check destroyed and remove from list if so, remove all objects that are over 450 units away from the player
				for(int i = 1; i < GameState._weapons.size(); i ++)
		    	{
					if ((i > 0 && GameUtils.getRange(GameState._weapons.get(0), GameState._weapons.get(i)) > 450) 
							|| (GameState._weapons.get(i).getDestroyedFlag() == true))
					{
						GameState._weapons.remove(i);
					}		    		
		    	}												
				
				long singleLoopTime = System.currentTimeMillis() - currentTime;
				if (singleLoopTime < 16)
				{
					sleep(16 - singleLoopTime);
				}
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}       
	
	/**
	 * move player gun up/down to get a limited firing arc spread of a couple of deg up and down. 
	 */
	private void gunModifier()
	{		
		if (_gunModifier == 0)
		{
			_gunModifier = (_gunModifierSwivel * GameState._density);
		}
		else
		{
			if (_gunModifier == _gunModifierSwivel * GameState._density)
			{
				_gunModifier = -1 * (_gunModifierSwivel* GameState._density);
			}
			else
			{
				if (_gunModifier == -1 * (_gunModifierSwivel* GameState._density))
				{
					_gunModifier = 0;
				}
			}
		}
	}
	
	/**
	 * determines the enemy rate of based off of player score 
	 * TODO fix! 
	 * @return
	 */
	private int getEnemyGunFireRate()
	{
		float rate = 115 - (GameState._playerScore * .2f);
		if (rate < 20)
		{
			rate = 20;
		}
		return (int)rate;
	}
	
	/**
	 * weapon collision check, if a weapon is within 10 x 10 unit box  of any other units
	 * let the weapons 2 that have collided figure out what to do (call each weapons's 
	 * onCollision() method) , ignore clouds, smoke trails, etc. 
	 * @param currentShip
	 */
	private void checkCollisions(MovementEngine currentShip)
	{		
		if (currentShip.getWeaponName() == Constants.PLAYER 
				|| currentShip.getWeaponName() == Constants.ENEMY_FIGHTER
				|| currentShip.getWeaponName() == Constants.ENEMY_BOSS	
				|| currentShip.getWeaponName() == Constants.MISSILE_PLAYER	
				|| currentShip.getWeaponName() == Constants.MISSILE_ENEMY
				|| currentShip.getWeaponName() == Constants.GUN_ENEMY	
				|| currentShip.getWeaponName() == Constants.GUN_PLAYER
				|| currentShip.getWeaponName() == Constants.PARACHUTE
				&& currentShip.getDestroyedFlag() == false)
		{
			for(int i = 0; i < GameState._weapons.size(); i ++)
			{					
				if (i < GameState._weapons.size())
				{
					MovementEngine ship = GameState._weapons.get(i);					
					if (ship.getWeaponName() == Constants.PLAYER 
							|| ship.getWeaponName() == Constants.ENEMY_FIGHTER
							|| ship.getWeaponName() == Constants.ENEMY_BOSS
							|| ship.getWeaponName() == Constants.MISSILE_PLAYER	
							|| ship.getWeaponName() == Constants.MISSILE_ENEMY
							|| ship.getWeaponName() == Constants.GUN_ENEMY	
							|| ship.getWeaponName() == Constants.GUN_PLAYER
							|| ship.getWeaponName() == Constants.PARACHUTE
							&& ship.getDestroyedFlag() == false)
					{
						int diffX = Math.abs((int)(currentShip.getX() - ship.getX())); 
						int diffY = Math.abs((int)(currentShip.getY() - ship.getY())); 
						if (diffX <= (10 * GameState._density) && diffY <= (10 * GameState._density))
						{							
							currentShip.onCollision(ship);
							ship.onCollision(currentShip);
						}						
					}
				}				
			}
		}
	}
	
	/**
	 * initialize the game! 
	 */
	public void initGame()
	{
		GameState.mIsRunning = true;
		GameState._density = _density;
	}
}
