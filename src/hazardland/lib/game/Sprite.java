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
	
	public int frame = 0 ;

	public boolean reverse = false;
	private boolean next = true;
	public boolean play = true;
	public boolean pause = false;
	public int limit = 0;
	private int current = 0;
	public Point center;
	
	boolean debug = true;
	
	public int skip = 0;
	private int hold = 0;
	
	public boolean skip ()
	{
		if (skip>0)
		{
			if (hold==0)
			{
				hold = skip;
				return false;
			}
			hold--;
			return true;
		}
		return false;
	}
	
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
		center = new Point (size.width/2, size.height/2);
	}
	
	public void draw (GL10 gl, Position position, Scale scale)
	{
		if (!pause)
		{
			next();
		}
		
		if (position.size<1)
		{
			System.out.println ("size decrasing " + position.size);
		}
		
		width = scale.width(size.width)*position.size;
		height = scale.height(size.height)*position.size;

		gl.glPushMatrix();
		
		gl.glTranslatef (scale.width(position.x+shift.x*position.size), scale.height(position.y+shift.y*position.size), 0f); //MOVE !!! 1f is size of figure if called after scaling, 1f is pixel if called before scaling
		
		if (position.corner!=0)
		{
			gl.glTranslatef (center.x, center.y, 0f);
			gl.glRotatef (position.corner, 0f, 0f, 1f); // ROTATE !!!
			gl.glTranslatef (-center.x, -center.y, 0f);			
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
		next (false);
	}
	
	public void next (boolean manual)
	{
		if (skip())
		{
			return;
		}
		if ((play || manual) && frames.length>1)
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
			stop ();
			return true;
		}
		return false;
	}
	

	public void play ()
	{
		play = true;
	}

	public void resume ()
	{
		pause = false;
	}
	
	public void stop ()
	{
		play = false;
		current = 0;
	}
	
	public void pause ()
	{
		pause = true;
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
