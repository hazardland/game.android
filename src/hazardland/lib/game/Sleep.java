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
		if (from==0)
		{
			from = (int) System.currentTimeMillis();
		}
		if (from+time<=(int)System.currentTimeMillis())
		{
			return true;
		}
		enabled = false;
		return false;		
	}
	
}
