package deque;

import org.junit.Test;


import java.util.Iterator;

public class ArrayDeque<Type> implements Deque<Type>, Iterable<Type> {
    private class arrdqIterator implements Iterator<Type> {
        private int idx;
        public arrdqIterator() {
            idx = 0;
        }
        @Override
        public boolean hasNext() {
            return idx < size;
        }
        @Override
        public Type next() {
            Type val =  get(idx);
            idx = idx + 1;
            return val;
        }
    }
    public Iterator<Type> iterator() {
        return new arrdqIterator();
    }
    private Object[] arr;
    private int size;
    private int length;
    private int idx_f,idx_l;
    public ArrayDeque() {
        arr = new Object[8];
        size = 0;
        length = arr.length;
        idx_f = arr.length/ 2 - 1;
        idx_l = arr.length/ 2;
    }
    public enum direction
    {
        LEFT, RIGHT;
    }
    @Override
    public void addLast(Type val) {

        size = size + 1;
        arr[idx_l] = val;
        idx_l = idx_l + 1;
        if(idx_l + 1 == length) {
            resize(length + length/ 2, direction.RIGHT);
        }
    }
    @Override
    public void addFirst(Type val) {

        size = size + 1;
        arr[idx_f] = val;
        idx_f = idx_f - 1;
        if(idx_f - 1 < 0)  {
            resize(length + length/ 2, direction.LEFT);
        }
    }
    @Override
    public Type removeLast() {
        if(size == 0) return null;
        size = size - 1;
        idx_l = idx_l - 1;
        Type val = (Type) arr[idx_l];
        arr[idx_l] = null;


        if(size < length/ 4) {
            resize(length/2);
        }

        return val;
    }
    @Override
    public Type removeFirst() {
        if(size == 0) return null;
        size = size - 1;
        idx_f = idx_f + 1;
        Type val = (Type) arr[idx_f];
        arr[idx_f] = null;
        if(size < length/ 4) {
            resize(length/2);
        }

        return val;
    }
    @Override
    public Type get(int idx) {
        if(idx > size) {
            return null;
        }
        Type val = (Type) arr[idx_f + idx + 1];
        return val ;
    }
//    public String printDeque() {
//        StringBuilder stringBuilder = new StringBuilder();
//        Iterator<Type> it = this.iterator();
//        while(it.hasNext()) {
//            stringBuilder.append(it.next().toString());
//            stringBuilder.append(' ');
//        }
//        return stringBuilder.toString();
//
//    }
    @Override
    public void printDeque() {
        Iterator<Type> it = this.iterator();
        while(it.hasNext()) {
            System.out.print(it.next().toString() + ' ');
        }


    }
    /**
    * resize when need to get a smaller array
    * @param aimSize size to change
    * */

    private void resize(int aimSize) {
        Object[] newArr = new Object[aimSize];
        System.arraycopy(this.arr, idx_f+ 1, newArr, aimSize/2-1, size);
        this.idx_f = aimSize/2-2;
        this.idx_l = idx_f + size + 1;
        this.length = aimSize;
        this.arr = newArr;
    }
    /**
     * resize when need to get a larger array in a specified direction
     * @param aimSize size to change
     * @param d left or right
     * */
    private void resize(int aimSize, direction d) {
        Object[] newArr = new Object[aimSize];
        if(d == direction.RIGHT) {
            System.arraycopy(this.arr, idx_f, newArr, 0, size);
            this.length = aimSize;
            this.arr = newArr;
        } else {
            System.arraycopy(this.arr, idx_f, newArr, length/2, size+ 1);
            this.idx_f = length/2 ;
            this.idx_l = idx_f + size+ 1;
            this.length = aimSize;
            this.arr = newArr;
        }


    }
    @Override
    public int size() {return size;}
    public int getLength() {return length;}

}
