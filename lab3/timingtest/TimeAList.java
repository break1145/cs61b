package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        /**
         * @param Ns is the first column
     *           times is the second column
     *           opCounts is the third column
         * */
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
        timeAListConstruction();
    }

    public static void timeAListConstruction() {

        AList<Integer> Ns = new AList<Integer>();
        AList<Integer> opCount = new AList<Integer>();
        AList<Double> times = new AList<Double>();

        runtest(1000,Ns, times, opCount);
        runtest(2000,Ns, times, opCount);
        runtest(4000,Ns, times, opCount);
        runtest(8000,Ns, times, opCount);
        runtest(16000,Ns, times, opCount);
        runtest(32000,Ns, times, opCount);
        runtest(64000,Ns, times, opCount);
        runtest(128000,Ns, times, opCount);

        printTimingTable(Ns, times, opCount);
    }
    public static void runtest(int N,AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        AList<Integer> alist = new AList<Integer>();
        Stopwatch sw = new Stopwatch();
        for(int i = 0;i < N;i ++) {
            alist.addLast(i);
        }
        double timeInSeconds = sw.elapsedTime();
        Ns.addLast(N);
        times.addLast(timeInSeconds);
        opCounts.addLast(alist.get_opCount());
    }
}
