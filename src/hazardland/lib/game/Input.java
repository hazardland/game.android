package hazardland.lib.game;

import java.util.ArrayList;

import android.view.MotionEvent;

public class Input
{
	public int id;
	World world;
	public Point click;
	public Point drag = new Point (0, 0);
	public Point stop = new Point (0, 0);
	public Range time = new Range (0, 0);
	
	public Vector vector = new Vector (new Vector(0,0), new Vector (0,0));
	
	float distance;
	private float watch;
	Point from;
	ArrayList<Point> history = new ArrayList<Point>(4);
	public Input (int id, World world)
	{
		this.id = id;
		this.world = world;
	}
	public void click (float x, float y)
	{
		time.from = time ();
		click = new Point (x,y);
		world.click (this);
		history.add (new Point (x, y, time()));
	}
	public void drag (float x, float y)
	{
		this.drag.x = x;
		this.drag.y = y;
		
		if (history.size()>4)
		{
			history.remove (0);
		}
		history.add (new Point(x, y, time()));
		
		time.to = time ();
		from = history.get (0);
		watch = time.to-from.time;		
		if (watch==0)
		{
			this.vector.x.speed = 0;
			this.vector.x.speed = 0;
		}
		else
		{
			this.vector.x.speed = ((drag.x-from.x)/Math.abs(time.to-from.time))*world.speed;
			this.vector.y.speed = ((drag.y-from.y)/Math.abs(time.to-from.time))*world.speed;
		}
        if (this.vector.x.speed<0)
        {
        	this.vector.x.slow = -this.vector.x.speed/world.slow;
        }
        else
        {
        	this.vector.x.slow = this.vector.x.speed/world.slow;
        }
        if (this.vector.y.speed<0)
        {
        	this.vector.y.slow = -this.vector.y.speed/world.slow;
        }
        else
        {
        	this.vector.y.slow = this.vector.y.speed/world.slow;
        }

        world.drag (this);
	}
	public void stop (float x, float y)
	{
		stop.x = x;
		stop.y = y;
		
		time.to = time ();
		from = history.get (0);
		watch = time.to-from.time;		
		if (watch==0)
		{
			this.vector.x.speed = 0;
			this.vector.x.speed = 0;
		}
		else
		{
			this.vector.x.speed = ((stop.x-from.x)/Math.abs(time.to-from.time))*world.speed;
			this.vector.y.speed = ((stop.y-from.y)/Math.abs(time.to-from.time))*world.speed;
		}
        if (this.vector.x.speed<0)
        {
        	this.vector.x.slow = -this.vector.x.speed/world.slow;
        }
        else
        {
        	this.vector.x.slow = this.vector.x.speed/world.slow;
        }
        if (this.vector.y.speed<0)
        {
        	this.vector.y.slow = -this.vector.y.speed/world.slow;
        }
        else
        {
        	this.vector.y.slow = this.vector.y.speed/world.slow;
        }
        world.stop (this);        
        click = null;
	}
	public static int id (MotionEvent event)
	{
		return event.getPointerId ((event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT);
	}
	public static int index (MotionEvent event)
	{
		return (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
	}
	public static int time ()
	{
		return (int) System.currentTimeMillis();		
	}
}
