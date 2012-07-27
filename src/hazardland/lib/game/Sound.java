package hazardland.lib.game;

public class Sound extends Job
{
	public static final int PLAY = 30;
	public static final int STOP = 31;
	int sound;
	int play = -1;
	int loop = 0;
	
	public Sound(int type, int sound)
	{
		super (type);
		this.sound = sound;
	}

	public Sound(int type, int sound, int loop)
	{
		super (type);
		this.sound = sound;
		this.loop = loop;
	}

	public boolean next ()
	{
		if (pause)
		{
			return true;
		}
		if (type==STOP)
		{
			entity.scene.sound (STOP, sound);
			return false;
		}
		else
		{
			if (!enabled)
			{
				return false;
			}
			if (play==-1)
			{
				play = entity.scene.sound (PLAY, sound, loop);
			}
			return true;
		}
	}
	
	public void finish ()
	{
		if (play!=-1)
		{
			enabled = false;
			entity.scene.sound (STOP, play);
		}
	}

}