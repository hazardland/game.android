package hazardland.lib.game;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;

public class World
{
	@SuppressLint ("UseSparseArrays")
	public Map <Integer, Subject> subjects = new HashMap <Integer, Subject> ();	
	public float x;
	public float y;
	public float width;
	public float height;
	public float speed = 1;
	public float slow = 100;
	public int load  = 0;
	public World (float x, float y, float width, float height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public boolean contact (Subject subject, Vector vector)
	{
		if (!subject.hit)
		{
			return false;
		}
		boolean contact = false;
		if (vector.type==Move.X || vector.type==Move.Y)
		{
			for (int position=subjects.size()-1; position>=0; position--) 
			{
				if (!subjects.get(position).hit)
				{
					continue;
				}
				if (subjects.get(position).id==subject.id)
				{
					continue;
				}
				if (subjects.get(position).intersect(subject, vector))
				{
					contact = true;
					try
					{
					    subjects.get(position).hit (subject, vector);
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
		for (int position=subjects.size()-1; position>=0; position--) 
		{
			if (!subjects.get(position).sensor)
			{
				continue;
			}
			subjects.get(position).apply (vector);
		}		
	}
	
	public Subject add (Subject subject)
	{
		subjects.put (subject.id, subject);
		return subjects.get (subject.id);
	}
	
	public Subject get (int character)
	{
		return subjects.get (character);
	}
	
	
	public void click (Input input)
	{
		for (int position=subjects.size()-1; position>=0; position--) 
		{
			if (!subjects.get(position).touch)
			{
				continue;
			}			
			if (subjects.get(position).click (input))
			{
				break;
			}
		}
	}
	
	public void drag (Input input)
	{
		for (int position=subjects.size()-1; position>=0; position--) 
		{
			if (!subjects.get(position).touch)
			{
				continue;
			}			
			if (subjects.get(position).drag (input))
			{
				break;
			}
		}
	}
	
	public void stop (Input input)
	{
		for (int position=subjects.size()-1; position>=0; position--) 
		{
			if (!subjects.get(position).touch)
			{
				continue;
			}			
			if (subjects.get(position).stop (input))
			{
				break;
			}
		}		
	}
	
}
