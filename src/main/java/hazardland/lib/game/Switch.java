package hazardland.lib.game;

public class Switch extends Entity
{
	private String off = "off";
	private String on = "on";
	private String disable = "DISABLE";
	private boolean active = false;
	public Switch (Scene scene, float x, float y, float width, float height, int image)
	{
		super (scene, x, y, width, height, null);
		touch = true;
		if (image>0)
		{
			int length = (int)(scene.width(image)/width);
			int limit = 0;
			if (length==1)
			{
				sprites.put (off, new Sprite (0, 0, width, height, Frame.generate (scene, image, 1, 1, 1, 0, 0, width, height)));
				disable = on = off;
			}
			if (length==2)
			{
				sprites.put (off, new Sprite (0, 0, width, height, Frame.generate (scene, image, 1, 1, 1, 0, 0, width, height)));
				sprites.put (on, new Sprite (0, 0, width, height, Frame.generate (scene, image, 1, 1, 1, width, 0, width, height)));
				disable = off;
			}
			else if (length>2)
			{
				limit = length - 2;
				sprites.put (off, new Sprite (0, 0, width, height, Frame.generate (scene, image, 1, limit, limit, 0, 0, width, height)));
				sprites.put (on, new Sprite (0, 0, width, height, Frame.generate (scene, image, 1, 1, 1, width*(length-limit), 0, width, height)));
				sprites.put (disable, new Sprite (0, 0, width, height, Frame.generate (scene, image, 1, 1, 1, width*(length-limit+1), 0, width, height)));
			}
			sprite (off);
		}		
	}

	public boolean enable ()
	{
		if (super.enable())
		{
			sprite(off).play ();
			return true;
		}
		return false;
	}
	
	public boolean disable ()
	{
		if (super.disable())
		{
			sprite(disable).play ();
			return true;
		}
		return false;
	}
	
	public void click ()
	{
		if (!active)
		{
			active = true;
			sprite (on).play();
			on ();
		}
		else
		{
			active = false;
			sprite (off).play();
			off ();
		}
	}

	public void on ()
	{
		
	}
	
	public void off ()
	{
		
	}

	public boolean pause ()
	{
		return false;
	}
	
	public boolean resume ()
	{
		return false;
	}			
}
