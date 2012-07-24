package hazardland.lib.game;

public class Frame
{
	//public float[] vector;
	public Vertex value;
	public Frame (float x, float y, float width, float height)
	{
//		System.out.println ("creating frame "+(x+width)+","+y+",\n,"+
//				+x+","+y+",\n,"+
//				+(x+width)+","+(y+height)+",\n,"+
//				+x+","+(y+height)+",\n");
		
		value = new Vertex (new float[]
				{
				x+width, y,
				x, y,
				x+width, y+height,
				x, y+height,
				});
		
	}
	
	public static Frame[] generate (int lines, int length, int limit, float x, float y, float width, float height, Scale scale)
	{
		Frame[] result = new Frame[limit];
		int current = 0;
		for (int line = 0; line < lines; line++)
		{
			for (int frame = 0; frame < length; frame++)
			{
				result[current] = new Frame (scale.width(x+(frame*width)), scale.height(y+(line*height)), scale.width(width), scale.height(height));
				current++;
				if (current==limit)
				{
					return result;
				}
			}
		}
		return result;
	}
}
