package us.ttyl.starship.movement.ships;

import us.ttyl.starship.core.AudioPlayer;
import us.ttyl.starship.core.Constants;
import us.ttyl.starship.core.GameState;
import us.ttyl.starship.movement.CircleEngine;
import us.ttyl.starship.movement.MovementEngine;

public class EnemyFighter extends CircleEngine
{
	public EnemyFighter(int direction, int currentDirection,
			double currentX, double currentY, double currentSpeed,
			double maxSpeed, double acceleration, double turnMode, int name,
			int endurance) {
		super(direction, currentDirection, currentX, currentY, currentSpeed, maxSpeed,
				acceleration, turnMode, name, endurance);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCollision(MovementEngine engine1, MovementEngine engine2)
	{
		if (engine1.getWeaponName() != engine2.getWeaponName())
		{
			//on collision with player, player missile or player gun, kill fighter, show explosion 
			if (engine2.getWeaponName() == Constants.PLAYER || engine2.getWeaponName() == Constants.MISSILE_PLAYER
					|| engine2.getWeaponName() == Constants.GUN_PLAYER)
			{
				// show explosion
				// remove enemy fighter from list
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
			}
		}
	}

}
