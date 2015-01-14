package us.ttyl.starship.movement;

public class PlanetEngine extends MovementEngine
{
	public PlanetEngine(int x, int y, int orbitRadius)
	{
		_currentX = (double)x;
		_currentY = (double)y;
	}
	
	@Override
	public void updateSpeed() 
	{
		// TODO Auto-generated method stub	
	}

	@Override
	public void updateSpeedIncrease() 
	{
		// TODO Auto-generated method stub	
	}

	@Override
	public void updateSpeedDecrease() 
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void updateDirection()
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void updateDisplacement()
	{
		// (x - h)^2 + (y - k)^2 = r2
				
		// TODO Auto-generated method stub	
		
	}

	@Override
	public void updateFuelUsage()
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void doOther()
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void onCollision(MovementEngine engine1, MovementEngine engine2) {
		// TODO Auto-generated method stub
		
	}
}
