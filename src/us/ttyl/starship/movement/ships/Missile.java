package us.ttyl.starship.movement.ships;

import us.ttyl.starship.core.Constants;
import us.ttyl.starship.movement.FollowEngine;
import us.ttyl.starship.movement.MovementEngine;

public class Missile extends FollowEngine
{
	public Missile(int direction, int currentDirection, double currentX,
			double currentY, double currentSpeed, double maxSpeed,
			double acceleration, int turnMode, int name, MovementEngine target,
			MovementEngine origin, int endurance) {
		super(direction, currentDirection, currentX, currentY, currentSpeed, maxSpeed,
				acceleration, turnMode, name, target, origin, endurance);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onCollision(MovementEngine engine2)
	{
		if ((engine2.getWeaponName() == Constants.MISSILE_ENEMY && getOrigin().getWeaponName() == Constants.PLAYER)
				|| (engine2.getWeaponName() == Constants.MISSILE_PLAYER && getOrigin().getWeaponName() == Constants.ENEMY_FIGHTER))
		{
			decrementHitPoints(1);
			checkDestroyed();
		}		
	}
}
