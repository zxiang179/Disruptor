package bhz.generate1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.IgnoreExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.WorkerPool;

public class Main2 {

	
	public static void main(String[] args) throws InterruptedException {
		int BUFFER_SIZE = 1024;
		int THREAD_SIZE = 4;
		
		EventFactory<Trade> eventFactory = new EventFactory<Trade>() {
			@Override
			public Trade newInstance() {
				return new Trade();
			}
		};
		
		RingBuffer<Trade> ringBuffer = RingBuffer.createSingleProducer(eventFactory, BUFFER_SIZE);
		SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();
		ExecutorService executor = Executors.newFixedThreadPool(THREAD_SIZE);
		
		WorkHandler<Trade> handler = new TradeHandler();
		WorkerPool<Trade> workerPool = new WorkerPool<Trade>(ringBuffer,sequenceBarrier,new IgnoreExceptionHandler(),handler);
		workerPool.start(executor);
		
		//下面这个生产8个数据
		for(int i=0;i<8;i++){
			long seq = ringBuffer.next();
			ringBuffer.get(seq).setPrice(Math.random()*9999);
			ringBuffer.publish(seq);
		}
		
		Thread.sleep(1000);//等待一秒，等待消费都处理结束
		workerPool.halt();//通知事件 或者说处理器可以结束了
		executor.shutdown();//终止线程
	}
}
