package org.jakelcode.schedule;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Pin Khe "Jake" Loo (03 February, 2015)
 */

public class ScheduleUID {
    private static AtomicLong id = new AtomicLong();

    public static void set(long initialValue) {
        id.set(initialValue);
    }

    public static long get() {
        return id.incrementAndGet();
    }
}
