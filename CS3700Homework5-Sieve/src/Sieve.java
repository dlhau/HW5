/*
 * David Hau
 * 11/5/2018
 * CS3700
 */

public class Sieve implements Runnable
{
	int maxNum = 0;
	
	Sieve(int maxNum)
	{
		this.maxNum = maxNum;
	}
	public void run()
	{
		long startTime, endTime, totalTime;
		startTime = System.currentTimeMillis();
		
		boolean prime[] = new boolean[maxNum +1 ]; 
        for(int i = 0; i < maxNum; i++)
        {
        	prime[i] = true; 
        }
          
        for(int p = 2; p*p <= maxNum; p++) 
        { 
            if(prime[p] == true) 
            { 
                for(int i = p*2; i <= maxNum; i += p)
                {
                	prime[i] = false;
                }
            } 
        } 
          
        // Print all prime numbers 
        for(int i = 2; i <= maxNum; i++) 
        { 
            if(prime[i] == true) 
            {
            	System.out.println(i); 
            } 
        }
        
        endTime = System.currentTimeMillis();
		totalTime = endTime - startTime;
		System.out.println("Time to Finish Sieve(Single Thread): " + totalTime + " ms");
	}
}
