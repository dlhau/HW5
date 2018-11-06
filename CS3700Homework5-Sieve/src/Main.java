/*
 * David Hau
 * 11/5/2018
 * CS3700
 */

public class Main
{
	public static void main(String[] args)
	{	
		Thread sieve = new Thread(new Sieve(1000000));
		sieve.start();
	}
}
