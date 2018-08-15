package operating.systems.philosophers;

import java.util.concurrent.ThreadLocalRandom;

/**
 * This class contains static utility methods, that are used in the application.
 */
public class Utilities
{
	/**
	 * This class should never be instantiated.
	 */
	private Utilities() { }

	/**
	 * Generates a random number between certain bounds.
	 *
	 * @param min The lowest value the random number should take.
	 * @param max The highest value the random number should take.
	 * @return A number between those bounds.
	 */
	public static int randomInt(int min, int max)
	{
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}
}
