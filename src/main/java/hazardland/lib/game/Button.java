package hazardland.lib.game;

/**
 * @author BioHazard<br>
 * implements simple button
 * all you need is to override button::click () method
 * and it will be called once a click occurs
 */
public class Button extends Entity
{
	protected String UP = "up";
	protected String DOWN = "down";
	protected String DISABLE = "disable";

	/**
	 * if image is 2x as large in width than button width
	 * it is assumed that image contains "UP" and "DOWN" states horizontally
	 * if image width is 3x as large in width than button width
	 * it is assumed that image contains "UP" and "DOWN" and "disabled" states horizontally
	 * in given order
	 * ---
	 * if you want to set custom sprites for button
	 * set sprites["UP"],sprites["DOWN"] and sprites["DISABLE"] manually
	 * in overriden button constructor or from outside of it
	 * @param scene
	 * @param x position on screen coordinate X
	 * @param y position on screen coordinate Y
	 * @param width width of button
	 * @param height height of button
     * @param image image resource of button (may contain UP, DOWN and disabled states)
     */
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
				sprites.put (UP, new Sprite (0, 0, width, height, Frame.generate (scene, image, 1, 1, 1, 0, 0, width, height)));
				DISABLE = DOWN = UP;
			}
			if (length==2)
			{
				sprites.put (UP, new Sprite (0, 0, width, height, Frame.generate (scene, image, 1, 1, 1, 0, 0, width, height)));
				sprites.put (DOWN, new Sprite (0, 0, width, height, Frame.generate (scene, image, 1, 1, 1, width, 0, width, height)));
				DISABLE = UP;
			}
			else if (length>2)
			{
				limit = length - 2;
				sprites.put (UP, new Sprite (0, 0, width, height, Frame.generate (scene, image, 1, limit, limit, 0, 0, width, height)));
				sprites.put (DOWN, new Sprite (0, 0, width, height, Frame.generate (scene, image, 1, 1, 1, width*(length-limit), 0, width, height)));
				sprites.put (DISABLE, new Sprite (0, 0, width, height, Frame.generate (scene, image, 1, 1, 1, width*(length-limit+1), 0, width, height)));
			}
			sprite ("UP");
		}		
	}

	/**
	 * override it and implement your custom logic for click event
	 * if you return true engine will stop propagating click
	 * event in click region to other object underlieng clicked object
	 * in case of false all objects below this button will receive click event also
	 * @param input input object containing event details
	 * @return true if click was received false if not
     */
	@Override
	public boolean click (Input input)
	{
		if (super.click(input))
		{
			sprite (DOWN);
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
				sprite(UP).play ();
				click ();
			}
			else
			{
				sprite(UP).play ();
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
			sprite(UP).play ();
			return true;
		}
		return false;
	}
	
	public boolean disable ()
	{
		if (super.disable())
		{
			sprite(DISABLE).play ();
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
