package hazardland.lib.game;

import android.annotation.SuppressLint;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.microedition.khronos.opengles.GL10;

public abstract class Entity
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
	public Point click;
	public int input = -1;
	public boolean enabled  = true;
	public boolean visible = true;
	private ArrayList <Job> childs = new ArrayList <Job>(0);
	public boolean hit = false;
	public boolean sensor = false;
	public boolean touch = false;
	public float weight = 1;
	public boolean pause = false;

	public Entity (Scene scene, int id, float x, float y, float width, float height, int image, int frames)
	{
		this (scene, id, x, y, width, height, new Sprite (scene.images.get(image), 0, 0, width, height,  
				Frame.generate (1, frames, frames, 0, 0, width, height, new Scale (scene.sizes.get(image),
						new Size (1, 1)))));
	}	
	
	public Entity (Scene scene, int id, float x, float y, float width, float height, int image, int frames, Point from)
	{
		this (scene, id, x, y, width, height, new Sprite (scene.images.get(image), 0, 0, width, height,  
				Frame.generate (1, frames, frames, from.x, from.y, width, height, new Scale (scene.sizes.get(image),
						new Size (1, 1)))));
	}	
	
	public Entity (Scene scene, int id, float x, float y, float width, float height, int image, Point from)
	{
		this (scene, id, x, y, width, height, new Sprite (scene.images.get(image), 0, 0, width, height,  
				Frame.generate (1, 1, 1, from.x, from.y, width, height, new Scale (scene.sizes.get(image),
						new Size (1, 1)))));
	}
	
	public Entity (Scene scene, int id, float x, float y, float width, float height, int image)
	{
		this (scene, id, x, y, width, height, new Sprite (scene.images.get(image), 0, 0, width, height,  
				Frame.generate (1, 1, 1, 0, 0, width, height, new Scale (new Size (width, height),
						new Size (1, 1)))));
	}
	
	public Entity (Scene scene, int id, float x, float y, float width, float height, Sprite sprite)
	{
		this.scene = scene;
		this.world = scene.world;
		this.id = id;
		this.size = new Size (width, height);
		this.position = new Position (x, y, 1, 0);
		if (sprite!=null)
		{
			sprites.put ("default", sprite);
		}
		if (this.id==-1)
		{
			this.id = world.entities.size ();
		}
		world.add (this);
		if (sprite!=null)
		{
			this.sprite = sprites.get ("default");
		}
	}
	
	public Entity draw (GL10 gl, Scale scale)
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
		if (pause)
		{
			return job;
		}
		if (jobs.containsKey (job.type))
		{
			jobs.remove (job.type);
		}
		job.entity = this;
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
		if (pause)
		{
			return false;
		}
		if (this.input!=-1)
		{
			return false;
		}
		click = inside (input.click.x, input.click.y);
		if (click!=null)
		{
			this.input = input.id;
			press ();
			return true;
		}
		return false;
	}
	
	public void press ()
	{
		
	}
	
	public void click ()
	{
		
	}
	
	public boolean drag (Input input)
	{
		if (pause)
		{
			return false;
		}		
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
			if (inside(input.stop.x,input.stop.y)!=null)
			{
				click ();
			}
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
	
	public boolean intersect (Entity entity, Vector vector)
	{
		if (rectangle().intersect (entity.rectangle()))
		{
			return true;
		}			
		return false;
	}
	
	public void hit (Entity entity, Vector vector)
	{
		
	}
	
	public void sensor (Vector vector)
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
	
	public boolean disable ()
	{
		if (!enabled)
		{
			return false;
		}
		enabled = false;
		return true;
	}
	
	public boolean enable ()
	{
		if (enabled)
		{
			return false;
		}
		enabled = true;
		return true;
	}
	
	public void show ()
	{
		this.visible = true;
	}
	
	public void hide ()
	{
		this.visible = false;
	}
	
	public boolean pause ()
	{
		if (!pause)
		{
			if (!jobs.isEmpty ())
			{
				for (Job job : jobs.values()) 
				{
					job.pause ();
				}
			}
			sprite.pause ();
			pause = true;
			return true;
		}
		return false;
	}
	
	public boolean resume ()
	{
		if (pause)
		{
			if (!jobs.isEmpty ())
			{
				for (Job job : jobs.values()) 
				{
					job.resume ();
				}
			}
			sprite.resume ();
			pause = false;
			return true;
		}
		return false;
	}
}