package us.ttyl.asteroids.ui.view;

import java.util.HashSet;
import java.util.Set;

import us.ttyl.asteroids.R;
import us.ttyl.starship.core.GameState;
import us.ttyl.starship.core.GameUtils;
import us.ttyl.starship.core.MainLoop;
import us.ttyl.starship.listener.GameStateListener;
import us.ttyl.starship.movement.FollowEngine;
import us.ttyl.starship.movement.FreeEngine;
import us.ttyl.starship.movement.MovementEngine;
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
	
	public double getRange(double x, double y)
	{
		return Math.sqrt((x * x) + (y * y));
	}
	 
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
	
	public void init(Context context)
	{		
		GameStateListener gsl = new GameStateListener()
		{
			@Override
			public void onPlayerDied() 
			{	
				GameState._lives = GameState._lives - 1;
				if (GameState._lives >= 0)
				{
					MovementEngine player = new FreeEngine(0, 0, 0d, 0d, 2d, 2d, 5, .1d, 0, "player", -1);
					GameState._weapons.set(0, player);
				}
				else
				{
					GameState._weapons.remove(0);
				}
				
				//remove all enemy guns and missiles
				for(int i =0 ; i < GameState._weapons.size(); i ++)
				{
					MovementEngine enemyWeapon = GameState._weapons.get(i);
					if (enemyWeapon.getWeaponName().equals("enenmy_gun") || enemyWeapon.getWeaponName().equals("enenmy_missile")
							|| enemyWeapon.getWeaponName().equals("enemy"))
					{
						GameState._weapons.remove(i);
					}
				}
			}
		};
		//initialize sprite array
		GameState._sprites = GameUtils.getTilesFromFile(context);
		GameState._bossSprites = GameUtils.getBossTilesFromFile(context);
		
		//add player to ship list
		MovementEngine player = new FreeEngine(0, 0, 0d, 0d, 2d, 2d, 5, .1d, 0, "player", -1); 
		GameState._weapons.add(player);			
		
		// start the game engine! 
		int density = (int)getContext().getResources().getDisplayMetrics().density;
		new MainLoop(gsl, density);
		
		//initialize deg map (reversed)
		_degrev = new int[360];
		int counter = 359;
		for(int i = 0; i < 360; i ++)
		{
			_degrev[i] = counter;
			counter = counter - 1;		
		}
		
	}
	
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
		
		private static final String TAG = "AsteroidViewThread";
		
		public AsteroidViewThread(Context context, SurfaceHolder holder)
		{
			mAppContext = context;
			mSurfaceHolder = holder;
			
			//setup the color for all ships
			mGunEnemy.setColor(Color.RED);
			mGunPlayer.setColor(Color.GREEN);
			mParticleExplosion.setColor(0xffff0000);
			mMissileSmoke1.setColor(0xffffaa00);
			mMissileSmoke2.setColor(0xffffff80);
			mMissileSmoke3.setColor(0xffff0000);
			mTextColor.setColor(0x7f000000);
			mControllerColor.setColor(0x7f00988a);
			
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
					canvas = mSurfaceHolder.lockCanvas(null);	
					synchronized (canvas) 
					{																											
						doDraw(canvas);							
					}
				}
				catch (Exception e)
				{
					Log.e(TAG, "", e);
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
		    MovementEngine me = GameState._weapons.elementAt(_selected);
		    double centerX = (int)me.getX();
		    double centerY = (int)me.getY();
		
		    //clear radar
		    //g.setColor(new Color(0,0,255));
		    //g.fillRect(0,0,500,500);
		
		    //draw center target (selected)
		    //g.setColor(new Color(255,0,0));
		    //g.fillRect(250,250,3,3);
		    int density = (int)getContext().getResources().getDisplayMetrics().density;
		    int pixelSize = 3 * density;
		    int misslePixel = 2 * density;
		    
		    int centerXCanvas = (int)(mAppContext.getResources().getDisplayMetrics().widthPixels / 2);
		    int centerYCanvas = (int)(mAppContext.getResources().getDisplayMetrics().heightPixels / 2);
		    
		    int canvasY = (int)(mAppContext.getResources().getDisplayMetrics().heightPixels);

		    int spriteXSize = GameUtils.getImageType(me.getCurrentDirection(), "player").getWidth();
		    int spriteYSize = GameUtils.getImageType(me.getCurrentDirection(), "player").getHeight();
		    if (GameState._weapons.get(0).getDestroyedFlag() == false)
		    {
		    	canvas.drawBitmap(GameUtils.getImageType(me.getCurrentDirection(), "player"), centerXCanvas - (spriteXSize/2), centerYCanvas - (spriteYSize/2), null);
		    }
				    
		    //draw all other targets relative to center target, don't draw center target
		    for (int i = 0; i < GameState._weapons.size(); i ++)
		    {
		    	if (i != _selected)
		    	{	
			        me = (MovementEngine)GameState._weapons.elementAt(i);
			        double x = GameUtils.getA(centerX, me.getX())/_scale;
			        double y = GameUtils.getB(centerY, me.getY())/_scale;
			        double track = GameUtils.track(x, y);
			        double range = getRange(x, y);
			        x = range * Math.cos(Math.toRadians(track));
			        y = range * Math.sin(Math.toRadians(track));
			        if (me.getWeaponName().equals("enemy"))
			        {
			        	canvas.drawBitmap(GameUtils.getImageType(me.getCurrentDirection(), "enemy"),(int)(centerXCanvas - (spriteXSize/2) + x), (int)(centerYCanvas - (spriteYSize/2) - y), null);
			        }
			        else if(me.getWeaponName().equals("gun_enemy"))
			        {			        	
			        	canvas.drawRect((int)(centerXCanvas + x), (int)(centerYCanvas - y), (int)(centerXCanvas + pixelSize + x), (int)(centerYCanvas + pixelSize - y), mGunEnemy);
			        }
			        else if(me.getWeaponName().equals("gun_player"))
			        {
			        	canvas.drawRect((int)(centerXCanvas + x), (int)(centerYCanvas - y), (int)(centerXCanvas + pixelSize + x), (int)(centerYCanvas + pixelSize - y), mGunPlayer);
			        }
			        else if(me.getWeaponName().equals("missile_player") || me.getWeaponName().equals("missile_enemy"))
			        {
			        	// g.setColor(new Color(0,0,0));
			            // g.fillRect((int)(_screenSize/2 + x), (int)(_screenSize/2 - y), 3, 3);
			        	canvas.drawBitmap(GameUtils.getImageType(me.getCurrentDirection(), "missile"),(int)(centerXCanvas + x - (18 * density)), (int)(centerYCanvas - y - (18 * density)), null);
			        }
			        else if(me.getWeaponName().equals("cloud"))
			        {		        
			        	canvas.drawBitmap(GameUtils.getImageType(me.getCurrentDirection(), "cloud"),(int)(centerXCanvas + x), (int)(centerYCanvas - y), null);
			        }
			        else if(me.getWeaponName().equals("explosion_particle"))
			        {		        
			        	//g.setColor(new Color(115,134,230));
			            //g.fillRect((int)(_screenSize/2 + x), (int)(_screenSize/2 - y), 3, 3);
			            canvas.drawRect((int)(centerXCanvas + x), (int)(centerYCanvas - y), (int)(centerXCanvas + pixelSize + x), (int)(centerYCanvas + pixelSize - y), mParticleExplosion);
			        }
			        else if(me.getWeaponName().equals("missile_smoke"))
			        {	
			        	Paint smokeColor = null;
			        	if (me.getEndurance() > 10)
			        	{
			        		smokeColor = mMissileSmoke1;
			        		//g.setColor(new Color(247,147,56));
			        	}
			        	if (me.getEndurance() >= 7 && me.getEndurance() <= 10)
			        	{
			        		smokeColor = mMissileSmoke2;
			        		//g.setColor(new Color(186,205,234));
			        	}
			        	if (me.getEndurance() < 7 )
			        	{
			        		smokeColor = mMissileSmoke3;
			        		//g.setColor(new Color(0,125,255));
			        	}
			            // g.fillRect((int)(_screenSize/2 + x), (int)(_screenSize/2 - y), 3, 3);
			        	
			        	int[] smokeOffset = GameUtils.getSmokeTrailXY(me.getCurrentDirection());
			        	int x1 = (int)(centerXCanvas + x) + (density * smokeOffset[0]);
			        	int y1 = (int)(centerYCanvas - y) + (density * smokeOffset[1]);
			        	int x2 = x1 + misslePixel;
			        	int y2 = y1 + misslePixel;
			        	canvas.drawRect(x1, y1, x2, y2, smokeColor);
			        }
		    	}	
		    }
		    // g.setColor(new Color(0,152,138));	    
		    canvas.drawText("score: " + GameState._playerScore, (int)(20 * density) , (int)(50 * density), mTextColor);
		    canvas.drawText("bullet: " + GameState._playerBulletsShot, (int)(60 * density) , (int)(50 * density), mTextColor);
		    canvas.drawText("enemy: " + GameState._playerEnemyShot, (int)(100 * density) , (int)(50 * density), mTextColor);
		    canvas.drawText("planes left: " + GameState._lives, (int)(200 * density) , (int)(50 * density), mTextColor);
		    
		    
		    //draw the controller circle
		    int directionControllerSize = 100 * density;
		    mControllerCircleX = getContext().getResources().getDisplayMetrics().widthPixels / 2;
		    mControllerCircleY = canvasY - directionControllerSize - (20 * density);
		    canvas.drawCircle(mControllerCircleX, mControllerCircleY, directionControllerSize, mControllerColor);
		    
		    //draw the special weapon circle
		    int specialWeaponSize = 30 * density;
		    mSpecialWeaponX = getContext().getResources().getDisplayMetrics().widthPixels - specialWeaponSize;
		    mSpecialWeaponY =  canvasY - directionControllerSize;
		    canvas.drawCircle(mSpecialWeaponX, mSpecialWeaponY, specialWeaponSize, mControllerColor);
		    
		    if (GameState._weapons.get(0).getWeaponName().equals("player") == false)
		    {
		    	canvas.drawText("Game Over",  centerXCanvas, centerYCanvas, mTextColor);
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
    	mAsteroidViewThread.start();
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
	}
	
	@Override
    public boolean onTouchEvent(MotionEvent motionEvent)
    {
		int count = MotionEventCompat.getPointerCount(motionEvent);
		
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
			
			// Log.i("kurt_test", "rangeController: " + rangeController + " | rangeSpecialWeapon: " + rangeSpecialWeapon);
					
			// process the events! 
			if (rangeController <= (100 * density))
			{		
				x = x - mControllerCircleX;
				y = y - mControllerCircleY;
				double x1 = GameUtils.getA(0, x);
			    double y1 = GameUtils.getB(0, y);		  
				int track = _degrev[(int)GameUtils.track(x1, y1)];		
				GameState._weapons.get(0).setDirection(track);	
			}
			if (rangeSpecialWeapon <= (30 * density))
			{
				if ((GameState._weapons.get(0).getWeaponName().equals("player") == false))
				{
					//restart game
					GameState._lives = 2;
					GameState._playerBulletsShot = 0;
					GameState._playerEnemyShot = 0;
					GameState._playerScore = 0;
					MovementEngine player = new FreeEngine(0, 0, 0d, 0d, 2d, 2d, 5, .1d, 0, "player", -1);
					
					//remove all enemy guns and missiles
					for(int i =0 ; i < GameState._weapons.size(); i ++)
					{
						MovementEngine enemyWeapon = GameState._weapons.get(i);
						if (enemyWeapon.getWeaponName().equals("enenmy_gun") || enemyWeapon.getWeaponName().equals("enenmy_missile") )
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
					if (timeDiff > 100)
					{
						mMissileLastLaunch = currentTime;
						// find closest target			
						Set <Integer> missleSet = new HashSet<Integer>();
						
						MovementEngine closestTarget = null;
						int closestTargetRange = Integer.MAX_VALUE;
						
						// select a target from weapon list
						for(int i = 1; i < GameState._weapons.size(); i ++)
						{
							MovementEngine currentShip = GameState._weapons.get(i);
							if (currentShip.getWeaponName().equals("enemy") && missleSet.contains(currentShip.hashCode()) == false)
							{
								int currentRange = GameUtils.getRange(GameState._weapons.get(0), currentShip);
								//System.out.println("currentRange: "+ currentRange);
								if (currentRange < closestTargetRange)
								{
									closestTarget = currentShip;
									closestTargetRange = currentRange;
									missleSet.add(closestTarget.hashCode());
								}
							}				
						}
						
						int targetTrack = (int)(GameUtils.getTargetTrack(GameState._weapons.get(0), closestTarget));
						
						// once the closest target is selected, launch the missile. 
						if (closestTarget != null)
						{					
							MovementEngine missile = new FollowEngine(targetTrack
									, targetTrack
									, (int)GameState._weapons.get(0).getX(), (int)GameState._weapons.get(0).getY(), .01, 10, .1, 1
									, "missile_player", closestTarget,  GameState._weapons.get(0), 250);  
							GameState._weapons.add(missile);
		//					if (GameState._muted == false)
		//					{
		//						GameState._audioPlayerMissile.play();
		//					}
						}
					}
				}
			}	
		}
    	return true;
    }
}
