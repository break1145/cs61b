package deque;

import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestArrayDeque {
    @Test
    public void testAddLast() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        ad.addLast(1);
        ad.addLast(2);
        String expected = "1 2 ";
//        assertEquals(expected, ad.printDeque());
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
//        assertEquals(expected, ad.printDeque());
    }
    @Test
    public void tsetAddFirstAndLast() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        ad.addLast(1);
        ad.addLast(2);
        ad.addFirst(1);
        ad.addFirst(5);
        String expected = "5 1 1 2 ";
//        assertEquals(expected, ad.printDeque());
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
//        assertEquals(expected, ad.printDeque());
        ad.removeFirst();
        ad.removeFirst();
        expected = "5 4 3 2 ";
//        assertEquals(expected, ad.printDeque());
    }
    @Test
    public void testRemoveFirst() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        for(int i = 0;i < 100;i ++) {
            ad.addLast(i);
        }
        while(! ad.isEmpty()) {
            ad.removeFirst();
            ad.removeLast();
        }

    }
    @Test
    public void testResize() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        ad.addFirst(0);
        ad.addFirst(1);
        ad.addFirst(2);
        ad.addFirst(3);
        ad.addFirst(4);
        ad.addFirst(5);
        ad.addFirst(6);
        ad.addFirst(7);
        ad.addFirst(114514);
        String expected = "114514 7 6 5 4 3 2 1 0 ";
//        assertEquals(expected, ad.printDeque());
        assertEquals(9, ad.size());
        for (int i = 0; i < 6; i++) {
            ad.removeFirst();
        }
        expected = "2 1 0 ";
//        assertEquals(expected, ad.printDeque());
    }
    @Test
    public void testGet() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        ad.addFirst(0);
        ad.addFirst(1);
        ad.addFirst(2);
        ad.addFirst(3);
        ad.addFirst(4);
        ad.addFirst(5);
        ad.addFirst(6);
        ad.addFirst(7);
        ad.addLast(2);
//        for(int i = 0;i < ad.size();i ++) {
//            System.out.print(ad.get(i).toString());
//        }
        ad = new ArrayDeque<>();
        for (int i = 0;i < 10;i ++) {
            ad.addLast(i);
        }
        for(int i = 0;i < ad.size();i ++) {
            System.out.print(ad.get(i).toString());
        }

    }
    @Test
    public void testxxx() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();

        for(int i = 1;i < 10;i ++) {
            ad.addFirst(0);
            ad.addFirst(1);
            ad.addFirst(2);
            ad.addFirst(3);
            ad.addFirst(4);
            ad.addFirst(5);
            ad.addFirst(6);
            ad.addFirst(7);
        }
        for(int i = 1;i < 60;i ++) {
            ad.removeLast();
        }
    }
    @Test
    public void testForEach() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        ad.addFirst(0);
        ad.addFirst(1);
        ad.addFirst(2);
        ad.addFirst(3);
        ad.addFirst(4);
        ad.addFirst(5);
        ad.addFirst(6);
        ad.addFirst(7);
        for(Integer item : ad) {
            System.out.print(item.toString() + ' ');
        }

    }
    @Test
    public void testbig() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        for(int i = 0;i < 10;i ++) {
            ad.addFirst(1);
//            ad.addLast(2);
//            ad.addFirst(3);
//            ad.addLast(4);
//            ad.addFirst(5);
//            ad.addLast(6);
        }

        for(int i = 0;i < 13;i ++) {
            ad.removeFirst();
//            ad.removeLast();
//            ad.removeFirst();
//            ad.removeLast();
        }
        ad.addLast(2);
            ad.addFirst(3);
            ad.addLast(4);
            ad.addFirst(5);
            ad.addLast(6);
        System.out.println(ad.size());
        System.out.println(ad.getLength());
    }
    @Test
    public void test_remove_add_first_IsEmpty() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        ad.isEmpty();
        ad.addFirst(1);
        ad.addFirst(2);
        ad.removeFirst();
        ad.addFirst(4);
        assertEquals("4", ad.get(0).toString());
    }
    @Test
    public void test_remove_add_last_IsEmpty() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        ad.addLast(1);
        ad.addLast(2);
        ad.removeLast();
        ad.removeLast();
        ad.addLast(3);
        assertEquals("3", ad.get(0).toString());
    }
    @Test
    public void testSomething() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        ad.addFirst(0);
        ad.addFirst(1);
        ad.removeFirst();
        ad.removeFirst();
        ad.addLast(5);
        System.out.print(ad.get(0));
    }
    @Test
    public void testfillup_and_clean() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        for(int i = 0;i < 8;i ++) {
            ad.addLast(i);
        }
        for(int i = 0;i < 60;i ++) {
            ad.removeLast();
            ad.removeFirst();
        }
        ad.addFirst(1);
        ad.addLast(2);
        ad.addFirst(3);
        ad.addLast(4);
        ad.addFirst(5);
        ad.addLast(6);
    }
    @Test
    public void testEquals() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        ad.addFirst(1);
        ad.addLast(2);
        ad.addFirst(3);
        ad.addLast(4);
        ad.addFirst(5);
        ad.addLast(6);
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        ad1.addFirst(1);
        ad1.addLast(2);
        ad1.addFirst(3);
        ad1.addLast(4);
        ad1.addFirst(5);
        ad1.addLast(6);
        assertTrue(ad.equals(ad1));
    }
    @Test
    public void test_with_multiple_iterators() {
        ArrayDeque<Integer> ad =new ArrayDeque<>();
        ad.addFirst(1);
        ad.addLast(2);
        ad.addFirst(3);
        ad.addLast(4);
        ad.addFirst(5);
        ad.addLast(6);
        Iterator<Integer> it = ad.iterator();
        Iterator<Integer> it2 = ad.iterator();
        Iterator<Integer> it3 = ad.iterator();
        it3.next();
        it2.next();
        it2.next();
        while(it.hasNext()) {
            System.out.print(it.next());
            System.out.print(it2.next());
            System.out.print(it3.next());
        }
    }
}
