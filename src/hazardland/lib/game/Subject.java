package hazardland.lib.game;

import android.annotation.SuppressLint;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.microedition.khronos.opengles.GL10;

public abstract class Subject
{
	public Map <String, Sprite> sprites = new HashMap <String, Sprite> ();
	@SuppressLint ("UseSparseArrays")
	public Map <Integer, Job> jobs = new ConcurrentHashMap <Integer, Job> ();

	public int id;
	public Scene scene;
	public World world;
	public Size size;
	public Position position;
	public Sprite sprite;
	public Space lock;
	public Space edge = new Space (0,0,0,0);
	public Point click;
	public int input = -1;
	public boolean enabled  = true;
	public boolean visible = true;
	private ArrayList <Job> childs = new ArrayList <Job>(0);
	public boolean hit = false;
	public boolean sensor = false;
	public boolean touch = false;
	public float weight = 1;
	
	public Subject (Scene scene, int id, float x, float y, float width, float height, Sprite sprite)
	{
		this.scene = scene;
		this.world = scene.world;
		this.id = id;
		this.size = new Size (width, height);
		this.position = new Position (x, y, 1, 0);
		sprites.put ("default", sprite);
		if (this.id==-1)
		{
			this.id = world.subjects.size ();
		}
		world.add (this);
		this.sprite = sprites.get ("default");
	}
	
	public Subject draw (GL10 gl, Scale scale)
	{
		if (!jobs.isEmpty ())
		{
			for (Iterator <Job> iterator = jobs.values().iterator(); iterator.hasNext();) 
			{
				Job job = iterator.next ();
				if (!enabled && job.type!=Job.ENABLE)
				{
					continue;
				}
				if (!job.next ())
				{
					childs.clear ();
					if (job.childs.size()>0)
					{
						childs = job.childs;
					}
					job.finish ();
					iterator.remove ();
					for (Job child : childs)
					{
						if (child.targets.size()>0)
						{
							for (Target target : child.targets)
							{
								if (target.type==Target.SUBJECT)
								{
									if (child.type==Job.KILL)
									{
										System.out.println ("we v got killer job to spread");
									}
									world.get (target.subject).job (child);
								}
								else
								{

								}
							}
						}
						else
						{
							job (child);
						}
					}
				}
			}
		}
		if (!enabled)
		{
			return this;
		}
		if (visible)
		{
			sprite.draw (gl, position, scale);
		}
		return this;
	}
	
	public Job job (Job job)
	{
		if (jobs.containsKey (job.type))
		{
			jobs.remove (job.type);
		}
		job.subject = this;
		job.prepare ();
		jobs.put (job.type, job);
		return jobs.get (job.type);
	}
	
	public Vector vector (int type)
	{
		if (jobs.get(type)!=null)
		{
			return jobs.get(type).vector;			
		}
		return null;
	}
	
	public void kill (int job)
	{
		if (jobs.containsKey (job))
		{
			job(job).finish ();
			jobs.remove (job);
		}
	}

	public void position (float x, float y)
	{
		if (lock!=null)
		{
			if (x>lock.width)
			{
				position.x = lock.width;
			}
			else if (x<lock.x)
			{
				position.x=1;
			}
			else
			{
				position.x = x;
			}
			if (y>lock.height)
			{
				position.y = lock.height;
			}
			else if (y<lock.y)
			{
				y = 1;
			}
			else
			{
				position.y = y;
			}
		}
		else
		{
			position.x = x;
			position.y = y;			
		}
	}
	
	public boolean click (Input input)
	{
		if (this.input!=-1)
		{
			return false;
		}
		click = inside (input.click.x, input.click.y);
		if (click!=null)
		{
			this.input = input.id;
			return true;
		}
		return false;
	}
	
	public boolean drag (Input input)
	{
		if (this.input!=input.id)
		{
			return false;
		}
		if (click!=null)
		{
			return true;
		}
		return false;
	}
	
	public boolean stop (Input input)
	{
		if (this.input!=input.id)
		{
			return false;
		}
		if (click!=null)
		{
			click = null;
			this.input = -1;
			return true;
		}
		return false;
	}
	
	public boolean stop ()
	{
		if (click!=null)
		{
			click = null;
			this.input = -1;
			return true;
		}
		return false;
	}	

	public Point inside (float x, float y)
	{
		if (x>=position.x && x<=position.x+size.width && y>=position.y && y<=position.y+size.height)
		{
			return new Point (x-position.x, y-position.y);
		}
		return null;
	}
	
	private RectF rectangle ()
	{
		return new RectF (position.x, position.y, position.x+size.width, position.y+size.height);
	}
	
	public boolean intersect (Subject subject, Vector vector)
	{
		if (rectangle().intersect (subject.rectangle()))
		{
			return true;
		}			
		return false;
	}
	
	public void hit (Subject subject, Vector vector)
	{
		
	}
	
	public void apply (Vector vector)
	{
		
	}	
	
	public Job job (int type)
	{
		return jobs.get (type);
	}
	
	public Sprite sprite (String sprite)
	{
		this.sprite = sprites.get (sprite);
		return this.sprite;
	}
	
	public void disable ()
	{
		this.enabled = false;
	}
	
	public void enable ()
	{
		this.enabled = true;
	}
	
	public void show ()
	{
		this.visible = true;
	}
	
	public void hide ()
	{
		this.visible = false;
	}
}