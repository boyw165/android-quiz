import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import cb.factory.PicoCacheFactory;
import cb.inteface.IPicoCache;

public class TestPicoCache {
    private byte[] data20kb_1 = new byte[20 << 10];
    private byte[] data20kb_2 = new byte[20 << 10];
    private byte[] data20kb_3 = new byte[20 << 10];

    private byte[] data50kb_1 = new byte[50 << 10];
    private byte[] data50kb_2 = new byte[50 << 10];
    private byte[] data50kb_3 = new byte[50 << 10];

    private byte[] data90kb_1 = new byte[90 << 10];
    private byte[] data90kb_2 = new byte[90 << 10];
    private byte[] data90kb_3 = new byte[90 << 10];

    @Before
    public void Before() {
        Arrays.fill(data20kb_1, (byte) 1);
        Arrays.fill(data20kb_2, (byte) 2);
        Arrays.fill(data20kb_3, (byte) 3);

        Arrays.fill(data50kb_1, (byte) 1);
        Arrays.fill(data50kb_2, (byte) 2);
        Arrays.fill(data50kb_3, (byte) 3);

        Arrays.fill(data90kb_1, (byte) 1);
        Arrays.fill(data90kb_2, (byte) 2);
        Arrays.fill(data90kb_3, (byte) 3);
    }

    @Test
    public void Case01() {
        System.out.printf("Case: MEMORY/FIFO strategy.\n");

        IPicoCache cache1 = PicoCacheFactory.create(PicoCacheFactory.TYPE_MEMORY,
                PicoCacheFactory.STRATEGY_FIFO);

        // Over fill it with many chunks of data.
        cache1.setCacheSize(20);
        cache1.put("data1", data20kb_1);
        cache1.put("data2", data20kb_2);
        cache1.put("data3", data20kb_3);

        Assert.assertTrue(cache1.get("data1") == null);
        Assert.assertTrue(cache1.get("data2") == null);
        Assert.assertTrue(cache1.get("data3") != null);

        // Test whether the clear function works.
        cache1.clear();

        Assert.assertTrue(cache1.get("data1") == null);
        Assert.assertTrue(cache1.get("data2") == null);
        Assert.assertTrue(cache1.get("data3") == null);

        // Over fill it with a data which is larger than the maximum cache size.
        cache1.setCacheSize(80);
        cache1.put("data1", data20kb_1);
        cache1.put("data2", data20kb_2);
        cache1.put("data3", data20kb_3);
        cache1.put("data4", data90kb_1);

        Assert.assertTrue(cache1.get("data1") == null);
        Assert.assertTrue(cache1.get("data2") == null);
        Assert.assertTrue(cache1.get("data3") == null);
        Assert.assertTrue(cache1.get("data4") == null);

        // Changing the caches size on the fly should be taken into account as well.
        cache1.put("data5", data50kb_1);
        cache1.setCacheSize(10);

        Assert.assertTrue(cache1.get("data5") == null);

        // Try to give the size an illegal value.
        try {
            cache1.setCacheSize(-100);
            Assert.assertTrue("Illegal cache size will trigger a runtime exception!", false);
        } catch (Exception e) {
            // DO NOTHING (Pass).
        }

        // Overriding the data with same key should be taken into account as well.
        cache1.clear();
        cache1.setCacheSize(150);
        cache1.put("data1", data50kb_1);
        cache1.put("data2", data50kb_2);
        cache1.put("data1", data50kb_3);

        Assert.assertTrue(Arrays.equals(cache1.get("data1"), data50kb_3));
        Assert.assertTrue(cache1.get("data2") != null);

        ////////////////////////////////////////////////////////////////////////////////////////////
        // LIFO & memory ///////////////////////////////////////////////////////////////////////////

        System.out.printf("Case: MEMORY/LIFO strategy.\n");

        IPicoCache cache2 = PicoCacheFactory.create(PicoCacheFactory.TYPE_MEMORY,
                PicoCacheFactory.STRATEGY_LIFO);

        cache2.setCacheSize(20);
        cache2.put("data1", data20kb_1);
        cache2.put("data2", data20kb_2);
        cache2.put("data3", data20kb_3);

        Assert.assertTrue(cache2.get("data1") != null);
        Assert.assertTrue(cache2.get("data2") == null);
        Assert.assertTrue(cache2.get("data3") == null);

        // Test whether the clear function works.
        cache2.clear();

        Assert.assertTrue(cache2.get("data1") == null);
        Assert.assertTrue(cache2.get("data2") == null);
        Assert.assertTrue(cache2.get("data3") == null);

        // Over fill it with a data which is larger than the maximum cache size.
        cache2.setCacheSize(80);
        cache2.put("data1", data20kb_1);
        cache2.put("data2", data20kb_2);
        cache2.put("data3", data20kb_3);
        cache2.put("data4", data90kb_1);
        cache2.put("data5", data20kb_1);

        Assert.assertTrue(cache2.get("data1") != null);
        Assert.assertTrue(cache2.get("data2") != null);
        Assert.assertTrue(cache2.get("data3") != null);
        Assert.assertTrue(cache2.get("data4") == null);
        Assert.assertTrue(cache2.get("data5") != null);

        // Changing the caches size on the fly should be taken into account as well.
        cache2.setCacheSize(10);

        Assert.assertTrue(cache2.get("data5") == null);

        // Try to give the size an illegal value.
        try {
            cache2.setCacheSize(-100);
            Assert.assertTrue("Illegal cache size will trigger a runtime exception!", false);
        } catch (Exception e) {
            // DO NOTHING (Pass).
        }

        // Overriding the data with same key should be taken into account as well.
        cache2.clear();
        cache2.setCacheSize(150);
        cache2.put("data1", data50kb_1);
        cache2.put("data2", data50kb_2);
        cache2.put("data1", data50kb_3);

        Assert.assertTrue(Arrays.equals(cache2.get("data1"), data50kb_3));
        Assert.assertTrue(cache2.get("data2") != null);
    }

    @Test
    public void Case02() {
        System.out.printf("Case: FILE/FIFO strategy.\n");

        IPicoCache cache1 = PicoCacheFactory.create(PicoCacheFactory.TYPE_FILE,
                PicoCacheFactory.STRATEGY_FIFO);

        // Over fill it with many chunks of data.
        cache1.setCacheSize(20);
        cache1.put("data1", data20kb_1);
        cache1.put("data2", data20kb_2);
        cache1.put("data3", data20kb_3);

        Assert.assertTrue(cache1.get("data1") == null);
        Assert.assertTrue(cache1.get("data2") == null);
        Assert.assertTrue(cache1.get("data3") != null);

        // Test whether the clear function works.
        cache1.clear();

        Assert.assertTrue(cache1.get("data1") == null);
        Assert.assertTrue(cache1.get("data2") == null);
        Assert.assertTrue(cache1.get("data3") == null);

        // Over fill it with a data which is larger than the maximum cache size.
        cache1.setCacheSize(80);
        cache1.put("data1", data20kb_1);
        cache1.put("data2", data20kb_2);
        cache1.put("data3", data20kb_3);
        cache1.put("data4", data90kb_1);

        Assert.assertTrue(cache1.get("data1") == null);
        Assert.assertTrue(cache1.get("data2") == null);
        Assert.assertTrue(cache1.get("data3") == null);
        Assert.assertTrue(cache1.get("data4") == null);

        // Changing the caches size on the fly should be taken into account as well.
        cache1.put("data5", data50kb_1);
        cache1.setCacheSize(10);

        Assert.assertTrue(cache1.get("data5") == null);

        // Try to give the size an illegal value.
        try {
            cache1.setCacheSize(-100);
            Assert.assertTrue("Illegal cache size will trigger a runtime exception!", false);
        } catch (Exception e) {
            // DO NOTHING (Pass).
        }

        // Overriding the data with same key should be taken into account as well.
        cache1.clear();
        // TODO: Check if there're any temporary files in somewhere.
        cache1.setCacheSize(150);
        cache1.put("data1", data50kb_1);
        cache1.put("data2", data50kb_2);
        cache1.put("data1", data50kb_3);

        Assert.assertTrue(Arrays.equals(cache1.get("data1"), data50kb_3));
        Assert.assertTrue(cache1.get("data2") != null);

        ////////////////////////////////////////////////////////////////////////////////////////////
        // LIFO & file /////////////////////////////////////////////////////////////////////////////

        System.out.printf("Case: FILE/LIFO strategy.\n");

        IPicoCache cache2 = PicoCacheFactory.create(PicoCacheFactory.TYPE_FILE,
                PicoCacheFactory.STRATEGY_LIFO);

        cache2.setCacheSize(20);
        cache2.put("data1", data20kb_1);
        cache2.put("data2", data20kb_2);
        cache2.put("data3", data20kb_3);

        Assert.assertTrue(cache2.get("data1") != null);
        Assert.assertTrue(cache2.get("data2") == null);
        Assert.assertTrue(cache2.get("data3") == null);

        // Test whether the clear function works.
        cache2.clear();

        Assert.assertTrue(cache2.get("data1") == null);
        Assert.assertTrue(cache2.get("data2") == null);
        Assert.assertTrue(cache2.get("data3") == null);

        // Over fill it with a data which is larger than the maximum cache size.
        cache2.setCacheSize(80);
        cache2.put("data1", data20kb_1);
        cache2.put("data2", data20kb_2);
        cache2.put("data3", data20kb_3);
        cache2.put("data4", data90kb_1);
        cache2.put("data5", data20kb_1);

        Assert.assertTrue(cache2.get("data1") != null);
        Assert.assertTrue(cache2.get("data2") != null);
        Assert.assertTrue(cache2.get("data3") != null);
        Assert.assertTrue(cache2.get("data4") == null);
        Assert.assertTrue(cache2.get("data5") != null);

        // Changing the caches size on the fly should be taken into account as well.
        cache2.setCacheSize(10);

        Assert.assertTrue(cache2.get("data5") == null);

        // Try to give the size an illegal value.
        try {
            cache2.setCacheSize(-100);
            Assert.assertTrue("Illegal cache size will trigger a runtime exception!", false);
        } catch (Exception e) {
            // DO NOTHING (Pass).
        }

        // Overriding the data with same key should be taken into account as well.
        cache2.clear();
        cache2.setCacheSize(150);
        cache2.put("data1", data50kb_1);
        cache2.put("data2", data50kb_2);
        cache2.put("data1", data50kb_3);

        Assert.assertTrue(Arrays.equals(cache2.get("data1"), data50kb_3));
        Assert.assertTrue(cache2.get("data2") != null);
    }

}
