hazardland
================

33 class 2d game framework for android using opengl es 1.0


Requirements
------------

just include library
better multitouch handling support from android 2.3.x

Usage
-----
we create cloud

    public class Cloud2 extends Entity
    {
        public Cloud2 (Scene scene)
        {
            super (scene, -1, 500, 600, 243, 52, R.drawable.cloud2);
            job (new Move (Move.X, 0-size.width, scene.screen.width, -5, 0, false, 0));
            }
    }

we create scene
    
    public class Scene2 extends Scene
    {
        protected void onCreate (Bundle state)
        {
            config.sensor = false;
            create (state);
        }
        public void open (GL10 gl)
        {
            image (gl, R.drawable.progress_background);
            image (gl, R.drawable.progress_foreground);
        }
        public void load (GL10 gl)
        {
            square.draw (gl, images.get (R.drawable.progress_background), display.width/2-206, display.height/2-20, 412f, 40f);
            square.draw (gl, images.get (R.drawable.progress_foreground), display.width/2-200, display.height/2-10, world.load()*4, 20, 0, 0f, 1f, 0f, 1f);     
        }
        public void load ()
        {
            image (R.drawable.s2_foni);
            image (R.drawable.s2_grubeli1);
            image (R.drawable.s2_grubeli2);
            image (R.drawable.s2_grubeli3);
            image (R.drawable.s2_grubeli4);
            image (R.drawable.s2_chiti1);
            image (R.drawable.s2_chiti2);
            image (R.drawable.s2_chiti3);
            image (R.drawable.s2_satvale);
            hold ();
            
            new Background (this);
            new Cloud1 (this);
            new Cloud2 (this);
            new Cloud3 (this);
            new Cloud4 (this);
    
            world.start ();
        }
    } 