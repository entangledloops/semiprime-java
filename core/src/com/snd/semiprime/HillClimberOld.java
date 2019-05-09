package com.snd.semiprime;

import java.math.BigInteger;
import java.util.*;

public class HillClimberOld
{
    // com.snd.semiprime.HillClimberOld
    final Random rnd = new Random(1);
    BigInteger sp;

    public HillClimberOld(BigInteger sp)
    {
        this.sp = sp;
    }

    public Node randomNode()
    {
        final int len = (int)Math.ceil(sp.bitLength() / 2);
        StringBuilder p = new StringBuilder(len);
        StringBuilder q = new StringBuilder(len);
        for (int i = 0; i < len; ++i) {
            p.append(i == 0 || i == len-1 || rnd.nextBoolean() ? '1' : '0');
            q.append(i == 0 || i == len-1 || rnd.nextBoolean() ? '1' : '0');
        }
        return new Node(new BigInteger(p.toString(), 2), new BigInteger(q.toString(), 2));
    }

    public boolean step(Node n, boolean up)
    {
        final boolean p = (n.p.bitCount() < n.q.bitCount()) == up;
        final int pos = 1 + rnd.nextInt(n.p.bitLength() - 2);
        if (p) n.p = up ? n.p.setBit(pos) : n.p.clearBit(pos);
        else n.q = up ? n.q.setBit(pos) : n.q.clearBit(pos);

        /*

        int maxAttempts = c.bitLength() - 2;
        int attempts = 0;

        int pos = 1 + rnd.nextInt(c.bitLength() - 2);
        while (c.testBit(pos) == up) {
            pos = 1 + rnd.nextInt(c.bitLength() - 2);

            if (++attempts >= maxAttempts) {

                return false;
            }
        }

        if (p) n.p = c.flipBit(pos);
        else n.q = c.flipBit(pos);*/

        return true;
    }

    public Node solve()
    {
        Node n = randomNode();
        Node bestNode = new Node(n);
        BigInteger dist = n.p.multiply(n.q).subtract(sp);
        BigInteger bestDist = dist;

        final int maxAttempts = sp.bitLength() / 2;
        int attempts = 0;
        int restarts = 0;

        System.out.println("targetLen: " + sp.bitLength() + ", maxAttempts: " + maxAttempts);

        //final List<Node> bestNodes = new ArrayList<>();
        while (!dist.equals(BigInteger.ZERO))
        {
            Node next = new Node(n);
            if (attempts >= maxAttempts || !step(next, dist.compareTo(BigInteger.ZERO) < 0)) {
                ++restarts;
                attempts = 0;
                n = randomNode();// : new Node(bestNode);//&& bestNodes.size() > 0 ? new Node(bestNodes.get(rnd.nextInt(bestNodes.size()))) : randomNode();
                dist = n.p.multiply(n.q).subtract(sp);
                continue;
            }

            BigInteger nextDist = next.p.multiply(next.q).subtract(sp);
            if (nextDist.abs().compareTo(dist.abs()) < 0) {
                if (nextDist.abs().compareTo(bestDist.abs()) < 0) {
                    bestNode = new Node(next);
                    //if (restarts > 1) bestNodes.add(bestNode);
                    bestDist = nextDist;
                    System.out.println(bestNode + " = " + bestNode.p.multiply(bestNode.q) + ", bestDist: " + bestDist.toString() + ", restarts: " + restarts);
                }
                attempts = 0;
                n = next;
                dist = nextDist;
            } else {
                ++attempts;
            }
        }

        System.out.println("restarts: " + restarts);

        return n;
    }

    public class Node
    {
        public BigInteger p, q;
        public Node(BigInteger p, BigInteger q) {
            this.p = p;
            this.q = q;
        }
        public Node(Node n) {
            this.p = new BigInteger(n.p.toString());
            this.q = new BigInteger(n.q.toString());
        }
        @Override
        public String toString() {
            return toString(2);
        }
        public String toString(int radix) {
            return p.toString(radix) + " * " + q.toString(radix);
        }
    }

    public static void main(String[] args)
    {
        /*
        final Random rnd = new Random(1);
        final long start = System.nanoTime();

        for (int i = 0; i < 10; ++i) {
            final int len = 16;
            BigInteger p = BigInteger.probablePrime(len, rnd);
            BigInteger q = BigInteger.probablePrime(len, rnd);
            BigInteger sp = p.multiply(q);
            if (sp.bitLength() % 2 != 0) {
                --i; continue;
            }
            System.out.println("target: " + p + " * " + q + " = " + sp);
            System.out.println("target: " + p.toString(2) + " * " + q.toString(2) + " = " + sp.toString(2));

            com.snd.semiprime.HillClimberOld hillClimber = new com.snd.semiprime.HillClimberOld(sp);
            Node soln = hillClimber.solve();
            System.out.println("soln: " + soln + " = " + sp.toString(10));
        }

        System.out.println("elapsed: " + ((System.nanoTime()-start)/1000000000.));*/

        /*
        double temp = INIT_TEMP;
        for (int i = 0; i < 500; ++i) {
            temp *= COOL_FRAC;
            double exponent = (K * temp);
            double merit = Math.pow(Math.E, exponent);
            System.out.println(merit);
        }*/
    }
}
