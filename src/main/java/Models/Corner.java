package Models;

import cern.colt.list.IntArrayList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arxa on 17/11/2016.
 */

public class Corner
{
    private int i;
    private int j;
    private int value;
    private boolean hasPixelsAround;
    private IntArrayList horDiffList; // Horizontal Difference List
    private IntArrayList verDiffList; // Vertical Difference List
    private double cornerDensityProbability;

    public Corner(){
    }

    public Corner(int i1, int j1, int value1)
    {
        i = i1;
        j = j1;
        value = value1;
        //hasPixelsAround = false;
        horDiffList = new IntArrayList();
        verDiffList = new IntArrayList();
        //cornerDensityProbability = 0.0;
    }

    public Corner(int i1, int j1)
    {
        i = i1;
        j = j1;
    }


    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Corner)) {
            return false;
        }
        Corner that = (Corner) other;
        // Custom equality check here.
        return this.i == that.getI()
                && this.j == that.getJ() && this.value == that.getValue();
    }

    /*@Override
    public int hashCode() {
        int hashCode = 1;

        hashCode = hashCode * 37 + this.i;
        hashCode = hashCode * 37 + this.j.hashCode();
        hashCode = hashCode * 37 + this.value.hashCode();

        return hashCode;
    }*/


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

    public IntArrayList getHorDiffList() {
        return horDiffList;
    }

    public IntArrayList getVerDiffList() {
        return verDiffList;
    }

    public double getCornerDensityProbability() {
        return cornerDensityProbability;
    }

    public void setCornerDensityProbability(double cornerDensityProbability) {
        this.cornerDensityProbability = cornerDensityProbability;
    }
}


