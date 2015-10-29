package cb.quiz.picocache.factory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.UUID;

import cb.quiz.picocache.iface.IPicoCache;

/**
 * Author: boyw165
 * Email: boy@cardinalblue.com
 *        boyw165@gmail.com
 * Date: 9.18.2015
 *
 * The architecture is:
 *                            PicoCacheFactory
 * ----------------------------------------------------------------------------
 *           create() => IPicoCache (implemented by CacheStore)
 *
 * CacheStore
 * - Use ICacheNodeFactory to create node in linked-list according to node type.
 * - Use ICacheStoreTrimmer to trim linked-list according to strategy.
 *
 *            ICacheStoreTrimmer             ICacheNodeFactory
 *                   |                              |
 *           .-------+-------.              .-------+--------.
 *           |               |              |                |
 *     FifoTrimmer      LifoTrimmer     MemoNode          FileNode
 *
 */
public class PicoCacheFactory {

    public static final int TYPE_MEMORY     = 0x00000001;

    public static final int STRATEGY_FIFO   = 0x10000001;
    public static final int STRATEGY_LIFO   = 0x10000002;

    private static final int DEFAULT_CACHE_SIZE = 20;

    // Trimmer worker in respect of STRATEGY_FIFO/LIFO.
    private static final ICacheStoreTrimmer mFifoTrimmer = new FifoTrimmer();
    private static final ICacheStoreTrimmer mLifoTrimmer = new LifoTrimmer();

    // Node factory.
    private static final ICacheNodeFactory mMemoNodeFactory = new CacheNodeFactory(TYPE_MEMORY);
//    private static final ICacheNodeFactory mFileNodeFactory = new CacheNodeFactory(TYPE_FILE);

    public PicoCacheFactory() {
        throw new RuntimeException("Cannot be constructed!");
    }

    public static IPicoCache create(int cacheType, int cacheStrategy) {
        ICacheStoreTrimmer trimmer = null;
        ICacheNodeFactory factory = null;

        // Cache type.
        switch (cacheType) {
            case TYPE_MEMORY:
                factory = mMemoNodeFactory;
                break;
//            case TYPE_FILE:
//                factory = mFileNodeFactory;
//                break;
        }

        // Cache strategy.
        switch (cacheStrategy) {
            case STRATEGY_FIFO:
                trimmer = mFifoTrimmer;
                break;
            case STRATEGY_LIFO:
                trimmer = mLifoTrimmer;
                break;
        }

        if (factory == null) {
            throw new RuntimeException("Wrong cache type.");
        }

        return new CacheStore(factory, trimmer);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Store ///////////////////////////////////////////////////////////////////////////////////////

    private static class CacheStore implements IPicoCache {

        // Current total size in bytes.
        protected int mSize;

        // Maximum total size in bytes.
        protected int mSizeMax;

        // The linked-list store.
        protected LinkedList<ICacheNode> mList;

        // The trimmer.
        protected ICacheStoreTrimmer mTrimmer = null;

        // The node factory.
        protected ICacheNodeFactory mNodeFactory = null;

        public CacheStore(ICacheNodeFactory factory, ICacheStoreTrimmer trimmer) {
            mSize = 0;
            mSizeMax = DEFAULT_CACHE_SIZE;
            mList = new LinkedList<>();
            mTrimmer = trimmer;
            mNodeFactory = factory;
        }

        @Override
        public synchronized void setCacheSize(int kb) throws IllegalArgumentException {
            if (kb <= 0) {
                throw new IllegalArgumentException("The cache size is either less/equal than 0" +
                        " or greater than Integer.MAX_VALUE");
            }

            mSizeMax = kb << 10;

            if (mTrimmer != null) {
                mSize = mTrimmer.trim(mList, mSize, mSizeMax);
            }
        }

        @Override
        public synchronized void put(String key, byte[] data) throws IllegalArgumentException {
            try {
                boolean isFound = false;
                ListIterator<ICacheNode> it = mList.listIterator();

                // Check whether the given key is already in the store.
                while (it.hasNext()) {
                    ICacheNode current = it.next();
                    if (current.isKey(key)) {

                        mSize -= current.size();
                        current.setData(data);
                        mSize += current.size();

                        isFound = true;
                        break;
                    }
                }

                // Create new node if it is new.
                if (!isFound) {
                    // Use node factory to create responsive cache node.
                    ICacheNode node = mNodeFactory.create(key, data);

                    mList.add(node);
                    mSize += node.size();
                }

                // Trim the cache if it is overflowed.
                if (mTrimmer != null) {
                    mSize = mTrimmer.trim(mList, mSize, mSizeMax);
                }

            } catch (Exception e) {
                // DO NOTHING.
            }
        }

        @Override
        public synchronized byte[] get(String key) {
            try {
                ListIterator<ICacheNode> it = mList.listIterator();

                while (it.hasNext()) {
                    ICacheNode current = it.next();
                    if (current.isKey(key)) {
                        return current.getData();
                    }
                }
            } catch (Exception e) {
                // DO NOTHING.
            }

            return null;
        }

        @Override
        public synchronized int size() {
            return mSize;
        }

        @Override
        public synchronized void clear() {
            if (!mList.isEmpty()) {
                mList.remove();
                mSize = 0;
            }
        }

        @Override
        public synchronized String toString() {
            String ret = super.toString().concat("\n");
            ListIterator<ICacheNode> it = mList.listIterator();

            while (it.hasNext()) {
                ICacheNode current = it.next();
                ret = ret.concat("key=" + current.getKey()
                        + "; data size=" + current.size() + "\n");
            }

            return ret;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Store Trimmer ///////////////////////////////////////////////////////////////////////////////

    private interface ICacheStoreTrimmer {
        /**
         * Trim the list so that its size is less or equal than the maximum size.
         * @param list The given list.
         * @param size The current size of the list in bytes.
         * @param max The maximum size of the list in bytes.
         * @return The size after trimming.
         */
        int trim(final LinkedList<ICacheNode> list, int size, final int max);
    }

    private static class FifoTrimmer implements ICacheStoreTrimmer {

        @Override
        public int trim(final LinkedList<ICacheNode> list, int size, final int max) {
            ListIterator<ICacheNode> it = list.listIterator();

            try {
                while (size > max && it.hasNext()) {
                    ICacheNode node = it.next();
                    // Remove the node.
                    it.remove();
                    // Subtract the node's size.
                    size -= node.size();
                    // Dispose the node.
                    node.dispose();
                }
            } catch (Exception e) {
                // DO NOTHING.
            }

            return size;
        }

    }

    private static class LifoTrimmer implements ICacheStoreTrimmer {

        @Override
        public int trim(final LinkedList<ICacheNode> list, int size, final int max) {
            ListIterator<ICacheNode> it = list.listIterator(list.size());

            try {
                while (size > max && it.hasPrevious()) {
                    ICacheNode node = it.previous();
                    // Remove the node.
                    it.remove();
                    // Subtract the node's size.
                    size -= node.size();
                    // Dispose the node.
                    node.dispose();
                }
            } catch (Exception e) {
                // DO NOTHING.
            }

            return size;
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Store Node //////////////////////////////////////////////////////////////////////////////////

    // Node factory interface.
    private interface ICacheNodeFactory {
        /**
         * Create a cache node with given key and value.
         * @param key Key.
         * @param value Value or data.
         * @return The node.
         */
        ICacheNode create(String key, byte[] value);
    }

    // Node interface.
    private interface ICacheNode {
        /**
         * Set the key.
         * @param key The given key.
         * @throws IllegalArgumentException, if the given key is an empty string.
         */
        void setKey(String key) throws IllegalArgumentException;

        /**
         * @return The key.
         */
        String getKey();

        /**
         * Set the data.
         * @param value The given data.
         * @throws IllegalArgumentException, if the given value is empty.
         */
        void setData(byte[] value) throws IllegalArgumentException;

        /**
         * @return The data.
         */
        byte[] getData();

        /**
         * Compare the given key with the key of current cache node.
         * @param key The given key.
         * @return True if they are the same; false if not.
         */
        boolean isKey(String key);

        /**
         * @return The size of the data.
         */
        int size();

        /**
         * Dispose the resources using by the cache node.
         */
        void dispose();
    }

    private static class CacheNodeFactory implements ICacheNodeFactory {

        private int mType;

        public CacheNodeFactory(int type) {
            mType = type;
        }

        @Override
        public ICacheNode create(String key, byte[] value) {
            ICacheNode node = null;

            switch (mType) {
                case TYPE_MEMORY:
                    node = new MemoNode();
                    break;
//                case TYPE_FILE:
//                    node = new FileNode();
//                    break;
            }

            if (node != null) {
                node.setKey(key);
                node.setData(value);
            }

            return node;
        }

        private static abstract class BaseNode implements ICacheNode {

            protected String mKey = null;

            @Override
            public void setKey(String key) throws IllegalArgumentException {
                if (key.length() <= 0) {
                    throw new IllegalArgumentException("Key cannot be empty.");
                }

                mKey = key;
            }

            @Override
            public String getKey() {
                return mKey;
            }

            @Override
            public void setData(byte[] value) throws IllegalArgumentException {
                if (value.length == 0) {
                    throw new IllegalArgumentException("The given byte array is empty.");
                }
            }

            @Override
            public byte[] getData() {
                return null;
            }

            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isKey(String key) {
                return mKey.compareTo(key) == 0;
            }

            @Override
            public void dispose() {
                mKey = null;
            }

        }

        private static class MemoNode extends BaseNode {

            private byte[] mValue = null;

            @Override
            public void setData(byte[] value) throws IllegalArgumentException {
                super.setData(value);
                mValue = value;
            }

            @Override
            public byte[] getData() {
                return mValue;
            }

            @Override
            public int size() {
                return mValue != null ? mValue.length : 0;
            }

            @Override
            public void dispose() {
                super.dispose();
                mValue = null;
            }

        }

        private static class FileNode extends BaseNode {

            private int mSize = 0;
            private String mFilePath = "/var/tmp/picocache-".concat(UUID.randomUUID().toString());

            @Override
            public void setData(byte[] value) throws IllegalArgumentException {
                super.setData(value);

                try {
                    FileOutputStream os = new FileOutputStream(mFilePath);

                    os.write(value);
                    os.close();

                    mSize = value.length;
//                mFile = Environment.getDownloadCacheDirectory();
                } catch (IOException e) {
                    // DO NOTHING.
                }
            }

            @Override
            public byte[] getData() {
                try {
                    File file = new File(mFilePath);
                    byte[] buffer = new byte[(int) file.length()];
                    FileInputStream is = new FileInputStream(file);
                    int readSize = 0;

                    readSize = is.read(buffer, 0, buffer.length);
                    is.close();

                    if (readSize == mSize) {
                        return buffer;
                    }
                } catch (IOException e) {
                    // DO NOTHING.
                }

                return null;
            }

            @Override
            public int size() {
                return mSize;
            }

            @Override
            public void dispose() {
                super.dispose();

                File file = new File(mFilePath);
                boolean isKilled = file.delete();

                if (isKilled) {
                    mSize = 0;
                    mFilePath = null;
                }
            }

        }

    }

}
