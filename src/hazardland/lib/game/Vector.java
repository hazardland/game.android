package hazardland.lib.game;

public class Vector
{
	public static final int X = 0;
	public static final int Y = 1;
	private static Vector buffer = new Vector (0,0,0); 
	public int type;
	public float speed;
	public float slow;
	public Vector x;
	public Vector y;
	public Vector (int type, float speed, float slow)
	{
		this.type = type;
		this.speed = speed;
		if (slow<0)
		{
			this.slow = -slow;	
		}
		else
		{
			this.slow = slow;
		}
	}
	public Vector (float speed, float slow)
	{
		this.speed = speed;
		this.slow = slow;
	}
	public Vector (Vector x, Vector y)
	{
		this.x = x;
		this.y = y;
		this.x.type = Move.X;
		this.y.type = Move.Y;
	}
	public static void swap (Vector vector1, Vector vector2)
	{
		buffer.type = vector2.type;
		buffer.speed = vector2.speed;
		buffer.slow = vector2.slow;
		vector2.type = vector1.type;
		vector2.speed = vector1.speed;
		vector2.slow = vector1.slow;
		vector1.type = buffer.type;
		vector1.speed = buffer.speed;
		vector1.slow = buffer.slow;
	}
}
