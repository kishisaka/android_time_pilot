package us.ttyl.starship.core;

import java.util.ArrayList;
import java.util.Vector;

import android.graphics.Bitmap;
import us.ttyl.starship.movement.MovementEngine;

public class GameState 
{
	public static boolean mIsRunning = false; 
	public static boolean mIsThrottlePressed = false;
	
	public static Vector <MovementEngine>_weapons = new Vector<MovementEngine>();
	public static ArrayList <Bitmap> _sprites;
	
	//player score
	public static int _playerScore = 0;
	public static int _playerBulletsShot = 0;
	public static int _playerEnemyShot = 0;
	
	//sound settings
	public static boolean _muted = false;
	
	// player life counter
	public static int _lives = 2;
}
