import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import sun.jvm.hotspot.utilities.Assert;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public class LruCache<K, V> {

    @NotNull
    private Map<K, Node> mCache = new HashMap<>();
    @Nullable
    private Node mHead = null;
    @Nullable
    private Node mTail = null;
    private final int mCapacity;

    public LruCache(int capacity) {
        mCapacity = capacity;
    }

    public void put(@NotNull K key, @NotNull V value) {
        Assert.that(mHead == null || mHead.mPreviousNode == null, "Head shouldn't have previous nodes!");
        Assert.that(mTail == null || mTail.mNextNode == null, "Tail shouldn't have next nodes!");

        if (mCache.containsKey(key)) {
            Assert.that(mHead != null, "Head shouldn't be null!");
            Assert.that(mTail != null, "Tail shouldn't be null!");

            Node storedNode = mCache.get(key);
            storedNode.mValue = value;
            bringToFront(storedNode);

            Assert.that(mHead == storedNode, "Node should be in the begging of the list!");
        } else {
            Assert.that(((mHead == null) == (mTail == null)),
                    "Head can't be null while Tail is nonnull and vice versa!" );

            Node newNode = new Node(key, value);
            if (mHead == null) {
                mHead = newNode;
                mTail = newNode;
            } else {
                Node prevHead = mHead;
                newNode.mNextNode = prevHead;
                prevHead.mPreviousNode = newNode;
                mHead = newNode;
            }

            Assert.that(mHead == newNode, "Node should be in the begging of the list!");
            mCache.put(key, newNode);

            if (mCache.size() > mCapacity) {
                mCache.remove(mTail.mKey);
                mTail = mTail.mPreviousNode;
                mTail.mNextNode = null;
            }
        }

        int listLen = getListLength();
        Assert.that(mCache.size() == listLen, "Linked list should be the same size as cache!");
    }

    private int getListLength() {
        int listLen = 0;
        Node start = mHead;
        while (start != null) {
            start = start.mNextNode;
            listLen++;
        }
        return listLen;
    }

    @Nullable
    public V get(@NotNull K key) {
        Assert.that(mHead == null || mHead.mPreviousNode == null, "Head shouldn't have previous nodes!");
        Assert.that(mTail == null || mTail.mNextNode == null, "Tail shouldn't have next nodes!");

        if (!mCache.containsKey(key)) {
            return null;
        }

        Node storedNode = mCache.get(key);
        bringToFront(storedNode);

        Assert.that(mHead == storedNode, "Node should be in the begging of the list!");
        int listLen = getListLength();
        Assert.that(mCache.size() == listLen, "Linked list should be the same size as cache!");
        return storedNode.mValue;
    }

    private void bringToFront(@NotNull Node node) {
        Assert.that(mHead != null, "Head shouldn't be null!");
        Assert.that(mTail != null, "Tail shouldn't be null!");
        Assert.that(mCache.containsValue(node), "Node should be present in cache!");

        if (mHead == node) {
            return;
        }

        if (mTail == node) {
            if (node.mPreviousNode != null) {
                mTail = node.mPreviousNode;
                mTail.mNextNode = null;
            }
        } else {
            Node prev = node.mPreviousNode;
            Node next = node.mNextNode;
            if (prev != null) {
                prev.mNextNode = next;
            }
            if (next != null) {
                next.mPreviousNode = prev;
            }
        }

        Node prevHead = mHead;
        node.mPreviousNode = null;
        node.mNextNode = prevHead;
        prevHead.mPreviousNode = node;
        mHead = node;
    }


    private class Node {
        @NotNull
        K mKey;
        @NotNull
        V mValue;
        @Nullable
        Node mPreviousNode;
        @Nullable
        Node mNextNode;

        Node(@NotNull K key, @NotNull V value) {
            mKey = key;
            mValue = value;
        }

    }
}
