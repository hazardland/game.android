package hazardland.lib.game.job;

import hazardland.lib.game.Job;


public class Image extends Job
{
	public final static int PLAY = 10;
	public final static int PAUSE = 11;
	public final static int LAST = 12;
	public final static int FIRST = 13;
	public final static int LIMIT = 14;
	public final static int CHANGE = 15;
	public final static int GOTO = 16;
	public String sprite;
	public boolean active = false;
	public int option;

	public Image(int type, String sprite, int option)
	{
		super (type);
		this.sprite = sprite;
		this.option = option;
	}

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
				if (!entity.sprite(sprite).play)
				{
					return false;
				}
			}
			else
			{
				System.out.println("activating sprite "+sprite);
				entity.sprite(sprite).play ();
				active = true;
			}
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
		else if (type==LIMIT)
		{
			entity.sprite (sprite).limit = option;
			return false;
		}
		else if (type==GOTO)
		{
			entity.sprite (sprite).frame = option;
			return false;
		}		
		else if (type==CHANGE)
		{
			entity.sprite (sprite);
			return false;
		}
		return true;
	}
	

}
