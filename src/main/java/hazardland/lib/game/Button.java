package hazardland.lib.game;

/**
 * @author BioHazard<br>
 * implements simple button all you need is to override public void Click () method 
 */
public class Button extends Entity
{
	protected String up = "up";
	protected String down = "down";
	protected String disable = "disable";
	public Button (Scene scene, float x, float y, float width, float height, int image)
	{
		super (scene, x, y, width, height, null);
		touch = true;
		if (image>0)
		{
			int length = (int)(scene.size(image).width/width);
			int limit = 0;
			if (length==1)
			{
				sprites.put (up, new Sprite (0, 0, width, height, Frame.generate (scene, image, 1, 1, 1, 0, 0, width, height)));
				disable = down = up;
			}
			if (length==2)
			{
				sprites.put (up, new Sprite (0, 0, width, height, Frame.generate (scene, image, 1, 1, 1, 0, 0, width, height)));
				sprites.put (down, new Sprite (0, 0, width, height, Frame.generate (scene, image, 1, 1, 1, width, 0, width, height)));
				disable = up;
			}
			else if (length>2)
			{
				limit = length - 2;
				sprites.put (up, new Sprite (0, 0, width, height, Frame.generate (scene, image, 1, limit, limit, 0, 0, width, height)));
				sprites.put (down, new Sprite (0, 0, width, height, Frame.generate (scene, image, 1, 1, 1, width*(length-limit), 0, width, height)));
				sprites.put (disable, new Sprite (0, 0, width, height, Frame.generate (scene, image, 1, 1, 1, width*(length-limit+1), 0, width, height)));
			}
			sprite ("up");
		}		
	}
	
	@Override
	public boolean click (Input input)
	{
		if (super.click(input))
		{
			sprite (down);
			return true;
		}
		return false;
	}
	@Override
	public boolean stop (Input input)
	{
		if (this.input!=input.id)
		{
			return false;
		}
		if (click!=null)
		{
			if (inside(input.stop.x,input.stop.y)!=null)
			{
				sprite(up).play ();
				click ();
			}
			else
			{
				sprite(up).play ();
			}
			click = null;
			this.input = -1;
			return true;
		}
		return false;
	}

	public boolean enable ()
	{
		if (super.enable())
		{
			sprite(up).play ();
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
	
	public boolean pause ()
	{
		return false;
	}
	
	public boolean resume ()
	{
		return false;
	}	
}
