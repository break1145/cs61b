package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> c) {
        this.comparator = c;
    }

    public T max() {
        if (isEmpty()) {
            return null;
        }
        T max = get(0);
        for (T item : this) {
            if (comparator.compare(max, item) < 0) {
                max = item;
            }
        }
        return max;
    }

    public T max(Comparator<T> c) {
        this.comparator = c;
        return max();
    }
}
