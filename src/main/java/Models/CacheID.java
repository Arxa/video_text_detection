package Models;

import cern.colt.list.IntArrayList;

/**
 * Created by arxa on 25/2/2017.
 */

public class CacheID
{
    // TODO USE ENUMS
    public static final int FIRST_FRAME = 1;
    public static final int SECOND_FRAME = 2;
    public static final IntArrayList cacheIds = new IntArrayList(new int[]{FIRST_FRAME,SECOND_FRAME});
}
