package us.ttyl.asteroids.ui.view;

import java.util.HashSet;
import java.util.Set;

import us.ttyl.asteroids.R;
import us.ttyl.starship.core.AudioPlayer;
import us.ttyl.starship.core.Constants;
import us.ttyl.starship.core.GameState;
import us.ttyl.starship.core.GameUtils;
import us.ttyl.starship.core.MainLoop;
import us.ttyl.starship.listener.GameStateListener;
import us.ttyl.starship.movement.MovementEngine;
import us.ttyl.starship.movement.ships.Bullet;
import us.ttyl.starship.movement.ships.Missile;
import us.ttyl.starship.movement.ships.PlayerFighter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * custom view to render the player view. Uses data in GameState holder which is updated
 * by the MainLoop. 
 * 
 * @author kurt ishisaka
 *
 */
public class AsteroidView extends SurfaceView implements SurfaceHolder.Callback 
{
	AsteroidViewThread mAsteroidViewThread = null;
	Bitmap mBackground = null;
	int mControllerCircleX = 0;
	int mControllerCircleY = 0; 
	
	int mSpecialWeaponX = 0;
	int mSpecialWeaponY = 0; 
	
	int mRestartX = 0;
	int mRestartY = 0; 
	
	int _scale = 1;
	int _selected = 0;
	
	long mMissileLastLaunch = 0;
	
	private static int[] _degrev = null;
	
	private GameStateListener mGameStateListener = new GameStateListener()
	{
		@Override
		public void onPlayerDied() 
		{	
			GameState._lives = GameState._lives - 1;
			if (GameState._lives >= 0)
			{
				MovementEngine player = new PlayerFighter(0, 0, 0d, 0d, 2d, 2d, 5, .1d, 0, Constants.PLAYER, -1, this);
				player.setMissileCount(Constants.START_MISSILE_COUNT);
				GameState._weapons.set(0, player);
				AudioPlayer.resumePlayerGun();
			}
			else
			{
				GameState._weapons.remove(0);
			}
			
			//remove all enemy guns and missiles
			for(int i =0 ; i < GameState._weapons.size(); i ++)
			{
				MovementEngine enemyWeapon = GameState._weapons.get(i);
				if (enemyWeapon.getWeaponName() == Constants.GUN_ENEMY || enemyWeapon.getWeaponName() == Constants.MISSILE_ENEMY
						|| enemyWeapon.getWeaponName() == Constants.ENEMY_FIGHTER)
				{
					GameState._weapons.remove(i);
				}
			}
		}
	};
	 
	public AsteroidView(Context context, AttributeSet attr)
	{
		super(context);
		SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        mBackground = BitmapFactory.decodeResource(getResources(), R.drawable.helicopter);
        
        //load up our sprites, 
        init(context);
        
		// create thread only; it's started in surfaceCreated()
		mAsteroidViewThread = new AsteroidViewThread(context, holder);
        setFocusable(true); // make sure we get key events 
	}
	
	/**
	 * initialize the game resources (sprites, etc) and prepare to run game. 
	 * @param context
	 */
	public void init(Context context)
	{		
		
		//initialize sprite array
		GameState._sprites = GameUtils.getTilesFromFile(context);
		GameState._bossSprites = GameUtils.getBossTilesFromFile(context);
		GameState._cloudSprites = GameUtils.getCloudTiles(context);
		GameState._bossBullet = GameUtils.getBossBullet(context);
		
		//initalize sound
		AudioPlayer.initSound(context);
		
		//add player to ship list
		MovementEngine player = new PlayerFighter(0, 0, 0d, 0d, 2d, 2d, 5, .1d, 0, Constants.PLAYER, -1, mGameStateListener); 
		player.setMissileCount(Constants.START_MISSILE_COUNT);
		GameState._weapons.add(player);			
		
		// start the game engine! 
		int density = (int)getContext().getResources().getDisplayMetrics().density;
		new MainLoop(density);
		
		//initialize deg map (reversed)
		_degrev = new int[360];
		int counter = 359;
		for(int i = 0; i < 360; i ++)
		{
			_degrev[i] = counter;
			counter = counter - 1;		
		}
		
	}
	
	/**
	 * the game rendering thread
	 * @author test
	 *
	 */
	public class AsteroidViewThread extends Thread
	{
		SurfaceHolder mSurfaceHolder = null;
		Context mAppContext = null;
		boolean mIsRunning = true;
		int ballLocationX = 0;
		int ballLocationY = 0;
		
		Paint mGunEnemy = new Paint();
		Paint mGunPlayer = new Paint();
		Paint mParticleExplosion = new Paint();
		Paint mMissileSmoke1 = new Paint();
		Paint mMissileSmoke2 = new Paint();
		Paint mMissileSmoke3 = new Paint();	
		Paint mTextColor = new Paint();
		Paint mControllerColor = new Paint();
		Paint mParticleExplosionBoss = new Paint();
		Paint mBorderColor = new Paint();
		
		private static final String TAG = "AsteroidViewThread";
		
		public AsteroidViewThread(Context context, SurfaceHolder holder)
		{
			mAppContext = context;
			mSurfaceHolder = holder;
			
			//setup the color for all ships
			mGunEnemy.setColor(Color.BLUE);
			mGunPlayer.setColor(Color.GREEN);
			mParticleExplosion.setColor(0xffff0000);
			mParticleExplosionBoss.setColor(0xffd3d3d3);
			mMissileSmoke1.setColor(0xffffaa00);
			mMissileSmoke2.setColor(0xffffff80);
			mMissileSmoke3.setColor(0xffff0000);
			mTextColor.setColor(Color.WHITE);
			mTextColor.setTextSize(40);
			mTextColor.setShadowLayer(2f, 2f, 2f, Color.GRAY);
			mControllerColor.setColor(0x7f00988a);
			mBorderColor.setColor(Color.BLACK);
			
		}
		
		public void run()
		{
			Log.d(TAG, "started asteroid view thread");
			
			//draw the player window to the screen	
			Canvas canvas = null;
			while (GameState.mIsRunning == true)
			{
				//draw to the onscreen buffer using onDraw(), lock the canvas first
				try
				{
					if (mSurfaceHolder != null)
					{
						canvas = mSurfaceHolder.lockCanvas(null);	
						if (canvas != null)
						{
							synchronized (canvas) 
							{																											
								doDraw(canvas);							
							}
						}						
					}
				}
				catch (Exception e)
				{
					Log.e(TAG, "canvas or surface null, forget this one and conti", e);
				}
				finally
				{
					if (canvas != null)
					{				
						//post the canvas to the surface 
						mSurfaceHolder.unlockCanvasAndPost(canvas);										
					}
				}		
			}
		}
		
		/**
		 * draw the tiles to the onscreen buffer here! 
		 * @param canvas
		 */
		public void doDraw(Canvas canvas)
		{
			// draw background						
			canvas.drawBitmap(mBackground, 0, 0, null);
			
			//draw the objects
			//set center target
			try
			{
			    MovementEngine me = GameState._weapons.elementAt(_selected);
			    double centerX = (int)me.getX();
			    double centerY = (int)me.getY();
			
			    //draw center target (selected)
			    int density = (int)getContext().getResources().getDisplayMetrics().density;
			    int pixelSize = 3 * density;
			    int misslePixel = 2 * density;
			    
			    int centerXCanvas = (int)(mAppContext.getResources().getDisplayMetrics().widthPixels / 2);
			    int centerYCanvas = (int)(mAppContext.getResources().getDisplayMetrics().heightPixels / 2);
	
			    int spriteXSize = GameUtils.getImageType(me.getCurrentDirection(), Constants.PLAYER).getWidth();
			    int spriteYSize = GameUtils.getImageType(me.getCurrentDirection(), Constants.PLAYER).getHeight();
			    if (GameState._weapons.get(0).getWeaponName()==(Constants.PLAYER) && GameState._weapons.get(0).getDestroyedFlag() == false)
			    {
			    	canvas.drawBitmap(GameUtils.getImageType(me.getCurrentDirection(), Constants.PLAYER), centerXCanvas - (spriteXSize/2), centerYCanvas - (spriteYSize/2), null);
			    }
			    
			    //draw all other targets relative to center target, don't draw center target
			    for (int i = 0; i < GameState._weapons.size(); i ++)
			    {
			    	if (i != _selected)
			    	{	
			    		try		    			
			    		{
					        me = (MovementEngine)GameState._weapons.elementAt(i);
					        double x = GameUtils.getA(centerX, me.getX())/_scale;
					        double y = GameUtils.getB(centerY, me.getY())/_scale;
					        double track = GameUtils.track(centerX, centerY, me.getX(), me.getY());
					        double range = GameUtils.getRange(x, y);
					        x = range * Math.cos(Math.toRadians(track));
					        y = range * Math.sin(Math.toRadians(track));					      
					        if (me.getWeaponName()==(Constants.ENEMY_FIGHTER))
					        {
					        	canvas.drawBitmap(GameUtils.getImageType(me.getCurrentDirection(), Constants.ENEMY_FIGHTER),(int)(centerXCanvas - (spriteXSize/2) + x), (int)(centerYCanvas - (spriteYSize/2) - y), null);
					        }
					        if (me.getWeaponName()==(Constants.PARACHUTE))
					        {
					        	canvas.drawBitmap(GameUtils.getImageType(me.getCurrentDirection(), Constants.PARACHUTE),(int)(centerXCanvas - (spriteXSize/2) + x), (int)(centerYCanvas - (spriteYSize/2) - y), null);
					        }
					        if (me.getWeaponName()==(Constants.ENEMY_BOSS))
					        {
					        	canvas.drawBitmap(GameUtils.getImageType(me.getCurrentDirection(), Constants.ENEMY_BOSS),(int)(centerXCanvas - (spriteXSize/2) + x), (int)(centerYCanvas - (spriteYSize/2) - y), null);
					        }
					        else if(me.getWeaponName()==(Constants.GUN_ENEMY))
					        {			        	
					        	if (me.getOrigin().getWeaponName() == Constants.ENEMY_FIGHTER)
					        	{
					        		canvas.drawRect((int)(centerXCanvas + x), (int)(centerYCanvas - y), (int)(centerXCanvas + pixelSize + x), (int)(centerYCanvas + pixelSize - y), mGunEnemy);
					        	}
					        	else
					        	{
					        		if (me instanceof Bullet)
					        		{
					        			canvas.drawRect((int)(centerXCanvas + x), (int)(centerYCanvas - y), (int)(centerXCanvas + pixelSize + x), (int)(centerYCanvas + pixelSize - y), mGunEnemy);
					        		}
					        		else
					        		{
					        			canvas.drawBitmap(GameState._bossBullet,(int)(centerXCanvas - (spriteXSize/2) + x), (int)(centerYCanvas - (spriteYSize/2) - y), null);
					        		}
					        	}
					        }
					        else if(me.getWeaponName()==(Constants.GUN_PLAYER))
					        {
					        	canvas.drawRect((int)(centerXCanvas + x), (int)(centerYCanvas - y), (int)(centerXCanvas + pixelSize + x), (int)(centerYCanvas + pixelSize - y), mGunPlayer);
					        }
					        else if(me.getWeaponName()==(Constants.MISSILE_PLAYER) || me.getWeaponName()==(Constants.MISSILE_ENEMY))
					        {
					        	canvas.drawBitmap(GameUtils.getImageType(me.getCurrentDirection(), Constants.MISSILE),(int)(centerXCanvas + x - (18 * density)), (int)(centerYCanvas - y - (18 * density)), null);
					        }
					        else if(me.getWeaponName()==(Constants.CLOUD))
					    	{		        
					    		canvas.drawBitmap(GameUtils.getImageType(me.getCurrentDirection(), Constants.CLOUD),(int)(centerXCanvas + x), (int)(centerYCanvas - y), null);
					    	}
					    	else if(me.getWeaponName()==(Constants.EXPLOSION_PARTICLE))
					    	{		        
					    		canvas.drawRect((int)(centerXCanvas + x), (int)(centerYCanvas - y), (int)(centerXCanvas + pixelSize + x), (int)(centerYCanvas + pixelSize - y), mParticleExplosion);
					    	}
					    	else if(me.getWeaponName()==(Constants.MISSILE_SMOKE))
					        {	
					        	Paint smokeColor = null;
					        	if (me.getEndurance() > 10)
					        	{
					        		smokeColor = mMissileSmoke1;
					        	}
					        	if (me.getEndurance() >= 7 && me.getEndurance() <= 10)
					        	{
					        		smokeColor = mMissileSmoke2;
					        	}
					        	if (me.getEndurance() < 7 )
					        	{
					        		smokeColor = mMissileSmoke3;
					        	}
					        	
					        	int[] smokeOffset = GameUtils.getSmokeTrailXY(me.getCurrentDirection());
					        	int x1 = (int)(centerXCanvas + x) + (density * smokeOffset[0]);
					        	int y1 = (int)(centerYCanvas - y) + (density * smokeOffset[1]);
					        	int x2 = x1 + misslePixel;
					        	int y2 = y1 + misslePixel;
					        	canvas.drawRect(x1, y1, x2, y2, smokeColor);
					        }
					    	else if(me.getWeaponName()==(Constants.BOSS_SMOKE))
					        {	
					    		canvas.drawRect((int)(centerXCanvas + x), (int)(centerYCanvas - y), (int)(centerXCanvas + pixelSize + x), (int)(centerYCanvas + pixelSize - y), mParticleExplosionBoss);
					        }	
			    		}
			    		catch(ArrayIndexOutOfBoundsException e)
			    		{
			    			// do nothing, simply continue to next weapon. this item might have already been destroyed by main loop
			    		}
				    }
			    }
			    // draw the top and bottom borders 
			    int width = getResources().getDisplayMetrics().widthPixels;
			    int height = getResources().getDisplayMetrics().heightPixels;
			    height = (height - width) / 2;  
			    canvas.drawRect(0, 0 ,width, height, mBorderColor);
			    canvas.drawRect(0,getResources().getDisplayMetrics().heightPixels - height, width, getResources().getDisplayMetrics().heightPixels, mBorderColor);
			     
			    // draw the score
			    canvas.drawText("1-UP: " + GameState._playerScore, (int)(15 * density) , (int)(50 * density), mTextColor);			   
			    
			    // draw the player life count
			    int counter = (getResources().getDisplayMetrics().widthPixels/density) - (30);
			    for(int i = 0; i < GameState._lives; i ++)
			    {			    	
			    	canvas.drawBitmap(GameUtils.getImageType(180, Constants.PLAYER), counter * density, 0 * density, null);
			    	counter = counter - 30;
			    }
			    
			    //draw the parachute captured count
			    counter = (getResources().getDisplayMetrics().widthPixels/density) - (30);
			    for(int i = 0; i < GameState.sParachutePickupCount; i ++)
			    {			    	
			    	canvas.drawBitmap(GameUtils.getImageType(0, Constants.PARACHUTE_SMALL), counter * density, 40 * density, null);
			    	counter = counter - 30;
			    }
			   
			    // draw player missle count
			    int missileCountOnes = GameState._weapons.get(0).getMissileCount() % 10;
			    int missileCountTens = GameState._weapons.get(0).getMissileCount() / 10;
			    counter = 0;
			    // draw 10s missle count
			    for(int i = 0; i < missileCountTens; i ++)
			    {
			    	canvas.drawBitmap(GameUtils.getImageType(90, Constants.MISSILE), counter * density, 0 * density, null);
			    	counter = counter + 8;
			    }
			    // draw ones missile count
			    for(int i = 0; i < missileCountOnes; i ++)
			    {
			    	canvas.drawBitmap(GameUtils.getImageType(90, Constants.MISSILE_SMALL), counter * density, 3 * density, null);
			    	counter = counter + 8;
			    }
			    	
			    //draw the controller circle
			    int directionControllerSize = height / 2;
			    mControllerCircleX = getContext().getResources().getDisplayMetrics().widthPixels / 2;
			    mControllerCircleY =  getContext().getResources().getDisplayMetrics().heightPixels - (height / 2);
			    canvas.drawCircle(mControllerCircleX, mControllerCircleY, directionControllerSize, mControllerColor);
			    
			    //draw the special weapon circle
			    int specialWeaponSize = height / 4;
			    mSpecialWeaponX = (int)(getContext().getResources().getDisplayMetrics().widthPixels * .85);
			    mSpecialWeaponY =  getContext().getResources().getDisplayMetrics().heightPixels - (height / 2);
			    canvas.drawCircle(mSpecialWeaponX, mSpecialWeaponY, specialWeaponSize, mControllerColor);
			    
			    if (GameState._weapons.get(0).getWeaponName()!=(Constants.PLAYER))
			    {
			    	canvas.drawText("Game Over",  centerXCanvas - (40 * density), centerYCanvas - (40 * density), mTextColor);
			    }				    
			}
			catch(Exception e)
			{
				// ignore, most likely the player ship was destroyed. 
			}
		}
		
		public void stopAsteroidViewThread()
		{
			Log.d(TAG, "stopped asteroid view thread");
			mIsRunning = false;
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		// start the thread here so that we don't busy-wait in run()
        // waiting for the surface to be created    	
    	Log.w(this.getClass().getName(), "surface created, starting main game loop");
    	if (mAsteroidViewThread.isAlive() == false)
    	{
    		mAsteroidViewThread.start();
    	}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) 
	{
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) 
	{
		mAsteroidViewThread.stopAsteroidViewThread();
		//kill game update thread
		GameState.mIsRunning = false;
		
		//no playfield, kill all audio streams, will come back when user returns. 
		AudioPlayer.killAllAudio();
	}
	
	/**
	 * return the closest target that is not currently tracked by other missiles. 
	 * @return
	 */
	public MovementEngine findClosestTarget()	
	{
		Set <MovementEngine> alreadyTrackedTargets = new HashSet <MovementEngine> ();
		
		//get all targets that are currently being tracked by missiles
		for(int i = 0; i < GameState._weapons.size(); i ++)
		{
			MovementEngine weapon = GameState._weapons.get(i);
			if (weapon.getWeaponName() == Constants.MISSILE_PLAYER)
			{
				alreadyTrackedTargets.add(((Missile)weapon).getTarget());
			}
		}
		
		int current = Integer.MAX_VALUE;
		MovementEngine closestEngine = null;
		
		// get the closest non tracked target
		for(int i = 0; i < GameState._weapons.size(); i ++)
		{
			MovementEngine weapon = GameState._weapons.get(i);
			if (weapon != null && alreadyTrackedTargets.contains(weapon) == false)
			{
				if (weapon.getWeaponName() == Constants.ENEMY_FIGHTER
						|| weapon.getWeaponName() == Constants.GUN_ENEMY
						|| weapon.getWeaponName() == Constants.MISSILE_ENEMY)
				{
					int range = GameUtils.getRange(GameState._weapons.get(0), weapon);
					if (range < current)
					{
						current = range;
						closestEngine = weapon;
					}
				}
			}			
		}
		return closestEngine;
	}
	
	@Override
    public boolean onTouchEvent(MotionEvent motionEvent)
    {
		int count = motionEvent.getPointerCount();
		
		// get the direction and special weapon motion events if exists. 		
		for(int countIndex = 0; countIndex < count; countIndex ++)
		{
			int x = 0;
			int y = 0;
			
			int density = (int)getContext().getResources().getDisplayMetrics().density;
			
			x = (int)MotionEventCompat.getX(motionEvent, countIndex);
			y = (int)MotionEventCompat.getY(motionEvent, countIndex);
			
			double rangeController = GameUtils.getRangeBetweenCoords(mControllerCircleX, mControllerCircleY, x, y);
			double rangeSpecialWeapon = GameUtils.getRangeBetweenCoords(mSpecialWeaponX, mSpecialWeaponY, x, y);
					
			// process the events! 
			if (rangeController <= (100 * density))
			{		
				x = x - mControllerCircleX;
				y = y - mControllerCircleY;
				int track = _degrev[(int)GameUtils.track(0,0,x,y)];
				GameState._weapons.get(0).setDirection(track);	
			}
			if (rangeSpecialWeapon <= (30 * density))
			{
				if ((GameState._weapons.get(0).getWeaponName()!=(Constants.PLAYER)))
				{
					//restart game
					GameState._lives = 2;
					GameState._playerBulletsShot = 0;
					GameState._playerEnemyShot = 0;
					GameState._playerScore = 0;
					MovementEngine player = new PlayerFighter(0, 0, 0d, 0d, 2d, 2d, 5, .1d, 0, Constants.PLAYER, -1, mGameStateListener);
					player.setMissileCount(Constants.START_MISSILE_COUNT);
					
					//resume player gun sound
					AudioPlayer.resumePlayerGun();
					
					//remove all enemy guns and missiles
					for(int i =0 ; i < GameState._weapons.size(); i ++)
					{
						MovementEngine enemyWeapon = GameState._weapons.get(i);
						if (enemyWeapon.getWeaponName()==(Constants.GUN_ENEMY) || enemyWeapon.getWeaponName()==(Constants.MISSILE_ENEMY) || enemyWeapon.getWeaponName()==(Constants.ENEMY_BOSS))
						{
							GameState._weapons.remove(i);
						}
					}
					GameState._weapons.set(0, player);	
				}
				else
				{
					//launch missiles
					long currentTime = System.currentTimeMillis();
					long timeDiff = currentTime - mMissileLastLaunch;
					if (timeDiff > 250 && GameState._weapons.get(0).getMissileCount() > 0)
					{
						// subtract one from missile count
						GameState._weapons.get(0).decrementMissileCount();
						
						mMissileLastLaunch = currentTime;
						for (int z = 0; z < 2; z ++)
						{
							// find closest target			
							MovementEngine closestTarget = findClosestTarget();
							int targetTrack = (int)(GameUtils.getTargetTrack(GameState._weapons.get(0), closestTarget));
							
							// once the closest target is selected, launch the missile. 
							if (closestTarget != null && targetTrack != -1)
							{					
								MovementEngine missile = new Missile(targetTrack
										, targetTrack
										, (int)GameState._weapons.get(0).getX(), (int)GameState._weapons.get(0).getY(), .01, 10, .1, 1
										, Constants.MISSILE_PLAYER, closestTarget,  GameState._weapons.get(0), 250);  
								GameState._weapons.add(missile);
							}
						}
						if (GameState._muted == false)
						{
							AudioPlayer.playMissileSound();
						}
					}					
				}
			}	
		}
    	return true;
    }
}
