package hazardland.lib.game;

import javax.microedition.khronos.opengles.GL10;

public class Square
{
	Vertex shape,texture;

	public Square()
	{
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
	
	
	public void draw (GL10 gl, int image, Scale scale, float x, float y, float width, float height)
	{
		draw (gl, image, scale, x, y, width, height, 0f, 0f, 0f, 0f, 1f);
	}

	
	public void draw (GL10 gl, int image, Scale scale, float x, float y, float width, float height, float corner)
	{
		draw (gl, image, scale, x, y, width, height, corner, 0f, 0f, 0f, 1f);
	}

	/** The draw method for the square with the GL context */
	public void draw (GL10 gl, int image, Scale scale, float x, float y, float width, float height, float corner, float red, float green, float blue, float alpha)
	{
		if (scale==null)
		{
			scale = new Scale(new Size(1,1), new Size(1,1));
		}
		gl.glPushMatrix();
		
		gl.glTranslatef (scale.width(x), scale.height(y), 0f); //MOVE !!! 1f is size of figure if called after scaling, 1f is pixel if called before scaling
		
		if (corner!=0)
		{
			gl.glTranslatef (scale.width(width/2), scale.height(height/2), 0f);
			gl.glRotatef (corner, 0f, 0f, 1f); // ROTATE !!!
			gl.glTranslatef (scale.width(-width/2), scale.height(-height/2), 0f);			
		}
		
		gl.glScalef (scale.width(width), scale.height(height), 0f); // ADJUST SIZE !!!

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
}
