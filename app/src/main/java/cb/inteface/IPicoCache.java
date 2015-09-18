package cb.inteface;

public interface IPicoCache {
    /**
     * Insert a chunk of data paired with given key.
     * @param key The key could be a URL, file path, etc...
     * @param data The binary data.
     */
    void put(String key, byte[] data) throws IllegalArgumentException;

    /**
     * Get the data refer to given key.
     * @param key The key paired with the data.
     * @return The data.
     */
    byte[] get(String key);

    /**
     * The total size of current data in Bytes.
     * @return The total size of current data in Bytes.
     */
    int size();

    /**
     * Clean the cache.
     */
    void clear();

    /**
     * Set the maximum cache size.
     * @param kb Numbers in KB.
     */
    void setCacheSize(int kb) throws IllegalArgumentException;
}
