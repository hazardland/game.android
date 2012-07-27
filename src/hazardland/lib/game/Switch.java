package hazardland.lib.game;

public class Switch extends Entity
{
	private String off = "off";
	private String on = "on";
	private String disable = "disable";
	private boolean active = false;
	public Switch (Scene scene, int id, float x, float y, float width, float height, int image)
	{
		super (scene, id, x, y, width, height, null);
		touch = true;
		if (image>0)
		{
			int length = (int)(scene.sizes.get(image).width/width);
			int limit = 0;
			if (length==1)
			{
				sprites.put (off, new Sprite (scene.images.get(image), 0, 0, width, height, Frame.generate (1, 1, 1, 0, 0, width, height, new Scale (new Size (scene.sizes.get(image).width, scene.sizes.get(image).height), new Size(1,1)))));
				disable = on = off;
			}
			if (length==2)
			{
				sprites.put (off, new Sprite (scene.images.get(image), 0, 0, width, height, Frame.generate (1, 1, 1, 0, 0, width, height, new Scale (new Size (scene.sizes.get(image).width, scene.sizes.get(image).height), new Size(1,1)))));
				sprites.put (on, new Sprite (scene.images.get(image), 0, 0, width, height, Frame.generate (1, 1, 1, width, 0, width, height, new Scale (new Size (scene.sizes.get(image).width, scene.sizes.get(image).height), new Size(1,1)))));
				disable = off;
			}
			else if (length>2)
			{
				limit = length - 2;
				sprites.put (off, new Sprite (scene.images.get(image), 0, 0, width, height, Frame.generate (1, limit, limit, 0, 0, width, height, new Scale (new Size (scene.sizes.get(image).width, scene.sizes.get(image).height), new Size(1,1)))));
				sprites.put (on, new Sprite (scene.images.get(image), 0, 0, width, height, Frame.generate (1, 1, 1, width*(length-limit), 0, width, height, new Scale (new Size (scene.sizes.get(image).width, scene.sizes.get(image).height), new Size(1,1)))));
				sprites.put (disable, new Sprite (scene.images.get(image), 0, 0, width, height, Frame.generate (1, 1, 1, width*(length-limit+1), 0, width, height, new Scale (new Size (scene.sizes.get(image).width, scene.sizes.get(image).height), new Size(1,1)))));
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
