To run the program, open an ANSI terminal (basically not Windows Command Prompt) and execute:
$ stty raw
$ javac jPaint.java
$ java jPaint
Once you are in the program, controls:

Initialization phase:
    Creating a new jPaint
         WASD to resize easel
	 ENTER when done
	 
    Loading an old jPaint (Press L)
        Type to name file
	No file extension necessary
	ENTER when done typing

Main screen:
    WASDQEZC to move cursor and paint if brush is down

    X to lift or place the brush
        By default the brush is up, when it is down you can paint with the current color

    R to go into the color menu
        0-7 to select a color for painting
	Will close when done

    V to go into the save screen
      	Type to name file
	Enter to select file name

    F to fill
      	Turns all connected tiles of the same color (as the one under the cursor) to the brush color

    B to change the size and shape of your brush
        0-9 to change the radius of the brush
	C to use a circle brush
	S to use a square brush  	
