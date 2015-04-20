package com.gitlab.zachdeibert.WarPlugin;

import java.util.Comparator;

public class RandomComparator<T> implements Comparator<T> {
    protected final double chance;
    
    protected int linearCongruential(final int modulus, final int multiplier, final int increment, final int seed) {
        return (multiplier * seed + increment) % modulus;
    }
    
    @Override
    public int compare(final T o1, final T o2) {
        final int h1 = o1.hashCode();
        final int h2 = o2.hashCode();
        return linearCongruential(h1, h1 ^ h2, h2, (int) System.currentTimeMillis());
    }
    
    public RandomComparator(final double chance) {
        this.chance = chance;
    }
}
