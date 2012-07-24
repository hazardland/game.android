package hazardland.lib.game;

import javax.microedition.khronos.opengles.GL10;

public class Sprite
{

	//Basic resource related
	
	int image;
	
	Point shift;
	Size size;
	
	private float width;
	private float height;
	
	//Frames or frame
	
	private  Frame[] frames;
	private Vertex shape;

	//Sprite related properties
	
	private int frame = 0 ;
	public boolean reverse = false;
	private boolean next = true;
	public boolean play = true;
	public int limit = 0;
	private int current = 0;

	boolean debug = true;

	
	public Sprite (int image, float x, float y, float width, float height, Frame[] frames)
	{
		this.image = image;
		this.size = new Size (width, height);
		this.shift = new Point (x, y);
		this.frames = frames;
		shape = new Vertex (new float[]
				{
				1f,1f,0f,
				0f,1f,0f,
				1f,0f,0f,
				0f,0f,0f,
				});
	}
	
	public void draw (GL10 gl, Position position, Scale scale)
	{
		next();
		
		width = scale.width(size.width)*position.size;
		height = scale.height(size.height)*position.size;

		gl.glPushMatrix();
		
		gl.glTranslatef (scale.width(position.x+shift.x*position.size), scale.height(position.y+shift.y*position.size), 0f); //MOVE !!! 1f is size of figure if called after scaling, 1f is pixel if called before scaling
		
		if (position.corner!=0)
		{
			gl.glTranslatef (width/2, height/2, 0f);
			gl.glRotatef (position.corner, 0f, 0f, 1f); // ROTATE !!!
			gl.glTranslatef (-width/2, -height/2, 0f);			
		}
		
		gl.glScalef (width, height, 0f); // ADJUST SIZE !!!

		// bind the previously generated texture
		gl.glBindTexture(GL10.GL_TEXTURE_2D, image);

		// Point to our buffers
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		// set the colour for the square
		//gl.glColor4f (0.0f, 1.0f, 0.0f, 0.5f);

		// Set the face rotation
		gl.glFrontFace(GL10.GL_CW);
		
		// Point to our vertex buffer
		gl.glVertexPointer (3, GL10.GL_FLOAT, 0, shape.buffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, frames[frame].value.buffer);

		// Draw the vertices as triangle strip
		gl.glDrawArrays (GL10.GL_TRIANGLE_STRIP, 0, shape.vertex.length / 3);

		// Disable the client state before leaving
		gl.glDisableClientState (GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		gl.glPopMatrix();		
		
	}
	
	public void next ()
	{
		if (play && frames.length>1)
		{
			if (frames.length==16)
			{
				//System.out.println("trying to incrase position");
			}				
			if (frame==frames.length-1)
			{
				limit ();
				if (reverse==true)
				{
					frame--;
					next = false;						
				}
				else
				{
					frame = 0;
					next = true;
				}
			}
			else if (frame==0)
			{
				if (!next)
				{
					limit ();
				}
				frame++;
				next = true;				
			}
			else
			{
				if (next)
				{
					frame ++;
				}
				else
				{
					frame --;
				}
			}
		}
	}
	
	private boolean limit ()
	{
		if (limit>0)
		{
			current++;
		}
		if (limit>0 && current==limit)
		{
			pause ();
			return true;
		}
		return false;
	}
	
	public void play ()
	{
		play = true;
	}
	
	public void pause ()
	{
		play = false;
		current = 0;
	}
	
	public void last ()
	{
		frame = frames.length-1;
	}
	
	public void first ()
	{
		frame = 0;
	}
	
}
