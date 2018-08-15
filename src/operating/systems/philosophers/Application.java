package operating.systems.philosophers;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

/**
 * The main class of the application.
 */
public class Application
{
	// Thanos Paravantis (P16112)

	/**
	 * The amount of philosophers specified by the user.
	 */
	private static int amount;

	/**
	 * The array of forks used by the philosophers.
	 */
	private static Fork[] forks;

	/**
	 * Application entry point.
	 */
	public static void main(String[] args)
	{
		amount = readAmount();
		initForks();
		initPhilosophers();
	}

	/**
	 * Reads the number of philosophers from the user.
	 *
	 * @return The number of philosophers.
	 */
	private static int readAmount()
	{
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter the number of philosophers: ");

		int amount = 0;

		try
		{
			do
			{
				amount = scanner.nextInt();

				if (amount < 0)
				{
					System.out.println("Please enter a positive integer between 3 and 10.");
				}
			}
			while (amount < 3 || amount > 10); // The user should enter a number between 3 and 10.
		}
		catch (InputMismatchException e)
		{
			System.out.println("Invalid input");
			System.exit(0);
		}

		scanner.close();

		return amount;
	}

	/**
	 * Initializes all the forks depending on the number of philosophers that were specified.
	 */
	private static void initForks()
	{
		System.out.println("Initializing Forks");

		forks = new Fork[amount];

		for (int i = 0; i < amount; i++)
		{
			Fork f = new Fork(i + 1);
			forks[i] = f;
		}
	}

	/**
	 * Initializes all philosophers and their respective threads, depending on the number specified by the user.
	 */
	private static void initPhilosophers()
	{
		System.out.println("Initializing Philosophers");

		// Array used to keep track of all philosophers so we can request a timings report later.
		Philosopher[] philosophers = new Philosopher[amount];

		// All threads associated with the philosophers.
		Thread[] threads = new Thread[amount];

		// A count down latch used to get notified when all philosophers have finished eating.
		CountDownLatch latch = new CountDownLatch(amount);

		for (int i = 0; i < amount; i++)
		{
			// We are calculating the correct index for the two forks that will be used by the philosopher.
			int leftIndex = i == 0 ? (amount - 1) : i - 1;
			int rightIndex = i == (amount - 1) ? (amount - 1) : i;

			Philosopher p = new Philosopher(i + 1, forks[leftIndex], forks[rightIndex], latch);
			philosophers[i] = p;

			threads[i] = new Thread(p, "Philosopher " + (i + 1));
		}

		for (int i = 0; i < amount; i++)
		{
			threads[i].start();
		}

		// Wait until all philosophers have finished, then generate the report.
		try
		{
			latch.await();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		double averageWait = 0;

		for (Philosopher p : philosophers)
		{
			averageWait += p.timingsReport();
		}

		double globalAverageWait = averageWait / amount;

		System.out.println("--- Global Report ---");
		System.out.println("Average time waiting to eat: " + globalAverageWait + "s");
	}
}
