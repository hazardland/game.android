package hazardland.lib.game;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;

public class World
{
	@SuppressLint ("UseSparseArrays")
	public Map <Integer, Entity> entities = new HashMap <Integer, Entity> ();	
	public float x;
	public float y;
	public float width;
	public float height;
	public float speed = 1;
	public float slow = 100;
	private int load  = 0;
	public boolean pause = false;
	public World (float x, float y, float width, float height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public boolean contact (Entity entity, Vector vector)
	{
		if (!entity.hit)
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
	
	public void apply (Vector vector)
	{
		for (int position=entities.size()-1; position>=0; position--) 
		{
			if (!entities.get(position).sensor)
			{
				continue;
			}
			entities.get(position).apply (vector);
		}		
	}
	
	public Entity add (Entity entity)
	{
		entities.put (entity.id, entity);
		return entities.get (entity.id);
	}
	
	public Entity get (int character)
	{
		return entities.get (character);
	}
	
	
	public void click (Input input)
	{
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
		load = 100;
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
	
	public void resume ()
	{
		pause = false;
		for (int position=entities.size()-1; position>=0; position--) 
		{
			entities.get(position).resume();
		}		
	}
	
}
