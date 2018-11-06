import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

/*
 * David Hau
 * 11/5/2018
 * CS3700
 */

public class BoundedBuffer
{
	private final Lock bufferLock;
	private final Condition bufferNotEmpty;
	private final Condition bufferNotFull;
    
    Queue<Integer> buffer;
    private int bufferMaxSize;
	private int producerNum;
	private int consumerNum;
    private int produced;
    private int consumed;
    
    private int producedCount = 0;
    private int consumedCount = 0;
    
    BoundedBuffer(int bufferMaxSize, int producerNum, int consumerNum, int produced)
    {
    	this.bufferMaxSize = bufferMaxSize;
    	this.producerNum = producerNum;
    	this.consumerNum = consumerNum;
    	this.produced = produced;
    	this.consumed = producerNum * produced;
    	buffer = new LinkedList<>();
    	bufferLock = new ReentrantLock();
    	bufferNotEmpty = bufferLock.newCondition();
        bufferNotFull = bufferLock.newCondition();
    }
    
    public void run()
    {
    	Thread[] producers = new Thread[producerNum];
        Thread[] consumers = new Thread[consumerNum];
        
        for(int i = 0; i < producerNum; i++)
        {
        	producers[i] = new Thread(new Producer(produced));
        }
        
        for(int i = 0; i < consumerNum; i++)
        {
        	consumers[i] = new Thread(new Consumer());
        }
        
        for(int i = 0; i < producerNum; i++)
        {
        	producers[i].start();
        }
        
        for(int i = 0; i < consumerNum; i++)
        {
        	consumers[i].start();
        }
        
        try
        {
            for(int i = 0; i < producerNum; i++)
            {
            	producers[i].join();
            }
        } catch(InterruptedException e) { e.printStackTrace(); }
        
        try
        {
            for(int i = 0; i < consumerNum; i++)
            {
            	consumers[i].join();
            }
        } catch(InterruptedException e) { e.printStackTrace(); }
    }
    
    public class Producer implements Runnable
    {
    	int producing = 0;
    	int produced;
    	
    	Producer(int produced)
    	{
    		this.produced = produced;
    	}
    	
    	@Override
    	public void run()
    	{
    		try
			{
				produce();
			} catch(InterruptedException e) { e.printStackTrace(); }
    	}
    	
    	private void produce() throws InterruptedException
    	{
            while(producing < produced)
            {
            	try
            	{
            		bufferLock.lock();
            		
            		while(buffer.size() >= bufferMaxSize)
            		{
            			bufferNotEmpty.await();
            		}
            		
            		if(buffer.offer(new Integer((int)(ThreadLocalRandom.current().nextInt()))))
    				{ 
    					producing = producing + 1;
    					producedCount = producedCount + 1;
    					System.out.println("Produced: " + producedCount);
                        bufferNotFull.signalAll();
                    }
            	}
            	catch(InterruptedException e)
            	{
            		e.printStackTrace();
            	}
            	finally
            	{
            		bufferLock.unlock();
            	}
            }
    	}
    }
    
    public class Consumer implements Runnable
    {
    	@Override
    	public void run()
    	{
    		try
    		{
    			consume();
    		} catch(InterruptedException e) { e.printStackTrace(); }
    		
    	}
    	
    	private void consume() throws InterruptedException
    	{
    		boolean hasItem = false;
            while(consumed > 0)
            {
                try
                {
                    bufferLock.lock();
                    while((buffer.size() <= 0) && (consumed > 0))
                    {
                    	bufferNotFull.awaitNanos(1000);
                    }
                    
                    hasItem = buffer.poll() != null;
                    
                    if(hasItem)
                    {
                        consumed = consumed - 1;
                        consumedCount = consumedCount + 1;
                        System.out.println("Consumed: " + consumedCount);
                        bufferNotEmpty.signalAll();
                    }
                }
                catch(InterruptedException e)
                {
                	e.printStackTrace();
                }
                finally
                {
                    bufferLock.unlock();
                    if(hasItem)
                    {
                    	try
                        {
                            Thread.sleep(1000);
                        } catch(InterruptedException e) { e.printStackTrace(); }
                    }
                }
            }
    	}
    }
    
}
