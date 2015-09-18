import junit.framework.Assert;

import org.junit.Test;

import cb.factory.PicoCacheFactory;
import cb.inteface.PicoCache;

public class TestPicoCache {
    private byte[] bmp1 = new byte[20 << 10];
    private byte[] bmp2 = new byte[20 << 10];
    private byte[] bmp3 = new byte[20 << 10];

    @Test
    public void Case01() {
        PicoCache cache = PicoCacheFactory.create(PicoCacheFactory.TYPE_MEMORY,
                PicoCacheFactory.STRATEGY_LIFO);

        cache.setCacheSize(20);
        cache.put("bmp1", bmp1);
        cache.put("bmp2", bmp2);
        cache.put("bmp3", bmp3);

        System.out.printf("Case: Trying to over fill the cache of type of LIFO/memory strategy.\n");

        Assert.assertTrue(cache.get("bmp1") != null);
        Assert.assertTrue(cache.get("bmp2") == null);
        Assert.assertTrue(cache.get("bmp3") == null);
    }

    @Test
    public void Case02() {
        PicoCache cache1 = PicoCacheFactory.create(PicoCacheFactory.TYPE_MEMORY,
                PicoCacheFactory.STRATEGY_FIFO);

        cache1.setCacheSize(20);
        cache1.put("data1", bmp1);
        cache1.put("data2", bmp2);
        cache1.put("data3", bmp3);

        System.out.printf("Case: Trying to over fill the cache of type of FIFO/memory strategy.\n");

        Assert.assertTrue(cache1.get("data1") == null);
        Assert.assertTrue(cache1.get("data2") == null);
        Assert.assertTrue(cache1.get("data3") != null);

        PicoCache cache2 = PicoCacheFactory.create(PicoCacheFactory.TYPE_FILE,
                PicoCacheFactory.STRATEGY_LIFO);

        cache2.setCacheSize(20);
        cache1.put("data1", bmp1);
        cache1.put("data2", bmp2);
        cache1.put("data3", bmp3);

        System.out.printf("Case: Trying to over fill the cache of type of FIFO/disk strategy.\n");

        Assert.assertTrue(cache1.get("data1") == null);
        Assert.assertTrue(cache1.get("data2") == null);
        Assert.assertTrue(cache1.get("data3") != null);
    }

    @Test
    public void Case03() {
        PicoCache cache = PicoCacheFactory.create(PicoCacheFactory.TYPE_FILE,
                PicoCacheFactory.STRATEGY_FIFO);

        cache.setCacheSize(20);
        cache.put("bmp1", bmp1);
        cache.put("bmp2", bmp2);
        cache.put("bmp3", bmp3);

        System.out.printf("Case: Trying to over fill the cache of type of FIFO/memory strategy.\n");

        Assert.assertTrue(cache.get("bmp1") == null);
        Assert.assertTrue(cache.get("bmp2") == null);
        Assert.assertTrue(cache.get("bmp3") != null);
    }

}