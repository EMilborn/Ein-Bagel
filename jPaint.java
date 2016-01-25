
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

    private int sliderR,sliderG,sliderB,sliderK; //slider values for coloring, grayscale uses K

    //ANSI escape sequences
    public static final String ANSI = "\033[";//ANSI escape code
    
    public static final String CLEAR = "\033c"; //special for reasons 

    public static final String RESET = ANSI+"0m";//resets settings

    public static final String BOLD = ANSI+"1m"; //bold for cursor

    
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
	color = color(colorNumber(1, 1, 1));//default paint color set
	for (int i = 0; i < height; i++) {
	    for(int j = 0; j < width; j++) {
		easel[i][j] = color(231);
	    }
	}//makes easel white
	cursorX = height / 2;
	cursorY = width / 2; //places cursor in center of screen
	cursorDown = false; //cursor up by default to prevent accidental painting 
	name = "";
	shape = 's';
	radius = 0;
	sliderR = 1;
	sliderG = 1;
	sliderB = 1;

    }
    /*********
     *METHODS*
     *********/

    /*******************************************************
     * public static int colorNumber (int r, int g, int b) *
     * returns the number corresponding to color (r,g,b)   *
     *******************************************************/
    
    public static int colorNumber(int r, int g, int b) {
	return r*36 + g*6 + b + 16;
    }

    /***************************************************
     * public String cursorColor ()                    *
     * returns the String to set the cursor text color *
     ***************************************************/

    public String cursorColor(){
	String ret = ANSI + "38;5;";
	if(sliderR+sliderG+sliderB >=8) { //closer to white than black
	    ret += 16;
	}
	else { //closer to black
	    ret += 231;
	}
	return ret + "m";
    }


    /*************************************************************
     * public void colorToRGB(String clr)                        *
     * inverts method colorNumber()                              *
     * by setting rgb sliders to correct values for String color *
     *************************************************************/

    public void colorToRGB(String clr){
	clr = clr.substring(7,10);//range for 3 digit number
	int RGB = Integer.parseInt(clr);
	RGB -= 16;
	sliderR = RGB/36;
	RGB -= sliderR * 36;
	sliderG = RGB/6;
	RGB -= sliderG * 6;
	sliderB = RGB;	
    }
    
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


    /**********************************************
     * public String brushToString()              *
     * returns a Stringifyed version of the brush *
     **********************************************/
    public String brushToString(){
	String ret = RESET;
	for (int y = 0; y < radius * 2 + 1; y++){
	    for (int x = 0; x < radius * 2 + 1; x++){
		int dist = (int)(2 * Math.sqrt((y - radius) * (y - radius) + (x - radius) * (x - radius)));
		if (shape == 's' || dist <= radius * 2){
		    ret += color;
		}
		else 
		    ret += RESET;
		ret += "  ";
	    }
	    ret += "\r\n";
	}
	return ret;
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
	    ret += "Your Drawing:\r\n";
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
			ret += BOLD + color + cursorColor() + (cursorDown ? "><" : "||") + RESET;//cursor center
		    }
		    else {
			ret += printEasel[i][j] + "  ";
		    }
		}
		ret += "\n" + del(easel[0].length*2);	
	    }
	    ret += RESET+"\nH\t-\tHelp menu\r\n";

	}

	else if(mode == "rgb") {
	    ret += "Color Menu:\r\n";
	    ret += "Q-\tRed: " + sliderR + "\t\tE+\r\n";
	    ret += "A-\tGreen: " + sliderG + "\tD+\r\n";
	    ret += "Z-\tBlue: " + sliderB + "\t\tC+\r\n";
	    ret += color(colorNumber(sliderR,sliderG,sliderB)) + "                          ";
	}
	    
	else if(mode == "save" || mode == "exitsave") {
	    ret += "Save Menu:\r\n";
	    ret += RESET + "Enter a name for your file: " + name + "\r\n";
	    ret += "Press Enter when done";
	}
	else if (mode == "brush"){
	    ret += "Brush Menu\r\n";
	    ret += "C\t-\tset shape to circle\r\n";
	    ret += "S\t-\tset shape to square\r\n" + del (35);
	    ret += "digit\t-\tset size to that digit\r\n\n";
	    ret += "Current Brush:\r\n";
	    ret += brushToString();
	}
	else if(mode == "help") {
	    ret +=
		"Help Menu:\r\n" +
		"Key\t-\tWhat it does\r\n" +
		"WASD\t-\tOrthogonal movement\r\n" +
		"QEZC\t-\tDiagonal movement\r\n" +
		"Space\t-\tExit program\r\n" +
		"R\t-\tEnter color mode\r\n" +
		"B\t-\tEnter brush mode\r\n" +
		"V\t-\tEnter save mode\r\n" +
		"F\t-\tOrthogonal fill\r\n" +
		"G\t-\tOrthogonal + diagonal fill\r\n" +
		"L\t-\tReplace\r\n" +
		"X\t-\tToggle brush up/down\r\n" +
		"T\t-\tDropper (Set current color to color under cursor)\r\n" +
		"M\t-\tHorizontal flip\r\n" +
		"N\t-\tVertical flip\r\n" +
		"H\t-\tThis dialog\r\n" +
		"Enter\t-\tExit this dialog\r\n";
	}
	else if (mode == "exit"){
	    ret +=
		"Do you want save before exiting?\r\n" +
		"Q - YES\t\tE - NO";
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
	        mode = "exit";
	    }

	    //MODE SWITCHES
	    else if(key == 'r') {// enter color mode
		mode = "rgb";
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
	    
	    else if(key == 'l'){
		replace(easel[cursorY][cursorX], color); // like fill but replaces all of that color
	    }

	    //OTHER TOOLS
	    else if(key == 'x') { //lift/place brush
		cursorDown = !cursorDown;
		if(cursorDown) paint();
	    }

	    else if(key == 't'){ //dropper tool
		color = easel[cursorY][cursorX];
		colorToRGB(color);
	    }

	    else if(key == 'm'){ //flip horizontal
		flipH();
	    }
	    
	    else if(key == 'n'){ //flip vertical
		flipV();
	    }
	    else if(key == 'h'){ //i need somebody
		mode = "help";
	    }
	}

	else if(mode == "rgb") {
	    if(key == 'q') sliderR -= 1;
	    if(key == 'e') sliderR += 1;
	    if(sliderR < 0) sliderR = 0;
	    if(sliderR > 5) sliderR = 5;
	    
	    if(key == 'a') sliderG -= 1;
	    if(key == 'd') sliderG += 1;
	    if(sliderG < 0) sliderG = 0;
	    if(sliderG > 5) sliderG = 5;
	    
	    if(key == 'z') sliderB -= 1;
	    if(key == 'c') sliderB += 1;
	    if(sliderB < 0) sliderB = 0;
	    if(sliderB > 5) sliderB = 5;

	    if(i == 13) {
		color = color(colorNumber(sliderR,sliderG,sliderB));
		mode = "main";
	    }
	}   
	
	else if(mode == "save" || mode == "exitsave") {
	    if(i == 127 || i == 8) { //8 and 127 are backspace and delete sometimes
		if(name.length() == 0) 
		    return;//cant delete if its already blank
		name = name.substring(0,name.length()-1);//shorten String name
	    }
	    
	    else if(i == 13) {//Enter/return
		if(name.length() > 0) save();//wont save if string is blank
		if(mode == "exitsave"){
		    System.out.print(CLEAR + RESET);
		    System.exit(0);
		}
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
	else if(mode == "help"){
	    if(i == 13) mode = "main"; //Enter
	}
	else if (mode == "exit"){
	    if(key == 'e'){
		System.out.print(CLEAR + RESET);
		System.exit(0);
	    }
	    if(key == 'q'){
		mode = "exitsave";
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
		//turns out \033 is stored as a single character, so each c is X[48;5;YYYm, y's start at ind 7 and end at 9
		ret += c.substring(7,10);		
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
		int pos = 3*(i*easel[0].length + j);
	        String newcolor = data.substring(pos,pos+3); //add row length to get down rows, then add which column to get to correct column, mult by 3 to scale
		String old = easel[i][j];
		easel[i][j] = old.substring(0,7) + newcolor + old.substring(10); //inserts the new color
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
	Scanner input;
	try {
	    input = new Scanner(new File("saves/" + fileName)); //makes a new scanner reading from the file
	}
	catch(Exception e) {
	    return null;
	}
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
	    System.out.print(color(196));
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
		    if(height > 32) height = 32;
		    if(width > 32) width = 32;
		    if(height < 1) height = 1;
		    if(width < 1) width = 1;
		    //System.out.println it
		    System.out.print(CLEAR);
		    System.out.print(color(196));
		    System.out.print(del(1)); //delete red? idk but you need it
		    for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
			    System.out.print("  "); //2 chars for correctness
			}
			System.out.println();
			System.out.print(del(width*2)); //deletes all that space
		    }
		    if (key == 'l') { //loading time, below printing so it can print different stuff instead
			loadMode = true;
			System.out.print(CLEAR + RESET + "\033[0;0H");
			System.out.print("Enter file name: " + name);
			
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
			    if(inst == null) {
				name = "";
				System.out.println("\r\nFile does not exist.\r");
				continue;
			    }
			    break;
			}
			else {
			    loadMode = false;
			    continue;
			}
		    }
		    else {
			name += key;
		    }
		    System.out.print(CLEAR + RESET + "\033[0;0H");
		    System.out.print("Enter file name: " + name + "\n");
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

