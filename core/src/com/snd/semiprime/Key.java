package com.snd.semiprime;

import java.math.BigInteger;
import java.util.Random;

public class Key
{
    public final BigInteger p, q, s;

    public Key(BigInteger p, BigInteger q, BigInteger s)
    {
        this.p = p;
        this.q = q;
        this.s = s;
    }

    /**
     * Generates a p,q,s set for a provided s len.
     * @param len Length of the target semiprime product.
     * @return An object containing p,q,s as BigIntegers.
     */
    public Key(int len)
    {
        int factorLen = Utils.factorLen(len);
        BigInteger p, q, s;
        do {
            p = BigInteger.probablePrime(factorLen, Utils.random());
            q = BigInteger.probablePrime(factorLen, Utils.random());
            s = p.multiply(q);
            //Log.o(p.toString(2) + " " + q.toString(2) + " " + s.toString(2));
        } while (s.bitLength() != len);

        this.p = p;
        this.q = q;
        this.s = s;
    }

    @Override
    public String toString()
    {
        return "p: " + p.toString(2) + " (" + p.toString() + ")\n" +
                "q: " + q.toString(2) + " (" + q.toString() + ")\n" +
                "s: " + s.toString(2) + " (" + s.toString() + ")\n";
    }
}
