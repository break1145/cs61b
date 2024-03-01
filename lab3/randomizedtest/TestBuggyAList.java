package randomizedtest;

import edu.princeton.cs.algs4.Insertion;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> alist_nobug = new AListNoResizing<>();
        BuggyAList<Integer> alist_buggy = new BuggyAList<>();


        int idx = 0;
        for(idx = 0;idx <= 5000;idx ++) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0){
                int randVal = StdRandom.uniform(0, 100);
                alist_buggy.addLast(randVal);
                alist_nobug.addLast(randVal);
                System.out.println("AddLast " + randVal);
            } else if (operationNumber == 1) {
                if(alist_buggy.size() < 1){
                    continue;
                }
                int lastItem0 = alist_buggy.getLast();
                int lastItem1 = alist_nobug.getLast();
                assertEquals(lastItem0, lastItem1);
                System.out.println("GetLast " + lastItem0 );
            } else if (operationNumber == 2){
                if(alist_buggy.size() < 1){
                    continue;
                }
                int lastItem0 = alist_buggy.removeLast();
                int lastItem1 = alist_nobug.removeLast();
                assertEquals(lastItem0, lastItem1);
                System.out.println("RemoveLast " + lastItem0);
            } else {
                int size = alist_buggy.size();
                int size1 = alist_nobug.size();
                System.out.println("size: " + size);
                assertEquals(size, size1);
            }
        }


    }
}
