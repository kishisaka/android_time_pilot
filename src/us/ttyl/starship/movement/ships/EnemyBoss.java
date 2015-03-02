package us.ttyl.starship.movement.ships;

import android.util.Log;
import us.ttyl.starship.core.AudioPlayer;
import us.ttyl.starship.core.Constants;
import us.ttyl.starship.core.GameState;
import us.ttyl.starship.movement.LineEngine;
import us.ttyl.starship.movement.MovementEngine;

public class EnemyBoss extends LineEngine
{
	public EnemyBoss(int direction, int currentDirection, double currentX,
			double currentY, double currentSpeed, double maxSpeed,
			double acceleration, int turnMode, int name, MovementEngine origin,
			int endurance, int hitpoints) {
		super(direction, currentDirection, currentX, currentY, currentSpeed, maxSpeed,
				acceleration, turnMode, name, origin, endurance, hitpoints);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onCollision(MovementEngine engine1, MovementEngine engine2)
	{
		if (engine1.getWeaponName() != engine2.getWeaponName())
		{
			Log.i("kurt_test", "EnemyBoss.onCollision() " + engine1.getWeaponName() + ":" + engine2.getWeaponName());
			//on collision with player, player missile or player gun, kill boss, show explosion 
			if (engine2.getWeaponName() == Constants.PLAYER || engine2.getWeaponName() == Constants.MISSILE_PLAYER
					|| engine2.getWeaponName() == Constants.GUN_PLAYER)
			{
				// show explosion
				// remove enemy boss from list
				engine1.decrementHitPoints(1);
				if (engine1.checkDestroyed())
				{
					// play death sound
					AudioPlayer.playShipDeath();
					
					MovementEngine parachute = new Parachute(270, 270
							, engine1.getX(), engine1.getY(), .2, 1, 1, 1, Constants.PARACHUTE
							, GameState._weapons.get(0), 400, 1); 
					GameState._weapons.add(parachute);
				}	
				// create particle explosion for shot down boss
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
			}
		}
	}
}
