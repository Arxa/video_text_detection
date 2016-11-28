package Models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arxa on 17/11/2016.
 */

public class Corner
{
    private Integer i;
    private Integer j;
    private Integer value;
    private boolean hasPixelsAround;
    private List<Integer> horDiffList; // Horizontal Difference List
    private List<Integer> verDiffList; // Vertical Difference List

    public Corner(){
    }

    public Corner(int i1, int j1, int value1)
    {
        i = i1;
        j = j1;
        value = value1;
        hasPixelsAround = false;
        horDiffList = new ArrayList<>();
        verDiffList = new ArrayList<>();
    }

    public Corner(int i1, int j1)
    {
        i = i1;
        j = j1;
    }

    public void setHasPixelsAround(boolean has)
    {
        hasPixelsAround = has;
    }

    @Override
    public String toString() {
        return "Corner{"+i+","+j+"}"+" : "+value;
    }

    public Integer getI() {
        return i;
    }

    public Integer getJ() {
        return j;
    }

    public Integer getValue() {
        return value;
    }

    public boolean getHasPixelsAround() {
        return hasPixelsAround;
    }

    public List<Integer> getHorDiffList() {
        return horDiffList;
    }

    public List<Integer> getVerDiffList() {
        return verDiffList;
    }
}
