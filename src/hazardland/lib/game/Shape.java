package hazardland.lib.game;

import javax.microedition.khronos.opengles.GL10;

public class Shape
{
	public Position position;
	public float x;
	public float y;
	public float width;
	public float height;
	public boolean visible = true;
	private Vertex shape, texture;
	private int image;
	private float alpha = 0.1f;
	private float red = 1;
	private float blue = 1;
	private float green = 1;
	public float corner = 0;
	private float size = 1;

	public Shape (Position position, float x, float y, float width, float height)
	{
		this.position = position;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		shape = new Vertex (new float[]
				{
				1f,1f,0f,
				0f,1f,0f,
				1f,0f,0f,
				0f,0f,0f,
				});
		
		texture = new Vertex (new float[]
				{
				1.0f, 0.0f,
				0.0f, 0.0f,
				1.0f, 1.0f,
				0.0f, 1.0f,
				});				
	}
	
	public void shape (float x, float y, float width, float height)
	{
		this.width = width;
		this.height = height;
		this.x = x;
		this.y = y;
	}
	
	public void image (int image)
	{
		this.image = image;
	}
	
	public void alpha (float alpha)
	{
		this.alpha = alpha;
	}
	
	public void color (float red, float blue, float green)
	{
		this.red = red;
		this.blue = blue;
		this.green = green;
	}
	
	public void corner (float corner)
	{
		this.corner = corner;
	}
	
	public float x ()
	{
		if (position!=null)
		{
			return position.x()+x;
		}
		else
		{
			return x;
		}
	}
	
	public float x (float position)
	{
		if (this.position!=null)
		{
			return position+x;
		}
		else
		{
			return x;
		}
	}	
	
	public float y ()
	{
		if (position!=null)
		{
			return position.y()+y;
		}
		else
		{
			return y;
		}
	}
	
	public float y (float position)
	{
		if (this.position!=null)
		{
			return position+y;
		}
		else
		{
			return y;
		}
	}

	public float width ()
	{
		if (position!=null)
		{		
			return position.x()+x+width/position.size();
		}
		else
		{
			return x+width;
		}
	}
	
	public float width (float position)
	{
		if (this.position!=null)
		{		
			return position+x+width/this.position.size();
		}
		else
		{
			return x+width;
		}
	}
	
	public float height ()
	{
		if (position!=null)
		{
			return position.y()+y+height/position.size();
		}
		else
		{
			return y+height;
		}
	}

	public float height (float position)
	{
		if (this.position!=null)
		{
			return position+y+height/this.position.size();
		}
		else
		{
			return y+height;
		}
	}	
	
	public float size ()
	{
		if (position!=null)
		{
			return position.size();
		}
		else
		{
			return size;
		}
	}
	
	public Shape (float x, float y, float width, float height)
	{
		this (null, x, y, width, height);
	}
	
	public boolean inside (float x, float y)
	{
		if (x>=x() && x<=width() && y>=y() && y<=height())
		{
			return true;
		}			
		return false;
	}

	public boolean inside (Point point)
	{
		if (inside(point.x, point.y))
		{
			return true;
		}
		return false;
	}

	public boolean inside (Entity entity)
	{
		if (inside(entity.shape.x(),entity.shape.y()) && inside(entity.shape.width(),entity.shape.height()))
		{
			return true;
		}
		return false;
	}

	public boolean inside (Entity entity, Point point)
	{
		if (inside(entity.shape.x(point.x),entity.shape.y(point.y)) && inside(entity.shape.width(point.x),entity.shape.height(point.y)))
		{
			return true;
		}
		return false;
	}	
	
	public void draw (GL10 gl, Scale scale)
	{
		draw (gl, scale, null);
	}
	
	public void draw (GL10 gl, Scale scale, Integer image)
	{
		if (!visible)
		{
			return;
		}
		if (scale==null)
		{
			scale = new Scale(new Size(1,1), new Size(1,1));
		}
		
		if (image==null)
		{
			image = this.image;
		}
		
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);		

		gl.glPushMatrix();
		
		gl.glTranslatef (scale.width(x()), scale.height(y()), 0f); //MOVE !!! 1f is size of figure if called after scaling, 1f is pixel if called before scaling
		
		if (corner!=0)
		{
			gl.glTranslatef (scale.width(width/2), scale.height(height/2), 0f);
			gl.glRotatef (corner, 0f, 0f, 1f); // ROTATE !!!
			gl.glTranslatef (scale.width(-width/2), scale.height(-height/2), 0f);			
		}
		
		gl.glScalef (scale.width(width/size()), scale.height(height/size()), 0f); // ADJUST SIZE !!!

		// bind the previously generated texture
		gl.glBindTexture(GL10.GL_TEXTURE_2D, image);

		// Point to our buffers
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		// set the colour for the square
		gl.glColor4f (red, green, blue, alpha);

		// Set the face rotation
		gl.glFrontFace(GL10.GL_CW);		
		
		// Point to our vertex buffer
		gl.glVertexPointer (3, GL10.GL_FLOAT, 0, shape.buffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texture.buffer);

		// Draw the vertices as triangle strip
		gl.glDrawArrays (GL10.GL_TRIANGLE_STRIP, 0, shape.vertex.length / 3);

		// Disable the client state before leaving
		gl.glDisableClientState (GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		gl.glPopMatrix();
	}
	
	public void show ()
	{
		this.visible = true;
	}
	
	public void hide ()
	{
		this.visible = false;
	}
	
}
