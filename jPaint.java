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
    
    public static final String BOLD = ANSI+"1m";

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
	for (int i = 0; i < height; i++) {
	    for(int j = 0; j < width; j++) {
		easel[i][j] = WHITE;
	    }
	}
	cursorX = 0;
	cursorY = 0;	
	cursorDown = true;
    }


    /*********
     *METHODS*
     *********/

    public static String del(int n) {
	return "\033[" + n + "D";
    }
    
    public String toString() {
	String ret = "";
	ret = (CLEAR); //now start drawing stuff, clear first
	if(mode == "main") {
	    for(int i = 0; i < easel.length; i++) {
		for(int j = 0; j < easel[0].length; j++) {
		    if(j == cursorX && i == cursorY) {
			ret += BLACK + CWHITE + BOLD + "{}";
		    }
		    else {
			ret += RESET + easel[i][j] + "  ";
		    }
		}
		ret += "\n" + del(easel[0].length*2);	
	    }
	}
	    
	else if(mode == "color") {
	    String x = RESET + CWHITE + "Choose color\n\n";
	    ret += x + del(x.length());
	    for(int i = 0; i < 8; i++) {
		ret += RESET + i + " " + ANSI + "4" + i + "m  \n" + del(4);
	    }
	}
	return ret;
    }
    //Handles input to do certain things in certain modes, returns if input is valid
    public boolean input (int i){
	char key = Character.toLowerCase((char) i);
	if(key == ' ') {
	    System.out.println(RESET);
	    System.exit(0); //stops program
	}
	if(mode.equals("main")) {
	    if("wasd".indexOf(key) != -1) {
		move(key);
		return true;
	    }
	    
	    if(key == 'c') {
		mode = "color";
		return true;
	    }

	    if(key == 'q') {
		cursorDown = !cursorDown;
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
    
    public void move(char key) {
	if (cursorDown)
	    easel[cursorY][cursorX] = color;
	
	if(cursorY > 0 && key == 'w')
	    cursorY -= 1;
	
	if(cursorY < easel.length - 1 && key == 's') 
	    cursorY += 1;

	if(cursorX > 0 && key == 'a') 
	    cursorX -= 1;
	
	if(cursorX < easel[0].length - 1 && key == 'd') 
	    cursorX += 1;

	//call paint function here
    }
    
    /******
     *MAIN*
     ******/
    
    public static void main(String[] args){
	int width = 16;
	int height = 16;
	while(true) { // sizing loop
	    int keyCode = 0;
	    try {
		keyCode = System.in.read(); //get input to start
	    } catch(Exception e) {
		continue; //no input
	    }
	    char key = Character.toLowerCase((char) keyCode);
	    if(key == 'w') {
		height -= 1;
	    }
	    else if (key == 's') {
		height += 1;
	    }
	    else if (key == 'a') {
		width -= 1;
	    }
	    else if (key == 'd') {
		width += 1;
	    }
	    else if (keyCode == 13) { //enter
		break; //we have correct width and height
	    }
	    else {
		System.out.println(keyCode);
		continue;
	    }
	    //System.out.println it
	    System.out.print(CLEAR);
	    System.out.print(RED);
	    System.out.print(del(1)); //delete red? idk but you need it
	    for(int i = 0; i < height; i++) {
		for(int j = 0; j < width; j++) {
		    System.out.print("  "); //2 chars for correctness
		}
		System.out.println();
		System.out.print(del(width*2)); //deletes all that space
	    }
	}
	
	jPaint inst = new jPaint(height,width);
	
	System.out.println(CLEAR);

	while(true) { //MAIN LOOP
	    int keyCode = 0;
	    try {
		keyCode = System.in.read(); //get input to start
	    } catch(Exception e) {
		continue; //no input
	    }
	    if(!inst.input(keyCode)) continue; //input returned false, invalid input 
	    System.out.printf(inst.toString());
	}
    }
}

