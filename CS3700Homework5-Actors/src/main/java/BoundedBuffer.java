import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;

/*
 * David Hau
 * 11/5/2018
 * CS3700
 */

public class BoundedBuffer
{
	private static ActorSystem actorSystem;

	private static ActorRef bufferRef;
	private static ActorRef[] producerRef;
	private static ActorRef[] consumerRef;

	private  int producerNum;
	private  int consumerNum;
	private static  int produced;
	private static AtomicInteger consumed;

	private static int producedCount = 0;
	private static int consumedCount = 0;

	BoundedBuffer(int bufferMaxSize, int producerNum, int consumerNum, int produced)
	{
		this.producerNum = producerNum;
		this.consumerNum = consumerNum;
		BoundedBuffer.produced = produced;
		consumed = new AtomicInteger(producerNum * produced);
		actorSystem = ActorSystem.create("producer-consumer-system");
		bufferRef = actorSystem.actorOf(Buffer.props(bufferMaxSize), "buffer-actor");
		producerRef = new ActorRef[producerNum];
	}

	public static class Buffer extends UntypedAbstractActor
	{
		private final int maxSize;
		private final Queue<Integer> buffer;

		public static Props props(int maxSize)
		{
			return Props.create(Buffer.class, () -> new Buffer(maxSize));
		}

		public Buffer(int maxSize)
		{
			this.maxSize = maxSize;
			buffer = new LinkedList<>();
		}

		public void onReceive(Object msg) throws Exception
		{
			if (msg instanceof Integer)
			{
				if (buffer.size() < maxSize)
				{
					buffer.add((Integer) msg);
					getSender().tell("", getSelf());
				}
				else
				{
					getSender().tell((Integer) msg, getSelf());
				}
			}
			else
			{
				if (buffer.size() > 0)
				{
					getSender().tell(buffer.poll(), getSelf());
				}
				else
				{
					getSender().tell("", getSelf());
				}
			}
		}
	}

	public long run()
	{
		long startTime = System.currentTimeMillis(), endTime;
		
		for (int i = 0; i < producerNum; i++)
		{
			producerRef[i] = actorSystem.actorOf(Producer.props(), "producer-actor-" + i);
		}

		consumerRef = new ActorRef[consumerNum];

		for (int i = 0; i < consumerNum; i++)
		{
			consumerRef[i] = actorSystem.actorOf(Consumer.props(), "consumer-actor-" + i);
		}

		for (int i = 0; i < producerNum; i++)
		{
			producerRef[i].tell("", ActorRef.noSender());
		}

		for (int i = 0; i < consumerNum; i++)
		{
			consumerRef[i].tell("", ActorRef.noSender());
		}

		while (consumed.get() > 0) {};

		actorSystem.terminate();
		
		endTime = System.currentTimeMillis();
		
		return endTime - startTime;
	}

	public static class Producer extends UntypedAbstractActor
	{
		public static Props props()
		{
			return Props.create(Producer.class, () -> new Producer());
		}
		
		int producing = 0;
		
		Producer()
		{
			producing = -1;
		}

		public void onReceive(Object msg) throws Exception
		{
			if (msg instanceof Integer)
			{
				bufferRef.tell((Integer) msg, getSelf());
			}
			else
			{
				producing = producing + 1;
				producedCount = producedCount + 1;
            	System.out.println("Produced: " + producedCount);
				if (producing < produced)
				{
					bufferRef.tell(new Integer((int) (Math.random())), getSelf());
				}
			}
		}
	}

	public static class Consumer extends UntypedAbstractActor
	{
		public static Props props()
		{
			return Props.create(Consumer.class, () -> new Consumer());
		}

		@Override
		public void onReceive(Object msg) throws Exception
		{
			if (msg instanceof Integer)
			{
				consumed.decrementAndGet();
				consumedCount = consumedCount + 1;
            	System.out.println("Consumed: " + consumedCount);
				if (consumed.get() > 0)
				{
					Thread.sleep(1000);
					bufferRef.tell("", getSelf());
				}
			}
			else
			{
				if (consumed.get() > 0)
				{
					bufferRef.tell("", getSelf());
				}
			}
		}
	}

}