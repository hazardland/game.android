package hazardland.lib.game;

public class Point
{
	public float x;
	public float y;
	public int time;
	public Point (float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	public Point (float x, float y, int time)
	{
		this.x = x;
		this.y = y;
		this.time = time;
	}

}