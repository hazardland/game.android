package hazardland.lib.game;

public class Move extends Job
{
	public static final int X = 0;
	public static final int Y = 1;
	public static final int CORNER = 2;
	public static final int SIZE = 3;	
	public float from;
	public float to;
	public boolean reverse = true;
	public int limit = 0;
	public int current = 0;

	public Move (int type, float from, float to, float speed, int limit, boolean reverse, float slow)
	{
		super (type);
		this.from = from;
		this.to = to;
		this.vector = new Vector (type, speed, slow);
		this.limit = limit;
		this.reverse = reverse;
	}
	
	public void first ()
	{
		switch (type)
		{
			case Move.X:
				entity.position.x = from;
			break;
			case Move.Y:
				entity.position.y = from;
			break;
			case Move.CORNER:
				entity.position.corner = from;
			break;
			case Move.SIZE:
				entity.position.size = from;
			break;
		}
	}	
	public void last ()
	{
		switch (type)
		{
			case Move.X:
				entity.position.x = to;
			break;
			case Move.Y:
				entity.position.y = to;
			break;
			case Move.CORNER:
				entity.position.corner = to;
			break;
			case Move.SIZE:
				entity.position.size = to;
			break;
		}
	}
	private void limit ()
	{
		if (limit==0)
		{
			return;
		}
		current++;
		if (current==limit)
		{
			stop ();
		}
	}
	public void stop ()
	{
		enabled = false;
		current = 0;
	}
	public boolean next ()
	{
		if (pause)
		{
			return true;
		}
		if (enabled)
		{
			if (vector.slow>0)
			{
				if (vector.speed>0)
				{
					vector.speed -= vector.slow;
					if (vector.speed<=0)
					{
						enabled = false;
						return false;
					}
				}
				else if (vector.speed<0)
				{
					vector.speed += vector.slow;
					if (vector.speed>=0)
					{
						enabled = false;
						return false;
					}
				}
			}
			if (reverse)
			{
				switch (type)
				{
					case Move.X:
						if (entity.position.x+vector.speed>=to || entity.position.x+vector.speed<=from)
						{
							if (vector.speed<0)
							{
								entity.position.x = from;
							}
							else
							{
								entity.position.x = to;
							}
							vector.speed *= -1;
							limit ();
						}
						else
						{
							entity.position.x += vector.speed;
						}
						//System.out.println ("Rotater vector.value is "+vector.value);
					break;
					case Move.Y:
						if (entity.position.y+vector.speed>=to || entity.position.y+vector.speed<=from)
						{
							if (vector.speed<0)
							{
								entity.position.y = from;
							}
							else
							{
								entity.position.y = to;
							}
							vector.speed *= -1;
							limit ();
						}
						else
						{
							entity.position.y += vector.speed;
						}
					break;
					case Move.CORNER:
						if (entity.position.corner+vector.speed>=to || entity.position.corner+vector.speed<=from)
						{
							if (vector.speed<0)
							{
								entity.position.corner = from;
							}
							else
							{
								entity.position.corner = to;
							}
							vector.speed *= -1;
							limit ();
						}
						else
						{
							entity.position.corner += vector.speed;
						}	
					break;
					case Move.SIZE:
						if (entity.position.size+vector.speed>=to || entity.position.size+vector.speed<=from)
						{
							if (vector.speed<0)
							{
								entity.position.size = from;
							}
							else
							{
								entity.position.size = to;
							}
							vector.speed *= -1;
							limit ();
						}
						else
						{
							entity.position.size += vector.speed;
						}	
					break;
				}
			}
			else
			{
				switch (type)
				{
					case Move.X:
						if (entity.position.x+vector.speed>=to)
						{
							entity.position.x = from;
							limit ();
						}
						else if (entity.position.x+vector.speed<=from)
						{
							entity.position.x = to;
							limit ();
						}
						else
						{
							entity.position.x += vector.speed;
						}
					break;
					case Move.Y:
						if (entity.position.y+vector.speed>=to)
						{
							entity.position.y = from;
							limit ();
						}
						else if (entity.position.y+vector.speed<=from)
						{
							entity.position.y = to;
							limit ();
						}
						else
						{
							entity.position.y += vector.speed;
						}
					break;
					case Move.CORNER:
						if (entity.position.corner+vector.speed>=to)
						{
							entity.position.corner = from;
							limit ();
						}
						else if (entity.position.corner+vector.speed<=from)
						{
							entity.position.corner = to;
							limit ();
						}
						else
						{
							entity.position.corner += vector.speed;
						}
					break;
					case Move.SIZE:
						if (entity.position.size+vector.speed>=to)
						{
							entity.position.size = from;
							limit ();
						}
						else if (entity.position.size+vector.speed<=from)
						{
							entity.position.size = to;
							limit ();
						}
						else
						{
							entity.position.size += vector.speed;
						}
					break;
				}
			}
			entity.world.contact (entity, vector);			
			return true;
		}
		return false;
	}

}
