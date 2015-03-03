package us.ttyl.starship.movement;

import us.ttyl.starship.core.GameState;

/**
 * used for gun type weapons, maybe even beams
 * @author kurt ishisaka
 *
 */
public class LineEngine extends MovementEngine
{
	protected LineEngine(
	  	int direction, 
	  	int currentDirection, 
	  	double currentX, 
	  	double currentY, 
	  	double currentSpeed, 
	  	double maxSpeed, 
	  	double acceleration, 
	  	int turnMode, 
	  	int name,
	  	MovementEngine origin, 
	  	int endurance,
	  	int hitpoints)
	{
	    _name = name;
	    _direction = direction;
	    _currentDirection = currentDirection;
	    _currentSpeed = currentSpeed;
	    _maxSpeed = maxSpeed;
	    _acceleration = acceleration;
	    _currentX = currentX;
	    _currentY = currentY;
	    _destroyed = false;
	    _turnMode = turnMode;
		_endurance = endurance;
		_origin = origin;
		_hitPoints = hitpoints;
	}
	
	@Override
	public void updateSpeed()
	{	
	    if (_currentSpeed > 0)
	    {
	    	_currentSpeed = _currentSpeed + _acceleration;
	    	if (_currentSpeed > _maxSpeed)
	    	{
	    		_currentSpeed = _maxSpeed;
	    	}
	    }
	}

	@Override
	public void updateDirection()
	{
	}

	@Override
	public void updateDisplacement() 
	{	
		_currentX = _currentX + (GameState._density * (Math.cos(Math.toRadians(_currentDirection)) * _currentSpeed));
	    _currentY = _currentY + (GameState._density * (Math.sin(Math.toRadians(_currentDirection)) * _currentSpeed));
	}
	
	@Override
	public void updateFuelUsage() 
	{
		if (_endurance > 0)
		{
			_endurance = _endurance - 1;
		}
	}

	@Override
	public void doOther()
	{
	}


	@Override
	public void updateSpeedIncrease() 
	{
	}

	@Override
	public void updateSpeedDecrease()
	{
	}
	

	@Override
	public void onCollision(MovementEngine engine2)
	{
	}

}
