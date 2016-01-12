public class jPaint{
    
    /*****************
     INSTANCE VARIABLES
     *****************/
    
    private String[][] easel;
    
    private int cursorX, cursorY;

    private boolean cursorDown;

    private String mode;

    private String color; //current color

    //ANSI escape sequences
    public static final String ANSI = "\033[";
    
    public static final String CLEAR = "\033c"; //special for reasons 

    //BACKGROUND COLORS
    public static final String BLACK = ANSI+"40m";
    public static final String RED = ANSI+"41m";
    public static final String GREEN = ANSI+"42m";
    public static final String YELLOW = ANSI+"43m";
    public static final String BLUE = ANSI+"44m";
    public static final String PINK = ANSI+"45m";
    public static final String CYAN = ANSI+"46m";
    public static final String WHITE = ANSI+"47m";

    //FOREGROUND COLORS
    public static final String CBLACK = ANSI+"30m";
    public static final String CWHITE = ANSI+"37m";
    
    public static final String RESET = ANSI+"0m";

    /**************
     *CONSTRUCTORS*
     **************/
    
    public jPaint(){
	this(16, 16);
    }

    public jPaint(int height, int width){
	easel =  new String[height][width];
	mode = "main";
	color = WHITE;
	
    }


    /*********
     *METHODS*
     *********/

    //Handles input to do certain things in certain modes, returns if input is valid
    public boolean input (int i){
	char key = (char) i;
	if(key == ' ') System.exit(0); //stops program
	if(mode.equals("main")) {
	    if(key == 'c') {
		mode = "color";
		return true;
	    }
	}
	else if(mode.equals("color")) {
	    int num =  Character.getNumericValue(key); //if key is not a number, gives -1
	    if(num == -1 || num > 7) { //0-7 selection
		return false;
	    }
	    //else:
	    color = ANSI + "4" + num + "m"; //sets the color to correct ANSI sequence
	    mode = "main";
	    return true;
	}
        return false;
    }
    
    
    /******
     *MAIN*
     ******/
    
    public static void main(String[] args){
	jPaint inst = new jPaint();
	System.out.println(CLEAR);
	while(true) { //MAIN LOOP
	    int keyCode = 0;
	    try {
		keyCode = System.in.read(); //get input to start
	    } catch(Exception e) {
		continue; //no input
	    }
	    if(!inst.input(keyCode)) continue; //input returned false, invalid input 
	    System.out.println(CLEAR); //now start drawing stuff, clear first
	    if(inst.mode == "main") {
		String tColor = CWHITE;
		if(inst.color.equals(WHITE)) tColor = CBLACK;
	        System.out.println(inst.color + tColor + "O"); //temporary
	    }
	    
	    else if(inst.mode == "color") {
		System.out.println("COLOR MODE"); //also temp
	    }

	}
    }
}

