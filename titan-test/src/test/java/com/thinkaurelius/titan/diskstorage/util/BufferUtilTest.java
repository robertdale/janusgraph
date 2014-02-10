package com.thinkaurelius.titan.diskstorage.util;

import com.google.common.primitives.Longs;
import com.thinkaurelius.titan.diskstorage.StaticBuffer;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Random;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */

public class BufferUtilTest {

    private static final Random random = new Random();


    @Test
    public void testCompareRandom() {
        int trials = 100000;
        for (int t = 0; t < trials; t++) {
            long val1 = Math.abs(random.nextLong());
            long val2 = Math.abs(random.nextLong());
            StaticBuffer b1 = BufferUtil.getLongBuffer(val1);
            StaticBuffer b2 = BufferUtil.getLongBuffer(val2);

            //Compare
            assertEquals(val1 + " : " + val2, Math.signum(Longs.compare(val1, val2)), Math.signum(b1.compareTo(b2)));
            assertEquals(Math.signum(Longs.compare(val2, val1)), Math.signum(b2.compareTo(b1)));
            assertEquals(0, b1.compareTo(b1));

            ByteBuffer bb1 = of(val1);
            ByteBuffer bb2 = of(val2);
            assertEquals(val1 + " : " + val2, Math.signum(Longs.compare(val1, val2)), Math.signum(ByteBufferUtil.compare(bb1,bb2)));
            assertEquals(Math.signum(Longs.compare(val2, val1)), Math.signum(ByteBufferUtil.compare(bb2, bb1)));
            assertEquals(0, ByteBufferUtil.compare(bb1, bb1));


            //Mixed Equals
            if (0.5<Math.random()) val2=val1;
            ByteBuffer bb = of(val2);
            assertEquals(val1==val2,BufferUtil.equals(b1,bb));
        }
    }

    @Test
    public void testNextBigger() {
        int trials = 100000;
        for (int t = 0; t < trials; t++) {
            long val = random.nextLong()>>>1;
            assert val>=0;
            StaticBuffer b = BufferUtil.getLongBuffer(val);
            assertEquals(val,b.getLong(0));
            StaticBuffer bn = BufferUtil.nextBiggerBuffer(b);
            assertEquals(8,bn.length());
            assertEquals(val+1,bn.getLong(0));
        }

        try {
            StaticBuffer b = BufferUtil.getLongBuffer(-1);
            BufferUtil.nextBiggerBuffer(b);
            fail();
        } catch (IllegalArgumentException e) {}
        StaticBuffer b = BufferUtil.getLongBuffer(-1);
        StaticBuffer bn = BufferUtil.nextBiggerBufferAllowOverflow(b);
        Assert.assertEquals(8,bn.length());
        Assert.assertTrue(BufferUtil.zeroBuffer(8).equals(bn));

    }

    public static ByteBuffer of(long val) {
        ByteBuffer bb = ByteBuffer.allocate(8).putLong(val);
        bb.flip();
        return bb;
    }

}
