package us.ttyl.starship.core;

import us.ttyl.asteroids.R;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class AudioPlayer 
{
	public static int sPlayerGunSoundId;
	public static int sMissileSoundId;
	public static int sShipDeathSoundId;
	
	public static SoundPool sSoundPool;
	
	/**
	 * do this once, load all sounds into pool and obtain sounds ids for them. 
	 * @param context
	 */
	public static void initSound(Context context)
	{
		sSoundPool = new SoundPool(200, AudioManager.STREAM_MUSIC, 0);
		sPlayerGunSoundId = sSoundPool.load(context, R.raw.gun3, 1);
		sShipDeathSoundId = sSoundPool.load(context, R.raw.enemy_death, 1);
		sMissileSoundId = sSoundPool.load(context, R.raw.missile_launch2, 1);}
	
	/**
	 * play the ship death sound
	 */
	public static void playShipDeath()
	{
		sSoundPool.play(sShipDeathSoundId, .25f, .25f, 0, 0, 1.0f);
	}
	
	/**
	 * play the missile sounds 
	 */
	public static void playMissileSound()
	{
		sSoundPool.play(sMissileSoundId, .25f, .25f, 0, 0, 1.0f);
	}
	
	/**
	 * stop the player gun sounds
	 */
	public static void resumePlayerGun()
	{
		sSoundPool.resume(sPlayerGunSoundId);
	}
	
	/**
	 * pause the player gun sound
	 */
	public static void pausePlayerGun()
	{
		sSoundPool.pause(sPlayerGunSoundId);
	}
	
	/**
	 * play the player gun sound
	 */
	public static void playPlayerGun()
	{
		sSoundPool.play(sPlayerGunSoundId, .5f, .5f, Integer.MAX_VALUE, -1, 1.0f);
	}
	
	/**
	 * only kill the player gun audio, all others will end very quickly. 
	 */
	public static void killAllAudio()
	{		
		sSoundPool.stop(sPlayerGunSoundId);
	}

}
