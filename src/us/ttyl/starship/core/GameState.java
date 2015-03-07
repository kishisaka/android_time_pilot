package us.ttyl.starship.core;

import java.util.ArrayList;
import java.util.Vector;

import android.graphics.Bitmap;
import android.media.SoundPool;
import us.ttyl.starship.movement.MovementEngine;

public class GameState 
{
	public static boolean mIsRunning = false; 
	public static boolean mIsThrottlePressed = false;
	public static boolean mWaitTimeBetweenLevels = false;
	
	public static boolean sFireEnemyGuns =false;
	
	// guns, missiles, player ship, enemy ships
	public static Vector <MovementEngine>_weapons = new Vector<MovementEngine>();
	
	//sprites
	public static ArrayList <Bitmap> _sprites;
	public static ArrayList <Bitmap> _bossSprites;
	public static ArrayList <Bitmap> _cloudSprites;
	public static ArrayList <Bitmap> _borders;
	public static Bitmap _bossBullet;
	
	//player score
	public static int _enemiesShotDown = 0;
	public static int _playerScore = 0;
	public static int _playerBulletsShot = 0;
	public static int _playerEnemyShot = 0; 
	
	//sound settings
	public static boolean _muted = false;
	
	// player life counter
	public static int _lives = 2;
	
	//screen density
	public static int _density = 1;
	
	public static float sPressure;
	public static float sTemp;
	
	//sounds
	public static SoundPool sSoundPool;
	public static int sPlayerGunSoundId;
	
	//game level indicator
	public static int sParachutePickupCount = 0;
	public static int sCurrentLevel = 1;
}

