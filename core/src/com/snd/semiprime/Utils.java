package com.snd.semiprime;

import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.locks.Lock;

/**
 * @author Stephen Dunn
 * @since January 16, 2016
 */
public class Utils
{
  private static int seed = 1;
  private static Random random = new Random(seed);

  public static void seed(int seed)
  {
    Utils.seed = seed;
    Utils.random = new Random(seed);
  }

  public static int seed()
  {
    return seed;
  }

  public static Random random()
  {
    return Utils.random;
  }

  public static void random(Random random)
  {
    Utils.random = random;
  }

  public static boolean lockAndRun(Lock lock, Runnable task)
  {
    if (null == lock || null == task) return false;
    lock.lock();
    try { task.run(); return true; }
    catch (Throwable t) { Log.e(t); return false; }
    finally { lock.unlock(); }
  }

  public static boolean jar()
  {
    try { return Utils.class.getResource("/" + Utils.class.getName().replace('.','/') + ".class").toString().startsWith("jar"); }
    catch (Throwable t) { return false; }
  }

  public static URL getResource(String resource)
  {
    return Utils.class.getClassLoader().getResource(resource);
  }

  public static InputStream getResourceFromJar(String resource)
  {
    return Utils.class.getResourceAsStream("/" + resource);
  }

  public static int factorLen(int len)
  {
    return (int) Math.ceil(len/2);
  }

  public static int factorLen(BigInteger sp)
  {
    return factorLen(sp.bitLength());
  }

  public static BigInteger factorMin(BigInteger sp)
  {
    final int len = factorLen(sp);
    StringBuilder sb = new StringBuilder(new String(new char[len]).replace('\0', '0'));
    sb.setCharAt(0,  '1');
    sb.setCharAt(len-1, '1');
    return new BigInteger(sb.toString(), 2);
  }

  public static BigInteger factorMid(BigInteger sp)
  {
    final int len = factorLen(sp);
    StringBuilder sb = new StringBuilder(new String(new char[len]).replace('\0', '1'));
    sb.setCharAt(len-2, '0');
    sb.setCharAt(1, '0');
    return new BigInteger(sb.toString(), 2);
  }

  public static BigInteger factorMax(BigInteger sp)
  {
    return new BigInteger(new String(new char[factorLen(sp)]).replace('\0', '1'), 2);
  }

  public static void main(String[] args)
  {
    int z = 187;
    for (int i = 1; i < 600; ++i) {
      if (i % z == 186) {
        System.out.println(i);
      }
    }
  }
}
