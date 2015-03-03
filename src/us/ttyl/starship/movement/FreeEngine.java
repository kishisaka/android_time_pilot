
package us.ttyl.starship.movement;

import us.ttyl.starship.core.GameState;

/**
 * the player's engine
 * @author kurt ishisaka
 *
 */
public class FreeEngine extends MovementEngine
{
	protected FreeEngine(
		  int direction
		  , int currentDirection
		  , double currentX
		  , double currentY
		  , double currentSpeed
		  , double desiredSpeed
		  , double maxSpeed
		  , double acceleration
		  , int turnMode
		  , int name
		  , int endurance
		  )
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
		_desiredSpeed = desiredSpeed;
		_origin = this;
	}

	public void doOther()
	{
	}

	public void updateDisplacement()
	{
		// pre-generate cos and sin later
	    if (_currentSpeed > 0)
	    {
	    	_currentX = _currentX + (GameState._density * (Math.cos(Math.toRadians(_currentDirection)) * _currentSpeed));
		    _currentY = _currentY + (GameState._density * (Math.sin(Math.toRadians(_currentDirection)) * _currentSpeed));
	    }
	}

	@Override
	public void updateSpeedIncrease()
	{  
		_currentSpeed = _currentSpeed + _acceleration;
		if (_currentSpeed > _maxSpeed)
		{
		 _currentSpeed = _maxSpeed;
		}
	}
  
  	@Override
  	public void updateSpeedDecrease() 
	{
		_currentSpeed = _currentSpeed - _acceleration;
		if (_currentSpeed <= 1)
		{
			_currentSpeed = 0;
		}		
	}
  
	@Override
	public void updateSpeed()
	{
		if (_currentSpeed < _desiredSpeed)
		{
			updateSpeedIncrease();
		}
		else
		{
			updateSpeedDecrease();
		}
		// TODO Auto-generated method stub	
	}
	
  	  
	public void updateDirection()
	{
		_currentDirection = _direction;
	}
	
	@Override
	public void updateFuelUsage() 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCollision(MovementEngine engine2) 
	{
	}

	/*
	public void updateDirection()
	{
	    int leftCount = 0;
	    double directionRequested = _direction;
	    double currentDirection = _currentDirection;
	    while (currentDirection != directionRequested)
	    {
	      	leftCount ++;
	      	currentDirection --;
	      	if (currentDirection < 0)
	      	{
	        	currentDirection = 359;
	      	}	
	    }
	
	    int rightCount = 0;
	    currentDirection = _currentDirection;
	    while (currentDirection != directionRequested)
	    {
	      	rightCount ++;
	      	currentDirection ++;
	      	if (currentDirection > 359)
	      	{
	        	currentDirection = 0;
	      	}
	    }
	    if (leftCount < rightCount)
	    {
	      for (int i = 0; i < _turnMode; i ++)
	      {
	        if (_currentDirection == _direction)
	        {
	          break;
	        }
	        else
	        {
	          _currentDirection ++;
	        }
	        if (_currentDirection > 359)
	        {
	          _currentDirection = 0;
	        }
	      }
	    }
	    else
	    {
	      for (int i = 0; i < _turnMode; i ++)
	      {
	        if (_currentDirection == _direction)
	        {
	          break;
	        }
	        else
	        {
	          _currentDirection --;
	        }
	        if (_currentDirection < 0)
	        {
	          _currentDirection = 359;
	        }
	      }
	    }
	  }
	  */
}