package deque;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestArrayDeque {
    @Test
    public void testAddLast() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        ad.addLast(1);
        ad.addLast(2);
        String expected = "1 2 ";
        assertEquals(expected, ad.printDeque());
    }
    @Test
    public void testAddFrist() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        ad.addFirst(0);
        ad.addFirst(1);
        ad.addFirst(2);
        ad.addFirst(3);
        ad.addFirst(4);
        ad.addFirst(5);
        ad.addFirst(6);
        ad.addFirst(7);
        String expected = "7 6 5 4 3 2 1 0 ";
        assertEquals(expected, ad.printDeque());
    }
    @Test
    public void tsetAddFirstAndLast() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        ad.addLast(1);
        ad.addLast(2);
        ad.addFirst(1);
        ad.addFirst(5);
        String expected = "5 1 1 2 ";
        assertEquals(expected, ad.printDeque());
    }
    @Test
    public void testRemoveLast() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        ad.addFirst(0);
        ad.addFirst(1);
        ad.addFirst(2);
        ad.addFirst(3);
        ad.addFirst(4);
        ad.addFirst(5);
        ad.addFirst(6);
        ad.addFirst(7);

        ad.removeLast();
        ad.removeLast();
        String expected = "7 6 5 4 3 2 ";
        assertEquals(expected, ad.printDeque());
        ad.removeFirst();
        ad.removeFirst();
        expected = "5 4 3 2 ";
        assertEquals(expected, ad.printDeque());

    }
}
