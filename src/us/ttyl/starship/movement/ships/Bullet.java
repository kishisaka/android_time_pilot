package us.ttyl.starship.movement.ships;

import us.ttyl.starship.core.Constants;
import us.ttyl.starship.movement.LineEngine;
import us.ttyl.starship.movement.MovementEngine;

public class Bullet extends LineEngine
{
	public Bullet(int direction, int currentDirection, double currentX,
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
		if (getOrigin().getWeaponName() != engine2.getWeaponName())
		{
			if (engine2.getWeaponName() == Constants.ENEMY_BOSS || engine2.getWeaponName() == Constants.ENEMY_FIGHTER)
			{
				engine1.decrementHitPoints(1);
				engine1.checkDestroyed();
			}
		}
	}
}
