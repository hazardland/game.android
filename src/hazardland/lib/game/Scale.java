package hazardland.lib.game;

public class Scale
{
	public float width;
	public float height;
	public Scale (Size from, Size to)
	{
		this.width = to.width/from.width;
		this.height = to.height/from.height;
	}
	public float width (float width)
	{
		return width*this.width; 
	}
	public float height (float height)
	{
		return height*this.height; 
	}
	
}
