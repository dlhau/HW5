import akka.actor.UntypedAbstractActor;
import akka.actor.ActorSystem;
import akka.actor.ActorRef;
import akka.actor.Props;

/*
 * David Hau
 * 11/5/2018
 * CS3700
 */

public class Main {
    
    private static ActorSystem actorSystem = ActorSystem.create("sieve-system");
    private static int primeCount = 0;
    private static final Object lock = new Object();
    
    public static void main(String[] args)
    {
        int num = 1000000;
        long time = print(num);
        System.out.println("Time to Finish Sieve(Actors):" + time + " ms.  ");
    }
    
    private static class Sieve extends UntypedAbstractActor
    {
        
        public static Props props(int localPrime)
        {
            return Props.create(Sieve.class, () -> new Sieve(localPrime));
        }
        
        private final int currentPrime;
        private ActorRef nextSieve;
        
        Sieve(int currentPrime)
        {
            this.currentPrime = currentPrime;
            System.out.println(currentPrime);
            primeCount = primeCount + 1;
        }
        
        @Override
        public void onReceive(Object msg) throws Exception
        {
            if(msg instanceof Integer)
            {
                int newNum = (Integer)msg;
                if(newNum % currentPrime > 0)
                {
                    if(nextSieve == null)
                    {
                    	nextSieve = actorSystem.actorOf(Sieve.props(newNum), "sieve-actor-" + newNum);
                    }
                    else
                    {
                    	nextSieve.tell(msg, getSelf());
                    }
                }
            }
            else if(nextSieve == null)
            {
                synchronized(lock)
                {
                	lock.notify();
                }
            }
            else
            {
                nextSieve.tell("", ActorRef.noSender());
            }
        }
    }
    
    private static long print(int num)
    {
        long startTime = System.currentTimeMillis(), endTime;
        
        if(num > 1)
        {
            ActorRef initSieve = actorSystem.actorOf(Sieve.props(2), "sieve-actor-2");
            synchronized(lock)
            {
                for(int i = 3; i <= num; i++)
                {
                	initSieve.tell(i, ActorRef.noSender());
                }
                initSieve.tell("", ActorRef.noSender());
                try
                {
                	lock.wait(100000);
                }
                catch(InterruptedException e)
                {
                	e.printStackTrace();
                }
                
                actorSystem.terminate();
            }
        }
        
        endTime = System.currentTimeMillis();
        return endTime - startTime;
    }
}