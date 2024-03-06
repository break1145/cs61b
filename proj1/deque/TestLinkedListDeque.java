package deque;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestLinkedListDeque {
    @Test
    public void testprintDeque() {
        LinkedListDeque<Integer> testDeque = new LinkedListDeque<>();
        testDeque.addLast(1);
        testDeque.addLast(2);
        testDeque.addLast(3);
        testDeque.addLast(4);
        testDeque.addLast(5);
        String expectedString = "1 2 3 4 5 ";
        assertEquals(expectedString , testDeque.printDeque());
    }

    @Test
    public void testAddLast() {
        LinkedListDeque<Integer> testDeque = new LinkedListDeque<>();
        testDeque.addLast(1);
        testDeque.addLast(2);
        testDeque.addLast(3);
        testDeque.addLast(4);
        testDeque.addLast(5);
        String expectedString = "1 2 3 4 5 ";
        assertEquals(expectedString , testDeque.printDeque());
    }

    @Test
    public void testAddFirst() {
        LinkedListDeque<Integer> testDeque = new LinkedListDeque<>();
        testDeque.addFirst(1);
        testDeque.addFirst(2);
        testDeque.addFirst(3);
        testDeque.addFirst(2);
        testDeque.addFirst(4);
        String expectedString = "4 2 3 2 1 ";
        assertEquals(expectedString , testDeque.printDeque());
    }

    @Test
    public void testAddFirstAndLast() {
        LinkedListDeque<Integer> testDeque = new LinkedListDeque<>();
        testDeque.addFirst(1);
        testDeque.addLast(3);
        testDeque.addFirst(2);
        testDeque.addLast(2);
        testDeque.addFirst(3);
        testDeque.addLast(1);
        testDeque.addLast(5);
        testDeque.addLast(4);
        testDeque.addFirst(2);
        testDeque.addFirst(4);
        String expectedString = "4 2 3 2 1 3 2 1 5 4 ";
        assertEquals(expectedString , testDeque.printDeque());
    }

    @Test
    public void testRemoveLast() {
        LinkedListDeque<Integer> testDeque = new LinkedListDeque<>();
        testDeque.addFirst(1);
        testDeque.addLast(3);
        testDeque.addFirst(2);
        testDeque.addLast(2);
        testDeque.addFirst(3);
        testDeque.addLast(1);
        testDeque.addLast(5);
        testDeque.addLast(4);
        testDeque.addFirst(2);
        testDeque.addFirst(4);
        testDeque.removeLast();
        testDeque.removeLast();
        testDeque.removeLast();
        String expectedString = "4 2 3 2 1 3 2 ";
        assertEquals(expectedString , testDeque.printDeque());
    }

    @Test
    public void testRemoveFirst() {
        LinkedListDeque<Integer> testDeque = new LinkedListDeque<>();
        testDeque.addFirst(1);
        testDeque.addLast(3);
        testDeque.addFirst(2);
        testDeque.removeFirst();
        testDeque.removeFirst();
        testDeque.removeFirst();
        String expectedString = "";
        assertEquals(expectedString , testDeque.printDeque());
    }
    @Test
    public void testRemoveFirstAndLast() {
        LinkedListDeque<Integer> testDeque = new LinkedListDeque<>();
        testDeque.addFirst(1);
        testDeque.addLast(3);
        testDeque.addFirst(2);
        testDeque.addLast(2);
        testDeque.addFirst(3);
        testDeque.addLast(1);
        testDeque.addLast(5);
        testDeque.addLast(4);
        testDeque.addFirst(2);
        testDeque.addFirst(4);
        testDeque.removeFirst();
        testDeque.removeFirst();
        testDeque.removeFirst();
        testDeque.removeLast();
        testDeque.removeLast();
        testDeque.removeLast();
        String expectedString = "2 1 3 2 ";
        assertEquals(expectedString , testDeque.printDeque());
    }

    @Test
    public void testSize() {
        LinkedListDeque<Integer> testDeque = new LinkedListDeque<>();
        testDeque.addFirst(1);
        testDeque.addLast(3);
        testDeque.addFirst(2);
        testDeque.addLast(2);
        testDeque.addFirst(3);
        testDeque.addLast(1);
        testDeque.addLast(5);
        testDeque.addLast(4);
        testDeque.addFirst(2);
        testDeque.addFirst(4);
        testDeque.removeFirst();
        testDeque.removeFirst();
        testDeque.removeFirst();
        testDeque.removeLast();
        testDeque.removeLast();
        testDeque.removeLast();
        int Size = testDeque.size();
        assertEquals(4, Size);
    }

}
