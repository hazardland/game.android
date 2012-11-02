package hazardland.lib.game;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.R.anim;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;

/**
 *the dark class does all job in dirt for you with opengl
 *@author BioHazard
 *
 */
@SuppressLint ("UseSparseArrays")
public class Scene extends Activity implements Renderer,OnTouchListener,SensorEventListener,Runnable
{
	/**
	 * use this for scene identify purposes
	 */
	public String name = "";
	
	/**
	 * the view of gl
	 */
	public GLSurfaceView view;
	/**
	 * sensor provider object
	 */
	private SensorManager sensor;
	/**
	 * accelerometer privider object
	 */
	private Sensor accelerometer;
	/**
	 * sound provider object
	 */
	private  SoundPool sound;
	//private AudioManager volume;
	
	/**
	 * the game loader thread
	 */
	private Thread load;

	/**
	 * resource provider object
	 */
	public Resources resources;
	/**
	 * holds resourse id opengl texture id mapping
	 */
	public SparseIntArray images = new SparseIntArray ();
	/**
	 * holds resource id sound id mappings
	 */
	public SparseIntArray sounds = new SparseIntArray ();
	/**
	 * holds music objects
	 */
	public Map <Integer, MediaPlayer> musics = new HashMap <Integer, MediaPlayer> ();
	/**
	 * holds input objects as many as fingers touching screen
	 */
	public Map <Integer, Input> inputs = new HashMap <Integer, Input> ();
	/**
	 * hodls bitmaps while load thread active and while not binded
	 */
	public Map <Integer, Bitmap> bitmaps = new ConcurrentHashMap <Integer, Bitmap> ();
	/**
	 * hodls all sizes of loaded textures
	 */
	public Map <Integer, Size> sizes = new HashMap <Integer, Size> ();

	/**
	 * converts screen width and height or x and y from screen virtual size to display phisical size
	 */
	public Scale scale;
	/**
	 * converts input width and height or x and y from display phisical size to screen size
	 */
	public Scale input;
	/**
	 * hods virtual size of screen
	 */
	public Size screen;
	/**
	 * holds devices physical size of display
	 */
	public Size display;
	/**
	 * the world of scene wich is responsible for delivering all input events and calculating hits and sending pause events to entities
	 */
	public World world;
	
	/**
	 * if screen is resize maintaining ascept ratio shift stores y axis shift amount value
	 */
	private float shift;
	
	/**
	 * simple square object can be used for drawing squares or textured squares
	 */
	public Square square = new Square ();
	/**
	 * defines if drawing has begin
	 */
	private boolean active = false;
	/**
	 * defines if scene is paused
	 */
	public boolean pause = false;
	
	/**
	 * holds scene config
	 */
	public Config config = new Config ();
	
	private boolean ready = false;
	
	private int last;
	
	public boolean finish = true;
	
    /**
     * call this method to init the scene from activity onCreate method
     * @param state
     * its a bundle provided on onCreate method
     */
	
	public void receive (Bundle state)
	{
		if (state==null)
		{
			return;
		}
		if (state.containsKey("sound"))
		{
			config.sound = state.getBoolean("sound");
		}
		if (state.containsKey("music"))
		{
			config.music = state.getBoolean("music");
		}		
		if (state.containsKey("text"))
		{
			config.text = state.getBoolean("text");
		}
	}

    public void create (Bundle state)
    {
    	super.onCreate (state);
    	receive (getIntent().getExtras());
    	if (config.fullscreen)
    	{
    		requestWindowFeature(Window.FEATURE_NO_TITLE);
    		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	}
    	if (config.orientation==Config.LANDSCAPE)
    	{
    		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    	}
    	else
    	{
    		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    	}
		view = new GLSurfaceView(this);
		view.setRenderer(this);
        setContentView(view);
        view.setOnTouchListener (this);
		this.resources = getResources ();
		this.screen = new Size (config.width, config.height);
		//this.strech = config.strech;

        if (config.sound)
        {
        	sound = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
            //volume = (AudioManager) getSystemService (AUDIO_SERVICE);
        }
		if (config.sensor)
		{
			sensor = (SensorManager)getSystemService(SENSOR_SERVICE);
	        accelerometer = sensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		}        
    }
    
	@Override
	public void onDrawFrame (GL10 gl)
	{
		if (bitmaps.size()>0)
		{
			if (!active)
			{
				active = true;
			}
			for (int resource: bitmaps.keySet ())
			{
				image (gl, resource);
			}
		}		
//		// clear Screen and Depth Buffer
		gl.glClear (GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
//		// Reset the Modelview Matrix
		gl.glLoadIdentity ();
		if (world.load()==100)
		{
			draw (gl);
			//System.out.println ("world entity count "+world.subjects.size ());
		}
		else
		{
			load (gl);
		}
		if (config.display==Config.FIT && shift>0)
		{
			square.draw (gl, 0, 0, -shift, display.width, shift, 0);
			square.draw (gl, 0, 0, display.height, display.width, shift, 0);
		}
	}

	@Override
	public void onSurfaceChanged (GL10 gl, int width, int height)
	{
		if (ready)
		{
			return;
		}
		
		if (config.display==Config.FIT)
		{
			display = new Size (width, width/(screen.width/screen.height));
			shift = (height-display.height)/2;
		}
		else if (config.display==Config.STRETCH)
		{
			display = new Size (width, height);
		}
		
		scale = new Scale (new Size (screen.width, screen.height), new Size (display.width, display.height));
		input = new Scale (new Size (display.width, display.height), new Size (screen.width, screen.height));

		//gl.glViewport (0, 0, (int)display.width, (int)display.height); // Reset The Current Viewport
		gl.glViewport (0, 0, width, height); // Reset The Current Viewport
		gl.glMatrixMode (GL10.GL_PROJECTION); // Select The Projection Matrix
		gl.glLoadIdentity (); // Reset The Projection Matrix

		gl.glOrthof (0, width, 0, height, -1f, 1f);
		
		if (config.display==Config.FIT)
		{
			gl.glTranslatef (0f, shift, 0.0f); // move the camera !!			
		}
		
		gl.glMatrixMode (GL10.GL_MODELVIEW); // Select The Modelview Matrix
		gl.glLoadIdentity (); // Reset The Modelview Matrix
		
		System.out.println ("width and height is " + display.width + "x" + display.height
				+ " scale is " + scale.width + "x" + scale.height);
		System.out.println ("android version is "+Build.VERSION.RELEASE);
		
		world = new World (0, 0, screen.width, screen.height);
		
		//debug ("surface changed world elements "+world.entities.size());
		
		//view.queueEvent (this);
		
		load = new Thread (this);
		load.start ();
		
		//load (gl);
		
		if (config.sensor)
		{
			//sensor.registerListener (this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
			//System.out.println ("orientation is " + getResources().getConfiguration().);
		}
		
		ready = true;
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) 
	{
		gl.glEnable(GL10.GL_TEXTURE_2D);			//Enable Texture Mapping ( NEW )
		gl.glShadeModel(GL10.GL_SMOOTH); 			//Enable Smooth Shading
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f); 	//Black Background
		gl.glClearDepthf(1.0f); 					//Depth Buffer Setup
		gl.glEnable(GL10.GL_DEPTH_TEST); 			//Enables Depth Testing
		gl.glDepthFunc(GL10.GL_LEQUAL); 			//The Type Of Depth Testing To Do
		
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL10.GL_BLEND);
		

		//Really Nice Perspective Calculations
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		
		open (gl);
		debug ("creating");
		
	}
	
	/**
	 * spread the world with pause signal
	 * every entity.pause () will be called an entity wil; deside what to do
	 * by default entity will pause all jobs and sprite plays
	 * to prevent this just override entities pause method
	 */
	
	public void pause ()
	{
		if (world!=null)
		{
			world.pause ();
		}
		pause = true;		
	}
	
	public void pause (int type)
	{
		if (world!=null)
		{
			world.pause (type);
		}
	}
	

	/**
	 * spread the world with resume signal
	 * every entity.resume () will be called an entity will deside what to do
	 * by default entity will resume all jobs and sprite plays
	 * to prevent this just override entities play method	
	 */
	public void resume ()
	{
		if (world!=null)
		{
			world.resume ();
		}
		pause = false;		
	}
	
	public void resume (int type)
	{
		if (world!=null)
		{
			world.resume (type);
		}
	}
	

	
	/**
	 * do the very first things in this method
	 * it is called after config is affected and
	 * scene is created
	 * 
	 * warn yourself that you cant draw from this method
	 * just load some first stuff
	 * like loader progress bar images or somethin
	 * @param gl
	 * also you can use that object for texture binding 
	 */
	public void open (GL10 gl)
	{
//		image (gl, R.drawable.progress_background);
//		image (gl, R.drawable.progress_foreground);		
	}
	
	
	/**
	 * this method is lounched from load thread
	 * use it for non gl loading purposes
	 * but you can also bind textures from it
	 * as gl thread is runing wich watches
	 * while you decode bitmaps for texture binding
	 * and binds them
	 */
	public void load ()
	{
//		square.draw (gl, images.get (R.drawable.progress_background), display.width/2-206, display.height/2-20, 412f, 40f);
//		square.draw (gl, images.get (R.drawable.progress_foreground), display.width/2-200, display.height/2-10, world.load()*4, 20, 0, 0f, 1f, 0f, 1f);		
	}
	
	/**
	 * goto another scene
	 * specify full class name of scene activity
	 * @param scene
	 */
	public void swap (String scene)
	{
		try
		{
			debug (scene);
			Intent intent = new Intent (this, Class.forName (scene));
			pass (intent);
			startActivity (intent);
			if (finish)
			{
				finish();
			}
			close ();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}		
	}
	
	public void pass (Intent intent)
	{
		
	}
	
	public void close ()
	{
		overridePendingTransition(anim.fade_in, anim.fade_out);		
	}
	
	/**
	 * this method is called instead of draw (gl) while
	 * you do world.start() from public void load () method
	 * (see load method without gl param)
	 * use this method for example for loader progress bar rendering
	 * @param gl
	 */
	public void load (GL10 gl)
	{
		System.out.println ("loading "+world.load()+"%");
	}
	
	/**
	 * after doing world.start () in scene.load (non gl instance method)
	 * it gl thread will call scene.draw method for drawing instead of scene.load (gl) method 
	 * @param gl
	 */
	public void draw (GL10 gl)
	{
		for (Entity entity : world.entities.values())
		{
			entity.draw (gl, scale);
		}
		if (time(last)<config.refresh)
		{
			//debug ("pausing "+(config.refresh-time(last)));
			sleep (config.refresh-time(last));
			last = time();
		}
		
	}
	
	
	/**
	 * just decode image for further texture binding
	 * usually called from non gl instance load method
	 * it prepares bitmaps for binding from parallel thread
	 * @param resource
	 * specify android drawable resource id
	 */
	public void image (int resource)
	{
		image (null, resource);	
	}

	
	/**
	 * decode bitmap and bind texture (if gl instance is presenet and load thread is not runing)
	 * or decode bitmap for further texture bind call (if gl==null)
	 * or texture bind which was previously decoded
	 * @param gl
	 * @param resource
	 */
	public void image (GL10 gl, int resource)
	{
		if (gl==null || load==null)
		{
			BitmapFactory.Options options = new BitmapFactory.Options ();
			options.inScaled = false;

			InputStream input = resources.openRawResource (resource);
			try
			{
				bitmaps.put (resource, BitmapFactory.decodeStream (input, null, options));
				sizes.put (resource, new Size (bitmaps.get(resource).getWidth (), bitmaps.get(resource).getHeight ()));
				System.out.println ("putting size");
			}
			catch (NullPointerException pointer) 
			{
				
			}
			catch (OutOfMemoryError error) 
			{
				bitmaps.clear ();
				kill (load);
				finish ();
			}
			finally
			{
				try
				{
					input.close ();
				}
				catch (IOException e)
				{

				}
			}
			System.out.println ("texture decoding id is " + bitmaps.size ());
			if (world!=null)
			{
				world.load (2);
			}
		}

		if (gl!=null)
		{
			if (bitmaps.get (resource)==null || bitmaps.get (resource).isRecycled ())
			{
				return;
			}
			int[] temp = new int[1];
			gl.glGenTextures (1, temp, 0);
			int id = temp[0];

			System.out.println ("texture binding id is " + id);

			// int id = next (gl);
			images.put (resource, id);

			gl.glBindTexture (GL10.GL_TEXTURE_2D, id);

			gl.glTexParameterf (GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
			gl.glTexParameterf (GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

			gl.glTexParameterf (GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
			gl.glTexParameterf (GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

			gl.glTexEnvf (GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);

			GLUtils.texImage2D (GL10.GL_TEXTURE_2D, 0, bitmaps.get (resource), 0);
			bitmaps.get (resource).recycle ();
			bitmaps.remove (resource);
			if (world!=null)
			{
				world.load (2);
			}
		}
	}
	
	public int text (GL10 gl, int resource, String text, int size, String font)
	{
		
		Paint paint = new Paint();
		paint.setTextSize (size);
		paint.setAntiAlias (true);
		if (font!=null)
		{
			paint.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/"+font+".ttf"));
		}
		paint.setARGB (0xff, 0x00, 0x00, 0x00);

		String[] lines = text.split("[\\n]");
		
		float width = 0;
	    float height = 0;
	    float string = 0;
	    Rect bound = new Rect();
	    
		Paint.FontMetrics metrics = paint.getFontMetrics();
		
		for (String line : lines) 
		{
			paint.getTextBounds(line, 0, line.length(), bound);
			if (bound.right>width)
			{
				width = bound.right;
			}
			if (bound.bottom+(-1)*bound.top>string)
			{
				string = bound.bottom+(-1)*bound.top;
			}			
			height += bound.bottom+(-1)*bound.top+metrics.leading+10; 
			debug ("bound top:"+bound.top+" left:"+bound.left+" bottom:"+bound.bottom+" right:"+bound.right+" therefore height: "+(bound.bottom+(-1)*bound.top+metrics.leading));
		}

		Bitmap bitmap = Bitmap.createBitmap ((int)width+100, (int)height+100, Bitmap.Config.ARGB_4444);
		Canvas canvas = new Canvas (bitmap);
		bitmap.eraseColor(Color.RED);
		
		int position = 0;
		for (String line : lines) 
		{
			position++;
			canvas.drawText (line, 0, (int) ((string+metrics.leading)*position), paint);
		}

		int[] temp = new int[1];
		gl.glGenTextures (1, temp, 0);
		int id = temp[0];

		images.put (resource, id);

		gl.glBindTexture (GL10.GL_TEXTURE_2D, id);

		gl.glTexParameterf (GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf (GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

		gl.glTexParameterf (GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf (GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

		gl.glTexEnvf (GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);

		GLUtils.texImage2D (GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		
		sizes.put (resource, new Size (bitmap.getWidth (), bitmap.getHeight ()));
		
		bitmap.recycle ();
		
		return id;
	}
	
	
	/**
	 * caution: use it after you finish image loads (in scenl.load() method)
	 * it will hold until all decoded bitmaps are binded and recycled
	 * otherwise you will get black figures without textures
	 * 
	 */
	public void hold ()
	{
		if (active)
		{
			while (bitmaps.size()>0)
			{
				sleep (5);
				//System.out.println ("holding "+bitmaps.size());
			}
		}
		else
		{
			finish ();
		}
	}

	@Override
	public boolean onTouch (View v, MotionEvent event)
	{
		if (world==null || !ready || !world.touch)
		{
			return false;
		}
		if (event.getAction()==MotionEvent.ACTION_DOWN || event.getAction ()==MotionEvent.ACTION_POINTER_DOWN || event.getAction ()==MotionEvent.ACTION_POINTER_2_DOWN || event.getAction ()==MotionEvent.ACTION_POINTER_3_DOWN || event.getAction ()==MotionEvent.ACTION_POINTER_1_DOWN)
		{
			inputs.put (Input.id (event), new Input(Input.id (event),world));
			inputs.get (Input.id (event)).click (input.width (event.getX (Input.index (event))), screen.height-input.height (event.getY(Input.index (event))-shift));
		}
		else if (event.getAction()==MotionEvent.ACTION_MOVE)
		{
			for (int index=0; index<event.getPointerCount(); index++)
			{
				if (inputs.get (event.getPointerId(index))!=null)
				{
					inputs.get (event.getPointerId(index)).drag (input.width (event.getX (index)), screen.height-input.height (event.getY(index)-shift));
				}
			}
		}
		else if (event.getAction()==MotionEvent.ACTION_UP || event.getAction ()==MotionEvent.ACTION_POINTER_UP || event.getAction ()==MotionEvent.ACTION_POINTER_2_UP || event.getAction ()==MotionEvent.ACTION_POINTER_3_UP || event.getAction ()==MotionEvent.ACTION_POINTER_1_UP)
		{
			if (inputs.get (Input.id(event))!=null)
			{
				inputs.get (Input.id(event)).stop (input.width (event.getX (Input.index (event))), screen.height-input.height (event.getY(Input.index (event))-shift));
				inputs.remove (Input.id (event));
			}
		}
		else if (event.getAction ()==MotionEvent.ACTION_CANCEL)
		{
			for (Input input : inputs.values ())
			{
				input.stop (this.input.width (event.getX (Input.index (event))), screen.height-this.input.height (event.getY(Input.index (event))-shift));
			}			
			inputs.clear ();
		}
		return true;
	}
	
	@Override
	public void onAccuracyChanged (Sensor sensor, int accuracy)
	{
		
	}

	@Override
	public void onSensorChanged (SensorEvent event)
	{
		if (world==null || !ready || !world.sensor)
		{
			//debug ("returning "+ready);
			return;
		}
		if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER)
		{
			if (Build.VERSION.SDK_INT<15)
			{
				world.sensor (new Vector (Vector.X, event.values[1]*world.speed, event.values[1]*world.speed/world.slow));
				world.sensor (new Vector (Vector.Y, -event.values[0]*world.speed, event.values[0]*world.speed/world.slow));
			}
			else
			{
				world.sensor (new Vector (Vector.X, -event.values[0]*world.speed, event.values[0]*world.speed/world.slow));
				world.sensor (new Vector (Vector.Y, event.values[1]*world.speed, event.values[1]*world.speed/world.slow));
			}
		}
	}
	
	
	
	@Override
	protected void onPause ()
	{
		super.onPause ();
		bitmaps.clear ();
		kill (load);
		if (config.sensor)
		{
			sensor.unregisterListener (this);
		}
//		if (config.sound)
//		{
//			sound.release ();
//		}
//		for (MediaPlayer music : musics.values ())
//		{
//			music.release ();
//		}
//		pause = true;
		pause ();
		debug ("pausing");
	}

	@Override
	protected void onResume ()
	{
		super.onResume ();
		if (config.sensor)
		{
			sensor.registerListener (this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
		}
        //pause = false;
        resume ();
        debug ("resuming");
	}
	
	/**
	 * prepare sound resource
	 * @param sound
	 */
	public void sound (int sound)
	{
		if (config.sound)
		{
			sounds.put (sound, this.sound.load(getBaseContext(), sound, 1));
		}
	}

	/**
	 * do some action with sounds see constants of class Sound
	 * @param sound
	 */
	public int sound (int action, int sound)
	{
		return sound (action, sound, 0);
	}
	
	public int sound (int action, int sound, float config)
	{
		int result = 0;		
		if (this.config.sound)
		{
	//		float volume = this.volume.getStreamVolume(AudioManager.STREAM_MUSIC);
	//		volume = volume / this.volume.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			switch (action)
			{
				case Sound.PLAY:
					result = this.sound.play (sounds.get(sound), 1, 1, 1, (int)config, 1f);
				break;
				case Sound.STOP:
					System.out.println ("stopping play "+sound);
					this.sound.stop (sound);
				break;
			}
		}
		return result;
	}
	
	public void music (int music)
	{
		if (this.config.music)
		{
			musics.put (music, MediaPlayer.create (getBaseContext(), music));
		}
	}

	public void music (int action, int music)
	{
		music (action, music, 0);
	}
	
	public void music (int action, int music, float config)
	{
		if (this.config.music)
		{
			if (musics.get(music)!=null)
			{
				switch (action)
				{
					case Music.PLAY:
						if (musics.get(music).isPlaying ())
						{
							musics.get(music).pause ();
							musics.get (music).seekTo (0);
						}
						if (config>0)
						{
							musics.get(music).setLooping (true);
						}
						musics.get(music).start ();
					break;
					case Music.STOP:
						musics.get(music).pause ();
						musics.get (music).seekTo (0);
						musics.get(music).setLooping (false);
					break;
					case Music.PAUSE:
						musics.get(music).pause ();
					break;				
					case Music.VOLUME:
						musics.get(music).setVolume (config, config);
					break;				
				}			
			}
		}
	}
	
	public void music (int music, Music job)
	{
		if (musics.get(music)!=null)
		{
			musics.get(music).setOnCompletionListener (job);
		}
	}
	
	@Override
	public void run ()
	{
		load ();
	}
	
	/**
	 * sleep some
	 * @param time
	 */
	public void sleep (int time)
	{
		try
		{
			java.lang.Thread.sleep (time);
		}
		catch (InterruptedException e)
		{

		}		
	}
	
	/**
	 * kill some thread
	 * @param thread
	 */
	public void kill (Thread thread)
	{
		if (thread!=null && thread.isAlive ())
		{
			thread.interrupt ();
			try
			{
				thread.join ();
			}
			catch (InterruptedException e)
			{
				Thread.currentThread ().interrupt ();
			}
			thread = null;
		}
	}
	
	public void debug (String string)
	{
		if (name!="")
		{
			System.out.println ("hazardland("+name+"): "+string);
		}
		else
		{
			System.out.println ("hazardland: "+string);
		}
	}
	
	public int time (int from)
	{
		if (from==0)
		{
			return 0;
		}
		return (int) System.currentTimeMillis()-from;
	}
	
	public int time ()
	{
		return (int) System.currentTimeMillis();
	}

	@Override
	protected void onDestroy ()
	{
		super.onDestroy ();
		debug ("destroing");
	}
	

//
//    public Bitmap SavePixels(GL10 gl)
//            {
//                int b[] = new int[Width * Height];
//                IntBuffer ib = IntBuffer.wrap(b);
//                ib.position(0);
//                gl.glReadPixels(0, 0, Width, Height, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, ib);
//     
//                // The bytes within the ints are in the wrong order for android, but convert into a
//                // bitmap anyway. They're also bottom-to-top rather than top-to-bottom. We'll fix
//                // this up soon using some fast API calls.
//                Bitmap glbitmap = Bitmap.createBitmap(b, Width, Height, Bitmap.Config.ARGB_4444);
//                ib = null; // we're done with ib
//                b = null; // we're done with b, so allow the memory to be freed
//     
//                // To swap the color channels, we'll use a ColorMatrix/ColorMatrixFilter. From the Android docs:
//                //
//                // This is a 5x4 matrix: [ a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t ]
//                // When applied to a color [r, g, b, a] the resulting color is computed as (after clamping):
//                //
//                // R' = a*R + b*G + c*B + d*A + e;
//                // G' = f*R + g*G + h*B + i*A + j;
//                // B' = k*R + l*G + m*B + n*A + o;
//                // A' = p*R + q*G + r*B + s*A + t;
//                //
//                // We want to swap R and B, so the coefficients will be:
//                // R' = B => 0,0,1,0,0
//                // G' = G => 0,1,0,0,0
//                // B' = R => 1,0,0,0,0
//                // A' = A => 0,0,0,1,0
//     
//                final float[] cmVals = { 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0 };
//     
//                Paint paint = new Paint();
//                paint.setColorFilter(new ColorMatrixColorFilter(new ColorMatrix(cmVals))); // our R<->B swapping paint
//     
//                Bitmap bitmap = Bitmap.createBitmap(Width, Height, Config.ARGB_4444); // the bitmap we're going to draw onto
//                Canvas canvas = new Canvas(bitmap); // we draw to the bitmap through a canvas
//                canvas.drawBitmap(glbitmap, 0, 0, paint); // draw the opengl bitmap onto the canvas, using the color swapping paint
//                glbitmap = null; // we're done with glbitmap, let go of its memory
//     
//                // the image is still upside-down, so vertically flip it
//                Matrix matrix = new Matrix();
//                matrix.preScale(1.0f, -1.0f); // scaling: x = x, y = -y, i.e. vertically flip
//                return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true); // new bitmap, using the flipping matrix
//            }
     	
}
