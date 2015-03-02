package us.ttyl.starship.movement.ships;

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

}
