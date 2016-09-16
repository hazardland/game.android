game.android
================

27 class 2d game framework for any Android version supporting OpenGL ES 1.0

Checkut a simple game source using this framework at https://github.com/hazardland/ferry.android


Requirements
------------

1. Just include library

Usage
-----
We create cloud using resource image located at R.drawable.cloud2
```JAVA
    public class Cloud2 extends Entity
    {
        public Cloud2 (Scene scene)
        {
            super (scene, -1, 500, 600, 243, 52, R.drawable.cloud2);
            job (new Move (Move.X, 0-size.width, scene.screen.width, -5, 0, false, 0));
        }
    }
```
We create scene
```JAVA    
    public class Scene2 extends Scene
    {
        protected void onCreate (Bundle state)
        {
            config.sensor = false;
            create (state);
        }

        //initialize resources for loader progress bar
        public void open (GL10 gl)
        {
            image (gl, R.drawable.progress_background);
            image (gl, R.drawable.progress_foreground);
        }
        //progress loading, progress loading, progress loading...
        public void load (GL10 gl)
        {
            square.draw (gl, images.get (R.drawable.progress_background), display.width/2-206, display.height/2-20, 412f, 40f);
            square.draw (gl, images.get (R.drawable.progress_foreground), display.width/2-200, display.height/2-10, world.load()*4, 20, 0, 0f, 1f, 0f, 1f);     
        }
        //here is actually what we load:
        public void load ()
        {
            image (R.drawable.sky);
            image (R.drawable.bird1);
            image (R.drawable.bird2);
            image (R.drawable.bird3);
            image (R.drawable.cloud1);
            //We load cloud image as well as other images
            image (R.drawable.cloud2);
            image (R.drawable.cloud3);
            hold ();
            
            //This is some class like cloud extending Entity class
            new Background (this);
            new Cloud1 (this);
            //Here is our class initing
            new Cloud2 (this);
            new Cloud3 (this);
            new Cloud4 (this);
            
            //Throw our Entity child classes into eternal world loop for drawing
            //As world will extract coordinates and staff from them and draw them on the screen
            //And world will also notify them that new frame (FPS) redraw is going to happen and
            //it is time to change coordinates and staff if needed
            world.start ();
        }
    } 
```