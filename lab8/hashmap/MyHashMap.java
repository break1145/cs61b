package hashmap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author break
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!


    private int size;
    private int maxSize;
    private double loadFactor;
    private Set<K> keySet;
    /** Constructors */
    public MyHashMap() {
        this(16, 0.75);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, 0.75);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.size = 0;
        this.maxSize = initialSize;
        this.loadFactor = maxLoad;
        this.buckets = createTable(initialSize);
        this.keySet = new HashSet<>();
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {return new HashSet<>();}

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!
    public void clear() {
        this.buckets = createTable(16);
        this.size = 0;
        this.maxSize = 16;
        this.keySet = new HashSet<>();
    }

    public boolean containsKey(K key) {
        return this.keySet.contains(key);
    }

    public V get(K key) {
        int pos = ((key.hashCode() % maxSize) + maxSize) % maxSize;
        if(buckets[pos] == null) {
            return null;
        }
        for(Node x : buckets[pos]) {
            if (x.key.equals(key)) {
                return x.value;
            }
        }
        return null;
    }

    public int size() { return this.size; }

    /**
     * 插入一个键值对，根据node的hashcode决定插入位置
     * @param key 键
     * @param value 值
     * */
    public void put(K key, V value) {
        Node node = createNode(key, value);
        put(node, this.buckets, true);
        size += 1;

    }
    /**
     * 将键值对插入哈希表
     * @param node 插入的Node
     * @param _buckets 被插入的桶数组
     * */
    private void put(Node node, Collection[] _buckets, boolean allowReSize) {
        int pos = ((node.key.hashCode() % maxSize) + maxSize) % maxSize;
        if(_buckets[pos] == null) {
            _buckets[pos] = createBucket();
        }
        if(this.containsKey(node.key)) {
            Iterator<Node> iterator = _buckets[pos].iterator();
            while(iterator.hasNext()) {
                Node x = iterator.next();
                if (x.key.equals(node.key)) {
                    iterator.remove(); // 安全地移除元素
                    this.size -= 1;
                }
            }
        }

        _buckets[pos].add(node);
        this.keySet.add(node.key);
        if(size / maxSize > loadFactor && allowReSize) {
            reSize(maxSize* 2);
        }
    }
    private void reSize(int newSize) {
        Collection[] newBuckets = new Collection[newSize];
        for(int i = 0;i < maxSize;i ++) {
            if(buckets[i] == null) {
                continue;
            }
            for(Node x: buckets[i]) {
                put(x, newBuckets, false);
            }
        }
        this.buckets = newBuckets;
    }

    public Set<K> keySet() { return this.keySet; }

    public V remove(K key) {
        int pos = ((key.hashCode() % maxSize) + maxSize) % maxSize;
        if(this.buckets[pos] == null || !this.containsKey(key)) {
            return null;
        }
        for(Node x: buckets[pos]) {
            if(x.key.equals(key)) {
                V val = x.value;
                buckets[pos].remove(x);
                this.keySet.remove(key);
                return val;
            }
        }
        return null;
    }

    public V remove(K key, V value) {
        return remove(key);
    }


    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

}
