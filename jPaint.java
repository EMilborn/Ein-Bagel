import java.io.PrintWriter;
import java.util.Scanner;
import java.io.File;

public class jPaint{
    
    /*****************
     INSTANCE VARIABLES
     *****************/
    
    private String[][] easel;
    
    private int cursorX, cursorY;

    private boolean cursorDown;

    private String mode;

    private String color; //current color

    private String name; 

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
	cursorDown = false;
	name = "";
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
	ret += "\033[0;0H"; //set cursor pos to 0,0 just in case something is weird
	if(mode == "main") {
	    for(int i = 0; i < easel.length; i++) {
		for(int j = 0; j < easel[0].length; j++) {
		    if(j == cursorX && i == cursorY) {
			ret += BLACK + CWHITE + BOLD + "{}";
		    }
		    else {
			ret += easel[i][j] + "  ";
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
	else if(mode == "save") {
	    ret += (RESET + "Enter a name for your file: " + name);
	}
	return ret;
    }
    //Handles input to do certain things in certain modes, returns if input is valid


    public boolean input (int i){
	char key = Character.toLowerCase((char) i);
	if(mode.equals("main")) {
	    if("wasdqezc".indexOf(key) != -1) {
		move(key);
		return true;
	    }
	    if(key == ' ') {
		System.out.println(RESET);
		System.exit(0); //stops program
	    }
	    if(key == 'f') {
		mode = "color";
		return true;
	    }

	    if(key == 'p') {
		cursorDown = !cursorDown;
		return true;
	    }
	    
	    if(key == 'v') {
		mode = "save";
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
	else if(mode == "save") {
	    if(i == 127 || i == 8) { //8 and 127 are backspace and delete sometimes
		if(name.length() == 0) return false;
		name = name.substring(0,name.length()-1);
		return true;
	    }
	    else if(i == 13) {
		if(name.length() > 0) save();
		mode = "main";
		return true;
	    }
	    else {
		name += key;
		return true;
	    }
	}

        return false;
    }
    
    public String toFile() {
	String ret = "";
	ret += easel.length;
	ret += " ";
	ret += easel[0].length + " ";
	for(String[] row : easel) {
	    for(String c : row) {
		//turns out \033 is stored as a single character, so each c is X[4Ym, Y is ind 3
		ret += c.charAt(3);
	    }
	}
	return ret;
    }

    public void save() {
	try {
	    PrintWriter writer = new PrintWriter("saves/" + name,"UTF-8");
	    writer.print(toFile());
	    writer.close();
	}
	catch(Exception e) {
	    System.out.println(e);
	    System.exit(1);
	}
    }

    public void loadData(String data) { //does the actual loading behind load
	for(int i = 0; i < easel.length; i++) {
	    for(int j = 0; j < easel[0].length; j++) {
		char newcolor = data.charAt(i * easel[0].length + j); //add row length to get down rows, then add which column to get to correct column
		String old = easel[i][j];
		easel[i][j] = old.substring(0,3) + newcolor + old.substring(4); //inserts the new color
	    }
	}
    }
    
    public static jPaint load(String file) { //takes info from file "saves/file" and loads it into a new jPaint
	try {
	    Scanner input = new Scanner(new File("saves/"+file)); //makes a new scanner reading from the file
	    //Scanner uses words. First word in file is number of rows, second is number of columns, third
	    //is the long string representing file data
	    int rows = Integer.parseInt(input.next());
	    int columns = Integer.parseInt(input.next());
	    String data = input.next();
	    input.close(); //we are done with file now
	    jPaint jp = new jPaint(rows,columns);
	    jp.loadData(data);
	    return jp;
	}
	catch(Exception e) {
	    System.out.println(e);
	    System.exit(1);
	}
	return null;
    }	

    public void move(char key) {
	if (cursorDown) //replace w/ paint functino later
	    easel[cursorY][cursorX] = color;
	
	if(key == 'w')
	    cursorY -= 1;
	
	if(key == 's') 
	    cursorY += 1;

	if(key == 'a') 
	    cursorX -= 1;
	
	if(key == 'd') 
	    cursorX += 1;

	if(key == 'q') {
	    cursorY -= 1;
	    cursorX -= 1;
	}
	
	if(key == 'e') {
	    cursorY -= 1;
	    cursorX += 1;
	}

	if(key == 'z') {
	    cursorX -= 1;
	    cursorY += 1;
	}
	
	if(key == 'c') {
	    cursorY += 1;
	    cursorX += 1;
	}
	if(cursorX < 0) cursorX = 0;
	if(cursorY < 0) cursorY = 0;
	if(cursorX > easel[0].length - 1) cursorX = easel[0].length - 1;
	if(cursorY > easel.length - 1) cursorY = easel.length - 1;
    }
    
    /******
     *MAIN*
     ******/
    
    public static void main(String[] args){
	jPaint inst = new jPaint(); //empty temp because java gets mad otherwise
	{ //loading phase block
	    int width = 16;
	    int height = 16;
	    boolean loadMode = false;
	    String name = "";
	    while(true) { // sizing loop
		int keyCode = 0;
		try {
		    keyCode = System.in.read(); //get input to start
		} catch(Exception e) {
		    continue; //no input
		}
		char key = Character.toLowerCase((char) keyCode);
		if(!loadMode) { //kinda ugly but works
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
		    else if (key == 'l') { //loading time
			loadMode = true;
		    }
		    else {
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
		else {
		    int i = keyCode;
		    //routine from save mode
		    if(i == 127 || i == 8) { //8 and 127 are backspace and delete sometimes
			if(name.length() == 0) continue;
			name = name.substring(0,name.length()-1);
		    }
		    else if(i == 13) {
			if(name.length() > 0) {
			    inst = load(name);
			    break;
			}
			else continue;
		    }
		    else {
			name += key;
		    }
		    System.out.print(CLEAR + RESET + "\033[0;0H");
		    System.out.print("Enter file name: " + name);
		}
		
		
	    }
	    if(!loadMode) //else its already initialized
		inst = new jPaint(height,width);
	}
	System.out.println(CLEAR);

	while(true) { //MAIN LOOP
	    int keyCode = 0;
	    try {
		keyCode = System.in.read(); //get input to start
	    } catch(Exception e) {
		continue; //no input
	    }
	    if(!inst.input(keyCode)) continue; //input returned false, invalid input 
	    System.out.print(inst);
	    System.out.print(del(1)); //fixes strange issue with moving board
	}
    }
}

