package hazardland.lib.game;

public class Image extends Job
{
	public final static int PLAY = 10;
	public final static int PAUSE = 11;
	public final static int LAST = 12;
	public final static int FIRST = 13;
	public String sprite;
	public boolean active = false;

	public Image(int type, String sprite)
	{
		super (type);
		this.sprite = sprite;
	}
	public boolean next ()
	{
		if (pause)
		{
			return true;
		}
		if (type==PLAY)
		{
			if (active)
			{
				if (!entity.sprite.play)
				{
					return false;
				}
			}
			entity.sprite (sprite).play ();
			active = true;
		}
		else if (type==PAUSE)
		{
			entity.sprite (sprite).stop ();
			return false;
		}
		else if (type==FIRST)
		{
			entity.sprite (sprite).first ();
			return false;
		}
		else if (type==LAST)
		{
			entity.sprite (sprite).last ();
			return false;
		}
		return true;
	}
	

}
