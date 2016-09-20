package hazardland.lib.game;

import hazardland.lib.game.job.Music;
import hazardland.lib.game.job.Sound;

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
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
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
	private Thread loader;

	/**
	 * resource provider object
	 */
	private Resources resources;
	/**
	 * holds resourse id opengl texture id mapping
	 */
	private SparseIntArray images = new SparseIntArray ();
	/**
	 * holds resource id sound id mappings
	 */
	private SparseIntArray sounds = new SparseIntArray ();
	/**
	 * holds music objects
	 */
	private Map <Integer, MediaPlayer> musics = new HashMap <Integer, MediaPlayer> ();
	/**
	 * holds input objects as many as fingers touching screen
	 */
	private Map <Integer, Input> inputs = new HashMap <Integer, Input> ();
	/**
	 * hodls bitmaps while load thread active and while not binded
	 */
	private Map <Integer, Bitmap> bitmaps = new ConcurrentHashMap <Integer, Bitmap> ();
	/**
	 * hodls all sizes of loaded textures
	 */
	private Map <Integer, Size> sizes = new HashMap <Integer, Size> ();

	/**
	 * converts screen width and height or x and y from screen virtual size to display phisical size
	 */
	public Scale scale;
	/**
	 * converts input width and height or x and y from display phisical size to screen size
	 */
	private Scale input;
	/**
	 * holds virtual size of screen
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
	public float shift;
	
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
	
	private long last;
	
	public boolean finish = true;
	
	private int rotation;
	
	/**
	 * initializing viewport
	 * set window properties (width, height, fullscreen, orientation based on config)
	 * initialize touch listener if config.sensor
	 * initialize sound if config.sound
	 * initialize music if config.music
	 * @param state
     */
    public void create (Bundle state)
    {
    	super.onCreate (state);
    	restore (getIntent().getExtras());
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

	/**
	 * setup viewport
	 * and call scene.open(GL10 gl)
	 * this method is called once when after an activity is created
	 * @param gl
	 * @param config
     */
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

		//@TODO
		//gl.glDisable (GL10.GL_DITHER);


		//Really Nice Perspective Calculations
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

		open (gl);
		debug ("creating");
//		if (opengl2())
//		{
//			debug ("opengl 2 detected");
//		}
//		else
//		{
//			debug ("opengl 2 not detected");
//		}
	}

	/**
	 * do the very first things in this method
	 * it is called after config is affected and
	 * scene is created from onSurfaceCreated method
	 *
	 * warn yourself that you cant draw from this method
	 * just load some first stuff
	 * like loader progress bar images or somethin
	 * @param gl
	 * also you can use that object for texture binding
	 */
	public void open (GL10 gl)
	{
        // load very few resources here
        // like progressbar animations resources
        //
        // image (gl, R.drawable.progress_background);
        // image (gl, R.drawable.progress_foreground);
	}

	/**
	 * initialize viewport
	 * called when activity created or when activity changed i.e. rotate and etc
	 * and start loader thread
	 * world object is initialized
	 * loader thread is started
	 * @param gl
	 * @param width
	 * @param height
	 */
	@Override
	public void onSurfaceChanged (GL10 gl, int width, int height)
	{
		if (ready)
		{
			return;
		}

		rotation = getWindowManager().getDefaultDisplay().getRotation();

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
		System.out.println ("android version is "+Build.VERSION.RELEASE+" rotation is "+rotation);

		//android.view.Display



		world = new World (0, 0, screen.width, screen.height);

		//debug ("surface changed world elements "+world.entities.size());

		//view.queueEvent (this);

		loader = new Thread (this);
		loader.start ();

		//load (gl);

		if (config.sensor)
		{
			//sensor.registerListener (this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
			//System.out.println ("orientation is " + getResources().getConfiguration().);
		}

		ready = true;
	}

	/**
	 * Here is a thread method run
	 * which is executed after calling:
	 *	  load = new Thread (this);
	 *	  load.start ();
	 * from onSurfaceChanged
	 * it is called once a surface is changed
	 */
	@Override
	public void run ()
	{
		loader ();
	}


    /**
     * this method is launched from loader thread
     * use it for non gl loading purposes
     * but you can also bind textures from it
     * as gl thread is running which watches
     * while you decode bitmaps for texture binding
     * and binds them
     */
    public void loader ()
    {
        // decode (R.drawable.character_sprite_1_of_3);
        // world.loaded (33); //Notify progress to loading(GL10) - world loaded 33%
        // decode (R.drawable.character_sprite_2_of_3);
        // world.loaded (66); //Notify progress to loading(GL10) world loaded 66%
        // decode (R.drawable.character_sprite_3_of_3);

        // world.start(); //start the game
    }


    /**
	 * drawing a frame
	 * drawing happens
	 * if world.start than it is assumed that game is loaded and running
	 * therefore is ** scene.draw (GL10 gl) is called
	 * until world.start it is assumed that we are still loading
	 * therefore ** scene.loading (GL10 gl) is called
	 * ---
	 * world starts in ** scene.loader() (a load method without gl parameter)
	 * if you do not call world.start() in scene.loader()
	 * than scene.load(GL10 gl) will be called on every frame draw
	 * ---
	 * scene.loading (GL10 gl) is called every time the frame is going to draw
	 * scene.loader is called once
	 * ---
	 * while loading you can draw a loader or progressbar
	 * @param gl
     */
	@Override
	public void onDrawFrame (GL10 gl)
	{
		//debug ("image: bitmaps size "+bitmaps.values().size());
		if (bitmaps.size()>0)
		{
			if (!active)
			{
				active = true;
			}
			for (int resource: bitmaps.keySet ())
			{
				debug ("image: entering binder for resource "+resource);
                //here comes decode(R.drawable.something) from decode method for binding
				image (gl, resource);
                //@TODO try bind (gl, resource) instead of image(gl,resource)
                //because it seems that why decoding twice same image ?
                //if load thread is running it only binds it
                //assuming bitmap is already decoded
			}
		}
		else
		{
			//debug ("image: bitmaps not populated");
		}
//		// clear Screen and Depth Buffer
		gl.glClear (GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
//		// Reset the Modelview Matrix
		gl.glLoadIdentity ();
		if (world.start)
		{
			draw (gl);
			//System.out.println ("world entity count "+world.subjects.size ());
		}
		else
		{
			loading (gl);
		}
		if (config.display==Config.FIT && shift>0)
		{
			square.draw (gl, 0, null, 0, -shift, display.width, shift, 0);
			square.draw (gl, 0, null, 0, display.height, display.width, shift, 0);
		}
	}

	/**
	 * this method is called instead of draw (gl) while
	 * you do world.start() from public void load () method
	 * (see load method without gl param)
	 * use this method for example for loader progress bar rendering
	 * @param gl
	 */
	public void loading (GL10 gl)
	{
        //square.draw (gl, images.get (R.drawable.progress_background), display.width/2-206, display.height/2-20, 412f, 40f);
        //advance progressbar by drawing world.loaded() percent
        //square.draw (gl, images.get (R.drawable.progress_foreground), display.width/2-200, display.height/2-10, world.loaded()*4, 20, 0, 0f, 1f, 0f, 1f);

		//System.out.println ("loading "+world.loaded()+"%");
	}


    /**
     * after doing world.start () in scene.load (non gl instance method)
     * it gl thread will call scene.draw method for drawing instead of scene.load (gl) method
     * @param gl
     */
    public void draw (GL10 gl)
    {
//		for (Entity entity : world.entities)
//		{
//			entity.draw (gl, scale);
//		}
        for (int position=0; position<world.entities.size(); position++)
        {
            world.entities.get(position).draw(gl,scale);
        }
        if (time(last)<config.refresh*1000000)
        {
            //debug ("pausing "+((config.refresh*1000000-time(last))/1000000));
            sleep (config.refresh*1000000-time(last));
            last = time();
        }
        else
        {
            //debug ("skipping pause cause time(last) time from last sleep is "+time(last));
            last = time();
        }

    }

	/**
	 * restore media state leaved when pausing activity
	 * called from activity onCreate method
	 * @param state
	 */

	public void restore (Bundle state)
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

    /**
     * decode bitmap and bind texture (if gl instance is present and load thread is not running)
     * or decode bitmap for further texture bind call (if gl==null)
     * or texture bind which was previously decoded
     * @param gl
     * @param resource
     */
    public void image (GL10 gl, int resource)
    {
        if (gl==null || loader==null)
        {
            decode (resource);
        }

        if (gl!=null)
        {
            bind (gl, resource);
        }
    }

    /**
     * We are using decode only in scene.load()
     * which is run in independent thread
     * decode method prepares images for main thread in bitmaps array
     * resource_id => decoded image bitmap
     * and waits until onDrawFrame method binds them (in main thread)
     * ---
     * Why images are decoded in load thread and bind in onDrawFrame method ?
     * because onDrawFrame has gl instance and load thread does not
     *
     * As we are not in main thread we wait as much as onDrawFrame requires to bind previous image
     * Also we could add next decoded image as soon as possible
     * but it is not good to hold onDrawFrame longer because next frames are waiting
     * to be drawn
     *
     * @param resource
     * specify android drawable resource id
     */
    public void decode (int resource)
    {
        debug ("decoding "+resource);
        while (bitmaps.size()>1)
        {
            sleep (25);
        }
        BitmapFactory.Options options = new BitmapFactory.Options ();
        options.inScaled = false;
        InputStream input = null;

        try
        {
            input = resources.openRawResource (resource);
        }
        catch (Resources.NotFoundException error)
        {
            System.out.println ("image: resource not found "+resource);
        }

        if (input==null)
        {
            fail ();
        }

        try
        {
            bitmaps.put (resource, BitmapFactory.decodeStream (input, null, options));
            System.out.println ("image: decoded image "+resource);
        }
        catch (NullPointerException pointer)
        {
            System.out.println ("image: failed to decode image "+resource);
            fail ();
        }
        catch (OutOfMemoryError error)
        {
            System.out.println ("image: out of memory while decoding image "+resource);
            fail ();
        }
        finally
        {
            //System.out.println ("image: unknown error while decoding image "+resource);
            try
            {
                input.close ();
            }
            catch (IOException e)
            {
                System.out.println ("image: io error while closing image stream "+resource);
            }
            //fail ();
        }


        System.out.println ("image: textures left to load " + bitmaps.size ());

        if (world!=null)
        {
            world.loaded (2);
        }
    }

    /**
     * one of bitmaps failed to extract
     * recycle all previous bitmaps (basically current) from buffer
     * because onDrawFrame will try to add corrupted bitmap again and again
     */
    public void fail ()
    {
        debug ("image: global fail");
        for (Bitmap bitmap : bitmaps.values())
        {
            if (!bitmap.isRecycled())
            {
                bitmap.recycle();
            }
        }
        bitmaps.clear ();
        kill (loader);
        finish ();
    }

    /**
     * binds decoded image from bitmaps array
     * to an opengl resource and stores returned opengl resource id
     * in resources array
     * android_resource_id -> opengl_resource_id
     * @param gl
     * @param resource
     */
    public void bind (GL10 gl, int resource)
    {

        if (bitmaps.get(resource)==null || bitmaps.get(resource).isRecycled())
        {
            System.out.println ("image: bind bitmap null or recycled " + resource);
            return;
        }
        else
        {
            sizes.put (resource, new Size (bitmaps.get(resource).getWidth (), bitmaps.get(resource).getHeight ()));
        }

        Size size = sizes.get (resource);

        int[] temp = new int[1];
        gl.glGenTextures (1, temp, 0);
        int id = temp[0];

        //System.out.println ("texture binding id is " + id);

        // int id = next (gl);
        images.put (resource, id);

        System.out.println ("image: texture binding id is " + id);

        gl.glBindTexture (GL10.GL_TEXTURE_2D, id);

        gl.glTexParameterf (GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf (GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

        gl.glTexParameterf (GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf (GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        gl.glTexEnvf (GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);


        int width = (int)size.width;
        int height = (int)size.height;

        if (width!=size.width)
        {
            width += 1;
        }

        if (height!=size.height)
        {
            height += 1;
        }

        width = fix (width);
        height = fix (height);

        Bitmap resize = null;

        if (width!=size.width || height!=size.height)
        {
            //resize = Bitmap.createBitmap(bitmaps.get(resource), 0, 0, width, height);
            try
            {
                resize = Bitmap.createScaledBitmap(bitmaps.get(resource), width, height, false);
                bitmaps.get(resource).recycle();
                debug ("scale: scaling from "+size.width+"x"+size.height+" to "+width+"x"+height);
                //Bitmap.create
            }
            catch (OutOfMemoryError e)
            {

            }
        }

        if (resize!=null)
        {
            GLUtils.texImage2D (GL10.GL_TEXTURE_2D, 0, resize, 0);
            resize.recycle();
        }
        else
        {
            GLUtils.texImage2D (GL10.GL_TEXTURE_2D, 0, bitmaps.get (resource), 0);
            debug ("scale: skipped for "+size.width+"x"+size.height+" image");
        }

        //Bitmap.createBitmap(source, x, y, width, height);



        System.out.println ("image: bind success for image " + resource);

        if (bitmaps!=null && bitmaps.get(resource)!=null)
        {
            if (!bitmaps.get(resource).isRecycled())
            {
                //sizes.put (resource, new Size (bitmaps.get(resource).getWidth (), bitmaps.get(resource).getHeight ()));
                try
                {
                    bitmaps.get (resource).recycle ();
                    System.out.println ("image: putting size for " + resource);
                }
                catch (Exception e)
                {
                    System.out.println ("image: bind recycle unknow error for image " + resource);
                }
            }
            else
            {
                System.out.println ("image: strange binded image " + resource + " already recycled");
            }
            bitmaps.remove (resource);
            System.out.println ("image: removed binded resource "+resource);
        }
        else
        {
            System.out.println ("image: strange binded image " + resource + " already unset");
        }
        if (world!=null)
        {
            world.loaded (2);
        }
    }

    /**
     * opengl has its own image id while app has its own
     * to get opengls image id we use image method
     * @param resource
     * @return
     */
    public int image (int resource)
    {
        return images.get(resource);
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
				for (Bitmap bitmap : bitmaps.values())
				{
					bitmap.recycle();
				}
				bitmaps.clear();
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
	

	
//	public int text (GL10 gl, int resource, String text, int size, String font)
//	{
//		
//		Paint paint = new Paint();
//		paint.setTextSize (size);
//		paint.setAntiAlias (true);
//		if (font!=null)
//		{
//			paint.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/"+font+".ttf"));
//		}
//		paint.setARGB (0xff, 0x00, 0x00, 0x00);
//
//		String[] lines = text.split("[\\n]");
//		
//		float width = 0;
//	    float height = 0;
//	    float string = 0;
//	    Rect bound = new Rect();
//	    
//		Paint.FontMetrics metrics = paint.getFontMetrics();
//		
//		for (String line : lines) 
//		{
//			paint.getTextBounds(line, 0, line.length(), bound);
//			if (bound.right>width)
//			{
//				width = bound.right;
//			}
//			if (bound.bottom+(-1)*bound.top>string)
//			{
//				string = bound.bottom+(-1)*bound.top;
//			}			
//			height += bound.bottom+(-1)*bound.top+metrics.leading+10; 
//			debug ("bound top:"+bound.top+" left:"+bound.left+" bottom:"+bound.bottom+" right:"+bound.right+" therefore height: "+(bound.bottom+(-1)*bound.top+metrics.leading));
//		}
//
//		Bitmap bitmap = Bitmap.createBitmap ((int)width+100, (int)height+100, Bitmap.Config.ARGB_4444);
//		Canvas canvas = new Canvas (bitmap);
//		bitmap.eraseColor(Color.RED);
//		
//		int position = 0;
//		for (String line : lines) 
//		{
//			position++;
//			canvas.drawText (line, 0, (int) ((string+metrics.leading)*position), paint);
//		}
//
//		int[] temp = new int[1];
//		gl.glGenTextures (1, temp, 0);
//		int id = temp[0];
//
//		images.put (resource, id);
//
//		gl.glBindTexture (GL10.GL_TEXTURE_2D, id);
//
//		gl.glTexParameterf (GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
//		gl.glTexParameterf (GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
//
//		gl.glTexParameterf (GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
//		gl.glTexParameterf (GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
//
//		gl.glTexEnvf (GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);
//
//		GLUtils.texImage2D (GL10.GL_TEXTURE_2D, 0, bitmap, 0);
//		
//		sizes.put (resource, new Size (bitmap.getWidth (), bitmap.getHeight ()));
//		
//		bitmap.recycle ();
//		
//		return id;
//	}
	
	
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
			if (rotation==1 || rotation==3)
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
		kill (loader);
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
	 * do some action with sounds see constants of class Sound.PLAY, Sound.STOP
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

    /**
     * prepare music resource
     * @param music
     */
	public void music (int music)
	{
		if (this.config.music)
		{
			musics.put (music, MediaPlayer.create (getBaseContext(), music));
		}
	}

    /**
     * do action with music resource
     * Music.PLAY, Music.STOP, Music.PAUSE
     * @param action
     * @param music
     */
	public void music (int action, int music)
	{
		music (action, music, 0);
	}

    /**
     * use this to set music volume with action Music.VOLUME and volume float config
     * @param action
     * @param music
     * @param config
     */
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

    /**
     * attach job to music complete
     * @param music
     * @param job
     */
	public void music (int music, Music job)
	{
		if (musics.get(music)!=null)
		{
			musics.get(music).setOnCompletionListener (job);
		}
	}
	
	/**
	 * sleep some
	 * @param time
	 */
	public void sleep (long time)
	{
		try
		{
			java.lang.Thread.sleep (time/1000000);
		}
		catch (InterruptedException e)
		{

		}
		catch (IllegalArgumentException e) 
		{

		}
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
	
	public long time (long from)
	{
		if (from==0)
		{
			return 0;
		}
		
		return System.nanoTime()-from;
	}
	
	public long time ()
	{
		return System.nanoTime();
	}

	@Override
	protected void onDestroy ()
	{
		super.onDestroy ();
		debug ("destroing");
	}
	
	private int fix (int input)
	{
		int result = 2;
		while (result<input)
		{
			result *= 2;
		}
		return result;
	}
	
	public Size size (int image)
	{
		return sizes.get(image);
	}
	
	public float width (int image)
	{
		return sizes.get(image).width;
	}
	
	public float height (int image)
	{
		return sizes.get(image).height;
	}
	
	public void start ()
	{
		world.start ();
	}
}
