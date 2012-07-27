package hazardland.lib.game;

import android.media.MediaPlayer;

public class Music extends Job implements android.media.MediaPlayer.OnCompletionListener
{
	public static final int PLAY = 40;
	public static final int STOP = 41;
	public static final int VOLUME = 42;
	public static final int PAUSE = 43;	
	int music;
	boolean play = false;
	int loop = 0;
	float volume;
	
	public Music (int type, int music)
	{
		super (type);
		this.music = music;
	}

	public Music (int type, int music, int loop)
	{
		this (type, music);
		this.loop = loop;
		this.volume = (float) loop;
	}

	public Music (int type, int music, float volume)
	{
		this (type, music);
		this.volume = volume;
		this.loop = (int) volume;
	}
	
	public void prepare ()
	{
		if (type==PLAY)
		{
			entity.scene.music (music, this);
		}		
	}

	public boolean next ()
	{
		if (pause)
		{
			return true;
		}
		if (type==STOP)
		{
			entity.scene.music (STOP, music);
			return false;
		}
		else if (type==VOLUME)
		{
			entity.scene.music (VOLUME, music, volume);
			return false;
		}
		else
		{
			if (!enabled)
			{
				return false;
			}
			if (!play)
			{
				entity.scene.music (PLAY, music, loop);
				play = true;
			}
			return true;
		}
	}
	
	public void finish ()
	{
		if (play)
		{
			enabled = false;
			entity.scene.music (STOP, music);
		}
	}

	public void onCompletion (MediaPlayer music)
	{
		enabled = false;
	}
	
	public void pause ()
	{
		super.pause();
		entity.scene.music (PAUSE, music);
	}
	
	public void resume ()
	{
		if (pause)
		{
			entity.scene.music (PLAY, music);
		}
		super.resume ();
	}

}
