package hazardland.lib.game;

public class Position
{
	private Entity entity;
	private float x;
	private float y;
	private float size;
	private float corner;
	
	public Position (Entity entity, float x, float y, float size, float corner)
	{
		this.entity = entity;
		this.x = x;
		this.y = y;
		this.size = size;
		this.corner = corner;
	}
	
	public void x (float x)
	{
		if (entity!=null)
		{
			this.x = entity.x (this.x, x);
		}
		else
		{
			this.x = x;
		}
	}
	public float x ()
	{
		return x;
	}

	public void y (float y)
	{
		if (entity!=null)
		{
			this.y = entity.y (this.y, y);
		}
		else
		{
			this.y = y;
		}
	}
	public float y ()
	{
		return y;
	}
	
	public void size (float size)
	{
		if (entity!=null)
		{
			this.size = entity.size (this.size, size);
		}
		else
		{
			this.size = size;
		}

	}
	public float size ()
	{
		return size;
	}
	
	public void corner (float corner)
	{
		if (entity!=null)
		{
			this.corner = entity.corner (this.corner, corner);
		}
		else
		{
			this.corner = corner;
		}
	}
	public float corner ()
	{
		return corner;
	}	
}
