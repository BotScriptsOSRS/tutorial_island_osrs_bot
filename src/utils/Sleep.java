package utils;

import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.utility.ConditionalSleep;

import java.util.Random;
import java.util.function.BooleanSupplier;

public final class Sleep extends ConditionalSleep {

    private final BooleanSupplier condition;
    private static final Random random = new Random();

    public Sleep(final BooleanSupplier condition, final int timeout) {
        super(timeout);
        this.condition = condition;
    }

    @Override
    public boolean condition() {
        return condition.getAsBoolean();
    }

    public static boolean until(final BooleanSupplier condition) {
        return until(condition, 5000); // Default timeout of 5000 milliseconds
    }

    public static boolean until(final BooleanSupplier condition, final int timeout) {
        return new Sleep(condition, timeout).sleep();
    }

    public static void randomSleep(int minDuration, int maxDuration) {
        int sleepDuration = minDuration + random.nextInt(maxDuration - minDuration + 1);
        try {
            MethodProvider.sleep(sleepDuration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
