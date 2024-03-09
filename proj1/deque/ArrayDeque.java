package deque;

import org.junit.Test;


import java.util.ArrayList;
import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private Object[] arr;
    private int size;
    private int length;
    private int idx_f, idx_l;
    public ArrayDeque() {
        arr = new Object[8];
        size = 0;
        length = arr.length;
        idx_f = arr.length / 2 - 1;
        idx_l = arr.length / 2;
    }
    public ArrayDeque(int size) {
        arr = new Object[size];
        size = 0;
        length = arr.length;
        idx_f = arr.length / 2 - 1;
        idx_l = arr.length / 2;
    }

    public Iterator<T> iterator() {
        return new arrdqIterator();
    }

    @Override
    public void addLast(T val) {

        size = size + 1;
        arr[idx_l] = val;
        if (idx_l + 1 == length) {
            resize(length + length / 2, direction.RIGHT);
        }
        idx_l = idx_l + 1;

    }

    @Override
    public void addFirst(T val) {

        size = size + 1;
        arr[idx_f] = val;
        if (idx_f - 1 < 0) {
            resize(length + length / 2, direction.LEFT);
        }
        idx_f = idx_f - 1;

    }

    @Override
    public T removeLast() {
        if (size == 0) return null;
        size = size - 1;
        idx_l = idx_l - 1;
        T val = (T) arr[idx_l];
        arr[idx_l] = null;
        if (size < length / 4 && length > 8) {
            resize(length / 2);
        }

        return val;
    }

    @Override
    public T removeFirst() {
        if (size == 0) return null;
        size = size - 1;
        idx_f = idx_f + 1;
        T val = (T) arr[idx_f];
        arr[idx_f] = null;
        if (size < length / 4 && length > 8) {
            resize(length / 2);
        }

        return val;
    }

    @Override
    public T get(int idx) {
        if (idx > size) {
            return null;
        }
        T val = (T) arr[idx_f + idx + 1];
        return val;
    }

    @Override
    public void printDeque() {
        Iterator<T> it = this.iterator();
        while (it.hasNext()) {
            System.out.print(it.next().toString() + ' ');
        }

    }

    /**
     * resize when need to get a smaller array
     *
     * @param aimSize size to change
     */

    private void resize(int aimSize) {
        Object[] newArr = new Object[aimSize];
        System.arraycopy(this.arr, idx_f + 1, newArr, aimSize / 2 - 1, size);
        this.idx_f = aimSize / 2 - 2;
        this.idx_l = idx_f + size + 1;
        this.length = aimSize;
        this.arr = newArr;
    }

    /**
     * resize when need to get a larger array in a specified direction
     *
     * @param aimSize size to change
     * @param d       left or right
     */
    private void resize(int aimSize, direction d) {
        Object[] newArr = new Object[aimSize];
        if (d == direction.RIGHT) {
            System.arraycopy(this.arr, idx_f, newArr, idx_f, size + 1);
            this.length = aimSize;
            this.arr = newArr;
        } else {
            System.arraycopy(this.arr, idx_f, newArr, length / 2, size + 1);
            if (idx_f == 0) {
                idx_f = length / 2;
                this.idx_l = idx_f + size;
            } else {
                this.idx_f = length / 2 - 1;
                this.idx_l = idx_f + size + 1;
            }


            this.length = aimSize;
            this.arr = newArr;
        }


    }

    @Override
    public int size() {
        return size;
    }

    public int getLength() {
        return length;
    }

    public boolean equals(ArrayDeque<T> ad) {
        ArrayList<T> al1 = new ArrayList<>();
        ArrayList<T> al2 = new ArrayList<>();
        for (T item : this) {
            al1.add(item);
        }
        for (T item : this) {
            al2.add(item);
        }
        return al1.equals(al2);
    }

    private enum direction {
        LEFT, RIGHT
    }

    private class arrdqIterator implements Iterator<T> {
        private int idx;

        public arrdqIterator() {
            idx = 0;
        }

        @Override
        public boolean hasNext() {
            return idx < size;
        }

        @Override
        public T next() {
            T val = get(idx);
            idx = idx + 1;
            return val;
        }
    }

}
