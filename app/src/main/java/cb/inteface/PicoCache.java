package cb.inteface;

import android.graphics.Bitmap;

/**
 * Created by boyw165 on 9/14/15.
 */
public interface PicoCache {
    void put(String key, byte[] data);
    byte[] get(String key);
    int size();
    void clear();
    void setCacheSize(int kb);
}
