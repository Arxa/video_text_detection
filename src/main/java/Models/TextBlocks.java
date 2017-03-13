package Models;

/**
 * Created by arxa on 12/3/2017.
 */

public class TextBlocks
{
    private double left;
    private double top;
    private double width;
    private double height;
    private double area;

    public TextBlocks(){
    }

    public TextBlocks(double left, double top, double width, double height, double area) {
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
        this.area = area;
    }
}
