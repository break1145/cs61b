package deque;

import java.util.Iterator;


public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {

    // last <-> sentinel <-> First <->sentinel1 <-> last
    private final dequeNode<T> sentinel;
    private final dequeNode<T> sentinel1;
    private int size;

    public LinkedListDeque() {
        sentinel = new dequeNode<>(null);
        sentinel1 = new dequeNode<>(null, sentinel, sentinel);
        sentinel.pre = sentinel1;
        sentinel.nxt = sentinel1;
        size = 0;
    }

    public Iterator<T> iterator() {
        return new linkedListDequeIterator<>();
    }

    /**
     * last <-> sentinel <-> First <->sentinel1 <-> last
     *
     * @param val value to addLast
     */
    @Override
    public void addLast(T val) {
        size += 1;
        dequeNode<T> newNode = new dequeNode<>(val);
        dequeNode<T> p = sentinel.pre;
        sentinel.pre = newNode;
        newNode.pre = p;
        newNode.nxt = sentinel;
        if (p != null) {
            p.nxt = newNode;
        }
        if (newNode.pre == null) {
            newNode.pre = sentinel1;
            sentinel1.nxt = newNode;
        }
    }

    /**
     * last <-> sentinel <-> First <->sentinel1 <-> last
     *
     * @param val value to addFirst
     */
    @Override
    public void addFirst(T val) {
        size += 1;
        dequeNode<T> newNode = new dequeNode<>(val);
        dequeNode<T> p = sentinel.nxt;
        sentinel.nxt = newNode;
        newNode.nxt = p;
        newNode.pre = sentinel;
        if (p != null) {
            p.pre = newNode;
        }
        if (newNode.nxt == null) {
            newNode.nxt = sentinel1;
            sentinel1.pre = newNode;
        }
//        return val;
    }

    @Override
    public int size() {
        return size;
    }

    public T getLast() {
        return sentinel.pre.val;
    }

    public T getFirst() {
        return sentinel.nxt.val;
    }

    /**
     * remove and delete the last node of the list
     */
    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        size -= 1;
        if (sentinel.pre == sentinel1) {
            dequeNode<T> p = sentinel1.pre.pre;
            T val = sentinel1.pre.val;
            sentinel1.pre = null;
            p.nxt = sentinel1;
            sentinel1.pre = p;
            return val;
        }
        dequeNode<T> p = sentinel.pre.pre;
        T val = sentinel.pre.val;
        sentinel.pre = null;
        p.nxt = sentinel;
        sentinel.pre = p;
        return val;
    }

    @Override
    public T get(int index) {
        index += 1;
        if (index > size) {
            return null;
        }
        dequeNode<T> p = sentinel;
        int idx = 0;
        while (idx < size) {
            p = p.nxt;
            if (p.val == null) {
                continue;
            }
            idx += 1;
            if (idx == index) {
                return p.val;
            }
        }
        return null;
    }

    /**
     * remove and delete the first node of the list
     */
    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        size -= 1;

        if (sentinel.nxt == sentinel1) {
            dequeNode<T> p = sentinel1.nxt.nxt;
            T val = sentinel1.nxt.val;
            p.pre = sentinel1;
            sentinel1.nxt = p;
            return val;
        }
        dequeNode<T> p = sentinel.nxt.nxt;
        T val = sentinel.nxt.val;
        p.pre = sentinel;
        sentinel.nxt = p;
        return val;
    }

    //    public String printDeque() {
//        StringBuilder str = new StringBuilder();
//        dequeNode<T> p = sentinel;
//        while(p .nxt != sentinel) {
//            p = p.nxt;
//            if (p.val == null) {continue;}
//            str.append(p.val);
//            str.append(' ');
//            System.out.printf(p.val.toString() + ' ');
//        }
//        System.out.println();
//        return str.toString();
//    }
    @Override
    public void printDeque() {
        dequeNode<T> p = sentinel;
        while (p.nxt != sentinel) {
            p = p.nxt;
            if (p.val == null) {
                continue;
            }
            System.out.print(p.val.toString() + ' ');

        }
        System.out.println();
    }

    public boolean equals(LinkedListDeque<T> lld) {
        boolean equals = true;
        for(int i = 0;i < lld.size;i ++) {
            if(lld.get(i) != this.get(i)) {
                equals = false;
                break;
            }
        }
        return equals;
    }

    private static class dequeNode<T> {
        public T val;
        dequeNode<T> pre, nxt;

        public dequeNode(T val) {
            this.val = val;
            this.pre = null;
            this.nxt = null;
        }

        /**
         * @param val node's value
         * @param pre pre node
         * @param nxt next node
         */
        public dequeNode(T val, dequeNode<T> pre, dequeNode<T> nxt) {
            this.val = val;
            this.pre = pre;
            this.nxt = nxt;
        }

    }

    private class linkedListDequeIterator<T> implements Iterator<T> {

        private int idx;

        public linkedListDequeIterator() {
            idx = 0;
        }

        @Override
        public boolean hasNext() {
            return idx < size() && get(idx) != null;
        }

        @Override
        public T next() {
            if (hasNext()) {
                T val = (T) get(idx);
                idx += 1;
                return val;
            }
            return null;
        }
    }


}
