package hazardland.lib.game;

import hazardland.lib.game.job.Move;

import java.util.ArrayList;
import java.util.TreeMap;

import android.annotation.SuppressLint;

public class World
{
	@SuppressLint ("UseSparseArrays")
	//public TreeMap <Integer, Entity> entities = new TreeMap <Integer, Entity> ();
	public ArrayList <Entity> entities = new ArrayList <Entity> ();
	public float x;
	public float y;
	public float width;
	public float height;
	public float speed = 1;
	public float slow = 100;
	private int load  = 0;
	public boolean pause = false;
	public boolean hit = false;
	public boolean touch = false;
	public boolean sensor = false;
	public boolean start = false;
	public World (float x, float y, float width, float height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public boolean contact (Entity entity, Vector vector)
	{
		if (!hit || !entity.hit)
		{
			return false;
		}
		boolean contact = false;
		if (vector.type==Move.X || vector.type==Move.Y)
		{
			for (int position=entities.size()-1; position>=0; position--) 
			{
				if (!entities.get(position).hit)
				{
					continue;
				}
				if (entities.get(position).id==entity.id)
				{
					continue;
				}
				if (entities.get(position).intersect(entity, vector))
				{
					contact = true;
					try
					{
					    entities.get(position).hit (entity, vector);
					}
					catch (NullPointerException exception)
					{
						
					}
				}
			}
		}
		return contact;
	}
	
	public void sensor (Vector vector)
	{
		if (!sensor)
		{
			return;
		}
		for (int position=entities.size()-1; position>=0; position--) 
		{
			if (!entities.get(position).sensor)
			{
				continue;
			}
			entities.get(position).sensor (vector);
		}	
	}
	
	public Entity add (Entity entity)
	{
		//entities.put (entity.id, entity);
		entities.add(entity);
		return entities.get (entity.id);
	}
	
	public Entity get (int character)
	{
		return entities.get (character);
	}
	
	
	public void click (Input input)
	{
		System.out.println ("click "+input.click.x+", "+input.click.y);		
		if (!touch)
		{
			return;
		}
		for (int position=entities.size()-1; position>=0; position--) 
		{
			if (!entities.get(position).touch)
			{
				continue;
			}			
			if (entities.get(position).click (input))
			{
				break;
			}
		}
	}
	
	public void drag (Input input)
	{
		if (!touch)
		{
			return;
		}		
		for (int position=entities.size()-1; position>=0; position--) 
		{
			if (!entities.get(position).touch)
			{
				continue;
			}			
			if (entities.get(position).drag (input))
			{
				break;
			}
		}
	}
	
	public void stop (Input input)
	{
		if (!touch)
		{
			return;
		}		
		for (int position=entities.size()-1; position>=0; position--) 
		{
			if (!entities.get(position).touch)
			{
				continue;
			}			
			if (entities.get(position).stop (input))
			{
				break;
			}
		}		
	}
	
	public int load ()
	{
		return load;
	}
	
	public int load (int load)
	{
		if (this.load+load>99)
		{
			this.load = 99;
		}
		else
		{
			this.load += load;
		}
		return this.load;
	}
	
	public void start ()
	{
		Entity entity;
		for (int position=entities.size()-1; position>=0; position--) 
		{
			entity = entities.get(position); 
			if (entity.touch)
			{
				touch = true;
			}
			if (entity.hit)
			{
				hit = true;
			}
			if (entity.sensor)
			{
				sensor = true;
			}		
		}		
		load = 100;
		start = true;
		pause = false;
	}
	
	public void pause ()
	{
		pause = true;
		for (int position=entities.size()-1; position>=0; position--) 
		{
			entities.get(position).pause();
		}		
	}

	public void pause (int type)
	{
		for (int position=entities.size()-1; position>=0; position--) 
		{
			entities.get(position).pause(type);
		}		
	}	
	
	public void resume ()
	{
		pause = false;
		for (int position=entities.size()-1; position>=0; position--) 
		{
			entities.get(position).resume();
		}		
	}

	public void resume (int type)
	{
		for (int position=entities.size()-1; position>=0; position--) 
		{
			entities.get(position).resume(type);
		}		
	}
	
	public void front (Entity entity)
	{
		entities.remove(entity);
		//entity.id = entities.size()+2;
		entities.add(entity);
	}
}
