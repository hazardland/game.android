package hazardland.lib.game;

public class Sleep extends Job
{
	int time;
	int from = 0;
	public Sleep (int time)
	{
		super (Job.SLEEP);
		this.time = time;
	}
	public boolean next ()
	{
		if (pause)
		{
			return true;
		}
		System.out.println ("from is "+from);
		if (from==0)
		{
			from = (int) System.currentTimeMillis();
			System.out.println ("from is "+from);
		}
		if (from+time<=(int)System.currentTimeMillis())
		{
			System.out.println ("waking "+(from+time)+" vs "+(int)System.currentTimeMillis());
			return true;
		}
		return false;		
	}
	
}
