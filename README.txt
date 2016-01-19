To run the program, open an ANSI terminal (basically not Windows Command Prompt) and execute:
$ stty raw
$ javac jPaint.java
$ java jPaint
Once you are in the program, controls:

Initialization phase:
    wasd to resize easel
    l to load from a file in the saves directory
    Loading screen:
        Type to name file
	Enter to select file name

Main screen:
    wasdqezc to move cursor and paint if brush is down
    p to switch brush state (down or up)
    f to go into the color menu
    v to go into the save screen
    Color menu:
        0-7 to select a color
    Save screen:
        Type to name file
	Enter to select file name
