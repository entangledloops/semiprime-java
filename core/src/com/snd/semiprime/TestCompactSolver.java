package com.snd.semiprime;

import java.io.File;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.function.BiFunction;

public class TestCompactSolver
{
    private final static int              seed    = 1;
    private final static Random           random  = new Random(seed);
    private final static   SimpleDateFormat format  = new SimpleDateFormat("MM-dd-yyyy");
    private final static   String           testDir = "test";
    private final static   String           prefix  = testDir + "/" + format.format(new Date()) + ".seed-" + seed + ".";

    public static boolean trends(int minLen, int maxLen, int runs)
    {
        CompactSolver solver = new CompactSolver();

        BiFunction<CompactSolver, CompactSolver.Node, Double> h = (s, n) ->
        {
            BigInteger p = n.p();
            BigInteger q = n.q();

            return (double) Math.abs(2*s.semiprime.bitCount() - (p.bitCount() + q.bitCount()));

            //return n.s().xor(s.semiprime).bitCount() / (double) s.semiprime.bitLength();

            /*
            int depth = n.depth()+1;
            if (depth < 8) {

                return ((double) depth) / s.semiprime.bitLength();
                //Log.o(c+"");
            }
            else {
                return Math.abs(random.nextGaussian()) * Math.abs(1 - ((double) (n.p().bitCount() + n.q().bitCount()) / s.semiprime.bitCount()));
                /*
                int c = n.s().shiftRight(depth).bitLength();
                int len = s.semiprime.bitLength();
                int dist = Math.abs(len - depth);
                int diff = Math.abs(dist - c);
                return 1 / (double)diff;*/
            //}
            //Log.o(n.toString() + " = depth(" + n.depth() + ") carry(" + c + ")");
        };
        CompactSolver.Heuristic heuristic = solver.new Heuristic("random", h);

        try (final PrintWriter csv = new PrintWriter(prefix + "trends.min-" + minLen + ".max-" + maxLen + ".runs-" + runs + ".csv"))
        {
            csv.write("len, expanded\n");

            int numThreads = 1;
            long testStartTime = System.nanoTime();

            for (int len = minLen; len <= maxLen; ++len)
            {
                long expanded = 0;
                long open = 0;

                for (int run = 0; run < runs; ++run)
                {
                    long startTime = System.nanoTime();

                    Key key = new Key(len);
                    BigInteger goal = key.s;
                    int maxDepth = (int) Math.ceil(goal.bitLength()/2.);
                    solver.goal(goal).numThreads(numThreads).maxDepth(maxDepth).heuristics(heuristic).run();
                    expanded += solver.expanded();
                    open += solver.open();

                    // temp --->
                    double millis = ((System.nanoTime() - startTime) / 1e6);
                    csv.write(len + ", " + expanded + "\n"); csv.flush();
                    Log.o("len: " + len + ", expanded: " + expanded + ", open: " + open + ", elapsed: " + millis + " ms");
                    expanded = open = 0;
                }

                /*
                double avgExpanded = (expanded / (double) runs);
                expanded = 0;

                double avgOpen = (open / (double) runs);
                open = 0;

                double avgMillis = ((System.nanoTime() - startTime) / 1e6) / runs;

                csv.write(len + ", " + avgExpanded + "\n"); csv.flush();
                Log.o("len: " + len + ", expanded: " + avgExpanded + ", open: " + avgOpen + ", elapsed: " + avgMillis + " ms");
                 */
            }

            double elapsed = (System.nanoTime() - testStartTime) / 1e6;
            Log.o("elapsed: " + elapsed + "ms");

            return true;
        }
        catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }

    public static boolean entropy()
    {
        BigInteger target = new BigInteger("1111000001", 2);
        CompactSolver solver = new CompactSolver();
        solver.goal(target).numThreads(1).run();
        return true;
    }



    public static void main(String[] args)
    {
        try { new File(testDir).mkdir(); } catch (Throwable ignored) {}
        //trends(32, 32, 10000);
        //entropy();



        /*
        BigInteger a = new BigInteger("11111", 2);
        BigInteger b = a.multiply(a);
        Log.o(a.toString() + " " + b.toString() + " " + b.toString(2));
         */
    }
}
