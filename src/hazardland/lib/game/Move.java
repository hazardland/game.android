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
	
	public void play ()
	{
		enabled = true;
	}
	public void pause ()
	{
		enabled = false;
	}
	public void first ()
	{
		switch (type)
		{
			case Move.X:
				subject.position.x = from;
			break;
			case Move.Y:
				subject.position.y = from;
			break;
			case Move.CORNER:
				subject.position.corner = from;
			break;
			case Move.SIZE:
				subject.position.size = from;
			break;
		}
	}	
	public void last ()
	{
		switch (type)
		{
			case Move.X:
				subject.position.x = to;
			break;
			case Move.Y:
				subject.position.y = to;
			break;
			case Move.CORNER:
				subject.position.corner = to;
			break;
			case Move.SIZE:
				subject.position.size = to;
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
						if (subject.position.x+vector.speed>=to || subject.position.x+vector.speed<=from)
						{
							if (vector.speed<0)
							{
								subject.position.x = from;
							}
							else
							{
								subject.position.x = to;
							}
							vector.speed *= -1;
							limit ();
						}
						else
						{
							subject.position.x += vector.speed;
						}
						//System.out.println ("Rotater vector.value is "+vector.value);
					break;
					case Move.Y:
						if (subject.position.y+vector.speed>=to || subject.position.y+vector.speed<=from)
						{
							if (vector.speed<0)
							{
								subject.position.y = from;
							}
							else
							{
								subject.position.y = to;
							}
							vector.speed *= -1;
							limit ();
						}
						else
						{
							subject.position.y += vector.speed;
						}
					break;
					case Move.CORNER:
						if (subject.position.corner+vector.speed>=to || subject.position.corner+vector.speed<=from)
						{
							if (vector.speed<0)
							{
								subject.position.corner = from;
							}
							else
							{
								subject.position.corner = to;
							}
							vector.speed *= -1;
							limit ();
						}
						else
						{
							subject.position.corner += vector.speed;
						}	
					break;
					case Move.SIZE:
						if (subject.position.size+vector.speed>=to || subject.position.size+vector.speed<=from)
						{
							if (vector.speed<0)
							{
								subject.position.size = from;
							}
							else
							{
								subject.position.size = to;
							}
							vector.speed *= -1;
							limit ();
						}
						else
						{
							subject.position.size += vector.speed;
						}	
					break;
				}
			}
			else
			{
				switch (type)
				{
					case Move.X:
						if (subject.position.x+vector.speed>=to)
						{
							subject.position.x = from;
							limit ();
						}
						else if (subject.position.x+vector.speed<=from)
						{
							subject.position.x = to;
							limit ();
						}
						else
						{
							subject.position.x += vector.speed;
						}
					break;
					case Move.Y:
						if (subject.position.y+vector.speed>=to)
						{
							subject.position.y = from;
							limit ();
						}
						else if (subject.position.y+vector.speed<=from)
						{
							subject.position.y = to;
							limit ();
						}
						else
						{
							subject.position.y += vector.speed;
						}
					break;
					case Move.CORNER:
						if (subject.position.corner+vector.speed>=to)
						{
							subject.position.corner = from;
							limit ();
						}
						else if (subject.position.corner+vector.speed<=from)
						{
							subject.position.corner = to;
							limit ();
						}
						else
						{
							subject.position.corner += vector.speed;
						}
					break;
					case Move.SIZE:
						if (subject.position.size+vector.speed>=to)
						{
							subject.position.size = from;
							limit ();
						}
						else if (subject.position.size+vector.speed<=from)
						{
							subject.position.size = to;
							limit ();
						}
						else
						{
							subject.position.size += vector.speed;
						}
					break;
				}
			}
			subject.world.contact (subject, vector);			
			return true;
		}
		return false;
	}

}
