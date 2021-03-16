package com.rick.cryptcloud.common;

import org.apache.commons.math3.primes.Primes;
import org.apache.commons.math3.util.ArithmeticUtils;

import java.math.BigInteger;
import java.util.Random;

public class RotationUtils {

    private static final Random random = new Random();

    private static final int MAX = 100;

    private static final int MIN = 2;

    public static long genPrime() {
        int num = random.nextInt(MAX - MIN + 1) + MIN;
        return Primes.nextPrime(num);
    }

    public static long genN(long p, long q) {
        return p * q;
    }

    public static long genfi(long p, long q) {
        return (p - 1) * (q - 1);
    }

    public static boolean primeJudge(long a, long b) {
        for (int j = 2; j <= a; j++) {
            if (a % j == 0 && b % j == 0)
                return false;
        }
        return true;
    }

    public static long getRpk(long fi) {
        long a = 1000;
        long rpk;
        while (true) {
            if (primeJudge(a, fi)) {
                rpk = a;
                break;
            }
            a++;
        }
        return rpk;
    }

    public static long getRsk(long rpk, long fi) {
        long i;
        long rsk;
        for (i = 1; ; i++) {
            if (((i * rpk) % fi) == 1) {
                rsk = i;
                break;
            }
        }
        return rsk;
    }

    public static long BDri(long rsk, long cur, long N) {
        BigInteger next;
        if (cur > N)
            System.out.println("Too Big");
        next = ArithmeticUtils.pow(BigInteger.valueOf(cur), BigInteger.valueOf(rsk)).mod(BigInteger.valueOf(N));
        return next.longValue();
    }

    public static long[] FDri(long rpk, long cur, int t, long N) {
        long[] list = new long[t];
        list[t - 1] = cur;
        for (int i = 0; i < t - 1; i++) {
            list[t - 2 - i] = ArithmeticUtils.pow(BigInteger.valueOf(list[t - 1 - i]), BigInteger.valueOf(rpk)).mod(BigInteger.valueOf(N)).longValue();
        }
        return list;
    }
}
