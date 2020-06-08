package test.foodfactory.orchrestation;

/**
 * An "utils" class with helper methods for thread orchestration
 *
 */
public class TimeHelper {

	public static long intervalFromTimestamp(long interval) {
		return System.currentTimeMillis() + interval;
	}

}
