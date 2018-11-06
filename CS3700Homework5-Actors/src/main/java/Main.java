/*
 * David Hau
 * 11/5/2018
 * CS3700
 */

public class Main
{
	
	public static void main(String[] args)
	{
		//BoundedBuffer producerConsumer = new BoundedBuffer(10, 5, 2, 100);
		BoundedBuffer producerConsumer = new BoundedBuffer(10, 2, 5, 100);
		//long timeOne = producerConsumer.run();
		
		//producerConsumer = new BoundedBuffer(10, 2, 5, 100);
		
		long timeTwo = producerConsumer.run();
		
		//System.out.println("Time to Finish Locks(5 Producers, 2 Consumers: " + timeOne  + " ms");
		System.out.println("Time to Finish Locks(2 Producers, 5 Consumers: " + timeTwo + " ms");
	}
}