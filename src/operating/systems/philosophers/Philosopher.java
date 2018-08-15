package operating.systems.philosophers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents a philosopher that can be started with a thread.
 */
public class Philosopher implements Runnable
{
	/**
	 * The priority of this philosopher in the table.
	 */
	private int priority;

	/**
	 * The left fork associated with this philosopher.
	 */
	private Fork leftFork;

	/**
	 * The right fork associated with this philosopher.
	 */
	private Fork rightFork;

	/**
	 * A count down latch instance from the main thread used to notify when this philosopher has finished eating.
	 */
	private CountDownLatch latch;

	/**
	 * The state of this philosopher.
	 */
	private PhilosopherState state = PhilosopherState.THINKING;

	/**
	 * A date formatter to print time stamps in the console.
	 */
	private DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	/**
	 * A thread-safe integer that represents the number of seconds this philosopher has been eating.
	 */
	private AtomicInteger secondsEating = new AtomicInteger(0);

	/**
	 * A thread-safe integer that represents the number of attempt to occupy the forks.
	 */
	private AtomicInteger eatAttempts = new AtomicInteger(0);

	/**
	 * A thread-safe integer that represents the number of second spent attempting to occupy the forks.
	 */
	private AtomicInteger secondsWaitingToEat = new AtomicInteger(0);

	/**
	 * A locked used to synchronize access on methods.
	 */
	private static ReentrantLock reentrantLock = new ReentrantLock();

	/**
	 * Initializes all parameters.
	 *
	 * @param priority  The priority of this philosopher in the table.
	 * @param leftFork  The left fork associated with this philosopher.
	 * @param rightFork The right fork associated with this philosopher.
	 * @param latch     A count down latch instance from the main thread used to notify when this philosopher has finished eating.
	 */
	public Philosopher(int priority, Fork leftFork, Fork rightFork, CountDownLatch latch)
	{
		this.priority = priority;
		this.leftFork = leftFork;
		this.rightFork = rightFork;
		this.latch = latch;
	}

	/**
	 * The initial thinking state of the philosopher when a thread is ran.
	 */
	@Override
	public void run()
	{
		try
		{
			think();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Makes the philosopher think for a couple of seconds.
	 *
	 * @throws InterruptedException
	 */
	private void think() throws InterruptedException
	{
		Thread thread = Thread.currentThread();

		// First we need to check if the philosopher has eaten for 20 seconds.
		// If that's the case then the philosopher has finished and should keep thinking.
		// Otherwise, we sleep this thread for a random amount of seconds and then the philosopher is hungry.

		if (secondsEating.get() >= 20)
		{
			updateState(PhilosopherState.THINKING);
			System.out.println(thread.getName() + " has finished!");
			latch.countDown();
		}
		else
		{
			updateState(PhilosopherState.THINKING);

			int sleepDuration = Utilities.randomInt(1, 10);
			Thread.sleep(sleepDuration * 1000);

			makeHungry(false);
		}
	}

	/**
	 * Makes the philosopher hungry and attempts to occupy the forks.
	 *
	 * @param attemptedToEat A flag used every time the philosopher attempted to eat.
	 * @throws InterruptedException
	 */
	private void makeHungry(boolean attemptedToEat) throws InterruptedException
	{
		// We need to update the state and sleep for a random amount of seconds.
		// Then we make an attempt to eat, even if it's unsuccessful.

		updateState(PhilosopherState.HUNGRY);

		int sleepDuration = Utilities.randomInt(1, 3);
		Thread.sleep(sleepDuration * 1000);

		// If the philosopher attempted to eat but failed, we want to update our timings.

		if (attemptedToEat)
		{
			eatAttempts.incrementAndGet();
			secondsWaitingToEat.addAndGet(sleepDuration);
		}

		attemptEat();
	}

	/**
	 * Makes the philosopher attempt to eat.
	 *
	 * @throws InterruptedException
	 */
	private void attemptEat() throws InterruptedException
	{
		// Lock so we can safely take the forks.
		reentrantLock.lock();

		Thread thread = Thread.currentThread();

		if (forksAreTaken())
		{
			reentrantLock.unlock();
			makeHungry(true);
			return;
		}

		updateState(PhilosopherState.EATING);

		leftFork.take(thread);
		rightFork.take(thread);

		// After we're finished taking the forks, we can let other threads in.
		reentrantLock.unlock();

		// The number of seconds spent eating depends on the priority of each philosopher.

		Thread.sleep(priority * 1000);
		secondsEating.addAndGet(priority);

		// Release the forks and start thinking again.

		reentrantLock.lock();
		leftFork.release();
		rightFork.release();
		reentrantLock.unlock();

		think();
	}

	/**
	 * Checks if the two associated forks are taken and prints error messages.
	 *
	 * @return Whether the two forks are taken.
	 */
	private boolean forksAreTaken()
	{
		Thread thread = Thread.currentThread();

		if (leftFork.isBeingUsed() || rightFork.isBeingUsed())
		{
			if (leftFork.isBeingUsed())
			{
				System.out.println(thread.getName() + " failed to take "
						+ leftFork.toString() + " because "
						+ leftFork.getUsedBy().getName() + " is eating.");
			}
			else
			{
				System.out.println(thread.getName() + " failed to take "
						+ rightFork.toString() + " because "
						+ rightFork.getUsedBy().getName() + " is eating.");
			}

			return true;
		}

		return false;
	}

	/**
	 * Updates the state of the philosopher and prints an update message.
	 *
	 * @param state The new state of the philosopher.
	 */
	private void updateState(PhilosopherState state)
	{
		this.state = state;
		Date date = new Date();

		Thread thread = Thread.currentThread();
		System.out.println(thread.getName() + " is " + state.toString() + " at time " + dateFormat.format(date));
	}

	/**
	 * Returns the average number of seconds spent waiting to eat.
	 * This will also print the value in the console.
	 *
	 * @return The average number of seconds spent waiting to eat.
	 */
	public double timingsReport()
	{
		int waitSeconds = secondsWaitingToEat.get();
		int attempts = eatAttempts.get();

		double averageWait = attempts > 0 ? (double) waitSeconds / attempts : 0;

		System.out.println("--- Philosopher " + priority + " Report ---");
		System.out.println("Average time waiting to eat: " + averageWait + "s");
		return averageWait;
	}
}
