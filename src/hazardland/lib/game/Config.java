package hazardland.lib.game;

public class Config
{
	/**
	 * strech or not the display
	 */
	public boolean strech = false;
	/**
	 * virtual screen width
	 */
	public float width = 1280;
	/**
	 * virtual screen height
	 */
	public float height = 720;
	
	/**
	 * enable sensors ? it is eating battary powers
	 */
	public boolean sensor = false;
	/**
	 * use sounds ? music is still avalible by setting this to false
	 */
	public boolean sound = true;
	/**
	 * sleep till next draw helps setting fps
	 */
	public int refresh = 25;
	public Config ()
	{
		
	}
}
