package hazardland.lib.game;

import android.annotation.SuppressLint;
import android.graphics.RectF;


import hazardland.lib.game.job.Move;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.microedition.khronos.opengles.GL10;

/**
 * @author User
 *
 */
public class Entity
{
	public Map <String, Sprite> sprites = new HashMap <String, Sprite> ();
	@SuppressLint ("UseSparseArrays")
	public Map <Integer, Job> jobs = new ConcurrentHashMap <Integer, Job> ();

	public int id;
	public Scene scene;
	public World world;
	public Size size;
	public Shape shape;
	public Position position;
	public Sprite sprite;
	public Shape lock;
	public Point click;
	public int input = -1;
	private boolean enabled  = true;
	private boolean visible = true;
	private ArrayList <Job> childs = new ArrayList <Job>(0);
	public boolean hit = false;
	public boolean sensor = false;
	public boolean touch = false;
	public float weight = 1;
	public boolean pause = false;
	private boolean drag = false;

	
	public Entity (Scene scene, float x, float y, float width, float height, int image, int lines, int length, int limit, float top, float left)
	{
		this (scene, x, y, width, height, new Sprite (0, 0, width, height,  
				Frame.generate (scene, image, lines, length, limit, top, left, width, height)));
	}	
	
	public Entity (Scene scene, float x, float y, float width, float height, int image, int lines, int length, int limit)
	{
		this (scene, x, y, width, height, new Sprite (0, 0, width, height,  
				Frame.generate (scene, image, lines, length, limit, 0, 0, width, height)));
	}	
	
	public Entity (Scene scene, float x, float y, float width, float height, int image, int frames)
	{
		this (scene, x, y, width, height, new Sprite (0, 0, width, height,  
				Frame.generate (scene, image, 1, frames, frames, 0, 0, width, height)));
	}	
	
	public Entity (Scene scene, float x, float y, float width, float height, int image, int frames, Point from)
	{
		this (scene, x, y, width, height, new Sprite (0, 0, width, height,  
				Frame.generate (scene, image, 1, frames, frames, from.x, from.y, width, height)));
	}	
	
	public Entity (Scene scene, float x, float y, float width, float height, int image, Point from)
	{
		this (scene, x, y, width, height, new Sprite (0, 0, width, height,  
				Frame.generate (scene, image, 1, 1, 1, from.x, from.y, width, height)));
	}
	
	public Entity (Scene scene, float x, float y, float width, float height, int image)
	{
		this (scene, x, y, width, height, new Sprite (0, 0, width, height,  
				Frame.generate (scene, image, 1, 1, 1, 0, 0, width, height)));
	}
	
	public Entity (Scene scene, float x, float y, int image)
	{
		this (scene, x, y, scene.width(image), scene.height(image), new Sprite (0, 0, scene.width(image), scene.height(image),  
				Frame.generate (scene, image, 1, 1, 1, 0, 0, scene.width(image), scene.height(image))));
	}

	
	public Entity (Scene scene, float x, float y, int image, boolean drag)
	{
		this (scene, x, y, scene.width(image), scene.height(image), new Sprite (0, 0, scene.width(image), scene.height(image),  
				Frame.generate (scene, image, 1, 1, 1, 0, 0, scene.width(image), scene.height(image))));
		drag (drag);
	}
	
	public Entity (Scene scene, float x, float y, float width, float height)
	{
		this (scene, x, y, width, height, null);
	}
	
	public void sprite (String name, float width, float height, int image, int frames)
	{
		this.sprites.put (name, new Sprite (0, 0, width, height,
						Frame.generate (scene, image, 1, frames, frames, 0, 0, width, height)));
	}
	
	public void drag (boolean drag)
	{
		if (drag)
		{
			touch = true;
			this.drag = true;
		}
		else
		{
			drag = false;
		}
	}
	
	public Entity (Scene scene, float x, float y, float width, float height, Sprite sprite)
	{
		this.scene = scene;
		this.world = scene.world;
	    this.id = this.world.entities.size ();
		this.size = new Size (width, height);
		this.position = new Position (this, x, y, 1, 0);
		this.shape = new Shape (position, 0, 0, width, height);	
		this.shape.hide ();
		if (sprite!=null)
		{
			sprites.put ("default", sprite);
		}
		world.add (this);
		if (sprite!=null)
		{
			this.sprite = sprites.get ("default");
		}
	}
	
	/**
	 * draws object on screen and executes next step of all jobs of entity
	 * called by default 25 times per second
	 * @param gl gl instance
	 * @param scale scene scale for fitting screen dimensins
	 * @return returns same instance for reuse
	 */
	public void draw (GL10 gl, Scale scale)
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
			//return this;
		}
		if (visible)
		{
			sprite.draw (gl, position, scale);
			shape.draw (gl, scale);
		}
		//return this;
	}
	
	/**
	 * attach job to object
	 * @param job job to attach
	 * @return returns newly added job
	 */
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
	
//	public Vector vector (int type)
//	{
//		if (jobs.get(type)!=null)
//		{
//			return jobs.get(type).vector;			
//		}
//		return null;
//	}
	
	/**
	 * kill specified job
	 * @param job
	 */
	public void kill (int job)
	{
		if (jobs.containsKey (job))
		{
			job(job).finish ();
			jobs.remove (job);
		}
	}

	public boolean click (Input input)
	{
		if (pause || !visible || !enabled)
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
			return drag (new Point(input.click.x-click.x, input.click.y-click.y),
					new Point(input.drag.x-click.x, input.drag.y-click.y),
					new Point (input.vector.x.speed, input.vector.y.speed),
					new Point (input.vector.y.slow, input.vector.y.slow));
		}
		return false;
	}
	
	public boolean drag (Point from, Point to, Point speed, Point slow)
	{
		if (drag)
		{
			position.x(to.x);
			position.y(to.y);
			System.out.println("position "+to.x+", "+to.y);
		}
		return true;
	}
	
	/**
	 * called from engine when drag stops
	 * input contains user input.click.x and input.click.y coordinates
	 * object containts click coordinates where click.x and click.y are inside object
	 * @param input input handler objet
	 * @return
	 */
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
			Boolean result = 
					stop (new Point(input.click.x-click.x, input.click.y-click.y), 
					new Point(input.stop.x-click.x,input.stop.y-click.y), 
					new Point (input.vector.x.speed, input.vector.y.speed), 
					new Point (input.vector.y.slow, input.vector.y.slow));
			click = null;
			this.input = -1;
			return result;
		}
		return false;
	}

	
	/**
	 * called right before user releases drag
	 * @param from object location when drag started
	 * @param to object location when drag ended
	 */
	public boolean stop (Point from, Point to, Point speed, Point slow)
	{
		return true;
	}
	
	/**
	 * called upon object hit to manually stop user drag
	 * and release object
	 * @return returns if seccesfully stopped drag
	 */
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

	
	public boolean inside (Entity entity)
	{
		if (shape.inside(entity))
		{
			return true;
		}
		return false;
	}
	
	public Point inside (float x, float y)
	{
		if (shape.inside (x,y))
		{
			return new Point (x-position.x(), y-position.y());
		}
//		if (x>=position.x() && x<=position.x()+size.width && y>=position.y() && y<=position.y()+size.height)
//		{
//			return new Point (x-position.x(), y-position.y());
//		}
		return null;
	}
	
	private RectF rectangle ()
	{
		return new RectF (shape.x(), shape.y(), shape.width(), shape.height());
	}
	
	public boolean intersect (Entity entity, Vector vector)
	{
		if (rectangle().intersect (entity.rectangle()))
		{
			return true;
		}			
		return false;
	}
	
	public void position (float x, float y)
	{
		position (x, y, lock);
	}
	
	
//	public void position (float x, float y, Shape lock)
//	{
//		if (lock!=null)
//		{
//			if (x>lock.width())
//			{
//				position.x (lock.width());
//				System.out.println("character: setting right lock edge y "+lock.width()+" for "+x);
//			}
//			else if (x<lock.x())
//			{
//				position.x (lock.x());
//				System.out.println("character: setting left lock edge y "+lock.x()+" for "+x);
//			}
//			else
//			{
//				position.x (x);
//				System.out.println("character: setting provided x "+x);				
//			}
//			if (y>lock.height())
//			{
//				position.y (lock.height());
//				System.out.println("character: setting top lock edge y "+lock.height()+" for "+y);				
//			}
//			else if (y<lock.y())
//			{
//				position.y (lock.y());
//				System.out.println("character: setting bottom lock edge y "+lock.y()+" for "+y);
//			}
//			else
//			{
//				position.y (y);
//				System.out.println("character: setting provided y "+y);
//			}
//		}
//		else
//		{
//			position.x (x);
//			position.y (y);			
//		}
//	}		
	
	/**
	 * limit object position inside shape
	 * @param x
	 * @param y
	 * @param lock
	 */
	public void position (float x, float y, Shape lock)
	{
		if (lock!=null)
		{
			System.out.println("position:-------------------------------");
			//System.out.println ("position: setting input x "+x+" shape.x "+shape.x(x)+" shape.width "+shape.width(x)+" width "+(shape.width(x)-shape.x(x))+" lock.width "+lock.width());
			//System.out.println ("position: setting input y "+y+" shape.y "+shape.y(y)+" shape.height "+shape.height(y)+" height "+(shape.height(y)-shape.x(y))+" lock.height "+lock.height());
			if (shape.x(x)>lock.width() || shape.width(x)>lock.width())
			{
				position.x (lock.width()-shape.x-shape.width);
				System.out.println("position: setting right lock edge x "+(lock.width()-(shape.width(x)-shape.x(x))-shape.x)+" for "+x);
			}
			else if (shape.x(x)<lock.x() || shape.width(x)<lock.x())
			{
				position.x (lock.x()-shape.x);
				System.out.println("position: setting left lock edge x "+(lock.x()-shape.x)+" for "+x);
			}
			else
			{
				position.x (x);
				System.out.println("position: setting provided x "+x);				
			}
			if (shape.y(y)>lock.height() || shape.height(y)>lock.height())
			{
				position.y (lock.height()-shape.y-shape.height);
				System.out.println("position: setting top lock edge y "+(lock.height()-(shape.height(y)-shape.y(y))-shape.y)+" for "+y);				
			}
			else if (shape.y(y)<lock.y() || shape.height(y)<lock.y())
			{
				position.y (lock.y()-shape.y);
				System.out.println("position: setting bottom lock edge y "+(lock.y()-shape.y)+" for "+y);
			}
			else
			{
				position.y (y);
				System.out.println("position: setting provided y "+y);
			}
		}
		else
		{
			position.x (x);
			position.y (y);			
		}
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
	
	public Sprite sprite ()
	{
		this.sprite = sprites.get ("default");
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

	
	public void pause (int type)
	{
		if (!jobs.isEmpty ())
		{
			for (Job job : jobs.values()) 
			{
				if (job.type==type)
				{
					job.pause ();
				}
			}
		}
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

	public void resume (int type)
	{
		if (!jobs.isEmpty ())
		{
			for (Job job : jobs.values()) 
			{
				if (job.type==type)
				{
					job.resume ();
				}
			}
		}
	}
	
	
	public void custom (String condition)
	{
		
	}
	
	public void custom ()
	{
		
	}
	
	public float x (float from, float to)
	{
		return to;
	}
	
	public float y (float from, float to)
	{
		return to;
	}
	
	public float size (float from, float to)
	{
		return to;
	}	

	public float corner (float from, float to)
	{
		return to;
	}
	
	public void move (float x, float y, float speed)
	{
		move (new Point(x,y), speed, null);
	}	
	
	public void move (float x, float y, float speed, Job after)
	{
		move (new Point(x,y), speed, after);
	}
	
	
	public void move (Point to, float speed)
	{
		move (to, speed, null);
	}
	
	public void move (Point to, float speed, Job after)
	{
		
		//Point to = new Point(x, y);
		float speedY = 1;
		float speedX = 1;
		int directionX = 1;
		int directionY = 1;
		if (Math.abs(to.x-position.x())>Math.abs(to.y-position.y()))
		{
			if (to.x-position.x()!=0)
			{
				speedY =  Math.abs(to.y-position.y())/Math.abs(to.x-position.x());
			}
		}
		else
		{
			if (to.y-position.y()!=0)
			{
				speedX =  Math.abs(to.x-position.x())/Math.abs(to.y-position.y());
			}
		}
		if (to.x>position.x())
		{
			directionX = 1; 
		}
		else if (to.x<position.x())
		{
			directionX = -1;
		}
		else
		{
			directionX = 0;
		}
		if (to.y>position.y())
		{
			directionY = 1; 
		}
		else if (to.y<position.y())
		{
			directionY = -1;
		}
		else
		{
			directionY = 0;
		}
		Move jobX = null;
		Move jobY = null;
		System.out.println("move x form "+position.x()+" to "+to.x+" with speed "+(directionX*speedX*speed));
		if (position.x()>to.x)
		{
			jobX = new Move(Move.X, to.x, position.x(), directionX*speedX*speed, 1, false, 0);
		}
		else
		{
			jobX = new Move(Move.X, position.x(), to.x, directionX*speedX*speed, 1, false, 0);
		}
		if (position.y()>to.y)
		{
			jobY = new Move(Move.Y, to.y, position.y(), directionY*speedY*speed, 1, false, 0);
		}
		else
		{
			jobY = new Move(Move.Y, position.y(), to.y, directionY*speedY*speed, 1, false, 0);	
		}
		System.out.println("move y form "+position.y()+" to "+to.y+" with speed "+(directionY*speedY*speed));			
		if (after!=null)
		{
			if (Math.abs(to.x-position.x())>Math.abs(to.y-position.y()))
			{
				jobX.follow(after);	
			}
			else
			{
				jobY.follow(after);
			}
			
		}
		job (jobX);
		job (jobY);
	}
	
	public boolean visible ()
	{
		return this.visible;
	}
	
	public boolean enabled ()
	{
		return this.enabled;
	}
}