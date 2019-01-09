import java.math.BigInteger;
import java.util.Random;

public class HillClimber {
    static final Random random = new Random(1);
    BigInteger sp;

    public HillClimber(BigInteger sp)
    {
        this.sp = sp;
    }

    public Node randomNode()
    {
        final int len = sp.bitLength() / 2;
        StringBuilder p = new StringBuilder(len);
        StringBuilder q = new StringBuilder(len);
        for (int i = 0; i < len; ++i) {
            p.append(i == 0 || i == len-1 || random.nextBoolean() ? '1' : '0');
            q.append(i == 0 || i == len-1 || random.nextBoolean() ? '1' : '0');
        }
        return new Node(new BigInteger(p.toString(), 2), new BigInteger(q.toString(), 2));
    }

    public void step(Node n, boolean up)
    {
        final boolean p = (n.p.bitCount() < n.q.bitCount()) == up;
        BigInteger c = p ? n.p : n.q;

        int pos = 1 + random.nextInt(c.bitLength() - 2);
        while (c.testBit(pos) == up) {
            pos = 1 + random.nextInt(c.bitLength() - 2);
        }

        if (p) {
            n.p = c.flipBit(pos);
        } else {
            n.q = c.flipBit(pos);
        }
    }

    public Node solve()
    {
        Node n = randomNode();
        BigInteger dist = n.p.multiply(n.q).subtract(sp);

        int attempts = 0;

        while (!dist.equals(BigInteger.ZERO))
        {
            if (attempts > 100) {
                //System.out.println("restart");
                n = randomNode();
                dist = n.p.multiply(n.q).subtract(sp);
            }

            //System.out.println(n.toString() + " = " + dist);
            final int cmp = dist.compareTo(BigInteger.ZERO);
            if (0 == cmp) break;


            Node next = new Node(new BigInteger(n.p.toString()), new BigInteger(n.q.toString()));
            step(next, cmp < 0);
            BigInteger nextDist = next.p.multiply(next.q).subtract(sp);
            //System.out.println("next: " + next + " = " + nextDist);
            if (nextDist.abs().compareTo(dist.abs()) < 0) {
                attempts = 0;
                n = next;
                dist = nextDist;
            } else {
                ++attempts;
            }
        }

        return n;
    }

    public class Node
    {
        public BigInteger p, q;
        public Node(BigInteger p, BigInteger q) {
            this.p = p;
            this.q = q;
        }
        @Override
        public String toString() {
            return p.toString(10) + " * " + q.toString(10);
        }
    }

    public static void main(String[] args)
    {
        BigInteger sp = BigInteger.probablePrime(32, random).multiply(BigInteger.probablePrime(32, random));
        HillClimber hillClimber = new HillClimber(sp);
        Node soln = hillClimber.solve();
        System.out.println(soln + " = " + sp.toString(10));
    }
}
