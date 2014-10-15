package us.ttyl.starship.listener;

public interface GameStateListener 
{
	/**
	 * when the player dies, reset the player ship so the player 
	 * can continue playing until they run out of reserve ships
	 */
	public void onPlayerDied();
}
