package hazardland.lib.game.job;

import hazardland.lib.game.Job;


public class Sleep extends Job
{
	int time;
	long from = 0;
	public Sleep (int time)
	{
		super (Job.SLEEP);
		this.time = time;
	}
	public boolean next ()
	{
		if (pause)
		{
			from = 0;
			return true;
		}
		//System.out.println ("from is "+from);
		if (from==0)
		{
			from = System.nanoTime();
			//System.out.println ("from is "+from);
		}
		if (from+time*1000000<=System.nanoTime())
		{
			//System.out.println ("waking from "+((System.nanoTime()-from)/1000000)+" milliseconds");
			return false;
		}
		return true;		
	}
	
}
