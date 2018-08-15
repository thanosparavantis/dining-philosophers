package operating.systems.philosophers;

/**
 * Represents the state of each philosopher.
 */
public enum PhilosopherState
{
	/**
	 * The philosopher is thinking and will become hungry later.
	 */
	THINKING,

	/**
	 * The philosopher is hungry and will eat once the associated forks are available.
	 */
	HUNGRY,

	/**
	 * The philosopher is eating and has occupied the two forks.
	 */
	EATING
}
