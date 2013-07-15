package hazardland.lib.game;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Vertex
{
	public FloatBuffer buffer; // buffer holding the vertices
	public float vertex[];
	public Vertex (float[] vertex)
	{
		this.vertex = vertex;
		// a float has 4 bytes so we allocate for each coordinate 4 bytes
		ByteBuffer factory = ByteBuffer.allocateDirect (vertex.length * 4);
		factory.order (ByteOrder.nativeOrder ());
		// allocates the memory from the byte buffer
		buffer = factory.asFloatBuffer ();
		// fill the vertexBuffer with the vertices
		buffer.put (vertex);
		// set the cursor position to the beginning of the buffer
		buffer.position (0);			
	}
}
