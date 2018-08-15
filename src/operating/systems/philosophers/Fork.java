package operating.systems.philosophers;

/**
 * This class represents a fork used by the philosophers.
 */
public class Fork
{
	/**
	 * A number used when printing messages related with this fork.
	 */
	private int id;

	/**
	 * The thread (philosopher) that is currently using this fork.
	 * May be null.
	 */
	private Thread usedBy;

	/**
	 * Initializes all parameters.
	 *
	 * @param id A number used when printing messages related with this fork.
	 */
	public Fork(int id)
	{
		this.id = id;
	}

	/**
	 * Marks this fork as released.
	 */
	public void release()
	{
		// System.out.println(usedBy.getName() + ": Releasing " + toString());
		this.usedBy = null;
	}

	/**
	 * Marks this fork as taken from a specific thread.
	 *
	 * @param usedBy The thread using this fork.
	 */
	public void take(Thread usedBy)
	{
		this.usedBy = usedBy;
		// System.out.println(usedBy.getName() + ": Taking " + toString());
	}

	/**
	 * Checks if the fork is being used by a specific thread.
	 *
	 * @return If the fork is being used.
	 */
	public boolean isBeingUsed()
	{
		return usedBy != null;
	}

	/**
	 * Returns the thread that is currently using this fork.
	 *
	 * @return The thread using this fork, may be null.
	 */
	public Thread getUsedBy()
	{
		return usedBy;
	}

	@Override
	public String toString()
	{
		return "Fork " + id;
	}
}
