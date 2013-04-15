package hazardland.lib.game;


import java.util.ArrayList;

public class Job
{
	public static final int SHOW = 4;
	public static final int HIDE = 5;
	public static final int ENABLE = 6;
	public static final int DISABLE = 7;
	public static final int SLEEP = 8;
	public static final int KILL = 9;
	public int type;
	public Vector vector;
	public ArrayList <Job> childs = new ArrayList <Job>();
	public ArrayList <Target> targets = new ArrayList <Target>();
	public Entity entity;
	protected boolean enabled = true;
	protected int job;
	public boolean pause = false;
	
	public Job (int type)
	{
		this.type = type;
	}

	public Job (int type, int job)
	{
		this.type = type;
		this.job = job;
	}
	
	public boolean next ()
	{
		if (pause)
		{
			return true;
		}
		switch (type)
		{
			case Job.HIDE:
				entity.hide ();
			break;
			case Job.SHOW:
				entity.show ();
			break;
			case Job.ENABLE:
				entity.enable ();
			break;
			case Job.DISABLE:
				entity.disable ();
			break;
			case Job.KILL:
				entity.kill (job);
			break;
		}
		enabled = false;
		return false;
	}
	
	public Job append (Job child)
	{
		childs.add (child);
		return this;
	}

	public Job append (Job child, int subject)
	{
		child.target (subject);
		childs.add (child);
		return this;
	}

	public Job append (Job child, int type, int subject)
	{
		child.target (type, subject);
		childs.add (child);
		return this;
	}
	
	public Job follow (Job child)
	{
		childs.add (child);
		return child;
	}

	public Job follow (Job child, int subject)
	{
		child.target (subject);
		childs.add (child);
		return child;
	}

	public Job follow (Job child, int type, int subject)
	{
		child.target (type, subject);
		childs.add (child);
		return child;
	}
	
	
	public Job target (int subject)
	{
		targets.add (new Target (subject));
		return this;
	}
	
	public Job target (int type, int subject)
	{
		targets.add (new Target (type, subject));
		return this;
	}
	
	public void speed (float value, float slow)
	{
		this.vector.speed = value;
		this.vector.slow = slow;
	}

	public void prepare ()
	{
		
	}

	public void finish ()
	{
		
	}
	
	public void pause ()
	{
		pause = true;
	}
	
	public void resume ()
	{
		pause = false;
	}
}
