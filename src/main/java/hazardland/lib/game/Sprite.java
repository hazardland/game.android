package hazardland.lib.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;

public class Sprite
{

	//Basic resource related

	public String name;
	
	Point shift;
	Size size;
	
	private float width;
	private float height;
	
	//Frames or frame
	
	//private  Frame[] frames;
	public ArrayList<Frame> frames;
	private HashMap<Integer,Integer> skips;
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
	
	private static Vertex shape ()
	{
		return new Vertex (new float[]
				{
				1f,1f,0f,
				0f,1f,0f,
				1f,0f,0f,
				0f,0f,0f,
				});
	}
	
	public boolean skip ()
	{
		if (skips!=null && skips.get(frame)!=null)
		{
			if (hold==0)
			{
				hold = skips.get(frame);
				return false;
			}
			hold--;
			return true;
		}
		else if (skip>0)
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
	
	public Sprite (float x, float y, float width, float height, Frame[] frames)
	{
		this.size = new Size (width, height);
		this.shift = new Point (x, y);
		shape = shape ();
		center = new Point (size.width/2, size.height/2);
		if (frames==null)
		{
			this.frames = new ArrayList<Frame>();
		}
		else
		{
			this.frames = new ArrayList<Frame>(Arrays.asList(frames));
		}
	}
	
	public Sprite (String name, float x, float y, float width, float height, Frame[] frames)
	{
		this(x, y, width, height, frames);
		this.name = name;
	}	
	
	public Sprite (float x, float y, float width, float height)
	{
		this (x, y, width, height, null);
	}
	
	public Sprite (float width, float height)
	{
		this(0, 0, width, height);
	}

	public Sprite add (Scene scene, int image, float x, float y, float width, float height)
	{
		frames.add (new Frame (scene, image, x, y, width, height));
		return this;
	}

	public Sprite add (Scene scene, int image, float width, float height)
	{
		frames.add (new Frame (scene, image, 0, 0, width, height));
		return this;
	}

	public Sprite add (int image)
	{
		frames.add (new Frame (null, image, 0, 0, 1, 1));
		return this;
	}

	public void draw (GL10 gl, Position position, Scale scale)
	{
		if (!pause && play)
		{
			next();
		}
		
		if (position.size()<1)
		{
			//System.out.println ("size decrasing " + position.size());
		}
		
		width = scale.width(size.width)*position.size();
		height = scale.height(size.height)*position.size();

		gl.glPushMatrix();
		
		gl.glTranslatef (scale.width(position.x()+shift.x*position.size()), scale.height(position.y()+shift.y*position.size()), 0f); //MOVE !!! 1f is size of figure if called after scaling, 1f is pixel if called before scaling
		
		if (position.corner()!=0)
		{
			gl.glTranslatef (scale.width(center.x), scale.height(center.y), 0f);
			gl.glRotatef (position.corner(), 0f, 0f, 1f); // ROTATE !!!
			gl.glTranslatef (scale.width(-center.x), scale.height(-center.y), 0f);			
		}
		
		gl.glScalef (width, height, 0f); // ADJUST SIZE !!!

		//gl.glDisable(GL10.GL_TEXTURE_2D);
		
		// bind the previously generated texture
		gl.glBindTexture(GL10.GL_TEXTURE_2D, frames.get(frame).image);	


		// Point to our buffers
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		// set the colour for the square
		//gl.glColor4f (0.0f, 1.0f, 0.0f, 0.5f);

		// Set the face rotation
		gl.glFrontFace(GL10.GL_CW);
		
		// Point to our vertex buffer
		gl.glVertexPointer (3, GL10.GL_FLOAT, 0, shape.buffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, frames.get(frame).value.buffer);

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
		if ((play || manual) && frames.size()>1)
		{
			if (next)
			{
				if (frame==frames.size()-1)
				{
					frame = 0;
				}
				else
				{
					frame ++;
				}
				//System.out.println("frame "+frame+" of "+(frames.size()-1)+" ["+name+"]");
				if (frame==frames.size()-1)
				{
					//System.out.println("frame last of "+(frames.size()-1)+" ["+name+"]");
					if (reverse)
					{
						next = false;
					}
					if (limit())
					{
						return;
					}
				}
			}
			else
			{
				frame --;
				//System.out.println("frame "+frame+" of "+(frames.size()-1)+" ["+name+"]");				
				if (frame==0)
				{
					//System.out.println("frame first of "+(frames.size()-1)+" ["+name+"]");
					if (reverse)
					{
						next = true;
					}
					if (limit())
					{
						return;
					}
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
//		if (!play && frame==frames.size()-1)
//		{
//			if (!reverse)
//			{
//				frame = 0;
//				System.out.println ("reseting frame to 0");
//			}
//			else
//			{
//				next = false;
//			}
//		}
		hold = 0;
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
		hold = 0;
		frame = frames.size()-1;
	}
	
	public void first ()
	{
		hold = 0;
		frame = 0;
	}
	
	public void skip (int frame, int time)
	{
		if (skips==null)
		{
			skips = new HashMap<Integer,Integer>();
		}
		skips.put(frame, time);
	}
	
}
