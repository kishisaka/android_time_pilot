package us.ttyl.starship.movement;

/**
 * all engines extend this and override the movement methods. 
 * @author kurt ishisaka
 *
 */
public abstract class MovementEngine
{
  boolean _destroyed = false;
  int _direction;
  int _currentDirection;  
  double _turnMode;
  double _currentTurnMode;
  double _currentX;
  double _currentY;
  double _maxSpeed;
  double _currentSpeed;
  double _acceleration;
  double _desiredSpeed;
  String _name;
  int _endurance;
  MovementEngine _origin;
  int _hitPoints = 1;

  public abstract void updateSpeed();
  public abstract void updateSpeedIncrease();
  public abstract void updateSpeedDecrease();
  public abstract void updateDirection();
  public abstract void updateDisplacement();
  public abstract void updateFuelUsage();
  public abstract void doOther();
  
  public double getAcceleration()
  {
	  return _acceleration;
  }
  
  public int getEndurance()
  {
	  return _endurance;
  }
  
  public double get_maxSpeed()
  {
	  return _maxSpeed;
  }
  
  public double get_desiredSpeed() 
  {
	  return _desiredSpeed;
  }
  
  public void set_desiredSpeed(double _desiredSpeed) 
  {
	  this._desiredSpeed = _desiredSpeed;
  }

   public void setDestroyedFlag(boolean setting)
  {
    _destroyed = setting;
  }

  public boolean getDestroyedFlag()
  {
    return _destroyed;
  }

  public void setDirection(int direction)
  {
    _direction = direction;
  }

  public int getDirection()
  {
    return _direction;
  }

  public int getCurrentDirection()
  {
    return _currentDirection;
  }

  public double getCurrentSpeed()
  {
    return _currentSpeed;
  }

  public String getWeaponName()
  {
    return _name;
  }

  public void setWeaponName(String name)
  {
    _name = name;
  }

  public double getX()
  {
    return _currentX;
  }

  public double getY()
  {
    return _currentY;
  }

  public void run()
  {
	  try
      {
		  doOther();
		  updateSpeed();
		  updateDirection();
		  updateDisplacement();
		  updateFuelUsage();
		  checkDestroyed();
      }
      catch (Exception e)
      {
    	  e.printStackTrace();
      }
  }

  public void checkDestroyed()
  {
	if (_endurance == 0 || _hitPoints == 0)
	{
	  _destroyed = true;
	}
  }

  public void setTurnMode(int turnMode)
  {
    _turnMode = turnMode;
  }

  public double getTurnMode()
  {
    return _turnMode;    
  }
  
  public MovementEngine getOrigin()
  {
	  return _origin;
  }
  
  public void setEndurance(int endurance)
  {
	 _endurance = endurance;
  }
  
  public void setHitPoints(int hitPoints)
  {
	  _hitPoints = hitPoints;
  }
  
  public void decrementHitPoints(int damage)
  {
	  _hitPoints = _hitPoints - damage;
  }
  
}