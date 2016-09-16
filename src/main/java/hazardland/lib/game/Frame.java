package hazardland.lib.game;

public class Frame
{
	public Integer image = null;
	public Vertex value;
	public Frame (Scene scene, int image, float x, float y, float width, float height)
	{
		if (scene!=null)
		{
			Scale scale = new Scale (new Size(scene.width(image),scene.height(image)), new Size(1, 1));
			this.image = scene.image(image);			
			x = scale.width(x);
			y = scale.height(y);
			width = scale.width(width);
			height = scale.height(height);
		}
		else
		{
			this.image = image;
		}
		value = new Vertex (new float[]
				{
				x+width, y,
				x, y,
				x+width, y+height,
				x, y+height,
				});
		
	}
	
	public static Frame[] generate (Scene scene, int image, Integer lines, Integer length, Integer limit, float x, float y, float width, float height)
	{
		if (lines==null)
		{
			lines = 1;
		}
		if (length==null)
		{
			length = 1;
		}
		if (limit==null)
		{
			limit = 1;
		}
		Frame[] result = new Frame[limit];
		int current = 0;
		for (int line = 0; line < lines; line++)
		{
			for (int frame = 0; frame < length; frame++)
			{
				//result[current] = new Frame (scale.width(x+(frame*width)), scale.height(y+(line*height)), scale.width(width), scale.height(height));
				result[current] = new Frame (scene, image, x+(frame*width), y+(line*height), width, height);
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
