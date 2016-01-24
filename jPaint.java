
import java.io.PrintWriter;
import java.util.Scanner;
import java.io.File;

public class jPaint{
    
    /*****************
     INSTANCE VARIABLES
     *****************/
    
    private String[][] easel; //paint easel to be printed
    
    private int cursorX, cursorY; //location of cursor

    private boolean cursorDown; //user can lift or place brush

    private String mode; //modes: save, main, color, brush

    private String color; //current color of brush

    private String name; //filename to save with

    private char shape; //shape of Brush, 'c' fir circle, 's' for square

    private int radius; //radius of Brush

    //ANSI escape sequences
    public static final String ANSI = "\033[";//ANSI escape code
    
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

    //TEXT COLORS AND SETTINGS
    public static final String CBLACK = ANSI+"30m";
    public static final String CWHITE = ANSI+"37m";
    
    public static final String BOLD = ANSI+"1m";

    public static final String RESET = ANSI+"0m";//resets settings

    private String[][] colors = new String[16][18];
    
    /**************
     *CONSTRUCTORS*
     **************/
    
    // default constructor
    public jPaint(){
	this(16, 16);
    }

    // creates new jPaint with height and width
    public jPaint(int height, int width){
	easel =  new String[height][width];//easel created
	mode = "main";//mode set
	color = BLACK;//default paint color set
	for (int i = 0; i < height; i++) {
	    for(int j = 0; j < width; j++) {
		easel[i][j] = WHITE;
	    }
	}//makes easel white
	cursorX = height / 2;
	cursorY = width / 2; //places cursor in center of screen
	cursorDown = false; //cursor up by default to prevent accidental painting 
	name = "";
	shape = 's';
	radius = 0;

	//COLOR FILLING
	//first fill all with black
	for (int y = 0; y < 16; y++){
	    for (int x = 0; x < 18; x++){
		colors[y][x] = color(0);
	    }
	}

	for (int y = 0; y < 2; y++){//adds bright colors
	    for (int x = 0; x < 8; x++){
		colors[y][x] = color(y * 8 + x);
	    }
	} 

	//RGB colors
	for (int red = 0; red < 6; red++){
	    for(int blue = 0; blue < 6; blue++){
		for (int green = 0; green < 3; green++){
		    colors[red + 2][green * 6 + blue] = color(red*36 + blue*6 + green + 16);
		}
	    }
	}
	
	for (int red = 0; red < 6; red++){
	    for(int blue = 0; blue < 6; blue++){
		for (int green = 3; green < 6; green++){
		    colors[red + 8][green * 6 + blue - 18] = color(red*36 + blue*6 + green + 16);
		}
	    }
	}
	
	//Greyscale
	for (int x = 0; x < 12; x++){
	    colors[14][x] = color(x + 232);
	    colors[15][x] = color(x + 244);
	}
		
	    
    }


    

    /*********
     *METHODS*
     *********/

    /**************************************
     * public static String color(int n)  *
     * returns the string form of color n *
     **************************************/

    public static String color(int n){
        String ret = ANSI + "48;5;" + String.format("%03d", n) + "m";
	return ret;
    }

    /***********************************
     * public static String del(int n) *
     * deletes n amount of characters  *
     ***********************************/

    public static String del(int n) {
	return ANSI + n + "D";
    }
    

    /********************************
     * public static toString()     *
     * over-written toString method *
     ********************************/
    public String toString() {
	String ret = "";
	ret = (CLEAR); //clear first
	ret += "\033[0;0H"; //set terminal cursor (not jPaint cursor) pos to 0,0 just in case something is weird
	if(mode == "main") {//different modes need different implementations of toString
	    String[][] printEasel = new String[easel.length][easel[0].length];
	    for(int i = 0; i < easel.length; i++) {//fills print easel
		for(int j = 0; j < easel[0].length; j++) {
		    printEasel[i][j] = easel[i][j];
		}
	    }

	    //copied from paint()
	    int dist = 0;//twice the distance(used for more precise int)
	    for (int x = cursorX - radius; x <= cursorX + radius; x++){//Displays brush
		for (int y = cursorY - radius; y <= cursorY + radius; y++){
		    dist = (int)(2 * Math.sqrt(((cursorX - x)*(cursorX - x)) + ((cursorY - y)*(cursorY - y))));
		    if (shape == 's' || dist <= 2 * radius){
			if (x >= 0 && y >=0 && x < printEasel[0].length && y < printEasel.length){
			    printEasel[y][x] = color;
			}
		    }
		}
	    }
	   
	    for(int i = 0; i < printEasel.length; i++) {//adds printEasel to ret
		for(int j = 0; j < printEasel[0].length; j++) {
		    if(j == cursorX && i == cursorY) {
			ret += BLACK + CWHITE + BOLD + (cursorDown ? "><" : "||");//cursor center
		    }
		    else {
			ret += printEasel[i][j] + "  ";
		    }
		}
		ret += "\n" + del(easel[0].length*2);	
	    }
	}
	    
	else if(mode == "color") {
	    for (int y = 0; y < 16; y++){
		for(int x = 0; x < 18; x++){
		    ret += colors[y][x] +"  ";
		}
		ret+="\n" + del(36);
	    }
	}
	else if(mode == "save") {
	    ret += (RESET + "Enter a name for your file: " + name);
	}
	else if (mode == "brush"){
	    ret += "c\t-\tset shape to circle\n" + del(35);
	    ret += "s\t-\tset shape to square\n" + del (35);
	    ret += "digit\t-\tset size to that digit\n\n" + del(38);
	    ret += "current radius: " + radius + "\n" + del (27);
	    ret += "current brush shape: ";
	    if (shape == 's')
		ret += "square";
	    else 
		ret+= "circle";
	    ret += "\n";
	}
	return ret;
    }
    //Handles input to do certain things in certain modes, returns if input is valid

    /*******************************************
     * public void input(int i)                *
     * does action associated with pressed key *
     * easily modifyable                       *
     *******************************************/
    public void input (int i){
	char key = Character.toLowerCase((char) i);
	if(mode.equals("main")) {
	    if("wasdqezc".indexOf(key) != -1) {//brush-movement keys
		move(key);
	    }
	    else if(key == ' ') {// button to exit program ( ctrl-c wouldnt reset colors)
		System.out.println(RESET);
		System.exit(0); //stops program
	    }

	    //MODE SWITCHES
	    else if(key == 'r') {// enter color mode
		mode = "color";
	    }
	    
	    else if(key == 'b'){// enter brush mode
		mode = "brush";
	    }

	    else if(key == 'v') {// enter save mode
		mode = "save";
	    }
	  	    
	    //FILL AND REPLACES
	    else if(key == 'f'){
		fill1(cursorX, cursorY, color); // fill1 = orthagonal only
	    }
	    
	    else if(key == 'g'){
		fill2(cursorX, cursorY, color); // Fill2 = orthagonal + diagonal
	    }
	    
	    else if(key == 'h'){
		replace(easel[cursorY][cursorX], color); // like fill but replaces all of that color
	    }

	    //OTHER TOOLS
	    else if(key == 'x') { //lift/place brush
		cursorDown = !cursorDown;
		if(cursorDown) paint();
	    }

	    else if(key == 't'){ //dropper tool
		color = easel[cursorY][cursorX];
	    }

	    else if(key == 'm'){ //flip horizontal
		flipH();
	    }
	    
	    else if(key == 'n'){ //flip vertical
		flipV();
	    }
	}
	else if(mode.equals("color")) {
	    int num =  Character.getNumericValue(key); //if key is not a number, gives -1
	    if(num == -1 || num > 7) { //0-7 selection only
		return;
	    }
	    //else:
	    color = ANSI + "4" + num + "m"; //sets the color to correct ANSI sequence
	    mode = "main";
	}
	
	else if(mode == "save") {
	    if(i == 127 || i == 8) { //8 and 127 are backspace and delete sometimes
		if(name.length() == 0) 
		    return;//cant delete if its already blank
		name = name.substring(0,name.length()-1);//shorten String name
	    }
	    
	    else if(i == 13) {//Enter/return
		if(name.length() > 0) save();//wont save if string is blank
		mode = "main";
	    }
	    else {//any other character
		name += key;
	    }
	}
	
	else if(mode == "brush"){
	    if ("cs".indexOf(key) != -1){//if key is c or s
		shape = key;
	    }
	    
	    else if ("0123456789".indexOf(key) != -1){//numbers change radius
		radius = Character.getNumericValue(key);
	    }
	    
	    else if (i == 13){//Enter
		mode = "main";
	    }
	}
    }
    
    /**********************************************
     * public String toFile()                     *
     * returns simplified string version of easel *
     * used for saving                            *
     **********************************************/
    public String toFile() {
	String ret = "";
	ret += easel.length;//Dimensions of the easel are the first part of the text file
	ret += " ";
	ret += easel[0].length + " ";
	for(String[] row : easel) {
	    for(String c : row) {
		//turns out \033 is stored as a single character, so each c is X[4Ym, Y is ind 3
		ret += c.charAt(3);//adds just the number which distinguishes this color from the rest
	    }
	}
	return ret;
    }
							
    /**************************************
     * public void save()                 *
     * saves as text file in saves folder *
     **************************************/
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

    /***********************************************
     * public void loadData(String data)           *
     * takes version of easel that is in text file *
     * and fills jPaint with proper version        *
     ***********************************************/
    public void loadData(String data) { 
	for(int i = 0; i < easel.length; i++) {
	    for(int j = 0; j < easel[0].length; j++) {
		char newcolor = data.charAt(i * easel[0].length + j); //add row length to get down rows, then add which column to get to correct column
		String old = easel[i][j];
		easel[i][j] = old.substring(0,3) + newcolor + old.substring(4); //inserts the new color
	    }
	}
    }

    /****************************************************************
     * public static jPaint load(String fileName)                   *
     * takes fileName and creates a string from that file's content *
     * then sends that data to loadData to fill the jPaint          *
     * returns the filled jPaint                                    *
     ****************************************************************/

    public static jPaint load(String fileName) { //takes info from file "saves/file" and loads it into a new jPaint
	try {
	    Scanner input = new Scanner(new File("saves/"+fileName)); //makes a new scanner reading from the file
	    //Scanner uses words. First word in file is number of rows, second is number of columns, third
	    //is the long string representing file data
	    int height = Integer.parseInt(input.next());
	    int width = Integer.parseInt(input.next());
	    String data = input.next();
	    input.close(); //we are done with file now
	    jPaint jp = new jPaint(height, width);
	    jp.loadData(data);
	    return jp;
	}
	catch(Exception e) {
	    System.out.println(e);
	    System.exit(1);
	}
	return null;
    }	

    /******************************************
     * public void move(char key)             *
     * moves cursor, direction depends on key *
     * sort of an extension of input          *
     ******************************************/

    public void move(char key) {
	
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
	if (cursorDown)
	    paint();
    }
    
    /****************************************
     * public void paint()                  *
     * paints at location of brush          *
     * called by move only if brush is down *
     ****************************************/
    public void paint(){
	int dist = 0;//twice the distance(used for more precise int)
	for (int x = cursorX - radius; x <= cursorX + radius; x++){
	    for (int y = cursorY - radius; y <= cursorY + radius; y++){
		dist = (int)(2 * Math.sqrt(((cursorX - x)*(cursorX - x)) + ((cursorY - y)*(cursorY - y))));
		if (shape == 's' || dist <= 2 * radius){//paints if its a square or if its within proper distance
		    if (x >= 0 && y >=0 && x < easel[0].length && y < easel.length){
		    easel[y][x] = color ;
		    }
		}
	    }
	}
    }

    /****************************************************
     * public void fill1(int x, int y, String newColor) *
     * fills orthagonally                               *
     ****************************************************/
    public void fill1(int x, int y, String newColor){//fill starting at this location and using newColor
	String oldColor = easel[y][x];//used later to check if neighbors are the old color
	easel[y][x] = newColor;
	if (oldColor.equals(newColor))
	    return;
	for(int i = y - 1; i <= y + 1; i++){
	    for (int j = x + Math.abs(i-y) - 1; j <= x - Math.abs(i-y) + 1; j++){ //abs(i-y) thing makes sure that it only goes to orthogonal neighbors
		if (j >= 0 && i >= 0 && j < easel[0].length && i < easel.length && easel[i][j].equals(oldColor)){ 
		    fill1 (j , i, newColor);
		}
	    }
	}
    }
    
    /****************************************************
     * public void fill2(int x, int y, String newColor) *
     * fills orthagonally and diagonally                *
     ****************************************************/
    public void fill2(int x, int y, String newColor){//fill starting at this location and using newColor
	String oldColor = easel[y][x];//used later to check if neighbors are the old color
	easel[y][x] = newColor;
	if (oldColor.equals(newColor))
	    return;
	for(int i = y - 1; i <= y + 1; i++){
	    for (int j = x - 1; j <= x + 1; j++){
		if (j >= 0 && i >= 0 && j < easel[0].length && i < easel.length && easel[i][j].equals(oldColor)){ 
		    fill2 (j , i, newColor);
		}
	    }
	}
    }
    
    /*************************************************
     * public void replace(String oldC, String newC) *
     * replaces all of oldC with newC                *
     *************************************************/
    public void replace(String oldC, String newC){

	if (oldC.equals(newC))
	    return;
	
	for (int row = 0; row < easel.length; row++){
	    for (int elmt = 0; elmt < easel[row].length; elmt++){
		if (easel[row][elmt].equals(oldC))
		    easel[row][elmt] = newC;
	    }
	}
    }

    /********************************
     * public void flipH()          *
     * flips the easel horizontally *
     ********************************/

    public void flipH(){
	String[][] flipped = new String[easel.length][easel[0].length];
	for (int y = 0; y < easel.length; y++){
	    for (int x = 0; x < easel[y].length; x++){
		flipped[y][x] = easel[y][easel[y].length - x - 1]; 
	    }
	}
	easel = flipped;	
    }

    /******************************
     * public void flipV()        *
     * flips the easel vertically *
     ******************************/

    public void flipV(){
	String[][] flipped = new String[easel.length][easel[0].length];
	for (int y = 0; y < easel.length; y++){
	    for (int x = 0; x < easel[y].length; x++){
		flipped[y][x] = easel[easel.length - y - 1][x]; 
	    }
	}
	easel = flipped;	
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

	    //System.out.println once before while loop
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
			System.out.print(CLEAR + RESET + "\033[0;0H");
			System.out.print("Enter file name: " + name);
			key = (char)0;//clears the 'l' so the starting filename isnt blank
		    }
		    else {
			continue;
		    }
		    if(height > 32) height = 32;
		    if(width > 32) width = 32;
		    if(height < 1) height = 1;
		    if(width < 1) width = 1;
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
		if(loadMode) { //instead of else, another if so that pressing L will update screen
		    
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

	//System.out.println once before loop
	System.out.print(inst);
	System.out.print(del(1)); //fixes strange issue with moving board

	while(true) { //MAIN LOOP
	    int keyCode = 0;
	    try {
		keyCode = System.in.read(); //get input to start
	    } catch(Exception e) {
		continue; //no input
	    }

	    inst.input(keyCode);
	    System.out.print(inst);
	    System.out.print(del(1)); //fixes strange issue with moving board
	}
    }
}

