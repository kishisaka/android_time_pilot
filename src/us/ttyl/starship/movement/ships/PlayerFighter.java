package us.ttyl.starship.movement.ships;

import android.util.Log;
import us.ttyl.starship.core.AudioPlayer;
import us.ttyl.starship.core.Constants;
import us.ttyl.starship.core.GameState;
import us.ttyl.starship.listener.GameStateListener;
import us.ttyl.starship.movement.FreeEngine;
import us.ttyl.starship.movement.MovementEngine;

public class PlayerFighter extends FreeEngine
{
	private boolean mWaiting = false;
	
	private GameStateListener mGameStateListener;
	private static final String TAG = "PLayerFighter";
	
	public PlayerFighter(int direction, int currentDirection, double currentX,
			double currentY, double currentSpeed, double desiredSpeed,
			double maxSpeed, double acceleration, int turnMode, int name,
			int endurance, GameStateListener gameStateListener) {
		super(direction, currentDirection, currentX, currentY, currentSpeed,
				desiredSpeed, maxSpeed, acceleration, turnMode, name, endurance);
		mGameStateListener = gameStateListener;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onCollision(MovementEngine engine1, MovementEngine engine2)
	{
		//on collision with enemy, enemy missile or enemy gun, kill player fighter, show explosion 
		if (engine1.getWeaponName() != engine2.getWeaponName() && mWaiting == false)
		{
			if (engine2.getWeaponName() == Constants.ENEMY_FIGHTER || engine2.getWeaponName() == Constants.ENEMY_BOSS 
					|| engine2.getWeaponName() == Constants.MISSILE_ENEMY || engine2.getWeaponName() == Constants.GUN_ENEMY
					&& getDestroyedFlag() == false)
			{
				// show explosion
				// remove player fighter from list
				engine1.decrementHitPoints(1);
				if (engine1.checkDestroyed())
				{
					// play death sound
					AudioPlayer.playShipDeath();
				}	
				// create particle explosion for shot down aircraft
				for(int particleCount = 0; particleCount < 15; particleCount ++)
				{
					int particleDirection = (int)(Math.random() * 360);
					int particleSpeed = (int)(Math.random() * 10);
					int particleEndurance = (int)(Math.random() * 50);
					MovementEngine explosionParticle = new ExplosionParticle(particleDirection, particleDirection
							, engine1.getX(), engine1.getY(), particleSpeed, 1, 1, 1, Constants.EXPLOSION_PARTICLE
							, null, particleEndurance, 1); 
					GameState._weapons.add(explosionParticle);
				}
				//pause the player gun sound
				try
				{
					AudioPlayer.pausePlayerGun();
				}
				catch(Exception e)
				{
					Log.e(TAG, "MainLoop.checkCollison() most likely sound does not exist", e); 
				}
				
				if (getDestroyedFlag() == true)
				{
					// check if player ship was destroyed, wait for 2 seconds, then restart the game 
					Thread deadWait = new Thread(new Runnable()
					{
						@Override
						public void run() 
						{
							try
							{
								mWaiting = true;
								// wait 2 seconds to show the explosion. 
								Thread.sleep(2000);								
								mGameStateListener.onPlayerDied();
								mWaiting = false;
							}
							catch(InterruptedException ie)
							{
								// ignore and continue!
							}
						}
					});
					deadWait.start();
				}
			}
		}
	}
}
