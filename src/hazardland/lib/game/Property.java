package hazardland.lib.game;


public class Property extends Job
{
	public final static int TOUCH = 51;
	public final static int HIT = 52;
	public final static int SENSOR = 53;
	
	public boolean value;

	public Property(int type, boolean value)
	{
		super (type);
		this.value = value; 
	}
	
	public boolean next ()
	{
		if (pause)
		{
			return true;
		}
		switch (type)
		{
			case TOUCH:
				entity.touch = value;
				entity.scene.debug ("setting touch value");
			break;
			case HIT:
				entity.hit = value;
			break;
			case SENSOR:
				entity.sensor = value;
			break;
		}
		return false;
	}

}
