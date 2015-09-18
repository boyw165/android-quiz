package cb.factory;

import java.util.LinkedList;
import java.util.ListIterator;

import cb.inteface.PicoCache;

/**
 * Created by boyw165 on 9/14/15.
 */
public class PicoCacheFactory {

    public static final int TYPE_MEMORY = 1;
    public static final int TYPE_FILE = 2;

    public static final int STRATEGY_FIFO = 100;
    public static final int STRATEGY_LIFO = 200;

    private static final int DEFAULT_CACHE_SIZE = 20;

    public static PicoCache create(int cacheType, int cacheStrategy) {
        PicoCache ret;
        BaseNode node;

//        switch (cacheType) {
//            case TYPE_MEMORY:
//                node = new MemoNode();
//                break;
//            case TYPE_FILE:
//                node = new FileNode();
//                break;
//            default:
//
//        }

        switch (cacheStrategy) {
            case STRATEGY_FIFO:
                ret = new FifoStore();
                break;
            case STRATEGY_LIFO:
                ret = new LifoStore();
                break;
            default:
                ret = new FifoStore();
        }

        return ret;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Store Node //////////////////////////////////////////////////////////////////////////////////

    private static class BaseNode {

        public String mKey;
        public byte[] mValue;

        public boolean isKey(String key) {
            return mKey.compareTo(key) == 0;
        }

        public int size() {
            return mValue.length;
        }

        @Override
        public String toString() {
            return String.format("key=%s, size=%s", mKey, size());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Store ///////////////////////////////////////////////////////////////////////////////////////

    private static abstract class BaseStore implements PicoCache {

        // Current total size in bytes.
        protected int mSize;

        // Maximum total size in bytes.
        protected int mSizeMax;

        // The linked-list store.
        protected LinkedList<BaseNode> mList;

        public BaseStore() {
            mSize = 0;
            mSizeMax = DEFAULT_CACHE_SIZE;
            mList = new LinkedList<>();
        }

        @Override
        public synchronized void setCacheSize(int kb) {
            if (kb > 0) {
                mSizeMax = kb << 10;
            }
        }

        @Override
        public synchronized void put(String key, byte[] data) {
            try {
                BaseNode current;
                boolean isFound = false;
                ListIterator<BaseNode> it = mList.listIterator();

                // Check whether the given key is already in the store.
                while (it.hasNext()) {
                    current = it.next();
                    if (current.isKey(key)) {

                        mSize -= current.size();
                        current.mValue = data;
                        mSize += current.size();

                        isFound = true;
                        break;
                    }
                }

                // Create new node if it is new.
                if (!isFound) {
                    BaseNode node = new BaseNode();

                    node.mKey = key;
                    node.mValue = data;

                    mSize += node.mValue.length;
                    mList.add(node);
                }

            } catch (Exception e) {
                // DO NOTHING.
            }
        }

        @Override
        public synchronized byte[] get(String key) {
            try {
                ListIterator<BaseNode> it = mList.listIterator();
                BaseNode current;

                while (it.hasNext()) {
                    current = it.next();
                    if (current.isKey(key)) {
                        return current.mValue;
                    }
                }

            } catch (Exception e) {
                // DO NOTHING.
            }

            return null;
        }

        @Override
        public int size() {
            return mSize;
        }

        @Override
        public synchronized void clear() {
            mList.remove();
        }

        @Override
        public String toString() {
            String ret = new String();
            ListIterator<BaseNode> it = mList.listIterator();
            BaseNode current;

            while (it.hasNext()) {
                current = it.next();
                ret = ret.concat("key=" + current.mKey
                        + "; data size=" + current.size() + "\n");
            }

            return ret;
        }
    }

    private static class FifoStore extends BaseStore {

        @Override
        public void put(String key, byte[] data) {
            super.put(key, data);

            try {
                if (mSize > mSizeMax) {
                    ListIterator<BaseNode> it = mList.listIterator(mList.size());
                    BaseNode current;
                    int totalSize = 0;

                    while (it.hasPrevious()) {
                        current = it.previous();
                        totalSize += current.size();

                        // Remove all the rest nodes because the strategy is FILO.
                        if (totalSize > mSizeMax) {
                            totalSize -= current.size();
                            mSize -= current.size();
                            it.remove();
                        }
                    }
                }
            } catch (Exception e) {
                // DO NOTHING.
            }
        }

    }

    private static class LifoStore extends BaseStore {

        @Override
        public synchronized void put(String key, byte[] data) {
            super.put(key, data);

            try {
                ListIterator<BaseNode> it = mList.listIterator(mList.size());
                BaseNode current;

                while (it.hasPrevious() && mSize > mSizeMax) {
                    current = it.previous();
                    it.remove();

                    mSize -= current.size();
                }
            } catch (Exception e) {
                // DO NOTHING.
            }
        }

    }
}
