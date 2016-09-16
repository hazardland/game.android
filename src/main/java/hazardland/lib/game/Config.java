package hazardland.lib.game;

public class Config
{
	public final static int STRETCH = 1;
	public final static int FIT = 2;
	public final static int NONE = 3;
	public final static int LANDSCAPE = 4;
	public final static int PORTRAIT = 5;
	/**
	 * display mode
	 */
	public int display = FIT;
	/**
	 * display orientation
	 */
	public int orientation = LANDSCAPE;
	
	/**
	 * fullscreen bool
	 */
	public boolean fullscreen = true;
	
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
	
	public boolean text = true;
	
	public boolean music = true;
	/**
	 * sleep till next draw helps setting fps
	 */
	public int refresh = 50;
	public Config ()
	{
		
	}
}
