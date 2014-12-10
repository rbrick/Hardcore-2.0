package org.hcsoups.hardcore.utils;

/**
 * Created by Ryan on 11/22/2014
 * <p/>
 * Project: HCSoups
 */
public class MathUtil {

    /**
     * Recursively called.
     *
     * In mathematics, the factorial of a non-negative integer n, denoted by n!,
     * is the product of all positive integers less than or equal to n.
     * For example, The value of 0! is 1, according to the convention for an empty product
     *
     * @param i -> the number you want the factorial of.
     * @return The factorial of i
     */
    public static int factorial(int i) {
        if(i == 0) return 1;
        else return i * factorial(i-1);
    }
}
