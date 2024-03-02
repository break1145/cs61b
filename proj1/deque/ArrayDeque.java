package deque;

public class ArrayDeque<Type> {
    private Object[] arr;
    private int size;
    private int length;
    private int idx_f,idx_l;
    public ArrayDeque() {
        arr = new Object[8];
        size = 0;
        length = arr.length;
        idx_f = length;
        idx_l = 0;
    }
    public void addLast(Type val) {
        size = size + 1;
        arr[idx_l] = val;
        idx_l = (idx_l + 1) % length;
    }
    public void addFirst(Type val) {
        size = size + 1;
        idx_f = (idx_f + length - 1) % length;
        arr[idx_f] = val;

    }
    public Type removeLast() {
        size = size - 1;
        idx_l = (idx_l + length - 1) % length;
        Type val = (Type) arr[idx_l];
        arr[idx_l] = null;
        return val;
    }
    public Type removeFirst() {
        size = size - 1;
        Type val = (Type) arr[idx_f];
        arr[idx_f] = null;
        idx_f = (idx_f + 1) % length;


        return val;
    }
    public Type get(int idx) {return (Type) arr[(idx + idx_f) % length];}
    public String printDeque() {
        StringBuffer str = new StringBuffer();
        for(int i = 0;i < length;i ++){
            if(get(i) == null) continue;
            System.out.printf(get(i).toString() + " ");
            str.append(get(i));
            str.append(' ');
        }
        System.out.println();
        return str.toString();
    }
    public int size() {return size;}
    public boolean isEmpty() {return size == 0;}
}
