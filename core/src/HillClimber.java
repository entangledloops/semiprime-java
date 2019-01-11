import java.math.BigInteger;
import java.util.Random;

public class HillClimber {
    static final Random rnd = new Random(1);
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
            p.append(i == 0 || i == len-1 || rnd.nextBoolean() ? '1' : '0');
            q.append(i == 0 || i == len-1 || rnd.nextBoolean() ? '1' : '0');
        }
        return new Node(new BigInteger(p.toString(), 2), new BigInteger(q.toString(), 2));
    }

    public void step(Node n, boolean up)
    {
        final boolean p = (n.p.bitCount() < n.q.bitCount()) == up;
        BigInteger c = p ? n.p : n.q;

        int pos = 1 + rnd.nextInt(c.bitLength() - 2);
        while (c.testBit(pos) == up) {
            pos = 1 + rnd.nextInt(c.bitLength() - 2);
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
        Node bestNode = new Node(n);
        BigInteger dist = n.p.multiply(n.q).subtract(sp);
        BigInteger bestDist = dist;

        final int maxAttempts = sp.bitLength() / 2;
        int attempts = 0;
        int restarts = 0;

        System.out.println("targetLen: " + sp.bitLength() + ", maxAttempts: " + maxAttempts);

        while (!dist.equals(BigInteger.ZERO))
        {
            if (attempts >= maxAttempts) {
                ++restarts;
                attempts = 0;
                n = randomNode();
                dist = n.p.multiply(n.q).subtract(sp);
            }

            Node next = new Node(n);
            step(next, dist.compareTo(BigInteger.ZERO) < 0);
            BigInteger nextDist = next.p.multiply(next.q).subtract(sp);

            if (nextDist.abs().compareTo(dist.abs()) < 0) {
                if (nextDist.abs().compareTo(bestDist.abs()) < 0) {
                    bestNode = new Node(next);
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
            return p.toString(10) + " * " + q.toString(10);
        }
    }

    public static void main(String[] args)
    {
        BigInteger p = BigInteger.probablePrime(128, rnd);
        BigInteger q = BigInteger.probablePrime(128, rnd);
        BigInteger sp = p.multiply(q);
        System.out.println(p + " * " + q + " = " + sp);

        HillClimber hillClimber = new HillClimber(sp);
        Node soln = hillClimber.solve();
        System.out.println(soln + " = " + sp.toString(10));
    }
}
