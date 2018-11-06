/*
 * David Hau
 * 11/5/2018
 * CS3700
 */

public class Main
{
	
	public static void main(String[] args)
	{
		long startTime = System.currentTimeMillis(), endTime;

		BoundedBuffer producerConsumer = new BoundedBuffer(10, 5, 2, 100);
		producerConsumer.run();
		
		endTime = System.currentTimeMillis();
		
		long p5c2 = endTime - startTime;
		
		startTime = System.currentTimeMillis();
		
		producerConsumer = new BoundedBuffer(10, 2, 5, 100);
		producerConsumer.run();
		
		endTime = System.currentTimeMillis();
		long p2c5 = endTime - startTime;
		
		System.out.println("Time to Finish Isolated Sectors(5 Producers, 2 Consumers: " + p5c2 + " ms");
		System.out.println("Time to Finish Isolated Sectors(2 Producers, 5 Consumers: " + p2c5 + " ms");
	}
}