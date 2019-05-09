package com.snd.semiprime;

import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;

public class CompactSolver implements Runnable
{
    /// default handler for thread exceptions
    private static final Thread.UncaughtExceptionHandler handler = (thread, t) -> Log.e(t);

    private Class<? extends Node>             nodeClass;
    private Constructor<Node>                 nodeConstructor;

    private final Lock                        lock;
    private final Condition                   stop;
    private final List<Thread>                threads;
    private final List<Heuristic>             heuristics;
    private final PriorityBlockingQueue<Node> open;
    private final AtomicLong                  expanded;
    private final AtomicBoolean               solving;
    private final AtomicReference<Node>       solution;
    private       int                         numThreads;
    private       int                         maxDepth;

    BigInteger semiprime;

    public CompactSolver()
    {
        this.nodeClass = PerformanceNode.class;
        this.nodeConstructor = (Constructor<Node>) this.nodeClass.getConstructors()[0];
        this.lock = new ReentrantLock();
        this.stop = lock.newCondition();
        this.threads = Collections.synchronizedList(new ArrayList<>());
        this.heuristics = new ArrayList<>();
        this.open = new PriorityBlockingQueue<>();
        this.expanded = new AtomicLong(0);
        this.solving = new AtomicBoolean(false);
        this.solution = new AtomicReference<>();
        this.numThreads = Runtime.getRuntime().availableProcessors();
    }

    @Override
    public void run()
    {
        // clear data from previous search
        stop().clear();

        // prepare search threads
        IntStream.range(0, numThreads).forEach(i -> threads.add(new Thread(this::solve)));
        threads.forEach(thread -> thread.setUncaughtExceptionHandler(handler));

        // setup the search
        solving.set(true);
        //open.add(new CompactNode(null, true, true));
        open.add(newNode(null, 1, 1));

        // launch all worker threads and wait for completion
        lock.lock();
        threads.forEach(Thread::start);
        stop.awaitUninterruptibly();
        lock.unlock();
        // TODO: interrupt not safe from signalling thread
        threads.forEach(thread -> { try { thread.interrupt(); thread.join(); } catch (Throwable ignored) {} });
    }

    public CompactSolver numThreads(int numThreads)
    {
        this.numThreads = numThreads;
        return this;
    }

    public CompactSolver nodeClass(Class<? extends Node> klass)
    {
        this.nodeClass = klass;
        this.nodeConstructor = (Constructor<Node>) klass.getConstructors()[0];
        return this;
    }

    private Node newNode(Node parent, int p, int q)
    {
        try { return nodeConstructor.newInstance(this, parent, p, q); }
        catch (Throwable t) { Log.e(t); return null; }
    }

    public CompactSolver heuristics(Heuristic... heuristics)
    {
        this.heuristics.clear();
        Collections.addAll(this.heuristics, heuristics);
        return this;
    }

    public CompactSolver goal(String semiprime, int radix)
    {
        return goal(new BigInteger(semiprime, radix));
    }

    public CompactSolver goal(BigInteger semiprime)
    {
        this.semiprime = semiprime;
        this.maxDepth = semiprime.bitLength()-1;
        return this;
    }

    public long expanded()
    {
        return expanded.get();
    }

    public int open()
    {
        return open.size();
    }

    public Node solution()
    {
        return solution.get();
    }

    /**
     * Set a search cutoff limit in search depth.
     * @param maxDepth Max search depth. For RSA keys, use ceil(bitLen/2).
     * @return this for chaining
     */
    public CompactSolver maxDepth(int maxDepth)
    {
        this.maxDepth = maxDepth;
        return this;
    }

    /**
     * Stop an existing search and clear old threads.
     * @return this for chaining
     */
    public CompactSolver stop()
    {
        solving.set(false);
        threads.forEach(thread -> { try { thread.interrupt(); thread.join(); } catch (Throwable ignored) {} });
        return this;
    }

    /**
     * Reset data structures, except for the target semiprime (so it can be reused in another search).
     * @return this for chaining
     */
    private CompactSolver clear()
    {
        threads.clear();
        open.clear();
        expanded.set(0);
        solution.set(null);
        return this;
    }

    public int solutionPos(BigInteger p, BigInteger q)
    {
        return 0;
    }

    public CompactSolver solve()
    {
        while (solving.get())
        {
            // grab best node from open
            Node n;
            try { n = open.take(); } catch (Throwable t) { return this; }

            // update expanded counter
            expanded.incrementAndGet();

            // skip nodes that are too large
            int depth = n.depth()+1;
            if (depth > maxDepth) continue;

            // grab factors for pruning behavior
            BigInteger p = n.p();
            BigInteger q = n.q();

            // expand n
            for (int pBit = 0; pBit < 2; ++pBit) {
                for (int qBit = 0; qBit < 2; ++qBit)
                {
                    // prune mirror image factors
                    if (pBit > qBit && p.equals(q)) continue;

                    // prune factors that can't produce a solution
                    BigInteger newP = 0 != pBit ? p.setBit(depth) : p;
                    BigInteger newQ = 0 != qBit ? q.setBit(depth) : q;
                    BigInteger s = newP.multiply(newQ);
                    if (s.testBit(depth) != semiprime.testBit(depth)) continue;

                    // build new node
                    Node child = newNode(n, pBit, qBit);

                    // compare current s to goal
                    int comp = s.compareTo(semiprime);

                    // prune if too large
                    if (comp > 0) continue;

                    Log.o(child.toString());

                    // goal check
                    if (s.equals(semiprime)) {
                        // skip degenerate solution of 1 * n = n
                        if (newP.equals(BigInteger.ONE) || newQ.equals(BigInteger.ONE)) continue;

                        // we found a real solution
                        solving.set(false);
                        solution.set(child);
                        lock.lock();
                        stop.signal();
                        lock.unlock();
                        return this;
                    }

                    //Log.o("bitA: " + s.testBit(depth) + ", bitB: " + semiprime.testBit(depth) + ", depth: " + depth + ", " + s.toString(2) + " " + semiprime.toString(2) + "\n" + child.toString() + " = " + s.toString() + " (" + s.toString(2) + ")");

                    // push node to open
                    if (!open.offer(child)) {
                        Log.e("child rejected");
                    }
                }
            }
        }

        return this;
    }

    public abstract class Node implements Comparable<Node>
    {
        public abstract BigInteger p();
        public abstract BigInteger q();
        public abstract BigInteger s();
        public abstract double h();
        public abstract int depth();

        public Node(Node parent, int p, int q) {}

        @Override
        public String toString()
        {
            BigInteger p = p();
            BigInteger q = q();
            BigInteger s = s();
            return p.toString() + " (" + p.toString(2) + "), " + q.toString() + " (" + q.toString(2) + ") = " + s.toString() + " (" + s.toString(2) + ")";
        }

        @Override
        public int compareTo(Node o)
        {
            return Double.compare(h(), o.h());
        }
    }

    public class PerformanceNode extends Node
    {
        BigInteger p, q, s;
        double h;
        int depth;

        public PerformanceNode(Node parent, int p, int q)
        {
            super(parent, p, q);

            if (null != parent) {
                BigInteger parentP = parent.p();
                BigInteger parentQ = parent.q();
                this.depth = parent.depth() + 1;
                this.p = 0 != p ? parentP.setBit(depth) : parentP;
                this.q = 0 != q ? parentQ.setBit(depth) : parentQ;
            }
            else {
                this.p = BigInteger.valueOf(p);
                this.q = BigInteger.valueOf(q);
            }

            this.s = this.p.multiply(this.q);
            for (Heuristic heuristic : heuristics) this.h += heuristic.h(this);
        }

        @Override
        public BigInteger p()
        {
            return p;
        }

        @Override
        public BigInteger q()
        {
            return q;
        }

        @Override
        public BigInteger s()
        {
            return s;
        }

        @Override
        public double h()
        {
            return h;
        }

        @Override
        public int depth()
        {
            return depth;
        }
    }

    public class CompactNode extends Node
    {
        CompactNode parent;
        boolean p, q;

        public CompactNode(Node parent, int p, int q)
        {
            super(parent, p, q);

            this.parent = (CompactNode) parent;
            this.p  = 0 < p;
            this.q = 0 < q;
        }

        @Override
        public BigInteger p()
        {
            StringBuilder sb = new StringBuilder();
            CompactNode cur = this;
            while (cur != null) {
                sb.append(cur.p ? '1' : '0');
                cur = cur.parent;
            }
            return new BigInteger(sb.toString(), 2);
        }

        @Override
        public BigInteger q()
        {
            StringBuilder sb = new StringBuilder();
            CompactNode cur = this;
            while (cur != null) {
                sb.append(cur.q ? '1' : '0');
                cur = cur.parent;
            }
            return new BigInteger(sb.toString(), 2);
        }

        @Override
        public BigInteger s()
        {
            return p().multiply(q());
        }

        @Override
        public double h()
        {
            double h = 0;
            for (Heuristic heuristic : heuristics) h += heuristic.h(this);
            return h;
        }

        @Override
        public int depth()
        {
            int depth = 0;
            CompactNode cur = parent;
            while (cur != null) {
                ++depth;
                cur = cur.parent;
            }
            return depth;
        }
    }

    public class Heuristic
    {
        final String                                  name;
        final BiFunction<CompactSolver, Node, Double> h;

        public Heuristic(String name, BiFunction<CompactSolver, Node, Double> h)
        {
            this.name = name;
            this.h = h;
        }

        public double h(Node n)
        {
            return h.apply(CompactSolver.this, n);
        }
    }
}
