package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable, V> implements Map61B<K, V>{

    private static class BSTNode<K extends Comparable, V> {
        public K key;
        public V value;
        public BSTNode(K k, V v) {
            this.key = k;
            this.value = v;
            this.Lchild = null;
            this.Rchild = null;
        }
        public BSTNode() {
            this.key = null;
            this.value = null;
            this.Lchild = null;
            this.Rchild = null;
        }
        public BSTNode<K, V> Lchild;
        public BSTNode<K, V> Rchild;
    }
    private BSTNode<K, V> root;
    private int size;
    public BSTMap() {
        root = null;
        size = 0;
    }

    @Override
    public void put (K key, V value) {
        size += 1;
        BSTNode<K, V> newNode = new BSTNode<>(key, value);
        if(root == null) {
            root = newNode;
        } else {
            insert(root, newNode);
        }

    }
    /**
     * as a helper function of put(K key, V value);
     * @param original node need to insert
     * */
    private void insert(BSTNode node, BSTNode original) {
        int res = original.key.compareTo(node.key);
        if (res > 0) {
            if(node.Rchild == null) {
                node.Rchild = original;
            } else {
                insert(node.Rchild, original);
            }
        } else if (res < 0) {
            if (node.Lchild == null) {
                node.Lchild = original;
            } else {
                insert(node.Lchild, original);
            }
        }
    }

    @Override
    public boolean containsKey(K key) {
        BSTNode node = find(key, root);
        if (node == null) return false;
        return (node.key.equals(key));
    }

    @Override
    public V get (K key) {
        BSTNode node = find(key, root);
        if (node == null) return null;
        return (V) node.value;
    }


    private BSTNode find(K key, BSTNode node) {
        if (node == null) return null;
        int res = key.compareTo(node.key);
        if (res > 0) {
            return find(key, node.Rchild);
        } else if (res < 0) {
            return find(key, node.Lchild);
        } else {
            return node;
        }
    }


    @Override
    public void clear() {
        if(root == null) {
            return;
        } else {

            root = null;
        }
        size = 0;
    }
    /**
     * ONLY USED FOR CLEAR
     * */
//    private void remove_WithoutRebuild(BSTNode node) {
//        if (node == null) return;
//        remove_WithoutRebuild(node.Lchild);
//        remove_WithoutRebuild(node.Rchild);
//        node = null;
//    }



    public void printInOrder(BSTNode node) {
        if(node == null) {
            return;
        }
        printInOrder(node.Lchild);
        System.out.print(node.value.toString() + ' ');
        printInOrder(node.Rchild);
    }

    @Override
    public int size(){return this.size;}

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove (K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator () {
        throw new UnsupportedOperationException();
    }

}
