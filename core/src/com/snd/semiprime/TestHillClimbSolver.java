package com.snd.semiprime;

import java.io.File;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.BiConsumer;

public class TestHillClimbSolver
{
    private static SimpleDateFormat format  = new SimpleDateFormat("MM-dd-yyyy");
    private static String           testDir = "test";

    public static String prefix()
    {
        return testDir + "/" + format.format(new Date()) + ".seed-" + Utils.seed() + ".";
    }

    public static boolean pairs(int minLen, int maxLen, int runs)
    {
        for (int run = 1; run <= runs; ++run) {
            Utils.seed(run);
            try (final PrintWriter csv = new PrintWriter(prefix() + "pairs.min-" + minLen + ".max-" + maxLen + ".runs-" + runs + ".csv")) {
                //csv.println("p, q, h*(n)");
                for (int len = minLen; len <= maxLen; ++len) {
                    //Key key = new Key(len);
                    Key key = new Key(null, null, new BigInteger("2403063739", 10));
                    //Log.o("key:\n" + key);

                    BigInteger max = Utils.factorMax(key.s);
                    Log.o("max: " + max.toString(2));

                    BigInteger mid = Utils.factorMid(key.s);
                    Log.o("mid: " + mid.toString(2));

                    BigInteger min = Utils.factorMin(key.s);
                    Log.o("min: " + min.toString(2));

                    BigInteger p = min, q = min, diff = key.s.subtract(p.multiply(q));
                    while (true) {
                        BigInteger p2 = p.add(BigInteger.TWO);
                        BigInteger q2 = q.add(BigInteger.TWO);
                        BigInteger nextDiffE = key.s.subtract(p2.multiply(q)).abs();
                        BigInteger nextDiffN = key.s.subtract(p2.multiply(q2)).abs();
                        csv.print("[ " + diff.toString() + " ][ " + diff.subtract(nextDiffE).abs().toString() + " ][ " + diff.subtract(nextDiffN).abs().toString() + " ][ " + p.toString(2) + " ][  " + q.toString(2) + " ], ");
                        //csv.print(diff.toString() + ", ");
                        diff = nextDiffE;

                        if (p.compareTo(mid) <= 0) p = p.add(BigInteger.TWO);
                        else if (q.compareTo(mid) <= 0) {
                            q = q.add(BigInteger.TWO);
                            p = q;
                            diff = key.s.subtract(p.multiply(q));
                            csv.println();
                        } else break;
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public static void hills(int len, int runs)
    {
        HillClimbSolver solver = new HillClimbSolver();

        try (final PrintWriter csv = new PrintWriter(prefix() + "hills.len-" + len + ".runs-" + runs + ".csv")) {
            for (int run = 0; run < runs; ++run) {
                Key key = new Key(len);
                Log.o(key.toString());
                solver.goal(key.s).csv(csv).run();

            }
        }
        catch (Throwable t) {
            Log.e(t);
        }
    }

    public static void stats(int minLen, int maxLen, int runs)
    {
        BiConsumer<BigInteger, BigInteger> callback = new BiConsumer<BigInteger, BigInteger>()
        {
            @Override
            public void accept(BigInteger p, BigInteger q)
            {
                Log.o("solution found:\n" +
                        p.toString(2) + " (" + p.toString() + ")\n" +
                        q.toString(2) + " (" + q.toString() + ")\n");
            }
        };

        final String func = Thread.currentThread().getStackTrace()[1].getMethodName();
        try (PrintWriter pw = new PrintWriter(prefix() + func  + ".minLen-" + minLen + ".maxLen-" + maxLen + ".runs-" + runs + ".csv")) {
            pw.println("len, elapsed, steps");

            for (int i = minLen; i <= maxLen; i += 2) {
                for (int run = 0; run < runs; ++run) {
                    Key key = new Key(i);
                    Log.o("solving:\n" + key.s.toString(2) + " (" + key.s.toString() + ")");
                    HillClimbSolver solver = new HillClimbSolver();//.callback(callback);
                    solver.goal(key.s).run();
                    pw.println(i + ", " + solver.elapsed() + " , " + solver.steps());
                }
            }
        }
        catch (Throwable t) {
            Log.e(t);
        }
    }

    public static void shapes(int minLen, int maxLen, int runs)
    {
        final String func = Thread.currentThread().getStackTrace()[1].getMethodName();
        try (PrintWriter pw = new PrintWriter(prefix() + func  + ".minLen-" + minLen + ".maxLen-" + maxLen + ".runs-" + runs + ".csv")) {
            for (int len = minLen; len <= maxLen; ++len) {
                for (int run = 0; run < runs; ++run) {
                    Key key = new Key(len);
                    BigInteger cur = Utils.factorMin(key.s);
                    BigInteger max = Utils.factorMax(key.s);
                    while (!cur.equals(max)) {
                        pw.println(key.s.subtract(cur.multiply(cur)).abs());
                        cur = cur.add(BigInteger.TWO);
                    }
                }
            }
        }
        catch (Throwable t) {
            Log.e(t);
        }
    }


    public static void main(String[] args)
    {
        try { new File(testDir).mkdir(); } catch (Throwable ignored) {}

        /*shapes(42, 42, 1);
        System.exit(0);*/

/*
        long x = 368;
        long y = 752;
        long z = 374;

        while (true)
        {
            long mod = (x+y) % z;
            if (mod == 0) {
                Log.o((x+y) + " " + z);
                break;
            }

            Log.o(mod + "");

            x += y;
            y -= 8;
            z -= 4;
        }
*/

        //stats(12, 42, 1000);
        //pairs(32, 32, 1);

        BiConsumer<BigInteger, BigInteger> callback = new BiConsumer<BigInteger, BigInteger>()
        {
            @Override
            public void accept(BigInteger p, BigInteger q)
            {
                Log.o("solution found:\n" +
                        p.toString(2) + " (" + p.toString() + ")\n" +
                        q.toString(2) + " (" + q.toString() + ")");
            }
        };

        try (PrintWriter pw = new PrintWriter("test/error.csv")) {
            //pw.println("len, steps, elapsed");
            pw.println("error");

            int minLen = 16;
            int maxLen = 16;
            int runs = 1;

            for (int i = minLen; i <= maxLen; i += 2) {
                for (int run = 1; run <= runs; ++run) {
                    Utils.seed(run);
/*
                    final BigInteger RSA_100_p = new BigInteger("37975227936943673922808872755445627854565536638199", 10);
                    final BigInteger RSA_100_q = new BigInteger("40094690950920881030683735292761468389214899724061", 10);
                    final BigInteger RSA_100 = new BigInteger("1522605027922533360535618378132637429718068114961380688657908494580122963258952897654000350692006139", 10);

                    Key key = new Key(RSA_100_p, RSA_100_q, RSA_100);*/
                    Key key = new Key(i);
                    //Key key = new Key(null, null, new BigInteger("2811438517"));
                    HillClimbSolver solver = new HillClimbSolver().callback(callback);

                    Log.o("solving:\n" + key);
                    solver.goal(key.s).run();
                    //String stats = "len: " + i + ", steps: " + solver.steps() + ", elapsed: " + (solver.elapsed()/1e6) + " ms";
                    //pw.println(stats);
                    //Log.o(stats);
                }
            }
        }
        catch (Throwable t) {
            Log.e(t);
        }


        //Key key = new Key(128);
        //HillClimbSolver solver = new HillClimbSolver();
        //solver.goal(BigInteger.valueOf(36089)).run();
    }
}
