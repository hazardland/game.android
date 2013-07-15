package hazardland.lib.game.job;

import hazardland.lib.game.Job;


public class Custom extends Job 
{
	public final static int CONDITION = 71;
	public String condition = "";
	public Custom () 
	{
		super (CONDITION);
	}
	public Custom (String condition) 
	{
		super (CONDITION);
		this.condition = condition; 
	}
	public boolean next () 
	{
		if (condition!="")
		{
			entity.custom (condition);
		}
		else
		{
			entity.custom ();
		}
		return false;
	}
	
}
