package deque;

public class LinkedListDeque<Type> implements Deque<Type>{

    private static class dequeNode<T>{
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
         * */
        public dequeNode(T val, dequeNode<T> pre,dequeNode<T> nxt) {
            this.val = val;
            this.pre = pre;
            this.nxt = nxt;
        }

    }

    // last <-> sentinel <-> First <->sentinel1 <-> last
    private dequeNode<Type> sentinel;
    private dequeNode<Type> sentinel1;
    private int size;

    public LinkedListDeque() {
        sentinel = new dequeNode<>(null);
        sentinel1 = new dequeNode<>(null,sentinel, sentinel);
        sentinel.pre = sentinel1;
        sentinel.nxt = sentinel1;
        size = 0;
    }

    /**
     * last <-> sentinel <-> First <->sentinel1 <-> last
     *
     * @param val value to addLast
     */
    @Override
    public void addLast(Type val) {
        size += 1;
        dequeNode<Type> newNode = new dequeNode<>(val);
        dequeNode<Type> p = sentinel.pre;
        sentinel.pre = newNode;
        newNode.pre = p;
        newNode.nxt = sentinel;
        if(p != null) {
            p.nxt = newNode;
        }
        if(newNode.pre == null) {
            newNode.pre = sentinel1;
            sentinel1.nxt = newNode;
        }

//        return val;
    }

    /**
     * last <-> sentinel <-> First <->sentinel1 <-> last
     *
     * @param val value to addFirst
     */
    @Override
    public void addFirst(Type val) {
        size += 1;
        dequeNode<Type> newNode = new dequeNode<>(val);
        dequeNode<Type> p = sentinel.nxt;
        sentinel.nxt = newNode;
        newNode.nxt = p;
        newNode.pre = sentinel;
        if(p != null) {
            p.pre = newNode;
        }
        if(newNode.nxt == null) {
            newNode.nxt = sentinel1;
            sentinel1.pre = newNode;
        }
//        return val;
    }

    @Override
    public int size() {return size;}

    public Type getLast() {return sentinel.pre.val;}

    public Type getFirst() {return sentinel.nxt.val;}

    /**
     * remove and delete the last node of the list
     * */
    @Override
    public Type removeLast() {
        if(size == 0){return null;}
        size -= 1;
        if(sentinel.pre == sentinel1) {
            dequeNode<Type> p = sentinel1.pre.pre;
            Type val = sentinel1.pre.val;
            sentinel1.pre = null;
            p.nxt = sentinel1;
            sentinel1.pre = p;
            return val;
        }
        dequeNode<Type> p = sentinel.pre.pre;
        Type val = sentinel.pre.val;
        sentinel.pre = null;
        p.nxt = sentinel;
        sentinel.pre = p;
        return val;
    }

    @Override
    public Type get(int index) {
        dequeNode<Type> p = sentinel;
        int idx = 0;
        while(p.nxt != sentinel) {
            idx += 1;
            p = p.nxt;
            if (p.val == null) {continue;}
            if (idx == index) {
                return p.val;
            }
        }
        return null;
    }

    /**
     * remove and delete the first node of the list
     * */
    @Override
    public Type removeFirst() {
        if(size == 0){return null;}
        size -=1;

        if(sentinel.nxt == sentinel1) {
            dequeNode<Type> p = sentinel1.nxt.nxt;
            Type val = sentinel1.nxt.val;
            p.pre = sentinel1;
            sentinel1.nxt = p;
            return val;
        }
        dequeNode<Type> p = sentinel.nxt.nxt;
        Type val = sentinel.nxt.val;
        p.pre = sentinel;
        sentinel.nxt = p;
        return val;
    }

//    public String printDeque() {
//        StringBuilder str = new StringBuilder();
//        dequeNode<Type> p = sentinel;
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
        dequeNode<Type> p = sentinel;
        while(p .nxt != sentinel) {
            p = p.nxt;
            if (p.val == null) {continue;}
            System.out.print(p.val.toString() + ' ');

        }
        System.out.println();
    }




}
