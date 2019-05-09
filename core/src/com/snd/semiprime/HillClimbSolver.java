package com.snd.semiprime;

import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.function.BiConsumer;

import static java.math.BigInteger.*;
import static java.math.BigInteger.ONE;
import static java.math.BigInteger.TWO;

public class HillClimbSolver implements Runnable
{
    BigInteger semiprime;
    BigInteger p, q;
    BiConsumer<BigInteger, BigInteger> callback;
    final int kernelSize = 6;

    private PrintWriter csv;
    private long elapsed;
    private BigInteger steps;

    public HillClimbSolver()
    {
    }

    public HillClimbSolver goal(BigInteger semiprime)
    {
        this.semiprime = semiprime;
        return this;
    }

    public HillClimbSolver csv(PrintWriter csv)
    {
        this.csv = csv;
        return this;
    }

    public long elapsed()
    {
        return elapsed;
    }

    public BigInteger steps()
    {
        return steps;
    }

    public HillClimbSolver callback(BiConsumer<BigInteger, BigInteger> callback)
    {
        this.callback = callback;
        return this;
    }

    private void printKernel(BigInteger[][] kernel)
    {
        for (int i = 0; i < kernel.length; ++i) {
            Log.o(Arrays.toString(kernel[i]));
        }
    }

    private BigInteger updateKernel(BigInteger[][] kernel, BigInteger p, BigInteger q)
    {
        BigInteger avg = ZERO;
        for (int i = 0; i < kernelSize; ++i) {
            for (int j = 0; j < kernelSize; ++j) {
                BigInteger nextP = p.add(valueOf(i));
                BigInteger nextQ = q.add(valueOf(j));
                kernel[i][j] = nextP.multiply(nextQ).subtract(semiprime).abs();
                if (kernel[i][j].compareTo(ZERO) == 0) {
                    Log.o("solution:\n" + p.toString() + "\n" + q.toString());
                    return null;
                }
                avg = avg.add(kernel[i][j]);
            }
        }
        return avg.divide(valueOf(kernelSize * kernelSize));
    }

    private BigInteger binarySearchToStart()
    {
        final int len = Utils.factorLen(semiprime);
        BigInteger min = Utils.factorMin(semiprime);
        BigInteger max = Utils.factorMax(semiprime);
        BigInteger mid;

        while (true)
        {
            mid = max.add(min).divide(TWO).subtract(ONE);
            BigInteger dist = semiprime.subtract(mid.multiply(mid));
            int comp = dist.compareTo(ZERO);

            //Log.o("min: " + min.toString(2) + ",  max: " + max.toString(2) + ", mid: " + mid.toString(2) + " " + mid.toString() + " " + dist);

            // mid < 0
            if (comp < 0) {
                // check if moving up by 1 = solution
                BigInteger nextMid = mid.subtract(TWO);
                if (semiprime.subtract(nextMid.multiply(nextMid)).compareTo(ZERO) >= 0) {
                    if (!nextMid.testBit(0)) nextMid = nextMid.subtract(ONE);
                    return nextMid;
                }

                max = mid;
            }
            // mid > 0
            else if (comp > 0) {
                BigInteger nextMid = mid.add(TWO);
                //Log.o(semiprime.subtract(nextMid.multiply(nextMid)).toString());
                if (semiprime.subtract(nextMid.multiply(nextMid)).compareTo(ZERO) <= 0) {
                    if (!mid.testBit(0)) mid = mid.subtract(ONE);
                    return mid;
                }

                min = mid;
            }
            // solution
            else {
                return mid;
            }
        }
    }

    public void solveByBruteforce()
    {
        final BigInteger FOUR = valueOf(4);
        final BigInteger EIGHT = valueOf(8);

        int factorLen = Utils.factorLen(semiprime);
        final BigInteger factorMin = Utils.factorMin(semiprime);
        final BigInteger factorMid = Utils.factorMid(semiprime);
        final BigInteger factorMax = Utils.factorMax(semiprime);

        p = binarySearchToStart();
        q = p;
        BigInteger diff = semiprime.subtract(p.multiply(q)).abs();

        if (diff.equals(ZERO)) {
            elapsed = System.nanoTime() - elapsed;
            if (null != callback) {
                callback.accept(p, q);
            }
            return;
        }

        BigInteger sqrt = semiprime.sqrt();
        if (semiprime.mod(sqrt).equals(ZERO)) {
            elapsed = System.nanoTime() - elapsed;
            if (null != callback) {
                callback.accept(p, q);
            }
            return;
        }

        BigInteger x = diff.divide(TWO);
        BigInteger y = p.multiply(TWO).subtract(TWO);//p.multiply(p).subtract(p.subtract(TWO).pow(2));
        //BigInteger y = semiprime.subtract(p.subtract(TWO).multiply(q.subtract(TWO))).subtract(x);
        BigInteger z = p.subtract(TWO);
        //BigInteger z = x.add(y).subtract(semiprime.subtract(p.multiply(q.subtract(TWO))));

        //x = x.divide(TWO);
        //y = y.divide(TWO);
        //z = z.divide(TWO);

        BigInteger mod = ONE;

        Log.o("a: " + p);

        // -------------------------------------------------------------------------------------------------
        BigInteger initialZ = z;
        //Log.o("initial z: " + z.toString());

        mod = semiprime.mod(z);
        while (!mod.equals(ZERO)) {
            Log.o("a: " + z + ", mod: " + mod.toString());
            z = z.subtract(TWO);
            mod = semiprime.mod(z);
        }

        q = z; //p.subtract(TWO.multiply(steps));
        p = semiprime.divide(q);

        //double initialP = initialZ.doubleValue();
        //double realP = Math.min(p.doubleValue(), q.doubleValue());
        //csv.println(""+(realP/initialP));
    }

    public void solveByCoprime()
    {
        final BigInteger FOUR = valueOf(4);
        final BigInteger EIGHT = valueOf(8);

        int factorLen = Utils.factorLen(semiprime);
        final BigInteger factorMin = Utils.factorMin(semiprime);
        final BigInteger factorMid = Utils.factorMid(semiprime);
        final BigInteger factorMax = Utils.factorMax(semiprime);

        p = binarySearchToStart();
        q = p;
        BigInteger diff = semiprime.subtract(p.multiply(q)).abs();

        if (diff.equals(ZERO)) {
            elapsed = System.nanoTime() - elapsed;
            if (null != callback) {
                callback.accept(p, q);
            }
            return;
        }

        BigInteger sqrt = semiprime.sqrt();
        if (semiprime.mod(sqrt).equals(ZERO)) {
            elapsed = System.nanoTime() - elapsed;
            if (null != callback) {
                callback.accept(p, q);
            }
            return;
        }

        BigInteger x = diff.divide(TWO);
        BigInteger y = p.multiply(TWO).subtract(TWO);
        BigInteger z = p.subtract(TWO);
        //BigInteger y = semiprime.subtract(p.subtract(TWO).multiply(q.subtract(TWO))).subtract(x);
        //BigInteger z = x.add(y).subtract(semiprime.subtract(p.multiply(q.subtract(TWO))));

        //x = x.divide(TWO);
        //y = y.divide(TWO);
        //z = z.divide(TWO);

        BigInteger mod = ONE;

        Log.o("a: " + p);

        Log.o("x: " + x + ", y: " + y + ", z: " + z);

        //try (PrintWriter pw = new PrintWriter("test/mod.csv")) { pw.println("mod");
        while (!mod.equals(ZERO))//(!z.equals(ZERO) && !y.equals(ZERO))
        {
            steps = steps.add(ONE);

            BigInteger temp = x;
            x = x.add(y);
            mod = x.mod(z);

            //if (mod.equals(ZERO)) Log.o("steps: " + steps);

            Log.o("x: " + temp + ", y: " + y + ", (x + y): " + x + ", z: " + z + ", mod: " + mod);
            //pw.println(mod);

            y = y.subtract(FOUR);
            z = z.subtract(TWO);
        }
        //} catch (Throwable t) { Log.e(t); }

        //Log.o("x: " + x + ", y: " + y + ", z: " + z + ", mod: " + mod);

        q = z.add(TWO); //p.subtract(TWO.multiply(steps));
        p = semiprime.divide(q);
    }

    public void solveByLineWalk()
    {
        int factorLen = Utils.factorLen(semiprime);
        final BigInteger factorMin = Utils.factorMin(semiprime);
        final BigInteger factorMid = Utils.factorMid(semiprime);
        final BigInteger factorMax = Utils.factorMax(semiprime);

        p = binarySearchToStart();
        q = p;
        BigInteger diff = semiprime.subtract(p.multiply(q)).abs();

        final BigInteger FOUR = valueOf(4);
        final BigInteger EIGHT = valueOf(8);

        BigInteger pE = p.add(BigInteger.TWO);
        BigInteger qE, pNE, qNE;
        BigInteger diffE, diffNE;

        while (!diff.equals(BigInteger.ZERO))
        {
            //steps = steps.add(ONE);

            if (null != csv) {
                csv.println(diff.toString());
            }

            pE = p.add(BigInteger.TWO);
            qE = q;
            diffE = pE.compareTo(factorMax) > 0 ? semiprime : pE.multiply(qE).subtract(semiprime).abs();

            if (diffE.equals(BigInteger.ZERO)) {
                p = pE; q = qE; break;
            }

            pNE = p;
            qNE = q.subtract(BigInteger.TWO);
            diffNE = pNE.multiply(qNE).subtract(semiprime).abs();

            if (diffNE.equals(BigInteger.ZERO)) {
                p = pNE; q = qNE; break;
            }

            int comp = diffE.compareTo(diffNE);
            if (comp <= 0) {
                p = pE;
                q = qE;
                diff = diffE;
            }
            else if (comp > 0) {
                p = pNE;
                q = qNE;
                diff = diffNE;
            }
        }
    }

    public void solveByDiff()
    {
        int factorLen = Utils.factorLen(semiprime);
        final BigInteger factorMin = Utils.factorMin(semiprime);
        final BigInteger factorMid = Utils.factorMid(semiprime);
        final BigInteger factorMax = Utils.factorMax(semiprime);

        p = binarySearchToStart();
        q = p;
        BigInteger diff = semiprime.subtract(p.multiply(q)).abs();

        final BigInteger pE = p.add(BigInteger.TWO);
        final BigInteger FOUR = valueOf(4);
        final BigInteger EIGHT = valueOf(8);



        if (!diff.equals(BigInteger.ZERO)) {
            while (true) {
                //steps = steps.add(ONE);

                // compute difference between current adjacent cells
                BigInteger nextDiff = semiprime.subtract(pE.multiply(q));
                BigInteger mod = diff.subtract(nextDiff);

                // goal check
                if (diff.mod(mod).equals(BigInteger.ZERO)) {
                    BigInteger whole = diff.divide(mod);
                    BigInteger toAdd = whole.multiply(BigInteger.TWO);
                    p = p.add(toAdd);
                    break;
                }

                q = q.subtract(BigInteger.TWO);
                diff = semiprime.subtract(p.multiply(q));
            }

            if (!p.multiply(q).equals(semiprime)) {
                Log.e("incorrect solution found:\np: " + p + "\nq: " + q);
                //System.exit(-1);
            }
        }
    }

    @Override
    public void run()
    {
        steps = ZERO;
        elapsed = System.nanoTime();

        solveByCoprime();

        elapsed = System.nanoTime() - elapsed;

        if (!p.multiply(q).equals(semiprime)) {
            Log.e("incorrect solution found:\np: " + p + "\nq: " + q + "\ns: " + semiprime);
            return;
        }

        if (null != callback) {
            callback.accept(p, q);
        }
    }
}
