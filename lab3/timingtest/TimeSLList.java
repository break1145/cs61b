package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        AList<Integer> Ns = new AList<Integer>();
        AList<Integer> opCounts = new AList<Integer>();
        AList<Double> times = new AList<Double>();

        runtest(1000,Ns, times, opCounts);
        runtest(2000,Ns, times, opCounts);
        runtest(4000,Ns, times, opCounts);
        runtest(8000,Ns, times, opCounts);
        runtest(16000,Ns, times, opCounts);
        runtest(32000,Ns, times, opCounts);
        runtest(64000,Ns, times, opCounts);
        runtest(128000,Ns, times, opCounts);
        printTimingTable(Ns, times, opCounts);

    }
    public static void runtest(int N,AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts){
        int M = 10000;
        SLList<Integer> slist = new SLList<Integer>();
        for(int i = 0;i < N;i ++) {
            slist.addLast(i);
        }
        Stopwatch sw = new Stopwatch();
        for(int i = 0;i < M;i ++) {
            slist.getLast();
        }
        Double timeInStop = sw.elapsedTime();
        Ns.addLast(N);
        times.addLast(timeInStop);
        opCounts.addLast(M);
    }


}
