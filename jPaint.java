public class jPaint{
    
    /*****************
     INSTANCE VARIABLES
     *****************/
    
    private String[][] easel;
    //We could switch to String array and not use any other files
    
    private int cursorX, cursorY;

    private boolean cursorDown;

    private String name;
    //Used for name when you save it as a file 


    /**************
     *CONSTRUCTORS*
     **************/
    
    public jPaint(){
	this(16, 16, "test");
    }

    public jPaint(int height, int width,String newName){
	easel =  new String[height][width];
	name = newName;
    }


    /*********
     *METHODS*
     *********/
    
    public void input (int i){
    }
    
    
    /******
     *MAIN*
     ******/
    
    public static void main(String[] args){
    }
}

