package com.learn.mlanalysissysbackend.tmp;

import java.math.BigInteger;

public class Main {
    public static void main(String[] args) {
        BigInteger num = BigInteger.ZERO;
        int count = 0;

        for (int i = 1; i <= 2026; i++) {
            num = num.add(BigInteger.valueOf(i));
            System.out.println(num);
            if (num.mod(BigInteger.valueOf(26)).equals(BigInteger.ZERO)) {
                num = BigInteger.ZERO;
                count++;
                continue;
            }
            num = num.multiply(BigInteger.valueOf(getCul(i + 1)));
        }

         System.out.println("Count: " + count);
    }

    public static int getCul(int num) {
        if (num >= 100) {
            return 1000;
        }
        if (num >= 10) {
            return 100;
        }
        return 10;
    }
}
