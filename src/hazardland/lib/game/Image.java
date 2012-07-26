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
				if (!subject.sprite.play)
				{
					return false;
				}
			}
			subject.sprite (sprite).play ();
			active = true;
		}
		else if (type==PAUSE)
		{
			subject.sprite (sprite).stop ();
			return false;
		}
		else if (type==FIRST)
		{
			subject.sprite (sprite).first ();
			return false;
		}
		else if (type==LAST)
		{
			subject.sprite (sprite).last ();
			return false;
		}
		return true;
	}
	

}
