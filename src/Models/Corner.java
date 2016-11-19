package Models;

/**
 * Created by arxa on 17/11/2016.
 */

public class Corner
{
    private static int i;
    private static int j;
    private static int value;
    private static boolean hasPixelsAround;

    public Corner(){
    }

    public Corner(int i1, int j1, int value1)
    {
        i = i1;
        j = j1;
        value = value1;
        hasPixelsAround = false;
    }

    public void setHasPixelsAround(boolean has)
    {
        hasPixelsAround = has;
    }

    @Override
    public String toString() {
        return "Corner{"+i+","+j+"}"+" : "+value;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    public int getValue() {
        return value;
    }

    public boolean getHasPixelsAround() {
        return hasPixelsAround;
    }
}
