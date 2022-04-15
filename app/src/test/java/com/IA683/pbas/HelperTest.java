package com.IA683.pbas;

import org.junit.Test;

import static org.junit.Assert.*;

import com.IA683.pbas.Helpers.Helper;

import java.util.ArrayList;
import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class HelperTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

/*    @Test
    public void pointPairListToString_isCorrect() {
        List<PointPair> pointPairList = new ArrayList<>();

        pointPairList.add(new PointPair(10, 10));
        pointPairList.add(new PointPair(20, 9));
        pointPairList.add(new PointPair(890, 70));
        pointPairList.add(new PointPair(2000, 30));
        pointPairList.add(new PointPair(100, 0));

        String s = Helper.convertPointPairListToString(pointPairList);

        assertEquals(Helper.convertStringToPointPairList(s).toString(), pointPairList.toString());

        assertEquals(s, "10,10.20,9.890,70.2000,30.100,0.");
    }*/
}