import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * David Hau
 * 11/5/2018
 * CS3700
 */

public class BoundedBuffer
{
	BlockingQueue<Integer> buffer;
	private int producerNum;
	private int consumerNum;
    private int produced;
    private AtomicInteger consumed;
    
    private int producedCount = 0;
    private int consumedCount = 0;
    
    BoundedBuffer(int bufferMaxSize, int producerNum, int consumerNum, int produced)
    {
    	this.producerNum = producerNum;
    	this.consumerNum = consumerNum;
    	this.produced = produced;
    	buffer = new LinkedBlockingQueue<>(bufferMaxSize);
    	consumed = new AtomicInteger(producerNum * produced);
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
    		for(int i = 0; i < produced; i++)
    		{
    			try
    			{
    				produce();
    			} catch(InterruptedException e) { e.printStackTrace(); }
    		}
    	}
    	
    	private void produce() throws InterruptedException
    	{
            while(producing < produced)
            {
            	
                if(buffer.offer(new Integer((int)(ThreadLocalRandom.current().nextInt()))))
                {
                	producing = producing + 1;
                	producedCount = producedCount + 1;
                	System.out.println("Produced: " + producedCount);
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
    		while(consumed.get() > 0)
    		{
                if(buffer.poll() != null)
                {
                    consumed.decrementAndGet();
                    consumedCount = consumedCount + 1;
                    System.out.println("Consumed: " + consumedCount);
                    try
                    {
                        Thread.sleep(1000);
                    } catch(Exception e) { e.printStackTrace(); }
                }
            }
    	}
    }
    
}
