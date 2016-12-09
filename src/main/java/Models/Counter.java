package Models;

import java.util.List;

/**
 * Created by arxa on 29/11/2016.
 */
public class Counter
{
    private int value;
    private int foundCounter;

    public Counter(){
    }

    public Counter(int value1,int foundCounter1){
        value = value1;
        foundCounter = foundCounter1;
    }

    public int getValue() {
        return value;
    }

    public int getFoundCounter() {
        return foundCounter;
    }

    public void incrementCounter() {
        foundCounter++;
    }

    public static boolean findValueAndAddToCounter(List<Counter> list, int value) {
        for (Counter object : list) {
            if (object.getValue() == value) {
                object.incrementCounter();
                return true;
            }
        }
        return false;
    }


    public static boolean cornerQualifies(List<Counter> list, Corner c)
    {
        int threshold = 8;
        for (int i=0; i < c.getHorDiffList().size(); i++)
        {
            for (Counter t : list)
            {
                if (t.getValue() == c.getHorDiffList().get(i))
                {
                    return t.getFoundCounter() > threshold;
                }
            }
        }
        return false;
    }
}
